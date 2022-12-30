package vault;

import java.util.Arrays;
import vault.gui.FolderCreationDialog;
import vault.gui.PasswordDialog;
import vault.fsys.Folder;
import vault.fsys.FolderFactory;
import vault.user.User;

public class Util {
    
    public static final int PASSWORD_ACCEPTED = 0;
    public static final int PASSWORD_DENIED = 1; 
    public static final int CANCEL  = 2;
    
    public static int requestPassword() {
        String password = new PasswordDialog(true).getPassword();
        if (password == null || password.isBlank()) {
            return CANCEL;
        }
        
        User user = Main.frameInstance.user;
        
        if (user.getPassword().unlock(password)) {
            return PASSWORD_ACCEPTED;
        } else {
            return PASSWORD_DENIED;
        }
    }
    
    public static int requestFolderPassword(Folder folder) {
        String password = new PasswordDialog(true).getPassword();
        if (password == null || password.isBlank()) {
            return CANCEL;
        }
        
        if (folder.getPassword().unlock(password)) {
            return PASSWORD_ACCEPTED;
        } else {
            return PASSWORD_DENIED;
        }
    }
    
    public static void createFolder() {
        var dialog = new FolderCreationDialog(Main.frameInstance, true);
        int opt = dialog.showDialog();
        if (opt == FolderCreationDialog.CREATE_OPTION) {
            String name = dialog.getFolderName();
            Folder current = Main.frameInstance.user.getFileSystem().getCurrent();
            current.addFolder(FolderFactory.createFolder(current, name));
            Main.reload();
            Main.saveUsers(); 
        }
    }
}
