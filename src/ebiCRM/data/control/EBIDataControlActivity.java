package ebiCRM.data.control;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMCompanyActivity;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyactivities;
import ebiNeutrinoSDK.model.hibernate.Companyactivitiesdocs;

import javax.swing.*;

public class EBIDataControlActivity {

    private Companyactivities companyActivity = null;  
    private EBICRMCompanyActivity activityPane = null;

    public EBIDataControlActivity(EBICRMCompanyActivity activityPane) {
        this.activityPane = activityPane;
        companyActivity = new Companyactivities();
    }

    public boolean dataStore(boolean isEdit) {
        try {
            activityPane.ebiModule.ebiContainer.showInActionStatus("Activity",true);
            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            if (isEdit == true) {
                createHistory(activityPane.ebiModule.ebiPGFactory.company);
                companyActivity.setChangeddate(new Date());
                companyActivity.setChangedfrom(EBIPGFactory.ebiUser);
            } else {
                companyActivity.setCreateddate(new Date());
                activityPane.isEdit = true;
            }
            
            companyActivity.setCreatedfrom(activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").getCreatedFrom());
         
            companyActivity.setCompany(activityPane.ebiModule.ebiPGFactory.company);
            companyActivity.setActivityname(activityPane.ebiModule.guiRenderer.getTextfield("activityNameText","Activity").getText());
            if(activityPane.ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").getSelectedItem() != null){
                companyActivity.setActivitytype(activityPane.ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").getSelectedItem().toString());
            }
            companyActivity.setTimerdisabled(activityPane.ebiModule.guiRenderer.getCheckBox("timerActiveBox", "Activity").isSelected() ? 1 : 0);
            
            int tstart =0;
            if(activityPane.ebiModule.
                    guiRenderer.getComboBox("timerStartText", "Activity").getSelectedItem() != null){
                        if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(activityPane.ebiModule.
                                            guiRenderer.getComboBox("timerStartText", "Activity").getSelectedItem().toString())){

                            try{

                                tstart = Integer.parseInt(activityPane.ebiModule.
                                                guiRenderer.getComboBox("timerStartText", "Activity").getSelectedItem().toString().split(" ")[0]);

                            }catch(NumberFormatException ex){ }
                        }
            }
            companyActivity.setTimerstart(tstart);

            if(activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").getSelectedItem() != null){
                if (activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").getSelectedIndex() != 0) {
                    companyActivity.setActivitystatus(activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").getSelectedItem().toString());
                }
            }
            
            if(activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getDate() != null){

                Calendar eDate = new GregorianCalendar();
                eDate.setTime(activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getDate());
                eDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(activityPane.ebiModule.guiRenderer.getSpinner("dueH","Activity").getValue().toString()));
                eDate.set(Calendar.MINUTE, Integer.parseInt(activityPane.ebiModule.guiRenderer.getSpinner("dueMin","Activity").getValue().toString()));
                eDate.set(Calendar.SECOND, 0);
                eDate.set(Calendar.MILLISECOND, 0);
                companyActivity.setDuedate(eDate.getTime());
            }

            companyActivity.setDuration(Integer.parseInt(activityPane.ebiModule.guiRenderer.getTextfield("durationText","Activity").getText()));

            StringBuffer color = new StringBuffer();
            color.append(activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").getBackground().getRed());
            color.append(",");
            color.append(activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").getBackground().getGreen());
            color.append(",");
            color.append(activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").getBackground().getBlue());

            companyActivity.setAcolor(color.toString());
            
            companyActivity.setActivitydescription(activityPane.ebiModule.guiRenderer.getTextarea("activityDescription","Activity").getText());

            if(!companyActivity.getCompanyactivitiesdocses().isEmpty()){
                Iterator iter = companyActivity.getCompanyactivitiesdocses().iterator();
                while(iter.hasNext()){
                    Companyactivitiesdocs  docs = (Companyactivitiesdocs) iter.next();
                    if(docs.getActivitydocid() < 0){ docs.setActivitydocid(null);}
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(docs);
                }
            }

            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(companyActivity);

            activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiSave",true);
            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            //dataShow(true);
            dataShowDoc();
            activityPane.ebiModule.ebiContainer.showInActionStatus("Activity",false);
            activityPane.ebiModule.allertTimer.setUpAvailableTimer();
            
            if(!isEdit){
            	activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setID(companyActivity.getActivityid());
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    
    public void dataCopy(int id){
    	
    	try {	
    		
	    	Query y = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities where activityid=? ").setInteger(0, id);
	        
	        if(y.list().size() > 0){
		        Iterator iter = y.iterate();
		
		        Companyactivities compActivity = (Companyactivities) iter.next();
		        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
		        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compActivity);
		        
		        activityPane.ebiModule.ebiContainer.showInActionStatus("Activity",true);
	
		        Companyactivities compAct = new Companyactivities();
		        compAct.setCreateddate(new Date());
		        compAct.setCreatedfrom(EBIPGFactory.ebiUser);
		         
		        compAct.setCompany(compActivity.getCompany());
		        compAct.setActivityname(compActivity.getActivityname()+" - (Copy)");
		        compAct.setActivitytype(compActivity.getActivitytype());
	
		        compAct.setTimerdisabled(compActivity.getTimerdisabled());
		        compAct.setTimerstart(compActivity.getTimerstart());
		        compAct.setActivitystatus(compActivity.getActivitystatus());
		        compAct.setDuedate(compActivity.getDuedate());
		        compAct.setDuration(compActivity.getDuration());
	
		        compAct.setAcolor(compActivity.getAcolor());
		        compAct.setActivitydescription(compActivity.getActivitydescription());
		        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(compAct);
		        
		            if(!compActivity.getCompanyactivitiesdocses().isEmpty()){
		                Iterator itd = compActivity.getCompanyactivitiesdocses().iterator();
		                while(itd.hasNext()){
		                    Companyactivitiesdocs  docs = (Companyactivitiesdocs) itd.next();
		                    Companyactivitiesdocs cd = new Companyactivitiesdocs();
		                    cd.setCompanyactivities(compAct);
		                    cd.setCreateddate(new Date());
		                    cd.setCreatedfrom(EBIPGFactory.ebiUser);
		                    cd.setFiles(docs.getFiles());
		                    cd.setName(docs.getName());
		                     
		                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cd);
		                }
		            }

                activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiSave",true);
	            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	            activityPane.ebiModule.ebiContainer.showInActionStatus("Activity",false);

	            dataShow(false);
                activityPane.ebiModule.allertTimer.setUpAvailableTimer();
	            activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").
				changeSelection(activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").
						convertRowIndexToView(activityPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(activityPane.tabModel.data,7 , compAct.getActivityid())),0,false,false);
		       
	        }
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }

    public void dataMove(int id){
        try{
            Query y = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities where activityid=? ").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(activityPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyactivities act = (Companyactivities) iter.next();
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(act);
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    activityPane.ebiModule.ebiContainer.showInActionStatus("Activity", true);

                    act.setCompany((Company)x.list().get(0));
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(act);

                    activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiSave",true);
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    activityPane.ebiModule.ebiContainer.showInActionStatus("Activity", false);
                    dataShow(false);
                    activityPane.ebiModule.allertTimer.setUpAvailableTimer();

                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    
    public void dataEdit(final int id) {
        dataNew();

        
        Query y = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities where activityid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
	        Iterator iter = y.iterate();
	
	        if (iter.hasNext()) {
	
	        		this.companyActivity = (Companyactivities) iter.next();
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(companyActivity);
	        		activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setID(companyActivity.getActivityid());
	                activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedDate(activityPane.ebiModule.ebiPGFactory.
                                            getDateToString(companyActivity.getCreateddate() == null ? new Date() : companyActivity.getCreateddate()));

	                activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedFrom(companyActivity.getCreatedfrom() == null ?
                                                                                                                EBIPGFactory.ebiUser : companyActivity.getCreatedfrom() );
	
	                if (companyActivity.getChangeddate() != null) {
	                    activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedDate(activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getChangeddate()));
	                    activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedFrom(companyActivity.getChangedfrom());
	                } else {
	                    activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedDate("");
	                    activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedFrom("");
	                }
	
	                activityPane.ebiModule.guiRenderer.getTextfield("activityNameText","Activity").setText(companyActivity.getActivityname());
	                activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").setDate(companyActivity.getDuedate() == null ?
                                                                                                                    new Date() : companyActivity.getDuedate() );

                    activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getEditor().
                                                                    setText(activityPane.ebiModule.ebiPGFactory.
                                                                                getDateToString(companyActivity.getDuedate() == null ?
                                                                                                new Date() : companyActivity.getDuedate() ));
	               
	                if(companyActivity.getTimerdisabled()  != null){
	                	activityPane.ebiModule.guiRenderer.getCheckBox("timerActiveBox", "Activity").setSelected(companyActivity.getTimerdisabled() == 1 ? true : false);
	                }
	                
	                if(companyActivity.getTimerstart() != null){
	                	activityPane.ebiModule.guiRenderer.getComboBox("timerStartText", "Activity").setSelectedItem(companyActivity.getTimerstart()+" min");
	                }
	                
	                activityPane.ebiModule.guiRenderer.getTextfield("durationText","Activity").setText(String.valueOf(companyActivity.getDuration() == null ?  0 : companyActivity.getDuration()));
	
	                int r;
	                int g;
	                int b;
	                if( companyActivity.getAcolor() != null){
                        String[] splCol =companyActivity.getAcolor().split(",");
                        r = Integer.parseInt(splCol[0]);
                        g = Integer.parseInt(splCol[1]);
                        b = Integer.parseInt(splCol[2]);

                        activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").setBackground(new Color(r, g, b));
                    }else{
                        activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").setBackground(Color.gray);
                    }

	                GregorianCalendar startDate = new GregorianCalendar();
	                startDate.setTime(companyActivity.getDuedate() == null ? new Date() : companyActivity.getDuedate());
	                startDate.set(Calendar.SECOND, 0);
	                startDate.set(Calendar.MILLISECOND, 0);
	
	                activityPane.ebiModule.guiRenderer.getSpinner("dueH","Activity").setValue(startDate.get(Calendar.HOUR_OF_DAY));
	                activityPane.ebiModule.guiRenderer.getSpinner("dueMin","Activity").setValue(startDate.get(Calendar.MINUTE));
	
	                if(companyActivity.getActivitytype() != null){
	                    activityPane.ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setSelectedItem(companyActivity.getActivitytype());
	                }
	
	                if (companyActivity.getActivitystatus() != null) {
	                    activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setSelectedItem(companyActivity.getActivitystatus());
	                }
	
	                activityPane.ebiModule.guiRenderer.getTextarea("activityDescription","Activity").setText(companyActivity.getActivitydescription());

	                activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiEdit",true);
	                this.dataShowDoc();

            		activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").
      					changeSelection(activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").
      							convertRowIndexToView(activityPane.ebiModule.dynMethod.
      									getIdIndexFormArrayInATable(activityPane.tabModel.data,7, id)),0,false,false);
            
	         
	            }
    	}else{
    		 EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
    	}
    }

    public void dataDelete(int id) {

        Query y = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities where activityid=?  ").setInteger(0, id);
        
        if(y.list().size() > 0){

        Iterator iter = y.iterate();
        while (iter.hasNext()) {

            Companyactivities act = (Companyactivities) iter.next();

                try {
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(act);

                    activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiDelete",true);
                    activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    activityPane.ebiModule.allertTimer.setUpAvailableTimer();
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.dataShow(false);

            }
        }
        dataNew();
    }

    public synchronized void dataShow(boolean reload) {
     try {
            boolean isEmpty = false;
            Iterator itr;
            Query query = null;

            int srow = 0;

            if(activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity") != null){
                srow = activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow();
            }

            if(activityPane.ebiModule.companyID != -1){
                    if(!reload){
                        query = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities as act where act.company.companyid=? order by act.createddate desc ");
                        query.setInteger(0, activityPane.ebiModule.companyID);
                        itr = query.iterate();
                        if(query.list().size() == 0){
                            isEmpty = true;
                        }
                        activityPane.tabModel.data = new Object[query.list().size()][8];
                    }else{
                        query = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities as act where act.company.companyid=? order by act.createddate desc ").setInteger(0,activityPane.ebiModule.companyID);

                        if(activityPane.ebiModule.ebiPGFactory.company != null){
                            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(activityPane.ebiModule.ebiPGFactory.company);
                            activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                        }
                        itr = query.iterate();
                        if(query.list().size() == 0){
                            isEmpty = true;
                        }
                      activityPane.tabModel.data = new Object[query.list().size()][8];
                    }
            }else{
                query = activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyactivities as act where act.company.companyid IS NULL order by act.createddate desc ");

                itr = query.iterate();
                if(query.list().size() == 0){
                    isEmpty = true;
                }
                activityPane.tabModel.data = new Object[query.list().size()][8];
            }

           if(!isEmpty){
                int i = 0;
                while (itr.hasNext()) {

                    Companyactivities obj = (Companyactivities) itr.next();
                    if(reload){
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(obj);
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    }
                    activityPane.tabModel.data[i][0] = obj.getActivityname();
                    activityPane.tabModel.data[i][1] = obj.getActivitytype() == null ? "" : obj.getActivitytype();

                    GregorianCalendar startDate = new GregorianCalendar();
                    startDate.setTime(obj.getDuedate());
                    startDate.set(Calendar.SECOND, 0);
                    startDate.set(Calendar.MILLISECOND, 0);

                    String min;
                    if(startDate.get(Calendar.MINUTE) < 10){
                      min = "0"+startDate.get(Calendar.MINUTE);
                    }else{
                      min = startDate.get(Calendar.MINUTE)+"";
                    }

                    String hour  ;
                    if(startDate.get(Calendar.HOUR_OF_DAY) < 10){
                      hour = "0"+startDate.get(Calendar.HOUR_OF_DAY);
                    }else{
                      hour = ""+startDate.get(Calendar.HOUR_OF_DAY);
                    }

                    activityPane.tabModel.data[i][2] = obj.getDuedate() == null ? "" : (activityPane.ebiModule.ebiPGFactory.getDateToString(obj.getDuedate()) +" "+hour+":"+min);

                    activityPane.tabModel.data[i][3] = obj.getDuration() == null ? "" : String.valueOf(obj.getDuration());
                    activityPane.tabModel.data[i][4] = obj.getAcolor() == null ? "" : obj.getAcolor();
                    activityPane.tabModel.data[i][5] = obj.getActivitystatus() == null ? "" : obj.getActivitystatus();
                    activityPane.tabModel.data[i][6] = obj.getActivitydescription() == null ? "" : obj.getActivitydescription();
                    activityPane.tabModel.data[i][7] = obj.getActivityid();
                    i++;
                }

                if(activityPane.ebiModule.getEBICalendar() != null){
                   activityPane.ebiModule.getEBICalendar().refresh();
                }
           }else{
              activityPane.tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "","",""}}; 
           }

         activityPane.tabModel.fireTableDataChanged();

         if(activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity") != null && srow >= activityPane.tabModel.data.length){
            activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").changeSelection(srow,0,false,false);
         }

        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    private void createHistory(Company com) {

        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + companyActivity.getCreatedfrom());

        if (companyActivity.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + companyActivity.getChangedfrom());
        }

        list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (companyActivity.getActivityname().equals(activityPane.ebiModule.guiRenderer.getTextfield("activityNameText","Activity").getText()) == true ? companyActivity.getActivityname() : companyActivity.getActivityname() +"$") );
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.setTime(companyActivity.getDuedate());
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        String min  ;
        if(startDate.get(Calendar.MINUTE) < 10){
          min = startDate.get(Calendar.MINUTE)+"0";
        }else{
          min = startDate.get(Calendar.MINUTE)+"";
        }

        String hour  ;
        if(startDate.get(Calendar.HOUR_OF_DAY) < 10){
          hour = "0"+startDate.get(Calendar.HOUR_OF_DAY);
        }else{
          hour = ""+startDate.get(Calendar.HOUR_OF_DAY);
        }
        list.add(EBIPGFactory.getLANG("EBI_LANG_DUE_DATE") + ": " + (activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getDuedate()).equals(activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getEditor().getText()) == true ? activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getDuedate())+hour+":"+min : activityPane.ebiModule.ebiPGFactory.getDateToString(companyActivity.getDuedate())+hour+":"+min +"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + (companyActivity.getActivitydescription().equals(activityPane.ebiModule.guiRenderer.getTextarea("activityDescription","Activity").getText()) == true ? companyActivity.getActivitydescription() : companyActivity.getActivitydescription()+"$") );
        if(companyActivity.getActivitystatus() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " +  (companyActivity.getActivitystatus().equals(activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").getSelectedItem().toString()) == true ? companyActivity.getActivitystatus() : companyActivity.getActivitystatus()+"$") );
        }
        if(companyActivity.getActivitytype() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_TYPE") + ": " + (companyActivity.getActivitytype().equals(activityPane.ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").getSelectedItem().toString()) == true ? companyActivity.getActivitytype() : companyActivity.getActivitytype() +"$") );
        }
        list.add(EBIPGFactory.getLANG("EBI_LANG_DURATION") + ": " + ((""+companyActivity.getDuration()).equals(activityPane.ebiModule.guiRenderer.getTextfield("durationText","Activity").getText()) == true ? companyActivity.getDuration() : companyActivity.getDuration() +"$") );
        
        list.add(EBIPGFactory.getLANG("EBI_LANG_TIMER_START") + ": " + ((""+companyActivity.getTimerstart()).equals(activityPane.ebiModule.guiRenderer.getComboBox("timerStartText", "Activity").getSelectedItem().toString()) == true ? companyActivity.getTimerstart() : companyActivity.getTimerstart() +"$") );
        
        list.add(EBIPGFactory.getLANG("EBI_LANG_TIMER_DISABLED") + ": " + ((""+companyActivity.getTimerdisabled()).equals(activityPane.ebiModule.guiRenderer.getCheckBox("timerActiveBox", "Activity").isSelected() ? "1" : "0" ) == true ? companyActivity.getTimerdisabled() : companyActivity.getTimerdisabled() +"$") );
        
        list.add("*EOR*"); // END OF RECORD

        if (!companyActivity.getCompanyactivitiesdocses().isEmpty()) {
            Iterator iter = companyActivity.getCompanyactivitiesdocses().iterator();

            while (iter.hasNext()) {
                Companyactivitiesdocs obj = (Companyactivitiesdocs) iter.next();

                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + obj.getName());
                list.add(activityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + activityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()));
                list.add(obj.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + obj.getCreatedfrom());
                list.add("*EOR*"); // END OF RECORD
            }
        }

        try {
            activityPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Activities", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataNew() {
    	
        this.companyActivity = new Companyactivities();

        activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedDate(activityPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedFrom(EBIPGFactory.ebiUser);
        activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedDate("");
        activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setChangedFrom("");
        activityPane.ebiModule.guiRenderer.getTextfield("activityNameText","Activity").setText("");
        activityPane.ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setSelectedIndex(0);
        activityPane.ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setSelectedIndex(0);
        activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").setDate(new java.util.Date());
        activityPane.ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getEditor().setText("");
        activityPane.ebiModule.guiRenderer.getTextarea("activityDescription","Activity").setText("");
        activityPane.ebiModule.guiRenderer.getSpinner("dueH","Activity").setValue(0);
        activityPane.ebiModule.guiRenderer.getSpinner("dueMin","Activity").setValue(0);
        activityPane.ebiModule.guiRenderer.getTextfield("durationText","Activity").setText("");
        activityPane.ebiModule.guiRenderer.getLabel("colorPanel","Activity").setBackground(new Color(5, 125, 255));
        activityPane.ebiModule.guiRenderer.getTable("tableActivity","Activity").setOpaque(true);
        activityPane.ebiModule.guiRenderer.getComboBox("timerStartText", "Activity").setSelectedIndex(0);
        activityPane.ebiModule.guiRenderer.getCheckBox("timerActiveBox", "Activity").setSelected(false);
        activityPane.ebiModule.ebiPGFactory.getDataStore("Activity","ebiNew",true);
        activityPane.ebiModule.guiRenderer.getVisualPanel("Activity").setID(-1);
        dataShow(false);
        dataShowDoc();
    }

    public void dataDeleteDoc(int id) {

        Iterator iter = this.companyActivity.getCompanyactivitiesdocses().iterator();
        while (iter.hasNext()) {

            Companyactivitiesdocs doc = (Companyactivitiesdocs) iter.next();

            if (id == doc.getActivitydocid()) {
               if(id >= 0){
                    try {
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                        activityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
               }
                this.companyActivity.getCompanyactivitiesdocses().remove(doc);
                this.dataShowDoc();
                break;
            }
        }
    }

    public void dataShowDoc() {
      try{
           if(this.companyActivity.getCompanyactivitiesdocses().size() > 0){
            activityPane.tabActDoc.data = new Object[this.companyActivity.getCompanyactivitiesdocses().size()][4];

            Iterator itr = this.companyActivity.getCompanyactivitiesdocses().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companyactivitiesdocs obj = (Companyactivitiesdocs) itr.next();

                activityPane.tabActDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                activityPane.tabActDoc.data[i][1] = activityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : activityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                activityPane.tabActDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                if(obj.getActivitydocid() == null || obj.getActivitydocid() < 0){obj.setActivitydocid(((i+1)*(-1)));}
                activityPane.tabActDoc.data[i][3] = obj.getActivitydocid();

                i++;
            }
           }else{
             activityPane.tabActDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
           }
           activityPane.tabActDoc.fireTableDataChanged();
      }catch(Exception ex){}
    }

    public void dataViewDoc(int id) {

        String FileName;
        String FileType;
        OutputStream fos;
        try {

            Iterator iter = this.companyActivity.getCompanyactivitiesdocses().iterator();
            while (iter.hasNext()) {

                Companyactivitiesdocs doc = (Companyactivitiesdocs) iter.next();

                if (id == doc.getActivitydocid()) {
                    // Get the BLOB inputstream 

                    String file = doc.getName().replaceAll(" ", "_");

                    //byte buffer[] = adress.getFiles().getBytes(1,(int)adress.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));

                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    resolverType(FileName, FileType);
                    break;
                }
            }
        } catch (FileNotFoundException exx) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        } catch (IOException exx1) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_LOADING_FILE")).Show(EBIMessage.INFO_MESSAGE);
        }
    }

    public void dataNewDoc() {
       try{
            File fs = activityPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
            if (fs != null) {
                byte[] file = readFileToByte(fs);

                if (file != null) {
                    //java.sql.Blob blb = Hibernate.createBlob(file);
                    Companyactivitiesdocs docs = new Companyactivitiesdocs();
                    docs.setCompanyactivities(this.companyActivity);
                    docs.setName(fs.getName());
                    docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                    docs.setCreatedfrom(EBIPGFactory.ebiUser);

                    docs.setFiles(file);
                    this.companyActivity.getCompanyactivitiesdocses().add(docs);
                    this.dataShowDoc();
                } else {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                }
            }
       }catch(Exception ex){}
    }

    private byte[] readFileToByte(File selFile) {
        InputStream st = readFileGetBlob(selFile);
        byte inBuf[]  ;
        try {
            int inBytes = st.available();
            inBuf = new byte[inBytes];
            st.read(inBuf, 0, inBytes);
        } catch (java.io.IOException ex) {
            return null;
        }

        return inBuf;
    }

    private InputStream readFileGetBlob(File file) {
        InputStream is  ;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            return null;
        }
        return is;
    }

    private void resolverType(String fileName, String type) {
       try{
        if (".jpg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
            EBIImageViewer view = new EBIImageViewer(activityPane.ebiModule.ebiPGFactory.getMainFrame(), new javax.swing.ImageIcon(fileName));
            view.setVisible(true);
        } else if (".pdf".equals(type)) {
            activityPane.ebiModule.ebiPGFactory.openPDFReportFile(fileName);
        } else if (".doc".equals(type)) {
            activityPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        } else {
            activityPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        }
       }catch(Exception exe){}
    }

    public Set<Companyactivities> getActivitiesesList() {
        return activityPane.ebiModule.ebiPGFactory.company.getCompanyactivitieses();
    }

    public Set<Companyactivitiesdocs> getActivitiesDocList() {
        return companyActivity.getCompanyactivitiesdocses();
    }

    public Companyactivities getCompanyActivity() {
        return companyActivity;
    }

    public void setCompanyActivity(Companyactivities companyActivity) {
        this.companyActivity = companyActivity;
    }
}