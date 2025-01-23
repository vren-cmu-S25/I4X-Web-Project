package db.mysql;

import java.sql.DriverManager; 
import java.sql.SQLException; 
import java.sql.Statement; 
import java.sql.Connection;

public class MySQLTableCreation {
	// Run this as Java application to reset db schema. 
	public static void main(String[] args) {
		try {
			// Ensure the driver is imported.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			// This is java.sql.Connection. Not com.mysql.jdbc.Connection. 
			Connection conn = null;
			
			// Step 1 Connect to MySQL. 
			try {
				System.out.println("Connecting to \n" + MySQLDBUtil.URL);
				conn = DriverManager.getConnection(MySQLDBUtil.URL); 
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage()); 
				System.out.println("SQLState " + e.getSQLState()); 
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return; 
			}
			
			
			// Step 2 Drop tables in case they exist. 
			Statement stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS machines"; 
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS users"; 
			stmt.executeUpdate(sql);
			sql = "DROP TABLE IF EXISTS AccountKey"; 
			stmt.executeUpdate(sql);


			// Step 3. Create new tables.
			sql = "CREATE TABLE machines " + "(name VARCHAR(255), " + "ip VARCHAR(50) NOT NULL, " 
			+ "hr VARCHAR(2), " + "min VARCHAR(2), " + "status BIT, " + " PRIMARY KEY ( ip ))"; 
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE users " + "(username VARCHAR(255) NOT NULL, " + "password VARCHAR(255) NOT NULL, " 
			+ " fname VARCHAR(255), lname VARCHAR(255), "+ " PRIMARY KEY ( username ))";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE AccountKey " + "(key_value VARCHAR(255) NOT NULL, " + " PRIMARY KEY ( key_value ))";
			stmt.executeUpdate(sql);
		
			// Step 4: insert data
			// Create a fake user
			sql = "INSERT INTO users " + "VALUES (\"m1ren\",\"1234\", \"Vincent\", \"Ren\")";		// should be hash value of password
			System.out.println("Executing query:\n" + sql); 
			stmt.executeUpdate(sql);
			
			System.out.println("Import is done successfully.");
		} catch (Exception e) {
				System.out.println(e.getMessage());
		}
	}
}
