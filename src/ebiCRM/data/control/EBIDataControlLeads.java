package ebiCRM.data.control;

import java.awt.Cursor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import ebiNeutrinoSDK.model.hibernate.Companycontactaddress;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMLead;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyaddress;
import ebiNeutrinoSDK.model.hibernate.Companycontacts;


public class EBIDataControlLeads {

     private EBICRMLead ebiLeadsPanel = null;
     private Company company =  null;
     private Companycontacts contact = null;
     private Companyaddress address = null;
     public boolean isEdit = false;
     private String searchText = "";


     public EBIDataControlLeads(EBICRMLead leads){
         this.ebiLeadsPanel = leads;

     }


    public boolean dataStore() {
      try{
          //ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
          ebiLeadsPanel.ebiModule.ebiContainer.showInActionStatus("Leads", true);
          //ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

          if (isEdit == true) {
            company.setChangeddate(new Date());
            contact.setChangeddate(new Date());
            address.setChangeddate(new Date());

            company.setChangedfrom(EBIPGFactory.ebiUser);
            contact.setChangedfrom(EBIPGFactory.ebiUser);
            address.setChangedfrom(EBIPGFactory.ebiUser);

          } else {
            
            company = new Company();
            company.setCreatedfrom(EBIPGFactory.ebiUser);
            company.setCreateddate(new Date());

            contact = new Companycontacts();
            contact.setCreateddate(new Date());
            contact.setCreatedfrom(EBIPGFactory.ebiUser);

            address = new Companyaddress();
            address.setCreateddate(new Date());
            address.setCreatedfrom(EBIPGFactory.ebiUser);

          }

          company.setName(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("compNameText","Leads").getText());
          if(!isEdit){
              contact.setContactid(null);
              company.setCategory("Leads");
              company.setCustomernr("-1");
              company.setCompanynumber(-1);
              company.setBeginchar("");
              company.setCooperation("");
              company.setIslock(false);
              company.setIsactual(false);
          }
          company.setWeb(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("internetText","Leads").getText());

          if(ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("classificationText","Leads").getSelectedItem() != null){
            company.setQualification(ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("classificationText","Leads").getSelectedItem().toString());
          }

          company.setDescription(ebiLeadsPanel.ebiModule.guiRenderer.getTextarea("descriptionText","Leads").getText());


          if(ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("genderText","Leads").getSelectedItem() != null){
              if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("genderText","Leads").getSelectedItem().toString())){
                  contact.setGender(ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("genderText","Leads").getSelectedItem().toString());
              }
          }

          contact.setCompany(company);
          contact.setTitle(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("titleText","Leads").getText());
          contact.setPosition(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("positionText","Leads").getText());
          contact.setName(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactNameText","Leads").getText());
          contact.setSurname(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactSurnameText","Leads").getText());
          contact.setPhone(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("telephoneText","Leads").getText());
          contact.setMobile(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactMobileText","Leads").getText());
          contact.setFax(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("faxText","Leads").getText());
          contact.setEmail(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("emailText","Leads").getText());



          address.setCompany(company);
          address.setAddresstype(address.getAddresstype() == null ? "" : address.getAddresstype());
          address.setZip(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressZipText","Leads").getText());
          address.setLocation(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCityText","Leads").getText());
          address.setStreet(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressStrNrText","Leads").getText());
          address.setCountry(ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCountryText","Leads").getText());

          contact.setCompanycontactaddresses(null);
          company.getCompanyaddresses().add(address);
          company.getCompanycontactses().add(contact);
          ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(company);
          ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(address);
          ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(contact);

          //Fill Visitcard
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("compNameLabel","Leads").setText(company.getName() == null ? "" : company.getName());
          
          String cName = contact.getGender() == null ? "" : contact.getGender() +" ";
                 cName += contact.getTitle() == null ? "" : contact.getTitle()+ " ";
                 cName += contact.getName() == null ? "" : contact.getName()+ " ";
                 cName += contact.getSurname() == null ? "" : contact.getSurname();
                ebiLeadsPanel.ebiModule.guiRenderer.getLabel("cName","Leads").setText(cName);

          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("addressLabel","Leads").setText(address.getStreet() == null ? "" : address.getStreet());

          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("zipLocationLabel","Leads").setText(address.getZip() == null ? "" : address.getZip()+" "+
                                                                                                address.getLocation() == null ? "" : address.getLocation());

          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("phoneLabel","Leads").setText(contact.getPhone() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE")+": "+contact.getPhone());
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("faxLabel","Leads").setText(contact.getPhone() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_FAX")+": "+contact.getPhone());
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("positionLabel","Leads").setText(contact.getPosition() == null ? "" : contact.getPosition());
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("webLabel","Leads").setText(company.getWeb() == null ? "" : company.getWeb());
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("mobileLabel","Leads").setText(contact.getMobile() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE")+": "+contact.getMobile());
          ebiLeadsPanel.ebiModule.guiRenderer.getLabel("emailLabel","Leads").setText(contact.getEmail() == null ? "" : contact.getEmail());

          ebiLeadsPanel.ebiModule.ebiPGFactory.getDataStore("Leads","ebiSave",true);
          ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
          
          if(!isEdit){
        	  ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setID(company.getCompanyid());
          }
          isEdit = true;
      }catch(Exception ex){ex.printStackTrace(); return false;}

          if("".equals(this.searchText)){
            dataShow();
          }else{
            dataShow(this.searchText);
          }
          
          ebiLeadsPanel.ebiModule.ebiContainer.showInActionStatus("Leads",false);

      return true;
    }

    
    
    public void dataCopy(int id){
    	 
        	
        	Query query;
            try {
                ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

                query = ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery(
                        "from Company where companyid=? ").setInteger(0, id);

                Iterator iter = query.iterate();
                if (iter.hasNext()) {
                    Company cmp = (Company) iter.next();
                        ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(cmp);
			            ebiLeadsPanel.ebiModule.ebiContainer.showInActionStatus("Leads", true);
			              
			            Company cp = new Company();
			            cp.setCreateddate(new Date());
			            cp.setCreatedfrom(EBIPGFactory.ebiUser);
			
			            cp.setName(cmp.getName()+" - (Copy)");
			            cp.setCategory(cmp.getCategory());
			            cp.setCustomernr(cmp.getCustomernr());
			            cp.setCompanynumber(cmp.getCompanynumber());
			            cp.setBeginchar(cmp.getBeginchar());
			            cp.setCooperation(cmp.getCooperation());
			            cp.setIslock(cmp.getIslock());
			            cp.setIsactual(false);
			            
			            cp.setWeb(cmp.getWeb());
			            cp.setQualification(cmp.getQualification());
			            cp.setDescription(cmp.getDescription());
			            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(cp);
			            
			            // Create new copy of the contact
			            Iterator ico = cmp.getCompanycontactses().iterator();
			            while(ico.hasNext()){
			               Companycontacts coc = (Companycontacts) ico.next(); 	
			            	
			            	Companycontacts co = new Companycontacts();
			            	co.setCompany(cp);
			            	co.setCreateddate(new Date());
			            	co.setCreatedfrom(EBIPGFactory.ebiUser);
				            co.setGender(coc.getGender());
				            co.setTitle(coc.getTitle());
				            co.setPosition(coc.getPosition());
				            co.setName(coc.getName());
				            co.setSurname(coc.getSurname());
				            co.setPhone(coc.getPhone());
				            co.setMobile(coc.getMobile());
				            co.setFax(coc.getFax());
				            co.setEmail(coc.getEmail());
				            cp.getCompanycontactses().add(contact);
				            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(co);
			            }
			            
			            Iterator iad = cmp.getCompanyaddresses().iterator();
			            while(iad.hasNext()){
			            	Companyaddress adc = (Companyaddress) iad.next();
			            	
				            Companyaddress ad = new Companyaddress();     
				            
				            ad.setCreateddate(new Date());
				            ad.setCreatedfrom(EBIPGFactory.ebiUser);
				            ad.setAddresstype(adc.getAddresstype());
				            ad.setCompany(cp);
				            ad.setZip(adc.getZip());
				            ad.setLocation(adc.getLocation());
				            ad.setStreet(adc.getStreet());
				            ad.setCountry(adc.getCountry());
				            cp.getCompanyaddresses().add(address);
				            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(ad);
			            }
			            
			            ebiLeadsPanel.ebiModule.ebiPGFactory.getDataStore("Leads","ebiSave",true);
			            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                }
          	
        }catch(Exception ex){
        	ex.printStackTrace();
        }
        dataShow();
    }

    public void dataEdit(int id) {

        dataNew();

        Query query;
        try {
            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            query = ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery(
                    "from Company where companyid=? ").setInteger(0, id);

            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                company = (Company) iter.next();
                ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setID(company.getCompanyid());
                ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(company);
                Iterator cit = company.getCompanycontactses().iterator();
                while(cit.hasNext()){
                    contact = (Companycontacts) cit.next();
                    ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(contact);
                    try{
                        if(Integer.parseInt(ebiLeadsPanel.tabModel.data[ebiLeadsPanel.selectedRow][12].toString()) == contact.getContactid() ){

                            String cName = contact.getGender() == null ? "" : contact.getGender() +" ";
                            cName += contact.getTitle() == null ? "" : contact.getTitle()+ " ";
                            cName += contact.getName() == null ? "" : contact.getName()+ " ";
                            cName += contact.getSurname() == null ? "" : contact.getSurname();
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("cName","Leads").setText(cName);

                            ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("genderText","Leads").setSelectedItem(contact.getGender() == null ? EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT") : contact.getGender());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("titleText","Leads").setText(contact.getTitle() == null ? "" : contact.getTitle());

                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("positionText","Leads").setText(contact.getPosition() == null ? "" : contact.getPosition());
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("positionLabel","Leads").setText(contact.getPosition() == null ? "" : contact.getPosition());

                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactNameText","Leads").setText(contact.getName() == null ? "" : contact.getName());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactSurnameText","Leads").setText(contact.getSurname() == null ? "" : contact.getSurname());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("telephoneText","Leads").setText(contact.getPhone() == null ? "" : contact.getPhone());
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("phoneLabel","Leads").setText(contact.getPhone() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_TELEPHONE")+": "+contact.getPhone());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("faxText","Leads").setText(contact.getFax() == null ? "" : contact.getFax());
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("faxLabel","Leads").setText(contact.getPhone() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_FAX")+": "+contact.getPhone());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("emailText","Leads").setText(contact.getEmail() == null ? "" : contact.getEmail());
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("emailLabel","Leads").setText(contact.getEmail() == null ? "" : contact.getEmail());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactMobileText","Leads").setText(contact.getMobile() == null ? "" : contact.getMobile());
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("mobileLabel","Leads").setText(contact.getMobile() == null ? "" : EBIPGFactory.getLANG("EBI_LANG_C_MOBILE_PHONE")+": "+contact.getMobile());

                            break;
                        }
                    }catch(Exception ex){}
                }

                Iterator ait = company.getCompanyaddresses().iterator();
                while(ait.hasNext()){
                    address = (Companyaddress) ait.next();
                    try{
                        if(Integer.parseInt(ebiLeadsPanel.tabModel.data[ebiLeadsPanel.selectedRow][13].toString()) == address.getAddressid() ){
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("addressLabel","Leads").setText(address.getStreet() == null ? "" : address.getStreet());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressStrNrText","Leads").setText(address.getStreet() == null ? "" : address.getStreet());

                            String zipLocation = address.getZip() == null ? "" : address.getZip()+" ";
                                   zipLocation += address.getLocation() == null ? "" : address.getLocation();
                            ebiLeadsPanel.ebiModule.guiRenderer.getLabel("zipLocationLabel","Leads").setText(zipLocation);

                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressZipText","Leads").setText(address.getZip() == null ? "" : address.getZip());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCityText","Leads").setText(address.getLocation() == null ? "" : address.getLocation());
                            ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCountryText","Leads").setText(address.getCountry() == null ? "" : address.getCountry());
                            break;
                        }
                    }catch(Exception ex){}
                }

                ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedDate(ebiLeadsPanel.ebiModule.ebiPGFactory.getDateToString(company.getCreateddate()));
                ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedFrom(company.getCreatedfrom());

                if(company.getCreateddate() != null){
                    ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setChangedDate(ebiLeadsPanel.ebiModule.ebiPGFactory.getDateToString(company.getChangeddate()));
                    ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setChangedFrom(company.getChangedfrom());
                }
                ebiLeadsPanel.ebiModule.guiRenderer.getLabel("compNameLabel","Leads").setText(company.getName() == null ? "" : company.getName());   
                ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("compNameText","Leads").setText(company.getName() == null ? "" : company.getName());
                ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("internetText","Leads").setText(company.getWeb() == null ? "" : company.getWeb());
                ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("classificationText","Leads").setSelectedItem(company.getQualification() == null ? "" : company.getQualification());
                ebiLeadsPanel.ebiModule.guiRenderer.getTextarea("descriptionText","Leads").setText(company.getDescription() == null ? "" : company.getDescription());
                ebiLeadsPanel.ebiModule.guiRenderer.getLabel("webLabel","Leads").setText(company.getWeb() == null ? "" : company.getWeb());
                isEdit  = true;
                
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }

            ebiLeadsPanel.ebiModule.ebiPGFactory.getDataStore("Leads","ebiEdit",true);

            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            ebiLeadsPanel.ebiModule.guiRenderer.getPanel("businessCard","Leads").updateUI();
        }catch(Exception ex){
            ex.printStackTrace();
            isEdit = false;
        }
    }


    public boolean dataDelete(int id) {
       dataNew();
        try {
            ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            Query query = ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery(
                    "from Company where companyid=? ").setInteger(0, id);

            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                Company comp = (Company) iter.next();
                ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(comp);

                ebiLeadsPanel.ebiModule.ebiPGFactory.getDataStore("Leads","ebiDelete",true);
                ebiLeadsPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        dataShow();
        return true;
    }

    public void dataShow() {

        ResultSet set = null;


        int srow = ebiLeadsPanel.ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow();
        try {
            PreparedStatement ps1 = ebiLeadsPanel.ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("" +
                            " SELECT COMPANY.COMPANYID,COMPANY.NAME,COMPANY.CATEGORY,COMPANY.WEB,COMPANY.QUALIFICATION,COMPANY.DESCRIPTION,COMPANYCONTACTS.GENDER," +
                            " COMPANYCONTACTS.CONTACTID,COMPANYCONTACTS.TITLE,COMPANYCONTACTS.SURNAME,COMPANYCONTACTS.NAME,COMPANYCONTACTS.POSITION,COMPANYCONTACTS.PHONE,COMPANYCONTACTS.FAX,COMPANYCONTACTS.MOBILE,COMPANYCONTACTS.EMAIL," +
                            " COMPANYADDRESS.ADDRESSID, COMPANYADDRESS.STREET,COMPANYADDRESS.ZIP,COMPANYADDRESS.LOCATION,COMPANYADDRESS.COUNTRY" +
                            " FROM COMPANY LEFT JOIN COMPANYCONTACTS  ON  " +
                            " COMPANYCONTACTS.COMPANYID=COMPANY.COMPANYID LEFT JOIN COMPANYADDRESS ON COMPANYADDRESS.COMPANYID=COMPANY.COMPANYID ");
            
            //ps1.setString(1,"Leads");
            set = ebiLeadsPanel.ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);

            if (set != null) {
                set.last();
                ebiLeadsPanel.tabModel.data = new Object[set.getRow()][14];

                if (set.getRow() > 0) {
                    set.beforeFirst();
                    int i = 0;
                    while (set.next()) {
                       ebiLeadsPanel.tabModel.data[i][0] = set.getString("COMPANY.NAME") == null ? "" : set.getString("COMPANY.NAME");
                       ebiLeadsPanel.tabModel.data[i][1] = set.getString("COMPANYCONTACTS.GENDER") == null ? "" : set.getString("COMPANYCONTACTS.GENDER");
                       ebiLeadsPanel.tabModel.data[i][2] = set.getString("COMPANYCONTACTS.POSITION") == null ? "" : set.getString("COMPANYCONTACTS.POSITION");
                       ebiLeadsPanel.tabModel.data[i][3] = set.getString("COMPANYCONTACTS.NAME") == null ? "" : set.getString("COMPANYCONTACTS.NAME");
                       ebiLeadsPanel.tabModel.data[i][4] = set.getString("COMPANYCONTACTS.SURNAME") == null ? "" : set.getString("COMPANYCONTACTS.SURNAME");
                       String zipLoc =  set.getString("COMPANYADDRESS.ZIP") == null ? "" : set.getString("COMPANYADDRESS.ZIP")+" ";
                              zipLoc += set.getString("COMPANYADDRESS.LOCATION") == null ? "" : set.getString("COMPANYADDRESS.LOCATION");
                       ebiLeadsPanel.tabModel.data[i][5] = zipLoc;
                       ebiLeadsPanel.tabModel.data[i][6] = set.getString("COMPANYADDRESS.COUNTRY") == null ? "" : set.getString("COMPANYADDRESS.COUNTRY");
                       ebiLeadsPanel.tabModel.data[i][7] = set.getString("COMPANYCONTACTS.PHONE") == null ? "" : set.getString("COMPANYCONTACTS.PHONE");
                       ebiLeadsPanel.tabModel.data[i][8] = set.getString("COMPANYCONTACTS.MOBILE") == null ? "" : set.getString("COMPANYCONTACTS.MOBILE");
                       ebiLeadsPanel.tabModel.data[i][9] = set.getString("COMPANYCONTACTS.EMAIL") == null ? "" : set.getString("COMPANYCONTACTS.EMAIL");
                       ebiLeadsPanel.tabModel.data[i][10] = set.getString("COMPANY.QUALIFICATION") == null ? "" : set.getString("COMPANY.QUALIFICATION");
                       ebiLeadsPanel.tabModel.data[i][11] = set.getInt("COMPANY.COMPANYID") == 0 ? 0 : set.getInt("COMPANY.COMPANYID");
                       ebiLeadsPanel.tabModel.data[i][12] = set.getString("COMPANYCONTACTS.CONTACTID") == null ? "" : set.getString("COMPANYCONTACTS.CONTACTID");
                       ebiLeadsPanel.tabModel.data[i][13] = set.getString("COMPANYADDRESS.ADDRESSID") == null ? "" : set.getString("COMPANYADDRESS.ADDRESSID");
                       i++;
                    }
                }else{
                    ebiLeadsPanel.tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","","","","","","","","","",""}};
                }
            }else {
                ebiLeadsPanel.tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","","","","","","","","","",""}};
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ebiLeadsPanel.tabModel.fireTableDataChanged();
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        ebiLeadsPanel.ebiModule.guiRenderer.getTable("leadsTable","Leads").changeSelection(srow,0,false,false);
    }

    public void dataShow(String searchText) {
        ResultSet set = null;
        this.searchText = searchText;
        int srow = ebiLeadsPanel.ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow();
        try {
            ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCursor(new Cursor(Cursor.WAIT_CURSOR));
            PreparedStatement ps1 = ebiLeadsPanel.ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("" +
                            " SELECT COMPANY.COMPANYID,COMPANY.NAME,COMPANY.COOPERATION,COMPANY.CATEGORY,COMPANY.WEB,COMPANY.QUALIFICATION,COMPANY.DESCRIPTION,COMPANYCONTACTS.GENDER," +
                            " COMPANYCONTACTS.CREATEDDATE, COMPANYCONTACTS.CONTACTID,COMPANYCONTACTS.TITLE,COMPANYCONTACTS.SURNAME,COMPANYCONTACTS.NAME,COMPANYCONTACTS.POSITION,COMPANYCONTACTS.PHONE,COMPANYCONTACTS.FAX,COMPANYCONTACTS.MOBILE,COMPANYCONTACTS.EMAIL," +
                            " COMPANYADDRESS.ADDRESSID, COMPANYADDRESS.STREET,COMPANYADDRESS.ZIP,COMPANYADDRESS.LOCATION,COMPANYADDRESS.COUNTRY" +
                            " FROM COMPANY LEFT JOIN COMPANYCONTACTS ON  " +
                            " COMPANYCONTACTS.COMPANYID=COMPANY.COMPANYID LEFT JOIN COMPANYADDRESS ON COMPANYADDRESS.COMPANYID=COMPANY.COMPANYID " +
                            " WHERE COMPANY.NAME LIKE ? OR COMPANY.CATEGORY LIKE ? OR COMPANY.COOPERATION LIKE ? OR COMPANY.QUALIFICATION LIKE ? OR COMPANY.DESCRIPTION LIKE ? OR COMPANY.WEB LIKE ? OR COMPANYCONTACTS.GENDER LIKE ? " +
                            " OR COMPANYCONTACTS.TITLE LIKE ? OR COMPANYCONTACTS.SURNAME LIKE ? OR COMPANYCONTACTS.NAME LIKE ? OR COMPANYCONTACTS.POSITION LIKE ? OR " +
                            " COMPANYCONTACTS.FAX LIKE ? OR COMPANYCONTACTS.MOBILE LIKE ? OR COMPANYCONTACTS.EMAIL LIKE ? OR" +
                            " COMPANYADDRESS.STREET LIKE ? OR COMPANYADDRESS.ZIP LIKE ? OR COMPANYADDRESS.LOCATION LIKE ? OR COMPANYADDRESS.COUNTRY LIKE ? ORDER BY COMPANYCONTACTS.CREATEDDATE DESC ");

            searchText = searchText+"%";
            ps1.setString(1,searchText);
            ps1.setString(2,searchText);
            ps1.setString(3,searchText);
            ps1.setString(4,searchText);
            ps1.setString(5,searchText);
            ps1.setString(6,searchText);
            ps1.setString(7,searchText);
            ps1.setString(8,searchText);
            ps1.setString(9,searchText);
            ps1.setString(10,searchText);
            ps1.setString(11,searchText);
            ps1.setString(12,searchText);
            ps1.setString(13,searchText);
            ps1.setString(14,searchText);
            ps1.setString(15,searchText);
            ps1.setString(16,searchText);
            ps1.setString(17,searchText);
            ps1.setString(18,searchText);
            set = ebiLeadsPanel.ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);

            if (set != null) {
                set.last();
                ebiLeadsPanel.tabModel.data = new Object[set.getRow()][14];

                if (set.getRow() > 0) {
                    set.beforeFirst();
                    int i = 0;
                    while (set.next()) {
                       ebiLeadsPanel.tabModel.data[i][0] = set.getString("COMPANY.NAME") == null ? "" : set.getString("COMPANY.NAME");
                       ebiLeadsPanel.tabModel.data[i][1] = set.getString("COMPANYCONTACTS.GENDER") == null ? "" : set.getString("COMPANYCONTACTS.GENDER");
                       ebiLeadsPanel.tabModel.data[i][2] = set.getString("COMPANYCONTACTS.POSITION") == null ? "" : set.getString("COMPANYCONTACTS.POSITION");
                       ebiLeadsPanel.tabModel.data[i][3] = set.getString("COMPANYCONTACTS.NAME") == null ? "" : set.getString("COMPANYCONTACTS.NAME");
                       ebiLeadsPanel.tabModel.data[i][4] = set.getString("COMPANYCONTACTS.SURNAME") == null ? "" : set.getString("COMPANYCONTACTS.SURNAME");
                       String zipLoc =  set.getString("COMPANYADDRESS.ZIP") == null ? "" : set.getString("COMPANYADDRESS.ZIP")+" ";
                              zipLoc += set.getString("COMPANYADDRESS.LOCATION") == null ? "" : set.getString("COMPANYADDRESS.LOCATION");
                       ebiLeadsPanel.tabModel.data[i][5] = zipLoc;
                       ebiLeadsPanel.tabModel.data[i][6] = set.getString("COMPANYADDRESS.COUNTRY") == null ? "" : set.getString("COMPANYADDRESS.COUNTRY");
                       ebiLeadsPanel.tabModel.data[i][7] = set.getString("COMPANYCONTACTS.PHONE") == null ? "" : set.getString("COMPANYCONTACTS.PHONE");
                       ebiLeadsPanel.tabModel.data[i][8] = set.getString("COMPANYCONTACTS.MOBILE") == null ? "" : set.getString("COMPANYCONTACTS.MOBILE");
                       ebiLeadsPanel.tabModel.data[i][9] = set.getString("COMPANYCONTACTS.EMAIL") == null ? "" : set.getString("COMPANYCONTACTS.EMAIL");
                       ebiLeadsPanel.tabModel.data[i][10] = set.getString("COMPANY.QUALIFICATION") == null ? "" : set.getString("COMPANY.QUALIFICATION");
                       ebiLeadsPanel.tabModel.data[i][11] = set.getString("COMPANY.COMPANYID") == null ? "" : set.getString("COMPANY.COMPANYID");
                       ebiLeadsPanel.tabModel.data[i][12] = set.getString("COMPANYCONTACTS.CONTACTID") == null ? "" : set.getString("COMPANYCONTACTS.CONTACTID");
                       ebiLeadsPanel.tabModel.data[i][13] = set.getString("COMPANYADDRESS.ADDRESSID") == null ? "" : set.getString("COMPANYADDRESS.ADDRESSID");
                       i++;
                    }
                }else{
                    ebiLeadsPanel.tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","","","","","","","","","",""}};
                }
            }else {
                ebiLeadsPanel.tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","","","","","","","","","",""}};
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            ebiLeadsPanel.tabModel.fireTableDataChanged();
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
       ebiLeadsPanel.ebiModule.guiRenderer.getTable("leadsTable","Leads").changeSelection(srow,0,false,false);
    }

    public void dataNew() {
      company = new Company();
      contact = new Companycontacts();
      address = new Companyaddress();
      isEdit = false;
      ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setID(-1);
      ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedDate(ebiLeadsPanel.ebiModule.ebiPGFactory.getDateToString(new Date()));
      ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedFrom(EBIPGFactory.ebiUser);
      ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setChangedDate("");
      ebiLeadsPanel.ebiModule.guiRenderer.getVisualPanel("Leads").setChangedFrom("");

      ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("genderText","Leads").getEditor().setItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("titleText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("positionText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactNameText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactSurnameText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("telephoneText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("faxText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("emailText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("contactMobileText","Leads").setText("");

      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressStrNrText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressZipText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCityText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("addressCountryText","Leads").setText("");

      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("compNameText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getTextfield("internetText","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getComboBox("classificationText","Leads").setSelectedIndex(0);
      ebiLeadsPanel.ebiModule.guiRenderer.getTextarea("descriptionText","Leads").setText("");

      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("cName","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("addressLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("zipLocationLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("phoneLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("faxLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("mobileLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("emailLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("compNameLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("webLabel","Leads").setText("");
      ebiLeadsPanel.ebiModule.guiRenderer.getLabel("positionLabel","Leads").setText("");

      ebiLeadsPanel.ebiModule.ebiPGFactory.getDataStore("Leads","ebiNew",true);
      dataShow();
    }

}
