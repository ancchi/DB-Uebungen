package datenbankzugriff;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Enumeration;

public class DBConnection {
    private final String url = "jdbc:mysql://localhost:3306/";
    private String user = "root";
    private String password = "";
    private String dbName = "probe";
    private String command;
    
    
    public DBConnection (String vornameSuchKriterium, String nachnameSuchKriterium) {
        
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Keine Treiber-Klasse vorhanden.");
            return;
        }
        
        /* Vorbereitung der Such-Abfragen */
        if(nachnameSuchKriterium.length() > 0 && vornameSuchKriterium.length() == 0) {
             this.setCommand("SELECT * FROM tabelle1 WHERE nachname LIKE '%"+nachnameSuchKriterium+"%'");
        } else if (nachnameSuchKriterium.length() == 0 && vornameSuchKriterium.length() > 0) {
            this.setCommand("SELECT * FROM tabelle1 WHERE vorname LIKE '%"+vornameSuchKriterium+"%'");
        } else if (nachnameSuchKriterium.length() > 0 && vornameSuchKriterium.length() > 0) {
            this.setCommand("SELECT * FROM tabelle1 WHERE vorname LIKE '%"+vornameSuchKriterium+"%' AND "+
                    "nachname LIKE '%"+nachnameSuchKriterium+"%'");
        } else {
            System.out.println("Bitte geben Sie Suchoptionen ein.");
        }
       
        Connection con = null;
        
        try {
            con = DriverManager.getConnection(this.getUrl(), this.getUser(), this.getPassword());
            Statement stt = con.createStatement();
            stt.execute("USE"+" "+this.getDbName());
            
            ResultSet searchResult = stt.executeQuery(this.getCommand());
            String ergebnis = "";
            
            
            while (searchResult.next()) {
                // nach jedem Vor- und Nachnamen wird ein Umbruch eingebaut
                //ergebnis = ergebnis.concat(searchResult.getString("vorname") + " " + searchResult.getString("nachname") + "\n");
                
                // Möglichkeit ohne concat -> Formatierung mit printf -> 
                System.out.printf("%s, %s%n", searchResult.getString("vorname"), searchResult.getString("nachname"));
            }
            
            // mit printf lässt sich nach mehrzeiligen Ergebnissen ein Umbruch einbauen -> Gruppierung der Werte eines Ergebnisses
            System.out.printf("%s%n", ergebnis);
            
            // Statement und ResultSet müssen nicht einzeln geschlossen werden, da es von beiden nur ein Exemplar gibt und
            // dieses jeweils automatisch durch das Schliessen der Connection geschlossen wird
//            searchResult.close();
//            stt.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        } finally { // Verbindung schließen
            if(con != null) {
                try { con.close(); } catch (SQLException e) {e.printStackTrace(); }
            }
        }
    }

    public String getUrl() {
        return url;
    }
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    
    
    
}
