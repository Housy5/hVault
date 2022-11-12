package vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vault.nfsys.FilePointer;
import vault.nfsys.FileSystem;
import vault.nfsys.FileSystemItem;

public class Sorter {
    
    private enum Type {
        AZ, ZA, NEWEST, OLDEST, IMAGES_FIRST, AUDIO_FIRST, DOCUMENTS_FIRST, VIDEOS_FIRST, BIGGEST, SMALLEST;
        
        public Type getInverse() {
            var values = Type.values();
            if (ordinal() % 2 == 0) {
                return values[ordinal() + 1];
            } else {
                return values[ordinal() - 1];
            }
        }
        
        public String toUIString() {
            return switch (this) {
                case AZ -> "A -> Z";
                case ZA -> "Z -> A";
                case NEWEST -> "Sort by Newest";
                case OLDEST -> "Sort by Oldest";
                case IMAGES_FIRST -> "Sort by Images";
                case AUDIO_FIRST -> "Sort by Audio";
                case VIDEOS_FIRST -> "Sort by Videos";
                case DOCUMENTS_FIRST -> "Sort by Documents";
                case BIGGEST -> "Sort by Size";
                case SMALLEST -> "Sort by Size";
            };
        }
    }
    
    private boolean inverse = false;
    private FileSystem fsys;
    private List<FileSystemItem> items;
    private Type type;
    
    public Sorter(FileSystem system) {
        fsys = system;
        items = new ArrayList<>();
    }
    
    public void sort(List<FileSystemItem> items) {
        switch (type) {
            case AZ -> bubbleAZ(items, false);
            case ZA -> bubbleAZ(items, true);
        }
    }
    
    public void bubbleAZ(List<FileSystemItem> items, boolean inverse) {
        boolean swapped = true;
        
        while (swapped) {
            swapped = false;
            
            for (int i = 0; i < items.size() - 1; i++) {
                if (items.get(i).getName().compareTo(items.get(i + 1).getName()) < 0) {
                    Collections.swap(items, i, i + 1);
                    swapped = true;
                }
            }
        }
        
        if (inverse) {
            Collections.reverse(items);
        }
    }
    
    public void bubbleBySize(List<FileSystemItem> items, boolean inverse) {
        boolean swapped = true;
        
        while (swapped) {
            swapped = false;
            
            for (int i = 0; i < items.size() - 1; i++) {
                if (items.get(i).getSize() > items.get(i + 1).getSize()) {
                    Collections.swap(items, i, i + 1);
                    swapped = true;
                }
            }
        }
        
        if (inverse) {
            Collections.reverse(items);
        }
    }
    
    public void bubbleByTime(List<FileSystemItem> items, boolean inverse) {
                boolean swapped = true;
        
        while (swapped) {
            swapped = false;
            
            for (int i = 0; i < items.size() - 1; i++) {
                if (items.get(i).getCreationDateAsLong() > items.get(i + 1).getCreationDateAsLong()) {
                    Collections.swap(items, i, i + 1);
                    swapped = true;
                }
            }
        }
        
        if (inverse) {
            Collections.reverse(items);
        }
    }
    
    public void sortByType(List<FilePointer> pointers, int type) {
        
    }
    
}
