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
	 * @param  title 		: Title for the container
	 * @param  component : Component instance
	 * @param  icon		: Container Icon
	 * @param  mnemo_key	    : Mnemonic key
	 * @return : void
	 */
	public int addContainer(String title,JComponent component,ImageIcon icon,int mnemo_key);
	
	/**
	 * Add scrollable component to the EBINeutrino Container
	 * @param  title 		: Container Title
	 * @param  component : Component instance
	 * @param  icon		: Container Icon
	 * @param  mnemo_key	    : Mnemonic key
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

    public void loadSelectedListIndex(int index);

    public int getSelectedListIndex();

    public void setSelectedModIndex(int index);

}
