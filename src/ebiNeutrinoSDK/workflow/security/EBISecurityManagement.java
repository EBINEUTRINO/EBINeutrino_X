package ebiNeutrinoSDK.workflow.security;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBISecurity;
import ebiNeutrinoSDK.interfaces.IEBIStoreInterface;
import ebiNeutrinoSDK.interfaces.IEBISystemUserRights;

/**
 * Allow to secure important functionality from a non privileged user
 *
 */

public class EBISecurityManagement implements IEBISecurity {
	
	private EBIPGFactory ebiFactory = null;
	private IEBISystemUserRights iuserRights  = null;
	
	/**
	 * Constructor
	 * @param module
	 * @param ebifunction
	 */
	public EBISecurityManagement(EBIPGFactory ebifunction){
		ebiFactory = ebifunction;
	}
	

	/**
	 * check if the module component have changes. 
	 * 
	 * @return : boolean 
	 */
	
	public boolean checkCanReleaseModules() {
		
		boolean ret = true;
		boolean canProceed  ;
		iuserRights = ebiFactory.getIEBISystemUserRights();
		try{
			if(EBIPGFactory.canRelease == false){
				if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGES_HAVE_CHANGES")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
					  
					if(!iuserRights.isCanSave() || !iuserRights.isAdministrator()){
						canProceed = secureModule();
					}else{
						canProceed =  true;
					}
					if(canProceed){
						    IEBIStoreInterface storeInterface=(IEBIStoreInterface) ebiFactory.getIEBIModule().getActiveModule();
							if(EBIPGFactory.isSaveOrUpdate == false){
								
								if(storeInterface.ebiSave() == true){
									EBIPGFactory.canRelease = true;
									return true;
								}else{
									EBIPGFactory.canRelease = false;
									return false;
								}
							}else if(EBIPGFactory.isSaveOrUpdate == true){
	
								if(storeInterface.ebiUpdate() == true){
									EBIPGFactory.canRelease = true;
									return true;
								}else{
									EBIPGFactory.canRelease = false;
									return false;
								}
							}
					}else{
						EBIPGFactory.canRelease = false;
						return false;
					}
				}else{
					EBIPGFactory.canRelease = true;
					return true;
				}
			}
        }catch (Exception ex){
            ex.printStackTrace();
        }
		return ret;
	}
		
    /**
     * show a password dialog if the user are not superuser
     * @param secure
     * @return boolean if password was correct
     */
	
	
	public boolean secureModule(){
	 boolean passing  ;
	 iuserRights = ebiFactory.getIEBISystemUserRights();
      if(!this.iuserRights.isAdministrator()){	
		ebiNeutrinoSDK.gui.dialogs.EBIAdminPassword pwdDialog = new ebiNeutrinoSDK.gui.dialogs.EBIAdminPassword(ebiFactory);
		pwdDialog.setVisible(true);
				
		if(pwdDialog.isOK == true){
			passing = true;
		}else{
			passing = false;
		}
      }else{
    	  passing = true;
      }		
		return passing;
	}

	
}
