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
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMAccountStack;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIImageViewer;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;
import ebiNeutrinoSDK.model.hibernate.Accountstack;
import ebiNeutrinoSDK.model.hibernate.Accountstackcd;
import ebiNeutrinoSDK.model.hibernate.Accountstackdocs;
import ebiNeutrinoSDK.model.hibernate.Companyproducttax;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

import javax.swing.*;


public class EBIDataControlAccountStack {

    private EBICRMAccountStack acStackPanel = null;
    public Timestamp lockTime = null;
    public String lockUser ="";
    public String lockModuleName = "";
    public int lockStatus=0;
    public int lockId =-1;
    public int accountID=-1;
    public Accountstack actStack = null;
    private double creditValue = 0.0;
    public final EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
    private NumberFormat currency=NumberFormat.getCurrencyInstance();

    public EBIDataControlAccountStack(EBICRMAccountStack acPan){
         this.acStackPanel = acPan;
         actStack = new Accountstack();
    }


    public boolean dataStore(boolean isEdit){
       try{
            if(accountID != -1){
                if(checkIslocked(accountID,true)){
                 return false;
                }
            }
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
            if (isEdit == false) {
                actStack.setCreateddate(new Date());
                actStack.setCreatedfrom(acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").getCreatedFrom());
            } else {
                actStack.setChangeddate(new Date());
                actStack.setChangedfrom(EBIPGFactory.ebiUser);
            }

            actStack.setAccountdate(acStackPanel.ebiModule.guiRenderer.getTimepicker("dateText","Account").getDate());
            actStack.setAccountnr(acStackPanel.ebiModule.guiRenderer.getTextfield("numberText","Account").getText());
            actStack.setAccountname(acStackPanel.ebiModule.guiRenderer.getTextfield("nameText","Account").getText());
            actStack.setAccountvalue(Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").getValue().toString()));

            actStack.setAccountDebit(acStackPanel.ebiModule.guiRenderer.getTextfield("debitText","Account").getText());
            actStack.setAccountDName(acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").getText());
            actStack.setAccountDValue(Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").getValue().toString()));

            actStack.setAccountCredit(acStackPanel.ebiModule.guiRenderer.getTextfield("creditText","Account").getText());
            actStack.setAccountCName(acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").getText());
            actStack.setAccountCValue(Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").getValue().toString()));

            actStack.setAccountType(acStackPanel.accountDebitCreditType);
            actStack.setAccountTaxType(acStackPanel.accountDebitTaxName);

            actStack.setDescription(acStackPanel.ebiModule.guiRenderer.getTextarea("descriptionText","Account").getText());

           if (!actStack.getAccountstackdocses().isEmpty()) {
                Iterator iter = actStack.getAccountstackdocses().iterator();
                while(iter.hasNext()){
                   Accountstackdocs docs = (Accountstackdocs)iter.next();
                   docs.setAccountstack(actStack);
                   if(docs.getAccountdocid() < 0){ docs.setAccountdocid(null);}
                   acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").saveOrUpdate(docs);
                }
            }
            storeActualCredit(acStackPanel.ebiModule.guiRenderer.getTextfield("creditText","Account").getText(),creditValue);
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").saveOrUpdate(actStack);
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
            dataNew(true);
       }catch(Exception ex){
          ex.printStackTrace();
       }
        return true;
    }

    public void dataEdit(int id) {
        dataNew(false);
        Query query;
        try {
        	
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();

            query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery(
                    "from Accountstack where acstackid=? ").setInteger(0, id);

            Iterator iter =  query.iterate();
            
            if (iter.hasNext()) {
                this.accountID = id;

                actStack = (Accountstack) iter.next();
                acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setID(id);
                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").refresh(actStack);

                acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setCreatedDate(acStackPanel.ebiModule.ebiPGFactory.
                                                                                            getDateToString(actStack.getCreateddate() == null
                                                                                                                    ? new Date() : actStack.getCreateddate()));

                acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setCreatedFrom(actStack.getCreatedfrom() ==
                                                                                    null ? EBIPGFactory.ebiUser : actStack.getCreatedfrom());

                if (actStack.getChangeddate() != null) {
                    acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setChangedDate(acStackPanel.ebiModule.ebiPGFactory.getDateToString(actStack.getChangeddate()));
                    acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setChangedFrom(actStack.getChangedfrom());
                } else {
                    acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setChangedDate("");
                    acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setChangedFrom("");
                }

                acStackPanel.ebiModule.guiRenderer.getTimepicker("dateText","Account").setDate(actStack.getAccountdate() == null ? new Date() : actStack.getAccountdate() );
                //acStackPanel.ebiModule.guiRenderer.getTimepicker("dateText","Account").getEditor().setText(acStackPanel.ebiModule.ebiPGFactory.getDateToString(actStack.getAccountdate()));
                acStackPanel.ebiModule.guiRenderer.getTextfield("numberText", "Account").setText(actStack.getAccountnr());
                acStackPanel.ebiModule.guiRenderer.getTextfield("nameText", "Account").setText(actStack.getAccountname());
                acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("amountText", "Account").setValue(actStack.getAccountvalue() == null ? 0.0 : actStack.getAccountvalue());
                acStackPanel.ebiModule.guiRenderer.getTextfield("debitText", "Account").setText(actStack.getAccountDebit());
                acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionDebit", "Account").setText(actStack.getAccountDName());
                acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("debitCal", "Account").setValue(actStack.getAccountDValue() == null ? 0.0 : actStack.getAccountDValue());

                acStackPanel.ebiModule.guiRenderer.getTextfield("creditText","Account").setText(actStack.getAccountCredit());
                acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setText(actStack.getAccountCName());
                acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("creditCal", "Account").setValue(actStack.getAccountCValue() == null ? 0.0 : actStack.getAccountCValue());
                acStackPanel.ebiModule.guiRenderer.getTextarea("descriptionText", "Account").setText(actStack.getDescription());
                acStackPanel.accountDebitCreditType = actStack.getAccountType();
                acStackPanel.accountDebitTaxName = actStack.getAccountTaxType();

                dataShowDoc();
                acStackPanel.isEdit = true;
                acStackPanel.ebiModule.ebiPGFactory.getDataStore("Account","ebiEdit",true);
                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
                
                acStackPanel.ebiModule.guiRenderer.getTable("accountTable","Account").
                				changeSelection(acStackPanel.ebiModule.guiRenderer.getTable("accountTable","Account").
                						convertRowIndexToView(acStackPanel.ebiModule.dynMethod.
                									getIdIndexFormArrayInATable(acStackPanel.tabModAccount.data,6, id)),0,false,false);
            
            }else{
            	EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
           
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDelete(int id){
        Query query;
        try {

            if(checkIslocked(id,true)){
                 return;
            }

            query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery(
                    "from Accountstack where acstackid=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

                if (iter.hasNext()) {

                    Accountstack act = (Accountstack) iter.next();

                    if (act.getAcstackid() == id) {
                        try {
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").delete(act);

                            acStackPanel.ebiModule.ebiPGFactory.getDataStore("Account","ebiDelete",true);
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
                        } catch (HibernateException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
        }catch(Exception ex){
           ex.printStackTrace();
        }
        dataNew(true);
    }

    public void dataNew(boolean load){
        // Remove lock
        lockId = -1;
        lockModuleName = "";
        lockUser = "";
        lockStatus = 0;
        lockTime =  null;
        this.accountID=-1;

        acStackPanel.accountDebitCreditType = 0;
        acStackPanel.accountDebitTaxName = "";
        creditValue = 0.0;
        acStackPanel.showCreditID = -1;
        acStackPanel.showDebitID = -1;
        acStackPanel.isEdit=false;
        acStackPanel.isDebit = false;
        acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").setID(-1);
        actStack = new Accountstack();

        acStackPanel.ebiModule.ebiPGFactory.getDataStore("Account","ebiNew",true);
        dataShowDoc();
        if(load){
            acStackPanel.initialize();
        }
    }

    public void dataShow(final String invoiceYear){
    	
            int srow =  acStackPanel.ebiModule.guiRenderer.getTable("accountTable","Account").getSelectedRow();
            Query query;
            try {
                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();

                if(!"".equals(invoiceYear) && !"null".equals(invoiceYear)){
                    Calendar calendar1 = new GregorianCalendar();
                    calendar1.set(Calendar.DAY_OF_MONTH,1);
                    calendar1.set(Calendar.MONTH,Calendar.JANUARY);
                    calendar1.set(Calendar.YEAR,Integer.parseInt(invoiceYear));

                    Calendar calendar2 = new GregorianCalendar();
                    calendar2.set(Calendar.DAY_OF_MONTH,31);
                    calendar2.set(Calendar.MONTH,Calendar.DECEMBER);
                    calendar2.set(Calendar.YEAR,Integer.parseInt(invoiceYear));

                    query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery("from Accountstack ac where ac.accountdate between ? and ? order by ac.createddate desc ");
                    query.setDate(0,calendar1.getTime());
                    query.setDate(1, calendar2.getTime());

                }else{
                    query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery("from Accountstack ac order by ac.createddate desc");
                }

                  if(query.list().size() > 0){

                        Iterator iter =  query.iterate();

                        acStackPanel.tabModAccount.data = new Object[query.list().size()][8];
                        String D = EBIPGFactory.getLANG("EBI_LANG_DEBIT").substring(0,3).toUpperCase();
                        String C = EBIPGFactory.getLANG("EBI_LANG_CREDIT").substring(0,3).toUpperCase();
                        int i = 0;
                        while (iter.hasNext()) {
                            Accountstack act = (Accountstack) iter.next();
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").refresh(act);
                            acStackPanel.tabModAccount.data[i][0] = act.getAccountType() == 1 ? D : C;
                            acStackPanel.tabModAccount.data[i][1] = acStackPanel.ebiModule.ebiPGFactory.getDateToString(act.getAccountdate());
                            acStackPanel.tabModAccount.data[i][2] = act.getAccountnr() == null ? "" : act.getAccountnr();
                            acStackPanel.tabModAccount.data[i][3] = act.getAccountname() == null ? "" : act.getAccountname();
                            acStackPanel.tabModAccount.data[i][4] = currency.format(act.getAccountvalue()) == null ? "" : currency.format(act.getAccountvalue());
                            acStackPanel.tabModAccount.data[i][5] = act.getAccountDebit() == null ? "" :  act.getAccountDebit();
                            acStackPanel.tabModAccount.data[i][6] = act.getAccountCredit() == null ? "" : act.getAccountCredit();
                            acStackPanel.tabModAccount.data[i][7] = act.getAcstackid();
                            i++;
                        }
                  }else{
                    acStackPanel.tabModAccount.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
                  }
                acStackPanel.tabModAccount.fireTableDataChanged();
                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
            }catch(Exception ex){
                ex.printStackTrace();
            }

            acStackPanel.ebiModule.guiRenderer.getTable("accountTable","Account").changeSelection(srow,0,false,false);


    }

    public void dataShowReport() {
       try{
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", 0);

        acStackPanel.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                acStackPanel.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PRINT_ACCOUNT")),
                "Account");
       }catch(Exception ex){}
    }


    public void dataDeleteDoc(int id) {
       try{
            Iterator iter = this.actStack.getAccountstackdocses().iterator();
            while (iter.hasNext()) {

                Accountstackdocs doc = (Accountstackdocs) iter.next();

                if (doc.getAccountdocid() == id) {
                      if(id >= 0){
                        try {
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").delete(doc);
                            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      }
                    this.actStack.getAccountstackdocses().remove(doc);
                    this.dataShowDoc();
                    break;
                }
            }
       }catch(Exception ex){}
    }

    public void dataNewDoc() {
      try{
        File fs = acStackPanel.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
        if (fs != null) {
            byte[] file = readFileToByte(fs);
            if (file != null) {
                Accountstackdocs docs = new Accountstackdocs();
//			   java.sql.Blob blb = Hibernate.createBlob(file);
                docs.setAccountstack(actStack);
                docs.setName(fs.getName());
                docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                docs.setCreatedfrom(EBIPGFactory.ebiUser);
                docs.setFiles(file);

                this.actStack.getAccountstackdocses().add(docs);
                this.dataShowDoc();

            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_CANNOT_READING")).Show(EBIMessage.ERROR_MESSAGE);
                return;
            }
        }
      }catch(Exception ex){}
    }

    public void dataViewDoc(int id) {

        String FileName  ;
        String FileType  ;
        OutputStream fos  ;
        try {

            Iterator iter = this.actStack.getAccountstackdocses().iterator();
            while (iter.hasNext()) {

                Accountstackdocs doc = (Accountstackdocs) iter.next();

                if (id == doc.getAccountdocid()) {
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
       try{
           if(this.actStack.getAccountstackdocses().size() > 0){
                acStackPanel.tabModDoc.data = new Object[this.actStack.getAccountstackdocses().size()][4];

                Iterator itr = this.actStack.getAccountstackdocses().iterator();
                int i = 0;
                while (itr.hasNext()) {

                    Accountstackdocs obj = (Accountstackdocs) itr.next();

                    acStackPanel.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                    acStackPanel.tabModDoc.data[i][1] = acStackPanel.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : acStackPanel.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                    acStackPanel.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                    if(obj.getAccountdocid() == null || obj.getAccountdocid() < 0){ obj.setAccountdocid(((i+1) *(-1)));}
                    acStackPanel.tabModDoc.data[i][3] = obj.getAccountdocid();
                    i++;
                }
           }else{
             acStackPanel.tabModDoc.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
           }
            acStackPanel.tabModDoc.fireTableDataChanged();
       }catch(Exception ex){}
    }

    public void dataStoreCreditDebit(boolean isEdit,int id){

        try {

            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();

            Query query;
            Accountstackcd actcd=null;

            if(isEdit){
                query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd where accountstackcdid=?").setInteger(0,id);
                Iterator it = query.iterate();
                if(it.hasNext()){
                    actcd = (Accountstackcd) it.next();
                }
            }else{
                if(checkifRecordExist(acStackPanel.ebiModule.guiRenderer.getTextfield("numberText","creditDebitDialog").getText(),
                        acStackPanel.ebiModule.guiRenderer.getTextfield("nameText","creditDebitDialog").getText())){
                    return;
                }
                actcd = new Accountstackcd();
            }
            actcd.setCreateddate(new Date());
            actcd.setCreatedfrom(EBIPGFactory.ebiUser);
            actcd.setCreditdebitnumber(acStackPanel.ebiModule.guiRenderer.getTextfield("numberText","creditDebitDialog").getText());
            actcd.setCreditdebitname(acStackPanel.ebiModule.guiRenderer.getTextfield("nameText","creditDebitDialog").getText());
            actcd.setCreditdebittaxtname(acStackPanel.ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").getSelectedItem().toString());

            if(acStackPanel.ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").getSelectedIndex() == 3){
                 actcd.setCreditdebitvalue(Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("valueText","creditDebitDialog").getValue() == null ? "0.0" :
                   acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("valueText","creditDebitDialog").getValue().toString() ));
            }else{
                actcd.setCreditdebitvalue(Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getLabel("taxValue","creditDebitDialog").getText().replace("%","")));
            }

            actcd.setCreditdebittype(acStackPanel.ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").getSelectedIndex());
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").saveOrUpdate(actcd);
            acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
            dataShowCreditDebit(acStackPanel.ebiModule.guiRenderer.getComboBox("selectCreditDebitText","Account").getSelectedIndex());
            dataNewCreditDebit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dataEditCreditDebit(int id){
        try {

            Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                    createQuery("from Accountstackcd where accountstackcdid=?").setInteger(0,id);

            Iterator it = query.iterate();
            if(it.hasNext()){
               Accountstackcd  actcd = (Accountstackcd) it.next();
               acStackPanel.ebiModule.guiRenderer.getTextfield("numberText","creditDebitDialog").setText(actcd.getCreditdebitnumber());
               acStackPanel.ebiModule.guiRenderer.getTextfield("nameText","creditDebitDialog").setText(actcd.getCreditdebitname());
               if(actcd.getCreditdebittype() == 3){
                  acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("valueText","creditDebitDialog").setValue(actcd.getCreditdebitvalue());
               }else{
                  acStackPanel.ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").setSelectedItem(actcd.getCreditdebittaxtname());
               }
               acStackPanel.ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").setSelectedIndex(actcd.getCreditdebittype());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDeleteCreditDebit(int id){
        try {

            Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                    createQuery("from Accountstackcd where accountstackcdid=?").setInteger(0,id);

            Iterator it = query.iterate();
            if(it.hasNext()){
               Accountstackcd  actcd = (Accountstackcd) it.next();
               acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
               acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").delete(actcd);
               acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();
               dataShowCreditDebit(acStackPanel.ebiModule.guiRenderer.getComboBox("selectCreditDebitText","Account").getSelectedIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataNewCreditDebit(){
         acStackPanel.ebiModule.guiRenderer.getTextfield("numberText","creditDebitDialog").setText("");
         acStackPanel.ebiModule.guiRenderer.getTextfield("nameText","creditDebitDialog").setText("");
         acStackPanel.ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").setSelectedIndex(0);
         acStackPanel.ebiModule.guiRenderer.getLabel("taxValue","creditDebitDialog").setText("0.0%");
         acStackPanel.ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").setSelectedIndex(0);
    }

    private String getAccountNamefromId(int id) {

        String name = "";
        Query query;

        try {
                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
                query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery(
                        "from Accountstack where acstackid=? ").setInteger(0, id);

                Iterator iter =  query.iterate();

                if (iter.hasNext()) {
                    Accountstack act = (Accountstack) iter.next();
                    acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").refresh(act);
                    name = act.getAccountname();

                }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return name;
    }


    public double getCreditDebitValue(String cdnr,int type){
      Query query;
      double toRet = 0.0;
      try{

           query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd where creditdebitnumber=? and creditdebittype=?");

           query.setString(0,cdnr);
           query.setString(1,cdnr);
            if(query.list().size()>0){
                Iterator iterx = query.iterate();
                if(iterx.hasNext()){
                    Accountstackcd cd = (Accountstackcd)iterx.next();
                    toRet = cd.getCreditdebitvalue();
                }
            }
      }catch(Exception ex){
          ex.printStackTrace();
      }

      return toRet;
    }

    public int getIDFromNumber(String nr, boolean isCredit){

        int toReturn = -1;
        try {

         Query query;
            if(isCredit){
                query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd where creditdebitnumber=? and creditdebittype>=2").setString(0,nr);

            }else{
                query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd where creditdebitnumber=? and creditdebittype < 2").setString(0,nr);
            }

            Iterator it = query.iterate();
            if(it.hasNext()){
               Accountstackcd  actcd = (Accountstackcd) it.next();
               toReturn = actcd.getAccountstackcdid();
            }

        } catch (Exception e) {
            e.printStackTrace();
       }
        return toReturn;
    }

    public void storeActualCredit(String acnr, double crdValue){

        try{
         Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                    createQuery("from Accountstackcd where creditdebitnumber=?").setString(0,acnr);

            Iterator it = query.iterate();
            if(it.hasNext()){
              Accountstackcd  actcd = (Accountstackcd) it.next();
              actcd.setCreditdebitvalue(crdValue);
              acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").saveOrUpdate(actcd);
            }
        }catch(Exception ex){
           ex.printStackTrace();
        }
    }

    public void dataCalculateTax(int id){
       try {
         Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                    createQuery("from Accountstackcd where accountstackcdid=?").setInteger(0,id);

            Iterator it = query.iterate();
            if(it.hasNext()){
               Accountstackcd  actcd = (Accountstackcd) it.next();

                double val = "".equals(acStackPanel.ebiModule.guiRenderer.
                                                                getFormattedTextfield("amountText","Account").getText()) ? 0.0 :
                                                Double.parseDouble(acStackPanel.ebiModule.guiRenderer.
                                                                getFormattedTextfield("amountText","Account").getValue().toString());


              if(actcd.getCreditdebittype() < 3){

                   acStackPanel.showDebitID = actcd.getAccountstackcdid();

                   acStackPanel.ebiModule.guiRenderer.
                            getFormattedTextfield("debitCal","Account").setValue(
                                                        ((actcd.getCreditdebitvalue() * val)/100));
                   acStackPanel.ebiModule.guiRenderer.getTextfield("debitText","Account").setText(actcd.getCreditdebitnumber());
                   acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").setText(actcd.getCreditdebitname());

                   if(actcd.getCreditdebittype() == 1){
                       acStackPanel.isDebit = true;
                   }else{
                       acStackPanel.isDebit = false;
                   }
                  acStackPanel.accountDebitCreditType = actcd.getCreditdebittype();
                  acStackPanel.accountDebitTaxName = actcd.getCreditdebittaxtname();
               }else{
                  acStackPanel.showCreditID = actcd.getAccountstackcdid();
                  acStackPanel.ebiModule.guiRenderer.getTextfield("creditText","Account").setText(actcd.getCreditdebitnumber());
                  acStackPanel.ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setText(actcd.getCreditdebitname());

                  double tax = Double.parseDouble(acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").getValue() == null ? "0.0" :
                                                    acStackPanel.ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").getValue().toString());

                  if(acStackPanel.isDebit){
                    creditValue =  actcd.getCreditdebitvalue() -  (tax + val);
                  }else{
                    creditValue =  actcd.getCreditdebitvalue() +  (tax + val);
                  }

                  acStackPanel.ebiModule.guiRenderer.
                            getFormattedTextfield("creditCal","Account").setValue(creditValue);
               }

            }
       } catch (Exception e) {
            e.printStackTrace();
       }

    }

    private boolean checkifRecordExist(String nr, String name) {
       boolean exist = false;
       try {
        Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd where creditdebitnumber=? or creditdebitname=?  ");
             query.setString(0,nr);
             query.setString(1,name);

             if(query.list().size() > 0){
               exist = true;
               EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
             }


       }catch(Exception ex){
          EBIExceptionDialog.getInstance(ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
         exist = true;
       }

       return exist;
    }

    public void dataShowCreditDebitExt(int type) {
        Query query;
        try {
                    if(type == 0){
                        query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                            createQuery("from Accountstackcd ");
                    }else{
                        query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                           createQuery("from Accountstackcd where creditdebittype=?").setInteger(0,type);
                    }

                    if(query.list().size() > 0){
                        acStackPanel.creditDebitMod.data = new Object[query.list().size()][3];
                        Iterator it = query.iterate();
                        int i=0;
                        while (it.hasNext()) {

                            Accountstackcd acCD = (Accountstackcd) it.next();
                            acStackPanel.creditDebitMod.data[i][0] = acCD.getCreditdebitnumber() == null ? "" : acCD.getCreditdebitnumber();
                            acStackPanel.creditDebitMod.data[i][1] = acCD.getCreditdebitname() == null ? "": acCD.getCreditdebitname();
                            acStackPanel.creditDebitMod.data[i][2] = acCD.getAccountstackcdid();
                            i++;

                        }
                    }else{
                        acStackPanel.creditDebitMod.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
                    }
                    acStackPanel.creditDebitMod.fireTableDataChanged();
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dataShowCreditDebit(int type) {
        Query query;
        try {
                if(type == 0){
                    query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery("from Accountstackcd ");
                }else{
                    String qs;
                    if(type <3){
                       qs = "from Accountstackcd where creditdebittype <=?";
                    }else{
                       qs = "from Accountstackcd where creditdebittype =?";
                    }

                    query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                                        createQuery(qs).setInteger(0,type);
                }
                    if(query.list().size() > 0){
                        acStackPanel.creditDebitMod.data = new Object[query.list().size()][3];
                        Iterator it = query.iterate();
                        int i=0;
                        while (it.hasNext()) {

                            Accountstackcd acCD = (Accountstackcd) it.next();
                            acStackPanel.creditDebitMod.data[i][0] = acCD.getCreditdebitnumber() == null ? "" : acCD.getCreditdebitnumber();
                            acStackPanel.creditDebitMod.data[i][1] = acCD.getCreditdebitname() == null ? "": acCD.getCreditdebitname();
                            acStackPanel.creditDebitMod.data[i][2] = acCD.getAccountstackcdid();
                            i++;

                        }
                    }else{
                        acStackPanel.creditDebitMod.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
                    }
                    acStackPanel.creditDebitMod.fireTableDataChanged();
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resolverType(String fileName, String type) {
       try{
            type = type.toLowerCase();
            if (".jpg".equals(type) || ".gif".equals(type) || ".png".equals(type)) {
                EBIImageViewer view = new EBIImageViewer(acStackPanel.ebiModule.ebiPGFactory.getMainFrame(),new javax.swing.ImageIcon(fileName));
                view.setVisible(true);
            } else if (".pdf".equals(type)) {
                acStackPanel.ebiModule.ebiPGFactory.openPDFReportFile(fileName);
            } else if (".doc".equals(type)) {
                acStackPanel.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
            } else {
                acStackPanel.ebiModule.ebiPGFactory.openTextDocumentFile(fileName);
            }
       }catch(Exception ex){}
    }

    private byte[] readFileToByte(File selFile) {
        InputStream st = readFileGetBlob(selFile);
        byte inBuf[];
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
        InputStream is;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FILE_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            return null;
        }
        return is;
    }

    public double getTaxValue(String value) {

        double ret=0.0;
        Query query;
        try {
            query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").
                    createQuery("from Companyproducttax where name=? ").setString(0,value);

                    Iterator it = query.iterate();

                    if (it.hasNext()) {

                        Companyproducttax tax = (Companyproducttax) it.next();
                        ret = tax.getTaxvalue();
                    }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void imporInvoicetoAccout(final String invoiceYear){

       final Runnable run = new Runnable(){

           public void run() {
               EBIWinWaiting wait = null;
               try{
                wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_IMPORT_DATA"));
                wait.setVisible(true);

                Calendar calendar1 = new GregorianCalendar();
                calendar1.set(Calendar.DAY_OF_MONTH,1);
                calendar1.set(Calendar.MONTH,Calendar.JANUARY);

                Calendar calendar2 = new GregorianCalendar();
                calendar2.set(Calendar.DAY_OF_MONTH,31);
                calendar2.set(Calendar.MONTH,Calendar.DECEMBER);

                   if(!"".equals(invoiceYear)){
                       calendar1.set(Calendar.YEAR,Integer.parseInt(invoiceYear));
                       calendar2.set(Calendar.YEAR,Integer.parseInt(invoiceYear));
                   }else{
                       calendar1.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));
                       calendar2.set(Calendar.YEAR,Calendar.getInstance().get(Calendar.YEAR));
                   }

                  acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").begin();
                  Query query = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery(
                                    "from Crminvoice where date between ? and ? ");
                  query.setDate(0,calendar1.getTime());
                  query.setDate(1, calendar2.getTime());

                  Iterator itx = query.iterate();
                  while(itx.hasNext()){
                     Crminvoice inv = (Crminvoice)itx.next();

                     Iterator ivnIter = inv.getCrminvoicepositions().iterator();

                     while(ivnIter.hasNext()){

                       Crminvoiceposition pox = (Crminvoiceposition)ivnIter.next();
                       String inpNr = "INV"+inv.getBeginchar()+inv.getInvoicenr()+pox.getPositionid();

                       Query query1 = acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").createQuery(
                                    "from Accountstack where accountnr=?");
                       query1.setString(0,inpNr);

                       Accountstack stck;
                       if(query1.list().size() <= 0){

                           stck = new Accountstack();
                           stck.setCreateddate(new Date());
                           stck.setCreatedfrom(EBIPGFactory.ebiUser);

                           stck.setAccountdate(inv.getDate());
                           stck.setAccountnr(inpNr);
                           stck.setAccountname(inv.getName());
                           double amount = (pox.getNetamount() * pox.getQuantity());
                           stck.setAccountvalue(amount);

                           stck.setAccountDebit("115");
                           stck.setAccountDName(EBIPGFactory.getLANG("EBI_LANG_CREDIT_FROM_INVOICE"));
                           double txy = ((amount * getTaxValue(pox.getTaxtype())) / 100);
                           stck.setAccountDValue(txy);

                           stck.setAccountCredit("1000");
                           stck.setAccountCName("Bank 1");
                           double nValx =getCreditDebitValue("1000",3)+(amount+txy);
                           stck.setAccountCValue(nValx);
                           storeActualCredit("1000",nValx);
                           stck.setAccountType(2);
                           stck.setDescription(inv.getContactdescription());
                           acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIACCOUNT_SESSION").saveOrUpdate(stck);
                       }
                     }
                  }

                acStackPanel.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIACCOUNT_SESSION").commit();

               }catch(Exception ex){
                  ex.printStackTrace();
               }finally{
                   if(wait!= null){
                       wait.setVisible(false);
                   }
               }
             dataShow(invoiceYear);
           }
       };


       Thread impThr = new Thread(run, "Import Invoice");
       impThr.start();

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
            PreparedStatement ps =  acStackPanel.ebiModule.ebiPGFactory.database.initPreparedStatement("SELECT * FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=?  ");
            ps.setInt(1,compNr);
            ps.setString(2,"CRMAccount");
            ResultSet rs = acStackPanel.ebiModule.ebiPGFactory.database.executePreparedQuery(ps);

            rs.last();

            if (rs.getRow() <= 0) {
                lockId = compNr;
                lockModuleName = "CRMAccount";
                lockUser = EBIPGFactory.ebiUser;
                lockStatus = 1;
                lockTime =  new Timestamp(new Date().getTime());
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
             acStackPanel.ebiModule.guiRenderer.getLabel("userx","pessimisticViewDialog").setText(lockUser);
             acStackPanel.ebiModule.guiRenderer.getLabel("statusx","pessimisticViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_LOCKED"));
             acStackPanel.ebiModule.guiRenderer.getLabel("timex","pessimisticViewDialog").setText(lockTime.toString());
        }catch(Exception ex){}
        return ret;
    }

    /**
     * Activate Pessimistic Lock for the GUI
     * @param enabled
     */

    public void activateLockedInfo(boolean enabled){
       try{
        //show red icon to a visual panel
        acStackPanel.ebiModule.guiRenderer.getVisualPanel("Account").showLockIcon(enabled);

        //hide the delete buttons from crm panel
        acStackPanel.ebiModule.guiRenderer.getButton("saveAccount","Account").setEnabled(enabled ? false : true);
        acStackPanel.ebiModule.guiRenderer.getButton("deleteAccount","Account").setVisible(enabled ? false : true);
       }catch(Exception ex){}
    }


}
