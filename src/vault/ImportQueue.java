package vault;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A basic queue structure that will import every ticket sequentially
 *
 * @author olivi
 */
public class ImportQueue extends Thread {

    private Node head = null;
    private boolean importing = false;
    private Node last = null;
    private boolean running = true;

    private final long sleepTime = 1000;

    private class Node {

        final ImportTicket ticket;
        Node next;

        Node(ImportTicket ticket) {
            this.ticket = ticket;
        }
    }

    private ImportQueue() {

    }

    public ImportTicket dequeue() {
        if (head == null) {
            return null;
        }

        ImportTicket ticket = head.ticket;
        leftShift();
        return ticket;
    }

    public boolean enqueue(ImportTicket ticket) {
        if (containsTicket(ticket)) {
            return false;
        } else if (head == null) {
            head = new Node(ticket);
            last = head;
            return true;
        } else {
            last.next = new Node(ticket);
            last = last.next;
            return true;
        }
    }

    public boolean isImporting() {
        return importing;
    }

    @Override
    public void run() {
        while (running) {
            try {
                while (hasNext()) {
                    importing = true;
                    ImportTicket ticket = dequeue();
                    ticket.process();
                }

                importing = false;
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(ImportQueue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stopExporting() {
        running = false;
        instance = null;
    }

    private boolean containsTicket(ImportTicket ticket) {
        Node curr = head;

        if (head == null) {
            return false;
        }

        while (curr.next != null && !curr.ticket.equals(ticket)) {
            curr = curr.next;
        }

        return curr.ticket.equals(ticket);
    }

    private boolean hasNext() {
        return head != null;
    }

    /**
     * Left shifts the head of the queue
     *
     * @return true when the head was successfully shifted one spot to the left
     */
    private boolean leftShift() {
        if (head == null) {
            return false;
        }

        head = head.next;
        return true;
    }

    private static ImportQueue instance = null;

    public static ImportQueue instance() {
        if (instance == null) {
            instance = new ImportQueue();
            instance.start();
        }
        return instance;
    }
}
