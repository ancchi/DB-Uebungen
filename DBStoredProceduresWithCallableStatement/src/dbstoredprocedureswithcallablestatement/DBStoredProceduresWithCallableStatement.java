
package dbstoredprocedureswithcallablestatement;

// Quelle: https://www.dpunkt.de/java/Programmieren_mit_Java/Java_Database_Connectivity/39.html
// Quelle: https://dev.mysql.com/doc/refman/5.7/en/stored-objects.html
// https://www.straub.as/java/jdbc/stopro0.html

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

/**
 * Stored procedure:
 * - 
 * - ist ein Objekt-Typ von Stored Objects, welche als SQL-Code in der Datenbank auf dem Server für die
 *  spätere Ausführung vorgehalten werden (weitere Typen: Stored function, Trigger, Event, View)
 * - das Objekt wird mit CREATE PROCEDURE erschaffen und mit CALL aufgerufen
 * - um eine Prozedur mit mehreren Anweisugnen in _Mysql_ zu erzeugen, muss ein Delemiter gesetzt werden, 
 *  damit Mysql das Semikolon nicht als Ende des Statements ansieht
 *  z.B. 
 *      delimiter //
 *      CREATE PROCEDURE dorepeat(p1 INT)
            BEGIN
                SET @x = 0;
                REPEAT SET @x = @x + 1; UNTIL @x > p1 END REPEAT;
            END
            //
        delimiter ;
        
        CALL dorepeat(1000);
        
        SELECT @x;
    - in dem Beispiel wird der Delimiter auf "//" gesetzt, die ganze Funktion wird ausgeführt, dann wird
      der Delimiter wieder auf ";" zurückgesetzt
    - die Prozedur kann nun mit CALL aufgerufen (siehe unten)
    * 
    * Unterschied zwischen Prozedur und Funktion:
    * - eine Prozedur hat keinen Rückgabewert, kann Werte aber für einen späteren Abruf vom CALLer vorbereiten;
        sie kann auch ein ResultSet generieren
       {call prozedurname(?, ?, ?)}
    * - man ruft sie in einer Expression auf und sie gibt dir direkt einen Wert zurück
       {? := call funktionsName(?, ?, ?)} -> Position 1 ist immer dem Rückgabewert zugeordnet
 *  
 *  IN und OUT bzw. IN OUT Paramter:
 * 
 * - IN-Parameter müssen gesetzt werden:
 * Bsp:
 *    CallableStatement cs = conn.prepareCall( "{call procedure_name(?, ?)}" );
                  cs.setInt(1, 25);
                  cs.setString(2, "wzlbrmpft");
    
    - OUT-Parameter müssen registriert werden:
    Bsp:
       CallableStatement cs = conn.prepareCall( "{call procedure_name(?, ?, ?)}" );
                  cs.setInt(1, 25); // IN
                  cs.registerOutParameter(2, java.sql.Types.DOUBLE); // OUT
                  cs.registerOutParameter(3, java.sql.Types.VARCHAR); // OUT
    
    - der RETURN-Wert von Funktionen wird wie ein OUT-Parameter registriert:
    Bsp:
        CallableStatement cs = conn.prepareCall( "{ ? = call procedure_name(?, ?)}" );
                  cs.registerOutParameter(1, java.sql.Types.DOUBLE); // RETURN
                  cs.setInt(2, 25); // IN
                  cs.registerOutParameter(3, java.sql.Types.VARCHAR); // OUT
    
    INOUT-Parameter müssen zuerst gesetzt und dann registriert werden:
    Bsp:
        CallableStatement cs = conn.prepareCall( "{call procedure_name(?, ?)}" );
                  cs.setInt(1, 25); // IN
                  cs.setString(2, "wzlbrmpft"); // INOUT
                  cs.registerOutParameter(2, java.sql.Types.VARCHAR); // INOUT
 * 
 * - nach dem Setzen und Registrieren der Parameter wird die Prozedur oder die Funktion mit
     cs.execute(); abgearbeitet
   - OUT-Parameter werden danach mit getXXX()-Methoden abgeholt
     Bsp:
         ResultSet rs = (ResultSet)cs.getObject(1);
         int i = cs.getInt(3);
    Erklärung Bsp:
    1. ResultSet an Index 1 der aufgerufenen Prozedur oder Funktion; wenn Funktion, dann ist das der Rückgabewert
    2. Integer an Index 3 der aufgerufenen Prozedur oder Funktion; wenn Funktion, dann ist das der 2. Übergabeparameter
 */

public class DBStoredProceduresWithCallableStatement {


     public static void main(String[] args) {
        
         String url = "rmi://localhost";
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
             
             /* ############################## Prozedur auf DB gespeichert ############################################## */
             
             // Quelle für PRODCEDURE in DB probe: https://dev.mysql.com/doc/refman/5.7/en/stored-programs-defining.html
             // der CALL muss in geschweiften Klammern stehen
             // die Prozedur ist in XAMPP gespeichert: DB auswählen -> Routinen
             /*
             SQL-Code in DB-Routine:
             DELIMITER $$
                CREATE DEFINER=`root`@`localhost` PROCEDURE `dorepeat`(IN `p1` INT, OUT `erg` INT)
                COMMENT 'das ist ja super, dass das funktioniert!'
                BEGIN
                    SET erg = 0;
                    REPEAT SET erg = erg + 1; UNTIL erg > p1 END REPEAT;
                END$$
             DELIMITER ;
             */
             
             // Routine auf der Connection aufrufen
             // Prozedur dorepeat befindet sich bereits auf dem Server
             CallableStatement call = con.prepareCall("{ CALL dorepeat(?, ?) }");
             call.setInt(1, 1000); // IN
             call.registerOutParameter(2, Types.INTEGER); // OUT
             call.execute();
             
             // OUT-Parameter abrufen und speichern
             int ergebnis = call.getInt(2);
             System.out.println(ergebnis);
             
             /* ############################## Prozedur wird erst über Java in DB eingespeist ################################# */
             
             createStoredProcedure(con);
             
             String preparedCallString = "{ CALL rowCount(?, ?) }";
             
             CallableStatement callStmt = con.prepareCall(preparedCallString);
             callStmt.setString(1, "Freddy");
             callStmt.registerOutParameter(1, Types.VARCHAR);
             callStmt.registerOutParameter(2, Types.INTEGER);
             callStmt.executeQuery();
             
             // Result:
             String name = callStmt.getString(1);
             int result = callStmt.getInt(2);
             System.out.println("Der Name \"" + name + "\" ist " + result + " Mal in der Spalte \"vorname\" enthalten.");
             
         } catch (NamingException nmEx) {
             nmEx.printStackTrace();
         } catch (SQLException sqlEx) {
             sqlEx.printStackTrace();
         } 
     }
     
     static void createStoredProcedure(Connection con) throws SQLException {
          Statement stmt = con.createStatement();
          // Hinweis: mit DELIMITER funktioniert es nicht, nur ohne
          String procedure = "CREATE OR REPLACE PROCEDURE rowCount(INOUT prename CHAR(30), OUT rowNumber INT)" +
                  " BEGIN" +
                  " SELECT COUNT(*) INTO rowNumber FROM probe.tabelle1 WHERE vorname = prename;" +
                  " END";
          stmt.execute(procedure);    
     }
    
}
