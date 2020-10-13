package dbnullexpliziteinfuegen;

// Hinweis: funktioniert nur mit laufendem "SmallNameServer"

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class DBNullExplizitEinfuegen {

    public static void main(String[] args) {
        
        String url = "rmi://localhost"; // Standardport 1099
        String jndiName = "MyDataSource";
        
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
        props.setProperty("java.naming.provider.url", url);
        
        try {
             Context intialContext = new InitialContext(props);
             Object objRef = intialContext.lookup(jndiName);
             
             MysqlDataSource dataSource = (MysqlDataSource)PortableRemoteObject.narrow(objRef, MysqlDataSource.class);
             dataSource.setUser("root");
             dataSource.setPassword("");
             Connection con = dataSource.getConnection();
             
             
             PreparedStatement prepStmt = con.prepareStatement("INSERT INTO fotos (description, imagetype, image) VALUES (?, ?, ?)");
             prepStmt.setString(1, "Bild von Löwe");
             prepStmt.setNull(2, Types.INTEGER);
             prepStmt.setNull(3, Types.BLOB);
             prepStmt.executeUpdate();
             
        } catch (NamingException nmEx) {
            nmEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
       
        
    }
    
}
