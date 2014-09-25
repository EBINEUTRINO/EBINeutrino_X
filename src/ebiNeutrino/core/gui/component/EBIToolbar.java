package ebiNeutrino.core.gui.component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIDesigner.DesignerPanel;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBISecurity;
import ebiNeutrinoSDK.interfaces.IEBIToolBar;
import ebiNeutrinoSDK.utils.EBIConstant;

/**
 * EBI Neutrino Toolbar component
 *
 */
public class EBIToolbar extends JToolBar implements IEBIToolBar {

	private HashMap<Object,Object> buttonList	= null;
	private int buttonID						= -1;
	private EBIMain ebiMain                     = null;
	private IEBISecurity iSecurity				= null;


    /**
	 * Constructor
	 * @param main
	 */
	public EBIToolbar(EBIMain main){
		ebiMain = main;
        setBorderPainted(true);
        setRollover(true);
        setFocusable(false);
        setFocusCycleRoot(false);
        setFocusTraversalKeysEnabled(false);
        iSecurity = ebiMain._ebifunction.getIEBISecurityInstance();
        buttonList = new HashMap<Object,Object>();
	}


     public void paintComponent(Graphics g) {


         Graphics2D g2 = (Graphics2D)g;
         // Draw bg top
         Color startColor = new Color(240,240,240);
         Color endColor = startColor.brighter();

         // A non-cyclic gradient
         GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, 20, endColor);
         g2.setPaint(gradient);
         g2.fillRect(0, 0, getWidth(), 20);

         Color sColor = startColor;
         Color eColor = endColor;

         // A non-cyclic gradient
         GradientPaint gradient1 = new GradientPaint(0, 0, sColor, getWidth(), 40, eColor);
         g2.setPaint(gradient1);
         g.setColor(startColor);
         g.fillRect(0, 21, getWidth(), getHeight());
         g.setColor(endColor);
         g.drawLine(0, 20, getWidth(), 20);

         setOpaque(true);
    }


	/**
	 * Show the addToolBarToEBIMain
	 * @param pane
	 * @param constraints
	 */
	public void addToolBarToEBIMain(){
        super.setOrientation(JToolBar.HORIZONTAL);
        ebiMain.getContentPane().add(this,BorderLayout.NORTH);

    }
    /**
     * Add the system home button to the toolBar
     */

    public JButton addLogoutButton(){

      JButton logout = new JButton(EBIConstant.ICON_LOGOUT);
      logout.setBorderPainted(false);
      logout.setOpaque(false);
      logout.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              if (iSecurity.checkCanReleaseModules()) {
                  ebiMain.addLoginModul();
              }
          }
      });

      logout.setToolTipText("<html><body><br><b>EBI Neutrino Logout</b><br><br></body></html>");
      return logout;
    }

    public JButton addUserSettingButton(){

      JButton usetting = new JButton(EBIConstant.ICON_USER);
      usetting.setBorderPainted(false);
      usetting.setFocusable(false);
      usetting.setOpaque(false);
      usetting.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              if (iSecurity.checkCanReleaseModules()) {
                  if (iSecurity.secureModule()) {
                      ebiMain.addUsermanagement();
                  }
              }
          }
      });

       usetting.setToolTipText("<html><body><br><b>" + EBIPGFactory.getLANG("EBI_LANG_USER_SETTING") + "</b><br><br></body></html>");
       return usetting;
    }

     public JButton addSystemSettingButton(){

      JButton ssetting = new JButton(EBIConstant.ICON_SETTING);
      ssetting.setBorderPainted(false);
      ssetting.setFocusable(false);
      ssetting.setOpaque(false);
      ssetting.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              if (iSecurity.checkCanReleaseModules()) {
                  if (iSecurity.secureModule()) {
                      ebiMain.addSystemSetting(-1);
                  }
              }
          }
      });

       ssetting.setToolTipText("<html><body><br><b>" + EBIPGFactory.getLANG("EBI_LANG_SETTING") + "</b><br><br></body></html>");
       return ssetting;
    }


    /**
	 * Return an instance of the JToolBar
	 * @return
	 */
	public JToolBar getJToolBar() {
		return this;
	}
	
	/**
	 * Remove all component from the toolbar
	 */
	
	public void resetToolBar(){
		buttonList = new HashMap<Object,Object>();
		getJToolBar().removeAll();
		buttonID=-1;
        
    }
	
	/**
	 * Add a toolBar button
	 * @param icon
	 * @param listener
	 * @return  Inserted ID
	 */
	public int addToolButton(ImageIcon icon,java.awt.event.ActionListener listener){
	   JButton jBarButton;
       if(icon != null){
		jBarButton = new JButton(icon);
       }else{
		jBarButton = new JButton(EBIConstant.ICON_NEW);
       }
       jBarButton.setBorderPainted(false);
       jBarButton.setOpaque(false);
	   jBarButton.addActionListener(listener);
       jBarButton.setFocusable(false);
       buttonList.put(++buttonID,jBarButton);
	   return buttonID;
	}
	
	/**
	 * Insert a custom component
	 * @param component  JComponent parameter
	 * @return  Inserted ID
	 */
	
	public int addCustomToolBarComponent(JComponent component){
		if(component != null){
            component.setFocusable(false);
            component.setOpaque(false);
			buttonList.put(++buttonID, component);
            add(component);
            updateUI();
		}

		return buttonID;
	}
	
	/**
	 * Add toolbar separator
	 */
	public void addButtonSeparator(){
	  buttonList.put(++buttonID,"-");
	}
	
	/**
	 * add tooltipp
	 * @param id
	 * @param text
	 */
	public void setComponentToolTipp(int id, String text){
		try{

           JComponent component = (JComponent)buttonList.get(id);
		   component.setToolTipText(text);
		   buttonList.put(id, component);

        }catch(NullPointerException ex){
			EBIExceptionDialog.getInstance("Toolbar point to an unavailable component").Show(EBIMessage.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Enable or disable a toolbar component
	 * @param id
	 * @param enabled
	 */
	public void setComponentToolBarEnabled(int id, boolean enabled){
		try{

             JComponent component = (JComponent)buttonList.get(id);
             if(component instanceof  JButton){
                 ((JButton)component).setBorderPainted(false);
             }
             component.setEnabled(enabled);
             buttonList.put(id, component);

        }catch(NullPointerException ex){
			EBIExceptionDialog.getInstance("Toolbar point to an unavailable component").Show(EBIMessage.ERROR_MESSAGE);
		}
    }

    /**
     * Return a toolbar component
     * @param id
     * @return
     */

    public JComponent getToolbarComponent(int id){
        JComponent comp = null;
        try{

            comp = (JComponent)buttonList.get(id);

        }catch(NullPointerException ex){
			EBIExceptionDialog.getInstance("Toolbar point to an unavailable component").Show(EBIMessage.ERROR_MESSAGE);
		}
        return comp;
    }

    /**
     * Return a toolbar button
     * @param id
     * @return
     */

    public JButton getToolbarButton(int id){
        JButton comp = null;
        try{

            comp = (JButton)buttonList.get(id);

        }catch(NullPointerException ex){
            EBIExceptionDialog.getInstance("Toolbar point to an unavailable component").Show(EBIMessage.ERROR_MESSAGE);
        }
        return comp;
    }

    /**
	 * Show the toolbar
	 */
	public void showToolBar(boolean mainWindows){


        if(mainWindows){

           if(ebiMain._ebifunction.iuserRights.isAdministrator()){
                addButtonSeparator();
                addloadDesignerButton();
           }
            getJToolBar().add(addLogoutButton());
           // getJToolBar().addSeparator();
        }
		for(int i=0; i<buttonList.size(); i++){
			if(buttonList.get(i) != null){
				if(buttonList.get(i) instanceof JComponent){
				 getJToolBar().add((JComponent)buttonList.get(i));
				}else if("-".equals(buttonList.get(i).toString())){
				 //getJToolBar().addSeparator();
				}
			}
        }

        if(mainWindows){
            //Add User and System Setting
            //getJToolBar().addSeparator();
           if(ebiMain._ebifunction.iuserRights.isAdministrator()){
            getJToolBar().add(addUserSettingButton());
            getJToolBar().add(addSystemSettingButton());
           }
        }

    }

    private void addloadDesignerButton(){

      int HOME = addToolButton(EBIConstant.ICON_DESIGNER,new ActionListener() {
			public void actionPerformed(ActionEvent e) {

                ebiMain._ebifunction.setFileDialogCurrentPath(new File(System.getProperty("user.dir")+"/ebiExtensions/EBICRM"));
                File fs = ebiMain._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);

                 if(fs != null){
                      ebiMain.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                      final DesignerPanel newFormFile = new DesignerPanel(fs,ebiMain);
                      ebiMain._ebifunction.setIEBIModule(newFormFile);

                      final JDialog designerDialog = new JDialog(ebiMain);
                      designerDialog.setAlwaysOnTop(true);
                      designerDialog.setName("LoadDesignerDialog");
                      designerDialog.setLocation(50, 50);
                      designerDialog.setTitle(EBIPGFactory.getLANG("EBI_LANG_DESIGNER_DIALOG"));
                      designerDialog.setSize(800,600);
                      designerDialog.getContentPane().setLayout(new BorderLayout());
                      designerDialog.getContentPane().add(newFormFile.getTabbedPane(),BorderLayout.CENTER);
                      designerDialog.setVisible(true);

                      designerDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                            public void windowClosing(java.awt.event.WindowEvent e) {
                                 designerDialog.setAlwaysOnTop(false);
                                 iSecurity.checkCanReleaseModules();
                                 newFormFile.setVisiblePopup(false);
                                 ebiMain._ebifunction.setIEBIModule(ebiMain);
                                 ebiMain.mng.reloadSelectedModule();
                            }
                      });
                     newFormFile.requestStartFocus();
                     ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                 } 
			}
		});

        setComponentToolTipp(HOME,"<html><body><br><b>EBI Neutrino Load Designer</b><br><br></body></html>");

    }
}
