package vault.nfsys;

import java.io.Serializable;
import java.util.List;

public class FileSystem implements Serializable {

    private Folder current;
    private final Folder root;


    /**
     * FileSystem Constructor-
     */
    public FileSystem() {
        root = FolderBuilder.createRootFolder();
    }
    
    /**
     * Returns a list of FilePointers that have one of the keys in it's name
     * @param keys The keys words to find in the names
     * @return 
     */
    public List<FilePointer> search(String[] keys) {
        return List.copyOf(root.search(keys));
    }

    
    /**
     * Tries to find the specified folder
     * @param path
     * @return 
     */
    public Folder findFolder(String path) {
        Folder curr = root;
        var tokens = path.split(":");
        var index = 1;

        if (tokens.length == 1) {
            return root;
        }

        outer:
        while (!curr.getFullName().equalsIgnoreCase(path)) {
            for (var folder : curr.getFolders()) {
                if (folder.getName().equalsIgnoreCase(tokens[index])) {
                    curr = folder;
                    index++;
                    continue outer;
                }
            }
            return null;
        }

        return curr;
    }

    /**
     * Recursively registers all file id's in the system
     */
    public void indexFileIDs() {
        root.loadFileIDDatabase();
    }

    /**
     * Returns the root folder 
     * @return the root folder
     */
    public Folder getRoot() {
        return root;
    }

    /**
     * Returns the current folder
     * @return the current folder
     */
    public Folder getCurrentFolder() {
        return current;
    }

    /**
     * Attempts to move to the specified folder
     * @param folder The specified folder
     */
    public void cd(Folder folder) {
        if (folder == null) {
            throw new NullPointerException("Can't move to an empty folder!");
        }
        current = folder;
    }

    /**
     * Attempts to remove the specified folder
     * @param folder 
     */
    public void removeFolder(Folder folder) {
        if (folder.equals(root)) {
            return;
        }
        folder.getParent().removeFolder(folder);
    }

    /**
     * Attempts to remove the specified file from the specified folder
     * @param file the file to be removed
     * @param parent the specified folder
     */
    public void removeFile(FilePointer file) {
        file.getParent().removeFile(file);
        file.getParent().removeFilePointer(file);
    }

    /**
     * Recursively validates the integrity of all the files in the system.
     */
    public void validateFiles() {
        getRoot().validateFiles();
    }
}
