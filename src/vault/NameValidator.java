package vault;

import vault.nfsys.FilePointer;
import vault.nfsys.Folder;

/**
 *
 * @author olivi
 */
public class NameValidator {

    public static final char[] ILLEGAL_CHARS = {'/', '<', '>', ';', '\"', '\\',
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
        StringBuilder name = new StringBuilder();
        StringBuilder ext = new StringBuilder();
        boolean flag = false;

        for (int i = 0; i < fileName.length(); i++) {
            char c = fileName.charAt(i);

            if (c == '.') {
                flag = true;
            } else if (flag && c == '.') {
                name.append(ext);
                ext.setLength(0);
            } else if (flag) {
                ext.append(c);
            } else {
                name.append(c);
            }
        }

        return new String[]{name.toString(), ext.toString()};
    }

    private static boolean containsIllegalChar(String fileName) {
        for (char c : ILLEGAL_CHARS) {
            if (fileName.contains(String.valueOf(c))) {
                return true;
            }
        }
        return false;
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
