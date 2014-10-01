package ebiNeutrinoSDK;

import java.awt.*;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import org.apache.log4j.Logger;
import org.hibernate.Query;

import ebiNeutrino.core.EBIDBConnection;
import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.gui.component.EBIExtensionContainer;
import ebiNeutrino.core.gui.component.EBIToolbar;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIContainer;
import ebiNeutrinoSDK.interfaces.IEBIDatabase;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.interfaces.IEBIModule;
import ebiNeutrinoSDK.interfaces.IEBIReportSystem;
import ebiNeutrinoSDK.interfaces.IEBISecurity;
import ebiNeutrinoSDK.interfaces.IEBISystemUserRights;
import ebiNeutrinoSDK.interfaces.IEBIToolBar;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Ebiuser;
import ebiNeutrinoSDK.utils.EBIPessimisticLocking;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;
import ebiNeutrinoSDK.utils.Encrypter;
import ebiNeutrinoSDK.workflow.security.EBISecurityManagement;
import ebiNeutrinoSDK.workflow.security.EBISystemUserRights;
import groovy.lang.Script;

import javax.swing.*;

/**
 * Access functionality like Database,Reporting, GUI
 */
public class EBIPGFactory {

    public static GregorianCalendar calendar = null;
    public static String ebiUser = null;
    public static boolean USE_ASB2C = false;
    public static String[] systemUsers = null;
    public static String host = "";
    public static String updateServer = "";
    public static String lastLoggedUser = "";
    public static String DateFormat = "";
    public static String DATABASE_SYSTEM ="";
    public static String selectedLanguage = "";
    public EBIHibernateSessionPooling hibernate = null;
    public static final EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
    public Company company = null;
    public static Logger logger = Logger.getLogger(EBIPGFactory.class.getName());
    private static EBIPropertiesRW Language = null;
    public static boolean canRelease = true;
    public static boolean isSaveOrUpdate = false;
    public EBISecurityManagement security = null;
    public IEBIToolBar toolbar = null;
    public IEBIContainer container = null;
    public IEBIDatabase database = null;
    public IEBIReportSystem report = null;
    public IEBIGUIRenderer gui = null;
    public IEBIModule ebiModule = null;
    private EBISystemUserRights userRights = null;
    public IEBISystemUserRights iuserRights = null;
    public EBIExceptionDialog message = null;
    public EBIPessimisticLocking plock = null;
    private Hashtable<String,Object> storableFactory = null;
    public Hashtable<String,Object> globalVariable = null;
    public static List moduleToView = new ArrayList<String>();
    private EBIMain mainFrame = null;
    public HashMap<String,Object> map = new HashMap<String,Object>();
    public JFileChooser fileDialog = new JFileChooser();
    public Calendar systemStartCal = null;
    public Calendar systemEndCal = null;
    public static java.awt.Color systemColor = new Color(180,180,180);


    public EBIPGFactory() {
        calendar = new GregorianCalendar();
        plock = new EBIPessimisticLocking(this);
        storableFactory = new Hashtable<String,Object>();
        message = EBIExceptionDialog.getInstance();
        globalVariable = new Hashtable<String, Object>();

    }



    protected void getLanguageInstance(String langFile,boolean reload) {
        // Read properties file.
        parseLanguageFrom("./"+langFile);
        Language = EBIPropertiesRW.getPropertiesInstance(new File("./"+langFile),reload,false);
    }

    /**
     * Reload EBI Neutrino translation system 
     *
     */
    public void reloadTranslationSystem() {
        EBIPropertiesRW prop = EBIPropertiesRW.getPropertiesInstance();
        getLanguageInstance("./"+prop.getValue("EBI_Neutrino_Language_File"),true);
    }

    private void parseLanguageFrom(String name) {
        try {
            String lName;
            if ((lName = name.substring(name.lastIndexOf("_") + 1)) != null) {
                if (!"".equals(lName) && lName != null) {
                    if ((lName = lName.substring(0, lName.lastIndexOf("."))) != null) {
                        if (!"".equals(lName)) {
                            if ("English".equals(lName)) {
                                selectedLanguage = "English";
                            } else if ("Deutsch".equals(lName)) {
                                selectedLanguage = "German";
                            } else if ("Italiano".equals(lName)) {
                                selectedLanguage = "Italian";
                            }else if ("Espanol".equals(lName)) {
                                selectedLanguage = "Spanish";
                            }else if ("PortuguesBrasil".equals(lName)) {
                                selectedLanguage = "Portuguese";
                            }
                        }
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }


    public Properties getPropertiesLanguage(){
        return Language.getProperties();
    }

    /**
     * Return a selected language word as String 
     * @param key
     * @return
     */
    public static String getLANG(String key) {
        String val;
        try {
            if ("".equals(val = Language.getValue(key))) {
                Language.setValue(key, key);
                Language.saveProperties();
                val = key;
            }
        } catch (NullPointerException ex) {
            Language.setValue(key, key);
            Language.saveProperties();
            return key;
        }
        return val;
    }

    /**
     * Format date to String
     * @param source
     * @return
     */
    public String getDateToString(Date source) {
        String format;
        if (source != null) {
            DateFormat df = new SimpleDateFormat(DateFormat);
            format = df.format(source);
        } else {
            format = "";
        }
        return format;

    }

    /**
     * Format String to java.util.Date
     * @param source
     * @return if source null return actual as type java.util.date
     */
    public java.util.Date getStringToDate(String source) {
        Date date = new java.util.Date();
        if (source != null && !"".equals(source)) {
            DateFormat df = new SimpleDateFormat(DateFormat);

            try {
                date = df.parse(source);
            } catch (ParseException ex) {
                EBIExceptionDialog.getInstance("Date format Error\n "+EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }
        }
        return date;
    }

    public void updateSystemYears(){
        if(!"null".equals(properties.getValue("SELECTED_SYSTEMYEAR_TEXT"))
                && !"".equals(properties.getValue("SELECTED_SYSTEMYEAR_TEXT"))){

            systemStartCal = new GregorianCalendar();
            systemStartCal.set(Calendar.DAY_OF_MONTH,1);
            systemStartCal.set(Calendar.MONTH,Calendar.JANUARY);
            systemStartCal.set(Calendar.YEAR,Integer.parseInt(properties.getValue("SELECTED_SYSTEMYEAR_TEXT")));

            systemEndCal = new GregorianCalendar();
            systemEndCal.set(Calendar.DAY_OF_MONTH,31);
            systemEndCal.set(Calendar.MONTH,Calendar.DECEMBER);
            systemEndCal.set(Calendar.YEAR,Integer.parseInt(properties.getValue("SELECTED_SYSTEMYEAR_TEXT")));

        }else{
            systemStartCal = new GregorianCalendar();
            systemStartCal.set(Calendar.DAY_OF_MONTH,1);
            systemStartCal.set(Calendar.MONTH,Calendar.JANUARY);
            systemStartCal.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));

            systemEndCal = new GregorianCalendar();
            systemEndCal.set(Calendar.DAY_OF_MONTH,31);
            systemEndCal.set(Calendar.MONTH,Calendar.DECEMBER);
            systemEndCal.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));
        }
    }

    /**
     * This Function check for valid user on Database database
     * @param user
     * @param pw
     * @return boolean
     */
    public boolean checkIsValidUser(String user, String pw) {

        try {
            hibernate.openHibernateSession("LOGINSESSION");
            moduleToView.clear();
            hibernate.getHibernateTransaction("LOGINSESSION").begin();

            Query query = hibernate.getHibernateSession("LOGINSESSION").createQuery("from Ebiuser user where user.ebiuser=?").setString(0, user);

            if (query.list().size() > 0) {
                Iterator it = query.iterate();
                Ebiuser User = (Ebiuser) it.next();
                hibernate.getHibernateSession("LOGINSESSION").refresh(User);
                //check password
                String pass = this.decryptPassword(User.getPasswd() == null ? "" : User.getPasswd());
                if (!pw.equals(pass)) {
                    return false;
                }

                ebiUser = User.getEbiuser();
                userRights = new EBISystemUserRights();
                userRights.setUserName(ebiUser);
                userRights.setCanDelete(User.getCandelete());
                userRights.setCanSave(User.getCansave());
                userRights.setCanPrint(User.getCanprint());
                userRights.setAdministrator(User.getIsAdmin());
                iuserRights = userRights;
                String [] path = new String[]{  "Summary/summaryGUI.xml",
                                        "Leads/leadsGUI.xml",
                                        "Company/companyGUI.xml",
                                        "Contact/contactGUI.xml",
                                        "Address/addressGUI.xml",
                                        "Bank/bankGUI.xml",
                                        "MeetingsCallManagement/meetingcallGUI.xml",
                                        "Activity/activityGUI.xml",
                                        "Opportunity/opportunityGUI.xml",
                                        "Offer/offerGUI.xml",
                                        "Order/orderGUI.xml",
                                        "CRMService/serviceGUI.xml",
                                        "Product/productGUI.xml",
                                        "Calendar",
                                        "Campaign/campaignGUI.xml",
                                        "CRMProblemSolution/problemSolutionGUI.xml",
                                        "Invoice/invoiceGUI.xml",
                                        "AccountStack/accountGUI.xml",
                                        "cashRegister/cashRegisterGUI.xml",
                                        "Project/projectGUI.xml"
                                        };

                if(User.getModuleid() != null){
                    String[] pt = User.getModuleid().split("_");
                    for(int i=0; i< pt.length; i++){
                        moduleToView.add(path[Integer.parseInt(pt[i])]);
                    }
                }
                moduleToView.add("CRMToolbar/crmToolbar.xml");
                moduleToView.add("Calendar");
                moduleToView.add("CRMDialog/exportDataDialog.xml");
                moduleToView.add("CRMDialog/crmCompanySearch.xml");
                moduleToView.add("CRMDialog/accountShowPTAX.xml");
                moduleToView.add("CRMDialog/addNewContactDialog.xml");
                moduleToView.add("CRMDialog/addnewReceiverDialogCampaign.xml");
                moduleToView.add("CRMDialog/autoIncNrDialog.xml");
                moduleToView.add("CRMDialog/costValueDialog.xml");
                moduleToView.add("CRMDialog/creditDebitDialog.xml");
                moduleToView.add("CRMDialog/crmCalendarGoogleSync.xml");
                moduleToView.add("CRMDialog/crmContactSearch.xml");
                moduleToView.add("CRMDialog/crmHistoryDialog.xml");
                moduleToView.add("CRMDialog/crmSelectionDialog.xml");
                moduleToView.add("CRMDialog/crmSettingDialog.xml");
                moduleToView.add("CRMDialog/importDataDialog.xml");
                moduleToView.add("CRMDialog/newProjectTaskDialog.xml");
                moduleToView.add("CRMDialog/productInsertDialog.xml");
                moduleToView.add("CRMDialog/productSearchDialog.xml");
                moduleToView.add("CRMDialog/propertiesDialog.xml");
                moduleToView.add("CRMDialog/sendMailDialogGUI.xml");
                moduleToView.add("CRMDialog/taxAdminDialog.xml");
                moduleToView.add("CRMDialog/valueSetDialog.xml");
                moduleToView.add("CRMDialog/csvSetImport.xml");
                moduleToView.add("CRMDialog/insertCSVDataDialog.xml");
                moduleToView.add("CRMDialog/timerDialog.xml");
                moduleToView.add("CRMDialog/crmPessimisticView.xml");
                moduleToView.add("CRMDialog/printSetup.xml");
               
            } else {
                return false;
            }
            hibernate.getHibernateTransaction("LOGINSESSION").commit();
            hibernate.removeHibernateSession("LOGINSESSION");

            loadStandardCompany();
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return true;
    }

    /**
     * Generate EBI Neutirno user password
     * @param password
     * @return generated password as a string 
     */
    public String encryptPassword(String password) {
        Encrypter encrypter = new Encrypter("EBINeutrino");

        // Encrypt
        String pwd = encrypter.encrypt(password);
        encrypter = null;
        return pwd;
    }

    /**
     * Decrypt EBI Neutirno user password
     * @param password
     * @return generated password as a string 
     */
    public String decryptPassword(String password) {
        Encrypter encrypter = new Encrypter("EBINeutrino");

        // Encrypt
        String pwd = encrypter.decrypt(password);
        encrypter = null;
        return pwd;
    }

    /**
     * Open report file
     * @param filename
     */
    public void openPDFReportFile(String filename) {
        try {

            EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

            String pdf_path = properties.getValue("EBI_Neutrino_PDF");

            filename = System.getProperty("user.dir")+"/"+filename;

            System.out.println(filename);
            if (isUnix()) {
                Runtime.getRuntime().exec(pdf_path + " " + filename);
            } else if(isMac()) {
                if("".equals(pdf_path)){
                    Runtime.getRuntime().exec("open " + filename);
                }else{
                    Runtime.getRuntime().exec(pdf_path + " " + filename);
                }
            }else{
                Runtime.getRuntime().exec("cmd /C start " + pdf_path + " " + filename);
            }

        } catch (Exception ex) {
            EBIExceptionDialog.getInstance(getLANG("EBI_LANG_ERROR_PDF_PROG_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
        }
    }

    /**
     * Open a Text reader program specified in the ebiNeutrino.properties  
     *  
     * @param fileName
     */
    public void openTextDocumentFile(String filename) {

        try {

            EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
            String path_office = properties.getValue("EBI_Neutrino_TextEditor_Path");

            filename = System.getProperty("user.dir")+"/"+filename;

            System.out.println(filename);
            if (isUnix()) {
                if("".equals(path_office)) {
                    Runtime.getRuntime().exec("ooffice -writer " + filename);
                }else{
                    Runtime.getRuntime().exec(path_office + " " + filename);
                }
            } else if(isMac()) {
                if("".equals(path_office)){
                    Runtime.getRuntime().exec("open " + filename);
                }else{
                    Runtime.getRuntime().exec(path_office + " " + filename);
                }
            }else{
                if("".equals(path_office)) {
                    Runtime.getRuntime().exec("ooffice -writer " + filename);
                }else{
                    Runtime.getRuntime().exec("cmd /C start ooffice.exe -writer " + filename);
                }
            }

        } catch (Exception ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex) + getLANG("EBI_LANG_ERROR_TXT_PROG_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
        }

    }


    /**
     * return the IEBISystemUserRights Interface
     */
    public IEBISystemUserRights getIEBISystemUserRights() {
        return iuserRights == null ? userRights : iuserRights;
    }

    public void setIEBISystemUserRights(EBISystemUserRights right){
         this.userRights =right;
    }

    /**
     * return the IEBIDatabase Interface
     */
    public IEBIDatabase getIEBIDatabase() {
        return database;
    }

    /**
     * return the IEBIDatabase Interface
     * @param obj
     */
    public void setIEBIDatabase(Object obj) {
        this.database = (EBIDBConnection) obj;
    }

    /**
     * return the IEBISecurity interface
     * @return Initialize the Interface IEBISecurity
     */
    public IEBISecurity getIEBISecurityInstance() {
        if (security == null) {
            security = new EBISecurityManagement(this);
        }
        return security;
    }

    /**
     * return the default IEBIToolBar instance
     * @return IEBIToolBar Interface instance
     */
    public IEBIToolBar getIEBIToolBarInstance() {
        return this.toolbar;
    }

    /**
     * Set the system default EBIToolbar 
     * @param obj
     */
    public void setIEBIToolBarInstance(Object obj) {
        this.toolbar = (EBIToolbar) obj;
    }

    /**
     * Set the system default EBIExtensionContainer 
     * @param obj
     */
    public void setIEBIContainerInstance(Object obj) {
        this.container = (EBIExtensionContainer) obj;
    }

    /**
     * return the default IEBIContainer instance
     * @return IEBIContainer Interface instance
     */
    public IEBIContainer getIEBIContainerInstance() {
        return this.container;
    }

    /**
     * return the default IEBIReportSystem instance
     * @return IEBIContainer Interface instance
     */
    public IEBIReportSystem getIEBIReportSystemInstance() {
        return this.report;
    }

    /**
     * return the default IEBIGUIRenderer Instance
     * @return
     */
    public IEBIGUIRenderer getIEBIGUIRendererInstance() {
        return this.gui;
    }

    public void setIEBIGUIRendererInstance(Object renderer) {
        this.gui = (IEBIGUIRenderer) renderer;
    }

    /**
     * Set the system default ReportSystem Interface
     * @param obj
     */
    public void setIEBIReportSystemInstance(Object obj) {
        this.report = (IEBIReportSystem) obj;
    }

    public void setIEBIModule(Object ebiModule) {
        this.ebiModule = (IEBIModule)ebiModule;
    }

    public IEBIModule getIEBIModule() {
        return ebiModule;
    }

    public void fillComboWithUser(){

       PreparedStatement ps1 = getIEBIDatabase().initPreparedStatement("SELECT EBIUSER FROM EBIUSER ");
       ResultSet resultSet = getIEBIDatabase().executePreparedQuery(ps1);

        if(resultSet != null){

            try {
                resultSet.last();
                int size = resultSet.getRow();

                if(size > 0){
                    systemUsers = new String[size+1];
                    systemUsers[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"); 
                    resultSet.beforeFirst();
                    int i = 1;
                    while(resultSet.next()){
                        systemUsers[i++]=resultSet.getString("EBIUSER");
                    }
                }
            } catch (SQLException ex) {
                   EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }finally {
                try {
                    resultSet.close();
                } catch (SQLException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                }
            }
        }
    }

    public void setDataStore(String packageName, Script storeAdapter){
       storableFactory.put(packageName, storeAdapter);
    }

    public void setDataStore(String packageName, Object storeAdapter){
        storableFactory.put(packageName, storeAdapter);
    }

    public void getDataStore(String packageName,String method,boolean script){
      if(storableFactory.get(packageName) != null){

          Object obj = storableFactory.get(packageName);
           if(script && obj instanceof  Script &&
                                ((Script)obj).getMetaClass()
                                                        .getMetaMethod(method,null) != null){

               ((Script)obj).invokeMethod(method,null);

           }
          try{
               if(!script &&  !(obj instanceof Script) &&
                       obj.getClass().getMethod(method,null) != null){

                   obj.getClass().getMethod(method,null).invoke(obj);

               }
          }catch(NoSuchMethodException e1){ }
           catch(IllegalAccessException e1){}
           catch(java.lang.reflect.InvocationTargetException e1){}

      }
    }

    public void resetDataStore(){
        storableFactory.clear();
    }


    /**
     * Exception to string
     * @param ex
     * @return
     */
    public static String printStackTrace(Exception ex) {
        StringBuffer buildTrace = new StringBuffer();
        StackTraceElement[] element = ex.getStackTrace();

        buildTrace.append("" + ex.getMessage() + "\n");
        buildTrace.append(" Cause :" + ex.getCause() +"\n");

        logger.error(buildTrace.toString(),ex.fillInStackTrace());
        logger.info(buildTrace.toString());
        return buildTrace.toString();
    }


    public static boolean isWindows(){

        String os = System.getProperty("os.name").toLowerCase();
        //windows
        return (os.indexOf( "win" ) >= 0);

    }

    public static boolean isMac(){

        String os = System.getProperty("os.name").toLowerCase();
        //Mac
        return (os.indexOf( "mac" ) >= 0);

    }

    public static boolean isUnix(){

        String os = System.getProperty("os.name").toLowerCase();
        //linux or unix
        return (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0);

    }


    public String convertReportCategoryToIndex(String category){
       String index ="0";

       if(EBIPGFactory.getLANG("EBI_LANG_PRINT_VIEWS").equals(category)){
          index = "0";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES").equals(category)){
          index = "1";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PRINT_DELIVERIES").equals(category)){
          index = "2";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PRINT_STATISTIC").equals(category)){
          index = "3";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL").equals(category)){
          index = "4";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_OFFER").equals(category)){
          index = "5";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_ORDER").equals(category)){
          index = "6";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY").equals(category)){
          index = "7";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_CAMPAIGN").equals(category)){
          index = "8";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_PRODUCT").equals(category)){
          index = "9";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_PROSOL").equals(category)){
          index = "10";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PROJECT").equals(category)){
          index = "11";
       }else if(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE").equals(category)){
          index = "12";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PRINT_ACCOUNT").equals(category)){
          index = "13";
       }else if(EBIPGFactory.getLANG("EBI_LANG_PRINT_OTHERS").equals(category)){
          index = "14";
       }

       return index;
    }

    public void addMainFrame(EBIMain frm){
        this.mainFrame  = frm;
    }

    public EBIMain getMainFrame(){
        return this.mainFrame;
    }
    
    public void loadStandardCompany() {
        
        ResultSet set = null;
        ResultSet set1 = null;
        
        try {

            PreparedStatement ps1 = getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANY com " +
                    "LEFT JOIN COMPANYBANK bnk ON com.COMPANYID=bnk.COMPANYID WHERE com.ISACTUAL=? ");
    
            ps1.setInt(1,1);

            set = getIEBIDatabase().executePreparedQuery(ps1);

                set.last();
                if (set.getRow() > 0) {
                    set.beforeFirst();
                    set.next();
                    map.put("COMPANY_NAME", set.getString("NAME"));
                    map.put("COMPANY_NAME1", set.getString("NAME2"));
                    map.put("COMPANY_TELEPHONE", set.getString("PHONE"));
                    map.put("COMPANY_FAX", set.getString("FAX"));
                    map.put("COMPANY_EMAIL", set.getString("EMAIL"));
                    map.put("COMPANY_WEB", set.getString("WEB"));


                    map.put("COMPANY_BANK_NAME", set.getString("BANKNAME"));
                    map.put("COMPANY_BANK_ACCOUNT_NR", set.getString("BANKACCOUNT"));
                    map.put("COMPANY_BANK_BSB", set.getString("BANKBSB"));
                    map.put("COMPANY_BANK_BIC", set.getString("BANKBIC"));
                    map.put("COMPANY_BANK_IBAN", set.getString("BANKIBAN"));
                    map.put("COMPANY_BANK_COUNTRY", set.getString("BANKCOUNTRY"));
                    map.put("COMPANY_TAX_INFORMATION", set.getString("TAXNUMBER"));
                    int companyID = set.getInt("COMPANYID");
                    set.close();

                    PreparedStatement ps2 = getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYCONTACTS con " +
                            " LEFT JOIN COMPANYCONTACTADDRESS cadr ON con.CONTACTID=cadr.CONTACTID  WHERE con.COMPANYID=? AND con.MAINCONTACT=? ");
                    ps2.setInt(1,companyID);
                    ps2.setInt(2,1);
                    set1 = getIEBIDatabase().executePreparedQuery(ps2);

                    set1.last();
                    if (set1.getRow() > 0) {
                        set1.beforeFirst();
                        set1.next();
                        map.put("COMPANY_CONTACT_NAME", set1.getString("NAME") == null ? "" : set1.getString("NAME"));
                        map.put("COMPANY_CONTACT_SURNAME", set1.getString("SURNAME") == null ? "" : set1.getString("SURNAME"));
                        map.put("COMPANY_CONTACT_POSITION", set1.getString("POSITION") == null ? "" : set1.getString("POSITION"));
                        map.put("COMPANY_CONTACT_EMAIL", set1.getString("EMAIL") == null ? "" : set1.getString("EMAIL"));
                        map.put("COMPANY_CONTACT_TELEPHONE", set1.getString("PHONE") == null ? "" : set1.getString("PHONE"));
                        map.put("COMPANY_CONTACT_FAX", set1.getString("FAX") == null ? "" : set1.getString("FAX"));
                        map.put("COMPANY_STR_NR", set1.getString("STREET") == null ? "" : set1.getString("STREET"));
                        map.put("COMPANY_ZIP", set1.getString("ZIP") == null ? "" : set1.getString("ZIP"));
                        map.put("COMPANY_LOCATION", set1.getString("LOCATION") == null ? "" : set1.getString("LOCATION"));
                    }
                }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
            	if(set != null){
            		set.close();
            	}
                if (set1 != null) {
                    set1.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    public String convertReportIndexToCategory(int index){
        
       String category = EBIPGFactory.getLANG("EBI_LANG_PRINT_VIEWS");

        if(index == 0){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_VIEWS");
        }else if(index == 1){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES");
        }else if(index == 2){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_DELIVERIES");
        }else if(index == 3){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_STATISTIC");
        }else if(index == 4){
          category = EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL");
        }else if(index == 5){
          category = EBIPGFactory.getLANG("EBI_LANG_C_OFFER");
        }else if(index == 6){
          category = EBIPGFactory.getLANG("EBI_LANG_C_ORDER");
        }else if(index == 7){
          category = EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY");
        }else if(index == 8){
          category = EBIPGFactory.getLANG("EBI_LANG_C_CAMPAIGN");
        }else if(index == 9){
          category = EBIPGFactory.getLANG("EBI_LANG_C_PRODUCT");
        }else if(index == 10){
          category = EBIPGFactory.getLANG("EBI_LANG_C_PROSOL");
        }else if(index == 11){
          category = EBIPGFactory.getLANG("EBI_LANG_PROJECT");
        }else if(index == 12){
          category = EBIPGFactory.getLANG("EBI_LANG_C_SERVICE");
        }else if(index == 13){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_ACCOUNT");
        }else if(index == 14){
          category = EBIPGFactory.getLANG("EBI_LANG_PRINT_OTHERS");
        }

      return category;
    }

    
    public File getOpenDialog(int type){
         fileDialog.setFileSelectionMode(type);

         File fs = null;
         if(fileDialog.showOpenDialog(getMainFrame()) == 0){
             fs = fileDialog.getSelectedFile();
         }

        return fs;
    }

    public void setFileDialogCurrentPath(File file){
        fileDialog.setCurrentDirectory(file);
    }

    public File getSaveDialog(int type){
        fileDialog.setFileSelectionMode(type);

        File fs = null;
        if(fileDialog.showSaveDialog(getMainFrame()) == 0){
            fs  = fileDialog.getSelectedFile();
        }

        return fs;
    }
    
    
    public void sendEMail(String to, String cc, String subject, String body, String attachFile){

        try {
           getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
           if("mozilla thunderbird".equals(properties.getValue("EBI_Neutrino_Email_Client").toLowerCase())) {

              if(isMac() || isUnix()){
                   ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", "thunderbird -compose " +
                           "\"to='"+to+"'," +
                           "cc='"+cc+"'," +
                           "subject='"+subject+"'," +
                           "body='"+body+"'," +
                           "attachment='file://"+System.getProperty("user.dir")+"/"+attachFile+"'\"");
                   pb.start();
              }else  if(isWindows()){
                  ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "thunderbird -compose " +
                           "\"to='"+to+"'," +
                           "cc='"+cc+"'," +
                           "subject='"+subject+"'," +
                           "body='"+body+"'," +
                           "attachment='file://"+System.getProperty("user.dir")+"/"+attachFile+"'\"");
                  pb.start();
              }

           }else if(isWindows() && "microsoft outlook".equals(properties.getValue("EBI_Neutrino_Email_Client").toLowerCase())){

                   ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "outlook.exe ipm.note " +
                           "\"/m '"+to+"'," +
                           " /c '"+cc+"'," +
                           " /s '"+subject+"'," +
                           "/b '"+body+"'," +
                           " /a '"+System.getProperty("user.dir")+"\\"+attachFile+"'\"");
                   pb.start();


           }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    public void resolverType(String fileName, String type) {
       try{
            getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
            type = type.toLowerCase();
            if (".jpg".equals(type) || ".jpeg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
                EBIImageViewer view = new EBIImageViewer(getMainFrame(), new ImageIcon(fileName));
                view.setVisible(true);
            } else if (".pdf".equals(type)) {
                openPDFReportFile(fileName);
            } else if (".doc".equals(type)) {
                openTextDocumentFile(fileName);
            } else {
                openTextDocumentFile(fileName);
            }
       }catch(Exception ex){}finally{
        getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
       }
    }

    public byte[] readFileToByte(File selFile) {
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

    public InputStream readFileGetBlob(File file) {
        InputStream is  ;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            return null;
        }
        return is;
    }


}
