package vault.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import vault.Main;

public class WelcomeScreen {

    JFrame frame;
    Timer timer;
    JLabel image = new JLabel(getIcon("/res/vault.png"));
    int count = 0;

    private ImageIcon getIcon(String path) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getResource(path)));
        } catch (IOException ex) {
            Logger.getLogger(Tile.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(frame, ex.getMessage());
            return null;
        }
    }

    private void createGUI() {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(Main.frameInstance);
        frame.setVisible(true);
        ((JPanel) frame.getContentPane()).setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
    }

    private void startTimer() {
        timer = new Timer(1000, (ActionEvent e) -> {
            count++;
            if (count == 10) {
                dispose();
            }
        });
        timer.start();
    }

    private void addImage() {
        image.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));
        image.setOpaque(false);
        image.setSize(600, 600);
        image.setText("<html>Welcome, <strong>" + Main.frameInstance.user.getUsername() + "</strong>!");
        image.setHorizontalTextPosition(JLabel.CENTER);
        image.setVerticalTextPosition(JLabel.BOTTOM);
        frame.add(image);
    }

    private void addIO() {
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dispose();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();

                    JMenuItem neverShowAgain = new JMenuItem("Never show this again.");
                    neverShowAgain.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseReleased(MouseEvent e) {
                            if (SwingUtilities.isLeftMouseButton(e)) {
                                Main.frameInstance.user.toggleWelcomeMessage();
                                Main.saveUsers();
                                dispose();
                            }
                        }
                    });

                    menu.add(neverShowAgain);
                    menu.show(frame, e.getX(), e.getY());
                }
            }
        });

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == VK_ENTER || e.getKeyLocation() == VK_ESCAPE) {
                    frame.dispose();
                }
            }
        });
    }

    public void dispose() {
        timer.stop();
        frame.dispose();
    }

    public WelcomeScreen() {
        createGUI();
        addImage();
        addIO();
        startTimer();
    }
}
