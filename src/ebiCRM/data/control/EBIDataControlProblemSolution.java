package ebiCRM.data.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMProblemSolution;
import ebiCRM.utils.EBICRMHistoryCreator;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsoldocs;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolposition;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolutions;

import javax.swing.*;

public class EBIDataControlProblemSolution {

     public Crmproblemsolutions compProsol = null;
     private EBICRMProblemSolution prosolPane = null;
     public int id = -1;
     public Timestamp lockTime = null;
     public String lockUser ="";
     public String lockModuleName = "";
     public int lockStatus=0;
     public int lockId =-1;

    public EBIDataControlProblemSolution(EBICRMProblemSolution prosolPane) {
        this.prosolPane = prosolPane;
        compProsol = new Crmproblemsolutions();
    }

    public boolean dataStore(boolean isEdit) {

        try {
            prosolPane.ebiModule.ebiContainer.showInActionStatus("Prosol", true);
            if(id != -1){
                if(checkIslocked(id,true)){
                 return false;
                }
            }

            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
            if (isEdit == false) {
                compProsol.setCreateddate(new Date());
                prosolPane.isEdit = true;
            } else {
                createHistory();
                compProsol.setChangeddate(new Date());
                compProsol.setChangedfrom(EBIPGFactory.ebiUser);
            }

            compProsol.setCreatedfrom(prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").getCreatedFrom());
            compProsol.setDescription(prosolPane.ebiModule.guiRenderer.getTextarea("prosolDescriptionText","Prosol").getText());
            compProsol.setServicenr(prosolPane.ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").getText());
            compProsol.setName(prosolPane.ebiModule.guiRenderer.getTextfield("prosolNameText","Prosol").getText());

            if(prosolPane.ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").getSelectedItem() != null){
                compProsol.setStatus(prosolPane.ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").getSelectedItem().toString());
            }

            if(prosolPane.ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").getSelectedItem() != null){
                compProsol.setCategory(prosolPane.ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").getSelectedItem().toString());
            }

            if(prosolPane.ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").getSelectedItem() != null){
                compProsol.setType(prosolPane.ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").getSelectedItem().toString());
            }

            if(prosolPane.ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").getSelectedItem() != null){
                compProsol.setClassification(prosolPane.ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").getSelectedItem().toString());
            }
            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(compProsol);

            //Save docs
            if (!this.compProsol.getCrmproblemsoldocses().isEmpty()) {
                Iterator iter = this.compProsol.getCrmproblemsoldocses().iterator();
                while(iter.hasNext()){
                    Crmproblemsoldocs docs = (Crmproblemsoldocs)iter.next();
                    docs.setCrmproblemsolutions(compProsol);
                    if(docs.getSolutiondocid() < 0){docs.setSolutiondocid(null);}
                    prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(docs);
                }
            }
            //Save positions
            if (!this.compProsol.getCrmproblemsolpositions().isEmpty()) {
                Iterator iter = this.compProsol.getCrmproblemsolpositions().iterator();
                while(iter.hasNext()){
                    Crmproblemsolposition pos = (Crmproblemsolposition)iter.next();
                    pos.setCrmproblemsolutions(compProsol);
                    if(pos.getProductid() < 0){pos.setProductid(null);}
                    prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(pos);
                }
            }

            prosolPane.ebiModule.ebiPGFactory.getDataStore("Prosol","ebiSave",true);
            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
            
            if(!isEdit){
            	prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setID(compProsol.getProsolid());
            }
            
            this.dataShow();
            dataShowDoc();
            dataShowProduct();

            prosolPane.ebiModule.ebiContainer.showInActionStatus("Prosol", false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    
    public void dataCopy(int id){
    	
    	 Query query;
         try {

             prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();

             query = prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").createQuery(
                     "from Crmproblemsolutions where prosolid=? ").setInteger(0, id);
         
             Iterator iter = query.iterate();
             if (iter.hasNext()) {
            	 Crmproblemsolutions csol = (Crmproblemsolutions) iter.next();
            	 prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").refresh(csol);
            	 
            	 prosolPane.ebiModule.ebiContainer.showInActionStatus("Prosol", true);
            	 prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();

            	 Crmproblemsolutions psoln = new Crmproblemsolutions();
            	 psoln.setCreateddate(new Date());
            	 psoln.setCreatedfrom(EBIPGFactory.ebiUser);
            	 psoln.setDescription(csol.getDescription());
            	 psoln.setServicenr(csol.getServicenr());
            	 psoln.setName(csol.getName()+" - (Copy)");
            	 psoln.setStatus(csol.getStatus());
            	 psoln.setCategory(csol.getCategory());
            	 psoln.setType(csol.getType());
            	 psoln.setClassification(csol.getClassification());

                 //Save docs
                 if (!csol.getCrmproblemsoldocses().isEmpty()) {
                     Iterator itd = csol.getCrmproblemsoldocses().iterator();
                     while(itd.hasNext()){
                         Crmproblemsoldocs docs = (Crmproblemsoldocs)itd.next();
                        
                         Crmproblemsoldocs dc = new Crmproblemsoldocs();
	                     dc.setCrmproblemsolutions(psoln);
	                     dc.setCreateddate(new Date());
	                     dc.setCreatedfrom(EBIPGFactory.ebiUser);
	                     dc.setFiles(docs.getFiles());
	                     dc.setName(docs.getName());
	                       
                         prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(dc);
                     }
                 }
                 //Save positions
                 if (!csol.getCrmproblemsolpositions().isEmpty()) {
                     Iterator itp = csol.getCrmproblemsolpositions().iterator();
                     while(itp.hasNext()){
                         Crmproblemsolposition pos = (Crmproblemsolposition)itp.next();
                         
                         Crmproblemsolposition p = new Crmproblemsolposition();
	                     p.setCrmproblemsolutions(psoln);
	                     p.setCategory(pos.getCategory());
	                     p.setCreateddate(new Date());
	                     p.setCreatedfrom(EBIPGFactory.ebiUser);
	                     p.setDescription(pos.getDescription());
	                     p.setNetamount(pos.getNetamount());
	                     p.setPretax(pos.getPretax());
	                     p.setProductname(pos.getProductname());
	                     p.setProductnr(pos.getProductnr());
	                     p.setTaxtype(pos.getTaxtype());
	                     p.setType(pos.getType());
                         
                         prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(p);
                     }
                 }
              
                 prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").saveOrUpdate(psoln);
                 prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
                 prosolPane.ebiModule.ebiContainer.showInActionStatus("Prosol", false);
                 
                 dataShow();
                 prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").
					changeSelection(prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").
						convertRowIndexToView(prosolPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(prosolPane.tabModProsol.data,7 , psoln.getProsolid())),0,false,false);
                 
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
             
         
    }
    
    public void dataEdit(int id) {
   

        try {
            dataNew(false);

            Query query;

            query = prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").createQuery(
                    "from Crmproblemsolutions where prosolid=? ").setInteger(0, id);
        
            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                this.id = id;
                
                // Set properties for pessimistic dialog
                prosolPane.ebiModule.pessimisticStruct.setLockId(id);
                prosolPane.ebiModule.pessimisticStruct.setModuleName("CRMProsol");

                this.compProsol = (Crmproblemsolutions) iter.next();
                prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").refresh(compProsol);
                prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setID(compProsol.getProsolid());
                
                    
                    prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedDate(prosolPane.ebiModule.ebiPGFactory.getDateToString(compProsol.getCreateddate() == null ? new Date() : compProsol.getCreateddate() ));
                    prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedFrom(compProsol.getCreatedfrom() == null ? EBIPGFactory.ebiUser : compProsol.getCreatedfrom());

                    if (compProsol.getChangeddate() != null) {
                        prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedDate(prosolPane.ebiModule.ebiPGFactory.getDateToString(compProsol.getChangeddate()));
                        prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedFrom(compProsol.getChangedfrom());
                    } else {
                        prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedDate("");
                        prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedFrom("");
                    }

                    prosolPane.ebiModule.guiRenderer.getTextfield("prosolNameText","Prosol").setText(compProsol.getName());
                    prosolPane.ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").setText(compProsol.getServicenr() == null ? "" : compProsol.getServicenr());

                    if(compProsol.getStatus() != null){
                        prosolPane.ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").setSelectedItem(compProsol.getStatus());
                    }

                    if(compProsol.getCategory() != null){
                        prosolPane.ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").setSelectedItem(compProsol.getCategory());
                    }

                    if(compProsol.getType() != null){
                        prosolPane.ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").setSelectedItem(compProsol.getType());
                    }

                    if(compProsol.getClassification() != null){
                        prosolPane.ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").setSelectedItem(compProsol.getClassification());
                    }

                    prosolPane.ebiModule.guiRenderer.getTextarea("prosolDescriptionText","Prosol").setText(compProsol.getDescription());
                    prosolPane.ebiModule.ebiPGFactory.getDataStore("Prosol","ebiEdit",true);

                    this.dataShowDoc();
                    this.dataShowProduct();
                    try {
                        checkIslocked(id,false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").
						changeSelection(prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").
								convertRowIndexToView(prosolPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(prosolPane.tabModProsol.data, 7, id)),0,false,false);

            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDelete(int id) {
        Query query  ;
        try {

            if(checkIslocked(id,true)){
                 return;
            }

            query = prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").createQuery(
                    "from Crmproblemsolutions where prosolid=? ").setInteger(0, id);
        
                Iterator iter = query.iterate();
        
                while (iter.hasNext()) {

                    this.compProsol = (Crmproblemsolutions) iter.next();

                    if (this.compProsol.getProsolid() == id) {
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").delete(this.compProsol);

                        prosolPane.ebiModule.ebiPGFactory.getDataStore("Prosol","ebiDelete",true);
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
                    }
                }
         }catch (Exception e) {
           e.printStackTrace();
         }
        dataNew(true);
        dataShow();
    }

    public void dataShow() {
        ResultSet set = null;



        try{
            int srow = prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").getSelectedRow();
            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
            PreparedStatement ps1 = prosolPane.ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT PROSOLID,SERVICENR,NAME,CLASSIFICATION,CATEGORY,TYPE,STATUS,DESCRIPTION FROM CRMPROBLEMSOLUTIONS ORDER BY CREATEDDATE DESC");
            set = prosolPane.ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps1);

            if (set != null) {
                set.last();
                prosolPane.tabModProsol.data = new Object[set.getRow()][8];

                if (set.getRow() > 0) {
                    set.beforeFirst();
                    int i = 0;
                    while (set.next()) {

                        prosolPane.tabModProsol.data[i][0] = set.getString("SERVICENR") == null ? "" : set.getString("SERVICENR");
                        prosolPane.tabModProsol.data[i][1] = set.getString("NAME") == null ? "" : set.getString("NAME");
                        prosolPane.tabModProsol.data[i][2] = set.getString("CLASSIFICATION") == null ? "" : set.getString("CLASSIFICATION");
                        prosolPane.tabModProsol.data[i][3] = set.getString("CATEGORY") == null ? "" : set.getString("CATEGORY");
                        prosolPane.tabModProsol.data[i][4] = set.getString("TYPE") == null ? "" : set.getString("TYPE");
                        prosolPane.tabModProsol.data[i][5] = set.getString("STATUS") == null ? "" : set.getString("STATUS");
                        prosolPane.tabModProsol.data[i][6] = set.getString("DESCRIPTION") == null ? "" : set.getString("DESCRIPTION");
                        prosolPane.tabModProsol.data[i][7] = set.getInt("PROSOLID");
                        i++;
                    }
                } else {
                    prosolPane.tabModProsol.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "","",""}};
                }
            } else {
                prosolPane.tabModProsol.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "","",""}};
            }
            prosolPane.ebiModule.guiRenderer.getTable("prosolTable","Prosol").changeSelection(srow,0,false,false);
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            prosolPane.tabModProsol.fireTableDataChanged();      
        }



    }

    public void dataShowReport(int id) {
        try{
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", id);

        prosolPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                prosolPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_PROSOL")),
                getprosolNamefromId(id));
        }catch (Exception ex){}
    }


    public void dataNew(boolean reload) {
       try{
            // Remove lock
            prosolPane.ebiModule.unlockCompanyRecord(id,lockUser,"CRMProsol");
            lockId = -1;
            lockModuleName = "";
            lockUser = "";
            lockStatus = 0;
            lockTime =  null;
            id=-1;

            this.compProsol = new Crmproblemsolutions();
            prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedDate(prosolPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
            prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedFrom(EBIPGFactory.ebiUser);
            prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedDate("");
            prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedFrom("");

            prosolPane.ebiModule.guiRenderer.getTextfield("prosolNameText","Prosol").setText("");
            prosolPane.ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").setSelectedIndex(0);
            prosolPane.ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").setSelectedIndex(0);
            prosolPane.ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").setSelectedIndex(0);
            prosolPane.ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").setSelectedIndex(0);
            prosolPane.ebiModule.guiRenderer.getTextarea("prosolDescriptionText","Prosol").setText("");
            prosolPane.ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").setText("");
            prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").setID(-1);
            prosolPane.ebiModule.ebiPGFactory.getDataStore("Prosol","ebiNew",true);
            if(reload){
                this.dataShowDoc();
                this.dataShowProduct();
            }
       }catch (Exception ex){}
    }

    private void createHistory() throws  Exception {
        EBICRMHistoryCreator hcreator = new EBICRMHistoryCreator(prosolPane.ebiModule);
        List<String> list = new ArrayList<String>();

        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + prosolPane.ebiModule.ebiPGFactory.getDateToString(compProsol.getCreateddate()));
        list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + compProsol.getCreatedfrom());

        if (compProsol.getChangeddate() != null) {
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + prosolPane.ebiModule.ebiPGFactory.getDateToString(compProsol.getChangeddate()));
            list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + compProsol.getChangedfrom());
        }

        list.add(EBIPGFactory.getLANG("EBI_LANG_PROSOL_NUMBER") + ": " + (compProsol.getServicenr().equals(prosolPane.ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").getText()) == true ? compProsol.getServicenr() : compProsol.getServicenr()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + (compProsol.getName().equals(prosolPane.ebiModule.guiRenderer.getTextfield("prosolNameText","Prosol").getText()) == true ? compProsol.getName() : compProsol.getName()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_C_STATUS") + ": " + (compProsol.getStatus().equals(prosolPane.ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").getSelectedItem().toString()) == true ? compProsol.getStatus() : compProsol.getStatus()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + (compProsol.getCategory().equals(prosolPane.ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").getSelectedItem().toString()) == true ? compProsol.getCategory() : compProsol.getCategory()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_TYPE") + ": " + (compProsol.getType().equals(prosolPane.ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").getSelectedItem().toString()) == true ? compProsol.getType() : compProsol.getType()+"$") );
        list.add(EBIPGFactory.getLANG("EBI_LANG_CLASSIFICATION") + ": " + (compProsol.getClassification().equals(prosolPane.ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").getSelectedItem().toString()) == true ? compProsol.getClassification() : compProsol.getClassification()+"$") );

        list.add(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + (compProsol.getDescription().equals(prosolPane.ebiModule.guiRenderer.getTextarea("prosolDescriptionText","Prosol").getText()) == true ? compProsol.getDescription() : compProsol.getDescription()+"$") );
        list.add("*EOR*"); // END OF RECORD


        if (!compProsol.getCrmproblemsoldocses().isEmpty()) {

            Iterator iter = compProsol.getCrmproblemsoldocses().iterator();
            while (iter.hasNext()) {
                Crmproblemsoldocs obj = (Crmproblemsoldocs) iter.next();
                list.add(obj.getName() == null ? EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " : EBIPGFactory.getLANG("EBI_LANG_FILENAME") + ": " + obj.getName());
                list.add(prosolPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " : EBIPGFactory.getLANG("EBI_LANG_C_ADDED_DATE") + ": " + prosolPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()));
                list.add(obj.getCreatedfrom() == null ? EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + obj.getCreatedfrom());
                list.add("*EOR*");
            }
        }

        if (!compProsol.getCrmproblemsolpositions().isEmpty()) {

            Iterator iter = compProsol.getCrmproblemsolpositions().iterator();

            while (iter.hasNext()) {
                Crmproblemsolposition obj = (Crmproblemsolposition) iter.next();
                list.add(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NUMBER") + ": " + obj.getProductnr());
                list.add(obj.getProductname() == null ? EBIPGFactory.getLANG("EBI_LANG_NAME") + ":" : EBIPGFactory.getLANG("EBI_LANG_NAME") + ": " + obj.getProductname());
                list.add(obj.getCategory() == null ? EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ":" : EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + ": " + obj.getCategory());
                list.add(obj.getTaxtype() == null ? EBIPGFactory.getLANG("EBI_LANG_TAX") + ":" : EBIPGFactory.getLANG("EBI_LANG_TAX") + ": " + obj.getTaxtype());
                list.add(String.valueOf(obj.getPretax()) == null ? EBIPGFactory.getLANG("EBI_LANG_PRICE") + ":" : EBIPGFactory.getLANG("EBI_LANG_PRICE") + ": " + String.valueOf(obj.getPretax()));
                list.add(obj.getDescription() == null ? EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ":" : EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + ": " + obj.getDescription());
                list.add("*EOR*");

            }
        }

        try {
            hcreator.setDataToCreate(new EBICRMHistoryDataUtil(compProsol.getProsolid(), "Prosol", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataNewDoc() {

        try {
            if(checkIslocked(id,true)){
                 return;
            }

            File fs = prosolPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
            if (fs != null) {
                byte[] file = readFileToByte(fs);
                if (file != null) {
                    Crmproblemsoldocs docs = new Crmproblemsoldocs();
    //			   java.sql.Blob blb = Hibernate.createBlob(file);
                    docs.setCrmproblemsolutions(compProsol);
                    docs.setName(fs.getName());
                    docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                    docs.setCreatedfrom(EBIPGFactory.ebiUser);
                    docs.setFiles(file);

                    this.compProsol.getCrmproblemsoldocses().add(docs);
                    this.dataShowDoc();

                } else {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataViewDoc(int id) {

        String FileName  ;
        String FileType  ;
        OutputStream fos  ;
        try {


            Iterator iter = this.compProsol.getCrmproblemsoldocses().iterator();
            while (iter.hasNext()) {

                Crmproblemsoldocs doc = (Crmproblemsoldocs) iter.next();

                if (id == doc.getSolutiondocid()) {
                    // Get the BLOB inputstream

                    String file = doc.getName().replaceAll(" ", "_");

//					byte buffer[] = doc.getFiles().getBytes(1,(int)adress.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));

                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    resolverType(FileName, FileType);
                    break;
                }
            }
        } catch (FileNotFoundException exx) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
        } catch (IOException exx1) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_LOADING_FILE")).Show(EBIMessage.INFO_MESSAGE);
        }

    }

    public void dataShowDoc() {
       try {
       if(this.compProsol.getCrmproblemsoldocses().size() > 0){
        prosolPane.tabModDoc.data = new Object[this.compProsol.getCrmproblemsoldocses().size()][4];

        Iterator itr = this.compProsol.getCrmproblemsoldocses().iterator();
        int i = 0;
        while (itr.hasNext()) {

            Crmproblemsoldocs obj = (Crmproblemsoldocs) itr.next();
            prosolPane.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
            prosolPane.tabModDoc.data[i][1] = prosolPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : prosolPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
            prosolPane.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
            if(obj.getSolutiondocid() == null || obj.getSolutiondocid() < 0){obj.setSolutiondocid(((i + 1)*(-1)));}
            prosolPane.tabModDoc.data[i][3] = obj.getSolutiondocid();
            i++;
        }
       }else{
           prosolPane.tabModDoc.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
       }
        prosolPane.tabModDoc.fireTableDataChanged();
       }catch (Exception ex){}
    }

    private void resolverType(String fileName, String type) {
      try{
        type = type.toLowerCase();
        if (".jpg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
            EBIImageViewer view = new EBIImageViewer(prosolPane.ebiModule.ebiPGFactory.getMainFrame(), new javax.swing.ImageIcon(fileName));
            view.setVisible(true);
        } else if (".pdf".equals(type)) {
            prosolPane.ebiModule.ebiPGFactory.openPDFReportFile(fileName);
        } else if (".doc".equals(type)) {
            prosolPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        } else {
            prosolPane.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
        }
      }catch (Exception ex){}
    }

    private byte[] readFileToByte(File selFile) {
        InputStream st = readFileGetBlob(selFile);
        byte inBuf[]  ;
        try {
            int inBytes = st.available();
            inBuf = new byte[inBytes];
            st.read(inBuf, 0, inBytes);
        } catch (java.io.IOException ex) {
            return null;
        }

        return inBuf;
    }

    private InputStream readFileGetBlob(File file) {
        InputStream is  ;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            return null;
        }
        return is;
    }

    public void dataShowProduct() {
     try{
          if(this.compProsol.getCrmproblemsolpositions().size() > 0){
                prosolPane.tabModProduct.data = new Object[this.compProsol.getCrmproblemsolpositions().size()][7];

                Iterator itr = this.compProsol.getCrmproblemsolpositions().iterator();
                int i = 0;

                NumberFormat currency=NumberFormat.getCurrencyInstance();

                while (itr.hasNext()) {

                    Crmproblemsolposition obj = (Crmproblemsolposition) itr.next();

                    prosolPane.tabModProduct.data[i][0] = obj.getProductnr();
                    prosolPane.tabModProduct.data[i][1] = obj.getProductname() == null ? "" : obj.getProductname();
                    prosolPane.tabModProduct.data[i][2] = obj.getCategory() == null ? "" : obj.getCategory();
                    prosolPane.tabModProduct.data[i][3] = obj.getTaxtype() == null ? "" : obj.getTaxtype();
                    prosolPane.tabModProduct.data[i][4] = currency.format(prosolPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(1),String.valueOf(0))) == null ? "" : currency.format(prosolPane.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),"1",String.valueOf(0)));
                    prosolPane.tabModProduct.data[i][5] = obj.getDescription() == null ? "" : obj.getDescription();
                    if(obj.getProductid() == null || obj.getProductid() < 0){obj.setProductid(((i + 1)*(-1)));}
                    prosolPane.tabModProduct.data[i][6] = obj.getProductid();
                    i++;
                }
          }else{
            prosolPane.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
          }
           prosolPane.tabModProduct.fireTableDataChanged();
     }catch (Exception ex){}
    }

    public void dataDeleteDoc(int id) {
        
         try {
            if(checkIslocked(id,true)){
                 return;
            }

            Iterator iter = this.compProsol.getCrmproblemsoldocses().iterator();
            while (iter.hasNext()) {

                Crmproblemsoldocs doc = (Crmproblemsoldocs) iter.next();

                if (doc.getSolutiondocid() == id) {
                     if(id >= 0){
                        try {
                            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
                            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").delete(doc);
                            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.compProsol.getCrmproblemsoldocses().remove(doc);
                    this.dataShowDoc();
                    break;
                }
            }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    public void dataDeleteProduct(int id) {

         try {
            if(checkIslocked(id,true)){
                 return;
            }

            Iterator iter = this.compProsol.getCrmproblemsolpositions().iterator();
            while (iter.hasNext()) {

                Crmproblemsolposition prosolpro = (Crmproblemsolposition) iter.next();

                if (prosolpro.getProductid() == id) {
                  if(id >= 0){
                    try {
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").delete(prosolpro);
                        prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                    this.compProsol.getCrmproblemsolpositions().remove(prosolpro);
                    this.dataShowProduct();
                    break;
                }
            }
         } catch (Exception e) {
             e.printStackTrace();
         }
    }

    public Crmproblemsolutions getcompProsol() {
        return compProsol;
    }

    public void setcompProsol(Crmproblemsolutions compProsol) {
        this.compProsol = compProsol;
    }

    public Set<Crmproblemsoldocs> getprosolDocList() {
        return compProsol.getCrmproblemsoldocses();
    }

    public Set<Crmproblemsolposition> getprosolPosList() {
        return compProsol.getCrmproblemsolpositions();
    }

    private String getprosolNamefromId(int id) {

        String name = "";
        try {

            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();

            Query query = prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").createQuery(
                    "from Crmproblemsolutions where prosolid=? ").setInteger(0, id);
       
            Iterator iter = query.iterate();
            while (iter.hasNext()) {
                Crmproblemsolutions prosol = (Crmproblemsolutions) iter.next();
                prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("PROSOL_SESSION").refresh(prosol);
                if (prosol.getProsolid() == id) {
                    name = prosol.getName();
                    break;
                }
            }
            prosolPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").commit();
            
        }catch(Exception ex){ ex.printStackTrace();}
        return name;
    }


    /**
     * Check if a loaded record is locked
     * @param compNr
     * @param showMessage
     * @throws Exception
     */

    public boolean checkIslocked(int compNr, boolean showMessage) throws Exception{
            boolean ret = false;
           try{
            PreparedStatement ps =  prosolPane.ebiModule.ebiPGFactory.database.initPreparedStatement("SELECT * FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=?  ");
            ps.setInt(1,compNr);
            ps.setString(2,"CRMProsol");
            ResultSet rs = prosolPane.ebiModule.ebiPGFactory.database.executePreparedQuery(ps);

            rs.last();

            if (rs.getRow() <= 0) {
                lockId = compNr;
                lockModuleName = "CRMProsol";
                lockUser = EBIPGFactory.ebiUser;
                lockStatus = 1;
                lockTime =  new Timestamp(new Date().getTime());
                prosolPane.ebiModule.lockCompanyRecord(compNr,"CRMProsol",lockTime);
                activateLockedInfo(false);
            }else{
                rs.beforeFirst();
                rs.next();
                lockId = rs.getInt("RECORDID");
                lockModuleName = rs.getString("MODULENAME");
                lockUser = rs.getString("USER");
                lockStatus = rs.getInt("STATUS");
                lockTime =  rs.getTimestamp("LOCKDATE");

                if(!lockUser.equals(EBIPGFactory.ebiUser)){
                    activateLockedInfo(true);
                }

                if(showMessage && !lockUser.equals(EBIPGFactory.ebiUser)){
                    ret = true;
                }
            }

             // Pessimistic Dialog view info
             prosolPane.ebiModule.guiRenderer.getLabel("userx","pessimisticViewDialog").setText(lockUser);
             prosolPane.ebiModule.guiRenderer.getLabel("statusx","pessimisticViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_LOCKED"));

             if(lockTime != null){
                prosolPane.ebiModule.guiRenderer.getLabel("timex","pessimisticViewDialog").setText(lockTime.toString());
             }
           }catch (Exception ex){}
        return ret;
    }

    /**
     * Activate Pessimistic Lock for the GUI
     * @param enabled
     */

    public void activateLockedInfo(boolean enabled){
       try{
        //show red icon to a visual panel
        prosolPane.ebiModule.guiRenderer.getVisualPanel("Prosol").showLockIcon(enabled);

        //hide the delete buttons from crm panel
        prosolPane.ebiModule.guiRenderer.getButton("saveprosol","Prosol").setEnabled(enabled ? false : true);
        
        prosolPane.ebiModule.guiRenderer.getButton("newprosolDoc","Prosol").setVisible(enabled ? false : true);
        prosolPane.ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").setVisible(enabled ? false : true);

        prosolPane.ebiModule.guiRenderer.getButton("newpProduct","Prosol").setVisible(enabled ? false : true);
        prosolPane.ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").setVisible(enabled ? false : true);
        
        prosolPane.ebiModule.guiRenderer.getButton("deleteprosol","Prosol").setVisible(enabled ? false : true);
       }catch (Exception ex){}
    }

}
