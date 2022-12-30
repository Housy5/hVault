package vault.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import vault.Constants;
import vault.Export;
import vault.Main;
import vault.fsys.Folder;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import static vault.Main.frameInstance;
import vault.FileTransferHandler;
import vault.IconUtil;
import vault.NameUtilities;
import vault.TransferData;
import vault.Util;
import vault.fsys.FileSystemItem;
import vault.interfaces.Updatable;
import vault.password.Password;

public class Tile extends JPanel implements Updatable {

    private static ImageIcon defaultFileIcon = null;
    private static final long serialVersionUID = 1L;
    private static final int toolTipWidth = 200;
    private static ImageIcon videoFileIcon = null;
    private static ImageIcon audioFileIcon = null;
    private static ImageIcon documentFileIcon = null;
    private static ImageIcon imageFileIcon = null;
    private static ImageIcon addIcon = null;
    private static ImageIcon folderIcon = null;

    public static int DOUBLE_CLICK_TIME = 500; //in millis
    public static Color BACKGROUND_COLOR;

    public String name;
    public Folder folder;
    public FilePointer file;
    public FileType type;

    private boolean selected;

    private long lastClicked = 0;

    private final String html = "<html><body style='width: %1spx; text-align: center;'>%1s";
    private final String toolTipHtml = "<html><body style='width: %1spx;'>%1s";

    private Tile instance;

    public class TileContextMenu extends JPopupMenu {

        public TileContextMenu() {

            var exportSelection = new JMenuItem("Export the Selected Items");
            exportSelection.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    var tiles = frameInstance.getSelectedTiles();
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

            var deleteSelection = new JMenuItem("Delete the Selected Items");
            deleteSelection.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    var tiles = frameInstance.getSelectedTiles();
                    var fsys = frameInstance.user.getFileSystem();

                    tiles.stream().filter(x -> x.isFile()).forEach(x -> fsys.removeFilePointer(x.file));
                    tiles.stream().filter(x -> x.isFolder()).forEach(x -> fsys.removeFolder(x.folder));

                    Main.reload();
                    Main.saveUsers();
                }
            });

            var open = new JMenuItem("Open on System");
            open.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        openFile(file);
                    }
                }
            });

            var export = new JMenuItem("Export");
            export.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int x = Util.requestPassword();

                        if (x == Util.PASSWORD_ACCEPTED) {
                            if (type == FileType.FILE) {
                                Export.exportFile(file);
                            } else if (type == FileType.FOLDER) {
                                Export.exportFolder(folder);
                            }
                        } else if (x == Util.PASSWORD_DENIED) {
                            MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
                        }
                    }
                }
            });

            var rename = new JMenuItem("Rename");
            rename.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        NameUtilities.renameItem(isFile() ? file : folder);
                    }
                }
            });

            var delete = new JMenuItem("Delete");
            delete.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        switch (type) {
                            case ADD -> {
                            }
                            case FILE -> {
                                deleteFile();
                            }
                            case FOLDER -> {
                                FileSystem fsys = frameInstance.user.getFileSystem();

                                if (folder.isLocked()) {
                                    int x = Util.requestFolderPassword(folder);

                                    if (x == Util.PASSWORD_ACCEPTED) {
                                        removeFolder(fsys);
                                    } else if (x == Util.PASSWORD_DENIED) {
                                        MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
                                    }
                                } else {
                                    removeFolder(fsys);
                                }
                            }
                        }
                    }
                }

                private void removeFolder(FileSystem fsys) {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            fsys.removeFolder(folder);
                            frameInstance.loadFolder(fsys.getCurrent());
                            Main.saveUsers();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }

                private void deleteFile() {
                    Runnable runnable = new Runnable() {
                        public void run() {
                            FileSystem fsys = frameInstance.user.getFileSystem();
                            fsys.deleteFilePointer(file);
                            
                            if (fsys.getCurrent().isSearchFolder()) {
                                fsys.getCurrent().removeFilePointer(file);
                            }
                            
                            if (file.isImage())
                                Main.removeThumbNail(file.getName());
                            
                            Main.reload();
                            Main.saveUsers();
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            });

            var cut = new JMenuItem("Cut");
            cut.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (type == FileType.FILE) {
                            frameInstance.getClipper().cut(file, frameInstance.user.getFileSystem().getCurrent());
                        } else if (type == FileType.FOLDER) {
                            if (folder.isLocked()) {
                                int x = Util.requestPassword();

                                if (x == Util.PASSWORD_ACCEPTED) {
                                    frameInstance.getClipper().cut(folder, frameInstance.user.getFileSystem().getCurrent());
                                } else if (x == Util.PASSWORD_DENIED) {
                                    MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
                                }
                            } else {
                                frameInstance.getClipper().cut(folder, frameInstance.user.getFileSystem().getCurrent());
                            }
                        }
                    }
                }
            });

            var copy = new JMenuItem("Copy");
            copy.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (type == FileType.FILE) {
                            frameInstance.getClipper().copy(file);
                        } else {
                            if (folder.isLocked()) {
                                int x = Util.requestFolderPassword(folder);

                                if (x == Util.PASSWORD_ACCEPTED) {
                                    frameInstance.getClipper().copy(folder);
                                } else if (x == Util.PASSWORD_DENIED) {
                                    MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
                                }
                            } else {
                                frameInstance.getClipper().copy(folder);
                            }
                        }
                    }
                }
            });

            var setPassword = new JMenuItem("Enable Password Protection");
            setPassword.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    var dialog = new NewPasswordDialog(frameInstance);
                    var passStr = dialog.getPassword();
                    if (passStr == null || passStr.isBlank()) {
                        return;
                    }

                    System.out.println("passStr: " + passStr + ".");

                    Password pass = new Password(passStr);
                    folder.setPassword(pass);
                    folder.setLocked(true);
                    Main.saveUsers();
                }
            });

            var removePassword = new JMenuItem("Disable Password Protection");
            removePassword.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int x = Util.requestFolderPassword(folder);

                        if (x == Util.PASSWORD_ACCEPTED) {
                            folder.setLocked(false);
                            Main.saveUsers();
                        } else if (x == Util.PASSWORD_DENIED) {
                            MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
                        }
                    }
                }
            });

            if (frameInstance.getSelectionCount() > 0) {
                add(exportSelection);
                add(deleteSelection);
                add(new JSeparator());
            }

            if (frameInstance.getSelectionCount() == 0) {
                if (Desktop.isDesktopSupported() && isFile()) {
                    add(open);
                }

                add(export);
                add(new JSeparator());
                add(rename);
                add(delete);
                add(new JSeparator());
                add(cut);
                add(copy);

                if (type == FileType.FOLDER) {
                    add(new JSeparator());
                    if (folder.isLocked()) {
                        add(removePassword);
                    } else {
                        add(setPassword);
                    }
                }
            }
        }

    }

    public FilePointer getFile() {
        return file;
    }
    
    @Override
    public void update() {
        BACKGROUND_COLOR = frameInstance.getBackground();
        manageBackground();
    }

    @SuppressWarnings("empty-statement")
    private void openFile(FilePointer original) {
        try {
            frameInstance.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            var f = Export.exportTemporaryFile(file);
            while (!f.exists() || f.length() < original.getSize());
            Desktop.getDesktop().open(f);
            frameInstance.setCursor(Cursor.getDefaultCursor());
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isFile() {
        return type == FileType.FILE;
    }

    public boolean isFolder() {
        return type == FileType.FOLDER;
    }

    public static enum FileType {
        FILE, FOLDER, ADD;

        ImageIcon getIcon() {
            return switch (this) {
                case FILE ->
                    defaultFileIcon;
                case FOLDER ->
                    folderIcon;
                case ADD ->
                    addIcon;
                default ->
                    null;
            };
        }
    }

    private void setInstance() {
        instance = this;
    }

    private void initResources() {
        try {
            if (defaultFileIcon == null) {
                defaultFileIcon = IconUtil.getInstance().getIcon("/res/file-free-icon-font_1.png");
            }
            if (audioFileIcon == null) {
                audioFileIcon = IconUtil.getInstance().getIcon("/res/music-file-free-icon-font (1).png");
            }
            if (videoFileIcon == null) {
                videoFileIcon = IconUtil.getInstance().getIcon("/res/file-video-free-icon-font.png");
            }
            if (documentFileIcon == null) {
                documentFileIcon = IconUtil.getInstance().getIcon("/res/file-invoice-free-icon-font.png");
            }
            if (imageFileIcon == null) {
                imageFileIcon = IconUtil.getInstance().getIcon("/res/picture-free-icon-font.png");
            }
            if (folderIcon == null) {
                folderIcon = IconUtil.getInstance().getIcon("/res/folder-free-icon-font_1.png");
            }
        } catch (IOException iOException) {
        }
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior() {
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }

    @Override
    public int getBaseline(int w, int h) {
        return 0;
    }

    private Icon getThumbNail() {
        var thumbnails = Main.getThumbNails();
        if (thumbnails.containsKey(file.getName())) {
            return thumbnails.get(file.getName());
        } else {
            return imageFileIcon;
        }
    }

    private Icon parseIcon() {
        if (file.isAudio())
            return audioFileIcon;
        if (file.isImage())
            return getThumbNail();
        if (file.isDocument())
            return documentFileIcon;
        if (file.isVideo())
            return videoFileIcon;
        return defaultFileIcon;
    }

    private void name(String str) {
        name = str;
        shortenName();
    }

    private void initFile(FilePointer pointer) {
        name(pointer.getName());
        file = pointer;
        type = FileType.FILE;

        Icon icon = parseIcon();
        JLabel iconlbl = new JLabel("", icon, JLabel.CENTER);
        JLabel txtlbl = new JLabel(String.format(html, 75, name));

        add(iconlbl, BorderLayout.CENTER);
        add(txtlbl, BorderLayout.SOUTH);

        String toolTipText = file.getName() + "<hr>" + file.getParent().getPath();
        setToolTipText(String.format(toolTipHtml, toolTipWidth, toolTipText));
    }

    private void initFolder(Folder folder) {
        name(folder.getName());
        this.folder = folder;
        type = FileType.FOLDER;

        JLabel iconlbl = new JLabel("", type.getIcon(), JLabel.CENTER);
        JLabel txtlbl = new JLabel(String.format(html, 75, name));

        add(iconlbl, BorderLayout.CENTER);
        add(txtlbl, BorderLayout.SOUTH);

        String toolTipText = folder.getName() + "<hr>" + folder.getPath();
        setToolTipText(String.format(toolTipHtml, toolTipWidth, toolTipText));
    }

    private void shortenName() {
        int length = 50;
        if (name.length() < length)
            return;
        name = name.substring(0, length) + "...";
    }

    public Tile(String name, Folder folder) {
        type = FileType.FOLDER;
        this.folder = folder;
        name(name);
        String toolTipText = "Return to: <hr> " + folder.getPath();
        setToolTipText(String.format(toolTipHtml, toolTipWidth, toolTipText));
        setLayout(new BorderLayout());

        JLabel iconlbl = new JLabel("", type.getIcon(), JLabel.CENTER);
        JLabel txtlbl = new JLabel(String.format(html, 75, name));

        add(iconlbl, BorderLayout.CENTER);
        add(txtlbl, BorderLayout.SOUTH);

        init();
    }

    public Tile(Object obj) {
        initResources();
        setLayout(new BorderLayout());

        if (obj instanceof FilePointer pointer) {
            initFile(pointer);
            file = pointer;
        } else if (obj instanceof Folder fol) {
            initFolder(fol);
            folder = fol;
        }

        init();
    }

    public void toggleSelected() {
        selected = !selected;
        manageBackground();
    }

    public void resetBackground() {
        setBackground(BACKGROUND_COLOR);
    }

    private void manageTracking() {
        if (selected) {
            frameInstance.track(this);
        } else {
            frameInstance.untrack(this);
        }
    }

    private void manageBackground() {
        if (!selected) {
            setBackground(BACKGROUND_COLOR);
        } else if (selected) {
            setBackground(BACKGROUND_COLOR.darker());
        }
    }

    private boolean isDoubleClick() {
        return System.currentTimeMillis() - lastClicked <= DOUBLE_CLICK_TIME;
    }

    private void doubleClickFile() {
        if (Desktop.isDesktopSupported()) {
            openFile(file);
        } else {
            int x = Util.requestPassword();

            if (x == Util.PASSWORD_ACCEPTED) {
                Export.exportFile(file);
            } else if (x == Util.PASSWORD_DENIED) {
                MessageDialog.show(frameInstance, Constants.ACCESS_DENIED_TEXT);
            }
        }
    }

    private void doubleClickFolder() {
        frameInstance.getFolderCursor().push(folder);
        frameInstance.loadFolder(folder);
    }

    private void rightClickTile(MouseEvent e) {
        if ((isFolder() && !name.equals("..")) || isFile()) {
            var menu = new TileContextMenu();
            menu.show(instance, e.getX(), e.getY());
        }
    }

    public Rectangle toRectangle() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    private void init() {
        BACKGROUND_COLOR = Main.frameInstance.getBackground();
        resetBackground();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
                    if ((isFolder() && !"..".equals(name)) || isFile()) {
                        toggleSelected();
                        manageTracking();
                    }
                } else if (SwingUtilities.isLeftMouseButton(e)) {
                    var now = System.currentTimeMillis();
                    if (isDoubleClick()) {
                        frameInstance.resetSelections();
                        switch (type) {
                            case FOLDER ->
                                doubleClickFolder();
                            case FILE ->
                                doubleClickFile();
                            case ADD -> {
                            }
                        }
                    }
                    lastClicked = now;
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    rightClickTile(e);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (type == FileType.ADD) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                setBackground(BACKGROUND_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (type == FileType.ADD) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }

                if (!selected) {
                    setBackground(BACKGROUND_COLOR);
                }
            }
        });

        setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                if (type == FileType.FILE || type == FileType.ADD) {
                    return;
                }
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    if (evt.isDataFlavorSupported(FileTransferHandler.FILE_POINTER_LIST_FLAVOR)) {
                        TransferData data = (TransferData) evt.getTransferable().getTransferData(FileTransferHandler.FILE_POINTER_LIST_FLAVOR);
                        Folder origin = frameInstance.user.getFileSystem().findFolder(data.getOrigin().getPath());

                        for (var pointer : data.getPointers()) {
                            Main.frameInstance.user.getFileSystem().transferFile(pointer, origin, folder);
                        }

                        Main.reload();
                        Main.saveUsers();
                    } else {
                        List<File> droppedObject = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        frameInstance.addFiles(droppedObject, folder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.show(Main.frameInstance, e.getMessage());
                }
            }
        });

        setInstance();
    }
}
