package ebiNeutrino.core.user.management;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;


/**
 * Old stuff
 * 
 */

public class EBIModulschangeCheck {
	
	public EBIMain ebiMain = null;
	
	public EBIModulschangeCheck(EBIMain main){
	   ebiMain = main;	
	}
        
	public boolean checkUser(){
		boolean ret = true;
		if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGES_HAVE_CHANGES")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
				if(ebiMain.user_management.isSaveOrUpdate == false){
					if(ebiMain.user_management.saveUser() == true){
						EBIMain.canReleaseUser = true;
						ret = true;
					}else{
						EBIMain.canReleaseUser = false;
						ret = false;
					}
				}else if(ebiMain.user_management.isSaveOrUpdate == true){
					if(ebiMain.user_management.updateUser() == true){
						EBIMain.canReleaseUser = true;
						ret = true;
					}else{
						EBIMain.canReleaseUser = false;
						ret = false;
					}
				}
			}else{
				EBIMain.canReleaseUser = true;
				ret = true;
			}
		return ret;
	}

}
