package db;
 
import java.util.Set;
import entity.machine;
import entity.logRecord;

public interface DBConnection {
	/**
	* Close the connection.
	*/
	public void close();
	
	/**
	* Register a new machine
	* @param mc 
	* @return true if success; false if the ip already exist*/
	public boolean registerMachine(machine mc);
	
	/**
	* Remove a machine given machine ip
	* @param ip */
	public void removeMachine(String ip);
	
	/**
	 * Toggle machine status.
	 * @param ip
	 */
	public void toggleMachine(String ip);
	
	
	/**
	* Get all registered machines. *
	* @return a set of machines*/
	public Set<machine> getMachines();

	/**
	 * Get full name of a user.
	 * @param username
	 * @return full name of the user */
	public String getFullname(String username);

	/**
	 * Return whether the credential is correct.
	 * @param username
	 * @param password
	 * @return boolean */
	public boolean verifyLogin(String username, String password);
	
	/**
	 * Create a new account
	 * @param username
	 * @param password
	 * @param fname
	 * @param lname
	 * @param key
	 * @return true: success; false: username already exist or key incorrect
	 */
	public boolean createAccount(String username, String password, String fname, String lname, String key);
	
	/**
	 * update
	 * @param key
	 */
	public void updateKey(String key);
	
	/**
	 * write log
	 * @param item
	 * @param status
	 */
	public void writeLog(machine item, String status);
	
    /**
     * Close the log table that is 7 days old.
     */
    public void closeLogTable();
    
    /**
     * download Log Table
     * @param tableName
     * @return list of records
     */
    public Set<logRecord> getLog(String tableName);
};
