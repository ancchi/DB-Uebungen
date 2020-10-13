package dbdatasourcewithnameserver;

// Quelle: https://www.straub.as/java/jdbc/datasource.html

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class DBDataSourceWithNameServer {
    
    // Hinweis: damit dieses Programm läuft, muss SmallNameServer gestartet sein

    public static void main(String[] args) {
        try {
            int port = 1099;
            String url = "rmi://localhost:" + port; // URL zum Nameserver auf Port 1099 (Standard-Port)
            String jndiName = "MyDataSource"; // Name des erstellten entfernten Objekts
            
            
            Properties props = new Properties();
            // Properties mit Key-Value-Paar als String
            // props.put("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
            // es wird schwer davon abgeraten "put(Object key, Object value)" von der Oberklasse Hashtable zu verwenden, weil dort
            // auch Werte eingegeben werden können, die keine Strings sind
            // stattdessen sollte "Properties.setProperty(String key, String value)" verwendet werden, welche "put" mit Strings aufruft
            props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
            props.setProperty("java.naming.provider.url", url);
            
            /**
             * Bedeutung von "java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory" und
             * "java.naming.provider.url", "rmi://localhost:1099":
             * 
             * "java.naming.factory.initial":
             * - der Name der Umgebungsvariable um die "Anfangs-Kontext-Fabrik" zu spezifizieren
             * - der Wert dieser Eigenschaft sollte der voll qualifizierte Klassenname der "Factory class" sein, welche den initialen
             *   Kontext herstellen wird
             * 
             * "com.sun.jndi.rmi.registry.RegistryContextFactory":
             * - voll qualifizierter Name der Klasse, die den initialen Kontext erstellt
             * 
             * "java.naming.provider.url":
             * - der Name der Umgebungsvariable, um den Ort des JBoss JNDI service providers zu spezifizieren, den der Client
             *   verwenden wird
             * 
             * "rmi://localhost:1099":
             * - Ort des JBoss JNDI service providers
             * 
             * // Quelle: https://docs.jboss.org/jbossas/docs/Server_Configuration_Guide/4/html/The_Naming_InitialContext_Factories-The_standard_naming_context_factory.html
             * 
             */
            
            // Properties verwenden, um Verbindung zum SmallNameServer herzustellen
            Context initialContext = new InitialContext(props); // wirft NamingException
            
            // Interface javax.naming.Context repräsentiert einen Namenskontext, welches ein Set von Name-zu-Objekt-Bindings
            // besitzt; besitzt Methoden, um diese Bindings zu prüfen und zu updaten
            // Klasse javax.naming.InitialContext ist der Start-Kontext um Namens-Operationen durchzuführen; stellt den Startpunkt
            // für die Namensauflösung zur Verfügung
            
            // Referenz für Objekt holen, dass dem "jndiName = 'MyDataSource'" zugeordnet ist - das ist ein MysqlDataSource-Objekt,
            // das zuvor im SmallNameServer an den initialen Kontext gebunden wurde
            Object objRef = initialContext.lookup(jndiName);
            System.out.println("Referenz = " + objRef.getClass().getName());
            
            // das MysqlDataSource-Objekt muss nun direkt als MysqlDataSource gespeichert werden
            // um dem Compiler das deutlich mitzuteilen, verwenden wir ein Type-Casting + PortableRemoteObject.narrow()
            // PortableRemoteObject.narrow() macht folgendes:
            // - PortableRemoteObject ist die einzige Klasse im package javax.rmi
            // -> ist ein Server-Objekt (Import und Export) - in diesem Falle für den Import verwendet
            // - die Methode narrow() nimmt eine Objekt-Referenz auf und versucht sie so einzugrenzen, dass sie mit der gegebenen
            // Schnittstelle übereinstimmt, so dass sie bei Erfolg ein Objekt des angegebenen Typs erschaffen hat
            // schlägt das fehl, wird eine Exception geworfen
            MysqlDataSource dataSource = (MysqlDataSource)PortableRemoteObject.narrow(objRef, MysqlDataSource.class);
            System.out.println("DataSource received.");
            System.out.println("DataSource-Klassenname = " + dataSource.getClass().getName());

            // wo kommt com.mysql.jdbc.jdbc2.optional.MysqlDataSource her?
            // -> es ist ein Package, das direkt java.lang.Object untersteht und aus der Library von MySQL-JDBC stammt
            // http://www.docjar.com/docs/api/com/mysql/jdbc/jdbc2/optional/MysqlDataSource.html
            
            
            // die Benutzerdaten für die Datenbank
            String username = "root";
            String password = "";
            
            // dataSource hat bereits die URL, den Port und den Datenbanknamen der Datenbank vom SmallNameServer erhalten
            // nun müssen noch die restlichen (User-)Daten zur Anmeldung angegeben werden
            dataSource.setUser(username);
            dataSource.setPassword(password);
            
            System.out.println("Databasename: " + dataSource.getDatabaseName()); // (nur so zum Test)
            
            // ganz normal die Connection aufbauen
            Connection con = dataSource.getConnection(); // wirft SQLException
            System.out.println("Connection established.");
            
            // do something with the connection of the database
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT * FROM tabelle1 WHERE nachname = 'Simpson'");
            
            while (resultSet.next()) {
                System.out.printf("%s, %s, %s%n", resultSet.getString("vorname"), resultSet.getString("nachname"),
                        resultSet.getDate("zeitstempel"));
            }
            
            // Connection schließen
            con.close();
            System.out.println("Connection closed.");
            
        } catch (NamingException nmEx) {
            System.out.println("NamingExceptions:");
            nmEx.printStackTrace();
        } catch (SQLException sqlEx) {
            System.out.println("SQLExceptions:");
            sqlEx.printStackTrace();
        }
    }
    
}
