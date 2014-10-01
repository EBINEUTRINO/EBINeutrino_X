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
        if(ebiMain.guiRenderer.existPackage("Leads") && name.equals("Leads")){

            ebiMain.guiRenderer.useGUI("Leads");
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane();
            ebiMain.guiRenderer.showGUI();

        }else if(name.equals("Leads")){

            ebiMain.guiRenderer.loadGUI(xmlPath);
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane().initialize();
                ((EBICRMModule) ebiMain.getActiveModule()).getLeadPane().initializeAction();
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