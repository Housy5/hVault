package vault;

import java.util.Arrays;
import vault.gui.FolderCreationDialog;
import vault.gui.PasswordDialog;
import vault.nfsys.Folder;
import vault.nfsys.FolderBuilder;
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
        String salt = user.salt;
        byte[] hash = Constants.messageDigest.digest(Main.mixPassAndSalt(password, salt).getBytes());
        
        if (Arrays.equals(hash, user.hash)) {
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
            Folder current = Main.frameInstance.user.fsys.getCurrentFolder();
            current.addFolder(FolderBuilder.createFolder(name, current));
            Main.reload();
            Main.saveUsers(); 
        }
    }
}
