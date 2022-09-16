package vault.nfsys;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SealedObject;
import vault.encrypt.Encryptor;

public class FilePointer implements Serializable {

    private UUID id;

    private String location;
    private String name;
    private Folder parent;

    private int size;

    public FilePointer() {
        if (usedIds == null) {
            usedIds = new ArrayList<>();
        }

        do {
            id = UUID.randomUUID();
        } while (usedIds.contains(id));

        usedIds.add(id);
    }

    public FilePointer copy() {
        FilePointer fp = new FilePointer();
        fp.setName(new String(name));
        fp.setLocation(new String(location));
        fp.setSize(size);
        fp.getValue().addReference(fp);
        return fp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        if (obj instanceof FilePointer pointer) {
            if (pointer.getID().equals(id) 
                    && pointer.getSize() == size
                    && pointer.getLocation().equals(location)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public UUID getID() {
        return id;
    }
    
    public byte[] getBytes() {
        var file = load();
        return file == null ? new byte[] {} : file.getBytes();
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public Folder getParent() {
        return parent;
    }

    public int getSize() {
        return size;
    }

    public HFile getValue() {
        return load();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.location);
        hash = 79 * hash + this.size;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    private HFile load() {
        File path = new File(location);
        HFile hFile = null;

        if (path.exists() && path.canRead()) {
            hFile = readValue(path);
        }

        return hFile;
    }

    private HFile readValue(File file) {
        try ( var objIn = new ObjectInputStream(new FileInputStream(file))) {
            var value = (HFile) Encryptor.decryptObject((SealedObject) objIn.readObject());
            return value;
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FilePointer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void setSize(int size) {
        this.size = size;
    }

    void addIDToDatabase() {
        if (usedIds == null) {
            usedIds = new ArrayList<>();
        }

        usedIds.add(id);
    }

    void prepareForRemoval() {
        usedIds.remove(id);
    }
    private static List<UUID> usedIds;
}
