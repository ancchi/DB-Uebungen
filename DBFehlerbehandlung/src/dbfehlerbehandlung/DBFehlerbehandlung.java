package dbfehlerbehandlung;

import com.sun.xml.internal.ws.util.xml.CDATA;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class DBFehlerbehandlung {
    
    String test = "maus";
    
   
    // Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/14.html
    public static void main(String[] args) {
       
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
        }
        
        try {
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            Statement stmt = con.createStatement();
            
            stmt.executeQuery("SELECT * FROM tabelleX");
            
            // Warnungen:
            SQLWarning warning = stmt.getWarnings();
            
            // gleiche Methoden wie bei Exceptions:
            while (warning != null) {
                System.out.println("Message Warnung: " + warning.getMessage());
                System.out.println("ANSI-92 Code: " + warning.getSQLState());
                System.out.println("DB-Code: " + warning.getErrorCode());
                //warning.printStackTrace(); 
                warning = warning.getNextWarning();
            }
            
          // Exceptions:  
        } catch (SQLException ex) {
            do {
                System.out.println("Message: " + ex.getMessage());
                // empfohlen bei DBsystemunabhängigen Anwendungen
                System.out.println("ANSI-92 Code: " + ex.getSQLState());
                System.out.println("DB-Code: " + ex.getErrorCode());
                // ex.printStackTrace();
                // Nächste Exception abrufen
                ex = ex.getNextException();
                // ex ist null, wenn keine Exception mehr vorhanden
            } while (ex != null);
        }
        
    }
    
}
