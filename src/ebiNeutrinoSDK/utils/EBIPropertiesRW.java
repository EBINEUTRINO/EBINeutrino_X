package ebiNeutrinoSDK.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

/**
 * This class allow you to manipulate the main configuration properties ebi_neutrino.properties
 *
 */

public class EBIPropertiesRW {
	
	private Properties properties = null;
    private static EBIPropertiesRW prop = null;
    private static EBIPropertiesRW propP = null;
    private static EBIPropertiesRW propD = null;
    private File path=new File(".");
    private boolean isCustomPath = false;

	public EBIPropertiesRW(){
        path = new File(System.getProperty("user.dir"));
        isCustomPath = false;
        try {
          properties = new Properties();
          properties.load(new FileInputStream(path+"/config/ebi_neutrino.properties"));
        } catch (IOException e) {
	    	EBIExceptionDialog.getInstance("Critical Error :Properties file cannot be found !").Show(EBIMessage.ERROR_MESSAGE);
	    }

    }

	public EBIPropertiesRW(File path){
		try {
          this.path = path;
          properties = new Properties();
          properties.load(new FileInputStream(path));
          isCustomPath = true;
        } catch (IOException e) {
	    	EBIExceptionDialog.getInstance("Critical Error :Properties file cannot be found !").Show(EBIMessage.ERROR_MESSAGE);
	    }
	}
	
	
	public static EBIPropertiesRW getPropertiesInstance(){
	    if(prop == null){
           prop = new EBIPropertiesRW();
        }
	   return prop; 
	}
	
	public static EBIPropertiesRW getPropertiesInstance(File path,boolean reload,boolean dialogstore){
        EBIPropertiesRW tmpPr;
	    if((propP == null || reload) && !dialogstore){
           tmpPr = new EBIPropertiesRW(path);
           propP = tmpPr;
        }else {
          tmpPr = new EBIPropertiesRW(path);
          propD = tmpPr;
        }
	   return tmpPr;
	}
	
	public String getValue(String key){
		String val;
        if(!isCustomPath){
		    val  = properties.getProperty(key);
        }else{
            val  = properties.getProperty(key);
        }
		if(val == null){
			val = "";
		}
		return val;
	}

	public void setValue(String key,String value){
		properties.setProperty(key,value);
	}

	public void saveProperties(){
	    // Write properties file.
       if(isCustomPath){
            try {
                properties.store(new FileOutputStream(path), null);
            } catch (IOException e) {
                EBIExceptionDialog.getInstance("Critical Error :Properties file cannot be found!").Show(EBIMessage.ERROR_MESSAGE);
            }
       }else{
            try {
                properties.store(new FileOutputStream(path+"/config/ebi_neutrino.properties"), null);
            } catch (IOException e) {
                EBIExceptionDialog.getInstance("Critical Error :Properties file cannot be found!").Show(EBIMessage.ERROR_MESSAGE);
            }
       }
	}

    public Properties getProperties(){
        return properties;
    }

}
