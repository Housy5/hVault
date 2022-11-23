package vault.gui.menu;

import javax.swing.*;
import vault.gui.Frame;
import vault.user.User;

public class EmptyMenu extends JPopupMenu {
    
    protected User user;
    protected Frame frame;
    
    public EmptyMenu(Frame frame, User user) {
        this.frame = frame;
        this.user = user;
    }    
}