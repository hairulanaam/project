/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JOptionPane;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Hakimfrh
 */
public class test {

    /**
     * @param args the command line arguments
     */
    
    static void test_session(){
        Session session = new Session();
        
        // TODO code application logic here
/*Write The File
        try {
            FileWriter fileWriter = new FileWriter("src/app/session");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("testhjegfusegfiusgfiuesf");
            bufferedWriter.newLine();

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
 //Read The File
        try {
            FileReader fileReader = new FileReader("src/app/session");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
*/
        System.out.println("==============================");
        System.out.println("id    : " +session.getId());
        System.out.println("name  : " +session.getName());
        System.out.println("Owner : " +session.isOwner());
    }
    
    static void test_whatsapp(){
        Whatsapp whatsapp = new Whatsapp();
        
        System.out.println(whatsapp.send("6285236765510", "ngetestbang"));
    }
    static void compare_Date(){
        java.util.Date date = new java.util.Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        try {
            
            Date tenggat = f.parse("2023-05-10");
            Date tanggal = f.parse(f.format(date));
            System.out.println(tenggat);
            System.out.println(tanggal);
            
            System.out.println(tanggal.compareTo(tenggat));
        } catch (ParseException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex);
        }
        
    }
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
            JasperViewer.viewReport(JPrint,false);  //view report
            //JasperPrintManager.printReport(JPrint, false);  // print report
        }catch (Exception e){
            System.out.println(e);
        }
    }
    
    static String kode_OTP(){
        Random rand = new Random();
        String kode = String.format("%06d",rand.nextInt(999999));
        return kode;
    }
    static void timer(){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            int remainingSeconds = 5 * 60;

            public void run() {
                int minutes = remainingSeconds / 60;
                int seconds = remainingSeconds % 60;
                System.out.printf("%02d:%02d\n", minutes, seconds);

                if (remainingSeconds > 0) {
                    remainingSeconds--;
                } else {
                    System.out.println("Timer expired!");
                    executorService.shutdown(); // Terminate the executor service
                }
            }
        };

        executorService.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }
    public static void main(String[] args) {
        //test_session();
        //test_whatsapp();
        //compare_Date();
        print("HM10230001");
        //System.out.println(kode_OTP());
       // timer();
       // System.out.println("adawdawd");
    }
    
}

