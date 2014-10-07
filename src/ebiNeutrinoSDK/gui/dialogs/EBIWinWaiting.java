package ebiNeutrinoSDK.gui.dialogs;

import ebiNeutrinoSDK.EBIPGFactory;

import java.awt.*;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class EBIWinWaiting extends EBIDialog {

		private JPanel jPanel = null;
		public JProgressBar jProgressBar = null;
		/**
		 * This is the default constructor
		 */
		public EBIWinWaiting(String name) {
			super(null);
			initialize();
		    setAlwaysOnTop(true);
            setUndecorated(true);
            setName("EBIWinWaiting");
            jProgressBar.setString(name);
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		    Dimension frameSize = getSize();
		    setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2); 
		}
		

		/**
		 * This method initializes this
		 * 
		 * @return void
		 */
		private void initialize() {
			this.setSize(560, 75);
			this.setContentPane(getJPanel());
		}

		/**
		 * This method initializes jPanel	
		 * 	
		 * @return javax.swing.JPanel	
		 */
		private JPanel getJPanel() {
			if (jPanel == null) {
				jPanel = new JPanel();
				jPanel.setLayout(null);
				jPanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.black,1));
				jPanel.add(getJProgressBar(), null);

			}
			return jPanel;
		}

		/**
		 * This method initializes jProgressBar	
		 * 	
		 * @return javax.swing.JProgressBar	
		 */
		private JProgressBar getJProgressBar() {
			if (jProgressBar == null) {
				jProgressBar = new JProgressBar();
				jProgressBar.setBounds(new java.awt.Rectangle(17,25,518,21));
				jProgressBar.setIndeterminate(true);
				jProgressBar.setStringPainted(true);
			}
			return jProgressBar;
		}

}  

