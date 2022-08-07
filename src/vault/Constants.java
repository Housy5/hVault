package vault;

import java.awt.Color;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Constants {

    public final static File USER_HOME_PATH = new File(System.getProperty("user.home") + "/hVault");
    public final static File SAVE_FILE = new File(USER_HOME_PATH.getAbsolutePath() + "/vault.vlt");
    public final static File LEDGER_FILE = new File(USER_HOME_PATH.getAbsoluteFile() + "/ledger.vlt");
    public final static Color BACKGROUND_COLOR = new Color(242, 242, 242);
    public final static Color BORDER_BACKGROUND_COLOR = new Color(0x17202a);

    public final static String PASSWORD_POPUP_TEXT = "<html><h3>Please enter your password: ";
    public final static String ACCESS_DENIED_TEXT = "<html><h3>Access denied!";

    public static MessageDigest messageDigest = null;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            if (!USER_HOME_PATH.exists()) {
                USER_HOME_PATH.mkdirs();
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
}
