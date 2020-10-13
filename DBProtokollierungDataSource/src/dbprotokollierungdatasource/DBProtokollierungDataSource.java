package dbprotokollierungdatasource;

// Hinweis: damit das Programm funktioniert, muss der SmallNameServer laufen

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class DBProtokollierungDataSource {

    public static void main(String[] args) {
        
        int port = 1099;
        String url = "rmi://localhost:" + port;
        String jndiName = "MyDataSource";
        
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
        props.setProperty("java.naming.provider.url", url);
        
        try {
            Context initialContext = new InitialContext(props);
            Object objRef = initialContext.lookup(jndiName);
            
            // ***Protokollierung in Logfile initialiseren:
            //FileWriter logfile = new FileWriter("test.log"); // throws IOException
            
            MysqlDataSource dataSource = (MysqlDataSource)PortableRemoteObject.narrow(objRef, MysqlDataSource.class);
            dataSource.setUser("root");
            dataSource.setPassword("");
            // ***Protokollierung nach Logfile aktivieren
            //dataSource.setLogWriter(new PrintWriter(logfile));
            
            Connection con = dataSource.getConnection();
            // **Logging in Output aktivieren
            dataSource.setLogWriter(new PrintWriter(new OutputStreamWriter(System.out)));
            Statement stmt = con.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabelle1");
            
            while (rs.next()) {
                System.out.printf("%s, %s%n", rs.getString("vorname"), rs.getString("nachname"));
            }
            
            con.close();
            
        } catch (NamingException nmEx) {
            nmEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
        
        
    }
    
}
