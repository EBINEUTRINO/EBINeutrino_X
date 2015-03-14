package ebiNeutrino.core.module.management;


import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import ebiCRM.EBICRMModule;
import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIRenderer.EBIGUIScripts;
import ebiNeutrinoSDK.EBIPGFactory;
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
    private int selectedModindex=-1;


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
        ebiMain.system.hibernate.removeAllHibernateSessions();
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

              if(!ebiExtension.ebiMain(null)){
                  return false;
              }
             addF5Action();
         }catch(Exception ex){
             ex.printStackTrace();
             EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
         }
         ebiMain.setVisible(true);
        return true;
	}

    public boolean showModule(Object module,Object o){

         try{
              ebiMain.container.removeAllFromContainer();
              if(!((IEBIExtension)module).ebiMain(o)){
                  return false;
              }

              this.module = module;
              ebiExtension =(IEBIExtension)module;
              loadCRM(selectedModName, selectedModXMLPath);
              ebiMain.container.setSelectedModIndex(selectedModindex);
              addF5Action();

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


    public void addF5Action(){
        Action refreshAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectedModindex = ebiMain.container.getSelectedListIndex();
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
        showModule(obj,restore);

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
      }catch(Exception ex){ ex.printStackTrace();}
    }

    /**
     * Load a groovy script from a specified location
     * @param name
     * @param path
     */

    public void loadScript(String name,String path){

        List<Object> toScript = new ArrayList<Object>();

        if ("groovy".equals(name)) {
            EBIGUIScripts script = new EBIGUIScripts();
            script.setType("groovy");
            script.setPath(path);
            script.setName(name);
            toScript.add(script);
        }

        ebiMain.guiRenderer.getScriptContainer().put("app",toScript);
        ebiMain.guiRenderer.excScript("app");

    }


    public String getSelectedModName() {
        return selectedModName;
    }

    public String getSelectedModXMLPath() {
        return selectedModXMLPath;
    }

}