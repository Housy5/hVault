package vault;

public class FileAlreadyExistsException extends RuntimeException {

    public FileAlreadyExistsException() {
    }

    public FileAlreadyExistsException(String msg) {
        super(msg);
    }
}
