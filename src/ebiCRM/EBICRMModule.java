package ebiCRM;

import java.awt.Cursor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.functionality.EBICRMDynamicFunctionalityMethods;
import ebiCRM.gui.component.EBICRMTabcontrol;
import ebiCRM.gui.component.EBICRMToolBar;
import ebiCRM.gui.dialogs.EBIPessimisticViewDialog;
import ebiCRM.gui.panels.EBICRMAccountStack;
import ebiCRM.gui.panels.EBICRMAddressPane;
import ebiCRM.gui.panels.EBICRMBankPane;
import ebiCRM.gui.panels.EBICRMCalendar;
import ebiCRM.gui.panels.EBICRMCampaign;
import ebiCRM.gui.panels.EBICRMCashRegisterStack;
import ebiCRM.gui.panels.EBICRMCompanyActivity;
import ebiCRM.gui.panels.EBICRMCompanyPane;
import ebiCRM.gui.panels.EBICRMContactPane;
import ebiCRM.gui.panels.EBICRMInvoice;
import ebiCRM.gui.panels.EBICRMLead;
import ebiCRM.gui.panels.EBICRMOffer;
import ebiCRM.gui.panels.EBICRMOpportunityPane;
import ebiCRM.gui.panels.EBICRMOrder;
import ebiCRM.gui.panels.EBICRMProblemSolution;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.gui.panels.EBICRMProjectPane;
import ebiCRM.gui.panels.EBICRMService;
import ebiCRM.gui.panels.EBICRMSummaryPane;
import ebiCRM.gui.panels.EBIMeetingProtocol;
import ebiCRM.utils.EBIAllertTimer;
import ebiCRM.utils.EBICRMHistoryCreator;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiCRM.utils.EBITimerTaskFixRate;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIExtension;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.interfaces.IEBIStoreInterface;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyhirarchie;

public class EBICRMModule implements IEBIExtension, IEBIStoreInterface {

    private EBICRMCompanyPane companyPane = null;
    private EBICRMContactPane contactPane = null;
    private EBIMeetingProtocol meetingReport = null;
    public String beginChar = "";
    private EBICRMAddressPane addressPane = null;
    private EBICRMOpportunityPane opportunityPane = null;
    private EBICRMCompanyActivity activitiesPane = null;
    private EBICRMProjectPane projectPane = null;
    private EBICRMBankPane bankPane = null;
    private EBICRMOffer offerPane = null;
    private EBICRMOrder orderPane = null;
    private EBICRMLead leadsPane = null;
    private EBICRMAccountStack accountPane =null;
    private EBICRMCashRegisterStack cashRegisterPane = null;
    private EBICRMSummaryPane summaryPane = null;
    private EBICRMProduct productPane = null;
    private EBICRMCalendar calendarPane = null;
    private EBICRMCampaign campaignPane = null;
    private EBICRMService servicePane = null;
    private EBICRMProblemSolution prosolPane = null;
    private EBICRMInvoice invoicePane = null;
    public EBIPGFactory ebiPGFactory = null;
    public int companyID = -1;
    public static boolean RELOAD = false;
    public static boolean isExistCompany = false;
    public EBICRMTabcontrol ebiContainer = null;
    public EBICRMDynamicFunctionalityMethods dynMethod = null;
    public EBICRMToolBar crmToolBar = null;
    public int EBICRM_SESSION = 0;
    public EBICRMHistoryCreator hcreator = null;
    public IEBIGUIRenderer guiRenderer = null;
    public EBIPessimisticViewDialog pessimisticStruct = null;
    public static final Logger logger = Logger.getLogger(EBICRMModule.class.getName());
    public EBIAllertTimer allertTimer = null;


    public EBICRMModule(EBIPGFactory func) {
        //Init CODE
        ebiPGFactory = func;
        hcreator = new EBICRMHistoryCreator(this);
        
        // tabProp = new EBISaveRestoreTableProperties();
        try {
            ebiPGFactory.hibernate.openHibernateSession("EBICRM_SESSION");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        dynMethod = new EBICRMDynamicFunctionalityMethods(this);
        crmToolBar = new EBICRMToolBar(this);
        ebiContainer = new EBICRMTabcontrol(this);
    }
    
    /**
     * Save a CRM Record
     */
    public boolean ebiSave() {
        boolean ret = false;
        if(guiRenderer.existPackage("Company")){
            if (checkCompany()) {
                if (updateCompany() == true) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_SAVED")).Show(EBIMessage.INFO_MESSAGE);
                    if (RELOAD == true) {
                        createUI(companyID,true);
                    }
                    ret = true;
                    RELOAD = false;
                } else {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_RECORD_SAVE")).Show(EBIMessage.ERROR_MESSAGE);
                }
            }
        }
        return ret;
    }

    /**
     * Update a CRM Record
     */
    public boolean ebiUpdate() {
        boolean ret = false;
        if(guiRenderer.existPackage("Company")){
            if (checkCompany()) {
                if (updateCompany() == true) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_SAVED")).Show(EBIMessage.INFO_MESSAGE);
                    if (RELOAD == true) {
                        createUI(companyID,true);
                    }
                    ret = true;
                    RELOAD = false;
                } else {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_RECORD_SAVE")).Show(EBIMessage.ERROR_MESSAGE);
                }
            }
        }
        return ret;
    }

    /**
     * Delete CRM Record
     */
    public boolean ebiDelete() {
        return true;
    }

    /**
     * CRM Module start point
     */
    public boolean ebiMain(final Object obj) {
        try{
            isExistCompany = false;
            RELOAD = false;

            guiRenderer = ebiPGFactory.getIEBIGUIRendererInstance();
            guiRenderer.loadProject("EBICRM/project.xml");

            if(!guiRenderer.isToolBarEmpty()){
                crmToolBar.setCRMToolBar();
            }

            dynMethod.initComboBoxes(false);

            if(pessimisticStruct == null || (obj != null && Integer.parseInt(obj.toString()) == -1)){
                pessimisticStruct = new EBIPessimisticViewDialog(this);
                pessimisticStruct.setModuleName("CRMRecord");
            }

            if(guiRenderer.existPackage("Company")){
                guiRenderer.getVisualPanel("Company").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
            }

            SwingUtilities.invokeLater(new Runnable(){  // Check for activities
        	   public void run(){
        		   allertTimer = new EBIAllertTimer(EBICRMModule.this);
                   allertTimer.setUpAvailableTimer();

                   java.util.Timer timerFix = new java.util.Timer();
                   timerFix.scheduleAtFixedRate(new EBITimerTaskFixRate(EBICRMModule.this), 12000, 32000); //Every 2 minute check for
               }
            });

            if(obj == null){ // Load Dashboard as default
                ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Summary");
            }

            crmToolBar.setDeleteButtonEnabled(false);
        }catch(Exception ex){
             ex.printStackTrace();
             logger.error("EBI Neutrino CRM Error:",ex.fillInStackTrace());
        }
        return true;
    }

    /**
     * remove crm module
     * @return
     */
    public Object ebiRemove() {
        companyPane = null;
        contactPane = null;
        addressPane = null;
        bankPane = null;
        projectPane = null;
        meetingReport = null;
        activitiesPane = null;
        opportunityPane = null;
        accountPane = null;
        offerPane = null;
        orderPane = null;
        leadsPane = null;
        summaryPane = null;
        servicePane = null;
        productPane = null;
        invoicePane = null;
        cashRegisterPane = null;
        campaignPane = null;
        calendarPane = null;
        prosolPane = null;
        return companyID;
    }

    public void onLoad(){

       if(guiRenderer.getVisualPanel("Company") != null){
            guiRenderer.getVisualPanel("Company").setCreatedDate(ebiPGFactory.getDateToString(new Date()));
            guiRenderer.getVisualPanel("Company").setCreatedFrom(EBIPGFactory.ebiUser);
       }
    }

    public void onExit(){
        unlockCompanyRecord(companyID, pessimisticStruct.getLockUser(),"CRMRecord");

        if(campaignPane != null){
            unlockCompanyRecord(campaignPane.dataControlCampaign.lockId,
                                            campaignPane.dataControlCampaign.lockUser,"CRMCampaign");
        }

        if(invoicePane != null){
            unlockCompanyRecord(invoicePane.dataControlInvoice.lockId,
                                            invoicePane.dataControlInvoice.lockUser,"CRMInvoice");
        }

        if(accountPane  != null){
            unlockCompanyRecord(accountPane.dataControlAccount.lockId,
                                            accountPane.dataControlAccount.lockUser,"CRMAccount");
        }

        if(productPane  != null){
            unlockCompanyRecord(productPane.dataControlProduct.lockId,
                                            productPane.dataControlProduct.lockUser,"CRMProduct");
        }

        if(prosolPane != null){
            unlockCompanyRecord(prosolPane.dataControlProsol.lockId,
                                            prosolPane.dataControlProsol.lockUser,"CRMProsol");
        }
        if(projectPane != null){
            unlockCompanyRecord(projectPane.dataControlProject.lockId,
                                            projectPane.dataControlProject.lockUser,"CRMProject");
        }


    }

    public EBICRMCalendar getEBICalendar() {
        if (calendarPane == null) {
            calendarPane = new EBICRMCalendar(this);
        }
        return calendarPane;
    }

    public EBICRMCompanyPane getCompanyPane() {
        if (companyPane == null) {
            companyPane = new EBICRMCompanyPane(this);
            ebiPGFactory.setDataStore("Company",companyPane);
        }

        return companyPane;
    }

    public EBICRMCampaign getEBICRMCampaign() {
        if (campaignPane == null) {
            campaignPane = new EBICRMCampaign(this);
            ebiPGFactory.setDataStore("Campaign",campaignPane);
            guiRenderer.getVisualPanel("Campaign").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
        return campaignPane;
    }

    public EBIMeetingProtocol getMeetingProtocol() {
        if (meetingReport == null) {
            meetingReport = new EBIMeetingProtocol(this);
            ebiPGFactory.setDataStore("MeetingCall",meetingReport);
        }

        if(guiRenderer.existPackage("MeetingCall")){
            guiRenderer.getVisualPanel("MeetingCall").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return meetingReport;
    }

    public EBICRMContactPane getContactPane() {
        if (contactPane == null) {
            contactPane = new EBICRMContactPane(this);
            ebiPGFactory.setDataStore("Contact",contactPane);
        }
        if(guiRenderer.existPackage("Contact")){
            guiRenderer.getVisualPanel("Contact").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
        return contactPane;
    }

    public EBICRMAddressPane getAddressPane() {
        if (addressPane == null) {
            addressPane = new EBICRMAddressPane(this);
            ebiPGFactory.setDataStore("Address",addressPane);
        }

        if(guiRenderer.existPackage("Address")){
            guiRenderer.getVisualPanel("Address").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return addressPane;

    }

    public EBICRMOpportunityPane getOpportunityPane() {
        if (opportunityPane == null) {
            opportunityPane = new EBICRMOpportunityPane(this);
            ebiPGFactory.setDataStore("Opportunity",opportunityPane);
        }

        if(guiRenderer.existPackage("Opportunity")){
            guiRenderer.getVisualPanel("Opportunity").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return opportunityPane;
    }

    public EBICRMCompanyActivity getActivitiesPane() {
        if (activitiesPane == null) {
            activitiesPane = new EBICRMCompanyActivity(this);
            ebiPGFactory.setDataStore("Activity",activitiesPane);
        }

        if(guiRenderer.existPackage("Activity")){
            guiRenderer.getVisualPanel("Activity").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return activitiesPane;
    }

    public EBICRMOffer getOfferPane() {
        if (offerPane == null) {
            offerPane = new EBICRMOffer(this);
            ebiPGFactory.setDataStore("Offer",offerPane);
        }

        if(guiRenderer.existPackage("Offer")){
            guiRenderer.getVisualPanel("Offer").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return offerPane;
    }

    public EBICRMLead getLeadPane() {
        if (leadsPane == null) {
            leadsPane = new EBICRMLead(this);
            ebiPGFactory.setDataStore("Leads",leadsPane);
        }
        return leadsPane;
    }

    public EBICRMOrder getOrderPane() {
        if (orderPane == null) {
            orderPane = new EBICRMOrder(this);
            ebiPGFactory.setDataStore("Order",orderPane);
        }

        if(guiRenderer.existPackage("Order")){
            guiRenderer.getVisualPanel("Order").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return orderPane;
    }

    public EBICRMService getServicePane(){

        if(servicePane == null){
            servicePane = new EBICRMService(this);
            ebiPGFactory.setDataStore("Service",servicePane);
        }

        if(guiRenderer.existPackage("Service")){
            guiRenderer.getVisualPanel("Service").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return servicePane;
    }

    public EBICRMProblemSolution getProsolPane(){

        if(prosolPane == null){
            prosolPane = new EBICRMProblemSolution(this);
            ebiPGFactory.setDataStore("Prosol",prosolPane);
            guiRenderer.getVisualPanel("Prosol").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return prosolPane;
    }

    public EBICRMProjectPane getProjectPane(){
        if(projectPane == null){
            projectPane = new EBICRMProjectPane(this);
            ebiPGFactory.setDataStore("Project",projectPane);
            guiRenderer.getVisualPanel("Project").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
        return projectPane;
    }

    public EBICRMInvoice getInvoicePane(){
        if(invoicePane == null){
            invoicePane = new EBICRMInvoice(this);
            ebiPGFactory.setDataStore("Invoice",invoicePane);
            guiRenderer.getVisualPanel("Invoice").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
        invoicePane.dataControlInvoice.dataShow(-1);
       return invoicePane; 
    }

    public EBICRMProduct getProductPane(){
        if(productPane == null){
            productPane = new EBICRMProduct(this);
            ebiPGFactory.setDataStore("Product",productPane);
            guiRenderer.getVisualPanel("Product").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
        return productPane;
    }

    public EBICRMAccountStack getAccountPane(){
        if(accountPane == null){
           accountPane = new EBICRMAccountStack(this);
           ebiPGFactory.setDataStore("Account",accountPane);
           guiRenderer.getVisualPanel("Account").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }
       return accountPane;
    }

    public EBICRMCashRegisterStack getCashRegisterPane(){
        if(cashRegisterPane == null){
           cashRegisterPane = new EBICRMCashRegisterStack(this);
           ebiPGFactory.setDataStore("CashRegister",cashRegisterPane);
        }
       return cashRegisterPane;
    }

    public EBICRMSummaryPane getSummaryPane() {
        if (summaryPane == null) {
            summaryPane = new EBICRMSummaryPane(this);
        }
        return summaryPane;
    }

    public EBICRMBankPane getBankdataPane() {
        if (bankPane == null) {
            bankPane = new EBICRMBankPane(this);
            ebiPGFactory.setDataStore("Bank",bankPane);
        }

        if(guiRenderer.existPackage("Bank")){
            guiRenderer.getVisualPanel("Bank").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
        }

        return bankPane;
    }

    /**
     * reset GUI called if the user click the new icon
     * @param reloadCRM
     * @param reloading
     */

    public synchronized void resetUI(final boolean reloadCRM, boolean reloading) {
       try{
        isExistCompany = false;

        unlockCompanyRecord(companyID, pessimisticStruct.getLockUser(),"CRMRecord");

        ebiPGFactory.company = null;
        ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").evict(ebiPGFactory.company);
        EBIPGFactory.isSaveOrUpdate = false;
        EBIPGFactory.canRelease = true;

        companyID = -1;
        pessimisticStruct.setLockId(-1);
        pessimisticStruct.setModuleName("");
        pessimisticStruct.setLockUser("");
        pessimisticStruct.setLockStatus(0);
        pessimisticStruct.setLockTime(null);
        
        crmToolBar.setDeleteButtonEnabled(false);
        //crmMenu.setDeleteItemEnabled(false);
        if(!reloadCRM){
            if(guiRenderer.existPackage("Company")){
                getCompanyPane().initialize();
                guiRenderer.getVisualPanel("Company").setID(-1);
            }
            if(guiRenderer.existPackage("Contact")){
                getContactPane().initialize();
                getContactPane().contactDataControl.dataNew();
            }
            if(guiRenderer.existPackage("Address")){
                getAddressPane().initialize();
                getAddressPane().addressDataControl.dataNew();
            }
            if(guiRenderer.existPackage("Bank")){
                getBankdataPane().initialize();
                getBankdataPane().bankDataControl.dataNew();
            }
            if(guiRenderer.existPackage("MeetingCall")){
               getMeetingProtocol().initialize();
               getMeetingProtocol().meetingDataControl.dataNew();

            }
            if(guiRenderer.existPackage("Activity")){
                getActivitiesPane().initialize();
                getActivitiesPane().activityDataControl.dataNew();
            }
            if(guiRenderer.existPackage("Opportunity")){
                getOpportunityPane().initialize();
                getOpportunityPane().opportuniyDataControl.dataNew();
            }
            if(guiRenderer.existPackage("Offer")){
                getOfferPane().initialize();
                getOfferPane().offerDataControl.dataNew(true);
            }
            if(guiRenderer.existPackage("Order")){
                getOrderPane().initialize();
                getOrderPane().orderDataControl.dataNew(true);
            }
            if(guiRenderer.existPackage("Service")){
                getServicePane().initialize();
                getServicePane().serviceDataControl.dataNew(true);
            }
        }
        if(reloading){
            if(guiRenderer.existPackage("Summary")){
                getSummaryPane().initialize();
            }
            if(guiRenderer.existPackage("Leads")){
                getLeadPane().initialize();
            }
        }

        //Reset Root to std. name
        ebiPGFactory.getIEBIContainerInstance().getTreeViewInstance().
                   getModel().valueForPathChanged(ebiPGFactory.getIEBIContainerInstance().getTreeViewInstance().getPathForRow(0),"EBI Neutrino");

       }catch(Exception ex){
    	   ex.printStackTrace();
           logger.error(ex.getMessage(), ex.fillInStackTrace());
       }

    }

    public boolean saveCompany(){

        try{
            if(!guiRenderer.existPackage("Company")){
                return false;
            }

            guiRenderer.getVisualPanel("Company").setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if (companyID != -1) {
                ebiUpdate();
            } else {
                ebiSave();
            }
            EBIPGFactory.canRelease = true;


        }catch(Exception ex){ex.printStackTrace(); logger.error("Error save record",ex.fillInStackTrace());} finally{
            if(guiRenderer.getVisualPanel("Company") != null){
                guiRenderer.getVisualPanel("Company").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
        return true;
    }

    /**
     * Check if obligatory company fields are filled
     * @return
     */

    private boolean checkCompany() {

        if ("".equals(guiRenderer.getTextfield("nameText","Company").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME1")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (guiRenderer.getComboBox("categoryText","Company").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if ("00".equals(guiRenderer.getTextfield("internalNrText","Company").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_CRM_INTERNAL_NUMMBER_EXHAUSTED")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if ("-1".equals(guiRenderer.getTextfield("internalNrText","Company").getText()) && "".equals(guiRenderer.getTextfield("custNrText","Company").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_COMPANY_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (EBIPGFactory.isSaveOrUpdate == false) {
            if (dynMethod.findCustomerNumber(guiRenderer.getTextfield("custNrText","Company").getText())) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_CUSTOMER_NUMBER_EXIST")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }

    /**
     * Save or Update a loaded company
     * @return
     */

    public boolean updateCompany() {

        try {
            ebiContainer.showInActionStatus("Company", true);
            ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            if (!EBIPGFactory.isSaveOrUpdate) {
                ebiPGFactory.company = new Company();
                RELOAD = true;
                ebiPGFactory.company.setCreatedfrom(EBIPGFactory.ebiUser);
                ebiPGFactory.company.setCreateddate(new Date());
            } else {
               createHistory(ebiPGFactory.company);
               ebiPGFactory.company.setChangeddate(new Date());
               ebiPGFactory.company.setChangedfrom(EBIPGFactory.ebiUser);

            }

            if(!"-1".equals(guiRenderer.getTextfield("internalNrText","Company").getText())){
               ebiPGFactory.company.setCompanynumber(Integer.parseInt(guiRenderer.getTextfield("internalNrText","Company").getText().replace(beginChar,"")));
            }else{
               ebiPGFactory.company.setCompanynumber(-1);
            }

            ebiPGFactory.company.setBeginchar(beginChar);
            ebiPGFactory.company.setName(guiRenderer.getTextfield("nameText","Company").getText());
            ebiPGFactory.company.setName2(guiRenderer.getTextfield("name1Text","Company").getText());

            if (guiRenderer.getComboBox("categoryText","Company").getSelectedItem() != null) {
                ebiPGFactory.company.setCategory(guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString());
                ebiPGFactory.company.setCooperation(guiRenderer.getComboBox("cooperationText","Company").getSelectedItem().toString());
            }

            ebiPGFactory.company.setPhone(guiRenderer.getTextfield("telephoneText","Company").getText());
            ebiPGFactory.company.setFax(guiRenderer.getTextfield("faxText","Company").getText());
            ebiPGFactory.company.setTaxnumber(guiRenderer.getTextfield("taxIDText","Company").getText());
            ebiPGFactory.company.setEmployee(guiRenderer.getTextfield("employeeText","Company").getText());
            ebiPGFactory.company.setWeb(guiRenderer.getTextfield("internetText","Company").getText());
            ebiPGFactory.company.setEmail(guiRenderer.getTextfield("emailText","Company").getText());
            ebiPGFactory.company.setCustomernr(guiRenderer.getTextfield("custNrText","Company").getText());

            if (guiRenderer.getComboBox("classificationText","Company").getSelectedItem() != null) {
                ebiPGFactory.company.setQualification(guiRenderer.getComboBox("classificationText","Company").getSelectedItem().toString());
            }

            ebiPGFactory.company.setIslock(guiRenderer.getCheckBox("lockCompany","Company").isSelected());

            //Save company hierarchy
            if(this.companyPane.listH.size() > 0){
                Iterator iterH = this.companyPane.listH.iterator();
                while(iterH.hasNext()){
                    Companyhirarchie hi = (Companyhirarchie) iterH.next();
                    ebiPGFactory.company.getCompanyhirarchies().add(hi);
                    ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(hi);
                }
            }

            ebiPGFactory.company.setDescription(guiRenderer.getTextarea("companyDescription","Company").getText());

            if (!EBIPGFactory.isSaveOrUpdate) {
                if (checkForMainCompany()) {
                    ebiPGFactory.company.setIsactual(true);
                } else {
                    ebiPGFactory.company.setIsactual(false);
                }
            }
            
            if(ebiPGFactory.company.getIsactual()){
            	ebiPGFactory.loadStandardCompany();
            }
            
            ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(ebiPGFactory.company);
            this.companyID = ebiPGFactory.company.getCompanyid();

            EBIPGFactory.isSaveOrUpdate = true;

            ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            ebiContainer.showInActionStatus("Company", false);

        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method check if the CRM System has a main company
     * @return
     */
    private boolean checkForMainCompany() {

        ResultSet rsSet = null;
        try {
            PreparedStatement ps = ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT ISACTUAL FROM COMPANY WHERE ISACTUAL=?");
            ps.setInt(1,1);
            rsSet = ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
            rsSet.last();
            if (rsSet.getRow() == 0) {
                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_NO_MAIN_COMPANY_WAS_FOUND_CREATE_ONE")).Show(EBIMessage.INFO_MESSAGE_YESNO)) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            if(rsSet != null){
                try {
                    rsSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Create history bevore save the loaded company
     * @param com
     */

    private void createHistory(Company com) {

        List<String> list = new ArrayList<String>();

        if (ebiPGFactory.company.getCreateddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + ebiPGFactory.getDateToString(ebiPGFactory.company.getCreateddate()));
        }
        
        if(ebiPGFactory.company.getCreatedfrom() != null){
          list.add(ebiPGFactory.company.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": "
                    : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + ebiPGFactory.company.getCreatedfrom());
        }

        if (ebiPGFactory.company.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + ebiPGFactory.getDateToString(ebiPGFactory.company.getChangeddate()));
        }

        if(ebiPGFactory.company.getChangedfrom() != null){
            list.add(ebiPGFactory.company.getChangedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": "
                    : EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + ebiPGFactory.company.getChangedfrom());
        }

        String nh = "";
        if(!String.valueOf(ebiPGFactory.company.getCompanynumber()).equals(guiRenderer.getTextfield("internalNrText","Company").getText())){
           nh = "$";
        }
        
        list.add(String.valueOf(ebiPGFactory.company.getCompanynumber()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_INTERNAL_NUMBER") + ": " + String.valueOf(-1)+nh
                : EBIPGFactory.getLANG("EBI_LANG_C_INTERNAL_NUMBER") + ": " + String.valueOf(ebiPGFactory.company.getCompanynumber())+nh);

        if(ebiPGFactory.company.getName() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_COMPANY_NAME1") + ": " + (ebiPGFactory.company.getName().equals(guiRenderer.getTextfield("nameText","Company").getText()) == true ? ebiPGFactory.company.getName() : ebiPGFactory.company.getName()+"$") );
        }
        if(ebiPGFactory.company.getName2() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_COMPANY_NAME2") + ": " + (ebiPGFactory.company.getName2().equals(guiRenderer.getTextfield("name1Text","Company").getText()) == true ? ebiPGFactory.company.getName2() : ebiPGFactory.company.getName2()+"$"));
        }
        if(ebiPGFactory.company.getCustomernr() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_COMPANY_CUSTOMER_NUMBER") + ": " + (ebiPGFactory.company.getCustomernr().equals(guiRenderer.getTextfield("custNrText","Company").getText()) == true ? ebiPGFactory.company.getCustomernr() : ebiPGFactory.company.getCustomernr()+"$"));
        }
        if(ebiPGFactory.company.getCategory() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + (ebiPGFactory.company.getCategory().equals(guiRenderer.getComboBox("categoryText","Company").getSelectedItem().toString()) == true ? ebiPGFactory.company.getCategory() : ebiPGFactory.company.getCategory()+"$") );
        }
        if(ebiPGFactory.company.getCooperation() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_COOPERATION") + ": " + (ebiPGFactory.company.getCooperation().equals(guiRenderer.getComboBox("cooperationText","Company").getSelectedItem().toString()) == true ? ebiPGFactory.company.getCooperation() : ebiPGFactory.company.getCooperation()+"$") );
        }
        if(ebiPGFactory.company.getPhone() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE") + ": " + (ebiPGFactory.company.getPhone().equals(guiRenderer.getTextfield("telephoneText","Company").getText()) == true ? ebiPGFactory.company.getPhone() : ebiPGFactory.company.getPhone()+"$") );
        }
        if(ebiPGFactory.company.getFax() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_FAX") + ": " + (ebiPGFactory.company.getFax().equals(guiRenderer.getTextfield("faxText","Company").getText()) == true ? ebiPGFactory.company.getFax() : ebiPGFactory.company.getFax()+"$") );
        }
        if(ebiPGFactory.company.getTaxnumber() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_VAT_NR") + ": " + (ebiPGFactory.company.getTaxnumber().equals(guiRenderer.getTextfield("taxIDText","Company").getText()) == true ? ebiPGFactory.company.getTaxnumber() : ebiPGFactory.company.getTaxnumber()+"$") );
        }
        if(ebiPGFactory.company.getEmployee() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_EMPLOYEE") + ": " +(ebiPGFactory.company.getEmployee().equals(guiRenderer.getTextfield("employeeText","Company").getText()) == true ? ebiPGFactory.company.getEmployee() : ebiPGFactory.company.getEmployee()+"$") );
        }
        if(ebiPGFactory.company.getWeb() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_HINTERNET") + ": " + (ebiPGFactory.company.getWeb().equals(guiRenderer.getTextfield("internetText","Company").getText()) == true ? ebiPGFactory.company.getWeb() : ebiPGFactory.company.getWeb()+"$") );
        }
        if(ebiPGFactory.company.getEmail() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_HEMAIL") + ": " + (ebiPGFactory.company.getEmail().equals(guiRenderer.getTextfield("emailText","Company").getText()) == true ? ebiPGFactory.company.getEmail() : ebiPGFactory.company.getEmail()+"$") );
        }
        if(ebiPGFactory.company.getQualification() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_KLASSIFICATION") + ": " + (ebiPGFactory.company.getQualification().equals(guiRenderer.getComboBox("classificationText","Company").getSelectedItem()) == true ? ebiPGFactory.company.getQualification() : ebiPGFactory.company.getQualification()+"$") );
        }
        if(ebiPGFactory.company.getIslock() != null){
            String chd = "";
            if(ebiPGFactory.company.getIslock() == true && !guiRenderer.getCheckBox("lockCompany","Company").isSelected() ||
                                        ebiPGFactory.company.getIslock() == false && guiRenderer.getCheckBox("lockCompany","Company").isSelected() ){
                chd = "$";
            }

            list.add(ebiPGFactory.company.getIslock() == true ? EBIPGFactory.getLANG("EBI_LANG_C_LOCK") + ": " + String.valueOf(true)+chd : EBIPGFactory.getLANG("EBI_LANG_C_LOCK") + ": " + String.valueOf(false)+chd);
        }
        if(ebiPGFactory.company.getDescription() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + (ebiPGFactory.company.getDescription().equals(guiRenderer.getTextarea("companyDescription","Company").getText()) == true ? ebiPGFactory.company.getDescription() : ebiPGFactory.company.getDescription()+"$") );
        }
        list.add("*EOR*"); // END OF RECORD
        try {
            hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com.getCompanyid(), "Company", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a loaded company
     * @return
     */

    public boolean deleteCRM() {
      boolean ret = true;
       try{
            pessimisticStruct.setModuleName("CRMRecord");
        
            if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true
                    && ebiPGFactory.company != null) {
                try {
                    ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(ebiPGFactory.company);
                    ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                resetUI(false,false);
                EBIPGFactory.canRelease = true;

            } else {
                ret = false;
            }
       }catch(Exception ex){
          ret = false;
          logger.error("Error delete record: ",ex.fillInStackTrace());
       }
        return ret;
    }

    /**
     * Create GUI and show data
     * @param compNr
     * @param reload
     * @return
     */

    public synchronized boolean createUI(int compNr,boolean reload) {
       try{
            resetUI(true,reload);

            companyID = compNr;
            EBIPGFactory.isSaveOrUpdate = true;

            crmToolBar.setDeleteButtonEnabled(true);

            // Load company data
            try {

                Query query = ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ");
                query.setInteger(0, compNr);

                Iterator it = query.iterate();
                if (it.hasNext()) {
                    ebiPGFactory.company = (Company) it.next();

                    ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(ebiPGFactory.company);
                    isExistCompany = true;
                    loadCompanyData();
                    loadContactData();
                    loadCompanyAdressData();
                    loadBankData();
                    loadCompanyMeetingProtocol();
                    loadOpportunity();
                    loadActivities();
                    loadOfferData();
                    loadOrderData();
                    loadServiceData();

                    if(ebiPGFactory.company != null){
                        if(ebiPGFactory.company.getIsactual() != null && ebiPGFactory.company.getIsactual() == false){
                            if(guiRenderer.getCheckBox("mainContactText","Contact") != null){
                                guiRenderer.getCheckBox("mainContactText","Contact").setVisible(false);
                            }
                        }else{
                           if(guiRenderer.getCheckBox("mainContactText","Contact") != null){
                             guiRenderer.getCheckBox("mainContactText","Contact").setVisible(true);
                           }
                        }
                    }

                }

                pessimisticStruct = new EBIPessimisticViewDialog(this);
                pessimisticStruct.setModuleName("CRMRecord");
                if(guiRenderer.existPackage("Company")){
                    guiRenderer.getVisualPanel("Company").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }

                if(guiRenderer.existPackage("Address")){
                    guiRenderer.getVisualPanel("Address").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Bank")){
                    guiRenderer.getVisualPanel("Bank").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("MeetingCall")){
                    guiRenderer.getVisualPanel("MeetingCall").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Activity")){
                    guiRenderer.getVisualPanel("Activity").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Opportunity")){
                    guiRenderer.getVisualPanel("Opportunity").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Offer")){
                    guiRenderer.getVisualPanel("Offer").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Order")){
                    guiRenderer.getVisualPanel("Order").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
                if(guiRenderer.existPackage("Service")){
                    guiRenderer.getVisualPanel("Service").setPessimisticViewDialog(pessimisticStruct.getPessimisticDialog());
                }
               checkIslocked(compNr,false);

               ebiPGFactory.getIEBIContainerInstance().getTreeViewInstance().
                                getModel().valueForPathChanged(ebiPGFactory.getIEBIContainerInstance().getTreeViewInstance().getPathForRow(0),ebiPGFactory.company.getName());


            } catch (org.hibernate.HibernateException ex) {
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            } catch (Exception ex) {
               ex.printStackTrace();
               EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
            }

            EBIPGFactory.canRelease = true;
       }catch(Exception ex){
           logger.error(ex.getMessage(), ex.fillInStackTrace());
       }

        return true;
    }

    /**
     * Refresh the hibernate loaded company bean
     */
    
    public void refreshCompany(){
    	try {
			ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
			ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(ebiPGFactory.company);
	    	ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }

    /**
     * Check if a loaded record is locked
     * @param compNr
     * @param showMessage
     * @throws Exception
     */

    public synchronized boolean checkIslocked(int compNr, boolean showMessage) throws Exception{
            boolean ret = false;

            ebiPGFactory.database.setAutoCommit(true);
            PreparedStatement ps =  ebiPGFactory.database.initPreparedStatement("SELECT * FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=? ");
            ps.setInt(1,compNr);
            ps.setString(2,"CRMRecord");
            ResultSet rs = ebiPGFactory.database.executePreparedQuery(ps);

            rs.last();

            if (rs.getRow() <= 0) {
                pessimisticStruct.setLockId(compNr);
                pessimisticStruct.setModuleName("CRMRecord");
                pessimisticStruct.setLockUser(EBIPGFactory.ebiUser);
                pessimisticStruct.setLockStatus(1);
                pessimisticStruct.setLockTime(new Timestamp(new Date().getTime()));
                lockCompanyRecord(compNr, "CRMRecord", pessimisticStruct.getLockTime());
                activateLockedInfo(false);
            }else{
                rs.beforeFirst();
                rs.next();
                pessimisticStruct.setLockId(rs.getInt("RECORDID"));
                pessimisticStruct.setModuleName(rs.getString("MODULENAME"));
                pessimisticStruct.setLockUser(rs.getString("USER"));
                pessimisticStruct.setLockStatus(rs.getInt("STATUS"));
                pessimisticStruct.setLockTime(rs.getTimestamp("LOCKDATE"));
                pessimisticStruct.setLockId(compNr);
                if(!pessimisticStruct.getLockUser().equals(EBIPGFactory.ebiUser)){
                    activateLockedInfo(true);
                }
                
                if(showMessage && !pessimisticStruct.equals(EBIPGFactory.ebiUser)){
                    ret = true;
                }
            }
        
             // Pessimistic Dialog view info
             guiRenderer.getLabel("userx","pessimisticViewDialog").setText(pessimisticStruct.getLockUser());
             guiRenderer.getLabel("statusx","pessimisticViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_LOCKED"));
             guiRenderer.getLabel("timex","pessimisticViewDialog").setText(pessimisticStruct.getLockTime().toString());
             ebiPGFactory.database.setAutoCommit(false);
        return ret;
    }

    /**
     * Activate Pessimistic Lock for the GUI 
     * @param enabled
     */

    public void activateLockedInfo(boolean enabled){

        //show red icon to a visual panel

            if(guiRenderer.existPackage("Company")){
               guiRenderer.getVisualPanel("Company").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Contact")){
               guiRenderer.getVisualPanel("Contact").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Address")){
               guiRenderer.getVisualPanel("Address").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Bank")){
               guiRenderer.getVisualPanel("Bank").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("MeetingCall")){
               guiRenderer.getVisualPanel("MeetingCall").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Activity")){
               guiRenderer.getVisualPanel("Activity").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Opportunity")){
               guiRenderer.getVisualPanel("Opportunity").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Offer")){
               guiRenderer.getVisualPanel("Offer").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Order")){
               guiRenderer.getVisualPanel("Order").showLockIcon(enabled);
            }
            if(guiRenderer.existPackage("Service")){
               guiRenderer.getVisualPanel("Service").showLockIcon(enabled);
            }

        //disable toolbar button
        if(guiRenderer.getToolBarButton("toolbarItemSave","ebiToolBar") != null){
            guiRenderer.getToolBarButton("toolbarItemSave","ebiToolBar").setEnabled(enabled ? false : true);
        }

        if(guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar") != null){
            guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar").setEnabled(enabled ? false : true);
        }

        //Disable crm buttons
        if(guiRenderer.existPackage("Contact")){
          guiRenderer.getButton("deleteContactAddress","Contact").setVisible(enabled ? false : true);
          guiRenderer.getButton("deleteContact","Contact").setVisible(enabled ? false : true);
          guiRenderer.getButton("saveContact","Contact").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Address")){
           guiRenderer.getButton("deleteAddress","Address").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveAddress","Address").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Bank")){
           guiRenderer.getButton("deleteBank","Bank").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveBank","Bank").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("MeetingCall")){
            guiRenderer.getButton("deleteMeetingContact","MeetingCall").setVisible(enabled ? false : true);
            guiRenderer.getButton("deleteMeetingDoc","MeetingCall").setVisible(enabled ? false : true);
            guiRenderer.getButton("deleteMeeting","MeetingCall").setVisible(enabled ? false : true);
            guiRenderer.getButton("saveMeeting","MeetingCall").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Activity")){
            guiRenderer.getButton("deleteActivityDoc","Activity").setVisible(enabled ? false : true);
            guiRenderer.getButton("deleteActivity","Activity").setVisible(enabled ? false : true);
            guiRenderer.getButton("saveActivity","Activity").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Opportunity")){
           guiRenderer.getButton("delteOppContact","Opportunity").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteOppDoc","Opportunity").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteOpportunity","Opportunity").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveOpportunity","Opportunity").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Offer")){
           guiRenderer.getButton("deleteOfferDOc","Offer").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteOfferProduct","Offer").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteOfferReceiver","Offer").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteOffer","Offer").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveOffer","Offer").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Order")){
           guiRenderer.getButton("deleteorderDoc","Order").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteorderProduct","Order").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteorderReceiver","Order").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteorder","Order").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveOrder","Order").setEnabled(enabled ? false : true);
        }
        if(guiRenderer.existPackage("Service")){
           guiRenderer.getButton("deleteServiceDoc","Service").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteServiceProduct","Service").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteServiceProsol","Service").setVisible(enabled ? false : true);
           guiRenderer.getButton("deleteService","Service").setVisible(enabled ? false : true);
           guiRenderer.getButton("saveService","Service").setEnabled(enabled ? false : true);
        }

    }

    /**
     * Lock a record
     * @param compNr
     */

    public void lockCompanyRecord(int compNr,String modType,Timestamp ltime){
        try {

            ebiPGFactory.database.setAutoCommit(true);
            if(compNr != -1){
                 PreparedStatement ps = ebiPGFactory.database.initPreparedStatement("INSERT INTO EBIPESSIMISTIC (RECORDID,MODULENAME,USER,LOCKDATE,STATUS) VALUES(?,?,?,?,?) ");
                 ps.setInt(1,compNr);
                 ps.setString(2,modType);
                 ps.setString(3,EBIPGFactory.ebiUser);
                 ps.setTimestamp(4, ltime);
                 ps.setInt(5, 1);
                 ebiPGFactory.database.executePreparedStmt(ps);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
           ebiPGFactory.database.setAutoCommit(false); 
        }
    }
    /**
     * Unlock a record
     * @param compNr
     */
    public synchronized void unlockCompanyRecord(int compNr,String luser,String module){

        if(luser.equals(EBIPGFactory.ebiUser)){
            unlockSQLStmt(compNr,module);
        }
        if(module.equals("CRMRecord")){
           activateLockedInfo(false);
        }else if(module.equals("CRMCampaign")){
           campaignPane.dataControlCampaign.activateLockedInfo(false);
        }else if(module.equals("CRMProduct")){
           productPane.dataControlProduct.activateLockedInfo(false);
        }else if(module.equals("CRMInvoice")){
           invoicePane.dataControlInvoice.activateLockedInfo(false);
        }else if(module.equals("CRMProject")){
           projectPane.dataControlProject.activateLockedInfo(false);
        }else if(module.equals("CRMProsol")){
           prosolPane.dataControlProsol.activateLockedInfo(false);
        }
    }

    public synchronized void forceUnlock(int compNr,String module){
       try {
            boolean pass;
            if (ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                pass = true;
            } else {
                pass = ebiPGFactory.getIEBISecurityInstance().secureModule();
            }
            if (pass) {
                if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_ARE_SURE_TO_FORCE_UNCLOCK")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true){
                    unlockSQLStmt(compNr,module);
                    if(module.equals("CRMRecord")){
                        createUI(compNr,true);
                    }else if(module.equals("CRMCampaign")){
                       campaignPane.dataControlCampaign.dataEdit(compNr);
                    }else if(module.equals("CRMProduct")){
                       productPane.dataControlProduct.dataEdit(compNr);
                    }else if(module.equals("CRMInvoice")){
                       invoicePane.dataControlInvoice.dataEdit(compNr);
                    }else if(module.equals("CRMProject")){
                       projectPane.dataControlProject.dataEdit(compNr);
                    }else if(module.equals("CRMProsol")){
                       prosolPane.dataControlProsol.dataEdit(compNr);
                    }
                }
            }
       }catch (Exception e) {
            e.printStackTrace();
       }
    }

    /**
     * Unlock SQL Statement used to remove a lock record
     * @param compNr
     * @param module
     * @throws SQLException
     */

    public synchronized void unlockSQLStmt(int compNr, String module){
        try{
            ebiPGFactory.database.setAutoCommit(true);

            PreparedStatement ps = ebiPGFactory.database.initPreparedStatement("DELETE FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=?  ");
            ps.setInt(1,compNr);
            ps.setString(2, module);
            ebiPGFactory.database.executePreparedStmt(ps);

        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
           ebiPGFactory.database.setAutoCommit(false); 
        }
    }

    /**
     * Load a company in the gui
     */

    public void loadCompanyData() {

        if(!guiRenderer.existPackage("Company") || companyID == -1){
            return;
        }

        guiRenderer.getVisualPanel("Company").setID(companyID);
        
        if (ebiPGFactory.company.getCreateddate() != null) {
            guiRenderer.getVisualPanel("Company").setCreatedDate(ebiPGFactory.getDateToString(ebiPGFactory.company.getCreateddate()));
            guiRenderer.getVisualPanel("Company").setCreatedFrom(ebiPGFactory.company.getCreatedfrom());
        }

        if (ebiPGFactory.company.getChangeddate() != null) {
            guiRenderer.getVisualPanel("Company").setChangedDate(ebiPGFactory.getDateToString(ebiPGFactory.company.getChangeddate()));
            guiRenderer.getVisualPanel("Company").setChangedFrom(ebiPGFactory.company.getChangedfrom());
        }
        
        beginChar = ebiPGFactory.company.getBeginchar();
        guiRenderer.getTextfield("rootText","Company").requestFocus();
        guiRenderer.getTextfield("internalNrText","Company").setText(String.valueOf(ebiPGFactory.company.getCompanynumber() == null ? -1 : ebiPGFactory.company.getCompanynumber()));
        guiRenderer.getTextfield("nameText","Company").setText(ebiPGFactory.company.getName());
        guiRenderer.getTextfield("name1Text","Company").setText(ebiPGFactory.company.getName2());

        guiRenderer.getTextfield("custNrText","Company").setText(ebiPGFactory.company.getCustomernr());

        if (ebiPGFactory.company.getCategory() != null) {
            guiRenderer.getComboBox("categoryText","Company").setSelectedItem(ebiPGFactory.company.getCategory());
        }
        
        if (ebiPGFactory.company.getCooperation() != null) {
            guiRenderer.getComboBox("cooperationText","Company").setSelectedItem(ebiPGFactory.company.getCooperation());
        }

        guiRenderer.getTextfield("telephoneText","Company").setText(ebiPGFactory.company.getPhone());
        guiRenderer.getTextfield("faxText","Company").setText(ebiPGFactory.company.getFax());

        guiRenderer.getTextfield("taxIDText","Company").setText(ebiPGFactory.company.getTaxnumber());
        guiRenderer.getTextfield("employeeText","Company").setText(ebiPGFactory.company.getEmployee());
        guiRenderer.getTextfield("internetText","Company").setText(ebiPGFactory.company.getWeb());

        guiRenderer.getTextfield("emailText","Company").setText(ebiPGFactory.company.getEmail());
        guiRenderer.getComboBox("classificationText","Company").setSelectedItem(ebiPGFactory.company.getQualification());

        if (ebiPGFactory.company.getIslock() != null) {
            guiRenderer.getCheckBox("lockCompany","Company").setSelected(ebiPGFactory.company.getIslock());
        }
        guiRenderer.getTextarea("companyDescription","Company").setText(ebiPGFactory.company.getDescription());
        getContactPane().contactDataControl.dataShow();
        getAddressPane().addressDataControl.dataShow();
        loadHierarchie();
    }

    public void loadHierarchie() {
        this.companyPane.listH = ebiPGFactory.company.getCompanyhirarchies();
        this.companyPane.showHierarchies();
    }

    public void loadContactData() {
        if(!guiRenderer.existPackage("Contact") || companyID == -1){

            return;
        }
        this.contactPane.contactDataControl.dataNew();
        guiRenderer.getComboBox("genderTex","Contact").grabFocus();
    }

    public void loadCompanyAdressData() {
        if(!guiRenderer.existPackage("Address") || companyID == -1){
            return;
        }
        addressPane.addressDataControl.dataNew();
        guiRenderer.getComboBox("addressTypeText","Address").setRequestFocusEnabled(true);
        guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
    }

    public void loadCompanyMeetingProtocol() {
        if(!guiRenderer.existPackage("MeetingCall") || companyID == -1){
            return;
        }
        meetingReport.meetingDataControl.dataNew();
        guiRenderer.getTextfield("subjectMeetingText","MeetingCall").grabFocus();
    }

    public void loadOpportunity() {
        if(!guiRenderer.existPackage("Opportunity") || companyID == -1){
            return;
        }
        opportunityPane.opportuniyDataControl.dataNew();
        guiRenderer.getComboBox("opportunityNameText","Opportunity").grabFocus();
    }

    public void loadActivities() {
        if(!guiRenderer.existPackage("Activity") || companyID == -1){
            return;
        }
        activitiesPane.activityDataControl.dataNew();
        guiRenderer.getTextfield("activityNameText","Activity").grabFocus();
    }

    public void loadBankData() {
        if(!guiRenderer.existPackage("Bank") || companyID == -1){
            return;
        }
        bankPane.bankDataControl.dataNew();
        guiRenderer.getTextfield("bankNameText","Bank").requestFocus();
    }

    public void loadOfferData() {
        if(!guiRenderer.existPackage("Offer") || companyID == -1){
            return;
        }
        offerPane.offerDataControl.dataNew(true);
        guiRenderer.getTextfield("offerNrText","Offer").requestFocus();
    }

    public void loadOrderData() {
        if(!guiRenderer.existPackage("Order") || companyID == -1){
            return;
        }
        orderPane.orderDataControl.dataNew(true);
        guiRenderer.getTextfield("orderNrText","Order").requestFocus();
    }

    public void loadServiceData() {
        if(!guiRenderer.existPackage("Service") || companyID == -1){
            return;
        }
        servicePane.serviceDataControl.dataNew(true);
        guiRenderer.getTextfield("serviceNrText","Service").requestFocus();
    }


    public void newRecord(){

        String modName = ebiPGFactory.getMainFrame().mng.getSelectedModName();
        if(!"".equals(modName) && !"".equals("Summary")){
           ebiPGFactory.getDataStore(modName,"ebiNew",false);
        }

    }

    public void saveRecord(){
        String modName = ebiPGFactory.getMainFrame().mng.getSelectedModName();
        if(!"".equals(modName) && !"".equals("Summary")){
            ebiPGFactory.getDataStore(modName,"ebiSave",false);
        }
    }

}