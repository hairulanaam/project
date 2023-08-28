/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;


import com.mysql.jdbc.Statement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarPainter;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author DL
 */

//JANGAN LUPA GANTI CONSTRAINT DB NDEK KOLOM SENG ID ID NG CASCADE!!!
public class pg_Dashboard extends javax.swing.JPanel {

    Date tgl = new Date();
    SimpleDateFormat allDate = new SimpleDateFormat("dd MMMMM yyyy");
    SimpleDateFormat hari = new SimpleDateFormat("dd");
    SimpleDateFormat bulan = new SimpleDateFormat("MM");
    SimpleDateFormat tahun = new SimpleDateFormat("yyyy");
    String dayNow = hari.format(tgl), tanggalSkrg = allDate.format(tgl);
    int monthNow = Integer.parseInt(bulan.format(tgl)), yearNow = Integer.parseInt(tahun.format(tgl)); 
    
    String id_pegawai="", old_id_pegawai, old_username, old_nama;
    int bulanCbox = monthNow, tahunCbox = yearNow;
    boolean confirm=true, backConfirm = true;
    boolean cboxAcc=false;
    public pg_Dashboard() {
        initComponents();
        setProfilToko();
        showLabaBulanIni();
        barang_terlaku();
        showTabelKaryawan();
        showTabelRestok();
        setLblTgl();
        showBarChart();
        showTopRightCorner();
        showCenterBottom();
        setCboxYear();
        pn_ringkasan.setVisible(true);
        pn_detail.setVisible(false);
        pn_pengaturanAkun.setVisible(false);
        pn_pengaturanToko.setVisible(false);
        
        pn_karyawan.setVisible(false);
        pn_editKaryawan.setVisible(false);
        pn_tambahKaryawan.setVisible(false);
        
        
    }
    void setLblTgl(){
        lbl_tgl.setText(tanggalSkrg);
    }
    String ascDesc(){
        if(setMonthPrevNext(-1)==12) return "ASC";
        return "DESC";
    }
    int setMonthPrevNext(int m){
        if (monthNow+m==13){
            return 1;
        }
        else if(monthNow+m==0){
            return 12;
        }
        return monthNow+m;
    }
    int setYearPrevNext(){
        if (setMonthPrevNext(-1)==12){
            return yearNow-1;
        }
        return yearNow;
    }
    
    void setProfilToko(){
        try{
            String sql = "SELECT toko.nama_toko, pegawai.nama from toko,pegawai where pegawai.role='owner'";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()){
                lbl_displayNamaToko.setText(res.getString(1));
                lbl_displayNamaPemilik.setText("Pemilik: "+res.getString(2));
            }
        }catch(SQLException e){
            
        }
    }
    
    void showLabaBulanIni(){
        try{
            int x=0;
            String sql = "SELECT SUM((barang.harga_jual - barang.harga_beli) * detail_transaksi.quantity) AS laba"
                    +" ,month(transaksi.tanggal_transaksi) as bulan, year(transaksi.tanggal_transaksi) as tahun"
                    +" FROM detail_transaksi"
                    +" JOIN barang ON barang.kode_barang = detail_transaksi.kode_barang"
                    +" JOIN transaksi ON transaksi.kode_transaksi = detail_transaksi.kode_transaksi"
                    +" GROUP BY bulan"
                    +" HAVING (bulan = "+monthNow+" and tahun = "+yearNow+")"
                    +" or (bulan = "+setMonthPrevNext(-1)+" and tahun = "+setYearPrevNext()+")"
                    +" order by bulan "+ascDesc();
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()&&res.getInt(2)==monthNow){
                lbl_labaBulanIni.setText("Rp"+res.getString(1));
                x=res.getInt(1);
                if(res.next()){
                    int banding=x-res.getInt(1);
                    String sbanding = Integer.toString(banding);
                    if(sbanding.contains("-")){
                        lbl_bandingLabaBulanLalu.setForeground(new java.awt.Color(238, 108, 77));
                        String fbanding = sbanding.replace("-","");
                        lbl_bandingLabaBulanLalu.setText("-Rp"+fbanding);
                    }else{
                        lbl_bandingLabaBulanLalu.setForeground(new java.awt.Color(79,217,146));
                        lbl_bandingLabaBulanLalu.setText("+Rp"+sbanding);
                    }
                }else{
                    lbl_bandingLabaBulanLalu.setText("Rp0");
                }
            }else{
                lbl_labaBulanIni.setText("Rp0");
                int banding=x-res.getInt(1);
                String sbanding = Integer.toString(banding);
                lbl_bandingLabaBulanLalu.setForeground(new java.awt.Color(238, 108, 77));
                String fbanding = sbanding.replace("-","");
                lbl_bandingLabaBulanLalu.setText("-Rp"+fbanding);
            }
            res.close();
        }catch(SQLException e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    String monthNumToName(int num){
        switch (num) {
            case 1:
                return "Januari";
            case 2:
                return "Februari";
            case 3:
                return "Maret";
            case 4:
                return "April";
            case 5:
                return "Mei";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "Agustus";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "November";
            case 12:
                return "Desember";
            default:
                break;
        }
        return "";
    }
    int monthNameToNum(String name){
        if(name.contains("Jan"))return 1;
        else if(name.contains("Feb"))return 2;
        else if(name.contains("Mar"))return 3;
        else if(name.contains("Apr"))return 4;
        else if(name.contains("Mei"))return 5;
        else if(name.contains("Jun"))return 6;
        else if(name.contains("Jul"))return 7;
        else if(name.contains("Agu"))return 8;
        else if(name.contains("Sep"))return 9;
        else if(name.contains("Okt"))return 10;
        else if(name.contains("Nov"))return 11;
        else if(name.contains("Des"))return 12;
        return 0;
    }
    void setCboxYear(){ //JUST FOR THE FIRST TIME I GUESS. BUAT ngisi cbox for TAHUN
        cboxAcc = false;
        try{
            String sql = "SELECT "
                    + "year(pengeluaran.tanggal_pengeluaran) a, "
                    + "year(transaksi.tanggal_transaksi) b, "
                    + "year(kerugian.tanggal_kerugian) c "
                    + "from transaksi, kerugian, pengeluaran "
                    + "group by a,b,c order by a, b, c asc;";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            int first=0;
            while(res.next()){
                for(int i = 1;i<=3;i++){
                    if(first<res.getInt(i)){
                        first=res.getInt(i);
                        cbox_tahun.addItem(res.getString(i));
                    }
                }
            }
            res.close();
            tahunCbox = Integer.parseInt(cbox_tahun.getItemAt(0));
            setCboxMonth();
        }catch(SQLException e){
            
        }
    }
    void setCboxMonth(){
        cboxAcc = false;
        try{
            String sql = "SELECT "
                    + "month(pengeluaran.tanggal_pengeluaran) a, "
                    + "month(transaksi.tanggal_transaksi) b, "
                    + "month(kerugian.tanggal_kerugian) c "
                    + "from transaksi, kerugian, pengeluaran "
                    + "where year(pengeluaran.tanggal_pengeluaran) = "+tahunCbox
                    + " or year(transaksi.tanggal_transaksi) = "+tahunCbox
                    + " or year(kerugian.tanggal_kerugian) = "+tahunCbox
                    + " group by a,b,c order by a, b, c asc;";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            int first=0;
            cbox_bulan.removeAllItems();
            while(res.next()){
                for(int i = 1;i<=3;i++){
                    if(first<res.getInt(i)){
                        first=res.getInt(i);
                        cbox_bulan.addItem(monthNumToName(res.getInt(i)));
                    }
                }
            }
            res.close();
            bulanCbox = monthNameToNum(cbox_bulan.getItemAt(0));
            setDetail();
        }catch(SQLException e){
            
        }
    }
    void setDetail(){
        //tabel pemasukan
        DefaultTableModel tbl1 = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl1.addColumn("Kode transaksi");
        tbl1.addColumn("Jumlah barang");
        tbl1.addColumn("Total harga");
        tbl1.addColumn("Tanggal");
        tbl_pemasukan.setModel(tbl1);
        try{
            String sql = "select kode_transaksi, jumlah_barang, total_harga,"
                    + " tanggal_transaksi from transaksi"
                    + " where month(tanggal_transaksi) = "+bulanCbox
                    + " and year(tanggal_transaksi) = "+tahunCbox
                    + " order by tanggal_transaksi, waktu_transaksi";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                tbl1.addRow(new Object[]{
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4)});
                tbl_pemasukan.setModel(tbl1);
            }
        }catch(SQLException e){
                    
        }
        //tabel pengeluaran
        DefaultTableModel tbl2 = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl2.addColumn("Kode pengeluaran");
        tbl2.addColumn("Total barang");
        tbl2.addColumn("Total pengeluaran");
        tbl2.addColumn("Tanggal");
        tbl_pengeluaran.setModel(tbl2);
        try{
            String sql = "select id_pengeluaran, total_barang, total_pengeluaran,"
                    + " tanggal_pengeluaran from pengeluaran"
                    +" where month(tanggal_pengeluaran) = "+bulanCbox
                    +" and year(tanggal_pengeluaran) = "+tahunCbox
                    +" order by tanggal_pengeluaran, waktu_pengeluaran";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                tbl2.addRow(new Object[]{
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4)});
                tbl_pengeluaran.setModel(tbl2);
            }
        }catch(SQLException e){}
        
        //tabel kerugian
        DefaultTableModel tbl3 = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl3.addColumn("Kode kerugian");
        tbl3.addColumn("Total barang");
        tbl3.addColumn("Total kerugian");
        tbl3.addColumn("Tanggal");
        tbl_kerugian.setModel(tbl3);
        try{
            String sql = "select id_kerugian, total_barang, total_kerugian,"
                    + " tanggal_kerugian from kerugian"
                    +" where month(tanggal_kerugian) = "+bulanCbox
                    +" and year(tanggal_kerugian) = "+tahunCbox
                    +" order by tanggal_kerugian, waktu_kerugian";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                tbl3.addRow(new Object[]{
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4)});
                tbl_kerugian.setModel(tbl3);
            }
        }catch(SQLException e){}
        
        //LABEL TOTAL SEMUANYA
        
        try{//LABEL PEMASUKAN
            String sql = "SELECT SUM(total_harga)AS pemasukan"
                    +" FROM transaksi"
                    +" where month(tanggal_transaksi) = "+bulanCbox
                    +" and year(tanggal_transaksi) = "+tahunCbox;
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()&&res.getString(1)!=null){
                lbl_pemasukan.setText("Rp"+res.getString(1));
            }else{
                lbl_pemasukan.setText("Rp0");
            }
        }catch(SQLException e){}
        
        try{//LABEL PENGELUARAN
            String sql = "SELECT SUM(total_pengeluaran)AS pengeluaran"
                    +" FROM pengeluaran"
                    +" where month(tanggal_pengeluaran) = "+bulanCbox
                    +" and year(tanggal_pengeluaran) = "+tahunCbox;
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()&&res.getString(1)!=null){
                lbl_pengeluaran.setText("Rp"+res.getString(1));
            }else{
                lbl_pengeluaran.setText("Rp0");
            }
        }catch(SQLException e){}
        
        try{//LABEL LABA
            String sql = "SELECT SUM((barang.harga_jual - barang.harga_beli)"
                    + " * detail_transaksi.quantity) AS laba"
                    +" FROM detail_transaksi"
                    +" JOIN barang ON barang.kode_barang = detail_transaksi.kode_barang"
                    +" JOIN transaksi ON transaksi.kode_transaksi = detail_transaksi.kode_transaksi"
                    +" where month(transaksi.tanggal_transaksi) = "+bulanCbox
                    +" and year(transaksi.tanggal_transaksi) = "+tahunCbox;
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()&&res.getString(1)!=null){
                lbl_laba.setText("Rp"+res.getString(1));
            }else{
                lbl_laba.setText("Rp0");
            }
        }catch(SQLException e){}
        
        try{//LABEL KERUGIAN
            String sql = "SELECT SUM(total_kerugian)AS kerugian"
                    +" FROM kerugian"
                    +" where month(tanggal_kerugian) = "+bulanCbox
                    +" and year(tanggal_kerugian) = "+tahunCbox;
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()&&res.getString(1)!=null){
                lbl_rugi.setText("Rp"+res.getString(1));
            }else {
                lbl_rugi.setText("Rp0");
            }
        }catch(SQLException e){}
    }
    
    void barang_terlaku(){
        try{
            String sql = "select barang.nama_barang, sum(detail_transaksi.quantity) jumlah,"
                    + " month(transaksi.tanggal_transaksi) bulan from detail_transaksi"
                    +" join barang on barang.kode_barang = detail_transaksi.kode_barang"
                    +" join transaksi on transaksi.kode_transaksi = detail_transaksi.kode_transaksi"
                    +" where month(transaksi.tanggal_transaksi) = "+monthNow
                    +" group by barang.kode_barang"
                    +" order by jumlah DESC"
                    +" LIMIT 3";
            java.sql.Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet rst = stm.executeQuery(sql);
            if (rst.next()){
                lbl_barangTerlaku1.setText("1. "+rst.getString(1));
                lbl_terjual1.setText("terjual "+rst.getString(2));
            }
            else{
                lbl_barangTerlaku1.setText("");
                lbl_terjual1.setText("");
            }
            if (rst.next()){
                lbl_barangTerlaku2.setText("2. "+rst.getString(1));
                lbl_terjual2.setText("terjual "+rst.getString(2));
            }
            else{
                lbl_barangTerlaku2.setText("");
                lbl_terjual2.setText("");
            }
            if (rst.next()){
                lbl_barangTerlaku3.setText("3. "+rst.getString(1));
                lbl_terjual3.setText("terjual "+rst.getString(2));
            }
            else{
                lbl_barangTerlaku3.setText("");
                lbl_terjual3.setText("");
            }            rst.close();
        }catch(SQLException e){}
    }
    
    void showTopRightCorner(){
        try{
            String sql = "select sum(sisa_hutang) from penghutang";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_utang.setText("Rp"+res.getString(1));
            else lbl_utang.setText("Rp0");
        }catch(SQLException e){
            
        }
        try{
            String sql = "SELECT SUM((barang.harga_jual - barang.harga_beli)"
                    + " * detail_transaksi.quantity) AS laba"
                    +" ,transaksi.tanggal_transaksi as tanggal"
                    +" FROM detail_transaksi"
                    +" JOIN barang ON barang.kode_barang = detail_transaksi.kode_barang"
                    +" JOIN transaksi ON transaksi.kode_transaksi = detail_transaksi.kode_transaksi"
                    +" group by tanggal"
                    +" having tanggal = CURRENT_DATE";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_labaHariIni.setText("Rp"+res.getString(1));
            else lbl_labaHariIni.setText("Rp0");
        }catch(SQLException e){
            
        }
        try{
            String sql = "select count(kode_transaksi) from transaksi where"
                    + " tanggal_transaksi = CURRENT_DATE";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_transaksiHariIni.setText(res.getString(1));
            else lbl_transaksiHariIni.setText("0");
        }catch(SQLException e){
            
        }
    }
    void showCenterBottom(){
        try{//rugi
            String sql = "select sum(total_kerugian) from kerugian";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_allRugi.setText("Rp"+res.getString(1));
            else lbl_allRugi.setText("Rp0");
            res.close();
        }catch(SQLException e){
            
        }
        try{//laba
            String sql = "SELECT SUM((barang.harga_jual - barang.harga_beli) * detail_transaksi.quantity) AS laba"
                    +" FROM detail_transaksi"
                    +" JOIN barang ON barang.kode_barang = detail_transaksi.kode_barang" 
                    +" JOIN transaksi ON transaksi.kode_transaksi = detail_transaksi.kode_transaksi";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_allLaba.setText("Rp"+res.getString(1));
            else lbl_allLaba.setText("Rp0");
            res.close();
        }catch(SQLException e){
            
        }
        try{
            String sql = "select sum(total_pengeluaran) from pengeluaran";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next())lbl_allPengeluaran.setText("Rp"+res.getString(1));
            else lbl_allPengeluaran.setText("Rp0");
            res.close();
        }catch(SQLException e){
            
        }
    }
    void showBarChart(){
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try{
            String sql = "SELECT SUM((barang.harga_jual - barang.harga_beli) * detail_transaksi.quantity) AS laba"
                    +" ,month(transaksi.tanggal_transaksi) as bulan, year(transaksi.tanggal_transaksi) as tahun"
                    +" FROM detail_transaksi"
                    +" JOIN barang ON barang.kode_barang = detail_transaksi.kode_barang"
                    +" JOIN transaksi ON transaksi.kode_transaksi = detail_transaksi.kode_transaksi"
                    +" GROUP BY bulan"
                    +" HAVING tahun = "+yearNow
                    +" order by bulan asc ";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while(res.next()){
                dataset.setValue(res.getDouble(1), "Laba", monthNumToName(res.getInt(2)));
            }
        }catch(SQLException e){
        }
        JFreeChart chart = ChartFactory.createBarChart(null,null,"Laba (Rp)", 
                dataset, PlotOrientation.VERTICAL, false,true,false);
        chart.setBackgroundPaint(new Color(237, 254, 255));
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
        categoryPlot.setOutlineVisible(false);
        categoryPlot.setRangeGridlinePaint(Color.BLUE);
        categoryPlot.setBackgroundPaint(new Color(237, 254, 255));
        BarRenderer renderer = (BarRenderer) categoryPlot.getRenderer();
        renderer.setShadowVisible(false);
        BarPainter painter = new StandardBarPainter();
        renderer.setBarPainter(painter);
        Color clr3 = new Color(6,167,125);
        renderer.setSeriesPaint(0, clr3);
        Font centuryGothic = new Font("Century Gothic", 0, 12);
        CategoryAxis domainAxis = categoryPlot.getDomainAxis();
        domainAxis.setTickLabelFont(centuryGothic);
        ValueAxis rangeAxis = categoryPlot.getRangeAxis();
        rangeAxis.setTickLabelFont(centuryGothic);
        ChartPanel barpChartPanel = new ChartPanel(chart);
        pn_grafik.removeAll();
        pn_grafik.add(barpChartPanel, BorderLayout.CENTER);
        pn_grafik.validate();
    }
    void showTabelKaryawan(){
        DefaultTableModel tbl = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl.addColumn("Nama");
        tbl.addColumn("Telepon");
        tbl.addColumn("Alamat");
        tbl.addColumn("E-mail");
        tbl_karyawan.setModel(tbl);
        try{
            String sql = "select nama, telepon, alamat, email from pegawai"
                    + " where role = 'karyawan' order by nama";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                tbl.addRow(new Object[]{
                    res.getString(1),
                    "+62"+res.getString(2),
                    res.getString(3),
                    res.getString(4)});
                tbl_karyawan.setModel(tbl);
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, e);
        }
        
    }
    void showTabelRestok(){
        DefaultTableModel tbl = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbl.addColumn("Nama");
        tbl.addColumn("Sisa stok");
        tbl_restok.setModel(tbl);
        try{
            String sql = "select nama_barang , stok_barang from barang where stok_barang <= 10 order by stok_barang";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            while (res.next()){
                tbl.addRow(new Object[]{
                    res.getString(1),
                    res.getString(2)});
                tbl_restok.setModel(tbl);
            }
            res.close();
        } catch (SQLException e){
            JOptionPane.showMessageDialog(this, e);
        }
    }
    
    
    void btn_tambahKaryawan(boolean cond){
        backConfirm = false;
        if(!cond||txt_tambahNamaKaryawan.getText().isEmpty()||
                txt_tambahUsernameKaryawan.getText().isEmpty()||
                txt_tambahNoWaKaryawan.getText().isEmpty()||
                txt_tambahAlamatKaryawan.getText().isEmpty()||
                txt_tambahEmailKaryawan.getText().isEmpty()||
                txt_tambahPassKaryawan.getText().isEmpty()||
                txt_tambahConfPassKaryawan.getText().isEmpty()||
                !lbl_userWarnTambahKaryawan.getText().isEmpty()||
                !lbl_emailWarnTambahKaryawan.getText().isEmpty()||
                !lbl_passWarnTambahKaryawan.getText().isEmpty()||
                !lbl_namaWarnTambahKaryawan.getText().isEmpty()){  
            btn_add_Karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_tambah_off.png")));
            btn_add_Karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            confirm = false;
            
        }else{
            btn_add_Karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_tambah.png")));
            btn_add_Karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            confirm = true;
        }
    }
    void btn_akunOwner(boolean cond){
        peng_akun = false;
        if(!cond||txt_namaOwner.getText().isEmpty()||
                txt_usernameOwner.getText().isEmpty()||
                txt_noWaOwner.getText().isEmpty()||
                txt_alamatOwner.getText().isEmpty()||
                txt_emailOwner.getText().isEmpty()||
                txt_passOwner.getText().isEmpty()||
                txt_confPassOwner.getText().isEmpty()||
                !lbl_userWarnOwner.getText().isEmpty()||
                !lbl_emailWarnOwner.getText().isEmpty()||
                !lbl_passWarnOwner.getText().isEmpty()||
                !lbl_namaWarnOwner.getText().isEmpty()){  
            btn_simpan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            confirm = false;
            
        }else{
            btn_simpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            confirm = true;
        }
    }
    void btn_toko(boolean cond){
        peng_toko = false;
        if(!cond||txt_namaToko.getText().isEmpty()||
                txt_alamatToko.getText().isEmpty()||
                txt_limitUtang.getText().isEmpty()){  
            btn_simpanToko.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            confirm = false;
            
        }else{
            btn_simpanToko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            confirm = true;
        }
    }
    void btn_simpanKaryawan(boolean cond){
        backConfirm = false;
        if(!cond||txt_editNamaKaryawan.getText().isEmpty()||
                txt_editUsernameKaryawan.getText().isEmpty()||
                txt_editNoWaKaryawan.getText().isEmpty()||
                txt_editAlamatKaryawan.getText().isEmpty()||
                txt_editEmailKaryawan.getText().isEmpty()||
                txt_editPassKaryawan.getText().isEmpty()||
                txt_editConfPassKaryawan.getText().isEmpty()||
                !lbl_userWarnEditKaryawan.getText().isEmpty()||
                !lbl_emailWarnEditKaryawan.getText().isEmpty()||
                !lbl_passWarnEditKaryawan.getText().isEmpty()||
                !lbl_namaWarnEditKaryawan.getText().isEmpty()){  
            btn_simpanKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_simpan_off.png")));
            btn_simpanKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            confirm = false;
            
        }else{
            btn_simpanKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_simpan.png")));
            btn_simpanKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            confirm = true;
        }
    }
    void emptying_txt(){
        txt_idcard.setText("");
        txt_add_idcard.setText("");
        txt_tambahNamaKaryawan.setText("");
        txt_tambahUsernameKaryawan.setText("");
        txt_tambahNoWaKaryawan.setText("");
        txt_tambahAlamatKaryawan.setText("");
        txt_tambahEmailKaryawan.setText("");
        txt_tambahPassKaryawan.setText("");
        txt_tambahConfPassKaryawan.setText("");
        txt_editUsernameKaryawan.setText("");
        txt_editNamaKaryawan.setText("");
        txt_editNoWaKaryawan.setText("");
        txt_editAlamatKaryawan.setText("");
        txt_editEmailKaryawan.setText("");
        txt_editPassKaryawan.setText("");
        txt_editConfPassKaryawan.setText("");
        txt_usernameOwner.setText("");
        txt_namaOwner.setText("");
        txt_noWaOwner.setText("");
        txt_alamatOwner.setText("");
        txt_emailOwner.setText("");
        txt_passOwner.setText("");
        txt_confPassOwner.setText("");
    }
    void lbl_setWarnEmpty(){
        lbl_userWarnEditKaryawan.setText("");
        lbl_namaWarnEditKaryawan.setText("");
        lbl_passWarnEditKaryawan.setText("");
        lbl_emailWarnEditKaryawan.setText("");
        lbl_userWarnTambahKaryawan.setText("");
        lbl_namaWarnTambahKaryawan.setText("");
        lbl_passWarnTambahKaryawan.setText("");
        lbl_emailWarnTambahKaryawan.setText("");
        lbl_userWarnOwner.setText("");
        lbl_namaWarnOwner.setText("");
        lbl_passWarnOwner.setText("");
        lbl_emailWarnOwner.setText("");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pn_sub = new javax.swing.JPanel();
        btn_ringkasan = new javax.swing.JLabel();
        btn_detail = new javax.swing.JLabel();
        btn_akun = new javax.swing.JLabel();
        btn_toko = new javax.swing.JLabel();
        btn_karyawan = new javax.swing.JLabel();
        lbl_displayNamaPemilik = new javax.swing.JLabel();
        lbl_displayNamaToko = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        pn_pengaturanToko = new javax.swing.JPanel();
        txt_limitUtang = new javax.swing.JTextField();
        txt_namaToko = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        txt_alamatToko = new javax.swing.JTextArea();
        btn_simpanToko = new javax.swing.JLabel();
        bg_pengToko = new javax.swing.JLabel();
        pn_pengaturanAkun = new javax.swing.JPanel();
        lbl_namaWarnOwner = new javax.swing.JLabel();
        lbl_passWarnOwner = new javax.swing.JLabel();
        lbl_userWarnOwner = new javax.swing.JLabel();
        lbl_emailWarnOwner = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txt_idcardOwner = new javax.swing.JTextField();
        lbl_batal3 = new javax.swing.JLabel();
        btn_scan3 = new javax.swing.JLabel();
        btn_rescan3 = new javax.swing.JLabel();
        txt_namaOwner = new javax.swing.JTextField();
        txt_emailOwner = new javax.swing.JTextField();
        txt_noWaOwner = new javax.swing.JTextField();
        txt_passOwner = new javax.swing.JPasswordField();
        txt_confPassOwner = new javax.swing.JPasswordField();
        jtextareaa = new javax.swing.JScrollPane();
        txt_alamatOwner = new javax.swing.JTextArea();
        txt_usernameOwner = new javax.swing.JTextField();
        btn_simpan = new javax.swing.JLabel();
        bg_pengAkun = new javax.swing.JLabel();
        pn_detail = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_pemasukan = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_pengeluaran = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_kerugian = new javax.swing.JTable();
        cbox_bulan = new javax.swing.JComboBox<>();
        cbox_tahun = new javax.swing.JComboBox<>();
        lbl_pemasukan = new javax.swing.JLabel();
        lbl_pengeluaran = new javax.swing.JLabel();
        lbl_laba = new javax.swing.JLabel();
        lbl_rugi = new javax.swing.JLabel();
        bg_detail = new javax.swing.JLabel();
        pn_ringkasan = new javax.swing.JPanel();
        lbl_bandingLabaBulanLalu = new javax.swing.JLabel();
        lbl_barangTerlaku1 = new javax.swing.JLabel();
        lbl_terjual1 = new javax.swing.JLabel();
        lbl_barangTerlaku2 = new javax.swing.JLabel();
        lbl_terjual2 = new javax.swing.JLabel();
        lbl_barangTerlaku3 = new javax.swing.JLabel();
        lbl_terjual3 = new javax.swing.JLabel();
        lbl_labaBulanIni = new javax.swing.JLabel();
        pn_grafik = new javax.swing.JPanel();
        lbl_labaHariIni = new javax.swing.JLabel();
        lbl_allRugi = new javax.swing.JLabel();
        lbl_allLaba = new javax.swing.JLabel();
        lbl_allPengeluaran = new javax.swing.JLabel();
        lbl_transaksiHariIni = new javax.swing.JLabel();
        lbl_utang = new javax.swing.JLabel();
        lbl_tgl = new javax.swing.JLabel();
        btn_refresh = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tbl_restok = new javax.swing.JTable();
        bg_ringkasan = new javax.swing.JLabel();
        pn_karyawan = new javax.swing.JPanel();
        pn_tambahKaryawan = new javax.swing.JPanel();
        txt_tambahPassKaryawan = new javax.swing.JPasswordField();
        txt_tambahConfPassKaryawan = new javax.swing.JPasswordField();
        txt_tambahUsernameKaryawan = new javax.swing.JTextField();
        txt_tambahNamaKaryawan = new javax.swing.JTextField();
        txt_tambahEmailKaryawan = new javax.swing.JTextField();
        btn_rescan2 = new javax.swing.JLabel();
        lbl_namaWarnTambahKaryawan = new javax.swing.JLabel();
        lbl_passWarnTambahKaryawan = new javax.swing.JLabel();
        lbl_userWarnTambahKaryawan = new javax.swing.JLabel();
        lbl_emailWarnTambahKaryawan = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbl_batal2 = new javax.swing.JLabel();
        btn_scan2 = new javax.swing.JLabel();
        txt_tambahNoWaKaryawan = new javax.swing.JTextField();
        jtextareaaa1 = new javax.swing.JScrollPane();
        txt_tambahAlamatKaryawan = new javax.swing.JTextArea();
        btn_add_Karyawan = new javax.swing.JLabel();
        btn_kembaliTambah = new javax.swing.JLabel();
        txt_add_idcard = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        pn_editKaryawan = new javax.swing.JPanel();
        txt_editPassKaryawan = new javax.swing.JPasswordField();
        txt_editConfPassKaryawan = new javax.swing.JPasswordField();
        txt_editUsernameKaryawan = new javax.swing.JTextField();
        txt_editNamaKaryawan = new javax.swing.JTextField();
        txt_editEmailKaryawan = new javax.swing.JTextField();
        btn_rescan = new javax.swing.JLabel();
        lbl_namaWarnEditKaryawan = new javax.swing.JLabel();
        lbl_passWarnEditKaryawan = new javax.swing.JLabel();
        lbl_userWarnEditKaryawan = new javax.swing.JLabel();
        lbl_emailWarnEditKaryawan = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbl_batal = new javax.swing.JLabel();
        btn_scan = new javax.swing.JLabel();
        txt_editNoWaKaryawan = new javax.swing.JTextField();
        jtextareaaa = new javax.swing.JScrollPane();
        txt_editAlamatKaryawan = new javax.swing.JTextArea();
        btn_hapusKaryawan = new javax.swing.JLabel();
        btn_simpanKaryawan = new javax.swing.JLabel();
        btn_kembaliEdit = new javax.swing.JLabel();
        txt_idcard = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_karyawan = new javax.swing.JTable();
        btn_tambahKaryawan = new javax.swing.JLabel();
        bg_karyawan = new javax.swing.JLabel();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(1366, 694));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pn_sub.setOpaque(false);
        pn_sub.setPreferredSize(new java.awt.Dimension(260, 694));
        pn_sub.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_on.png"))); // NOI18N
        btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_ringkasan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_ringkasanMouseClicked(evt);
            }
        });
        pn_sub.add(btn_ringkasan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 370, 260, -1));

        btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_off.png"))); // NOI18N
        btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_detail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_detailMouseClicked(evt);
            }
        });
        pn_sub.add(btn_detail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 430, 260, -1));

        btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_off.png"))); // NOI18N
        btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_akun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_akunMouseClicked(evt);
            }
        });
        pn_sub.add(btn_akun, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 490, 260, -1));

        btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_off.png"))); // NOI18N
        btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_toko.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_tokoMouseClicked(evt);
            }
        });
        pn_sub.add(btn_toko, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 550, 260, -1));

        btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_off.png"))); // NOI18N
        btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_karyawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_karyawanMouseClicked(evt);
            }
        });
        pn_sub.add(btn_karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 610, 260, -1));

        lbl_displayNamaPemilik.setFont(new java.awt.Font("Century Gothic", 0, 15)); // NOI18N
        lbl_displayNamaPemilik.setForeground(new java.awt.Color(40, 50, 65));
        lbl_displayNamaPemilik.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_displayNamaPemilik.setText("Pemilik: Misnadi");
        pn_sub.add(lbl_displayNamaPemilik, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 260, 260, -1));

        lbl_displayNamaToko.setFont(new java.awt.Font("Century Gothic", 1, 30)); // NOI18N
        lbl_displayNamaToko.setForeground(new java.awt.Color(41, 50, 65));
        lbl_displayNamaToko.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_displayNamaToko.setText("Toko Irul");
        pn_sub.add(lbl_displayNamaToko, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 219, 260, 40));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/bg.png"))); // NOI18N
        pn_sub.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_sub, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 694));

        pn_pengaturanToko.setMaximumSize(new java.awt.Dimension(1366, 768));
        pn_pengaturanToko.setOpaque(false);
        pn_pengaturanToko.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_limitUtang.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_limitUtang.setText("Toko Irul");
        txt_limitUtang.setBorder(null);
        txt_limitUtang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_limitUtangKeyReleased(evt);
            }
        });
        pn_pengaturanToko.add(txt_limitUtang, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 354, 194, -1));

        txt_namaToko.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_namaToko.setText("Toko Irul");
        txt_namaToko.setBorder(null);
        txt_namaToko.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_namaTokoKeyReleased(evt);
            }
        });
        pn_pengaturanToko.add(txt_namaToko, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 170, 220, -1));

        jScrollPane6.setBorder(null);

        txt_alamatToko.setColumns(10);
        txt_alamatToko.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_alamatToko.setRows(3);
        txt_alamatToko.setTabSize(6);
        txt_alamatToko.setText("asdfasdfasd");
        txt_alamatToko.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_alamatTokoKeyReleased(evt);
            }
        });
        jScrollPane6.setViewportView(txt_alamatToko);

        pn_pengaturanToko.add(jScrollPane6, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 230, 224, 77));

        btn_simpanToko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/peng akun/btn_simpan.png"))); // NOI18N
        btn_simpanToko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_simpanToko.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_simpanTokoMouseClicked(evt);
            }
        });
        pn_pengaturanToko.add(btn_simpanToko, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 510, -1, -1));

        bg_pengToko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/peng toko/bg.png"))); // NOI18N
        bg_pengToko.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanToko.add(bg_pengToko, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_pengaturanToko, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));

        pn_pengaturanAkun.setMaximumSize(new java.awt.Dimension(1366, 768));
        pn_pengaturanAkun.setOpaque(false);
        pn_pengaturanAkun.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_namaWarnOwner.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_namaWarnOwner.setForeground(new java.awt.Color(255, 102, 102));
        lbl_namaWarnOwner.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_namaWarnOwner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanAkun.add(lbl_namaWarnOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, 160, 20));

        lbl_passWarnOwner.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_passWarnOwner.setForeground(new java.awt.Color(255, 102, 102));
        lbl_passWarnOwner.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_passWarnOwner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanAkun.add(lbl_passWarnOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 260, 90, 20));

        lbl_userWarnOwner.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_userWarnOwner.setForeground(new java.awt.Color(255, 102, 102));
        lbl_userWarnOwner.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_userWarnOwner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanAkun.add(lbl_userWarnOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 140, 160, 20));

        lbl_emailWarnOwner.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_emailWarnOwner.setForeground(new java.awt.Color(255, 102, 102));
        lbl_emailWarnOwner.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_emailWarnOwner.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanAkun.add(lbl_emailWarnOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 200, 180, 20));

        jLabel6.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        jLabel6.setText("+62");
        pn_pengaturanAkun.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 289, -1, -1));

        txt_idcardOwner.setFont(new java.awt.Font("Tahoma", 0, 1)); // NOI18N
        txt_idcardOwner.setBorder(null);
        txt_idcardOwner.setOpaque(false);
        txt_idcardOwner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_idcardOwnerFocusLost(evt);
            }
        });
        txt_idcardOwner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idcardOwnerActionPerformed(evt);
            }
        });
        pn_pengaturanAkun.add(txt_idcardOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 370, -1, -1));

        lbl_batal3.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_batal3.setForeground(new java.awt.Color(255, 102, 102));
        lbl_batal3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_batal3.setText("Batalkan");
        lbl_batal3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_batal3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_batal3MouseClicked(evt);
            }
        });
        pn_pengaturanAkun.add(lbl_batal3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 370, 60, 10));

        btn_scan3.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btn_scan3.setForeground(new java.awt.Color(64, 89, 107));
        btn_scan3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_scan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png"))); // NOI18N
        btn_scan3.setText("Klik untuk scan E-KTP anda");
        btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_scan3MouseClicked(evt);
            }
        });
        pn_pengaturanAkun.add(btn_scan3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 350, 230, -1));

        btn_rescan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_rescan.png"))); // NOI18N
        btn_rescan3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_rescan3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_rescan3MouseClicked(evt);
            }
        });
        pn_pengaturanAkun.add(btn_rescan3, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 370, -1, -1));

        txt_namaOwner.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_namaOwner.setBorder(null);
        txt_namaOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_namaOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_namaOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 169, 220, -1));

        txt_emailOwner.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_emailOwner.setBorder(null);
        txt_emailOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_emailOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_emailOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 229, 220, -1));

        txt_noWaOwner.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_noWaOwner.setBorder(null);
        txt_noWaOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_noWaOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_noWaOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 289, 200, -1));

        txt_passOwner.setText("Rizky Dwi Lestari");
        txt_passOwner.setBorder(null);
        txt_passOwner.setOpaque(false);
        txt_passOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_passOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_passOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(402, 229, 220, 18));

        txt_confPassOwner.setText("Rizky Dwi Lestari");
        txt_confPassOwner.setBorder(null);
        txt_confPassOwner.setOpaque(false);
        txt_confPassOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_confPassOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_confPassOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(402, 289, 220, 18));

        jtextareaa.setBorder(null);

        txt_alamatOwner.setColumns(10);
        txt_alamatOwner.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_alamatOwner.setRows(3);
        txt_alamatOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_alamatOwnerKeyReleased(evt);
            }
        });
        jtextareaa.setViewportView(txt_alamatOwner);

        pn_pengaturanAkun.add(jtextareaa, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 355, 224, 77));

        txt_usernameOwner.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_usernameOwner.setBorder(null);
        txt_usernameOwner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_usernameOwnerKeyReleased(evt);
            }
        });
        pn_pengaturanAkun.add(txt_usernameOwner, new org.netbeans.lib.awtextra.AbsoluteConstraints(403, 169, 220, -1));

        btn_simpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/peng akun/btn_simpan.png"))); // NOI18N
        btn_simpan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_simpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_simpanMouseClicked(evt);
            }
        });
        pn_pengaturanAkun.add(btn_simpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 510, -1, -1));

        bg_pengAkun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/peng akun/bg.png"))); // NOI18N
        bg_pengAkun.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_pengaturanAkun.add(bg_pengAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_pengaturanAkun, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));

        pn_detail.setMaximumSize(new java.awt.Dimension(1366, 768));
        pn_detail.setOpaque(false);
        pn_detail.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tbl_pemasukan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_pemasukan.setRowSelectionAllowed(false);
        tbl_pemasukan.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tbl_pemasukan);

        pn_detail.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, 615, 145));

        tbl_pengeluaran.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_pengeluaran.setRowSelectionAllowed(false);
        tbl_pengeluaran.setShowVerticalLines(false);
        jScrollPane2.setViewportView(tbl_pengeluaran);

        pn_detail.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 385, 615, 105));

        tbl_kerugian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_kerugian.setRowSelectionAllowed(false);
        tbl_kerugian.setShowVerticalLines(false);
        jScrollPane3.setViewportView(tbl_kerugian);

        pn_detail.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 570, 615, 85));

        cbox_bulan.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        cbox_bulan.setForeground(new java.awt.Color(48, 75, 105));
        cbox_bulan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbox_bulanMouseClicked(evt);
            }
        });
        cbox_bulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbox_bulanActionPerformed(evt);
            }
        });
        pn_detail.add(cbox_bulan, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 43, 130, 34));

        cbox_tahun.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        cbox_tahun.setForeground(new java.awt.Color(48, 75, 105));
        cbox_tahun.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cbox_tahunMouseClicked(evt);
            }
        });
        cbox_tahun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbox_tahunActionPerformed(evt);
            }
        });
        pn_detail.add(cbox_tahun, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 43, 80, 34));

        lbl_pemasukan.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_pemasukan.setForeground(new java.awt.Color(41, 50, 65));
        lbl_pemasukan.setText("Rp-");
        pn_detail.add(lbl_pemasukan, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 370, -1));

        lbl_pengeluaran.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_pengeluaran.setForeground(new java.awt.Color(41, 50, 65));
        lbl_pengeluaran.setText("Rp-");
        pn_detail.add(lbl_pengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 235, 370, -1));

        lbl_laba.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_laba.setForeground(new java.awt.Color(41, 50, 65));
        lbl_laba.setText("Rp-");
        pn_detail.add(lbl_laba, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 332, 370, -1));

        lbl_rugi.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_rugi.setForeground(new java.awt.Color(41, 50, 65));
        lbl_rugi.setText("Rp-");
        pn_detail.add(lbl_rugi, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 428, 370, -1));

        bg_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/detail/bg.png"))); // NOI18N
        pn_detail.add(bg_detail, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_detail, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));

        pn_ringkasan.setMaximumSize(new java.awt.Dimension(1366, 768));
        pn_ringkasan.setOpaque(false);
        pn_ringkasan.setPreferredSize(new java.awt.Dimension(1105, 694));
        pn_ringkasan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lbl_bandingLabaBulanLalu.setFont(new java.awt.Font("Century Gothic", 1, 20)); // NOI18N
        lbl_bandingLabaBulanLalu.setForeground(new java.awt.Color(79, 217, 146));
        lbl_bandingLabaBulanLalu.setText("Rp-");
        pn_ringkasan.add(lbl_bandingLabaBulanLalu, new org.netbeans.lib.awtextra.AbsoluteConstraints(394, 150, 180, -1));

        lbl_barangTerlaku1.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        lbl_barangTerlaku1.setForeground(new java.awt.Color(255, 255, 255));
        lbl_barangTerlaku1.setText("-");
        pn_ringkasan.add(lbl_barangTerlaku1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, 330, -1));

        lbl_terjual1.setFont(new java.awt.Font("Century Gothic", 2, 14)); // NOI18N
        lbl_terjual1.setForeground(new java.awt.Color(255, 255, 255));
        lbl_terjual1.setText("-");
        pn_ringkasan.add(lbl_terjual1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 260, 190, -1));

        lbl_barangTerlaku2.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        lbl_barangTerlaku2.setForeground(new java.awt.Color(255, 255, 255));
        lbl_barangTerlaku2.setText("-");
        pn_ringkasan.add(lbl_barangTerlaku2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 330, -1));

        lbl_terjual2.setFont(new java.awt.Font("Century Gothic", 2, 14)); // NOI18N
        lbl_terjual2.setForeground(new java.awt.Color(255, 255, 255));
        lbl_terjual2.setText("-");
        pn_ringkasan.add(lbl_terjual2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 290, 190, -1));

        lbl_barangTerlaku3.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        lbl_barangTerlaku3.setForeground(new java.awt.Color(255, 255, 255));
        lbl_barangTerlaku3.setText("-");
        pn_ringkasan.add(lbl_barangTerlaku3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, 330, -1));

        lbl_terjual3.setFont(new java.awt.Font("Century Gothic", 2, 14)); // NOI18N
        lbl_terjual3.setForeground(new java.awt.Color(255, 255, 255));
        lbl_terjual3.setText("-");
        pn_ringkasan.add(lbl_terjual3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 320, 190, -1));

        lbl_labaBulanIni.setFont(new java.awt.Font("Century Gothic", 1, 30)); // NOI18N
        lbl_labaBulanIni.setForeground(new java.awt.Color(255, 255, 255));
        lbl_labaBulanIni.setText("Rp-");
        pn_ringkasan.add(lbl_labaBulanIni, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 163, 340, -1));

        pn_grafik.setOpaque(false);
        pn_grafik.setLayout(new java.awt.BorderLayout());
        pn_ringkasan.add(pn_grafik, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 430, 440, 250));

        lbl_labaHariIni.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_labaHariIni.setForeground(new java.awt.Color(41, 50, 65));
        lbl_labaHariIni.setText("Rp-");
        pn_ringkasan.add(lbl_labaHariIni, new org.netbeans.lib.awtextra.AbsoluteConstraints(685, 89, 370, -1));

        lbl_allRugi.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lbl_allRugi.setForeground(new java.awt.Color(238, 108, 77));
        lbl_allRugi.setText("-");
        pn_ringkasan.add(lbl_allRugi, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 613, 300, -1));

        lbl_allLaba.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lbl_allLaba.setForeground(new java.awt.Color(6, 167, 125));
        lbl_allLaba.setText("-");
        pn_ringkasan.add(lbl_allLaba, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 533, 300, -1));

        lbl_allPengeluaran.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        lbl_allPengeluaran.setForeground(new java.awt.Color(238, 108, 77));
        lbl_allPengeluaran.setText("-");
        pn_ringkasan.add(lbl_allPengeluaran, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 458, 300, -1));

        lbl_transaksiHariIni.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_transaksiHariIni.setForeground(new java.awt.Color(41, 50, 65));
        lbl_transaksiHariIni.setText("-");
        pn_ringkasan.add(lbl_transaksiHariIni, new org.netbeans.lib.awtextra.AbsoluteConstraints(685, 185, 370, -1));

        lbl_utang.setFont(new java.awt.Font("Century Gothic", 1, 37)); // NOI18N
        lbl_utang.setForeground(new java.awt.Color(41, 50, 65));
        lbl_utang.setText("Rp-");
        pn_ringkasan.add(lbl_utang, new org.netbeans.lib.awtextra.AbsoluteConstraints(685, 285, 370, -1));

        lbl_tgl.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        lbl_tgl.setForeground(new java.awt.Color(255, 255, 255));
        lbl_tgl.setText("12 Januari 2023");
        pn_ringkasan.add(lbl_tgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 250, -1));

        btn_refresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/Ringkasan/btn_refresh.png"))); // NOI18N
        btn_refresh.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pn_ringkasan.add(btn_refresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(554, 30, -1, -1));

        tbl_restok.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_restok.setShowVerticalLines(false);
        tbl_restok.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(tbl_restok);

        pn_ringkasan.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(802, 437, 260, 209));

        bg_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/Ringkasan/bg.png"))); // NOI18N
        bg_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_ringkasan.add(bg_ringkasan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_ringkasan, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));

        pn_karyawan.setMaximumSize(new java.awt.Dimension(1366, 768));
        pn_karyawan.setOpaque(false);
        pn_karyawan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pn_tambahKaryawan.setOpaque(false);
        pn_tambahKaryawan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_tambahPassKaryawan.setText("jPasswordField1");
        txt_tambahPassKaryawan.setBorder(null);
        txt_tambahPassKaryawan.setOpaque(false);
        txt_tambahPassKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahPassKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahPassKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 167, 220, 18));

        txt_tambahConfPassKaryawan.setText("jPasswordField1");
        txt_tambahConfPassKaryawan.setBorder(null);
        txt_tambahConfPassKaryawan.setOpaque(false);
        txt_tambahConfPassKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahConfPassKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahConfPassKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 227, 220, 18));

        txt_tambahUsernameKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_tambahUsernameKaryawan.setText("Toko Irul");
        txt_tambahUsernameKaryawan.setBorder(null);
        txt_tambahUsernameKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahUsernameKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahUsernameKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 106, 220, -1));

        txt_tambahNamaKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_tambahNamaKaryawan.setText("Toko Irul");
        txt_tambahNamaKaryawan.setBorder(null);
        txt_tambahNamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahNamaKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahNamaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 106, 220, -1));

        txt_tambahEmailKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_tambahEmailKaryawan.setText("Toko Irul");
        txt_tambahEmailKaryawan.setBorder(null);
        txt_tambahEmailKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahEmailKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahEmailKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 166, 220, -1));

        btn_rescan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_rescan.png"))); // NOI18N
        btn_rescan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_rescan2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_rescan2MouseClicked(evt);
            }
        });
        pn_tambahKaryawan.add(btn_rescan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, -1, -1));

        lbl_namaWarnTambahKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_namaWarnTambahKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_namaWarnTambahKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_namaWarnTambahKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_tambahKaryawan.add(lbl_namaWarnTambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 160, 20));

        lbl_passWarnTambahKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_passWarnTambahKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_passWarnTambahKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_passWarnTambahKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_tambahKaryawan.add(lbl_passWarnTambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 200, 90, 20));

        lbl_userWarnTambahKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_userWarnTambahKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_userWarnTambahKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_userWarnTambahKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_tambahKaryawan.add(lbl_userWarnTambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 160, 20));

        lbl_emailWarnTambahKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_emailWarnTambahKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_emailWarnTambahKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_emailWarnTambahKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        pn_tambahKaryawan.add(lbl_emailWarnTambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 180, 20));

        jLabel4.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        jLabel4.setText("+62");
        pn_tambahKaryawan.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 226, -1, -1));

        lbl_batal2.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_batal2.setForeground(new java.awt.Color(255, 102, 102));
        lbl_batal2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_batal2.setText("Batalkan");
        lbl_batal2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_batal2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_batal2MouseClicked(evt);
            }
        });
        pn_tambahKaryawan.add(lbl_batal2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, 60, 10));

        btn_scan2.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btn_scan2.setForeground(new java.awt.Color(64, 89, 107));
        btn_scan2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_scan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png"))); // NOI18N
        btn_scan2.setText("Klik untuk scan E-KTP anda");
        btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_scan2MouseClicked(evt);
            }
        });
        pn_tambahKaryawan.add(btn_scan2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 290, 230, -1));

        txt_tambahNoWaKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_tambahNoWaKaryawan.setText("83213123");
        txt_tambahNoWaKaryawan.setBorder(null);
        txt_tambahNoWaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahNoWaKaryawanKeyReleased(evt);
            }
        });
        pn_tambahKaryawan.add(txt_tambahNoWaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 226, 200, -1));

        jtextareaaa1.setBorder(null);

        txt_tambahAlamatKaryawan.setColumns(10);
        txt_tambahAlamatKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_tambahAlamatKaryawan.setRows(3);
        txt_tambahAlamatKaryawan.setText("asdfasdfasd");
        txt_tambahAlamatKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tambahAlamatKaryawanKeyReleased(evt);
            }
        });
        jtextareaaa1.setViewportView(txt_tambahAlamatKaryawan);

        pn_tambahKaryawan.add(jtextareaaa1, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 292, 224, 77));

        btn_add_Karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_tambah.png"))); // NOI18N
        btn_add_Karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_add_Karyawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_add_KaryawanMouseClicked(evt);
            }
        });
        pn_tambahKaryawan.add(btn_add_Karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 430, -1, -1));

        btn_kembaliTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_kembali.png"))); // NOI18N
        btn_kembaliTambah.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_kembaliTambah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_kembaliTambahMouseClicked(evt);
            }
        });
        pn_tambahKaryawan.add(btn_kembaliTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 430, -1, -1));

        txt_add_idcard.setFont(new java.awt.Font("Tahoma", 0, 1)); // NOI18N
        txt_add_idcard.setBorder(null);
        txt_add_idcard.setOpaque(false);
        txt_add_idcard.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_add_idcardFocusLost(evt);
            }
        });
        txt_add_idcard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_add_idcardActionPerformed(evt);
            }
        });
        pn_tambahKaryawan.add(txt_add_idcard, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 310, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/pn_tambahkaryawan.png"))); // NOI18N
        jLabel5.setText("jLabel3");
        pn_tambahKaryawan.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1110, -1));

        pn_karyawan.add(pn_tambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1105, -1));

        pn_editKaryawan.setOpaque(false);
        pn_editKaryawan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txt_editPassKaryawan.setText("jPasswordField1");
        txt_editPassKaryawan.setBorder(null);
        txt_editPassKaryawan.setOpaque(false);
        txt_editPassKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editPassKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editPassKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 167, 220, 18));

        txt_editConfPassKaryawan.setText("jPasswordField1");
        txt_editConfPassKaryawan.setBorder(null);
        txt_editConfPassKaryawan.setOpaque(false);
        txt_editConfPassKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editConfPassKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editConfPassKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 227, 220, 18));

        txt_editUsernameKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_editUsernameKaryawan.setText("Toko Irul");
        txt_editUsernameKaryawan.setBorder(null);
        txt_editUsernameKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editUsernameKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editUsernameKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 106, 220, -1));

        txt_editNamaKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_editNamaKaryawan.setText("Toko Irul");
        txt_editNamaKaryawan.setBorder(null);
        txt_editNamaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editNamaKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editNamaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 106, 220, -1));

        txt_editEmailKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_editEmailKaryawan.setText("Toko Irul");
        txt_editEmailKaryawan.setBorder(null);
        txt_editEmailKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editEmailKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editEmailKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(97, 166, 220, -1));

        btn_rescan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_rescan.png"))); // NOI18N
        btn_rescan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_rescan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_rescanMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(btn_rescan, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, -1, -1));

        lbl_namaWarnEditKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_namaWarnEditKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_namaWarnEditKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_namaWarnEditKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_editKaryawan.add(lbl_namaWarnEditKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 160, 20));

        lbl_passWarnEditKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_passWarnEditKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_passWarnEditKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_passWarnEditKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_editKaryawan.add(lbl_passWarnEditKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 200, 90, 20));

        lbl_userWarnEditKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_userWarnEditKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_userWarnEditKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_userWarnEditKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_editKaryawan.add(lbl_userWarnEditKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 160, 20));

        lbl_emailWarnEditKaryawan.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_emailWarnEditKaryawan.setForeground(new java.awt.Color(255, 102, 102));
        lbl_emailWarnEditKaryawan.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_emailWarnEditKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_editKaryawan.add(lbl_emailWarnEditKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 140, 180, 20));

        jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        jLabel2.setText("+62");
        pn_editKaryawan.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 226, -1, -1));

        lbl_batal.setFont(new java.awt.Font("Century Gothic", 1, 10)); // NOI18N
        lbl_batal.setForeground(new java.awt.Color(255, 102, 102));
        lbl_batal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_batal.setText("Batalkan");
        lbl_batal.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lbl_batal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbl_batalMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(lbl_batal, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 310, 60, 10));

        btn_scan.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btn_scan.setForeground(new java.awt.Color(64, 89, 107));
        btn_scan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn_scan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png"))); // NOI18N
        btn_scan.setText("Klik untuk scan E-KTP anda");
        btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_scanMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(btn_scan, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 290, 230, -1));

        txt_editNoWaKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_editNoWaKaryawan.setText("83213123");
        txt_editNoWaKaryawan.setBorder(null);
        txt_editNoWaKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editNoWaKaryawanKeyReleased(evt);
            }
        });
        pn_editKaryawan.add(txt_editNoWaKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(117, 226, 200, -1));

        jtextareaaa.setBorder(null);

        txt_editAlamatKaryawan.setColumns(10);
        txt_editAlamatKaryawan.setFont(new java.awt.Font("Century Gothic", 0, 12)); // NOI18N
        txt_editAlamatKaryawan.setRows(3);
        txt_editAlamatKaryawan.setText("asdfasdfasd");
        txt_editAlamatKaryawan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_editAlamatKaryawanKeyReleased(evt);
            }
        });
        jtextareaaa.setViewportView(txt_editAlamatKaryawan);

        pn_editKaryawan.add(jtextareaaa, new org.netbeans.lib.awtextra.AbsoluteConstraints(94, 292, 224, 77));

        btn_hapusKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_hapus.png"))); // NOI18N
        btn_hapusKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_hapusKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_hapusKaryawanMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(btn_hapusKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 430, -1, -1));

        btn_simpanKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_simpan.png"))); // NOI18N
        btn_simpanKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_simpanKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_simpanKaryawanMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(btn_simpanKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 430, -1, -1));

        btn_kembaliEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_kembali.png"))); // NOI18N
        btn_kembaliEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_kembaliEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_kembaliEditMouseClicked(evt);
            }
        });
        pn_editKaryawan.add(btn_kembaliEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 430, -1, -1));

        txt_idcard.setFont(new java.awt.Font("Tahoma", 0, 1)); // NOI18N
        txt_idcard.setBorder(null);
        txt_idcard.setOpaque(false);
        txt_idcard.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txt_idcardFocusLost(evt);
            }
        });
        txt_idcard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_idcardActionPerformed(evt);
            }
        });
        pn_editKaryawan.add(txt_idcard, new org.netbeans.lib.awtextra.AbsoluteConstraints(404, 310, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/pn_editkaryawan.png"))); // NOI18N
        jLabel3.setText("jLabel3");
        pn_editKaryawan.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1110, -1));

        pn_karyawan.add(pn_editKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 90, 1105, -1));

        tbl_karyawan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbl_karyawan.setShowVerticalLines(false);
        tbl_karyawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbl_karyawanMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbl_karyawan);

        pn_karyawan.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 155, 694, 338));

        btn_tambahKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_tambah.png"))); // NOI18N
        btn_tambahKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_tambahKaryawan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_tambahKaryawanMouseClicked(evt);
            }
        });
        pn_karyawan.add(btn_tambahKaryawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, -1, -1));

        bg_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/bg.png"))); // NOI18N
        bg_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        pn_karyawan.add(bg_karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        add(pn_karyawan, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ringkasanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_ringkasanMouseClicked
        // TODO add your handling code here:
        pn_ringkasan.setVisible(true);
        pn_detail.setVisible(false);
        pn_pengaturanAkun.setVisible(false);
        pn_pengaturanToko.setVisible(false);
        pn_karyawan.setVisible(false);
        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_on.png"))); 
        btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_off.png"))); 
        btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_off.png"))); 
        btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_off.png"))); 
        btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_off.png"))); 
        btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btn_ringkasanMouseClicked

    private void btn_detailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_detailMouseClicked
        // TODO add your handling code here:
        pn_ringkasan.setVisible(false);
        pn_detail.setVisible(true);
        pn_pengaturanAkun.setVisible(false);
        pn_pengaturanToko.setVisible(false);
        pn_karyawan.setVisible(false);
        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_off.png"))); 
        btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_on.png"))); 
        btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_off.png"))); 
        btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_off.png"))); 
        btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_off.png"))); 
        btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }//GEN-LAST:event_btn_detailMouseClicked
    String old_id_owner, old_nama_owner;
    boolean peng_akun = true;
    private void btn_akunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_akunMouseClicked
        // TODO add your handling code here:
        
        try{
            String sql = "select id_pegawai, nama, telepon, alamat, username,"
                    + " email, password from pegawai where role = 'owner'";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()){
                old_id_owner=res.getString("id_pegawai");
                id_pegawai=res.getString("id_pegawai");
                old_nama_owner = res.getString("nama");
                old_username = res.getString("username");
                txt_namaOwner.setText(res.getString("nama"));
                txt_usernameOwner.setText(res.getString("username"));
                txt_emailOwner.setText(res.getString("email"));
                txt_noWaOwner.setText(res.getString("telepon"));
                txt_alamatOwner.setText(res.getString("alamat"));
                txt_passOwner.setText(res.getString("password"));
                txt_confPassOwner.setText(res.getString("password"));
            }
            if(res.getString("id_pegawai").length()==3){
                btn_scan3.setText("Klik untuk scan E-KTP");
                btn_scan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
                btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                
            }else{
                btn_scan3.setText("Data E-KTP telah tersimpan");
                btn_scan3.setIcon(null);
                btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            btn_simpan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            txt_idcardOwner.setFocusable(false);
            
            lbl_batal3.setVisible(false);
            btn_rescan3.setVisible(false);
            
            pn_ringkasan.setVisible(false);
            pn_detail.setVisible(false);
            pn_pengaturanAkun.setVisible(true);
            pn_pengaturanToko.setVisible(false);
            pn_karyawan.setVisible(false);
            btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_off.png"))); 
            btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_off.png"))); 
            btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_on.png"))); 
            btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_off.png"))); 
            btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_off.png"))); 
            btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan, mohon coba lagi beberapa saat", "Kesalahan",1);
        }
    }//GEN-LAST:event_btn_akunMouseClicked
    boolean peng_toko = true;
    private void btn_tokoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_tokoMouseClicked
        // TODO add your handling code here:
        try{
            String sql = "select nama_toko, alamat_toko, limit_hutang from toko";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()){
                txt_namaToko.setText(res.getString(1));
                txt_alamatToko.setText(res.getString(2));
                txt_limitUtang.setText(res.getString(3));
                
            }
            
            pn_ringkasan.setVisible(false);
            pn_detail.setVisible(false);
            pn_pengaturanAkun.setVisible(false);
            pn_pengaturanToko.setVisible(true);
            pn_karyawan.setVisible(false);
            btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_off.png"))); 
            btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_off.png"))); 
            btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_off.png"))); 
            btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_on.png"))); 
            btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_off.png"))); 
            btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan, mohon coba lagi beberapa saat", "Kesalahan",1);
        }
    }//GEN-LAST:event_btn_tokoMouseClicked

    private void btn_karyawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_karyawanMouseClicked
        // TODO add your handling code here:
        
        pn_ringkasan.setVisible(false);
        pn_detail.setVisible(false);
        pn_pengaturanAkun.setVisible(false);
        pn_pengaturanToko.setVisible(false);
        pn_karyawan.setVisible(true);
        btn_ringkasan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_ringkasan_off.png"))); 
        btn_ringkasan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_detail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_detail_off.png"))); 
        btn_detail.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_akun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_akun_off.png"))); 
        btn_akun.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_toko.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_toko_off.png"))); 
        btn_toko.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Sub_dashboard/btn_karyawan_on.png"))); 
        btn_karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_btn_karyawanMouseClicked

    private void btn_kembaliEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_kembaliEditMouseClicked
        // TODO add your handling code here:
        if(txt_idcard.isFocusOwner()){
            txt_idcard.setText("");
            txt_idcard.setFocusable(false);
            btn_scan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
            btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_scan.setText("Klik untuk scan E-KTP");
            lbl_batal.setVisible(false);
            btn_simpanKaryawan(true);
        }
        if(backConfirm==false){
            int resp = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin kembali?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(resp == JOptionPane.YES_OPTION){
                old_id_pegawai="";
                id_pegawai="";
                old_username="";
                old_nama="";
                showTabelKaryawan();
                btn_tambahKaryawan.setVisible(true);
                pn_editKaryawan.setVisible(false);
                jScrollPane4.setVisible(true);
                tbl_karyawan.setVisible(true);
                backConfirm = true;
            }
        }else{
            old_id_pegawai="";
            id_pegawai="";
            old_username="";
            old_nama="";
            showTabelKaryawan();
            btn_tambahKaryawan.setVisible(true);
            pn_editKaryawan.setVisible(false);
            jScrollPane4.setVisible(true);
            tbl_karyawan.setVisible(true);
        }
        emptying_txt();
        lbl_setWarnEmpty();
    }//GEN-LAST:event_btn_kembaliEditMouseClicked

    private void tbl_karyawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbl_karyawanMouseClicked
        // TODO add your handling code here:
        int baris = tbl_karyawan.rowAtPoint(evt.getPoint());
        String nama, alamat, email;
        nama = tbl_karyawan.getValueAt(baris, 0).toString();
        alamat = tbl_karyawan.getValueAt(baris, 2).toString();
        email = tbl_karyawan.getValueAt(baris, 3).toString();
        try{
            String sql = "select id_pegawai, telepon, username, password from pegawai where nama = '"+nama+"'"
                    + " and alamat = '"+alamat+"'"
                    + " and email = '"+email+"'"
                    + " and role = 'karyawan'";
            Connection con = (Connection)koneksi.configDB();
            java.sql.Statement stm = con.createStatement();
            java.sql.ResultSet res = stm.executeQuery(sql);
            if(res.next()){
                old_id_pegawai=res.getString("id_pegawai");
                id_pegawai=res.getString("id_pegawai");
                old_nama = nama;
                txt_editUsernameKaryawan.setText(res.getString("username"));
                old_username = res.getString("username");
                txt_editPassKaryawan.setText(res.getString("password"));
                txt_editConfPassKaryawan.setText(res.getString("password"));
                txt_editNamaKaryawan.setText(nama);
                txt_editNoWaKaryawan.setText(res.getString("telepon"));
                txt_editAlamatKaryawan.setText(alamat);
                txt_editEmailKaryawan.setText(email);
            }
            if(res.getString("id_pegawai").length()==3){
                btn_scan.setText("Klik untuk scan E-KTP");
                btn_scan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
                btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                
            }else{
                btn_scan.setText("Data E-KTP telah tersimpan");
                btn_scan.setIcon(null);
                btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            }
            btn_tambahKaryawan.setVisible(false);
            btn_simpanKaryawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_simpan_off.png")));
            btn_simpanKaryawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            txt_idcard.setFocusable(false);
            
            lbl_batal.setVisible(false);
            btn_rescan.setVisible(false);
            pn_editKaryawan.setVisible(true);
            jScrollPane4.setVisible(false);
            tbl_karyawan.setVisible(false);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan, mohon coba lagi beberapa saat", "Kesalahan",1);
        }
        
        
    }//GEN-LAST:event_tbl_karyawanMouseClicked

    private void btn_scanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_scanMouseClicked
        // TODO add your handling code here:
        if(id_pegawai.length()==3){
            txt_idcard.setFocusable(true);
            btn_scan.setIcon(null);
            btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            btn_scan.setText("Scan kartu E-KTP");
            lbl_batal.setVisible(true);
            txt_idcard.requestFocus();
            btn_simpanKaryawan(false);
        }else{
            
        }
    }//GEN-LAST:event_btn_scanMouseClicked

    private void txt_idcardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idcardActionPerformed
        // TODO add your handling code here:
        if(txt_idcard.getText().length()==10){
            String kode=txt_idcard.getText();
            try{
                String sql = "select id_pegawai from pegawai where id_pegawai = '"+kode+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if (res.next()){
                    txt_idcard.setText("");
                    JOptionPane.showMessageDialog(null, "Terdapat data yang sama","Peringatan",2);
                }else{
                    id_pegawai=kode;
                    txt_idcard.setText("");
                    txt_idcard.setFocusable(false);
                    btn_scan.setText("Data E-KTP telah tersimpan");
                    btn_scan.setIcon(null);
                    btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    btn_rescan.setVisible(true);
                    lbl_batal.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Data tersimpan","Scan Berhasil",1);
                    btn_simpanKaryawan(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Mohon coba lagi beberapa saat","Scan Gagal",2);
            }
        }else{
            txt_idcard.setText("");
            JOptionPane.showMessageDialog(null, "Silahkan coba kembali","Scan Gagal",2);
            
        }
    }//GEN-LAST:event_txt_idcardActionPerformed

    private void lbl_batalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_batalMouseClicked
        // TODO add your handling code here:
        txt_idcard.setText("");
        txt_idcard.setFocusable(false);
        btn_scan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
        btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan.setText("Klik untuk scan E-KTP");
        lbl_batal.setVisible(false);
        btn_simpanKaryawan(true);
    }//GEN-LAST:event_lbl_batalMouseClicked

    private void btn_rescanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_rescanMouseClicked
        // TODO add your handling code here:
        txt_idcard.setFocusable(true);
        btn_scan.setIcon(null);
        btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_scan.setText("Scan kartu E-KTP");
        lbl_batal.setVisible(true);
        txt_idcard.requestFocus();
        btn_rescan.setVisible(false);
        id_pegawai=old_id_pegawai;
        btn_simpanKaryawan(false);
    }//GEN-LAST:event_btn_rescanMouseClicked

    private void txt_idcardFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_idcardFocusLost
        // TODO add your handling code here:
        txt_idcard.requestFocus();
    }//GEN-LAST:event_txt_idcardFocusLost

    private void txt_editEmailKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editEmailKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_editEmailKaryawan.getText().contains("@")||!txt_editEmailKaryawan.getText().contains(".")){
            lbl_emailWarnEditKaryawan.setText("Masukkan email dengan tepat");
            btn_simpanKaryawan(false);
        }
        else if(txt_editEmailKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else {
            btn_simpanKaryawan(true);
            lbl_emailWarnEditKaryawan.setText("");
        }
        
    }//GEN-LAST:event_txt_editEmailKaryawanKeyReleased

    private void txt_editNamaKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editNamaKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_editNamaKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else{
            String nama = txt_editNamaKaryawan.getText();
            try{
                String sql = "select * from pegawai where nama = '"+nama
                        +"' and nama !='"+old_nama+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_namaWarnEditKaryawan.setText("Nama telah digunakan");
                    btn_simpanKaryawan(false);
                    
                }else{
                    lbl_namaWarnEditKaryawan.setText("");
                    btn_simpanKaryawan(true);
                }
            }catch(SQLException e){
//                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_editNamaKaryawanKeyReleased

    private void txt_editNoWaKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editNoWaKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_editNoWaKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else {
            btn_simpanKaryawan(true);
        }
    }//GEN-LAST:event_txt_editNoWaKaryawanKeyReleased

    private void txt_editAlamatKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editAlamatKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_editAlamatKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else {
            btn_simpanKaryawan(true);
        }
    }//GEN-LAST:event_txt_editAlamatKaryawanKeyReleased

    private void txt_editUsernameKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editUsernameKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_editUsernameKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else{
            String username=txt_editUsernameKaryawan.getText();
            try{
                String sql = "select * from pegawai where username = '"+username+"' and username !='"+old_username+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_userWarnEditKaryawan.setText("Username telah digunakan");
                    btn_simpanKaryawan(false);
                    
                }else{
                    lbl_userWarnEditKaryawan.setText("");
                    btn_simpanKaryawan(true);
                }
            }catch(SQLException e){
//                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_editUsernameKaryawanKeyReleased

    private void txt_editConfPassKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editConfPassKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_editConfPassKaryawan.getText().equals(txt_editPassKaryawan.getText())){
            lbl_passWarnEditKaryawan.setText("Harus sama");
            btn_simpanKaryawan(false);
        }else{
            lbl_passWarnEditKaryawan.setText("");
            btn_simpanKaryawan(true);
        }
    }//GEN-LAST:event_txt_editConfPassKaryawanKeyReleased

    private void txt_editPassKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_editPassKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_editConfPassKaryawan.getText().equals(txt_editPassKaryawan.getText())){
            lbl_passWarnEditKaryawan.setText("Harus sama");
            btn_simpanKaryawan(false);
        }else{
            lbl_passWarnEditKaryawan.setText("");
            btn_simpanKaryawan(true);
        }
    }//GEN-LAST:event_txt_editPassKaryawanKeyReleased

    private void btn_simpanKaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_simpanKaryawanMouseClicked
        // TODO add your handling code here:
        if (!confirm){
            JOptionPane.showMessageDialog(null, "Data tidak terpenuhi!");
        }
        else if (backConfirm){
            JOptionPane.showMessageDialog(null, "Tidak ada perubahan!");
        } else {
            try{
                String sql = "update pegawai set id_pegawai = '"+id_pegawai+"', "
                        + "nama = '"+txt_editNamaKaryawan.getText()+"', "
                        + "telepon = '"+txt_editNoWaKaryawan.getText()+"', "
                        + "alamat = '"+txt_editAlamatKaryawan.getText()+"', "
                        + "email = '"+txt_editEmailKaryawan.getText()+"', "
                        + "username = '"+txt_editUsernameKaryawan.getText()+"', "
                        + "password = '"+txt_editConfPassKaryawan.getText()+"' where id_pegawai = '"+old_id_pegawai+"'";
                
                Statement state = (Statement) koneksi.configDB().createStatement();
                state.executeUpdate(sql);
                state.close();
                JOptionPane.showMessageDialog(null,"Data karyawan berhasil disimpan");
                old_id_pegawai="";
                id_pegawai="";
                old_username="";
                old_nama="";
                showTabelKaryawan();
                btn_tambahKaryawan.setVisible(true);
                pn_editKaryawan.setVisible(false);
                jScrollPane4.setVisible(true);
                tbl_karyawan.setVisible(true);
                backConfirm = true;
                lbl_setWarnEmpty();
                emptying_txt();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }//GEN-LAST:event_btn_simpanKaryawanMouseClicked

    private void btn_hapusKaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_hapusKaryawanMouseClicked
        // TODO add your handling code here:
        if(txt_idcard.isFocusOwner()){
            txt_idcard.setText("");
            txt_idcard.setFocusable(false);
            btn_scan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
            btn_scan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_scan.setText("Klik untuk scan E-KTP");
            lbl_batal.setVisible(false);
            btn_simpanKaryawan(true);
        }
        int resp = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin menghapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if(resp == JOptionPane.YES_OPTION){
            try{
                String sql="delete from pegawai where id_pegawai = '"+old_id_pegawai+"'";
                Statement state = (Statement) koneksi.configDB().createStatement();
                state.executeUpdate(sql);
                state.close();
                old_id_pegawai="";
                id_pegawai="";
                old_username="";
                old_nama="";
                showTabelKaryawan();
                btn_tambahKaryawan.setVisible(true);
                pn_editKaryawan.setVisible(false);
                jScrollPane4.setVisible(true);
                tbl_karyawan.setVisible(true);
                JOptionPane.showMessageDialog(null,"Data berhasil dihapus");
                backConfirm = true;
                lbl_setWarnEmpty();
                emptying_txt();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Coba kembali");
            }
        }
    }//GEN-LAST:event_btn_hapusKaryawanMouseClicked

//TAMBAH KARYAWAN BELOW ############################################################################################################
    
    private void txt_tambahPassKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahPassKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_tambahConfPassKaryawan.getText().equals(txt_tambahPassKaryawan.getText())){
            lbl_passWarnTambahKaryawan.setText("Harus sama");
            btn_tambahKaryawan(false);
        }else{
            lbl_passWarnTambahKaryawan.setText("");
            btn_tambahKaryawan(true);
        }
    }//GEN-LAST:event_txt_tambahPassKaryawanKeyReleased

    private void txt_tambahConfPassKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahConfPassKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_tambahConfPassKaryawan.getText().equals(txt_tambahPassKaryawan.getText())){
            lbl_passWarnTambahKaryawan.setText("Harus sama");
            btn_tambahKaryawan(false);
        }else{
            lbl_passWarnTambahKaryawan.setText("");
            btn_tambahKaryawan(true);
        }
    }//GEN-LAST:event_txt_tambahConfPassKaryawanKeyReleased

    private void txt_tambahUsernameKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahUsernameKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_tambahUsernameKaryawan.getText().isEmpty()){btn_tambahKaryawan(false);}
        else{
            String username=txt_tambahUsernameKaryawan.getText();
            try{
                String sql = "select * from pegawai where username = '"+username+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_userWarnTambahKaryawan.setText("Username telah digunakan");
                    btn_tambahKaryawan(false);
                    
                }else{
                    lbl_userWarnTambahKaryawan.setText("");
                    btn_tambahKaryawan(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_tambahUsernameKaryawanKeyReleased

    private void txt_tambahNamaKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahNamaKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_tambahNamaKaryawan.getText().isEmpty()){btn_simpanKaryawan(false);}
        else{
            String nama = txt_tambahNamaKaryawan.getText();
            try{
                String sql = "select * from pegawai where nama = '"+nama+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_namaWarnTambahKaryawan.setText("Nama telah digunakan");
                    btn_tambahKaryawan(false);
                }else{
                    lbl_namaWarnTambahKaryawan.setText("");
                    btn_tambahKaryawan(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_tambahNamaKaryawanKeyReleased

    private void txt_tambahEmailKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahEmailKaryawanKeyReleased
        // TODO add your handling code here:
        if(!txt_tambahEmailKaryawan.getText().contains("@")||!txt_tambahEmailKaryawan.getText().contains(".")){
            lbl_emailWarnTambahKaryawan.setText("Masukkan email dengan tepat");
            btn_tambahKaryawan(false);
        }
        else if(txt_tambahEmailKaryawan.getText().isEmpty()){btn_tambahKaryawan(false);}
        else {
            btn_tambahKaryawan(true);
            lbl_emailWarnTambahKaryawan.setText("");
        }
    }//GEN-LAST:event_txt_tambahEmailKaryawanKeyReleased

    private void btn_rescan2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_rescan2MouseClicked
        // TODO add your handling code here:
        txt_add_idcard.setFocusable(true);
        btn_scan2.setIcon(null);
        btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_scan2.setText("Scan kartu E-KTP");
        id_pegawai="";
        lbl_batal2.setVisible(true);
        txt_add_idcard.requestFocus();
        btn_rescan2.setVisible(false);
        btn_tambahKaryawan(false);
    }//GEN-LAST:event_btn_rescan2MouseClicked

    private void lbl_batal2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_batal2MouseClicked
        // TODO add your handling code here:
        txt_add_idcard.setText("");
        txt_add_idcard.setFocusable(false);
        btn_scan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
        btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan2.setText("Klik untuk scan E-KTP");
        lbl_batal2.setVisible(false);
        btn_tambahKaryawan(true);
    }//GEN-LAST:event_lbl_batal2MouseClicked

    private void btn_scan2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_scan2MouseClicked
        // TODO add your handling code here:
        txt_add_idcard.setFocusable(true);
        btn_scan2.setIcon(null);
        btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_scan2.setText("Scan kartu E-KTP");
        lbl_batal2.setVisible(true);
        txt_add_idcard.requestFocus();
        btn_tambahKaryawan(false);
    }//GEN-LAST:event_btn_scan2MouseClicked

    private void txt_tambahNoWaKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahNoWaKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_tambahNoWaKaryawan.getText().isEmpty()){btn_tambahKaryawan(false);}
        else {
            btn_tambahKaryawan(true);
        }
    }//GEN-LAST:event_txt_tambahNoWaKaryawanKeyReleased

    private void txt_tambahAlamatKaryawanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_tambahAlamatKaryawanKeyReleased
        // TODO add your handling code here:
        if(txt_tambahAlamatKaryawan.getText().isEmpty()){btn_tambahKaryawan(false);}
        else {
            btn_tambahKaryawan(true);
        }
    }//GEN-LAST:event_txt_tambahAlamatKaryawanKeyReleased

    private void txt_add_idcardFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_add_idcardFocusLost
        // TODO add your handling code here:
        txt_add_idcard.requestFocus();
    }//GEN-LAST:event_txt_add_idcardFocusLost

    private void txt_add_idcardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_add_idcardActionPerformed
        // TODO add your handling code here:
        if(txt_add_idcard.getText().length()==10){
            String kode=txt_add_idcard.getText();
            try{
                String sql = "select id_pegawai from pegawai where id_pegawai = '"+kode+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if (res.next()){
                    txt_add_idcard.setText("");
                    JOptionPane.showMessageDialog(null, "Terdapat data yang sama","Peringatan",2);
                }else{
                    id_pegawai=kode;
                    txt_add_idcard.setText("");
                    txt_add_idcard.setFocusable(false);
                    btn_scan2.setText("Data E-KTP telah tersimpan");
                    btn_scan2.setIcon(null);
                    btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    btn_rescan2.setVisible(true);
                    lbl_batal2.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Data tersimpan","Scan Berhasil",1);
                    btn_tambahKaryawan(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Mohon coba lagi beberapa saat","Scan Gagal",2);
            }
        }else{
            txt_add_idcard.setText("");
            JOptionPane.showMessageDialog(null, "Silahkan coba kembali","Scan Gagal",2);
            
        }
    }//GEN-LAST:event_txt_add_idcardActionPerformed

    private void btn_add_KaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_add_KaryawanMouseClicked
        // TODO add your handling code here:
        if (!confirm){
            JOptionPane.showMessageDialog(null, "Data tidak terpenuhi!");
        } else {
            int resp = JOptionPane.showConfirmDialog(null, "Apakah data yang dimasukkan sudah benar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(resp == JOptionPane.YES_OPTION){
                
                if(id_pegawai.equals("")){
                    int id = 100;
                    try{
                        String sql = "select id_pegawai from pegawai where length(id_pegawai) = 3 order by id_pegawai";
                        java.sql.Connection con=(Connection) koneksi.configDB();
                        java.sql.Statement stm=con.createStatement();
                        java.sql.ResultSet res=stm.executeQuery(sql);
                        
                        while(res.next()&&res.getString(1).equals(Integer.toString(id))){
                            id++;
                        }
                        res.close();
                        id_pegawai = Integer.toString(id);
                    }catch(SQLException | HeadlessException e){
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
                try{
                    String sql = "insert into pegawai values ('"+id_pegawai+"', "
                            + "'"+txt_tambahNamaKaryawan.getText()         +"', "
                            + "'"+txt_tambahNoWaKaryawan.getText()         +"', "
                            + "'"+txt_tambahAlamatKaryawan.getText()       +"', "
                            + "'"+txt_tambahEmailKaryawan.getText()        +"', "
                            + "'"+txt_tambahUsernameKaryawan.getText()     +"', "
                            + "'"+txt_tambahConfPassKaryawan.getText()     +"', "
                            + "'karyawan')";

                    java.sql.Connection con= (Connection) koneksi.configDB();
                    java.sql.PreparedStatement pst=con.prepareStatement(sql);
                    pst.execute();
                    pst.close();
                    JOptionPane.showMessageDialog(null,"Data berhasil ditambahkan");
                    old_id_pegawai="";
                    id_pegawai="";
                    old_username="";
                    old_nama="";
                    showTabelKaryawan();
                    btn_tambahKaryawan.setVisible(true);
                    pn_tambahKaryawan.setVisible(false);
                    pn_editKaryawan.setVisible(false);
                    jScrollPane4.setVisible(true);
                    tbl_karyawan.setVisible(true);
                    backConfirm = true;
                    lbl_setWarnEmpty();
                    emptying_txt();
                }catch(SQLException | HeadlessException e){
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }//GEN-LAST:event_btn_add_KaryawanMouseClicked

    private void btn_tambahKaryawanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_tambahKaryawanMouseClicked
        // TODO add your handling code here:
        confirm=false;
        btn_tambahKaryawan.setVisible(false);
        txt_add_idcard.setText("");
        txt_add_idcard.setFocusable(false);
        btn_add_Karyawan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_tambah_off.png")));
        btn_add_Karyawan.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_scan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
        btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan2.setText("Klik untuk scan E-KTP");
        lbl_batal2.setVisible(false);
        btn_rescan2.setVisible(false);
        pn_tambahKaryawan.setVisible(true);
        pn_editKaryawan.setVisible(false);
        jScrollPane4.setVisible(false);
        tbl_karyawan.setVisible(false);
        lbl_setWarnEmpty();
        emptying_txt();
    }//GEN-LAST:event_btn_tambahKaryawanMouseClicked

    private void btn_kembaliTambahMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_kembaliTambahMouseClicked
        // TODO add your handling code here:
        if(txt_add_idcard.isFocusOwner()){
            txt_add_idcard.setText("");
            txt_add_idcard.setFocusable(false);
            btn_scan2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
            btn_scan2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            btn_scan2.setText("Klik untuk scan E-KTP");
            lbl_batal2.setVisible(false);
            btn_tambahKaryawan(true);
        }
        if(backConfirm==false){
            int resp = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin kembali?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(resp == JOptionPane.YES_OPTION){
                old_id_pegawai="";
                id_pegawai="";
                old_username="";
                old_nama="";
                showTabelKaryawan();
                btn_tambahKaryawan.setVisible(true);
                pn_tambahKaryawan.setVisible(false);
                pn_editKaryawan.setVisible(false);
                jScrollPane4.setVisible(true);
                tbl_karyawan.setVisible(true);
                backConfirm = true;
            }
        }else{
            old_id_pegawai="";
            id_pegawai="";
            old_username="";
            old_nama="";
            showTabelKaryawan();
            btn_tambahKaryawan.setVisible(true);
            pn_tambahKaryawan.setVisible(false);
            pn_editKaryawan.setVisible(false);
            jScrollPane4.setVisible(true);
            tbl_karyawan.setVisible(true);
        }
        lbl_setWarnEmpty();
    }//GEN-LAST:event_btn_kembaliTambahMouseClicked
    
    private void cbox_bulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbox_bulanActionPerformed
        // TODO add your handling code here:
        if(cboxAcc == true){
            bulanCbox = monthNameToNum((String) cbox_bulan.getSelectedItem());
//            JOptionPane.showMessageDialog(null, "tes1"+cboxAcc);
            setDetail();
        }
    }//GEN-LAST:event_cbox_bulanActionPerformed

    private void cbox_tahunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbox_tahunActionPerformed
        // TODO add your handling code here:
        if(cboxAcc == true){
//            JOptionPane.showMessageDialog(null, "tes2"+cboxAcc);
            tahunCbox = Integer.parseInt((String)cbox_tahun.getSelectedItem());
            setCboxMonth(); 
        }
    }//GEN-LAST:event_cbox_tahunActionPerformed

    private void cbox_bulanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbox_bulanMouseClicked
        // TODO add your handling code here:
        cboxAcc = true;
    }//GEN-LAST:event_cbox_bulanMouseClicked

    private void cbox_tahunMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cbox_tahunMouseClicked
        // TODO add your handling code here:
        cboxAcc = true;
    }//GEN-LAST:event_cbox_tahunMouseClicked

    private void txt_idcardOwnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txt_idcardOwnerFocusLost
        // TODO add your handling code here:
        txt_idcardOwner.requestFocus();
    }//GEN-LAST:event_txt_idcardOwnerFocusLost

    private void txt_idcardOwnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_idcardOwnerActionPerformed
        // TODO add your handling code here:
        if(txt_idcardOwner.getText().length()==10){
            String kode=txt_idcardOwner.getText();
            try{
                String sql = "select id_pegawai from pegawai where id_pegawai = '"+kode+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if (res.next()){
                    txt_idcard.setText("");
                    JOptionPane.showMessageDialog(null, "Terdapat data yang sama","Peringatan",2);
                }else{
                    id_pegawai=kode;
                    txt_idcardOwner.setText("");
                    txt_idcardOwner.setFocusable(false);
                    btn_scan3.setText("Data E-KTP telah tersimpan");
                    btn_scan3.setIcon(null);
                    btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    btn_rescan3.setVisible(true);
                    lbl_batal3.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Data tersimpan","Scan Berhasil",1);
                    btn_akunOwner(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Mohon coba lagi beberapa saat","Scan Gagal",2);
            }
        }else{
            txt_idcardOwner.setText("");
            JOptionPane.showMessageDialog(null, "Silahkan coba kembali","Scan Gagal",2);
            
        }
    }//GEN-LAST:event_txt_idcardOwnerActionPerformed

    private void btn_rescan3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_rescan3MouseClicked
        // TODO add your handling code here:
        txt_idcardOwner.setFocusable(true);
        btn_scan3.setIcon(null);
        btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn_scan3.setText("Scan kartu E-KTP");
        lbl_batal3.setVisible(true);
        txt_idcardOwner.requestFocus();
        btn_rescan3.setVisible(false);
        id_pegawai=old_id_owner;
        btn_akunOwner(false);
    }//GEN-LAST:event_btn_rescan3MouseClicked

    private void txt_namaOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaOwnerKeyReleased
        // TODO add your handling code here:
        if(txt_namaOwner.getText().isEmpty()){btn_akunOwner(false);}
        else{
            String nama = txt_namaOwner.getText();
            try{
                String sql = "select * from pegawai where nama = '"+nama+"' and nama != '"+old_nama_owner+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_namaWarnOwner.setText("Nama telah digunakan");
                    btn_akunOwner(false);
                }else{
                    lbl_namaWarnOwner.setText("");
                    btn_akunOwner(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_namaOwnerKeyReleased

    private void txt_emailOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_emailOwnerKeyReleased
        // TODO add your handling code here:
        if(!txt_emailOwner.getText().contains("@")||!txt_emailOwner.getText().contains(".")){
            lbl_emailWarnOwner.setText("Masukkan email dengan tepat");
            btn_akunOwner(false);
        }
        else if(txt_emailOwner.getText().isEmpty()){btn_akunOwner(false);}
        else {
            btn_akunOwner(true);
            lbl_emailWarnOwner.setText("");
        }
    }//GEN-LAST:event_txt_emailOwnerKeyReleased

    private void txt_noWaOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_noWaOwnerKeyReleased
        // TODO add your handling code here:
        if(txt_noWaOwner.getText().isEmpty()){btn_akunOwner(false);}
        else {
            btn_akunOwner(true);
        }
    }//GEN-LAST:event_txt_noWaOwnerKeyReleased

    private void txt_alamatOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alamatOwnerKeyReleased
        // TODO add your handling code here:
        if(txt_alamatOwner.getText().isEmpty()){btn_akunOwner(false);}
        else {
            btn_akunOwner(true);
        }
    }//GEN-LAST:event_txt_alamatOwnerKeyReleased

    private void txt_usernameOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_usernameOwnerKeyReleased
        // TODO add your handling code here:
        if(txt_usernameOwner.getText().isEmpty()){btn_akunOwner(false);}
        else{
            String username=txt_usernameOwner.getText();
            try{
                String sql = "select * from pegawai where username = '"+username+"' and username !='"+old_username+"'";
                Connection con = (Connection)koneksi.configDB();
                java.sql.Statement stm = con.createStatement();
                java.sql.ResultSet res = stm.executeQuery(sql);
                if(res.next()){
                    lbl_userWarnOwner.setText("Username telah digunakan");
                    btn_akunOwner(false);
                    
                }else{
                    lbl_userWarnOwner.setText("");
                    btn_akunOwner(true);
                }
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_usernameOwnerKeyReleased

    private void txt_passOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_passOwnerKeyReleased
        // TODO add your handling code here:
        if(!txt_confPassOwner.getText().equals(txt_passOwner.getText())){
            lbl_passWarnOwner.setText("Harus sama");
            btn_akunOwner(false);
        }else{
            lbl_passWarnOwner.setText("");
            btn_akunOwner(true);
        }
    }//GEN-LAST:event_txt_passOwnerKeyReleased

    private void txt_confPassOwnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_confPassOwnerKeyReleased
        // TODO add your handling code here:
        if(!txt_confPassOwner.getText().equals(txt_passOwner.getText())){
            lbl_passWarnOwner.setText("Harus sama");
            btn_akunOwner(false);
        }else{
            lbl_passWarnOwner.setText("");
            btn_akunOwner(true);
        }
    }//GEN-LAST:event_txt_confPassOwnerKeyReleased

    private void btn_scan3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_scan3MouseClicked
        // TODO add your handling code here:
        if(old_id_owner.length()==3){
            txt_idcardOwner.setFocusable(true);
            btn_scan3.setIcon(null);
            btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
            btn_scan3.setText("Scan kartu E-KTP");
            lbl_batal3.setVisible(true);
            txt_idcardOwner.requestFocus();
            btn_akunOwner(false);
        }else{
            
        }
    }//GEN-LAST:event_btn_scan3MouseClicked

    private void lbl_batal3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_batal3MouseClicked
        // TODO add your handling code here:
        txt_idcardOwner.setText("");
        txt_idcardOwner.setFocusable(false);
        btn_scan3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/asset/Dashboard/karyawan/btn_scanrfid.png")));
        btn_scan3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_scan3.setText("Klik untuk scan E-KTP");
        lbl_batal3.setVisible(false);
        btn_akunOwner(true);
    }//GEN-LAST:event_lbl_batal3MouseClicked

    private void btn_simpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_simpanMouseClicked
        // TODO add your handling code here:
        if (!confirm){
            JOptionPane.showMessageDialog(null, "Data tidak terpenuhi!");
        }
        else if (peng_akun){
            JOptionPane.showMessageDialog(null, "Tidak ada perubahan!");
        } else {
            try{
                String sql = "update pegawai set id_pegawai = '"+id_pegawai+"', "
                        + "nama = '"+txt_namaOwner.getText()+"', "
                        + "telepon = '"+txt_noWaOwner.getText()+"', "
                        + "alamat = '"+txt_alamatOwner.getText()+"', "
                        + "email = '"+txt_emailOwner.getText()+"', "
                        + "username = '"+txt_usernameOwner.getText()+"', "
                        + "password = '"+txt_confPassOwner.getText()+"' where id_pegawai = '"+old_id_pegawai+"'";
                
                Statement state = (Statement) koneksi.configDB().createStatement();
                state.executeUpdate(sql);
                state.close();
                JOptionPane.showMessageDialog(null,"Data karyawan berhasil disimpan");
                old_id_owner="";
                id_pegawai="";
                old_username="";
                old_nama="";
                peng_akun = true;
                setProfilToko();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }//GEN-LAST:event_btn_simpanMouseClicked

    private void txt_namaTokoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_namaTokoKeyReleased
        // TODO add your handling code here:
        btn_toko(true);
    }//GEN-LAST:event_txt_namaTokoKeyReleased

    private void txt_alamatTokoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_alamatTokoKeyReleased
        // TODO add your handling code here:
        btn_toko(true);
    }//GEN-LAST:event_txt_alamatTokoKeyReleased

    private void txt_limitUtangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_limitUtangKeyReleased
        // TODO add your handling code here:
        btn_toko(true);
    }//GEN-LAST:event_txt_limitUtangKeyReleased

    private void btn_simpanTokoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_simpanTokoMouseClicked
        // TODO add your handling code here:
        if (!confirm){
            JOptionPane.showMessageDialog(null, "Data tidak terpenuhi!");
        }
        else if (peng_toko){
            JOptionPane.showMessageDialog(null, "Tidak ada perubahan!");
        } else {
            try{
                String sql = "update toko set nama_toko = '"+txt_namaToko.getText()+"', "
                        + "alamat_toko = '"+txt_alamatToko.getText()+"', "
                        + "limit_hutang = '"+txt_limitUtang.getText()+"'";
                
                Statement state = (Statement) koneksi.configDB().createStatement();
                state.executeUpdate(sql);
                state.close();
                JOptionPane.showMessageDialog(null,"Data toko berhasil disimpan");
                peng_toko = true;
                setProfilToko();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }//GEN-LAST:event_btn_simpanTokoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bg_detail;
    private javax.swing.JLabel bg_karyawan;
    private javax.swing.JLabel bg_pengAkun;
    private javax.swing.JLabel bg_pengToko;
    private javax.swing.JLabel bg_ringkasan;
    private javax.swing.JLabel btn_add_Karyawan;
    private javax.swing.JLabel btn_akun;
    private javax.swing.JLabel btn_detail;
    private javax.swing.JLabel btn_hapusKaryawan;
    private javax.swing.JLabel btn_karyawan;
    private javax.swing.JLabel btn_kembaliEdit;
    private javax.swing.JLabel btn_kembaliTambah;
    private javax.swing.JLabel btn_refresh;
    private javax.swing.JLabel btn_rescan;
    private javax.swing.JLabel btn_rescan2;
    private javax.swing.JLabel btn_rescan3;
    private javax.swing.JLabel btn_ringkasan;
    private javax.swing.JLabel btn_scan;
    private javax.swing.JLabel btn_scan2;
    private javax.swing.JLabel btn_scan3;
    private javax.swing.JLabel btn_simpan;
    private javax.swing.JLabel btn_simpanKaryawan;
    private javax.swing.JLabel btn_simpanToko;
    private javax.swing.JLabel btn_tambahKaryawan;
    private javax.swing.JLabel btn_toko;
    private javax.swing.JComboBox<String> cbox_bulan;
    private javax.swing.JComboBox<String> cbox_tahun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jtextareaa;
    private javax.swing.JScrollPane jtextareaaa;
    private javax.swing.JScrollPane jtextareaaa1;
    private javax.swing.JLabel lbl_allLaba;
    private javax.swing.JLabel lbl_allPengeluaran;
    private javax.swing.JLabel lbl_allRugi;
    private javax.swing.JLabel lbl_bandingLabaBulanLalu;
    private javax.swing.JLabel lbl_barangTerlaku1;
    private javax.swing.JLabel lbl_barangTerlaku2;
    private javax.swing.JLabel lbl_barangTerlaku3;
    private javax.swing.JLabel lbl_batal;
    private javax.swing.JLabel lbl_batal2;
    private javax.swing.JLabel lbl_batal3;
    private javax.swing.JLabel lbl_displayNamaPemilik;
    private javax.swing.JLabel lbl_displayNamaToko;
    private javax.swing.JLabel lbl_emailWarnEditKaryawan;
    private javax.swing.JLabel lbl_emailWarnOwner;
    private javax.swing.JLabel lbl_emailWarnTambahKaryawan;
    private javax.swing.JLabel lbl_laba;
    private javax.swing.JLabel lbl_labaBulanIni;
    private javax.swing.JLabel lbl_labaHariIni;
    private javax.swing.JLabel lbl_namaWarnEditKaryawan;
    private javax.swing.JLabel lbl_namaWarnOwner;
    private javax.swing.JLabel lbl_namaWarnTambahKaryawan;
    private javax.swing.JLabel lbl_passWarnEditKaryawan;
    private javax.swing.JLabel lbl_passWarnOwner;
    private javax.swing.JLabel lbl_passWarnTambahKaryawan;
    private javax.swing.JLabel lbl_pemasukan;
    private javax.swing.JLabel lbl_pengeluaran;
    private javax.swing.JLabel lbl_rugi;
    private javax.swing.JLabel lbl_terjual1;
    private javax.swing.JLabel lbl_terjual2;
    private javax.swing.JLabel lbl_terjual3;
    private javax.swing.JLabel lbl_tgl;
    private javax.swing.JLabel lbl_transaksiHariIni;
    private javax.swing.JLabel lbl_userWarnEditKaryawan;
    private javax.swing.JLabel lbl_userWarnOwner;
    private javax.swing.JLabel lbl_userWarnTambahKaryawan;
    private javax.swing.JLabel lbl_utang;
    private javax.swing.JPanel pn_detail;
    private javax.swing.JPanel pn_editKaryawan;
    private javax.swing.JPanel pn_grafik;
    private javax.swing.JPanel pn_karyawan;
    private javax.swing.JPanel pn_pengaturanAkun;
    private javax.swing.JPanel pn_pengaturanToko;
    private javax.swing.JPanel pn_ringkasan;
    private javax.swing.JPanel pn_sub;
    private javax.swing.JPanel pn_tambahKaryawan;
    private javax.swing.JTable tbl_karyawan;
    private javax.swing.JTable tbl_kerugian;
    private javax.swing.JTable tbl_pemasukan;
    private javax.swing.JTable tbl_pengeluaran;
    private javax.swing.JTable tbl_restok;
    private javax.swing.JTextField txt_add_idcard;
    private javax.swing.JTextArea txt_alamatOwner;
    private javax.swing.JTextArea txt_alamatToko;
    private javax.swing.JPasswordField txt_confPassOwner;
    private javax.swing.JTextArea txt_editAlamatKaryawan;
    private javax.swing.JPasswordField txt_editConfPassKaryawan;
    private javax.swing.JTextField txt_editEmailKaryawan;
    private javax.swing.JTextField txt_editNamaKaryawan;
    private javax.swing.JTextField txt_editNoWaKaryawan;
    private javax.swing.JPasswordField txt_editPassKaryawan;
    private javax.swing.JTextField txt_editUsernameKaryawan;
    private javax.swing.JTextField txt_emailOwner;
    private javax.swing.JTextField txt_idcard;
    private javax.swing.JTextField txt_idcardOwner;
    private javax.swing.JTextField txt_limitUtang;
    private javax.swing.JTextField txt_namaOwner;
    private javax.swing.JTextField txt_namaToko;
    private javax.swing.JTextField txt_noWaOwner;
    private javax.swing.JPasswordField txt_passOwner;
    private javax.swing.JTextArea txt_tambahAlamatKaryawan;
    private javax.swing.JPasswordField txt_tambahConfPassKaryawan;
    private javax.swing.JTextField txt_tambahEmailKaryawan;
    private javax.swing.JTextField txt_tambahNamaKaryawan;
    private javax.swing.JTextField txt_tambahNoWaKaryawan;
    private javax.swing.JPasswordField txt_tambahPassKaryawan;
    private javax.swing.JTextField txt_tambahUsernameKaryawan;
    private javax.swing.JTextField txt_usernameOwner;
    // End of variables declaration//GEN-END:variables
}
