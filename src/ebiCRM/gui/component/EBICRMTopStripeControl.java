package ebiCRM.gui.component;

import javax.swing.ImageIcon;
import ebiCRM.EBICRMModule;


public class EBICRMTopStripeControl {

	private EBICRMModule ebiModule = null;
    public static ImageIcon oldIcon = null;
    public static ImageIcon newIcon = new ImageIcon("images/icon_wait.gif");

    public EBICRMTopStripeControl(EBICRMModule module){
	   ebiModule = module;

    }

    public synchronized void showInActionStatus(final String name, final boolean show){

        final Runnable run = new Runnable(){

          public synchronized void run(){

                if(ebiModule.gui.getVisualPanel(name) != null){
                  if(show){
                	  if(oldIcon == null){
                		  oldIcon = ebiModule.gui.getVisualPanel(name).getModuleIcon();
                	  }
                      ebiModule.gui.getVisualPanel(name).setModuleIcon(newIcon);
                      ebiModule.gui.getVisualPanel(name).updateUI();
                           
                  }else{
                	 
                	  try {
  						    Thread.sleep(1000);
                      } catch (InterruptedException e) {
  						    e.printStackTrace();
                      }
                      ebiModule.gui.getVisualPanel(name).setModuleIcon(oldIcon);
                      ebiModule.gui.getVisualPanel(name).updateUI();
                  }
                }
            }
        };

        Thread tr = new Thread(run,"Save Module");
        tr.start();
    }

}
