package ebiNeutrinoSDK.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import ebiNeutrinoSDK.EBIPGFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ebicrm
 * Date: 08.06.2010
 * Time: 20:47:39
 * To change this template use File | Settings | File Templates.
 */

public class EBIPessimisticLocking {

    private EBIPGFactory ebiPGFactory = null;
    
    public EBIPessimisticLocking(EBIPGFactory ebiFactory){
       this.ebiPGFactory = ebiFactory;
    }


    public boolean lockRecord(int id, String table){
       boolean success = true;
        try {
            PreparedStatement ps = ebiPGFactory.database.initPreparedStatement("INSERT INTO EBIPESSIMISTIC (RECORDID,MODULENAME,LOCKDATE,STATUS,USER) values (?,?,?,?,?) ");
            ps.setInt(1,id);
            ps.setString(2,table);
            ps.setTimestamp(3, new Timestamp(new Date().getTime()));
            ps.setInt(4,1);
            ps.setString(5,EBIPGFactory.ebiUser);
            ebiPGFactory.database.executePreparedStmt(ps);

        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean unlockRecord(int id, String table){
       boolean success;
        try {

            PreparedStatement ps = ebiPGFactory.database.initPreparedStatement(" SELECT RECORDIS,MODULENAME,USER FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=? AND USER=?");
            ps.setInt(1,id);
            ps.setString(2,table);
            ps.setString(3,EBIPGFactory.ebiUser);
            ResultSet set =  ebiPGFactory.database.executePreparedQuery(ps);

            set.last();
            if(set.getRow() > 0){
                ps = ebiPGFactory.database.initPreparedStatement(" DELETE FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=? ");
                ps.setInt(1,id);
                ps.setString(2,table);
                success = ebiPGFactory.database.executePreparedStmt(ps);

            }else{
                success = false;
            }
        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean forceUnlock(int id, String table){
        boolean success;

        try {

            PreparedStatement ps = ebiPGFactory.database.initPreparedStatement(" DELETE FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=? ");
            ps.setInt(1,id);
            ps.setString(2,table);
            success = ebiPGFactory.database.executePreparedStmt(ps);

        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }

    public boolean isRecordLocked(int id, String table){
        boolean success = false;
        try {

            PreparedStatement ps = ebiPGFactory.database.initPreparedStatement(" SELECT RECORDIS,MODULENAME,USER FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=? ");
            ps.setInt(1,id);
            ps.setString(2,table);
            ResultSet set =  ebiPGFactory.database.executePreparedQuery(ps);

            set.last();
            if(set.getRow() > 0){
                success = true;
            }

        } catch (SQLException e) {
            success = false;
            e.printStackTrace();
        }

       return success;
    }

}
