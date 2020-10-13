
package dbautocommitfalse;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

// Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/51.html

public class DBAutoCommitFalse {


    public static void main(String[] args) {
        
        String url = "rmi://localhost:1099";
        String jndiName = "MyDataSource";
        
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
        props.setProperty("java.naming.provider.url", url);
        
        try {
            
            InitialContext initialContext = new InitialContext(props);
            MysqlDataSource dataSource = (MysqlDataSource)initialContext.lookup(jndiName);
            dataSource.setUser("root");
            dataSource.setPassword("");
            
            Connection con = dataSource.getConnection();
            // Auto-Commit ausschalten
            con.setAutoCommit(false);
            
            Statement stmt = con.createStatement();
            
            // try-catch-Block falls Fehler auftauchen
            try {
                stmt.executeUpdate("INSERT INTO tabelle1 (vorname, nachname) VALUES('Sabine', 'Ulwerson')");
                stmt.executeUpdate("DELETE FROM tabelle1 WHERE nachname = 'Zitterbacke'");
                stmt.executeUpdate("UPDATE tabelle1 SET vorname = 'Liana' WHERE vorname = 'Amelia' AND nachname = 'Lamida'");
                // nun explizit den Commit auf der Connection veranlassen
                con.commit();
            } catch (SQLException sqlEx) {
                System.out.println("Es ist ein Fehler bei der Ausführung der Anweisungen aufgetreten:");
                System.out.println("Ein Rollback wird durchgeführt.");
                // Rollback bei Fehler
                con.rollback();
            }
            
            // Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/52.html
            // Isolationsebene:
            
            DatabaseMetaData meta = con.getMetaData();
            System.out.print("Isoltationsebene: ");
            switch(meta.getDefaultTransactionIsolation()) {
                case Connection.TRANSACTION_NONE:
                  System.out.println("none"); break;
                case Connection.TRANSACTION_READ_UNCOMMITTED:
                  System.out.println("read uncommitted"); break;
                case Connection.TRANSACTION_READ_COMMITTED:
                  System.out.println("read committed"); break;
                case Connection.TRANSACTION_REPEATABLE_READ:
                  System.out.println("repeatable_read"); break;
                case Connection.TRANSACTION_SERIALIZABLE:
                  System.out.println("serializable"); break;
            }
            
        } catch (NamingException nmEx) {
            nmEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
    }
    
}
