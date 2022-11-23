package vault.password;

import java.io.Serializable;
import java.util.Arrays;
import vault.Constants;
import vault.Main;

public class Password implements Serializable {
    
    private final String hash;
    private final String salt;
    
    private String hash(String str) {
        return Arrays.toString(Constants.messageDigest.digest(Main.mixPassAndSalt(str, salt).getBytes()));
    }
    
    public Password(String pass) {
        salt = Main.generateSalt();
        hash = hash(pass);
    }
    
    public boolean unlock(String str) {
        String strHash = hash(str);
        return strHash.equals(hash);
    }
}
