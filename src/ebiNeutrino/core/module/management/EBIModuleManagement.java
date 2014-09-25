package ebiNeutrino.core.module.management;


import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

import ebiCRM.EBICRMModule;
import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIDesigner.EBIGUIWidgetsBean;
import ebiNeutrino.core.GUIRenderer.EBIMutableTreeNode;
import ebiNeutrino.core.gui.component.EBITreeFactory;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIExtension;

/**
 * Module management class
 *
 */

public class EBIModuleManagement {

    private IEBIExtension ebiExtension = null;
    private EBIMain ebiMain = null;
    private Object module = null;
    private String selectedModName = "";
    private String selectedModXMLPath = "";


	/**
	 * Constructor
	 */
	public EBIModuleManagement(EBIMain main, Object module){
		ebiExtension = (IEBIExtension)module;
		this.module = module;
		ebiMain = main;
	}
	/**
	 * Release extension method
	 */
	public void releaseModule(){
       try{
       if(ebiExtension != null){
        ebiExtension.ebiRemove();
        ebiMain.container.removeAllFromContainer();
        EBIPGFactory.canRelease = true;
		EBIPGFactory.isSaveOrUpdate = false;
        //ebiExtension = null;
		this.module = null;
        ebiMain.guiRenderer.init();
        ebiMain._ebifunction.hibernate.removeAllHibernateSessions();
		System.gc();
       }
       removeF5Action();
       }catch(Exception ex){
          ex.printStackTrace();
          EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
       }
    }

    public Object releaseModule(Object obj){
        Object o = null;
        try{
           if(obj != null){
            o = ((IEBIExtension)obj).ebiRemove();
            EBIPGFactory.canRelease = true;
            EBIPGFactory.isSaveOrUpdate = false;
            ebiExtension = null;
            this.module = null;
            ebiMain.guiRenderer.init();
            System.gc();
           }
           removeF5Action();
        }catch(Exception ex){
             ex.printStackTrace();
             EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }
        return o;
	}

    public void onExit(){
        ebiExtension.onExit();
    }

    public void onLoad(){
        ebiExtension.onLoad();
    }

    /**
	 * Call the main method ebiMain
	 */
	public boolean showModule(){
         try{
         
              //ebiMain._ebifunction.getIEBIToolBarInstance().resetToolBar();
              if(!ebiExtension.ebiMain(null)){
                  return false;
              }
             addF5Action();
             addAltShowToolbar();
         }catch(Exception ex){
             ex.printStackTrace();
             EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
         }
         ebiMain.container.getTreeViewInstance().updateUI();
         ebiMain.setVisible(true);
        return true;
	}

    public boolean showModule(Object module,Object o,boolean resetToolBar){
         try{
		  ebiMain.container.removeAllFromContainer();
          if(resetToolBar){
		    ebiMain._ebifunction.getIEBIToolBarInstance().resetToolBar();
          }

 		  if(!((IEBIExtension)module).ebiMain(o)){
			  return false;
		  }

          this.module = module;
          ebiMain.container.getTreeViewInstance().updateUI();
          ebiExtension =(IEBIExtension)module;
          loadCRM(selectedModName,selectedModXMLPath);
          ebiMain.container.setSelectedExtension(selectedModName);
          addF5Action();
          addAltShowToolbar();

         }catch(Exception ex){
             ex.printStackTrace();
             EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
         }
         ebiMain.setVisible(true);
        return true;
	}

    public Object getActiveModule(){
		return this.module;
	}

    public void addAltShowToolbar(){
         Action showToolBarAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try{
                    if(!ebiMain._ebifunction.getIEBIToolBarInstance().getJToolBar().isVisible()){
                        ebiMain._ebifunction.getIEBIToolBarInstance().getJToolBar().setVisible(true);
                        ebiMain.statusBar.setVisible(true);
                        ebiMain.container.hideSplit(true);
                    }else{
                        ebiMain._ebifunction.getIEBIToolBarInstance().getJToolBar().setVisible(false);
                        ebiMain.statusBar.setVisible(false);
                        ebiMain.container.hideSplit(false);
                    }
                }catch(Exception ex){}
            }
        };
        try{
            InputMap inputMap = ((JPanel)ebiMain.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke("F1"), "SHOWTOOLBAR");
            ((JPanel)ebiMain.getContentPane()).getActionMap().put("SHOWTOOLBAR", showToolBarAction);
        }catch(Exception ex){}
    }

    public void addF5Action(){
        Action refreshAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                reloadSelectedModule();
            }
        };
        try{
            InputMap inputMap = ((JPanel)ebiMain.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(KeyStroke.getKeyStroke("F5"), "REFRESH");
            ((JPanel)ebiMain.getContentPane()).getActionMap().put("REFRESH", refreshAction);
        }catch(Exception ex){}

    }

    public void reloadSelectedModule(){
        ebiMain.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        Object obj = getActiveModule();
        //Return object to restore
        Object restore = releaseModule(obj);

        showModule(obj,restore,false);

        ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    private void removeF5Action(){
        ((JPanel)ebiMain.getContentPane()).getActionMap().remove("REFRESH");
    }

    public void loadCRM(String name, String xmlPath){

      try{
        if(ebiMain.guiRenderer.existPackage("Summary") && name.equals("Summary")){

            ebiMain.guiRenderer.useGUI("Summary");
                ((EBICRMModule) ebiMain.getActiveModule()).getSummaryPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Summary")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getSummaryPane();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Leads") && name.equals("Leads")){

            ebiMain.guiRenderer.useGUI("Leads");
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Leads")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Company") && name.equals("Company")){

            ebiMain.guiRenderer.useGUI("Company");
                ((EBICRMModule) ebiMain.getActiveModule()).getCompanyPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Company")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getCompanyPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getCompanyPane().initializeAction();
                ((EBICRMModule) ebiMain.getActiveModule()).loadCompanyData();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Contact") && name.equals("Contact")){

            ebiMain.guiRenderer.useGUI("Contact");
                ((EBICRMModule) ebiMain.getActiveModule()).getContactPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Contact")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getContactPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getContactPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Address") && name.equals("Address")){

            ebiMain.guiRenderer.useGUI("Address");
            ((EBICRMModule) ebiMain.getActiveModule()).getAddressPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Address")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
            ((EBICRMModule) ebiMain.getActiveModule()).getAddressPane().initialize();
            ((EBICRMModule) ebiMain.getActiveModule()).getAddressPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Bank") && name.equals("Bank")){

            ebiMain.guiRenderer.useGUI("Bank");
                ((EBICRMModule) ebiMain.getActiveModule()).getBankdataPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Bank")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getBankdataPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getBankdataPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("MeetingCall") && name.equals("MeetingCall")){

            ebiMain.guiRenderer.useGUI("MeetingCall");
                ((EBICRMModule) ebiMain.getActiveModule()).getMeetingProtocol();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("MeetingCall")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getMeetingProtocol().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getMeetingProtocol().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else  if(ebiMain.guiRenderer.existPackage("Activity") && name.equals("Activity")){

            ebiMain.guiRenderer.useGUI("Activity");
                ((EBICRMModule) ebiMain.getActiveModule()).getActivitiesPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Activity")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
              ((EBICRMModule) ebiMain.getActiveModule()).getActivitiesPane().initialize();
              ((EBICRMModule) ebiMain.getActiveModule()).getActivitiesPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Opportunity") && name.equals("Opportunity")){

            ebiMain.guiRenderer.useGUI("Opportunity");
                ((EBICRMModule) ebiMain.getActiveModule()).getOpportunityPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Opportunity")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getOpportunityPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getOpportunityPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Offer") && name.equals("Offer")){

            ebiMain.guiRenderer.useGUI("Offer");
            ((EBICRMModule) ebiMain.getActiveModule()).getOfferPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Offer")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getOfferPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getOfferPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Order") && name.equals("Order")){

            ebiMain.guiRenderer.useGUI("Order");
                ((EBICRMModule) ebiMain.getActiveModule()).getOrderPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Order")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getOrderPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getOrderPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Service") && name.equals("Service")){

            ebiMain.guiRenderer.useGUI("Service");
                ((EBICRMModule) ebiMain.getActiveModule()).getServicePane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Service")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getServicePane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getServicePane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Invoice") && name.equals("Invoice")){

            ebiMain.guiRenderer.useGUI("Invoice");
                ((EBICRMModule) ebiMain.getActiveModule()).getInvoicePane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Invoice")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getInvoicePane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getInvoicePane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Product") && name.equals("Product")){

            ebiMain.guiRenderer.useGUI("Product");
                ((EBICRMModule) ebiMain.getActiveModule()).getProductPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Product")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getProductPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getProductPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Account") && name.equals("Account")){

            ebiMain.guiRenderer.useGUI("Account");
                ((EBICRMModule) ebiMain.getActiveModule()).getAccountPane();
               // ((EBICRMModule) ebiMain.getActiveModule()).getAccountPane().dataControlAccount.dataShow(EBIPGFactory.);
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Account")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getAccountPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getAccountPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Prosol") && name.equals("Prosol")){

            ebiMain.guiRenderer.useGUI("Prosol");
                ((EBICRMModule) ebiMain.getActiveModule()).getProsolPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Prosol")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getProsolPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getProsolPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Project") && name.equals("Project")){

            ebiMain.guiRenderer.useGUI("Project");
                ((EBICRMModule) ebiMain.getActiveModule()).getProjectPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Project")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getProjectPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getProjectPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("CashRegister") && name.equals("CashRegister")){

            ebiMain.guiRenderer.useGUI("CashRegister");
                ((EBICRMModule) ebiMain.getActiveModule()).getCashRegisterPane();
            ((EBICRMModule) ebiMain.getActiveModule()).getCashRegisterPane().
                        dataControlCashRegister.dataShow(ebiMain.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("CashRegister")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getCashRegisterPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getCashRegisterPane().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(ebiMain.guiRenderer.existPackage("Campaign") && name.equals("Campaign")){

            ebiMain.guiRenderer.useGUI("Campaign");
                ((EBICRMModule) ebiMain.getActiveModule()).getEBICRMCampaign();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Campaign")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getEBICRMCampaign().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getEBICRMCampaign().initializeAction();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals(EBIPGFactory.getLANG("EBI_LANG_C_TAB_CALENDAR"))){
            ebiMain.container.addScrollableContainer(EBIPGFactory.getLANG("EBI_LANG_C_TAB_CALENDAR"),
                    ((EBICRMModule) ebiMain.getActiveModule()).getEBICalendar(),null,-1);
        }else if(xmlPath != null){
            ebiMain.guiRenderer.loadGUI(xmlPath);
            ebiMain.guiRenderer.showGUI();
        }

        selectedModName = name;
        selectedModXMLPath = xmlPath;
      }catch(Exception ex){}
    }

    public String getSelectedModName() {
        return selectedModName;
    }

    public String getSelectedModXMLPath() {
        return selectedModXMLPath;
    }

}