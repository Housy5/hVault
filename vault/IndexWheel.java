package vault;


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
    
    public void add(T t) {
        ValueNode valNode = new ValueNode(t);
        IndexNode idxNode = new IndexNode(size);
        valNode.currentIndex = idxNode;
        
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
    
    public void rotateLeft() {
        ValueNode curr = head;
        
        while (curr != null) {
            curr.currentIndex = curr.currentIndex.prev;
            curr = curr.next;
        }
    }
    
    public void rotateRight() {
        ValueNode curr = head;
        
        while (curr != null) {
            curr.currentIndex = curr.currentIndex.next;
            curr = curr.next;
        }
    }
    
    public int indexFor(T val) {
        ValueNode node = nodeFor(val);
        return node == null ? null : node.currentIndex.value;
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
