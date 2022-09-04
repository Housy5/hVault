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
            JOptionPane.showMessageDialog(null, String.format("""
            <html><body style='width: 400px;'><h3>Error! We detected an instance that's already running on this computer.</h3><p>In case this message started appearing after a crash. And you can't run the program even when there is no other instance currently active. Go to: <strong>\"%s\"</strong> and delete <strong>\"ledger.vlt\"</strong>. Then try restarting the program.
            """, LEDGER_FILE.getParent()), "info", JOptionPane.INFORMATION_MESSAGE);
            return false;
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
