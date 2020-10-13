package datenbankmitnetbeansdb;

/* Quelle: Java ist auch eine Insel, 24.4.3 
    http://openbook.rheinwerk-verlag.de/javainsel9/javainsel_24_004.htm#mj8ff92b2e4af1f98e8f70e3333c1efe60 */

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;


public class DatenbankMitNetbeansDB {
    
    /*
    - Netbeans bringt eine Beispieldatenbank für die ab Java 1.6 mitgelieferte Datenbank
      "JAVADB" aus dem jdk mit -> siehe Services jdbc:derby://localhost:1527/sample -> Connect
    - Man muss diese DB im Projekt als Library hinzufügen -> C:\Program Files\Java\jdk1.8.0_131\db\lib\derbyclient.jar
    - weitere Informationen durch Rechtsklick auf den Service jdbc:derby://localhost:1527/sample -> Properties
    */

 
    public static void main(String[] args) {
    
       Connection con = null;
       
       try {
           con = DriverManager.getConnection("jdbc:derby://localhost:1527/sample","app","app");
           Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
           
           // Abfrage von Strings
           ResultSet rsCustomer = stmt.executeQuery("SELECT name, addressline1, phone FROM customer");
           
           while (rsCustomer.next()) {
               System.out.printf("%s, %s,%s%n", rsCustomer.getString(1), rsCustomer.getString(2), rsCustomer.getString(3));
           }
           
           rsCustomer.close();
          
           // Abfrage eines Datums
            ResultSet rsPurchaseOrder = stmt.executeQuery("SELECT shipping_date, freight_company FROM purchase_order");
            
            while (rsPurchaseOrder.next()) {
                System.out.printf("%s, %s%n", rsPurchaseOrder.getDate(1), rsPurchaseOrder.getString(2));
                // Konvertierung von sql.Date nach utilDate (Quelle: https://www.java67.com/2012/12/how-to-convert-sql-date-to-util-date.html)
                java.util.Date utilDate = new java.util.Date(rsPurchaseOrder.getDate(1).getTime());
                System.out.println("utilDate: " + utilDate);
                // Konvertierung von util.Date nach sql.Date (Quelle: https://www.java67.com/2012/12/how-to-convert-sql-date-to-util-date.html)
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                System.out.println("sqlDate: " + sqlDate);
            }
            
            // Anzahl der Zeilen in einem Resultset:
            rsPurchaseOrder.last();
            int rows = rsPurchaseOrder.getRow();
            rsPurchaseOrder.beforeFirst();
            
            System.out.println(rows);
            
            
            rsPurchaseOrder.close();
            stmt.close();
            
       } catch ( SQLException ex) {
           ex.printStackTrace();
       } finally {
           if (con != null) {
               try { con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
           }
       }
       
       // gibt alle DB-Treiber aus
        for (Enumeration<Driver> drivers = DriverManager.getDrivers(); drivers.hasMoreElements();) {
            System.out.println(drivers.nextElement().getClass().getName());
        }
        
        DriverManager.setLogWriter(new PrintWriter(System.out)); 
       
    }
    
}
