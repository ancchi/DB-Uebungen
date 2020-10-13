/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dballgemeinerexecuteaufrufwithnameserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author BlackBeauty
 */
public class QueryCoWorker {
    
    public QueryCoWorker(Connection con, String sql) {
        
        try {
            Statement stmt = con.createStatement();
            boolean isResultSet = stmt.execute(sql);
            
            do {
                // wenn true, dann Result; wenn false, dann Update
                if (isResultSet == true) {
                    System.out.println("Es ist ein ResultSet.");
                    ResultSet rs = stmt.getResultSet();
                    while (rs.next()) {
                        System.out.printf("%s, %s, %s%n", rs.getString("vorname"), rs.getString("nachname"), rs.getDate("zeitstempel"));
                    }
                } else if (isResultSet == false) {
                    System.out.println("Es ist ein UpdateCount.");
                    int updateCount = stmt.getUpdateCount();
                    System.out.println("UpdateCount: " + updateCount);
                }
                // stmt.getMoreResults() bewegt sich zum nächsten Result des Statements
                // es muss dafür dataSource.setAllowMultiQueries(true); gesetzt werden (Standard ist false)
                isResultSet = stmt.getMoreResults();
                // Statement.execute() kann mehrere Results zurückgeben
            } while (isResultSet || stmt.getUpdateCount() != -1);
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        
        
            
        
    }
    
}
