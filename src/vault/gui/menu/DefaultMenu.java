/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault.gui.menu;

import java.awt.EventQueue;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import vault.*;
import vault.gui.*;
import vault.nfsys.*;
import vault.queue.*;
import vault.user.*;

public class DefaultMenu extends EmptyMenu {

    private static final long serialVersionUID = 1L;

    private JMenuItem createExportSelectionItem() {
        var exportSelection = new JMenuItem("Export The Selected Items");
        exportSelection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                var tiles = frame.getSelectedTiles();
                List<Object> objs = new ArrayList<>();
                tiles.forEach(tile -> {
                    if (tile.isFile()) {
                        objs.add(tile.file);
                    } else if (tile.isFolder()) {
                        objs.add(tile.folder);
                    }
                });
                Export.exportAllV2(objs);
            }
        });
        
        return exportSelection;
    }
    
    private JMenuItem createRefreshItem() {
        var refresh = new JMenuItem("Refresh");
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e == null) {
                    return;
                }
                frame.loadFolder(user.fsys.getCurrentFolder());
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
                        Export.exportAll(user.fsys.getCurrentFolder().getFiles());
                    } else if (x == Util.PASSWORD_DENIED) {
                        JOptionPane.showMessageDialog(Main.frameInstance,
                                Constants.ACCESS_DENIED_TEXT,
                                "info",
                                JOptionPane.INFORMATION_MESSAGE);
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
                if (e == null) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int x = JOptionPane.showConfirmDialog(Main.frameInstance,
                            "(Cannot be undone) Are you sure you want to delete every file in this folder?");

                    if (x == JOptionPane.YES_OPTION) {
                        user.fsys.getCurrentFolder().removeAllFiles();
                        frame.loadFolder(user.fsys.getCurrentFolder());
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
                    Tile.addFolder();
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
                    Clipper.paste();
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

                    if (!Export.exportTasks.isEmpty()
                            || !Export.importTasks.isEmpty()
                            || ImportQueue.instance().isImporting()
                            || ExportQueue.instance().isExporting()) {
                        JOptionPane.showMessageDialog(Main.frameInstance, "When exporting or importing files, you can't log out!",
                                "info",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    int x = JOptionPane.showConfirmDialog(Main.frameInstance, "Would you like to log out?");

                    if (x == JOptionPane.YES_OPTION) {
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

                    frame.loadFolder(user.fsys.getCurrentFolder());
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
                if (e == null) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(e)) {
                    user.showWelcomeMsg = true;
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

                    if (newPass == null) {
                        return;
                    }

                    user.hash = Constants.messageDigest.digest(Main.mixPassAndSalt(newPass, user.salt).getBytes());
                    Main.saveUsers();
                    JOptionPane.showMessageDialog(Main.frameInstance,
                            "Your password has been successfully updated!",
                            "info",
                            JOptionPane.INFORMATION_MESSAGE);
                } else if (x == Util.PASSWORD_DENIED) {
                    JOptionPane.showMessageDialog(Main.frameInstance, Constants.ACCESS_DENIED_TEXT, "info", JOptionPane.INFORMATION_MESSAGE);
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
                String input = JOptionPane.showInputDialog(Main.frameInstance, "Your search keywords (separated by ','):");
                if (input != null) {
                    String[] keyWords = input.replaceAll(" ", "").split(",");
                    var foundItems = user.fsys.search(keyWords);
                    if (!foundItems.isEmpty()) {
                        Folder searchFolder = FolderBuilder.createSearchFolder(user.fsys.getCurrentFolder(), foundItems);
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
            add(new JSeparator());
        }
        
        if (user.fsys.getCurrentFolder().getFileCount() > 1)  {
            add(createExportAllItem());
            add(createDeleteAllItem());
            add(new JSeparator());
        }
        
        add(createNewFolderItem());
        add(createNewFileItem());
        add(new JSeparator());
        add(createSearchItem());
        add(new JSeparator());
        add(createLogoutItem()); 
        add(createPasswordItem());
        add(new JSeparator());
        add(createSearchItem());
        
        if (!user.showWelcomeMsg) {
            add(new JSeparator());
            add(createEnableWelcomeMsgItem());
        }
    }

}
