package vault.queue;

import java.util.LinkedList;
import java.util.List;

public class ExportQueue implements Runnable {
    
    private boolean running;
    private Thread thread;
    
    private final List<ExportTicket> tickets;
    private final List<ExportTicket> exports;
    
    private ExportQueue() {
        running = false;
        tickets = new LinkedList<>();
        exports = new LinkedList<>();
        thread = new Thread(this);
    }
    
    public final void start() {
        
    }
    
    @Override
    public void run() {
        while (running) {
            
        }
    }
    
    private static ExportQueue instance;
    
    public static ExportQueue getInstance() {
        if (instance == null) {
            instance = new ExportQueue();
        }
        
        return instance;
    }
}
