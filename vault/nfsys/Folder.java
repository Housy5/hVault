package vault.nfsys;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import vault.NameUtilities;

public class Folder implements Serializable {

    private String name;
    private String fullName;

    private Folder parent;

    private volatile List<Folder> folders;
    private volatile List<FilePointer> files;
    private boolean searchFolder = false;
    private boolean locked = false;

    public Folder() {
        folders = new ArrayList<>();
        files = new ArrayList<>();
    }

    public boolean containsFolderNameExcluded(String newName, Folder folder) {
        for (Folder fol : folders) {
            if (fol.getName().equalsIgnoreCase(newName) && fol != folder) {
                return true;
            }
        }
        return false;
    }
    
    public boolean containsFileNameExcluded(String newName, FilePointer exclude) {
        for (FilePointer pointer : files) {
            if (pointer.getName().equalsIgnoreCase(newName) && pointer != exclude) {
                return true;
            }
        }
        return false;
    }

    public boolean isSearchFolder() {
        return searchFolder;
    }

    public void setSearchFolder(boolean searchFolder) {
        this.searchFolder = searchFolder;
    }

    List<FilePointer> search(String[] keys) {
        List<FilePointer> pointers = new ArrayList<>();

        for (FilePointer pointer : files) {
            for (String key : keys) {
                if (pointer.getName().toLowerCase().contains(key.trim().toLowerCase())) {
                    pointers.add(pointer);
                }
            }
        }

        for (Folder fol : folders) {
            pointers.addAll(fol.search(keys));
        }

        return pointers;
    }

    void loadFileIDDatabase() {
        for (FilePointer pointer : files) {
            pointer.addIDToDatabase();
        }

        for (Folder folder : folders) {
            folder.loadFileIDDatabase();
        }
    }

    public int getFileCount() {
        return files.size();
    }

    public int getFolderCount() {
        return folders.size();
    }

    protected void setFolders(List<Folder> folders) {
        this.folders = folders;
    }

    protected void setFiles(List<FilePointer> files) {
        this.files = files;
    }

    public void addFolder(Folder folder) {
        if (folder == null) {
            return;
        }

        if (containsFolderName(folder.getName())) {
            final String newName = NameUtilities.nextFolderName(name, this);
            if (newName == null) {
                return;
            }
            folder.setName(newName);
        }
        
        folders.add(folder);
        sortFolders();
    }

    protected void removeContent() {
        for (FilePointer file : files) {
            removeFile(file);
        }
        for (Folder fol : folders) {
            fol.removeContent();
        }
        files.clear();
        folders.clear();
    }

    public boolean containsFolder(Folder folder) {
        if (folder == null) {
            throw new NullPointerException();
        }
        return folders.contains(folder);
    }

    public boolean containsFolderName(String folderName) {
        return folders.stream().filter(folder -> folder.getName().equalsIgnoreCase(folderName)).count() > 0;
    }

    public void removeFolderReference(Folder folder) {
        if (folder != null && folders.contains(folder)) {
            folders.remove(folder);
            sortFolders();
        }
    }

    void removeFolder(Folder folder) {
        if (folder != null && folders.contains(folder)) {
            folder.removeContent();
            folders.remove(folder);
            sortFolders();
        }
    }

    public void addFile(FilePointer fp) {
        if (fp == null || files.contains(fp)) {
            return;
        }

        files.add(fp);
        sortFiles();
    }

    public boolean containsFile(FilePointer file) {
        if (file == null) {
            throw new NullPointerException();
        }
        return files.contains(file);
    }

    public boolean containsFileName(String fileName) {
        return files.stream().filter((fp) -> fp.getName().equalsIgnoreCase(fileName)).count() > 0;
    }

    public void removeFilePointer(FilePointer pointer, boolean prepare) {
        if (pointer != null) {
            if (prepare) {
                pointer.prepareForRemoval();
            }
            files.remove(pointer);
            sortFiles();
        }
    }

    public void removeFilePointer(FilePointer pointer) {
        removeFilePointer(pointer, true);
    }

    public void removeFile(FilePointer file) {
        if (file != null && files.contains(file)) {
            var value = file.getValue();

            value.removeReference(file);
            file.prepareForRemoval();
            sortFiles();
        }
    }

    public void removeItems(List<Object> objs) {
        List<FilePointer> files = new ArrayList<>();
        List<Folder> folders = new ArrayList<>();
    }
    
    public void removeAllFiles() {
        for (FilePointer pointer : files) {
            removeFile(pointer);
        }
        files.clear();
    }

    public void sortFolders() {
        var sorted = false;

        while (!sorted) {
            sorted = true;

            for (int i = 1; i < folders.size(); i++) {
                var folder = folders.get(i);
                var prevFolder = folders.get(i - 1);

                if (folder.getName().compareToIgnoreCase(prevFolder.getName()) < 0) {
                    Collections.swap(folders, i, i - 1);
                    sorted = false;
                }
            }
        }
    }

    public void sortFiles() {
        var sorted = files.isEmpty();

        while (!sorted) {
            sorted = true;

            for (int i = 1; i < files.size(); i++) {
                var file = files.get(i);
                var prevFile = files.get(i - 1);

                if (file.getName().compareToIgnoreCase(prevFile.getName()) < 0) {
                    Collections.swap(files, i, i - 1);
                    sorted = false;
                }
            }
        }
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Folder> getFolders() {
        return Collections.unmodifiableCollection(folders);
    }

    public Collection<FilePointer> getFiles() {
        return Collections.unmodifiableCollection(files);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public void remap(Folder parent) {
        fullName = parent.fullName + ":" + name;

        for (Folder folder : folders) {
            folder.remap(this);
        }
    }

    public void validateFiles() {
        for (int i = 0; i < files.size(); i++) {
            FilePointer pointer = files.get(i);

            if (!new File(pointer.getLocation()).exists()) {
                removeFilePointer(pointer);
                i--;
            }
        }

        folders.forEach((Folder folder) -> folder.validateFiles());
    }

    public Folder copy() {
        List<Folder> foldersCopy = new ArrayList<>();
        List<FilePointer> filesCopy = new ArrayList<>();
        Folder folder = new Folder();

        if (folders.isEmpty()) {
            for (Folder fol : folders) {
                Folder f = fol.copy();
                f.setParent(folder);
                foldersCopy.add(f);
            }
        }

        if (files.isEmpty()) {
            for (FilePointer file : files) {
                FilePointer copy = file.copy();
                copy.setParent(folder);
                filesCopy.add(copy);
            }
        }

        folder.setName(new String(name));
        folder.setFiles(filesCopy);
        folder.setFolders(foldersCopy);
        folder.setLocked(isLocked());

        return folder;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.fullName);
        hash = 83 * hash + Objects.hashCode(this.parent);
        hash = 83 * hash + Objects.hashCode(this.folders);
        hash = 83 * hash + Objects.hashCode(this.files);
        hash = 83 * hash + Objects.hashCode(this.searchFolder);
        hash = 83 * hash + Objects.hashCode(this.locked);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Folder other = (Folder) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.fullName, other.fullName)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        if (!Objects.equals(this.folders, other.folders)) {
            return false;
        }
        if (!Objects.equals(this.locked, other.locked)) {
            return false;
        }
        if (!Objects.equals(this.searchFolder, other.searchFolder)) {
            return false;
        }
        return Objects.equals(this.files, other.files);
    }
}
