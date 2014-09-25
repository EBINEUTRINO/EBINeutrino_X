package ebiNeutrino.core.setup;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class EBILanguageSetup extends EBIDialogExt {

	private EBIVisualPanelTemplate jContentPane = null;
	private JComboBox jComboBoxLanguage = null;
	private JButton jButton = null;

	public EBILanguageSetup() {
		super(null);
        setClosable(true);
		storeLocation(true);
		storeSize(true);
		initialize();

		parseLanguageFileFromDir();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(457, 187);
		this.setContentPane(getJContentPane());
		setTitle("Language Setup");
		setResizable(false);
		setModal(true);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = getSize();
	    setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2); 
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel1=new JLabel();
			jLabel1.setBounds(new Rectangle(9, 67, 81, 22));
			jLabel1.setText("Language:");
			JLabel jLabel=new JLabel();
			jLabel.setBounds(new Rectangle(10, 4, 372, 42));
			jLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			jLabel.setText("EBI Neutrino R1 Language Setup");
			jContentPane = new EBIVisualPanelTemplate();
            jContentPane.setEnableChangeComponent(false);
            jContentPane.setClosable(true);
			jContentPane.setLayout(null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJComboBoxLanguage(), null);
			jContentPane.add(getJButton(), null);
            jContentPane.setModuleIcon(EBIConstant.ICON_APP);
            jContentPane.setModuleTitle("EBI Neutrino R1 Language Setup");
		}
		return jContentPane;
	}

	
	private void parseLanguageFileFromDir(){
		String[] value  ;
		
		File dir = new File("language/");
		File files[] = dir.listFiles();
		
		String builder = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT")+",";
		for(int i=0; i<files.length; i++){
			try{
			 String lName;
			 if((lName = files[i].getName().substring(files[i].getName().lastIndexOf("_")+1)) != null ){
				 if(!"".equals(lName) && lName != null){
					 if((lName = lName.substring(0, lName.lastIndexOf("."))) != null){
						 if(!"".equals(lName)){
						   builder += lName;
						   if(i< files.length){
							  builder += ",";
						   }
						 }
					 }
				 }
			 }
			}catch(StringIndexOutOfBoundsException ex){}
		}
		value = builder.trim().split(",");
		this.jComboBoxLanguage.setModel(new javax.swing.DefaultComboBoxModel(value));
	}

	/**
	 * This method initializes jComboBoxLanguage	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBoxLanguage() {
		if (jComboBoxLanguage == null) {
			jComboBoxLanguage = new JComboBox();
			jComboBoxLanguage.setBounds(new Rectangle(92, 67, 335, 23));
            jComboBoxLanguage.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                   if(!jComboBoxLanguage.getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
                     jButton.setEnabled(true);
                   }else{
                      jButton.setEnabled(false); 
                   }
                }
            });
		}
		return jComboBoxLanguage;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(334, 118, 105, 26));
			jButton.setText("Apply");
            jButton.setEnabled(false);
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

                    if(!validateInput()){
						return;
					}
					EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
                    EBIPGFactory.selectedLanguage = jComboBoxLanguage.getSelectedItem().toString();
					properties.setValue("EBI_Neutrino_Language_File", "language/EBINeutrinoLanguage_"+jComboBoxLanguage.getSelectedItem().toString()+".properties");
					properties.saveProperties();
					setVisible(false);
				}
			});
		}
		return jButton;
	}



	private boolean validateInput() {
	
		if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(jComboBoxLanguage.getSelectedItem().toString())){
			EBIExceptionDialog.getInstance("Please select a language!").Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
}
