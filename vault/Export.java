package vault;

import vault.queue.ImportQueue;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import vault.encrypt.Encryptor;
import vault.gui.Frame;
import vault.nfsys.FilePointer;
import vault.nfsys.Folder;
import vault.queue.ExportQueue;
import vault.queue.ExportTicket;

public class Export {

    private static final String TEMP_PATH = Constants.USER_HOME_PATH + "/temp/";

    public final static List<IOTask> exportTasks = new LinkedList<>();
    public final static List<IOTask> importTasks = new LinkedList<>();

    private static Timer ioTimer;

    private static boolean isImporting() {
        return !importTasks.isEmpty() || ImportQueue.instance().isImporting();
    }
    
    private static boolean isExporting() {
        return !exportTasks.isEmpty() || ExportQueue.instance().isExporting();
    }
    
    public static void startIOMonitor(Frame f) {
        if (ioTimer != null && ioTimer.isRunning()) {
            ioTimer.stop();
        }

        ioTimer = new Timer(250, (ActionEvent e) -> {
            if ((!isImporting() && !isExporting()) && f.getCursor().getType() != Cursor.DEFAULT_CURSOR) {
                f.setCursor(Cursor.getDefaultCursor());
            } else if ((isImporting() || isExporting()) && f.getCursor().getType() != Cursor.WAIT_CURSOR) {
                f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            }
        });
        ioTimer.start();
    }
    
    public static void stopIOMonitor() {
        ioTimer.stop();
    }

    public static class IOTask {

        FilePointer file;
        File path;

        public IOTask(FilePointer file, File path) {
            this.file = file;
            this.path = path;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.file);
            hash = 37 * hash + Objects.hashCode(this.path);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IOTask other = (IOTask) obj;
            if (!Objects.equals(this.file, other.file)) {
                return false;
            }
            return Objects.equals(this.path, other.path);
        }

    }

    private static JFileChooser createChooser() {
        var chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose an export directory.");
        return chooser;
    }

    private static String generateTempName(String extension) {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        int length = 10;
        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                sb.append((char) rand.nextInt((int) 'a', (int) 'z'));
            }
            sb.append(".").append(extension);
        } while (new File(TEMP_PATH + sb.toString()).exists() || !NameValidator.isValidName(sb.toString()));
        return sb.toString();
    }

    public static File exportTemporaryFile(FilePointer f) {
        File file = new File(TEMP_PATH + generateTempName(NameValidator.splitNameAndExtension(f.getName())[1]));
        File dir = new File(Constants.USER_HOME_PATH + "/temp");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        export(f, file, false);
        file.deleteOnExit();
        
        return file;
    }

    private static void export(FilePointer f) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);

        if (result == JFileChooser.APPROVE_OPTION) {
            ExportQueue.instance().addTicket(new ExportTicket(f, chooser.getSelectedFile().toPath()));
        }
    }
    
    public static void exportFile(FilePointer f) {
        export(f);
    }
    
    public static void exportFolder(Folder folder) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            ExportQueue.instance().addTicket(new ExportTicket(folder, chooser.getSelectedFile().toPath()));
        }
    }

    public static void exportAllV2(Collection<Object> objects) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            ExportQueue queue = ExportQueue.instance();
            objects.forEach(obj -> queue.addTicket(new ExportTicket(obj, path)));
        }
    } 
    
    public static void exportAll(Collection<FilePointer> files) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);

        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            ExportQueue queue = ExportQueue.instance();
            files.forEach(file -> queue.addTicket(new ExportTicket(file, path)));
        }
    }

    public static void export(FilePointer f, File dir, boolean flag) {
        String newName = null;

        if (dir.exists()) {
            newName = NameUtilities.nextFileName(f.getName(), null);
            
            if (newName == null) {
                JOptionPane.showMessageDialog(Main.frameInstance, "We couldn't export \"" + f.getName() + "\" to the specified location.", "info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        
        var exportDir = newName == null ? dir : new File(dir.getParent() + "/" + newName);
        
        var task = new IOTask(f, dir);
        if (exportTasks.contains(task)) {
            return;
        }
        exportTasks.add(task);
        
        new Thread(() -> {
            try {
                var out = new FileOutputStream(exportDir);
                out.write(Encryptor.decode(f.getBytes()));
                out.close();
                Main.frameInstance.setCursor(Cursor.getDefaultCursor());
                
                if (flag) {
                    JOptionPane.showMessageDialog(Main.frameInstance, f.getName() + " has finished exporting.", "info", JOptionPane.INFORMATION_MESSAGE);
                }
                exportTasks.remove(task);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Main.frameInstance, "Export failed!\n" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();

    }
}
