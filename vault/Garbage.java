package vault;

import java.awt.event.ActionEvent;
import javax.swing.Timer;

public class Garbage {

    private static final long MAX_CACHE_SIZE = (long) Integer.MAX_VALUE;
    
    public static void start() {
        new Timer(5 * 1000, (ActionEvent e) -> {
            Runtime.getRuntime().gc();
        }).start();
    }

}
