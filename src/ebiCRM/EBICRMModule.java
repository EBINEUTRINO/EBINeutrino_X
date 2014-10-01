package ebiCRM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

import ebiCRM.functionality.EBICRMDynamicFunctionalityMethods;
import ebiCRM.gui.component.EBICRMTabcontrol;
import ebiCRM.gui.component.EBICRMToolBar;
import ebiCRM.gui.dialogs.EBIPessimisticViewDialog;
import ebiCRM.gui.panels.EBICRMAccountStack;
import ebiCRM.gui.panels.EBICRMInvoice;
import ebiCRM.gui.panels.EBICRMLead;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.utils.EBICRMHistoryCreator;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIExtension;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;


public class EBICRMModule implements IEBIExtension {

    public String beginChar = "";
    private EBICRMLead leadsPane = null;
    private EBICRMAccountStack accountPane =null;
    private EBICRMProduct productPane = null;
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
    public static String classification[]=null;
    public static String categories[]=null;
    public static String cooperations[]=null;
    public static String gendersList[]=new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                                        EBIPGFactory.getLANG("EBI_LANG_C_MALE"),EBIPGFactory.getLANG("EBI_LANG_C_MALE"),EBIPGFactory.getLANG("EBI_LANG_C_FEMALE")};



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

            if(obj == null){ // Load Dashboard as default
                ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Leads");
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
        accountPane = null;
        leadsPane = null;
        productPane = null;
        invoicePane = null;
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

    }

    public EBICRMLead getLeadPane() {
        if (leadsPane == null) {
            leadsPane = new EBICRMLead(this);
            ebiPGFactory.setDataStore("Leads",leadsPane);
        }
        return leadsPane;
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
        }else if(module.equals("CRMProduct")){
           productPane.dataControlProduct.activateLockedInfo(false);
        }else if(module.equals("CRMInvoice")){
           invoicePane.dataControlInvoice.activateLockedInfo(false);
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
                    if(module.equals("CRMProduct")){
                       productPane.dataControlProduct.dataEdit(compNr);
                    }else if(module.equals("CRMInvoice")){
                       invoicePane.dataControlInvoice.dataEdit(compNr);
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


}