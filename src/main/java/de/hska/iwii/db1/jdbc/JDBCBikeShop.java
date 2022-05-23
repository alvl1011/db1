package de.hska.iwii.db1.jdbc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import de.hska.iwii.db1.jdbc.utils.Database;
import de.hska.iwii.db1.jdbc.utils.DatabaseUtils;

/**
 * Diese Klasse ist die Basis für Ihre Lösung. Mit Hilfe der
 * Methode reInitializeDB können Sie die beim Testen veränderte
 * Datenbank wiederherstellen.
 */
public class JDBCBikeShop {
	
	private Database db;
	
	/**
	 * Initialize shop
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param database
	 */
	public JDBCBikeShop(String host, String port, String user, String password, String database) {
		this.db = new Database(host, port, user, password, database);
	}
	
	
	public Database getDatabase() {
		return this.db;
	}
	
}
