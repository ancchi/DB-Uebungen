package dbpreparedstatement;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

// QUELLE: http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_24_008.htm#mjdeb4eefa360476894b8cd02a4767f015

public class DBPreparedStatement {

    public static void main(String[] args) {
        
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Kein Treiber vorhanden.");
            ex.printStackTrace();
        }
        
        try {
           Connection con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
           // Prepared Statement, das zur Datenbank geschickt und dort in ein internes Format umgesetzt (kompiliert) wird
           // später verweist ein Programm auf diese vorübersetzten zwischengespeicherten Anweisungen, die DB kann sie
           // dann schnell ausführen
           
           // Update
           PreparedStatement preparedUpdate = con.prepareStatement("UPDATE tabelle1 SET vorname = ? WHERE vorname = ? and nachname = ?");
           // es müssen immer alle Parameter gesetzt werden!
            preparedUpdate.setString(1, "Sunny");
            preparedUpdate.setString(2, "Sanni");
            preparedUpdate.setString(3, "Flower");
            preparedUpdate.executeUpdate();
            preparedUpdate.setString(1, "Emilia");
            preparedUpdate.setString(2, "Annelie");
            preparedUpdate.setString(3, "Schatz");
            preparedUpdate.executeUpdate();
            
            preparedUpdate.clearParameters(); // löscht alle Parameter dieses SQL-Statements
            
            // Select
            
            PreparedStatement preparedSelect = con.prepareStatement("SELECT vorname, nachname FROM tabelle1 WHERE vorname = ?");
            preparedSelect.setString(1, "Lisa");
            
            ResultSet rs = preparedSelect.executeQuery();
            
            while (rs.next()) {
                System.out.printf("%s, %s%n", rs.getString(1), rs.getString(2));
            }
           
        } catch (SQLException ex) {
            System.out.println("Es ist ein Fehler aufgetreten.");
            ex.printStackTrace();
        }
    }
    
}
