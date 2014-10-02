package ebiCRM.data.control;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMInvoice;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyorder;
import ebiNeutrinoSDK.model.hibernate.Companyproducttax;
import ebiNeutrinoSDK.model.hibernate.Companyservice;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class EBIDataControlInvoice {

    public Crminvoice invoice = null;
    private EBICRMInvoice invoicePane = null;
    public int id = -1;
    public Timestamp lockTime = null;
    public String lockUser ="";
    public String lockModuleName = "";
    public int lockStatus=0;
    public int lockId =-1;
    public final EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

    public EBIDataControlInvoice(EBICRMInvoice invoicePane) {
        this.invoicePane = invoicePane;
        invoice = new Crminvoice();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            invoicePane.ebiModule.ebiContainer.showInActionStatus("Invoice", true);
            if(id != -1){
                if(checkIslocked(id,true)){
                 return false;
                }
            }

            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            if (isEdit == false) {
                invoice.setCreateddate(new Date());
            } else {
                invoice.setChangeddate(new Date());
                invoice.setChangedfrom(EBIPGFactory.ebiUser);
            }

            // Invoice main data
            invoice.setCreatedfrom(invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").getCreatedFrom());
            invoice.setInvoicenr(invoicePane.invoiceNr);
            invoice.setBeginchar(invoicePane.beginChar);
            invoice.setName(invoicePane.ebiModule.guiRenderer.getTextfield("invoiceNameText","Invoice").getText());

            if(invoicePane.ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").getSelectedItem() != null){
                invoice.setStatus(invoicePane.ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").getSelectedItem().toString());
            }

            if(!isEdit){
                if(invoicePane.ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem() != null){
                    invoice.setCategory(invoicePane.ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem().toString());
                }
                invoicePane.isEdit = true;
            }

            if(invoicePane.ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").getDate() != null){
                invoice.setDate(invoicePane.ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").getDate());
            }else {
               invoice.setDate(new Date()); 
            }
            
            if(!"".equals(invoicePane.ebiModule.guiRenderer.getTextfield("orderText","Invoice").getText())){
                invoice.setAssosiation(invoicePane.ebiModule.guiRenderer.getTextfield("orderText","Invoice").getText());
            }
            // Invoice rec
            if(invoicePane.ebiModule.guiRenderer.getComboBox("genderText","Invoice").getSelectedItem() != null){
            	invoice.setGender(invoicePane.ebiModule.guiRenderer.getComboBox("genderText","Invoice").getSelectedItem().toString());
            }
            invoice.setPosition(invoicePane.ebiModule.guiRenderer.getTextfield("titleText","Invoice").getText());
            invoice.setCompanyname(invoicePane.ebiModule.guiRenderer.getTextfield("companyNameText","Invoice").getText());
            invoice.setContactname(invoicePane.ebiModule.guiRenderer.getTextfield("nameText","Invoice").getText());
            invoice.setContactsurname(invoicePane.ebiModule.guiRenderer.getTextfield("surnameText","Invoice").getText());
            invoice.setContactstreet(invoicePane.ebiModule.guiRenderer.getTextfield("streetNrText","Invoice").getText());
            invoice.setContactzip(invoicePane.ebiModule.guiRenderer.getTextfield("zipText","Invoice").getText());
            invoice.setContactlocation(invoicePane.ebiModule.guiRenderer.getTextfield("locationText","Invoice").getText());
            invoice.setContactpostcode(invoicePane.ebiModule.guiRenderer.getTextfield("postCodeText","Invoice").getText());
            invoice.setContactcountry(invoicePane.ebiModule.guiRenderer.getTextfield("countryText","Invoice").getText());
            invoice.setContacttelephone(invoicePane.ebiModule.guiRenderer.getTextfield("telefonText","Invoice").getText());
            invoice.setContactfax(invoicePane.ebiModule.guiRenderer.getTextfield("faxText","Invoice").getText());
            invoice.setContactemail(invoicePane.ebiModule.guiRenderer.getTextfield("emailText","Invoice").getText());
            invoice.setContactweb(invoicePane.ebiModule.guiRenderer.getTextfield("internetText","Invoice").getText());
            invoice.setContactdescription(invoicePane.ebiModule.guiRenderer.getTextarea("recDescription","Invoice").getText());

            // Position save
            if (!invoice.getCrminvoicepositions().isEmpty()) {
                Iterator iter = invoice.getCrminvoicepositions().iterator();
                while(iter.hasNext()){
                   Crminvoiceposition pos = (Crminvoiceposition)iter.next();
                   pos.setCrminvoice(invoice);
                   if(pos.getPositionid() < 0){ pos.setPositionid(null);}
                   invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").saveOrUpdate(pos);
                }
            }

            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").saveOrUpdate(invoice);

            invoicePane.ebiModule.ebiPGFactory.getDataStore("Invoice","ebiSave",true);
            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();

            this.dataShow(-1);
            dataShowProduct();
            
            if(!isEdit){
            	invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setID(invoice.getInvoiceid());
            }
            
            invoicePane.ebiModule.ebiContainer.showInActionStatus("Invoice", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void dataEdit(int id) {
        dataNew(false);

        Query query;
        try {
            if(!invoicePane.isEdit){
            	invoicePane.isEdit = true;
            }
            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            
            query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

            if (iter.hasNext()) {
                this.id = id;

                invoice = (Crminvoice) iter.next();
                invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setID(invoice.getInvoiceid());
                invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(invoice);

                invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setCreatedDate(invoicePane.ebiModule.ebiPGFactory.getDateToString(invoice.getCreateddate() == null ? new Date() : invoice.getCreateddate()  ));
                invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setCreatedFrom(invoice.getCreatedfrom() == null ? EBIPGFactory.ebiUser : invoice.getCreatedfrom());

                if (invoice.getChangeddate() != null) {
                    invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedDate(invoicePane.ebiModule.ebiPGFactory.getDateToString(invoice.getChangeddate()));
                    invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedFrom(invoice.getChangedfrom());
                } else {
                    invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedDate("");
                    invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedFrom("");
                }

                 invoicePane.invoiceNr = invoice.getInvoicenr() == null ? 0 :  invoice.getInvoicenr();
                 invoicePane.beginChar = invoice.getBeginchar() == null ? "" : invoice.getBeginchar();
                 invoicePane.ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").setText(invoice.getBeginchar()+invoice.getInvoicenr());
                 invoicePane.ebiModule.guiRenderer.getTextfield("invoiceNameText","Invoice").setText(invoice.getName());

                 if(invoice.getStatus() != null){
                    invoicePane.ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setSelectedItem(invoice.getStatus());
                 }

                 if(invoice.getCategory() != null){
                    invoicePane.ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setSelectedItem(invoice.getCategory());
                 }

                 if(invoice.getDate() != null){
                    invoicePane.ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").setDate(invoice.getDate());
                    invoicePane.ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").getEditor().setText(invoicePane.ebiModule.ebiPGFactory.getDateToString(invoice.getDate()));
                 }
                 if(invoice.getAssosiation() != null && !"".equals(invoice.getAssosiation().toString())){
                    invoicePane.ebiModule.guiRenderer.getTextfield("orderText","Invoice").setText(invoice.getAssosiation());
                    invoicePane.ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(true); 
                 }
                 // Invoice rec

                 if(invoice.getGender() != null){
                    invoicePane.ebiModule.guiRenderer.getComboBox("genderText","Invoice").setSelectedItem(invoice.getGender());
                 }

                 invoicePane.ebiModule.guiRenderer.getTextfield("titleText","Invoice").setText(invoice.getPosition());
                 invoicePane.ebiModule.guiRenderer.getTextfield("companyNameText","Invoice").setText(invoice.getCompanyname());
                 invoicePane.ebiModule.guiRenderer.getTextfield("nameText","Invoice").setText(invoice.getContactname());
                 invoicePane.ebiModule.guiRenderer.getTextfield("surnameText","Invoice").setText(invoice.getContactsurname());
                 invoicePane.ebiModule.guiRenderer.getTextfield("streetNrText","Invoice").setText(invoice.getContactstreet());
                 invoicePane.ebiModule.guiRenderer.getTextfield("zipText","Invoice").setText(invoice.getContactzip());
                 invoicePane.ebiModule.guiRenderer.getTextfield("locationText","Invoice").setText(invoice.getContactlocation());
                 invoicePane.ebiModule.guiRenderer.getTextfield("postCodeText","Invoice").setText(invoice.getContactpostcode());
                 invoicePane.ebiModule.guiRenderer.getTextfield("countryText","Invoice").setText(invoice.getContactcountry());
                 invoicePane.ebiModule.guiRenderer.getTextfield("telefonText","Invoice").setText(invoice.getContacttelephone());
                 invoicePane.ebiModule.guiRenderer.getTextfield("faxText","Invoice").setText(invoice.getContactfax());
                 invoicePane.ebiModule.guiRenderer.getTextfield("emailText","Invoice").setText(invoice.getContactemail());
                 invoicePane.ebiModule.guiRenderer.getTextfield("internetText","Invoice").setText(invoice.getContactweb());
                 invoicePane.ebiModule.guiRenderer.getTextarea("recDescription","Invoice").setText(invoice.getContactdescription());

                 invoicePane.ebiModule.ebiPGFactory.getDataStore("Invoice","ebiEdit",true);

                 this.dataShowProduct();
                 calculateTotalAmount();
                 checkIslocked(id,false);
                 
                 invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").
 					changeSelection(invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").
 							convertRowIndexToView(invoicePane.ebiModule.dynMethod.
 									getIdIndexFormArrayInATable(((EBIAbstractTableModel) invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getModel()).data, 9, id)),0,false,false);
                 
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
          invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDelete(int id) {

        Query query;
        try {

            if(checkIslocked(id,true)){
                 return;
            }

            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

                if (iter.hasNext()) {

                    Crminvoice inv = (Crminvoice) iter.next();

                    if (inv.getInvoiceid() == id) {
                        try {
                            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").delete(inv);

                            invoicePane.ebiModule.ebiPGFactory.getDataStore("Invoice","ebiDelete",true);
                            invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
                        } catch (HibernateException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
        }catch(Exception ex){
           ex.printStackTrace();  
        }
        dataNew(true);
        dataShow(-1);
    }

    public void dataShow(int showID) {
      try{
        int srow = invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getSelectedRow();
        EBIAbstractTableModel model = (EBIAbstractTableModel) invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getModel();

        String sName = "";
        if(showID != -1){
            if(srow > -1 && model.data.length >= srow){
                sName = model.data[invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(srow)][0].toString();
            }
        }

        Query query;
        try {
            
            if(invoicePane.ebiModule.ebiPGFactory.systemStartCal != null &&  invoicePane.ebiModule.ebiPGFactory.systemEndCal != null){

                query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice cm where cm.date between ? and ? order by createddate desc");
                query.setTimestamp(0,invoicePane.ebiModule.ebiPGFactory.systemStartCal.getTime());
                query.setTimestamp(1, invoicePane.ebiModule.ebiPGFactory.systemEndCal.getTime());

            }else{
                query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice order by createddate desc");
            }

            if(query.list().size() > 0){

                    Iterator iter =  query.iterate();

                    model.data = new Object[query.list().size()][10];

                    int i = 0;
                    while (iter.hasNext()) {

                        Crminvoice inv = (Crminvoice) iter.next();
                        if((""+inv.getInvoicenr()).equals(sName) && !"".equals(sName)){
                            srow = invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(i);
                        }else if(inv.getInvoiceid() == showID){
                            srow = invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").convertRowIndexToView(i);
                        }

                        model.data[i][0] = inv.getBeginchar() +inv.getInvoicenr();
                        model.data[i][1] = inv.getName() == null ? "" : inv.getName();
                        model.data[i][2] = inv.getStatus() == null ? "" : inv.getStatus();
                        model.data[i][3] = inv.getCategory() == null ? "" : inv.getCategory();
                        model.data[i][4] = inv.getGender() == null ? "" : inv.getGender();
                        model.data[i][5] = inv.getCompanyname() == null ? "" : inv.getCompanyname();
                        model.data[i][6] = inv.getContactname() == null ? "" : inv.getContactname();
                        model.data[i][7] = inv.getContactsurname() == null ? "" : inv.getContactsurname();
                        model.data[i][8] = inv.getDate() == null ? "" : invoicePane.ebiModule.ebiPGFactory.getDateToString(inv.getDate());
                        model.data[i][9] = inv.getInvoiceid();
                        i++;
                    }   
              }else{
                model.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "",""}};
              }
            model.fireTableDataChanged();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        invoicePane.ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").changeSelection(srow,0,false,false);
      }catch (Exception ex){}
    }

    
    
    public Hashtable<String,Double> getTaxName(int id){
        Hashtable<String,Double> taxTable = null;
    	try{
   	 	Query y = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice where invoiceid=? ").setInteger(0, id);
        
   	 	NumberFormat cashFormat=NumberFormat.getCurrencyInstance();
        cashFormat.setMinimumFractionDigits(2);
        cashFormat.setMaximumFractionDigits(3);
   	    taxTable = new Hashtable<String,Double>();
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
        
        	    Crminvoice or = (Crminvoice) iter.next();
   	
        		Iterator itx = or.getCrminvoicepositions().iterator();
        		while(itx.hasNext()){
        			Crminvoiceposition pos = (Crminvoiceposition)itx.next();
                    if(pos.getTaxtype() != null){
                        if(taxTable.containsKey(pos.getTaxtype())){

                            taxTable.put(pos.getTaxtype(),taxTable.get(pos.getTaxtype())+(((pos.getNetamount() * pos.getQuantity()) * invoicePane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }else{
                            taxTable.put(pos.getTaxtype(),(((pos.getNetamount() * pos.getQuantity()) * invoicePane.ebiModule.dynMethod.getTaxVal(pos.getTaxtype())) / 100));

                        }
                    }
        		}	
        }
        }catch (Exception ex){}
   	return taxTable;
   }
    
   public void dataShowReport(int id) {
      try{
        if(invoicePane.isEdit){
            if(!dataStore(invoicePane.isEdit)){
               return;
            }
        }

        final Map<String, Object> map = new HashMap<String, Object>();

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

        invoicePane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                invoicePane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES")),
                getInvoiceNamefromId(id));
      }catch (Exception ex){}
    }

    public String dataShowAndMailReport(int id, boolean showWindow) {

           String fileName="";
        try{
            if(invoicePane.isEdit){
                if(!dataStore(invoicePane.isEdit)){
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
                Query query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Crminvoice where invoiceId=? ").setInteger(0, id);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Crminvoice inv = (Crminvoice)iter.next();
                    invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(inv);

                    if(!"".equals(inv.getContactemail())){
                         fileName = invoicePane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map,
                                         invoicePane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES")),
                                                getInvoiceNamefromId(id),showWindow,true, inv.getContactemail());
                    }else{
                        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_NO_RECEIVER_WAS_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }
           

        }catch (Exception ex){}
           return fileName;
    }

    public void dataNew(boolean reload) {
       try{
            // Remove lock
            lockId = -1;
            lockModuleName = "";
            lockUser = "";
            lockStatus = 0;
            lockTime =  null;
            id=-1;
            invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").setID(-1);

            invoice = new Crminvoice();
            invoicePane.initialize();
            invoicePane.ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(false);
            invoicePane.ebiModule.ebiPGFactory.getDataStore("Invoice","ebiNew",true);
            if(reload){
                this.dataShowProduct();
            }
       }catch (Exception ex){}
    }

    public void dataShowProduct() {
      try{
          if(this.invoice.getCrminvoicepositions().size() > 0){
                invoicePane.tabModProduct.data = new Object[this.invoice.getCrminvoicepositions().size()][9];

                Iterator itr = invoice.getCrminvoicepositions().iterator();
                int i = 0;

                NumberFormat currency=NumberFormat.getCurrencyInstance();

                while (itr.hasNext()) {

                    Crminvoiceposition obj = (Crminvoiceposition) itr.next();

                    invoicePane.tabModProduct.data[i][0] = String.valueOf(obj.getQuantity());
                    invoicePane.tabModProduct.data[i][1] = obj.getProductnr();
                    invoicePane.tabModProduct.data[i][2] = obj.getProductname() == null ? "" : obj.getProductname();
                    invoicePane.tabModProduct.data[i][3] = obj.getCategory() == null ? "" : obj.getCategory();
                    invoicePane.tabModProduct.data[i][4] = obj.getTaxtype() == null ? "" : obj.getTaxtype();
                    invoicePane.tabModProduct.data[i][5] = currency.format(obj.getNetamount() == null ? "" : invoicePane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
                    invoicePane.tabModProduct.data[i][6] = obj.getDeduction().equals("")  ? "" : obj.getDeduction()+"%";
                    invoicePane.tabModProduct.data[i][7] = obj.getDescription() == null ? "" : obj.getDescription();
                    if(obj.getPositionid() == null || obj.getPositionid() < 0){ obj.setPositionid(((i + 1)*(-1)));}
                    invoicePane.tabModProduct.data[i][8] = obj.getPositionid();
                    i++;
                }
            }else{
                invoicePane.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", ""}};
            }
            invoicePane.tabModProduct.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataDeleteProduct(int id) {

        try {
            if(checkIslocked(id,true)){
                 return;
            }

            Iterator iter = invoice.getCrminvoicepositions().iterator();
            while (iter.hasNext()) {

                Crminvoiceposition invpro = (Crminvoiceposition) iter.next();

                if (invpro.getPositionid() == id) {
                  if(id >= 0){
                    try {
                        invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                        invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").delete(invpro);
                        invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                    invoice.getCrminvoicepositions().remove(invpro);
                    this.dataShowProduct();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Crminvoice getInvoice() {
        return invoice;
    }

    private String getInvoiceNamefromId(int id) {

        String name = "";
        Query query;
        
        try {
                invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Crminvoice where invoiceId=? ").setInteger(0, id);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Crminvoice invoice = (Crminvoice) iter.next();
                    invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(invoice);
                    name = invoice.getName();

                }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return name;
    }

    public boolean loadCompanyOrder(int orderId){
        Query query;
        boolean ret = false;
        try {
                invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Companyorder where orderid=? ").setInteger(0, orderId);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Companyorder order = (Companyorder) iter.next();
                    invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(order);
                    ret = true;
                }
           invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit(); 
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return ret;
    }

    public boolean loadCompanyService(int serviceId){
       Query query;
       boolean ret = false;
        try {
                invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Companyservice where serviceid=? ").setInteger(0, serviceId);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Companyservice service = (Companyservice) iter.next();
                    invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(service);

                    ret = true;
                }
           invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return ret;
    }

    public void calculateTotalAmount(){

        double amount=0.0;
        double deduction=0.0;
        double tax=0.0;

       try{
            Iterator iter = invoice.getCrminvoicepositions().iterator();
            while(iter.hasNext()){
                Crminvoiceposition inv = (Crminvoiceposition)iter.next();
                amount += inv.getNetamount() * inv.getQuantity();

                if(!"".equals(inv.getDeduction())){
                    deduction += (((inv.getNetamount() * inv.getQuantity()) * Integer.parseInt(inv.getDeduction())) / 100);
                }

                double vtax = getTaxVal(inv.getTaxtype());
                if(vtax != 0.0){
                    double toNet = (inv.getNetamount() * inv.getQuantity());
                    double todeduct =  inv.getDeduction().equals("") ? 0.0 : ((toNet * Integer.parseInt(inv.getDeduction())) / 100);
                    tax  +=   (((toNet - todeduct)* vtax) / 100);

                }
            }

            amount = amount - deduction;
            deduction = deduction * (-1);

            invoicePane.ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setValue(deduction);
            invoicePane.ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setValue(amount);
            invoicePane.ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setValue(tax);
            invoicePane.ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setValue(amount+tax);
       }catch (Exception ex){}

    }

    private double getTaxVal(String cat){
        double val = 0.0;
         Query query;
        try {
            query = invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").
                    createQuery("from Companyproducttax where name=? ").setString(0,
                    cat);

                    Iterator it = query.iterate();

                    if (it.hasNext()) {
                      Companyproducttax tax = (Companyproducttax) it.next();
                      invoicePane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(tax);  
                      val = tax.getTaxvalue();
                    }
        }catch(Exception ex){
             ex.printStackTrace();
        }
      return val;
    }

    /**
     * Check if a loaded record is locked
     * @param compNr
     * @param showMessage
     * @throws Exception
     */

    public boolean checkIslocked(int compNr, boolean showMessage) throws Exception{
            boolean ret = false;
           try{
            PreparedStatement ps =  invoicePane.ebiModule.ebiPGFactory.database.initPreparedStatement("SELECT * FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=?  ");
            ps.setInt(1,compNr);
            ps.setString(2,"CRMInvoice");
            ResultSet rs = invoicePane.ebiModule.ebiPGFactory.database.executePreparedQuery(ps);

            rs.last();

            if (rs.getRow() <= 0) {
                lockId = compNr;
                lockModuleName = "CRMInvoice";
                lockUser = EBIPGFactory.ebiUser;
                lockStatus = 1;
                lockTime =  new Timestamp(new Date().getTime());
                activateLockedInfo(false);
            }else{
                rs.beforeFirst();
                rs.next();
                lockId = rs.getInt("RECORDID");
                lockModuleName = rs.getString("MODULENAME");
                lockUser = rs.getString("USER");
                lockStatus = rs.getInt("STATUS");
                lockTime =  rs.getTimestamp("LOCKDATE");

                if(!lockUser.equals(EBIPGFactory.ebiUser)){
                    activateLockedInfo(true);
                }

                if(showMessage && !lockUser.equals(EBIPGFactory.ebiUser)){
                    ret = true;
                }
            }

             // Pessimistic Dialog view info
             invoicePane.ebiModule.guiRenderer.getLabel("userx","pessimisticViewDialog").setText(lockUser);
             invoicePane.ebiModule.guiRenderer.getLabel("statusx","pessimisticViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_LOCKED"));
             invoicePane.ebiModule.guiRenderer.getLabel("timex","pessimisticViewDialog").setText(lockTime.toString());
           }catch (Exception ex){}
        return ret;
    }

    /**
     * Activate Pessimistic Lock for the GUI
     * @param enabled
     */

    public void activateLockedInfo(boolean enabled){
       try{
        //show red icon to a visual panel
        invoicePane.ebiModule.guiRenderer.getVisualPanel("Invoice").showLockIcon(enabled);

        //hide the delete buttons from crm panel
        invoicePane.ebiModule.guiRenderer.getButton("saveInvoice","Invoice").setEnabled(enabled ? false : true);
        invoicePane.ebiModule.guiRenderer.getButton("newPosition","Invoice").setVisible(enabled ? false : true);
        invoicePane.ebiModule.guiRenderer.getButton("deletePosition","Invoice").setVisible(enabled ? false : true);

        invoicePane.ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setVisible(enabled ? false : true);
       }catch (Exception ex){}
    }

}