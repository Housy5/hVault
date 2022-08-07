package vault.encrypt;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import vault.Main;

public class Encryptor {

    private static EncryptionStrategy strategy;

    private final static EncryptionStrategy DEFAULT_STRATEGY = new InvertStrategy();

    static {
        strategy = DEFAULT_STRATEGY;
    }

    public static EncryptionStrategy getStrategy() {
        return strategy;
    }

    public static void setStrategy(EncryptionStrategy strategy) {
        Encryptor.strategy = strategy;
    }

    public static byte[] encrypt(byte[] arr) {
        if (strategy == null) {
            throw new NullPointerException("The Encryption Strategy can't be null.");
        }
        return strategy.encrypt(arr);
    }

    public static byte[] decode(byte[] arr) {
        if (strategy == null) {
            throw new NullPointerException("The Encryption Strategy can't be null.");
        }
        return strategy.decode(arr);
    }
    
    public static SealedObject encryptObject(Serializable object) {
        try {
            var user = Main.frameInstance.user;
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, user.getKey(), user.getIv());
            
            SealedObject sealedObject = new SealedObject(object, cipher);
            return sealedObject;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | IllegalBlockSizeException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Serializable decryptObject(SealedObject object) {
        try {
            var user = Main.frameInstance.user;
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, user.getKey(), user.getIv());
            return (Serializable) object.getObject(cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Encryptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
