package vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import vault.format.FormatDetector;
import vault.nfsys.FilePointer;
import vault.nfsys.FileSystem;
import vault.nfsys.FileSystemItem;
import vault.nfsys.Folder;

public class Sorter {

    public static enum Type {
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
                case AZ ->
                    "A -> Z";
                case ZA ->
                    "Z -> A";
                case NEWEST ->
                    "Sort by Newest";
                case OLDEST ->
                    "Sort by Oldest";
                case IMAGES_FIRST ->
                    "Sort by Images";
                case AUDIO_FIRST ->
                    "Sort by Audio";
                case VIDEOS_FIRST ->
                    "Sort by Videos";
                case DOCUMENTS_FIRST ->
                    "Sort by Documents";
                case BIGGEST ->
                    "Sort by Size";
                case SMALLEST ->
                    "Sort by Size";
            };
        }
    }

    private FileSystem fsys;
    private List<FileSystemItem> items;
    private IndexWheel<Integer> indexWheel;
    private Type type;

    public Sorter(FileSystem system) {
        fsys = system;
        type = Type.AZ;
        items = new ArrayList<>();
        indexWheel = new IndexWheel<>();

        indexWheel.add(FormatDetector.AUDIO);
        indexWheel.add(FormatDetector.DOCUMENT);
        indexWheel.add(FormatDetector.IMAGE);
        indexWheel.add(FormatDetector.VIDEO);
    }

    public List<? extends FileSystemItem> sort(List<? extends FileSystemItem> items) {
        return switch (type) {
            case AZ ->
                bubbleAZ(items, false);
            case ZA ->
                bubbleAZ(items, true);
            case NEWEST ->
                bubbleByTime(items, false);
            case OLDEST ->
                bubbleByTime(items, true);
            case SMALLEST ->
                bubbleBySize(items, false);
            case BIGGEST ->
                bubbleBySize(items, true);
            case IMAGES_FIRST ->
                sortByType(items, FormatDetector.IMAGE);
            case AUDIO_FIRST ->
                sortByType(items, FormatDetector.AUDIO);
            case DOCUMENTS_FIRST ->
                sortByType(items, FormatDetector.DOCUMENT);
            case VIDEOS_FIRST ->
                sortByType(items, FormatDetector.VIDEO);
        };
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public List<? extends FileSystemItem> bubbleAZ(List<? extends FileSystemItem> items, boolean inverse) {
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
        
        return items;
    }

    public List<? extends FileSystemItem> bubbleBySize(List< ? extends FileSystemItem> items, boolean inverse) {
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
        
        return items;
    }

    public List<? extends FileSystemItem> bubbleByTime(List<? extends FileSystemItem> items, boolean inverse) {
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
        
        return items;
    }

    public List<FileSystemItem> sortByType(List<? extends FileSystemItem> items, int type) {
        List<FilePointer> pointers = (List<FilePointer>) items.stream().filter(x -> x instanceof FilePointer).toList();
        List<Folder> folders = (List<Folder>) items.stream().filter(x -> x instanceof Folder).toList();
        bubbleAZ(folders, false);
        
        FormatDetector fm = FormatDetector.instance();
        List<FilePointer> images = pointers.stream().filter(x -> fm.detectFormat(x.getName()) == FormatDetector.IMAGE).toList();
        bubbleAZ(images, false);
        List<FilePointer> audio = pointers.stream().filter(x -> fm.detectFormat(x.getName()) == FormatDetector.AUDIO).toList();
        bubbleAZ(audio, false);
        List<FilePointer> videos = pointers.stream().filter(x -> fm.detectFormat(x.getName()) == FormatDetector.VIDEO).toList();
        bubbleAZ(videos, false);
        List<FilePointer> documents = pointers.stream().filter(x -> fm.detectFormat(x.getName()) == FormatDetector.DOCUMENT).toList();
        bubbleAZ(documents, false);
        List<FilePointer> others = pointers.stream().filter(x -> fm.detectFormat(x.getName()) == FormatDetector.OTHER).toList(); 
        bubbleAZ(others, false);
        indexWheel.alignWith(type, 0);
        
        List<FileSystemItem> result = new ArrayList<>();
        result.addAll(folders);
        
        for (int i = 0; i < indexWheel.size(); i++) {
            switch (indexWheel.valueAt(i)) {
                case FormatDetector.AUDIO -> result.addAll(audio);
                case FormatDetector.DOCUMENT -> result.addAll(documents);
                case FormatDetector.IMAGE -> result.addAll(images);
                case FormatDetector.VIDEO -> result.addAll(videos);
                default -> throw new IllegalArgumentException();
            }
        }
       
        return result;
    }
}
