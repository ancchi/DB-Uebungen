package smallnameserver;

// Quelle: https://www.straub.as/java/jdbc/datasource.html

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;



public class SmallNameServer {

    /**
     * Ein DataSourceObject wird normalerweise von einem Namensdienst geliefert. Das Beispiel "DBVerbindungMitDataSource"
     * ist ein sehr einfaches Beispiel und wird in der Praxis nicht so verwendet.
     * Zur Demonstration, wie man ein DataSourceObject von einem Nameserver erhält, hier ein kleiner Namensdienst, der das
     * gewünschte Objekt liefern wird. Die Klasse "LocateRegistry" macht das möglich.
     * 
     * Das zugehörige Programm, mit dem das Objekt vom Namensdienst abgefragt wird, heisst: "DBDataSourceWithNameServer"
     */
    
    public static void main(String[] args) {
        
        try {
            int port = 1099; // (Standard)
            LocateRegistry.createRegistry(port);
            System.out.println("Nameserver started");
            // erzeugt und exportiert ein Register auf dem (in diesem Fall) lokalen Host, das Anfragen auf dem spezifizierten Port
            // entgegennimmt (wirft java.rmi.RemoteException)
            
            // Eigenschaften für die Initialisierung des Kontexts festlegen
            Properties props = new Properties();
            props.put("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
            props.put("java.naming.provider.url", "rmi://localhost:"+port); // lokaler Namensserver
            // die letzte Zeile kann auch weggelassen werden, wenn der defuault-Port verwendet wird
            
            // zu liefernde DataSource konfigurieren (MysqlDataSource für die eigene DB)
            // das wird dann das "vorkonfigurierte" DataSource-Objekt, welches beim Call übergeben wird
            MysqlDataSource myDBDataSource = new MysqlDataSource();
            String server = "localhost";
            int mysqlPort = 3306; //default
            String databaseName = "probe";
            myDBDataSource.setServerName(server);
            myDBDataSource.setPort(mysqlPort);
            myDBDataSource.setDatabaseName(databaseName);
            
            
            // Setzen eine Name-Value-Paares (JNDI naming) für den Kontext des Nameservers
            Context initialNamingContext = new InitialContext(props); // wirft NamingException
            // das Objekt MysqlDataSource myDBDataSource mit einem Key "MyDataSource" an den Kontext binden
            initialNamingContext.bind("MyDataSource", myDBDataSource); // wirft NamingException // Benamsung ändern????!!!!
            System.out.println("Context created, naming completed.");
            
        } catch (RemoteException rmiEx) {
            rmiEx.printStackTrace();
        } catch (NamingException nmEx) {
            nmEx.printStackTrace();
        }
        
    }
    
}
