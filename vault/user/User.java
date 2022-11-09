package vault.user;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import vault.nfsys.FileSystem;

public class User implements Serializable {

    public FileSystem fsys;
    public byte[] hash;
    public String salt;
    public boolean showStartUpMsg;
    public boolean showWelcomeMsg;
    public String username;
    private final byte[] iv = new byte[16];
    private final int keySize = 256;

    private SecretKey key;

    public User() {
        fsys = new FileSystem();
        createKey();
        createIv();
    }

    public final IvParameterSpec getIv() {
        return new IvParameterSpec(iv);
    }
    
    /**
     * Returns the key assigned to this user
     * @return The SecretKey assigned to this User
     */
    public final SecretKey getKey() {
        return key;
    }

    /**
     * Creates an random initialization vector
     */
    private void createIv() {
        new SecureRandom().nextBytes(iv);
    }

    /**
     * Creates a secret key
     */
    private void createKey() {
        try {
            var keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keySize);
            key = keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
