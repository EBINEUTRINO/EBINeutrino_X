package ebiNeutrinoSDK.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Description:
 * This Dialog setup the EBI Neutrino Database
 *
 * @author Francesco Bramante
 * The IEBIDatabase offer the standard sql methods for manipulate the database
 *
 */


public interface IEBIDatabase {

	/**
	 * Connecting to the database
	 * @param Driver
	 * @param host
	 * @param db
	 * @param Password
	 * @param user
	 * @param dbType
	 * @param SID
	 * @return
	 */
	public boolean connect(String Driver,String host,String db,String Password,String user,String dbType,String SID,String useUpperCase);



    public boolean isValidConnection();

	/**
	 * 
	 * @param autocommit
	 */
    public void setAutoCommit(boolean autocommit);

    /**
     *
     * @return  true if autoCommit enabled
     */

    public boolean isAutoCommit();
    
    /**
     * Return the active connection
     * @return
     */
    
    public Connection getActiveConnection();
    
	/**
	 * Set the active connection
	 * @param con
	 */
    public void setActiveConnection(Connection con);
    
	/**
	 * Execute SQL Query and return a ResultSet
	 * @param query
	 * @return
	 */
	public ResultSet execute(String query) throws SQLException;

	
	/**
	 * Execute SQL Query 
	 * @param query
	 * @return return true if query is executed or otherwise false 
	 */
	public boolean exec(String query) throws SQLException;


    /**
      * Execute SQL Query
      * @param query
    */

	public void execExt(String query) throws SQLException;

	/**
	 * This method create new PreparedStatement
	 * @param query
	 * @return Preparedstatement 
	 */
	public PreparedStatement initPreparedStatement(String query);
	/**
	 * this method execute the one preparedstatement
	 * @param ps
	 * @return generated key (id)
	 */
	public String executePreparedStmtGetKey(PreparedStatement ps);
	
	/**
	 * this method execute the one preparedstatement
	 * @param ps
	 * @return return true if the preparedstatement is successfully executed otherwise false
	 */
	
	public boolean executePreparedStmt(PreparedStatement ps);
	/**
	 * this method execute the one preparedstatement
	 * @param ps  Preparedstatement
	 * @return return ResultSet if successfully or null
	 */
	public ResultSet executePreparedQuery(PreparedStatement ps);
    
	/**
	 * Close the specified ResultSet and Statement
	 * @param set
	 */
	public void closeResultSet(ResultSet set );
}
