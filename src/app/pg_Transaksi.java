/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.*;

/**
 *
 * @author Hakimfrh
 */
public class pg_Transaksi extends javax.swing.JPanel {

    /**
     * Creates new form Transaksi
     */
    String kodeTransaksi = "";
    
    static void print(String kodeStruk){
        try {
            boolean hutang = kodeStruk.substring(0, 1).equals("H");
            String report = "";
            if(hutang) report = "src/app/report2.jrxml";
            else report = "src/app/report1.jrxml";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            Map<String, Object> parameter = new HashMap<String,Object>();
            parameter.put("id",kodeStruk);
            JasperReport JRpt=JasperCompileManager.compileReport(report);
            JasperPrint JPrint=JasperFillManager.fillReport(JRpt, parameter, conn);
            //JasperViewer.viewReport(JPrint,false);
            JasperPrintManager.printReport(JPrint, false);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
    private void tabletransaksi(){
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Kode Struk");
        model.addColumn("Total Barang");
        model.addColumn("Total Harga");
        model.addColumn("Tanggal / Waktu");
        try {
            String sql = "select * from transaksi WHERE kode_transaksi like '%" + txt_critrnsksi.getText()
                    +"%' OR tanggal_transaksi like '%" + txt_critrnsksi.getText() 
                    +"%' order by tanggal_transaksi desc;";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                model.addRow(new Object[]{
                    res.getString("kode_transaksi"),
                    res.getString("jumlah_barang"),
                    res.getString("total_harga"),
                    res.getString("tanggal_transaksi") +" / " +res.getString("waktu_transaksi")});
                }
            tbl_riwayat.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
    }
    
    private void displayDetail(String kode_transaksi){
        boolean hutang = kode_transaksi.substring(0, 1).equals("H");
        String tanggal="";
        String nama_penghutang="";
        String nama_pegawai="";
        int jumlah_barang=0;
        int total_harga=0;
        int bayar=0;
        int kembali=0;
        
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Nama Barang");
        model.addColumn("Quantity");
        model.addColumn("Harga Satuan");
        model.addColumn("Sub Total");
        try {
            String sql;
            if(hutang){
                sql ="select pegawai.nama, transaksi.*, detail_transaksi.*, barang.nama_barang, barang.satuan_stok, penghutang.nama as nama_penghutang " +
                        "from transaksi " +
                        "join detail_transaksi on transaksi.kode_transaksi = detail_transaksi.kode_transaksi " +
                        "join pegawai on transaksi.id_pegawai = pegawai.id_pegawai " +
                        "join barang on detail_transaksi.kode_barang = barang.kode_barang " +
                        "join penghutang on transaksi.id_penghutang = penghutang.id_penghutang " +
                        "where transaksi.kode_transaksi = '" +kode_transaksi +"';";
            }else{
                sql ="select pegawai.nama, transaksi.*, detail_transaksi.*, barang.nama_barang, barang.satuan_stok " +
                        "from transaksi " +
                        "join detail_transaksi on transaksi.kode_transaksi = detail_transaksi.kode_transaksi " +
                        "join pegawai on transaksi.id_pegawai = pegawai.id_pegawai " +
                        "join barang on detail_transaksi.kode_barang = barang.kode_barang " +
                        "where transaksi.kode_transaksi = '" +kode_transaksi +"';";
            }
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet res=stm.executeQuery(sql);
            while (res.next()){
                jumlah_barang = res.getInt("jumlah_barang");
                total_harga = res.getInt("total_harga");
                bayar = res.getInt("tunai_bayar");
                kembali = res.getInt("tunai_kembali");
                tanggal = res.getString("tanggal_transaksi") +" / " +res.getString("waktu_transaksi");
                nama_pegawai = res.getString("nama");
                if(hutang) nama_penghutang = res.getString("nama_penghutang");
                model.addRow(new Object[]{
                    res.getString("nama_barang"),
                    res.getString("quantity")+" " +res.getString("satuan_stok"),
                    res.getString("harga_satuan"),
                    res.getString("subtotal")});
                }
            tbl_detail.setModel(model);
            }catch (Exception e) {             
                System.out.println(e);
        }
        
        lbl_totalHarga.setText("Rp. " +Integer.toString(total_harga));
        lbl_jumlahBarang.setText(Integer.toString(jumlah_barang));
        lbl_bayar.setText("Rp. " +Integer.toString(bayar));
        txt_kodeStruk.setText(kode_transaksi);
        lbl_tanggal.setText(tanggal);
        lbl_kembali.setText("Rp. " +kembali);
        lbl_kasir.setText(nama_pegawai);
        lbl_hutang.setText(hutang?nama_penghutang:"-");
        pn_detailTransaksi.setVisible(true);
        pn_riwayatTransaksi.setVisible(false);
        
        btn_riwayatTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_riwayattrx_off.png")));
        btn_detailTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_detailtrx_on.png")));
    }
    
    public pg_Transaksi() {
        initComponents();
        tabletransaksi();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pn_riwayatTransaksi = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_riwayat = new javax.swing.JTable();
        txt_critrnsksi = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pn_detailTransaksi = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_detail = new javax.swing.JTable();
        txt_kodeStruk = new javax.swing.JTextField();
        lbl_kasir = new javax.swing.JLabel();
        lbl_hutang = new javax.swing.JLabel();
        lbl_tanggal = new javax.swing.JLabel();
        lbl_jumlahBarang = new javax.swing.JLabel();
        lbl_totalHarga = new javax.swing.JLabel();
        lbl_bayar = new javax.swing.JLabel();
        lbl_kembali = new javax.swing.JLabel();
        btn_cari = new javax.swing.JLabel();
        btn_cetak = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        btn_detailTransaksi = new javax.swing.JLabel();
        btn_riwayatTransaksi = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pn_riwayatTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        pn_riwayatTransaksi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_riwayat.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_riwayat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_riwayatMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl_riwayat);

        pn_riwayatTransaksi.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, 980, 490));

        txt_critrnsksi.setBorder(null);
        txt_critrnsksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_critrnsksiKeyPressed(evt);
            }
        });
        pn_riwayatTransaksi.add(txt_critrnsksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(779, 108, 260, 20));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Transaksi/riwayat transaksi/bg.png"))); // NOI18N
        pn_riwayatTransaksi.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_riwayatTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1105, 694));

        pn_detailTransaksi.setBackground(new java.awt.Color(255, 255, 255));
        pn_detailTransaksi.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_detail.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_detail.setEnabled(false);
        jScrollPane2.setViewportView(tbl_detail);

        pn_detailTransaksi.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 363, 980, 280));

        txt_kodeStruk.setBorder(null);
        txt_kodeStruk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_kodeStrukActionPerformed(evt);
            }
        });
        pn_detailTransaksi.add(txt_kodeStruk, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 137, 250, 20));

        lbl_kasir.setText("XXXXXXXX");
        pn_detailTransaksi.add(lbl_kasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 200, 120, 20));

        lbl_hutang.setText("Rp. XX XXX");
        pn_detailTransaksi.add(lbl_hutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 264, 180, 20));

        lbl_tanggal.setText("XX - XXXXX - XXXX");
        pn_detailTransaksi.add(lbl_tanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 196, 180, 20));

        lbl_jumlahBarang.setText("XX");
        pn_detailTransaksi.add(lbl_jumlahBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 230, 180, 20));

        lbl_totalHarga.setText("Rp. XX XXX");
        pn_detailTransaksi.add(lbl_totalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 264, 180, 20));

        lbl_bayar.setText("Rp. XXXXXX");
        pn_detailTransaksi.add(lbl_bayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 196, 180, 20));

        lbl_kembali.setText("Rp. XXXXXX");
        pn_detailTransaksi.add(lbl_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 230, 180, 20));

        btn_cari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Transaksi/detail transaski/btn_cari.png"))); // NOI18N
        btn_cari.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_cariMouseClicked(evt);
            }
        });
        pn_detailTransaksi.add(btn_cari, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 134, -1, -1));

        btn_cetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Transaksi/detail transaski/btn_cetakstruk.png"))); // NOI18N
        btn_cetak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_cetakMouseClicked(evt);
            }
        });
        pn_detailTransaksi.add(btn_cetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 230, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Transaksi/detail transaski/bg.png"))); // NOI18N
        pn_detailTransaksi.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_detailTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, 1105, 694));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_detailTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_detailtrx_off.png"))); // NOI18N
        btn_detailTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_detailTransaksiMouseClicked(evt);
            }
        });
        jPanel1.add(btn_detailTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 180, -1, -1));

        btn_riwayatTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_riwayattrx_on.png"))); // NOI18N
        btn_riwayatTransaksi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_riwayatTransaksiMouseClicked(evt);
            }
        });
        jPanel1.add(btn_riwayatTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/bg.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 694));
    }// </editor-fold>//GEN-END:initComponents

    private void btn_riwayatTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_riwayatTransaksiMouseClicked
        // TODO add your handling code here:
        pn_riwayatTransaksi.setVisible(true);
        pn_detailTransaksi.setVisible(false);
        
        btn_riwayatTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_riwayattrx_on.png")));
        btn_detailTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_detailtrx_off.png")));
    }//GEN-LAST:event_btn_riwayatTransaksiMouseClicked

    private void btn_detailTransaksiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_detailTransaksiMouseClicked
        // TODO add your handling code here:
        pn_riwayatTransaksi.setVisible(false);
        pn_detailTransaksi.setVisible(true);
        
        btn_riwayatTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_riwayattrx_off.png")));
        btn_detailTransaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_transaksi/btn_detailtrx_on.png")));
    }//GEN-LAST:event_btn_detailTransaksiMouseClicked

    private void btn_cariMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cariMouseClicked
        // TODO add your handling code here:
        displayDetail(txt_kodeStruk.getText());
        kodeTransaksi=txt_kodeStruk.getText();
    }//GEN-LAST:event_btn_cariMouseClicked

    private void tbl_riwayatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_riwayatMouseClicked
        // TODO add your handling code here:
        displayDetail(tbl_riwayat.getValueAt(tbl_riwayat.rowAtPoint(evt.getPoint()), 0).toString());
    }//GEN-LAST:event_tbl_riwayatMouseClicked

    private void txt_kodeStrukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_kodeStrukActionPerformed
        // TODO add your handling code here:
        displayDetail(txt_kodeStruk.getText());
    }//GEN-LAST:event_txt_kodeStrukActionPerformed

    private void btn_cetakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_cetakMouseClicked
        // TODO add your handling code here:
        
        print(txt_kodeStruk.getText());
        
    }//GEN-LAST:event_btn_cetakMouseClicked

    private void txt_critrnsksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_critrnsksiKeyPressed
        // TODO add your handling code here:
        tabletransaksi();
    }//GEN-LAST:event_txt_critrnsksiKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel btn_cari;
    private javax.swing.JLabel btn_cetak;
    private javax.swing.JLabel btn_detailTransaksi;
    private javax.swing.JLabel btn_riwayatTransaksi;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_bayar;
    private javax.swing.JLabel lbl_hutang;
    private javax.swing.JLabel lbl_jumlahBarang;
    private javax.swing.JLabel lbl_kasir;
    private javax.swing.JLabel lbl_kembali;
    private javax.swing.JLabel lbl_tanggal;
    private javax.swing.JLabel lbl_totalHarga;
    private javax.swing.JPanel pn_detailTransaksi;
    private javax.swing.JPanel pn_riwayatTransaksi;
    private javax.swing.JTable tbl_detail;
    private javax.swing.JTable tbl_riwayat;
    private javax.swing.JTextField txt_critrnsksi;
    private javax.swing.JTextField txt_kodeStruk;
    // End of variables declaration//GEN-END:variables
}
