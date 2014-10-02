package ebiCRM.data.control;

import java.awt.Cursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;

import ebiNeutrinoSDK.model.hibernate.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.dialogs.EBICRMDialogSearchProduct;
import ebiCRM.gui.dialogs.EBIDialogProperties;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

public class EBIDataControlProduct {

    public Crmproduct product = null;
    private Crmproductdependency dependency = null;
    private Crmproductdimension dimension = null;
    private EBICRMProduct productPane = null;
    private int id = -1;
    public Timestamp lockTime = null;
    public String lockUser ="";
    public String lockModuleName = "";
    public int lockStatus=0;
    public int lockId =-1;

    public EBIDataControlProduct(EBICRMProduct productPane) {
        product = new Crmproduct();
        this.productPane = productPane;
        dimension = new Crmproductdimension();
        dependency = new Crmproductdependency();
    }

    public boolean dataStore(boolean isEdit){

        try {
            productPane.ebiModule.ebiContainer.showInActionStatus("Product",true);
            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
            if (!isEdit) {
                this.product.setCreateddate(new Date());
                this.product.setCreatedfrom(EBIPGFactory.ebiUser);
                productPane.isEdit = true;
            } else {
                this.product.setChangeddate(new Date());
                this.product.setChangedfrom(EBIPGFactory.ebiUser);
            }

            product.setProductnr(productPane.ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").getText());
            product.setProductname(productPane.ebiModule.guiRenderer.getTextfield("ProductNameText","Product").getText());

            if(productPane.ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").getSelectedItem() != null){
               product.setCategory(productPane.ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").getSelectedItem().toString());
            }
            if(productPane.ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").getSelectedItem() != null){
                product.setType(productPane.ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").getSelectedItem().toString());
            }

            if(productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem() != null){
                product.setTaxtype(productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString());
            }

            product.setPretax(Double.parseDouble(productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").getValue() == null ? "0.0" : productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").getValue().toString()));
            product.setNetamount(Double.parseDouble(productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").getValue() == null ? "0.0" : productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").getValue().toString()));
            product.setSaleprice(Double.parseDouble(productPane.ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").getValue() == null ? "0.0" : productPane.ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").getValue().toString()));

            product.setDescription(productPane.ebiModule.guiRenderer.getTextarea("productDescription","Product").getText());


            if(!product.getCrmproductdimensions().isEmpty()){
                Iterator itdim = product.getCrmproductdimensions().iterator();
                while(itdim.hasNext()){
                    Crmproductdimension dimx = (Crmproductdimension)itdim.next();
                    dimx.setCrmproduct(product);
                    if(dimx.getDimensionid() < 0){ dimx.setDimensionid(null);}
                    productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(dimx);
                }
            }
            
            if(!product.getCrmproductdependencies().isEmpty()){
                Iterator itdip = product.getCrmproductdependencies().iterator();
                while(itdip.hasNext()){
                    Crmproductdependency cdip = (Crmproductdependency) itdip.next();
                    cdip.setCrmproduct(product);
                    if(cdip.getDependencyid() < 0){ cdip.setDependencyid(null);}
                    productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(cdip);
                }
            }

            if(!product.getCrmproductdocs().isEmpty()){
                Iterator itdip = product.getCrmproductdocs().iterator();
                while(itdip.hasNext()){
                    Crmproductdocs cdocs = (Crmproductdocs) itdip.next();
                    cdocs.setCrmproduct(product);
                    if(cdocs.getProductdocid() < 0){ cdocs.setProductdocid(null);}
                    productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(cdocs);
                }
            }

            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(product);
            productPane.ebiModule.ebiPGFactory.getDataStore("Product","ebiSave",true);
            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
            
            if(!isEdit){
            	productPane.ebiModule.guiRenderer.getVisualPanel("Product").setID(product.getProductid());
            }
            dataNew();

            productPane.ebiModule.ebiContainer.showInActionStatus("Product", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    
    public void dataCopy(int id){
    	 Query query;
         try {

             productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();

             query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                     "from Crmproduct where productid=? ").setInteger(0, id);

             Iterator iter = query.list().iterator();

             if (iter.hasNext()) {

            	 Crmproduct pro = (Crmproduct) iter.next();
            	 productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(pro);
            	 
            	 Crmproduct pnew = new Crmproduct();
            	 pnew.setCreateddate(new Date());
            	 pnew.setCreatedfrom(EBIPGFactory.ebiUser);
            	 pnew.setProductnr(pro.getProductnr());
            	 pnew.setProductname(pro.getProductname()+" - (Copy)");
            	 pnew.setCategory(pro.getCategory());
            	 pnew.setType(pro.getType());
            	 pnew.setTaxtype(pro.getTaxtype());

            	 pnew.setPretax(pro.getPretax());
            	 pnew.setNetamount(pro.getNetamount());
            	 pnew.setSaleprice(pro.getSaleprice());

            	 pnew.setDescription(pro.getDescription());
            	 pnew.setPicture(pro.getPicture());
            	 pnew.setPicturename(pro.getPicturename());

                 if(!pro.getCrmproductdimensions().isEmpty()){
                     Iterator itdim = pro.getCrmproductdimensions().iterator();
                     while(itdim.hasNext()){
                         Crmproductdimension dimx = (Crmproductdimension)itdim.next();
                        
                         Crmproductdimension nd = new Crmproductdimension();
                         nd.setCrmproduct(pnew);
                         nd.setCreateddate(new Date());
                         nd.setCreatedfrom(EBIPGFactory.ebiUser);
                         nd.setName(dimx.getName());
                         nd.setValue(dimx.getValue());
                         
                         productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(nd);
                     }
                 }
                 
                 if(!pro.getCrmproductdependencies().isEmpty()){
                     Iterator itdip = pro.getCrmproductdependencies().iterator();
                     while(itdip.hasNext()){
                         Crmproductdependency cdip = (Crmproductdependency) itdip.next();

                         Crmproductdependency d = new Crmproductdependency();
                         d.setCrmproduct(pnew);
                         d.setCreateddate(new Date());
                         d.setCreatedfrom(EBIPGFactory.ebiUser);
                         d.setProductname(cdip.getProductname());
                         d.setProductnr(cdip.getProductnr());
               
                         productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(d);
                     }
                 }
                 
                 productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(pnew);
                 productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
                 
                 dataShow();
                 productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").
 					changeSelection(productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").
 						convertRowIndexToView(productPane.ebiModule.dynMethod.
 								getIdIndexFormArrayInATable(productPane.productModel.data,5 , pnew.getProductid())),0,false,false);
                 
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         
    }

    public void dataEdit(int id) {


        try {
            productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCursor(new Cursor(Cursor.WAIT_CURSOR));
            dataNew();

            Query query;
            query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();

            if (iter.hasNext()) {
                this.id = id;

                this.product = (Crmproduct) iter.next();
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(product);
                productPane.ebiModule.guiRenderer.getVisualPanel("Product").setID(product.getProductid());
                productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCreatedDate(productPane.ebiModule.ebiPGFactory.getDateToString(product.getCreateddate() == null ? new Date() : product.getCreateddate()));
                productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCreatedFrom(product.getCreatedfrom() == null ? EBIPGFactory.ebiUser : product.getCreatedfrom());

                if (product.getChangeddate() != null) {
                    productPane.ebiModule.guiRenderer.getVisualPanel("Product").setChangedDate(productPane.ebiModule.ebiPGFactory.getDateToString(product.getChangeddate()));
                    productPane.ebiModule.guiRenderer.getVisualPanel("Product").setChangedFrom(product.getChangedfrom());
                }
                productPane.ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").setText(product.getProductnr());
                productPane.ebiModule.guiRenderer.getTextfield("ProductNameText","Product").setText(product.getProductname());

                if(product.getCategory() != null){
                    productPane.ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setSelectedItem(product.getCategory());
                }

                if(product.getCategory() != null){
                    productPane.ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setSelectedItem(product.getCategory());
                }

                if(product.getType() != null){
                    productPane.ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").setSelectedItem(product.getType());
                }

                if(product.getTaxtype() != null){
                    productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").setSelectedItem(product.getTaxtype());
                }

                productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setValue(product.getPretax() == null ? 0 : product.getPretax());
                productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setValue(product.getNetamount() == null ? 0 : product.getNetamount());
                productPane.ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setValue(product.getSaleprice() == null ? 0 :  product.getSaleprice());
                productPane.ebiModule.guiRenderer.getTextarea("productDescription","Product").setText(product.getDescription() == null ? "" : product.getDescription());

                productPane.ebiModule.ebiPGFactory.getDataStore("Product","ebiEdit",true);
                dataShowDependency();
                dataShowDimension();
                dataShowDoc();

                productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").
					changeSelection(productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").
							convertRowIndexToView(productPane.ebiModule.dynMethod.
									getIdIndexFormArrayInATable(productPane.productModel.data, 5, id)),0,false,false);
                
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        productPane.isEdit = true;
        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void dataDelete(int id) {
        try {

            Query query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.iterate();

            if (iter.hasNext()) {
                Crmproduct prd = (Crmproduct) iter.next();
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(prd);

                productPane.ebiModule.ebiPGFactory.getDataStore("Product","ebiDelete",true);
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
            }
            dataNew();
            dataShow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void dataShow() {

        ResultSet set = null;

        try {

            int srow = productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").getSelectedRow();
            PreparedStatement ps = productPane.ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT PRODUCTID,PRODUCTNR,PRODUCTNAME,CATEGORY,TYPE,DESCRIPTION FROM CRMPRODUCT ORDER BY CREATEDDATE DESC");
            set = productPane.ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);

            if (set != null) {
                set.last();
                productPane.productModel.data = new Object[set.getRow()][6];

                if (set.getRow() > 0) {
                    set.beforeFirst();
                    int i = 0;
                    while (set.next()) {

                        productPane.productModel.data[i][0] = set.getString("PRODUCTNR") == null ? "" : set.getString("PRODUCTNR");
                        productPane.productModel.data[i][1] = set.getString("PRODUCTNAME") == null ? "" : set.getString("PRODUCTNAME");
                        productPane.productModel.data[i][2] = set.getString("CATEGORY") == null ? "" : set.getString("CATEGORY");
                        productPane.productModel.data[i][3] = set.getString("TYPE") == null ? "" : set.getString("TYPE");
                        productPane.productModel.data[i][4] = set.getString("DESCRIPTION") == null ? "" : set.getString("DESCRIPTION");
                        productPane.productModel.data[i][5] = set.getInt("PRODUCTID");
                        i++;
                    }

                } else {
                    productPane.productModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
                }

            } else {
                productPane.productModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};
            }
            productPane.ebiModule.guiRenderer.getTable("companyProductTable","Product").changeSelection(srow,0,false,false);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace(); 
                }
            }
            productPane.productModel.fireTableDataChanged();
        }

    }
    
    public void dataShowReport(int id) {
      try{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);
        productPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                productPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_PRODUCT")),
                getProductNamefromId(id));
      }catch (Exception ex){}
    }

    public void dataNew() {
      try{
        // Remove lock
        lockId = -1;
        lockModuleName = "";
        lockUser = "";
        lockStatus = 0;
        lockTime =  null;
        id=-1;

        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCreatedDate(productPane.ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setCreatedFrom(EBIPGFactory.ebiUser);
        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setChangedDate("");
        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setChangedFrom("");
        productPane.ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").setText("");
        productPane.ebiModule.guiRenderer.getTextfield("ProductNameText","Product").setText("");
        productPane.ebiModule.guiRenderer.getTextarea("productDescription","Product").setText("");
        productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setText("");
        productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setText("");
        productPane.ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setText("");
        productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setValue(null);
        productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setValue(null);
        productPane.ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setValue(null);
        productPane.ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setSelectedIndex(0);
        productPane.ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").setSelectedIndex(0);
        productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").setSelectedIndex(0);
        productPane.ebiModule.guiRenderer.getButton("deleteProperties","Product").setEnabled(false);
        productPane.ebiModule.guiRenderer.getButton("editProperties","Product").setEnabled(false);
        productPane.ebiModule.guiRenderer.getButton("deleteRelation","Product").setEnabled(false);
        product = new Crmproduct();
        productPane.ebiModule.guiRenderer.getVisualPanel("Product").setID(-1);
        productPane.ebiModule.ebiPGFactory.getDataStore("Product","ebiNew",true);
        dataShowDependency();
        dataShowDimension();
        dataShowDoc();
        dataShow();
      }catch (Exception ex){}

    }

    public void dataNewDoc() {

        try {

            File fs = productPane.ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);
            if (fs != null) {
                byte[] file = productPane.ebiModule.ebiPGFactory.readFileToByte(fs);
                if (file != null) {
                    Crmproductdocs docs = new Crmproductdocs();
    //			    java.sql.Blob blb = Hibernate.createBlob(file);
                    docs.setCrmproduct(product);
                    docs.setName(fs.getName());
                    docs.setCreateddate(new java.sql.Date(new java.util.Date().getTime()));
                    docs.setCreatedfrom(EBIPGFactory.ebiUser);
                    docs.setFiles(file);
                    product.getCrmproductdocs().add(docs);
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

            Iterator iter = product.getCrmproductdocs().iterator();
            while (iter.hasNext()) {

                Crmproductdocs doc = (Crmproductdocs) iter.next();

                if (id == doc.getProductdocid()) {
                    // Get the BLOB inputstream

                    String file = doc.getName().replaceAll(" ", "_");

//					byte buffer[] = doc.getFiles().getBytes(1,(int)adress.getFiles().length());
                    byte buffer[] = doc.getFiles();
                    FileName = "tmp/" + file;
                    FileType = file.substring(file.lastIndexOf("."));

                    fos = new FileOutputStream(FileName);

                    fos.write(buffer, 0, buffer.length);

                    fos.close();
                    productPane.ebiModule.ebiPGFactory.resolverType(FileName, FileType);
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
            if(product.getCrmproductdocs().size() > 0){
                productPane.tabModDoc.data = new Object[product.getCrmproductdocs().size()][4];

                Iterator itr = product.getCrmproductdocs().iterator();
                int i = 0;
                while (itr.hasNext()) {

                    Crmproductdocs obj = (Crmproductdocs) itr.next();

                    productPane.tabModDoc.data[i][0] = obj.getName() == null ? "" : obj.getName();
                    productPane.tabModDoc.data[i][1] = productPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate()) == null ? "" : productPane.ebiModule.ebiPGFactory.getDateToString(obj.getCreateddate());
                    productPane.tabModDoc.data[i][2] = obj.getCreatedfrom() == null ? "" : obj.getCreatedfrom();
                    if(obj.getProductdocid() == null || obj.getProductdocid() < 0){ obj.setProductdocid(((i + 1) * (-1)));}
                    productPane.tabModDoc.data[i][3] = obj.getProductdocid();
                    i++;
                }
            }else{
                productPane.tabModDoc.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
            }
            productPane.tabModDoc.fireTableDataChanged();
      }catch (Exception ex){}
    }


    public void dataDeleteDoc(int id) {

        try {

            Iterator iter = product.getCrmproductdocs().iterator();
            while (iter.hasNext()) {

                Crmproductdocs doc = (Crmproductdocs) iter.next();

                if (doc.getProductdocid() == id) {
                    if(id >= 0){
                        try {
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    product.getCrmproductdocs().remove(doc);
                    this.dataShowDoc();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void dataNewDependency() {
        
        try {
            EBICRMDialogSearchProduct prod = new EBICRMDialogSearchProduct(productPane.ebiModule, product.getCrmproductdependencies());
            prod.setVisible();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDeleteDependency(int id) {
        
        try {
            Iterator iter = product.getCrmproductdependencies().iterator();

            while (iter.hasNext()) {

                this.dependency = (Crmproductdependency) iter.next();

                if (id == this.dependency.getDependencyid()){
                      if(id >=0){
                        try {
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(this.dependency);
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      }
                    this.product.getCrmproductdependencies().remove(this.dependency);
                    dataShowDependency();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataShowDependency() {
      try{
          if(product.getCrmproductdependencies().size() > 0 ){
            productPane.productDependencyModel.data = new Object[product.getCrmproductdependencies().size()][3];
            Iterator iter = product.getCrmproductdependencies().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Crmproductdependency dip = (Crmproductdependency) iter.next();

                productPane.productDependencyModel.data[i][0] = dip.getProductnr();
                productPane.productDependencyModel.data[i][1] = dip.getProductname();
                if(dip.getDependencyid() == null || dip.getDependencyid() < 0){ dip.setDependencyid(((i + 1) * (-1)));}
                productPane.productDependencyModel.data[i][2] = dip.getDependencyid();
                i++;
            }
          }else{
             productPane.productDependencyModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
          }
            productPane.productDependencyModel.fireTableDataChanged();
      }catch (Exception ex){}
    }

    public void dataNewDimension() {
        
        try {

            EBIDialogProperties dim = new EBIDialogProperties(productPane, product.getCrmproductdimensions(), null);
            dim.setVisible();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataEditDimension(int id) {
        
        try {

            Iterator iter = product.getCrmproductdimensions().iterator();
            while (iter.hasNext()) {

                this.dimension = (Crmproductdimension) iter.next();

                if (id == dimension.getDimensionid()) {
                    EBIDialogProperties dim = new EBIDialogProperties(productPane, product.getCrmproductdimensions(), dimension);
                    dim.setVisible();
                    if (!dim.cancel) {
                        dataShowDimension();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataDeleteDimension(int id) {

        try {
            Iterator iter = product.getCrmproductdimensions().iterator();
            while (iter.hasNext()) {

                this.dimension = (Crmproductdimension) iter.next();

                if (id == dimension.getDimensionid()) {
                      if(id >= 0){
                        try {
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(dimension);
                            productPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                      }
                    this.product.getCrmproductdimensions().remove(dimension);
                    dataShowDimension();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataShowDimension() {
      try{
          if(this.product.getCrmproductdimensions().size() > 0){
            productPane.productModelDimension.data = new Object[this.product.getCrmproductdimensions().size()][3];
            Iterator iter = this.product.getCrmproductdimensions().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Crmproductdimension dim = (Crmproductdimension) iter.next();

                productPane.productModelDimension.data[i][0] = dim.getName();
                productPane.productModelDimension.data[i][1] = dim.getValue();
                if(dim.getDimensionid() == null || dim.getDimensionid() < 0){ dim.setDimensionid(((i + 1) *(-1)));}
                productPane.productModelDimension.data[i][2] = dim.getDimensionid();

                i++;
            }
          }else{
            productPane.productModelDimension.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
          }
           productPane.productModelDimension.fireTableDataChanged();
      }catch (Exception ex){}
    }


    public void calculateClearPrice() {
        Query query;

        try {

            query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").
                    createQuery("from Companyproducttax where name=? ").setString(0,
                    productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString());


            Iterator it = query.iterate();

            if (it.hasNext()) {

                Companyproducttax tax = (Companyproducttax) it.next();
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(tax);
                double pre = new Double(productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").getValue().toString());

                double mwst = (tax.getTaxvalue() / 100 ) + 1.0;

                double clear = (pre / mwst);

                BigDecimal bd = new BigDecimal(clear);
                BigDecimal bd_round = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setValue(new Double(bd_round.doubleValue()));

            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculatePreTaxPrice() {

        Query query;
        try {
            query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").
                    createQuery("from Companyproducttax where name=? ").setString(0,
                    productPane.ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString());

                    Iterator it = query.iterate();

                    if (it.hasNext()) {

                        Companyproducttax tax = (Companyproducttax) it.next();
                        productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(tax);
                        double clear = new Double(productPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").getValue().toString());

                        double pre = (clear + ((clear * tax.getTaxvalue()) / 100));

                        BigDecimal bd = new BigDecimal(pre);
                        BigDecimal bd_round = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
                        productPane.ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setValue(new Double(bd_round.doubleValue()));
                    }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Crmproductdependency> getDependencyList() {
        return product.getCrmproductdependencies();
    }

    public Set<Crmproductdimension> getDimensionList() {
        return product.getCrmproductdimensions();
    }

    public Crmproduct getProduct() {
        return this.product;
    }

    public void setProduct(Crmproduct product) {
        this.product = product;
    }

    public Crmproductdependency getDependency() {
        return dependency;
    }

    public void setDependency(Crmproductdependency dependency) {
        this.dependency = dependency;
    }

    public Crmproductdimension getDimension() {
        return dimension;
    }

    private String getProductNamefromId(int id) {

        String name = "";
        try{
            Query query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                        "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.iterate();

            while (iter.hasNext()) {
                Crmproduct pr = (Crmproduct) iter.next();
                productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(pr);
                if (pr.getProductid() == id) {
                    name = pr.getProductname();
                    break;
                }
            }
        }catch(Exception ex){ ex.printStackTrace();}
        return name;
    }

    public boolean existProduct(String ProductNr) {

        boolean exist = false;
        try{
            Query query = productPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                        "from Crmproduct where productnr=? ").setString(0, ProductNr);

            if(query.list().size() > 0){
               exist = true; 
            }
        }catch(Exception ex){ ex.printStackTrace();}
        return exist;
    }


        /**
     * Check if a loaded record is locked
     * @param compNr
     * @param showMessage
     * @throws Exception
     */

}