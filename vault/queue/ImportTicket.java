package vault.queue;

import java.io.File;
import java.util.Objects;
import java.util.UUID;
import vault.fsys.Folder;

public class ImportTicket {

    private final UUID uuid;

    private final File file;
    private final Folder parent;

    public ImportTicket(File file, Folder parent) {
        this.file = file;
        this.parent = parent;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImportTicket other = (ImportTicket) obj;
        if (!Objects.equals(this.uuid, other.uuid)) {
            return false;
        }
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        return Objects.equals(this.parent, other.parent);
    }
    
    public final File getFile() {
        return file;
    }
    
    public final Folder getParent() {
        return parent;
    }
    
    public final UUID getUUID() {
        return uuid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.uuid);
        hash = 71 * hash + Objects.hashCode(this.file);
        hash = 71 * hash + Objects.hashCode(this.parent);
        return hash;
    }
  
}
