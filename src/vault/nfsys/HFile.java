package vault.nfsys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;
import vault.encrypt.Encryptor;

public class HFile implements Serializable {

    private byte[] value;

    private final List<FilePointer> references;
    private final File location;

    private void selfDestruct() {
        location.delete();
    }

    private void update() {
        if (!location.exists()) {
            try {
                location.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            }
        }
        try ( var out = new ObjectOutputStream(new FileOutputStream(location))) {
            out.writeObject(Encryptor.encryptObject(this));
            out.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public HFile(File location) {
        references = new ArrayList<>();
        this.location = location;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.hashCode(this.value);
        return hash;
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
        final HFile other = (HFile) obj;
        return Arrays.equals(this.value, other.value);
    }

    public byte[] getBytes() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public void removeReference(FilePointer pointer) {
        references.remove(pointer);
        if (references.isEmpty()) {
            selfDestruct();
        } else {
            update();
        }
    }

    public void addReference(FilePointer pointer) {
        references.add(pointer);
        update();
    }
}
