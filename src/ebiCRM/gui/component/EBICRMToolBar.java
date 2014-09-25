package ebiCRM.gui.component;

import javax.swing.JToggleButton;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.component.listener.EBICRMActionListener;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMToolBar extends EBICRMActionListener{

	private EBICRMModule ebiModule = null;
	
	public EBICRMToolBar(EBICRMModule module){
		super(module);
	   ebiModule = module;
    }
	
	/**
	 * Initialize CRM ToolBar 
	 *
	 */
	public void setCRMToolBar(){

        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemNew","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemNew","ebiToolBar").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemNew","ebiToolBar").addActionListener(newListenerAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemSave","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSave","ebiToolBar").setIcon(EBIConstant.ICON_SAVE);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSave","ebiToolBar").addActionListener(saveListenerAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchCompany","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchCompany","ebiToolBar").setIcon(EBIConstant.ICON_SEARCH);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchCompany","ebiToolBar").addActionListener(searchCompanyAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemHistory","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemHistory","ebiToolBar").setIcon(EBIConstant.ICON_HISTORY);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemHistory","ebiToolBar").addActionListener(searchCompanyHistoryAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchContact","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchContact","ebiToolBar").setIcon(EBIConstant.ICON_CONTACT);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemSearchContact","ebiToolBar").addActionListener(searchContactAction());
        }
        if( ebiModule.guiRenderer.getToolBarButton("toolbarItemCompanyReport","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemCompanyReport","ebiToolBar").setIcon(EBIConstant.ICON_REPORT);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemCompanyReport","ebiToolBar").addActionListener(printReportAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar").setEnabled(false);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar").addActionListener(deleteListenerAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemCRMSetting","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemCRMSetting","ebiToolBar").setIcon(EBIConstant.ICON_CRM_SETTING);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemCRMSetting","ebiToolBar").addActionListener(crmSettingAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemExport","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemExport","ebiToolBar").setIcon(EBIConstant.ICON_CSV_EXPORT);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemExport","ebiToolBar").addActionListener(exportCSVAction());
        }
        if(ebiModule.guiRenderer.getToolBarButton("toolbarItemImport","ebiToolBar") != null){
            ebiModule.guiRenderer.getToolBarButton("toolbarItemImport","ebiToolBar").setIcon(EBIConstant.ICON_CSV_IMPORT);
            ebiModule.guiRenderer.getToolBarButton("toolbarItemImport","ebiToolBar").addActionListener(importCSVAction());
        }
        if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
            if(ebiModule.guiRenderer.getToolBarButton("toolbarItemImportSQL","ebiToolBar") != null){
                ebiModule.guiRenderer.getToolBarButton("toolbarItemImportSQL","ebiToolBar").setIcon(EBIConstant.ICON_NEW);
                ebiModule.guiRenderer.getToolBarButton("toolbarItemImportSQL","ebiToolBar").addActionListener(importSQLAction());
            }
        }else{
           ebiModule.guiRenderer.getToolBarButton("toolbarItemImportSQL","ebiToolBar").setVisible(false);
        }

    }

	
	/**
	 * Enable or disable ToolBar delete button
	 * @param enabled
	 */
	public void setDeleteButtonEnabled(boolean enabled){
		if(ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar")!= null){
           ebiModule.guiRenderer.getToolBarButton("toolbarItemDelete","ebiToolBar").setEnabled(enabled);
        }
	}
}
