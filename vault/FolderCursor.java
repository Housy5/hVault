package vault;

import vault.nfsys.FileSystem;
import vault.nfsys.Folder;

public class FolderCursor {
    
    private int size = 0, cursor = 0;
    private Node head, last;
    private final FileSystem fsys;

    public FolderCursor(FileSystem pFsys) {
        fsys = pFsys;
    }
    
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
    
    private Node createDefaultNode() {
        Node node = new Node(fsys.getCurrentFolder());
        return node;
    }
    
    private Node getNodeFor(Folder folder) {
        Node current = head;
        while (!current.value.equals(folder) && current.next != null) {
            current = current.next;
        } 
        
        return current.value.equals(folder) ? current : createDefaultNode();
    }
    
    private Node getCurrentNode() {
        return getNode(cursor - 1);
    }
    
    private void chopByFolder(Folder folder) {
        Node node = getNodeFor(folder);
        chopByNode(node);
    }
    
    private void chopByNode(Node node) {
        if (node == null) {
            //do nothing
        } else if (node == head) {
            head = null;
            last = null;
            size = cursor = 0;
        } else {
            Node prev = node.prev;
            prev.next = null;
            node.prev = null;
            last = prev;
            size = cursor;
        } 
    }
    
    public void chop(Object obj) {
        if (obj instanceof Folder folder) {
            chopByFolder(folder);
        } else if (obj instanceof Node node) {
            chopByNode(node);
        } else {
            throw new IllegalArgumentException();
        }
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
        if (cursor >= size) {
            cursor = size;
            return last.value;
        } else {
            Node current = getCurrentNode();
            if (current.next != null && !isValidFolder(current.next.value)) {
                chop(current.next);
                cursor++;
                return current.value;
            } else {
                cursor++;
                return current.next.value != null ? current.next.value : createDefaultNode().value;
            }
        }
    }
    
    public Folder prev() {
        cursor--;
         if (cursor <= 1) {
             cursor = 1;
             return fsys.getRoot();
         } else {
             return fsys.getCurrentFolder().getParent();
         }
    }

    private boolean isValidFolder(Folder folder) {
        return fsys.getCurrentFolder().containsFolder(folder);
    }
}
