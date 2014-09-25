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
import ebiCRM.gui.dialogs.EBINewProjectTaskDialog;
import ebiCRM.gui.panels.EBICRMAddressPane;
import ebiCRM.gui.panels.EBICRMCampaign;
import ebiCRM.gui.panels.EBICRMCompanyActivity;
import ebiCRM.gui.panels.EBICRMCompanyPane;
import ebiCRM.gui.panels.EBICRMInvoice;
import ebiCRM.gui.panels.EBICRMOffer;
import ebiCRM.gui.panels.EBICRMOpportunityPane;
import ebiCRM.gui.panels.EBICRMOrder;
import ebiCRM.gui.panels.EBICRMProblemSolution;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.gui.panels.EBICRMProjectPane;
import ebiCRM.gui.panels.EBICRMService;
import ebiCRM.gui.panels.EBIMeetingProtocol;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyactivitystatus;
import ebiNeutrinoSDK.model.hibernate.Companyactivitytype;
import ebiNeutrinoSDK.model.hibernate.Companycategory;
import ebiNeutrinoSDK.model.hibernate.Companyclassification;
import ebiNeutrinoSDK.model.hibernate.Companycooperation;
import ebiNeutrinoSDK.model.hibernate.Companymeetingtype;
import ebiNeutrinoSDK.model.hibernate.Companynumber;
import ebiNeutrinoSDK.model.hibernate.Companyofferstatus;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitybgstatus;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitybustyp;
import ebiNeutrinoSDK.model.hibernate.Companyopportunityevstatus;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitysstage;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitystatus;
import ebiNeutrinoSDK.model.hibernate.Companyorderstatus;
import ebiNeutrinoSDK.model.hibernate.Companyproductcategory;
import ebiNeutrinoSDK.model.hibernate.Companyproducttaxvalue;
import ebiNeutrinoSDK.model.hibernate.Companyproducttype;
import ebiNeutrinoSDK.model.hibernate.Companyservicecategory;
import ebiNeutrinoSDK.model.hibernate.Companyservicestatus;
import ebiNeutrinoSDK.model.hibernate.Companyservicetype;
import ebiNeutrinoSDK.model.hibernate.Crmaddresstype;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignstatus;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoicecategory;
import ebiNeutrinoSDK.model.hibernate.Crminvoicenumber;
import ebiNeutrinoSDK.model.hibernate.Crminvoicestatus;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolcategory;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolclass;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolstatus;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsoltype;
import ebiNeutrinoSDK.model.hibernate.Crmprojectstatus;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttaskstatus;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttasktype;

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
            setMeetingProtocolArt();
            setOpportunityBusinessType();
            setOpportunitySalesStage();
            setOpportunityStatus();
            setOpportunityBudgetStatus();
            setOpportunityEvalStatus();
            setCRMAdressType();
            setActivityType();
            setActivityStatus();
            setOfferStatus();
            setOrderStatus();
            setServiceStatus();
            setServiceType();
            setServiceCategory();


            setProsolStatus();
            setProsolType();
            setProsolCategory();
            setProsolClassification();

            setProductCategory();
            setProductType();
            setProductTax();

            setCampaignStatus();
            setProjectStatus();
            setProjectTaskStatus();
            setPojectTaskType();
            setInvoiceCategory();
            setInvoiceStatus();

            if (isloaded == true) {

                if(ebiModule.guiRenderer.existPackage("Summary")){
                    String sumobj = ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.categories));
                    ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").setSelectedItem(sumobj);
                }

                if(ebiModule.guiRenderer.existPackage("Company")){
                    String cobj = ebiModule.guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("categoryText","Company").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.categories));
                    ebiModule.guiRenderer.getComboBox("categoryText","Company").setSelectedItem(cobj);

                    String coobj = ebiModule.guiRenderer.getComboBox("cooperationText","Company").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("cooperationText","Company").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.cooperations));
                    ebiModule.guiRenderer.getComboBox("cooperationText","Company").setSelectedItem(coobj);

                    String cclobj = ebiModule.guiRenderer.getComboBox("classificationText","Company").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("classificationText","Company").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.classification));
                    ebiModule.guiRenderer.getComboBox("classificationText","Company").setSelectedItem(cclobj);
                }

                if(ebiModule.guiRenderer.existPackage("Leads")){
                    String ldobj = ebiModule.guiRenderer.getComboBox("classificationText","Leads").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("classificationText","Leads").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.classification));
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

                if ( ebiModule.guiRenderer.existPackage("Campaign")) {
                    String campStatus = ebiModule.guiRenderer.getComboBox("CampaignStatusText","Campaign").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("CampaignStatusText","Campaign").setModel(new DefaultComboBoxModel(EBICRMCampaign.campaignStatus));
                    ebiModule.guiRenderer.getComboBox("CampaignStatusText","Campaign").setSelectedItem(campStatus);
                }

                if(ebiModule.guiRenderer.existPackage("Project")){
                   String prjStatus = ebiModule.guiRenderer.getComboBox("prjStatusText","Project").getSelectedItem().toString(); 
                   ebiModule.guiRenderer.getComboBox("prjStatusText","Project").setModel(new DefaultComboBoxModel(EBICRMProjectPane.projectStatus));
                   ebiModule.guiRenderer.getComboBox("prjStatusText","Project").setSelectedItem(prjStatus);

                }

                if(ebiModule.guiRenderer.existPackage("Invoice")){
                   String invStatus = ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").getSelectedItem().toString();
                   ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setModel(new DefaultComboBoxModel(EBICRMInvoice.invoiceStatus));
                   ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setSelectedItem(invStatus);

                   String invCategory = ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem().toString();
                   ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setModel(new DefaultComboBoxModel(EBICRMInvoice.invoiceCategory));
                   ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setSelectedItem(invCategory);
                }


                if(ebiModule.guiRenderer.existPackage("Activity")) {
                    String actobj = ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").getSelectedItem().toString();

                    ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setModel(new DefaultComboBoxModel(EBICRMCompanyActivity.actType));
                    ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setSelectedItem(actobj);

                    String actsobj = ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setModel(new DefaultComboBoxModel(EBICRMCompanyActivity.actStatus));
                    ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setSelectedItem(actsobj);
                }

                if(ebiModule.guiRenderer.existPackage("MeetingCall")){
                    String meetingobj = ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setModel(new DefaultComboBoxModel(EBIMeetingProtocol.art));
                    ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setSelectedItem(meetingobj);
                }

                if(ebiModule.guiRenderer.existPackage("Address")){
                    String adrobj = ebiModule.guiRenderer.getComboBox("addressTypeText","Address").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("addressTypeText","Address").setModel(new DefaultComboBoxModel(EBICRMAddressPane.AddressType));
                    ebiModule.guiRenderer.getComboBox("addressTypeText","Address").setSelectedItem(adrobj);
                }

                if(ebiModule.guiRenderer.existPackage("Opportunity")){
                    String budobj = ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setModel(new DefaultComboBoxModel(EBICRMOpportunityPane.oppBudgetStatus));
                    ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setSelectedItem(budobj);

                    String busobj = ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setModel(new DefaultComboBoxModel(EBICRMOpportunityPane.oppBussinesType));
                    ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setSelectedItem(busobj);

                    String evalobj = ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setModel(new DefaultComboBoxModel(EBICRMOpportunityPane.oppEvalStatus));
                    ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setSelectedItem(evalobj);

                    String oppobj = ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setModel(new DefaultComboBoxModel(EBICRMOpportunityPane.oppStatus));
                    ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setSelectedItem(oppobj);

                    String stgobj = ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setModel(new DefaultComboBoxModel(EBICRMOpportunityPane.oppSalesStage));
                    ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setSelectedItem(stgobj);
                }

                if(ebiModule.guiRenderer.existPackage("Offer")){
                    String sttobj = ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setModel(new DefaultComboBoxModel(EBICRMOffer.offerStatus));
                    ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setSelectedItem(sttobj);
                }

                if(ebiModule.guiRenderer.existPackage("Order")){
                    String statobj = ebiModule.guiRenderer.getComboBox("orderStatusText","Order").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setModel(new DefaultComboBoxModel(EBICRMOrder.orderStatus));
                    ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setSelectedItem(statobj);
                }

                if(ebiModule.guiRenderer.existPackage("Service")){
                    String ssobj = ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setModel(new DefaultComboBoxModel(EBICRMService.serviceStatus));
                    ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setSelectedItem(ssobj);

                    String scobj = ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setModel(new DefaultComboBoxModel(EBICRMService.serviceCategory));
                    ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setSelectedItem(scobj);

                    String stobj = ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setModel(new DefaultComboBoxModel(EBICRMService.serviceType));
                    ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setSelectedItem(stobj);
                }

                if(ebiModule.guiRenderer.existPackage("Prosol")){

                    String prsobj = ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").setModel(new DefaultComboBoxModel(EBICRMProblemSolution.prosolStatus));
                    ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").setSelectedItem(prsobj);

                    String prcobj = ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").setModel(new DefaultComboBoxModel(EBICRMProblemSolution.prosolCategory));
                    ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").setSelectedItem(prcobj);

                    String prtobj = ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").setModel(new DefaultComboBoxModel(EBICRMProblemSolution.prosolType));
                    ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").setSelectedItem(prtobj);

                    String prclobj = ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").setModel(new DefaultComboBoxModel(EBICRMProblemSolution.prosolClassification));
                    ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").setSelectedItem(prclobj);
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

    private synchronized void setOfferStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyofferstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOffer.offerStatus = new String[query.list().size() + 1];
            EBICRMOffer.offerStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyofferstatus compofst = (Companyofferstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compofst);
                EBICRMOffer.offerStatus[i] = compofst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setCampaignStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmcampaignstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMCampaign.campaignStatus = new String[query.list().size() + 1];
            EBICRMCampaign.campaignStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crmcampaignstatus comporst = (Crmcampaignstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMCampaign.campaignStatus[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProjectStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmprojectstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProjectPane.projectStatus = new String[query.list().size() + 1];
            EBICRMProjectPane.projectStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crmprojectstatus prjstatus = (Crmprojectstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(prjstatus);
                EBICRMProjectPane.projectStatus[i] = prjstatus.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProjectTaskStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmprojecttaskstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBINewProjectTaskDialog.taskStatus = new String[query.list().size() + 1];
            EBINewProjectTaskDialog.taskStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crmprojecttaskstatus taskstatus = (Crmprojecttaskstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(taskstatus);
                EBINewProjectTaskDialog.taskStatus[i] = taskstatus.getName();
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

    private synchronized void setPojectTaskType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmprojecttasktype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBINewProjectTaskDialog.taskType = new String[query.list().size() + 1];
            EBINewProjectTaskDialog.taskType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {

                Crmprojecttasktype taskType = (Crmprojecttasktype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(taskType);
                EBINewProjectTaskDialog.taskType[i] = taskType.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setOrderStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyorderstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOrder.orderStatus = new String[query.list().size() + 1];
            EBICRMOrder.orderStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyorderstatus comporst = (Companyorderstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMOrder.orderStatus[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setServiceStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyservicestatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMService.serviceStatus = new String[query.list().size() + 1];
            EBICRMService.serviceStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyservicestatus comporst = (Companyservicestatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMService.serviceStatus[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProsolStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmproblemsolstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProblemSolution.prosolStatus = new String[query.list().size() + 1];
            EBICRMProblemSolution.prosolStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Crmproblemsolstatus comporst = (Crmproblemsolstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMProblemSolution.prosolStatus[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setServiceType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyservicetype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMService.serviceType = new String[query.list().size() + 1];
            EBICRMService.serviceType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyservicetype compsety = (Companyservicetype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compsety);
                EBICRMService.serviceType[i] = compsety.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProsolType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmproblemsoltype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProblemSolution.prosolType = new String[query.list().size() + 1];
            EBICRMProblemSolution.prosolType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Crmproblemsoltype compsety = (Crmproblemsoltype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compsety);
                EBICRMProblemSolution.prosolType[i] = compsety.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setServiceCategory() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyservicecategory order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMService.serviceCategory = new String[query.list().size() + 1];
            EBICRMService.serviceCategory[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyservicecategory compsest = (Companyservicecategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compsest);
                EBICRMService.serviceCategory[i] = compsest.getName();
                i++;                                                                  
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProsolCategory() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmproblemsolcategory order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProblemSolution.prosolCategory = new String[query.list().size() + 1];
            EBICRMProblemSolution.prosolCategory[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Crmproblemsolcategory compsest = (Crmproblemsolcategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compsest);
                EBICRMProblemSolution.prosolCategory[i] = compsest.getName();
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
            EBICRMCompanyPane.classification = new String[query.list().size() + 1];
            EBICRMCompanyPane.classification[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyclassification comporst = (Companyclassification) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMCompanyPane.classification[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setProsolClassification() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmproblemsolclass order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMProblemSolution.prosolClassification = new String[query.list().size() + 1];
            EBICRMProblemSolution.prosolClassification[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Crmproblemsolclass comporst = (Crmproblemsolclass) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comporst);
                EBICRMProblemSolution.prosolClassification[i] = comporst.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setActivityType() {
        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyactivitytype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMCompanyActivity.actType = new String[query.list().size() + 1];
            EBICRMCompanyActivity.actType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyactivitytype compacttype = (Companyactivitytype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compacttype);
                EBICRMCompanyActivity.actType[i] = compacttype.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void setActivityStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyactivitystatus order by name");
            java.util.Iterator it = query.iterate();
            int i = 1;
            EBICRMCompanyActivity.actStatus = new String[query.list().size() + 1];
            EBICRMCompanyActivity.actStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyactivitystatus compacttype = (Companyactivitystatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compacttype);
                EBICRMCompanyActivity.actStatus[i] = compacttype.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setCRMAdressType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmaddresstype order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMAddressPane.AddressType = new String[query.list().size() + 1];
            EBICRMAddressPane.AddressType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Crmaddresstype crmadrtype = (Crmaddresstype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(crmadrtype);
                EBICRMAddressPane.AddressType[i] = crmadrtype.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void setOpportunityStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyopportunitystatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOpportunityPane.oppStatus = new String[query.list().size() + 1];
            EBICRMOpportunityPane.oppStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyopportunitystatus compoppstat = (Companyopportunitystatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compoppstat);
                EBICRMOpportunityPane.oppStatus[i] = compoppstat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void setOpportunityBudgetStatus() {
        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyopportunitybgstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOpportunityPane.oppBudgetStatus = new String[query.list().size() + 1];
            EBICRMOpportunityPane.oppBudgetStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyopportunitybgstatus compoppstat = (Companyopportunitybgstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compoppstat);
                EBICRMOpportunityPane.oppBudgetStatus[i] = compoppstat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private synchronized void setOpportunityEvalStatus() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyopportunityevstatus order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOpportunityPane.oppEvalStatus = new String[query.list().size() + 1];
            EBICRMOpportunityPane.oppEvalStatus[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyopportunityevstatus compoppevstat = (Companyopportunityevstatus) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compoppevstat);
                EBICRMOpportunityPane.oppEvalStatus[i] = compoppevstat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setOpportunityBusinessType() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyopportunitybustyp order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOpportunityPane.oppBussinesType = new String[query.list().size() + 1];
            EBICRMOpportunityPane.oppBussinesType[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyopportunitybustyp compoppbustype = (Companyopportunitybustyp) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compoppbustype);
                EBICRMOpportunityPane.oppBussinesType[i] = compoppbustype.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setOpportunitySalesStage() {

        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companyopportunitysstage order by name");
            Iterator it = query.iterate();
            int i = 1;
            EBICRMOpportunityPane.oppSalesStage = new String[query.list().size() + 1];
            EBICRMOpportunityPane.oppSalesStage[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companyopportunitysstage compoppsstage = (Companyopportunitysstage) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compoppsstage);
                EBICRMOpportunityPane.oppSalesStage[i] = compoppsstage.getName();
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
            EBICRMCompanyPane.categories = new String[query.list().size() + 1];
            EBICRMCompanyPane.categories[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companycategory compcat = (Companycategory) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compcat);
                EBICRMCompanyPane.categories[i] = compcat.getName();
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
            EBICRMCompanyPane.cooperations = new String[query.list().size() + 1];
            EBICRMCompanyPane.cooperations[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companycooperation compcat = (Companycooperation) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compcat);
                EBICRMCompanyPane.cooperations[i] = compcat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public synchronized void setMeetingProtocolArt() {


        try {
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Companymeetingtype as cmt order by cmt.name");
            Iterator it = query.iterate();
            int i = 1;
            EBIMeetingProtocol.art = new String[query.list().size() + 1];
            EBIMeetingProtocol.art[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            while (it.hasNext()) {
                Companymeetingtype compcat = (Companymeetingtype) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(compcat);
                EBIMeetingProtocol.art[i] = compcat.getName();
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean findCustomerNumber(String Nr) {

        try {
            
            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company where customernr=? ").setString(0, Nr);

            if (query.list().size() > 0) {
                return true;
            }

        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }

    public  String getCompanyNameFromID(int id) {
        String sCompany = "";
        try {

            Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company where CompanyID=? order by name ").setInteger(0, id);

            Iterator it = query.iterate();
            if (it.hasNext()) {
                Company lcmp = (Company) it.next();
                ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(lcmp);
                sCompany = lcmp.getName();
            }

        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sCompany;
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
