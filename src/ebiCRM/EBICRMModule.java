package ebiCRM;

import org.apache.log4j.Logger;
import ebiCRM.functionality.EBICRMDynamicFunctionalityMethods;
import ebiCRM.gui.component.EBICRMTabcontrol;
import ebiCRM.gui.panels.EBICRMAccountStack;
import ebiCRM.gui.panels.EBICRMInvoice;
import ebiCRM.gui.panels.EBICRMLead;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiNeutrinoSDK.EBIPGFactory;
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
    public int EBICRM_SESSION = 0;
    public IEBIGUIRenderer guiRenderer = null;
    public static final Logger logger = Logger.getLogger(EBICRMModule.class.getName());
    public static String classification[]=null;
    public static String categories[]=null;
    public static String cooperations[]=null;
    public static String gendersList[]=new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                                        EBIPGFactory.getLANG("EBI_LANG_C_MALE"),EBIPGFactory.getLANG("EBI_LANG_C_FEMALE")};



    public EBICRMModule(EBIPGFactory func) {
        //Init CODE
        ebiPGFactory = func;
        
        // tabProp = new EBISaveRestoreTableProperties();
        try {
            ebiPGFactory.hibernate.openHibernateSession("EBICRM_SESSION");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        dynMethod = new EBICRMDynamicFunctionalityMethods(this);
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

            dynMethod.initComboBoxes(false);
            ebiPGFactory.getIEBIContainerInstance().setSelectedListIndex(0);
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

    }

    public void onExit(){

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
        }
        invoicePane.dataControlInvoice.dataShow(-1);
       return invoicePane; 
    }

    public EBICRMProduct getProductPane(){
        if(productPane == null){
            productPane = new EBICRMProduct(this);
            ebiPGFactory.setDataStore("Product",productPane);
        }
        return productPane;
    }

    public EBICRMAccountStack getAccountPane(){
        if(accountPane == null){
           accountPane = new EBICRMAccountStack(this);
           ebiPGFactory.setDataStore("Account",accountPane);
        }
       return accountPane;
    }

}