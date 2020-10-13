package datenbankeinfach;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class DatenbankEinfach {

    /* Quelle: https://www.youtube.com/watch?v=e1PBJeZ1g5c */
    
    private static Connection con = null;
    private static ResultSet resultSet = null;
    private static Statement stmt = null;
    
    
    public static void main(String[] args) {
        
          try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Keine Treiber-Klasse vorhanden.");
            return;
        }
       
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            stmt = con.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM tabelle1");
            
            // Abruf der Connection auf dem Statement
            System.out.println("Connection aus Statement-Sicht: " + stmt.getConnection());
            
            while(resultSet.next()) {
                System.out.println(resultSet.getString("vorname") + " " + resultSet.getString("nachname"));
            }
            
            
        } catch(SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        // Aufzählung aller Treiber
        for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
            System.out.println(drivers.nextElement().getClass().getName());
        }
    }
    
}
