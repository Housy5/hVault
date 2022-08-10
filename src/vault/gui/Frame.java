package vault.gui;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import vault.Export;
import vault.Main;
import vault.Clipper;
import vault.Constants;
import vault.FileTransferHandler;
import vault.ImportQueue;
import vault.ImportTicket;
import vault.Util;
import vault.nfsys.Folder;
import vault.nfsys.FilePointer;
import vault.nfsys.FolderBuilder;
import vault.user.User;

public final class Frame extends javax.swing.JFrame {

    public User user;

    private final int maxTitleLength = 50;

    public Frame(User user) {
        initComponents();
        this.user = user;
        setLocationRelativeTo(null);

        setTitle(user.username + "'s  Vault!");

        user.fsys.indexFileIDs();
        user.fsys.validateFiles();
        Main.saveUsers();
        user.fsys.cd(user.fsys.getRoot());

        jPanel1.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    Folder folder = user.fsys.getCurrentFolder();
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    if (evt.isDataFlavorSupported(FileTransferHandler.FILE_POINTER_LIST_FLAVOR)) {
                        return;
                    }
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    addFile(droppedFiles, folder);
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        try {
            this.setIconImage(ImageIO.read(getClass().getResource("/res/vault.png")));
        } catch (IOException ex) {
        }

    }

    private String createTitleMessage(String fullname) {
        String result = fullname;
        if (fullname.length() > maxTitleLength) {
            result = "..." + fullname.substring(fullname.length() - (maxTitleLength + 1), fullname.length() - 1);
        }
        return result;
    }

    public final static String IDLE_STATE = "Idling...";
    public final static String PROGRESS_STATE = "%d/%d Files.";
    public final static String DELETING_STATE = "Deleting Stuff...";
    
    public void showState(String state) {
        jLabel1.setText(state);
    } 
    
    /**
     * Loads the specified folder to the screen.
     *
     * @param folder
     */
    public void loadFolder(Folder folder) {
        user.fsys.cd(folder);
        DragSource dragSource = new DragSource();
        String titleMsg = createTitleMessage(folder.getFullName());
        ((TitledBorder) jPanel1.getBorder()).setTitle(titleMsg);

        for (var comp : jPanel1.getComponents()) {
            if (comp instanceof Tile) {
                jPanel1.remove(comp);
            }
        }

        if (folder.getParent() != null) {
            var parentTile = new Tile("..", folder.getParent());
            jPanel1.add(parentTile);
        }

        for (var fol : folder.getFolders()) {
            var tile = new Tile(fol);
            jPanel1.add(tile);
        }

        for (var hFile : folder.getFiles()) {
            var tile = new Tile(hFile);
            jPanel1.add(tile);
            dragSource.createDefaultDragGestureRecognizer(tile, DnDConstants.ACTION_MOVE, (DragGestureEvent e) -> {
                var cursor = Cursor.getDefaultCursor();
                if (e.getDragAction() == DnDConstants.ACTION_MOVE) {
                    cursor = DragSource.DefaultMoveDrop;
                }
                e.startDrag(cursor, createTransferable(hFile));
            });
        }

        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private Transferable createTransferable(FilePointer pointer) {
        List<FilePointer> selectedFiles = new ArrayList<>();
        selectedFiles.add(pointer);

        for (var comp : jPanel1.getComponents()) {
            if (comp instanceof Tile tile) {
                if (tile.isSelected() && tile.type == Tile.FileType.FILE && !selectedFiles.contains(tile.file)) {
                    selectedFiles.add(tile.file);
                }
            }
        }

        return new FileTransferHandler(selectedFiles, user.fsys.getCurrentFolder());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Vault");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Folder Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 14))); // NOI18N
        jPanel1.setName(""); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel1MouseReleased(evt);
            }
        });
        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEADING);
        flowLayout1.setAlignOnBaseline(true);
        jPanel1.setLayout(flowLayout1);
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setDoubleBuffered(false);
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabel1.setText("Idling...");
        jPanel2.add(jLabel1);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Adds the specified files to the specified folder
     *
     * @param f The file to be imported
     * @param folder The destination folder
     */
    public void addFile(List<File> f, Folder folder) {
        ImportQueue queue = ImportQueue.instance();
        queue.enqueue(new ImportTicket(f, folder));
    }

    /**
     * Don't ask questions
     */
    private void addFile() {
        var chooser = new JFileChooser();
        chooser.showOpenDialog(jPanel1);

        var file = chooser.getSelectedFile();
        var folder = user.fsys.getCurrentFolder();

        if (file != null) {
            addFile(List.of(file), folder);
        }
    }

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        if (evt == null) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(evt)) {
            
            if (user.fsys.getCurrentFolder().isSearchFolder()) {
                return;
            }
            
            var menu = new JPopupMenu();

            var refresh = new JMenuItem("Refresh");
            refresh.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e == null) {
                        return;
                    }
                    loadFolder(user.fsys.getCurrentFolder());
                }
            });

            var exportAll = new JMenuItem("Export All Files");
            exportAll.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int x = Util.requestPassword();
                        
                        if (x == Util.PASSWORD_ACCEPTED) {
                            Export.exportAll(user.fsys.getCurrentFolder().getFiles());
                        } else if (x == Util.PASSWORD_DENIED) {
                            JOptionPane.showMessageDialog(Main.frameInstance, Constants.ACCESS_DENIED_TEXT);
                        }
                    }
                }
            });

            var deleteAll = new JMenuItem("Delete All Files");
            deleteAll.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e == null) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (JOptionPane.showConfirmDialog(Main.frameInstance,
                                "<html><h3>Are you sure you want to delete every file in this folder? (cannot be undone)")
                                == JOptionPane.YES_OPTION) {
                            user.fsys.getCurrentFolder().removeAllFiles();
                            loadFolder(user.fsys.getCurrentFolder());
                            Main.saveUsers();
                        }
                    }
                }
            });

            var addFolder = new JMenuItem("New Folder");
            addFolder.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e == null) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        Tile.addFolder(jPanel1);
                    }
                }
            });

            var addItem = new JMenuItem("New File");
            addItem.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e == null) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        addFile();
                    }
                }
            });

            var paste = new JMenuItem("Paste");
            paste.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e == null) {
                        return;
                    }
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        Clipper.paste();
                    }
                }
            });

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
                                || ImportQueue.instance().isImporting()) {
                            JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>You can't log out while files are being exported or imported!");
                            return;
                        }

                        int x = JOptionPane.showConfirmDialog(Main.frameInstance, "<html><h3>Are you sure you want to log out?");

                        if (x == JOptionPane.YES_OPTION) {
                            ImportQueue.instance().stopExporting();
                            LoginFrame lf = new LoginFrame();
                            lf.setLocationRelativeTo(Main.frameInstance);
                            lf.setVisible(true);
                            Main.frameInstance.dispose();
                            Main.frameInstance = null;
                            return;
                        }

                        loadFolder(user.fsys.getCurrentFolder());
                        Main.saveUsers();
                    }
                }
            });

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

            var changePassword = new JMenuItem("Change Password");
            changePassword.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    int x = Util.requestPassword();
                    
                    if (x == Util.PASSWORD_ACCEPTED) {
                        var newPass = new PasswordDialog("<html><h3>Enter a new password: ").getPassword();
                        if (newPass == null) {
                            return;
                        }
                        var newPassCheck = new PasswordDialog("<html><h3>Enter your new password again: ").getPassword();
                        if (newPassCheck == null) {
                            return;
                        }

                        newPass = newPass.trim();
                        newPassCheck = newPassCheck.trim();
                        
                        if (newPass.equals(newPassCheck)) {
                            user.hash = Constants.messageDigest.digest(Main.mixPassAndSalt(newPass, user.salt).getBytes());
                            Main.saveUsers();
                            JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>Successfully changed the password!");
                        } else {
                            JOptionPane.showMessageDialog(Main.frameInstance, "<html><h3>The passwords didn't match!");
                        }
                    } else if (x == Util.PASSWORD_DENIED) {
                        JOptionPane.showMessageDialog(Main.frameInstance, Constants.ACCESS_DENIED_TEXT);
                    }
                }
            });

            var search = new JMenuItem("Search");
            search.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    String input = JOptionPane.showInputDialog(Main.frameInstance, "<html><h3>Please enter your search keywords (separate every keyword with a ','): ");
                    if (input == null || input.isBlank()) {
                        return;
                    }
                    String[] keyWords = input.split(",");
                    if (keyWords != null && keyWords.length > 0) {
                        List<FilePointer> pointers = user.fsys.search(keyWords);
                        Folder searchFolder = FolderBuilder.createSearchFolder(user.fsys.getCurrentFolder(), pointers);
                        loadFolder(searchFolder);
                    }
                }
            });

            if (user.fsys.getCurrentFolder().getFileCount() > 1) {
                menu.add(exportAll);
                menu.add(deleteAll);
                menu.add(new JSeparator());
            }

            menu.add(addFolder);
            menu.add(addItem);
            menu.add(new JSeparator());
            menu.add(search);
            menu.add(new JSeparator());
            menu.add(paste);
            menu.add(new JSeparator());
            menu.add(logout);
            menu.add(changePassword);

            if (!user.showWelcomeMsg) {
                menu.add(new JSeparator());
                menu.add(enableWelcomeMsg);
            }

            menu.show(jPanel1, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jPanel1MouseReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!Export.exportTasks.isEmpty() || !Export.importTasks.isEmpty() || ImportQueue.instance().isImporting()) {
            JOptionPane.showMessageDialog(this, "You can't exit while files are being exported or imported.");
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
}
