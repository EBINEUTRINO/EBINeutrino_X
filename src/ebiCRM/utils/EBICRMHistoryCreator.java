package ebiCRM.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.model.hibernate.Ebicrmhistory;

public class EBICRMHistoryCreator {

    private EBICRMHistoryDataUtil field = new EBICRMHistoryDataUtil();
    private EBICRMModule ebiModule = null;
    private StringBuffer stringTosave = new StringBuffer();

    /**
     * copy constructor
     * @param ebiPGFunction
     */
    public EBICRMHistoryCreator(EBICRMModule ebiModule) {
        this.ebiModule = ebiModule;
    }

    /**
     * Set an ArrayList of history values 
     * @param list
     * @throws Exception
     */
    public void setDataToCreate(EBICRMHistoryDataUtil list) throws Exception {
        field = new EBICRMHistoryDataUtil();
        stringTosave = new StringBuffer();
        field = list;
        if (parseList()) {
            saveData();
        } else {
            throw new Exception("Given a bad class list!");
        }
    }

    /**
     * save history value
     */
    protected void saveData() {
        try {
            Ebicrmhistory crmHistory = new Ebicrmhistory();
            crmHistory.setCompanyid(field.getCompanyId());
            crmHistory.setCategory(field.getCategory());
            splitStringValue();
            crmHistory.setChangedvalue(stringTosave.toString());
            crmHistory.setChangeddate(new java.util.Date());
            crmHistory.setChangedfrom(EBIPGFactory.ebiUser);
            ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").saveOrUpdate(crmHistory);

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse the history values
     * @return true successfully parsed otherwise false
     * @throws Exception
     */
    protected boolean parseList() throws Exception {

        if (field.getCompanyId() == 0) {
            return false;
        }
        if ("".equals(field.getCategory()) || field.getCategory() == null) {
            return false;
        }

        Iterator iter = field.getField().iterator();
        int count = 0;
        while (iter.hasNext()) {
            String str = (String) iter.next();
            if ("".equals(str) || str == null) {
                count++;
            } else {
                break;
            }
        }

        if (count == field.getField().size()) {
            throw new Exception("Object list is empty!");
        }

        return true;
    }

    /**
     * Split the EBICRMHistoryDataUtil into a single string like (Name|Surname|etc.)
     * 
     */
    protected void splitStringValue() {

        Iterator iter = field.getField().iterator();

        int i = 0;

        while (iter.hasNext()) {
            String val = (String) iter.next();

            stringTosave.append(val);
            if (i != field.getField().size()) {
                stringTosave.append("|");
            }

        }

    }

    protected List<String> getListFromString(String str) {
        List<String> l = new ArrayList<String>();

        char[] array = str.toCharArray();
        String toAdd = "";

        for (int i = 0; i < array.length; i++) {

            if (array[i] == '|') {
                l.add(toAdd);
                toAdd = "";
            } else {
                toAdd += String.valueOf(array[i]);
            }

        }

        return l;
    }

    /**
     * Retrieve an history record from the database with the companyID and Category
     * (Category could be "Contact", "Address", "Order", "Offer" etc..) 
     * @param companyID
     * @param category
     * @return
     */
    public List<EBICRMHistoryDataUtil> retrieveDBHistory(int companyID, String category) {
        List<EBICRMHistoryDataUtil> toReturn = new ArrayList<EBICRMHistoryDataUtil>();
        try {
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
            Query query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("from Ebicrmhistory where companyid=? and category=? ").setInteger(0, companyID).setString(1, category);

            Iterator it = query.iterate();
            while (it.hasNext()) {
                Ebicrmhistory crmHistory = (Ebicrmhistory) it.next();
                ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(crmHistory);
                EBICRMHistoryDataUtil util = new EBICRMHistoryDataUtil();
                util.setCompanyId(crmHistory.getCompanyid());
                util.setCategory(crmHistory.getCategory());
                util.setChangedDate(ebiModule.ebiPGFactory.getDateToString(crmHistory.getChangeddate()));
                util.setChangedFrom(crmHistory.getChangedfrom());
                util.setField(getListFromString(crmHistory.getChangedvalue()));
                toReturn.add(util);
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return toReturn;
    }
}
