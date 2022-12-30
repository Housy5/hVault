package vault.queue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import vault.Main;
import vault.gui.FinishedImportDialog;
import vault.gui.MessageDialog;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import vault.fsys.Folder;
import vault.fsys.FolderFactory;

public class ImportQueue implements Runnable {

    private boolean importing = false;
    private boolean running = false;
    private Thread thread;

    private final long sleepTime = 1000;
    private final Queue<ImportTicket> tickets;
    private final List<File> importedFiles;
    private final List<FilePointer> importedPointers;
    private final List<Folder> importedFolders;
    private final FileSystem fsys;

    private ImportQueue() {
        tickets = new LinkedList<>();
        fsys = Main.frameInstance.user.getFileSystem();
        importedFiles = new ArrayList<>();
        importedPointers = new ArrayList<>();
        importedFolders = new ArrayList<>();
    }

    private void handleDirectory(ImportTicket ticket) {
        Folder parent = ticket.getParent();
        File file = ticket.getFile();

        Folder folder = FolderFactory.createFolder(parent, file.getName());
        parent.addFolder(folder);
        File[] files = file.listFiles();
        Arrays.stream(files).parallel().map(x -> new ImportTicket(x, folder)).forEach(x -> tickets.add(x));
        importedFolders.add(folder);
    }

    private byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            MessageDialog.show(Main.frameInstance, ex.getMessage());
            return new byte[]{};
        }
    }

    private void handleFile(ImportTicket ticket) {
        Folder parent = ticket.getParent();
        File file = ticket.getFile();

        if (file.length() >= Integer.MAX_VALUE)
            return;

        FilePointer fp = new FilePointer(file.getName());
        fp.setSize(file.length());
        fp.setParent(parent);
        fp.setSize(file.length());
        fp.setCreationDate(LocalDateTime.now());
        fp.saveContent(readAllBytes(file));
        parent.addFilePointer(fp);

        importedFiles.add(file);
        importedPointers.add(fp);
    }

    private void finalizeImport() {
        var dialog = new FinishedImportDialog(Main.frameInstance, List.copyOf(importedFiles));
        int opt = dialog.showDialog();

        if (opt == FinishedImportDialog.DELETE_ALL_OPTION) {
            importedFiles.forEach(file -> file.delete());
        } else if (opt == FinishedImportDialog.CANCEL_OPTION) {
            importedPointers.parallelStream().forEach(x -> fsys.removeFilePointer(x));
            Collections.reverse(importedFolders);
            importedFolders.forEach(x -> fsys.removeFolder(x));
        }

        if (opt != FinishedImportDialog.CANCEL_OPTION) {
            Main.frameInstance.finishImport();
        }

        importedPointers.clear();
        importedFolders.clear();
        importedFiles.clear();
    }

    private void handleTicket(ImportTicket ticket) {
        File file = ticket.getFile();

        if (file.isDirectory()) {
            handleDirectory(ticket);
        } else if (file.isFile()) {
            handleFile(ticket);
        }
    }

    public boolean isImporting() {
        return importing;
    }

    public int count() {
        return tickets.size();
    }

    @Override
    public void run() {
        while (running) {
            try {
                while (!tickets.isEmpty()) {
                    importing = true;
                    ImportTicket ticket = tickets.poll();
                    handleTicket(ticket);
                }

                if (importing) {
                    finalizeImport();
                    Main.saveUsers();
                    Main.reload();
                }

                importing = false;
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {

            }
        }
    }

    public final void start() {
        if (running) {
            return;
        }
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public final void stop() {
        running = false;
        instance = null;
    }

    public final void addTicket(ImportTicket ticket) {
        tickets.add(ticket);
    }

    private static ImportQueue instance = null;

    public static ImportQueue instance() {
        if (instance == null) {
            instance = new ImportQueue();
            instance.start();
        }
        return instance;
    }
}
