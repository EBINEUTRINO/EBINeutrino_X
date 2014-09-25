package ebiCRM.data.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.*;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.dialogs.EBIMeetingAddContactDialog;
import ebiCRM.gui.panels.EBIMeetingProtocol;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companymeetingcontacts;
import ebiNeutrinoSDK.model.hibernate.Companymeetingdoc;
import ebiNeutrinoSDK.model.hibernate.Companymeetingprotocol;

import javax.swing.*;

public class EBIDataControlMeetingProtocol {

    private EBIMeetingProtocol meetingPane = null;
    public Companymeetingprotocol meetingProtocol = null;

    public EBIDataControlMeetingProtocol(EBIMeetingProtocol meetingPane) {
        this.meetingPane = meetingPane;
        meetingProtocol = new Companymeetingprotocol();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall", true);
        	meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            if (isEdit == true) {
                createHistory(meetingPane.ebiModule.ebiPGFactory.company);
                meetingProtocol.setChangeddate(new Date());
                meetingProtocol.setChangedfrom(EBIPGFactory.ebiUser);
            } else {
                meetingProtocol.setCreateddate(new Date());
                meetingProtocol.setCreatedfrom(meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").getCreatedFrom());
                meetingPane.isEdit = true;
            }
            
            meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            meetingProtocol.setCompany(meetingPane.ebiModule.ebiPGFactory.company);

            if(meetingPane.ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").getSelectedItem() != null){
                meetingProtocol.setMeetingtype(meetingPane.ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").getSelectedItem().toString());
            }
            
            meetingProtocol.setMeetingsubject(meetingPane.ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").getText());
            meetingProtocol.setProtocol(meetingPane.ebiModule.guiRenderer.getTextarea("meetingDescription","MeetingCall").getText());

            if(meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").getDate() != null){
                meetingProtocol.setMetingdate(meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").getDate());
            }

            //Save meeting contacts
            if(!this.meetingProtocol.getCompanymeetingcontactses().isEmpty()){
                Iterator iter = this.meetingProtocol.getCompanymeetingcontactses().iterator();
                while(iter.hasNext()){
                    Companymeetingcontacts cont = (Companymeetingcontacts)iter.next();
                    if(cont.getMeetingcontactid() < 0){cont.setMeetingcontactid(null);}
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cont);
                }
            }
            //Save docs
            if(!this.meetingProtocol.getCompanymeetingdocs().isEmpty()){
                Iterator iter = this.meetingProtocol.getCompanymeetingdocs().iterator();
                while(iter.hasNext()){
                    Companymeetingdoc doc = (Companymeetingdoc)iter.next();
                    if(doc.getMeetingdocid() < 0){ doc.setMeetingdocid(null);}
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(doc);
                }
            }

            meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(meetingProtocol);

            meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiSave",true);
            meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            
            if(!isEdit){
            	meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setID(meetingProtocol.getMeetingprotocolid());
            }
            
            dataShow();
            dataShowContact();
            dataShowDoc();
            meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall",false);
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    
    //copyMeeting
    public void dataCopy(int id){
    	 try {
    		 Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=? ").setInteger(0, id);
         
	         if(y.list().size() > 0){
	         		 Iterator iter = y.iterate();
	         		 Companymeetingprotocol mProtocol = (Companymeetingprotocol) iter.next();
                     meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
	         		 meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(mProtocol);

	                 meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall", true);
	                 
	                 Companymeetingprotocol mPro = new Companymeetingprotocol();
	                 mPro.setCreateddate(new Date());
	                 mPro.setCreatedfrom(EBIPGFactory.ebiUser);
	                 
	                 mPro.setCompany(mProtocol.getCompany());
	                 mPro.setMeetingtype(mProtocol.getMeetingtype());
	                 mPro.setMeetingsubject(mProtocol.getMeetingsubject()+" - (Copy)");
	                 mPro.setProtocol(mProtocol.getProtocol());
	                 mPro.setMetingdate(mProtocol.getMetingdate());
	
	                 //Save meeting contacts
	                 if(!mProtocol.getCompanymeetingcontactses().isEmpty()){
	                     Iterator itc = mProtocol.getCompanymeetingcontactses().iterator();
	                     while(itc.hasNext()){
	                         Companymeetingcontacts cont = (Companymeetingcontacts)itc.next();
	                         
	                         Companymeetingcontacts mcon = new Companymeetingcontacts();
	                         mcon.setCompanymeetingprotocol(mPro);
	                         mcon.setCreateddate(new Date());
	                         mcon.setCreatedfrom(EBIPGFactory.ebiUser);
	                         mcon.setGender(cont.getGender());
	                         mcon.setName(cont.getName());
	                         mcon.setSurname(cont.getSurname());
	                         mcon.setPos(cont.getPos());
	                         mcon.setPosition(cont.getPosition());
	                         mcon.setBirddate(cont.getBirddate());
	                         mcon.setCountry(cont.getCountry());
	                         mcon.setEmail(cont.getEmail());
	                         mcon.setFax(cont.getFax());
	                         mcon.setLocation(cont.getLocation());
	                         mcon.setMittelname(cont.getMittelname());
	                         mcon.setMobile(cont.getMobile());
	                         mcon.setPbox(cont.getPbox());
	                         mcon.setPhone(cont.getPhone());
	                         mcon.setStreet(cont.getStreet());
	                         mcon.setZip(cont.getZip());
	                         mcon.setDescription(cont.getDescription());
	                         
	                         meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(mcon);
	                     }
	                 }
	                 //Save docs
	                 if(!mProtocol.getCompanymeetingdocs().isEmpty()){
	                     Iterator itd = mProtocol.getCompanymeetingdocs().iterator();
	                     while(itd.hasNext()){
	                         Companymeetingdoc doc = (Companymeetingdoc)itd.next();
	                         
	                         Companymeetingdoc cdc = new Companymeetingdoc();
	                         cdc.setCompanymeetingprotocol(mProtocol);
	                         cdc.setCreateddate(new Date());
	                         cdc.setCreatedfrom(EBIPGFactory.ebiUser);
	                         cdc.setFiles(doc.getFiles());
	                         cdc.setName(doc.getName());
	                         meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cdc);
	                     }
	                 }
	
	                 meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(mPro);
                     meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiSave",true);

	                 meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	                 
	                 meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall",false);
	                 dataShow();
	                 
	                 meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").
	    				changeSelection(meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").
	    						convertRowIndexToView(meetingPane.ebiModule.dynMethod.
	    								getIdIndexFormArrayInATable(meetingPane.tableModel.data,4, mPro.getMeetingprotocolid())),0,false,false);
	                 
	         }
         
         } catch (HibernateException e) {
             e.printStackTrace();
         } catch (Exception e) {
             e.printStackTrace();
         } 
    	 
    }


    public void dataMove(int id){
        try{
            Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=?").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(meetingPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companymeetingprotocol mcall = (Companymeetingprotocol) iter.next();
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(mcall);
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall", true);

                    mcall.setCompany((Company)x.list().get(0));
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(mcall);

                    meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiSave",true);
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    meetingPane.ebiModule.ebiContainer.showInActionStatus("MeetingCall", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void dataEdit(int id) {
        dataNew();

        Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
            meetingProtocol = (Companymeetingprotocol) iter.next();
            meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(meetingProtocol);
            meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setID(meetingProtocol.getMeetingprotocolid());

            if(meetingProtocol.getMeetingtype() != null){
                meetingPane.ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setSelectedItem(meetingProtocol.getMeetingtype());
            }

            meetingPane.ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").setText(meetingProtocol.getMeetingsubject() == null ? "" : meetingProtocol.getMeetingsubject());
            meetingPane.ebiModule.guiRenderer.getTextarea("meetingDescription","MeetingCall").setText(meetingProtocol.getProtocol() == null ? "" : meetingProtocol.getProtocol());


            if(meetingProtocol.getMetingdate() != null){
                meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").setDate(meetingProtocol.getMetingdate());
                meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").getEditor().
                                            setText(meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getMetingdate()));
            }

            meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedDate(meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getCreateddate() == null ? new Date() : meetingProtocol.getCreateddate()));
            meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedFrom(meetingProtocol.getCreatedfrom() == null ? EBIPGFactory.ebiUser : meetingProtocol.getCreatedfrom() );

            if (meetingProtocol.getChangeddate() != null) {
                meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedDate(meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getChangeddate()));
                meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedFrom(EBIPGFactory.ebiUser);
            } else {
                meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedDate("");
                meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedFrom("");
            }


            meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiEdit",true);

            this.dataShowContact();
            this.dataShowDoc();
            
            meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").
			changeSelection(meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").
					convertRowIndexToView(meetingPane.ebiModule.dynMethod.
						getIdIndexFormArrayInATable(meetingPane.tableModel.data, 4, id)),0,false,false);
        }else{
           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
    }

    public void dataDelete(int id) {

        Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){

        	Iterator iter = y.iterate();

            Companymeetingprotocol meet = (Companymeetingprotocol) iter.next();

                try {
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(meet);
                    meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiDelete",true);
                    meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            dataNew();
            dataShow();
        }
 
    }

    public void dataShow() {
        try{
            int srow = meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectedRow();

            Query y = null;
            if(meetingPane.ebiModule.companyID != -1){
                y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where company.companyid=? order by createddate desc ");
                y.setInteger(0, meetingPane.ebiModule.companyID);
            }else{
                y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where company.companyid IS NULL order by createddate desc ");
            }

            if(y.list().size() > 0){

                meetingPane.tableModel.data = new Object[y.list().size()][5];
                Iterator iter = y.iterate();
                int i = 0;

                while (iter.hasNext()) {
                    Companymeetingprotocol obj = (Companymeetingprotocol) iter.next();

                    meetingPane.tableModel.data[i][0] = obj.getMetingdate() == null ? "" :  meetingPane.ebiModule.ebiPGFactory.getDateToString(obj.getMetingdate());
                    meetingPane.tableModel.data[i][1] = obj.getMeetingsubject() == null ? "" : obj.getMeetingsubject();
                    meetingPane.tableModel.data[i][2] = obj.getMeetingtype() == null ? "" : obj.getMeetingtype();
                    meetingPane.tableModel.data[i][3] = obj.getProtocol() == null ? "" : obj.getProtocol();
                    meetingPane.tableModel.data[i][4] = obj.getMeetingprotocolid();
                    i++;
                }

            }else{
                meetingPane.tableModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "",""}};
            }
            meetingPane.tableModel.fireTableDataChanged();
            meetingPane.ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").changeSelection(srow,0,false,false);
        }catch (Exception ex){}
    }

    public void dataShowReport(int id) {
       try{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ID", id);

            meetingPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                    useReportSystem(map,
                    meetingPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL")),
                    getMeetingNamefromId(id));
       }catch (Exception ex){}
    }

    public String dataShowAndMailReport(int id, boolean showWindow) {
        String fileName="";

        if(meetingPane.isEdit){
            if(!dataStore(meetingPane.isEdit)){
                return null;
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);

        String to= "";
        try {
            Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=? ").setInteger(0, id);

            Iterator iter = y.iterate();
            if(iter.hasNext()){
                Companymeetingprotocol meeting = (Companymeetingprotocol) iter.next();
                meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(meeting);

                Iterator ix = meeting.getCompanymeetingcontactses().iterator();
                int i =0;
                int c = y.list().size();
                while(ix.hasNext()){
                    Companymeetingcontacts rec = (Companymeetingcontacts)ix.next();

                    to += rec.getEmail();
                    if(i<= c-1){
                        to += ";";
                    }
                    i++;
                }

                //EMail is send from the Report generator
                fileName = meetingPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                        meetingPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL")),
                        getMeetingNamefromId(id),showWindow,true,to);
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_NO_RECEIVER_WAS_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }

        return fileName;
    }

    public void dataNew() {
       try{
        meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedDate(meetingPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedFrom(EBIPGFactory.ebiUser);
        meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedDate("");
        meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedFrom("");

        meetingPane.ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setSelectedIndex(0);
        meetingPane.ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").setText("");
        meetingPane.ebiModule.guiRenderer.getTextarea("meetingDescription","MeetingCall").setText("");
        meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").setDate(new java.util.Date());
        meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").getEditor().setText("");

        meetingPane.ebiModule.ebiPGFactory.getDataStore("MeetingCall","ebiNew",true);
        meetingPane.ebiModule.guiRenderer.getVisualPanel("MeetingCall").setID(-1);
        this.meetingProtocol = new Companymeetingprotocol();
        dataShow();
        dataShowContact();
        dataShowDoc();
       }catch (Exception x){}
    }

    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();
        if (meetingProtocol.getCreateddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getCreateddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + EBIPGFactory.ebiUser);
        }
        if (meetingProtocol.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + EBIPGFactory.ebiUser);
        }
        
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_MEETING_TYPE") + ": " + ( meetingProtocol.getMeetingtype().equals(meetingPane.ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").getSelectedItem().toString()) == true ? meetingProtocol.getMeetingtype() : meetingProtocol.getMeetingtype()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_MEMO_SUBJECT") + ": " + (meetingProtocol.getMeetingsubject().equals(meetingPane.ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").getText()) == true ? meetingProtocol.getMeetingsubject() : meetingProtocol.getMeetingsubject()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + (meetingProtocol.getProtocol().equals(meetingPane.ebiModule.guiRenderer.getTextarea("meetingDescription","MeetingCall").getText()) == true ? meetingProtocol.getProtocol() : meetingProtocol.getProtocol()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_DATE") + ": " + (meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getMetingdate()).equals(meetingPane.ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").getEditor().getText()) == true ? meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getMetingdate()) : meetingPane.ebiModule.ebiPGFactory.getDateToString(meetingProtocol.getMetingdate())+"$") );

        list.add("*EOR*"); // END OF RECORD

        if (!meetingProtocol.getCompanymeetingcontactses().isEmpty()) {
            Iterator iter = meetingProtocol.getCompanymeetingcontactses().iterator();
            while (iter.hasNext()) {
                Companymeetingcontacts contact = (Companymeetingcontacts) iter.next();

                list.add(EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ": " + contact.getGender());
                list.add(EBIPGFactory.getLANG("EBI_LANG_SURNAME") + ": " + contact.getSurname());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + contact.getName());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_MITTEL_NAME") + ": " + contact.getMittelname());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_POSITION") + ": " + contact.getPosition());

                list.add(EBIPGFactory.getLANG("EBI_LANG_C_BIRDDATE") + ": " + meetingPane.ebiModule.ebiPGFactory.getDateToString(contact.getBirddate()));
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE") + ": " + contact.getPhone());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_FAX") + ": " + contact.getFax());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE") + ": " + contact.getMobile());
                list.add(EBIPGFactory.getLANG("EBI_LANG_EMAIL") + ": " + contact.getEmail());
                list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + contact.getDescription());

            }
        }

        if (!meetingProtocol.getCompanymeetingdocs().isEmpty()) {
            Iterator iter = meetingProtocol.getCompanymeetingdocs().iterator();
            while (iter.hasNext()) {
                Companymeetingdoc doc = (Companymeetingdoc) iter.next();

                list.add(doc.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + doc.getName());
                list.add(meetingPane.ebiModule.ebiPGFactory.getDateToString(doc.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + meetingPane.ebiModule.ebiPGFactory.getDateToString(doc.getCreateddate()));
                list.add(doc.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + doc.getCreatedfrom());
                list.add("*EOR*");
            }
        }

        try {
            meetingPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Meeting", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataAddContact() {
       try{
        EBIMeetingAddContactDialog newContact = new EBIMeetingAddContactDialog(this.meetingPane.ebiModule,true,null,null,false);
        newContact.setVisible();
       }catch (Exception e){}
    }

    public void addContact(EBIMeetingAddContactDialog newContact, Companymeetingcontacts contact){
            try {
                contact.setCompanymeetingprotocol(this.meetingProtocol);
                contact.setGender(newContact.getGenderText());
                contact.setSurname(newContact.getSurnameText());
                contact.setName(newContact.getNameText());
                contact.setPosition(newContact.getPositionText());
                contact.setStreet(newContact.getStreet());
                contact.setZip(newContact.getZip());
                contact.setLocation(newContact.getLocation());
                contact.setPbox(newContact.getPbox());
                contact.setCountry(newContact.getCountry());
                contact.setBirddate(newContact.getBirddateText());
                contact.setPhone(newContact.getTelephoneText());
                contact.setFax(newContact.getFaxText());
                contact.setMobile(newContact.getMobileText());
                contact.setEmail(newContact.getEMailText());
                contact.setDescription(newContact.getDescriptionText());
                this.meetingProtocol.getCompanymeetingcontactses().add(contact);

                dataShowContact();
             } catch (HibernateException e) {
                    e.printStackTrace();
             } catch(Exception e){
                 e.printStackTrace();
            }

    }

    public void dataEditContact(int id) {
          try{
            Iterator iter = this.meetingProtocol.getCompanymeetingcontactses().iterator();

            while (iter.hasNext()) {

                Companymeetingcontacts contact = (Companymeetingcontacts) iter.next();

                if (contact.getMeetingcontactid() == id) {
                    EBIMeetingAddContactDialog newContact = new EBIMeetingAddContactDialog(this.meetingPane.ebiModule,true,contact,null,true);

                    newContact.setGenderText(contact.getGender());
                    newContact.setSurnameText(contact.getSurname());
                    newContact.setNameText(contact.getName());
                    newContact.setStreet(contact.getStreet());
                    newContact.setZip(contact.getZip());
                    newContact.setLocation(contact.getLocation());
                    newContact.setPbox(contact.getPbox());
                    newContact.setCountry(contact.getCountry());
                    newContact.setPositionText(contact.getPosition());
                    newContact.setBirddateText(contact.getBirddate());
                    newContact.setTelephoneText(contact.getPhone());
                    newContact.setFaxText(contact.getFax());
                    newContact.setMobileText(contact.getMobile());
                    newContact.setEMailText(contact.getEmail());
                    newContact.setDescriptionText(contact.getDescription());
                    if(contact.getPos() != null){
                        newContact.setMainContact(contact.getPos() == 1 ? true : false);
                    }
                    newContact.setVisible();
                    break;
                }
            }

            dataShowContact();
          }catch (Exception ex){}
    }

    public void dataDeleteContact(int id) {
       try{
        Iterator iter = this.meetingProtocol.getCompanymeetingcontactses().iterator();
        while (iter.hasNext()) {

            Companymeetingcontacts con = (Companymeetingcontacts) iter.next();

            if (con.getMeetingcontactid() == id) {

                this.meetingProtocol.getCompanymeetingcontactses().remove(con);

                if(id >= 0){
                    try {
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(con);
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (HibernateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.dataShowContact();
                break;
            }
        }
       }catch (Exception ex){}
    }

    public void dataShowContact() {
       try{
            if (!this.meetingProtocol.getCompanymeetingcontactses().isEmpty()) {
                meetingPane.tabModelContact.data = new Object[this.meetingProtocol.getCompanymeetingcontactses().size()][7];

                Iterator itr = this.meetingProtocol.getCompanymeetingcontactses().iterator();
                int i = 0;
                while (itr.hasNext()) {
                    Companymeetingcontacts obj = (Companymeetingcontacts) itr.next();
                    meetingPane.tabModelContact.data[i][0] = obj.getPosition() == null ? "" : obj.getPosition();
                    meetingPane.tabModelContact.data[i][1] = obj.getGender() == null ? "" : obj.getGender();
                    meetingPane.tabModelContact.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                    meetingPane.tabModelContact.data[i][3] = obj.getName() == null ? "" : obj.getName();
                    meetingPane.tabModelContact.data[i][4] = obj.getPhone() == null ? "" : obj.getPhone();
                    meetingPane.tabModelContact.data[i][5] = obj.getMobile() == null ? "" : obj.getMobile();
                    if(obj.getMeetingcontactid() == null || obj.getMeetingcontactid() < 0){ obj.setMeetingcontactid(((i + 1) * (-1)));}
                    meetingPane.tabModelContact.data[i][6] = obj.getMeetingcontactid();
                    i++;
                }

            } else {
                meetingPane.tabModelContact.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
            }

           meetingPane.tabModelContact.fireTableDataChanged();
       }catch(Exception ex){}

    }

    public void dataStoreDoc() {
       try{
        File fs = meetingPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
        if (fs != null) {
            byte[] file = readFileToByte(fs);
            if (file != null) {
              try{
                Companymeetingdoc docs = new Companymeetingdoc();
                //java.sql.Blob blb = Hibernate.createBlob(file);
                docs.setCompanymeetingprotocol(this.meetingProtocol);
                docs.setName(fs.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);
                docs.setFiles(file);
                this.meetingProtocol.getCompanymeetingdocs().add(docs);
                this.dataShowDoc();
                  
              }catch(Exception e){
                  e.printStackTrace();
              }
            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                return;
            }
        }
       }catch (Exception ex){}
    }

    public void dataEditDoc(int id) {

        String FileName;
        String FileType;
        OutputStream fos;
        try {

            Iterator iter = this.meetingProtocol.getCompanymeetingdocs().iterator();
            while (iter.hasNext()) {

                Companymeetingdoc doc = (Companymeetingdoc) iter.next();

                if (id == doc.getMeetingdocid()) {
                    // Get the BLOB inputstream 

                    String file = doc.getName().replaceAll(" ", "_");

                    //byte buffer[] = doc.getFiles().getBytes(1,(int)doc.getFiles().length());
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

    public void dataDeleteDoc(int id) {
        try{
        Iterator iter = this.meetingProtocol.getCompanymeetingdocs().iterator();
        while (iter.hasNext()) {

            Companymeetingdoc doc = (Companymeetingdoc) iter.next();

            if (id == doc.getMeetingdocid()) {
                if(id >= 0){
                    try {
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                        meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (HibernateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.meetingProtocol.getCompanymeetingdocs().remove(doc);
                this.dataShowDoc();
                break;
            }
        }
        }catch (Exception ex){}
    }

    public void dataShowDoc() {
      try{
       if(!this.meetingProtocol.getCompanymeetingdocs().isEmpty()){
        meetingPane.tabmeetingDoc.data = new Object[this.meetingProtocol.getCompanymeetingdocs().size()][4];

        Iterator itr = this.meetingProtocol.getCompanymeetingdocs().iterator();
        int i = 0;
        while (itr.hasNext()) {

            Companymeetingdoc obj = (Companymeetingdoc) itr.next();
            
            meetingPane.tabmeetingDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
            meetingPane.tabmeetingDoc.data[i][1] = meetingPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : meetingPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
            meetingPane.tabmeetingDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
            if(obj.getMeetingdocid() == null || obj.getMeetingdocid() < 0){obj.setMeetingdocid(((i +1)*(-1)));}
            meetingPane.tabmeetingDoc.data[i][3] = obj.getMeetingdocid();
            i++;
        }
       }else{
          meetingPane.tabmeetingDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}}; 
       }
        meetingPane.tabmeetingDoc.fireTableDataChanged();
      }catch (Exception ex){}
    }

    private void resolverType(String fileName, String type) {
      try{
        if (".jpg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
            EBIImageViewer view = new EBIImageViewer(meetingPane.ebiModule.ebiPGFactory.getMainFrame(), new javax.swing.ImageIcon(fileName));
            view.setVisible(true);
        } else if (".pdf".equals(type)) {
            meetingPane.ebiModule.ebiPGFactory.openPDFReportFile(fileName);
        } else if (".doc".equals(type)) {
            meetingPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        } else {
            meetingPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        }
      }catch (Exception ex){}
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

    public Set<Companymeetingprotocol> getMeetingPlist() {
        return meetingPane.ebiModule.ebiPGFactory.company.getCompanymeetingprotocols();
    }

    public Set<Companymeetingcontacts> getMeetingContactlist() {
        return meetingProtocol.getCompanymeetingcontactses();
    }

    public Set<Companymeetingdoc> getMeetingDocList() {
        return meetingProtocol.getCompanymeetingdocs();
    }

    public Companymeetingprotocol getMeetingProtocol() {
        return meetingProtocol;
    }

    public void setMeetingProtocol(Companymeetingprotocol meetingProtocol) {
        this.meetingProtocol = meetingProtocol;
    }

    private String getMeetingNamefromId(int id) {

        String name = "";
       try{
        Query y = meetingPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companymeetingprotocol where meetingprotocolid=? ").setInteger(0, id);

        if(y.list().size() > 0){
            name = ((Companymeetingprotocol)y.list().get(0)).getMeetingsubject();
        }
       }catch (Exception e){}
        return name;
    }
}
