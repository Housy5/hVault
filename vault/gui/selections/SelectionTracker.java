package vault.gui.selections;

import java.util.*;
import vault.gui.Tile;

public class SelectionTracker {
    
    private final List<Tile> selectedTiles;
    
    public SelectionTracker() {
        selectedTiles = new ArrayList<>();
    }
    
    public void track(Tile tile) {
        if (!selectedTiles.contains(tile)) {
            selectedTiles.add(tile);
        }
    }
    
    public void untrack(Tile tile) {
        selectedTiles.remove(tile);
    }
    
    public void resetSelections() {
        selectedTiles.forEach(x -> x.toggleSelected());
        selectedTiles.clear();
    }
    
    public List<Tile> getSelectedTiles() {
        return List.copyOf(selectedTiles);
    }
    
    public int getSelectionCount() {
        return selectedTiles.size();
    }
}
