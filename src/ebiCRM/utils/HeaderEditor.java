package ebiCRM.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class HeaderEditor{  
	
    //private HeaderSelector selector;  
    public String[] items = null;  
   
    public HeaderEditor(HeaderSelector hs){ }  
   
    public Object showEditor(Component parent, int col, String currentValue){  
    	Object retVal = "";	
    	if(items != null){
	       // items[0] = currentValue;  
	        String message = "Value for Column Nr: " + (col + 1) + ":";  
	        retVal = JOptionPane.showInputDialog(parent,  
	                                                    message,  
	                                                    "Table Value",  
	                                                    JOptionPane.INFORMATION_MESSAGE,  
	                                                    null,  
	                                                    items,  
	                                                    currentValue);  
	        if(retVal == null)  
	            retVal = currentValue;
    	}
        return retVal;  
    }  
    
}  