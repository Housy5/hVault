package vault;

import javax.swing.JOptionPane;
import vault.nfsys.Folder;
import vault.nfsys.FilePointer;

public class Clipper {

    private static enum Type {
        COPY, CUT, NONE;
    }

    private static Object item;
    private static Folder origin;
    private static Type type;

    private static boolean hasContent() {
        return item != null && type == Type.CUT && origin != null;
    }
    
    private static void restore() {
        if (item instanceof FilePointer pointer) {
            if (origin.containsFile(pointer)) {
                pointer.setName(NameUtilities.nextFileName(pointer.getName(), origin));
            }
            
            origin.addFile(pointer);
        } else if (item instanceof Folder folder) {
            if (origin.containsFolder(folder)){
                folder.setName(NameUtilities.nextFolderName(folder.getName(), origin));
            }
            
            origin.addFolder(folder);
            folder.remap(origin);
        }
        
        Main.frameInstance.loadFolder(Main.frameInstance.user.fsys.getCurrentFolder());
    }
    
    public static void paste() {
        if (item == null) {
            return;
        }

        var fsys = Main.frameInstance.user.fsys;
        
        if (fsys.getCurrentFolder().isSearchFolder()) {
            JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>You can't paste here!", "info", JOptionPane.INFORMATION_MESSAGE);
            return; 
        }

        if (item instanceof FilePointer filePointer) {
            Folder current = fsys.getCurrentFolder();
            String newName = null;
            
            if (current.containsFileName(filePointer.getName())) {
                newName = NameUtilities.nextFileName(filePointer.getName(), current);
                
                if (newName == null) {
                    JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>Couldn't paste the file \"" + filePointer.getName() + "\" over here. :(", "info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            
            if (type == Type.COPY) {
                FilePointer copy = filePointer.copy();
                if (newName != null) {
                    copy.setName(newName);
                }
                copy.setParent(current);
                current.addFile(copy);
            } else if (type == Type.CUT) {
                if (newName != null) {
                    filePointer.setName(newName);
                }
                filePointer.setParent(current);
                current.addFile(filePointer);
            }
            Main.frameInstance.loadFolder(fsys.getCurrentFolder());
        } else if (item instanceof Folder fol) {
            Folder current = fsys.getCurrentFolder();
            String newName = null;
            if (current.containsFolderName(fol.getName())) {
                newName = NameUtilities.nextFolderName(fol.getName(), current);

                if (newName == null) {
                    JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>Couldn't paste the folder \"" + fol.getName() + "\" over here. :(", "info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            if (type == Type.COPY) {
                Folder copy = fol.copy(); 
                if (newName != null){
                    copy.setName(newName);
                }
                current.addFolder(copy);
                copy.setParent(current);
                copy.remap(current);
            } else {
                if (newName != null) {
                    fol.setName(newName);
                }
                current.addFolder(fol);
                fol.setParent(current);
                fol.remap(current);                
            }

            Main.frameInstance.loadFolder(current);
        }

        Main.saveUsers();

        if (type == Type.CUT) {
            item = null;
            origin = null;
            type = Type.NONE;
        }
    }

    public static void copy(Object obj) {
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

    public static void cut(Object obj, Folder objOrigin) {
        if (hasContent()) {
            restore();
        }
        
        type = Type.CUT;
        
        if (obj instanceof FilePointer fp) {
            objOrigin.removeFilePointer(fp, false);
            Main.frameInstance.loadFolder(objOrigin);
        } else if (obj instanceof Folder fol) {
            objOrigin.removeFolderReference(fol);
            Main.frameInstance.loadFolder(objOrigin);
        }
        item = obj;
        origin = objOrigin;
    }
}
