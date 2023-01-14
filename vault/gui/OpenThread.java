package vault.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vault.Export;
import static vault.Main.frameInstance;
import vault.fsys.FilePointer;

public class OpenThread extends Thread {
    
    private final FilePointer pointer;
    
    public OpenThread(FilePointer pointer) {
        this.pointer = pointer;
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
            frameInstance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            var f = Export.exportTemporaryFile(pointer);
            while (!f.exists() || f.length() < pointer.getSize());
            Desktop.getDesktop().open(f);
            frameInstance.setCursor(Cursor.getDefaultCursor());
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(frameInstance, ex.getMessage());
        }
    }
}
