package ebiCRM.gui.dialogs;

import java.util.Date;

import javax.swing.JTextField;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlCampaign;
import ebiCRM.data.control.EBIDataControlOffer;
import ebiCRM.data.control.EBIDataControlOrder;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyofferreceiver;
import ebiNeutrinoSDK.model.hibernate.Companyorderreceiver;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignreceiver;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMAddContactAddressType {

    private EBICRMModule ebiModule = null;
    public boolean isSaved = false;
    private boolean isOrder = false;
    private boolean isCampaign = false;
    private EBIDataControlOffer dataControlOffer = null;
    private EBIDataControlOrder dataControlOrder = null;
    private EBIDataControlCampaign dataControlCampaign = null;
    private JTextField phoneForContact = new JTextField();
    private Companyofferreceiver receiver = null;
    private Companyorderreceiver receiver1 = null;
    private Crmcampaignreceiver receiver2 = null;


    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlOffer dataControlOffer) {
        ebiModule = ebiMod;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialog.xml");
        this.dataControlOffer = dataControlOffer;
    }

    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlOffer dataControlOffer, Companyofferreceiver rec) {
        ebiModule = ebiMod;
        this.receiver = rec;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialog.xml");
        this.dataControlOffer = dataControlOffer;
    }

    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlOrder dataControlOrder) {
        ebiModule = ebiMod;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialog.xml");
        this.dataControlOrder = dataControlOrder;
        isOrder = true;
    }

    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlOrder dataControlOrder, Companyorderreceiver rec) {
        ebiModule = ebiMod;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialog.xml");
        this.receiver1 = rec;
        this.dataControlOrder = dataControlOrder;
        isOrder = true;
    }

    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlCampaign dataControlCampaign) {
        ebiModule = ebiMod;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialogCampaign.xml");
        this.dataControlCampaign = dataControlCampaign;
        ebiModule.guiRenderer.getLabel("companyNumber","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_COMPANY_NUMBER"));
        ebiModule.guiRenderer.getLabel("companyName","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_COMPANY_NAME"));
        isCampaign = true;
    }

    public EBICRMAddContactAddressType(EBICRMModule ebiMod, EBIDataControlCampaign dataControlCampaign, Crmcampaignreceiver campRec) {
        ebiModule = ebiMod;
        this.receiver2 = campRec;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addnewReceiverDialogCampaign.xml");
        this.dataControlCampaign = dataControlCampaign;
        isCampaign = true;
    }


    public void setVisible(){
         ebiModule.guiRenderer.getEBIDialog("addNewReceiverDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_ADD_CONTACT_SEND_TYPE"));
         ebiModule.guiRenderer.getVisualPanel("addNewReceiverDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_ADD_CONTACT_SEND_TYPE"));

         ebiModule.guiRenderer.getLabel("fax","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_FAX"));
         ebiModule.guiRenderer.getLabel("email","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_EMAIL"));
         ebiModule.guiRenderer.getLabel("country","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY"));
         ebiModule.guiRenderer.getLabel("postCode","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE"));
         ebiModule.guiRenderer.getLabel("zipLocation","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_ZIP")+"/"+EBIPGFactory.getLANG("EBI_LANG_C_LOCATION"));
         ebiModule.guiRenderer.getLabel("street","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR"));
         ebiModule.guiRenderer.getLabel("position","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_POSITION"));
         ebiModule.guiRenderer.getLabel("surname","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SURNAME"));
         ebiModule.guiRenderer.getLabel("name","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_NAME"));
         ebiModule.guiRenderer.getLabel("gender","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_GENDER"));
         ebiModule.guiRenderer.getLabel("typeDispatch","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE"));

         ebiModule.guiRenderer.getButton("closeButton","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLOSE"));
         ebiModule.guiRenderer.getButton("closeButton","addNewReceiverDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog("addNewReceiverDialog").setVisible(false);
                    isSaved = false;
                }
            });

        ebiModule.guiRenderer.getButton("applyButton","addNewReceiverDialog").setText(EBIPGFactory.getLANG("EBI_LANG_INSERT"));
        ebiModule.guiRenderer.getButton("applyButton","addNewReceiverDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!validateInput()) {
                        return;
                    }

                    isSaved = true;
                    if (!isCampaign) {
                        if (!isOrder) {
                            addReciever();
                            dataControlOffer.dataShowReceiver();
                        } else {
                            addReciever1();
                            dataControlOrder.dataShowReceiver();
                        }
                    } else {
                        addReciever2();
                        dataControlCampaign.dataShowReciever();
                    }

                    newReciever();
                }
            });

        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setEditable(true);
        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setModel(new javax.swing.DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                EBIPGFactory.getLANG("EBI_LANG_EMAIL"),
                EBIPGFactory.getLANG("EBI_LANG_C_POST"),
                EBIPGFactory.getLANG("EBI_LANG_C_FAX")
            }));

        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setModel(new javax.swing.DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                EBIPGFactory.getLANG("EBI_LANG_C_MALE"),
                EBIPGFactory.getLANG("EBI_LANG_C_FEMALE")
            }));
        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setEditable(true);

        ebiModule.guiRenderer.getButton("searchReciever","addNewReceiverDialog").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchReciever","addNewReceiverDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIDialogSearchContact addCon = new EBIDialogSearchContact(ebiModule,false);

                    addCon.setValueToComponent(ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog"), "Gender");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog"), "Surname");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog"), "contact.Name");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog"), "Street");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog"), "Zip");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog"), "Location");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog"), "Country");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog"), "PBox");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog"), "Position");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog"), "contact.EMail");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog"), "contact.Fax");
                    addCon.setValueToComponent(phoneForContact, "contact.Phone");


                    if (isCampaign) {     
                        addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("companyNrText","addNewReceiverDialog"), "company.CUSTOMERNR");
                        addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("companyNameText","addNewReceiverDialog"), "company.NAME");
                    }

                    addCon.setVisible();

                }
            });

        ebiModule.guiRenderer.getButton("newReciever","addNewReceiverDialog").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newReciever","addNewReceiverDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newReciever();
                }
            });

      if(this.receiver != null){
        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setSelectedItem(this.receiver.getReceivervia());
        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setSelectedItem(this.receiver.getGender());
        ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").setText(this.receiver.getName());
        ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").setText(this.receiver.getSurname());
        ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").setText(this.receiver.getPosition());
        ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").setText(this.receiver.getPbox());
        ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").setText(this.receiver.getStreet());
        ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").setText(this.receiver.getLocation());
        ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").setText(this.receiver.getZip());
        ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").setText(this.receiver.getEmail());
        ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").setText(this.receiver.getFax());
        ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").setText(this.receiver.getCountry());
        if(this.receiver.getCnum() != null){
            ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").setSelected(this.receiver.getCnum() == 1 ? true : false);
        }
      }
        
      if(this.receiver1 != null){
        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setSelectedItem(this.receiver1.getReceivervia());
        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setSelectedItem(this.receiver1.getGender());
        ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").setText(this.receiver1.getName());
        ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").setText(this.receiver1.getSurname());
        ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").setText(this.receiver1.getPosition());
        ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").setText(this.receiver1.getPbox());
        ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").setText(this.receiver1.getStreet());
        ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").setText(this.receiver1.getLocation());
        ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").setText(this.receiver1.getZip());
        ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").setText(this.receiver1.getEmail());
        ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").setText(this.receiver1.getFax());
        ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").setText(this.receiver1.getCountry());
        if(this.receiver1.getCnum() != null){
            ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").setSelected(this.receiver1.getCnum() == 1 ? true : false); 
        }
      }

      if(this.receiver2 != null){
        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setSelectedItem(this.receiver2.getReceivervia());
        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setSelectedItem(this.receiver2.getGender());
        ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").setText(this.receiver2.getName());
        ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").setText(this.receiver2.getSurname());
        ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").setText(this.receiver2.getPosition());
        ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").setText(this.receiver2.getPbox());
        ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").setText(this.receiver2.getStreet());
        ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").setText(this.receiver2.getLocation());
        ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").setText(this.receiver2.getZip());
        ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").setText(this.receiver2.getEmail());
        ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").setText(this.receiver2.getFax());
        ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").setText(this.receiver2.getCountry());
        ebiModule.guiRenderer.getTextfield("companyNameText","addNewReceiverDialog").setText(this.receiver2.getCompanyname());
        ebiModule.guiRenderer.getTextfield("companyNrText","addNewReceiverDialog").setText(this.receiver2.getCompanynumber());
        ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").setSelected(this.receiver2.getCnum() == 1 ? true : false);  
      }

        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").requestFocus();
      ebiModule.guiRenderer.showGUI();

    }

    public boolean validateInput() {
        if (ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_SEND_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (EBIPGFactory.getLANG("EBI_LANG_EMAIL").equals(ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedItem()) && "".equals(ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_EMAIL")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (EBIPGFactory.getLANG("EBI_LANG_C_FAX").equals(ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedItem().toString()) && "".equals(ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").getText())) {

            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_FAX")).Show(EBIMessage.ERROR_MESSAGE);
            return false;                                                                                                     
        }
        return true;
    }

    public void newReciever() {
        ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").setSelectedIndex(0);
        ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").setText("");
        ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").setSelected(false);

        if(isCampaign){
            ebiModule.guiRenderer.getTextfield("companyNrText","addNewReceiverDialog").setText("");
            ebiModule.guiRenderer.getTextfield("companyNameText","addNewReceiverDialog").setText("");
        }
    }

    public void addReciever() {

        Companyofferreceiver of;
        if(this.receiver == null){
            of = new Companyofferreceiver();
        }else{
            of = this.receiver;
        }
        of.setCompanyoffer(this.dataControlOffer.getCompOffer());
        of.setCnum(ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").isSelected() ? 1 : 0);
        of.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
        of.setCreatedfrom(EBIPGFactory.ebiUser);
        of.setReceivervia(ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedItem().toString());
        of.setGender(ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").getSelectedItem().toString());
        of.setName(ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").getText());
        of.setSurname(ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").getText());
        of.setPosition(ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").getText());
        of.setPbox(ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").getText());
        of.setStreet(ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").getText());
        of.setLocation(ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").getText());
        of.setZip(ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").getText());
        of.setEmail(ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").getText());
        of.setFax(ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").getText());
        of.setPhone(phoneForContact.getText());
        of.setCountry(ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").getText());
        if(this.receiver == null){
            this.dataControlOffer.getOfferRecieverList().add(of);
            this.dataControlOffer.getCompOffer().getCompanyofferreceivers().add(of);
        }
    }

    public void addReciever1() {

        Companyorderreceiver of;

        if(this.receiver1 == null){
           of =  new Companyorderreceiver();
        }else{
           of = this.receiver1; 
        }

        of.setCompanyorder(this.dataControlOrder.getCompOrder());
        of.setCnum(ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").isSelected() ? 1 : 0);
        of.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
        of.setCreatedfrom(EBIPGFactory.ebiUser);
        of.setReceivervia(ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedItem().toString());
        of.setGender(ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").getSelectedItem().toString());
        of.setName(ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").getText());
        of.setSurname(ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").getText());
        of.setPosition(ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").getText());
        of.setPbox(ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").getText());
        of.setStreet(ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").getText());
        of.setLocation(ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").getText());
        of.setZip(ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").getText());
        of.setEmail(ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").getText());
        of.setFax(ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").getText());
        of.setCountry(ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").getText());
        of.setPhone(phoneForContact.getText());

        if(this.receiver1 == null){
            this.dataControlOrder.getCompOrder().getCompanyorderreceivers().add(of);
        }
    }

    public void addReciever2() {

        Crmcampaignreceiver campaignre;
        if(this.receiver2 == null){
         campaignre = new Crmcampaignreceiver();
        }else{
           campaignre = this.receiver2;
        }

        campaignre.setCrmcampaign(dataControlCampaign.getCampaign());
        campaignre.setCnum(ebiModule.guiRenderer.getCheckBox("mainContact","addNewReceiverDialog").isSelected() ? 1 : 0);
        campaignre.setCompanynumber(ebiModule.guiRenderer.getTextfield("companyNrText","addNewReceiverDialog").getText());
        campaignre.setCompanyname(ebiModule.guiRenderer.getTextfield("companyNameText","addNewReceiverDialog").getText());
        campaignre.setCreateddate(new Date());
        campaignre.setCreatedfrom(EBIPGFactory.ebiUser);
        campaignre.setReceivervia(ebiModule.guiRenderer.getComboBox("typeDispatchText","addNewReceiverDialog").getSelectedItem().toString());
        campaignre.setGender(ebiModule.guiRenderer.getComboBox("genderText","addNewReceiverDialog").getSelectedItem().toString());
        campaignre.setName(ebiModule.guiRenderer.getTextfield("nameText","addNewReceiverDialog").getText());
        campaignre.setSurname(ebiModule.guiRenderer.getTextfield("surnameText","addNewReceiverDialog").getText());
        campaignre.setPosition(ebiModule.guiRenderer.getTextfield("positionText","addNewReceiverDialog").getText());
        campaignre.setPbox(ebiModule.guiRenderer.getTextfield("postcodeText","addNewReceiverDialog").getText());
        campaignre.setStreet(ebiModule.guiRenderer.getTextfield("streetText","addNewReceiverDialog").getText());
        campaignre.setLocation(ebiModule.guiRenderer.getTextfield("locationText","addNewReceiverDialog").getText());
        campaignre.setZip(ebiModule.guiRenderer.getTextfield("zipText","addNewReceiverDialog").getText());
        campaignre.setEmail(ebiModule.guiRenderer.getTextfield("emailText","addNewReceiverDialog").getText());
        campaignre.setFax(ebiModule.guiRenderer.getTextfield("faxText","addNewReceiverDialog").getText());
        campaignre.setCountry(ebiModule.guiRenderer.getTextfield("countryText","addNewReceiverDialog").getText());
        campaignre.setPhone(phoneForContact.getText());

        if(this.receiver2 == null){
            dataControlCampaign.getCampaignReceiverList().add(campaignre);
            dataControlCampaign.getCampaign().getCrmcampaignreceivers().add(campaignre);
        }
    }
}