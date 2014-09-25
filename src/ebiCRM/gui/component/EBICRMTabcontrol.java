package ebiCRM.gui.component;


import java.awt.Cursor;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.CloseableTabbedPaneListener;
import ebiNeutrinoSDK.interfaces.IEBIContainer;

public class EBICRMTabcontrol {

	private EBICRMModule ebiModule = null;
    public static ImageIcon oldIcon = null;
    public static ImageIcon newIcon = new ImageIcon("images/icon_wait.gif");

    public EBICRMTabcontrol(EBICRMModule module){
	   ebiModule = module;

    }

    public synchronized void showInActionStatus(final String name, final boolean show){

        final Runnable run = new Runnable(){

          public synchronized void run(){

                if(ebiModule.guiRenderer.getVisualPanel(name) != null){
                  if(show){
                	  if(oldIcon == null){
                		  oldIcon = ebiModule.guiRenderer.getVisualPanel(name).getModuleIcon();
                	  }
                      ebiModule.guiRenderer.getVisualPanel(name).setModuleIcon(newIcon);
                      ebiModule.guiRenderer.getVisualPanel(name).updateUI();
                           
                  }else{
                	 
                	  try {
  						Thread.sleep(1000);
                        } catch (InterruptedException e) {
  						e.printStackTrace();
                        }
                      ebiModule.guiRenderer.getVisualPanel(name).setModuleIcon(oldIcon);
                      ebiModule.guiRenderer.getVisualPanel(name).updateUI();
                  }
                }
            }
        };

        Thread tr = new Thread(run,"Save Module");
        tr.start();


    }

}
