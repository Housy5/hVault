package vault.gui.dnd;

import vault.fsys.*;
import java.util.*;

public class FSITransferData {
    
    private Folder origin;
    private List<FileSystemItem> items;

    public FSITransferData(List<FileSystemItem> items, Folder origin) {
        this.origin = origin;
        this.items = items;
    }

    public Folder getOrigin() {
        return origin;
    }

    public List<FileSystemItem> getItems() {
        return items;
    }
}
