package vault.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.List;
import vault.Constants;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import vault.fsys.Folder;

public class UserSaver {
    
    private FileSystem fsys;
    private final File saveFile;
    
    public UserSaver(User user) {
        fsys = user.getFileSystem();
        saveFile = new File(Constants.USER_HOME_PATH.getAbsolutePath() + "/" + user.getUsername());
    }
    
    private List<String> getSaveData() {
        return fsys.getRoot().getSaveData();
    }
    
    private void writeToFile(List<String> data) {
        try (var out = new PrintStream(new FileOutputStream(saveFile))) {
            data.forEach(x -> out.println(x));
        } catch (Exception e) {
            throw new RuntimeException(); 
        }
    }
    
    public void save() {
        var data = getSaveData();
        writeToFile(data);
    }
    
    private List<String> readAllData() {
        try {
            return Files.readAllLines(saveFile.toPath());
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }
    
    private void processFolder(String data) {
        Folder folder = new Folder();
        folder.parse(data);
        folder.setParent(fsys.findFolder(folder.getParentPath()));
        fsys.moveTo(folder.getParent());
        fsys.addFolder(folder);
    }
    
    private void processPointer(String data) {
        FilePointer pointer = new FilePointer();
        pointer.parse(data);
        pointer.setParent(fsys.findFolder(pointer.getParentPath()));
        fsys.moveTo(fsys.findFolder(pointer.getParentPath()));
        fsys.addFilePointer(pointer);
    }
    
    public boolean load() {
        if (!saveFile.exists())
            return false;
        var data = readAllData();
        data.stream().filter(x -> x.startsWith("folder")).forEach(x -> processFolder(x));
        data.stream().filter(x -> x.startsWith("pointer")).forEach(x -> processPointer(x));
        return true;
    }
}
