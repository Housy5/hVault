package vault;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import vault.gui.Frame;
import vault.gui.LoginFrame;

import vault.user.User;

public class Main {

    public static Frame frameInstance = null;
    public static Image icon = null;
    public static Main instance;
    public static Map<String, User> users;

    static {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        loadUsers();
    }

    public static void reload() {
        frameInstance.loadFolder(frameInstance.user.fsys.getCurrentFolder());
    }
    
    /**
     * Generates an unique salt.
     *
     * @return returns the generated salt.
     */
    public static String generateSalt() {
        var random = new SecureRandom();
        var sb = new StringBuilder();
        var min = 33; // ASCII '!'
        var max = 127; // ASCII 'DEL'

        do {
            sb.setLength(0);
            var length = random.nextInt(10, 100);

            for (var i = 0; i < length; i++) {
                sb.append((char) random.nextInt(min, max));
            }
        } while (!isUniqueSalt(sb.toString()));
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

    /**
     * Mixes the password and salt together.
     *
     * @param password
     * @param salt
     * @return The mix of the password and salt.
     */
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

    /**
     * Saves all the users to a file.
     *
     * @throws IOException
     */
    public static void saveUsers() {
        File f = Constants.SAVE_FILE;
        try (var out = new ObjectOutputStream(new FileOutputStream(f))){
            out.writeObject(users);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks to see if the salt is already used for an other user.
     *
     * @param salt The generated salt.
     * @return returns true if the generated salt is unique and false if it
     * already exists.
     */
    private static boolean isUniqueSalt(String salt) {
        return users.values().stream().filter(user -> user.salt.equalsIgnoreCase(salt)).count() == 0;
    }

    /**
     * Loads the users from a file
     */
    private static void loadUsers() {
        File f = Constants.SAVE_FILE;
        
        if (f.exists()) {
            try (var in = new ObjectInputStream(new FileInputStream(f))){
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

    private Main() {
    }
}
