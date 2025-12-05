package vault.fsys;

import java.util.*;

public class FileSystem {

    private Folder root;
    private Folder current;
    private final Map<String, Folder> folderMap;
    private Stack<Folder> previous, next;
    
    public FileSystem() {
        folderMap = new HashMap<>();
        root = FolderFactory.createRootFolder();
        current = root;
        folderMap.put(root.getPath(), root);
        previous = new Stack<>();
        next = new Stack<>();
    }

    public final void transferItems(List<FileSystemItem> items, Folder origin, Folder destination) {
        for (FileSystemItem item : items) {
            if (item instanceof FilePointer fp) {
                transferFile(fp, origin, destination);
            } else if (item instanceof Folder folder) {
                if (destination.equals(folder))
                    continue;
                transferFolder(folder, origin, destination);
            }
        }
    }
    
    
    public final void transferFile(FilePointer pointer, Folder origin, Folder destination) {
        origin.removeFilePointerReference(pointer);
        destination.addFilePointer(pointer);
        pointer.setParent(destination);
    }
    
    public final void transferFolder(Folder folder, Folder origin, Folder destination) {
        origin.removeFolderReference(folder);
        destination.addFolder(folder);
        folder.setParent(destination);
    }

    public final void deleteFilePointer(FilePointer pointer) {
        pointer.getParent().removeFilePointer(pointer);
    }

    public final List<FilePointer> search(String... keys) {
        return root.search(keys);
    }
    
    public final void clearBrowsingHistory() {
        previous.clear();
        next.clear();
    }

    public final void moveTo(Folder folder) {
        if (current.equals(folder)) {
            return;
        }
        
        if (folder.isSearchFolder()) {
            next.clear();
            current = folder;
            return;
        }
        
        previous.push(current);
        current = folder;
        if (next.isEmpty()) {
            return;
        }
        if (!next.peek().equals(folder)) {
            next.clear();
        } else {
            next.pop();
        }
    }

    public final boolean back() {
        if (current.isSearchFolder()) {
            current = current.getParent();
            return true;
        }
        if (previous.isEmpty())
            return false;
        next.push(current);
        current = previous.pop();
        return true;
    }
    
    public final boolean next() {
        if (next.isEmpty())
            return false;
        previous.push(current);
        current = next.pop();
        return true;
    }
    
    public final Folder getRoot() {
        return root;
    }

    public final void setRoot(Folder folder) {
        root = folder;
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
        boolean result = pointer.getParent().removeFilePointerReference(pointer);
        return result;
    }

    public final Folder findFolder(String path) {
        if (folderMap.containsKey(path))
            return folderMap.get(path);
        return null;
    }
}
