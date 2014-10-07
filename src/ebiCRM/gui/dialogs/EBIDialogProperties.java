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
import ebiCRM.gui.panels.EBICRMProduct;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmproductdimension;
import ebiNeutrinoSDK.model.hibernate.Crmproductdimensions;

public class EBIDialogProperties {

    private static final long serialVersionUID = 1L;
    private Set<Crmproductdimension> dimensionList = null;
    private EBICRMProduct ebiProduct = null;
    private String[] properties = null;
    private boolean isProperties = false;
    private boolean isProjectCost = false;
    public boolean cancel = false;
    private boolean isEdit = false;
    private Crmproductdimension dimension = null;
    private EBICRMModule ebiModule = null;
    private String pack = "";

    public EBIDialogProperties(EBICRMProduct module, Set<Crmproductdimension> dimensions, Crmproductdimension dims) {
        super();
        ebiProduct = module;

        ebiModule = ebiProduct.mod;
        ebiModule.gui.loadGUI("CRMDialog/propertiesDialog.xml");

        dimensionList = dimensions;
        try {
            ebiProduct.mod.system.hibernate.openHibernateSession("EBI_PROPERTIES");
            ebiProduct.mod.system.hibernate.getHibernateTransaction("EBI_PROPERTIES").begin();
        } catch (Exception e) {
        }
        getAvalProperties();
        ebiModule.gui.getComboBox("propertiesText","propertiesDialog").setModel(new javax.swing.DefaultComboBoxModel(properties));

        if (dims != null) {

            dimension = dims;
            ebiModule.gui.getTextarea("propertiesValueText","propertiesDialog").setText(dims.getValue());
            ebiModule.gui.getComboBox("propertiesText","propertiesDialog").setSelectedItem(dims.getName());

            if (ebiModule.gui.getComboBox("propertiesText","propertiesDialog").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                ebiModule.gui.getComboBox("propertiesText","propertiesDialog").insertItemAt(dims.getName(), 1);
                ebiModule.gui.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(1);
            }
            isEdit = true;
        }

        isProperties = true;
    }


    public void setVisible(){

       if(!isProjectCost){
            pack="propertiesDialog";
            ebiModule.gui.getEBIDialog(pack).setTitle(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));
            ebiModule.gui.getVisualPanel(pack).setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));

            ebiModule.gui.getLabel("value",pack).setText(EBIPGFactory.getLANG("EBI_LANG_VALUE"));
            ebiModule.gui.getLabel("properties",pack).setText(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));

       }else{
            pack="costValueDialog";
            NumberFormat taxFormat=NumberFormat.getNumberInstance();
            taxFormat.setMinimumFractionDigits(2);
            taxFormat.setMaximumFractionDigits(3);

            ebiModule.gui.getFormattedTextfield("nameValue",pack).setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
       }

       ebiModule.gui.getButton("closeButton",pack).setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
       ebiModule.gui.getButton("closeButton",pack).addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.gui.getEBIDialog(pack).setVisible(false);
                    cancel = true;
                }
        });
        
        ebiModule.gui.getButton("applyButton",pack).setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));

        if(!isEdit){
            ebiModule.gui.getButton("applyButton",pack).setEnabled(false);
        }

        ebiModule.gui.getButton("applyButton",pack).addActionListener(new java.awt.event.ActionListener() {

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
                    }
                }
        });

        ebiModule.gui.getComboBox("propertiesText",pack).addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.gui.getComboBox("propertiesText",pack).getSelectedItem().toString())) {
                        ebiModule.gui.getButton("applyButton",pack).setEnabled(true);
                    }
                }
        });
        ebiModule.gui.showGUI();
    }

    private void getAvalProperties() {

        Query query;

        try {
            query = ebiProduct.mod.system.hibernate.getHibernateSession("EBI_PROPERTIES").createQuery("from Crmproductdimensions ");


            properties = new String[query.list().size() + 1];
            Iterator it = query.iterate();
            properties[0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
            int i = 1;
            while (it.hasNext()) {
                Crmproductdimensions dim = (Crmproductdimensions) it.next();
                ebiProduct.mod.system.hibernate.getHibernateSession("EBI_PROPERTIES").refresh(dim);
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
        dimension.setName(ebiModule.gui.getComboBox("propertiesText","propertiesDialog").getSelectedItem().toString());
        dimension.setValue(ebiModule.gui.getTextarea("propertiesValueText","propertiesDialog").getText());
        dimensionList.add(dimension);
        ebiProduct.dataControlProduct.getProduct().getCrmproductdimensions().add(dimension);

        if(ebiProduct.isEdit){
            try{

                ebiProduct.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                ebiProduct.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(dimension);
                ebiProduct.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();

            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        ebiProduct.dataControlProduct.dataShowDimension();
        ebiModule.gui.getTextarea("propertiesValueText","propertiesDialog").setText("");
        ebiModule.gui.getComboBox("propertiesText","propertiesDialog").setSelectedIndex(0);
        ebiModule.gui.getComboBox("propertiesText","propertiesDialog").grabFocus();

    }

    private boolean validateInput() {
        if ("".equals(this.ebiModule.gui.getTextarea("propertiesValueText","propertiesDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROPERTY_VALUE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validateInputCost() {
        if ("".equals(ebiModule.gui.getFormattedTextfield("nameValue","costValueDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROPERTY_VALUE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}