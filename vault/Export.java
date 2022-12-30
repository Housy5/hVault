package vault;

import vault.queue.ImportQueue;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;
import vault.gui.CursorType;
import vault.queue.ExportQueue;
import vault.queue.ExportTicket;

public class Export {

    private static final String TEMP_PATH = Constants.USER_HOME_PATH + "/temp";
    private static final File TEMP_FOLDER = new File(TEMP_PATH);
    private static Random random;
    private static Timer ioTimer;
    
    private static final int RANDOM_NAME_LENGTH = 16;

    static {
        random = new Random();
        validateTempFolder();
    }
    
    private static void validateTempFolder() {
        TEMP_FOLDER.mkdirs();
    }
    
    private static boolean isImporting() {
        return ImportQueue.instance().isImporting();
    }
    
    private static boolean isExporting() {
        return ExportQueue.instance().isExporting();
    }
    
    public static void startIOMonitor() {
        if (ioTimer != null && ioTimer.isRunning()) {
            ioTimer.stop();
        }

        ioTimer = new Timer(250, (ActionEvent e) -> {
            if (!isImporting() && !isExporting()) {
                Main.changeCursor(CursorType.DEFAULT);
            } else if (isImporting() || isExporting()) {
                Main.changeCursor(CursorType.WAIT);
            }
        });
        ioTimer.start();
    }
    
    public static void stopIOMonitor() {
        ioTimer.stop();
    }

    private static JFileChooser createChooser() {
        var chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose an export directory.");
        return chooser;
    }

    private static String generateTempName() {
        var sb = new StringBuilder(RANDOM_NAME_LENGTH);
        for (var len = RANDOM_NAME_LENGTH; len > 0; len--) {
            sb.append(random.nextInt('a', 'z' + 1));
        }
        return sb.toString();
    }

    private static File createTempFileFor(FilePointer pointer) {
        File temp;
        do {
            var fileName = generateTempName() + "." + pointer.getExtension();
            temp = new File(TEMP_PATH + "/" + fileName);
        } while (temp.exists());
        temp.deleteOnExit();
        return temp;
    }
    
    public static File exportTemporaryFile(FilePointer pointer) {
        var temp = createTempFileFor(pointer);
        directExport(pointer, temp);
        return temp;
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

    public static void exportAll(List<? extends FileSystemItem> objects) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            ExportQueue queue = ExportQueue.instance();
            objects.forEach(obj -> queue.addTicket(new ExportTicket(obj, path)));
        }
    } 

    public static void directExport(FilePointer pointer, File exportDir) {
        if (exportDir.exists())
            throw new FileAlreadyExistsException();
        
        new Thread(() -> {
            try (var out = new FileOutputStream(exportDir)){
                out.write(pointer.getContent());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Main.frameInstance, "Export failed!\n" + e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }).start();
    }
}
