package vault.gui.menu;

import java.awt.EventQueue;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import vault.*;
import vault.gui.*;
import vault.fsys.*;
import vault.password.Password;
import vault.queue.*;
import vault.user.*;

public class DefaultMenu extends EmptyMenu {

    private static final long serialVersionUID = 1L;

    private JMenuItem createExportSelectionItem() {
        var exportSelection = new JMenuItem("Export the Selected Items");
        exportSelection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                var tiles = frame.getSelectedTiles();
                List<FileSystemItem> objs = new ArrayList<>();
                tiles.forEach(tile -> {
                    if (tile.isFile()) {
                        objs.add(tile.file);
                    } else if (tile.isFolder()) {
                        objs.add(tile.folder);
                    }
                });
                Export.exportAll(objs);
            }
        });
        
        return exportSelection;
    }
    
    private JMenuItem createDeleteSelectionItem() {
        var deleteSelection = new JMenuItem("Delete the Selected Items");
        deleteSelection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                var tiles = frame.getSelectedTiles();
                var fsys = frame.user.getFileSystem();
                
                tiles.stream().filter(x -> x.isFile()).forEach(x -> fsys.removeFilePointer(x.file));
                tiles.stream().filter(x -> x.isFolder()).filter(x-> !x.folder.isLocked()).forEach(x -> fsys.removeFolder(x.folder));
                
                frame.loadFolder(fsys.getCurrent());
                Main.saveUsers();
            }
        });
        return deleteSelection;
    }
    
    private JMenuItem createRefreshItem() {
        var refresh = new JMenuItem("Refresh");
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e == null) {
                    return;
                }
                frame.loadFolder(user.getFileSystem().getCurrent());
            }
        });
        return refresh;
    }

    private JMenuItem createExportAllItem() {
        var exportAll = new JMenuItem("Export All Files");
        exportAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int x = Util.requestPassword();

                    if (x == Util.PASSWORD_ACCEPTED) {
                        Export.exportAll(user.getFileSystem().getCurrent().getPointers());
                    } else if (x == Util.PASSWORD_DENIED) {
                        MessageDialog.show(frame, Constants.ACCESS_DENIED_TEXT);
                    }
                }
            }
        });
        return exportAll;
    }

    private JMenuItem createDeleteAllItem() {
        var deleteAll = new JMenuItem("Delete All Files");
        deleteAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int opt = ConfirmDialog.show(Main.frameInstance, "(Cannot be undone) Are you sure you want to delete every file in this folder?");
                    if (opt == ConfirmDialog.YES) {
                        user.getFileSystem().removeAllPointers();
                        frame.loadFolder(user.getFileSystem().getCurrent());
                        Main.saveUsers();
                    }
                }
            }
        });
        return deleteAll;
    }

    private JMenuItem createNewFolderItem() {
        var addFolder = new JMenuItem("New Folder");
        addFolder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Util.createFolder();
                }
            }
        });
        return addFolder;
    }

    private JMenuItem createNewFileItem() {
        var addItem = new JMenuItem("New File");
        addItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    frame.addFile();
                }
            }
        });
        return addItem;
    }

    private JMenuItem createPasteItem() {
        var paste = new JMenuItem("Paste");
        paste.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Main.frameInstance.getClipper().paste(); 
                }
            }
        });
        return paste;
    }

    private JMenuItem createLogoutItem() {
        var logout = new JMenuItem("Log Out");
        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e == null) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {

                    if (ImportQueue.instance().isImporting()
                            || ExportQueue.instance().isExporting()) {
                        MessageDialog.show(frame, "You can't log out while importing or exporting files!");
                        return;
                    }

                    int x = ConfirmDialog.show(frame, "Are you sure you want to log out?");                    
                    if (x == ConfirmDialog.YES) {
                        ImportQueue.instance().stop();
                        ExportQueue.instance().stop();
                        frame.stopProgressTimer();
                        Export.stopIOMonitor();
                        LoginFrame lf = new LoginFrame();
                        lf.setLocationRelativeTo(Main.frameInstance);
                        EventQueue.invokeLater(() -> lf.setVisible(true));
                        Main.frameInstance.dispose();
                        Main.frameInstance = null;
                        return;
                    }

                    frame.loadFolder(user.getFileSystem().getCurrent());
                    Main.saveUsers();
                }
            }
        });
        return logout;
    }

    private JMenuItem createEnableWelcomeMsgItem() {
        var enableWelcomeMsg = new JMenuItem("Enable Welcome Message");
        enableWelcomeMsg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    user.toggleWelcomeMessage();
                    Main.saveUsers();
                }
            }
        });
        return enableWelcomeMsg;
    }

    private JMenuItem createPasswordItem() {
        var changePassword = new JMenuItem("Change Password");
        changePassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int x = Util.requestPassword();

                if (x == Util.PASSWORD_ACCEPTED) {
                    var dialog = new NewPasswordDialog(Main.frameInstance);
                    String newPass = dialog.getPassword();
                    if (newPass == null || newPass.isBlank())
                        return;
                    user.setPassword(new Password(newPass));
                    MessageDialog.show(frame, "Your password has been updated!");
                } else if (x == Util.PASSWORD_DENIED) {
                    MessageDialog.show(frame, Constants.ACCESS_DENIED_TEXT);
                }
            }
        });
        return changePassword;
    }

    private JMenuItem createSearchItem() {
        var search = new JMenuItem("Search");
        search.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                SearchDialog dialog = new SearchDialog(Main.frameInstance, true);
                int opt = dialog.open();
                if (opt == SearchDialog.SEARCH) {
                    String[] keywords = dialog.keywordsAsArray();
                    var foundItems = user.getFileSystem().search(keywords);
                    if (!foundItems.isEmpty()) {
                        Folder searchFolder = FolderFactory.createSearchFolder(user.getFileSystem().getCurrent(), foundItems);
                        frame.loadFolder(searchFolder);
                    }
                }
            }
        });
        return search;
    }

    public DefaultMenu(Frame frame, User user) {
        super(frame, user);

        if (frame.getSelectionCount() > 0) {
            add(createExportSelectionItem());
            add(createDeleteSelectionItem());
            add(new JSeparator());
        }
        
        if (user.getFileSystem().getCurrent().getPointers().size() > 1)  {
            add(createExportAllItem());
            add(createDeleteAllItem());
            add(new JSeparator());
        }
        
        add(createNewFolderItem());
        add(createNewFileItem());
        add(new JSeparator());
        add(createSearchItem());
        add(new JSeparator());
        add(createPasteItem());
        add(new JSeparator());
        add(createLogoutItem()); 
        add(createPasswordItem());
        
        if (!user.showWelcomeMessage()) {
            add(new JSeparator());
            add(createEnableWelcomeMsgItem());
        }
    }

}
