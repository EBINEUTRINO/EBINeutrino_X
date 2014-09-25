package ebiNeutrinoSDK.gui.dialogs;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;

/**
 * Root password dialog  
 */

public class EBIAdminPassword extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton jButtonCancel = null;
	private JButton jButtonOk = null;
	private JPasswordField jPasswordField = null;
	public  boolean isOK = false;
	private EBIPGFactory ebiPGFactory = null;

    /**
	 * @param owner
	 */
	public EBIAdminPassword(EBIPGFactory owner) {
        super(owner.getMainFrame());
		ebiPGFactory = owner;
		setTitle(EBIPGFactory.getLANG("EBI_LANG_ROOT_PASSWORD"));
		this.setResizable(false);
		this.setModal(true);

		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(515, 207);
        this.setLocation((ebiPGFactory.getMainFrame().getWidth() / 2)-(getWidth() /2), (ebiPGFactory.getMainFrame().getHeight() / 2)-(getHeight() / 2));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel1=new JLabel();
			jLabel1.setBounds(new Rectangle(16, 74, 71, 22));
			jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_PASSWORD")+":");
			JLabel jLabel=new JLabel();
			jLabel.setBounds(new Rectangle(16, 13, 366, 35));
			jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_ROOT_PASSWORD"));
			jLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel.setIcon(EBIConstant.ICON_KEY);
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJButtonCancel(), null);
			jContentPane.add(getJButtonOk(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJPasswordField(), null);
            jContentPane.setBackground(EBIPGFactory.systemColor);
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
			jButtonCancel.setBounds(new Rectangle(366, 133, 121, 29));
			jButtonCancel.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					isOK = false;
					setVisible(false);
					dispose();
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jButtonOk	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton();
			jButtonOk.setBounds(new Rectangle(249, 133, 111, 29));
			jButtonOk.setText(EBIPGFactory.getLANG("EBI_LANG_OK"));
			jButtonOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					checkRoot();
				}
			});
		}
		return jButtonOk;
	}
	
	private void checkRoot(){
        ResultSet xset = null;
		try{
			 String passwd = ebiPGFactory.encryptPassword(jPasswordField.getText());
             PreparedStatement ps1 = ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM EBIUSER WHERE EBIUSER=? and PASSWD=?");
             ps1.setString(1, "root");
             ps1.setString(2, passwd);
             xset = ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);
			 xset.next();
			if(xset.getRow() >0){			
				isOK = true;
			}else{
			  EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PASSWORD_NOT_CORRECT")).Show(EBIMessage.ERROR_MESSAGE);
			}
			}catch(SQLException ex){
				EBIExceptionDialog.getInstance(ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
			}finally{
              if(xset != null){
                  try {
                      xset.close();
                  } catch (SQLException e) {
                      e.printStackTrace();
                  }
              }
            }
			
			setVisible(false);
	}

	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setBounds(new Rectangle(90, 75, 408, 21));
			jPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER ){
						checkRoot();
					}
				}
			});
		}
		return jPasswordField;
	}

}
