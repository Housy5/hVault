package vault;

import java.io.File;
import vault.nfsys.Folder;

public class NameUtilities {

    public static final String nextFileName(final String original, final Folder dir) {
        String newName = null;
        int count = 1;
        String[] tokens = NameValidator.splitNameAndExtension(original);

        do {
            newName = tokens[0] + "(" + String.valueOf(count) + ")." + tokens[1];
            count++;
        } while (((dir != null) && (dir.containsFileName(newName))
                || (new File(newName).exists() && dir == null))
                && count < Integer.MAX_VALUE);

        return count == Integer.MAX_VALUE
                ? null
                : newName;
    }

    public static final String nextFolderName(final String original, final Folder dir) {
        String newName = null;
        int count = 1;

        do {
            newName = original + "(" + String.valueOf(count) + ")";
        } while (dir.containsFolderName(newName) && count < Integer.MAX_VALUE);

        return count == Integer.MAX_VALUE ? null : newName;
    }
}
