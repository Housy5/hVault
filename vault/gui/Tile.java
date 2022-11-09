package vault.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
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
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import vault.Constants;
import vault.Export;
import vault.Main;
import vault.NameValidator;
import vault.Clipper;
import vault.nfsys.Folder;
import vault.nfsys.FilePointer;
import vault.nfsys.FileSystem;
import static vault.Main.frameInstance;
import vault.nfsys.FolderBuilder;
import vault.FileTransferHandler;
import vault.NameUtilities;
import vault.TransferData;
import vault.Util;
import vault.format.FormatDetector;

public class Tile extends JPanel {

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

            var deleteSelection = new JMenuItem("Delete the Selected Items");
            deleteSelection.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    var tiles = frameInstance.getSelectedTiles();
                    var fsys = frameInstance.user.fsys;

                    tiles.stream().filter(x -> x.isFile()).forEach(x -> fsys.removeFile(x.file));
                    tiles.stream().filter(x -> x.isFolder()).forEach(x -> fsys.removeFolder(x.folder));

                    frameInstance.loadFolder(fsys.getCurrentFolder());
                    Main.saveUsers();
                }
            });

            var open = new JMenuItem("Open");
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
                            JOptionPane.showMessageDialog(frameInstance, Constants.ACCESS_DENIED_TEXT, "info", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });

            var rename = new JMenuItem("Rename");
            rename.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (type == FileType.FILE) {
                            renameFile();
                        } else {
                            renameFolder();
                        }
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
                                FileSystem fsys = frameInstance.user.fsys;
                                fsys.removeFile(file);

                                if (fsys.getCurrentFolder().isSearchFolder()) {
                                    fsys.getCurrentFolder().removeFilePointer(file);
                                }

                                frameInstance.loadFolder(fsys.getCurrentFolder());
                                Main.saveUsers();
                            }
                            case FOLDER -> {
                                FileSystem fsys = frameInstance.user.fsys;

                                if (folder.isLocked()) {
                                    int x = Util.requestPassword();

                                    if (x == Util.PASSWORD_ACCEPTED) {
                                        removeFolder(fsys);
                                    } else if (x == Util.PASSWORD_DENIED) {
                                        JOptionPane.showMessageDialog(frameInstance, Constants.ACCESS_DENIED_TEXT, "info", JOptionPane.INFORMATION_MESSAGE);
                                    }
                                } else {
                                    removeFolder(fsys);
                                }

                            }
                        }
                    }
                }

                private void removeFolder(FileSystem fsys) {
                    fsys.removeFolder(folder);
                    frameInstance.showState(Frame.IDLE_STATE);
                    frameInstance.loadFolder(fsys.getCurrentFolder());
                    Main.saveUsers();
                }
            });

            var cut = new JMenuItem("Cut");
            cut.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (type == FileType.FILE) {
                            Clipper.cut(file, frameInstance.user.fsys.getCurrentFolder());
                        } else if (type == FileType.FOLDER) {
                            if (folder.isLocked()) {
                                int x = Util.requestPassword();

                                if (x == Util.PASSWORD_ACCEPTED) {
                                    Clipper.cut(folder, frameInstance.user.fsys.getCurrentFolder());
                                } else if (x == Util.PASSWORD_DENIED) {
                                    JOptionPane.showMessageDialog(frameInstance, Constants.ACCESS_DENIED_TEXT, "info", JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                Clipper.cut(folder, frameInstance.user.fsys.getCurrentFolder());
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
                            Clipper.copy(file);
                        } else {
                            if (folder.isLocked()) {
                                int x = Util.requestPassword();

                                if (x == Util.PASSWORD_ACCEPTED) {
                                    Clipper.copy(folder);
                                } else if (x == Util.PASSWORD_DENIED) {
                                    JOptionPane.showMessageDialog(frameInstance,
                                            Constants.ACCESS_DENIED_TEXT,
                                            "info",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                            } else {
                                Clipper.copy(folder);
                            }
                        }
                    }
                }
            });

            var setPassword = new JMenuItem("Enable Password Protection");
            setPassword.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    folder.setLocked(true);
                }
            });

            var removePassword = new JMenuItem("Disable Password Protection");
            removePassword.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        int x = Util.requestPassword();

                        if (x == Util.PASSWORD_ACCEPTED) {
                            folder.setLocked(false);
                        } else if (x == Util.PASSWORD_DENIED) {
                            JOptionPane.showMessageDialog(frameInstance,
                                    Constants.ACCESS_DENIED_TEXT,
                                    "info",
                                    JOptionPane.INFORMATION_MESSAGE);
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

    private void renameFile() {
        var newName = "";
        var originalName = NameValidator.splitNameAndExtension(file.getName())[0];
        var ext = NameValidator.splitNameAndExtension(file.getName())[1];
        var parent = file.getParent();
        int count = 0;

        do {
            String msg = count == 0
                    ? "Enter a new file name (No extension): "
                    : "The name you have entered already exists or is invalid!";
            newName = JOptionPane.showInputDialog(frameInstance, msg, originalName);
            if (newName == null) {
                return;
            }
            newName = newName.trim() + "." + ext;
            count = (count % Integer.MAX_VALUE) + 1;
        } while (!NameValidator.isValidNameExcluded(newName, file, parent));

        file.setName(newName);
        parent.sortFiles();
        frameInstance.loadFolder(parent);
        Main.saveUsers();
    }

    public boolean isFile() {
        return type == FileType.FILE;
    }

    public boolean isFolder() {
        return type == FileType.FOLDER;
    }

    private void renameFolder() {
        var newName = "";
        var current = frameInstance.user.fsys.getCurrentFolder();
        int count = 0;

        do {
            String msg = count == 0
                    ? "Enter a new folder name:  "
                    : "The name you have entered already exists or is invalid!";
            newName = JOptionPane.showInputDialog(frameInstance, msg, folder.getName());
            if (newName == null) {
                return;
            }
            newName = newName.trim();
            count = (count % Integer.MAX_VALUE) + 1;
        } while (!NameValidator.isValidFolderName(newName) || current.containsFolderNameExcluded(newName, folder));

        folder.setName(newName);
        folder.remap(folder.getParent());
        current.sortFolders();
        Main.frameInstance.loadFolder(current);
        Main.saveUsers();
    }

    /**
     * Sequence to add a folder.
     */
    public static void addFolder() {
        String folname = JOptionPane.showInputDialog(frameInstance, "Name of the folder: ");
        if (folname == null) {
            folname = "Untitled Folder";
        }

        var user = frameInstance.user;
        if (user.fsys.getCurrentFolder().containsFolderName(folname)) {
            folname = NameUtilities.nextFolderName(folname, user.fsys.getCurrentFolder());

            if (folname == null) {
                JOptionPane.showMessageDialog(frameInstance, "Failed to create a new folder!");
                return;
            }
        }

        Folder current = frameInstance.user.fsys.getCurrentFolder();
        current.addFolder(FolderBuilder.createFolder(folname, current));

        frameInstance.loadFolder(current);
        Main.saveUsers();
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
        if (defaultFileIcon == null) {
            defaultFileIcon = getIcon("/res/file-free-icon-font_1.png");
        }
        if (audioFileIcon == null) {
            audioFileIcon = getIcon("/res/music-file-free-icon-font (1).png");
        }
        if (videoFileIcon == null) {
            videoFileIcon = getIcon("/res/file-video-free-icon-font.png");
        }
        if (documentFileIcon == null) {
            documentFileIcon = getIcon("/res/file-invoice-free-icon-font.png");
        }
        if (imageFileIcon == null) {
            imageFileIcon = getIcon("/res/picture-free-icon-font.png");
        }
        if (folderIcon == null) {
            folderIcon = getIcon("/res/folder-free-icon-font_1.png");
        }
        if (addIcon == null) {
            addIcon = getIcon("/res/add (1).png");
        }
    }

    private ImageIcon getIcon(String path) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getResource(path)));
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, "", ex);
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
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

    private Icon parseIcon() {
        FormatDetector detector = FormatDetector.instance();
        int x = detector.detectFormat(file.getName());

        return switch (x) {
            case FormatDetector.AUDIO ->
                audioFileIcon;
            case FormatDetector.DOCUMENT ->
                documentFileIcon;
            case FormatDetector.VIDEO ->
                videoFileIcon;
            case FormatDetector.OTHER ->
                defaultFileIcon;
            case FormatDetector.IMAGE ->
                imageFileIcon;
            default ->
                defaultFileIcon;
        };
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

        String toolTipText = file.getName() + "<hr>" + file.getParent().getFullName();
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

        String toolTipText = folder.getName() + "<hr>" + folder.getFullName();
        setToolTipText(String.format(toolTipHtml, toolTipWidth, toolTipText));
    }

    private void shortenName() {
        String[] tokens = name.split(" ");
        int longest = 0;

        for (String str : tokens) {
            if (str.length() > longest) {
                longest = str.length();
            }
        }

        if (longest > 10 || name.length() > 20) {
            int length = longest > 10 ? 10 : 20;
            name = name.substring(0, length) + "...";
        }
    }

    public Tile(String name, Folder folder) {
        type = FileType.FOLDER;
        this.folder = folder;
        name(name);
        String toolTipText = "Return to: <hr> " + folder.getFullName();
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
            resetBackground();
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
                JOptionPane.showMessageDialog(instance,
                        Constants.ACCESS_DENIED_TEXT,
                        "info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void doubleClickFolder() {
        if (folder.isLocked()) {
            int x = Util.requestPassword();

            if (x == Util.PASSWORD_ACCEPTED) {
                frameInstance.loadFolder(folder);
            } else if (x == Util.PASSWORD_DENIED) {
                JOptionPane.showMessageDialog(instance,
                        Constants.ACCESS_DENIED_TEXT,
                        "info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            frameInstance.loadFolder(folder);
        }
    }

    private void rightClickTile(MouseEvent e) {
        if ((isFolder() && !name.equals("..")) || isFile()) {
            var menu = new TileContextMenu();
            menu.show(instance, e.getX(), e.getY());
        }
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
                        Folder origin = frameInstance.user.fsys.findFolder(data.getOrigin().getFullName());

                        for (var pointer : data.getPointers()) {
                            if (folder.containsFileName(pointer.getName())) {
                                String newName = NameUtilities.nextFileName(pointer.getName(), folder);

                                if (newName == null) {
                                    JOptionPane.showMessageDialog(frameInstance,
                                            "Couldn't move \"" + pointer.getName() + "\" :( ",
                                            "warning",
                                            JOptionPane.WARNING_MESSAGE);
                                    continue;
                                }

                                pointer.setName(newName);
                            }

                            pointer.setParent(folder);
                            origin.removeFilePointer(pointer, false);
                            folder.addFile(pointer);
                        }

                        frameInstance.loadFolder(frameInstance.user.fsys.getCurrentFolder());
                        Main.saveUsers();
                    } else {
                        List<File> droppedObject = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                        frameInstance.addFile(droppedObject, folder);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(Main.frameInstance,
                            e.getMessage(),
                            "error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setInstance();
    }
}
