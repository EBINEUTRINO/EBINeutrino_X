package ebiNeutrino.core.setup;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBISetup extends EBIDialog{

	private EBIVisualPanelTemplate jContentPane = null;
	private JMenuBar jJMenuBar            = null;
	private JMenu fileMenu           	  = null;
	private JMenu helpMenu           	  = null;
	private JMenuItem exitMenuItem   	  = null;
	private JMenuItem aboutMenuItem  	  = null;
	private JTabbedPane jTabbedPane  	  = null;
	public  EBISetupDB dbSetup 		 	  = null;
    public EBIPGFactory _ebifunction      = null;
    public boolean DBConfigured           = false;

	/**
	 * This is the default constructor
	 */
	public EBISetup(EBIPGFactory func) {
        super(null);
		storeLocation(true);
		storeSize(true);
		setModal(true);
		setResizable(false);
        setName("EBISetup");
        _ebifunction = func;
        dbSetup    = new EBISetupDB(this);
        getJTabbedPane().addTab("EBI Database setup",dbSetup);
		initialize();

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(575, 440);
		this.setContentPane(getJContentPane());
		this.setTitle("EBI Neutrino R1 Database Setup");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) { 
			jContentPane = new EBIVisualPanelTemplate(false);
            jContentPane.setClosable(true);
            jContentPane.setEnableChangeComponent(false);
			jContentPane.getPanel().setLayout(new BorderLayout());
            jContentPane.setModuleIcon(EBIConstant.ICON_APP);
            jContentPane.setModuleTitle("EBI Database setup");
			jContentPane.add(getJTabbedPane(), null);
            jContentPane.updateUI();

		}
		return jContentPane;
	}
	
	public JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setName("jTabbedPane");
			jTabbedPane.setTabPlacement(javax.swing.JTabbedPane.TOP);
		}
		return jTabbedPane;
	}
	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes jMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About...");
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIExceptionDialog.getInstance("EBI Neutrino R1 Database setup").Show(EBIMessage.INFO_MESSAGE);
				}
			});
		}
		return aboutMenuItem;
	}
}