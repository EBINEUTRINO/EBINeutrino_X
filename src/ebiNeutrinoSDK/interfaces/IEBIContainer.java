package ebiNeutrinoSDK.interfaces;

import ebiNeutrino.core.GUIRenderer.EBIMutableTreeNode;
import org.jdesktop.swingx.JXPanel;

import javax.swing.*;

/**
 * Interface that help manipulate the EBIContainer (JTabbedPane)
 *
 */

public interface IEBIContainer {
	
	/**
	 * Add a component to the EBINeutrino Container
	 * @param  String title 		: Title for the container
	 * @param  JComponent component : Component instance
	 * @param  ImageIcon icon		: Container Icon 
	 * @param  int mnemo_key	    : Mnemonic key 
	 * @return : void
	 */
	public int addContainer(String title,JComponent component,ImageIcon icon,int mnemo_key);
	
	/**
	 * Add scrollable component to the EBINeutrino Container
	 * @param  String title 		: Container Title
	 * @param  JComponent component : Component instance
	 * @param  ImageIcon icon		: Container Icon 
	 * @param  int mnemo_key	    : Mnemonic key 
	 * @return index
	 */
	            
	public int addScrollableContainer(String title,JComponent component,ImageIcon icon,int mnemo_key);
	

	/**
	 * Remove all from Container
	 * @return 
	 */
	
	public void removeAllFromContainer();

	
	/**
	 * get the mainPane instance
	 * @return  JTabbedPane
	 */	
	
	public JXPanel getMainPaneInstance();


    public void setSelectedListIndex(int index);


}
