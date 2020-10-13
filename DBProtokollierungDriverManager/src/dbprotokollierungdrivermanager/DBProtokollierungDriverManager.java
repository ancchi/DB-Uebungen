
package dbprotokollierungdrivermanager;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DBProtokollierungDriverManager {

    public static void main(String[] args) {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            // Logging einrichten (Ausgabe in Output)
            PrintWriter logger = new PrintWriter(new OutputStreamWriter(System.out));
            DriverManager.setLogWriter(logger);
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/probe", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabelle1");
            while (rs.next()) {
                System.out.printf("%s, %s, %s%n", rs.getString("vorname"), rs.getString("nachname"), rs.getDate("zeitstempel"));
            }
            
            con.close();
            
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
    }
}
