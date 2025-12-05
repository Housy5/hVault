package vault.gui.status;

import java.util.*;

public class StatusManager {

    private static Map<Integer, Stack<ProgramStatus>> states = new HashMap<>();

    public static void push(ProgramStatus s) {
        states.putIfAbsent(s.getWeight(), new Stack<>());
        states.get(s.getWeight()).push(s);
    }

    public static void pop(ProgramStatus s) {
        states.putIfAbsent(s.getWeight(), new Stack<>());
        states.get(s.getWeight()).pop();
    }
    
    public static ProgramStatus nextStatus() {
        List<Integer> keys = new ArrayList<>();
        keys.addAll(states.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);
        for (int key : keys) {
            if (states.get(key).isEmpty()) {
                continue;
            }
            return states.get(key).peek();
        }
        return ProgramStatus.WAITING;
    }
    
}
