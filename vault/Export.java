package vault;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import javax.swing.JFileChooser;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;
import vault.io.IOQueue;
import vault.io.IOTask;


public class Export {


    private static JFileChooser createChooser() {
        var chooser = new JFileChooser(System.getProperty("user.home"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Choose an export directory.");
        return chooser;
    }

    private static void export(FilePointer f) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);

        if (result == JFileChooser.APPROVE_OPTION) {
            IOQueue.addIOTask(IOTask.createExportTask(f, chooser.getSelectedFile().toPath()));
        }
    }
    
    public static void exportFile(FilePointer f) {
        export(f);
    }
    
    public static void exportFolder(Folder folder) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            IOQueue.addIOTask(IOTask.createExportTask(folder, chooser.getSelectedFile().toPath()));
        }
    }

    public static void exportAll(List<? extends FileSystemItem> objects) {
        var chooser = createChooser();
        var result = chooser.showSaveDialog(Main.frameInstance);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            objects.stream().map(x -> IOTask.createExportTask(x, path)).forEach(x -> IOQueue.addIOTask(x));
        }
    } 
}
