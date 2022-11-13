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
                folders = bubbleAZ(folders, false);
                pointers = bubbleAZ(pointers, false);
            }
            case ZA -> {
                folders = bubbleAZ(folders, true);
                pointers = bubbleAZ(pointers, true);
            }
            case NEWEST -> {
                folders = bubbleByTime(folders, false);
                pointers = bubbleByTime(pointers, false);
            }
            case OLDEST -> {
                folders = bubbleByTime(folders, true);
                pointers = bubbleByTime(pointers, true);
            }
            case SMALLEST -> {
                folders = bubbleBySize(folders, false);
                pointers = bubbleBySize(pointers, false);
            }
            case BIGGEST -> {
                folders = bubbleBySize(folders, true);
                pointers = bubbleBySize(pointers, true);
            }
            case IMAGES_FIRST -> {
                folders = bubbleAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.IMAGE);
            }
            case AUDIO_FIRST -> {
                folders = bubbleAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.AUDIO);
            }
            case DOCUMENTS_FIRST -> {
                folders = bubbleAZ(folders, false);
                pointers = sortByType(pointers, FormatDetector.DOCUMENT);
            }
            case VIDEOS_FIRST -> {
                folders = bubbleAZ(folders, false);
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

    public List<Object> bubbleAZ(List<Object> items, boolean inverse) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < items.size() - 1; i++) {
                Object obj = items.get(i);
                Object obj2 = items.get(i + 1);
                if (obj instanceof FileSystemItem && obj2 instanceof FileSystemItem) {
                    var item = (FileSystemItem) obj;
                    var item2 = (FileSystemItem) obj2;
                    if (item.getName().compareTo(item2.getName()) > 0) {
                        Collections.swap(items, i, i + 1);
                        swapped = true;
                    }
                }
            }
        }

        if (inverse) {
            Collections.reverse(items);
        }

        return items;
    }

    public List<Object> bubbleBySize(List<Object> items, boolean inverse) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < items.size() - 1; i++) {
                Object obj = items.get(i);
                Object obj2 = items.get(i + 1);

                if (obj instanceof FileSystemItem && obj2 instanceof FileSystemItem) {
                    var item = (FileSystemItem) obj;
                    var item2 = (FileSystemItem) obj2;

                    if (item.getSize() > item2.getSize()) {
                        Collections.swap(items, i, i + 1);
                        swapped = true;
                    }
                }
            }
        }

        if (inverse) {
            Collections.reverse(items);
        }

        return items;
    }

    public List<Object> bubbleByTime(List<Object> items, boolean inverse) {
        boolean swapped = true;

        while (swapped) {
            swapped = false;

            for (int i = 0; i < items.size() - 1; i++) {
                Object obj = items.get(i);
                Object obj2 = items.get(i + 1);

                if (obj instanceof FileSystemItem && obj2 instanceof FileSystemItem) {
                    var item = (FileSystemItem) obj;
                    var item2 = (FileSystemItem) obj2;

                    if (item.getCreationDateAsLong() > item2.getCreationDateAsLong()) {
                        Collections.swap(items, i, i + 1);
                        swapped = true;
                    }
                }
            }
        }

        if (inverse) {
            Collections.reverse(items);
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

        bubbleAZ(audio, false);
        bubbleAZ(images, false);
        bubbleAZ(documents, false);
        bubbleAZ(videos, false);
        bubbleAZ(others, false);

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
