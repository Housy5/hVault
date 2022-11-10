package vault;

import vault.nfsys.Folder;

public class FolderCursor {
    
    private int size = 0, cursor = 0;
    private Node head, last;
    
    private class Node {
        Folder value;
        Node next;
        Node prev;
        
        Node(Folder folder) {
            value = folder;
        }
    }
    
    private Node getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        Node current = head;
        while (index > 0) {
            current = current.next;
            index--;
        }
        
        return current;
    }
    
    private Node getCurrentNode() {
        return getNode(cursor - 1);
    }
    
    public void push(Folder folder) {
        Node node = new Node(folder);
        
        if (size == 0) {
            head = node;
            last = node;
            cursor++;
        } else if (cursor == size) {
            if (last.value.equals(folder)) {
                return;
            }
            last.next = node;
            node.prev = last;
            last = node;
            cursor++;
        } else if (cursor < size) {
            Node current = getCurrentNode();
            if (current.next.value.equals(folder)) {
                cursor++;
                return;
            } else {
                current.next = node;
                node.prev = node;
                last = node;
                size = cursor;
                cursor++;
            }
        }
        size++;
    }
    
    public Folder next() {
        cursor++;
        if (cursor >= size) {
            cursor = size;
            return last.value;
        } else {
            return getCurrentNode().value;
        }
    }
    
    public Folder prev() {
        cursor--;
         if (cursor <= 1) {
             cursor = 1;
             return head.value;
         } else {
             return getCurrentNode().value;
         }
    }
}
