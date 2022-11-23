package vault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import vault.format.FormatDetector;
import vault.nfsys.FilePointer;
import vault.nfsys.FileSystem;
import vault.nfsys.FileSystemItem;
import vault.nfsys.Folder;

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

    public List<Object> sort(List<Object> items) {
        List<Object> folders = new ArrayList<>();
        List<Object> pointers = new ArrayList<>();

        for (var item : items) {
            if (item instanceof Folder folder) {
                folders.add(folder);
            } else if (item instanceof FilePointer pointer) {
                pointers.add(pointer);
            }
        }

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
                pointers = sortByType(pointers, FormatDetector.IMAGE);
            }
            case AUDIO_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.AUDIO);
            }
            case DOCUMENTS_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.DOCUMENT);
            }
            case VIDEOS_FIRST -> {
                folders = sortAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.VIDEO);
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

    public List<Object> sortByType(List<Object> items, int type) {
        List<Object> audio = new ArrayList<>(), documents = new ArrayList<>(), images = new ArrayList<>(), videos = new ArrayList<>(), others = new ArrayList<>();
        FormatDetector fm = FormatDetector.instance();

        for (var item : items) {
            if (item instanceof FilePointer pointer) {
                switch (fm.detectFormat(pointer.getName())) {
                    case FormatDetector.AUDIO ->
                        audio.add(pointer);
                    case FormatDetector.DOCUMENT ->
                        documents.add(pointer);
                    case FormatDetector.IMAGE ->
                        images.add(pointer);
                    case FormatDetector.VIDEO ->
                        videos.add(pointer);
                    case FormatDetector.OTHER ->
                        others.add(pointer);
                    default ->
                        throw new IllegalStateException("How did you get here!?");
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }

        sortAZ(audio, false);
        sortAZ(images, false);
        sortAZ(documents, false);
        sortAZ(videos, false);
        sortAZ(others, false);

        indexWheel.alignWith(type, 0);

        List<Object> result = new ArrayList<>();
        for (int i = 0; i < indexWheel.size(); i++) {
            switch (indexWheel.valueAt(i)) {
                case FormatDetector.AUDIO ->
                    result.addAll(audio);
                case FormatDetector.DOCUMENT ->
                    result.addAll(documents);
                case FormatDetector.IMAGE ->
                    result.addAll(images);
                case FormatDetector.VIDEO ->
                    result.addAll(videos);
                default ->
                    throw new IllegalArgumentException();
            }
        }

        return result;
    }
}
