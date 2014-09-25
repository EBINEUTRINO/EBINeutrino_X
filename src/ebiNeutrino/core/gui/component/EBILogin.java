package ebiNeutrino.core.gui.component;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIRenderer.EBIButton;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIExtendedPanel;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class EBILogin extends JFrame {

    public JTextField jTextUser = null;
    private JPasswordField jPasswordField = null;
    private EBIButton jButtonLogin = null;
    private EBIButton jButtonCancel = null;
    public EBIMain main = null;
    private EBIExtendedPanel jPanelControl = null;
    

    /**
     * This is the default constructor
     */
    public EBILogin(EBIMain eb) {
        main = eb;
        initialize();
        initFocusTravesal();
        setTitle("EBI Neutrino R1 Login");
        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

        if (!"".equals(properties.getValue("EBI_Neutrino_Last_Logged_User")) ||
                !"null".equals(properties.getValue("EBI_Neutrino_Last_Logged_User"))) {

            jTextUser.setFocusable(false);
            jPasswordField.requestFocusInWindow();
            jPasswordField.requestFocus();
            jPasswordField.grabFocus();
            jTextUser.setFocusable(true);
        } else {
            jTextUser.requestFocusInWindow();
            jTextUser.grabFocus();
            jTextUser.requestFocus();
        }
        if(main.mng != null){
        	main.mng.onExit();
        	main.mng.releaseModule();
        }
        //checkUser();
    }

    private void initialize() {
        this.setLayout(null);
        this.add(getJPanelControl(), null);
        this.setBackground(EBIPGFactory.systemColor);
        this.requestFocusInWindow();

    }

    public void initFocusTravesal() {

        final KeyStroke KEYSTROKE_UP = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        final KeyStroke KEYSTROKE_DOWN = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        final KeyStroke KEYSTROKE_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        final KeyStroke KEYSTROKE_SHIFT_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK);
        //forward focus set
        Set<KeyStroke> forward = new HashSet<KeyStroke>();
        forward.add(KEYSTROKE_DOWN);
        forward.add(KEYSTROKE_TAB);
        this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        //backward focus set
        Set<KeyStroke> backward = new HashSet<KeyStroke>();
        backward.add(KEYSTROKE_UP);
        backward.add(KEYSTROKE_SHIFT_TAB);
        this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
    }

    /**
     * This method initializes jTextUser	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextUser() {
        if (jTextUser == null) {
            jTextUser = new JTextField();
            jTextUser.setBounds(new java.awt.Rectangle(117, 54, 280, 20));
            jTextUser.setText(EBIPGFactory.lastLoggedUser);
            jTextUser.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                        jPasswordField.requestFocus();
                    }
                }
            });
        }
        return jTextUser;
    }

    /**
     * This method initializes jPasswordField	
     * 	
     * @return javax.swing.JPasswordField	
     */
    private JPasswordField getJPasswordField() {
        if (jPasswordField == null) {
            jPasswordField = new JPasswordField();
            //jPasswordField.setText("ebineutrino");
            jPasswordField.setBounds(new java.awt.Rectangle(117, 86, 280, 20));
            jPasswordField.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        checkUser();
                    }
                    if (e.getKeyCode() == KeyEvent.VK_TAB || e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                        jTextUser.requestFocus();
                    }
                }
            });
        }
        return jPasswordField;
    }

    /**
     * This method initializes jButtonLogin	
     * 	
     * @return javax.swing.JButton	
     */
    private EBIButton getJButtonLogin() {
        if (jButtonLogin == null) {
            jButtonLogin = new EBIButton();
            jButtonLogin.setText(EBIPGFactory.getLANG("EBI_LANG_LOGIN"));
            jButtonLogin.setBounds(new java.awt.Rectangle(154, 140, 100, 25));
            jButtonLogin.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    checkUser();
                }
            });
        }
        return jButtonLogin;
    }

    public void checkUser() {
    	
        String user = jTextUser.getText().replaceAll("\'", " ");
        String password = String.valueOf(jPasswordField.getPassword()).replaceAll("\'", " ");

        boolean ret = main._ebifunction.checkIsValidUser(user, password);
        
        if (ret != false) {
        	main.splash.setVisible(true);
        	main.showBusinessModule();
            saveLastLoggedUser();
            setVisible(false);
            main.splash.setVisible(false);
            
        } else {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_USER_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
        }
    }

    /**
     * This method initializes jButtonAbbrechen	
     * 	
     * @return javax.swing.JButton	
     */
    private EBIButton getJButtonCancel() {
        if (jButtonCancel == null) {
            jButtonCancel = new EBIButton();
            jButtonCancel.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
            jButtonCancel.setBounds(new java.awt.Rectangle(264, 140, 110, 25));
            jButtonCancel.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_CLOSE")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true) {
                        setVisible(false);
                        System.exit(0);
                    }
                }
            });
        }
        return jButtonCancel;
    }

    /**
     * This method initializes jPanelControl	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelControl() {
        if (jPanelControl == null) {
            JLabel jLabel1=new JLabel();
            jLabel1.setBounds(new java.awt.Rectangle(30, 86, 82, 18));
            jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_PASSWORD"));
            JLabel jLabel=new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(30, 54, 81, 20));
            jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_USER"));
            jLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jPanelControl = new EBIExtendedPanel("EBI Neutrino Login","images/user.png");
            jPanelControl.setLayout(null);
            jPanelControl.setBounds(new java.awt.Rectangle(0, 0, 524, 217));
            jPanelControl.add(jLabel, null);
            jPanelControl.add(jLabel1, null);
            jPanelControl.add(getJButtonCancel(), null);
            jPanelControl.add(getJButtonLogin(), null);
            jPanelControl.add(getJPasswordField(), null);
            jPanelControl.add(getJTextUser(), null);

            JLabel jLabel4=new JLabel();
            jLabel4.setBounds(new java.awt.Rectangle(404, 113, 24, 24));
            jLabel4.setText("");
            JLabel jLabel3=new JLabel();
            jLabel3.setBounds(new java.awt.Rectangle(404, 80, 25, 27));
            jLabel3.setText("");
            jPanelControl.add(jLabel3, null);
            jPanelControl.add(jLabel4, null);
            jPanelControl.setBackground(EBIPGFactory.systemColor);

        }
        return jPanelControl;
    }

    private void saveLastLoggedUser() {
        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
        properties.setValue("EBI_Neutrino_Last_Logged_User", this.jTextUser.getText());
        properties.saveProperties();
    }

    public void setVisible(boolean visible){
        super.setVisible(visible);
        if(main.isVisible() && visible == true){
            main.setVisible(false);
        }
    }
}

