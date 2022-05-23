package de.hska.iwii.db1.jdbc.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Database {
	
	private Connection databaseConnection;
	
	private String catalog;
	
	private final String schema = "public";
	
	private List<String> tableNames = new ArrayList<String>();
	
	/**
	 * Defines database object
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param database
	 */
	public Database(String host, String port, String user, String password, String database) {
		this.databaseConnection = this.getPGSQLConnection(host, port, user, password, database);
		this.catalog = database;
		this.setTableNames(this.databaseConnection);
	}
	
	/**
	 * Returns database connection
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return this.databaseConnection;
	}
	
	/**
	 * Returns database catalog
	 * 
	 * @return
	 */
	public String getCatalog() {
		return this.catalog;
	}
	
	/**
	 * Returns database schema
	 * 
	 * @return
	 */
	public String getSchema() {
		return this.schema;
	}
	
	/**
	 * Get table names on this connection
	 * @return
	 */
	public List<String> getTables() {
		return this.tableNames;
	}
	
	public void setTableNames(Connection con) {
		boolean result = this.executeInTransaction(() -> {
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(this.catalog, this.schema, "%", null);
			
			while (rs.next()) {
				  this.tableNames.add(rs.getString(3));
			}
			return true;
		});
		
		if (!result) {
			throw new RuntimeException("Tables can not be filled.");
		}
	}
		
	/**
	 * Get PostgreSQL connection
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param database
	 * @return
	 * @throws SQLException
	 */
	public Connection getPGSQLConnection(String host, String port, String user, String password, String database) {
		
		try {
			Class.forName("org.postgresql.Driver");
			
			Properties properties = new Properties();
			properties.put("user", user);
			properties.put("password", password);
			
			return DriverManager.getConnection("jdbc:postgresql://"
					+ host + ":"
					+ port + "/"
					+ database, properties);
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find driver for PostgreSQL.");
			return null;
		} catch (SQLException e) {
			System.err.println("Cannot set connection for PostgreSQL.");
			return null;
		}
		
		
	}
	
	/**
	 * Close active database-connection
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		try {
			this.databaseConnection.close();
		} catch (SQLException e) {
			throw new SQLException("Problem during closing connection");
		}
    }
	
	/**
	 * Makes executable in transaction
	 * 
	 * @param function
	 * @return
	 */
	public boolean executeInTransaction(Callable<Boolean> function) {
		try {
			
			this.databaseConnection.setAutoCommit(false);
			
			ExecutorService executorService = Executors.newSingleThreadExecutor();
        	Future<Boolean> future = executorService.submit(function);
			
			boolean result = future.get();
			
			if (result) {
				this.databaseConnection.commit();
				return result;
			}
			
			throw new RuntimeException("Invalid database transaction result");
			
		} catch(Throwable t) {
			try {
				System.err.println(t);
				this.databaseConnection.rollback();
			} catch(Throwable throwable) {
				System.err.println(throwable);
			}
			return false;
		}
	}
	
	/**
     * Stellt die Datenbank aus der SQL-Datei wieder her.
     * - Alle Tabllen mit Inhalt ohne Nachfrage löschen.
     * - Alle Tabellen wiederherstellen.
     * - Tabellen mit Daten füllen.
     * <p>
     * Getestet mit MsSQL 12, MySql 8.0.8, Oracle 11g, Oracle 18 XE, PostgreSQL 11.
     * <p>
     * Das entsprechende Sql-Skript befindet sich im Ordner ./sql im Projekt.
     * @param connection Geöffnete Verbindung zu dem DBMS, auf dem die
     * 					Bike-Datenbank wiederhergestellt werden soll. 
     */
    public void reInitializeDB() {
        try {
            System.out.println("\nInitializing DB.");
            this.databaseConnection.setAutoCommit(true);
            String productName = this.databaseConnection.getMetaData().getDatabaseProductName();
            boolean isMsSql = productName.equals("Microsoft SQL Server");
            Statement statement = this.databaseConnection.createStatement();
            int numStmts = 0;
            
            // Liest den Inhalt der Datei ein.
            String[] fileContents = new String(Files.readAllBytes(Paths.get("sql/hska_bike.sql")),
					StandardCharsets.UTF_8).split(";");
            
            for (String sqlString : fileContents) {
                try {
                	// Microsoft kenn den DATE-Operator nicht.
                    if (isMsSql) {
                        sqlString = sqlString.replace(", DATE '", ", '");
                    }
                    statement.execute(sqlString);
                    System.out.print((++numStmts % 80 == 0 ? "/\n" : "."));
                } catch (SQLException e) {
                    System.out.print("\n" + sqlString.replace('\n', ' ').trim() + ": ");
                    System.out.println(e.getMessage());
                }
            }
            statement.close();
            System.out.println("\nBike database is reinitialized on " + productName +
                    "\nat URL " + this.databaseConnection.getMetaData().getURL()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
