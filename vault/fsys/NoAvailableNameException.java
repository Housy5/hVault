package vault.fsys;

public class NoAvailableNameException extends RuntimeException {

    public NoAvailableNameException() {
    }

    public NoAvailableNameException(String msg) {
        super(msg);
    }
}
