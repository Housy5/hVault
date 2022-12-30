package vault.queue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import vault.fsys.FilePointer;
import vault.fsys.Folder;

public class ExportTicket {
    
    private final Object value;
    private final Path path;
    
    public ExportTicket(Object obj, Path path) {
        this.path = path;
        this.value = obj;
    }
    
    public final boolean isFolder() {
        return value instanceof Folder;
    }
    
    public final boolean isFilePointer() {
        return value instanceof FilePointer;
    }
    
    public final Optional<FilePointer> getFilePointer() {
        if (isFilePointer()) {
            return Optional.of((FilePointer) value);
        } else {
            return Optional.empty();
        }
    }
    
    public final Optional<Folder> getFolder() {
        if (isFolder()) {
            return Optional.of((Folder) value);
        } else {
            return Optional.empty();
        }
    }
    
    public final Optional<Path> getPath() {
        if (Files.isDirectory(path)) {
            return Optional.of(path);
        } else {
            return Optional.empty();
        }
    }
    
}
