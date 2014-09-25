package ebiNeutrinoSDK.workflow.security;

import ebiNeutrinoSDK.interfaces.IEBISystemUserRights;

/**
 * This is a new version of the EBINeutrino user management functionality
 *
 */

public class EBISystemUserRights implements IEBISystemUserRights {

	private boolean isAdministrator;
	private boolean canSave;
	private boolean canDelete;
	private boolean canPrint;
	private boolean canView;
	private String userName;
	
	
	public EBISystemUserRights(){
		isAdministrator = false;
		canSave = false;
		canDelete = false;
		canPrint = false;
		canView = false;
		userName = "";
	}

	public void setUserName(String userName){
	  this.userName =  userName;
	}
	
	public String getUserName(){
		return this.userName;
	}

	public boolean isAdministrator() {
		return isAdministrator;
	}


	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}


	public boolean isCanSave() {
		return canSave;
	}


	public void setCanSave(boolean canSave) {
		this.canSave = canSave;
	}


	public boolean isCanDelete() {
		return canDelete;
	}


	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}


	public boolean isCanPrint() {
		return canPrint;
	}


	public void setCanPrint(boolean canPrint) {
		this.canPrint = canPrint;
	}


	public boolean isCanView() {
		return canView;
	}


	public void setCanView(boolean canView) {
		this.canView = canView;
	}
	
	
}
