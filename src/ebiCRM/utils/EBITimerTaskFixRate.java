package ebiCRM.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;

import javax.swing.*;

public class EBITimerTaskFixRate extends TimerTask {

	private EBICRMModule ebiModule = null;
	
	public EBITimerTaskFixRate(EBICRMModule module){
		ebiModule = module;
	}
	
	public void run(){
		ResultSet set = null;
		Calendar date1 = GregorianCalendar.getInstance();
		date1.setTime(new Date());
		date1.set(Calendar.HOUR_OF_DAY, 0);
		
		Calendar date2 = GregorianCalendar.getInstance();
		date2.setTime(new Date());
		date2.set(Calendar.HOUR_OF_DAY, 23);
		
		try{

			PreparedStatement ps = ebiModule.ebiPGFactory.
					getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYACTIVITIES " +
																	" WHERE DUEDATE BETWEEN ? AND ? AND TIMERDISABLED=? AND CREATEDFROM=?");

			ps.setTimestamp(1, new java.sql.Timestamp(date1.getTimeInMillis()));
			ps.setTimestamp(2, new java.sql.Timestamp(date2.getTimeInMillis()));
			ps.setInt(3, 0);
			ps.setString(4, EBIPGFactory.ebiUser);

			set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);

			set.last();

			if(set.getRow() != EBIAllertTimer.countTask){
                ebiModule.allertTimer.setUpAvailableTimer();
                ebiModule.allertTimer.run();
			}
            if(!set.isClosed()){
			    set.close();
            }
            try{
                ebiModule.checkIslocked(ebiModule.companyID,true);
            }catch(Exception ex){}
		}catch(SQLException ex){
			ex.printStackTrace();
		}
			
	}
	

}
