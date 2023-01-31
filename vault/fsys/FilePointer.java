package vault.fsys;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import vault.Constants;
import vault.NameUtilities;
import vault.fsys.io.EncryptedIO;
import vault.fsys.io.FileSystemFormat;

public final class FilePointer extends FileSystemItem implements FileSystemFormat {

    private File content;
    private FileFormat format;
    private String parentPath;
    private String ext;
    
    public FilePointer() {
        
    }
    
    public FilePointer(String name) {
        setName(name);
        initFormat();
        initContentFile();
    }

    public final String getParentPath() {
        return parentPath;
    }
    
    public final File getContentFile() {
        return content;
    }
    
    private void initContentFile() {
        content = new File(Constants.FILES_PATH.getAbsolutePath() + "/" + NameUtilities.generateRandomFileName());
    }
    
    private void initFormat() {
        format = FileFormatDetector.detectFormat(this.getExtension());
    }

    private boolean isFormat(FileFormat format) {
        return this.format == format;
    }

    public final boolean isImage() {
        return isFormat(FileFormat.IMAGE);
    }

    public final boolean isVideo() {
        return isFormat(FileFormat.VIDEO);
    }

    public final boolean isAudio() {
        return isFormat(FileFormat.AUDIO);
    }

    public final boolean isDocument() {
        return isFormat(FileFormat.DOCUMENT);
    }

    public final void saveContent(byte[] data) {
        EncryptedIO.export(this, data);
    }
    
    public final void saveBigFile(File file) {
        
    }
    
    public final byte[] getContent() {
        if (content == null)
            throw new RuntimeException("There is no content file!");
        return EncryptedIO.readContent(this);
    }
    
    public final boolean delete() {
        return content.delete();
    }
    
    public final FilePointer copy() {
        FilePointer pointer = new FilePointer(getName());
        pointer.setCreationDate(LocalDateTime.now());
        pointer.setParent(getParent());
        pointer.setSize(getSize());
        pointer.saveContent(EncryptedIO.readContent(this));
        return pointer;
    }
    
    @Override
    public String format() {
        //type::name::creation_date::parent_url::content_file_name::file_format::size
        return "pointer::" + getName() + "::" + getCreationDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "::" + getParent().getPath() + "::" + content.getAbsolutePath() + "::" + format.toString() + "::" + getSize();
    }

    @Override
    public void parse(String data) {
        String[] arr = data.split("::");
        if (!arr[0].equals("pointer"))
            throw new IllegalArgumentException();
        this.setName(arr[1]);
        this.setCreationDate(LocalDateTime.parse(arr[2]));
        parentPath = arr[3]; 
        content = new File(arr[4]);
        format = FileFormat.valueOf(arr[5]);
        setSize(Long.parseLong(arr[6]));
    }
    
    private String parseExtension() {
        var name = getName();
        int partition = name.lastIndexOf(".");
        return partition == -1 || partition == name.length() ? "" : name.substring(partition + 1);
    }
    
    public final String getExtension() {
        if (ext == null)
            ext = parseExtension();
        return ext;
    }
}
