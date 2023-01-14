package vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import vault.fsys.FileFormat;
import vault.fsys.FilePointer;
import vault.fsys.FileSystem;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;

public class Sorter {

    private class NameComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof FileSystemItem && o2 instanceof FileSystemItem) {
                return ((FileSystemItem) o1).getName().compareTo(((FileSystemItem) o2).getName());
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

    private class SizeComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof FileSystemItem && o2 instanceof FileSystemItem) {
                var item1 = (FileSystemItem) o1;
                var item2 = (FileSystemItem) o2;

                return Long.signum(item1.getSize() - item2.getSize());
            } else {
                throw new IllegalArgumentException();
            }
        }
        
    }

    private class TimeComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            if (o1 instanceof FileSystemItem && o2 instanceof FileSystemItem) {
                var item1 = (FileSystemItem) o1;
                var item2 = (FileSystemItem) o2;

                return Long.signum(item2.getCreationDateAsLong() - item1.getCreationDateAsLong());
            } else {
                throw new IllegalArgumentException();
            }
        }

    }

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
    private IndexWheel<FileFormat> indexWheel;
    private Type type;

    public Sorter(FileSystem system) {
        fsys = system;
        type = Type.AZ;
        items = new ArrayList<>();
        indexWheel = new IndexWheel<>();

        indexWheel.add(FileFormat.AUDIO);
        indexWheel.add(FileFormat.DOCUMENT);
        indexWheel.add(FileFormat.IMAGE);
        indexWheel.add(FileFormat.VIDEO);
    }

    public List<Object> sort(List<Object> items) {
        List<Object> folders = new ArrayList<>(); 
        List<Object> pointers = new ArrayList<>();
                
        folders.addAll(items.parallelStream().filter(x -> x instanceof Folder).toList());
        pointers.addAll(items.parallelStream().filter(x -> x instanceof FilePointer).toList());
        
        switch (type) {
            case AZ -> {
                folders = sortAZ(folders, false);
                pointers = sortAZ(pointers, false);
            }
            case ZA -> {
                folders = sortAZ(folders, true);
                pointers = sortAZ(pointers, true);
            }
            case NEWEST -> {
                folders = sortTime(folders, false);
                pointers = sortTime(pointers, false);
            }
            case OLDEST -> {
                folders = sortTime(folders, true);
                pointers = sortTime(pointers, true);
            }
            case SMALLEST -> {
                folders = sortSize(folders, false);
                pointers = sortSize(pointers, false);
            }
            case BIGGEST -> {
                folders = sortSize(folders, true);
                pointers = sortSize(pointers, true);
            }
            case IMAGES_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FileFormat.IMAGE);
            }
            case AUDIO_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FileFormat.AUDIO);
            }
            case DOCUMENTS_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FileFormat.DOCUMENT);
            }
            case VIDEOS_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FileFormat.VIDEO);
            }
        }

        List<Object> result = new ArrayList<>();
        result.addAll(folders);
        result.addAll(pointers);
        return result;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<Object> sortAZ(List<Object> items, boolean inverse) {
        if (inverse) {
            Collections.sort(items, new NameComparator().reversed());
        } else {
            Collections.sort(items, new NameComparator());
        }
        return items;
    }

    public List<Object> sortSize(List<Object> items, boolean inverse) {
        if (inverse) {
            Collections.sort(items, new SizeComparator().reversed());
        } else {
            Collections.sort(items, new SizeComparator());
        }

        return items;
    }

    public List<Object> sortTime(List<Object> items, boolean inverse) {
        if (inverse) {
            Collections.sort(items, new TimeComparator().reversed());
        } else {
            Collections.sort(items, new TimeComparator());
        }

        return items;
    }

    public List<Object> sortByType(List<Object> items, FileFormat type) {
        var audio = new ArrayList<Object>();
        var documents = new ArrayList<Object>();
        var images = new ArrayList<Object>();
        var videos = new ArrayList<Object>();
        var others = new ArrayList<Object>();
        var pointers = items.parallelStream().filter(x -> x instanceof FilePointer).map(x -> (FilePointer) x).toList();
        
        audio.addAll(pointers.parallelStream().filter(FilePointer::isAudio).toList());
        documents.addAll(pointers.parallelStream().filter(FilePointer::isDocument).toList());
        images.addAll(pointers.parallelStream().filter(FilePointer::isImage).toList());
        videos.addAll(pointers.parallelStream().filter(FilePointer::isVideo).toList());
        others.addAll(pointers.parallelStream().filter(x -> !x.isAudio()).filter(x -> !x.isDocument()).filter(x -> !x.isImage()).filter(x -> !x.isVideo()).toList());

        sortAZ(audio, false);
        sortAZ(images, false);
        sortAZ(documents, false);
        sortAZ(videos, false);
        sortAZ(others, false);

        indexWheel.alignWith(type, 0);

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < indexWheel.size(); i++) {
            System.out.println(result.size());
            switch (indexWheel.valueAt(i)) {
                case AUDIO ->
                    result.addAll(audio);
                case DOCUMENT ->
                    result.addAll(documents);
                case IMAGE ->
                    result.addAll(images);
                case VIDEO ->
                    result.addAll(videos);
                default ->
                    throw new IllegalArgumentException();
            }
        }
        result.addAll(others);
        
        return result;
    }
}
