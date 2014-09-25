package ebiNeutrino.core.gui.Dialogs;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ebiNeutrino.core.EBIVersion;

public class EBIAboutPanel1 extends JPanel {

	private static final long serialVersionUID = 1L;
	private JEditorPane jTextArea = null;
	private JScrollPane jScrollPane = null;

	/**
	 * This is the default constructor
	 */
	public EBIAboutPanel1() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setLayout(new BorderLayout());
		this.add(getJScrollPane(), BorderLayout.CENTER);
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JEditorPane getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JEditorPane();
			jTextArea.setEditable(false);
			jTextArea.setContentType("text/html");
			jTextArea.setText("\t<center><b>EBI Neutrino R1 Release "+ EBIVersion.getInstance().getVersion()+" </b><br>EBI Neutrino R1 the ERP / CRM Framework<br><br>(c) EBI Neutrino R1 Community</center>  ");
		}
		return jTextArea;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

}
