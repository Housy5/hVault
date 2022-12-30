package vault.fsys;

import java.time.LocalDateTime;
import java.util.List;

public final class FolderFactory {
    
    private FolderFactory() {}
    
    public static Folder createFolder(Folder parent, String name) {
        Folder folder = new Folder();
        folder.setParent(parent);
        folder.setName(name);
        folder.setPath(parent.getPath(), name);
        folder.setCreationDate(LocalDateTime.now());
        return folder;
    }
    
    public static Folder createSearchFolder(Folder parent, List<FilePointer> pointers) {
        Folder folder = Folder.fromList(pointers);
        folder.setName("Search");
        folder.setParent(parent);
        folder.setPath("Search");
        folder.setCreationDate(LocalDateTime.now());
        folder.setSearchFolder(true);
        return folder;
    }
    
    public static Folder createRootFolder() {
        Folder root = new Folder();
        root.setName("Root");
        root.setParent(root);
        root.setPath(root.getName());
        return root;
    }
}
