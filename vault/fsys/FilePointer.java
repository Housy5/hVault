package vault.fsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import vault.io.FileEncryptor;

public class FilePointer extends FileSystemItem {

    private File content;
    private FileFormat format;
    private String ext;

    public FilePointer() {

    }

    public FilePointer(String name) {
        setName(name);
        initFormat();
    }

    @Override
    public long getSize() {
        return content.getTotalSpace();
    }
    
    public final String getParentPath() {
        return getParent().getPath();
    }

    public final File getContentFile() {
        return content;
    }
    
    public final void setContentFile(File file) {
        this.content = file;
    }

    private void initFormat() {
        format = FileFormatDetector.detectFormat(this.getExtension());
    }

    public FileFormat getFileFormat() {
        return format;
    }
    
    public void setFileFormat(FileFormat format) {
        this.format = format;
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

    public final boolean delete() {
        return content.delete();
    }

    public final void copyContent(File file) {
        byte[] buffer = new byte[1024 * 1024]; // 1MB buffer size

        try ( InputStream inputStream = new FileInputStream(file );  
              OutputStream outputStream = new FileOutputStream(content)) {

            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private final int memLimit = 20_000_000;
    
    public final byte[] getContent() {
        if (content.length() > memLimit)
            return null;
        return FileEncryptor.decryptFile(content);
    }
    

    public final FilePointer copy() {
        FilePointer pointer = new FilePointer(getName());
        pointer.setCreationDate(LocalDateTime.now());
        pointer.setParent(getParent());
        pointer.setSize(getSize());
        pointer.copyContent(content);
        return pointer;
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
