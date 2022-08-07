package vault.nfsys;

import java.io.File;

public class FileAlreadyExistsException extends RuntimeException {

    private File file;
    private Folder folder;

    /**
     * Creates a new instance of <code>FileAlreadyExistsException</code> without
     * detail message.
     *
     * @param file The file that caused the exception to be thrown.
     * @param folder The Folder that already contained the file.
     */
    public FileAlreadyExistsException(File file, Folder folder) {
        this.file = file;
        this.folder = folder;
    }

    /**
     * Constructs an instance of <code>FileAlreadyExistsException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public FileAlreadyExistsException(String msg) {
        super(msg);
    }

    /**
     *
     * @return The file that triggered the exception.
     */
    public File getFile() {
        return file;
    }

    /**
     *
     * @return The folder that already contained the specified file.
     */
    public Folder getFolder() {
        return folder;
    }
}
