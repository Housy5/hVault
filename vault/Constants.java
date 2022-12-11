package vault;

import java.awt.Color;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import vault.gui.MessageDialog;

public class Constants {

    public final static File USER_HOME_PATH = new File(System.getProperty("user.home") + "/AppData/Local/hVault");
    public final static File FILES_PATH = new File(USER_HOME_PATH.getAbsolutePath() + "/files");
    public final static File SAVE_FILE = new File(USER_HOME_PATH.getAbsolutePath() + "/vault.vlt");
    public final static File THUMBNAIL_FILE = new File(USER_HOME_PATH.getAbsolutePath() + "/thumbnails.vlt");
    public final static File LEDGER_FILE = new File(USER_HOME_PATH.getAbsolutePath() + "/ledger.vlt");
    public final static File UIMODE_FILE = new File(USER_HOME_PATH.getAbsolutePath() + "/uimode.vlt");
    public final static File LIGHT_MODE_SETTINGS = new File(USER_HOME_PATH.getAbsolutePath() + "/light_mode_settings.init");
    public final static File DARK_MODE_SETTINGS = new File(USER_HOME_PATH.getAbsolutePath() + "/dark_mode_settings.init");
    public final static Color BACKGROUND_COLOR = new Color(242, 242, 242);
    public final static Color BORDER_BACKGROUND_COLOR = new Color(0x17202a);
    public final static Color FONT_COLOR = new Color(0x657c8c);

    public final static String PASSWORD_POPUP_TEXT = "Please enter your password: ";
    public final static String UPSIDE_DOWN_EMOTE = "";
    public final static String ANGRY_FACE = "";
    public final static String STOP_HAND = "";
    public final static String SHRUG_PERSON = "";
    public final static String FACE_PALM = "";
    public final static String ACCESS_DENIED_TEXT = "You have been denied access " + FACE_PALM;
    
    public final static UIMode DEFAULT_UIMODE = UIMode.DARK;
    
    public static MessageDigest messageDigest = null;

    public final static String URL_SEPARATOR = " \u27a4 ";
    public final static int MAX_URL_LENGTH = 50;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            if (!USER_HOME_PATH.exists()) {
                USER_HOME_PATH.mkdirs();
            }
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            MessageDialog.show(null, ex.getMessage());
        }
    }
}
