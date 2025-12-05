/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vault.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class GlassPanel extends JPanel {

    private Point start;
    private Point end;
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (start == null || end == null) {
            return;
        }
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.max(start.x, end.x) - x;
        int height = Math.max(start.y, end.y) - y;
        g.setColor(new Color(0, 120, 215, 25));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(0, 120, 215, 75));
        g.drawRect(x, y, width, height);
    }
    
    public void setStartPoint(Point p) {
        start = p;
    }
    
    public void setEndPoint(Point p) {
        end = p;
        repaint();
    }
    
    public Rectangle getRectangle() {
        if (start == null || end == null)
            return null;
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.max(start.x, end.x) - x;
        int height = Math.max(start.y, end.y) - y;
        return new Rectangle(x, y, width, height);
    }
    
    public void reset() {
        start = null;
        end = null;
        repaint();
    }
}
