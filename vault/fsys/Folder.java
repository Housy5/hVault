package vault.fsys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import vault.fsys.io.FileSystemFormat;
import vault.password.Password;

public final class Folder extends FileSystemItem {
     
    private boolean locked = false;
    private boolean search = false;
    private Password password;
    
    private List<FilePointer> pointers;
    private List<Folder> subFolders;
    
    public Folder() {
        pointers = new ArrayList<>();
        subFolders = new ArrayList<>();
    }
    
    private boolean containsKey(String name, String...keys) {
        return Arrays.stream(keys).parallel().filter(x -> name.toLowerCase().contains(x.toLowerCase())).count() > 0;
    }
    
    private List<FilePointer> searchFiles(String... keys) {
        return pointers.parallelStream().filter(x -> containsKey(x.getName(), keys)).toList();
    }
    
    private List<FilePointer> searchSubFolders(String... keys) {
        List<FilePointer> list = new ArrayList<>();
        subFolders.parallelStream().forEach(x -> list.addAll(x.search(keys)));
        return list;
    } 
    
    public final List<FilePointer> search(String...keys) {
        List<FilePointer> list = new ArrayList<>();
        list.addAll(searchFiles(keys));
        list.addAll(searchSubFolders(keys));
        return list;
    }
    
    public final List<Object> getAllItems() {
        List<Object> items = new ArrayList<>();
        items.addAll(subFolders);
        items.addAll(pointers);
        return items;
    }
    
    public final void removeAllPointers() {
        pointers.parallelStream().forEach(FilePointer::delete);
        pointers.clear();
    }
    
    public final List<Folder> getSubFolders() {
        return List.copyOf(subFolders);
    } 
    
    public final boolean containsPointerName(String target) {
        return pointers.parallelStream().filter(x -> x.getName().equalsIgnoreCase(target)).count() > 0;
    }
    
    public final boolean containsFolderName(String target) {
        return subFolders.parallelStream().filter(x -> x.getName().equalsIgnoreCase(target)).count() > 0;
    }
 
    public final Password getPassword() {
        return password;
    }
    
    public final void setPassword(Password password) {
        this.password = password;
    }
    
    public final void lock(Password password) {
        this.password = password;
    }
    
    public final void unlock() {
        password = null;
    } 
    
    public final boolean isLocked() {
        return password != null;
    }
    
    public final boolean isSearchFolder() {
        return search;
    }
    
    public final void setSearchFolder(boolean isSearchFolder) {
        search = isSearchFolder;
    }
    
    public final boolean addFilePointer(FilePointer pointer) {
        if (pointers.contains(pointer))
            return false;
        if (this.containsPointerName(pointer.getName()))
            pointer.nextName(this);
        return pointers.add(pointer);
    }
    
    public final List<FilePointer> getPointers() {
        return List.copyOf(pointers);
    }
    
    public final boolean removeFilePointerReference(FilePointer pointer) {
        return pointers.remove(pointer);
    }
    
    public final void removeFilePointer(FilePointer pointer) {
        pointer.delete();
        pointers.remove(pointer);
    }
    
    public final boolean addFolder(Folder folder) {
        if (subFolders.contains(folder))
            return false;
        if (containsFolderName(folder.getName()))
            folder.nextName(this);
        return subFolders.add(folder);
    }

    public final boolean removeFolderReference(Folder ref) {
        return subFolders.remove(ref);
    }
    
    public final boolean removeFolder(Folder folder) {
        folder.clearContent();
        return subFolders.remove(folder);
    }
    
    public final void clearContent() {
        clearFilePointers();
        clearSubFolders();
    }

    public final void clearSubFolders() {
        subFolders.forEach(x -> x.clearContent());
        subFolders.clear();
    }

    public final void clearFilePointers() {
        pointers.forEach(FilePointer::delete);
        pointers.clear();
    }
    
    @Override
    public long getSize() {
        long sum = pointers.stream().mapToLong(FilePointer::getSize).sum();
        sum += subFolders.stream().mapToLong(Folder::getSize).sum();
        return sum;
    }
    
    public final Folder copy() {
        Folder copy = new Folder();
        copy.setName(getName());
        copy.setCreationDate(LocalDateTime.now());
        copy.setPassword(password);
        pointers.parallelStream().map(FilePointer::copy).map(x -> x.setParent(copy)).map(x -> (FilePointer) x).forEach(x -> copy.addFilePointer(x));
        subFolders.parallelStream().map(Folder::copy).map(x -> x.setParent(copy)).map(x -> (Folder) x).forEach(x -> copy.addFolder(x));
        return copy;
    }

    public List<FilePointer> getAllFilePointers() {
        List<FilePointer> pointers = new ArrayList<>();
        pointers.addAll(this.pointers);
        subFolders.parallelStream().forEach(x -> pointers.addAll(x.getAllFilePointers()));
        return pointers;
    }
    
    public static Folder fromList(List<FilePointer> pointers) {
        Folder folder = new Folder();
        folder.pointers = pointers;
        return folder;
    }
}