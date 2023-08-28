/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author Hakimfrh
 */
public class Session {
        private String id="";
        private boolean owner = false;
        private String nama = "";
        
    public Session() {
        
        try {
            FileReader fileReader = new FileReader("src/app/session");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                id=line;
            }

            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        try {
            String sql = "SELECT * FROM pegawai WHERE id_pegawai ='" +id +"'";
            java.sql.Connection conn= (Connection) koneksi.configDB();
            java.sql.PreparedStatement pst=conn.prepareStatement(sql);
            java.sql.ResultSet rs = pst.executeQuery(sql);
            if(rs.next()) {
                owner = rs.getString("role").equals("owner");
                nama = rs.getString("nama");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
        
    boolean isOwner(){
        return owner;
    }
    
    String getId(){
        return id;
    }
    String getName(){
        return nama;
    }

}
