package vault.queue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.JOptionPane;
import vault.Main;
import vault.NameUtilities;
import vault.encrypt.Encryptor;
import vault.nfsys.FilePointer;
import vault.nfsys.Folder;

public class ExportQueue implements Runnable {

    private boolean running;
    private boolean exporting;
    private Thread thread;

    private final Queue<ExportTicket> tickets;
    private final List<FilePointer> exports;

    private final long SLEEP_TIME = 500;

    private final String DESKTOP_PATH = System.getProperty("user.home") + "/Desktop";

    private ExportQueue() {
        running = false;
        tickets = new LinkedList<>();
        exports = new LinkedList<>();
    }

    public final boolean isExporting() {
        return exporting;
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
        if (!running) {
            return;
        }
        running = false;
    }
    
    public final int count() {
        return tickets.size();
    }

    public final void addTicket(ExportTicket ticket) {
        tickets.add(ticket);
    }
    
    private void errorMessage(String fileName) {
        JOptionPane.showMessageDialog(Main.frameInstance, "We couldn't export \"" + fileName + "\" to the specified location.", "info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportFile(FilePointer pointer, Path exportDirectory) {
        String fileName = pointer.getName();
        Path exportFile = Paths.get(exportDirectory.toString() + "/" + fileName);

        while (Files.exists(exportFile)) {
            fileName = NameUtilities.nextSystemFileName(fileName, exportDirectory);

            if (fileName == null) {
                errorMessage(fileName);
                return;
            }

            exportFile = Paths.get(exportDirectory.toString() + "/" + fileName);
        }

        try {
            Files.createFile(exportFile);
            Files.write(exportFile, Encryptor.decode(pointer.getBytes()));
        } catch (IOException e) {
            errorMessage(fileName);
            return;
        }

        exports.add(pointer);
    }

    private void exportFolder(Folder folder, Path exportDirectory) {
        String folderName = folder.getName();
        Path newDirectory = Paths.get(exportDirectory + "/" + folderName);

        while (Files.exists(newDirectory) && Files.isDirectory(newDirectory)) {
            folderName = NameUtilities.nextSystemFolderName(folderName, exportDirectory);

            if (folderName == null) {
                JOptionPane.showMessageDialog(Main.frameInstance, "Failed to export the folder \"" + folder.getName() + "\"");
                return;
            }

            newDirectory = Paths.get(exportDirectory + "/" + folderName);
        }

        try {
            Files.createDirectory(newDirectory);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(Main.frameInstance, "Failed to export the folder \"" + folder.getName() + "\"");
            return;
        }
        
        final Path dir = newDirectory;
        folder.getFiles().forEach(file -> tickets.add(new ExportTicket(file, dir)));
        folder.getFolders().forEach(fol -> tickets.add(new ExportTicket(fol, dir)));
    }

    private void handleTicket(ExportTicket ticket) {
        Path path = ticket.getPath().orElse(Paths.get(DESKTOP_PATH));

        if (ticket.isFilePointer()) {
            if (!ticket.getFilePointer().isEmpty()) {
                exportFile(ticket.getFilePointer().get(), path);
            }
        } else if (ticket.isFolder()) {
            if (!ticket.getFolder().isEmpty()) {
                exportFolder(ticket.getFolder().get(), path);
            }
        } else {
            JOptionPane.showMessageDialog(Main.frameInstance, "Export failed: invalid export ticket!");
        }
    }

    private void finalizeExports() {
        JOptionPane.showMessageDialog(Main.frameInstance,
                "Finished exporting " + exports.size() + " file(s).");
        exporting = false;
        exports.clear();
    }

    @Override
    public void run() {
        while (running) {
            try {
                while (!tickets.isEmpty()) {
                    ExportTicket ticket = tickets.poll();
                    exporting = true;

                    if (ticket != null) {
                        handleTicket(ticket);
                    }
                }

                if (exporting) {
                    finalizeExports();
                }

                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }
    }

    private static ExportQueue instance;

    public static ExportQueue instance() {
        if (instance == null) {
            instance = new ExportQueue();
            instance.start();
        }

        return instance;
    }
}
