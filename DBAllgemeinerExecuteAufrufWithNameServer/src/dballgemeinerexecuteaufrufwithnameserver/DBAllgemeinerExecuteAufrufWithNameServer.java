package dballgemeinerexecuteaufrufwithnameserver;

// Hinweis: damit das Programm funktioniert, muss der SmallNameServer laufen
// Quelle: https://www.straub.as/java/jdbc/execute.html
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;


public class DBAllgemeinerExecuteAufrufWithNameServer {

  
    public static void main(String[] args) {
        
        try {
    //        int port = 1099; // Standard-Port
    //        String url = "rmi://localhost:" + port;
            String url = "rmi://localhost"; // bei Verwendung des Standard-Ports reicht das aus
            String jndiName = "MyDataSource"; // JNDI-Name für die Datenbankverbindung

            Properties props = new Properties();
            props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
            props.setProperty("java.naming.provider.url", url);

            Context initialContext = new InitialContext(props);
            Object objRef = initialContext.lookup(jndiName);
            
            // Verbindung von NameServer geholt
            // PortableRemoteObject.narrow([..]) macht aus der Objekt-Referenz ein Object des angegebenen Typs
            MysqlDataSource dataSource = (MysqlDataSource)PortableRemoteObject.narrow(objRef, MysqlDataSource.class);
            // DB und User hinzufügen
            // String databaseName = "probe";
            String userName = "root";
            String password = "";
            
            // um mehrere, durch Semikolon getrennte SQL-Anfragen zu erlauben, muss "allowMultiQueries=true" hinter dem
            // Datenbanknamen angegeben werden oder die Methode setAllowMultiQueries(true) auf der dataSource aufgerufen werden :)
            // Quelle: https://stackoverflow.com/questions/10797794/multiple-queries-executed-in-java-in-single-statement
            dataSource.setAllowMultiQueries(true);
            dataSource.setUser(userName);
            dataSource.setPassword(password);
            
            Connection con = dataSource.getConnection();
            // Quelle: http://www.java2s.com/example/java-api/java/sql/statement/getmoreresults-0-0.html
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
            /**
             * Das Szenario geht davon aus, dass über eine GUI verschiedene Anfragen an die Datenbank
             * gestellt werden - das kann man mit Statement.execute(String sql) machen.
             * Beim Ergebnis muss nun sichergestellt werden, dass es richtig weiterverarbeitet wird - entweder
             * als ResultSet oder als UpdateCount
             * 
             * Hinweis: durch den Zusatz "allowMultiQueries=true" hinter dem DB-Namen ist es möglich, mehrere
             * Anweisungen, mit einem Semikolon getrennt, einzugeben
             * diese werden dann in der Klasse QueryCoWorker mit stmt.getMoreResults() nacheinander abgefragt
             */
            while (true) {
                System.out.println("sql>");
                System.out.flush();
                String sql = in.readLine();
                
                if ((sql == null) || sql.equals("quit"))
                    break;
                
                QueryCoWorker queryCoWorker = new QueryCoWorker(con, sql);
            }
            
            con.close();
            
        } catch (NamingException nmEx) {
            nmEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }
    
}
