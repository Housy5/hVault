package vault.encrypt;

import housy.lib.io.ObjectIO;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import vault.Constants;

public final class GlobalKeyManager {

    private static class GlobalKey implements Serializable {

        private static final long serialVersionUID = 1L;
        private static final SecureRandom random = new SecureRandom();

        private SecretKey key;
        private int keySize = 128;
        private byte[] iv = new byte[16];

        public GlobalKey() {
            generateKey();
            generateIv();
        }

        private void generateKey() {
            try {
                var keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(keySize);
                key = keyGen.generateKey();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        private void generateIv() {
            random.nextBytes(iv);
        }

        public SecretKey getKey() {
            return key;
        }

        public IvParameterSpec getIv() {
            return new IvParameterSpec(iv);
        }
    }

    private static GlobalKey globalKey;
    private static File keyFile = new File(Constants.USER_HOME_PATH.getAbsolutePath() + "/globalkey.hkey");

    private GlobalKeyManager() {
    }

    static {
        initGlobalKey();
    }

    private static void initGlobalKey() {
        if (!keyFile.exists())
            globalKey = createGlobalKey();
        else
            globalKey = loadGlobalKey(keyFile);
    }

    private static GlobalKey loadGlobalKey(File keyFile) {
        try {
            return (GlobalKey) ObjectIO.readObjectFrom(keyFile);
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException();
        }
    }

    private static GlobalKey createGlobalKey() {
        GlobalKey key = new GlobalKey();
        new Thread(() -> save(key)).start();
        return key;
    }

    private static void save(GlobalKey key) {
        try {
            keyFile.createNewFile();
            ObjectIO.putObjectTo(keyFile, key);
        } catch (IOException ex) {
            throw new RuntimeException();
        }
    }

    public static SecretKey getKey() {
        return globalKey.getKey();
    }

    public static IvParameterSpec getIv() {
        return globalKey.getIv();
    }
}