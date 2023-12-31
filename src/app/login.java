
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Hakimfrh
 */

public class login extends javax.swing.JFrame {

    /**
     * Creates new form login
     */
    //@Override
    

    
    PreparedStatement pst;
    ResultSet rs;
        private boolean isPunyaakun(){
            boolean result = false;
            try {
                String sql = "SELECT username FROM pegawai";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement pst=conn.prepareStatement(sql);
                java.sql.ResultSet rs = pst.executeQuery(sql);
                if(rs.next()) {
                  result = true;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
            return result;
        }
        
        private void createSession(String id){
            try {
                FileWriter fileWriter = new FileWriter("src/app/session");
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                bufferedWriter.write(id);
                //bufferedWriter.newLine();

                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    public login() {
        setUndecorated(true);
        initComponents();
        //login2.setVisible(false);
        pn_rfid.setVisible(isPunyaakun());
        pn_login.setVisible(!isPunyaakun());
        btn_lupapass.setVisible(isPunyaakun());
        bck_rfid.setVisible(isPunyaakun());
        btn_daftar.setVisible(!isPunyaakun());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pn_rfid = new javax.swing.JPanel();
        ext_rfid = new javax.swing.JLabel();
        cr_lain = new javax.swing.JLabel();
        bg_card = new javax.swing.JLabel();
        txt_rfid = new javax.swing.JTextField();
        bg_rfid = new javax.swing.JLabel();
        pn_login = new javax.swing.JPanel();
        txt_username = new javax.swing.JTextField();
        txt_password = new javax.swing.JPasswordField();
        btn_masuk = new javax.swing.JLabel();
        btn_daftar = new javax.swing.JLabel();
        btn_lupapass = new javax.swing.JLabel();
        ext_login = new javax.swing.JLabel();
        bck_rfid = new javax.swing.JLabel();
        bg_login = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pn_rfid.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ext_rfid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_exit.png"))); // NOI18N
        ext_rfid.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ext_rfid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ext_rfidMouseClicked(evt);
            }
        });
        pn_rfid.add(ext_rfid, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        cr_lain.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_caraLain.png"))); // NOI18N
        cr_lain.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cr_lain.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cr_lainMouseClicked(evt);
            }
        });
        pn_rfid.add(cr_lain, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, -1, -1));

        bg_card.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/pn_rfid.png"))); // NOI18N
        pn_rfid.add(bg_card, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, -1, -1));

        txt_rfid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_rfidKeyPressed(evt);
            }
        });
        pn_rfid.add(txt_rfid, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 190, 230, 170));

        bg_rfid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/bg.png"))); // NOI18N
        pn_rfid.add(bg_rfid, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(pn_rfid, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 450));

        pn_login.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_username.setBorder(null);
        txt_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_usernameActionPerformed(evt);
            }
        });
        txt_username.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_usernameKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_usernameKeyReleased(evt);
            }
        });
        pn_login.add(txt_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 215, 220, 20));

        txt_password.setBorder(null);
        pn_login.add(txt_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 284, 220, 20));

        btn_masuk.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_masuk.png"))); // NOI18N
        btn_masuk.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_masuk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_masukMouseClicked(evt);
            }
        });
        pn_login.add(btn_masuk, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 350, -1, -1));

        btn_daftar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_daftar.png"))); // NOI18N
        btn_daftar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_daftar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_daftarMouseClicked(evt);
            }
        });
        pn_login.add(btn_daftar, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 390, -1, -1));

        btn_lupapass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_lupa.png"))); // NOI18N
        btn_lupapass.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pn_login.add(btn_lupapass, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 390, -1, -1));

        ext_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_exit.png"))); // NOI18N
        ext_login.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ext_login.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ext_loginMouseClicked(evt);
            }
        });
        pn_login.add(ext_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 10, -1, -1));

        bck_rfid.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/btn_backtorfid.png"))); // NOI18N
        bck_rfid.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        bck_rfid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bck_rfidMouseClicked(evt);
            }
        });
        pn_login.add(bck_rfid, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 390, -1, -1));

        bg_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/login/bg.png"))); // NOI18N
        pn_login.add(bg_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, -1));

        getContentPane().add(pn_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 450));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txt_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_usernameActionPerformed

    private void btn_masukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_masukMouseClicked
        // TODO add your handling code here:
        try {
            String sql = "SELECT * FROM pegawai WHERE username='"+txt_username.getText()
            +"'AND password='"+txt_password.getText()+"'";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            if(rs.next()) {
                if (rs.getString("role").equals("owner")){
                        dispose();
                        new TokoKelontong().setVisible(true);
                    } else if (rs.getString("role").equals("pegawai")){
                        dispose();
                        new Kasir().setVisible(true);
                    }
                JOptionPane.showMessageDialog(null, "Berhasil Login");
            } else {
                JOptionPane.showMessageDialog(null, "username atau password salah");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }//GEN-LAST:event_btn_masukMouseClicked

    private void btn_daftarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_daftarMouseClicked
        // TODO add your handling code here:
        new Register().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_daftarMouseClicked

    private void txt_usernameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_usernameKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_usernameKeyPressed

    private void txt_usernameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_usernameKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_usernameKeyReleased

    private void txt_rfidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_rfidKeyPressed
        // TODO add your handling code here:
        //pn_rfid.
        txt_rfid.requestFocus();
        String Barcode = this.txt_rfid.getText();
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                java.sql.Connection conn = (Connection) koneksi.configDB();
                pst = conn.prepareStatement("select * from pegawai WHERE id_pegawai =?");
                pst.setString(1, Barcode);
                rs = pst.executeQuery();

                if (rs.next()){
                    if (rs.getString("role").equals("owner")){
                        dispose();
                        new TokoKelontong().setVisible(true);
                    } else if (rs.getString("role").equals("pegawai")){
                        dispose();
                        new Kasir().setVisible(true);
                    }
                    JOptionPane.showMessageDialog(null, "Berhasil Login");
                }else{
                txt_rfid.setText("");
                JOptionPane.showMessageDialog(this, "GAGAL");
                }
            } catch (Exception e) {
            }
        }
        System.out.println(txt_rfid.getText());
    }//GEN-LAST:event_txt_rfidKeyPressed

    private void ext_rfidMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ext_rfidMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_ext_rfidMouseClicked

    private void cr_lainMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cr_lainMouseClicked
        // TODO add your handling code here:
        pn_rfid.setVisible(false);
        pn_login.setVisible(true);
    }//GEN-LAST:event_cr_lainMouseClicked

    private void ext_loginMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ext_loginMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_ext_loginMouseClicked

    private void bck_rfidMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bck_rfidMouseClicked
        // TODO add your handling code here:
        txt_rfid.setText("");
        txt_rfid.requestFocus();
        pn_rfid.setVisible(true);
        pn_login.setVisible(false);
    }//GEN-LAST:event_bck_rfidMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bck_rfid;
    private javax.swing.JLabel bg_card;
    private javax.swing.JLabel bg_login;
    private javax.swing.JLabel bg_rfid;
    private javax.swing.JLabel btn_daftar;
    private javax.swing.JLabel btn_lupapass;
    private javax.swing.JLabel btn_masuk;
    private javax.swing.JLabel cr_lain;
    private javax.swing.JLabel ext_login;
    private javax.swing.JLabel ext_rfid;
    private javax.swing.JPanel pn_login;
    private javax.swing.JPanel pn_rfid;
    private javax.swing.JPasswordField txt_password;
    private javax.swing.JTextField txt_rfid;
    private javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}
