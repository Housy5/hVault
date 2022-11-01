package vault;

import java.util.Arrays;
import vault.nfsys.FilePointer;
import vault.nfsys.Folder;

/**
 *
 * @author olivi
 */
public class NameValidator {

    public static final Character[] ILLEGAL_CHARS = {'/', '<', '>', ';', '\"', '\\',
        '|', '?', '*', '^', '{', '}', '[', ']'};
    public static final int MAX_LENGTH = 100;

    public static final int MIN_RANGE = 31;
    public static final String[] RESERVED_NAMES = {"CON", "PRN", "AUX", "NUL", "COM1",
        "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1",
        "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", ".."};

    public static boolean isValidName(String fileName) {
        return isValidName(fileName, Main.frameInstance.user.fsys.getCurrentFolder());
    }

    public static boolean isValidFolderName(String folderName) {
        for (String str : RESERVED_NAMES) {
            if (str.equalsIgnoreCase(folderName)) {
                return false;
            }
        }
        return !containsIllegalChar(folderName) && folderName.length() < 50;
    }

    public static boolean isValidNameExcluded(String fileName, FilePointer exclude, Folder parent) {
        var name = splitNameAndExtension(fileName)[1];

        if (fileName.endsWith(".") || fileName.endsWith(" ")
                || fileName.isBlank() || fileName.length() > MAX_LENGTH
                || isFilledWith('.', fileName.toCharArray())
                || isFilledWith(' ', fileName.toCharArray())
                || containsIllegalChar(fileName)) {
            return false;
        }

        for (String str : RESERVED_NAMES) {
            if (str.equalsIgnoreCase(name)) {
                return false;
            }
        }

        return !parent.containsFileNameExcluded(fileName, exclude);
    }

    public static boolean isValidName(String fileName, Folder parent) {
        var name = splitNameAndExtension(fileName)[1];

        if (fileName.endsWith(".") || fileName.endsWith(" ")
                || fileName.isBlank() || fileName.length() > MAX_LENGTH
                || isFilledWith('.', fileName.toCharArray())
                || isFilledWith(' ', fileName.toCharArray())
                || containsIllegalChar(fileName)) {
            return false;
        }

        for (String str : RESERVED_NAMES) {
            if (str.equalsIgnoreCase(name)) {
                return false;
            }
        }

        return !parent.containsFileName(fileName);
    }

    public static String[] splitNameAndExtension(String fileName) {
        int sepPoint = fileName.length();

        for (int i = fileName.length() - 1; i >= 0; i--) {
            if (fileName.charAt(i) == '.') {
                sepPoint = i;
                break;
            }
        }

        String name = fileName.substring(0, sepPoint);
        String ext = sepPoint == fileName.length() ? "" : fileName.substring(sepPoint + 1, fileName.length());

        return new String[]{name, ext};
    }

    private static boolean containsIllegalChar(String fileName) {
        return Arrays.stream(ILLEGAL_CHARS)
                .filter(c -> fileName.contains(c.toString()))
                .count() > 0;
    }

    private static boolean isFilledWith(char x, char[] arr) {
        for (char c : arr) {
            if (c != x) {
                return false;
            }
        }
        return true;
    }
}
