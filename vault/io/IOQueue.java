package vault.io;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import vault.Constants;
import vault.Main;
import vault.NameUtilities;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;
import vault.fsys.FolderFactory;

import static vault.Main.frameInstance;
import vault.gui.status.ProgramStatus;
import vault.gui.status.StatusManager;

public class IOQueue implements Runnable {
    
    private Queue<IOTask> queue = new ConcurrentLinkedQueue<>();
    
    private static final Path homePath = Constants.USER_HOME_PATH.toPath();
    private static final Path tempPath = homePath.resolve("temp");
    private static final Path filePath = homePath.resolve("files");
    
    private static final IOQueue instance = new IOQueue();
    
    static {
        try {
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
            }
            
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath);
            }
            
            instance.start();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }  
    
    
    public static void addIOTask(IOTask task) {
        instance.queue.add(task);
    }
    
    
    private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int randomNameLength = 16;
    
    private void start() {
        new Thread(this).start();
    }

    private Path createRandomPath(Path path) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(randomNameLength);
        Path result = null;
        
        while (result == null || Files.exists(result)) {
            for (int i = 0; i < randomNameLength; i++) {
                sb.append(chars.charAt(random.nextInt(0, chars.length())));
            }
            result = path.resolve(sb.toString());
        }
        
        return result;
    }
    
    @Override
    public void run() {
        boolean update = false;
        while (true) {
            try {
                while (!queue.isEmpty()) {
                    update = true;
                    
                    IOTask current = queue.poll();
                    switch (current.getType()) {
                        case EXPORT -> {
                            doExportTask(current);
                        }
                        case IMPORT -> {
                            doImportTask(current);
                        }
                        case OPEN -> {
                            update = false; // don't need to update and save when you're just opening a file.
                            doOpenTask(current);
                        }
                    }
                }
                
                if (update) {
                    Main.save();
                    Main.reload();
                    update = false;
                }
                
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void doExportTask(IOTask task) {
        StatusManager.push(ProgramStatus.EXPORTING);
        Path output = task.getOutput();
        FileSystemItem item = task.getSource();
        
        if (!Files.isDirectory(homePath)) {
            task.setStatus(Status.FAILED);
            StatusManager.pop(ProgramStatus.EXPORTING);
            return;
        }
        
        if (item instanceof Folder folder) {
            try {
                Path newDir = output.resolve(folder.getName());
                if (Files.exists(newDir)) {
                    newDir = newDir.resolveSibling(NameUtilities.nextSystemFolderName(folder.getName(), output));
                }
                Files.createDirectory(newDir);
                for (FilePointer fp : folder.getPointers()) {
                    queue.add(IOTask.createExportTask(fp, newDir));
                }
                
                for (Folder f : folder.getSubFolders()) {
                    queue.add(IOTask.createExportTask(f, newDir));                    
                }
            } catch (IOException e) {
                e.printStackTrace();
                task.setStatus(Status.FAILED);
                StatusManager.pop(ProgramStatus.EXPORTING);
                return;
            }
        } else if (item instanceof FilePointer fp) {
            try {
                String fileName = fp.getName();
                Path export = output.resolve(fileName);
                
                if (Files.exists(export)) {
                    export = export.resolveSibling(NameUtilities.nextSystemFileName(fileName, output));
                }
                
                Files.createFile(export);
                FileEncryptor.decryptFile(fp.getContentFile(), export.toFile());
            } catch (IOException e) {
                e.printStackTrace();
                task.setStatus(Status.FAILED);
                StatusManager.pop(ProgramStatus.EXPORTING);
                return;
            }
        }
        task.setStatus(Status.COMPLETE);
        StatusManager.pop(ProgramStatus.EXPORTING);
    }
    
    private void doImportTask(IOTask task) {
        StatusManager.push(ProgramStatus.IMPORTING);
        if (Files.isDirectory(task.getInput())) {
            try {
                Path path = task.getInput();
                Folder parent = (Folder) task.getDestination();
                Folder folder = FolderFactory.createFolder(parent, path.getFileName().toString());
                parent.addFolder(folder);
                Files.list(path).map(x -> IOTask.createImportTask(folder, x)).forEach(x -> queue.add(x));
            } catch (IOException ex) {
                ex.printStackTrace();
                task.setStatus(Status.FAILED);
                StatusManager.pop(ProgramStatus.IMPORTING);
                return;
            }
        } else if (true) {
            try {
                Folder parent = (Folder) task.getDestination();
                Path content = createRandomPath(filePath);
                Path input = task.getInput();
                FileEncryptor.encryptFile(task.getInput().toFile(), content.toFile());
                
                FilePointer fp = new FilePointer(input.getFileName().toString());
                fp.setSize(Files.size(input));
                fp.setParent(parent);
                fp.setCreationDate(LocalDateTime.now());
                fp.setContentFile(content.toFile());
                
                parent.addFilePointer(fp);
            } catch (IOException e) {
                e.printStackTrace();
                task.setStatus(Status.FAILED);
                StatusManager.pop(ProgramStatus.IMPORTING);
                return;
            }
        }
        
        task.setStatus(Status.COMPLETE);
        StatusManager.pop(ProgramStatus.IMPORTING);
    }
    
    private void doOpenTask(IOTask task) {
        try {
            
            StatusManager.push(ProgramStatus.OPENING);
            
            if (!Desktop.isDesktopSupported()) {
                task.setStatus(Status.FAILED);
                return;
            }
            
            frameInstance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            FilePointer pointer = (FilePointer) task.getSource();
            Path export = Path.of(createRandomPath(tempPath).toString() + "." + pointer.getExtension());
            FileEncryptor.decryptFile(pointer.getContentFile(), export.toFile());
            Desktop.getDesktop().open(export.toFile());
            task.setStatus(Status.COMPLETE);
            frameInstance.setCursor(Cursor.getDefaultCursor());
        } catch (IOException e) {
            e.printStackTrace();
            task.setStatus(Status.FAILED);
        }
        
        StatusManager.pop(ProgramStatus.OPENING);
    }
}

