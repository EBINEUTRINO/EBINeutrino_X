package ebiCRM.data.control;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.Query;

import ebiCRM.gui.dialogs.EBIMeetingAddContactDialog;
import ebiCRM.gui.panels.EBICRMOpportunityPane;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyofferdocs;
import ebiNeutrinoSDK.model.hibernate.Companyofferreceiver;
import ebiNeutrinoSDK.model.hibernate.Companyopportunity;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitycontact;
import ebiNeutrinoSDK.model.hibernate.Companyopporunitydocs;

import javax.swing.*;

public class EBIDataControlOpportunity {

    private Companyopportunity opportunity = null;
    private EBICRMOpportunityPane opportunityPane = null;
    private String evalStatus = "";
    private String budgetStatus = "";
    private String purchState = "";
    private String sStatus = "";

    public EBIDataControlOpportunity(EBICRMOpportunityPane opportunityPane) {
        opportunity = new Companyopportunity();
        this.opportunityPane = opportunityPane;
        
    }

    public boolean dataStore(boolean isEdit) {

        try {
            opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", true);
            opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            if (isEdit == false) {
                opportunity.setCreateddate(new Date());
                opportunityPane.isEdit = true;
            } else {
                createHistory(opportunityPane.ebiModule.ebiPGFactory.company);
                opportunity.setChangeddate(new Date());
                opportunity.setChangedfrom(EBIPGFactory.ebiUser);
            }

            opportunity.setCreatedfrom(opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").getCreatedFrom());
            
            opportunity.setCompany(opportunityPane.ebiModule.ebiPGFactory.company);
            opportunity.setName(opportunityPane.ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().getItem().toString());

            if(opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem() != null){
                opportunity.setEvaluationstatus(opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem().toString());

                if (isEdit == true && opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem().equals(this.evalStatus)) {
                    opportunity.setEvaluetiondate(new java.sql.Date(new Date().getTime()));
                } else {
                    opportunity.setEvaluetiondate(new java.sql.Date(new Date().getTime()));
                }
            }

            if(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem() != null){
                opportunity.setBudgetstatus(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString());

                if (isEdit == true && opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().equals(this.budgetStatus)) {
                    opportunity.setBudgetdate(new java.sql.Date(new Date().getTime()));
                } else {
                    opportunity.setBudgetdate(new java.sql.Date(new Date().getTime()));
                }
            }

            opportunity.setSalestage(opportunityPane.ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").getSelectedItem().toString());
            if (isEdit == true && opportunityPane.ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").getSelectedItem().equals(this.purchState)) {
                opportunity.setSalestagedate(new java.sql.Date(new Date().getTime()));
            } else {
                opportunity.setSalestagedate(new java.sql.Date(new Date().getTime()));
            }

            if(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem() != null){
                opportunity.setOpportunitystatus(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString());
                if (isEdit == true && opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString().equals(this.sStatus)) {
                    opportunity.setOpportunitystatusdate(new java.sql.Date(new Date().getTime()));
                } else {
                    opportunity.setOpportunitystatusdate(new java.sql.Date(new Date().getTime()));
                }
            }

            if(opportunityPane.ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").getSelectedItem() != null){
                opportunity.setProbability(opportunityPane.ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").getSelectedItem().toString());
            }

            if(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem() != null){
                opportunity.setBusinesstype(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem().toString());
            }

            if (!"".equals(opportunityPane.ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").getText())) {
                try {
                    opportunity.setOpportunityvalue(Double.parseDouble(opportunityPane.ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").getValue().toString()));
                } catch (NumberFormatException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_VALID_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }

            if (opportunityPane.ebiModule.guiRenderer.getCheckBox("closeOpportunity","Opportunity").isSelected()) {
                opportunity.setIsclose(true);
            } else {
                opportunity.setIsclose(false);
            }

            if (opportunityPane.ebiModule.guiRenderer.getCheckBox("closeOpportunity","Opportunity").isSelected()) {
                opportunity.setClosedate(new java.sql.Date(new Date().getTime()));
            }

            opportunity.setDescription(opportunityPane.ebiModule.guiRenderer.getTextarea("opportunityDescription","Opportunity").getText());

           //Save contacts
           if(!opportunity.getCompanyopportunitycontacts().isEmpty()){
               Iterator iter = opportunity.getCompanyopportunitycontacts().iterator();
               while(iter.hasNext()){
                   Companyopportunitycontact contact = (Companyopportunitycontact)iter.next();
                   contact.setCompanyopportunity(opportunity);
                   if(contact.getOpportunitycontactid() < 0){contact.setOpportunitycontactid(null);}
                   opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(contact);
               }
           }
           //Save docs
           if(!this.opportunity.getCompanyopporunitydocses().isEmpty()){
               Iterator iter = opportunity.getCompanyopporunitydocses().iterator();
               while(iter.hasNext()){
                   Companyopporunitydocs doc = (Companyopporunitydocs)iter.next();
                   doc.setCompanyopportunity(opportunity);
                   if(doc.getDocid() < 0){doc.setDocid(null);}
                   opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(doc);
               }
           }

           opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(opportunity);
           opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiSave",true);
           opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            
           if(!isEdit){
            	opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setID(opportunity.getOpportunityid());
           }
            
           dataShow();
           dataShowDoc();
           showOpportunityContacts();
           opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }
    
    
    public void dataCopy(int id){
    	
    	try {	
	    	 Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=?").setInteger(0, id);
	         
	         if(y.list().size() > 0){
	         	Iterator iter = y.iterate();
	
	         		Companyopportunity opportunity = (Companyopportunity) iter.next();
	         		opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(opportunity);
	         		
	         		opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
	                opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", true);
	                
	                Companyopportunity opnew = new Companyopportunity();
	                
	                opnew.setCreateddate(new Date());
	                opnew.setCreatedfrom(EBIPGFactory.ebiUser);
	                
	                opnew.setCompany(opportunity.getCompany());
	                opnew.setName(opportunity.getName()+" - (Copy)");
	
	                opnew.setEvaluationstatus(opportunity.getEvaluationstatus());
	                opnew.setEvaluetiondate(opportunity.getEvaluetiondate());
	                opnew.setBudgetstatus(opportunity.getBudgetstatus());
	                opnew.setBudgetdate(opportunity.getBudgetdate());
	                opnew.setSalestage(opportunity.getSalestage());
	                opnew.setSalestagedate(opportunity.getSalestagedate());
	                
	                opnew.setOpportunitystatus(opportunity.getOpportunitystatus());
	                opnew.setOpportunitystatusdate(opportunity.getOpportunitystatusdate());
	
	                opnew.setProbability(opportunity.getProbability());
	                opnew.setBusinesstype(opportunity.getBusinesstype());
	
	                opnew.setOpportunityvalue(opportunity.getOpportunityvalue());
	                opnew.setIsclose(opportunity.getIsclose());
	                opnew.setClosedate(opportunity.getClosedate());
	                opnew.setDescription(opportunity.getDescription());
	
	               //Save contacts
	               if(!opportunity.getCompanyopportunitycontacts().isEmpty()){
	                   Iterator itc = opportunity.getCompanyopportunitycontacts().iterator();
	                   while(itc.hasNext()){
	                       Companyopportunitycontact contact = (Companyopportunitycontact)itc.next();
	                       
	                       Companyopportunitycontact cd = new Companyopportunitycontact();
	                       cd.setBirddate(contact.getBirddate());
	                       cd.setCompanyopportunity(opnew);
	                       cd.setCountry(contact.getCountry());
	                       cd.setCreateddate(new Date());
	                       cd.setCreatedfrom(EBIPGFactory.ebiUser);
	                       cd.setDescription(contact.getDescription());
	                       cd.setEmail(contact.getEmail());
	                       cd.setFax(contact.getFax());
	                       cd.setGender(contact.getGender());
	                       cd.setLocation(contact.getLocation());
	                       cd.setMittelname(contact.getMittelname());
	                       cd.setMobile(contact.getMobile());
	                       cd.setName(contact.getName());
	                       cd.setPbox(contact.getPbox());
	                       cd.setPhone(contact.getPhone());
	                       cd.setPos(contact.getPos());
	                       cd.setPosition(contact.getPosition());
	                       cd.setStreet(contact.getStreet());
	                       cd.setSurname(contact.getSurname());
	                       cd.setZip(contact.getZip());
	                       
	                       opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cd);
	                   }
	               }
	               
	               //Save docs
	               if(!opportunity.getCompanyopporunitydocses().isEmpty()){
	                   Iterator itd = opportunity.getCompanyopporunitydocses().iterator();
	                   while(itd.hasNext()){
	                       Companyopporunitydocs doc = (Companyopporunitydocs)itd.next();
	                       
	                       Companyopporunitydocs dc = new Companyopporunitydocs();
	                       dc.setCompanyopportunity(opnew);
	                       dc.setCreateddate(new Date());
	                       dc.setCreatedfrom(EBIPGFactory.ebiUser);
	                       dc.setFiles(doc.getFiles());
	                       dc.setName(doc.getName());
	                       
	                       opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(dc);
	                   }
	               }

	                opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(opnew);
                    opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiSave",true);
	                opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	                opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", false);
	                
	                dataShow();
	                
	                opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").
					changeSelection(opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").
							convertRowIndexToView(opportunityPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(opportunityPane.tabModel.data,7 , opnew.getOpportunityid())),0,false,false);
	                
	         	
	         }
         
         } catch (Exception ex) {
             ex.printStackTrace();
         }
    	
    	 
    }


    public void dataMove(int id){

        try{
            Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=?").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(opportunityPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyopportunity opp = (Companyopportunity) iter.next();
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(opp);
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", true);

                    opp.setCompany((Company)x.list().get(0));
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(opp);

                    opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiSave",true);
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    opportunityPane.ebiModule.ebiContainer.showInActionStatus("Opportunity", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public void dataEdit(int id) {
      try{
        dataNew();

        Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=?").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

                this.opportunity = (Companyopportunity) iter.next();
                opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(opportunity);
            	opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setID(opportunity.getOpportunityid());

                if(opportunity.getName() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().setItem(opportunity.getName());
                }

                if(opportunity.getEvaluationstatus() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setSelectedItem(opportunity.getEvaluationstatus());
                    evalStatus = opportunity.getEvaluationstatus();
                }

                if(opportunity.getBudgetstatus() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setSelectedItem(opportunity.getBudgetstatus());
                    budgetStatus = opportunity.getBudgetstatus();
                }

                if(opportunity.getSalestage() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setSelectedItem(opportunity.getSalestage());
                    purchState = opportunity.getSalestage();
                }

                if(opportunity.getOpportunitystatus() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setSelectedItem(opportunity.getOpportunitystatus());
                    sStatus = opportunity.getOpportunitystatus();
                }

                if(opportunity.getProbability() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").setSelectedItem(opportunity.getProbability());
                }

                if(opportunity.getBusinesstype() != null){
                    opportunityPane.ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setSelectedItem(opportunity.getBusinesstype());
                }

                opportunityPane.ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setValue(new Double(this.opportunity.getOpportunityvalue() == null ? 0.0 : this.opportunity.getOpportunityvalue()));

                if(opportunity.getIsclose() != null){
                    opportunityPane.ebiModule.guiRenderer.getCheckBox("closeOpportunity","Opportunity").setSelected(opportunity.getIsclose());
                }

                opportunityPane.ebiModule.guiRenderer.getTextarea("opportunityDescription","Opportunity").setText(opportunity.getDescription());

                opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedDate(opportunityPane.ebiModule.ebiPGFactory.getDateToString(opportunity.getCreateddate() == null ? new Date() : opportunity.getCreateddate()));
                opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedFrom(opportunity.getCreatedfrom() == null ? EBIPGFactory.ebiUser : opportunity.getCreatedfrom());

                if (opportunity.getChangeddate() != null) {
                    opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedDate(opportunityPane.ebiModule.ebiPGFactory.getDateToString(opportunity.getChangeddate()));
                    opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedFrom(opportunity.getChangedfrom());
                } else {
                    opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedDate("");
                    opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedFrom("");
                }

                opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiEdit",true);
                this.showOpportunityContacts();
                this.dataShowDoc();
                
                opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").
					changeSelection(opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").
							convertRowIndexToView(opportunityPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(opportunityPane.tabModel.data, 7, id)),0,false,false);
 
            }else{
            	 EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
      }catch (Exception ex){}
    }

    public void createOfferFromOpportunity(int id){
       try{
        opportunityPane.ebiModule.getOfferPane().offerDataControl.dataNew(true);

        Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=?").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

            Companyopportunity opp = (Companyopportunity) iter.next();

                opportunityPane.ebiModule.guiRenderer.getTextfield("offerNameText","Offer").setText(opp.getName());
                opportunityPane.ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").setText(opp.getDescription());
                opportunityPane.ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").setText(opp.getName());
                opportunityPane.ebiModule.getOfferPane().offerDataControl.opportunityID = id;

                if(opp.getCompanyopportunitycontacts().size() > 0){
                
                    if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_CONTACT_DATA")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
                            Iterator it = opp.getCompanyopportunitycontacts().iterator();
                            Companyopportunitycontact contact;
                            while (it.hasNext()) {

                                contact = (Companyopportunitycontact) it.next();

                                Companyofferreceiver offRec = new Companyofferreceiver();
                                offRec.setReceivervia("EMail");
                                offRec.setGender(contact.getGender());
                                offRec.setSurname(contact.getSurname());
                                offRec.setName(contact.getName());
                                offRec.setPosition(contact.getPosition());
                                offRec.setFax(contact.getFax());
                                offRec.setEmail(contact.getEmail());
                                offRec.setCnum(contact.getPos());
                                opportunityPane.ebiModule.getOfferPane().offerDataControl.getCompOffer().getCompanyofferreceivers().add(offRec);

                            }
                        opportunityPane.ebiModule.getOfferPane().offerDataControl.dataShowReceiver();

                    }
                }
                if(opp.getCompanyopporunitydocses().size() > 0 ){
                    if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_DOCUMENT")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                        Iterator itr = opp.getCompanyopporunitydocses().iterator();

                        while (itr.hasNext()) {

                            Companyopporunitydocs obj = (Companyopporunitydocs) itr.next();
                            Companyofferdocs docs = new Companyofferdocs();
                            docs.setName(obj.getName());
                            docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                            docs.setCreatedfrom(EBIPGFactory.ebiUser);
                            docs.setFiles(obj.getFiles());
                            opportunityPane.ebiModule.getOfferPane().offerDataControl.getOfferDocList().add(docs);

                        }
                        opportunityPane.ebiModule.getOfferPane().offerDataControl.dataShowDoc();
                    }
           
            }
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
       }catch (Exception ex){}
    }

    public void dataDelete(int id) {
       try{
        Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=?").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

            Companyopportunity opp = (Companyopportunity) iter.next();

                try {
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(opp);

                    opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiDelete",true);
                    opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

                    if(this.opportunityPane.ebiModule.ebiPGFactory.company != null){
                        this.opportunityPane.ebiModule.ebiPGFactory.company.getCompanyopportunities().remove(opp);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
             dataNew();
             dataShow();
        }
        }catch(Exception ex){}
    }

    public void dataShow() {
     try{
      int srow = opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").getSelectedRow();

      Query y = null;
      if(opportunityPane.ebiModule.companyID != -1){
            y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where company.companyid=? order by createddate desc");
            y.setInteger(0, opportunityPane.ebiModule.companyID);
      }else{
            y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where company.companyid IS NULL order by createddate desc");
      }

      if(y.list().size() > 0){
    	    opportunityPane.tabModel.data = new Object[y.list().size()][8];
      		Iterator iter = y.iterate();
            int i = 0;

            NumberFormat currency=NumberFormat.getNumberInstance();
            currency.setMinimumFractionDigits(2);
            currency.setMaximumFractionDigits(2);

            while (iter.hasNext()) {
                Companyopportunity obj = (Companyopportunity) iter.next();
                
                opportunityPane.tabModel.data[i][0] = obj.getName() == null ? "" : obj.getName();
                opportunityPane.tabModel.data[i][1] = obj.getSalestage() == null ? "" : obj.getSalestage();
                opportunityPane.tabModel.data[i][2] = obj.getProbability() == null ? "" : obj.getProbability();
                opportunityPane.tabModel.data[i][3] = obj.getBusinesstype() == null ? "" : obj.getBusinesstype();
                try{
                    opportunityPane.tabModel.data[i][4] = currency.format(obj.getOpportunityvalue()).equals("0")  ? "" :  currency.format(obj.getOpportunityvalue());
                }catch(Exception ex){
                    opportunityPane.tabModel.data[i][4] = "";
                }

                opportunityPane.tabModel.data[i][5] = obj.getIsclose() == null ? "" : obj.getIsclose();
                opportunityPane.tabModel.data[i][6] = obj.getClosedate() == null ? "" : opportunityPane.ebiModule.ebiPGFactory.getDateToString(obj.getClosedate());
                opportunityPane.tabModel.data[i][7] = obj.getOpportunityid();
                i++;
            }
      }else{
        opportunityPane.tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "",""}}; 
      }
      opportunityPane.tabModel.fireTableDataChanged();
      opportunityPane.ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").changeSelection(srow,0,false,false);  
     }catch (Exception ex){ ex.printStackTrace();}
    }

    public void dataShowReport(int id) {
       try{
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ID", id);

            opportunityPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                    useReportSystem(map,
                    opportunityPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY")),
                    getOpportunityNamefromId(id));
       }catch (Exception ex){}

    }

    public String dataShowAndMailReport(int id, boolean showWindow) {
        String fileName="";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);

        try {

            Query query =  opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=? ").setInteger(0, id);

            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                Companyopportunity opp = (Companyopportunity) iter.next();

                Iterator it =  opp.getCompanyopportunitycontacts().iterator();
                String to = "";
                int i = 0;

                int c =  opp.getCompanyopportunitycontacts().size();

                while(it.hasNext()){

                   Companyopportunitycontact rec = (Companyopportunitycontact)it.next();

                   to += rec.getEmail();

                   if(i<=c-1){
                        to+=";";
                   }
                   i++;

                }
                fileName = opportunityPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                             opportunityPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY")),
                                    getOpportunityNamefromId(id),showWindow,true,to);
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
            opportunity = new Companyopportunity();
            opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedDate(opportunityPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
            opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedFrom(EBIPGFactory.ebiUser);
            opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedDate("");
            opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedFrom("");
            opportunityPane.ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().setItem("");
            opportunityPane.ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setText("");
            opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setSelectedIndex(0);
            opportunityPane.ebiModule.guiRenderer.getTextarea("opportunityDescription","Opportunity").setText("");
            opportunityPane.ebiModule.guiRenderer.getCheckBox("closeOpportunity","Opportunity").setSelected(false);

            opportunityPane.ebiModule.guiRenderer.getVisualPanel("Opportunity").setID(-1);

            opportunityPane.ebiModule.ebiPGFactory.getDataStore("Opportunity","ebiNew",true);
            dataShow();
            showOpportunityContacts();
            dataShowDoc();
       }catch (Exception ex){ ex.printStackTrace();}
    }

    public void dataAddContact() {
       try{
        EBIMeetingAddContactDialog newContact = new EBIMeetingAddContactDialog(this.opportunityPane.ebiModule,false,null,null,false);
        newContact.setVisible();
       }catch (Exception ex){}
    }

    public void dataRemoveContact(int id) {
       try{
        Iterator iter = this.opportunity.getCompanyopportunitycontacts().iterator();
        while (iter.hasNext()) {

            Companyopportunitycontact con = (Companyopportunitycontact) iter.next();

            if (con.getOpportunitycontactid() == id) {
                 if(id >= 0){
                    try {
                        opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(con);
                        opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                 }
                this.opportunity.getCompanyopportunitycontacts().remove(con);
                this.showOpportunityContacts();
                break;
            }
        }
       }catch (Exception ex){}
    }

    public void dataEditContact(int id) {
      try{
        Iterator iter = this.opportunity.getCompanyopportunitycontacts().iterator();
        Companyopportunitycontact contact;
        while (iter.hasNext()) {

            contact = (Companyopportunitycontact) iter.next();

            if (contact.getOpportunitycontactid() == id) {
                EBIMeetingAddContactDialog newContact = new EBIMeetingAddContactDialog(this.opportunityPane.ebiModule,false,null,contact,true);
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
      }catch (Exception ex){}
    }

    public void addContact(EBIMeetingAddContactDialog newContact, Companyopportunitycontact contact){
       try{
        contact.setCompanyopportunity(opportunity);
        contact.setGender(newContact.getGenderText());
        contact.setSurname(newContact.getSurnameText());
        contact.setName(newContact.getNameText());
        contact.setStreet(newContact.getStreet());
        contact.setZip(newContact.getZip());
        contact.setLocation(newContact.getLocation());
        contact.setPbox(newContact.getPbox());
        contact.setCountry(newContact.getCountry());
        contact.setPosition(newContact.getPositionText());
        contact.setBirddate(newContact.getBirddateText());
        contact.setPhone(newContact.getTelephoneText());
        contact.setFax(newContact.getFaxText());
        contact.setMobile(newContact.getMobileText());
        contact.setEmail(newContact.getEMailText());
        contact.setDescription(newContact.getDescriptionText());
        if(contact.getPos() == null){
            contact.setPos(0);
        }

        this.opportunity.getCompanyopportunitycontacts().add(contact);
        showOpportunityContacts();
       }catch (Exception ex){}
    }

    public void showOpportunityContacts() {
      try{
      if(this.opportunity.getCompanyopportunitycontacts().size() > 0){
            opportunityPane.tabModelContact.data = new Object[this.opportunity.getCompanyopportunitycontacts().size()][7];

            Iterator itr = this.opportunity.getCompanyopportunitycontacts().iterator();
            int i = 0;
            while (itr.hasNext()) {
                Companyopportunitycontact obj = (Companyopportunitycontact) itr.next();

                opportunityPane.tabModelContact.data[i][0] = obj.getPosition() == null ? "" : obj.getPosition();
                opportunityPane.tabModelContact.data[i][1] = obj.getGender() == null ? "" : obj.getGender();
                opportunityPane.tabModelContact.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                opportunityPane.tabModelContact.data[i][3] = obj.getName() == null ? "" : obj.getName();
                opportunityPane.tabModelContact.data[i][4] = obj.getPhone() == null ? "" : obj.getPhone();
                opportunityPane.tabModelContact.data[i][5] = obj.getMobile() == null ? "" : obj.getMobile();
                if(obj.getOpportunitycontactid() == null || obj.getOpportunitycontactid() < 0){ obj.setOpportunitycontactid(((i+1)*(-1)));}
                opportunityPane.tabModelContact.data[i][6] = obj.getOpportunitycontactid();
                i++;
            }
      }else{
         opportunityPane.tabModelContact.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
      }
        opportunityPane.tabModelContact.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataDeleteDoc(int id) {
         try{
            Iterator iter = this.opportunity.getCompanyopporunitydocses().iterator();
            while (iter.hasNext()) {

                Companyopporunitydocs doc = (Companyopporunitydocs) iter.next();

                if (id == doc.getDocid()) {
                    if(id >= 0){
                        try {
                            opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                            opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                            opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    this.opportunity.getCompanyopporunitydocses().remove(doc);
                    this.dataShowDoc();
                    break;
                }
            }
         }catch (Exception ex){}
    }

    public void dataShowDoc() {
         try{
          if(this.opportunity.getCompanyopporunitydocses().size() > 0){
                opportunityPane.tabOpportunityDoc.data = new Object[this.opportunity.getCompanyopporunitydocses().size()][4];

                Iterator itr = this.opportunity.getCompanyopporunitydocses().iterator();
                int i = 0;
                while (itr.hasNext()) {

                    Companyopporunitydocs obj = (Companyopporunitydocs) itr.next();
                    opportunityPane.tabOpportunityDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                    opportunityPane.tabOpportunityDoc.data[i][1] = opportunityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : opportunityPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                    opportunityPane.tabOpportunityDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                    if(obj.getDocid() == null || obj.getDocid() < 0){ obj.setDocid(((i+1)*(-1)));}
                    opportunityPane.tabOpportunityDoc.data[i][3] = obj.getDocid();

                    i++;
                }
          }else{
             opportunityPane.tabOpportunityDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
          }
           opportunityPane.tabOpportunityDoc.fireTableDataChanged();
         }catch (Exception ex){}
    }

    public void dataViewDoc(int id) {

        String FileName  ;
        String FileType  ;
        OutputStream fos  ;
        try {

            Iterator iter = this.opportunity.getCompanyopporunitydocses().iterator();
            while (iter.hasNext()) {

                Companyopporunitydocs doc = (Companyopporunitydocs) iter.next();

                if (id == doc.getDocid()) {
                    // Get the BLOB inputstream

                    String file = doc.getName().replaceAll(" ", "_");

                    //byte buffer[] = adress.getFiles().getBytes(1,(int)adress.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));

                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    opportunityPane.ebiModule.ebiPGFactory.resolverType(FileName, FileType);
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
        File fs = opportunityPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
        if (fs != null) {

            byte[] file = opportunityPane.ebiModule.ebiPGFactory.readFileToByte(fs);

            if (file != null) {
                //java.sql.Blob blb = Hibernate.createBlob(file);

                Companyopporunitydocs docs = new Companyopporunitydocs();
                docs.setCompanyopportunity(this.opportunity);
                docs.setName(fs.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);

                docs.setFiles(file);
                
                this.opportunity.getCompanyopporunitydocses().add(docs);
                this.dataShowDoc();
            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                return;
            }
        }
       }catch (Exception ex){}
    }

    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (this.opportunity.getName().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().getItem().toString()) == true ? this.opportunity.getName() : this.opportunity.getName()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_EVALUATING_STATUS") + ": " + (this.opportunity.getEvaluationstatus().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getEvaluationstatus() : this.opportunity.getEvaluationstatus()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_BUDGETSTATUS") + ": " + (this.opportunity.getBudgetstatus().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getBudgetstatus() : this.opportunity.getBudgetstatus()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_SALE_STAGE") + ": " + (this.opportunity.getSalestage().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getSalestage() : this.opportunity.getSalestage()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " + (this.opportunity.getOpportunitystatus().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getOpportunitystatus() : this.opportunity.getOpportunitystatus()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_PROBABILITY") + ": " + (this.opportunity.getProbability().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getProbability() : this.opportunity.getProbability()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_BUSINESS_TYP") + ": " + (this.opportunity.getBusinesstype().equals(opportunityPane.ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem().toString()) == true ? this.opportunity.getBusinesstype() : this.opportunity.getBusinesstype()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_VALUE") + ": " + (String.valueOf(this.opportunity.getOpportunityvalue()).equals(opportunityPane.ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").getValue().toString()) == true ? String.valueOf(this.opportunity.getOpportunityvalue()) : String.valueOf(this.opportunity.getOpportunityvalue())+"$") );

        String chCls = "";
        if(opportunityPane.ebiModule.guiRenderer.getCheckBox("closeOpportunity","Opportunity").isSelected() != this.opportunity.getIsclose() ){
            chCls = "$";
        }

        list.add(EBIPGFactory.getLANG("EBI_LANG_CLOSED") + ": " + (String.valueOf(this.opportunity.getIsclose()))+chCls);

        list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + (this.opportunity.getDescription().equals(opportunityPane.ebiModule.guiRenderer.getTextarea("opportunityDescription","Opportunity").getText()) == true ? this.opportunity.getDescription() : this.opportunity.getDescription()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + opportunityPane.ebiModule.ebiPGFactory.getDateToString(opportunity.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + opportunity.getCreatedfrom());

        if (opportunity.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + opportunityPane.ebiModule.ebiPGFactory.getDateToString(opportunity.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + opportunity.getChangedfrom());
        }
        list.add("*EOR*"); // END OF RECORD


        if (!this.opportunity.getCompanyopportunitycontacts().isEmpty()) {

            Iterator iter = this.opportunity.getCompanyopportunitycontacts().iterator();

            while (iter.hasNext()) {
                Companyopportunitycontact obj = (Companyopportunitycontact) iter.next();
                list.add(obj.getPosition() == null ? EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ":" : EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ": " + obj.getPosition());
                list.add(obj.getSurname() == null ? EBIPGFactory.getLANG("EBI_LANG_SURNAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_SURNAME") + ": " + obj.getSurname());
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + obj.getName());
                list.add(obj.getPhone() == null ? EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE") + ":" : obj.getPhone());
                list.add(obj.getMobile() == null ? EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE") + ": " + obj.getMobile());
                list.add("*EOR*"); // END OF RECORD
            }
        }

        try {
            opportunityPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Opportunity", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Companyopportunity> getOppportunityList() {

        Query y = null;
        try{
        if(opportunityPane.ebiModule.companyID != -1){
                y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                            createQuery("from Companyopportunity where company.companyid=? ").setInteger(0, opportunityPane.ebiModule.companyID);
        }else{
                y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                            createQuery("from Companyopportunity where company is null ");
        }
        }catch (Exception ex){}
        return y.list();
    }

    public Companyopportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Companyopportunity opportunity) {
        this.opportunity = opportunity;
    }

    public String getEvalStatus() {
        return evalStatus;
    }

    public void setEvalStatus(String evalStatus) {
        this.evalStatus = evalStatus;
    }

    public String getBudgetStatus() {
        return budgetStatus;
    }

    public void setBudgetStatus(String budgetStatus) {
        this.budgetStatus = budgetStatus;
    }

    public String getPurchState() {
        return purchState;
    }

    public void setPurchState(String purchState) {
        this.purchState = purchState;
    }

    public String getSStatus() {
        return sStatus;
    }

    public void setSStatus(String status) {
        sStatus = status;
    }

    private String getOpportunityNamefromId(int id) {

        String name = "";
        try{
            Query y = opportunityPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyopportunity where opportunityid=? ").setInteger(0, id);

            if(y.list().size() > 0){
                name = ((Companyopportunity)y.list().get(0)).getName();
            }
        }catch (Exception ex){}
        return name;
    }
}
