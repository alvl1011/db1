/**
 * 
 */
package de.hska.iwii.db1.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import de.hska.iwii.db1.jdbc.utils.Database;

/**
 * @author vovaa
 *
 */
public class Main {

	public static void main(String[] args) throws SQLException {
		
    	JDBCBikeShop jb = new JDBCBikeShop("datenbanken1.ddns.net", "3690", "g06", "r6Qs8ShEBU", "g06");
    	Database db = jb.getDatabase();
    	
    	// Aufgabe 4.1
    	System.out.println("***********");
    	System.out.println("Aufgabe 4.1");
    	System.out.println("***********\n");
    	testConnection(jb);
    	
    	
    	
    }
	
	/**
	 * Aufgabe 4.1
	 * - <strong>Datenbank-Verbindung</strong>: Stellen Sie per JDBC eine Verbindung zur Datenbank her,
	 * - geben Sie den Namen der Datenbank sowie Informationen zum JDBC-Treiber aufder Konsole aus. 
	 * - Schließen Sie die Datenbank-Verbindung wieder. Kapseln Sie da-bei das Öffnen, 
	 * - den Abruf der Informationen sowie das Schließen der Verbindung inseparaten Methoden einer Klasse
	 * 
	 * @param jb
	 */
	public static void testConnection(JDBCBikeShop jb) {
        try {
        	Database db = jb.getDatabase();
        	DatabaseMetaData data = db.getConnection().getMetaData();
            System.out.println("Name of Database: " + db.getCatalog());
            System.out.println("JDBC-Driver: " + data.getDriverName());
            db.close();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
