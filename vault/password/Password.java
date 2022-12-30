package vault.password;

import java.io.Serializable;
import java.util.Arrays;
import vault.Constants;
import vault.Main;

public class Password implements Serializable {
    
    private String hash;
    private String salt;
    
    private String hash(String str) {
        return Arrays.toString(Constants.messageDigest.digest(Main.mixPassAndSalt(str, salt).getBytes()));
    }
    
    private Password() {
        
    }
    
    public Password(String pass) {
        salt = Main.generateSalt();
        hash = hash(pass);
    }
    
    public boolean unlock(String str) {
        String strHash = hash(str);
        return strHash.equals(hash);
    }
    
    @Override
    public String toString() {
        return hash + "//" + salt;
    }
    
    public static Password parse(String str) {
        if (str.equalsIgnoreCase("null"))
            return null;
        var arr = str.split("//");
        var pass = new Password();
        pass.hash = arr[0];
        pass.salt = arr[1];
        return pass;
    }
}
