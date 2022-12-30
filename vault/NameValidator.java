package vault;

import java.util.Arrays;
import vault.fsys.Folder;

public class NameValidator {

    public static final char[] ILLEGAL_CHARS = {'/', '<', '>', ';', '\"', '\\',
        '|', '?', '*', '^', '{', '}', '[', ']'};
    public static final int MAX_LENGTH = 100;

    public static final int MIN_RANGE = 31;
    public static final String[] RESERVED_NAMES = {"CON", "PRN", "AUX", "NUL", "COM1",
        "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1",
        "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9", ".."};

    public static boolean isValidPointerName(String fileName) {
        return NameValidator.isValidPointerName(fileName, Main.frameInstance.user.getFileSystem().getCurrent());
    }

    public static boolean isValidFolderName(String folderName, Folder parent) {
        if (folderName.isBlank())
            return false;
        if (isReservedName(folderName))
            return false;
        if (containsIllegalChar(folderName))
            return false;
        if (folderName.length() > MAX_LENGTH)
            return false;
        return !parent.containsFolderName(folderName);
    }

    public static boolean isValidPointerName(String fileName, Folder parent) {
        if (fileName.endsWith("."))
            return false;
        if (fileName.isBlank())
            return false;
        if (fileName.length() > MAX_LENGTH)
            return false;
        if (isFilledWith('.', fileName))
            return false;
        if (containsIllegalChar(fileName))
            return false;
        if (parent.containsPointerName(fileName))
            return false;
        return isReservedName(fileName);
    }

    private static boolean isReservedName(String fileName) {
        return Arrays.stream(RESERVED_NAMES).parallel().filter(x -> x.equalsIgnoreCase(fileName)).count() > 0;
    }

    private static boolean containsIllegalChar(String fileName) {
        for (var c : ILLEGAL_CHARS)
            if (fileName.contains(String.valueOf(c)))
                return true;
        return false;
    }

    private static boolean isFilledWith(char x, String fileName) {
        var chars = fileName.toCharArray();
        for (var c : chars)
            if (c != x)
                return false;
        return true;
    }
}
