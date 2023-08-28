/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Hakimfrh
 */
public class Whatsapp {
    
    public Whatsapp(){
        
    }
    
    String send(String address, String text){
        
        String result="success";
        
            try {
                  String idInstance = "1101817338";
                  String apiTokenInstance = "267240452ba84cbcaaff1488012901e4b1034acb01e044dcb1";
                  String url = "https://api.green-api.com/waInstance" + idInstance + "/sendMessage/" + apiTokenInstance;

                  String payload = "{\r\n\t\"chatId\": \"" +address +"@c.us\",\r\n\t\"message\": \""+text +"\"\r\n}";

                  URL obj = new URL(url);
                  HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                  con.setRequestMethod("POST");
                  con.setRequestProperty("Content-Type", "application/json");
                  con.setDoOutput(true);

                  OutputStream os = con.getOutputStream();
                  os.write(payload.getBytes());
                  os.flush();
                  os.close();

                  int responseCode = con.getResponseCode();

                  BufferedReader in = new BufferedReader(
                          new InputStreamReader(con.getInputStream()));
                  String inputLine;
                  StringBuffer response = new StringBuffer();
                  while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                  }
                  in.close();

                  System.out.println(response.toString());
            } catch (Exception e) {
                System.out.println(e);
                result=e.toString();
            }
        return result;
    }
}
