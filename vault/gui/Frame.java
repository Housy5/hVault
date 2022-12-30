package vault.gui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.*;
import vault.*;
import vault.gui.menu.DefaultMenu;
import vault.gui.selections.SelectionTracker;
import vault.interfaces.Updatable;
import vault.fsys.Folder;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import vault.fsys.FileSystemItem;
import vault.queue.*;
import vault.user.User;

public final class Frame extends JFrame implements Updatable {

    public User user;
    private Timer progressTimer;

    private final SelectionTracker selectionTracker;
    private Sorter sorter;
    private FolderCursor folderCursor;
    private Clipper clipper;
    private FileSystem fsys;
    private long finishImportTime = 0L;

    private Color progressLblColor = Color.BLACK;

    private Rectangle selectionRect = null;
    private Point startPoint;

    private List<Tile> tiles;

    private void initFileSystem() {
        fsys = user.getFileSystem();
        fsys.moveTo(fsys.getRoot());
    }

    private void initFolderCursor() {
        folderCursor = new FolderCursor(fsys);
        folderCursor.push(fsys.getCurrent());
    }

    private void initClipperAndSorter() {
        sorter = new Sorter(fsys);
        clipper = new Clipper(fsys);
    }

    public Frame(User user) {
        initComponents();
        this.user = user;
        setLocationRelativeTo(null);

        selectionTracker = new SelectionTracker();
        setTitle(user.getUsername() + "'s  Vault!");

        initFileSystem();
        initFolderCursor();
        initClipperAndSorter();

        addDropTarget();
        initIcon();
    }

    @Override
    public void update() {
        Runnable runnable = () -> {
            try {
                Properties p = Main.getUIProperties();
                var primaryBorder = (TitledBorder) jPanel1.getBorder();
                var line = new LineBorder((Color) p.get("primary.color"), 1, true);
                primaryBorder.setBorder(line);
                var border = (CompoundBorder) jPanel3.getBorder();
                var outside = border.getOutsideBorder();
                var inside = new LineBorder((Color) p.get("secondary.color"), 1, true);
                jPanel3.setBorder(new CompoundBorder(outside, inside));
                jSeparator1.setForeground((Color) p.get("secondary.color"));
                jSeparator2.setForeground((Color) p.get("secondary.color"));
                progressLblColor = (Color) p.get("primary.color");
                modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
                Arrays.stream(jPanel1.getComponents()).filter(x1 -> x1 instanceof Updatable).forEach(x2 -> ((Updatable) x2).update());
                repaint();
            } catch (IOException ex) {
                Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.show(null, ex.getMessage());
            }
        };
        EventQueue.invokeLater(runnable);
    }

    private void switchCursorTo(Cursor cursor) {
        if (getCursor().equals(cursor))
            return;
        this.setCursor(cursor);
    }

    public void switchToWaitCursor() {
        switchCursorTo(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public void switchToDefaultCursor() {
        switchCursorTo(Cursor.getDefaultCursor());
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

    public FolderCursor getFolderCursor() {
        return folderCursor;
    }

    private void addDropTarget() {
        jPanel1.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    Folder folder = fsys.getCurrent();
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    if (evt.isDataFlavorSupported(FileTransferHandler.FILE_POINTER_LIST_FLAVOR)) {
                        return;
                    }
                    @SuppressWarnings("unchecked")
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    addFiles(droppedFiles, folder);
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
                    MessageDialog.show(Main.frameInstance, ex.getMessage());
                } catch (ClassCastException e) {
                    Logger.getLogger(Frame.class.getName()).log(Level.WARNING, "Failed when trying to cast the transfer data to List<fILE>.", e);
                }
            }
        });
    }

    public void initTimer() {
        progressTimer = new Timer(250, (ActionEvent e) -> {
            updateProgressLabel();
            updateSortButtons();
        });
        progressTimer.start();
    }

    private void initIcon() {
        try {
            setIconImage(IconUtil.getInstance().getImage("/res/vault.png"));
            modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
        } catch (IOException ex) {
            MessageDialog.show(this, ex.getMessage());
        }
    }

    public void finishImport() {
        finishImportTime = System.currentTimeMillis();
    }

    private void resetSortButtons() {
        azbtn.setText("A-Z");
        sizebtn.setText("Size");
        timebtn.setText("Newest");
        typebtn.setText("Images");
    }

    private void updateSortButtons() {
        Runnable runnable = () -> {
            resetSortButtons();
            switch (sorter.getType()) {
                case AZ ->
                    azbtn.setText("Z-A");
                case ZA ->
                    azbtn.setText("A-Z");
                case BIGGEST ->
                    sizebtn.setText("Size");
                case SMALLEST ->
                    sizebtn.setText("Size");
                case NEWEST ->
                    timebtn.setText("Oldest");
                case OLDEST ->
                    timebtn.setText("Newest");
                case IMAGES_FIRST ->
                    typebtn.setText("Videos");
                case VIDEOS_FIRST ->
                    typebtn.setText("Audio");
                case AUDIO_FIRST ->
                    typebtn.setText("Documents");
                case DOCUMENTS_FIRST ->
                    typebtn.setText("Images");
            }
        };
        EventQueue.invokeLater(runnable);
    }

    private void showProgress(String message, Color color) {
        jLabel1.setText(message);
        jLabel1.setForeground(color);
    }

    private void showProgress(String message) {
        showProgress(message, progressLblColor);
    }

    private void updateProgressLabel() {
        Runnable runnable = () -> {
            var importQueue = ImportQueue.instance();
            var exportQueue = ExportQueue.instance();

            if (importQueue.isImporting() && exportQueue.isExporting()) {
                showProgress(String.format("Importing: %d files left, Exporting: %d files left.", importQueue.count() + 1, exportQueue.count() + 1));
            } else if (importQueue.isImporting()) {
                showProgress(String.format("Importing: %d files left.", importQueue.count() + 1));
            } else if (exportQueue.isExporting()) {
                showProgress(String.format("Exporting: %d files left.", exportQueue.count() + 1));
            } else if (System.currentTimeMillis() - finishImportTime < 3000) {
                showProgress("Success!", new Color(0x87a96b));
            } else {
                showProgress(IDLE_STATE);
            }
        };
        EventQueue.invokeLater(runnable);
    }

    private String createTitleMessage(String fullname) {
        String result = NameUtilities.reformatFullFolderName(fullname);
        if (fullname.length() > Constants.MAX_URL_LENGTH) {
            result = NameUtilities.shortenFullFolderName(fullname);
        }
        return result;
    }

    public final static String IDLE_STATE = "Waiting...";

    public void showState(String state) {
        jLabel1.setText(state);
    }

    private void scanThumbNails() {
        Thread t = new Thread(() -> {
            var map = Main.thumbnails;
            var files = fsys.getCurrent().getPointers();
            var missing = files.parallelStream().filter(FilePointer::isImage).filter(x -> !map.containsKey(x.getName())).toList();

            if (!missing.isEmpty()) {
                missing.forEach(x -> map.putIfAbsent(x.getName(), loadThumbNail(x)));
                Main.saveThumbNails();
                Main.reload();
            }
        });
        t.start();
    }

    private ImageIcon loadThumbNail(FilePointer pointer) {
        try {
            var img = IconUtil.getInstance().getImage(pointer);
            var scaled = img.getScaledInstance(32, 32, Image.SCALE_FAST);
            return new ImageIcon(scaled);
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, "Something went wrong trying to create a thumbnail.", ex);
            return null;
        }
    }

    private void clearAllTiles() {
        Arrays.stream(display.getComponents()).parallel().filter(x -> x instanceof Tile).forEach(x -> display.remove(x));
    }

    private boolean checkForPassword(Folder folder) {
        if (!folder.isLocked() || (folder.equals(fsys.getCurrent()) || folder.equals(fsys.getCurrent().getParent())))
            return true;
        int result = Util.requestFolderPassword(folder);
        if (result == Util.PASSWORD_DENIED) {
            MessageDialog.show(this, Constants.ACCESS_DENIED_TEXT);
            return false;
        } else if (result == Util.CANCEL) {
            return false;
        } else {
            return true;
        }
    }

    private void addFolder(Folder folder) {
        display.add(new Tile(folder));
    }

    private void addFilePointer(FilePointer pointer) {
        var tile = new Tile(pointer);
        var dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(tile, DnDConstants.ACTION_MOVE, (DragGestureEvent e) -> {
            var cursor = Cursor.getDefaultCursor();
            if (e.getDragAction() == DnDConstants.ACTION_MOVE) {
                cursor = DragSource.DefaultMoveDrop;
            }
            e.startDrag(cursor, createTransferable(pointer));
        });
        display.add(tile);
    }

    private void addFileSystemItem(FileSystemItem item) {
        if (item instanceof Folder folder)
            addFolder(folder);
        else if (item instanceof FilePointer pointer)
            addFilePointer(pointer);
    }

    private void updateTitle(Folder folder) {
        String titleMsg = createTitleMessage(NameUtilities.reformatFullFolderName(folder.getPath()));
        ((TitledBorder) jPanel1.getBorder()).setTitle(titleMsg);
    }

    private void reval() {
        tiles = getTiles(true);
        jPanel1.revalidate();
        jPanel1.repaint();
        scanThumbNails();
    }

    public void loadFolder(Folder folder) {
        Runnable runnable = new Runnable() {
            public void run() {
                if (!checkForPassword(folder))
                    return;
                fsys.moveTo(folder);
                clearAllTiles();
                updateTitle(folder);

                var items = sorter.sort(folder.getAllItems());
                items.stream().map(x -> (FileSystemItem) x).forEach(x -> addFileSystemItem(x));
                reval();
            }
        };
        EventQueue.invokeLater(runnable);
    }

    public Clipper getClipper() {
        return clipper;
    }

    private Transferable createTransferable(FilePointer pointer) {
        List<FilePointer> selectedFiles = new ArrayList<>();
        selectedFiles.addAll(getTiles(false).parallelStream().filter(x -> x.isSelected() && x.isFile()).map(Tile::getFile).toList());
        selectedFiles.add(pointer);
        return new FileTransferHandler(selectedFiles, user.getFileSystem().getCurrent());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        display = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(10, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(10, 32767));
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        azbtn = new javax.swing.JButton();
        sizebtn = new javax.swing.JButton();
        timebtn = new javax.swing.JButton();
        typebtn = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        modelbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Vault");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder((Color) Main.getUIProperties().get("primary.color")
            , 1, true), "Folder Name", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 14))); // NOI18N
jPanel1.setName(""); // NOI18N
jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));
jPanel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
    public void mouseDragged(java.awt.event.MouseEvent evt) {
        jPanel1MouseDragged(evt);
    }
    });
    jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            jPanel1MouseClicked(evt);
        }
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            jPanel1MouseReleased(evt);
        }
    });
    jPanel1.setLayout(new java.awt.CardLayout());

    jScrollPane2.setBorder(null);
    jScrollPane2.setViewportView(display);

    display.setFocusable(false);
    display.setMaximumSize(new Dimension(jScrollPane2.getWidth(), 32767));
    java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEADING);
    flowLayout1.setAlignOnBaseline(true);
    display.setLayout(flowLayout1);
    jScrollPane2.setViewportView(display);

    jPanel1.add(jScrollPane2, "card2");

    getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

    jPanel2.setDoubleBuffered(false);
    jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

    jLabel1.setText("Waiting...");
    jPanel2.add(jLabel1);

    getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

    jPanel3.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3), new javax.swing.border.LineBorder((Color) Main.getUIProperties().get("secondary.color"), 1, true)));
    jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

    jButton1.setText("\u276e");
    jButton1.setFocusPainted(false);
    jButton1.setFocusTraversalPolicyProvider(true);
    jButton1.setFocusable(false);
    jButton1.setRequestFocusEnabled(false);
    jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            jButton1MouseReleased(evt);
        }
    });
    jPanel3.add(jButton1);

    jButton2.setText("\u276f");
    jButton2.setFocusPainted(false);
    jButton2.setFocusable(false);
    jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            jButton2MouseReleased(evt);
        }
    });
    jPanel3.add(jButton2);
    jPanel3.add(filler1);

    jSeparator1.setForeground((Color) Main.getUIProperties().get("secondary.color")
    );
    jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    jSeparator1.setPreferredSize(new java.awt.Dimension(10, 20));
    jPanel3.add(jSeparator1);

    jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
    jLabel2.setText("Sort by:");
    jPanel3.add(jLabel2);

    azbtn.setText("A-Z");
    azbtn.setFocusPainted(false);
    azbtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            azbtnActionPerformed(evt);
        }
    });
    jPanel3.add(azbtn);

    sizebtn.setText("Size");
    sizebtn.setFocusPainted(false);
    sizebtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            sizebtnActionPerformed(evt);
        }
    });
    jPanel3.add(sizebtn);

    timebtn.setText("Newest");
    timebtn.setFocusPainted(false);
    timebtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            timebtnActionPerformed(evt);
        }
    });
    jPanel3.add(timebtn);

    typebtn.setText("Images");
    typebtn.setFocusPainted(false);
    typebtn.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            typebtnActionPerformed(evt);
        }
    });
    jPanel3.add(typebtn);

    jSeparator2.setForeground((Color) Main.getUIProperties().get("secondary.color")
    );
    jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
    jSeparator2.setPreferredSize(new java.awt.Dimension(10, 20));
    jPanel3.add(jSeparator2);

    modelbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/eye_light_mode.png"))); // NOI18N
    modelbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    modelbl.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseReleased(java.awt.event.MouseEvent evt) {
            modelblMouseReleased(evt);
        }
    });
    jPanel3.add(modelbl);

    getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

    pack();
    }// </editor-fold>//GEN-END:initComponents

    public void addFiles(List<File> f, Folder folder) {
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
        var folder = user.getFileSystem().getCurrent();

        if (file != null) {
            addFiles(List.of(file), folder);
        }
    }

    public void stopProgressTimer() {
        progressTimer.stop();
    }

    private void resetSelectionRectangle() {
        selectionRect = null;
        startPoint = null;
        jPanel1.repaint();
    }

    private List<Tile> getTiles(boolean update) {
        if (update) {
            return Arrays.stream(jPanel1.getComponents()).filter(x -> x instanceof Tile).map(x -> (Tile) x).toList();
        }
        return tiles;
    }

    private void jPanel1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseReleased
        if (SwingUtilities.isRightMouseButton(evt)) {
            if (!user.getFileSystem().getCurrent().isSearchFolder()) {
                var menu = new DefaultMenu(this, user);
                menu.show(jPanel1, evt.getX(), evt.getY());
            }
        } else if (SwingUtilities.isLeftMouseButton(evt)) {
            resetSelectionRectangle();
        }
    }//GEN-LAST:event_jPanel1MouseReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (ImportQueue.instance().isImporting() || ExportQueue.instance().isExporting()) {
            MessageDialog.show(this, "You can't exit the program while importing or exporting files!");
        } else {
            System.exit(0);
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            loadFolder(folderCursor.next());
        }
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            loadFolder(folderCursor.prev());
        }
    }//GEN-LAST:event_jButton1MouseReleased

    private void azbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_azbtnActionPerformed
        var type = sorter.getType();

        if (type == Sorter.Type.AZ) {
            sorter.setType(Sorter.Type.ZA);
        } else {
            sorter.setType(Sorter.Type.AZ);
        }

        Main.reload();
    }//GEN-LAST:event_azbtnActionPerformed

    private void sizebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizebtnActionPerformed
        var type = sorter.getType();

        if (type == Sorter.Type.BIGGEST) {
            sorter.setType(Sorter.Type.SMALLEST);
        } else {
            sorter.setType(Sorter.Type.BIGGEST);
        }

        Main.reload();
    }//GEN-LAST:event_sizebtnActionPerformed

    private void timebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timebtnActionPerformed
        var type = sorter.getType();

        if (type == Sorter.Type.NEWEST) {
            sorter.setType(Sorter.Type.OLDEST);
        } else {
            sorter.setType(Sorter.Type.NEWEST);
        }

        Main.reload();
    }//GEN-LAST:event_timebtnActionPerformed

    private void typebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typebtnActionPerformed
        var type = sorter.getType();

        if (null == type) {
            sorter.setType(Sorter.Type.IMAGES_FIRST);
        } else {
            switch (type) {
                case IMAGES_FIRST ->
                    sorter.setType(Sorter.Type.VIDEOS_FIRST);
                case VIDEOS_FIRST ->
                    sorter.setType(Sorter.Type.AUDIO_FIRST);
                case AUDIO_FIRST ->
                    sorter.setType(Sorter.Type.DOCUMENTS_FIRST);
                default ->
                    sorter.setType(Sorter.Type.IMAGES_FIRST);
            }
        }

        Main.reload();
    }//GEN-LAST:event_typebtnActionPerformed

    private void modelblMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modelblMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            Main.toggleUIMode(this);
        }
    }//GEN-LAST:event_modelblMouseReleased

    private void jPanel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseDragged
        if (startPoint == null) {
            startPoint = evt.getPoint();
        } else {
            jPanel1.paintImmediately(0, 0, jPanel1.getWidth(), jPanel1.getHeight());
            var point = evt.getPoint();
            int x = Math.min(startPoint.x, point.x);
            int y = Math.min(startPoint.y, point.y);
            int maxX = Math.max(point.x, startPoint.x);
            int maxY = Math.max(point.y, startPoint.y);
            int width = maxX - x;
            int height = maxY - y;
            selectionRect = new Rectangle(x, y, width, height);
            Graphics g = jPanel1.getGraphics();
            g.setColor((Color) Main.getUIProperties().get("secondary.color"));
            g.drawRect(x, y, width, height);

            var tiles = getTiles(false);
            for (Tile tile : tiles) {
                if (!tile.isSelected() && selectionRect.intersects(tile.toRectangle())) {
                    tile.toggleSelected();
                    track(tile);
                } else if (tile.isSelected() && !selectionRect.intersects(tile.toRectangle())) {
                    tile.toggleSelected();
                    untrack(tile);
                }
            }
        }
    }//GEN-LAST:event_jPanel1MouseDragged

    private void jPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt) && selectionTracker.getSelectionCount() > 0) {
            resetSelections();
        }
    }//GEN-LAST:event_jPanel1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton azbtn;
    private javax.swing.JPanel display;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel modelbl;
    private javax.swing.JButton sizebtn;
    private javax.swing.JButton timebtn;
    private javax.swing.JButton typebtn;
    // End of variables declaration//GEN-END:variables
    private static final long serialVersionUID = 1L;
}
