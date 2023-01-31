package vault.fsys.io;

public final class ByteBuffer {
    
    public static final int BUFFER_FULL = -1;
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    private static final int DEFAULT_BUFFER_SIZE = 16;
    
    private byte[] buffer;
    private int cursor;
    private int bufferSize;
    
    public ByteBuffer(int bufferSize) {
        if (bufferSize < 0)
            throw new IllegalArgumentException("The buffer's can't be a less than 0.");
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
        cursor = 0;
    }
    
    public ByteBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }
    
    public boolean add(byte b) {
        if (cursor == bufferSize)
            return false;
        buffer[cursor] = b;
        cursor++;
        return true;
    }
    
    public void clear() {
        for (int i = 0; i < bufferSize; i++)
            buffer[i] = 0;
        cursor = 0;
    }
    
    public byte[] toArray() {
        return buffer;
    }
    
    public boolean isFull() {
        return cursor == bufferSize;
    }
    
    public boolean isEmpty() {
        return cursor == 0;
    }
}
