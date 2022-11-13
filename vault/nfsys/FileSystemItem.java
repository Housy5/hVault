package vault.nfsys;

import java.io.Serializable;
import java.time.LocalDateTime;

public abstract class FileSystemItem implements Serializable {
    
    
    String name;
    Folder parent;
    LocalDateTime creationDate;
    
    private long creationDateLong = -1;
    
    public final String getName() {
        return name;
    }
    
    public final Folder getParent() {
        return parent;
    }
    
    public final void setName(String name) {
        this.name = name;
    }
    
    public final void setParent(Folder parent) {
        this.parent = parent;
    }
    
    public final void setCreationDate(LocalDateTime date) {
        creationDate = date;
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
    
    public abstract long getSize();
}
