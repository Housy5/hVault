package vault.fsys;

import java.util.*;

public class FileSystem {

    private Folder root;
    private Folder current;
    private Map<String, Folder> folderMap;

    public FileSystem() {
        folderMap = new HashMap<>();
        root = FolderFactory.createRootFolder();
        current = root;
        folderMap.put(root.getPath(), root);
    }

    public final void transferFile(FilePointer pointer, Folder origin, Folder destination) {
        origin.removeFilePointer(pointer);
        destination.addFilePointer(pointer);
        pointer.setParent(destination);
    }
    
    public final void deleteFilePointer(FilePointer pointer) {
        pointer.getParent().deleteFilePointer(pointer);
    }
    
    public final List<FilePointer> search(String... keys) {
        return root.search(keys);
    }

    public final void moveTo(Folder folder) {
        current = folder;
    }

    public final Folder getRoot() {
        return root;
    }
    
    public final Folder getCurrent() {
        return current;
    }
    
    public final void addFolder(Folder folder) {
        folderMap.put(folder.getPath(), folder);
        current.addFolder(folder);
    }
    
    public final void addFilePointer(FilePointer pointer) {
        current.addFilePointer(pointer);
    }
    
    public final boolean removeFolder(Folder folder) {
        if (!current.removeFolder(folder))
            return false;
        folderMap.remove(folder.getPath());
        return true;
    }

    public final void removeAllPointers() {
        current.removeAllPointers();
    }
    
    public final boolean removeFilePointer(FilePointer pointer) {
        return pointer.getParent().removeFilePointer(pointer);
    }
    
    public final Folder findFolder(String path) {
        if (folderMap.containsKey(path))
            return folderMap.get(path);
        return null;
    }
}