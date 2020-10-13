package dbdatentypblob;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

// Quelle: https://www.straub.as/java/jdbc/blob.html

public class DBDatentypBLOB {

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        
        Connection con = null;
        
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/probe", "root", "");
            
            String pSql = "INSERT INTO tabelle1 (vorname, nachname, bildname, avatar) VALUES(?, ?, ?, ?)";
            PreparedStatement pStmt = con.prepareStatement(pSql);
            
            pStmt.setString(1, "Leo");
            pStmt.setString(2, "Parden");
            pStmt.setString(3, "Leoparden.jpg");
           // Fotodaten einlesen:
            // absoluter Dateipfad vom Wurzelverzeichnis des Projektes aus
            // erste Variante mit String
            String pathToImage = "src\\imgs\\Leoparden.jpg";
            InputStream fotoStream = new BufferedInputStream(new FileInputStream(pathToImage));
            pStmt.setBinaryStream(4, fotoStream, fotoStream.available()); 
            pStmt.execute();
            
            pStmt.setString(1, "Frida");
            pStmt.setString(2, "Fuchser");
            pStmt.setString(3, "Füchse.jpg");
            // zweite Variante mit File
            File secondPTImage = new File("src\\imgs\\Füchse.jpg");
            InputStream fotoStream2 = new BufferedInputStream(new FileInputStream(secondPTImage));
            // Größe des Bildes mit length() der Klasse File ermitteln; Rückgabe als long, benötigt wird int
            pStmt.setBinaryStream(4, fotoStream2, (int)secondPTImage.length()); 
            pStmt.execute();
            
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tabelle1 WHERE avatar IS NOT NULL");
            while (rs.next()) {
            
                // Blob aus der ersten Zeile auslesen
                BufferedInputStream bis = new BufferedInputStream(rs.getBinaryStream("avatar"));
                // mit ImageIO kann man aus dem InputStream ein BufferedImage erzeugen
                BufferedImage buffImage = ImageIO.read(bis);

                // Foto in einem JFrame ausgeben:
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                JLabel imageLabel = new JLabel( new ImageIcon(buffImage));
                JScrollPane jsp = new JScrollPane(imageLabel);

                frame.add(jsp);
                frame.pack();
                frame.setVisible(true);
            
            }
    
        // mehrere Exceptions behandeln
        } catch (SQLException ex) {
            ex.printStackTrace();
            // tritt bei einem Dateipfad auf
        } catch (FileNotFoundException ex) { // TODO hier weiter!!
                    ex.printStackTrace();
            // tritt bei fotoStream.available() auf
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlEx) {
                    sqlEx.printStackTrace();
                }} 
        }
        
    }
    
}
