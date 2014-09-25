package ebiCRM.data.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMAddressPane;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companyaddress;

public class EBIDataControlAddress {

    private Companyaddress address = null;
    private final EBICRMAddressPane addressPane;

    public EBIDataControlAddress(EBICRMAddressPane addressModule) {
        this.addressPane = addressModule;
    }

    public boolean dataStore(boolean isEdit) {
        try {
            addressPane.ebiModule.ebiContainer.showInActionStatus("Address", true);
            addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            if (isEdit == false) {
                address = new Companyaddress();
                address.setCreateddate(new Date());
                address.setCreatedfrom(EBIPGFactory.ebiUser);
                addressPane.isEdit = true;
            } else {
                createHistory(addressPane.ebiModule.ebiPGFactory.company);
                address.setChangeddate(new Date());
                address.setChangedfrom(EBIPGFactory.ebiUser);
            }

            address.setCompany(addressPane.ebiModule.ebiPGFactory.company);
            if(this.addressPane.guiRenderer.getComboBox("addressTypeText","Address").getSelectedItem() != null){
                address.setAddresstype(this.addressPane.guiRenderer.getComboBox("addressTypeText","Address").getSelectedItem().toString());
            }

            address.setStreet(this.addressPane.guiRenderer.getTextfield("streetText","Address").getText());
            address.setZip(this.addressPane.guiRenderer.getTextfield("zipText","Address").getText());
            address.setLocation(this.addressPane.guiRenderer.getTextfield("LocationText","Address").getText());
            address.setPbox(this.addressPane.guiRenderer.getTextfield("postcodeText","Address").getText());
            address.setCountry( this.addressPane.guiRenderer.getTextfield("countryText","Address").getText());

            addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(address);

            addressPane.ebiModule.ebiPGFactory.getDataStore("Address","ebiSave",true);
            addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            dataShow();
            addressPane.ebiModule.ebiContainer.showInActionStatus("Address",false);
            
            if(!isEdit){
            	addressPane.ebiModule.guiRenderer.getVisualPanel("Address").setID(address.getAddressid());
            }

            if(addressPane.ebiModule.guiRenderer.existPackage("Company")){
               if(address.getCompany()!= null){
                    if(address.getCompany().getIsactual()){
                        addressPane.ebiModule.ebiPGFactory.loadStandardCompany();
                    }
               }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }
    
    public void dataCopy(int id){
    	
    	
    	try{
    		Query y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where addressid=? ").setInteger(0, id);
        
	        if(y.list().size() > 0){
	        
	        	Iterator iter = y.iterate();
	          	Companyaddress adr = (Companyaddress) iter.next();
	          	
	          	addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

	          	Companyaddress adrsn = new Companyaddress();
                addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(adr);
	          	adrsn.setCreateddate(new Date());
	            adrsn.setCreatedfrom(EBIPGFactory.ebiUser);
	
	            adrsn.setCompany(adr.getCompany());
	            adrsn.setAddresstype(adr.getAddresstype()+" (Copy)");
	            adrsn.setStreet(adr.getStreet());
	            adrsn.setZip(adr.getZip());
	            adrsn.setLocation(adr.getLocation());
	            adrsn.setPbox(adr.getPbox());
	            adrsn.setCountry(adr.getCountry());
	
	            addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(adrsn);
                addressPane.ebiModule.ebiPGFactory.getDataStore("Address","ebiSave",true);

	            addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	            dataShow();
	            
	            addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").
				changeSelection(addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").
						convertRowIndexToView(addressPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(addressPane.tabModel.data,6, adrsn.getAddressid())),0,false,false);
	            
	            addressPane.ebiModule.ebiContainer.showInActionStatus("Address",false);
	          	   	
	        }
        
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}

    }

    public void dataMove(int id){
        try{
            Query y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where addressid=? ").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(addressPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companyaddress adr = (Companyaddress) iter.next();
                    addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(adr);
                    addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    addressPane.ebiModule.ebiContainer.showInActionStatus("Address", true);

                    adr.setCompany((Company)x.list().get(0));
                    addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(adr);

                    addressPane.ebiModule.ebiPGFactory.getDataStore("Address","ebiSave",true);
                    addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    addressPane.ebiModule.ebiContainer.showInActionStatus("Address", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void dataEdit(int id) {
        dataNew();

        Query y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where addressid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
        
          Iterator iter = y.iterate();

            	address = (Companyaddress) iter.next();
            	addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(address);
            	addressPane.ebiModule.guiRenderer.getVisualPanel("Address").setID(address.getAddressid());

                if(address.getAddresstype() != null){
                    addressPane.guiRenderer.getComboBox("addressTypeText","Address").setSelectedItem(address.getAddresstype());
                }

                addressPane.guiRenderer.getTextfield("streetText","Address").setText(address.getStreet());
                addressPane.guiRenderer.getTextfield("zipText","Address").setText(address.getZip());
                addressPane.guiRenderer.getTextfield("LocationText","Address").setText(address.getLocation());
                addressPane.guiRenderer.getTextfield("postcodeText","Address").setText(address.getPbox());
                addressPane.guiRenderer.getTextfield("countryText","Address").setText(address.getCountry());

                addressPane.guiRenderer.getVisualPanel("Address").setCreatedDate(addressPane.ebiModule.ebiPGFactory.getDateToString(address.getCreateddate() == null ? new Date() : address.getCreateddate()));
                addressPane.guiRenderer.getVisualPanel("Address").setCreatedFrom(address.getCreatedfrom() == null ? EBIPGFactory.ebiUser : address.getCreatedfrom() );
                
                if(address.getChangeddate() != null){
                    addressPane.guiRenderer.getVisualPanel("Address").setChangedDate(addressPane.ebiModule.ebiPGFactory.getDateToString(address.getChangeddate()));
                    addressPane.guiRenderer.getVisualPanel("Address").setChangedFrom(EBIPGFactory.ebiUser);
                }

                addressPane.ebiModule.ebiPGFactory.getDataStore("Address", "ebiEdit",true);
                
                addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").
					changeSelection(addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").
							convertRowIndexToView(addressPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(addressPane.tabModel.data, 6, id)),0,false,false);
                
        }else{
        	 EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        }
    }

    public void dataDelete(int id) {
       try{
        Query y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where addressid=? ").setInteger(0, id);
        
        if(y.list().size() > 0){
	        Iterator iter = y.iterate();
		
    		address = (Companyaddress) iter.next();

            try {
                addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(address);
                addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
            } catch (HibernateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            addressPane.ebiModule.ebiPGFactory.getDataStore("Address","ebiDelete",true);
            this.dataShow();
	
	        dataNew();
        }
       }catch(Exception ex){}
    }

    public void dataShow() {
    	try{
               int srow =0;

               if(addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address") != null){
                    srow = addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").getSelectedRow();
               }

               Query y= null;
               if(addressPane.ebiModule.companyID != -1){
                    y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where company.companyid=? order by createddate desc ");
                    y.setInteger(0, addressPane.ebiModule.companyID);
               }else{
                    y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companyaddress where company.companyid IS NULL order by createddate desc ");
               }
               if(y.list().size() > 0){

                    addressPane.tabModel.data = new Object[y.list().size()][7];

                    Iterator itr = y.iterate();

                    int i = 0;
                    while (itr.hasNext()) {

                        Companyaddress obj = (Companyaddress) itr.next();

                        addressPane.tabModel.data[i][0] = obj.getAddresstype() == null ? "" : obj.getAddresstype();
                        addressPane.tabModel.data[i][1] = obj.getStreet() == null ? "" : obj.getStreet();
                        addressPane.tabModel.data[i][2] = obj.getZip() == null ? "" : obj.getZip();
                        addressPane.tabModel.data[i][3] = obj.getLocation() == null ? "" : obj.getLocation();
                        addressPane.tabModel.data[i][4] = obj.getPbox() == null ? "" : obj.getPbox();
                        addressPane.tabModel.data[i][5] = obj.getCountry() == null ? "" : obj.getCountry();
                        addressPane.tabModel.data[i][6] = obj.getAddressid();
                        i++;
                    }
            }else{
               addressPane.tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
            }

            addressPane.tabModel.fireTableDataChanged();

            addressPane.ebiModule.getCompanyPane().getDataTableCompany().data = addressPane.tabModel.data;
            addressPane.ebiModule.getCompanyPane().getDataTableCompany().fireTableDataChanged();

            if(addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address") != null){
                addressPane.ebiModule.guiRenderer.getTable("companyAddess","Address").changeSelection(srow,0,false,false);
            }
        }catch(Exception ex){}

    }

    public void dataNew() {
        try{
        addressPane.initialize();
        addressPane.ebiModule.ebiPGFactory.getDataStore("Address","ebiNew",true);
        addressPane.ebiModule.guiRenderer.getVisualPanel("Address").setID(-1);
        dataShow();
        }catch(Exception ex){}

    }

    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();
        if(address.getCreateddate() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + addressPane.ebiModule.ebiPGFactory.getDateToString(address.getCreateddate()));
        }
        if(address.getCreatedfrom() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + address.getCreatedfrom());
        }

        if (address.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + addressPane.ebiModule.ebiPGFactory.getDateToString(address.getChangeddate()));
        }

        if(address.getChangedfrom() != null){
           list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + address.getChangedfrom());
        }

        if(address.getAddresstype() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_ADRESS_TYPE") + ": " + (address.getAddresstype().equals(addressPane.guiRenderer.getComboBox("addressTypeText","Address").getSelectedItem().toString()) == true ? address.getAddresstype() : address.getAddresstype()+"$") );
        }

        if(address.getStreet() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR") + ": " + (address.getStreet().equals(addressPane.guiRenderer.getTextfield("streetText","Address").getText()) == true ? address.getStreet() : address.getStreet()+"$") );
        }

        if(address.getZip() != null){
            String zLch = "";
            if(!address.getZip().equals(addressPane.guiRenderer.getTextfield("zipText","Address").getText()) || !address.getLocation().equals(addressPane.guiRenderer.getTextfield("LocationText","Address").getText())){
                zLch = "$";
            }
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_ZIP_LOCATION") + ": " + address.getZip() + " " + address.getLocation()+zLch);
        }

        if(address.getPbox() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE") + ": " + (address.getPbox().equals(addressPane.guiRenderer.getTextfield("postcodeText","Address").getText()) == true ? address.getPbox() : address.getPbox()+"$") );
        }

        if(address.getCountry() != null){
            list.add(EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + (address.getCountry().equals(addressPane.guiRenderer.getTextfield("countryText","Address").getText()) == true ? address.getCountry() : address.getCountry()+"$") );
        }
        
        list.add("*EOR*"); // END OF RECORD

        try {
            addressPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid() , "Address", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Companyaddress> getAddressList() {
        Query y = null;
         try{


            if(addressPane.ebiModule.companyID != -1){
                y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                        createQuery("from Companyaddress where company.companyid=? ").setInteger(0, addressPane.ebiModule.companyID);
            }else{
                y = addressPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").
                        createQuery("from Companyaddress where company is null ");
            }
         }catch (Exception ex){}
        return y.list();
    }

    public Companyaddress getAddress() {
        return address;
    }

    public void setAddress(Companyaddress address) {
        this.address = address;
    }
}