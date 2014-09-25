package ebiCRM.data.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMContactPane;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companycontactaddress;
import ebiNeutrinoSDK.model.hibernate.Companycontacts;

public class EBIDataControlContact {

    private Companycontacts contact = null;
    private EBICRMContactPane contactPane = null;

    public EBIDataControlContact(EBICRMContactPane pane) {
        this.contactPane = pane;
        contact = new Companycontacts();
    }

    public boolean dataStore(boolean isEdit) {
        try {
            contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", true);
            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            if (isEdit == true) {
                dataHistory(contactPane.ebiModule.ebiPGFactory.company);
                contact.setChangeddate(new Date());
                contact.setChangedfrom(EBIPGFactory.ebiUser);
            } else {
                contact.setCreateddate(new Date());
                contact.setCreatedfrom(EBIPGFactory.ebiUser);
                contactPane.isEdit = true;
            }

            contact.setCompany(contactPane.ebiModule.ebiPGFactory.company);

            if(contactPane.guiRenderer.getComboBox("genderTex","Contact").getSelectedItem() != null){
                contact.setGender(contactPane.guiRenderer.getComboBox("genderTex","Contact").getSelectedItem().toString());
            }

            contact.setPosition(contactPane.guiRenderer.getTextfield("positionText","Contact").getText());
            contact.setSurname(contactPane.guiRenderer.getTextfield("surnameText","Contact").getText());
            contact.setName(contactPane.guiRenderer.getTextfield("nameText","Contact").getText());
            contact.setMittelname(contactPane.guiRenderer.getTextfield("middleNameText","Contact").getText());
            contact.setTitle(contactPane.guiRenderer.getTextfield("titleText", "Contact").getText());

            if(contactPane.guiRenderer.getTimepicker("birddateText","Contact").getDate() != null){
                contact.setBirddate(contactPane.guiRenderer.getTimepicker("birddateText","Contact").getDate());
            }

            contact.setPhone(contactPane.guiRenderer.getTextfield("telefonText","Contact").getText());
            contact.setFax(contactPane.guiRenderer.getTextfield("faxText","Contact").getText());
            contact.setMobile(contactPane.guiRenderer.getTextfield("mobileText","Contact").getText());
            contact.setEmail(contactPane.guiRenderer.getTextfield("emailText","Contact").getText());
            contact.setDescription(contactPane.guiRenderer.getTextarea("contactDescription", "Contact").getText());
            contact.setMaincontact(contactPane.guiRenderer.getCheckBox("mainContactText","Contact").isSelected());

            // Save contact address
            if (!this.contact.getCompanycontactaddresses().isEmpty()) {
                Iterator iter = this.contact.getCompanycontactaddresses().iterator();
                while(iter.hasNext()){
                    Companycontactaddress cadd = (Companycontactaddress) iter.next();
                    cadd.setCompanycontacts(contact);
                    if(cadd.getAddressid() < 0){
                        cadd.setAddressid(null);
                    }
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cadd);
                }
            }

            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(contact);

            contactPane.ebiModule.ebiPGFactory.getDataStore("Contact","ebiSave",true);
            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
          
            if(!isEdit){
            	contactPane.ebiModule.guiRenderer.getVisualPanel("Contact").setID(contact.getContactid());
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(contactPane.ebiModule.ebiPGFactory.company != null){
            contactPane.ebiModule.ebiPGFactory.company.getCompanycontactses().add(contact);
            if(contact.getCompany().getIsactual()){
                contactPane.ebiModule.ebiPGFactory.loadStandardCompany();
            }
        }

        dataShow();
        showCompanyContactAddress();
        contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", false);
        return true;
    }
    
    public void dataCopy(int id){
    	
    	try{
    	Query y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where contactid=? ").setInteger(0,id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
        	
        	Companycontacts con = (Companycontacts) iter.next();
        	contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(con);
            contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", true);

            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            Companycontacts conx = new Companycontacts();
            conx.setCreateddate(new Date());
            conx.setCreatedfrom(EBIPGFactory.ebiUser);

            conx.setCompany(con.getCompany());
            conx.setGender(con.getGender());
            conx.setPosition(con.getPosition()+" - (Copy)");
            conx.setSurname(con.getSurname());
            conx.setName(con.getName());
            conx.setMittelname(con.getMittelname());
            conx.setTitle(con.getTitle());
            conx.setBirddate(con.getBirddate());
            conx.setPhone(con.getPhone());
            conx.setFax(con.getFax());
            conx.setMobile(con.getMobile());
            conx.setEmail(con.getEmail());
            conx.setDescription(con.getDescription());
            conx.setMaincontact(con.getMaincontact());
            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(conx);
            
            // Save contact address
            if (!con.getCompanycontactaddresses().isEmpty()) {
                
            	Iterator itx = con.getCompanycontactaddresses().iterator();
                
                while(itx.hasNext()){
                    Companycontactaddress cadd = (Companycontactaddress) itx.next();

                    Companycontactaddress adc = new Companycontactaddress();
                    adc.setCreateddate(new Date());
                    adc.setCreatedfrom(EBIPGFactory.ebiUser);
                    adc.setAddresstype(cadd.getAddresstype());
                    adc.setCountry(cadd.getCountry());
                    adc.setLocation(cadd.getLocation());
                    adc.setStreet(cadd.getStreet());
                    adc.setPbox(cadd.getPbox());
                    adc.setZip(cadd.getZip());
                    adc.setCompanycontacts(conx);
                   
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(adc);
                }
            }

            contactPane.ebiModule.ebiPGFactory.getDataStore("Contact","ebiSave",true);
            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            dataShow();
            
            contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").
				changeSelection(contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").
						convertRowIndexToView(contactPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(contactPane.tableModel.data,6, conx.getContactid())),0,false,false);
        }
        
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	
    	
        contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", false);
            
            
    }


    public void dataMove(int id){

        try{
            Query y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where contactid=? ").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(contactPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companycontacts con = (Companycontacts) iter.next();
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(con);
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", true);

                    con.setCompany((Company) x.list().get(0));
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(con);

                    contactPane.ebiModule.ebiPGFactory.getDataStore("Contact", "ebiSave",true);
                    contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    contactPane.ebiModule.ebiContainer.showInActionStatus("Contact", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }

    }


    public void dataEdit(int id) {

        dataNew();
        
        Query y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where contactid=? ").setInteger(0,id);
        
        if(y.list().size() > 0){
        	Iterator iter = y.iterate();
            contact = (Companycontacts) iter.next();
            contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(contact);
            contactPane.ebiModule.guiRenderer.getVisualPanel("Contact").setID(contact.getContactid());

            if(contact.getGender() != null){
                contactPane.guiRenderer.getComboBox("genderTex","Contact").setSelectedItem(contact.getGender());
            }

            contactPane.guiRenderer.getTextfield("surnameText","Contact").setText(contact.getSurname());
            contactPane.guiRenderer.getTextfield("nameText","Contact").setText(contact.getName());
            contactPane.guiRenderer.getTextfield("middleNameText","Contact").setText(contact.getMittelname());
            contactPane.guiRenderer.getTextfield("positionText","Contact").setText(contact.getPosition());
            contactPane.guiRenderer.getTextfield("titleText","Contact").setText(contact.getTitle());

            if(contact.getBirddate() != null){
                contactPane.guiRenderer.getTimepicker("birddateText","Contact").setDate(contact.getBirddate());
                contactPane.guiRenderer.getTimepicker("birddateText","Contact").getEditor().setText(contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getBirddate()));
            }

            contactPane.guiRenderer.getTextfield("telefonText","Contact").setText(contact.getPhone());
            contactPane.guiRenderer.getTextfield("faxText","Contact").setText(contact.getFax());
            contactPane.guiRenderer.getTextfield("mobileText","Contact").setText(contact.getMobile());
            contactPane.guiRenderer.getTextfield("emailText","Contact").setText(contact.getEmail());
            contactPane.guiRenderer.getTextarea("contactDescription","Contact").setText(contact.getDescription());

            if(contact.getMaincontact() != null && contact.getMaincontact() == true){
                contactPane.guiRenderer.getCheckBox("mainContactText","Contact").setSelected(true);
            }

            contactPane.guiRenderer.getVisualPanel("Contact").setCreatedDate(contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getCreateddate() == null ? new Date() : contact.getCreateddate()));
            contactPane.guiRenderer.getVisualPanel("Contact").setCreatedFrom(contact.getCreatedfrom() == null ? "" : contact.getCreatedfrom());

            if (contact.getChangeddate() != null) {
                contactPane.guiRenderer.getVisualPanel("Contact").setChangedDate(contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getChangeddate()));
                contactPane.guiRenderer.getVisualPanel("Contact").setChangedFrom(contact.getChangedfrom());
            } else {
                contactPane.guiRenderer.getVisualPanel("Contact").setChangedDate("");
                contactPane.guiRenderer.getVisualPanel("Contact").setChangedFrom("");
            }
            try {
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(contact);
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            } catch (Exception e) {
                e.printStackTrace();
            }

            contactPane.ebiModule.ebiPGFactory.getDataStore("Contact","ebiEdit",true);
            this.showCompanyContactAddress();
            
            contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").
				changeSelection(contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").
						convertRowIndexToView(contactPane.ebiModule.dynMethod.
							getIdIndexFormArrayInATable(contactPane.tableModel.data, 6, id)),0,false,false);
           
        }else{
        	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }

    }

    public void dataDelete(int id) {
        try{
        Query y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where contactid=? ").setInteger(0,id);
        
        if(y.list().size() > 0){
    		Iterator iter = y.iterate();

        	Companycontacts con = (Companycontacts) iter.next();

            try {
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(con);

                contactPane.ebiModule.ebiPGFactory.getDataStore("Contact","ebiDelete",true);
                contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            } catch (HibernateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(this.contactPane.ebiModule.ebiPGFactory.company != null){
                this.contactPane.ebiModule.ebiPGFactory.company.getCompanycontactses().remove(con);
            }
            this.dataShow();
        }
        dataNew();
        }catch (Exception ex){}
    }

    public void dataShow() {
      try{
        int srow =0;

        if(contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact") != null){
            srow =contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").getSelectedRow();
        }
        Query y = null;

        if(contactPane.ebiModule.companyID != -1){
            y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where company.companyid=? order by createddate desc  ");
            y.setInteger(0, contactPane.ebiModule.companyID);
        }else{
            y = contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companycontacts where company.companyid IS NULL order by createddate desc  ");
        }
        
        if(y.list().size() > 0){
        
            contactPane.tableModel.data = new Object[y.list().size()][7];

            Iterator itr = y.iterate();
            int i = 0;
            String mainContactMarker;
            while (itr.hasNext()) {
                Companycontacts obj = (Companycontacts) itr.next();
                if(obj.getMaincontact() != null && obj.getMaincontact() == true){
                   mainContactMarker = "**";
                }else{
                   mainContactMarker = "";  
                }
                contactPane.tableModel.data[i][0] = obj.getPosition() == null ? "" : obj.getPosition();
                contactPane.tableModel.data[i][1] = obj.getGender() == null ? "" : mainContactMarker+obj.getGender();
                contactPane.tableModel.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                contactPane.tableModel.data[i][3] = obj.getName() == null ? "" : obj.getName();
                contactPane.tableModel.data[i][4] = obj.getPhone() == null ? "" : obj.getPhone();
                contactPane.tableModel.data[i][5] = obj.getMobile() == null ? "" : obj.getMobile();
                contactPane.tableModel.data[i][6] = obj.getContactid();
                i++;
            }
        }else{
          contactPane.tableModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "",""}};
        }

        contactPane.ebiModule.getCompanyPane().ctabModel.data = contactPane.tableModel.data;
        contactPane.ebiModule.getCompanyPane().ctabModel.fireTableDataChanged();

        contactPane.tableModel.fireTableDataChanged();
        if(contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact") != null){
            contactPane.ebiModule.guiRenderer.getTable("companyContacts","Contact").changeSelection(srow,0,false,false);
        }
      }catch (Exception ex){}
    }

    public void dataNew() {
      try{
        contact = new Companycontacts();
        contactPane.initialize();
        contactPane.ebiModule.ebiPGFactory.getDataStore("Contact","ebiNew",true);
        contactPane.ebiModule.guiRenderer.getVisualPanel("Contact").setID(-1);
        dataShow();
        showCompanyContactAddress();
      }catch (Exception ex){}
    }

    private void dataHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();
        if(contact.getCreateddate() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getCreateddate()));
        }
        if(contact.getCreatedfrom() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + contact.getCreatedfrom());
        }
        if (contact.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getChangeddate()));
        }
        if(contact.getChangedfrom() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + contact.getChangedfrom());
        }
        if(contact.getGender() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_GENDER") + ": " + (contact.getGender().equals(contactPane.guiRenderer.getComboBox("genderTex","Contact").getSelectedItem().toString()) == true ? contact.getGender() : contact.getGender()+"$") );
        }
        if(contact.getSurname() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_SURNAME") + ": " + (contact.getSurname().equals(contactPane.guiRenderer.getTextfield("surnameText","Contact").getText()) == true ? contact.getSurname() : contact.getSurname()+"$") );
        }
        if(contact.getName() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_CNAME") + ": " + (contact.getName().equals(contactPane.guiRenderer.getTextfield("nameText","Contact").getText()) == true ? contact.getName() : contact.getName()+"$") );
        }
        if(contact.getMittelname() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_MITTEL_NAME") + ": " + (contact.getMittelname().equals(contactPane.guiRenderer.getTextfield("middleNameText","Contact").getText()) == true ? contact.getMittelname() : contact.getMittelname()+"$") );
        }
        if(contact.getPosition() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_POSITION") + ": " + (contact.getPosition().equals(contactPane.guiRenderer.getTextfield("positionText","Contact").getText()) == true ? contact.getPosition() : contact.getPosition()+"$") );
        }
        if(contact.getTitle() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_TITLE") + ": " + (contact.getTitle().equals(contactPane.guiRenderer.getTextfield("titleText","Contact").getText()) == true ? contact.getTitle() : contact.getTitle()+"$") );
        }
        if(contact.getBirddate() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_BIRDDATE") + ": " + (contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getBirddate()).equals(contactPane.guiRenderer.getTimepicker("birddateText","Contact").getEditor().getText()) == true ? contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getBirddate()) : contactPane.ebiModule.ebiPGFactory.getDateToString(contact.getBirddate())+"$") );
        }
        if(contact.getPhone() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE") + ": " + (contact.getPhone().equals(contactPane.guiRenderer.getTextfield("telefonText","Contact").getText()) == true ? contact.getPhone() : contact.getPhone()+"$") );
        }
        if(contact.getFax() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_FAX") + ": " + (contact.getFax().equals(contactPane.guiRenderer.getTextfield("faxText","Contact").getText()) == true ? contact.getFax() : contact.getFax()+"$") );
        }
        if(contact.getMobile() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE") + ": " + (contact.getMobile().equals(contactPane.guiRenderer.getTextfield("mobileText","Contact").getText()) == true ? contact.getMobile() : contact.getMobile()+"$") );
        }
        if(contact.getEmail() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_EMAIL") + ": " + (contact.getEmail().equals(contactPane.guiRenderer.getTextfield("emailText","Contact").getText()) == true ? contact.getEmail() : contact.getEmail()+"$") );
        }
        if(contact.getDescription() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_DESCRIPTION") + ": " + (contact.getDescription().equals(contactPane.guiRenderer.getTextarea("contactDescription","Contact").getText()) == true ? contact.getDescription() : contact.getDescription()+"$") );
        }
        list.add("*EOR*"); // END OF RECORD

        if (!contact.getCompanycontactaddresses().isEmpty()) {

            Iterator iter = contact.getCompanycontactaddresses().iterator();
            while (iter.hasNext()) {
                Companycontactaddress obj = (Companycontactaddress) iter.next();
                if(obj.getAddresstype() != null){
                    list.add(EBIPGFactory.getLANG("EBI_LANG_C_ADRESS_TYPE") + ": " + obj.getAddresstype() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_ADRESS_TYPE") + ": " + obj.getAddresstype());
                }
                if(obj.getStreet() != null){
                    list.add(EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ": " + obj.getStreet() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ": " + obj.getStreet());
                }
                if(obj.getZip() != null){
                    list.add(EBIPGFactory.getLANG("EBI_LANG_C_ZIP_LOCATION") + obj.getZip() + " " + obj.getLocation() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_ZIP_LOCATION") + obj.getZip() + " " + obj.getLocation());
                }
                if(obj.getPbox() != null){
                    list.add(EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ": " + obj.getPbox() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ": " + obj.getPbox());
                }
                if(obj.getCountry() != null){
                    list.add(EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + obj.getCountry() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + obj.getCountry());
                }
                list.add("*EOR*"); // END OF RECORD
            }
        }

        try {
            contactPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Contact", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCompanyContactAddress() {
       try{
        if (this.contact.getCompanycontactaddresses().size() > 0) {

            contactPane.addressModel.data = new Object[this.contact.getCompanycontactaddresses().size()][7];

            Iterator itr = this.contact.getCompanycontactaddresses().iterator();
            int i = 0;
            while (itr.hasNext()) {

                Companycontactaddress obj = (Companycontactaddress) itr.next();
               
                contactPane.addressModel.data[i][0] = obj.getAddresstype() == null ? "" : obj.getAddresstype();
                contactPane.addressModel.data[i][1] = obj.getStreet() == null ? "" : obj.getStreet();
                contactPane.addressModel.data[i][2] = obj.getZip() == null ? "" : obj.getZip();
                contactPane.addressModel.data[i][3] = obj.getLocation() == null ? "" : obj.getLocation();
                contactPane.addressModel.data[i][4] = obj.getPbox() == null ? "" : obj.getPbox();
                contactPane.addressModel.data[i][5] = obj.getCountry() == null ? "" : obj.getCountry();
                contactPane.addressModel.data[i][6] = obj.getAddressid();
                i++;
            }

        } else {
            contactPane.addressModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
        }
         contactPane.addressModel.fireTableDataChanged();
       }catch (Exception ex){}
    }

    public void dataAddressDelete(int id) throws Exception {
        try{
            Iterator iter = this.contact.getCompanycontactaddresses().iterator();
            while (iter.hasNext()) {

                Companycontactaddress address = (Companycontactaddress) iter.next();

                    if (address.getAddressid() == id) {

                        this.contact.getCompanycontactaddresses().remove(address);

                        if(id >= 0){
                           contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                           contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(address);
                           contactPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                        }
                        break;
                    }
            }
            this.showCompanyContactAddress();
        }catch (Exception ex){}
    }

    public Companycontacts getContact() {
        return contact;
    }

    public Set<Companycontacts> getContactList() {
        return this.contactPane.ebiModule.ebiPGFactory.company.getCompanycontactses();
    }

    public Set<Companycontactaddress> getCoaddressList() {
        return contact.getCompanycontactaddresses();
    }
}