package ebiCRM.gui.component.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBIDialogSearchContact;
import ebiCRM.gui.dialogs.EBIExportDialog;
import ebiCRM.gui.dialogs.EBIImportDialog;
import ebiCRMSetting.CRMSetting;
import ebiNeutrinoSDK.gui.dialogs.EBIImportSQLFiles;
import ebiNeutrinoSDK.interfaces.IEBISecurity;

public class EBICRMActionListener {

	private EBICRMModule ebiModule = null;
	public IEBISecurity  iSecurity = null;

	public EBICRMActionListener(EBICRMModule module){
	    ebiModule = module;
	    iSecurity    = module.ebiPGFactory.getIEBISecurityInstance();

	}

	protected ActionListener searchCompanyHistoryAction(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Company"),ebiModule).setVisible();
			}
		};
	}

	protected ActionListener crmSettingAction(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                boolean pass  ;
                if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
					pass = true;
				}else{
					pass = iSecurity.secureModule();
				}
                if(pass){
				    CRMSetting setting = new CRMSetting(ebiModule);
				    setting.setVisible();
                }
			}
		};
	}

	/**
	 * ActionListener search contact
	 * @return
	 */

	protected ActionListener searchContactAction(){
	   return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(iSecurity.checkCanReleaseModules() == true){
					showSearchContactDialog();
				}
			}
		};
	}

    /**
	 * ActionListener Export CSV
	 * @return
	 */

	protected ActionListener exportCSVAction(){
	   return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                boolean pass;
				if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
						ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
					pass = true;
				}else{
					pass = iSecurity.secureModule();
				}
				if(pass){
                    new EBIExportDialog(ebiModule).setVisible();
                }
			}
		};
	}

     /**
	 * ActionListener Import CSV
	 * @return
	 */

	protected ActionListener importCSVAction(){
	   return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                boolean pass  ;
				if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
						ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
					pass = true;
				}else{
					pass = iSecurity.secureModule();
				}
				if(pass){
                    new EBIImportDialog(ebiModule).setVisible();
                }
			}
		};
	}

    /**
	 * ActionListener Import CSV
	 * @return
	 */

	protected ActionListener importSQLAction(){
	   return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                boolean pass;
				if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
					pass = true;
				}else{
					pass = iSecurity.secureModule();
				}
				if(pass){
                    EBIImportSQLFiles importSQL = new EBIImportSQLFiles(ebiModule.ebiPGFactory);
                    importSQL.setVisible(true);
                }
			}
		};
	}

	/**
	 * ActionListener printReport
	 * @return
	 */
	protected ActionListener printReportAction(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean pass;
				if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
						ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
					pass = true;
				}else{
					pass = iSecurity.secureModule();
				}
				if(pass){
                    Map<String,Object> map =  new HashMap<String, Object>();
                    if(ebiModule.ebiPGFactory.company != null){
                        map.put("COMPANYID",ebiModule.ebiPGFactory.company.getCompanyid());
                    }
                    ebiModule.ebiPGFactory.getIEBIReportSystemInstance().useReportSystem(map);
				}
			}
		};
	}

	public void showSearchContactDialog(){
		EBIDialogSearchContact searchContact = new EBIDialogSearchContact(ebiModule,true);
 	    searchContact.setVisible();
	}

}
