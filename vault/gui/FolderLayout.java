package vault.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.Arrays;

public final class FolderLayout implements LayoutManager {

    public static final int DEFAULT_PADDING = 10;
    
    private int padding;
    private final Container observer;
    
    public FolderLayout(Container observer) {
        padding = DEFAULT_PADDING;
        this.observer = observer;
    }
    
    public void setPadding(int padding) {
        if (padding < 0)
            throw new IllegalArgumentException("The padding needs to be either a positive number or 0.");
        this.padding = padding;
    }
    
    public int getPadding() {
        return padding;
    }
    
    private int maxComponentWidth(Component... compArray) {
        return Arrays.stream(compArray).mapToInt(x -> x.getPreferredSize().width).max().orElse(64);
    }
    
    private int maxComponentHeight(Component... compArray) {
        return Arrays.stream(compArray).mapToInt(x -> x.getPreferredSize().height).max().orElse(64);
    }
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }
    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        var width = observer.getWidth();
        var maxWidth = maxComponentWidth(parent.getComponents());
        var maxHeight = maxComponentHeight(parent.getComponents());
        return new Dimension(width + padding * 2, (width / maxWidth) * maxHeight + padding * 2);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        var comps = parent.getComponents();
        var yOffset = maxComponentHeight(parent.getComponents());
        int xPos = padding, yPos = padding;
                
        for (int i = 0; i < comps.length; i++) {
            Component comp = comps[i];
            if (i == 0) {
                xPos = padding;
            } else {
                xPos = comps[i - 1].getX() + comps[i - 1].getWidth() + padding;
            }
            if (xPos > observer.getWidth() - comp.getPreferredSize().width - padding) {
                xPos = padding;
                yPos += yOffset + padding;
            }
            
            comp.setSize(comp.getPreferredSize());
            comp.setLocation(xPos, yPos);
        }
        
        parent.repaint();
    }
}
