package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companymeetingcontacts;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitycontact;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIMeetingAddContactDialog {

    private EBICRMModule ebiModule = null;
    private boolean isMeeting = false;
    private boolean isEdit = false;
    private Companymeetingcontacts contact = null;
    private Companyopportunitycontact ocontact = null;


    public EBIMeetingAddContactDialog(EBICRMModule module, boolean isMeeting,Companymeetingcontacts contact, Companyopportunitycontact ocontact,boolean isEdit) {
        ebiModule = module;
        this.isMeeting = isMeeting;
        this.contact = contact;
        this.ocontact = ocontact;
        this.isEdit = isEdit;
        ebiModule.guiRenderer.loadGUI("CRMDialog/addNewContactDialog.xml");
        ebiModule.guiRenderer.getComboBox("genderText","addNewContactDialog").setModel(new javax.swing.DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), EBIPGFactory.getLANG("EBI_LANG_C_MALE"), EBIPGFactory.getLANG("EBI_LANG_C_FEMALE")}));
    }


    public void setVisible(){

        ebiModule.guiRenderer.getLabel("email","addNewContactDialog").setIcon(EBIConstant.ICON_SEND_MAIL);
        ebiModule.guiRenderer.getTimepicker("birddateText","addNewContactDialog").setFormats(EBIPGFactory.DateFormat);
        ebiModule.guiRenderer.getTimepicker("birddateText","addNewContactDialog").setDate(new java.util.Date());

        ebiModule.guiRenderer.getButton("newContact", "addNewContactDialog").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newContact", "addNewContactDialog").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetFields();
				//TODO RESET FIELD
			}
		});
        
        ebiModule.guiRenderer.getButton("searchContact", "addNewContactDialog").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchContact", "addNewContactDialog").addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 EBIDialogSearchContact addCon = new EBIDialogSearchContact(ebiModule,false);

                 addCon.setValueToComponent(ebiModule.guiRenderer.getComboBox("genderText","addNewContactDialog"), "Gender");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("positionText","addNewContactDialog"), "Position");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("surnameText","addNewContactDialog"), "Surname");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("nameText","addNewContactDialog"), "contact.Name");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("streetNrText","addNewContactDialog"), "Street");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("zipText","addNewContactDialog"), "Zip");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("locationText","addNewContactDialog"), "Location");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("countryText","addNewContactDialog"), "Country");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("pboxText","addNewContactDialog"), "PBox");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("emailText","addNewContactDialog"), "contact.EMail");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("faxText","addNewContactDialog"), "contact.Fax");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("telephoneText","addNewContactDialog"), "contact.PHONE");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("mobileText","addNewContactDialog"), "contact.MOBILE");
                 
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTimepicker("birddateText","addNewContactDialog"), "contact.BIRDDATE");
                 addCon.setValueToComponent(ebiModule.guiRenderer.getTextarea("contactDescription","addNewContactDialog"), "contact.DESCRIPTION");
                 
                 addCon.setVisible();
			}
		});
        
        ebiModule.guiRenderer.getButton("closeButton","addNewContactDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog("addNewContactDialog").setVisible(false);
                }
        });
        
        ebiModule.guiRenderer.getButton("applyButton","addNewContactDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!validateInput()) {
                        return;
                    }

                    if(isMeeting){
                        if(isEdit){
                            contact.setPos(ebiModule.guiRenderer.getCheckBox("mainContact","addNewContactDialog").isSelected() ? 1 : 0);
                            ebiModule.getMeetingProtocol().meetingDataControl.addContact(EBIMeetingAddContactDialog.this,contact);
                        }else{
                            Companymeetingcontacts contact = new Companymeetingcontacts();
                            contact.setCreateddate(new Date());
                            contact.setPos(ebiModule.guiRenderer.getCheckBox("mainContact","addNewContactDialog").isSelected() ? 1 : 0);
                            contact.setCreatedfrom(EBIPGFactory.ebiUser);
                            ebiModule.getMeetingProtocol().meetingDataControl.addContact(EBIMeetingAddContactDialog.this,contact);
                        }
                    }else{

                        if(isEdit){
                            ocontact.setPos(ebiModule.guiRenderer.getCheckBox("mainContact","addNewContactDialog").isSelected() ? 1 : 0);
                            ebiModule.getOpportunityPane().opportuniyDataControl.addContact(EBIMeetingAddContactDialog.this,ocontact);
                        }else{
                            Companyopportunitycontact contact = new Companyopportunitycontact();
                            contact.setCreateddate(new Date());
                            contact.setPos(ebiModule.guiRenderer.getCheckBox("mainContact","addNewContactDialog").isSelected() ? 1 : 0);
                            contact.setCreatedfrom(EBIPGFactory.ebiUser);
                            ebiModule.getOpportunityPane().opportuniyDataControl.addContact(EBIMeetingAddContactDialog.this,contact);
                        }
                    }
                    ebiModule.guiRenderer.getEBIDialog("addNewContactDialog").setVisible(false);
                }
            });
       ebiModule.guiRenderer.showGUI();
    }

    
    public void resetFields(){
    	setBirddateText(null);
    	setCountry("");
    	setDescriptionText("");
    	setEMailText("");
    	setFaxText("");
    	setGenderText(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
    	setLocation("");
    	setMainContact(false);
    	setMobileText("");
    	setNameText("");
    	setPbox("");
    	setPositionText("");
    	setStreet("");
    	setSurnameText("");
    	setTelephoneText("");
    	setZip("");
    }
    
    
    public String getSurnameText() {
        return ebiModule.guiRenderer.getTextfield("surnameText","addNewContactDialog").getText();
    }

    public void setSurnameText(String surname) {
        ebiModule.guiRenderer.getTextfield("surnameText","addNewContactDialog").setText(surname);
    }

    public String getNameText() {
        return ebiModule.guiRenderer.getTextfield("nameText","addNewContactDialog").getText();
    }

    public void setNameText(String name) {
        ebiModule.guiRenderer.getTextfield("nameText","addNewContactDialog").setText(name);
    }

    public Date getBirddateText() {
        return ebiModule.guiRenderer.getTimepicker("birddateText","addNewContactDialog").getDate();
    }

    public void setBirddateText(Date date) {
        ebiModule.guiRenderer.getTimepicker("birddateText","addNewContactDialog").setDate(date);
    }

    public String getGenderText() {
        return ebiModule.guiRenderer.getComboBox("genderText","addNewContactDialog").getSelectedItem().toString();
    }

    public void setGenderText(String gender) {
        ebiModule.guiRenderer.getComboBox("genderText","addNewContactDialog").setSelectedItem(gender);
    }

    public String getPositionText() {
        return ebiModule.guiRenderer.getTextfield("positionText","addNewContactDialog").getText();
    }

    public void setPositionText(String position) {
        ebiModule.guiRenderer.getTextfield("positionText","addNewContactDialog").setText(position);
    }

    public String getTelephoneText() {
        return ebiModule.guiRenderer.getTextfield("telephoneText","addNewContactDialog").getText();
    }

    public void setTelephoneText(String telephone) {
        ebiModule.guiRenderer.getTextfield("telephoneText","addNewContactDialog").setText(telephone);
    }

    public String getMobileText() {
        return ebiModule.guiRenderer.getTextfield("mobileText","addNewContactDialog").getText();
    }

    public void setMobileText(String mobile) {
        ebiModule.guiRenderer.getTextfield("mobileText","addNewContactDialog").setText(mobile);
    }

    public String getEMailText() {
        return ebiModule.guiRenderer.getTextfield("emailText","addNewContactDialog").getText();
    }

    public void setEMailText(String email) {
        ebiModule.guiRenderer.getTextfield("emailText","addNewContactDialog").setText(email);
    }

    public String getFaxText() {
        return ebiModule.guiRenderer.getTextfield("faxText","addNewContactDialog").getText();
    }

    public void setFaxText(String fax) {
        ebiModule.guiRenderer.getTextfield("faxText","addNewContactDialog").setText(fax);
    }

    public String getDescriptionText() {
        return ebiModule.guiRenderer.getTextarea("contactDescription","addNewContactDialog").getText();
    }

    public void setDescriptionText(String description) {
        ebiModule.guiRenderer.getTextarea("contactDescription","addNewContactDialog").setText(description);
    }

    public void setMainContact(boolean isSelected){
        ebiModule.guiRenderer.getCheckBox("mainContact","addNewContactDialog").setSelected(isSelected);
    }
    
    public void setStreet(String street){
    	 ebiModule.guiRenderer.getTextfield("streetNrText","addNewContactDialog").setText(street);
    }
    
    public String getStreet(){
    	return ebiModule.guiRenderer.getTextfield("streetNrText","addNewContactDialog").getText();
    }
    
    public void setZip(String zip){
    	ebiModule.guiRenderer.getTextfield("zipText","addNewContactDialog").setText(zip);
    }
    
    public String getZip(){
    	return ebiModule.guiRenderer.getTextfield("zipText","addNewContactDialog").getText();
    }
    
    public void setLocation(String location){
    	ebiModule.guiRenderer.getTextfield("locationText","addNewContactDialog").setText(location);
    }
    
    public String getLocation(){
    	return ebiModule.guiRenderer.getTextfield("locationText","addNewContactDialog").getText();
    }
    
    public void setPbox(String pbox){
    	ebiModule.guiRenderer.getTextfield("pboxText","addNewContactDialog").setText(pbox);
    }
    
    public String getPbox(){
    	return ebiModule.guiRenderer.getTextfield("pboxText","addNewContactDialog").getText();
    }
    
    public void setCountry(String country){
    	ebiModule.guiRenderer.getTextfield("countryText","addNewContactDialog").setText(country);
    }
    
    public String getCountry(){
    	return ebiModule.guiRenderer.getTextfield("countryText","addNewContactDialog").getText();
    }

    private boolean validateInput() {
        if (ebiModule.guiRenderer.getComboBox("genderText","addNewContactDialog").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_GENDER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("nameText","addNewContactDialog").getText().trim())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}