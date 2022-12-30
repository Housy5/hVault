package vault.user;

import java.io.Serializable;
import vault.fsys.FileSystem;
import vault.password.Password;

public class User implements Serializable {

    private transient FileSystem fsys;
    private transient UserSaver saver;
    private Password password;
    private boolean showStartUpMsg;
    private boolean showWelcomeMsg;
    private String username;

    public User() {
        fsys = new FileSystem();
        showStartUpMsg = true;
        showWelcomeMsg = true;
    }
    
    public final void setSaver(UserSaver saver) {
        this.saver = saver;
    }
    
    public final FileSystem getFileSystem() {
        return fsys;
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
    
    public final void init() {
        fsys = new FileSystem();
        saver = new UserSaver(this);
        saver.load();
    }
    
    public final void save() {
        saver.save();
    }
}
