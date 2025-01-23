package db.mysql;

import java.util.Set;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import db.DBConnection;
import entity.logRecord;
import entity.logRecord.logRecordBuilder;
import entity.machine;
import entity.machine.machineBuilder;

public class MySQLConnection implements DBConnection {
	private Connection conn;
	
	public MySQLConnection() { 
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public void close() {
		if (conn != null) { 
			try {
				conn.close(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

	@Override
	public boolean registerMachine(machine mc) {
		if (conn == null) {return false;}
		if (ipExist(mc.getIp())) {return false;}
		try {
			String query = "INSERT IGNORE INTO machines VALUES(?, ?, ?, ?, ?)";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, mc.getName());
			statement.setString(2, mc.getIp());
			statement.setString(3, mc.getHr());
			statement.setString(4, mc.getMin());
			statement.setBoolean(5, mc.isStatus());
	
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void removeMachine(String ip) {
		if (conn == null) {return;}
		try {
			String query = "DELETE FROM machines WHERE ip = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, ip);
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void toggleMachine(String ip) {
		if (conn == null) {return;}
		try {
			String query = "UPDATE machines SET status = NOT status WHERE ip = ?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setString(1, ip);
			
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<machine> getMachines() {
		if (conn == null) {return new HashSet<> ();}
		Set<machine> machines = new HashSet<> ();
		try {
			String sql = "SELECT * from machines";
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next() ) {
				String name = rs.getString("name");
				String ip = rs.getString("ip");
				String hr = rs.getString("hr");
				String min = rs.getString("min");
				boolean status = rs.getBoolean("status");
				machineBuilder mcbuilder = new machineBuilder();
				mcbuilder.setName(name);
				mcbuilder.setIp(ip);
				mcbuilder.setHr(hr);
				mcbuilder.setMin(min);
				mcbuilder.setStatus(status);
				
				machines.add(mcbuilder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return machines;
	}

	@Override
	public String getFullname(String username) {
		if (conn == null) {return null;}
		String name = "";
		try {
			String sql = "SELECT fname, lname FROM users WHERE username = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				name = String.join(" ", rs.getString("fname"), rs.getString("lname"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return name;
	}

	@Override
	public boolean verifyLogin(String username, String password) {
	    if (conn == null) return false;
	    try {
	        // Fetch the hashed password from the database
	        String sql = "SELECT password FROM users WHERE username = ?";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        statement.setString(1, username);
	        ResultSet rs = statement.executeQuery();
	        if (rs.next()) {
	            // Retrieve the hashed password from the result set
	            String storedHashedPassword = rs.getString("password");
	            // Hash the user input password
	            String hashedPassword = hashStr(password);
	            // Compare the hashed passwords
	            return storedHashedPassword.equals(hashedPassword);
	        }
	    } catch (Exception e) {
	        System.out.println(e.getMessage());
	    }
	    return false;
	}
	
	@Override
	public boolean createAccount(String username, String password, String fname, String lname, String key) {
	    if (conn == null) return false;
	    if (username == "" || password == "" || fname == "" || lname == "") return false;
	    if (!isKeyEqual(key)) return false;
	    if (usernameExist(username)) return false;
	    try {
	        // Hash the password
	        String hashedPassword = hashStr(password);
	        
	        // Insert the user details into the database
	        String sql = "INSERT INTO users (username, password, fname, lname) VALUES (?, ?, ?, ?)";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        statement.setString(1, username);
	        statement.setString(2, hashedPassword); // Store the hashed password
	        statement.setString(3, fname);
	        statement.setString(4, lname);
	        statement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // Return false if an exception occurs
	    }
	    return true;
	}
	
	@Override
	public void updateKey(String key) {
	    if (conn == null) return;
	    try {
	        // Drop old key
	        String sql = "DELETE FROM AccountKey;";
	        PreparedStatement statement1 = conn.prepareStatement(sql);
	        statement1.execute();

	        // Hash new key
	        String hashedKey = hashStr(key);

	        // Insert new key
	        sql = "INSERT INTO AccountKey (key_value) VALUES (?);"; // Replace key_column_name with the actual column name
	        PreparedStatement statement2 = conn.prepareStatement(sql);
	        statement2.setString(1, hashedKey);
	        statement2.execute();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void writeLog(machine item, String status) {
		if (conn == null) return;
		PreparedStatement statement = null;

        try {
            String tableName = "log_" + getCurrentDate();

            // Ensure the table exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "name VARCHAR(50),"
                    + "ip VARCHAR(50),"
                    + "hr VARCHAR(2),"
                    + "min VARCHAR(2),"
                    + "status VARCHAR(10)"
                    + ")";
            statement = conn.prepareStatement(createTableSQL);
            statement.executeUpdate();
            statement.close();

            // Insert log record
            String insertSQL = "INSERT INTO " + tableName + " (name, ip, hr, min, status) VALUES (?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(insertSQL);
            statement.setString(1, item.getName());
            statement.setString(2, item.getIp());
            statement.setString(3, item.getHr());
            statement.setString(4, item.getMin());
            statement.setString(5, status);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
	
	public void closeLogTable() {
		if (conn == null) return;
        LocalDate currentDate = LocalDate.now();
        LocalDate targetDate = currentDate.minusDays(7);
        String tableName = "log_" + targetDate.toString();

        String closeTableSQL = "ALTER TABLE " + tableName + " CLOSE";
        
        try ( 
        	PreparedStatement statement = conn.prepareStatement(closeTableSQL)) {
            statement.executeUpdate();
            System.out.println("Closed log table: " + tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public Set<logRecord> getLog(String tableName) {
		if (conn == null) return null;
		Set<logRecord> records = new HashSet<> ();
		
		try {
			String sql = "SELECT * FROM " + tableName;
			PreparedStatement statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next() ) {
				String name = rs.getString("name");
				String ip = rs.getString("ip");
				String hr = rs.getString("hr");
				String min = rs.getString("min");
				String status = rs.getString("status");
				logRecordBuilder lrbuilder= new logRecordBuilder();
				lrbuilder.setName(name);
				lrbuilder.setIp(ip);
				lrbuilder.setHr(hr);
				lrbuilder.setMin(min);
				lrbuilder.setStatus(status);
				
				records.add(lrbuilder.build());
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return records;
	}

	
	// Helper function
	/**
	 * Search if the username already exist
	 * @param username
	 * @return true if exist; false otherwise 
	 */
	public boolean usernameExist(String username) {
		if (conn == null) return false;
		try {
			String sql = "SELECT username FROM users WHERE username = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, username);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Search if the ip already exist
	 * @param ip
	 * @return true if exist; false otherwise 
	 */
	public boolean ipExist(String ip) {
		if (conn == null) return false;
		try {
			String sql = "SELECT ip FROM machines WHERE ip = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, ip);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * search if key valid
	 * @param key
	 * @return true if valid; false otherwise
	 */
	public boolean isKeyEqual(String key) {
	    if (conn == null) return false;
	    try {
	        String hashedKey = hashStr(key);
	        String sql = "SELECT key_value FROM AccountKey";
	        PreparedStatement statement = conn.prepareStatement(sql);
	        ResultSet rs = statement.executeQuery();
	        if (rs.next()) {
	            String storedKey = rs.getString("key_value");
	            return hashedKey.equals(storedKey);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	/**
	 * hash password using SHA3-256
	 * @param password
	 * @return hashed value
	 */
    private String hashStr(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        return sdf.format(new Date());
    }

}
