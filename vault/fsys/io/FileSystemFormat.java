package vault.fsys.io;

public interface FileSystemFormat {
    
    public String format();
    public void parse(String data);
}
