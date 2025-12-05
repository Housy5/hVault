package vault.gui;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.SwingUtilities;
import vault.IconUtil;
import vault.Main;
import vault.NameValidator;
import vault.fsys.FilePointer;
import vault.fsys.FileSystemItem;
import vault.fsys.Folder;

public final class RenameDialog extends javax.swing.JDialog {

    private State state = State.WAITING;
    private final FileSystemItem item;
    private String newName;
    private int exitCode = CANCEL_OPTION;

    private String register;
    
    public static final int RENAME_OPTION = 1, CANCEL_OPTION = 0;

    private enum State {
        ACCEPTED, INVALID, WAITING
    };

    private boolean validateName(String name) {
        var current = Main.getCurrentFolder();
        if (item instanceof Folder) {
            return validateFolderName(name, current);
        } else if (item instanceof FilePointer pointer) {
            return validatePointerName(name, pointer);
        } else {
            throw new IllegalStateException("'item' is supposed to be an instance of either Folder or FilePointer!");
        }
    }

    private boolean isEnterKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_ENTER;
    }

    private void accept() {
        if (state == State.ACCEPTED) {
            exitCode = RENAME_OPTION;
            newName = register;
            dispose();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public RenameDialog(java.awt.Frame parent, boolean modal, FileSystemItem item) {
        super(parent, modal);
        initComponents();
        setResizable(false);
        setLocationRelativeTo(parent);
        this.item = item;
    }

    public String getNewName() {
        return newName;
    }

    public int showDialog() {
        setVisible(true);
        return exitCode;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.CardLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("RENAME");
        jPanel1.add(jLabel1, "card2");

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("New name:");
        jPanel2.add(jLabel2);

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField1.setPreferredSize(new java.awt.Dimension(150, 25));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        jPanel2.add(jTextField1);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/loading.png"))); // NOI18N
        jPanel2.add(jLabel3);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0, 5, 0));

        jButton1.setText("ACCEPT");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jButton1KeyReleased(evt);
            }
        });
        jPanel3.add(jButton1);

        jButton2.setText("CANCEL");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        jButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jButton2KeyReleased(evt);
            }
        });
        jPanel3.add(jButton2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        if (isEnterKey(evt)) {
            accept();
        } else {
            try {
                String name = jTextField1.getText();

                if (name == null || name.isBlank()) {
                    jLabel3.setIcon(IconUtil.getInstance().getIcon("/res/loading.png"));
                    state = State.WAITING;
                } else if (validateName(name)) {
                    jLabel3.setIcon(IconUtil.getInstance().getIcon("/res/check (2).png"));
                    state = State.ACCEPTED;
                } else {
                    jLabel3.setIcon(IconUtil.getInstance().getIcon("/res/cross.png"));
                    state = State.INVALID;
                }
            } catch (IOException e) {
                MessageDialog.show(Main.frameInstance, e.getMessage());
            }
        }
    }//GEN-LAST:event_jTextField1KeyReleased

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt)) {
            accept();
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        if (SwingUtilities.isLeftMouseButton(evt)) {
            exitCode = CANCEL_OPTION;
            dispose();
        }
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton1KeyReleased
        if (isEnterKey(evt)) {
            accept();
        }
    }//GEN-LAST:event_jButton1KeyReleased

    private void jButton2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton2KeyReleased
        if (isEnterKey(evt)) {
            exitCode = CANCEL_OPTION;
            dispose();
        }
    }//GEN-LAST:event_jButton2KeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

    private boolean validateFolderName(String name, Folder current) {
        register = name;
        return NameValidator.isValidFolderName(name, current);
    }

    private boolean validatePointerName(String name, FilePointer pointer) {
        var ext = pointer.getExtension();
        register = name = name + "." + ext;
        return NameValidator.isValidPointerName(name);
    }
}
