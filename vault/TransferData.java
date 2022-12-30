/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import vault.fsys.FilePointer;
import vault.fsys.Folder;

/**
 *
 * @author olivi
 */
public class TransferData implements Serializable {
    
    private List<FilePointer> pointers;
    private volatile Folder origin;
    
    public TransferData(List<FilePointer> pointers, Folder origin) {
        this.origin = origin;
        this.pointers = pointers;
    }

    public List<FilePointer> getPointers() {
        return pointers;
    }

    public void setPointers(List<FilePointer> pointers) {
        this.pointers = pointers;
    }

    public Folder getOrigin() {
        return origin;
    }

    public void setOrigin(Folder origin) {
        this.origin = origin;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.pointers);
        hash = 89 * hash + Objects.hashCode(this.origin);
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
        final TransferData other = (TransferData) obj;
        if (!Objects.equals(this.pointers, other.pointers)) {
            return false;
        }
        return Objects.equals(this.origin, other.origin);
    }
    
    
}
