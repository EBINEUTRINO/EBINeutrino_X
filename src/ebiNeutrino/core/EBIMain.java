package ebiNeutrino.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import ebiNeutrino.core.gui.lookandfeel.MoodyBlueTheme;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.swingx.JXStatusBar;

import ebiCRM.EBICRMModule;
import ebiNeutrino.core.GUIRenderer.EBIGUIRenderer;
import ebiNeutrino.core.gui.Dialogs.EBISplashScreen;
import ebiNeutrino.core.gui.component.EBIExtensionContainer;
import ebiNeutrino.core.gui.component.EBILogin;
import ebiNeutrino.core.gui.component.EBIToolbar;
import ebiNeutrino.core.module.management.EBIModuleManagement;
import ebiNeutrino.core.settings.EBISystemSetting;
import ebiNeutrino.core.settings.EBISystemSettingPanel;
import ebiNeutrino.core.user.management.EBIUserManagement;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIModule;
import ebiNeutrinoSDK.interfaces.IEBISecurity;
import ebiNeutrinoSDK.interfaces.IEBIToolBar;
import ebiNeutrinoSDK.utils.EBIConstant;
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

    public EBILogin login = null;
    public EBIPGFactory _ebifunction = null;
    public EBIUserManagement user_management = null;
    public static boolean canReleaseUser = false;
    public String appTitle = "EBI Neutrino R1 CRM / ERP Framework";
    public EBISystemSetting systemSetting = null;
    public static Logger logger = Logger.getLogger(EBIMain.class.getName());
    public EBIToolbar ebiBar = null;
    public EBIExtensionContainer container = null;
    protected IEBISecurity iSecurity = null;
    protected IEBIToolBar bar = null;
    public EBISplashScreen splash = null;
    public EBIModuleManagement mng = null;
    public EBIGUIRenderer guiRenderer = null;
    public EBIDialogExt frameSetting = null;
    public JXStatusBar statusBar = null;
    public EBICRMModule ebiModule = null;
    public JPanel panAllert = null;
    public JScrollPane pallert = null;
    private JLabel stName = null;
    public int USER_DELETE_ID =-1;
    public EBIToolbar userSysBar=null;

    public void disposeComponent() {
        user_management = null;
        login = null;
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
    }


    public static void main(String[] args) throws Exception {
           System.setProperty("file.encoding", "utf-8");
	       EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
           ToolTipManager.sharedInstance().setInitialDelay(0);

           try{

               UIManager.put("SplitPaneDivider.border", BorderFactory.createLineBorder(new Color(220,220,220)) );

               if(!"".equals(properties.getValue("EBI_Neutrino_LookandFeel"))){
                	if(properties.getValue("EBI_Neutrino_LookandFeel").indexOf("WindowsLookAndFeel") >-1 
                							&& System.getProperty("os.name").toLowerCase().indexOf("mac") > -1 ){
                		
                			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                		
                	}else if(properties.getValue("EBI_Neutrino_LookandFeel").indexOf("WindowsLookAndFeel") >-1 
										&& System.getProperty("os.name").toLowerCase().indexOf("lin") > -1 ){
                		
                			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                	}else if(properties.getValue("EBI_Neutrino_LookandFeel").indexOf("WindowsLookAndFeel") >-1 
										&& System.getProperty("os.name").toLowerCase().indexOf("uni") > -1 ){
                		
                			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                	}else{
                        MetalLookAndFeel.setCurrentTheme(new MoodyBlueTheme());
                		UIManager.setLookAndFeel(properties.getValue("EBI_Neutrino_LookandFeel"));

                	}
                }else{
                	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }


                final EBIMain application = new EBIMain();
                application.pack();

                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                application.setSize(d.width, d.height);
                //application.setExtendedState(EBIMain.MAXIMIZED_BOTH);

                // Try to read the update xml file from a server for checking if a new version is available
                /*EBISocketDownloader fileLoader = new EBISocketDownloader();
                fileLoader.SysPath = EBIPGFactory.updateServer;
                fileLoader.setConnection();

                if(fileLoader.readConfig()){ // ONLINE UPDATE FUNCTIONALITY

                    final EBIDialogExt ext = new EBIDialogExt(null);
                    ext.setName("SystemUpdateDialog");
                    ext.storeLocation(true);
                    ext.storeSize(true);
                    ext.setTitle(EBIPGFactory.getLANG("EBI_LANG_SYSTEM_UPDATE_DIALOG"));
                    ext.setModal(true);
                    ext.setSize(200,150);
                    ext.setLayout(null);

                    JLabel title = new JLabel("<html><body><b>"+EBIPGFactory.getLANG("EBI_LANG_UPDATE_FOR_SYSTEM_AVAILABLE")+"</b></body></html>");
                    title.setBounds(170,10,260,30);
                    ext.add(title,null);

                    JLabel body = new JLabel("<html><body>"+EBIPGFactory.getLANG("EBI_LANG_NEW_VERSION_AVAILABLE")+"" +
                            "<br><br>" +
                            ""+EBIPGFactory.getLANG("EBI_LANG_LOCAL_VERSION")+": "+fileLoader.localVer+"<br>" +
                            ""+EBIPGFactory.getLANG("EBI_LANG_ONLINE_VERSION")+": "+fileLoader.onlineVer+"<br><br>" +
                            ""+EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_UPDATE_YOUR_SYSTEM")+"</body></html>");
                    body.setBounds(170,20,260,180);
                    ext.add(body,null);


                    JLabel img = new JLabel(new ImageIcon("images/update.png"));
                    img.setBounds(10,30, 120, 120);
                    ext.add(img, null);

                    // Action Yes no
                    JButton yes = new JButton(EBIPGFactory.getLANG("EBI_LANG_YES"));
                    yes.setBounds(220,200,100,25);
                    yes.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e){
                            try {

                                if(EBIPGFactory.isWindows()){
                                    Runtime.getRuntime().exec("update/updateNeutrinoWindows.bat "+EBIPGFactory.updateServer);
                                }else if(EBIPGFactory.isMac()){
                                    Runtime.getRuntime().exec("update/updateNeutrinoMAC.sh "+EBIPGFactory.updateServer);
                                }else if(EBIPGFactory.isUnix()){
                                    Runtime.getRuntime().exec("update/updateNeutrinoLinux.sh "+EBIPGFactory.updateServer);
                                }

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                           System.exit(0);
                        }
                    });
                    ext.add(yes, null);

                    JButton no = new JButton(EBIPGFactory.getLANG("EBI_LANG_NO"));
                    no.setBounds(325,200,100,25);
                    no.addActionListener(new ActionListener(){
                        public void actionPerformed(ActionEvent e){
                             ext.setVisible(false);
                        }
                    });
                    ext.add(no, null);
                    ext.setVisible(true);

              }                               */
              //SwingUtilities.updateComponentTreeUI(application);
              //Load CRM module ebiCRM.jar

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

        _ebifunction = new EBIPGFactory();
        EBIDBConnection conn = new EBIDBConnection(_ebifunction);

        _ebifunction.setIEBIDatabase(conn);
        new EBINeutrinoSystemInit(_ebifunction, splash);

        if (EBINeutrinoSystemInit.isConfigured) {

            /********************/
            initialize();
            //Initialize Container, tab panel
            container = new EBIExtensionContainer(this);

            container.initContainer();
            _ebifunction.setIEBIContainerInstance(container);
            _ebifunction.setIEBIModule(this);

            /*********************/
            //Initialize report system
            EBIReportSystem reportSystem = new EBIReportSystem(_ebifunction);
            _ebifunction.setIEBIReportSystemInstance(reportSystem);

            /*********************/
            //Create toolbars
            ebiBar = new EBIToolbar(this);
            ebiBar.addToolBarToEBIMain();
            _ebifunction.setIEBIToolBarInstance(ebiBar);

            iSecurity = _ebifunction.getIEBISecurityInstance();
            bar = _ebifunction.getIEBIToolBarInstance();

            /*********************/
            //Initialize xml gui renderer
            guiRenderer = new EBIGUIRenderer(this);
            _ebifunction.setIEBIGUIRendererInstance(this.guiRenderer);

            System.out.println("Tasks Executed!");
            
            //Add timer task to keepalive the database
            // java.util.Timer timer = new java.util.Timer();
            //timer.scheduleAtFixedRate(new EBIKeepDatabaseAliveTask(_ebifunction), 2500*60,2500*60); //2,5... minute
            // timer.sc

            }
           
            // CREATE TASKBAR
            statusBar = new JXStatusBar();
            stName = new JLabel("EBI Neutrino R1 "+EBIVersion.getInstance().getVersion());
            statusBar.add(stName);

            JLabel stHost = new JLabel("Host: " + EBIPGFactory.host);
            stHost.setIcon(new ImageIcon("images/agt_runit.png"));
            statusBar.add(stHost);

            pallert = new JScrollPane();
            panAllert = new JPanel();
            panAllert.setVisible(false);
            pallert.setVisible(false);
            pallert.setViewportView(panAllert);
            statusBar.add(pallert);

            getContentPane().add(statusBar, BorderLayout.SOUTH);

            _ebifunction.addMainFrame(this);

      }catch(Exception ex){ex.printStackTrace(); _ebifunction.message.debug(EBIPGFactory.printStackTrace(ex));}
    }

    private void initialize() {
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setTitle(appTitle);
        this.addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(WindowEvent winEvt) {
                try{
                    if (iSecurity.checkCanReleaseModules()) {
                        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_CLOSE")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true) {
                            if(mng != null){
                            	mng.onExit();
                            }
                            System.exit(0);
                        }
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
    		  ebiModule = new EBICRMModule(_ebifunction);
    		  mng = new EBIModuleManagement(EBIMain.this,ebiModule);
    		  mng.showModule(); 
    		  mng.onLoad();
       }
      };
      Thread loadBusinessModule = new Thread(run,"LoadCRM_ERP_Module");
      loadBusinessModule.start();
    	
    }

    public void addLoginModul() {

        login = new EBILogin(this);
        login.addWindowListener(this.getWindowListeners()[0]);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        login.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        login.setName("EBILoginDialog");
        login.setResizable(false);
        login.setSize(500, 217);
        login.setLocation(((int)d.getWidth() -login.getWidth())  / 2, ((int) d.getHeight() -login.getHeight()) / 2);
        if(splash != null){splash.setVisible(false);}
        login.setVisible(true);

    }


    public void addSystemSetting(final int selectedList) {
        frameSetting = new EBIDialogExt(this);
        systemSetting = new EBISystemSetting(this);
        final EBIToolbar sysBar = new EBIToolbar(this);

        int NEW_ID = sysBar.addToolButton(EBIConstant.ICON_NEW, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                if (EBISystemSetting.selectedModule == -1) {

                    addSystemSetting(selectedList);

                } else {
                    if (EBISystemSetting.selectedModule == 4) {
                       try{
                        systemSetting.listName.report.newReport();
                       }catch(Exception ex){ex.printStackTrace();}
                    }
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

            }
        });

        ((JButton)sysBar.getToolbarComponent(NEW_ID)).setMnemonic(KeyEvent.VK_N);
        sysBar.setComponentToolTipp(NEW_ID, EBIPGFactory.getLANG("EBI_LANG_T_LOAD_ALL_SETTING"));

        int SAVE_ID = sysBar.addToolButton(EBIConstant.ICON_SAVE, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                systemSetting.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                if (EBISystemSetting.selectedModule == 2) {

                    systemSetting.listName.einstp.saveSystemSetting();
                    mng.reloadSelectedModule();
                    frameSetting.setVisible(false);

                    addSystemSetting(1);
                    frameSetting.requestFocus();
                    
                } else if (EBISystemSetting.selectedModule == 3) {

                    systemSetting.listName.lnf.saveTheme();

                } else if (EBISystemSetting.selectedModule == 4) {

                    systemSetting.listName.report.saveReport();

                }
                systemSetting.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        ((JButton)sysBar.getToolbarComponent(SAVE_ID)).setMnemonic(KeyEvent.VK_S);
        sysBar.setComponentToolTipp(SAVE_ID, EBIPGFactory.getLANG("EBI_LANG_T_SAVE_SETTING"));

        int DELETE_ID = sysBar.addToolButton(EBIConstant.ICON_DELETE, new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                setCursor(new Cursor(Cursor.WAIT_CURSOR));

                if (EBISystemSetting.selectedModule == 4) {

                    systemSetting.listName.report.deleteReport();

                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        ((JButton)sysBar.getToolbarComponent(DELETE_ID)).setMnemonic(KeyEvent.VK_D);
        sysBar.setComponentToolTipp(DELETE_ID, EBIPGFactory.getLANG("EBI_LANG_T_DELETE_SETTING"));
        sysBar.showToolBar(false);

        systemSetting.setChangePropertiesVisible(false);
        systemSetting.getPanel().add(sysBar.getJToolBar(), BorderLayout.NORTH);

        if (selectedList != -1) {
            systemSetting.listName.jListnames.setSelectedIndex(selectedList);
            systemSetting.listName.cpanel.removeAll();
            systemSetting.listName.cpanel.updateUI();
            systemSetting.listName.einstp = new EBISystemSettingPanel(this);
            systemSetting.listName.cpanel.add(systemSetting.listName.einstp, java.awt.BorderLayout.CENTER);
            systemSetting.listName.einstp.updateUI();
        }


        frameSetting.setName("SystemSetting");
        frameSetting.storeLocation(true);
        frameSetting.storeSize(true);
        frameSetting.setSize(new Dimension(800, 600));
        frameSetting.setResizable(true);
        systemSetting.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_SYSTEM_SETTING"));
        systemSetting.setModuleIcon(EBIConstant.ICON_APP);
        frameSetting.add(systemSetting);

        if(frameSetting != null && !frameSetting.isVisible()){
          frameSetting.setVisible(true);
        }

    }

    public void addUsermanagement() {

        user_management = new EBIUserManagement(this);
        userSysBar = new EBIToolbar(this);
        int NEW_ID = userSysBar.addToolButton(EBIConstant.ICON_NEW, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                user_management.resetFields();
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        ((JButton)userSysBar.getToolbarComponent(NEW_ID)).setMnemonic(KeyEvent.VK_N);
        userSysBar.setComponentToolTipp(NEW_ID, EBIPGFactory.getLANG("EBI_LANG_T_NEW_USER"));

        int SAVE_ID = userSysBar.addToolButton(EBIConstant.ICON_SAVE, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                if (user_management.isSaveOrUpdate == false) {
                    user_management.saveUser();
                    canReleaseUser = true;
                } else if (user_management.isSaveOrUpdate == true) {
                    user_management.updateUser();
                    canReleaseUser = true;
                }
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        ((JButton)userSysBar.getToolbarComponent(SAVE_ID)).setMnemonic(KeyEvent.VK_S);
        userSysBar.setComponentToolTipp(SAVE_ID, EBIPGFactory.getLANG("EBI_LANG_T_SAVE_USER"));

        USER_DELETE_ID = userSysBar.addToolButton(EBIConstant.ICON_DELETE, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                user_management.userDelete();
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        ((JButton)userSysBar.getToolbarComponent(USER_DELETE_ID)).setMnemonic(KeyEvent.VK_D);
        userSysBar.setComponentToolTipp(USER_DELETE_ID, EBIPGFactory.getLANG("EBI_LANG_T_DELETE_USER"));
        userSysBar.setComponentToolBarEnabled(USER_DELETE_ID, false);
        userSysBar.showToolBar(false);

        user_management.DELETE_BUTTON_ID = USER_DELETE_ID;
        user_management.bar = userSysBar;

        user_management.getPanel().add(userSysBar,BorderLayout.NORTH);
        user_management.setChangePropertiesVisible(false);
        frameSetting = new EBIDialogExt(this);
        user_management.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_USER_SETTING"));
        user_management.setModuleIcon(EBIConstant.ICON_APP);
        frameSetting.setName("Usermanagement");
        frameSetting.setModal(true);
        frameSetting.storeLocation(true);
        frameSetting.storeSize(true);
        frameSetting.setSize(new Dimension(800,600));
        frameSetting.setResizable(true);

        frameSetting.setContentPane(user_management);
        if(frameSetting != null && !frameSetting.isVisible()){
          frameSetting.setVisible(true);
        }
    }
      
    public Object getActiveModule() {
        return mng.getActiveModule();
    }

}  

