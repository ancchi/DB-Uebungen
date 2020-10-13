package dbdatumundzeit;
// Quellen:
// https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/19.html
// http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_24_006.htm#mj3cd8803a2f729fc42d74f686a789a7b2

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DBDatumUndZeit {

    /*
        Besonderheiten, die beachtet werden müssen:
         Basisklasse: 
         - java.util.Date beinhaltet Datum und Uhrzeit -> java.util.Date(int year, int month, int date, int hrs, int min, int sec)
         - void setTime( long time) -> wenn das Datum ein Zeitstempel vom Typ long ist, werden auch hier die Millisekunden berücksichtigt
    
         Subklassen:
         - java.sql.Date beinhaltet nur das Datum -> java.sql.Date(int year, int month, int day)
         -> Zeit wird weg-"normalisiert" und nur der Datumsanteil in der DB gespeichtert
    
         - java.sql.Time beinhaltet nur die Uhrzeit -> java.sql.Time(int hour, int minute, int second)
         -> Datum wird "ausgeblendet" und nur der Zeitanteil in der DB gespeichtert
    
         - java.sql.Timestamp beinhaltet Datum und Zeit + Nanosekunden
            -> Timestamp(int year, int month, int date, int hour, int minute, int second, int nano)
         -> bei der Umwandlung eines java.sql.Timestamp in ein java.util.Date gehen die Nanosekunden verloren

   */
    
   
    
    public static void main(String[] args) {
        
        // einfaches Java-Util-Date:
        java.util.Date utilDateToday = new java.util.Date();
        System.out.println("utilDateToday: " + utilDateToday);
        
        // java.util.Date in ein java.sql.Date umwandeln:
        java.sql.Date sqlDateToday = new java.sql.Date(new java.util.Date().getTime()); // Verwendung von getTime()
        System.out.println("sqlDateToday: " + sqlDateToday);
        
        
        // Datum aus DB holen und für Ausgabe formatieren
        // Verbindung zu Treiber DB
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        // Verbindung zu DB
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabelle1 WHERE nachname like 'Sonnenschein'");
            
            while (rs.next()) {
                System.out.println("Zeitstempel DB: " + rs.getTimestamp("zeitstempel"));
                // java.sql.Date ganz einfach nach java.util.Date
                java.util.Date utilDate = rs.getTimestamp("zeitstempel");
                
                System.out.println("utilDate: " + utilDate);
                // Datum für die Ausgabe formatieren:
                DateFormat df = new SimpleDateFormat("EEEE, dd. MMMM yyyy hh:mm 'Uhr', G, z");
                // G - Ära
                // z - allgemeine Zeitzone
                // ' - Hochkommats für nicht zu interpretierenden Text
                System.out.println(df.format(utilDate));
            }
            
            
            // Datum zu DB hinzufügen: (Spalte "Zeitstempel" setzt die Uhrzeit von Seiten der DB automatisch)
            java.util.Date datum = new java.util.Date();
            PreparedStatement prep = con.prepareStatement("INSERT INTO tabelle1 (vorname, nachname, zeitstempel) VALUES(?,?,?)");   
            prep.setString(1, "Anny");
            prep.setString(2, "Starwalker");
            prep.setTimestamp(3, new java.sql.Timestamp(datum.getTime()));
            prep.executeUpdate();
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        
        
        
        
        
    }
    
}
