package vault;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import vault.fsys.Folder;
import vault.gui.CursorType;
import vault.gui.Frame;
import vault.gui.LoginFrame;
import vault.gui.MessageDialog;
import vault.interfaces.Updatable;

import vault.user.User;

public class Main {

    public static Frame frameInstance = null;
    public static Image icon = null;
    public static Main instance;
    public static Map<String, User> users;
    public static Map<String, ImageIcon> thumbnails;
    public static volatile Properties uiProperties;
    public static Font notoEmojiFont;

    private static volatile UIMode uimode;

    static {
        initUIMode();
        initLaf();
        loadUsers();
        initUsers();
        loadThumbNails();
    }

    public static UIMode getUIMode() {
        return uimode;
    }

    public static Folder getCurrentFolder() {
        return frameInstance.user.getFileSystem().getCurrent();
    }
    
    public static void toggleUIMode(JFrame currentFrame) {
        Runnable runnable = () -> {
            uimode = switch (uimode) {
                case DARK ->
                    UIMode.LIGHT;
                case LIGHT ->
                    UIMode.DARK;
            };

            saveUIMode();
            updateUIManager();
            initUIProperties();
            SwingUtilities.updateComponentTreeUI(currentFrame);

            if (currentFrame instanceof Updatable updatable) {
                updatable.update();
            }
        };
        EventQueue.invokeLater(runnable);
    }

    public static void updateUIManager() {
        if (uimode == UIMode.DARK) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (uimode == UIMode.LIGHT) {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.show(null, ex.getMessage());
            }
        }
    }

    public static void changeCursor(CursorType type) {
        switch (type) {
            case DEFAULT -> frameInstance.switchToDefaultCursor();
            case WAIT -> frameInstance.switchToWaitCursor();
        }
    }
    
    public static Properties getUIProperties() {
        return uiProperties;
    }

    private static void initUIMode() {
        try {
            uimode = loadUIMode();
        } catch (IOException e) {
            uimode = Constants.DEFAULT_UIMODE;
            saveUIMode();
        }

        initUIProperties();
    }

    private static void initUIProperties() {
        uiProperties = new Properties();
        switch (uimode) {
            case DARK -> {
                uiProperties.put("primary.color", new Color(0xA49580));
                uiProperties.put("secondary.color", new Color(0x808fa4));
            }
            case LIGHT -> {
                uiProperties.put("primary.color", Color.BLACK);
                uiProperties.put("secondary.color", Color.BLACK);
            }
        }
    }

    private static void saveUIMode() {
        Path path = Constants.UIMODE_FILE.toPath();
        try {
            Files.writeString(path, uimode.toString());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed to save the UI mode to a file.", ex);
            MessageDialog.show(null, ex.getMessage());
        }
    }

    private static UIMode loadUIMode() throws IOException {
        Path path = Constants.UIMODE_FILE.toPath();
        if (Files.exists(path)) {
            return UIMode.valueOf(Files.readString(path));
        } else {
            throw new IOException();
        }
    }

    public static Map<String, ImageIcon> getThumbNails() {
        return thumbnails;
    }

    public static void reload() {
        frameInstance.loadFolder(frameInstance.user.getFileSystem().getCurrent());
    }

    public static String generateSalt() {
        var random = new SecureRandom();
        var sb = new StringBuilder();
        var min = 33; // ASCII '!'
        var max = 127; // ASCII 'DEL'

        sb.setLength(0);
        var length = random.nextInt(10, 100);

        for (var i = 0; i < length; i++) {
            sb.append((char) random.nextInt(min, max));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        if (SessionLedger.attemptStart()) {
            Garbage.start();
            var lf = new LoginFrame();
            EventQueue.invokeLater(() -> lf.setVisible(true));
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    while (SessionLedger.isRunning()) {
                        SessionLedger.stopSession();
                    }
                }
            });
        }
    }

    public static String mixPassAndSalt(String password, String salt) {
        var passes = 0;
        var result = password + salt;
        var chars = result.toCharArray();

        while (passes < Math.log(chars.length)) {
            var arr1 = new char[chars.length / 2 + 1];
            var arr2 = new char[chars.length / 2 + 1];
            var arr1Count = 0;
            var arr2Count = 0;

            for (int i = 0; i < chars.length; i++) {
                if (i % 2 == 0) {
                    arr1[arr1Count] = chars[i];
                    arr1Count++;
                } else {
                    arr2[arr2Count] = chars[i];
                    arr2Count++;
                }
            }

            result = new String(arr1).trim() + new String(arr2).trim();
            chars = result.toCharArray();
            passes++;
        }

        return result;
    }

    public static void saveUsers() {
        serializeUserMap();
        users.values().parallelStream().forEach(User::save);
    }

    private static void saveUserData() {
        users.values().forEach(User::save);
    }
    
    public static void saveThumbNails() {
        File f = Constants.THUMBNAIL_FILE;
        try ( var out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(thumbnails);
            saveUserData();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Failed while trying to save the thumbnails", ex);
            MessageDialog.show(null, ex.getMessage());
        }
    }

    private static void loadUsers() {
        File f = Constants.SAVE_FILE;

        if (f.exists()) {
            try ( var in = new ObjectInputStream(new FileInputStream(f))) {
                users = (HashMap<String, User>) in.readObject();
            } catch (FileNotFoundException ex) {
                users = new HashMap<>();
            } catch (IOException | ClassNotFoundException ex) {
                users = new HashMap<>();
            }
        } else {
            users = new HashMap<>();
        }
    }
    
    private static void initUsers() {
        users.values().parallelStream().forEach(User::init);
    }

    
    /**
     * Reset's the thumbnails when the save files exceeds 500mb
     */
    static void cleanThumbNails() {
        if (Constants.THUMBNAIL_FILE.length() > 5_000_000) {
            Constants.THUMBNAIL_FILE.delete();
            thumbnails.clear();
        }
    }
    
    public static void removeThumbNail(String key) {
        thumbnails.remove(key);
        saveThumbNails();
    }
    
    public static void loadThumbNails() {
        File f = Constants.THUMBNAIL_FILE;

        if (f.exists()) {
            try ( var in = new ObjectInputStream(new FileInputStream(f))) {
                thumbnails = (HashMap<String, ImageIcon>) in.readObject();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.show(null, ex.getMessage());
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                MessageDialog.show(null, ex.getMessage());
            }
        } else {
            thumbnails = new HashMap<>();
        }
    }

    private Main() {
    }

    private static void initLaf() {
        try {
            if (uimode == UIMode.DARK) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else if (uimode == UIMode.LIGHT) {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (UnsupportedLookAndFeelException e) {
            MessageDialog.show(null, e.getMessage());
            e.printStackTrace();
        }
    }

    private static void serializeUserMap() {
        File f = Constants.SAVE_FILE;
        try ( var out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(users);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(null, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(null, ex.getMessage());
        }
    }
}
