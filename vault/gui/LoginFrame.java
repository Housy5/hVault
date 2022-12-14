package vault.gui;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import static java.awt.event.KeyEvent.VK_ENTER;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import vault.Constants;
import vault.Export;
import vault.IconUtil;
import vault.Main;
import vault.interfaces.Updatable;
import vault.user.User;

public class LoginFrame extends javax.swing.JFrame implements Updatable{

    public  BufferedImage currentIcon;
    private long lastPress;
    private final long MINIMUM_WAIT_TIME = 1000;

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);

        try {
            currentIcon = IconUtil.getInstance().getImage("/res/vault (256).png");
            setIconImage(IconUtil.getInstance().getImage("/res/vault.png"));
            modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
        } catch (IOException ex) {
            ex.printStackTrace();
            MessageDialog.show(this, ex.getMessage());
        }

        jTextField1.requestFocus();
    }

    @Override
    public void update() {
        try {
            modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
        } catch (IOException ex) {
            Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(this, ex.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        modelbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("hVault - Login");
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("LOGIN");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel2.setText("Username: ");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel3.setText("Password: ");

        jPasswordField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyReleased(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setText("SIGN UP");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton1MouseReleased(evt);
            }
        });
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jButton1KeyReleased(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("LOG IN");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jButton2KeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vault (256).png"))); // NOI18N
        jLabel4.setToolTipText("<html><strong>Icon created by:</strong><p>Nikita Golubev");

        modelbl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/eye_light_mode.png"))); // NOI18N
        modelbl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        modelbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                modelblMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(modelbl))))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(modelbl)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //signUp();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * Switches over to the sign up frame.
     */
    private void signUp() {
        var sf = new SignUpFrame();
        sf.setLocationRelativeTo(this);
        EventQueue.invokeLater(() -> sf.setVisible(true));

        dispose();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //login();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * Rotates the logo
     *
     * @param user the user to be logged in.
     */
    private void rotateWheel(User user) {
        new Thread(() -> {
            var img = currentIcon;
            int turns = 10;

            var startTime = System.nanoTime();
            var delta = 0d;
            var updateTime = Math.pow(10, 9) / 60d;

            while (turns > 0) {

                var elapsed = System.nanoTime() - startTime;
                delta += elapsed / updateTime;

                if (delta >= 1) {
                    var rads = Math.toRadians(90);
                    var sin = Math.abs(Math.sin(rads));
                    var cos = Math.abs(Math.cos(rads));

                    var w = (int) Math.floor(img.getWidth() * cos + img.getHeight() * sin);
                    var h = (int) Math.floor(img.getHeight() * cos + img.getWidth() * sin);
                    var rotatedImg = new BufferedImage(w, h, img.getType());
                    var at = new AffineTransform();
                    at.translate(w / 2, h / 2);
                    at.rotate(rads, 0, 0);
                    at.translate(-img.getWidth() / 2, -img.getHeight() / 2);

                    AffineTransformOp rotateOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
                    rotateOp.filter(img, rotatedImg);

                    jLabel4.setIcon(new ImageIcon(rotatedImg));
                    jLabel4.repaint();
                    jLabel4.revalidate();
                    img = rotatedImg;
                    turns--;
                    delta--;
                }

                startTime = System.nanoTime();
            }

            switchToMain(user);
        }).start();
    }

    /**
     * The login method
     */
    private void login() {
        String username = jTextField1.getText();
        String password = jPasswordField1.getText();

        if (username.isBlank() || password.isBlank()) {
            return;
        }

        username = username.trim();
        password = password.trim();

        if (Main.users.containsKey(username)) {
            var user = Main.users.get(username);
            if (user.getPassword().unlock(password)) {
                rotateWheel(user);
            } else {
                MessageDialog.show(this, Constants.ACCESS_DENIED_TEXT);
            }
        } else {
            MessageDialog.show(this, Constants.ACCESS_DENIED_TEXT);
        }
    }

    public void switchToMain(User user) {
        Frame frame = new Frame(user);
        frame.setLocationRelativeTo(this);
        EventQueue.invokeLater(() -> frame.setVisible(true));
        Main.frameInstance = frame;
        frame.initTimer();
        Export.startIOMonitor();
        frame.loadFolder(user.getFileSystem().getRoot());
        dispose();

        if (user.showStartUpMessage()) {
            Runnable runnable = new Runnable() {
                public void run() {
                    var startUpMsg = new StartUpMessage(frame, true);
                    startUpMsg.setVisible(true);
                }
            };
            EventQueue.invokeLater(runnable);
        }

        if (user.showWelcomeMessage()) {
            Runnable runnable = new Runnable() {
                public void run() {
                    new WelcomeScreen();
                }
            };
            EventQueue.invokeLater(runnable);
        }
    }

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        long now = System.currentTimeMillis();

        if (evt.getKeyCode() == KeyEvent.VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            jPasswordField1.requestFocus();
            lastPress = now;
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jPasswordField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyReleased
        long now = System.currentTimeMillis();

        if (evt.getKeyCode() == KeyEvent.VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            login();
            lastPress = now;
        }
    }//GEN-LAST:event_jPasswordField1KeyReleased

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed

    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed

    }//GEN-LAST:event_jButton1KeyPressed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //System.out.println("Test");
    }//GEN-LAST:event_formWindowClosing

    private void jButton2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyReleased
        long now = System.currentTimeMillis();

        if (evt.getKeyCode() == VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            login();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton2KeyReleased

    private void jButton1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyReleased
        long now = System.currentTimeMillis();

        if (evt.getKeyCode() == VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            signUp();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton1KeyReleased

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        long now = System.currentTimeMillis();

        if (SwingUtilities.isLeftMouseButton(evt) && now - lastPress > MINIMUM_WAIT_TIME) {
            login();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton2MouseReleased

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        long now = System.currentTimeMillis();

        if (SwingUtilities.isLeftMouseButton(evt) && now - lastPress > MINIMUM_WAIT_TIME) {
            signUp();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton1MouseReleased

    private void modelblMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_modelblMouseReleased
        if (SwingUtilities.isLeftMouseButton(evt)) {
            Main.toggleUIMode(this);
        }
    }//GEN-LAST:event_modelblMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private volatile javax.swing.JLabel jLabel4;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel modelbl;
    // End of variables declaration//GEN-END:variables
}
