package dbpositioning;
// Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/32.html

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBPositioning {

    /**
     * Positionierung:
     * Positionierung des Cursors in einem ResultSet oder Reihenfolge, in der die Results abgerufen werden
     * con.createStatement() erhält Konstanten vom ResultSet als Parameter
     * das zurückgegebene ResultSet besitzt daraufhin die gewünschten Eigenschaften
     */
    
    public static void main(String[] args) {
        
            try {
            Class.forName("com.mysql.jdbc.Driver");
            
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/probe", "root", "");
             
            // createStatement(int resultSetType, int resultSetConcurrency) - Positionierbarkeit, Veränderbarkeit
            // createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            /*
            ResultSet-Type:
            (Postitionierbarkeit)
            - TYPE_FORWARD_ONLY, TYPE_SCROLL_INSENSITIVE, TYPE_SCROLL_SENSITIVE
            - bestimmt, ob Änderungen, die während des Vorgangs am Datensatz vorgenommen werden, sichtbar sind (SENSITIVE) 
                oder nicht (INSENSITIVE)
                -> SENSITIVE: wird z.B. ein Datensatz, also eine ganze Zeile gelöscht, wandert der Cursor an die Position _vor_
                    der gelöschten Zeile
            
            ResultSet-Concurrency:
            (Veränderbarkeit; Concurrency = Parallelität, gleichzeitiger Zugriff)
            - CONCUR_READ_ONLY, CONCUR_UPDATABLE
            - bestimmt, ob Änderungen an der Ergebnismenge möglich sind
            - wenn CONCUR_READ_ONLY, dann ist weder ein Update, noch ein Insert oder ein Delete möglich
            
            ResultSet-Holdability:
            - es gibt 2: CLOSE_CURSORS_AT_COMMIT und HOLD_CURSORS_OVER_COMMIT
                -> beeinflusst, ob das ResultSet-Object nach dem commit geschlossen wird oder offen gehalten wird
            */
            
            // Änderungen möglich
            //Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            // nur lesen!
            Statement stmt1 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            ResultSet rs1 = stmt1.executeQuery("SELECT * FROM tabelle1");
            
            int anzahlZeilen = 0;
            
            while (rs1.next()) {
                System.out.printf("%s, %s, %s%n", rs1.getString("vorname"), rs1.getString("nachname"), rs1.getDate("zeitstempel"));
                anzahlZeilen++;
            }
                System.out.println("");
            
                // Positionierung des Cursors im ResultSet + Update-Beispiele:
                // first()
                System.out.println(rs1.first()); // gibt boolean zurück
                System.out.println(rs1.getString("vorname"));
                
                System.out.println("");
                
                // beforeFirst() und next()
                rs1.beforeFirst(); // gibt void zurück, Zeigt auf Pos vor dem ersten Eintrag
                System.out.println(rs1.next()); // auf die nächste Position
                System.out.println(rs1.getString("vorname")); // Vorname der aktuellen Position
                
                System.out.println("");
                
                // last() und previous()
                System.out.println(rs1.last());
                System.out.println("letzter: " + rs1.getString("vorname"));
                System.out.println(rs1.previous());
                System.out.println("der davor: " + rs1.getString("vorname"));
                
                System.out.println("");
                
                // absolute()
                System.out.println(rs1.absolute(5)); // Pos 5 von oben
                System.out.println("fünfter von oben ist: " + rs1.getString("vorname"));
                System.out.println(rs1.absolute(-4)); // Pos 4 von unten
                System.out.println("vierter von unten ist: " + rs1.getString("vorname"));
                // Update
                //rs1.updateString("vorname", "Freddy"); // Änderung im Puffer
                //rs1.updateRow(); // comitten der Änderung (wurde mit CONCUR_UPDATABLE ausgeführt)
                
                System.out.println("");
                
                // relative()
                // Position ausgehend vom aktuellen Datensatz
                System.out.println(rs1.relative(1)); // eine Pos nach dem aktuellen
                System.out.println("datensatz nach dem vierten von unten: " + rs1.getString("vorname"));
                // update
                //rs1.updateString("nachname", "Zitterbacke");
                //rs1.updateRow();
                System.out.println(rs1.relative(-3)); // 3 Pos vor dem aktuellen
                System.out.println("drei darüber: " + rs1.getString("vorname"));
                
                // neuen Datensatz hinzufügen:
                //rs1.moveToInsertRow(); // neue Zeile; eine Pos nach dem letzten Datensatz
                // Daten pro Column eingeben
                //rs1.updateString("vorname", "Emily"); // Puffer
                //rs1.updateString("nachname", "Dickens"); // Puffer
                //rs1.insertRow(); // abschicken - DB
                System.out.println(rs1.getString("vorname")); // Zeiger ist auf inserted element
                //rs1.moveToCurrentRow(); // Zeiger wieder auf vorherige Position setzen (vom letzten rs.last())
                System.out.println(rs1.getString("vorname"));
                
                // Anzahl der Tabellenzeilen ermitteln
                
                // 1. erst den gesamten Datensatz holen, dann last(), dann getRow()
                rs1.last();
                System.out.println("Anzahl der Tabellenzeilen mit last(): " + rs1.getRow());
                
                // 2. mit SQL-Anweisung + in ResultSet speichern
                ResultSet rs2 = stmt1.executeQuery("SELECT count(*) FROM tabelle1");
                rs2.next();
                long zeilen = rs2.getLong(1);
                System.out.println("zeilen über SQL-Anweisung mit count(*): " + zeilen);
                
                // 3. mit einem count++ in der while-Schleife - siehe oben
                // am effizientesten, wenn die while-Schleife sowieso existiert
                System.out.println("zeilen mit Zählen in while-Schleife: " + anzahlZeilen);
                
        } catch (ClassNotFoundException cnfEx) {
            cnfEx.printStackTrace();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
    
}
