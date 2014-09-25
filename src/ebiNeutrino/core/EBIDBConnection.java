package ebiNeutrino.core;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.SwingUtilities;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIDatabase;


public class EBIDBConnection implements IEBIDatabase {
	
	public Connection conn = null;
	public Statement stmt = null; 
	private String connectionUrl = null;
    private String user = null;
    private String password = null;
    private String driver = null;
    public static boolean toUpperCase=true;
    private EBIPGFactory ebiPGFunction = null;
    private boolean isAutocommit = false;

	public EBIDBConnection(EBIPGFactory main){
        this.ebiPGFunction = main;
    }
	
	/**
	 * connect to a database system
	 * @param driver
	 * @param host
	 * @param db
	 * @param password
	 * @param user
	 * @param dbType
	 * @param SID
	 * @return
	 */
	
	public boolean connect(String driver,String host,String db,String password,String user,String dbType,String SID,String toUpper){
	    try {
              this.user = user.trim();
              this.password = password.trim();
              this.driver = driver;
	          Class.forName(this.driver).newInstance();
	          connectionUrl = null;

              if("yes".equals(toUpper.toLowerCase())){ 
                 toUpperCase = true;
                 db = db.toUpperCase();
              }else {
                 db = db.toLowerCase();
              }

	          if("mysql".equals(dbType)){
	        	  connectionUrl = "jdbc:"+dbType+"://"+host+"/"+db.trim()+"?useUnicode=true&connectTimeout=0&socketTimeout=0&interactiveClient=true&reconnectAtTxEnd=true&autoReconnect=true&tcpKeepAlive=true&characterEncoding=utf8&jdbcCompliantTruncation=false&zeroDateTimeBehavior=round";

	          }else if("oracle".equals(dbType)){
	        	  connectionUrl = "jdbc:"+dbType+":thin:@"+host+":"+SID.trim();
	          }else{
                  return false;
              }

              conn =  DriverManager.getConnection(connectionUrl, this.user, this.password);

        } catch (SQLException ex) {
            ex.printStackTrace();
            EBIPGFactory.logger.error("Error connection to the database", ex.fillInStackTrace());
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            EBIPGFactory.logger.error("Error connection to the database", ex.fillInStackTrace());
            return false;
        }
        return true;
	}

	/**
	 * reconnect to a database
	 * @return
	 */
    private boolean reconnect(){
        try {
              Class.forName(this.driver).newInstance();
              conn = DriverManager.getConnection(connectionUrl, this.user, this.password);
              ebiPGFunction.setIEBIDatabase(this);

        } catch (SQLException ex) {
           exceptionHandle(ex);
            EBIPGFactory.logger.error("Error connection to the database", ex.fillInStackTrace());
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            EBIPGFactory.logger.error("Error connection to the database", ex.fillInStackTrace());
            return false;
        }
        return true;
    }

	/**
	 * Execute SQL Query
	 * @param query
	 * @return  SQL ResultSet
	 */
	public synchronized ResultSet execute(String query) throws SQLException {
		ResultSet rs = null;
		try {

             stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
             if (stmt.execute(query)) {
                rs = stmt.getResultSet();
             }

		} catch(SQLException ex) {
           exceptionHandle(ex);
		}
	
		return rs;
    }

	/**
	 * Execute SQL Query 
	 * @param query
	 * @return return true if query is executed or otherwise false 
	 */

	public synchronized boolean exec(String query) throws SQLException{
		boolean ret =true; 
		try {
                setAutoCommit(true);
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				stmt.executeUpdate(query);
				stmt.close();

		} catch(SQLException ex) {
			exceptionHandle(ex);
            ret = false;
		}finally {
            setAutoCommit(false);
        }

		return ret;
	}

    /**
      * Execute SQL Query
      * @param query
      * @return String Exception as string otherwise empty string
    */

	public synchronized void execExt(String query) throws SQLException{
		try {
                setAutoCommit(true);
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				stmt.executeUpdate(query);
				stmt.close();

		} catch(SQLException ex) {
            exceptionHandle(ex);
		}finally {
            setAutoCommit(false);
        }
	}


	/**
	 * create new PreparedStatement
	 * @param query
	 * @return Preparedstatement 
	 */
	public synchronized PreparedStatement initPreparedStatement(String query){
	  PreparedStatement ps = null;	
		try{
            if(toUpperCase){
                query = query.toUpperCase();
            }else{
               query = query.toLowerCase();
            }

		    ps = conn.prepareStatement(query,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		}catch(SQLException ex){
			exceptionHandle(ex);
		}
		return ps;
	}

    public boolean isValidConnection(){
        try {
            if(!conn.isClosed()){
              return true;
            }
        } catch (SQLException e) {
           exceptionHandle(e);
        }
       return false;
    }

	/**
	 * Execute preparedstatement
	 * @param ps
	 * @return generated key (id)
	 */
	public synchronized String executePreparedStmtGetKey(PreparedStatement ps){
		ResultSet key;
		String gkey = "";
		try{
			   ps.execute();
			   key = ps.getGeneratedKeys();
			   key.next();
			   gkey = key.getString(1);
			   ps.close();
        }catch(SQLException ex){
           exceptionHandle(ex);
        }
		 return gkey;
	}
	
	/**
	 * Execute preparedstatement
	 * @param ps
	 * @return return true if the preparedstatement is successfully executed
	 */

	public synchronized boolean executePreparedStmt(PreparedStatement ps){
		try{
			   ps.execute();
			   ps.close();
		   }catch(SQLException ex){			   
			  exceptionHandle(ex);
              return false;
		   }

		   return true;
		   
	}
	/**
	 * Execute a query with a PreperadStatment
	 * @param ps  Preparedstatement
	 * @return return ResultSet if successfully or null
	 */
	public synchronized ResultSet executePreparedQuery(PreparedStatement ps){
		ResultSet set  = null;   
		try{
		    set = ps.executeQuery();
        }catch(SQLException ex){
          exceptionHandle(ex);
        }
		return set;
	}
	
	/**
	 * Close a ResultSet
	 * @param set
	 */
	public synchronized void closeResultSet(ResultSet set ){
		try{
			stmt.close();
			set.close();
			
		}catch(SQLException ex){
            exceptionHandle(ex);
		}
	}
	
    /**
     * Enable autocommit 
     */
    public void setAutoCommit(boolean autocommit){
    	try{
    		conn.setAutoCommit(autocommit);
            isAutocommit = autocommit;
    	}catch(SQLException ex){
    		exceptionHandle(ex);
    	}
    }

    public boolean isAutoCommit(){
       return isAutocommit; 
    }

    /**
     * Return an active connection
     * @return
     */
    public Connection getActiveConnection(){
      return conn;    	
    }

	/**
	 * Set an active database connection
	 * @param con
	 */
    public void setActiveConnection(Connection con){
      this.conn = con;   	
    }

    /**
     * Handle an Exception to reconnect
     * @param ex
     */
    private void exceptionHandle(SQLException ex) {

        String ext = "\nSQLException: " + ex.getMessage();
               ext+="\nSQLState: " + ex.getSQLState();
               ext+="\nVendorError: " + ex.getErrorCode();
               ext+="\nSQL: "+ex.getSQLState();
               ex.printStackTrace();
               if(ex.getErrorCode() != 238){
                   try {
                      if(conn.isClosed() && ex.getSQLState().equals("08S01")){
                          try {
                              SwingUtilities.invokeAndWait( new Runnable() {
                                        public void run() {
                                            reconnect();    // Try to fix host database problem with wait_timeout
                                            //ebiPGFunction.mng.closeOpenedDialogs();
                                            //ebiPGFunction.addLoginModul();
                                            //ebiPGFunction.mng.releaseModule();
                                        }
                                      });
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          } catch (InvocationTargetException e) {
                              e.printStackTrace();
                          }

                          EBIExceptionDialog.getInstance("The database server has cut the connection! \n EBI Neutrino R1 is trying to reconnect\n\n Please ask your Administrator to upgrade the timeout variable! ").Show(EBIMessage.INFO_MESSAGE);
                      }else{
                        EBIExceptionDialog.getInstance(ext).Show(EBIMessage.ERROR_MESSAGE);
                      }
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }else{ // If session expired reconnect
                  try {
                         SwingUtilities.invokeAndWait( new Runnable() {
                                        public void run() {
                                            reconnect();    // Try to fix host database problem with wait_timeout
                                            //ebiMain.mng.closeOpenedDialogs();
                                            //ebiMain.addLoginModul();
                                            //ebiMain.mng.releaseModule();
                                        }
                                      });
                          } catch (InterruptedException e) {
                              e.printStackTrace();
                          } catch (InvocationTargetException e) {
                              e.printStackTrace();
                          }
                  
               }

    }

}