package dbtransaktionen;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DBTransaktionen {
    
    // QUELLE: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/51.html
    // sinnvoll, um sicherzustellen, dass mehrere SQL-Befehle nur dann ausgeführt werden, wenn _alle_ erfolgreich sind

    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        Connection con = null;
        
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            // Auto-Commit deaktivieren
            con.setAutoCommit(false);
            Statement stmt = con.createStatement();
            
            try {
                stmt.executeUpdate("INSERT INTO tabelle1 (vorname, nachname) VALUES ( 'Edward', 'mit den Scherenhänden' )");
                stmt.executeUpdate("UPDATE tabelle1 SET nachname = 'Blume' WHERE nachname = 'Holz'");
                stmt.executeUpdate("DELETE FROM tabelle1 WHERE nachname = 'Morgenstern'");
                stmt.executeQuery("SELECT vorname, nachname, zeitstempel FROM tabelle1;");
                // wenn kein Fehler aufgetreten ist, Änderungen committen (aktuelle Transaktion wird beendet)
                // nachdem commit() ausgeführt wurde, wird automatisch eine neue Transaktion begonnen
                con.commit();
                
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    System.out.printf("%s, %s, %s%n", rs.getString("vorname"), rs.getString("nachname"), rs.getDate("zeitstempel"));
                }
                
                // Isolations-Ebenen zum Festlegen, inwiefern in einer Transaktion durchgeführte Änderungen für
                // andere Clients sichtbar sind
                // Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/52.html
                // https://docs.oracle.com/javase/7/docs/api/index.html?overview-summary.html
                
                // Prüfen, welche Isolationsebenen von der aktuellen DB unterstützt werden
                DatabaseMetaData metaData = con.getMetaData();
                
                System.out.print("Default Isolationsebene der Datenbank: ");
                switch (metaData.getDefaultTransactionIsolation()) {
                    case Connection.TRANSACTION_NONE:
                        System.out.println("none"); break;
                    case Connection.TRANSACTION_READ_UNCOMMITTED:
                        System.out.println("read umcomitted"); break;
                    case Connection.TRANSACTION_READ_COMMITTED:
                        System.out.println("read committed"); break;
                    case Connection.TRANSACTION_REPEATABLE_READ:
                        System.out.println("repeatable read"); break;
                    case Connection.TRANSACTION_SERIALIZABLE:
                        System.out.println("serializable"); break;
                }
                
            } catch (SQLException ex) {
                // Rollback aller SQL-Anweisungen, falls ein Fehler auftritt
                con.rollback();
                System.out.println("Es ist ein Fehler aufgetreten. Alle Anweisungen wurden zurückgesetzt.");
            }
            
        } catch (SQLException ex) {
            System.out.println("Es ist ein SQL-Fehler aufgetreten.");
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    } 
}
