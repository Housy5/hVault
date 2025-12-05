package vault.fsys;

import java.util.Arrays;

public final class FileFormatDetector {

    private static final String[] audioFormats = {"mp3", "wav", "flac", "aac", "ogg", "wma", "alac"};
    private static final String[] videoFormats = {"mpg", "mov", "wmv", "rm", "mp4"};
    private static final String[] documentFormats = {"doc", "docx", "odt", "rtf", "tex", "txt", "wpd", "pdf"};
    private static final String[] imageFormats = {"jpeg", "jpg", "bpm", "png", "gif", "tiff", "psd", "eps"};

    private FileFormatDetector() {
    }

    private static boolean contains(String[] extensionArray, String target) {
        return Arrays.stream(extensionArray).parallel().anyMatch(x -> x.equalsIgnoreCase(target));
    }

    private static boolean checkAudio(String extension) {
        return contains(audioFormats, extension);
    }

    private static boolean checkDocument(String extension) {
        return contains(documentFormats, extension);
    }

    private static boolean checkVideo(String extension) {
        return contains(videoFormats, extension);
    }

    private static boolean checkImage(String extension) {
        return contains(imageFormats, extension);
    }

    public final static FileFormat detectFormat(FilePointer pointer) {
        return detectFormat(pointer.getExtension());
    }

    public final static FileFormat detectFormat(String ext) {
        if (checkAudio(ext))
            return FileFormat.AUDIO;
        if (checkVideo(ext))
            return FileFormat.VIDEO;
        if (checkImage(ext))
            return FileFormat.IMAGE;
        if (checkDocument(ext))
            return FileFormat.DOCUMENT;
        return FileFormat.OTHER;
    }
}
