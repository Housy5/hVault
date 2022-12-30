package vault.fsys;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import vault.fsys.io.FileSystemFormat;
import vault.password.Password;

public final class Folder extends FileSystemItem implements FileSystemFormat {
     
    private boolean locked = false;
    private boolean search = false;
    private Password password;
    private String path, parentPath;
    
    private List<FilePointer> pointers;
    private List<Folder> subFolders;
    
    public Folder() {
        pointers = new ArrayList<>();
        subFolders = new ArrayList<>();
    }
    
    private boolean containsKey(String name, String...keys) {
        return Arrays.stream(keys).parallel().filter(x -> name.contains(x)).count() > 0;
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
        List<FilePointer> list = searchFiles(keys);
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
  
    public final String getParentPath() {
        return parentPath;
    }
    
    public final void setPath(String... elements) {
        var sb = new StringBuilder();
        Arrays.stream(elements).forEach(x -> sb.append(":").append(x));
        path = sb.substring(1); //element 0 will always be a ':'.
    }
    
    public final String getPath() {
        return path;
    }
    
    public final void setPassword(Password pass) {
        password = pass;
    }
    
    public final Password getPassword() {
        return password;
    }
    
    public final void setLocked(boolean locked) {
        this.locked = locked;
    } 
    
    public final boolean isLocked() {
        return locked;
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
    
    public final boolean removeFilePointer(FilePointer pointer) {
        return pointers.remove(pointer);
    }
    
    public final void deleteFilePointer(FilePointer pointer) {
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
        copy.setLocked(locked);
        copy.setPassword(password);
        pointers.parallelStream().map(FilePointer::copy).map(x -> x.setParent(copy)).map(x -> (FilePointer) x).forEach(x -> copy.addFilePointer(x));
        subFolders.parallelStream().map(Folder::copy).map(x -> x.setParent(copy)).map(x -> (Folder) x).forEach(x -> copy.addFolder(x));
        return copy;
    }

    private List<FilePointer> getAllFilePointers() {
        List<FilePointer> pointers = new ArrayList<>();
        pointers.addAll(this.pointers);
        subFolders.parallelStream().forEach(x -> pointers.addAll(x.getAllFilePointers()));
        return pointers;
    }
    
    private List<String> getFileSaveData() {
        var pointers = getAllFilePointers();
        return pointers.parallelStream().map(x -> x.format()).toList();
    }
    
    public final List<String> getSaveData() {
        List<String> data = new ArrayList<>();
        subFolders.forEach(x -> data.add(x.format()));
        subFolders.forEach(x -> data.addAll(x.getSaveData()));
        if (getName().equals("Root")) {
            data.addAll(getFileSaveData());
        }
        return data;
    }
    
    @Override
    public String format() {
        //type::name::path::parent_url::pasword_data::locked_flag::search_flag::creation_data_as_string
        String password = this.password == null ? "null" : this.password.toString();
        return "folder::" + getName() + "::" + getPath() + "::" + getParent().getPath() + "::" + password + "::" + Boolean.toString(locked) + "::" + Boolean.toString(search) + "::" + getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Override
    public void parse(String data) {
        var arr = data.split("::");
        if (!arr[0].equals("folder"))
            throw new IllegalArgumentException();
        this.setName(arr[1]);
        this.path = arr[2];
        this.parentPath = arr[3];
        this.setPassword(Password.parse(arr[4]));
        this.locked = Boolean.parseBoolean(arr[5]);
        this.search = Boolean.parseBoolean(arr[6]);
        this.setCreationDate(LocalDateTime.parse(arr[7]));
    }
    
    public final void remap() {
        setPath(getParent().getPath(), getName());
        subFolders.forEach(Folder::remap);
    }
    
    public static Folder fromList(List<FilePointer> pointers) {
        Folder folder = new Folder();
        folder.pointers = pointers;
        return folder;
    }
}