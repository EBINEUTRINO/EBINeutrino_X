package ebiNeutrino.core.gui.Dialogs;

import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIEMailSendReciveStatus extends EBIDialogExt {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JButton jButtonCancel = null;

	private JProgressBar jProgressBar = null;
	
	private String       typeAction   = "";
	
	public boolean isBreak = false;

    private JLabel jLabel = null;

	/**
	 * @param owner
	 */
	public EBIEMailSendReciveStatus(String type) {
		super(null);
		type = typeAction;
        setName("EBIEMailSendReciveStatus");
        storeLocation(true);
		initialize();
		this.setResizable(false);
		this.setTitle(EBIPGFactory.getLANG("EBI_LANG_C_EMAIL_SEND_RECIEVE"));

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(521, 157);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
		    jLabel=new JLabel();
			jLabel.setBounds(new Rectangle(10, 11, 329, 30));
			jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_C_EMAIL_SEND_RECIEVE"));
			jLabel.setIcon(EBIConstant.ICON_RECIEVE_MAIL);
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJButtonCancel(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJProgressBar(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setBounds(new Rectangle(386, 89, 121, 27));
			jButtonCancel.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isBreak = true;
					setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(11, 46, 488, 22));
			jProgressBar.setStringPainted(true);
			jProgressBar.setString(this.typeAction);
			jProgressBar.setIndeterminate(true);
		}
		return jProgressBar;
	}

    public void setInfoText(String info){
         jLabel.setText(info);
    }

}
