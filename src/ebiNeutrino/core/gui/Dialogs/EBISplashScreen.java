package ebiNeutrino.core.gui.Dialogs;

import ebiNeutrinoSDK.EBIPGFactory;

import java.awt.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

public class EBISplashScreen extends JWindow {

	private JPanel jContentPane = null;
	private JProgressBar jProgressBar = null;

	/**
	 * This is the default constructor
	 */
	public EBISplashScreen() {

        initialize();  
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = getSize();
        setAlwaysOnTop(false);
	    setLocation((d.width - frameSize.width) / 2 , ((d.height-150) - frameSize.height) / 2);
	     
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(485, 330);
		this.setContentPane(getJContentPane());
        this.getContentPane().setBackground(EBIPGFactory.systemColor);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel=new JLabel();
			jLabel.setText("");
			jLabel.setBounds(new Rectangle(0, -5, 485, 338));
            jLabel.setIcon(new ImageIcon("images/splash.png"));
            jContentPane = new JPanel();                        
			jContentPane.setLayout(null);
			jContentPane.add(getJProgressBar(), null);
			jContentPane.add(jLabel, null);
			
		}
		return jContentPane;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(17, 255, 251, 19));
			jProgressBar.setIndeterminate(true);
			jProgressBar.setStringPainted(true);
			jProgressBar.setString("EBI Neutrino R1 Loading...");
		}
		return jProgressBar;
	}

}
