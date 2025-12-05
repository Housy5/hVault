package vault.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;

public class FSITransferHandler implements Transferable {

    
    public final static DataFlavor FILE_POINTER_LIST_FLAVOR = new DataFlavor(FilePointer.class, "File-Pointer-List-Flavor");

    private List<FileSystemItem> items;
    private Folder origin;
    
    public FSITransferHandler(List<FileSystemItem> items, Folder origin) {
        this.items = items;
        this.origin = origin;
    }
    
    @Override
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(df)) {
            return new FSITransferData(items, origin);
        } else {
            throw new UnsupportedFlavorException(df);
        }
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {FILE_POINTER_LIST_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(FILE_POINTER_LIST_FLAVOR);
    }

}
