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
import ebiNeutrinoSDK.utils.EBIGIFFilter;
import ebiNeutrinoSDK.utils.EBIJPGFilter;
import ebiNeutrinoSDK.utils.EBIPNGFilter;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMService;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyservice;
import ebiNeutrinoSDK.model.hibernate.Companyservicedocs;
import ebiNeutrinoSDK.model.hibernate.Companyservicepositions;
import ebiNeutrinoSDK.model.hibernate.Companyservicepsol;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;

import javax.swing.*;

public class EBIDataControlService {

    public Companyservice compService = null;
    private EBICRMService servicePane = null;

    public EBIDataControlService(EBICRMService servicePane) {
        this.servicePane = servicePane;
        compService = new Companyservice();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            servicePane.ebiModule.ebiContainer.showInActionStatus("Service", true);
            servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            if (isEdit == false) {
                compService.setCreateddate(new Date());
                servicePane.isEdit = true;
            } else {
                createHistory(servicePane.ebiModule.ebiPGFactory.company);
                compService.setChangeddate(new Date());
                compService.setChangedfrom(EBIPGFactory.ebiUser);
            }

            compService.setCreatedfrom(servicePane.ebiModule.guiRenderer.getVisualPanel("Service").getCreatedFrom());
            compService.setCompany(servicePane.ebiModule.ebiPGFactory.company);
            compService.setDescription(servicePane.ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").getText());
            compService.setServicenr(servicePane.ebiModule.guiRenderer.getTextfield("serviceNrText","Service").getText());
            compService.setName(servicePane.ebiModule.guiRenderer.getTextfield("serviceNameText","Service").getText());

            if(servicePane.ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").getSelectedItem() != null){
                compService.setStatus(servicePane.ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").getSelectedItem().toString());
            }

            if(servicePane.ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").getSelectedItem() != null){
                compService.setCategory(servicePane.ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").getSelectedItem().toString());
            }

            if(servicePane.ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").getSelectedItem() != null){
                compService.setType(servicePane.ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").getSelectedItem().toString());
            }

            //SAVE DOC
            if (!compService.getCompanyservicedocses().isEmpty()) {
                Iterator iter = compService.getCompanyservicedocses().iterator();
                while(iter.hasNext()){
                    Companyservicedocs docs = (Companyservicedocs) iter.next();
                    docs.setCompanyservice(compService);
                    if(docs.getServicedocid() < 0){ docs.setServicedocid(null); }
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(docs);
                }
            }
            //SAVE POSITION
            if (!compService.getCompanyservicepositionses().isEmpty()) {
                Iterator iter = compService.getCompanyservicepositionses().iterator();

                while(iter.hasNext()){
                    Companyservicepositions pos = (Companyservicepositions) iter.next();
                    pos.setCompanyservice(compService);
                    if(pos.getPositionid() < 0){pos.setPositionid(null);}
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(pos);
                }
            }
            //SAVE PROBLEM
            if (!compService.getCompanyservicepsols().isEmpty()) {
                Iterator it = compService.getCompanyservicepsols().iterator();
                while(it.hasNext()){
                    Companyservicepsol psol = (Companyservicepsol)it.next();
                    psol.setCompanyservice(compService);
                    if(psol.getProsolid() < 0){psol.setProsolid(null);}
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(psol);
                }
            }

            servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(compService);
            servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiSave",true);
            servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            
            if(!isEdit){
            	servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setID(compService.getServiceid());
            }
            
            this.dataShow();
            dataShowDoc();
            dataShowProduct();
            dataShowProblemSolution();
            servicePane.ebiModule.ebiContainer.showInActionStatus("Service", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    public void dataCopy(int id){
    
    	try {
    		
    		Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0, id);
         
    		if(y.list().size() > 0){
         		Iterator iter = y.iterate();
         		Companyservice service = (Companyservice) iter.next();
         		
         		servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
         		servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(service);
                servicePane.ebiModule.ebiContainer.showInActionStatus("Service", true);
               
                Companyservice serv = new Companyservice();

                serv.setCreateddate(new Date());
                serv.setCreatedfrom(EBIPGFactory.ebiUser);
                serv.setCompany(service.getCompany());
                serv.setDescription(service.getDescription());
                serv.setServicenr(service.getServicenr());
                serv.setName(service.getName()+" - (Copy)");
                serv.setStatus(service.getStatus());
                serv.setCategory(service.getCategory());
                serv.setType(service.getType());

                //SAVE DOC
                if (!service.getCompanyservicedocses().isEmpty()) {
                    Iterator itd = service.getCompanyservicedocses().iterator();
                    while(itd.hasNext()){
                        Companyservicedocs docs = (Companyservicedocs) itd.next();
                        
                        Companyservicedocs dc = new Companyservicedocs();
                        dc.setCompanyservice(serv);
                        dc.setCreateddate(new Date());
                        dc.setCreatedfrom(EBIPGFactory.ebiUser);
                        dc.setFiles(docs.getFiles());
                        dc.setName(docs.getName());
                        
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(dc);
                    }
                }
                //SAVE POSITION
                if (!service.getCompanyservicepositionses().isEmpty()) {
                    Iterator itp = service.getCompanyservicepositionses().iterator();

                    while(itp.hasNext()){
                        Companyservicepositions pos = (Companyservicepositions) itp.next();
                        
                        Companyservicepositions p = new Companyservicepositions();
                        p.setCompanyservice(serv);
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
                        
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(p);
                    }
                }
                //SAVE PROBLEM
                if (!service.getCompanyservicepsols().isEmpty()) {
                    Iterator it = service.getCompanyservicepsols().iterator();
                    while(it.hasNext()){
                        Companyservicepsol psol = (Companyservicepsol)it.next();
                        
                        Companyservicepsol sv = new Companyservicepsol();
                        sv.setCompanyservice(serv);
                        sv.setCategory(psol.getCategory());
                        sv.setClassification(psol.getClassification());
                        sv.setCreateddate(new Date());
                        sv.setCreatedfrom(EBIPGFactory.ebiUser);
                        sv.setDescription(psol.getDescription());
                        sv.setName(psol.getName());
                        sv.setSolutionnr(psol.getSolutionnr());
                        sv.setStatus(psol.getStatus());
                        sv.setType(psol.getType());
                        
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(sv);
                    }
                }


                servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(serv);
                servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiSave",true);
                servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                
                servicePane.ebiModule.ebiContainer.showInActionStatus("Service", false);
                
                dataShow();
                servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").
				changeSelection(servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").
						convertRowIndexToView(servicePane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(servicePane.tabModService.data,6 , serv.getServiceid())),0,false,false);
            
         	}
         } catch (Exception e) {
             e.printStackTrace();
         }
         
    }

    public void dataMove(int id){
        try{
            Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(servicePane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyservice serv = (Companyservice) iter.next();
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(serv);
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    servicePane.ebiModule.ebiContainer.showInActionStatus("Service", true);

                    serv.setCompany((Company)x.list().get(0));
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(serv);

                    servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiSave",true);
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    servicePane.ebiModule.ebiContainer.showInActionStatus("Service", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void dataEdit(int id) {
       try{
        dataNew(false);

        Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();

           		this.compService = (Companyservice) iter.next();
                servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compService);
            	servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setID(compService.getServiceid());
                servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setCreatedDate(servicePane.ebiModule.ebiPGFactory.getDateToString(compService.getCreateddate() == null ? new Date() : compService.getCreateddate() ));
                servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setCreatedFrom(compService.getCreatedfrom() == null ? EBIPGFactory.ebiUser : compService.getCreatedfrom());

                if (compService.getChangeddate() != null) {
                    servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedDate(servicePane.ebiModule.ebiPGFactory.getDateToString(compService.getChangeddate()));
                    servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedFrom(compService.getChangedfrom());
                } else {
                    servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedDate("");
                    servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedFrom("");
                }

                servicePane.ebiModule.guiRenderer.getTextfield("serviceNameText","Service").setText(compService.getName());
                servicePane.ebiModule.guiRenderer.getTextfield("serviceNrText","Service").setText(compService.getServicenr() == null ? "" : compService.getServicenr());

                if(compService.getStatus() != null){
                    servicePane.ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setSelectedItem(compService.getStatus());
                }

                if(compService.getCategory() != null){
                    servicePane.ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setSelectedItem(compService.getCategory());
                }

                if(compService.getType() != null){
                    servicePane.ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setSelectedItem(compService.getType());
                }
                servicePane.ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").setText(compService.getDescription());
                servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiEdit",true);

                this.dataShowDoc();
                this.dataShowProduct();
                this.dataShowProblemSolution();
                
                servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").
					changeSelection(servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").
							convertRowIndexToView(servicePane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(servicePane.tabModService.data, 6, id)),0,false,false);
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
       }catch (Exception ex){}
    }

    public void dataDelete(int id) {
       try{
        Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
            this.compService = (Companyservice) iter.next();

                try {

                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(this.compService);

                    servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiDelete",true);

                    servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(this.servicePane.ebiModule.ebiPGFactory.company != null){
                    this.servicePane.ebiModule.ebiPGFactory.company.getCompanyservices().remove(this.compService);
                }
                dataNew(true);
                dataShow();
        }
       }catch (Exception ex){}
    }

    public void dataShow() {
       try{
          int srow = servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").getSelectedRow();

          Query y = null;
          if(servicePane.ebiModule.companyID != -1){
                y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where company.companyid=? order by createddate desc ");
                y.setInteger(0, servicePane.ebiModule.companyID);
          }else{
                y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where company.companyid IS NULL order by createddate desc ");
          }

          if(y.list().size() > 0){

                servicePane.tabModService.data = new Object[y.list().size()][8];

                Iterator itr = y.iterate();
                int i = 0;
                while (itr.hasNext()) {

                    Companyservice service = (Companyservice) itr.next();

                    servicePane.tabModService.data[i][0] = service.getServicenr() == null ? "" : service.getServicenr();
                    servicePane.tabModService.data[i][1] = service.getName() == null ? "" : service.getName();
                    servicePane.tabModService.data[i][2] = service.getStatus() == null ? "" : service.getStatus();
                    servicePane.tabModService.data[i][3] = service.getType() == null ? "" : service.getType();
                    servicePane.tabModService.data[i][4] = service.getCategory() == null ? "" : service.getCategory();
                    servicePane.tabModService.data[i][5] = service.getDescription() == null ? "" : service.getDescription();
                    servicePane.tabModService.data[i][6] = service.getServiceid();
                    i++;
                }

          }else{
            servicePane.tabModService.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
          }

          servicePane.tabModService.fireTableDataChanged();
          servicePane.ebiModule.guiRenderer.getTable("companyServiceTable","Service").changeSelection(srow,0,false,false);
       }catch (Exception ex){}

    }

    public void dataShowReport(int id) {
          try{
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("ID", id);

            servicePane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                    useReportSystem(map,
                    servicePane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE")),
                    getserviceNamefromId(id));
          }catch (Exception ex){}
    }


    public String dataShowAndMailReport(int id, boolean showWindow) {
        String fileName="";

        if(servicePane.isEdit){
            if(!dataStore(servicePane.isEdit)){
                return null;
            }
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);


        String to= "";
        try {
            Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0, id);

            Iterator iter = y.iterate();
            if(iter.hasNext()){
                Companyservice offer = (Companyservice) iter.next();
                servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(offer);


                //EMail is send from the Report generator
                fileName = servicePane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                                servicePane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE")),
                                    getserviceNamefromId(id),showWindow,true,to);
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
            this.compService = new Companyservice();
            servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setCreatedDate(servicePane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
            servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setCreatedFrom(EBIPGFactory.ebiUser);
            servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedDate("");
            servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setChangedFrom("");

            servicePane.ebiModule.guiRenderer.getTextfield("serviceNameText","Service").setText("");
            servicePane.ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setSelectedIndex(0);
            servicePane.ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setSelectedIndex(0);
            servicePane.ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setSelectedIndex(0);
            servicePane.ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").setText("");
            servicePane.ebiModule.guiRenderer.getTextfield("serviceNrText","Service").setText("");

            servicePane.ebiModule.ebiPGFactory.getDataStore("Service","ebiNew",true);
            servicePane.ebiModule.guiRenderer.getVisualPanel("Service").setID(-1);

            if(reload){
                dataShow();
                this.dataShowDoc();
                this.dataShowProduct();
                this.dataShowProblemSolution();
            }
          }catch (Exception ex){}
    }

    public void createInvoiceFromService(int id){
       try{
        servicePane.ebiModule.getInvoicePane().dataControlInvoice.dataNew(false);
        Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                                        createQuery("from Companyservice where serviceid=? ").setInteger(0, id);

        if(y.list().size() > 0){
            Iterator iter = y.iterate();

            Companyservice serv = (Companyservice) iter.next();

            if (serv.getServiceid() == id) {

                  //Invoice field
                  servicePane.ebiModule.guiRenderer.getTextfield("invoiceNameText","Invoice").setText(serv.getName());
                  servicePane.ebiModule.guiRenderer.getTextfield("orderText","Invoice").setText(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE")+": "+serv.getServiceid());

                  if(!serv.getCompanyservicepositionses().isEmpty()){
                   Iterator ip = serv.getCompanyservicepositionses().iterator();
                    while(ip.hasNext()){
                      Companyservicepositions pos = (Companyservicepositions)ip.next();
                      Crminvoiceposition inpos = new Crminvoiceposition();

                      inpos.setCrminvoice(servicePane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice());
                      inpos.setPositionid(((servicePane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice().getCrminvoicepositions().size()+1) * (-1)));
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
                      servicePane.ebiModule.getInvoicePane().dataControlInvoice.getInvoice().getCrminvoicepositions().add(inpos);
                      servicePane.ebiModule.getInvoicePane().dataControlInvoice.dataShowProduct();
                    }
                  }
                servicePane.ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(true);
            }
        }
       }catch (Exception ex){}
    }



    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + servicePane.ebiModule.ebiPGFactory.getDateToString(compService.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + compService.getCreatedfrom());

        if (compService.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + servicePane.ebiModule.ebiPGFactory.getDateToString(compService.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + compService.getChangedfrom());
        }

        list.add(EBIPGFactory.getLANG("EBI_LANG_SERVICE_NUMBER") + ": " + (compService.getServicenr().equals(servicePane.ebiModule.guiRenderer.getTextfield("serviceNrText","Service").getText()) == true ? compService.getServicenr() : compService.getServicenr()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (compService.getName().equals(servicePane.ebiModule.guiRenderer.getTextfield("serviceNameText","Service").getText()) == true ? compService.getName() : compService.getName()+"$") );
   
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " + (compService.getStatus().equals(servicePane.ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").getSelectedItem().toString()) == true ? compService.getStatus() : compService.getStatus()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + (compService.getCategory().equals(servicePane.ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").getSelectedItem().toString()) == true ? compService.getCategory() : compService.getCategory()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_TYPE") + ": " + (compService.getType().equals(servicePane.ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").getSelectedItem().toString()) == true ? compService.getType() : compService.getType()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + (compService.getDescription().equals(servicePane.ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").getText()) == true ? compService.getDescription() : compService.getDescription()+"$") );
        list.add("*EOR*"); // END OF RECORD

        if (!compService.getCompanyservicedocses().isEmpty()) {

            Iterator iter = compService.getCompanyservicedocses().iterator();
            while (iter.hasNext()) {
                Companyservicedocs obj = (Companyservicedocs) iter.next();
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + obj.getName());
                list.add(servicePane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + servicePane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()));
                list.add(obj.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + obj.getCreatedfrom());
                list.add("*EOR*");
            }
        }

        if (!compService.getCompanyservicepositionses().isEmpty()) {

            Iterator iter = compService.getCompanyservicepositionses().iterator();

            while (iter.hasNext()) {
                Companyservicepositions obj = (Companyservicepositions) iter.next();
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

        if (!compService.getCompanyservicepsols().isEmpty()) {

            Iterator iter = compService.getCompanyservicepsols().iterator();
            while (iter.hasNext()) {
                Companyservicepsol obj = (Companyservicepsol) iter.next();
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + obj.getName());
                list.add(obj.getClassification() == null ? EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION") + ":" : EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION") + ": " + obj.getClassification());
                list.add(obj.getStatus() == null ? EBIPGFactory.getLANG("EBI_LANG_STATUS") + ":" : EBIPGFactory.getLANG("EBI_LANG_STATUS") + ": " + obj.getStatus());
                list.add(obj.getType() == null ? EBIPGFactory.getLANG("EBI_LANG_TYPE") + ":" : EBIPGFactory.getLANG("EBI_LANG_TYPE") + ": " + obj.getType());
                list.add(obj.getCategory() == null ? EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ":" : EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + obj.getCategory());
                list.add(obj.getDescription() == null ? EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ":" : EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + obj.getDescription());
                list.add("*EOR*");
            }
        }

        try {
            servicePane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Service", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataNewDoc() {
       try{
        JFileChooser ch = new JFileChooser();

        ch.setFileSelectionMode(ch.FILES_ONLY);
        ch.showOpenDialog(servicePane.ebiModule.ebiPGFactory.getMainFrame());

        if (ch.getSelectedFile() != null) {
            File selFile = ch.getSelectedFile();
            byte[] file = readFileToByte(selFile);
            if (file != null) {
                Companyservicedocs docs = new Companyservicedocs();
//			   java.sql.Blob blb = Hibernate.createBlob(file);
                docs.setCompanyservice(compService);
                docs.setName(selFile.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);
                docs.setFiles(file);
                this.compService.getCompanyservicedocses().add(docs);
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

            Iterator iter = this.compService.getCompanyservicedocses().iterator();
            while (iter.hasNext()) {

                Companyservicedocs doc = (Companyservicedocs) iter.next();

                if (id == doc.getServicedocid()) {
                    // Get the BLOB inputstream

                    String file = doc.getName().replaceAll(" ", "_");

//					byte buffer[] = doc.getFiles().getBytes(1,(int)adress.getFiles().length());
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

    public void dataShowDoc() {
         try{
          if(this.compService.getCompanyservicedocses().size() > 0){
                servicePane.tabModDoc.data = new Object[this.compService.getCompanyservicedocses().size()][4];

                Iterator itr = this.compService.getCompanyservicedocses().iterator();
                int i = 0;
                while (itr.hasNext()) {

                    Companyservicedocs obj = (Companyservicedocs) itr.next();

                    servicePane.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                    servicePane.tabModDoc.data[i][1] = servicePane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : servicePane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                    servicePane.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                    if(obj.getServicedocid() == null || obj.getServicedocid() < 0){ obj.setServicedocid(((i + 1) *(-1)));}
                    servicePane.tabModDoc.data[i][3] = obj.getServicedocid();
                    i++;
                }
          }else{
             servicePane.tabModDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
          }
           servicePane.tabModDoc.fireTableDataChanged();
         }catch (Exception ex){}
    }

    private void resolverType(String fileName, String type) {
      try{
        type = type.toLowerCase();
        if (".jpg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
            EBIImageViewer view = new EBIImageViewer(servicePane.ebiModule.ebiPGFactory.getMainFrame(),new javax.swing.ImageIcon(fileName));
            view.setVisible(true);
        } else if (".pdf".equals(type)) {
            servicePane.ebiModule.ebiPGFactory.openPDFReportFile(fileName);
        } else if (".doc".equals(type)) {
            servicePane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        } else {
            servicePane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
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

    public void dataShowProduct() {
      try{
           if(this.compService.getCompanyservicepositionses().size() > 0){
                servicePane.tabModProduct.data = new Object[this.compService.getCompanyservicepositionses().size()][9];

                Iterator itr = this.compService.getCompanyservicepositionses().iterator();
                int i = 0;
                NumberFormat currency=NumberFormat.getCurrencyInstance();

                while (itr.hasNext()) {

                    Companyservicepositions obj = (Companyservicepositions) itr.next();

                    servicePane.tabModProduct.data[i][0] = String.valueOf(obj.getQuantity());
                    servicePane.tabModProduct.data[i][1] = obj.getProductnr();
                    servicePane.tabModProduct.data[i][2] = obj.getProductname() == null ? "" : obj.getProductname();
                    servicePane.tabModProduct.data[i][3] = obj.getCategory() == null ? "" : obj.getCategory();
                    servicePane.tabModProduct.data[i][4] = obj.getTaxtype() == null ? "" : obj.getTaxtype();
                    servicePane.tabModProduct.data[i][5] = currency.format(servicePane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction()))) == null ? "" : currency.format(servicePane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
                    servicePane.tabModProduct.data[i][6] = obj.getDeduction().equals("") ? "" : obj.getDeduction()+"%";
                    servicePane.tabModProduct.data[i][7] = obj.getDescription() == null ? "" : obj.getDescription();
                    if(obj.getPositionid() == null || obj.getPositionid() < 0){ obj.setPositionid(((i + 1) *(-1)));}
                    servicePane.tabModProduct.data[i][8] = obj.getPositionid();
                    i++;
                }
           }else{
             servicePane.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};
           }
           servicePane.tabModProduct.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataShowProblemSolution() {
     try{
      if(this.compService.getCompanyservicepsols().size() >0){
        servicePane.tabModProsol.data = new Object[this.compService.getCompanyservicepsols().size()][8];

        Iterator itr = this.compService.getCompanyservicepsols().iterator();
        int i = 0;
        while (itr.hasNext()) {

            Companyservicepsol obj = (Companyservicepsol) itr.next();

            servicePane.tabModProsol.data[i][0] = obj.getSolutionnr() == null ? "" : obj.getSolutionnr();
            servicePane.tabModProsol.data[i][1] = obj.getName() == null ? "" : obj.getName();
            servicePane.tabModProsol.data[i][2] = obj.getClassification() == null ? "" : obj.getClassification();
            servicePane.tabModProsol.data[i][3] = obj.getCategory() == null ? "" : obj.getCategory();
            servicePane.tabModProsol.data[i][4] = obj.getType() == null ? "" : obj.getType();
            servicePane.tabModProsol.data[i][5] = obj.getStatus() == null ? "" : obj.getStatus();
            servicePane.tabModProsol.data[i][6] = obj.getDescription() == null ? "" : obj.getDescription();
            if(obj.getProsolid() == null || obj.getProsolid() < 0){ obj.setProsolid(((i + 1) * (-1)));}
            servicePane.tabModProsol.data[i][7] = obj.getProsolid();
            i++;
        }
      }else{
        servicePane.tabModProsol.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "",""}};
      }
       servicePane.tabModProsol.fireTableDataChanged();
     }catch (Exception ex){}
    }

    public void dataDeleteDoc(int id) {
       try{
        Iterator iter = this.compService.getCompanyservicedocses().iterator();
        while (iter.hasNext()) {

            Companyservicedocs doc = (Companyservicedocs) iter.next();

            if (doc.getServicedocid() == id) {
                  if(id >=0){
                    try {
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.compService.getCompanyservicedocses().remove(doc);
                this.dataShowDoc();
                break;
            }
        }
       }catch (Exception ex){}
    }

    public void dataDeleteProblemSolution(int id) {
      try{
        Iterator iter = this.compService.getCompanyservicepsols().iterator();
        while (iter.hasNext()) {

            Companyservicepsol servicerec = (Companyservicepsol) iter.next();

            if (servicerec.getProsolid() == id) {
                  if(id>=0){
                    try {
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(servicerec);
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.compService.getCompanyservicepsols().remove(servicerec);
                this.dataShowProblemSolution();
                break;
            }
        }
      }catch (Exception ex){}
    }

    public void dataDeleteProduct(int id) {
      try{
        Iterator iter = this.compService.getCompanyservicepositionses().iterator();
        while (iter.hasNext()) {

            Companyservicepositions servicepro = (Companyservicepositions) iter.next();

            if (servicepro.getPositionid() == id) {
                  if(id >=0){
                    try {
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(servicepro);
                        servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.compService.getCompanyservicepositionses().remove(servicepro);
                this.dataShowProduct();
                break;
            }
        }
      }catch (Exception ex){}
    }

    public Companyservice getcompService() {
        return compService;
    }

    public void setcompService(Companyservice compService) {
        this.compService = compService;
    }

    public Set<Companyservicedocs> getserviceDocList() {
        return compService.getCompanyservicedocses();
    }

    public Set<Companyservicepositions> getservicePosList() {
        return compService.getCompanyservicepositionses();
    }

    public Set<Companyservicepsol> getserviceProSolList() {
        return compService.getCompanyservicepsols();
    }

    public Set<Companyservice> getServiceList() {
        return servicePane.ebiModule.ebiPGFactory.company.getCompanyservices();
    }

    private String getserviceNamefromId(int id) {

        String name = "";
       try{
        Query y = servicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyservice where serviceid=? ").setInteger(0, id);

        if(y.list().size() > 0){
            name = ((Companyservice)y.list().get(0)).getName();
        }
       }catch (Exception ex){}

        return name;
    }
}