package ebiCRMSetting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.dialogs.EBIDialogInternalNumberAdministration;
import ebiCRM.gui.dialogs.EBIDialogTaxAdministration;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogValueSetter;
import ebiNeutrinoSDK.utils.EBIConstant;


public class CRMSetting {

	public EBICRMModule ebiModule  = null;
    
	public CRMSetting(EBICRMModule main) {
		ebiModule = main;

        main.gui.loadGUI("CRMDialog/crmSettingDialog.xml");
		initialize();
		
	}

    public void setVisible(){
        ebiModule.gui.getEBIDialog("crmSettingDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_SETTING"));
        ebiModule.gui.getVisualPanel("crmSettingDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_SETTING"));

        ebiModule.gui.getLabel("autoIncAdm","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_INTERNAL_NUMBER_SETTINGS"));
        ebiModule.gui.getLabel("category","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.gui.getLabel("cooperation","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_COOPERATION"));
        ebiModule.gui.getLabel("classification","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_KLASSIFICATION"));
        ebiModule.gui.getLabel("activityStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITY_STATUS"));
        ebiModule.gui.getLabel("activityType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITY_TYPE"));
        ebiModule.gui.getLabel("offerStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_OFFER_STATUS"));
        ebiModule.gui.getLabel("orderStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ORDER_STATUS"));
        ebiModule.gui.getLabel("productCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_CATEGORY"));
        ebiModule.gui.getLabel("productType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_TYPE"));
        ebiModule.gui.getLabel("ProductProperties","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_PROPERTIES"));
        ebiModule.gui.getLabel("taxautority","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TAX_AUTORITY"));
        ebiModule.gui.getLabel("tax","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_TYP"));
        ebiModule.gui.getLabel("serviceStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_STATUS"));
        ebiModule.gui.getLabel("serviceType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TYPE"));
        ebiModule.gui.getLabel("serviceCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.gui.getLabel("prosolStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_STATUS"));
        ebiModule.gui.getLabel("prosolType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TYPE"));
        ebiModule.gui.getLabel("prosolCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.gui.getLabel("prosolClassfication","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION"));

        ebiModule.gui.getButton("autoIncAdmButton","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_MANAGING"));
        ebiModule.gui.getButton("autoIncAdmButton","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogInternalNumberAdministration adm = new EBIDialogInternalNumberAdministration(ebiModule,false);
					adm.setVisible();
				}

		});

        ebiModule.gui.getButton("invoiceNrAdministration","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogInternalNumberAdministration adm = new EBIDialogInternalNumberAdministration(ebiModule,true);
					adm.setVisible();
				}

		});

        ebiModule.gui.getButton("catBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("catBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyCategory", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CATEGORY_TYP"));
				    setCategory.setVisible();

				}
		});

        ebiModule.gui.getButton("coopBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("coopBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyCooperation", EBIPGFactory.getLANG("EBI_LANG_C_CRM_COOPERATION_TYP"));
				    setCategory.setVisible();

				}
		});

        ebiModule.gui.getButton("classBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("classBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyClassification", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CLASSIFICATION_TYPE"));
				    setCategory.setVisible();
			}
		});


        ebiModule.gui.getButton("activityStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("activityStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyActivityStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ACTIVITY_STATUS"));
				    setCategory.setVisible();
				}
		});


        ebiModule.gui.getButton("activityTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("activityTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyActivityType", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ACTIVITY_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.gui.getButton("offerStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("offerStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyOfferStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_OFFER_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.gui.getButton("orderStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("orderStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"CompanyOrderStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ORDER_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.gui.getButton("taxAdminBnt","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_ADMINISTRATION"));
		ebiModule.gui.getButton("taxAdminBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogTaxAdministration tadmin = new EBIDialogTaxAdministration(ebiModule);
					tadmin.setVisible();
				}
		});

        ebiModule.gui.getButton("taxautbutton","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("taxautbutton","crmSettingDialog").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.system,"COMPANYTAXOFFICE", EBIPGFactory.getLANG("EBI_LANG_TAX_AUTORITY"));
                setCategory.setVisible();
            }
        });
        
        ebiModule.gui.getButton("productCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("productCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"COMPANYPRODUCTCATEGORY", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_CATEGORY_TYP"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.gui.getButton("productTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("productTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"COMPANYPRODUCTTYPE", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_TYPE"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.gui.getButton("productPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("productPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"CRMPRODUCTDIMENSIONS", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_PROPERTIES"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.gui.getButton("productTaxBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("productTaxBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"COMPANYPRODUCTTAXVALUE", EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_TYP"));
					setValueSetter.setVisible();
                    refreshingCombo();
				}
		});


        ebiModule.gui.getButton("serviceStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("serviceStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"companyservicestatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.gui.getButton("serviceTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("serviceTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"companyservicetype", EBIPGFactory.getLANG("EBI_LANG_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("serviceCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("serviceCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"companyservicecategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("prosolStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("prosolStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmproblemsolstatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.gui.getButton("prosolTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("prosolTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmproblemsoltype", EBIPGFactory.getLANG("EBI_LANG_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("prosolCateBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("prosolCateBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmproblemsolcategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("prosolClassBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("prosolClassBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmproblemsolclass", EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION"));
                    setValueSetter.setVisible();
                }
		});


        ebiModule.gui.getButton("prjStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("prjStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmprojectstatus", EBIPGFactory.getLANG("EBI_LANG_PROJECT_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("taskStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("taskStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmprojecttaskstatus", EBIPGFactory.getLANG("EBI_LANG_TASK_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("taskTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("taskTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmprojecttasktype", EBIPGFactory.getLANG("EBI_LANG_TASK_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("costPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("costPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmprojectcosts", EBIPGFactory.getLANG("EBI_LANG_COST_PROPERTY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("taskPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("taskPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                                                   
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crmprojectprops", EBIPGFactory.getLANG("EBI_LANG_TASK_PROPERTY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("invoiceStsBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("invoiceStsBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crminvoicestatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.gui.getButton("invoiceCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.gui.getButton("invoiceCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.system,"Crminvoicecategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

       ebiModule.gui.showGUI();

    }

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		ebiModule.gui.getEBIDialog("crmSettingDialog").addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				refreshingCombo();
			}
		});
	}

	public void refreshingCombo(){
		final Runnable runnable = new Runnable() {
		    public void run() {
                try{
		    	 ebiModule.dynMethod.initComboBoxes(true);
                }catch (Exception ex){}
            }
		  };

		  Thread thread=new Thread(runnable,"refreshComboThread");
	      thread.start();
	}
} 
