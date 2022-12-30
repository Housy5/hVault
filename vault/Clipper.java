package vault;

import vault.gui.MessageDialog;
import vault.fsys.Folder;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;

public class Clipper {

    private void reset() {
        item = null;
        origin = null;
        type = Type.NONE;
    }

    private static enum Type {
        COPY, CUT, NONE;
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
        if (item instanceof FilePointer pointer) {
            origin.addFilePointer(pointer);
        } else if (item instanceof Folder folder) {
            origin.addFolder(folder);
            folder.remap();
        }
        Main.reload();
    }

    private void pasteFilePointer(FilePointer filePointer) {
        Folder current = fsys.getCurrent();

        if (type == Type.COPY) {
            FilePointer copy = filePointer.copy();
            copy.setParent(current);
            current.addFilePointer(copy);
        } else if (type == Type.CUT) {
            filePointer.setParent(current);
            current.addFilePointer(filePointer);
            reset();
        }
    }

    public void pasteFolder(Folder fol) {
        Folder current = fsys.getCurrent();

        if (type == Type.COPY) {
            Folder copy = fol.copy();
            current.addFolder(copy);
            copy.setParent(current);
            copy.remap();
        } else {
            current.addFolder(fol);
            fol.setParent(current);
            fol.remap();
        }
    }

    public void paste() {
        if (item == null) {
            return;
        }

        if (fsys.getCurrent().isSearchFolder()) {
            MessageDialog.show(Main.frameInstance, "You are not allowed to paste stuff here " + Constants.ANGRY_FACE);
            return;
        }

        if (item instanceof FilePointer filePointer) {
            pasteFilePointer(filePointer);
        } else if (item instanceof Folder fol) {
            pasteFolder(fol);
        }

        Main.reload();
        Main.saveUsers();
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
            objOrigin.removeFilePointer(fp);
        } else if (obj instanceof Folder fol) {
            objOrigin.removeFolder(fol);
        }
        Main.reload();
        item = obj;
        origin = objOrigin;
    }
}
