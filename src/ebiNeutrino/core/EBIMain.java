package ebiNeutrino.core;

import java.awt.*;
import java.awt.event.WindowEvent;

import javax.swing.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.swingx.JXStatusBar;

import ebiCRM.EBICRMModule;
import ebiNeutrino.core.GUIRenderer.EBIGUIRenderer;
import ebiNeutrino.core.gui.Dialogs.EBISplashScreen;
import ebiNeutrino.core.gui.component.EBIExtensionContainer;
import ebiNeutrino.core.module.management.EBIModuleManagement;
import ebiNeutrino.core.settings.EBISystemSetting;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIModule;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;


/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Description:
 * This is the main class for EBI Neutrino
 * 
 * JVM start point:
 * public static void main(String[] args)
 */


public class EBIMain extends JFrame implements IEBIModule {

    public EBIPGFactory system = null;
    public String appTitle = "EBI Neutrino CRM / ERP";
    public EBISystemSetting systemSetting = null;
    public static Logger logger = Logger.getLogger(EBIMain.class.getName());
    public EBIExtensionContainer container = null;
    public EBISplashScreen splash = null;
    public EBIModuleManagement mod_management = null;
    public EBIGUIRenderer guiRenderer = null;
    public JXStatusBar statusBar = null;
    public EBICRMModule ebiModule = null;
    private JLabel stName = null;


    public static void main(String[] args) throws Exception {
           System.setProperty("file.encoding", "utf-8");
           try{
                final EBIMain application = new EBIMain();
                application.pack();

                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                application.setSize(d.width, d.height);

              application.addLoginModul();
            } catch (Exception exx) {
                exx.printStackTrace();
                logger.error(EBIPGFactory.printStackTrace(exx));
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(exx)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                System.exit(1);
            }
    }

    /**
     * EBIMain default constructor it initialize the system functionality
     */

    public EBIMain() {
   
      try{
        splash = new EBISplashScreen();
        PropertyConfigurator.configure("config/ebiLogger.config");
        splash.setVisible(true);
        
        logger.info("Entering application.");

        system = new EBIPGFactory();
        EBIDBConnection conn = new EBIDBConnection(system);

        system.setIEBIDatabase(conn);
        new EBINeutrinoSystemInit(system, splash);

        if (EBINeutrinoSystemInit.isConfigured) {

            /********************/
            initialize();
            //Initialize Container, tab panel
            container = new EBIExtensionContainer(this);

            container.initContainer();
            system.setIEBIContainerInstance(container);
            system.setIEBIModule(this);

            /*********************/
            //Initialize report system
            EBIReportSystem reportSystem = new EBIReportSystem(system);
            system.setIEBIReportSystemInstance(reportSystem);

            /*********************/
            //Initialize xml gui renderer
            guiRenderer = new EBIGUIRenderer(this);
            system.setIEBIGUIRendererInstance(this.guiRenderer);

            System.out.println("Tasks Executed!");


            }
           
            // CREATE TASKBAR
            statusBar = new JXStatusBar();
            stName = new JLabel("EBI Neutrino "+EBIVersion.getInstance().getVersion());
            statusBar.add(stName);

            JLabel stHost = new JLabel("Host: " + EBIPGFactory.host);
            stHost.setIcon(new ImageIcon("images/agt_runit.png"));
            statusBar.add(stHost);

            getContentPane().add(statusBar, BorderLayout.SOUTH);

            system.addMainFrame(this);

      }catch(Exception ex){ex.printStackTrace(); system.message.debug(EBIPGFactory.printStackTrace(ex));}
    }

    private void initialize() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle(appTitle);
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(WindowEvent winEvt) {
                try{

                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_CLOSE")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true) {
                        if(mod_management != null){
                            mod_management.onExit();
                        }
                        System.exit(0);
                    }

                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
    
    public void showBusinessModule(){
      final Runnable run = new Runnable(){
    	  public void run(){
    		  ebiModule = new EBICRMModule(system);
    		  mod_management = new EBIModuleManagement(EBIMain.this,ebiModule);
    		  mod_management.showModule();
    		  mod_management.onLoad();
       }
      };
      Thread loadBusinessModule = new Thread(run,"LoadCRM_ERP_Module");
      loadBusinessModule.start();
    	
    }

    public void addLoginModul() {

        boolean ret = system.checkIsValidUser("root", "ebineutrino");

        if (ret != false) {

            splash.setVisible(true);
            showBusinessModule();
            EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
            properties.setValue("EBI_Neutrino_Last_Logged_User", "root");
            properties.saveProperties();
            splash.setVisible(false);

        } else {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_USER_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
        }

    }
      
    public Object getActiveModule() {
        return mod_management.getActiveModule();
    }

}  

