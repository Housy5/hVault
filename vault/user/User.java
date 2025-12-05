package vault.user;

import java.io.Serializable;
import vault.fsys.FileSystem;
import vault.password.Password;

public class User implements Serializable {

    private FileSystem fsys;
    private Password password;
    private boolean showStartUpMsg;
    private boolean showWelcomeMsg;
    private String username;

    public User() {
        fsys = new FileSystem();
        showStartUpMsg = false;
        showWelcomeMsg = false;
    }
    
    public final FileSystem getFileSystem() {
        return fsys;
    }

    public final void setFileSystem(FileSystem fsys) {
        this.fsys = fsys;
    }
    
    public final Password getPassword() {
        return password;
    }
    
    public final void setPassword(Password pass) {
        password = pass;
    }
    
    public final void toggleStartUpMessage() {
        showStartUpMsg = !showStartUpMsg;
    }
    
    public final boolean showStartUpMessage() {
        return showStartUpMsg;
    }
    
    public final void toggleWelcomeMessage() {
        showWelcomeMsg = !showWelcomeMsg;
    }
    
    public final boolean showWelcomeMessage() {
        return showWelcomeMsg;
    }
    
    public final String getUsername() {
        return username;
    }
    
    public final void setUsername(String usrn) {
        username = usrn;
    }
    
}
