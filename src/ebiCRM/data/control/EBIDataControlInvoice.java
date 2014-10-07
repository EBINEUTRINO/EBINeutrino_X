package ebiCRM.data.control;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
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

import javax.swing.*;

public class EBIDataControlInvoice {

    public Crminvoice invoice = null;
    private EBICRMInvoice invoicePane = null;
    public int id = -1;
    public final EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

    public EBIDataControlInvoice(EBICRMInvoice invoicePane) {
        this.invoicePane = invoicePane;
        invoice = new Crminvoice();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            invoicePane.mod.ebiContainer.showInActionStatus("Invoice", true);
            invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            if (isEdit == false){
                invoice.setCreateddate(new Date());
            } else {
                invoice.setChangeddate(new Date());
                invoice.setChangedfrom(EBIPGFactory.ebiUser);
            }

            // Invoice main data
            invoice.setCreatedfrom(invoicePane.mod.gui.getVisualPanel("Invoice").getCreatedFrom());
            invoice.setInvoicenr(invoicePane.invoiceNr);
            invoice.setBeginchar(invoicePane.beginChar);
            invoice.setName(invoicePane.mod.gui.getTextfield("invoiceNameText","Invoice").getText());

            if(invoicePane.mod.gui.getComboBox("invoiceStatusText","Invoice").getSelectedItem() != null){
                invoice.setStatus(invoicePane.mod.gui.getComboBox("invoiceStatusText","Invoice").getSelectedItem().toString());
            }

            if(!isEdit){
                if(invoicePane.mod.gui.getComboBox("categoryText","Invoice").getSelectedItem() != null){
                    invoice.setCategory(invoicePane.mod.gui.getComboBox("categoryText","Invoice").getSelectedItem().toString());
                }
                invoicePane.isEdit = true;
            }

            if(invoicePane.mod.gui.getTimepicker("invoiceDateText","Invoice").getDate() != null){
                invoice.setDate(invoicePane.mod.gui.getTimepicker("invoiceDateText","Invoice").getDate());
            }else {
               invoice.setDate(new Date()); 
            }

            // Invoice rec
            if(invoicePane.mod.gui.getComboBox("genderText","Invoice").getSelectedItem() != null){
            	invoice.setGender(invoicePane.mod.gui.getComboBox("genderText","Invoice").getSelectedItem().toString());
            }
            invoice.setPosition(invoicePane.mod.gui.getTextfield("titleText","Invoice").getText());
            invoice.setCompanyname(invoicePane.mod.gui.getTextfield("companyNameText","Invoice").getText());
            invoice.setContactname(invoicePane.mod.gui.getTextfield("nameText","Invoice").getText());
            invoice.setContactsurname(invoicePane.mod.gui.getTextfield("surnameText","Invoice").getText());
            invoice.setContactstreet(invoicePane.mod.gui.getTextfield("streetNrText","Invoice").getText());
            invoice.setContactzip(invoicePane.mod.gui.getTextfield("zipText","Invoice").getText());
            invoice.setContactlocation(invoicePane.mod.gui.getTextfield("locationText","Invoice").getText());
            invoice.setContactpostcode(invoicePane.mod.gui.getTextfield("postCodeText","Invoice").getText());
            invoice.setContactcountry(invoicePane.mod.gui.getTextfield("countryText","Invoice").getText());
            invoice.setContacttelephone(invoicePane.mod.gui.getTextfield("telefonText","Invoice").getText());
            invoice.setContactfax(invoicePane.mod.gui.getTextfield("faxText","Invoice").getText());
            invoice.setContactemail(invoicePane.mod.gui.getTextfield("emailText","Invoice").getText());
            invoice.setContactweb(invoicePane.mod.gui.getTextfield("internetText","Invoice").getText());
            invoice.setContactdescription(invoicePane.mod.gui.getTextarea("recDescription","Invoice").getText());

            // Position save
            if (!invoice.getCrminvoicepositions().isEmpty()){
                Iterator iter = invoice.getCrminvoicepositions().iterator();
                while(iter.hasNext()){
                   Crminvoiceposition pos = (Crminvoiceposition)iter.next();
                   pos.setCrminvoice(invoice);
                   if(pos.getPositionid() < 0){ pos.setPositionid(null);}
                   invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").saveOrUpdate(pos);
                }
            }

            invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").saveOrUpdate(invoice);

            invoicePane.mod.system.getDataStore("Invoice","ebiSave",true);
            invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();

            this.dataShow();
            dataShowProduct();
            
            if(!isEdit){
            	invoicePane.mod.gui.getVisualPanel("Invoice").setID(invoice.getInvoiceid());
            }
            
            invoicePane.mod.ebiContainer.showInActionStatus("Invoice", false);

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
            invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            
            query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

            if (iter.hasNext()) {
                this.id = id;

                invoice = (Crminvoice) iter.next();
                invoicePane.mod.gui.getVisualPanel("Invoice").setID(invoice.getInvoiceid());
                invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(invoice);

                invoicePane.mod.gui.getVisualPanel("Invoice").setCreatedDate(invoicePane.mod.system.getDateToString(invoice.getCreateddate() == null ? new Date() : invoice.getCreateddate()  ));
                invoicePane.mod.gui.getVisualPanel("Invoice").setCreatedFrom(invoice.getCreatedfrom() == null ? EBIPGFactory.ebiUser : invoice.getCreatedfrom());

                if (invoice.getChangeddate() != null) {
                    invoicePane.mod.gui.getVisualPanel("Invoice").setChangedDate(invoicePane.mod.system.getDateToString(invoice.getChangeddate()));
                    invoicePane.mod.gui.getVisualPanel("Invoice").setChangedFrom(invoice.getChangedfrom());
                } else {
                    invoicePane.mod.gui.getVisualPanel("Invoice").setChangedDate("");
                    invoicePane.mod.gui.getVisualPanel("Invoice").setChangedFrom("");
                }

                 invoicePane.invoiceNr = invoice.getInvoicenr() == null ? 0 :  invoice.getInvoicenr();
                 invoicePane.beginChar = invoice.getBeginchar() == null ? "" : invoice.getBeginchar();
                 invoicePane.mod.gui.getTextfield("invoiceNrText","Invoice").setText(invoice.getBeginchar()+invoice.getInvoicenr());
                 invoicePane.mod.gui.getTextfield("invoiceNameText","Invoice").setText(invoice.getName());

                 if(invoice.getStatus() != null){
                    invoicePane.mod.gui.getComboBox("invoiceStatusText","Invoice").setSelectedItem(invoice.getStatus());
                 }

                 if(invoice.getCategory() != null){
                    invoicePane.mod.gui.getComboBox("categoryText","Invoice").setSelectedItem(invoice.getCategory());
                 }

                 if(invoice.getDate() != null){
                    invoicePane.mod.gui.getTimepicker("invoiceDateText","Invoice").setDate(invoice.getDate());
                    invoicePane.mod.gui.getTimepicker("invoiceDateText","Invoice").getEditor().setText(invoicePane.mod.system.getDateToString(invoice.getDate()));
                 }
                 if(invoice.getAssosiation() != null && !"".equals(invoice.getAssosiation().toString())){
                    invoicePane.mod.gui.getTextfield("orderText","Invoice").setText(invoice.getAssosiation());
                    invoicePane.mod.gui.getButton("selectOrder","Invoice").setEnabled(true);
                 }
                 // Invoice rec
                 if(invoice.getGender() != null){
                    invoicePane.mod.gui.getComboBox("genderText","Invoice").setSelectedItem(invoice.getGender());
                 }

                 invoicePane.mod.gui.getTextfield("titleText","Invoice").setText(invoice.getPosition());
                 invoicePane.mod.gui.getTextfield("companyNameText","Invoice").setText(invoice.getCompanyname());
                 invoicePane.mod.gui.getTextfield("nameText","Invoice").setText(invoice.getContactname());
                 invoicePane.mod.gui.getTextfield("surnameText","Invoice").setText(invoice.getContactsurname());
                 invoicePane.mod.gui.getTextfield("streetNrText","Invoice").setText(invoice.getContactstreet());
                 invoicePane.mod.gui.getTextfield("zipText","Invoice").setText(invoice.getContactzip());
                 invoicePane.mod.gui.getTextfield("locationText","Invoice").setText(invoice.getContactlocation());
                 invoicePane.mod.gui.getTextfield("postCodeText","Invoice").setText(invoice.getContactpostcode());
                 invoicePane.mod.gui.getTextfield("countryText","Invoice").setText(invoice.getContactcountry());
                 invoicePane.mod.gui.getTextfield("telefonText","Invoice").setText(invoice.getContacttelephone());
                 invoicePane.mod.gui.getTextfield("faxText","Invoice").setText(invoice.getContactfax());
                 invoicePane.mod.gui.getTextfield("emailText","Invoice").setText(invoice.getContactemail());
                 invoicePane.mod.gui.getTextfield("internetText","Invoice").setText(invoice.getContactweb());
                 invoicePane.mod.gui.getTextarea("recDescription","Invoice").setText(invoice.getContactdescription());

                 invoicePane.mod.system.getDataStore("Invoice","ebiEdit",true);

                 this.dataShowProduct();
                 calculateTotalAmount();

            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
          invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDelete(int id) {

        Query query;
        try {

            invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
            query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

                if (iter.hasNext()) {

                    Crminvoice inv = (Crminvoice) iter.next();

                    if (inv.getInvoiceid() == id) {
                        try {
                            invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").delete(inv);

                            invoicePane.mod.system.getDataStore("Invoice","ebiDelete",true);
                            invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
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
        dataShow();
    }

    public void dataShow() {

        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        invoicePane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));

                        EBIAbstractTableModel model = (EBIAbstractTableModel) invoicePane.mod.gui.getTable("tableTotalInvoice","Invoice").getModel();

                        Query query;
                        try {

                            if(invoicePane.mod.system.systemStartCal != null &&  invoicePane.mod.system.systemEndCal != null){

                                query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice cm where cm.date between ? and ? order by createddate desc");
                                query.setTimestamp(0,invoicePane.mod.system.systemStartCal.getTime());
                                query.setTimestamp(1, invoicePane.mod.system.systemEndCal.getTime());

                            }else{
                                query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice order by createddate desc");
                            }

                            if(query.list().size() > 0){

                                Iterator iter =  query.iterate();

                                model.data = new Object[query.list().size()][10];

                                int i = 0;
                                while (iter.hasNext()) {

                                    Crminvoice inv = (Crminvoice) iter.next();
                                    model.data[i][0] = inv.getBeginchar() +inv.getInvoicenr();
                                    model.data[i][1] = inv.getName() == null ? "" : inv.getName();
                                    model.data[i][2] = inv.getStatus() == null ? "" : inv.getStatus();
                                    model.data[i][3] = inv.getCategory() == null ? "" : inv.getCategory();
                                    model.data[i][4] = inv.getGender() == null ? "" : inv.getGender();
                                    model.data[i][5] = inv.getCompanyname() == null ? "" : inv.getCompanyname();
                                    model.data[i][6] = inv.getContactname() == null ? "" : inv.getContactname();
                                    model.data[i][7] = inv.getContactsurname() == null ? "" : inv.getContactsurname();
                                    model.data[i][8] = inv.getDate() == null ? "" : invoicePane.mod.system.getDateToString(inv.getDate());
                                    model.data[i][9] = inv.getInvoiceid();
                                    i++;
                                }
                            }else{
                                model.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "",""}};
                            }
                            model.fireTableDataChanged();
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }finally {
                            invoicePane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }

                    }
                });
            }
        });

        thr.start();
    }

    public Hashtable<String,Double> getTaxName(int id){
        Hashtable<String,Double> taxTable = null;
    	try{
   	 	Query y = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery("from Crminvoice where invoiceid=? ").setInteger(0, id);
        
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

                            taxTable.put(pos.getTaxtype(),taxTable.get(pos.getTaxtype())+(((pos.getNetamount() * pos.getQuantity()) * invoicePane.mod.dynMethod.getTaxVal(pos.getTaxtype())) / 100));
                        }else{
                            taxTable.put(pos.getTaxtype(),(((pos.getNetamount() * pos.getQuantity()) * invoicePane.mod.dynMethod.getTaxVal(pos.getTaxtype())) / 100));

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

        invoicePane.mod.system.getIEBIReportSystemInstance().
                useReportSystem(map,
                invoicePane.mod.system.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES")),
                getInvoiceNamefromId(id));
      }catch (Exception ex){ ex.printStackTrace();}
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
                Query query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Crminvoice where invoiceId=? ").setInteger(0, id);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Crminvoice inv = (Crminvoice)iter.next();
                    invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(inv);

                    if(!"".equals(inv.getContactemail())){
                         fileName = invoicePane.mod.system.getIEBIReportSystemInstance().useReportSystem(map,
                                         invoicePane.mod.system.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES")),
                                                getInvoiceNamefromId(id),showWindow,true, inv.getContactemail());
                    }else{
                        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_NO_RECEIVER_WAS_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }
           

        }catch (Exception ex){ex.printStackTrace();}
           return fileName;
    }

    public void dataNew(boolean reload) {
       try{
            id=-1;
            invoicePane.mod.gui.getVisualPanel("Invoice").setID(-1);

            invoice = new Crminvoice();
            invoicePane.initialize();
            invoicePane.mod.system.getDataStore("Invoice","ebiNew",true);
            if(reload){
                this.dataShowProduct();
            }
       }catch (Exception ex){ex.printStackTrace();}
    }

    public void dataShowProduct() {

        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                invoicePane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(invoice.getCrminvoicepositions().size() > 0){
                                invoicePane.tabModProduct.data = new Object[invoice.getCrminvoicepositions().size()][9];

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
                                    invoicePane.tabModProduct.data[i][5] = currency.format(obj.getNetamount() == null ? "" : invoicePane.mod.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
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
                        }catch (Exception ex){ ex.printStackTrace();}finally {
                            invoicePane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                });
            }
        });

        thr.start();


    }

    public void dataDeleteProduct(int id) {

        try {

            Iterator iter = invoice.getCrminvoicepositions().iterator();
            while (iter.hasNext()) {

                Crminvoiceposition invpro = (Crminvoiceposition) iter.next();

                if (invpro.getPositionid() == id) {
                  if(id >= 0){
                    try {
                        invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                        invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").delete(invpro);
                        invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
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
                invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Crminvoice where invoiceId=? ").setInteger(0, id);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Crminvoice invoice = (Crminvoice) iter.next();
                    invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(invoice);
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
                invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Companyorder where orderid=? ").setInteger(0, orderId);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Companyorder order = (Companyorder) iter.next();
                    invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(order);
                    ret = true;
                }
           invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return ret;
    }

    public boolean loadCompanyService(int serviceId){
       Query query;
       boolean ret = false;
        try {
                invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").begin();
                query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").createQuery(
                        "from Companyservice where serviceid=? ").setInteger(0, serviceId);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Companyservice service = (Companyservice) iter.next();
                    invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(service);

                    ret = true;
                }
           invoicePane.mod.system.hibernate.getHibernateTransaction("EBIINVOICE_SESSION").commit();
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

            invoicePane.mod.gui.getFormattedTextfield("deductionText","Invoice").setValue(deduction);
            invoicePane.mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setValue(amount);
            invoicePane.mod.gui.getFormattedTextfield("taxText","Invoice").setValue(tax);
            invoicePane.mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setValue(amount+tax);
       }catch (Exception ex){ex.printStackTrace();}

    }

    private double getTaxVal(String cat){
        double val = 0.0;
         Query query;
        try {
            query = invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").
                    createQuery("from Companyproducttax where name=? ").setString(0,
                    cat);

                    Iterator it = query.iterate();

                    if (it.hasNext()) {
                      Companyproducttax tax = (Companyproducttax) it.next();
                      invoicePane.mod.system.hibernate.getHibernateSession("EBIINVOICE_SESSION").refresh(tax);
                      val = tax.getTaxvalue();
                    }
        }catch(Exception ex){
             ex.printStackTrace();
        }
      return val;
    }
}