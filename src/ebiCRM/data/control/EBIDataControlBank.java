package ebiCRM.data.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ebiCRM.gui.dialogs.EBIMoveRecord;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMBankPane;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Company;
import ebiNeutrinoSDK.model.hibernate.Companybank;

public class EBIDataControlBank {

    private Companybank companyBank = null;
    private EBICRMBankPane bankPane = null;

    public EBIDataControlBank(EBICRMBankPane bankPane) {
        this.bankPane = bankPane;
        companyBank = new Companybank();
    }

    public boolean dataStore(boolean isEdit) {  
        try {
             bankPane.ebiModule.ebiContainer.showInActionStatus("Bank",true);
            bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();

            if (isEdit == false) {
                companyBank = new Companybank();
                companyBank.setCreateddate(new Date());
                companyBank.setCreatedfrom(EBIPGFactory.ebiUser);
                bankPane.isEdit = true;
            } else {
                createHistory(bankPane.ebiModule.ebiPGFactory.company);
                companyBank.setChangeddate(new Date());
                companyBank.setChangedfrom(EBIPGFactory.ebiUser);
            }

            companyBank.setCompany(bankPane.ebiModule.ebiPGFactory.company);
            companyBank.setBankname(bankPane.ebiModule.guiRenderer.getTextfield("bankNameText","Bank").getText());
            companyBank.setBankbsb(bankPane.ebiModule.guiRenderer.getTextfield("abaNrText","Bank").getText());
            companyBank.setBankaccount(bankPane.ebiModule.guiRenderer.getTextfield("accountNrText","Bank").getText());
            companyBank.setBankbic(bankPane.ebiModule.guiRenderer.getTextfield("bicText","Bank").getText());
            companyBank.setBankiban(bankPane.ebiModule.guiRenderer.getTextfield("ibanText","Bank").getText());
            companyBank.setBankcountry(bankPane.ebiModule.guiRenderer.getTextfield("countryBankText","Bank").getText());

            bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(companyBank);

            bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiSave",true);
            bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();

            dataShow();
            bankPane.ebiModule.ebiContainer.showInActionStatus("Bank", false);
            
            if(!isEdit){
            	bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setID(companyBank.getBankid());
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    
    
    public void dataCopy(int id){
    	try {
    	
	    	 Query y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where bankid=?").setInteger(0, id);
	         
	         if(y.list().size() > 0){
	         	 Iterator iter = y.iterate();
	         	 bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
	             		Companybank bank = (Companybank) iter.next();
	                    bankPane.ebiModule.ebiContainer.showInActionStatus("Bank",true);
                        bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(bank);
	                    Companybank compbank = new Companybank();
	                    compbank.setCreateddate(new Date());
	                    compbank.setCreatedfrom(EBIPGFactory.ebiUser);
	
	                    compbank.setCompany(bank.getCompany());
	                    compbank.setBankname(bank.getBankname()+" - (Copy)");
	                    compbank.setBankbsb(bank.getBankbsb());
	                    compbank.setBankaccount(bank.getBankaccount());
	                    compbank.setBankbic(bank.getBankbic());
	                    compbank.setBankiban(bank.getBankiban());
	                    compbank.setBankcountry(bank.getBankcountry());
	
	                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(compbank);

                        bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiSave",true);
	                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
	
	                    bankPane.ebiModule.ebiContainer.showInActionStatus("Bank", false);
	                    
	                    dataShow();
	                    
	                    bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").
	    				changeSelection(bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").
	    						convertRowIndexToView(bankPane.ebiModule.dynMethod.
	    								getIdIndexFormArrayInATable(bankPane.tabModel.data,6, compbank.getBankid())),0,false,false);
	                    
	         	}
    	
    	} catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    }


    public void dataMove(int id){
        try{
            Query y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where bankid=?").setInteger(0,id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                EBIMoveRecord move = new EBIMoveRecord(bankPane.ebiModule);
                move.setVisible();
                if(move.getSelectedID() > -1){
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    Query x = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Company c where c.companyid=? ").setLong(0,move.getSelectedID());

                    Companybank bank = (Companybank) iter.next();
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(bank);
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(x.list().get(0));
                    bankPane.ebiModule.ebiContainer.showInActionStatus("Bank", true);

                    bank.setCompany((Company)x.list().get(0));
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(bank);

                    bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiSave",true);
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                    bankPane.ebiModule.ebiContainer.showInActionStatus("Bank", false);
                    dataShow();
                }
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    public void dataEdit(int id) {
       try{
            dataNew();

            Query y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where bankid=?").setInteger(0, id);

            if(y.list().size() > 0){
                Iterator iter = y.iterate();

                    companyBank = (Companybank) iter.next();
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(companyBank);
                    bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setID(companyBank.getBankid());
                    bankPane.ebiModule.guiRenderer.getTextfield("bankNameText","Bank").setText(companyBank.getBankname());
                    bankPane.ebiModule.guiRenderer.getTextfield("abaNrText","Bank").setText(companyBank.getBankbsb());
                    bankPane.ebiModule.guiRenderer.getTextfield("accountNrText","Bank").setText(companyBank.getBankaccount());
                    bankPane.ebiModule.guiRenderer.getTextfield("bicText","Bank").setText(companyBank.getBankbic());
                    bankPane.ebiModule.guiRenderer.getTextfield("ibanText","Bank").setText(companyBank.getBankiban());
                    bankPane.ebiModule.guiRenderer.getTextfield("countryBankText","Bank").setText(companyBank.getBankcountry());

                    bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedDate(bankPane.ebiModule.ebiPGFactory.getDateToString(companyBank.getCreateddate() == null ? new Date() : companyBank.getCreateddate() ));
                    bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedFrom(companyBank.getCreatedfrom() == null ? EBIPGFactory.ebiUser : companyBank.getCreatedfrom() );

                    if (companyBank.getChangeddate() != null) {
                        bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedDate(bankPane.ebiModule.ebiPGFactory.getDateToString(companyBank.getChangeddate()));
                        bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedFrom(EBIPGFactory.ebiUser);
                    } else {
                        bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedDate("");
                        bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedFrom("");
                    }

                    bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiEdit",true);

                    bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").
                    changeSelection(bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").
                            convertRowIndexToView(bankPane.ebiModule.dynMethod.
                                    getIdIndexFormArrayInATable(bankPane.tabModel.data, 6, id)),0,false,false);

            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
       }catch (Exception ex){}
    }

    public void dataDelete(int id) {
       try{
        Query y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where bankid=?").setInteger(0, id);
        
        if(y.list().size() > 0){
        	
        	Iterator iter = y.iterate();
        	 
            companyBank = (Companybank) iter.next();

                try {
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(companyBank);

                    bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiDelete",true);
                    bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(this.bankPane.ebiModule.ebiPGFactory.company != null){
                    this.bankPane.ebiModule.ebiPGFactory.company.getCompanybanks().remove(companyBank);
                }

                this.dataShow();
        }
        dataNew();
       }catch (Exception ex){}
    }

    public void dataShow() {
      try{
           int srow = bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectedRow();

           Query y = null;
           if(bankPane.ebiModule.companyID != -1){
                y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where company.companyid=? order by createddate desc  ");
                y.setInteger(0, bankPane.ebiModule.companyID);
           }else{
               y = bankPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Companybank where company.companyid IS NULL order by createddate desc  ");
           }

           if(y.list().size() > 0){

                bankPane.tabModel.data = new Object[y.list().size()][7];

                Iterator iter = y.iterate();
                int i = 0;
                while (iter.hasNext()) {

                    Companybank obj = (Companybank) iter.next();
                    bankPane.tabModel.data[i][0] = obj.getBankname() == null ? "" : obj.getBankname();
                    bankPane.tabModel.data[i][1] = obj.getBankbsb() == null ? "" : obj.getBankbsb();
                    bankPane.tabModel.data[i][2] = obj.getBankaccount() == null ? "" : obj.getBankaccount();
                    bankPane.tabModel.data[i][3] = obj.getBankbic() == null ? "" : obj.getBankbic();
                    bankPane.tabModel.data[i][4] = obj.getBankiban() == null ? "" : obj.getBankiban();
                    bankPane.tabModel.data[i][5] = obj.getBankcountry() == null ? "" : obj.getBankcountry();
                    bankPane.tabModel.data[i][6] = obj.getBankid();
                    i++;
                }

           }else{
             bankPane.tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
           }

           bankPane.tabModel.fireTableDataChanged();
           bankPane.ebiModule.guiRenderer.getTable("companyBankTable","Bank").changeSelection(srow,0,false,false);
      }catch (Exception ex){}
    }

    public void dataNew() {
        try{
            bankPane.ebiModule.guiRenderer.getTextfield("bankNameText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getTextfield("accountNrText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getTextfield("abaNrText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getTextfield("bicText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getTextfield("ibanText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getTextfield("countryBankText","Bank").setText("");
            bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedDate(bankPane.ebiModule.ebiPGFactory.getDateToString(new Date()));
            bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedFrom(EBIPGFactory.ebiUser);
            bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedDate("");
            bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setChangedFrom("");

            bankPane.ebiModule.ebiPGFactory.getDataStore("Bank","ebiNew",true);
            bankPane.ebiModule.guiRenderer.getVisualPanel("Bank").setID(-1);
            dataShow();
        }catch (Exception ex){}
    }

    private void createHistory(Company com) throws Exception{

        List<String> list = new ArrayList<String>();
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + bankPane.ebiModule.ebiPGFactory.getDateToString(companyBank.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + EBIPGFactory.ebiUser);

        if (companyBank.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + bankPane.ebiModule.ebiPGFactory.getDateToString(companyBank.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + EBIPGFactory.ebiUser);
        }
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_BANK_NAME") + ": " + (companyBank.getBankname().equals(bankPane.ebiModule.guiRenderer.getTextfield("bankNameText","Bank").getText()) == true  ? companyBank.getBankname() : companyBank.getBankname() +"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_BANK_CODE") + ": " + (companyBank.getBankbsb().equals(bankPane.ebiModule.guiRenderer.getTextfield("abaNrText","Bank").getText()) == true ? companyBank.getBankbsb() : companyBank.getBankbsb()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_KONTO_NR") + ": " + (companyBank.getBankaccount().equals(bankPane.ebiModule.guiRenderer.getTextfield("accountNrText","Bank").getText()) == true ? companyBank.getBankaccount() : companyBank.getBankaccount()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_BIC") + ": " + (companyBank.getBankbic().equals(bankPane.ebiModule.guiRenderer.getTextfield("bicText","Bank").getText()) == true ? companyBank.getBankbic() : companyBank.getBankbic()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_IBAN") + ": " + (companyBank.getBankiban().equals(bankPane.ebiModule.guiRenderer.getTextfield("ibanText","Bank").getText()) == true ? companyBank.getBankiban() : companyBank.getBankiban()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY") + ": " + (companyBank.getBankcountry().equals(bankPane.ebiModule.guiRenderer.getTextfield("countryBankText","Bank").getText()) == true ? companyBank.getBankcountry() : companyBank.getBankcountry()+"$") );

        list.add("*EOR*"); // END OF RECORD

        try {
            bankPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(com == null ? -1 : com.getCompanyid(), "Bankdata", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Companybank getCompanyBank() {
        return companyBank;
    }

    public void setCompanyBank(Companybank companyBank) {
        this.companyBank = companyBank;
    }

    public Set<Companybank> getBankList() {
        return this.bankPane.ebiModule.ebiPGFactory.company.getCompanybanks();
    }
}