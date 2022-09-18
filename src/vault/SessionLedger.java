package vault;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.JOptionPane;
import static vault.Constants.*;

public class SessionLedger {

    static long id = System.currentTimeMillis();

    static boolean isRunning() {
        try {
            if (!LEDGER_FILE.exists()) {
                return false;
            }

            String line = read();

            return line.split("-")[0].equalsIgnoreCase("Running");
        } catch (Exception e) {
            return false;
        }
    }

    private static String read() throws FileNotFoundException {
        var in = new Scanner(new FileInputStream(LEDGER_FILE));
        var line = in.nextLine();
        in.close();
        return line;
    }

    static boolean attemptStart() {
        if (isRunning()) {
            int option = JOptionPane.showConfirmDialog(null, """
                                                             There seems to be an already running instance!
                                                             
                                                             Would you like to start another instance?
                                                             (Force a new instance only when you are certain there are no other instances running.
                                                             A new instance being forced while an active instance is running may result in loss or corruption of data.)
                                                             """);
            if (option == JOptionPane.YES_OPTION) {
                LEDGER_FILE.delete();
                return attemptStart();
            } else {
                return false;
            }
        } else {
            startSession();
            return true;
        }
    }

    private static void startSession() {
        try {
            var out = new FileOutputStream(LEDGER_FILE);
            out.write(("Running-" + id).getBytes());
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void stopSession() {
        try {
            var out = new FileOutputStream(LEDGER_FILE);
            out.write("Stopped".getBytes());
            out.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
