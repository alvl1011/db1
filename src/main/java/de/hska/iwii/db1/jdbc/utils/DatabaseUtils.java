package de.hska.iwii.db1.jdbc.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class DatabaseUtils {
	
	/**
	 * Print result of SQL query in table sight
	 * 
	 * Example:
	 * 
	 * Table: kunde
	 *	nr          | name                           | strasse                        | plz         | ort                  | sperre 
	 *	number      | char                           | char                           | number      | char                 | number 
	 *	------------+--------------------------------+--------------------------------+-------------+----------------------+-------
     *     		  1 | Fahrrad Shop                   | Obere Regenstr. 4              |       93059 | Regensburg           |      0 
     *            2 | Zweirad-Center Staller         | Kirschweg 20                   |       44267 | Dortmund             |      0 
     *            3 | Maier Ingrid                   | Universit�tsstr. 33            |       93055 | Regensburg           |      1 
     *            4 | Rafa - Seger KG                | Liebigstr. 10                  |       10247 | Berlin               |      0 
     *            5 | Biker Ecke                     | Lessingstr. 37                 |       22087 | Hamburg              |      0 
     *            6 | Fahrr�der Hammerl              | Schindlerplatz 7               |       81739 | M�nchen              |      0 
	 * 
	 * @param set
	 * @throws SQLException
	 */
	public static void printResult(ResultSet set) throws SQLException {
    	
    	ResultSetMetaData md = set.getMetaData();
    	int columnsCount = md.getColumnCount();
    	
    	// column width
    	int[] columnWidth = new int[columnsCount];
        for (int i = 0; i < columnWidth.length; i++) {
            columnWidth[i] = Math.max(md.getColumnDisplaySize(i + 1), md.getColumnLabel(i + 1).length());
        }
        
        //  attributes
        for (int i = 1; i <= columnsCount; i++) {
            if (i > 1) System.out.print("| ");
            String columnName = md.getColumnName(i);
            columnName = Utils.concat(columnName, columnWidth[i - 1]);
            System.out.print(columnName + " ");
        }
        System.out.println();

        //  type
        set.next();
        for (int i = 1; i <= columnsCount; i++) {
            if (i > 1) System.out.print("| ");
            String columnName = set.getString(i);
            String newColumnName = "char";
            if (Utils.isInt(columnName)) {
                newColumnName = "number";
            }
            newColumnName = Utils.concat(newColumnName, columnWidth[i - 1]);
            System.out.print(newColumnName + " ");
        }
        System.out.println();


        // dash "-"
        for (int i = 1; i <= columnsCount; i++) {
            if (i > 1) System.out.print("-+-");
            String result = String.format("%-" + columnWidth[i - 1] + "s", "-").replace(' ', '-');
            result = Utils.concat(result, columnWidth[i - 1]);
            System.out.print(result);
        }
        System.out.println();

        // table 
        do {
            for (int i = 1; i <= columnsCount; i++) {
                if (i > 1) System.out.print("| ");
                String columnValue = set.getString(i);
                columnValue = Utils.concat(columnValue, columnWidth[i - 1]);
                System.out.print(columnValue + " ");
            }
            System.out.println();
        } while (set.next());
    	
    }
	
	/**
	 * " SELECT * FROM {table} " query
	 * @param table
	 * @param db
	 */
	public static void selectAll(String table, Database db) {
		selectAll(new ArrayList<String>(Arrays.asList(new String[]{table})), db);
    }

	/**
	 * " SELECT * FROM {table} " query for all tables
	 * @param table
	 * @param db
	 */
    public static void selectAll(List<String> tables, Database db) {
    	db.executeInTransaction(() -> {
    		Statement stmt = db.getConnection().createStatement();
            for (String table : tables) {
                String select = "SELECT * FROM " + table + ";";
                ResultSet set = stmt.executeQuery(select);
                System.out.println("Table: " + table);
                printResult(set);
            }
            stmt.close();
            return true;
    	});
        
    }
    
    /**
     * Makes custom SQL query
     * 
     * @param query
     * @param db
     */
    public static void customQuery(String query, Database db) {
    	db.executeInTransaction(() -> {
    		Statement stmt = db.getConnection().createStatement();
            ResultSet set = stmt.executeQuery(query);
            printResult(set);
            stmt.close();
            return true;
    	});
    }
    
    public static void customUpdate(String query, Database db) {
    	db.executeInTransaction(() -> {
    		Statement stmt = db.getConnection().createStatement();
            stmt.executeUpdate(query);
            stmt.close();
            return true;
    	});
    }
    
    /**
     * Makes custom query in prepared statement form because of protecting from SQL-injections
     * - It must be minimum one prepared statement
     * 
     * @param query SQL query
     * @param parameter Parameter for statement
     * @param db
     */
    public static void customQuery(String query, String parameter, Database db) {
    	db.executeInTransaction(() -> {
    		PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setString(1, parameter);
            stmt.addBatch();
            ResultSet set = stmt.executeQuery();
            DatabaseUtils.printResult(set);
            stmt.close();
            return true;
    	});
    }
}
