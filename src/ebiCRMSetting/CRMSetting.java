package ebiCRMSetting;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

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

        main.guiRenderer.loadGUI("CRMDialog/crmSettingDialog.xml");
		initialize();
		
	}

    public void setVisible(){
        ebiModule.guiRenderer.getEBIDialog("crmSettingDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_SETTING"));
        ebiModule.guiRenderer.getVisualPanel("crmSettingDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_SETTING"));

        ebiModule.guiRenderer.getLabel("autoIncAdm","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_INTERNAL_NUMBER_SETTINGS"));
        ebiModule.guiRenderer.getLabel("category","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.guiRenderer.getLabel("cooperation","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_COOPERATION"));
        ebiModule.guiRenderer.getLabel("classification","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_KLASSIFICATION"));
        ebiModule.guiRenderer.getLabel("addressType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ADRESS_TYPE"));
        ebiModule.guiRenderer.getLabel("reportType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_MEMO_TYPE"));
        ebiModule.guiRenderer.getLabel("busType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_BUSINESS_TYP"));
        ebiModule.guiRenderer.getLabel("saleStage","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SALE_STAGE"));
        ebiModule.guiRenderer.getLabel("budgetType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_BUDGET_TYPE"));
        ebiModule.guiRenderer.getLabel("evalType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_EVALUATING_TYPE"));
        ebiModule.guiRenderer.getLabel("oppStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY_STATUS"));
        ebiModule.guiRenderer.getLabel("activityStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITY_STATUS"));
        ebiModule.guiRenderer.getLabel("activityType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITY_TYPE"));
        ebiModule.guiRenderer.getLabel("offerStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_OFFER_STATUS"));
        ebiModule.guiRenderer.getLabel("orderStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ORDER_STATUS"));
        ebiModule.guiRenderer.getLabel("productCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_CATEGORY"));
        ebiModule.guiRenderer.getLabel("productType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_TYPE"));
        ebiModule.guiRenderer.getLabel("ProductProperties","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_PROPERTIES"));
        ebiModule.guiRenderer.getLabel("tax","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_TYP"));
        ebiModule.guiRenderer.getLabel("campStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_STATUS"));
        ebiModule.guiRenderer.getLabel("CampType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));
        ebiModule.guiRenderer.getLabel("serviceStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_STATUS"));
        ebiModule.guiRenderer.getLabel("serviceType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TYPE"));
        ebiModule.guiRenderer.getLabel("serviceCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.guiRenderer.getLabel("prosolStatus","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_STATUS"));
        ebiModule.guiRenderer.getLabel("prosolType","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TYPE"));
        ebiModule.guiRenderer.getLabel("prosolCategory","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.guiRenderer.getLabel("prosolClassfication","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION"));

        ebiModule.guiRenderer.getButton("autoIncAdmButton","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_MANAGING"));
        ebiModule.guiRenderer.getButton("autoIncAdmButton","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogInternalNumberAdministration adm = new EBIDialogInternalNumberAdministration(ebiModule,false);
					adm.setVisible();
				}

		});

        ebiModule.guiRenderer.getButton("invoiceNrAdministration","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogInternalNumberAdministration adm = new EBIDialogInternalNumberAdministration(ebiModule,true);
					adm.setVisible();
				}

		});

        ebiModule.guiRenderer.getButton("catBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("catBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyCategory", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CATEGORY_TYP"));
				    setCategory.setVisible();

				}
		});

        ebiModule.guiRenderer.getButton("coopBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("coopBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyCooperation", EBIPGFactory.getLANG("EBI_LANG_C_CRM_COOPERATION_TYP"));
				    setCategory.setVisible();

				}
		});

        ebiModule.guiRenderer.getButton("classBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("classBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyClassification", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CLASSIFICATION_TYPE"));
				    setCategory.setVisible();
			}
		});


        ebiModule.guiRenderer.getButton("addressTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("addressTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CRMAddressType", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ADRESS_TYP"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("reportTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("reportTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyMeetingType", EBIPGFactory.getLANG("EBI_LANG_C_CRM_MEMO_TYP"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("bussTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("bussTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOpportunityBusTyp", EBIPGFactory.getLANG("EBI_LANG_C_CRM_BUSINESS_TYP"));
				    setCategory.setVisible();
				}
		});


        ebiModule.guiRenderer.getButton("saleStgBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("saleStgBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOpportunitySStage", EBIPGFactory.getLANG("EBI_LANG_C_CRM_SALE_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("budTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("budTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOpportunityBgStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_BUDGET_TYP"));
				    setCategory.setVisible();
				}
			});

        ebiModule.guiRenderer.getButton("evalTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("evalTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOpportunityEvStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_EVALUATING_TYP"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("oppstatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("oppstatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOpportunityStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_OPPORTUNITY_TYPE"));
				    setCategory.setVisible();
				}
		});


        ebiModule.guiRenderer.getButton("activityStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("activityStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyActivityStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ACTIVITY_STATUS"));
				    setCategory.setVisible();
				}
		});


        ebiModule.guiRenderer.getButton("activityTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("activityTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyActivityType", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ACTIVITY_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("offerStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("offerStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOfferStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_OFFER_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("orderStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("orderStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setCategory = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CompanyOrderStatus", EBIPGFactory.getLANG("EBI_LANG_C_CRM_ORDER_TYPE"));
				    setCategory.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("taxAdminBnt","crmSettingDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_ADMINISTRATION"));
		ebiModule.guiRenderer.getButton("taxAdminBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogTaxAdministration tadmin = new EBIDialogTaxAdministration(ebiModule);
					tadmin.setVisible();
				}
		});
        
        ebiModule.guiRenderer.getButton("productCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("productCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"COMPANYPRODUCTCATEGORY", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_CATEGORY_TYP"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("productTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("productTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"COMPANYPRODUCTTYPE", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_TYPE"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("productPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("productPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CRMPRODUCTDIMENSIONS", EBIPGFactory.getLANG("EBI_LANG_C_CRM_PRODUCT_PROPERTIES"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("productTaxBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("productTaxBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"COMPANYPRODUCTTAXVALUE", EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_TYP"));
					setValueSetter.setVisible();
                    refreshingCombo();
				}
		});

        ebiModule.guiRenderer.getButton("campaignStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("campaignStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CRMCAMPAIGNSTATUS", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CAMPAIGN_STATUS"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("campaignTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("campaignTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"CRMCAMPAIGNPROPS", EBIPGFactory.getLANG("EBI_LANG_C_CRM_CAMPAIGN_PROPERTIES"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("serviceStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("serviceStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"companyservicestatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("serviceTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("serviceTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"companyservicetype", EBIPGFactory.getLANG("EBI_LANG_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("serviceCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("serviceCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"companyservicecategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("prosolStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("prosolStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmproblemsolstatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
					setValueSetter.setVisible();
				}
		});

        ebiModule.guiRenderer.getButton("prosolTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("prosolTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmproblemsoltype", EBIPGFactory.getLANG("EBI_LANG_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("prosolCateBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("prosolCateBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmproblemsolcategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("prosolClassBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("prosolClassBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmproblemsolclass", EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION"));
                    setValueSetter.setVisible();
                }
		});


        ebiModule.guiRenderer.getButton("prjStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("prjStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmprojectstatus", EBIPGFactory.getLANG("EBI_LANG_PROJECT_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("taskStatusBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("taskStatusBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmprojecttaskstatus", EBIPGFactory.getLANG("EBI_LANG_TASK_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("taskTypeBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("taskTypeBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmprojecttasktype", EBIPGFactory.getLANG("EBI_LANG_TASK_TYPE"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("costPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("costPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmprojectcosts", EBIPGFactory.getLANG("EBI_LANG_COST_PROPERTY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("taskPropBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("taskPropBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                                                   
                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crmprojectprops", EBIPGFactory.getLANG("EBI_LANG_TASK_PROPERTY"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("invoiceStsBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("invoiceStsBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crminvoicestatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
                    setValueSetter.setVisible();
                }
		});

        ebiModule.guiRenderer.getButton("invoiceCatBnt","crmSettingDialog").setIcon(EBIConstant.ICON_EDIT_DIALOG);
        ebiModule.guiRenderer.getButton("invoiceCatBnt","crmSettingDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(ebiModule.ebiPGFactory,"Crminvoicecategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
                    setValueSetter.setVisible();
                }
		});

       ebiModule.guiRenderer.showGUI();

    }

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {

		ebiModule.guiRenderer.getEBIDialog("crmSettingDialog").addWindowListener(new WindowAdapter() {
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
