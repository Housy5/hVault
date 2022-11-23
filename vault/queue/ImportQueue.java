package vault.queue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import vault.Constants;
import vault.Main;
import vault.NameUtilities;
import vault.encrypt.Encryptor;
import vault.gui.FinishedImportDialog;
import vault.gui.MessageDialog;
import vault.nfsys.FilePointer;
import vault.nfsys.FileSystem;
import vault.nfsys.Folder;
import vault.nfsys.FolderBuilder;
import vault.nfsys.HFile;

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
        fsys = Main.frameInstance.user.fsys;
        importedFiles = new ArrayList<>();
        importedPointers = new ArrayList<>();
        importedFolders = new ArrayList<>();
    }

    private void handleDirectory(ImportTicket ticket) {
        Folder parent = ticket.getParent();
        File file = ticket.getFile();

        Folder folder = FolderBuilder.createFolder(file.getName(), parent);
        folder.setCreationDate(LocalDateTime.now());
        parent.addFolder(folder);
        File[] files = file.listFiles();
        if (files != null) {
            Arrays.stream(files).forEach(x -> tickets.add(new ImportTicket(x, folder)));
        }
        importedFolders.add(folder);
    }
    
    private byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            MessageDialog.show(Main.frameInstance, ex.getMessage());
            return new byte[] {};
        }
    }
    
    private void validateSavePath() {
        File path = Constants.FILES_PATH;
        if (!path.exists() || path.isDirectory()) {
            path.mkdirs();
        }
    }

    private boolean exportSaveFile(HFile hFile, FilePointer fp) {
        validateSavePath();
        File f = new File(fp.getLocation());
        try ( var out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(Encryptor.encryptObject(hFile));
        } catch (IOException ex) {
            MessageDialog.show(Main.frameInstance, ex.getMessage());
            return false;
        }
        
        return true;
    }
    
    private void handleFile(ImportTicket ticket) {
        Folder parent = ticket.getParent();
        File file = ticket.getFile();
        String fpName = file.getName();
        String randomName = NameUtilities.generateRandomFileName();
        
        if (parent.containsFileName(fpName)) {
            fpName = NameUtilities.nextFileName(fpName, parent);
        }
        
        if (file.length() < Integer.MAX_VALUE && fpName != null) {
            FilePointer fp = new FilePointer();
            fp.setName(fpName);
            fp.setLocation(String.format("%s/%s", Constants.FILES_PATH.getAbsolutePath(), randomName));
            fp.setSize(file.length());
            fp.setParent(parent);
            fp.setCreationDate(LocalDateTime.now());
            
            byte[] bytes = Encryptor.encrypt(readAllBytes(file));
            if (bytes.length == 0) {
                return;
            }
            
            HFile hfile = new HFile(new File(fp.getLocation()));
            hfile.setValue(bytes);
            exportSaveFile(hfile, fp);
            hfile.addReference(fp);
            
            parent.addFile(fp);
            
            importedFiles.add(file);
            importedPointers.add(fp);
        }
    }

    private void finalizeImport() {
        var dialog = new FinishedImportDialog(Main.frameInstance, List.copyOf(importedFiles));
        int opt = dialog.showDialog();
        
        if (opt == FinishedImportDialog.DELETE_ALL_OPTION) {
            importedFiles.forEach(file -> file.delete());
        } else if (opt == FinishedImportDialog.CANCEL_OPTION) {
            importedPointers.forEach(x -> fsys.removeFile(x));
            Collections.reverse(importedFolders);
            importedFolders.forEach(x -> fsys.removeFolder(x));
            Main.saveUsers();
            Main.reload();
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

                    if (ticket != null) {
                        handleTicket(ticket);
                    }
                }
                
                if (importing) {
                    Main.saveUsers();
                    Main.reload();
                    finalizeImport();
                }

                importing = false;
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {

            }
        }
    }

    public final void start() {
        if (running)
            return;
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
