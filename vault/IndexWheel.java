package vault;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class IndexWheel<T> {
    
    private class IndexNode {
        int value;
        IndexNode prev;
        IndexNode next;
        
        public IndexNode(int idx) {
            value = idx;
        }
    }
    
    private class ValueNode {
        T value;
        ValueNode next;
        ValueNode prev;
        IndexNode currentIndex;
        
        public ValueNode(T val) {
            value = val;
        }
    }
    
    private ValueNode head;
    private ValueNode tail;
    
    private IndexNode idxHead;
    private IndexNode idxTail;
    
    private int size = 0;
    private Map<T ,Integer> indexMap;
    
    public IndexWheel() {
        indexMap = new HashMap<>();
    }
    
    public void clear() {
        head = null;
        tail = null;
        idxHead = null;
        idxTail = null;
        size = 0;
        indexMap.clear();
    }
    
    public void add(T t) {
        ValueNode valNode = new ValueNode(t);
        IndexNode idxNode = new IndexNode(size);
        valNode.currentIndex = idxNode;
        indexMap.put(t, idxNode.value);
        
        if (size == 0) {
           idxHead = idxNode;
           idxTail = idxNode;
           
           head = valNode;
           tail = valNode;
        } else {
            idxTail.next = idxNode;
            idxNode.prev = idxTail;
            idxTail = idxNode;
            
            tail.next = valNode;
            valNode.prev = tail;
            tail = valNode;
        }
        
        idxHead.prev = idxTail;
        idxTail.next = idxHead;
        size++;
    }
    
    public int size() {
        return size;
    }
    
    public void alignWith(T t, int index) {
        int idx = indexFor(t);
        
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (idx == -1) {
            throw new NoSuchElementException();
        } else if (idx < index) {
            int distance = index - idx;
            
            for (int i = 0; i < distance; i++) {
                rotateRight();
            }
        } else if (idx > index) {
            int distance = idx - index;
            
            for (int i = 0; i < distance; i++) {
                rotateLeft();
            }
        }
    }
    
    public void rotateLeft() {
        ValueNode curr = head;
        
        while (curr != null) {
            curr.currentIndex = curr.currentIndex.prev;
            indexMap.put(curr.value, curr.currentIndex.value);
            curr = curr.next;
        }
    }
    
    public void rotateRight() {
        ValueNode curr = head;
        
        while (curr != null) {
            curr.currentIndex = curr.currentIndex.next;
            indexMap.put(curr.value, curr.currentIndex.value);
            curr = curr.next;
        }
    }
    
    public int indexFor(T val) {
        return indexMap.getOrDefault(val, -1);
    }
    
    public T valueAt(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        
        ValueNode curr = head;
        while (curr.currentIndex.value != index) {
            curr = curr.next;
        }
        
        return curr.value;
    }
    
    private ValueNode nodeFor(T val) {
        ValueNode curr = head;
        ValueNode result = null ;
        
        while (curr != null && result == null) {
            if (curr.value.equals(val))
                result = curr;
            curr = curr.next;
        }
        
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        ValueNode curr = head;
        while (curr != null) {
            sb.append(curr.value.toString()).append(": ").append(String.valueOf(curr.currentIndex.value)).append("\n");
            curr = curr.next;
        }
        
        return sb.toString();
    }
}
