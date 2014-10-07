package ebiNeutrino.core.setup;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;
import ebiNeutrinoSDK.utils.Encrypter;



/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Description:
 * This Dialog setup the EBI Neutrino Database
 *
 */

public class EBISetupDB extends JPanel {
	private JTextField jTextUser = null;
	private JTextField jTexPassword = null;
	private JButton jButtonAbbrechen = null;
	private JButton jButtonGenerating = null;
	private JTextField jTextIP = null;
	private JButton jButtonTest = null;
	private EBISetup setup = null;
	private JButton jButtonO = null;
	private JPopupMenu.Separator jSepara = null;
	private JComboBox jComboDatabaseDriver = null;
	private JComboBox jTextDatabaseType = null;
	private JTextField jTextSIDCatalog = null;
	private JLabel jLabel7 = null;
	private JButton jButtonImportSchema = null;
	private Connection conn = null;
	private String databaseType = "";

    /**
	 * This is the default constructor
	 */
	public EBISetupDB(EBISetup setUp) {
		super();
		setup = setUp;
		initialize();
		jComboDatabaseDriver.addItem("Please select");
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		jLabel7 = new JLabel();
		jLabel7.setBounds(new Rectangle(443, 99, 115, 20));
		jLabel7.setText("SID");
		jLabel7.setVisible(false);
		JLabel jLabel6=new JLabel();
		jLabel6.setBounds(new Rectangle(15, 100, 126, 19));
		jLabel6.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel6.setText("Database name:");
		JLabel jLabel3=new JLabel();
		jLabel3.setBounds(new Rectangle(15, 130, 126, 19));
		jLabel3.setHorizontalTextPosition(SwingConstants.TRAILING);
		jLabel3.setHorizontalAlignment(SwingConstants.RIGHT);
		jLabel3.setText("Database driver:");
		this.setLayout(null);
		this.setSize(568, 352);

        JLabel jLabel5=new JLabel();
		jLabel5.setBounds(new Rectangle(19, 2, 81, 51));
		jLabel5.setText("");

        JLabel jLabel4=new JLabel();
		jLabel4.setBounds(new Rectangle(15, 159, 126, 19));
		jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel4.setText("Host/IP:");

        JLabel jLabel1=new JLabel();
		jLabel1.setBounds(new Rectangle(15, 190, 126, 19));
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel1.setText("User:");

		JLabel jLabel2=new JLabel();
		jLabel2.setBounds(new Rectangle(15, 220, 126, 19));
		jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		jLabel2.setText("Password:");

		JLabel jLabel=new JLabel();
		jLabel.setBounds(new Rectangle(95, 11, 314, 33));
		jLabel.setFont(new Font("Dialog", Font.BOLD, 14));
		jLabel.setText("EBI Neutrino database setup");

        this.add(jLabel, null);
		this.add(jLabel1, null);
		this.add(jLabel2, null);
		this.add(getJTextUser(), null);
		this.add(getJTexPassword(), null);
		this.add(getJButtonAbbrechen(), null);
		this.add(getJButtonSpeichern(), null);
		this.add(jLabel4, null);
		this.add(getJTextIP(), null);
		this.add(getJButtonTest(), null);
		this.add(jLabel5, null);
		this.add(getJButtonO(), null);
		this.add(getJSepara(), null);
		this.add(jLabel3, null);
		this.add(getJComboDatabaseDriver(), null);
		this.add(jLabel6, null);
		this.add(getJTextDatabaseName(), null);
		this.add(getJTextOracleSID(), null);
		this.add(jLabel7, null);
		this.add(getJButtonImportSchema(), null);
	}
	
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		// Draw bg top
        Color startColor = new Color(195,212,232);
        Color endColor = new Color(48,78,112);

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, 250, endColor);
        g2.setPaint(gradient);
		g2.fillRect(0, 0, getWidth(), 60);

        Color sColor = new JPanel().getBackground();
        Color eColor = sColor;

        // A non-cyclic gradient
        GradientPaint gradient1 = new GradientPaint(0,0, sColor, getWidth(), 60, eColor);
        g2.setPaint(gradient1);
        
        g.fillRect(0, 61, getWidth(), getHeight());

        //g2.drawImage(EBIConstant.NEUTRINO_HEADER.getImage(), 0, 0, null);
        g.setColor(new Color(220,220,220));
		g.drawLine(0, 60, getWidth(), 60);
		setOpaque(false);
	}
	/**
	 * This method initializes jTextUser	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextUser() {
		if (jTextUser == null) {
			jTextUser = new JTextField();
			jTextUser.setBounds(new Rectangle(144, 189, 307, 20));
		}
		return jTextUser;
	}

	/**
	 * This method initializes jTexPassword	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTexPassword() {
		if (jTexPassword == null) {
			jTexPassword = new JTextField();
			jTexPassword.setBounds(new Rectangle(144, 219, 309, 20));
		}
		return jTexPassword;
	}

	/**
	 * This method initializes jButtonAbbrechen	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAbbrechen() {
		if (jButtonAbbrechen == null) {
			jButtonAbbrechen = new JButton();
			jButtonAbbrechen.setBounds(new Rectangle(436, 314, 123, 25));
			jButtonAbbrechen.setText("Exit");
			jButtonAbbrechen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					 if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_CLOSE")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
						 System.exit(0);
					 }
				}
			});
		}
		return jButtonAbbrechen;
	}

	/**
	 * This method initializes jButtonSpeichern	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonSpeichern() {
		if (jButtonGenerating == null) {
			jButtonGenerating = new JButton();
			jButtonGenerating.setBounds(new Rectangle(191, 250, 133, 25));
			jButtonGenerating.setText("Generating");
			jButtonGenerating.setEnabled(false);
			jButtonGenerating.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!checkField()){
						return;
					}
					try {
				        // Create encrypter/decrypter class
				        Encrypter encrypter = new Encrypter("EBINeutrino");
				    
				        // Encrypt
				        String Pwdencrypted = encrypter.encrypt(jTexPassword.getText());
				        String Usrencrypted = encrypter.encrypt(jTextUser.getText().trim());

                        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
                        properties.setValue("EBI_Neutrino_Database_Driver",jComboDatabaseDriver.getSelectedItem().toString());
                        properties.setValue("EBI_Neutrino_Database", jTextDatabaseType.getSelectedItem().toString().trim());
                        properties.setValue("EBI_Neutrino_Database_UpperCase", "yes");
                        properties.setValue("EBI_Neutrino_Database_Name", jTextSIDCatalog.getText().trim());
                        properties.setValue("EBI_Neutrino_Password", Pwdencrypted);
                        properties.setValue("EBI_Neutrino_User", Usrencrypted);
                        properties.setValue("EBI_Neutrino_Host", jTextIP.getText().trim());
                        properties.setValue("EBI_Neutrino_Oracle_SID", jTextSIDCatalog.getText().trim());

                        properties.saveProperties();

                        EBIExceptionDialog.getInstance("Database connection data, are saved successfully!").Show(EBIMessage.INFO_MESSAGE);
                        jButtonO.setEnabled(true);
                        jButtonImportSchema.setEnabled(true);
                        configureHibernateFiles();
                        checkIfSchemaExist();



                        encrypter = null;
                        setup.DBConfigured = true;

				    } catch (Exception ex) {
                        ex.printStackTrace();
				    }
				}

			});
		}
		return jButtonGenerating;
	}


     public void configureHibernateFiles(){

        File dir = new File("hibernate/");

        FilenameFilter filter = new FilenameFilter() {
				     public boolean accept(File dir, String name) {
				            return name.endsWith(".hbm.xml");
				     }
	    };

        String[] children = dir.list(filter);
        if (children == null) {
            EBIExceptionDialog.getInstance("Critical Error: No Hibernate files exsist system will exit now! ").Show(EBIMessage.ERROR_MESSAGE);
            System.exit(1);
        } else {

            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];

                try {
                             File xml  = new File("./hibernate/"+filename);
                             FileReader reader = new FileReader(xml.getAbsolutePath());
                             BufferedReader in = new BufferedReader(reader);

                             String string;
                             StringBuffer newFile = new StringBuffer();
                             while ((string = in.readLine()) != null) {
                                 int ind;
                                 if((ind = string.indexOf("table=")) != -1){
                                    String tmp =  string.substring(ind+7);
                                    tmp = tmp.substring(0,tmp.indexOf("\""));
                                    string = string.replace("table=\""+tmp+"\"","table=\""+tmp.toUpperCase()+"\"");
                                 }

                                newFile.append(string);
                                newFile.append("\n");

                             }
                             in.close();

                             FileWriter writer = new FileWriter(xml.getAbsolutePath());
                             writer.write(newFile.toString().replaceAll("catalog=\"(.*)\"", "catalog=\""+jTextSIDCatalog.getText()+"\""));
                             writer.close();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                             e.printStackTrace();
                        }
                }
          }
    }

	private void checkIfSchemaExist() throws Exception {

        if(EBIExceptionDialog.getInstance("Would you like to import the EBI Neutrino database schema?").Show(EBIMessage.INFO_MESSAGE_YESNO)){
             setup._ebifunction.getIEBIDatabase().setActiveConnection(conn);
             new EBISchemaImport(setup._ebifunction,databaseType,this.jTextSIDCatalog.getText(),true).setVisible(true);
             setup._ebifunction.getIEBIDatabase().getActiveConnection().setCatalog(jTextSIDCatalog.getText().trim());
        }
	}
	
	private boolean checkField(){
		
		if("".equals(jTextDatabaseType.getSelectedItem().toString())){
			EBIExceptionDialog.getInstance("Please select the database!").Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
		if("".equals(jComboDatabaseDriver.getSelectedItem().toString())){
			EBIExceptionDialog.getInstance("Please select the database driver!").Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
		if(jTextSIDCatalog.isVisible()){
			if("".equals(jTextSIDCatalog.getText())){
				EBIExceptionDialog.getInstance("Please insert the Catalog or SID for connecting to a database!").Show(EBIMessage.ERROR_MESSAGE);
				return false;
			}
		}
		if("".equals(this.jTextIP.getText())){
			EBIExceptionDialog.getInstance("Please insert the database host!").Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
		if("".equals(this.jTextUser.getText())){
			EBIExceptionDialog.getInstance("Please insert the database user!").Show(EBIMessage.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	/**
	 * This method initializes jTextIP	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextIP() {
		if (jTextIP == null) {
			jTextIP = new JTextField();
			jTextIP.setBounds(new Rectangle(144, 159, 307, 20));
		}
		return jTextIP;
	}

	/**
	 * This method initializes jButtonTest	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonTest() {
		if (jButtonTest == null) {
			jButtonTest = new JButton();
			jButtonTest.setBounds(new Rectangle(16, 250, 169, 24));
			jButtonTest.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jButtonTest.setText("Test Connection");
			jButtonTest.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!checkField()){
						return;
					}
				
					try { 
			            Class.forName(jComboDatabaseDriver.getSelectedItem().toString()).newInstance();
			            
			        } catch (Exception ex) { 
			        	EBIExceptionDialog.getInstance("ERROR : Database driver was not found! ").Show(EBIMessage.ERROR_MESSAGE);
			        	return;
			        }
			        try {
				    	 
				          String conn_url = null;
				          String dbType = jTextDatabaseType.getSelectedItem().toString().toLowerCase();
				          String host = jTextIP.getText();
				          
				          if("mysql".equals(jTextDatabaseType.getSelectedItem().toString().toLowerCase())){
				        	  conn_url = "jdbc:"+dbType+"://"+host+"/?useUnicode=true&characterEncoding=utf8";
				          }else if("oracle".equals(jTextDatabaseType.getSelectedItem().toString().toLowerCase())){
				        	  conn_url = "jdbc:"+dbType+":thin:@"+host+":"+ jTextSIDCatalog.getText();
				          }
				         
				          conn =  DriverManager.getConnection(conn_url,jTextUser.getText() , jTexPassword.getText());
			                
                          EBIExceptionDialog.getInstance("Connection is ok!").Show(EBIMessage.INFO_MESSAGE);
			    
			              jButtonGenerating.setEnabled(true);
			        } catch (Exception ex) {
			        	 EBIExceptionDialog.getInstance("Connection Error: \nCheck your connection data also check if your database is running! \n\n"+EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
			        	 jButtonGenerating.setEnabled(false);
			        }
				}
			});
		}
		return jButtonTest;
	}

	/**
	 * This method initializes jButtonO	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonO() {
		if (jButtonO == null) {
			jButtonO = new JButton();
			jButtonO.setBounds(new Rectangle(308, 314, 123, 25));
			jButtonO.setText("Ok");
			jButtonO.setEnabled(false);
			jButtonO.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    setup.setVisible(false);
				}
			});
		}
		return jButtonO;
	}

	/**
	 * This method initializes jSepara	
	 * 	
	 * @return javax.swing.JPopupMenu.Separator	
	 */
	private JPopupMenu.Separator getJSepara() {
		if (jSepara == null) {
			jSepara = new JPopupMenu.Separator();
			jSepara.setBounds(new Rectangle(9, 281, 553, 11));
		}
		return jSepara;
	}

	/**
	 * This method initializes jComboDatabaseDriver	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboDatabaseDriver() {
		if (jComboDatabaseDriver == null) {
			jComboDatabaseDriver = new JComboBox();
			jComboDatabaseDriver.setBounds(new Rectangle(144, 130, 150, 20));
		}
		return jComboDatabaseDriver;
	}

	/**
	 * This method initializes jTextDatabaseName	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JComboBox getJTextDatabaseName() {
		if (jTextDatabaseType == null) {
			jTextDatabaseType = new JComboBox();
			jTextDatabaseType.setBounds(new Rectangle(144, 100, 150, 20));
			jTextDatabaseType.addItem("Please select");
			jTextDatabaseType.addItem("Oracle");
			jTextDatabaseType.addItem("MySQL");
			jTextDatabaseType.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if("oracle".equals(jTextDatabaseType.getSelectedItem().toString().toLowerCase())){
						jComboDatabaseDriver.removeAllItems();
						jComboDatabaseDriver.addItem("oracle.jdbc.driver.OracleDriver");
						jComboDatabaseDriver.updateUI();
						jLabel7.setText("SID");
                        jTextSIDCatalog.setText("");
                        jTextSIDCatalog.setVisible(true);
                        jLabel7.setVisible(true);
						databaseType = "oracle";
					}else if("mysql".equals(jTextDatabaseType.getSelectedItem().toString().toLowerCase())){
						jComboDatabaseDriver.removeAllItems();
						jComboDatabaseDriver.addItem("com.mysql.jdbc.Driver");
						jComboDatabaseDriver.updateUI();
						jTextSIDCatalog.setText("EBINEUTRINODB");
                        jTextSIDCatalog.setVisible(true);
                        jLabel7.setText("Catalog");
                        jLabel7.setVisible(true);
						databaseType = "mysql";
					}
				}
			});
			
		}
		return jTextDatabaseType;
	}

	/**
	 * This method initializes jTextOracleSID	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextOracleSID() {
		if (jTextSIDCatalog == null) {
			jTextSIDCatalog = new JTextField();
			jTextSIDCatalog.setBounds(new Rectangle(296, 100, 145, 20));
            jTextSIDCatalog.setVisible(false);
		}
		return jTextSIDCatalog;
	}

	/**
	 * This method initializes jButtonImportSchema	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonImportSchema() {
		if (jButtonImportSchema == null) {
			jButtonImportSchema = new JButton();
			jButtonImportSchema.setBounds(new Rectangle(332, 250, 174, 25));
			jButtonImportSchema.setText("DB Schema Import ");
			jButtonImportSchema.setEnabled(false);
			jButtonImportSchema.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(!checkField()){
						return;
					}
                    try {
                        checkIfSchemaExist();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
			});
		}
		return jButtonImportSchema;
	}

}  
