package ebiCRM.functionality;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.panels.EBICRMInvoice;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyactivitystatus;
import ebiNeutrinoSDK.model.hibernate.Companyactivitytype;
import ebiNeutrinoSDK.model.hibernate.Companycategory;
import ebiNeutrinoSDK.model.hibernate.Companyclassification;
import ebiNeutrinoSDK.model.hibernate.Companycooperation;
import ebiNeutrinoSDK.model.hibernate.Companynumber;
import ebiNeutrinoSDK.model.hibernate.Companyofferstatus;
import ebiNeutrinoSDK.model.hibernate.Companyorderstatus;
import ebiNeutrinoSDK.model.hibernate.Companyproductcategory;
import ebiNeutrinoSDK.model.hibernate.Companyproducttaxvalue;
import ebiNeutrinoSDK.model.hibernate.Companyproducttype;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoicecategory;
import ebiNeutrinoSDK.model.hibernate.Crminvoicenumber;
import ebiNeutrinoSDK.model.hibernate.Crminvoicestatus;

public class EBICRMDynamicFunctionalityMethods {

    private EBICRMModule ebiModule = null;
    private EBIPGFactory ebiPGFactory = null;

    public EBICRMDynamicFunctionalityMethods(EBICRMModule module) {
        ebiModule = module;
        ebiPGFactory = module.ebiPGFactory;
    }

    public Object[] getInternNumber(String category, boolean isInvoice) {

        Object[] toRet = new Object[2];
        try {
            String qu;
            if(isInvoice){
              qu = "Crminvoicenumber";
            }else{
              qu = "Companynumber"; 
            }
            
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM "+qu+" comp WHERE comp.category=?").setString(0, category);

            if (query.list().size() > 0) {
                
                Iterator it = query.iterate();
                Object incNr = it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(incNr);
                
                toRet[0] = getAvailableInternalNumber(category,isInvoice);

                if (Integer.parseInt(toRet[0].toString()) == 0) {
                    toRet[0] = incNr instanceof Companynumber ? ((Companynumber)incNr).getNumberfrom().intValue() : ((Crminvoicenumber)incNr).getNumberfrom().intValue();
                } else {
                    toRet[0] = Integer.parseInt(toRet[0].toString()) + 1;
                }

                int nrTo = incNr instanceof Companynumber ? ((Companynumber)incNr).getNumberto().intValue() : ((Crminvoicenumber)incNr).getNumberto().intValue();
                
                if (Integer.parseInt(toRet[0].toString()) > nrTo) {
                    toRet[0] = -1;
                }

                String bgCahr = incNr instanceof Companynumber ? ((Companynumber)incNr).getBeginchar() : ((Crminvoicenumber)incNr).getBeginchar();
                toRet[1] = bgCahr == null ? "" : bgCahr;

            } else {

                toRet[0] = -1;
                toRet[1] = "";

            }
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INTERNAL_NUMBER_NOTEXIST")).Show(EBIMessage.ERROR_MESSAGE);
            toRet[0] = -1;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return toRet;
    }

    private int getAvailableInternalNumber(String category, boolean isInvoice) {

        int toRet = 0;
        try {
            String qu;
            String nr;
            if(isInvoice){
               qu = "Crminvoice";
               nr = "invoicenr";
            }else{
               qu = "Company";
               nr = "companynumber";
            }

            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from "+qu+" as comp where comp.category=? order by comp."+nr+" desc");
                  query.setString(0, category);


            Iterator itx = query.iterate();

            if (itx.hasNext()) {
                Object comp = itx.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comp);
                toRet = comp instanceof Company ? ((Company)comp).getCompanynumber() : ((Crminvoice)comp).getInvoicenr();
            }

        } catch (org.hibernate.HibernateException ex) {
             ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return toRet;
    }

    public void initComboBoxes(boolean isloaded) throws Exception {

            boolean haveModuleChange = EBIPGFactory.canRelease;
            setCRMCategory();
            setCRMCooperation();
            setClassification();

            setProductCategory();
            setProductType();
            setProductTax();

            setInvoiceCategory();
            setInvoiceStatus();

            if (isloaded == true) {


                if(ebiModule.guiRenderer.existPackage("Leads")){
                    String ldobj = ebiModule.guiRenderer.getComboBox("classificationText","Leads").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("classificationText","Leads").setModel(new DefaultComboBoxModel(EBICRMModule.classification));
                    ebiModule.guiRenderer.getComboBox("classificationText","Leads").setSelectedItem(ldobj);
                }

                if (ebiModule.guiRenderer.existPackage("Product")) {
                    String protax = ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").setModel(new DefaultComboBoxModel(EBICRMProduct.taxType));
                    ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").setSelectedItem(protax);

                    String protype = ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").setModel(new DefaultComboBoxModel(EBICRMProduct.type));
                    ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").setSelectedItem(protype);

                    String procat = ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setModel(new DefaultComboBoxModel(EBICRMProduct.category));
                    ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setSelectedItem(procat);

                }

                if(ebiModule.guiRenderer.existPackage("Invoice")){
                   String invStatus = ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").getSelectedItem().toString();
                   ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setModel(new DefaultComboBoxModel(EBICRMInvoice.invoiceStatus));
                   ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setSelectedItem(invStatus);

                   String invCategory = ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem().toString();
                   ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setModel(new DefaultComboBoxModel(EBICRMInvoice.invoiceCategory));
                   ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setSelectedItem(invCategory);
                }

            }
            EBIPGFactory.canRelease = haveModuleChange;
    }

    private synchronized void setProductTax() {
        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyproducttaxvalue order by name");
            Iterator it = query.iterate();
            int i = 1;

            EBICRMProduct.taxType = new String[query.list().size() + 1];
            EBICRMProduct.taxType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyproducttaxvalue compofst = (Companyproducttaxvalue) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compofst);
                EBICRMProduct.taxType[i] = compofst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private synchronized void setProductType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyproducttype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProduct.type = new String[query.list().size() + 1];
            EBICRMProduct.type[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyproducttype compofst = (Companyproducttype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compofst);
                EBICRMProduct.type[i] = compofst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProductCategory() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyproductcategory order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProduct.category = new String[query.list().size() + 1];
            EBICRMProduct.category[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyproductcategory compofst = (Companyproductcategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compofst);
                EBICRMProduct.category[i] = compofst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private synchronized void setInvoiceStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crminvoicestatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMInvoice.invoiceStatus = new String[query.list().size() + 2];
            EBICRMInvoice.invoiceStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crminvoicestatus invstatus = (Crminvoicestatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(invstatus);
                EBICRMInvoice.invoiceStatus[i] = invstatus.getName();
                i++;
            }
            EBICRMInvoice.invoiceStatus[i] = EBIPGFactory.getLANG("EBI_LANG_CANCELLATION");

        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setInvoiceCategory() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crminvoicecategory order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMInvoice.invoiceCategory = new String[query.list().size() + 1];
            EBICRMInvoice.invoiceCategory[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crminvoicecategory invcategory = (Crminvoicecategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(invcategory);
                EBICRMInvoice.invoiceCategory[i] = invcategory.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setClassification() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyclassification order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMModule.classification = new String[query.list().size() + 1];
            EBICRMModule.classification[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyclassification comporst = (Companyclassification) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMModule.classification[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void setCRMCategory() {

        try {

            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companycategory order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMModule.categories = new String[query.list().size() + 1];
            EBICRMModule.categories[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companycategory compcat = (Companycategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compcat);
                EBICRMModule.categories[i] = compcat.getName();
                i++;
            }

        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setCRMCooperation() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companycooperation order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMModule.cooperations = new String[query.list().size() + 1];
            EBICRMModule.cooperations[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companycooperation compcat = (Companycooperation) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compcat);
                EBICRMModule.cooperations[i] = compcat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public double calculatePreTaxPrice(double pValue, String quantity, String deduction) {

        double retValue = 0.0;
        double nVal =0.0;
        
        try {

           if(!quantity.equals("")){

               try{
                   
                    nVal = pValue * Integer.parseInt(quantity);

                    double tVal;
                    if(!deduction.equals("")){
                        tVal =  ((nVal *   Integer.parseInt(deduction)) / 100);
                        nVal =  nVal - tVal;
                    }

                }catch(NumberFormatException ex){ex.printStackTrace();}


                BigDecimal bd = new BigDecimal(nVal);
                BigDecimal bd_round = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                retValue = bd_round.doubleValue();

           }
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retValue;
    }
    
    
    public int getIdIndexFormArrayInATable(Object data[][], int pos , int id ){
    
    	int i = 0;
    	for(i=0; i<data.length-1; i++){
    		if(Integer.parseInt(data[i][pos].toString()) ==  id){
    			break;
    		}
    	}
    	
    	return i;
    }
    
    public double getTaxVal(String cat){
        double val = 0.0;
        
        ResultSet set = null;
        try{
        	ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
        	PreparedStatement ps =ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT NAME,TAXVALUE FROM COMPANYPRODUCTTAX WHERE NAME=?");
        	ps.setString(1, cat);
        	
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
        	set.last();
        	if(set.getRow() > 0){
        		set.beforeFirst();
        		while(set.next()){
        			val = set.getDouble("TAXVALUE");
        		}
        	}
  
        }catch(SQLException ex){
        	ex.printStackTrace();
        }finally{
        	try {
				set.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
        }
        
      return val;
    }
    
}
