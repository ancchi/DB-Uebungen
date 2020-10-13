package dbstapelaktualisierung;

// QUELLE: http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_24_007.htm

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;

public class DBStapelaktualisierung {
    
    
   
    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Keine Treiber-Klasse vorhanden.");
            return;
        }
        
        Connection con = null;
        
        try {      
            
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/probe", "root", "");

        /* einfaches executeUpdate */
        Statement stmtUpdate = con.createStatement();
        stmtUpdate.executeUpdate("UPDATE tabelle1 SET vorname = 'Hanna' WHERE vorname = 'Hanni' AND nachname = 'Alder'");

        stmtUpdate.close();
        
        /* mehrere Updates mit executeBatch() auf _einem_ Statement */
        int[] updateCounts = null;
        try {
            // der AutoCommit der Connection wird auf false gesetzt, damit nicht jede SQL-Anweisung als einzelne Transaktion
            // gewertet wird; deswegen muss allerdings im Anschluss bei einem Fehler ein Rollback gemacht werden***
            con.setAutoCommit(false);
            
            Statement stmtBatch = con.createStatement();
            stmtBatch.addBatch("INSERT INTO tabelle1 (vorname, nachname) VALUES ('Anastasia', 'Elinquento')");
            stmtBatch.addBatch("DELETE FROM tabelle1 WHERE vorname = 'Tito' AND nachname = 'Jackson'");
            stmtBatch.addBatch("UPDATE tabelle1 SET vorname = 'Sanni' WHERE vorname = 'Sunny' AND nachname = 'Flower'");
            updateCounts = stmtBatch.executeBatch();
            
            // updateCounts enthält verschiedene Werte für verschiedene SQL-Befehle
            for (int i = 0; i < updateCounts.length; i++) {
                System.out.println("updateCount: " + updateCounts[i]);
            }
            
            stmtBatch.close();
        } catch ( BatchUpdateException bUpEx) {
            /* Behandeln */
            
            // ***da nicht jede Transaktion einzeln ausgeführt wird, muss bei einem Fehler ein Rollback gemacht werden
            con.rollback();
        } catch ( SQLException sqlEx) {
            for ( ; sqlEx != null; sqlEx = sqlEx.getNextException() ) {
                // textuelle Beschreibung des Fehlers
                System.err.println( "Message:    " + sqlEx.getMessage() );
                // Fehlercode mi SQL-Status - datenbankunabhängig
                System.err.println( "SQL State:  " + sqlEx.getSQLState() );
                // Fehlercode vom JDBC-Treiber - datenbankabhängig
                System.err.println( "Error Code: " + sqlEx.getErrorCode() );
            }
        }

        // Spielerei für das INSERT, damit der Name nicht 10.000 Mal in der Tabelle landet:
        // Lösche den Namen 'Anastasia', 'Elinquento' einmal, ordne nach dem Timestamp
        // auch, wenn es das jetzt scheinbar tut, sollte es nicht verwendet werden, um doppelte Einträge in der DB zu verhindern!
        Statement stmtDelete = con.createStatement();
        stmtDelete.executeUpdate("DELETE FROM tabelle1 WHERE vorname = 'Anastasia' AND nachname = 'Elinquento' " +
                "ORDER BY zeitstempel LIMIT 1");
        
        stmtDelete.close(); 
        
        // executeQuery
        Statement stmtQuery = con.createStatement();
        ResultSet rsQuery = stmtQuery.executeQuery("SELECT * FROM tabelle1");
        
       
        while (rsQuery.next()) {
            System.out.printf("%s, %s, %s, %s%n", rsQuery.getInt("id"), rsQuery.getString("vorname"), rsQuery.getString("nachname"), rsQuery.getTimestamp("zeitstempel"));
        }
        
        // rsQuery.close(); ist nicht notwendig, da das ResultSet durch dessen Statement mitgeschlossen wird
        stmtQuery.close();
        

       } catch (SQLException ex) {
           ex.printStackTrace();
       } finally {
            if (con != null) {
                /* con.close() schliesst auch gleichzeitig alle Statements; da innerhalb dieser Klasse mehrere
                    Statements vewendet wurden, schliesse ich diese jedoch jeweils einzeln */
                try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
    
}
