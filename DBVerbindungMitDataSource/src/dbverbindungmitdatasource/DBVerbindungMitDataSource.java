package dbverbindungmitdatasource;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

// Quelle: https://www.straub.as/java/jdbc/datasource.html

public class DBVerbindungMitDataSource {

    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        String server = "localhost";
        int port = 3306;
        String databaseName = "probe";
        String username = "root";
        String password = "";
        
        try {
             MysqlDataSource dataSource = new MysqlDataSource();
             if( dataSource instanceof DataSource)
             System.out.println("MysqlDataSource implements the javax.sql.DataSource interface");
             System.out.println("datasource created");
             
             dataSource.setServerName(server);
             dataSource.setPort(port);
             dataSource.setDatabaseName(databaseName);
             dataSource.setUser(username);
             dataSource.setPassword(password);
             
             // Connection herstellen:
             Connection con = dataSource.getConnection();
             System.out.println("Connection established");
             
             Statement stmt = con.createStatement();
             
             ResultSet result = stmt.executeQuery("SELECT * FROM tabelle1");
             
             while (result.next()) {
                 System.out.printf("%s, %s, %s, %s, %s, %s%n", result.getInt("id"), result.getString("vorname"),
                         result.getString("nachname"), result.getString("bildname"), result.getBlob("avatar"), result.getDate("zeitstempel"));
             }
             
             con.close();
             System.out.println("Connection closed");
             
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
