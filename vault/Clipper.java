package vault;

import java.util.List;
import vault.gui.MessageDialog;
import vault.fsys.Folder;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import vault.fsys.FileSystemItem;

public class Clipper {

    private void reset() {
        item = null;
        origin = null;
        type = Type.NONE;
    }

    private static enum Type {
        COPY, CUT, NONE, COPY_MANY, CUT_MANY;
    }

    private Object item;
    private Folder origin;
    private Type type;
    private final FileSystem fsys;

    public Clipper(FileSystem system) {
        fsys = system;
    }

    private boolean hasContent() {
        return item != null && type == Type.CUT && origin != null;
    }

    private void restore() {
        if (type == Type.CUT) {
            restoreSingleItem();
        } else if (type == Type.CUT_MANY) {
            restoreMultipleItems();
        }

        type = Type.NONE;
        item = null;
        origin = null;

        Main.reload();
    }

    private void restoreSingleItem() {
        if (item instanceof FilePointer pointer) {
            origin.addFilePointer(pointer);
        } else if (item instanceof Folder folder) {
            origin.addFolder(folder);
        }
    }

    private void restoreMultipleItems() {
        @SuppressWarnings("unchecked")
        List<FileSystemItem> items = (List<FileSystemItem>) item;
        for (FileSystemItem item : items) {
            if (item instanceof FilePointer pointer) {
                origin.addFilePointer(pointer);
            } else if (item instanceof Folder folder) {
                origin.addFolder(folder);
            }
        }
    }

    private void pasteFilePointer(FilePointer filePointer, Folder dest) {
        if (type == Type.COPY || type == Type.COPY_MANY) {
            FilePointer copy = filePointer.copy();
            copy.setParent(dest);
            dest.addFilePointer(copy);
        } else if (type == Type.CUT) {
            filePointer.setParent(dest);
            dest.addFilePointer(filePointer);
            reset();
        }
    }
    
    private void pasteFilePointer(FilePointer fp) {
        pasteFilePointer(fp, fsys.getCurrent());
    }

    private void pasteFolder(Folder fol, Folder dest) {
        if (type == Type.COPY || type == Type.COPY_MANY) {
            Folder copy = fol.copy();
            dest.addFolder(copy);
            copy.setParent(dest);
        } else {
            dest.addFolder(fol);
            fol.setParent(dest);
        }
    }
    
    private void pasteFolder(Folder fol) {
        pasteFolder(fol, fsys.getCurrent());
    }

    public void paste(Folder dest) {
        if (dest.isSearchFolder()) {
            MessageDialog.show(Main.frameInstance, "You are not allowed to paste stuff here " + Constants.ANGRY_FACE);
            return;
        }

        if (type == Type.COPY || type == Type.CUT) {
            if (item instanceof FilePointer filePointer) {
                pasteFilePointer(filePointer);
            } else if (item instanceof Folder fol) {
                if (fol.equals(dest)) {
                    origin.addFolder(fol);
                } else {
                    pasteFolder(fol);
                }
            }
        } else if (type == Type.COPY_MANY || type == Type.CUT_MANY) {
            @SuppressWarnings("unchecked")
            List<FileSystemItem> items = (List<FileSystemItem>) item;
            for (FileSystemItem item : items) {
                if (item instanceof FilePointer filePointer) {
                    pasteFilePointer(filePointer);
                } else if (item instanceof Folder fol) {
                    if (fol.equals(dest)) {
                        origin.addFolder(fol);
                        continue;
                    }
                    pasteFolder(fol);
                }
            }
        }

        Main.reload();
        Main.save();
    }

    public void paste() {
        paste(fsys.getCurrent());
        /*        if (item == null) {
        return;
        }
        
        if (fsys.getCurrent().isSearchFolder()) {
        MessageDialog.show(Main.frameInstance, "You are not allowed to paste stuff here " + Constants.ANGRY_FACE);
        return;
        }
        
        if (type == Type.COPY || type == Type.CUT) {
        if (item instanceof FilePointer filePointer) {
        pasteFilePointer(filePointer);
        } else if (item instanceof Folder fol) {
        if (fol.equals(fsys.getCurrent())) {
        origin.addFolder(fol);
        } else {
        pasteFolder(fol);
        }
        }
        } else if (type == Type.COPY_MANY || type == Type.CUT_MANY) {
        @SuppressWarnings("unchecked")
        List<FileSystemItem> items = (List<FileSystemItem>) item;
        for (FileSystemItem item : items) {
        if (item instanceof FilePointer filePointer) {
        pasteFilePointer(filePointer);
        } else if (item instanceof Folder fol) {
        if (fol.equals(fsys.getCurrent())) {
        origin.addFolder(fol);
        continue;
        }
        pasteFolder(fol);
        }
        }
        }
        
        Main.reload();
        Main.save();*/
    }

    public void copy(Object obj) {
        if (hasContent()) {
            restore();
        }

        type = Type.COPY;
        if (obj instanceof Folder fol) {
            item = fol;
        } else if (obj instanceof FilePointer hFile) {
            item = hFile;
        }
    }

    public void cut(Object obj, Folder objOrigin) {
        if (hasContent()) {
            restore();
        }

        type = Type.CUT;

        if (obj instanceof FilePointer fp) {
            objOrigin.removeFilePointerReference(fp);
        } else if (obj instanceof Folder fol) {
            objOrigin.removeFolderReference(fol);
        }
        Main.reload();
        item = obj;
        origin = objOrigin;
    }

    public void cutMany(List<FileSystemItem> items, Folder origin) {
        if (hasContent()) {
            restore();
        }

        type = Type.CUT_MANY;

        for (FileSystemItem item : items) {
            if (item instanceof FilePointer fp) {
                origin.removeFilePointerReference(fp);
            } else if (item instanceof Folder f) {
                origin.removeFolderReference(f);
            }
        }

        Main.reload();
        item = items;
        this.origin = origin;
    }

    public void copyMany(List<FileSystemItem> items) {
        if (hasContent()) {
            restore();
        }

        type = Type.COPY_MANY;
        item = items;
    }
}
