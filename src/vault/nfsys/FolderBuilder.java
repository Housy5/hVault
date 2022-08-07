package vault.nfsys;

import java.util.List;

public class FolderBuilder {

    public static Folder createRootFolder() {
        Folder root = new Folder();
        root.setName("Root");
        root.setParent(null);
        root.setFullName(root.getName());
        return root;
    }

    public static Folder createFolder(String name, Folder parent) {
        Folder folder = new Folder();
        folder.setName(name);
        folder.setFullName(parent.getFullName() + ":" + name);
        folder.setParent(parent);
        return folder;
    }

    public static Folder createLockedFolder(String name, Folder parent) {
        Folder folder = createFolder(name, parent);
        folder.setLocked(true);
        return folder;
    }
    
    public static Folder createParentFolder(Folder parent) {
        Folder folder = new Folder();
        folder.setName("..");
        folder.setParent(parent);
        return folder;
    }
    
    public static Folder createSearchFolder(Folder origin, List<FilePointer> content) {
        Folder folder = new Folder();
        folder.setName("Search");
        folder.setFullName("Search");
        folder.setParent(origin);
        folder.setFiles(content);
        folder.sortFiles();
        folder.setSearchFolder(true);
        return folder;
    }
}
