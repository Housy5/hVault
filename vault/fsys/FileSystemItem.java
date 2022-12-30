package vault.fsys;

import java.time.LocalDateTime;
import java.util.Objects;
import vault.NameUtilities;

public class FileSystemItem {

    private String name;
    private Folder parent;
    private LocalDateTime creationDate;

    private long creationDateLong = -1;
    private long size;

    public final String getName() {
        return name;
    }

    public final Folder getParent() {
        return parent;
    }

    public final FileSystemItem setName(String name) {
        this.name = name;
        return this;
    }

    public final FileSystemItem setParent(Folder parent) {
        this.parent = parent;
        return this;
    }

    public final FileSystemItem setCreationDate(LocalDateTime date) {
        creationDate = date;
        return this;
    }

    public final LocalDateTime getCreationDate() {
        return creationDate;
    }

    public final long getCreationDateAsLong() {
        if (creationDateLong == -1) {
            int day = creationDate.getDayOfMonth();
            int month = creationDate.getMonthValue();
            int year = creationDate.getYear();
            int hours = creationDate.getHour();
            int minutes = creationDate.getMinute();
            int seconds = creationDate.getSecond();
            creationDateLong = (year * 10000 + month * 100 + day) * 1000000 + hours * 10000 + minutes * 100 + seconds;
        }

        return creationDateLong;
    }

    private String nextFolderName(String name, Folder folder) throws NoAvailableNameException {
        long counter = 1L;
        String result;
        do {
            result = String.format("%s(%d)", name, counter);
            counter++;
        } while (folder.containsFolderName(result) && counter > 0);
        if (counter < 0)
            throw new NoAvailableNameException();
        return result;
    }

    private String nextPointerName(String name, Folder folder) throws NoAvailableNameException {
        long counter = 1L;
        String result;
        String[] tokens = NameUtilities.splitNameAndExtension(name);
        do {
            result = String.format("%s(%d).%s", tokens[0], counter, tokens[1]);
            counter++;
        } while (folder.containsPointerName(result) && counter > 0);
        if (counter < 0)
            throw new NoAvailableNameException();
        return result;
    }

    public final void nextName(Folder folder) throws NoAvailableNameException {
        if (!name.contains("."))
            name = nextFolderName(name, folder);
        else
            name = nextPointerName(name, folder);
    }

    public final FileSystemItem setSize(long size) {
        this.size = size;
        return this;
    }

    public long getSize() {
        return size;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        //hash = 79 * hash + Objects.hashCode(this.parent);
        hash = 79 * hash + Objects.hashCode(this.creationDate);
        hash = 79 * hash + (int) (this.creationDateLong ^ (this.creationDateLong >>> 32));
        hash = 79 * hash + (int) (this.size ^ (this.size >>> 32));
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
        final FileSystemItem other = (FileSystemItem) obj;
        if (this.creationDateLong != other.creationDateLong) {
            return false;
        }
        if (this.size != other.size) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.parent, other.parent)) {
            return false;
        }
        return Objects.equals(this.creationDate, other.creationDate);
    }
}
