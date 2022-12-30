package vault.fsys.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import vault.Constants;
import vault.encrypt.GlobalKeyManager;
import vault.fsys.FilePointer;

public final class EncryptedIO {

    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
   
    private EncryptedIO(){}
    
    private static byte[] encryptData(byte[] data, SecretKey key, IvParameterSpec iv) {
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    private static byte[] decryptData(byte[] data, SecretKey key, IvParameterSpec iv) {
        try {
            var cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private static void ensureSavePath() {
        File path = Constants.FILES_PATH;
        if (!path.exists() || path.isDirectory()) {
            path.mkdirs();
        }
    }
    
    private static void write(File file, byte[] data) {
        try {
            Files.write(file.toPath(), data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    private static byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
    
    public static void export(FilePointer pointer, byte[] data) {
        System.out.println("Test");
        ensureSavePath();
        var encryptedData = encryptData(data, GlobalKeyManager.getKey(), GlobalKeyManager.getIv());
        write(pointer.getContentFile(), encryptedData);
    }
    
    public static byte[] readContent(FilePointer pointer) {
        var encryptedBytes = readAllBytes(pointer.getContentFile());
        return decryptData(encryptedBytes, GlobalKeyManager.getKey(), GlobalKeyManager.getIv());
    }
}
