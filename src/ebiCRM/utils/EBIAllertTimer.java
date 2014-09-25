package ebiCRM.utils;

import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;

public class EBIAllertTimer extends TimerTask {
	
	private EBICRMModule ebiModule = null;
	private Timer timer = null;
	private EBICRMTaskItem taskItem= null;
	public static int countTask = 0;
	
	public EBIAllertTimer(EBICRMModule modul){
		this.ebiModule = modul;
	}

	
	public synchronized void setUpAvailableTimer(){
	    // this is a very complicated method it do a recursion to itself and call the method run auf specified interval
		ResultSet set = null;
		Calendar date1 = GregorianCalendar.getInstance();
		date1.setTime(new Date());
		date1.set(Calendar.HOUR_OF_DAY, 0);

		Calendar date2 = GregorianCalendar.getInstance();
		date2.setTime(new Date());
		date2.set(Calendar.HOUR_OF_DAY, 23);
		
		try{
			ebiModule.ebiPGFactory.getMainFrame().panAllert.removeAll();
			ebiModule.ebiPGFactory.getMainFrame().panAllert.add(new JLabel(new ImageIcon("images/mieten.png")));
			ebiModule.ebiPGFactory.getMainFrame().pallert.setVisible(false);
			ebiModule.ebiPGFactory.getMainFrame().panAllert.setVisible(false);
			ebiModule.ebiPGFactory.getMainFrame().pallert.updateUI();
			ebiModule.ebiPGFactory.getMainFrame().panAllert.updateUI();

            countTask=0;
			//PreaparedStatement
			PreparedStatement ps = ebiModule.ebiPGFactory.
							getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYACTIVITIES " +
																			" WHERE DUEDATE BETWEEN ? AND ? AND TIMERDISABLED=? AND CREATEDFROM=? ORDER BY TIMESTAMP(DUEDATE) DESC");

			ps.setTimestamp(1, new java.sql.Timestamp(date1.getTimeInMillis()));
			ps.setTimestamp(2, new java.sql.Timestamp(date2.getTimeInMillis()));
			ps.setInt(3, 0);
			ps.setString(4, EBIPGFactory.ebiUser);

			set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);

			set.last();
			if(set.getRow() > 0){
				set.beforeFirst();
				while(set.next()){
					EBIAllertTimer timer = new EBIAllertTimer(ebiModule);
					timer.setTimer(new Timer());
					EBICRMTaskItem taskItem = new EBICRMTaskItem(ebiModule);
					taskItem.setTimer(timer.getTimer());
					taskItem.setCompanyId(set.getInt("COMPANYID"));
					taskItem.setId(set.getInt("ACTIVITYID"));
					taskItem.setDueDate(set.getTimestamp("DUEDATE"));
					taskItem.setDuration(set.getInt("DURATION"));
					taskItem.setTaskName(set.getString("ACTIVITYNAME"));
					taskItem.setTaskMessage(set.getString("ACTIVITYDESCRIPTION"));
                    taskItem.setBackgroundColor(set.getString("ACOLOR"));
                    timer.setTaskItem(taskItem);
                    ebiModule.ebiPGFactory.getMainFrame().panAllert.add(taskItem);
                    run();
					timer.getTimer().schedule(timer, new Date(set.getTimestamp("DUEDATE").getTime()-((1000 * 60) * set.getInt("TIMERSTART"))));
                    countTask++;
				}
			}else{
                run();
            }
			set.close();

		}catch(SQLException ex){
			ex.printStackTrace();
		}finally{
			if(set != null){
				try {
					set.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	
	public synchronized void run(){

		 if(!ebiModule.ebiPGFactory.getMainFrame().panAllert.isVisible()){

			 ebiModule.ebiPGFactory.getMainFrame().panAllert.setVisible(true);
			 ebiModule.ebiPGFactory.getMainFrame().pallert.setVisible(true);

         }

         if(getTaskItem() != null){
             getTaskItem().setVisible(true);
         }

         if(ebiModule.ebiPGFactory.getMainFrame().statusBar != null && ebiModule.ebiPGFactory.getMainFrame() != null){
             ebiModule.ebiPGFactory.getMainFrame().statusBar.updateUI();
         }

         if(countTask <= 0){
            ebiModule.ebiPGFactory.getMainFrame().pallert.setPreferredSize(new Dimension(ebiModule.ebiPGFactory.getMainFrame().getWidth()-230,20));
         }else if(countTask >= 1){
            ebiModule.ebiPGFactory.getMainFrame().pallert.setPreferredSize(new Dimension(ebiModule.ebiPGFactory.getMainFrame().getWidth()-230,48));
         }


            try{
                 if(ebiModule.ebiPGFactory.getMainFrame().pallert != null && ebiModule.ebiPGFactory.getMainFrame() != null){
                    ebiModule.ebiPGFactory.getMainFrame().pallert.updateUI();
                    ebiModule.ebiPGFactory.getMainFrame().panAllert.updateUI();
                 }
            }catch(NullPointerException ex){}
         ebiModule.ebiPGFactory.getMainFrame().repaint();
	}


	public Timer getTimer() {
		return timer;
	}


	public void setTimer(Timer timer) {
		this.timer = timer;
	}


	public EBICRMTaskItem getTaskItem() {
		return taskItem;
	}


	public void setTaskItem(EBICRMTaskItem taskItem) {
		this.taskItem = taskItem;
	}
	

}
