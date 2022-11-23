package vault.nfsys;

public class OversizedFileException extends RuntimeException {

    public OversizedFileException() {

    }

    public OversizedFileException(String msg) {
        super(msg);
    }
}
