package ebiCRM.gui.dialogs;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.panels.EBICRMCampaign;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.gui.panels.EBICRMProjectPane;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignprop;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignprops;
import ebiNeutrinoSDK.model.hibernate.Crmproductdimension;
import ebiNeutrinoSDK.model.hibernate.Crmproductdimensions;
import ebiNeutrinoSDK.model.hibernate.Crmprojectcost;
import ebiNeutrinoSDK.model.hibernate.Crmprojectcosts;
import ebiNeutrinoSDK.model.hibernate.Crmprojectprop;
import ebiNeutrinoSDK.model.hibernate.Crmprojectprops;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttask;

public class EBIDialogProperties {

    private static final long serialVersionUID = 1L;
    private Set<Crmproductdimension> dimensionList = null;
    private Set<Crmcampaignprop> propertiesList = null;
    private Crmprojecttask projectTask = null;
    private EBICRMProduct ebiProduct = null;
    private EBICRMCampaign ebiCampaign = null;
    private EBICRMProjectPane ebiProject = null;
    private String[] properties = null;
    private boolean isCampaign = false;
    private boolean isProperties = false;
    private boolean isProjectProperties = false;
    private boolean isProjectCost = false;
    public boolean cancel = false;
    private boolean isEdit = false;
    private Crmproductdimension dimension = null;
    private Crmcampaignprop campaignProperties = null;
    private Crmprojectprop  projectProperties = null;
    private Crmprojectcost  projectCosts = null;
    private EBICRMModule ebiModule = null;
    private String pack = "";

    public EBIDialogProperties(EBICRMProduct module, Set<Crmproductdimension> dimensions, Crmproductdimension dims) {
        super();
        ebiProduct = module;

        ebiModule = ebiProduct.ebiModule;
        ebiModule.guiRenderer.loadGUI("CRMDialog/propertiesDialog.xml");

        dimensionList = dimensions;
        try {
            ebiProduct.ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBI_PROPERTIES");
            ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBI_PROPERTIES").begin();
        } catch (Exception e) {
        }
        getAvalProperties();
        ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setModel(new javax.swing.DefaultComboBoxModel(properties));

        if (dims != null) {

            dimension = dims;
            ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").setText(dims.getValue());
            ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedItem(dims.getName());

            if (ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").insertItemAt(dims.getName(), 1);
                ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(1);
            }
            isEdit = true;
        }

        isProperties = true;
    }

    public EBIDialogProperties(EBICRMProjectPane module, Crmprojecttask task, Object prop,boolean isCost){
        ebiProject = module;

        projectTask = task;
        ebiModule = module.ebiModule;
        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBI_PROPERTIES");
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBI_PROPERTIES").begin();
        } catch (Exception e) {
        }

        if(isCost){
          projectCosts = (Crmprojectcost)prop;
          ebiProject.ebiModule.guiRenderer.loadGUI("CRMDialog/costValueDialog.xml");
          getAvalProjectCosts();
          ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").setModel(new javax.swing.DefaultComboBoxModel(properties));
          isProjectCost = true;  
        }else{
          projectProperties = (Crmprojectprop)prop;  
          ebiProject.ebiModule.guiRenderer.loadGUI("CRMDialog/propertiesDialog.xml");
          getAvalProjectProperties();
          ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setModel(new javax.swing.DefaultComboBoxModel(properties));
          isProjectProperties = true;
        }


        if (prop != null) {
            String name;
            Object value;
            if(isCost){
              name = ((Crmprojectcost)prop).getName();
              value = ((Crmprojectcost)prop).getValue();
              ebiModule.guiRenderer.getFormattedTextfield("nameValue","costValueDialog").setValue(value);
              ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").setSelectedItem(name);
              if (ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                 ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").insertItemAt(name, 1);
                 ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").setSelectedIndex(1);
              }
            }else{
              name = ((Crmprojectprop)prop).getName();
              value = ((Crmprojectprop)prop).getValue();
              ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").setText(value.toString());
              ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedItem(name);
              if (ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                   ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").insertItemAt(name, 1);
                   ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(1);
              }
            }
            
            isEdit = true;
        }

    }


    public EBIDialogProperties(EBICRMCampaign module, Set<Crmcampaignprop> prop, Crmcampaignprop props) {
        super();
        ebiCampaign = module;
        
        ebiModule = ebiCampaign.ebiModule;
        ebiModule.guiRenderer.loadGUI("CRMDialog/propertiesDialog.xml");

        propertiesList = prop;
        try {
            ebiCampaign.ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBI_PROPERTIES");
            ebiCampaign.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBI_PROPERTIES").begin();
        } catch (Exception e) {
        }

        getAvalCampaignProperties();
        ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setModel(new javax.swing.DefaultComboBoxModel(properties));

        if (props != null) {

            campaignProperties = props;
 
            ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").setText(props.getValue());
            ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedItem(props.getName());

            if (ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").insertItemAt(props.getName(), 1);
                ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(1);
            }

            isEdit = true;
        }

        isCampaign = true;
    }

    public void setVisible(){

       if(!isProjectCost){
            pack="propertiesDialog";
            ebiModule.guiRenderer.getEBIDialog(pack).setTitle(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));
            ebiModule.guiRenderer.getVisualPanel(pack).setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));

            ebiModule.guiRenderer.getLabel("value",pack).setText(EBIPGFactory.getLANG("EBI_LANG_VALUE"));
            ebiModule.guiRenderer.getLabel("properties",pack).setText(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));

       }else{
            pack="costValueDialog";
            NumberFormat taxFormat=NumberFormat.getNumberInstance();
            taxFormat.setMinimumFractionDigits(2);
            taxFormat.setMaximumFractionDigits(3);

            ebiModule.guiRenderer.getFormattedTextfield("nameValue",pack).setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));   
       }

       ebiModule.guiRenderer.getButton("closeButton",pack).setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
       ebiModule.guiRenderer.getButton("closeButton",pack).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog(pack).setVisible(false);
                    cancel = true;
                }
        });
        
        ebiModule.guiRenderer.getButton("applyButton",pack).setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));

        if(!isEdit){
            ebiModule.guiRenderer.getButton("applyButton",pack).setEnabled(false);
        }

        ebiModule.guiRenderer.getButton("applyButton",pack).addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if(!isProjectCost){
                        if (!validateInput()) {
                            return;
                        }

                    }else{
                        if(!validateInputCost()){
                            return;
                        }
                    }

                    if (isProperties) {
                        saveProperties();
                    } else if(isCampaign) {
                        saveCampaignProperties();
                    } else if(isProjectCost){
                        saveProjectCost();
                    } else if(isProjectProperties){
                        saveProjectProperties();
                    }
                }
        });

        ebiModule.guiRenderer.getComboBox("propertiesText",pack).addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("propertiesText",pack).getSelectedItem().toString())) {
                        ebiModule.guiRenderer.getButton("applyButton",pack).setEnabled(true);
                    }
                }
        });
        ebiModule.guiRenderer.showGUI();
    }

    private void getAvalProperties() {

        Query query;

        try {
            query = ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").createQuery("from Crmproductdimensions ");


            properties = new String[query.list().size() + 1];
            Iterator it = query.iterate();
            properties[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            int i = 1;
            while (it.hasNext()) {
                Crmproductdimensions dim = (Crmproductdimensions) it.next();
                ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").refresh(dim);
                properties[i] = dim.getName();
                i++;
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAvalProjectProperties() {

        Query query;

        try {
            query = ebiProject.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").createQuery("from Crmprojectprops ");

            if(query != null){
                properties = new String[query.list().size() + 1];
                Iterator it = query.iterate();
                properties[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
                int i = 1;
                while (it.hasNext()) {
                    Crmprojectprops prop = (Crmprojectprops) it.next();
                    ebiProject.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").refresh(prop);
                    properties[i] = prop.getName();
                    i++;
                }
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAvalProjectCosts() {

        Query query;

        try {
           query = ebiProject.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").createQuery("from Crmprojectcosts ");

           if(query != null){
            properties = new String[query.list().size() + 1];
            Iterator it = query.iterate();
            properties[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            int i = 1;
            while (it.hasNext()) {
                Crmprojectcosts cost = (Crmprojectcosts) it.next();

                ebiProject.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").refresh(cost);
                properties[i] = cost.getName();
                i++;
            }
           }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAvalCampaignProperties() {

        Query query;
        try {
            query = ebiCampaign.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").createQuery("from Crmcampaignprops ");

            properties = new String[query.list().size() + 1];
            Iterator it = query.iterate();
            properties[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            int i = 1;
            while (it.hasNext()) {
                Crmcampaignprops dim = (Crmcampaignprops) it.next();
                ebiCampaign.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBI_PROPERTIES").refresh(dim);
                properties[i] = dim.getName();
                i++;
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveProperties() {

        if (!isEdit) {
            dimension = new Crmproductdimension();
        }
        dimension.setCrmproduct(ebiProduct.dataControlProduct.getProduct());
        dimension.setCreateddate(new Date());
        dimension.setCreatedfrom(EBIPGFactory.ebiUser);
        dimension.setName(ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().toString());
        dimension.setValue(ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").getText());
        dimensionList.add(dimension);
        ebiProduct.dataControlProduct.getProduct().getCrmproductdimensions().add(dimension);

        if(ebiProduct.isEdit){
            try{

                ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(dimension);
                ebiProduct.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        ebiProduct.dataControlProduct.dataShowDimension();
        ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").setText("");
        ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").grabFocus();

    }

    private void saveCampaignProperties() {

        if (!isEdit) {
            campaignProperties = new Crmcampaignprop();
        }

        campaignProperties.setCrmcampaign(ebiCampaign.dataControlCampaign.getCampaign());
        campaignProperties.setCreateddate(new Date());
        campaignProperties.setCreatedfrom(EBIPGFactory.ebiUser);
        campaignProperties.setName(ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().toString());
        campaignProperties.setValue(ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").getText());
        propertiesList.add(campaignProperties);
        ebiCampaign.dataControlCampaign.getCampaign().getCrmcampaignprops().add(campaignProperties);
        ebiCampaign.dataControlCampaign.dataShowProperties();
    }

    private void saveProjectProperties() {

        if (!isEdit) {
            projectProperties = new Crmprojectprop();
            projectProperties.setPropertiesid((projectTask.getCrmprojectprops().size()+1) * (-1));
        }

        projectProperties.setCrmprojecttask(projectTask);
        projectProperties.setCreateddate(new Date());
        projectProperties.setCreatedfrom(EBIPGFactory.ebiUser);
        projectProperties.setName(ebiModule.guiRenderer.getComboBox("propertiesText","propertiesDialog").getSelectedItem().toString());
        projectProperties.setValue(ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").getText());
        projectTask.getCrmprojectprops().add(projectProperties);
        ebiProject.projTask.showProperties();
    }

    private void saveProjectCost() {

        if (!isEdit) {
            projectCosts = new Crmprojectcost();
            projectCosts.setCostid((projectTask.getCrmprojectprops().size()+1) * (-1));
            projectCosts.setCrmprojecttask(projectTask);
        }
        projectCosts.setCreateddate(new Date());
        projectCosts.setCreatedfrom(EBIPGFactory.ebiUser);
        projectCosts.setName(ebiModule.guiRenderer.getComboBox("propertiesText","costValueDialog").getSelectedItem().toString());
        projectCosts.setValue(Double.parseDouble(ebiModule.guiRenderer.getFormattedTextfield("nameValue","costValueDialog").getValue().toString()));
        projectTask.getCrmprojectcosts().add(projectCosts);
        ebiProject.projTask.showCost();
    }

    private boolean validateInput() {
        if ("".equals(this.ebiModule.guiRenderer.getTextarea("propertiesValueText","propertiesDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROPERTY_VALUE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateInputCost() {
        if ("".equals(ebiModule.guiRenderer.getFormattedTextfield("nameValue","costValueDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROPERTY_VALUE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}