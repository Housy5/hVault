package vault.format;

import java.util.HashMap;
import java.util.Map;

public class FormatDetector {
    
    public static final int AUDIO = 0;
    public static final int VIDEO = 1;
    public static final int DOCUMENT = 2;
    public static final int OTHER = 3;
    public static final int IMAGE = 4;
    
    private final String[] audioFormats = {"mp3", "wav", "flac", "aac", "ogg", "wma", "alac"};
    private final String[] videoFormats = {"mpg", "mov", "wmv", "rm", "mp4"};
    private final String[] documentFormats = {"doc", "docx", "odt", "rtf", "tex", "txt", "wpd", "pdf"};
    private final String[] imageFormats = {"jpeg", "jpg", "bpm", "png", "gif", "tiff", "psd", "eps"};
    
    private final Map<String, String> extMap;
    
    private FormatDetector() {
        extMap = new HashMap<>();
    }
    
    private String getExtension(String name) {
        StringBuilder sb = new StringBuilder();
        if (extMap.containsKey(name)) {
            return extMap.get(name);
        }
        
        for (int i = name.length() - 1 ; i >= 0; i--) {
            char c = name.charAt(i);
            
            if (c == '.') {
                break;
            }
            
            sb.append(c);
        }
        
        String ext =  sb.isEmpty() || sb.length() == name.length() ? ""  : sb.reverse().toString();
        extMap.put(name, ext);
        return ext;
    } 
    
    private boolean isAudioFile(String ext) {
        for (String audioFormat : audioFormats) {
            if (audioFormat.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isVideoFile(String ext) {
        for (String vf : videoFormats) {
            if (vf.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isDocument(String ext) {
        for (String doc : documentFormats) {
            if (doc.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isImage(String ext) {
        for (String img : imageFormats) {
            if (img.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
    
    public int detectFormat(String name) {
        String ext = getExtension(name);
        if (isAudioFile(ext)) {
            return AUDIO;
        } else if (isVideoFile(ext)) {
            return VIDEO;
        } else if (isDocument(ext)) {
            return DOCUMENT;
        } else if (isImage(ext)) {
            return IMAGE;
        } else {
            return OTHER;
        }
    }
    
    private static FormatDetector instance = null;
    
    public static FormatDetector instance() {
        if (instance == null) {
            instance = new FormatDetector();
        }
        
        return instance;
    }
}
