package vault;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import vault.nfsys.Folder;

public class NameUtilities {

    public static final String nextFileName(final String original, final Folder dir) {
        String newName = null;
        int count = 1;
        String[] tokens = NameValidator.splitNameAndExtension(original);

        do {
            newName = tokens[0] + "(" + String.valueOf(count) + ")." + tokens[1];
            count++;
        } while (dir.containsFileName(newName) && count < Integer.MAX_VALUE);

        return count == Integer.MAX_VALUE ? null : newName;
    }

    public static final String nextSystemFileName(final String original, final Path exportPath) {
        String[] tokens = NameValidator.splitNameAndExtension(original);
        Path filePath = Paths.get(exportPath.toString() + "/" + original);
        int count = 1;
        
        while (Files.exists(filePath)) {
            filePath = Paths.get(exportPath.toString() + "/" + tokens[0] + "(" + String.valueOf(count) + ")." + tokens[1]);
            count++;
        }
        
        return count == Integer.MAX_VALUE ? null : filePath.toFile().getName();
    }
    
    public static final String nextSystemFolderName(final String original, final Path exportPath) {
        String newName = "";
        int count = 1;
        
        do {
            newName = original + "(" + String.valueOf(count + ")");
            count++;
        } while (Files.exists(Paths.get(exportPath.toString() + "/" + newName)) && count < Integer.MAX_VALUE);
        
        return count == Integer.MAX_VALUE ? null : newName;
    }
    
    public static final String nextFolderName(final String original, final Folder dir) {
        String newName = null;
        int count = 1;
        
        do {
            newName = original + "(" + String.valueOf(count) + ")";
            count++;
        } while (dir.containsFolderName(newName) && count < Integer.MAX_VALUE);

        return count == Integer.MAX_VALUE ? null : newName;
    }
    
        /**
     * Generates a random file name
     * @return a randomly generated file
     */
    public static final String generateRandomFileName() {
        Random random = new Random();
        int length = random.nextInt(10, 20);
        StringBuilder sb = new StringBuilder();
        String result = "";

        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                sb.append((char) random.nextInt((int) 'a', 'z' + 1));
            }
            sb.append(".vlt");
            result = sb.toString();
        } while (new File(Constants.FILES_PATH.getAbsolutePath() + result).exists());

        return result;
    }
}
