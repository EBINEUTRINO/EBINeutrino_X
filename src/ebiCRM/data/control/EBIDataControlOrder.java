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
import ebiNeutrinoSDK.model.hibernate.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.dialogs.EBICRMAddContactAddressType;
import ebiCRM.gui.panels.EBICRMOrder;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

import javax.swing.*;

public class EBIDataControlOrder {

    public Companyorder compOrder = null;
    private EBICRMOrder orderPane = null;
    private int offerID = 0;

    public EBIDataControlOrder(EBICRMOrder orderPane) {
        this.orderPane = orderPane;
        compOrder = new Companyorder();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            orderPane.ebiModule.ebiContainer.showInActionStatus("Order", true);
            orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            if (isEdit == false) {
                compOrder.setCreateddate(new Date());
                orderPane.isEdit = true;
            } else {
                createHistory(orderPane.ebiModule.ebiPGFactory.company);
                compOrder.setChangeddate(new Date());
                compOrder.setChangedfrom(EBIPGFactory.ebiUser);
            }

            compOrder.setCreatedfrom(orderPane.ebiModule.guiRenderer.getVisualPanel("Order").getCreatedFrom());
            
            compOrder.setCompany(orderPane.ebiModule.ebiPGFactory.company);
            if(orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getDate() != null){
                compOrder.setOfferdate(orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getDate());
            }else{
                compOrder.setOfferdate(new Date());
            }

            if(orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getDate() != null){
                compOrder.setValidto(orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getDate());
            }else{
                compOrder.setValidto(new Date());
            }

            compOrder.setDescription(orderPane.ebiModule.guiRenderer.getTextarea("orderDescription","Order").getText());
            compOrder.setOrdernr(orderPane.ebiModule.guiRenderer.getTextfield("orderNrText","Order").getText());
            compOrder.setName(orderPane.ebiModule.guiRenderer.getTextfield("orderNameText","Order").getText());

            if (!"".equals(orderPane.ebiModule.guiRenderer.getTextfield("orderOfferText","Order").getText())) {
                compOrder.setOfferid(offerID);
            }

            if(orderPane.ebiModule.guiRenderer.getComboBox("orderStatusText","Order").getSelectedItem() != null){
                compOrder.setStatus(orderPane.ebiModule.guiRenderer.getComboBox("orderStatusText","Order").getSelectedItem().toString());
            }

            if (!compOrder.getCompanyorderdocses().isEmpty()) {
                Iterator iter = compOrder.getCompanyorderdocses().iterator();
                while(iter.hasNext()){
                   Companyorderdocs docs = (Companyorderdocs)iter.next();
                   docs.setCompanyorder(compOrder);
                   if(docs.getOrderdocid() < 0){ docs.setOrderdocid(null);} 
                   orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(docs);
                }
            }
            //Save position
            if (!compOrder.getCompanyorderpositionses().isEmpty()) {
                Iterator iter = compOrder.getCompanyorderpositionses().iterator();
                while(iter.hasNext()){
                   Companyorderpositions pos = (Companyorderpositions)iter.next();
                   pos.setCompanyorder(compOrder);
                   if(pos.getPositionid() < 0){ pos.setPositionid(null);}
                   orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(pos);
                }
            }
            // Save Receiver
            if (!compOrder.getCompanyorderreceivers().isEmpty()) {
                Iterator iter = compOrder.getCompanyorderreceivers().iterator();
                while(iter.hasNext()){
                   Companyorderreceiver rec = (Companyorderreceiver)iter.next();
                   rec.setCompanyorder(compOrder);
                   if(rec.getReceiverid() < 0){ rec.setReceiverid(null);}
                   orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(rec);
                }
            }

            orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(compOrder);

            orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiSave",true);
            orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            
            if(!isEdit){
            	orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setID(compOrder.getOrderid());
            }
            
            this.dataShow();
            dataShowProduct();
            dataShowDoc();
            dataShowReceiver();
            orderPane.ebiModule.ebiContainer.showInActionStatus("Order", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void dataCopy(int id){
    	try {	
    		Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
	        if(y.list().size() > 0){
	        	Iterator iter = y.iterate();
	
		        	Companyorder order = (Companyorder) iter.next();
		        
		        	orderPane.ebiModule.ebiContainer.showInActionStatus("Order", true);
		        	orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
		        	orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(order);
		        	Companyorder ordnew = new Companyorder();
	                
		        	ordnew.setCreateddate(new Date());
		        	ordnew.setCreatedfrom(EBIPGFactory.ebiUser);
	                
		        	ordnew.setCompany(order.getCompany());
		        	ordnew.setOfferdate(order.getOfferdate());
		        	ordnew.setValidto(order.getValidto());
	
		        	ordnew.setDescription(order.getDescription());
		        	ordnew.setOrdernr(order.getOrdernr());
		        	ordnew.setName(order.getName()+" - (Copy)");
		        	ordnew.setIsrecieved(order.getIsrecieved());
		        	ordnew.setOfferid(order.getOfferid());
		        	ordnew.setStatus(order.getStatus());
	
	                if (!order.getCompanyorderdocses().isEmpty()) {
	                    Iterator itd = order.getCompanyorderdocses().iterator();
	                    while(itd.hasNext()){
	                        Companyorderdocs docs = (Companyorderdocs)itd.next();
	                       
	                        Companyorderdocs dc = new Companyorderdocs();
	                        dc.setCompanyorder(ordnew);
	                        dc.setCreateddate(new Date());
	                        dc.setCreatedfrom(EBIPGFactory.ebiUser);
	                        dc.setFiles(docs.getFiles());
	                        dc.setName(docs.getName());
	                       
	                        orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(dc);
	                    }
	                }
	                
	                //Save position
	                if (!order.getCompanyorderpositionses().isEmpty()) {
	                    Iterator itp = order.getCompanyorderpositionses().iterator();
	                    while(itp.hasNext()){
	                       Companyorderpositions pos = (Companyorderpositions)itp.next();
	                       
	                       Companyorderpositions p = new Companyorderpositions();
	                        p.setCompanyorder(ordnew);
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
	                       
	                       orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(p);
	                    }
	                }
	                // Save Receiver
	                if (!order.getCompanyorderreceivers().isEmpty()) {
	                    Iterator itr = order.getCompanyorderreceivers().iterator();
	                    while(itr.hasNext()){
	                       Companyorderreceiver rec = (Companyorderreceiver)itr.next();
	                       
	                       	Companyorderreceiver r = new Companyorderreceiver();
	                        r.setCompanyorder(ordnew);
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
	                       
	                       orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(r);
	                    }
	                }
	

	                orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(ordnew);
                    orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiSave",true);
	                orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	               
	                orderPane.ebiModule.ebiContainer.showInActionStatus("Order", false);
	                
	                dataShow();
	                
	                orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").
					changeSelection(orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").
							convertRowIndexToView(orderPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(orderPane.tabModOrder.data,7 , ordnew.getOrderid())),0,false,false);
	            
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    
    }


    public void dataMove(int id){
        try{
            Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(orderPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyorder ord = (Companyorder) iter.next();
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(ord);
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    orderPane.ebiModule.ebiContainer.showInActionStatus("Order", true);

                    ord.setCompany((Company)x.list().get(0));
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(ord);

                    orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiSave",true);
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    orderPane.ebiModule.ebiContainer.showInActionStatus("Order", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
    
    public void dataEdit(int id) {
      try {
        dataNew(false);

        Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

                this.compOrder = (Companyorder) iter.next();
                orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compOrder);
            	orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setID(compOrder.getOrderid());
                orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedDate(orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getCreateddate() == null ? new Date() : compOrder.getCreateddate()));
                orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedFrom(compOrder.getCreatedfrom() == null ? EBIPGFactory.ebiUser : compOrder.getCreatedfrom());

                if(compOrder.getOfferdate() != null){
                    orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").setDate(compOrder.getOfferdate());
                    orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getEditor().setText(orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getOfferdate()));

                }

                if(compOrder.getValidto() != null){
                    orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").setDate(compOrder.getValidto());
                    orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getEditor().setText(orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getValidto()));
                }

                if (compOrder.getChangeddate() != null) {
                    orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setChangedDate(orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getChangeddate()));
                    orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setChangedFrom(compOrder.getChangedfrom());
                } else {
                    orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setChangedDate("");
                    orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setChangedFrom("");
                }
                
                orderPane.ebiModule.guiRenderer.getTextfield("orderNameText","Order").setText(compOrder.getName());
                orderPane.ebiModule.guiRenderer.getTextfield("orderNrText","Order").setText(compOrder.getOrdernr() == null ? "" : compOrder.getOrdernr());

                if(compOrder.getStatus() != null){
                    orderPane.ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setSelectedItem(compOrder.getStatus());
                }

                if (compOrder.getOfferid() != null) {
                    orderPane.ebiModule.guiRenderer.getTextfield("orderOfferText","Order").setText(orderPane.ebiModule.getOfferPane().offerDataControl.getOfferNamefromId(compOrder.getOfferid()));
                }

                orderPane.ebiModule.guiRenderer.getTextarea("orderDescription","Order").setText(compOrder.getDescription());
                orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiEdit",true);

                this.dataShowDoc();
                this.dataShowProduct();
                this.dataShowReceiver();
                
                orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").
					changeSelection(orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").
							convertRowIndexToView(orderPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(orderPane.tabModOrder.data, 7, id)),0,false,false);
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
      }catch (Exception ex){}
    }

    public void createInvoiceFromOrder(int id){
       try{
        orderPane.ebiModule.getInvoicePane().dataControlInvoice.dataNew(false);

        Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

            Companyorder ord = (Companyorder) iter.next();

                  //Invoice field
                  orderPane.ebiModule.guiRenderer.getTextfield("invoiceNameText","Invoice").setText(ord.getName());

                  orderPane.ebiModule.guiRenderer.getTextfield("orderText","Invoice").setText(EBIPGFactory.getLANG("EBI_LANG_C_ORDER")+": "+ord.getOrderid());

                  if(!ord.getCompanyorderpositionses().isEmpty()){
                   Iterator ip = ord.getCompanyorderpositionses().iterator();
                    while(ip.hasNext()){
                      Companyorderpositions pos = (Companyorderpositions)ip.next();
                      Crminvoiceposition inpos = new Crminvoiceposition();

                      inpos.setCrminvoice(orderPane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice());
                      inpos.setPositionid(((orderPane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice().getCrminvoicepositions().size()+1) * (-1)));
                      inpos.setProductid(pos.getProductid());
                      inpos.setCategory(pos.getCategory());
                      inpos.setCreateddate(new Date());
                      inpos.setCreatedfrom(EBIPGFactory.ebiUser);
                      inpos.setDeduction(pos.getDeduction());
                      inpos.setDescription(pos.getDescription());
                      inpos.setNetamount(pos.getNetamount());
                      inpos.setPretax(pos.getPretax());
                      inpos.setProductname(pos.getProductname());
                      inpos.setProductnr(pos.getProductnr());
                      inpos.setQuantity(pos.getQuantity());
                      inpos.setTaxtype(pos.getTaxtype());
                      inpos.setType(pos.getType());
                      orderPane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice().getCrminvoicepositions().add(inpos);
                      orderPane.ebiModule.getInvoicePane().dataControlInvoice.dataShowProduct();
                    }
                  }

                 if(!ord.getCompanyorderreceivers().isEmpty()){
                     Iterator ir =  ord.getCompanyorderreceivers().iterator();
                     int size = ord.getCompanyorderreceivers().size();
                     while(ir.hasNext()){
                        Companyorderreceiver re = (Companyorderreceiver) ir.next();
                        if(re.getCnum() != null && re.getCnum() == 1){
                            //Invoice contact field
                            orderPane.ebiModule.guiRenderer.getComboBox("genderText","Invoice").setSelectedItem(re.getGender());
                            orderPane.ebiModule.guiRenderer.getTextfield("titleText","Invoice").setText(re.getPosition());
                            orderPane.ebiModule.guiRenderer.getTextfield("companyNameText","Invoice").setText(ord.getCompany() == null ? "" : ord.getCompany().getName());
                            orderPane.ebiModule.guiRenderer.getTextfield("nameText","Invoice").setText(re.getName());
                            orderPane.ebiModule.guiRenderer.getTextfield("surnameText","Invoice").setText(re.getSurname());
                            orderPane.ebiModule.guiRenderer.getTextfield("streetNrText","Invoice").setText(re.getStreet());
                            orderPane.ebiModule.guiRenderer.getTextfield("zipText","Invoice").setText(re.getZip());
                            orderPane.ebiModule.guiRenderer.getTextfield("locationText","Invoice").setText(re.getLocation());
                            orderPane.ebiModule.guiRenderer.getTextfield("postCodeText","Invoice").setText(re.getPbox());
                            orderPane.ebiModule.guiRenderer.getTextfield("countryText","Invoice").setText(re.getCountry());
                            orderPane.ebiModule.guiRenderer.getTextfield("telefonText","Invoice").setText(re.getPhone());
                            orderPane.ebiModule.guiRenderer.getTextfield("faxText","Invoice").setText(re.getFax());
                            orderPane.ebiModule.guiRenderer.getTextfield("emailText","Invoice").setText(re.getEmail());
                            orderPane.ebiModule.guiRenderer.getTextfield("internetText","Invoice").setText(ord.getCompany() == null ? "" : ord.getCompany().getWeb());
                            orderPane.ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(true);
                            break;
                        }else if(size == 1){
                            orderPane.ebiModule.guiRenderer.getComboBox("genderText","Invoice").setSelectedItem(re.getGender());
                            orderPane.ebiModule.guiRenderer.getTextfield("titleText","Invoice").setText(re.getPosition());
                            orderPane.ebiModule.guiRenderer.getTextfield("companyNameText","Invoice").setText(ord.getCompany() == null ? "" : ord.getCompany().getName());
                            orderPane.ebiModule.guiRenderer.getTextfield("nameText","Invoice").setText(re.getName());
                            orderPane.ebiModule.guiRenderer.getTextfield("surnameText","Invoice").setText(re.getSurname());
                            orderPane.ebiModule.guiRenderer.getTextfield("streetNrText","Invoice").setText(re.getStreet());
                            orderPane.ebiModule.guiRenderer.getTextfield("zipText","Invoice").setText(re.getZip());
                            orderPane.ebiModule.guiRenderer.getTextfield("locationText","Invoice").setText(re.getLocation());
                            orderPane.ebiModule.guiRenderer.getTextfield("postCodeText","Invoice").setText(re.getPbox());
                            orderPane.ebiModule.guiRenderer.getTextfield("countryText","Invoice").setText(re.getCountry());
                            orderPane.ebiModule.guiRenderer.getTextfield("telefonText","Invoice").setText(re.getPhone());
                            orderPane.ebiModule.guiRenderer.getTextfield("faxText","Invoice").setText(re.getFax());
                            orderPane.ebiModule.guiRenderer.getTextfield("emailText","Invoice").setText(re.getEmail());
                            orderPane.ebiModule.guiRenderer.getTextfield("internetText","Invoice").setText(ord.getCompany() == null ? "" : ord.getCompany().getWeb());
                            orderPane.ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(true);
                     }
                 }
            }


        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
       }catch (Exception ex){}
    }

    public void createServiceFromOrder(int id){
        try{
        orderPane.ebiModule.getServicePane().serviceDataControl.dataNew(true);

        Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

                Companyorder ord = (Companyorder) iter.next();

                orderPane.ebiModule.guiRenderer.getTextfield("serviceNrText","Service").setText(ord.getOrdernr() == null ? "" : ord.getOrdernr());
                orderPane.ebiModule.guiRenderer.getTextfield("serviceNameText","Service").setText(ord.getName());

                orderPane.ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").setText(ord.getDescription());

                if(ord.getCompanyorderpositionses().size() > 0){
                     Iterator it = ord.getCompanyorderpositionses().iterator();
                     if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_PRODUCT")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                            Companyorderpositions posi;
                            while (it.hasNext()) {

                                posi = (Companyorderpositions) it.next();

                                Companyservicepositions servPos = new Companyservicepositions();
                                servPos.setProductid(posi.getProductid());
                                servPos.setCategory(posi.getCategory());
                                servPos.setDeduction(posi.getDeduction());
                                servPos.setDescription(posi.getDescription());
                                servPos.setNetamount(posi.getNetamount());
                                servPos.setPretax(posi.getPretax());
                                servPos.setProductname(posi.getProductname());
                                servPos.setProductnr(posi.getProductnr());
                                servPos.setQuantity(posi.getQuantity());
                                servPos.setTaxtype(posi.getTaxtype());
                                servPos.setType(posi.getType());

                                orderPane.ebiModule.getServicePane().serviceDataControl.getservicePosList().add(servPos);
                                orderPane.ebiModule.getServicePane().serviceDataControl.getcompService().getCompanyservicepositionses().add(servPos);

                            }
                         orderPane.ebiModule.getServicePane().serviceDataControl.dataShowProduct();
                    }
                }

               
                if(ord.getCompanyorderdocses().size() > 0) {
                    Iterator itr = ord.getCompanyorderdocses().iterator();
                    if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_IMPORT_DOCUMENT")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                        while (itr.hasNext()) {

                            Companyorderdocs obj = (Companyorderdocs) itr.next();
                            Companyservicedocs docs = new Companyservicedocs();
                            docs.setName(obj.getName());
                            docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                            docs.setCreatedfrom(EBIPGFactory.ebiUser);
                            docs.setFiles(obj.getFiles());
                            orderPane.ebiModule.getServicePane().serviceDataControl.getserviceDocList().add(docs);

                        }
                        orderPane.ebiModule.getServicePane().serviceDataControl.dataShowDoc();
                    }
            }
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
        }catch (Exception ex){}
    }

    public void dataDelete(int id) {

        Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
      
            this.compOrder = (Companyorder) iter.next();
  
                try {
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(this.compOrder);

                    orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiDelete",true);
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

                    if(this.orderPane.ebiModule.ebiPGFactory.company!= null){
                        this.orderPane.ebiModule.ebiPGFactory.company.getCompanyorders().remove(compOrder);
                    }
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dataNew(true);
                dataShow();
        }
        
    }

    public void dataShow() {
      try{
      int srow = orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").getSelectedRow();

      Query y = null;
      if(orderPane.ebiModule.companyID != -1){
        y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where company.companyid=? and offerdate between ? and ? order by createddate desc");
        y.setInteger(0, orderPane.ebiModule.companyID);
        y.setDate(1, orderPane.ebiModule.ebiPGFactory.systemStartCal.getTime());
        y.setDate(2, orderPane.ebiModule.ebiPGFactory.systemEndCal.getTime());
      }else{
          y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where company.companyid IS NULL and offerdate between ? and ? order by createddate desc");
          y.setDate(0, orderPane.ebiModule.ebiPGFactory.systemStartCal.getTime());
          y.setDate(1, orderPane.ebiModule.ebiPGFactory.systemEndCal.getTime());
      }

      if(y.list().size() > 0){
      	
            orderPane.tabModOrder.data = new Object[y.list().size()][8];

            Iterator iter = y.iterate();
            int i = 0;
            while (iter.hasNext()) {

                Companyorder order = (Companyorder) iter.next();

                orderPane.tabModOrder.data[i][0] = order.getName() == null ? "" : order.getName();
                orderPane.tabModOrder.data[i][1] = order.getOfferdate() == null ? "" : orderPane.ebiModule.ebiPGFactory.getDateToString(order.getOfferdate());
                orderPane.tabModOrder.data[i][2] = order.getValidto() == null ? "" : orderPane.ebiModule.ebiPGFactory.getDateToString(order.getValidto());
                orderPane.tabModOrder.data[i][3] = String.valueOf(order.getOfferid() == null ? "0" : order.getOfferid());
                orderPane.tabModOrder.data[i][4] = order.getStatus() == null ? "" : order.getStatus();
                orderPane.tabModOrder.data[i][5] = order.getDescription() == null ? "" : order.getDescription();
                orderPane.tabModOrder.data[i][6] = order.getIsrecieved() == null ? 0 : order.getIsrecieved();
                orderPane.tabModOrder.data[i][7] = order.getOrderid();
                i++;
            }
      }else{
        orderPane.tabModOrder.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};  
      }
      orderPane.tabModOrder.fireTableDataChanged();
      orderPane.ebiModule.guiRenderer.getTable("companyorderTable","Order").changeSelection(srow,0,false,false);
      }catch (Exception ex){}
    }

    
    public Hashtable<String,Double> getTaxName(int id){

       Hashtable<String,Double> taxTable = null;
       try{
           Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);
        
   	 	NumberFormat cashFormat=NumberFormat.getCurrencyInstance();
        cashFormat.setMinimumFractionDigits(2);
        cashFormat.setMaximumFractionDigits(3);
   	 	taxTable = new Hashtable<String,Double>();
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
        
        	Companyorder or = (Companyorder) iter.next();
   	
        		Iterator itx = or.getCompanyorderpositionses().iterator();
        		while(itx.hasNext()){
        			Companyorderpositions pos = (Companyorderpositions)itx.next();
                    if(pos.getTaxtype() != null){
                        if(taxTable.containsKey(pos.getTaxtype())){
                            taxTable.put(pos.getTaxtype(),taxTable.get(pos.getTaxtype())+(((pos.getNetamount() * pos.getQuantity()) * orderPane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }else{
                            taxTable.put(pos.getTaxtype(),(((pos.getNetamount() * pos.getQuantity()) * orderPane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }
                    }
        	
        		}	
        }
       }catch (Exception ex){}
   	return taxTable;
   }
    
    
    public void dataShowReport(int id) {
        try{
            if(orderPane.isEdit){
                if(!dataStore(orderPane.isEdit)){
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

            orderPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                    useReportSystem(map,
                    orderPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_ORDER")),
                    getOrderNamefromId(id));
        }catch (Exception ex){}
    }

    public String dataShowAndMailReport(int id, boolean showWindow) {
           String fileName="";
           String to = "";

            if(orderPane.isEdit){
                if(!dataStore(orderPane.isEdit)){
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

        try {
            Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);

            Iterator iter = y.iterate();
            if(iter.hasNext()){
                Companyorder order = (Companyorder) iter.next();
                orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(order);

                Iterator ix = order.getCompanyorderreceivers().iterator();
                int i = 0;
                int c = order.getCompanyorderreceivers().size();
                while(ix.hasNext()){
                    Companyorderreceiver rec = (Companyorderreceiver)ix.next();
                    to += rec.getEmail();
                    if(i<=c){
                        to+=";";
                    }
                    i++;
                }

                fileName = orderPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                        orderPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_ORDER")),
                        getOrderNamefromId(id),showWindow,true,to);
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
       try{
        this.compOrder = new Companyorder();
        orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedDate(orderPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedFrom(EBIPGFactory.ebiUser);
        orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedDate("");
        orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setCreatedFrom("");

        orderPane.ebiModule.guiRenderer.getTextfield("orderNameText","Order").setText("");
        orderPane.ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setSelectedIndex(0);
        orderPane.ebiModule.guiRenderer.getTextfield("orderOfferText","Order").setText("");
        orderPane.ebiModule.guiRenderer.getTextarea("orderDescription","Order").setText("");
        orderPane.ebiModule.guiRenderer.getTextfield("orderNrText","Order").setText("");
        orderPane.ebiModule.guiRenderer.getVisualPanel("Order").setID(-1);
        orderPane.ebiModule.ebiPGFactory.getDataStore("Order","ebiNew",true);
        orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getEditor().setText("");
        orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getEditor().setText("");

        if(reload){
            dataShow();
            this.dataShowDoc();
            this.dataShowProduct();
            this.dataShowReceiver();
        }
       }catch (Exception ex){}
    }


    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + compOrder.getCreatedfrom());

        if (compOrder.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + compOrder.getChangedfrom());
        }

        if(compOrder.getOrdernr() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_ORDER_NUMBER") + ": " + (compOrder.getOrdernr().equals(orderPane.ebiModule.guiRenderer.getTextfield("orderNrText","Order").getText()) == true ? compOrder.getOrdernr() : compOrder.getOrdernr()+"$") );
        }

        if(compOrder.getName() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (compOrder.getName().equals(orderPane.ebiModule.guiRenderer.getTextfield("orderNameText","Order").getText()) == true ? compOrder.getName() : compOrder.getName()+"$") );
        }

        if(compOrder.getStatus() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " + (compOrder.getStatus().equals(orderPane.ebiModule.guiRenderer.getComboBox("orderStatusText","Order").getSelectedItem().toString()) == true ? compOrder.getStatus() : compOrder.getStatus()+"$") );
        }

        if(compOrder.getDescription() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + (compOrder.getDescription().equals(orderPane.ebiModule.guiRenderer.getTextarea("orderDescription","Order").getText()) == true ? compOrder.getDescription() : compOrder.getDescription()+"$") );
        }

        if(compOrder.getOfferdate() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_CREATED_DATE") + ": " + (orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getOfferdate()).equals(orderPane.ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getEditor().getText()) == true ? orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getOfferdate()) : orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getOfferdate())+"$") );
        }

        if(compOrder.getValidto() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_RECEIVED_DATE") + ": " + (orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getValidto()).equals(orderPane.ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getEditor().getText()) == true ? orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getValidto()) : orderPane.ebiModule.ebiPGFactory.getDateToString(compOrder.getValidto())+"$") );
        }

        list.add("*EOR*"); // END OF RECORD


        if (!compOrder.getCompanyorderdocses().isEmpty()) {

            Iterator iter = compOrder.getCompanyorderdocses().iterator();
            while (iter.hasNext()) {
                Companyorderdocs obj = (Companyorderdocs) iter.next();
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + obj.getName());
                list.add(orderPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + orderPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()));
                list.add(obj.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + obj.getCreatedfrom());
                list.add("*EOR*");
            }
        }

        if (!compOrder.getCompanyorderpositionses().isEmpty()) {

            Iterator iter = compOrder.getCompanyorderpositionses().iterator();

            while (iter.hasNext()) {
                Companyorderpositions obj = (Companyorderpositions) iter.next();
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

        if (!compOrder.getCompanyorderreceivers().isEmpty()) {

            Iterator iter = compOrder.getCompanyorderreceivers().iterator();
            while (iter.hasNext()) {
                Companyorderreceiver obj = (Companyorderreceiver) iter.next();
                list.add(obj.getReceivervia() == null ? EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE") + ": " + obj.getReceivervia());
                list.add(obj.getGender() == null ? EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ": " + obj.getGender());
                list.add(obj.getSurname() == null ? EBIPGFactory.getLANG("EBI_LANG_NAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + obj.getSurname());
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + obj.getName());
                list.add(obj.getPosition() == null ? EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ":" : EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION") + ": " + obj.getPosition());
                list.add(obj.getStreet() == null ? EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ": " + obj.getStreet());
                list.add(obj.getZip() == null ? EBIPGFactory.getLANG("EBI_LANG_C_ZIP") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_ZIP") + ": " + obj.getZip());
                list.add(obj.getLocation() == null ? EBIPGFactory.getLANG("EBI_LANG_C_LOCATION") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_LOCATION") + ": " + obj.getLocation());
                list.add(obj.getPbox() == null ? EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ": " + obj.getPbox());
                list.add(obj.getCountry() == null ? EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + obj.getCountry());
                list.add("*EOR*");
            }
        }

        try {
            orderPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Order", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataNewDoc() {
      try{
        File fs = orderPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
        if (fs != null) {
            byte[] file = orderPane.ebiModule.ebiPGFactory.readFileToByte(fs);
            if (file != null) {
                Companyorderdocs docs = new Companyorderdocs();
//			   java.sql.Blob blb = Hibernate.createBlob(file);
                docs.setCompanyorder(compOrder);
                docs.setName(fs.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);
                docs.setFiles(file);
 
                this.compOrder.getCompanyorderdocses().add(docs);
                this.dataShowDoc();

            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                return;
            }
        }
      }catch (Exception ex){}
    }

    public void dataViewDoc(int id) {

        String FileName  ;
        String FileType  ;
        OutputStream fos  ;
        try {

            Iterator iter = this.compOrder.getCompanyorderdocses().iterator();
            while (iter.hasNext()) {

                Companyorderdocs doc = (Companyorderdocs) iter.next();

                if (id == doc.getOrderdocid()) {
                    // Get the BLOB inputstream 

                    String file = doc.getName().replaceAll(" ", "_");

//					byte buffer[] = doc.getFiles().getBytes(1,(int)adress.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));

                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    orderPane.ebiModule.ebiPGFactory.resolverType(FileName, FileType);
                    break;
                }
            }
        } catch (FileNotFoundException exx) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        } catch (IOException exx1) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_LOADING_FILE")).Show(EBIMessage.INFO_MESSAGE);
        }

    }

    public void dataShowDoc() {
      try{
       if(this.compOrder.getCompanyorderdocses().size() > 0){
            orderPane.tabModDoc.data = new Object[this.compOrder.getCompanyorderdocses().size()][4];

            Iterator itr = this.compOrder.getCompanyorderdocses().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companyorderdocs obj = (Companyorderdocs) itr.next();

                orderPane.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                orderPane.tabModDoc.data[i][1] = orderPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : orderPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                orderPane.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                if(obj.getOrderdocid() == null || obj.getOrderdocid() < 0){ obj.setOrderdocid(((i+1) *(-1)));}
                orderPane.tabModDoc.data[i][3] = obj.getOrderdocid();
                i++;
            }
       }else{
         orderPane.tabModDoc.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
       }
        orderPane.tabModDoc.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataShowProduct() {
      try{
      if(this.compOrder.getCompanyorderpositionses().size() > 0){
            orderPane.tabModProduct.data = new Object[this.compOrder.getCompanyorderpositionses().size()][9];

            Iterator itr = this.compOrder.getCompanyorderpositionses().iterator();
            int i = 0;
          
            NumberFormat currency=NumberFormat.getCurrencyInstance();

            while (itr.hasNext()) {

                Companyorderpositions obj = (Companyorderpositions) itr.next();

                orderPane.tabModProduct.data[i][0] = String.valueOf(obj.getQuantity());
                orderPane.tabModProduct.data[i][1] = obj.getProductnr();
                orderPane.tabModProduct.data[i][2] = obj.getProductname() == null ? "" : obj.getProductname();
                orderPane.tabModProduct.data[i][3] = obj.getCategory() == null ? "" : obj.getCategory();
                orderPane.tabModProduct.data[i][4] = obj.getTaxtype() == null ? "" : obj.getTaxtype();
                orderPane.tabModProduct.data[i][5] = currency.format(orderPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction()))) == null ? "" : currency.format(orderPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
                orderPane.tabModProduct.data[i][6] = obj.getDeduction().equals("") ? "" : obj.getDeduction()+"%";
                orderPane.tabModProduct.data[i][7] = obj.getDescription() == null ? "" : obj.getDescription();
                if(obj.getPositionid() == null || obj.getPositionid() < 0){ obj.setPositionid(((i + 1)*(-1)));}
                orderPane.tabModProduct.data[i][8] = obj.getPositionid();
                i++;
            }
        }else{
            orderPane.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};
        }
        orderPane.tabModProduct.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataShowReceiver() {
      try{
       if(this.compOrder.getCompanyorderreceivers().size() > 0){
            orderPane.tabModReceiver.data = new Object[this.compOrder.getCompanyorderreceivers().size()][11];

            Iterator itr = this.compOrder.getCompanyorderreceivers().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companyorderreceiver obj = (Companyorderreceiver) itr.next();

                orderPane.tabModReceiver.data[i][0] = obj.getReceivervia() == null ? "" : obj.getReceivervia();
                orderPane.tabModReceiver.data[i][1] = obj.getGender() == null ? "" : obj.getGender();
                orderPane.tabModReceiver.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                orderPane.tabModReceiver.data[i][3] = obj.getName() == null ? "" : obj.getName();
                orderPane.tabModReceiver.data[i][4] = obj.getPosition() == null ? "" : obj.getPosition();
                orderPane.tabModReceiver.data[i][5] = obj.getStreet() == null ? "" : obj.getStreet();
                orderPane.tabModReceiver.data[i][6] = obj.getZip() == null ? "" : obj.getZip();
                orderPane.tabModReceiver.data[i][7] = obj.getLocation() == null ? "" : obj.getLocation();
                orderPane.tabModReceiver.data[i][8] = obj.getPbox() == null ? "" : obj.getPbox();
                orderPane.tabModReceiver.data[i][9] = obj.getCountry() == null ? "" : obj.getCountry();
                if(obj.getReceiverid() == null || obj.getReceiverid() < 0){ obj.setReceiverid(((i + 1)*(-1)));}
                orderPane.tabModReceiver.data[i][10] = obj.getReceiverid();
                i++;
            }
       }else{
          orderPane.tabModReceiver.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "", "", ""}};
       }
        orderPane.tabModReceiver.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataDeleteDoc(int id) {

        Iterator iter = this.compOrder.getCompanyorderdocses().iterator();
        while (iter.hasNext()) {

            Companyorderdocs doc = (Companyorderdocs) iter.next();

            if (doc.getOrderdocid() == id) {
                  if(id >= 0){
                    try {
                        orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                        orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.compOrder.getCompanyorderdocses().remove(doc);
                this.dataShowDoc();
                break;
            }
        }
    }

    public void dataDeleteReceiver(int id) {

        Iterator iter = this.compOrder.getCompanyorderreceivers().iterator();
        while (iter.hasNext()) {

            Companyorderreceiver orderrec = (Companyorderreceiver) iter.next();

            if (orderrec.getReceiverid() == id) {
               if(id >=0){
                try {
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(orderrec);
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
               }
                this.compOrder.getCompanyorderreceivers().remove(orderrec);
                this.dataShowReceiver();
                break;
            }
        }
    }

    public void dataEditReceiver(int id) {
        try{
        Iterator iter = this.compOrder.getCompanyorderreceivers().iterator();
        while (iter.hasNext()) {

            Companyorderreceiver orderrec = (Companyorderreceiver) iter.next();

            if (orderrec.getReceiverid() == id) {

                EBICRMAddContactAddressType addCo = new EBICRMAddContactAddressType(orderPane.ebiModule, this,orderrec);
                addCo.setVisible();

                this.dataShowReceiver();
                break;
            }
        }
        }catch (Exception ex){}

    }

    public void dataDeleteProduct(int id) {

        Iterator iter = this.compOrder.getCompanyorderpositionses().iterator();
        while (iter.hasNext()) {

            Companyorderpositions orderpro = (Companyorderpositions) iter.next();

            if (orderpro.getPositionid() == id) {
              if(id >= 0){
                try {
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(orderpro);
                    orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                }catch (Exception e) {
                    e.printStackTrace();
                }
              }
                this.compOrder.getCompanyorderpositionses().remove(orderpro);
                this.dataShowProduct();
                break;
            }
        }
    }

    public Companyorder getCompOrder() {
        return compOrder;
    }

    public Set<Companyorderdocs> getOrderDocList() {
        return compOrder.getCompanyorderdocses();
    }

    public Set<Companyorderpositions> getOrderPosList() {
        return compOrder.getCompanyorderpositionses();
    }

    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }

    public int getOfferID(){
        return this.offerID;
    }

    public List<Companyoffer> getOfferList() {

        Query y = null;
            try{
                if(orderPane.ebiModule.companyID != -1){
                    y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                            createQuery("from Companyoffer where company.companyid=? ").setInteger(0, orderPane.ebiModule.companyID);
                }else{
                    y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                            createQuery("from Companyoffer where company is null ");
                }
            }catch (Exception ex){}
        return y.list();
    }

    private String getOrderNamefromId(int id) {

        String name = "";
           try{
            Query y = orderPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyorder where orderid=? ").setInteger(0, id);

            if(y.list().size() > 0){
                name = ((Companyorder)y.list().get(0)).getName();
            }
           }catch (Exception ex){}
        return name;
    }
}