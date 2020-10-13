package datenbankzugriff;
import datenbankzugriff.DBConnection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 *
 * @author BlackBeauty
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBConnection nachname = new DBConnection("", "ack");
        DBConnection vorname = new DBConnection("li", "");
        DBConnection beides = new DBConnection("li", "anc");
        
         // Aufzählung aller Treiber
        for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
            System.out.println(drivers.nextElement().getClass().getName());
        }
    }
    
}
