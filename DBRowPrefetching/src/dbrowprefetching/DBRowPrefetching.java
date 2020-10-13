
package dbrowprefetching;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

// Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/46.html

/*
    - beim Row Prefetching werden nicht alle Abfrageergebnisse sofort zum Client übertragen, sondern nach
      Bedarf vom DBS angefordert
    - damit enthält ein ResultSet nicht sofort nach Ausführung alle Daten, sondern stellt lediglich den
      Mechanismus zum Abrufen der Daten bereit
    - mit diesem Meachanismus wird nicht jede Zeile einzeln angefordert, sondern in größeren "Portionen"
    - diese werden im Puffer es Clients zwischengespeichert und verarbeitet; erst dann fordert das ResultSet
      die nächste festgelegte Anzahl (Abfragegröße) an Datensätzen an 
*/

public class DBRowPrefetching {

    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            Statement stmt = con.createStatement();
            
            // Anzahl der Zeilen, die in einem Rutsh abgerufen werden sollen über das Statement
            stmt.setFetchSize(5);
            
            ResultSet rs = stmt.executeQuery("SELECT * from tabelle1");
            
            // Anzahl der Zeilen, die über das ResultSet abgefragt werden sollen -> überschreibt die über das
            // Statement gesetzte FetchSize
            rs.setFetchSize(3);
            
            while(rs.next()) {
                System.out.printf("%s, %s, %s%n", rs.getString("vorname"), rs.getString("nachname"), rs.getDate("zeitstempel"));
            }
            
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
        
    }
    
}
