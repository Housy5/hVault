package vault;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.swing.JOptionPane;
import vault.gui.Frame;
import vault.nfsys.FileSystem;
import vault.nfsys.Folder;

public class ImportTicket {

    private List<File> files;
    private Folder parent;
    private final UUID uuid;

    private final Frame frame;
    private final FileSystem fsys;

    public ImportTicket(List<File> files, Folder parent) {
        this.files = files;
        this.parent = parent;
        this.uuid = UUID.randomUUID();
        this.frame = Main.frameInstance;
        this.fsys = Main.frameInstance.user.fsys;
    }

    public void process() {
        if (files.isEmpty()) {
            return;
        }

        int count = fsys.addFiles(files, parent);

        if (count == 0) {
            return;
        } else if (count == files.size()) {
            String msg = files.size() > 1
                    ? "Do you want to delete all the original files from your hard drive?"
                    : "Do you want to delete the original file from your hard drive?";
            int opt = JOptionPane.showConfirmDialog(frame, msg);

            if (opt == JOptionPane.YES_OPTION) {
                for (File file : files) {
                    file.delete();
                }
            }
        }

        frame.loadFolder(fsys.getCurrentFolder());
        Main.saveUsers();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.files);
        hash = 37 * hash + Objects.hashCode(this.parent);
        hash = 37 * hash + Objects.hashCode(this.uuid);
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
        final ImportTicket other = (ImportTicket) obj;
        if (!Objects.equals(this.files, other.files)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        return Objects.equals(this.uuid, other.uuid);
    }
}
