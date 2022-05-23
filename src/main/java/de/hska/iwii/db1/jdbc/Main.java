/**
 * 
 */
package de.hska.iwii.db1.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.hska.iwii.db1.jdbc.utils.Database;
import de.hska.iwii.db1.jdbc.utils.DatabaseUtils;

/**
 * @author vovaa
 *
 */
public class Main {

	public static void main(String[] args) throws SQLException {
		
    	JDBCBikeShop jb = new JDBCBikeShop("datenbanken1.ddns.net", "3690", "g06", "r6Qs8ShEBU", "g06");
    	
    	// Aufgabe 4.1
    	System.out.println("***********");
    	System.out.println("Aufgabe 4.1");
    	System.out.println("***********\n");
    	testConnection(jb);
    	
    	// Because in first task we had to close connection
    	jb = new JDBCBikeShop("datenbanken1.ddns.net", "3690", "g06", "r6Qs8ShEBU", "g06");
    	
    	// Aufgabe 4.2
    	System.out.println("\n***********");
    	System.out.println("Aufgabe 4.2");
    	System.out.println("***********\n");
    	workWithResultSet(jb);
    	
    	// Aufgabe 4.3
    	System.out.println("\n***********");
    	System.out.println("Aufgabe 4.3");
    	System.out.println("***********\n");
    	joinOperations(jb);
    	
    	// Aufgabe 4.4
    	System.out.println("\n***********");
    	System.out.println("Aufgabe 4.4");
    	System.out.println("***********\n");
    	alterDatabse(jb);
    	
    	
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Aufgabe 4.2:
	 * 
	 * - <strong>Arbeit mit demResultSet</strong>: 
	 * - Nehmen Sie Aufgabe 5.1 als Basis und lesen Sie ineiner weiteren Methode mit
	 * 
	 * ---- SELECT persnr, name, ort, aufgabe FROM personal ----
	 * 
	 * @param jb
	 */
	public static void workWithResultSet(JDBCBikeShop jb) {
        try {
        	Database db = jb.getDatabase();
        	DatabaseUtils.customQuery("SELECT persnr, name, ort, aufgabe FROM personal", db);
         } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Aufgabe 4.3:
	 * 
	 * - In dieser Aufgabeüber Sie die Verwendung von
	 * - JOIN-Operationenmit JDBC. Zunächst lesen Sie
	 * - alle Kunden- Lieferantenbeziehungen in dieser Form aus, 
	 * - wobei Sie die Ausgabe-methode aus Aufgabe 4.2 verwenden:
	 * 
	 * @param jb
	 */
	public static void joinOperations(JDBCBikeShop jb) {
        try {
        	Database db = jb.getDatabase();
        	 String query = "SELECT distinct kunde.name as kunde, kunde.nr as knr, lieferant.name as lieferant, " +
        	 	     "lieferant.nr as lnr" +
                     " FROM lieferant" +
                     " FULL OUTER JOIN lieferung on lieferant.nr = lieferung.liefnr" +
                     " FULL OUTER JOIN teilestamm on lieferung.teilnr = teilestamm.teilnr" +
                     " FULL OUTER JOIN auftragsposten on teilestamm.teilnr = auftragsposten.teilnr" +
                     " FULL OUTER JOIN auftrag on auftragsposten.auftrnr = auftrag.auftrnr" +
                     " JOIN kunde on auftrag.kundnr = kunde.nr" +
                     " order by kunde.name asc";
        	DatabaseUtils.customQuery(query, db);
        	System.out.println("\n\n-------------------------------------FILTER--------------------------------------------------\n\n");
        	String query1 = "SELECT distinct kunde.name as kunde, kunde.nr as knr, lieferant.name as lieferant, " +
       	 	     "lieferant.nr as lnr" +
                    " FROM lieferant" +
                    " FULL OUTER JOIN lieferung on lieferant.nr = lieferung.liefnr" +
                    " FULL OUTER JOIN teilestamm on lieferung.teilnr = teilestamm.teilnr" +
                    " FULL OUTER JOIN auftragsposten on teilestamm.teilnr = auftragsposten.teilnr" +
                    " FULL OUTER JOIN auftrag on auftragsposten.auftrnr = auftrag.auftrnr" +
                    " JOIN kunde on auftrag.kundnr = kunde.nr" +
                    " WHERE kunde.name like ?" +
                    " order by kunde.name asc";
        	DatabaseUtils.customQuery(query1, "Rafa%", db);
         } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	/**
	 * Aufgabe 4.4:
	 * 
	 * - Hier üben Sie das Ändern von Zeilen in einer Tabelle
	 * 
	 * @param jb
	 */
	public static void alterDatabse(JDBCBikeShop jb) {
		try {
			Database db = jb.getDatabase();
			String update_add_Kunde = "INSERT INTO kunde VALUES (7,'Neuer Kunde', 'Kundenstraße 3', 66666, 'kunden hölle', 0);"; //KUNDEN EINFÜGEN
			DatabaseUtils.customUpdate(update_add_Kunde, db);
			DatabaseUtils.selectAll("kunde", db);
			String update_add_Auftrag = "INSERT INTO auftrag VALUES (6,'2022-05-12', 7, 2)"; //AUFTRAG EINFÜGEN
			DatabaseUtils.customUpdate(update_add_Auftrag, db);
			DatabaseUtils.selectAll("auftrag", db);
			String update_add_Auftragsposten = "INSERT INTO auftragsposten VALUES (53, 6, 500013, 6, '280.00')"; //Auftragsposten bei Auftrag ergänzen
			DatabaseUtils.customUpdate(update_add_Auftragsposten, db);
			DatabaseUtils.selectAll("auftragsposten", db);
			String update_add_Sperre = "UPDATE kunde SET sperre = 1 WHERE nr = 7"; //Kunden Sperren
			DatabaseUtils.customUpdate(update_add_Sperre, db);
			DatabaseUtils.selectAll("kunde", db);
			String update_delete_Kunde = "DELETE FROM kunde WHERE nr = 7 "; //Kunden löschen
			DatabaseUtils.customUpdate(update_delete_Kunde, db);
			DatabaseUtils.selectAll("kunde", db);
			//Richtige lösch reihenfolge
			String update_delete_Auftragsposten = "DELETE FROM auftragsposten WHERE auftrnr = 6 "; //
			DatabaseUtils.customUpdate(update_delete_Auftragsposten, db);
			String update_delete_Auftrag = "DELETE FROM auftrag WHERE kundnr = 7 "; //
			DatabaseUtils.customUpdate(update_delete_Auftrag, db);
			DatabaseUtils.customUpdate(update_delete_Kunde, db);
			DatabaseUtils.selectAll("kunde", db);
			DatabaseUtils.selectAll("auftrag", db);
			DatabaseUtils.selectAll("auftragsposten", db);
			db.reInitializeDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
