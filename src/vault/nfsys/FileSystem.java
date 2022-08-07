package vault.nfsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vault.Main;
import vault.NameUtilities;
import vault.encrypt.Encryptor;

public class FileSystem implements Serializable {

    private final String filesPath = System.getProperty("user.home") + "/hVault/files/";

    private Folder current;
    private final Folder root;

    /**
     * Validates the path where files are stored.
     */
    private void validateSavePath() {
        File path = new File(filesPath);
        if (path.exists() && path.isDirectory()) {
            return;
        }
        path.mkdirs();
    }

    /**
     * Exports the file to the base system-
     * @param hFile
     * @param fp 
     */
    private void exportSaveFile(HFile hFile, FilePointer fp) {
        validateSavePath();
        File f = new File(fp.getLocation());
        try ( var out = new ObjectOutputStream(new FileOutputStream(f))) {
            out.writeObject(Encryptor.encryptObject(hFile));
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads the specified byte and returns it's bytes
     * @param file the file to be read
     * @return the bytes contained in the specified file
     */
    private byte[] readFileBytes(File file) {
        try ( var in = new FileInputStream(file)) {
            return in.readAllBytes();
        } catch (IOException ex) {
            Logger.getLogger(FileSystem.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * Generates a random file name
     * @return a randomly generated file
     */
    private String generateRandomFileName() {
        Random random = new Random();
        int length = random.nextInt(10, 20);
        StringBuilder sb = new StringBuilder();
        String result = "";

        do {
            sb.setLength(0);
            for (int i = 0; i < length; i++) {
                sb.append((char) random.nextInt((int) 'a', 'z'));
            }
            sb.append(".vlt");
            result = sb.toString();
        } while (new File(filesPath + result).exists());

        return result;
    }

    /**
     * FileSystem Constructor-
     */
    public FileSystem() {
        root = FolderBuilder.createRootFolder();
    }
    
    /**
     * Returns a list of FilePointers that have one of the keys in it's name
     * @param keys The keys words to find in the names
     * @return 
     */
    public List<FilePointer> search(String[] keys) {
        return List.copyOf(root.search(keys));
    }

    
    /**
     * Tries to find the specified folder
     * @param path
     * @return 
     */
    public Folder findFolder(String path) {
        Folder curr = root;
        var tokens = path.split(":");
        var index = 1;

        if (tokens.length == 1) {
            return root;
        }

        outer:
        while (!curr.getFullName().equalsIgnoreCase(path)) {
            for (var folder : curr.getFolders()) {
                if (folder.getName().equalsIgnoreCase(tokens[index])) {
                    curr = folder;
                    index++;
                    continue outer;
                }
            }
            return null;
        }

        return curr;
    }

    /**
     * Recursively registers all file id's in the system
     */
    public void indexFileIDs() {
        root.loadFileIDDatabase();
    }

    /**
     * Returns the root folder 
     * @return the root folder
     */
    public Folder getRoot() {
        return root;
    }

    /**
     * Returns the current folder
     * @return the current folder
     */
    public Folder getCurrentFolder() {
        return current;
    }

    /**
     * Attempts to move to the specified folder
     * @param folder The specified folder
     */
    public void cd(Folder folder) {
        if (folder == null) {
            throw new NullPointerException("Can't move to an empty folder!");
        }
        current = folder;
    }

    /**
     * Attempts to remove the specified folder
     * @param folder 
     */
    public void removeFolder(Folder folder) {
        if (folder.equals(root)) {
            return;
        }

        folder.getParent().removeFolder(folder);
    }

    /**
     * Attempts to remove the specified file from the specified folder
     * @param file the file to be removed
     * @param parent the specified folder
     */
    public void removeFile(FilePointer file, Folder parent) {
        parent.removeFile(file);
        parent.removeFilePointer(file);
    }

    /**
     * Attempts to add one or more files to the specified folder
     * When trying to add files with the same name as other files contained in the specified folder.The program automatically will attempt to add (n) to the back of the file name up to Integer.MAX_VALUE.
     * If for some reason that is still taken or the file size exceeds roughly 2g then the file will be skipped.
     * @param files the list of files to be added
     * @param parent the specified folder
     * @return the amount of files that have been added.
     */
    public int addFiles(List<File> files, Folder parent) {
        int count = 0;
        
        file_loop:
        for (File file : files) {
            try {
                addFile(file, parent);
                count++;
            } catch (FileAlreadyExistsException e) {
                int opt = JOptionPane.showConfirmDialog(Main.frameInstance, "\"" + e.getFile().getName() + "\" already exists in \"" + e.getFolder().getFullName() + "\"\nAre you sure you want to add it again?");

                if (opt == JOptionPane.YES_OPTION) {
                    String newName = NameUtilities.nextFileName(file.getName(), parent);

                    if (newName == null) {
                        JOptionPane.showMessageDialog(Main.frameInstance, "Couldn't add \"" + newName + "\" to the selected folder.");
                        continue;
                    }

                    addFile(file, parent, newName);
                    count++;
                }
            } catch (OversizedFileException e) {
                JOptionPane.showMessageDialog(Main.frameInstance, "\"" + file.getName() + "\" exceeds the 2gb file limit!");
            }
        }
        return count;
    }

    /**
     * Attempts to add a file to the specified Folder
     * @param file the file to be added
     * @param parent the specified folder
     * @throws FileAlreadyExistsException
     * @throws OversizedFileException 
     */
    public void addFile(File file, Folder parent) throws FileAlreadyExistsException,
            OversizedFileException {
        addFile(file, parent, file.getName());
    }

    /**
     * Attempts to add a file to the specified Folder
     * @param file the file to be added
     * @param parent the specified Folder
     * @param rename a new name in case previous names were already taken
     */
    public void addFile(File file, Folder parent, String rename) {
        if (file == null || !file.exists() || parent == null) {
            throw new NullPointerException();
        }
        if (parent.containsFileName(rename)) {
            throw new FileAlreadyExistsException(file, parent);
        }
        if (file.length() > Integer.MAX_VALUE) {
            throw new OversizedFileException();
        }

        byte[] bytes = Encryptor.encrypt(readFileBytes(file));
        String name = generateRandomFileName();
        
        FilePointer fp = new FilePointer();
        fp.setName(rename);
        fp.setLocation(filesPath + name);
        fp.setSize((int) file.length());
        fp.setParent(parent);

        HFile hFile = new HFile(new File(fp.getLocation()));
        hFile.setValue(bytes);
        hFile.addReference(fp);

        exportSaveFile(hFile, fp);
        parent.addFile(fp);
    }

    /**
     * Recursively validates the integrity of all the files in the system.
     */
    public void validateFiles() {
        getRoot().validateFiles();
    }
}
