package vault.gui.status;

public enum ProgramStatus {
    IMPORTING(2), EXPORTING(2), OPENING(1), WAITING(0);
    
    private int weight = 0;
    
    private ProgramStatus(int weight) {
        this.weight = weight;
    }
    
    public int getWeight() {
        return weight;
    }
}
