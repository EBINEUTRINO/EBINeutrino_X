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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.dialogs.EBICRMAddContactAddressType;
import ebiCRM.gui.panels.EBICRMOffer;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyoffer;
import ebiNeutrinoSDK.model.hibernate.Companyofferdocs;
import ebiNeutrinoSDK.model.hibernate.Companyofferpositions;
import ebiNeutrinoSDK.model.hibernate.Companyofferreceiver;
import ebiNeutrinoSDK.model.hibernate.Companyopportunity;
import ebiNeutrinoSDK.model.hibernate.Companyorderdocs;
import ebiNeutrinoSDK.model.hibernate.Companyorderpositions;
import ebiNeutrinoSDK.model.hibernate.Companyorderreceiver;

import javax.swing.*;

public class EBIDataControlOffer {

    public Companyoffer compOffer = null;
    private EBICRMOffer offerPane = null;
    public int opportunityID = 0;

    public EBIDataControlOffer(EBICRMOffer offerPane) {
        this.offerPane = offerPane;
        compOffer = new Companyoffer();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", true);
            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            if (isEdit == false) {
                compOffer.setCreateddate(new Date());
                offerPane.isEdit = true;
            } else {
                createHistory(offerPane.ebiModule.ebiPGFactory.company);
                compOffer.setChangeddate(new Date());
                compOffer.setChangedfrom(EBIPGFactory.ebiUser);
            }

            compOffer.setCreatedfrom(offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").getCreatedFrom());
            compOffer.setCompany(offerPane.ebiModule.ebiPGFactory.company);

            if(offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer").getDate() != null){
                compOffer.setOfferdate(offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText", "Offer").getDate());
            }else{
                compOffer.setOfferdate(new Date());
            }

            if(offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer").getDate() != null){
                compOffer.setValidto(offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer").getDate());
            }

            compOffer.setDescription(offerPane.ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").getText());
            compOffer.setOffernr(offerPane.ebiModule.guiRenderer.getTextfield("offerNrText","Offer").getText());
            compOffer.setName(offerPane.ebiModule.guiRenderer.getTextfield("offerNameText","Offer").getText());

            if (!"".equals(offerPane.ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").getText())) {
                compOffer.setOpportunityid(opportunityID);
            }

            if(offerPane.ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").getSelectedItem() != null){
                compOffer.setStatus(offerPane.ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").getSelectedItem().toString());
            }

            //Save docs
            if (!this.compOffer.getCompanyofferdocses().isEmpty()) {
                Iterator iter = this.compOffer.getCompanyofferdocses().iterator();
                while(iter.hasNext()){
                    Companyofferdocs doc = (Companyofferdocs)iter.next();
                    doc.setCompanyoffer(this.compOffer);
                    if(doc.getOfferdocid() < 0){ doc.setOfferdocid(null);}
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(doc);
                }

            }
            //Save position
            if (!this.compOffer.getCompanyofferpositionses().isEmpty()) {
                Iterator iter = this.compOffer.getCompanyofferpositionses().iterator();
                while(iter.hasNext()){
                    Companyofferpositions pos = (Companyofferpositions)iter.next();
                    pos.setCompanyoffer(this.compOffer);
                    if(pos.getPositionid() < 0){ pos.setPositionid(null);}
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(pos);
                }
            }
            //Save receiver
            if (!this.compOffer.getCompanyofferreceivers().isEmpty()) {
                Iterator iter = this.compOffer.getCompanyofferreceivers().iterator();
                while(iter.hasNext()){
                    Companyofferreceiver rec = (Companyofferreceiver)iter.next();
                    rec.setCompanyoffer(this.compOffer);
                    if(rec.getReceiverid() < 0){ rec.setReceiverid(null);}
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(rec);
                }
            }

            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(compOffer);

            offerPane.ebiModule.ebiPGFactory.getDataStore("Offer", "ebiSave",true);
            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            if(!isEdit){
            	offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setID(compOffer.getOfferid());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.dataShow();
        dataShowProduct();
        dataShowDoc();
        dataShowReceiver();
        offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", false);
        return true;
    }

    
    public void dataCopy(int id){
    	try {
	        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);
	        
	        if(y.list().size() > 0){
	        	Iterator iter = y.iterate();
	        
	        	Companyoffer offer = (Companyoffer) iter.next();
	        	offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(offer);
	        	offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
	        	
	                offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", true);
	
	                Companyoffer ofnew = new Companyoffer();    
	                ofnew.setCreateddate(new Date());
	                ofnew.setCreatedfrom(EBIPGFactory.ebiUser);
	                ofnew.setCompany(offer.getCompany());
	                ofnew.setOfferdate(offer.getOfferdate());
	                ofnew.setValidto(offer.getValidto());
	
	                ofnew.setDescription(offer.getDescription());
	                ofnew.setOffernr(offer.getOffernr());
	                ofnew.setName(offer.getName()+" - (Copy)");
	                ofnew.setIsrecieved(offer.getIsrecieved());
	                ofnew.setOpportunityid(offer.getOpportunityid());
	                
	                ofnew.setStatus(offer.getStatus());
	
	                //Save docs
	                if (!offer.getCompanyofferdocses().isEmpty()) {
	                    Iterator itd = offer.getCompanyofferdocses().iterator();
	                    while(itd.hasNext()){
	                        Companyofferdocs doc = (Companyofferdocs)itd.next();
	                        
	                        Companyofferdocs dc = new Companyofferdocs();
	                        dc.setCompanyoffer(ofnew);
	                        dc.setCreateddate(new Date());
	                        dc.setCreatedfrom(EBIPGFactory.ebiUser);
	                        dc.setFiles(doc.getFiles());
	                        dc.setName(doc.getName());
	                        
	                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(dc);
	                    }
	
	                }
	                //Save position
	                if (!offer.getCompanyofferpositionses().isEmpty()) {
	                    Iterator itp = offer.getCompanyofferpositionses().iterator();
	                    while(itp.hasNext()){
	                        Companyofferpositions pos = (Companyofferpositions)itp.next();
	                       
	                        Companyofferpositions p = new Companyofferpositions();
	                        p.setCompanyoffer(ofnew);
	                        p.setCategory(pos.getCategory());
	                        p.setCreateddate(new Date());
	                        p.setCreatedfrom(EBIPGFactory.ebiUser);
	                        p.setDeduction(pos.getDeduction());
	                        p.setDescription(pos.getDescription());
	                        p.setNetamount(pos.getNetamount());
	                        p.setPretax(pos.getPretax());
	                        p.setProductid(pos.getProductid());
	                        p.setProductname(pos.getProductname());
	                        p.setProductnr(pos.getProductnr());
	                        p.setQuantity(pos.getQuantity());
	                        p.setTaxtype(pos.getTaxtype());
	                        p.setType(pos.getType());
	                        
	                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(p);
	                    }
	                }
	                
	                //Save receiver
	                if (!offer.getCompanyofferreceivers().isEmpty()) {
	                    Iterator itr = offer.getCompanyofferreceivers().iterator();
	                    while(itr.hasNext()){
	                        Companyofferreceiver rec = (Companyofferreceiver)itr.next();
	                      
	                        Companyofferreceiver r = new Companyofferreceiver();
	                        r.setCompanyoffer(ofnew);
	                        r.setCnum(rec.getCnum());
	                        r.setCountry(rec.getCountry());
	                        r.setCreateddate(new Date());
	                        r.setCreatedfrom(EBIPGFactory.ebiUser);
	                        r.setEmail(rec.getEmail());
	                        r.setFax(rec.getFax());
	                        r.setGender(rec.getGender());
	                        r.setLocation(rec.getLocation());
	                        r.setMittelname(rec.getMittelname());
	                        r.setName(rec.getName());
	                        r.setPbox(rec.getPbox());
	                        r.setPhone(rec.getPhone());
	                        r.setPosition(rec.getPosition());
	                        r.setReceivervia(rec.getReceivervia());
	                        r.setStreet(rec.getStreet());
	                        r.setSurname(rec.getSurname());
	                        r.setZip(rec.getZip());
	                        
	                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(r);
	                    }
	                }

	                offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(ofnew);
	                
	                offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", false);
                    offerPane.ebiModule.ebiPGFactory.getDataStore("Offer","ebiSave",true);
	                offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	               
	                dataShow();
	                
	                offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").
					changeSelection(offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").
							convertRowIndexToView(offerPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(offerPane.tabModoffer.data,7 , ofnew.getOfferid())),0,false,false);
	                
	        }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }


    public void dataMove(int id){
        try{
            Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=?").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(offerPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyoffer offer = (Companyoffer) iter.next();
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(offer);
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", true);

                    offer.setCompany((Company)x.list().get(0));
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(offer);

                    offerPane.ebiModule.ebiPGFactory.getDataStore("Offer","ebiSave",true);
                    offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    offerPane.ebiModule.ebiContainer.showInActionStatus("Offer", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void dataEdit(int id) {
        dataNew(false);

        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);

        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
        
        		this.compOffer = (Companyoffer) iter.next();
                this.offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compOffer);
        		offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setID(compOffer.getOfferid());
                offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedDate(offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getCreateddate() == null ? new Date() : compOffer.getCreateddate()));
                offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedFrom(compOffer.getCreatedfrom() == null ? EBIPGFactory.ebiUser : compOffer.getCreatedfrom());

                if(compOffer.getOfferdate() != null){
                    offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer").setDate(compOffer.getOfferdate());
                    offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer").getEditor().setText(offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getOfferdate()));
                }

                if(compOffer.getValidto() != null){
                    offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer").setDate(compOffer.getValidto());
                    offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer").getEditor().setText(offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getValidto()));
                }

                if (compOffer.getChangeddate() != null) {
                    offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedDate(offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getChangeddate()));
                    offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedFrom(compOffer.getChangedfrom());
                } else {
                    offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedDate("");
                    offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedFrom("");
                }
                offerPane.ebiModule.guiRenderer.getTextfield("offerNrText","Offer").setText(compOffer.getOffernr() == null ? "" : compOffer.getOffernr());
                offerPane.ebiModule.guiRenderer.getTextfield("offerNameText","Offer").setText(compOffer.getName());

                if(compOffer.getStatus() != null){
                    offerPane.ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setSelectedItem(compOffer.getStatus());
                }

                if (compOffer.getOpportunityid() != null) {
                    offerPane.ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").setText(getOpportunityName(compOffer.getOpportunityid()));
                }
                offerPane.ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").setText(compOffer.getDescription());
                offerPane.ebiModule.ebiPGFactory.getDataStore("Offer","ebiEdit",true);

                this.dataShowDoc();
                this.dataShowProduct();
                this.dataShowReceiver();
                
                offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").
    				changeSelection(offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").
    						convertRowIndexToView(offerPane.ebiModule.dynMethod.
    								getIdIndexFormArrayInATable(offerPane.tabModoffer.data, 7, id)),0,false,false);
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
    }

    public void createOrderFromOffer(int id){

        offerPane.ebiModule.getOrderPane().orderDataControl.dataNew(true);
        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

            Companyoffer ofr = (Companyoffer) iter.next();

                offerPane.ebiModule.guiRenderer.getTextfield("orderNrText","Order").setText(ofr.getOffernr() == null ? "" : ofr.getOffernr());
                offerPane.ebiModule.guiRenderer.getTextfield("orderNameText","Order").setText(ofr.getName());

                offerPane.ebiModule.guiRenderer.getTextfield("orderOfferText","Order").setText(ofr.getName());
                offerPane.ebiModule.getOrderPane().orderDataControl.setOfferID(id);

                offerPane.ebiModule.guiRenderer.getTextarea("orderDescription","Order").setText(ofr.getDescription());

                if(ofr.getCompanyofferpositionses().size() > 0){
                     Iterator it = ofr.getCompanyofferpositionses().iterator();
                     if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_PRODUCT")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                            Companyofferpositions posi;
                            while (it.hasNext()) {
    
                                posi = (Companyofferpositions) it.next();

                                Companyorderpositions ordPos = new Companyorderpositions();
                                ordPos.setProductid(posi.getProductid());
                                ordPos.setCategory(posi.getCategory());
                                ordPos.setDeduction(posi.getDeduction());
                                ordPos.setDescription(posi.getDescription());
                                ordPos.setNetamount(posi.getNetamount());
                                ordPos.setPretax(posi.getPretax());
                                ordPos.setProductname(posi.getProductname());
                                ordPos.setProductnr(posi.getProductnr());
                                ordPos.setQuantity(posi.getQuantity());
                                ordPos.setTaxtype(posi.getTaxtype());
                                ordPos.setType(posi.getType());
                                offerPane.ebiModule.getOrderPane().orderDataControl.getCompOrder().getCompanyorderpositionses().add(ordPos);

                            }
                         offerPane.ebiModule.getOrderPane().orderDataControl.dataShowProduct();
                    }
                }

                if(ofr.getCompanyofferreceivers().size() > 0){
                    Iterator itsr = ofr.getCompanyofferreceivers().iterator();
                    if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_CONTACTS")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                            Companyofferreceiver contact  ;
                            while (itsr.hasNext()) {

                                contact = (Companyofferreceiver) itsr.next();
                                Companyorderreceiver ordRec = new Companyorderreceiver();
                                ordRec.setReceivervia(contact.getReceivervia());
                                ordRec.setCnum(contact.getCnum());
                                ordRec.setGender(contact.getGender());
                                ordRec.setSurname(contact.getSurname());
                                ordRec.setName(contact.getName());
                                ordRec.setPosition(contact.getPosition());
                                ordRec.setFax(contact.getFax());
                                ordRec.setEmail(contact.getEmail());
                                ordRec.setCountry(contact.getCountry());
                                ordRec.setLocation(contact.getLocation());
                                ordRec.setStreet(contact.getStreet());
                                ordRec.setZip(contact.getZip());
                                ordRec.setPbox(contact.getPbox());
                                offerPane.ebiModule.getOrderPane().orderDataControl.getCompOrder().getCompanyorderreceivers().add(ordRec);

                            }
                         offerPane.ebiModule.getOrderPane().orderDataControl.dataShowReceiver();

                    }
                }
                if(ofr.getCompanyofferdocses().size() > 0) {
                    Iterator itr = ofr.getCompanyofferdocses().iterator();
                    if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_DOCUMENT")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                        while (itr.hasNext()) {

                            Companyofferdocs obj = (Companyofferdocs) itr.next();
                            Companyorderdocs docs = new Companyorderdocs();
                            docs.setName(obj.getName());
                            docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                            docs.setCreatedfrom(EBIPGFactory.ebiUser);
                            docs.setFiles(obj.getFiles());
                            offerPane.ebiModule.getOrderPane().orderDataControl.getOrderDocList().add(docs);

                        }
                        offerPane.ebiModule.getOrderPane().orderDataControl.dataShowDoc();
                    }
                }
           
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }

    }

    public void dataDelete(int id) {

        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

            this.compOffer = (Companyoffer) iter.next();
            try {
                offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(this.compOffer);

                offerPane.ebiModule.ebiPGFactory.getDataStore("Offer","ebiDelete",true);
                offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            } catch (HibernateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
	        this.dataNew(true);
	        this.dataShow();
        }
    }

    public void dataShow() {

      int srow = offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").getSelectedRow();

      Query y = null;
      if(offerPane.ebiModule.companyID != -1){
            y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where company.companyid=? and offerdate between ? and ? order by createddate desc ");
            y.setInteger(0, offerPane.ebiModule.companyID);
            y.setDate(1, offerPane.ebiModule.ebiPGFactory.systemStartCal.getTime());
            y.setDate(2, offerPane.ebiModule.ebiPGFactory.systemEndCal.getTime());
      }else{
            y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where company.companyid IS NULL and offerdate between ? and ? order by createddate desc ");
            y.setDate(0, offerPane.ebiModule.ebiPGFactory.systemStartCal.getTime());
            y.setDate(1, offerPane.ebiModule.ebiPGFactory.systemEndCal.getTime());
      }

      if(y.list().size() > 0){
    	    offerPane.tabModoffer.data = new Object[y.list().size()][8];
    	    
      		Iterator iter = y.iterate();
           
            int i = 0;
            while (iter.hasNext()) {

                Companyoffer cOffer = (Companyoffer) iter.next();
                
                offerPane.tabModoffer.data[i][0] = cOffer.getName() == null ? "" : cOffer.getName();
                offerPane.tabModoffer.data[i][1] = cOffer.getOfferdate() == null ? "" : offerPane.ebiModule.ebiPGFactory.getDateToString(cOffer.getOfferdate());
                offerPane.tabModoffer.data[i][2] = cOffer.getValidto() == null ? "" : offerPane.ebiModule.ebiPGFactory.getDateToString(cOffer.getValidto());
                offerPane.tabModoffer.data[i][3] = String.valueOf(cOffer.getOpportunityid() == null ? "0" : cOffer.getOpportunityid());
                offerPane.tabModoffer.data[i][4] = cOffer.getStatus() == null ? "" : cOffer.getStatus();
                offerPane.tabModoffer.data[i][5] = cOffer.getDescription() == null ? "" : cOffer.getDescription();
                offerPane.tabModoffer.data[i][6] = cOffer.getIsrecieved() == null ? 0 : cOffer.getIsrecieved();
                offerPane.tabModoffer.data[i][7] = cOffer.getOfferid();
                i++;
            }

      }else{
          offerPane.tabModoffer.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};
      }
       
      offerPane.tabModoffer.fireTableDataChanged();
      offerPane.ebiModule.guiRenderer.getTable("companyOfferTable","Offer").changeSelection(srow,0,false,false);
    }

    public Hashtable<String,Double> getTaxName(int id){
    	
    	 Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);
         
    	 NumberFormat cashFormat=NumberFormat.getCurrencyInstance();
         cashFormat.setMinimumFractionDigits(2);
         cashFormat.setMaximumFractionDigits(3);
    	 Hashtable<String,Double> taxTable = new Hashtable<String,Double>();
         
         if(y.list().size() > 0){
         	Iterator iter = y.iterate();
         
         	Companyoffer of = (Companyoffer) iter.next();

         		Iterator itx = of.getCompanyofferpositionses().iterator();
         		while(itx.hasNext()){
         			Companyofferpositions pos = (Companyofferpositions)itx.next();
                    if(pos.getTaxtype() != null){
                        if(taxTable.containsKey(pos.getTaxtype())){
                            taxTable.put(pos.getTaxtype(),taxTable.get(pos.getTaxtype())+(((pos.getNetamount() * pos.getQuantity()) * offerPane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }else{
                            taxTable.put(pos.getTaxtype(),(((pos.getNetamount() * pos.getQuantity()) * offerPane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }
                    }
         		}
         }
    	
    	return taxTable;
    }

    public void dataShowReport(int id) {

        if(offerPane.isEdit){
            if(!dataStore(offerPane.isEdit)){
               return;
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);
        Hashtable<String, Double> taxTable = getTaxName(id);
        Iterator itx = taxTable.keySet().iterator();
        
        String taxTypes = "";
        String taxValues = "";
        
        NumberFormat cashFormat=NumberFormat.getCurrencyInstance();
        cashFormat.setMinimumFractionDigits(2);
        cashFormat.setMaximumFractionDigits(3);
        
        while(itx.hasNext()){
        	String key = ((String)itx.next());
        	taxTypes += key+":\n";
        	taxValues += cashFormat.format(taxTable.get(key))+"\n";
        }
        
        map.put("TAXDIFF_TEXT",  taxTypes);
        map.put("TAXDIFF_VALUE",  taxValues);
        
        offerPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                offerPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_OFFER")),
                getOfferNamefromId(id));

    }

    public String dataShowAndMailReport(int id, boolean showWindow) {
           String fileName="";

           if(offerPane.isEdit){
                if(!dataStore(offerPane.isEdit)){
                   return null;
                }
           }

           Map<String, Object> map = new HashMap<String, Object>();
           map.put("ID", id);
           Hashtable<String, Double> taxTable = getTaxName(id);
           Iterator itx = taxTable.keySet().iterator();
           
           String taxTypes = "";
           String taxValues = "";
           
           NumberFormat cashFormat=NumberFormat.getCurrencyInstance();
           cashFormat.setMinimumFractionDigits(2);
           cashFormat.setMaximumFractionDigits(3);
           
           while(itx.hasNext()){
           	String key = ((String)itx.next());
           	taxTypes += key+":\n";
           	taxValues += cashFormat.format(taxTable.get(key))+"\n";
           }
           
            map.put("TAXDIFF_TEXT",  taxTypes);
            map.put("TAXDIFF_VALUE",  taxValues);
            String to= "";
            try {
                Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);

                    Iterator iter = y.iterate();
                    if(iter.hasNext()){
                        Companyoffer offer = (Companyoffer) iter.next();
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(offer);

                        Iterator ix = offer.getCompanyofferreceivers().iterator();
                        int i =0;
                        int c = y.list().size();
                        while(ix.hasNext()){
                            Companyofferreceiver rec = (Companyofferreceiver)ix.next();
                            
                            to += rec.getEmail();
                            if(i<= c-1){
                              to += ";";
                            }
                            i++;
                        }

                        //EMail is send from the Report generator
                        fileName = offerPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                                        offerPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_OFFER")),
                                                                                                            getOfferNamefromId(id),showWindow,true,to);
                    }else{
                         EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_NO_RECEIVER_WAS_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                    }

            } catch (Exception ex) {
                ex.printStackTrace();
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }

           return fileName;
   }

    public void dataNew(boolean reload) {
        
        compOffer = new Companyoffer();
        offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedDate(offerPane.ebiModule.ebiPGFactory.getDateToString(new Date()));
        offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedFrom(EBIPGFactory.ebiUser);
        offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedDate("");
        offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setChangedFrom("");
        
        offerPane.ebiModule.guiRenderer.getTextfield("offerNrText","Offer").setText("");
        offerPane.ebiModule.guiRenderer.getTextfield("offerNameText","Offer").setText("");
        offerPane.ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setSelectedIndex(0);
        offerPane.ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").setText("");
        offerPane.ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").setText("");
        offerPane.ebiModule.guiRenderer.getTextfield("offerNrText","Offer").setText("");
        offerPane.ebiModule.ebiPGFactory.getDataStore("Offer","ebiNew",true);
        offerPane.ebiModule.guiRenderer.getVisualPanel("Offer").setID(-1);

        offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer")
                .getEditor().setText("");

        offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer")
                .getEditor().setText("");

        if(reload){
            dataShow();
            this.dataShowDoc();
            this.dataShowProduct();
            this.dataShowReceiver();
        }
    }

    private void createHistory(Company cm) {

        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + compOffer.getCreatedfrom());

        if (compOffer.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + compOffer.getChangedfrom());
        }

        if(compOffer.getOffernr() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_OFFER_NUMBER") + ": " + (compOffer.getOffernr().equals(offerPane.ebiModule.guiRenderer.getTextfield("offerNrText","Offer").getText()) == true ? compOffer.getOffernr() : compOffer.getOffernr()+"$") );
        }

        if(compOffer.getName() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (compOffer.getName().equals(offerPane.ebiModule.guiRenderer.getTextfield("offerNameText","Offer").getText()) == true ? compOffer.getName() : compOffer.getName()+"$") );
        }

        if(compOffer.getStatus() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " + (compOffer.getStatus().equals(offerPane.ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").getSelectedItem().toString()) == true ? compOffer.getStatus() : compOffer.getStatus()+"$") );
        }

        if(compOffer.getOfferdate() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_SEND_DATE") + ": " + (offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getOfferdate()).equals(offerPane.ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer").getEditor().getText()) == true ? offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getOfferdate()) : offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getOfferdate())+"$") );
        }

        if(compOffer.getValidto() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_VALID_TO") + ": " + (offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getValidto()).equals(offerPane.ebiModule.guiRenderer.getTimepicker("validToText","Offer").getEditor().getText()) == true ? offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getValidto()) : offerPane.ebiModule.ebiPGFactory.getDateToString(compOffer.getValidto())+"$") );
        }

        if(compOffer.getDescription() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + (compOffer.getDescription().equals(offerPane.ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").getText()) == true ? compOffer.getDescription() : compOffer.getDescription()+"$") );
        }

        list.add("*EOR*"); // END OF RECORD

        if (!compOffer.getCompanyofferdocses().isEmpty()) {

            Iterator iter = compOffer.getCompanyofferdocses().iterator();
            while (iter.hasNext()) {
                Companyofferdocs obj = (Companyofferdocs) iter.next();
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + obj.getName());
                list.add(offerPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + offerPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()));
                list.add(obj.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + obj.getCreatedfrom());
                list.add("*EOR*");
            }

        }

        if (!compOffer.getCompanyofferpositionses().isEmpty()) {

            Iterator iter = compOffer.getCompanyofferpositionses().iterator();

            while (iter.hasNext()) {
                Companyofferpositions obj = (Companyofferpositions) iter.next();
                list.add(EBIPGFactory.getLANG("EBI_LANG_QUANTITY") + ": " + String.valueOf(obj.getQuantity()));
                list.add(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NUMBER") + ": " + obj.getProductnr());
                list.add(obj.getProductname() == null ? EBIPGFactory.getLANG("EBI_LANG_NAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + obj.getProductname());
                list.add(obj.getCategory() == null ? EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ":" : EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + obj.getCategory());
                list.add(obj.getTaxtype() == null ? EBIPGFactory.getLANG("EBI_LANG_TAX") + ":" : EBIPGFactory.getLANG("EBI_LANG_TAX") + ": " + obj.getTaxtype());
                list.add(String.valueOf(obj.getPretax()) == null ? EBIPGFactory.getLANG("EBI_LANG_PRICE") + ":" : EBIPGFactory.getLANG("EBI_LANG_PRICE") + ": " + String.valueOf(obj.getPretax()));
                list.add(String.valueOf(obj.getDeduction()) == null ? EBIPGFactory.getLANG("EBI_LANG_DEDUCTION") + ":" : EBIPGFactory.getLANG("EBI_LANG_DEDUCTION") + ": " + String.valueOf(obj.getDeduction()));
                list.add(obj.getDescription() == null ? EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ":" : EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + obj.getDescription());
                list.add("*EOR*");
            }
        }

        if (!compOffer.getCompanyofferreceivers().isEmpty()) {

            Iterator iter = compOffer.getCompanyofferreceivers().iterator();
            while (iter.hasNext()) {
                Companyofferreceiver obj = (Companyofferreceiver) iter.next();
                list.add(obj.getReceivervia() == null ? EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE") + ": " + obj.getReceivervia());
                list.add(obj.getGender() == null ? EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ": " + obj.getGender());
                list.add(obj.getSurname() == null ? EBIPGFactory.getLANG("EBI_LANG_NAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + obj.getSurname());
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + obj.getName());
                list.add(obj.getPosition() == null ? EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ":" : EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ":" + obj.getPosition());
                list.add(obj.getStreet() == null ? EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ": " + obj.getStreet());
                list.add(obj.getZip() == null ? EBIPGFactory.getLANG("EBI_LANG_C_ZIP") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_ZIP") + ": " + obj.getZip());
                list.add(obj.getLocation() == null ? EBIPGFactory.getLANG("EBI_LANG_C_LOCATION") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_LOCATION") + ": " + obj.getLocation());
                list.add(obj.getPbox() == null ? EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ": " + obj.getPbox());
                list.add(obj.getCountry() == null ? EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + obj.getCountry());
                list.add("*EOR*");
            }
        }

        try {
            offerPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(cm == null ? -1 : cm.getCompanyid(), "Offer", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getOpportunityName(int id) {
        String retName = "";
        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                createQuery("from Companyopportunity where opportunityid=? ").setInteger(0, id);

        Iterator iter = y.list().iterator();
        if (iter.hasNext()) {
            Companyopportunity opportunity = (Companyopportunity) iter.next();
            this.opportunityID = opportunity.getOpportunityid();
            retName = opportunity.getName();

        }
        return retName;
    }

    public void dataDeleteDoc(int id) {

        Iterator iter = compOffer.getCompanyofferdocses().iterator();
        while (iter.hasNext()) {

            Companyofferdocs doc = (Companyofferdocs) iter.next();

            if (doc.getOfferdocid() == id) {
                   if(id >= 0){
                        try {
                            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                            offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                   }
                this.compOffer.getCompanyofferdocses().remove(doc);
                this.dataShowDoc();
                break;
            }
        }
    }

    public void dataDeleteReceiver(int id) {

        Iterator iter = compOffer.getCompanyofferreceivers().iterator();
        while (iter.hasNext()) {

            Companyofferreceiver offerrec = (Companyofferreceiver) iter.next();

            if (offerrec.getReceiverid() == id) {
                  if(id >=0){
                    try {
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(offerrec);
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.compOffer.getCompanyofferreceivers().remove(offerrec);
                this.dataShowReceiver();
                break;
            }
        }
    }

    public void dataEditReceiver(int id) {

        Iterator iter = compOffer.getCompanyofferreceivers().iterator();
        while (iter.hasNext()) {

            Companyofferreceiver offerrec = (Companyofferreceiver) iter.next();

            if (offerrec.getReceiverid() == id) {
                EBICRMAddContactAddressType addCo = new EBICRMAddContactAddressType(offerPane.ebiModule, this,offerrec);
                addCo.setVisible();
                
                this.dataShowReceiver();
                break;
            }
        }
    }

    public void dataDeleteProduct(int id) {

        Iterator iter = compOffer.getCompanyofferpositionses().iterator();
        while (iter.hasNext()) {

            Companyofferpositions offerpro = (Companyofferpositions) iter.next();

            if (offerpro.getPositionid() == id) {
                   if(id >=0){
                    try {
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(offerpro);
                        offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (HibernateException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   }
                this.compOffer.getCompanyofferpositionses().remove(offerpro);
                this.dataShowProduct();
                break;
            }

        }
    }

    public void dataShowReceiver() {
       if(this.compOffer.getCompanyofferreceivers().size() > 0){
            offerPane.tabModReceiver.data = new Object[this.compOffer.getCompanyofferreceivers().size()][11];

            Iterator itr = this.compOffer.getCompanyofferreceivers().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companyofferreceiver obj = (Companyofferreceiver) itr.next();

                offerPane.tabModReceiver.data[i][0] = obj.getReceivervia() == null ? "" : obj.getReceivervia();
                offerPane.tabModReceiver.data[i][1] = obj.getGender() == null ? "" : obj.getGender();
                offerPane.tabModReceiver.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                offerPane.tabModReceiver.data[i][3] = obj.getName() == null ? "" : obj.getName();
                offerPane.tabModReceiver.data[i][4] = obj.getPosition() == null ? "" : obj.getPosition();
                offerPane.tabModReceiver.data[i][5] = obj.getStreet() == null ? "" : obj.getStreet();
                offerPane.tabModReceiver.data[i][6] = obj.getZip() == null ? "" : obj.getZip();
                offerPane.tabModReceiver.data[i][7] = obj.getLocation() == null ? "" : obj.getLocation();
                offerPane.tabModReceiver.data[i][8] = obj.getPbox() == null ? "" : obj.getPbox();
                offerPane.tabModReceiver.data[i][9] = obj.getCountry() == null ? "" : obj.getCountry();
                if(obj.getReceiverid() == null || obj.getReceiverid() < 0){obj.setReceiverid(((i+1)*(-1)));}
                offerPane.tabModReceiver.data[i][10] = obj.getReceiverid();
                i++;
            }
       }else{
           offerPane.tabModReceiver.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "", "", ""}};
       }
        offerPane.tabModReceiver.fireTableDataChanged();
    }

    public void dataShowProduct() {
      if(this.compOffer.getCompanyofferpositionses().size() > 0){
        offerPane.tabModProduct.data = new Object[this.compOffer.getCompanyofferpositionses().size()][9];

        Iterator itr = this.compOffer.getCompanyofferpositionses().iterator();
        int i = 0;
        NumberFormat currency=NumberFormat.getCurrencyInstance();

        while (itr.hasNext()) {
            Companyofferpositions obj = (Companyofferpositions) itr.next();

            offerPane.tabModProduct.data[i][0] = String.valueOf(obj.getQuantity());
            offerPane.tabModProduct.data[i][1] = obj.getProductnr();
            offerPane.tabModProduct.data[i][2] = obj.getProductname() == null ? "" : obj.getProductname();
            offerPane.tabModProduct.data[i][3] = obj.getCategory() == null ? "" : obj.getCategory();
            offerPane.tabModProduct.data[i][4] = obj.getTaxtype() == null ? "" : obj.getTaxtype();
            offerPane.tabModProduct.data[i][5] = currency.format(offerPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction()))) == null ? "" : currency.format(offerPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
            offerPane.tabModProduct.data[i][6] = obj.getDeduction().equals("") ? "" : obj.getDeduction()+"%";
            offerPane.tabModProduct.data[i][7] = obj.getDescription() == null ? "" : obj.getDescription();
            if(obj.getPositionid() == null || obj.getPositionid() < 0){ obj.setPositionid(((i+1)*(-1)));}
            offerPane.tabModProduct.data[i][8] = obj.getPositionid();
            i++;
        }
      }else{
         offerPane.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};
      }
        offerPane.tabModProduct.fireTableDataChanged();
    }

    public void dataShowDoc() {
      if(this.compOffer.getCompanyofferdocses().size() > 0){
            offerPane.tabModDoc.data = new Object[this.compOffer.getCompanyofferdocses().size()][4];

            Iterator itr = this.compOffer.getCompanyofferdocses().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companyofferdocs obj = (Companyofferdocs) itr.next();

                offerPane.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                offerPane.tabModDoc.data[i][1] = offerPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : offerPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                offerPane.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                if(obj.getOfferdocid() == null || obj.getOfferdocid() < 0){ obj.setOfferdocid(((i+1)*(-1)));}
                offerPane.tabModDoc.data[i][3] = obj.getOfferdocid();

                i++;
            }
      }else{
           offerPane.tabModDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
      }
        offerPane.tabModDoc.fireTableDataChanged();
    }


    public void dataViewDoc(int id) {
        String FileName  ;
        String FileType  ;
        OutputStream fos  ;
        try {

            Iterator iter = this.compOffer.getCompanyofferdocses().iterator();
            while (iter.hasNext()) {

                Companyofferdocs doc = (Companyofferdocs) iter.next();

                if (id == doc.getOfferdocid()) {
                    // Get the BLOB inputstream

                    String file = doc.getName().replaceAll(" ", "_");
//						byte buffer[] = doc.getFiles().getBytes(1, (int)doc.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));
                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    offerPane.ebiModule.ebiPGFactory.resolverType(FileName, FileType);
                    break;
                }
            }
        } catch (FileNotFoundException exx) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
        } catch (IOException exx1) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_LOADING_FILE")).Show(EBIMessage.ERROR_MESSAGE);
        }
    }



    public void dataAddDoc() {

        File fs = offerPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
        if (fs != null) {

            byte[] file = offerPane.ebiModule.ebiPGFactory.readFileToByte(fs);
            if (file != null) {
                Companyofferdocs docs = new Companyofferdocs();
                docs.setCompanyoffer(compOffer);
                docs.setName(fs.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);
                docs.setFiles(file);
                this.compOffer.getCompanyofferdocses().add(docs);
                this.dataShowDoc();
            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                return;
            }
        }
    }

    public Companyoffer getCompOffer() {
        return compOffer;
    }

    public void setCompOffer(Companyoffer compOffer) {
        this.compOffer = compOffer;
    }

    public Set<Companyofferdocs> getOfferDocList() {
        return compOffer.getCompanyofferdocses();
    }

    public Set<Companyofferpositions> getOfferPosList() {
        return compOffer.getCompanyofferpositionses();
    }

    public Set<Companyofferreceiver> getOfferRecieverList() {
        return compOffer.getCompanyofferreceivers();
    }

    public String getOfferNamefromId(int id) {

        String name = "";
        Query y = offerPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyoffer where offerid=? ").setInteger(0, id);

        if(y.list().size() > 0){
             name = ((Companyoffer)y.list().get(0)).getName();
        }

        return name;
    }
}