package ebiNeutrino.core;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;

import org.hibernate.cfg.Configuration;

import ebiNeutrino.core.gui.Dialogs.EBISplashScreen;
import ebiNeutrino.core.setup.EBILanguageSetup;
import ebiNeutrino.core.setup.EBISetup;
import ebiNeutrinoSDK.EBIHibernateSessionPooling;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;
import ebiNeutrinoSDK.utils.Encrypter;

/**
 * Initialize EBI Neutrino System
 * Check Database connectivity 
 *
 */

public class EBINeutrinoSystemInit extends EBIPGFactory {

	private EBIPGFactory ebiPGFactory = null;
	public static boolean isConfigured = false; 
	private EBIPropertiesRW properties =  null;
    private boolean toReturn;

	public EBINeutrinoSystemInit(EBIPGFactory func,EBISplashScreen spl){

		properties = EBIPropertiesRW.getPropertiesInstance();

		ebiPGFactory = func;
        boolean ret = Init(properties.getValue("EBI_Neutrino_Database_Name"));
		if(ret == false){
			isConfigured = false;
			if(spl!= null){
                spl.setVisible(false);
            }
            if(checkConnection() == true){
                if(spl != null){
                    spl.setVisible(true);
                }
                if(Init(properties.getValue("EBI_Neutrino_Database_Name"))== false){
                    isConfigured = false;
                    if (spl != null) {
                        spl.setVisible(false);
                    }
                }
	        }
	   }
	}
    
	public EBINeutrinoSystemInit(EBIPGFactory func){
		ebiPGFactory = func;
        properties = EBIPropertiesRW.getPropertiesInstance();
		if(Init(properties.getValue("EBI_Neutrino_Database_Name"))== false){
			isConfigured = false;
		}
	    checkConnection();
	}
	
	/**
	 * Initialize EBI Neutrino Systems
	 * 
	 * @param data      
	 * @return void
	 */
	
	private boolean Init(final String data){

		try {
		Encrypter encrypter = new Encrypter("EBINeutrino");

        EBIPGFactory.USE_ASB2C = Boolean.parseBoolean(properties.getValue("EBI_Neutrino_UserAsB2C"));
		EBIPGFactory.host = properties.getValue("EBI_Neutrino_Host");
		final String password = encrypter.decrypt(properties.getValue("EBI_Neutrino_Password"));
		final String user = encrypter.decrypt(properties.getValue("EBI_Neutrino_User"));
		EBIPGFactory.updateServer = properties.getValue("EBI_Neutrino_Update_Server");
		EBIPGFactory.DateFormat = "".equals(properties.getValue("EBI_Neutrino_Date_Format")) ? "dd.MM.yyyy" : properties.getValue("EBI_Neutrino_Date_Format");
		final String Driver  = properties.getValue("EBI_Neutrino_Database_Driver");
		final String dbType  = properties.getValue("EBI_Neutrino_Database");
		final String oracSID = properties.getValue("EBI_Neutrino_Oracle_SID");
        final String useUpperCase = properties.getValue("EBI_Neutrino_Database_UpperCase");
		EBIPGFactory.lastLoggedUser = properties.getValue("EBI_Neutrino_Last_Logged_User");
		getLanguageInstance("".equals(properties.getValue("EBI_Neutrino_Language_File")) ? "language/EBINeutrinoLanguage_English.properties" : properties.getValue("EBI_Neutrino_Language_File"),false);

        //Set global database system    
        DATABASE_SYSTEM = dbType.toLowerCase();

		if(!"".equals(Driver)){
            toReturn = ebiPGFactory.getIEBIDatabase().connect(Driver, EBIPGFactory.host.trim(),data,password,user,dbType.toLowerCase(),oracSID,useUpperCase);
		}else{
		    toReturn = false;
		}

		isConfigured = toReturn;
		if(toReturn == false){
			return toReturn;
		}

		ebiPGFactory.fillComboWithUser();
        ebiPGFactory.updateSystemYears();
		/***************************************************************/
		/** Configure Hibernate **/
		/**************************************************************/

                    Configuration cfg  = new Configuration();
                    File dir = new File("hibernate/");

                    FilenameFilter filter = new FilenameFilter() {
                         public boolean accept(File dir, String name) {
                            return name.endsWith(".hbm.xml");
                         }
                    };
                
				    String[] children = dir.list(filter);
				    if (children == null) {
				        EBIExceptionDialog.getInstance("No Hibernate files found, system will exit now! ").Show(EBIMessage.ERROR_MESSAGE);
				    } else {
				        for (int i=0; i<children.length; i++) {
				            // Get filename of file or directory
				            String filename = children[i];
	                        cfg.addResource(filename);
	                    }
				    }

				    if("mysql".equals(dbType.toLowerCase())){
				     cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLInnoDBDialect");
				    }else if("oracle".equals(dbType.toLowerCase())){
				     cfg.setProperty("hibernate.dialect","org.hibernate.dialect.Oracle10gDialect");
				    }else if("hsqldb".equals(dbType.toLowerCase())){
				     cfg.setProperty("hibernate.dialect","org.hibernate.dialect.HSQLDialect");
				    }

                    cfg.setProperty("hibernate.connection.schema", data);
                    ebiPGFactory.hibernate = new EBIHibernateSessionPooling(ebiPGFactory,cfg);

		  } catch (Exception ex) {
			 ex.printStackTrace();
		     EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
             toReturn = false;
		  }
		  return toReturn;
	}
	
	
	/**
	 * Check if we have a connection to the database
	 * then a setup dialog will appear
	 */
	public boolean checkConnection(){
		if(isConfigured == false){
            JFrame frame = new JFrame("EBI Neutrino R1 Setup");
            frame.setUndecorated( true );
            frame.setVisible( true );
            frame.setLocationRelativeTo( null );
			if(EBIExceptionDialog.getInstance("No database connection possible\nWould you like to configure a connection?\n").Show(EBIMessage.INFO_MESSAGE_YESNO) == true){

                EBILanguageSetup setLanguage = new EBILanguageSetup();
				setLanguage.setVisible(true);

				EBISetup application = new EBISetup(ebiPGFactory);
				application.setResizable(false);
				application.setVisible(true);
                  
                if(!application.DBConfigured){
                    System.exit(1);
                }

                frame.dispose();

			  }else{
				System.exit(1);
			  }
		}
		return true;
	}
}
