/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.Timer;
import javax.swing.RowSorter;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTable;
import javax.swing.table.TableRowSorter;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class pg_Utang extends javax.swing.JPanel {
   
    public pg_Utang() {
        initComponents();
        Tampil_Tanggal();
        tbl_penghutangg();
        tbl_piutang();
        tbl_pembayaran();
        tbl_tenggat();
        penghutang();
        total_hutang();
        setmonthnow();
        setyearnow();
        total_utang();
        riwayat.setVisible(false);
    }

    
    
    public void Tampil_Tanggal() {
        java.util.Date tglsekarang = new java.util.Date();
        SimpleDateFormat smpdtfmt = new SimpleDateFormat("dd MMMMMMMMM yyyy", Locale.getDefault());
        String tanggal = smpdtfmt.format(tglsekarang);
        lbl_tanggal.setText(tanggal);
    }
    
    public void tbl_penghutangg(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NIK");
        model.addColumn("Nama");
        model.addColumn("Sisa Utang");
        model.addColumn("Tenggat Utang");
        try {
            String sql ="Select * From penghutang WHERE status='HUTANG' OR status='TELAT';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("sisa_hutang"),
                    res.getString("tenggat_hutang"),
                });
                }
            tbl_penghutangg.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    public void tbl_tenggat(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NIK");
        model.addColumn("Nama");
        model.addColumn("Sisa Utang");
        model.addColumn("Tenggat Utang");
        try {
            String sql ="Select * From penghutang WHERE status='TELAT';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("sisa_hutang"),
                    res.getString("tenggat_hutang")
                });
                }
            tbl_tenggatt.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    
 
    public void penghutang(){
        try {
            String sql ="SELECT COUNT(id_penghutang) FROM penghutang WHERE status = 'HUTANG' OR status = 'TELAT';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            res.next();
                lbl_penghutang.setText(res.getString(1));
                
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    public void total_hutang(){
        try {
            String sql ="SELECT SUM(sisa_hutang) FROM penghutang WHERE status = 'HUTANG' OR status = 'TELAT';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            res.next();
                lbl_total_hutang.setText("Rp. "+res.getString(1));
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    public void tbl_piutang(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id Penghutang");
        model.addColumn("Nama Penghutang");
        model.addColumn("Tanggal Utang");
        model.addColumn("Jumlah Utang");
        try {
            String sql ="Select * From hutang_keluar RIGHT JOIN penghutang ON penghutang.id_penghutang = hutang_keluar.id_penghutang;";
            //String sql ="Select * From hutang_keluar;";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("tanggal"),
                    res.getString("jumlah_hutang")
                });
                }
            tbl_piutang.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    public void tbl_pembayaran(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Id Penghutang");
        model.addColumn("Nama Penghutang");
        model.addColumn("Tanggal Utang");
        model.addColumn("Jumlah Bayar");
        try {
            String sql ="Select * From hutang_masuk RIGHT JOIN penghutang ON penghutang.id_penghutang = hutang_masuk.id_penghutang;";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("tanggal"),
                    res.getString("jumlah_bayar")
                });
                }
            tbl_pembayaran.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    Date tgl = new Date();
    SimpleDateFormat hari = new SimpleDateFormat("dd");
    SimpleDateFormat bulan = new SimpleDateFormat("MM");
    SimpleDateFormat tahun = new SimpleDateFormat("yyyy");
    String day = hari.format(tgl), monthnow = bulan.format(tgl), yearnow = tahun.format(tgl);
    String month = monthnow, year = yearnow;
    
    private void setmonthnow(){
        switch (monthnow) {
            case "01":
                cboxbulan.setSelectedItem("Januari");
                break;
            case "02":
                cboxbulan.setSelectedItem("Februari");
                break;
            case "03":
                cboxbulan.setSelectedItem("Maret");
                break;
            case "04":
                cboxbulan.setSelectedItem("April");
                break;
            case "05":
                cboxbulan.setSelectedItem("Mei");
                break;
            case "06":
                cboxbulan.setSelectedItem("Juni");
                break;
            case "07":
                cboxbulan.setSelectedItem("Juli");
                break;
            case "08":
                cboxbulan.setSelectedItem("Agutus");
                break;
            case "09":
                cboxbulan.setSelectedItem("September");
                break;
            case "10":
                cboxbulan.setSelectedItem("Oktober");
                break;
            case "11":
                cboxbulan.setSelectedItem("November");
                break;
            case "12":
                cboxbulan.setSelectedItem("Desember");
                break;
            default:
                break;
        }
    }
    private void selectedMonth(){
        if (cboxbulan.getSelectedItem().equals("Januari")){ month = "01";}
        else if (cboxbulan.getSelectedItem().equals("Februari")){ month = "02";}
        else if (cboxbulan.getSelectedItem().equals("Maret")){ month = "03";}
        else if (cboxbulan.getSelectedItem().equals("April")){ month = "04";}
        else if (cboxbulan.getSelectedItem().equals("Mei")){ month = "05";}
        else if (cboxbulan.getSelectedItem().equals("Juni")){ month = "06";}
        else if (cboxbulan.getSelectedItem().equals("Juli")){ month = "07";}
        else if (cboxbulan.getSelectedItem().equals("Agustus")){ month = "08";}
        else if (cboxbulan.getSelectedItem().equals("September")){ month = "09";}
        else if (cboxbulan.getSelectedItem().equals("Oktober")){ month = "10";}
        else if (cboxbulan.getSelectedItem().equals("November")){ month = "11";}
        else if (cboxbulan.getSelectedItem().equals("Desember")){ month = "12";}
    }
    
    private void setyearnow(){
        switch(yearnow){
            case "2022":
                cboxtahun.setSelectedItem("2022");
                break;
            case "2023":
                cboxtahun.setSelectedItem("2023");
                break;
            default:
                break;
        }
    }
    
    public void total_utang(){
        try{
            String sqlbulan = "select sum(sisa_hutang), month(tenggat_hutang) m, year(tenggat_hutang) y from penghutang group by m, y"
                    + " having m = "+month+" and y = "+ year;
            
            java.sql.Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet rst = stm.executeQuery(sqlbulan);
            if (rst.next()){
                lbl_totalutang.setText("Rp. "+rst.getString(1));         
            } else {
                lbl_totalutang.setText("Rp. 0");
            }
        }catch(SQLException e){
            
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sub_utang = new javax.swing.JPanel();
        btn_riwayat = new javax.swing.JLabel();
        btn_ringkasan = new javax.swing.JLabel();
        bg = new javax.swing.JLabel();
        ringkasan = new javax.swing.JPanel();
        jScrollPan = new javax.swing.JScrollPane();
        tbl_tenggatt = new javax.swing.JTable();
        lbl_totalutang = new javax.swing.JLabel();
        refersh = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btn_batal = new javax.swing.JLabel();
        btn_hapus = new javax.swing.JLabel();
        btn_simpan = new javax.swing.JLabel();
        cboxbulan = new javax.swing.JComboBox<>();
        cboxtahun = new javax.swing.JComboBox<>();
        lbl_utang = new javax.swing.JLabel();
        txt_telp = new javax.swing.JTextField();
        lbl_nama = new javax.swing.JLabel();
        jdate = new com.toedter.calendar.JDateChooser();
        lbl_total_hutang = new javax.swing.JLabel();
        lbl_penghutang = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_penghutangg = new javax.swing.JTable();
        urutkan = new javax.swing.JLabel();
        lbl_tanggal = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        riwayat = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_pembayaran = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_piutang = new javax.swing.JTable();
        background1 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sub_utang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_riwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_riwayatutang_off.png"))); // NOI18N
        btn_riwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_riwayatMouseClicked(evt);
            }
        });
        sub_utang.add(btn_riwayat, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, -1, -1));

        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_ringkasan_on.png"))); // NOI18N
        btn_ringkasan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_ringkasanMouseClicked(evt);
            }
        });
        sub_utang.add(btn_ringkasan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, -1, -1));

        bg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/bg.png"))); // NOI18N
        bg.setMaximumSize(new java.awt.Dimension(1000, 1000));
        sub_utang.add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(sub_utang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 690));

        ringkasan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_tenggatt.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NIK", "Nama", "Sisa Utang", "Tenggat Utang"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPan.setViewportView(tbl_tenggatt);

        ringkasan.add(jScrollPan, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 438, 354, 120));

        lbl_totalutang.setFont(new java.awt.Font("Century Gothic", 0, 35)); // NOI18N
        ringkasan.add(lbl_totalutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(47, 325, 220, 40));

        refersh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/Ringkasan/btn_refresh.png"))); // NOI18N
        refersh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        refersh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refershMouseClicked(evt);
            }
        });
        ringkasan.add(refersh, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 90, -1, -1));

        jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        jLabel2.setText("Tenggat Utang");
        ringkasan.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(965, 90, -1, -1));

        btn_batal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/Ringkasan/btn_batal_on.png"))); // NOI18N
        btn_batal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_batal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_batalMouseClicked(evt);
            }
        });
        ringkasan.add(btn_batal, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 620, -1, -1));

        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/Ringkasan/btn_hapus_on.png"))); // NOI18N
        btn_hapus.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_hapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_hapusMouseClicked(evt);
            }
        });
        ringkasan.add(btn_hapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 620, -1, -1));

        btn_simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/Ringkasan/btn_simpan_on.png"))); // NOI18N
        btn_simpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_simpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_simpanMouseClicked(evt);
            }
        });
        ringkasan.add(btn_simpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 620, -1, -1));

        cboxbulan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli ", "Agustus", "September", "Oktober", "November", "Desember" }));
        cboxbulan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cboxbulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxbulanActionPerformed(evt);
            }
        });
        ringkasan.add(cboxbulan, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 280, 120, 35));

        cboxtahun.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", " " }));
        cboxtahun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cboxtahun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboxtahunActionPerformed(evt);
            }
        });
        ringkasan.add(cboxtahun, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 280, 80, 35));

        lbl_utang.setFont(new java.awt.Font("Century Gothic", 0, 22)); // NOI18N
        ringkasan.add(lbl_utang, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 513, 190, 22));

        txt_telp.setBorder(null);
        ringkasan.add(txt_telp, new org.netbeans.lib.awtextra.AbsoluteConstraints(696, 575, 190, 19));

        lbl_nama.setFont(new java.awt.Font("Century Gothic", 0, 30)); // NOI18N
        ringkasan.add(lbl_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(613, 477, 200, 30));
        ringkasan.add(jdate, new org.netbeans.lib.awtextra.AbsoluteConstraints(692, 540, 190, -1));

        lbl_total_hutang.setFont(new java.awt.Font("Century Gothic", 0, 45)); // NOI18N
        lbl_total_hutang.setForeground(new java.awt.Color(255, 255, 255));
        ringkasan.add(lbl_total_hutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, 210, 60));

        lbl_penghutang.setBackground(new java.awt.Color(255, 255, 255));
        lbl_penghutang.setFont(new java.awt.Font("Century Gothic", 0, 45)); // NOI18N
        lbl_penghutang.setForeground(new java.awt.Color(255, 255, 255));
        ringkasan.add(lbl_penghutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 140, 120, 50));

        tbl_penghutangg.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "NIK", "Nama", "Sisa Utang", "Tenggat Utang"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_penghutangg.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tbl_penghutangg.setGridColor(new java.awt.Color(250, 250, 250));
        tbl_penghutangg.setRowHeight(20);
        tbl_penghutangg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_penghutanggMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_penghutangg);

        ringkasan.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(599, 124, -1, 303));

        urutkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/Ringkasan/btn_flipsort.png"))); // NOI18N
        urutkan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        urutkan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                urutkanMouseClicked(evt);
            }
        });
        ringkasan.add(urutkan, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 80, -1, -1));

        lbl_tanggal.setFont(new java.awt.Font("Century Gothic", 1, 23)); // NOI18N
        ringkasan.add(lbl_tanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 310, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/Ringkasan/bg.png"))); // NOI18N
        ringkasan.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1130, 690));

        add(ringkasan, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1110, 690));

        riwayat.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_pembayaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tbl_pembayaran);

        riwayat.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 426, 1010, 203));

        tbl_piutang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Id Penghutang", "Tanggal Utang", "Jumlah Utang"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbl_piutang);

        riwayat.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 138, 1010, 203));

        background1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Utang/riwayat utang/bg.png"))); // NOI18N
        riwayat.add(background1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(riwayat, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1110, 690));
    }// </editor-fold>//GEN-END:initComponents

    private void btn_riwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_riwayatMouseClicked
        sub_utang.setVisible(true);
        riwayat.setVisible(true);
        ringkasan.setVisible(false);
        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_ringkasan_off.png")));
        btn_riwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_riwayatutang_on.png")));
    }//GEN-LAST:event_btn_riwayatMouseClicked

    private void btn_ringkasanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_ringkasanMouseClicked
        sub_utang.setVisible(true);
        riwayat.setVisible(false);
        ringkasan.setVisible(true);
        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_ringkasan_on.png")));
        btn_riwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_utang/btn_riwayatutang_off.png")));
    }//GEN-LAST:event_btn_ringkasanMouseClicked

    private void urutkanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_urutkanMouseClicked
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("NIK");
        model.addColumn("Nama");
        model.addColumn("Sisa Utang");
        model.addColumn("Tenggat Utang");
        try {
            String sql ="SELECT * FROM penghutang WHERE status = 'HUTANG' OR status = 'TELAT' ORDER BY ABS(DATEDIFF(tenggat_hutang, CURDATE())) ASC;";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("sisa_hutang"),
                    res.getString("tenggat_hutang")
                });
                }
            tbl_penghutangg.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }//GEN-LAST:event_urutkanMouseClicked

    private void tbl_penghutanggMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_penghutanggMouseClicked
        DefaultTableModel model = (DefaultTableModel)tbl_penghutangg.getModel();
        int selectedRow = tbl_penghutangg.getSelectedRow();
                 
        lbl_nama.setText(model.getValueAt(selectedRow,1).toString());
        lbl_utang.setText("Rp. "+model.getValueAt(selectedRow,2).toString());
        try
            {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String)model.getValueAt(selectedRow, 3).toString());
                
                String id = model.getValueAt(selectedRow,0).toString();
                String sql="select * from penghutang where id_penghutang ='"+id +"';";
                java.sql.Connection conn=(Connection)koneksi.configDB();
                java.sql.Statement stm=conn.createStatement();
                java.sql.ResultSet rs=stm.executeQuery(sql);
            
                rs.next();
                jdate.setDate(date);
                txt_telp.setText(rs.getString("telepon")); 
            }catch(Exception e)
            {
                JOptionPane.showMessageDialog(null,"GAGAL");
            }
    }//GEN-LAST:event_tbl_penghutanggMouseClicked


    private void btn_simpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_simpanMouseClicked
        int selectedRow = tbl_penghutangg.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel)tbl_penghutangg.getModel();
        String date = "yyyy-MM-dd";
        SimpleDateFormat fm = new SimpleDateFormat(date);
        String tanggal = String.valueOf(fm.format(jdate.getDate()));
        try {
            String id = model.getValueAt(selectedRow,0).toString();
            
            String sql = "UPDATE penghutang  SET telepon = '" +txt_telp.getText()+"', tenggat_hutang = '" +tanggal+"' WHERE id_penghutang='"+id +"';";
            java.sql.Connection conn=(Connection) koneksi.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            pst.execute(); 
            JOptionPane.showMessageDialog(this,"Data Berhasil Di Update");
            
            
        } catch(Exception e)
            {
                JOptionPane.showMessageDialog(null,"GAGAL");
            }
    }//GEN-LAST:event_btn_simpanMouseClicked

    private void btn_hapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_hapusMouseClicked
        int selectedRow = tbl_penghutangg.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel)tbl_penghutangg.getModel();
        try {
            String id = model.getValueAt(selectedRow,0).toString();
            String sql = "DELETE FROM penghutang WHERE id_penghutang='"+id +"';";
            java.sql.Connection conn=(Connection) koneksi.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            pst.execute(); 
            JOptionPane.showMessageDialog(this,"Data Berhasil Di Hapus");
        } catch(Exception e)
            {
                JOptionPane.showMessageDialog(null,"GAGAL\n"+e);
            }
    }//GEN-LAST:event_btn_hapusMouseClicked

    private void btn_batalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_batalMouseClicked
        lbl_nama.setText("");
        lbl_utang.setText("");
        jdate.setDate(null);
        txt_telp.setText("");
    }//GEN-LAST:event_btn_batalMouseClicked

    private void refershMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refershMouseClicked
        DefaultTableModel model = (DefaultTableModel)tbl_penghutangg.getModel();
        model.setRowCount(0);
        tbl_penghutangg();
        tbl_tenggat();
        penghutang();
        total_hutang();
        total_utang();
    }//GEN-LAST:event_refershMouseClicked

    private void cboxbulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxbulanActionPerformed
        selectedMonth();
        total_utang();
    }//GEN-LAST:event_cboxbulanActionPerformed

    private void cboxtahunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboxtahunActionPerformed
        year = (String) cboxtahun.getSelectedItem();
        total_utang();
    }//GEN-LAST:event_cboxtahunActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background1;
    private javax.swing.JLabel bg;
    private javax.swing.JLabel btn_batal;
    private javax.swing.JLabel btn_hapus;
    private javax.swing.JLabel btn_ringkasan;
    private javax.swing.JLabel btn_riwayat;
    private javax.swing.JLabel btn_simpan;
    private javax.swing.JComboBox<String> cboxbulan;
    private javax.swing.JComboBox<String> cboxtahun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private com.toedter.calendar.JDateChooser jdate;
    private javax.swing.JLabel lbl_nama;
    private javax.swing.JLabel lbl_penghutang;
    private javax.swing.JLabel lbl_tanggal;
    private javax.swing.JLabel lbl_total_hutang;
    private javax.swing.JLabel lbl_totalutang;
    private javax.swing.JLabel lbl_utang;
    private javax.swing.JLabel refersh;
    private javax.swing.JPanel ringkasan;
    private javax.swing.JPanel riwayat;
    private javax.swing.JPanel sub_utang;
    private javax.swing.JTable tbl_pembayaran;
    private javax.swing.JTable tbl_penghutangg;
    private javax.swing.JTable tbl_piutang;
    private javax.swing.JTable tbl_tenggatt;
    private javax.swing.JTextField txt_telp;
    private javax.swing.JLabel urutkan;
    // End of variables declaration//GEN-END:variables
}
