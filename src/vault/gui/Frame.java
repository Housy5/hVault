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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import vault.Export;
import vault.Main;
import vault.FileTransferHandler;
import vault.queue.ImportQueue;
import vault.queue.ImportTicket;
import vault.gui.menu.DefaultMenu;
import vault.gui.selections.SelectionTracker;
import vault.nfsys.Folder;
import vault.nfsys.FilePointer;
import vault.queue.ExportQueue;
import vault.user.User;

public final class Frame extends javax.swing.JFrame {

    public User user;
    private Timer progressTimer;
    
    private SelectionTracker selectionTracker;

    private final int maxTitleLength = 50;

    public Frame(User user) {
        initComponents();
        this.user = user;
        setLocationRelativeTo(null);
        
        selectionTracker = new SelectionTracker();
        setTitle(user.username + "'s  Vault!");

        user.fsys.indexFileIDs();
        user.fsys.validateFiles();
        Main.saveUsers();
        user.fsys.cd(user.fsys.getRoot());

        addDropTarget();

        initTimer();
        initIcon();
    }

    public void resetSelections() {
        selectionTracker.resetSelections();
    }

    public void track(Tile tile) {
        selectionTracker.track(tile);
    }
    
    public void untrack(Tile tile) {
        selectionTracker.untrack(tile);
    }
    
    public long getSelectionCount() {
        return selectionTracker.getSelectionCount();
    }

    public List<Tile> getSelectedTiles() {
        return selectionTracker.getSelectedTiles();
    }
    
    private void addDropTarget() {
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
                    JOptionPane.showMessageDialog(Main.frameInstance, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void initTimer() {
        progressTimer = new Timer(500, (ActionEvent e) -> {
            updateProgressLabel();
        });
        progressTimer.start();
    }

    private void initIcon() {
        try {
            this.setIconImage(ImageIO.read(getClass().getResource("/res/vault.png")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(Main.frameInstance, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProgressLabel() {
        ImportQueue importQueue = ImportQueue.instance();
        ExportQueue exportQueue = ExportQueue.instance();

        if (importQueue.isImporting()) {
            jLabel1.setText(String.format("Importing: %d files left.", importQueue.count()));
        } else if (exportQueue.isExporting()) {
            jLabel1.setText(String.format("Exporting: %d files left.", exportQueue.count()));
        } else {
            jLabel1.setText(IDLE_STATE);
        }
    }

    private String createTitleMessage(String fullname) {
        String result = fullname;
        if (fullname.length() > maxTitleLength) {
            result = "..." + fullname.substring(fullname.length() - (maxTitleLength + 1), fullname.length() - 1);
        }
        return result;
    }

    public final static String IDLE_STATE = "Waiting...";

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

        Arrays.stream(jPanel1.getComponents())
                .filter(component -> component instanceof Tile)
                .forEach(component -> jPanel1.remove(component));

        if (folder.getParent() != null) {
            var parentTile = new Tile("..", folder.getParent());
            jPanel1.add(parentTile);
        }

        folder.getFolders().forEach(fol -> jPanel1.add(new Tile(fol)));

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(164, 149, 128), 1, true), "Folder Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 0, 14))); // NOI18N
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

        jLabel1.setText("Waiting...");
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
        f.forEach(x -> queue.addTicket(new ImportTicket(x, folder)));
    }

    /**
     * Don't ask questions
     */
    public void addFile() {
        var chooser = new JFileChooser();
        chooser.showOpenDialog(jPanel1);

        var file = chooser.getSelectedFile();
        var folder = user.fsys.getCurrentFolder();

        if (file != null) {
            addFile(List.of(file), folder);
        }
    }

    public void stopProgressTimer() {
        progressTimer.stop();
    }

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            if (!user.fsys.getCurrentFolder().isSearchFolder()) {
                var menu = new DefaultMenu(this, user);
                menu.show(jPanel1, evt.getX(), evt.getY());
            }
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            resetSelections();
        }
    }//GEN-LAST:event_jPanel1MouseReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (!Export.exportTasks.isEmpty() || !Export.importTasks.isEmpty() || ImportQueue.instance().isImporting() || ExportQueue.instance().isExporting()) {
            JOptionPane.showMessageDialog(this,
                    "You cannot exit the program while importing or exporting files!",
                    "info",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    private static final long serialVersionUID = 1L;
}
