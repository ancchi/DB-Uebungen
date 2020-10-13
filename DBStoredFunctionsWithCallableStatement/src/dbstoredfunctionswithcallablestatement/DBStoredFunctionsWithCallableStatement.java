package dbstoredfunctionswithcallablestatement;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class DBStoredFunctionsWithCallableStatement {

    public static void main(String[] args) {
        String url = "rmi://localhost:1099";
        String jndiName = "MyDataSource";
        
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "com.sun.jndi.rmi.registry.RegistryContextFactory");
        props.setProperty("java.naming.provider.url", url);
        
        try {
            Context initialContext = new InitialContext(props);
            Object objRef = initialContext.lookup(jndiName);
            
            MysqlDataSource dataSource = (MysqlDataSource)PortableRemoteObject.narrow(objRef, MysqlDataSource.class);
            dataSource.setUser("root");
            dataSource.setPassword("");
            Connection con = dataSource.getConnection();
            
            createStoredFunction(con); 
            
            CallableStatement cstmt = con.prepareCall("{ ? = CALL hello( ? )}");
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, "world");
            cstmt.execute();
            
            String sentence = cstmt.getString(1);
            System.out.println(sentence);
            
            
            compareNumbers(con);
            
            CallableStatement call = con.prepareCall("{ ? = CALL SimpleCompare(?, ?) }");
            call.registerOutParameter(1, Types.VARCHAR);
            call.setInt(2, 20);
            call.setInt(3, 20);
            call.execute();
            
            String vergleich = call.getString(1);
            System.out.println(vergleich);
            
        } catch (NamingException nameEx) {
            nameEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
        
    }
    
    private static void createStoredFunction(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        String storedFunction = 
                "CREATE OR REPLACE FUNCTION hello (name CHAR(20)) " +
                "RETURNS CHAR(50) DETERMINISTIC  " + // Datentyp des zurückgegebenen Wertes
                "RETURN CONCAT('Hello, ',name,'!');";
        stmt.execute(storedFunction);
        // deterministisch: immer gleicher Ablauf und gleiche Zwischenergebnisse sowie 
        // gleiches Endergebnis bei gleichem Input
    }
    
    
    private static void compareNumbers(Connection con) throws SQLException {
        Statement stmt = con.createStatement();
        // Beispiele: https://dev.mysql.com/doc/refman/5.7/en/if.html
        String storedFunction = 
                "CREATE OR REPLACE FUNCTION SimpleCompare(zahl1 INT, zahl2 INT) " +
                "RETURNS VARCHAR(20) " + // Datentyp des zurückgegebenen Wertes
                    "BEGIN " +
                        "DECLARE s VARCHAR(20);" +
                        "IF zahl1 > zahl2 THEN SET s = '>';" +
                        "ELSEIF zahl1 = zahl2 THEN SET s = '=';" +
                        "ELSE SET s = '<';" +
                        "END IF;" +
                        "SET s = CONCAT(zahl1, ' ', s, ' ', zahl2);" +
                        "RETURN s;" +
                    "END";
        stmt.execute(storedFunction);
    }
    
}
