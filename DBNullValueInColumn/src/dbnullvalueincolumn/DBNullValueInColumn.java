package dbnullvalueincolumn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/32.html

public class DBNullValueInColumn {

    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/probe", "root", "");
            
            /**
             * Prüfung, ob in einer Spalte ein NULL-Wert ist
             * normale Abfrage gibt 0 oder false zurück, wenn NULL
             * -> 1. Prüfung mit ResultSet.wasNull() möglich
             * -> 2. Prüfung mit ResultSet.getObject("spaltenname"/"spaltenindex") - gibt für jeden Datentyp null zurück, wenn NULL-Wert vorhanden
             */
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabelle1");
            
            while(rs.next()) {
                System.out.print("Für " + rs.getString("vorname") + " " + rs.getString("nachname"));
                // die Spalte abrufen, die auf NULL geprüft werden soll
                rs.getBlob("avatar");
                // danach kann auf ResultSet wasNull() aufgerufen werden und die vorher abgerufene Spalte wird auf NULL geprüft
                if (rs.wasNull()) {
                    System.out.println(" ist kein Avatar vorhanden.");
                } else {
                    System.out.println(" ist ein Bild hinterlegt.");
                }
                System.out.println(""
                        + "Wert mit getObject(): " + rs.getObject("avatar"));
            }
            
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
    }
    
}
