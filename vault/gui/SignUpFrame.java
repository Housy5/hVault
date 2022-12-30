package vault.gui;

import java.awt.EventQueue;
import static java.awt.event.KeyEvent.VK_ENTER;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import vault.Constants;
import vault.Export;
import vault.IconUtil;
import vault.Main;
import vault.interfaces.Updatable;
import vault.password.Password;
import vault.user.User;
import vault.user.UserFactory;

public class SignUpFrame extends javax.swing.JFrame implements Updatable {

    public BufferedImage currentIcon;
    private long lastPress;
    private final long MINIMUM_WAIT_TIME = 1000;


    public SignUpFrame() {
        initComponents();

        try {
            currentIcon = IconUtil.getInstance().getImage("/res/vault (256).png");
            setIconImage(IconUtil.getInstance().getImage("/res/vault.png"));
            modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
        } catch (IOException ex) {
            ex.printStackTrace();
            MessageDialog.show(null, ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jPasswordField2 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        modelbl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("hVault - Sign Up");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SIGN UP");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Username: ");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setText("Password: ");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Password again:");

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jPasswordField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });

        jPasswordField2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jPasswordField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField2KeyPressed(evt);
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
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("BACK");
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
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/vault (256).png"))); // NOI18N
        jLabel5.setToolTipText("<html><strong>Icon created by:</strong><p>Nikita Golubev");

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
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(jPasswordField2, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel3))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                            .addComponent(jTextField1)))
                                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(modelbl)))))
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
                        .addComponent(jLabel5))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jPasswordField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(modelbl)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    }//GEN-LAST:event_jButton2ActionPerformed

        @Override
    public void update() {
        try {
            modelbl.setIcon(IconUtil.getInstance().getUIModeIcon(Main.getUIMode()));
        } catch (IOException ex) {
            Logger.getLogger(SignUpFrame.class.getName()).log(Level.SEVERE, null, ex);
            MessageDialog.show(this, ex.getMessage());
        }
    }
    
    private void back() {
        var lf = new LoginFrame();
        lf.setLocationRelativeTo(this);
        lf.setVisible(true);

        dispose();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    }//GEN-LAST:event_jButton1ActionPerformed

    private void rotateWheel(User user) {
        new Thread(() -> {
            BufferedImage img = currentIcon;
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

                    jLabel5.setIcon(new ImageIcon(rotatedImg));
                    jLabel5.repaint();
                    jLabel5.revalidate();
                    img = rotatedImg;
                    turns--;
                    delta--;
                }

                startTime = System.nanoTime();
            }

            switchToMain(user);
        }).start();
    }

    private void switchToMain(User user) {
        Frame frame = new Frame(user);
        frame.setLocationRelativeTo(this);
        EventQueue.invokeLater(() -> frame.setVisible(true));
        Main.frameInstance = frame;
        frame.initTimer();
        Export.startIOMonitor();
        frame.loadFolder(user.getFileSystem().getRoot());
        dispose();

        if (user.showStartUpMessage()) {
            var startUpMsg = new StartUpMessage(this, true);
            startUpMsg.setVisible(true);
        }

        new WelcomeScreen();
    }

    private void signUp() {
        var username = jTextField1.getText();
        var pass1 = jPasswordField1.getText();
        var pass2 = jPasswordField2.getText();
        if (pass1.isBlank() || pass2.isBlank() || username.isBlank()) {
            return;
        }
        username = username.trim();
        pass1 = pass1.trim();
        pass2 = pass2.trim();
        if (Main.users.containsKey(username)) {
            MessageDialog.show(this, "It seems like there already is an user with that username " + Constants.SHRUG_PERSON);
            return;
        }
        if (!pass1.equals(pass2)) {
            MessageDialog.show(this, "The passwords didn't match " + Constants.FACE_PALM);
            return;
        }
        
        User user = UserFactory.createUser(username, pass2);
        
        Main.users.put(username, user);
        Main.saveUsers();
        rotateWheel(user);
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyChar() == '\n') {
            jPasswordField1.requestFocus();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed
        if (evt.getKeyCode() == '\n') {
            jPasswordField2.requestFocus();
        }
    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void jPasswordField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField2KeyPressed
        long now = System.currentTimeMillis();
        
        if (evt.getKeyChar() == '\n' && now - lastPress > MINIMUM_WAIT_TIME) {
            signUp();
            lastPress = now;
        }
    }//GEN-LAST:event_jPasswordField2KeyPressed

    private void jButton1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyPressed
        long now = System.currentTimeMillis();
        
        if (evt.getKeyCode() == VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            signUp();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton1KeyPressed

    private void jButton2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyPressed
        long now = System.currentTimeMillis();
        
        if (evt.getKeyCode() == VK_ENTER && now - lastPress > MINIMUM_WAIT_TIME) {
            back();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton2KeyPressed

    private void jButton1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseReleased
        long now = System.currentTimeMillis();
        
        if (SwingUtilities.isLeftMouseButton(evt) && now - lastPress > MINIMUM_WAIT_TIME) {
            signUp();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton1MouseReleased

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased
        long now = System.currentTimeMillis();
        
        if (SwingUtilities.isLeftMouseButton(evt) && now - lastPress > MINIMUM_WAIT_TIME) {
            back();
            lastPress = now;
        }
    }//GEN-LAST:event_jButton2MouseReleased

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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel modelbl;
    // End of variables declaration//GEN-END:variables
}
