package vault;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import vault.fsys.FilePointer;
import vault.fsys.Folder;

public class FileTransferHandler implements Transferable {

    private final TransferData data;
    
    public final static DataFlavor FILE_POINTER_LIST_FLAVOR = new DataFlavor(FilePointer.class, "File-Pointer-List-Flavor");
    
    public FileTransferHandler(List<FilePointer> pointers, Folder origin) {
        data = new TransferData(pointers, origin);
    }
    
    @Override
    public Object getTransferData(DataFlavor df) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(df)) {
            return data;
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
