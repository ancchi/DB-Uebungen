import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Diese Klasse wird für unser Tutorial benutzt
public class JDBCTutorial {

	//In der Variablen conn wird die Verbindung zur Datenbank gespeichert
	Connection conn = null;
	//Die Adresse des Computers, auf der Mysql läuft
	String host = "localhost";
	//Der Name der Mysql-Datenbank, die wir verwenden wollen
	String database = "movie";
	//Der Benutzername (Standard bei xampp: root)
	String user = "root";
	//Das Passwort des Benutzers (Standard bei xampp: leer)
	String password = "";
	
	public void connect() {
		try {
			//Versuch des Verbindungsaufbaus
		    conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, password);
		    System.out.println("Erfolgreich verbunden");
		} catch (SQLException ex) {
		    //Möglich auftretene Fehler werden hier ausgegeben
			System.err.println("Fehler beim Verbindungsaufbau!");
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	//Ein einfaches Beispiel für das Verwenden von Statements
	public void statementExample() {
		//Mit der Datenbank verbinden
		connect();
		
		try {
			Statement st = conn.createStatement();
			//Ausführen einer Anfrage, die die Film-ID aller Filme, in deren Titel "Tatort" vorkommt, liefert
			ResultSet rs = st.executeQuery("SELECT mid FROM movie WHERE title like 'Tatort%'");
			while (rs.next()) {
				//Wir iterieren (umgangssprachlich: gehen) über jede Zeile der Antwort-Relation
				
				//Wir rufen das erste Attribut der Zeile ab, welches eine Zeichenkette (englisch: String) ist
				String mid = rs.getString(1);
				//Wir geben das Attribut aus
				System.out.println(mid);
			}
			
			//Wir schließen die Anfrage an die Datenbank, da wir sie nicht weiter verwenden und nur eine 
			//begrenze Anzahl Anfragen gleichzeitig offener sein können.
			st.close();
		} catch (SQLException ex) {
		    //Möglich auftretene Fehler werden hier ausgegeben
		    System.err.println("SQLException: " + ex.getMessage());
		    System.err.println("SQLState: " + ex.getSQLState());
		    System.err.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	public void preparedStatementExample() {
		//Mit der Datenbank verbinden
		connect();
		while (true) {

			//  prompt the user to enter their name
			System.out.print("Bitte geben Sie einen Filmtitel ein: ");

			//  open up standard input
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			String title = null;

			// Lesen des Titels von der Eingabe
			try {
				title = br.readLine();
			} catch (IOException ioe) {
				System.err.println("Fehler beim Lesen des Titels. Programm beendet sich jetzt.");
				System.exit(1);
			}


			try {
				//Anlegen eines sogenannten "PreparedStatements" - dies ist eine Anfrage, die oft mit nur 
				//leicht veränderten Parametern abgesendet werden wird. Durch das Anlegen dieser Prepared Statements
				//kann die Datenbank diese Anfragen schneller beantworten. In diesem Fall muss in der Anfrage nur
				//die entsprechende movie_id eingesetzt werden
				PreparedStatement pst = conn.prepareStatement("" +
						"SELECT a.name, m.title FROM actress a, movie m " +
						"WHERE a.movie_id = m.mid " +
						"AND m.title LIKE ?");

				//Durch die '%' drücken wir in SQL aus, dass auch Zeichen auch vor oder nach dem angegeben
				//Titel stehen dürfen
				title = "%" + title + "%";

				//Die mid wird nun im PreparedStatement eingesetzt
				pst.setString(1, title);
				//Die Anfrage wird abgeschickt
				ResultSet result = pst.executeQuery();
				while (result.next()) {
					System.out.println("Im Film " + result.getString(2) + " hat Schauspielerin "
							+ result.getString(1) + " mitgespielt");
				}
				System.out.println("Alle Ergebnisse ausgegeben");
				//Wir schließen die Anfragen an die Datenbank, da wir sie nicht weiter verwenden und nur eine 
				//begrenze Anzahl Anfragen gleichzeitig offener sein können.
				pst.close();
			} catch (SQLException ex) {
				//Möglich auftretene Fehler werden hier ausgegeben
				System.err.println("SQLException: " + ex.getMessage());
				System.err.println("SQLState: " + ex.getSQLState());
				System.err.println("VendorError: " + ex.getErrorCode());
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Ausführen der Beispiele
		JDBCTutorial tutorial = new JDBCTutorial();
		//Verbindungstest zur Datenbank
		tutorial.connect();
		//Beispiel für eine einfache SQL-Anfrage
		tutorial.statementExample();
		//Beispiel für prepared Statements
		tutorial.preparedStatementExample();

	}

}
