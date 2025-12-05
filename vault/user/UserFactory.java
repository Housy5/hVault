package vault.user;

import vault.fsys.FileSystem;
import vault.fsys.FolderFactory;
import vault.password.Password;

public class UserFactory {
    
    public static final User createUser(String username, String password) {
        User user = new User();
        FileSystem fs = user.getFileSystem();
        fs.addFolder(FolderFactory.createFolder(fs.getRoot(), "Videos"));
        fs.addFolder(FolderFactory.createFolder(fs.getRoot(), "Images"));
        fs.addFolder(FolderFactory.createFolder(fs.getRoot(), "Documents"));
        fs.addFolder(FolderFactory.createFolder(fs.getRoot(), "Audio"));
        user.setUsername(username);
        user.setPassword(new Password(password));
        return user;
    }
}
