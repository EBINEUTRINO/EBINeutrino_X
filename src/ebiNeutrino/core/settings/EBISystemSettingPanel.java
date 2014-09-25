package ebiNeutrino.core.settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.*;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIRenderer.EBIButton;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.*;

public class EBISystemSettingPanel extends JPanel {

    private JPanel jPanelSysAlg = null;
    private JTextField jtextPDFPath = null;
    private JButton jButtonSelectPath = null;
    private JTextField jtextBrowserPath = null;
    private JButton jButtonBrowserPath = null;
    private JPanel generalSettings = null;
    private JTextField jTextEditorPath = null;
    private JButton jButtonOpenTextEditor = null;
    private EBIMain ebiMain = null;
    private JComboBox jComboBoxLanguage = null;
    private JComboBox jComboBoxEMailClient = null;
    private JComboBox jComboDateFormat = null;
    private JCheckBox isB2C = null;
    private JComboBox systemDate = null;
    private EBIButton updateYear = null;
    private String selectedYear = "";


    /**
     * This is the default constructor
     */
    public EBISystemSettingPanel(EBIMain main) {
        super();
        ebiMain = main;
        initialize();
        setBackground(EBIPGFactory.systemColor);

        parseLanguageFileFromDir();
        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

        if (!"".equals(properties.getValue("EBI_Neutrino_PDF")) ) {
            jtextPDFPath.setText(properties.getValue("EBI_Neutrino_PDF"));
        }
        if (!"".equals(properties.getValue("EBI_Neutrino_Browser"))) {
            jtextBrowserPath.setText(properties.getValue("EBI_Neutrino_Browser"));
        }
        if (!"".equals(properties.getValue("EBI_Neutrino_Language_File"))) {
            parseLanguageFromCombo(properties.getValue("EBI_Neutrino_Language_File"));
        }
        if (!"".equals(properties.getValue("EBI_Neutrino_TextEditor_Path"))) {
            this.jTextEditorPath.setText(properties.getValue("EBI_Neutrino_TextEditor_Path"));
        }
        if (!"".equals(properties.getValue("EBI_Neutrino_Date_Format"))) {
            this.jComboDateFormat.getEditor().setItem(properties.getValue("EBI_Neutrino_Date_Format"));
        }
        if (!"".equals(properties.getValue("EBI_Neutrino_Email_Client"))) {
            this.jComboBoxEMailClient.setSelectedItem(properties.getValue("EBI_Neutrino_Email_Client"));
        }

        try {

            if(!"null".equals(properties.getValue("SELECTED_SYSTEMYEAR_TEXT")) && !"".equals(properties.getValue("SELECTED_SYSTEMYEAR_TEXT"))){
                systemDate.setSelectedItem(properties.getValue("SELECTED_SYSTEMYEAR_TEXT"));
            }

            if(!"null".equals(properties.getValue("SYSTEMYEAR_TEXT")) && !"".equals(properties.getValue("SYSTEMYEAR_TEXT"))){

                String [] years = properties.getValue("SYSTEMYEAR_TEXT").split(",");
                if(years != null){
                    for(int i = 0; i< years.length; i++){
                        if(years[i] != null){
                            systemDate.insertItemAt(years[i],i);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if("true".equals(properties.getValue("EBI_Neutrino_UserAsB2C"))) {
            this.isB2C.setSelected(true);
            EBIPGFactory.USE_ASB2C = true;
        }else{
            this.isB2C.setSelected(false);
            EBIPGFactory.USE_ASB2C = false; 
        }

        EBISystemSetting.selectedModule = 2;

    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
		// Draw bg top
        Color startColor = new Color(250,250,250);
        Color endColor = new Color(230,230,230);

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(255, 255, endColor, 38, 255, startColor);
        g2.setPaint(gradient);
		g2.fillRect(0, 0, getWidth(), 45);

        Color sColor = new JPanel().getBackground();
        Color eColor = sColor;

        // A non-cyclic gradient
        GradientPaint gradient1 = new GradientPaint(0,0, sColor, getWidth(), 38, eColor);
        g2.setPaint(gradient1);
        g2.setColor(EBIPGFactory.systemColor);
        g.fillRect(0, 46, getWidth(), getHeight());

        g2.setColor(EBIPGFactory.systemColor);

        g.setColor(new Color(220,220,220));
		g.drawLine(0, 45, getWidth(), 45);
		setOpaque(false);
    }

    private void parseLanguageFileFromDir() {
        String[] value  ;

        File dir = new File("language/");
        File files[] = dir.listFiles();

        String builder = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT") + ",";
        for (int i = 0; i < files.length; i++) {
            try {
                String lName;
                if ((lName = files[i].getName().substring(files[i].getName().lastIndexOf("_") + 1)) != null) {
                    if (!"".equals(lName) && lName != null) {
                        if ((lName = lName.substring(0, lName.lastIndexOf("."))) != null) {
                            if (!"".equals(lName)) {
                                builder += lName;
                                if (i < files.length) {
                                    builder += ",";
                                }
                            }
                        }
                    }
                }
            } catch (StringIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
        value = builder.trim().split(",");
        this.jComboBoxLanguage.setModel(new javax.swing.DefaultComboBoxModel(value));
    }

    private void parseLanguageFromCombo(String name) {
        try {
            String lName;
            if ((lName = name.substring(name.lastIndexOf("_") + 1)) != null) {
                if (!"".equals(lName) && lName != null) {
                    if ((lName = lName.substring(0, lName.lastIndexOf("."))) != null) {
                        if (!"".equals(lName)) {
                            this.jComboBoxLanguage.setSelectedItem(lName);
                        }
                    }
                }
            }
        } catch (StringIndexOutOfBoundsException ex) {
        }
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        JLabel jLabel1=new JLabel();
        jLabel1.setBounds(new java.awt.Rectangle(70, 15, 120, 20));
        jLabel1.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_SYSTEM_SETTING"));
        JLabel jLabel=new JLabel();
        jLabel.setBounds(new java.awt.Rectangle(20, 10, 30, 30));
        jLabel.setIcon(EBIConstant.ICON_SETTING);
        jLabel.setText("");
        this.setLayout(null);
        this.setSize(500, 346);
        this.add(jLabel, null);
        this.add(jLabel1, null);
        this.add(getJPanelSysAlg(), null);
        this.add(getJPanelLogo(), null);

    }

    /**
     * This method initializes jPanelSysAlg	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelSysAlg() {
        if (jPanelSysAlg == null) {
            JLabel jLabel6=new JLabel();
            jLabel6.setBounds(new Rectangle(10, 83, 126, 20));
            jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel6.setFont(new Font("Dialog", Font.PLAIN, 12));
            jLabel6.setText(EBIPGFactory.getLANG("EBI_LANG_TEXT_EDITOR_PATH"));
            JLabel jLabel3=new JLabel();
            jLabel3.setBounds(new Rectangle(9, 59, 126, 20));
            jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel3.setFont(new Font("Dialog", Font.PLAIN, 12));
            jLabel3.setText(EBIPGFactory.getLANG("EBI_LANG_BROWSER_PATH"));
            JLabel jLabel2=new JLabel();
            jLabel2.setBounds(new Rectangle(9, 34, 126, 20));
            jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel2.setFont(new Font("Dialog", Font.PLAIN, 12));
            jLabel2.setText(EBIPGFactory.getLANG("EBI_LANG_PDF_VIEWER_PATH"));

            JLabel jLabel8=new JLabel();
            jLabel8.setBounds(new Rectangle(9, 108, 126, 20));
            jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
            jLabel8.setFont(new Font("Dialog", Font.PLAIN, 12));
            jLabel8.setText(EBIPGFactory.getLANG("EBI_LANG_EMAIL_CLIENT"));

            jPanelSysAlg = new JPanel();
            jPanelSysAlg.setOpaque(false);
            jPanelSysAlg.setLayout(null);
            jPanelSysAlg.setBounds(new Rectangle(15, 70, 560, 150));
            jPanelSysAlg.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_SYSTEMS_PATH"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12)));
            jPanelSysAlg.add(jLabel2, null);
            jPanelSysAlg.add(getjTextPDFPath(), null);
            jPanelSysAlg.add(getJButtonSelectPath(), null);
            jPanelSysAlg.add(jLabel3, null);
            jPanelSysAlg.add(getJComboBrowserPath(), null);
            jPanelSysAlg.add(getJButtonBrowserPath(), null);
            jPanelSysAlg.add(jLabel6, null);
            jPanelSysAlg.add(getJTextEditorPath(), null);
            jPanelSysAlg.add(getJButtonOpenTextEditor(), null);
            jPanelSysAlg.add(jLabel8, null);
            jPanelSysAlg.add(getJComboBoxEMailClient(), null);
        }
        return jPanelSysAlg;
    }

    /**
     * This method initializes jComboAdobePath	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JTextField getjTextPDFPath() {
        if (jtextPDFPath == null) {
            jtextPDFPath = new JTextField();
            jtextPDFPath.setBounds(new java.awt.Rectangle(139, 34, 314, 20));
            jtextPDFPath.setEditable(true);
        }
        return jtextPDFPath;
    }

    /**
     * This method initializes jButtonSelectPath	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonSelectPath() {
        if (jButtonSelectPath == null) {
            jButtonSelectPath = new JButton();
            jButtonSelectPath.setBounds(new java.awt.Rectangle(458, 34, 35, 20));
            jButtonSelectPath.setText("...");
            jButtonSelectPath.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File file = ebiMain._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);

                    if (file != null) {
                        jtextPDFPath.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return jButtonSelectPath;
    }

    /**
     * This method initializes jComboBrowserPath	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JTextField getJComboBrowserPath() {
        if (jtextBrowserPath == null) {
            jtextBrowserPath = new JTextField();
            jtextBrowserPath.setEditable(true);
            jtextBrowserPath.setBounds(new Rectangle(139, 59, 314, 20));
        }
        return jtextBrowserPath;
    }

    /**
     * This method initializes jButtonBrowserPath	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonBrowserPath() {
        if (jButtonBrowserPath == null) {
            jButtonBrowserPath = new JButton();
            jButtonBrowserPath.setBounds(new Rectangle(458, 59, 35, 20));
            jButtonBrowserPath.setText("...");
            jButtonBrowserPath.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File file = ebiMain._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);
                    if (file != null) {
                        jtextBrowserPath.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return jButtonBrowserPath;
    }

    public void saveSystemSetting() {
        boolean isSaved = false;
        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

        if (!"".equals(jtextPDFPath.getText())) {
            properties.setValue("EBI_Neutrino_PDF", jtextPDFPath.getText());
        }
        if (!"".equals(jtextBrowserPath.getText()) ) {
            properties.setValue("EBI_Neutrino_Browser", jtextBrowserPath.getText());
        }
        if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(this.jComboBoxLanguage.getSelectedItem().toString())) {
            properties.setValue("EBI_Neutrino_Language_File", "language/EBINeutrinoLanguage_" + this.jComboBoxLanguage.getSelectedItem().toString() + ".properties");
            isSaved = true;
        }
        if (!"".equals(this.jTextEditorPath.getText())) {
            properties.setValue("EBI_Neutrino_TextEditor_Path", this.jTextEditorPath.getText());
        }
        if (this.jComboDateFormat.getEditor().getItem() != null) {
            properties.setValue("EBI_Neutrino_Date_Format", this.jComboDateFormat.getEditor().getItem().toString());
        }

        if(this.isB2C.isSelected()){
            properties.setValue("EBI_Neutrino_UserAsB2C", "true");       
        }else{
            properties.setValue("EBI_Neutrino_UserAsB2C", "false");
        }

        if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(jComboBoxEMailClient.getSelectedItem())){
            properties.setValue("EBI_Neutrino_Email_Client",  jComboBoxEMailClient.getSelectedItem().toString());
        }

        properties.saveProperties();
        setYearsToProperties();

        if (isSaved == true) {
            ebiMain._ebifunction.reloadTranslationSystem();
        }

        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_INFO_SETTING_SAVED")).Show(EBIMessage.INFO_MESSAGE);
    }



    /**
     * This method initializes jTextEditorPath	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextEditorPath() {
        if (jTextEditorPath == null) {
            jTextEditorPath = new JTextField();
            jTextEditorPath.setBounds(new Rectangle(139, 83, 314, 20));
           
        }
        return jTextEditorPath;
    }

    private JComboBox getJComboBoxEMailClient() {
        if (jComboBoxEMailClient == null) {
            jComboBoxEMailClient = new JComboBox();
            jComboBoxEMailClient.setBounds(new Rectangle(139, 108, 314, 20));
            jComboBoxEMailClient.addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
            jComboBoxEMailClient.addItem("Mozilla Thunderbird");
            jComboBoxEMailClient.addItem("Microsoft Outlook");
        }
        return jComboBoxEMailClient;
    }





    /**
     * This method initializes jButtonOpenTextEditor	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonOpenTextEditor() {
        if (jButtonOpenTextEditor == null) {
            jButtonOpenTextEditor = new JButton();
            jButtonOpenTextEditor.setBounds(new Rectangle(458, 84, 35, 20));
            jButtonOpenTextEditor.setText("...");
            jButtonOpenTextEditor.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    File file = ebiMain._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);
                    if (file != null) {
                        jTextEditorPath.setText(file.getAbsolutePath());
                    }
                }
            });
        }
        return jButtonOpenTextEditor;
    }


    /**
     * This method initializes generalSettings
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanelLogo() {
        if (generalSettings == null) {
            JLabel jLabel5=new JLabel();
            jLabel5.setBounds(new Rectangle(19, 52, 119, 20));
            jLabel5.setText(EBIPGFactory.getLANG("EBI_LANG_DATE_FORMAT"));
            jLabel5.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel5.setFont(new Font("Dialog", Font.PLAIN, 12));
            JLabel jLabel4=new JLabel();
            jLabel4.setBounds(new Rectangle(19, 27, 119, 21));
            jLabel4.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel4.setText(EBIPGFactory.getLANG("EBI_LANG_LANGUAGE"));
            jLabel4.setFont(new Font("Dialog", Font.PLAIN, 12));
            JLabel jLabel8=new JLabel();
            jLabel8.setBounds(new Rectangle(19, 79, 119, 21));
            jLabel8.setHorizontalAlignment(SwingConstants.RIGHT);
            jLabel8.setText(EBIPGFactory.getLANG("EBI_LANG_SYSTEM_DATE"));
            jLabel8.setFont(new Font("Dialog", Font.PLAIN, 12));
            generalSettings = new JPanel();
            generalSettings.setLayout(null);
            generalSettings.setOpaque(false);
            generalSettings.setBounds(new Rectangle(15, 225, 480, 140));
            generalSettings.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_PANEL_NAME_GENERAL_SETTING"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            generalSettings.add(jLabel4, null);
            generalSettings.add(jLabel8, null);
            generalSettings.add(getJComboBoxLanguage(), null);
            generalSettings.add(jLabel5, null);
            generalSettings.add(getJComboDateFormat(), null);
            generalSettings.add(getUserAsB2C(),null);
            generalSettings.add(getEBISystemDate(),null);
            generalSettings.add(getUpdateYear(),null);
        }
        return generalSettings;
    }

    /**
     * This method initializes jComboBoxLanguage	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJComboBoxLanguage() {
        if (jComboBoxLanguage == null) {
            jComboBoxLanguage = new JComboBox();
            jComboBoxLanguage.setBounds(new Rectangle(142, 27, 214, 20));
        }
        return jComboBoxLanguage;
    }

    /**
     * This method initializes jComboDateFormat	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJComboDateFormat() {
        if (jComboDateFormat == null) {
            jComboDateFormat = new JComboBox();
            jComboDateFormat.setBounds(new Rectangle(142, 52, 214, 20));
            jComboDateFormat.setEditable(true);
            jComboDateFormat.addItem("dd.MM.yyyy");
            jComboDateFormat.addItem("MM.dd.yyyy");
            jComboDateFormat.addItem("yyyy.mm.dd");
            jComboDateFormat.addItem("dd/MM/yyyy");
            jComboDateFormat.addItem("MM/dd/yyyy");
            jComboDateFormat.addItem("yyyy/mm/dd");

            jComboDateFormat.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIPGFactory.DateFormat = jComboDateFormat.getSelectedItem().toString();
                }
            });
        }
        return jComboDateFormat;
    }

    private JComboBox getEBISystemDate() {
        if (systemDate == null) {
            systemDate = new JComboBox();
            systemDate.setEditable(true);
            systemDate.setBounds(new Rectangle(142, 79, 214, 20));
            systemDate.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(((JComboBox)e.getSource()).getSelectedIndex() != -1){
                       selectedYear = ((JComboBox)e.getSource()).getSelectedItem().toString();
                    }
                }
            });

        }
        return systemDate;
    }

    private EBIButton getUpdateYear() {
        if (updateYear == null) {
            updateYear = new EBIButton();
            updateYear.setBounds(new Rectangle(370, 79, 100, 20));
            updateYear.setText(EBIPGFactory.getLANG("EBI_LANG_UPDATE"));
            updateYear.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setYearsToProperties();
                }
            });
        }
        return updateYear;
    }

    public void setYearsToProperties(){
        if (!validateDateInput()) {
            return;
        }
        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
        if (systemDate.getItemCount() >= 1) {
            boolean isAvailable = false;
            if (!"".equals(systemDate.getEditor().getItem())) {
                for (int i = 0; i <= systemDate.getItemCount(); i++) {
                    if (systemDate.getEditor().getItem().equals(systemDate.getItemAt(i))) {
                        isAvailable = true;
                        break;
                    }
                }
                if (!isAvailable) {
                    systemDate.addItem(systemDate.getEditor().getItem().toString());
                }
            } else {
                if (!"".equals(selectedYear)) {
                    systemDate.removeItem(selectedYear);
                }
            }
        } else {
            if (!"".equals(systemDate.getEditor().getItem())) {
                systemDate.addItem(systemDate.getEditor().getItem().toString());
            }
        }

        // create comma separated value
        String vSave = "";
        if (systemDate.getItemCount() > 0) {

            for (int i = 0; i < systemDate.getItemCount(); i++) {

                if (i < systemDate.getItemCount() - 1) {
                    vSave += systemDate.getItemAt(i).toString() + ",";
                } else {
                    vSave += systemDate.getItemAt(i).toString();
                }
            }
        }

        //  Sort
        if (systemDate.getItemCount() > 0) {

            String[] avalItems = vSave.split(",");
            Arrays.sort(avalItems);
            String selected = systemDate.getSelectedItem().toString();
            systemDate.removeAllItems();
            vSave = "";
            for (int i = 0; i < avalItems.length; i++) {
                systemDate.addItem(avalItems[i]);
                if (i < avalItems.length - 1) {
                    vSave += avalItems[i] + ",";
                } else {
                    vSave += avalItems[i];
                }
            }
            systemDate.setSelectedItem(selected);
            properties.setValue("SYSTEMYEAR_TEXT", vSave);
        }

        properties.setValue("SELECTED_SYSTEMYEAR_TEXT", systemDate.getEditor().getItem().toString());
        properties.saveProperties();
        ebiMain._ebifunction.updateSystemYears();
        ebiMain.mng.reloadSelectedModule();
    }

    private JCheckBox getUserAsB2C() {
        if (isB2C == null) {
            isB2C = new JCheckBox();
            isB2C.setBounds(new Rectangle(148, 108, 200, 20));
            isB2C.setText(EBIPGFactory.getLANG("EBI_LANG_USE_AS_B2C"));
            isB2C.setFocusTraversalKeysEnabled(false);
            isB2C.setOpaque(false);
            isB2C.setFont(new Font("Dialog", Font.PLAIN, 12));
        }
        return isB2C;
    }

    public boolean validateDateInput(){
        if(!"".equals(systemDate.getEditor().getItem().toString())){
            if(systemDate.getEditor().getItem().toString().length() < 4){
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_MESSAGE_VALID_YEAR")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }

            try{
                Integer.parseInt(systemDate.getEditor().getItem().toString());
            }catch(NumberFormatException ex){
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_MESSAGE_VALID_YEAR")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }
}