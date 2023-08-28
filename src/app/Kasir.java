/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Hakimfrh
 */
public class Kasir extends javax.swing.JFrame {

    /**
     * Creates new form Kasir
     */
    
    ArrayList<String> penghutang = new ArrayList<>();
    
    String kodeBarang="";
    String namaBarang="";
    boolean isTenggat=false;
    boolean selesai=false;
    boolean enable = false;
    int harga=0;
    int sisaStok=0;
    int baris=-1;
    
    int kembali=0;
    int bayar=0;
    int hutangbaru=0;
    int sisahutang=0;
    int limitHutang=0;
    int totalHutang=0;
    
    String nik;
    
    
    
    public Kasir() {
        setUndecorated(true);
        initComponents();
        loadHutang();
        Session session = new Session();
        pn_bayarUtang.setVisible(false);
        pn_kasir.setVisible(true);
        
        lbl_kasir.setText(session.getName());
        pn_kasirHutang.setVisible(false);
        lbl_stokTidakcukup.setVisible(false);
        lbl_errorHutang.setVisible(false);
        lbl_errorHutang1.setVisible(false);
        lbl_errorHutang2.setVisible(false);
        btn_kembali.setVisible(session.isOwner());        
    }
    static void print(String kodeStruk){
        try {
                boolean hutang = kodeStruk.substring(0, 1).equals("H");
                String report;
                if(hutang) report = "src/app/report2.jrxml";
                else report = "src/app/report1.jrxml";
                java.sql.Connection conn=(Connection)koneksi.configDB();
                Map<String, Object> parameter = new HashMap<String,Object>();
                parameter.put("id",kodeStruk);
                JasperReport JRpt=JasperCompileManager.compileReport(report);
                JasperPrint JPrint=JasperFillManager.fillReport(JRpt, parameter, conn);
                JasperViewer.viewReport(JPrint,false);
                //JasperPrintManager.printReport(JPrint, false);
            }catch (Exception e){
                System.out.println(e);
            }
    }
    void cekSelesai(){
        enable = false;
        boolean hutang=btn_hutang.isSelected();
        boolean newHutang= jComboBox1.getSelectedIndex()==0;
        if((!hutang) && ((jTable1.getRowCount()>0) && (Integer.valueOf(txt_tunaiBayar.getText()))>=hitungTotal())) enable = true;
        if(hutang && newHutang){
            if(!txt_nama.getText().equals("") && !txt_telp.getText().equals("") && !txt_nik.getText().equals("")) enable = true;
            
            lbl_errorHutang.setVisible(false);
            lbl_errorHutang1.setVisible(false);
            lbl_errorHutang2.setVisible(false);
        }
        if(hutang && !newHutang){
            boolean islimit = totalHutang>limitHutang;
            boolean invalid = isTenggat || islimit;
            lbl_errorHutang.setVisible(invalid);
            short textLength = 0;
            if (isTenggat) textLength++;
            if (islimit) textLength++;
            if (invalid && !newHutang){
                if (textLength > 1){
                    lbl_errorHutang1.setText(" - Melebihi tenggat hutang");
                    lbl_errorHutang2.setText(" - Melebihi limit hutang");
                    lbl_errorHutang1.setVisible(true);
                    lbl_errorHutang2.setVisible(true);
                }else{
                    lbl_errorHutang1.setText(isTenggat?" - Melebihi tenggat hutang":" - Melebihi limit hutang");
                    lbl_errorHutang1.setVisible(true);
                    lbl_errorHutang2.setVisible(false);
                }
            }else{
                lbl_errorHutang1.setVisible(false);
                lbl_errorHutang2.setVisible(false);
                enable=true;
            }
            System.out.println(enable);
        }
        
        if(enable) btn_selesai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_selesai_on.png")));
        else btn_selesai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_selesai_off.png")));
    }
    
    static String generateKode(boolean ishutang){
         java.util.Date date = new java.util.Date();
         SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
         SimpleDateFormat m = new SimpleDateFormat("MM");
         SimpleDateFormat t = new SimpleDateFormat("dd");
         SimpleDateFormat y = new SimpleDateFormat("YY");
         int month=Integer.parseInt(m.format(date));
         int transaksi=0;
         try{
            
            String sql = "select count(kode_transaksi) from transaksi where tanggal_transaksi='"+f.format(date)+"';";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement stm=conn.prepareStatement(sql);
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
            transaksi=Integer.parseInt(res.getString(1));
            }
        }catch (Exception e){
             System.out.println(e);
        }
         
         String a;
         String b= "";
         String c= t.format(date);
         String d= y.format(date);
         int    e= transaksi;
         
         if(month==1){ b="J";}//januari
         if(month==2){ b="F";}//februari
         if(month==3){ b="M";}//maret
         if(month==4){ b="A";}//april
         if(month==5){ b="M";}//mei
         if(month==6){ b="U";}//juni
         if(month==7){ b="L";}//juli
         if(month==8){ b="G";}//agustus
         if(month==9){ b="S";}//september
         if(month==10){ b="O";}//oktokber
         if(month==11){ b="N";}//november
         if(month==12){ b="D";}//desember
         
         a = ishutang?"H":"T";
        String struk= String.format("%s%s%s%s%04d",a,b,c,d,e);
   
         return struk.toUpperCase();
   }
    
    void loadTableHutang(){
        try{
        DefaultTableModel model2 = (DefaultTableModel) jTable2.getModel();
        model2.setRowCount(0);
            String sql = "select * from penghutang WHERE status != 'LUNAS' order by nama asc;";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement stm=conn.prepareStatement(sql);
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                Object[] newRowData = {
                    res.getString("id_penghutang"),
                    res.getString("nama"),
                    res.getString("telepon")};
                model2.addRow(newRowData);
                int rowIndex = model2.getRowCount() - 1;
                model2.fireTableRowsInserted(rowIndex, rowIndex);
            }
            
        }catch (Exception e){
             System.out.println(e);
        }
    }
    
    void loadHutang(){
         try{
            String sql = "select * from penghutang order by nama asc;";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement stm=conn.prepareStatement(sql);
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                penghutang.add(res.getString("id_penghutang"));
                jComboBox1.addItem(res.getString("nama"));
            }
            
            String sql1 = "select limit_hutang from toko;";
            java.sql.PreparedStatement stm1=conn.prepareStatement(sql1);
            java.sql.ResultSet res1 = stm1.executeQuery(sql1);
            while (res1.next()){
                limitHutang=res1.getInt("limit_hutang");
            }
            
        }catch (Exception e){
             System.out.println(e);
        }
         System.out.println(limitHutang);
         
    }
    
    void addItem(String kode, String nama, int jumlah, int harga){
      if(sisaStok < Integer.valueOf(txt_jumlah.getText())){
        JOptionPane.showMessageDialog(this, "Silahkan cek kembali stok yang ada");
      }else{
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int subTotal=jumlah*harga;
        boolean newItem = true;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            if (jTable1.getValueAt(i, 0) != null && jTable1.getValueAt(i, 0).toString().contains(kode)){
                newItem=false;
                int newJumlah =  Integer.valueOf(jTable1.getValueAt(i, 2).toString()) + jumlah;
                if(sisaStok < newJumlah){
                    JOptionPane.showMessageDialog(this, "Silahkan cek kembali stok yang ada");
                }else{
                    subTotal=newJumlah*harga;
                    model.setValueAt(newJumlah, i, 2);
                    model.setValueAt(subTotal, i, 4);
                }
            }
        }
        if(newItem){
            Object[] newRowData = {kode,nama,jumlah,harga,subTotal};
            model.addRow(newRowData);
            int rowIndex = model.getRowCount() - 1;
            model.fireTableRowsInserted(rowIndex, rowIndex);
        }
      }
        refresh();
    }
    void refresh(){
        lbl_grandTotal.setText("Rp. " +hitungTotal());
        bayar=Integer.valueOf(txt_hutangBayar.getText());
        hutangbaru = hitungTotal()-bayar;
        totalHutang = sisahutang+hutangbaru;
        lbl_totalHutang.setText("Rp. " +totalHutang);
        txt_kodeBarang.setText("");
        cekSelesai();
    }
    int hitungTotal(){
        int grandTotal = 0;
        for (int i = 0; i < jTable1.getRowCount(); i++) {
            grandTotal += Integer.valueOf(jTable1.getValueAt(i, 4).toString());
        }
        return grandTotal;
    }
    
    void clearBayarhutang(){
        nik = "";
        totalHutang = 0;
        sisahutang = 0;
        bayar = 0;
        
        lbl_namaHutang.setText("");
        lbl_hutang.setText("Rp. -");
        lbl_sisaHutang.setText("Rp. -");
        txt_nikHutang.setText("");
        txt_bayarHutang.setText("0");
    }
    
    void clearEdit(){
        lbl_kodeBarang.setText("");
        lbl_harga.setText("");
        lbl_subTotal.setText("");
        lbl_namaBarang.setText("");
        txt_editJumlah.setText("");
        
        bg_txtJumlah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_jumlah_off.png")));
        btn_ubah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_ubah_off.png")));
        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_hapus_off.png")));
        
        baris = -1;
        txt_kodeBarang.requestFocus();
        txt_editJumlah.setFocusable(false);
        txt_editJumlah.setBackground(new java.awt.Color(247, 255, 255));
    }
    
    void clearAll(){
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        kodeBarang="";
        namaBarang="";
        harga=0;
        sisaStok=0;
        kembali=0;
        bayar=0;
        sisahutang=0;
        totalHutang=0;
        
        lbl_grandTotal.setText("Rp. 0");
        lbl_totalHutang.setText("Rp. 0");
        txt_tunaiBayar.setText("0");
        txt_hutangBayar.setText("0");
        pn_kasirHutang.setVisible(false);
        penghutang.clear();
        int startIndex = 1;
        int itemCount = jComboBox1.getItemCount();
        for (int i = startIndex; i < itemCount; i++) {
            jComboBox1.removeItemAt(startIndex);
        }
        jComboBox1.setSelectedIndex(0);
        btn_tunai.setSelected(true);
        btn_hutang.setSelected(false);
        
        loadHutang();
        clearEdit();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        btn_kembali = new javax.swing.JLabel();
        btn_exit = new javax.swing.JLabel();
        btn_minimize = new javax.swing.JLabel();
        btn_bayarHutang = new javax.swing.JLabel();
        btn_kasir = new javax.swing.JLabel();
        bg_menu = new javax.swing.JLabel();
        pn_kasir = new javax.swing.JPanel();
        lbl_namaBarang = new javax.swing.JLabel();
        lbl_kodeBarang = new javax.swing.JLabel();
        lbl_harga = new javax.swing.JLabel();
        lbl_subTotal = new javax.swing.JLabel();
        txt_editJumlah = new javax.swing.JTextField();
        txt_kodeBarang = new javax.swing.JTextField();
        txt_jumlah = new javax.swing.JTextField();
        bg_txtJumlah = new javax.swing.JLabel();
        btn_tambah = new javax.swing.JLabel();
        btn_ubah = new javax.swing.JLabel();
        btn_hapus = new javax.swing.JLabel();
        lbl_grandTotal = new javax.swing.JLabel();
        btn_selesai = new javax.swing.JLabel();
        lbl_sisaStok = new javax.swing.JLabel();
        lbl_tmbhNamabarang = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pn_kasirHutang = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        txt_nik = new javax.swing.JTextField();
        txt_nama = new javax.swing.JTextField();
        txt_telp = new javax.swing.JTextField();
        txt_hutangBayar = new javax.swing.JTextField();
        bg_txtDatahutang = new javax.swing.JLabel();
        lbl_totalHutang = new javax.swing.JLabel();
        lbl_errorHutang2 = new javax.swing.JLabel();
        lbl_errorHutang1 = new javax.swing.JLabel();
        lbl_errorHutang = new javax.swing.JLabel();
        bg_kasirHutang = new javax.swing.JLabel();
        txt_tunaiBayar = new javax.swing.JTextField();
        lbl_kembali = new javax.swing.JLabel();
        lbl_kasir = new javax.swing.JLabel();
        btn_tunai = new javax.swing.JRadioButton();
        btn_hutang = new javax.swing.JRadioButton();
        lbl_stokTidakcukup = new javax.swing.JLabel();
        cb_cetak = new javax.swing.JCheckBox();
        bg_kasirUtang = new javax.swing.JLabel();
        pn_bayarUtang = new javax.swing.JPanel();
        lbl_namaHutang = new javax.swing.JLabel();
        lbl_hutang = new javax.swing.JLabel();
        lbl_sisaHutang = new javax.swing.JLabel();
        txt_nikHutang = new javax.swing.JTextField();
        txt_bayarHutang = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        btn_selesaiHutang = new javax.swing.JLabel();
        bg_bayarUtang = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_kembali.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_kembalikepembukuan.png"))); // NOI18N
        btn_kembali.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_kembaliMouseClicked(evt);
            }
        });
        getContentPane().add(btn_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 10, -1, -1));

        btn_exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_exit.png"))); // NOI18N
        btn_exit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_exitMouseClicked(evt);
            }
        });
        getContentPane().add(btn_exit, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 10, -1, -1));

        btn_minimize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_minimize.png"))); // NOI18N
        btn_minimize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_minimizeMouseClicked(evt);
            }
        });
        getContentPane().add(btn_minimize, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 40, -1, -1));

        btn_bayarHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_bayarutang_off.png"))); // NOI18N
        btn_bayarHutang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_bayarHutangMouseClicked(evt);
            }
        });
        getContentPane().add(btn_bayarHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, -1, -1));

        btn_kasir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_kasir_on.png"))); // NOI18N
        btn_kasir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_kasirMouseClicked(evt);
            }
        });
        getContentPane().add(btn_kasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, -1, -1));

        bg_menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/bg.png"))); // NOI18N
        getContentPane().add(bg_menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pn_kasir.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pn_kasir.add(lbl_namaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 120, 220, 20));
        pn_kasir.add(lbl_kodeBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 58, 220, 20));

        lbl_harga.setText("Rp. -,");
        pn_kasir.add(lbl_harga, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 58, 150, 20));

        lbl_subTotal.setText("Rp.-, ");
        pn_kasir.add(lbl_subTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 58, 210, 20));

        txt_editJumlah.setBackground(new java.awt.Color(247, 255, 255));
        txt_editJumlah.setBorder(null);
        txt_editJumlah.setDisabledTextColor(new java.awt.Color(247, 255, 255));
        txt_editJumlah.setFocusable(false);
        txt_editJumlah.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_editJumlahFocusLost(evt);
            }
        });
        txt_editJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_editJumlahActionPerformed(evt);
            }
        });
        pn_kasir.add(txt_editJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 120, 150, 20));

        txt_kodeBarang.setBorder(null);
        txt_kodeBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_kodeBarangActionPerformed(evt);
            }
        });
        txt_kodeBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_kodeBarangKeyReleased(evt);
            }
        });
        pn_kasir.add(txt_kodeBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 58, 220, 20));

        txt_jumlah.setBorder(null);
        txt_jumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_jumlahActionPerformed(evt);
            }
        });
        txt_jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_jumlahKeyReleased(evt);
            }
        });
        pn_kasir.add(txt_jumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 121, 110, 20));

        bg_txtJumlah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_jumlah_off.png"))); // NOI18N
        pn_kasir.add(bg_txtJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 90, -1, 60));

        btn_tambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_tambah.png"))); // NOI18N
        btn_tambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_tambahMouseClicked(evt);
            }
        });
        pn_kasir.add(btn_tambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, -1, -1));

        btn_ubah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_ubah_off.png"))); // NOI18N
        btn_ubah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_ubahMouseClicked(evt);
            }
        });
        pn_kasir.add(btn_ubah, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 120, -1, -1));

        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_hapus_off.png"))); // NOI18N
        btn_hapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_hapusMouseClicked(evt);
            }
        });
        pn_kasir.add(btn_hapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(1120, 120, -1, -1));

        lbl_grandTotal.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lbl_grandTotal.setForeground(new java.awt.Color(64, 89, 107));
        lbl_grandTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_grandTotal.setText("Rp. -,");
        pn_kasir.add(lbl_grandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(1110, 488, 200, 30));

        btn_selesai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_selesai_off.png"))); // NOI18N
        btn_selesai.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_selesaiMouseClicked(evt);
            }
        });
        pn_kasir.add(btn_selesai, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 540, -1, -1));
        pn_kasir.add(lbl_sisaStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 150, 20));
        pn_kasir.add(lbl_tmbhNamabarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, 150, 20));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Kode Barang", "Nama Barang", "Jumlah", "Harga Satuan", "Sub Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        pn_kasir.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(63, 185, 1240, 280));

        pn_kasirHutang.setBackground(new java.awt.Color(237, 254, 255));
        pn_kasirHutang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tambah" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        pn_kasirHutang.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 30));

        txt_nik.setBorder(null);
        txt_nik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nikKeyReleased(evt);
            }
        });
        pn_kasirHutang.add(txt_nik, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 10, 190, 20));

        txt_nama.setBorder(null);
        txt_nama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_namaKeyReleased(evt);
            }
        });
        pn_kasirHutang.add(txt_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 38, 190, 20));

        txt_telp.setBorder(null);
        txt_telp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_telpKeyReleased(evt);
            }
        });
        pn_kasirHutang.add(txt_telp, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 68, 190, 20));

        txt_hutangBayar.setBorder(null);
        txt_hutangBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_hutangBayarKeyReleased(evt);
            }
        });
        pn_kasirHutang.add(txt_hutangBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 98, 190, 20));

        bg_txtDatahutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_data_on.png"))); // NOI18N
        pn_kasirHutang.add(bg_txtDatahutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 7, -1, -1));

        lbl_totalHutang.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        lbl_totalHutang.setForeground(new java.awt.Color(41, 50, 65));
        lbl_totalHutang.setText("Rp. -,");
        pn_kasirHutang.add(lbl_totalHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, 150, 20));

        lbl_errorHutang2.setFont(new java.awt.Font("Century Gothic", 1, 11)); // NOI18N
        lbl_errorHutang2.setForeground(new java.awt.Color(255, 0, 0));
        lbl_errorHutang2.setText(" - Melebihi limit hutang");
        pn_kasirHutang.add(lbl_errorHutang2, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 174, 270, -1));

        lbl_errorHutang1.setFont(new java.awt.Font("Century Gothic", 1, 11)); // NOI18N
        lbl_errorHutang1.setForeground(new java.awt.Color(255, 0, 0));
        lbl_errorHutang1.setText(" - Melebihi tenggat hutang");
        pn_kasirHutang.add(lbl_errorHutang1, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 162, 270, -1));

        lbl_errorHutang.setFont(new java.awt.Font("Century Gothic", 1, 11)); // NOI18N
        lbl_errorHutang.setForeground(new java.awt.Color(255, 0, 0));
        lbl_errorHutang.setText("Tidak dapat dilanjutkan.");
        pn_kasirHutang.add(lbl_errorHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 150, 270, -1));

        bg_kasirHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/pn_bayarutang.png"))); // NOI18N
        pn_kasirHutang.add(bg_kasirHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pn_kasir.add(pn_kasirHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 480, 414, 202));

        txt_tunaiBayar.setText("0");
        txt_tunaiBayar.setBorder(null);
        txt_tunaiBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tunaiBayarKeyReleased(evt);
            }
        });
        pn_kasir.add(txt_tunaiBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 520, 290, 20));

        lbl_kembali.setText("XXX.XXXX");
        pn_kasir.add(lbl_kembali, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 590, 230, 30));

        lbl_kasir.setFont(new java.awt.Font("Century Gothic", 1, 36)); // NOI18N
        lbl_kasir.setForeground(new java.awt.Color(64, 89, 107));
        pn_kasir.add(lbl_kasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 610, 310, 40));

        btn_tunai.setBackground(new java.awt.Color(237, 254, 255));
        buttonGroup1.add(btn_tunai);
        btn_tunai.setSelected(true);
        btn_tunai.setText("Tunai");
        btn_tunai.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btn_tunaiItemStateChanged(evt);
            }
        });
        pn_kasir.add(btn_tunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(458, 493, 70, 20));

        btn_hutang.setBackground(new java.awt.Color(237, 254, 255));
        buttonGroup1.add(btn_hutang);
        btn_hutang.setText("Hutang");
        btn_hutang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                btn_hutangItemStateChanged(evt);
            }
        });
        pn_kasir.add(btn_hutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(458, 514, 70, 20));

        lbl_stokTidakcukup.setFont(new java.awt.Font("Century Gothic", 1, 11)); // NOI18N
        lbl_stokTidakcukup.setForeground(new java.awt.Color(255, 0, 0));
        lbl_stokTidakcukup.setText("Stok Tidak Mencukupi");
        pn_kasir.add(lbl_stokTidakcukup, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 145, -1, -1));

        cb_cetak.setBackground(new java.awt.Color(237, 254, 255));
        cb_cetak.setSelected(true);
        cb_cetak.setText("Cetak struk");
        pn_kasir.add(cb_cetak, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 658, -1, -1));

        bg_kasirUtang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/bg.png"))); // NOI18N
        pn_kasir.add(bg_kasirUtang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(pn_kasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 1366, 690));

        pn_bayarUtang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        pn_bayarUtang.add(lbl_namaHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 66, 190, 20));
        pn_bayarUtang.add(lbl_hutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 128, 190, 20));
        pn_bayarUtang.add(lbl_sisaHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 189, 190, 20));

        txt_nikHutang.setBorder(null);
        txt_nikHutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_nikHutangKeyReleased(evt);
            }
        });
        pn_bayarUtang.add(txt_nikHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 98, 180, 20));

        txt_bayarHutang.setBorder(null);
        txt_bayarHutang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_bayarHutangKeyReleased(evt);
            }
        });
        pn_bayarUtang.add(txt_bayarHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 157, 180, 20));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NIK", "Nama", "Telp"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        pn_bayarUtang.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 60, 430, 200));

        btn_selesaiHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/bayar utang/btn_done_off.png"))); // NOI18N
        btn_selesaiHutang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_selesaiHutangMouseClicked(evt);
            }
        });
        pn_bayarUtang.add(btn_selesaiHutang, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 240, -1, -1));

        bg_bayarUtang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/bayar utang/bg.png"))); // NOI18N
        pn_bayarUtang.add(bg_bayarUtang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getContentPane().add(pn_bayarUtang, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 1366, 690));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_exitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_exitMouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btn_exitMouseClicked

    private void btn_minimizeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_minimizeMouseClicked
        // TODO add your handling code here:
        setExtendedState(this.ICONIFIED);
    }//GEN-LAST:event_btn_minimizeMouseClicked

    private void btn_tunaiItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btn_tunaiItemStateChanged
        // TODO add your handling code here:
        pn_kasirHutang.setVisible(btn_hutang.isSelected());
    }//GEN-LAST:event_btn_tunaiItemStateChanged

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
        if(jComboBox1.getSelectedIndex()!=0){
            
            String id_penghutang = penghutang.get(jComboBox1.getSelectedIndex()-1);
            bg_txtDatahutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_data_off.png")));
            txt_nik.disable();
            txt_nama.disable();
            txt_telp.disable();
            try{
                String sql = "select * from penghutang where id_penghutang = '"+id_penghutang +"';";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement stm=conn.prepareStatement(sql);
                java.sql.ResultSet res = stm.executeQuery(sql);
                while (res.next()){
                    isTenggat= res.getString("status").equals("TELAT");
                    sisahutang=res.getInt("sisa_hutang");
                    totalHutang = sisahutang+hutangbaru;
                    lbl_totalHutang.setText("Rp. " +totalHutang);
                    txt_nik.setText(res.getString("id_penghutang"));
                    txt_nama.setText(res.getString("nama"));
                    txt_telp.setText(res.getString("telepon"));
                }
            }catch (Exception e){
                 System.out.println(e);
            }
        }else{
            bg_txtDatahutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_data_on.png")));
            txt_nik.enable();
            txt_nama.enable();
            txt_telp.enable();
            txt_nik.setText("");
            txt_nama.setText("");
            txt_telp.setText("");
            sisahutang=0;
            totalHutang = sisahutang+hutangbaru;
            lbl_totalHutang.setText("Rp. " +totalHutang);
        }
        refresh();
        cekSelesai();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void btn_tambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_tambahMouseClicked
        // TODO add your handling code here:
        //if(sisaStok>=0){
            addItem(kodeBarang,namaBarang,Integer.valueOf(txt_jumlah.getText()),harga);
        //}
    }//GEN-LAST:event_btn_tambahMouseClicked

    private void btn_hutangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_btn_hutangItemStateChanged
        // TODO add your handling code here:
        pn_kasirHutang.setVisible(btn_hutang.isSelected());        
    }//GEN-LAST:event_btn_hutangItemStateChanged

    private void txt_kodeBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_kodeBarangKeyReleased
        // TODO add your handling code here:
        try{
            String sql ="select * from barang where kode_barang = '"+txt_kodeBarang.getText()+"';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet rs=stm.executeQuery(sql);
            if(rs.next()){
                kodeBarang=rs.getString("kode_barang");
                namaBarang=rs.getString("nama_barang");
                sisaStok=rs.getInt("stok_barang");
                harga=rs.getInt("harga_jual");
                lbl_tmbhNamabarang.setText(rs.getString("nama_barang"));
                lbl_sisaStok.setText(Integer.toString(sisaStok));
                txt_jumlah.setText("1");
            }else{
                lbl_tmbhNamabarang.setText("");
                lbl_sisaStok.setText("");
                txt_jumlah.setText("0");
                sisaStok=-1;
            }
        }catch (Exception e) {             
                System.out.println(e);
        }
        
    }//GEN-LAST:event_txt_kodeBarangKeyReleased

    private void txt_jumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_jumlahActionPerformed
        // TODO add your handling code here:
        addItem(kodeBarang,namaBarang,Integer.valueOf(txt_jumlah.getText()),harga);
    }//GEN-LAST:event_txt_jumlahActionPerformed

    private void txt_jumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_jumlahKeyReleased
        // TODO add your handling code here:
        System.out.println("release");
        lbl_stokTidakcukup.setVisible(sisaStok < Integer.valueOf(txt_jumlah.getText()));
    }//GEN-LAST:event_txt_jumlahKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        baris = jTable1.rowAtPoint(evt.getPoint());
        
        lbl_kodeBarang.setText(jTable1.getValueAt(baris, 0).toString());        
        lbl_namaBarang.setText(jTable1.getValueAt(baris, 1).toString());
        txt_editJumlah.setText(jTable1.getValueAt(baris, 2).toString());
        lbl_harga.setText("Rp. "+jTable1.getValueAt(baris, 3).toString());
        lbl_subTotal.setText("Rp. "+jTable1.getValueAt(baris, 4).toString());
        
        try{
            String sql ="select * from barang where kode_barang = '"+lbl_kodeBarang.getText()+"';";
            java.sql.Connection conn=(Connection)koneksi.configDB();
            java.sql.Statement stm=conn.createStatement();
            java.sql.ResultSet rs=stm.executeQuery(sql);
            if(rs.next()){
                sisaStok=rs.getInt("stok_barang");
            }
        }catch(Exception e){
            System.out.println(e);
        }
        bg_txtJumlah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/txt_jumlah_on.png")));
        btn_ubah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_ubah_on.png")));
        btn_hapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/kasir/btn_hapus_on.png")));
        txt_editJumlah.setFocusable(true);
        txt_editJumlah.setBackground(new java.awt.Color(255, 255, 255));
        txt_editJumlah.requestFocus();
    }//GEN-LAST:event_jTable1MouseClicked

    private void txt_kodeBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_kodeBarangActionPerformed
        // TODO add your handling code here:
        //if(sisaStok>=0){
            addItem(kodeBarang,namaBarang,Integer.valueOf(txt_jumlah.getText()),harga);
        //}
        txt_kodeBarang.setText("");
    }//GEN-LAST:event_txt_kodeBarangActionPerformed

    private void btn_ubahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_ubahMouseClicked
        // TODO add your handling code here:
        if(baris>=0){
        if(sisaStok < Integer.valueOf(txt_editJumlah.getText())){
            JOptionPane.showMessageDialog(this, "Silahkan cek kembali stok yang ada");
        }else{
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            int newJumlah = Integer.valueOf(txt_editJumlah.getText());
            int subtotal = Integer.valueOf(jTable1.getValueAt(baris, 3).toString());
            subtotal*=newJumlah;
            model.setValueAt(newJumlah, baris, 2);
            model.setValueAt (subtotal,baris, 4);
            clearEdit();
            refresh();
        }
        }
    }//GEN-LAST:event_btn_ubahMouseClicked

    private void btn_hapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_hapusMouseClicked
        // TODO add your handling code here:
        if(baris>=0){
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.removeRow(baris);
            model.fireTableRowsDeleted(baris, baris);
            
            lbl_grandTotal.setText("Rp. " +hitungTotal());
            clearEdit();
            refresh();
        }
    }//GEN-LAST:event_btn_hapusMouseClicked

    private void btn_selesaiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_selesaiMouseClicked
        // TODO add your handling code here:
        boolean hutang=btn_hutang.isSelected();
        
    if(enable){
        boolean success=true;
        boolean newHutang= jComboBox1.getSelectedIndex()==0;
        int total_barang = jTable1.getRowCount();
        String kode_transaksi = generateKode(hutang);
        Session session = new Session();
        
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tanggal = currentDate.format(dateFormatter);
        String waktu = currentTime.format(timeFormatter);
        
        if(hutang&&newHutang){
            try{
                String sql = "INSERT INTO `penghutang`(`id_penghutang`, `nama`, `telepon`) VALUES ('"
                    +txt_nik.getText()+"','" +txt_nama.getText()+"','" +txt_telp.getText()+"')";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement pst=conn.prepareStatement(sql);
                pst.execute();
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
                success = false;
            }  
        }
        
        if(hutang){
            try{
                String sql = "INSERT INTO `hutang_keluar`(`id_penghutang`, `tanggal`, `jumlah_hutang`) VALUES ('"
                    +txt_nik.getText()+"','" +tanggal+"','" +hutangbaru+"')";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement pst=conn.prepareStatement(sql);
                pst.execute();
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
                success = false;
            }  
        }
        
        try{
            String sql;
            if(hutang){
                sql = "INSERT INTO `transaksi`(`kode_transaksi`, `id_pegawai`,`id_penghutang`, `tanggal_transaksi`,`waktu_transaksi`, `jumlah_barang`, `total_harga`, `tunai_bayar`, `tunai_kembali`) VALUES('"
                    +kode_transaksi+"','" +session.getId() +"','"+txt_nik.getText() +"','" +tanggal +"','" +waktu +"','" +total_barang +"','" +hitungTotal() +"','" +bayar+"','" +0 +"')"; 
            }else{
                sql = "INSERT INTO `transaksi`(`kode_transaksi`, `id_pegawai`, `tanggal_transaksi`,`waktu_transaksi`, `jumlah_barang`, `total_harga`, `tunai_bayar`, `tunai_kembali`) VALUES('"
                    +kode_transaksi+"','" +session.getId() +"','" +tanggal +"','" +waktu +"','" +total_barang +"','" +hitungTotal() +"','" +bayar +"','" +kembali +"')";
            }
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            pst.execute();
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
            success = false;
        }  
        
        for(int i=0; i<=total_barang-1; i++){
            try{
                String sql = "INSERT INTO `detail_transaksi`(`kode_transaksi`, `kode_barang`, `quantity`,`harga_satuan`, `subtotal`) VALUES('"
                    +kode_transaksi+"','" 
                    +jTable1.getValueAt(i,0).toString() +"','" 
                    +jTable1.getValueAt(i,2).toString() +"','" 
                    +jTable1.getValueAt(i,3).toString() +"','" 
                    +jTable1.getValueAt(i,4).toString() +"')";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement pst=conn.prepareStatement(sql);
                pst.execute();
            }catch (Exception e){
                JOptionPane.showMessageDialog(this, e.getMessage());
                success = false;
            }  
        }
        
        if(success){
            JOptionPane.showMessageDialog(this, "Transkasi Berhasil");
            if (cb_cetak.isSelected()) print(kode_transaksi);
            clearAll();
        }
    }
    }//GEN-LAST:event_btn_selesaiMouseClicked

    private void txt_tunaiBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tunaiBayarKeyReleased
        // TODO add your handling code here:
        bayar=Integer.valueOf(txt_tunaiBayar.getText());
        kembali=bayar -hitungTotal();
        lbl_kembali.setText("Rp. " +kembali);
        cekSelesai();
    }//GEN-LAST:event_txt_tunaiBayarKeyReleased

    private void txt_hutangBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_hutangBayarKeyReleased
        // TODO add your handling code here:
        bayar=Integer.valueOf(txt_hutangBayar.getText());
        hutangbaru = hitungTotal()-bayar;
        int totalHutang = sisahutang+hutangbaru;
        lbl_totalHutang.setText("Rp. " +totalHutang);
    }//GEN-LAST:event_txt_hutangBayarKeyReleased

    private void btn_bayarHutangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_bayarHutangMouseClicked
        // TODO add your handling code here:
        clearBayarhutang();
        loadTableHutang();
        pn_kasir.setVisible(false);
        pn_bayarUtang.setVisible(true);
        btn_bayarHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_bayarutang_on.png")));
        btn_kasir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_kasir_off.png")));
    }//GEN-LAST:event_btn_bayarHutangMouseClicked

    private void btn_kasirMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_kasirMouseClicked
        // TODO add your handling code here:
        clearAll();
        cekSelesai();
         pn_kasir.setVisible(true);
        pn_bayarUtang.setVisible(false);
        btn_bayarHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_bayarutang_off.png")));
        btn_kasir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/Menu/btn_kasir_on.png")));
    }//GEN-LAST:event_btn_kasirMouseClicked

    private void txt_nikKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nikKeyReleased
        // TODO add your handling code here:
        cekSelesai();
    }//GEN-LAST:event_txt_nikKeyReleased
                                 
    private void txt_namaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaKeyReleased
        // TODO add your handling code here:
        cekSelesai();
    }//GEN-LAST:event_txt_namaKeyReleased

    private void txt_telpKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_telpKeyReleased
        // TODO add your handling code here:
        cekSelesai();
    }//GEN-LAST:event_txt_telpKeyReleased

    private void txt_editJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_editJumlahActionPerformed
        // TODO add your handling code here:
        if(baris>=0){
            
        if(sisaStok < Integer.valueOf(txt_editJumlah.getText())){
            JOptionPane.showMessageDialog(this, "Silahkan cek kembali stok yang ada");
        }else{
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            int newJumlah = Integer.valueOf(txt_editJumlah.getText());
            int subtotal = Integer.valueOf(jTable1.getValueAt(baris, 3).toString());
            subtotal*=newJumlah;
            model.setValueAt(newJumlah, baris, 2);
            model.setValueAt (subtotal,baris, 4);
            clearEdit();
            refresh();
        }
        }
    }//GEN-LAST:event_txt_editJumlahActionPerformed

    private void txt_editJumlahFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_editJumlahFocusLost
        // TODO add your handling code here:
        clearEdit();
        refresh();
    }//GEN-LAST:event_txt_editJumlahFocusLost

    private void btn_kembaliMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_kembaliMouseClicked
        // TODO add your handling code here:
        new TokoKelontong().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btn_kembaliMouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
        baris = jTable2.rowAtPoint(evt.getPoint());
        nik = (jTable2.getValueAt(baris, 0).toString()); 
        try{
            String sql = "select * from penghutang where id_penghutang = '"+nik+"';";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement stm=conn.prepareStatement(sql);
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                lbl_namaHutang.setText(res.getString("nama"));
                txt_nikHutang.setText(nik);
                totalHutang = res.getInt("sisa_hutang");
                lbl_hutang.setText("Rp. " +totalHutang);
                bayar = Integer.valueOf(txt_bayarHutang.getText());
                sisahutang = totalHutang - bayar;
                lbl_sisaHutang.setText("Rp. " +sisahutang);
            }
        }catch (Exception e){
             System.out.println(e);
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void txt_bayarHutangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_bayarHutangKeyReleased
        // TODO add your handling code here:
        bayar = Integer.valueOf(txt_bayarHutang.getText());
        sisahutang = totalHutang - bayar;
        if(bayar > totalHutang){
            sisahutang = 0;
            kembali = bayar - totalHutang;
            bayar = totalHutang;
        }else kembali = 0;
        
        lbl_sisaHutang.setText("Rp. " +sisahutang);
        if(bayar>0 && baris >= 0) btn_selesaiHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/bayar utang/btn_done_on.png")));
        else btn_selesaiHutang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Kasir/bayar utang/btn_done_off.png")));
    }//GEN-LAST:event_txt_bayarHutangKeyReleased

    private void btn_selesaiHutangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_selesaiHutangMouseClicked
        // TODO add your handling code here:
        if(bayar>0 && baris >= 0){
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String tanggal = currentDate.format(dateFormatter);
            
            try{
                String sql = "INSERT INTO `hutang_masuk`(`id_penghutang`, `tanggal`, `jumlah_bayar`) VALUES ('" 
                        +nik +"','" +tanggal +"','" +bayar +"');";
                java.sql.Connection conn= (Connection) koneksi.configDB();
                java.sql.PreparedStatement pst=conn.prepareStatement(sql);
                pst.execute();
                String message = "Pembayaran berhasil";
                if(sisahutang==0) message += "\nHutang Lunas";
                else message += "\nHutang tersisa Rp. "+sisahutang;
                if(kembali>0) message += "\nKembali Rp. "+kembali;
                JOptionPane.showMessageDialog(this, message);
                clearBayarhutang();
                loadTableHutang();
            }catch (Exception e){
                 System.out.println(e);
            }
        }
    }//GEN-LAST:event_btn_selesaiHutangMouseClicked

    private void txt_nikHutangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_nikHutangKeyReleased
        // TODO add your handling code here:
        nik = txt_nikHutang.getText(); 
        try{
            String sql = "select * from penghutang where id_penghutang = '"+nik+"';";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement stm=conn.prepareStatement(sql);
            java.sql.ResultSet res = stm.executeQuery(sql);
            if (res.next()){
                lbl_namaHutang.setText(res.getString("nama"));
                totalHutang = res.getInt("sisa_hutang");
                lbl_hutang.setText("Rp. " +totalHutang);
                bayar = Integer.valueOf(txt_bayarHutang.getText());
                sisahutang = totalHutang - bayar;
                lbl_sisaHutang.setText("Rp. " +sisahutang);
            }else{
                lbl_namaHutang.setText("");
                lbl_hutang.setText("Rp. -");
                bayar = Integer.valueOf(txt_bayarHutang.getText());
                sisahutang = totalHutang - bayar;
                lbl_sisaHutang.setText("Rp. " +sisahutang);
            }
        }catch (Exception e){
             System.out.println(e);
        }
    }//GEN-LAST:event_txt_nikHutangKeyReleased

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
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bg_bayarUtang;
    private javax.swing.JLabel bg_kasirHutang;
    private javax.swing.JLabel bg_kasirUtang;
    private javax.swing.JLabel bg_menu;
    private javax.swing.JLabel bg_txtDatahutang;
    private javax.swing.JLabel bg_txtJumlah;
    private javax.swing.JLabel btn_bayarHutang;
    private javax.swing.JLabel btn_exit;
    private javax.swing.JLabel btn_hapus;
    private javax.swing.JRadioButton btn_hutang;
    private javax.swing.JLabel btn_kasir;
    private javax.swing.JLabel btn_kembali;
    private javax.swing.JLabel btn_minimize;
    private javax.swing.JLabel btn_selesai;
    private javax.swing.JLabel btn_selesaiHutang;
    private javax.swing.JLabel btn_tambah;
    private javax.swing.JRadioButton btn_tunai;
    private javax.swing.JLabel btn_ubah;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cb_cetak;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lbl_errorHutang;
    private javax.swing.JLabel lbl_errorHutang1;
    private javax.swing.JLabel lbl_errorHutang2;
    private javax.swing.JLabel lbl_grandTotal;
    private javax.swing.JLabel lbl_harga;
    private javax.swing.JLabel lbl_hutang;
    private javax.swing.JLabel lbl_kasir;
    private javax.swing.JLabel lbl_kembali;
    private javax.swing.JLabel lbl_kodeBarang;
    private javax.swing.JLabel lbl_namaBarang;
    private javax.swing.JLabel lbl_namaHutang;
    private javax.swing.JLabel lbl_sisaHutang;
    private javax.swing.JLabel lbl_sisaStok;
    private javax.swing.JLabel lbl_stokTidakcukup;
    private javax.swing.JLabel lbl_subTotal;
    private javax.swing.JLabel lbl_tmbhNamabarang;
    private javax.swing.JLabel lbl_totalHutang;
    private javax.swing.JPanel pn_bayarUtang;
    private javax.swing.JPanel pn_kasir;
    private javax.swing.JPanel pn_kasirHutang;
    private javax.swing.JTextField txt_bayarHutang;
    private javax.swing.JTextField txt_editJumlah;
    private javax.swing.JTextField txt_hutangBayar;
    private javax.swing.JTextField txt_jumlah;
    private javax.swing.JTextField txt_kodeBarang;
    private javax.swing.JTextField txt_nama;
    private javax.swing.JTextField txt_nik;
    private javax.swing.JTextField txt_nikHutang;
    private javax.swing.JTextField txt_telp;
    private javax.swing.JTextField txt_tunaiBayar;
    // End of variables declaration//GEN-END:variables
}
