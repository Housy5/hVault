package vault.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

public class DisplayManager {
    
    private class Row extends JPanel {
        
        Row() {
            FlowLayout layout = new FlowLayout();
            layout.setAlignment(FlowLayout.LEADING);
            layout.setAlignOnBaseline(true);
            setLayout(layout);
        }
    }
    
    private List<Row> rows;
    private List<Component> comps;
    private Row current;
    private JPanel display;
    
    public DisplayManager(JPanel display) {
        this.display = display;
        rows = new ArrayList<>();
        comps = new ArrayList<>();
        current = null;
    }
    
    private void addFirstRow(Component comp) {
        current = new Row();
        current.add(comp);
        rows.add(current);
        comps.add(comp);
        display.add(current);
    }
    
    private void appendRow(Component comp) {
        current = new Row();
        rows.add(current);
        comps.add(comp);
        current.add(comp);
        display.add(current);
    }
    
    public void add(Component comp) {
        synchronized (this) {
            if (current == null) {
                addFirstRow(comp);
                return;
            }
            
            int currentWidth = current.getPreferredSize().width;
            int compWidth = comp.getPreferredSize().width;
                       
            if (currentWidth >= display.getWidth() - compWidth) {    
                appendRow(comp);
                return;
            }
            
            current.add(comp);
            comps.add(comp);
            display.repaint();
        }
    }
    
    public void clear() {
        rows.clear();
        display.removeAll();
        current = null;
        comps.clear();
    }
    
    public void reevaluate() {
        synchronized (this) {
            display.removeAll();
            current = null;
            rows.clear();
            for (Component comp : comps)
                add(comp);
            display.repaint();
        }
    }
}
