package ebiNeutrino.core.setup;

import java.awt.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ebiNeutrino.core.EBIDBConnection;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.Encrypter;

public class EBISchemaImport extends EBIDialogExt {

	private static final long serialVersionUID = 1L;
	private EBIVisualPanelTemplate jContentPane = null;
	private JButton jButtonCancel = null;
	private JButton jButtonImport = null;
	private JLabel jLabel1 = null;
	private JProgressBar jProgressBar = null;
	private int availableLine = 0;
	private FileInputStream fstream = null;
	private DataInputStream in = null;
	private double i = 0;
	private StringBuffer toImport = new StringBuffer();
	private boolean failed = true;
	private EBIPGFactory _ebifunction = null;
    private String dbType  = "";
    private StringBuilder errorReport = new StringBuilder();
    private String catalog = "";
    private boolean useUpperCase = false;

	public EBISchemaImport(EBIPGFactory owner,String databaseType,String catalogDB,boolean upperCase) {
		super(null);
        EBIDBConnection.toUpperCase = upperCase;
        useUpperCase = upperCase;
		_ebifunction = owner;
		dbType = databaseType;
        initialize();
		setTitle("EBI Neutrino database schema import");
		setResizable(false);
		setModal(true);
        setName("EBISchemaImport");
        setClosable(true);
        catalog = catalogDB;
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
		this.setSize(490, 178);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) {
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(11, 50, 455, 20));
			jLabel1.setText("Table:");
			JLabel jLabel= new JLabel();
			jLabel.setBounds(new Rectangle(132, 9, 266, 28));
			jLabel.setText("EBI Neutrino Database Schema Import");
			jContentPane = new EBIVisualPanelTemplate();
            jContentPane.setEnableChangeComponent(false);
			jContentPane.setLayout(null);
			jContentPane.add(getJButtonCancel(), null);
			jContentPane.add(getJButtonImport(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJProgressBar(), null);
            jContentPane.setClosable(true);

            jContentPane.setModuleIcon(EBIConstant.ICON_APP);
            jContentPane.setModuleTitle("EBI Neutrino Database Schema Import");
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
			jButtonCancel.setBounds(new Rectangle(359, 110, 112, 29));
			jButtonCancel.setText("Cancel");
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jButtonImport	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonImport() {
		if (jButtonImport == null) {
			jButtonImport = new JButton();
			jButtonImport.setBounds(new Rectangle(235, 110, 120, 30));
			jButtonImport.setText("Import");
			jButtonImport.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                  
					if("oracle".equals(dbType)){ // Import Oracle schema
					 if(!importDDLSchema()){
						 EBIExceptionDialog.getInstance("Import DDL schema was not successfully, the file format is damaged!").Show(EBIMessage.ERROR_MESSAGE);
					 }
					}else if("mysql".equals(dbType) ){ //Import MySQL Schema
						if(!importSQLSchema("mysql.sql")){
							EBIExceptionDialog.getInstance("Import SQL schema was not successfully, the file format is damaged!").Show(EBIMessage.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return jButtonImport;
	}

    /**
     * import MySQL schema
     * @param fileName
     * @return
     */
		
	private boolean importSQLSchema(final String fileName){

    	Runnable runnable = new Runnable() {

		    public void run() {
		     jButtonImport.setEnabled(false);
		 	 jButtonCancel.setEnabled(false);
		     try{

                 if(useUpperCase){
                    catalog = catalog.toUpperCase();
                 }else{
                    catalog = catalog.toLowerCase(); 
                 }

                _ebifunction.getIEBIDatabase().execExt("CREATE DATABASE IF NOT EXISTS "+catalog);
                _ebifunction.getIEBIDatabase().getActiveConnection().setCatalog(catalog);

                 errorReport.append("\n");
		    	 
		        // Open the file that is the first command line parameter
		        fstream = new FileInputStream(new File("sql/"+fileName));

		        // Convert our input stream to a DataInputStream
		        in = new DataInputStream(fstream);
		        
		        availableLine = in.available();
		       
		    	boolean isFirstLine = true;
		        while (in.available() != 0){
		        	
		            String line =  in.readLine();

                    // show the first line to the import dialog label
		            if(isFirstLine){
		            	String firstLine = line;
                        String tableShow = (firstLine.replace("(", "").replaceAll("create table if not exists", "")).replaceAll("EBINEUTRINODB", catalog);
		            	jLabel1.setText("Table:"+tableShow);
		            }


		            if(!"/".equals(line.trim())){   // Not import until end of sql statement 
		            	
                        i += line.length()+1;
                        // Static replace why the hibernate is set as default to generate DATABASE named EBINEUTRINODB
                        if(!"EBINEUTRINODB".equals(catalog)){
                        	toImport.append(line.replaceAll("EBINEUTRINODB.", catalog+".")+"\n");
                        }else{
                        	toImport.append(line+"\n");
                        }

                        //Calculate available line to import set the progressbar to the % result
                        jProgressBar.setValue(((int)((i / availableLine) * 100)) );  // Irrational result cast to int
                        isFirstLine = false;

		            }else if(!"".equals(line.trim())){     // end of sql statement import to db
		            	// SQL Import Table
		            	if(toImport.toString().length()>1){
                            _ebifunction.getIEBIDatabase().execExt(toImport.toString().toUpperCase());
                            errorReport.append("\n");
                            toImport = new StringBuffer();
		            	    isFirstLine = true;
		            	}else{
                            toImport = new StringBuffer();
                            isFirstLine = true;
		            	}
		            }
		            i++;
		        }
		        if(!failed){
		   	        jButtonImport.setEnabled(true);
		        }else{
		        	createAdminUser();
		           	jButtonImport.setVisible(false);
		           	jButtonCancel.setText("Finish");
		        }
		        
		        jButtonCancel.setEnabled(true);
	        
	        }catch(IOException ex){
	        	EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
	        	ex.printStackTrace();
                errorReport.append(EBIPGFactory.printStackTrace(ex));
                errorReport.append("\n");
	        	failed = false;
                 setVisible(false);
	        } catch (SQLException ex) {
                 errorReport.append(EBIPGFactory.printStackTrace(ex));
                 errorReport.append("\n");
                 ex.printStackTrace();
                 setVisible(false);
            }finally{
                 try {
                    if(in != null){
                         in.close();
                    }
                 } catch (IOException ex) {
                     EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                     setVisible(false);
                 }
             }
		  }
	  };
        // Close the input stream
	    Thread thread = new Thread(runnable);
	    thread.start();
       
		return failed;
	}

    /**
     * Import the oracle ddl schema
     * @return
     */
	private boolean importDDLSchema(){
		
    	Runnable runnable = new Runnable() {
		    public void run() {
		    	
		    	
		    	jButtonImport.setEnabled(false);
		 	    jButtonCancel.setEnabled(false);
                 try{

                    // Open the file that is the first command line parameter
                    fstream = new FileInputStream(new File("sql/oracle.ddl"));

                    // Convert our input stream to a DataInputStream
                    in = new DataInputStream(fstream);

                    availableLine = in.available();

                    boolean isFirstLine = true;
                    while (in.available() != 0){

                        String line =  in.readLine();
                        if(isFirstLine){
                            String firstLine = line;
                            jLabel1.setText("Table:"+firstLine.replace("(", "").replaceAll("create table", "").trim());
                            isFirstLine = false;
                        }
                        if(!"/".equals(line.trim())){

                            i += line.length()+1;
                            toImport.append(line.replaceAll("^\\s+", ""));   // Remove the begin whitespace for oracle!
                            double prz = (i / availableLine) * 100;
                            jProgressBar.setValue((int)prz);

                            try{
                                Thread.sleep(20);
                            }catch(InterruptedException ex){}
                            isFirstLine = false;

                        }else {
                            // SQL Import Table
                            if(toImport.length()>1){

                                 _ebifunction.getIEBIDatabase().execExt(toImport.toString());
                                 toImport = new StringBuffer();
                                 isFirstLine = true;
                            }else{
                                toImport = new StringBuffer();
                                isFirstLine = true;
                            }
                       }
                        i++;
                    }
                    if(!failed){
                        jButtonImport.setEnabled(true);
                    }else{
                        createAdminUser();
                        jButtonImport.setVisible(false);
                        jButtonCancel.setText("Finish");
                    }

                    jButtonCancel.setEnabled(true);
                    in.close();

                }catch(IOException ex){
                    errorReport.append(EBIPGFactory.printStackTrace(ex));
                    errorReport.append("\n");
                    failed = false;
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
		  }
	  };
        // Close the input stream
	    Thread thread = new Thread(runnable);
	    thread.start();
       
		return failed;
	}


    /**
     * Insert a default EBI Neutrino user after import schema
     */

	private void createAdminUser(){

		try{
             _ebifunction.getIEBIDatabase().setAutoCommit(true);
	         _ebifunction.getIEBIDatabase().getActiveConnection().setCatalog(catalog);

                String sqlUsr = "INSERT INTO EBIUSER " +
                        "(ID,EBIUSER,PASSWD,CREATEDDATE,CREATEDFROM,IS_ADMIN,CANSAVE,CANPRINT,CANDELETE) " +
                        "VALUES(?,?,?,?,?,?,?,?,?) ";

                PreparedStatement ps = _ebifunction.getIEBIDatabase().initPreparedStatement(sqlUsr);
                ps.setInt(1,1);
                ps.setString(2,"root");
			    ps.setString(3,generatePassword("ebineutrino"));
			    ps.setDate(4,new java.sql.Date(new java.util.Date().getTime()));
			    ps.setString(5,"Installer");
			    ps.setInt(6,1);
			    ps.setInt(7,0);
			    ps.setInt(8,0);
			    ps.setInt(9,0);

			 _ebifunction.getIEBIDatabase().executePreparedStmt(ps);

		}catch(SQLException ex){
            ex.printStackTrace();
            errorReport.append(EBIPGFactory.printStackTrace(ex));
            errorReport.append("\n");
		}finally{
            _ebifunction.getIEBIDatabase().setAutoCommit(false);
        }

        JDialog repDialog = new JDialog();
        JScrollPane panes1 = new JScrollPane();
        repDialog.setModal(true);
        repDialog.setSize(450,300);
        repDialog.setLocation(getX(),getY());
        repDialog.setTitle("Information dialog");
        repDialog.setName("repDialog");
        JTabbedPane pane = new JTabbedPane();

        JPanel panelRep = new JPanel();
        panelRep.setLayout(new BorderLayout());
        panes1.setViewportView(new JEditorPane("text/html","<b>Installer Info:</b><br>EBI Neutrino Installer created the default user<br><br>" +
					"<b><font color='red'>User:root</font></b><br>" +
					"<b><font color='red'>Password:ebineutrino</font></b><br><br>" +
					"now you are ready to login!<br><br>SECURITY WARNING: Don't forget to change the root password after login!!<br>"));

        panelRep.add(panes1,BorderLayout.CENTER);

        pane.add("Report", panelRep);
        JScrollPane panes2 = new JScrollPane();
        JPanel panelRepError = new JPanel();
        panelRepError.setLayout(new BorderLayout());
        panes2.setViewportView(new JEditorPane("text/html","".equals(errorReport.toString()) ? "<b>No Errors available</b>" : errorReport.toString() ));
        panelRepError.add(panes2,BorderLayout.CENTER);

        pane.add("Error Report", panelRepError);
        repDialog.getContentPane().setLayout(new BorderLayout());
        repDialog.getContentPane().add(pane,BorderLayout.CENTER);
        repDialog.setVisible(true);

	}

	private String generatePassword(String password){

        Encrypter encrypter = new Encrypter("EBINeutrino");
        // Encrypt
        String pwd = encrypter.encrypt(password);

      return pwd;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setBounds(new Rectangle(10, 73, 462, 30));
			jProgressBar.setMaximum(100);
			jProgressBar.setMinimum(0);
			jProgressBar.setStringPainted(true);
			
		}
		return jProgressBar;
	}
}