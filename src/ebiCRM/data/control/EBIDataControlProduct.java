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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
    private Companyproducttax tax=null;
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
            productPane.mod.ebiContainer.showInActionStatus("Product",true);
            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
            if (!isEdit) {
                this.product.setCreateddate(new Date());
                this.product.setCreatedfrom(EBIPGFactory.ebiUser);
                productPane.isEdit = true;
            } else {
                this.product.setChangeddate(new Date());
                this.product.setChangedfrom(EBIPGFactory.ebiUser);
            }

            product.setProductnr(productPane.mod.gui.getTextfield("ProductNrTex","Product").getText());
            product.setProductname(productPane.mod.gui.getTextfield("ProductNameText","Product").getText());

            if(productPane.mod.gui.getComboBox("ProductCategoryText","Product").getSelectedItem() != null){
               product.setCategory(productPane.mod.gui.getComboBox("ProductCategoryText","Product").getSelectedItem().toString());
            }
            if(productPane.mod.gui.getComboBox("ProductTypeText","Product").getSelectedItem() != null){
                product.setType(productPane.mod.gui.getComboBox("ProductTypeText","Product").getSelectedItem().toString());
            }

            if(productPane.mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem() != null){
                product.setTaxtype(productPane.mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString());
            }

            product.setPretax(Double.parseDouble(productPane.mod.gui.getFormattedTextfield("productGrossText","Product").getValue() == null ? "0.0" : productPane.mod.gui.getFormattedTextfield("productGrossText","Product").getValue().toString()));
            product.setNetamount(Double.parseDouble(productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").getValue() == null ? "0.0" : productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").getValue().toString()));
            product.setSaleprice(Double.parseDouble(productPane.mod.gui.getFormattedTextfield("salePriceText","Product").getValue() == null ? "0.0" : productPane.mod.gui.getFormattedTextfield("salePriceText","Product").getValue().toString()));

            product.setDescription(productPane.mod.gui.getTextarea("productDescription","Product").getText());


            if(!product.getCrmproductdimensions().isEmpty()){
                Iterator itdim = product.getCrmproductdimensions().iterator();
                while(itdim.hasNext()){
                    Crmproductdimension dimx = (Crmproductdimension)itdim.next();
                    dimx.setCrmproduct(product);
                    if(dimx.getDimensionid() < 0){ dimx.setDimensionid(null);}
                    productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(dimx);
                }
            }
            
            if(!product.getCrmproductdependencies().isEmpty()){
                Iterator itdip = product.getCrmproductdependencies().iterator();
                while(itdip.hasNext()){
                    Crmproductdependency cdip = (Crmproductdependency) itdip.next();
                    cdip.setCrmproduct(product);
                    if(cdip.getDependencyid() < 0){ cdip.setDependencyid(null);}
                    productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(cdip);
                }
            }

            if(!product.getCrmproductdocs().isEmpty()){
                Iterator itdip = product.getCrmproductdocs().iterator();
                while(itdip.hasNext()){
                    Crmproductdocs cdocs = (Crmproductdocs) itdip.next();
                    cdocs.setCrmproduct(product);
                    if(cdocs.getProductdocid() < 0){ cdocs.setProductdocid(null);}
                    productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(cdocs);
                }
            }

            productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(product);
            productPane.mod.system.getDataStore("Product","ebiSave",true);
            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
            
            if(!isEdit){
            	productPane.mod.gui.getVisualPanel("Product").setID(product.getProductid());
            }
            dataNew();
            dataShow();
            productPane.mod.ebiContainer.showInActionStatus("Product", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
    
    public void dataCopy(int id){
    	 Query query;
         try {

             productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();

             query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                     "from Crmproduct where productid=? ").setInteger(0, id);

             Iterator iter = query.list().iterator();

             if (iter.hasNext()) {

            	 Crmproduct pro = (Crmproduct) iter.next();
            	 productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(pro);
            	 
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
                         
                         productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(nd);
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
               
                         productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(d);
                     }
                 }
                 
                 productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").saveOrUpdate(pnew);
                 productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
                 
                 dataShow();

                 
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         
    }

    public void dataEdit(int id) {


        try {
            productPane.mod.gui.getVisualPanel("Product").setCursor(new Cursor(Cursor.WAIT_CURSOR));
            dataNew();

            Query query;
            query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();

            if (iter.hasNext()) {
                this.id = id;

                this.product = (Crmproduct) iter.next();
                productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(product);
                productPane.mod.gui.getVisualPanel("Product").setID(product.getProductid());
                productPane.mod.gui.getVisualPanel("Product").setCreatedDate(productPane.mod.system.getDateToString(product.getCreateddate() == null ? new Date() : product.getCreateddate()));
                productPane.mod.gui.getVisualPanel("Product").setCreatedFrom(product.getCreatedfrom() == null ? EBIPGFactory.ebiUser : product.getCreatedfrom());

                if (product.getChangeddate() != null) {
                    productPane.mod.gui.getVisualPanel("Product").setChangedDate(productPane.mod.system.getDateToString(product.getChangeddate()));
                    productPane.mod.gui.getVisualPanel("Product").setChangedFrom(product.getChangedfrom());
                }
                productPane.mod.gui.getTextfield("ProductNrTex","Product").setText(product.getProductnr());
                productPane.mod.gui.getTextfield("ProductNameText","Product").setText(product.getProductname());

                if(product.getCategory() != null){
                    productPane.mod.gui.getComboBox("ProductCategoryText","Product").setSelectedItem(product.getCategory());
                }

                if(product.getCategory() != null){
                    productPane.mod.gui.getComboBox("ProductCategoryText","Product").setSelectedItem(product.getCategory());
                }

                if(product.getType() != null){
                    productPane.mod.gui.getComboBox("ProductTypeText","Product").setSelectedItem(product.getType());
                }

                if(product.getTaxtype() != null){
                    productPane.mod.gui.getComboBox("productTaxTypeTex","Product").setSelectedItem(product.getTaxtype());
                }

                productPane.mod.gui.getFormattedTextfield("productGrossText","Product").setValue(product.getPretax() == null ? 0 : product.getPretax());
                productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").setValue(product.getNetamount() == null ? 0 : product.getNetamount());
                productPane.mod.gui.getFormattedTextfield("salePriceText","Product").setValue(product.getSaleprice() == null ? 0 :  product.getSaleprice());
                productPane.mod.gui.getTextarea("productDescription","Product").setText(product.getDescription() == null ? "" : product.getDescription());

                productPane.mod.system.getDataStore("Product","ebiEdit",true);
                dataShowDependency();
                dataShowDimension();
                dataShowDoc();
                
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        productPane.isEdit = true;
        productPane.mod.gui.getVisualPanel("Product").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    public void dataDelete(int id) {
        try {

            Query query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.iterate();

            if (iter.hasNext()) {
                Crmproduct prd = (Crmproduct) iter.next();
                productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(prd);

                productPane.mod.system.getDataStore("Product","ebiDelete",true);
                productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
            }
            dataNew();
            dataShow();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void dataShow() {
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {
                productPane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ResultSet set = null;

                        try {

                            PreparedStatement ps = productPane.mod.system.getIEBIDatabase().initPreparedStatement("SELECT PRODUCTID,PRODUCTNR,PRODUCTNAME,CATEGORY,TYPE,DESCRIPTION FROM CRMPRODUCT ORDER BY CREATEDDATE DESC");
                            set = productPane.mod.system.getIEBIDatabase().executePreparedQuery(ps);

                            if (set != null) {
                                set.last();
                                productPane.productModel.data = new Object[set.getRow()][6];

                                if(set.getRow() > 0){
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
                            productPane.mod.system.getMainFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                });
            }
        });

        thr.start();

    }
    
    public void dataShowReport(int id) {
      try{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);
        productPane.mod.system.getIEBIReportSystemInstance().
                useReportSystem(map,
                productPane.mod.system.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_C_PRODUCT")),
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

        productPane.mod.gui.getVisualPanel("Product").setCreatedDate(productPane.mod.system.getDateToString(new java.util.Date()));
        productPane.mod.gui.getVisualPanel("Product").setCreatedFrom(EBIPGFactory.ebiUser);
        productPane.mod.gui.getVisualPanel("Product").setChangedDate("");
        productPane.mod.gui.getVisualPanel("Product").setChangedFrom("");
        productPane.mod.gui.getTextfield("ProductNrTex","Product").setText("");
        productPane.mod.gui.getTextfield("ProductNameText","Product").setText("");
        productPane.mod.gui.getTextarea("productDescription","Product").setText("");
        productPane.mod.gui.getFormattedTextfield("productGrossText","Product").setText("");
        productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").setText("");
        productPane.mod.gui.getFormattedTextfield("salePriceText","Product").setText("");
        productPane.mod.gui.getFormattedTextfield("productGrossText","Product").setValue(null);
        productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").setValue(null);
        productPane.mod.gui.getFormattedTextfield("salePriceText","Product").setValue(null);
        productPane.mod.gui.getComboBox("ProductCategoryText","Product").setSelectedIndex(0);
        productPane.mod.gui.getComboBox("ProductTypeText","Product").setSelectedIndex(0);
        productPane.mod.gui.getComboBox("productTaxTypeTex","Product").setSelectedIndex(0);
        productPane.mod.gui.getButton("deleteProperties","Product").setEnabled(false);
        productPane.mod.gui.getButton("editProperties","Product").setEnabled(false);
        productPane.mod.gui.getButton("deleteRelation","Product").setEnabled(false);
        product = new Crmproduct();
        productPane.mod.gui.getVisualPanel("Product").setID(-1);
        productPane.mod.system.getDataStore("Product","ebiNew",true);
        dataShowDependency();
        dataShowDimension();
        dataShowDoc();
      }catch (Exception ex){}

    }

    public void dataNewDoc() {

        try {

            File fs = productPane.mod.system.getOpenDialog(JFileChooser.FILES_ONLY);
            if (fs != null) {
                byte[] file = productPane.mod.system.readFileToByte(fs);
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
                    productPane.mod.system.resolverType(FileName, FileType);
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
                    productPane.tabModDoc.data[i][1] = productPane.mod.system.getDateToString(obj.getCreateddate()) == null ? "" : productPane.mod.system.getDateToString(obj.getCreateddate());
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
                            productPane.mod.system.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                            productPane.mod.system.hibernate.getHibernateSession("EBICRM_SESSION").delete(doc);
                            productPane.mod.system.hibernate.getHibernateTransaction("EBICRM_SESSION").commit();
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
            EBICRMDialogSearchProduct prod = new EBICRMDialogSearchProduct(productPane.mod, product.getCrmproductdependencies());
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
                            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                            productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(this.dependency);
                            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
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
                            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
                            productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").delete(dimension);
                            productPane.mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").commit();
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

        try {

            if(tax == null || !productPane.mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString().equals(tax.getName())){
                final Query query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").
                        createQuery("from Companyproducttax where name=? ").setString(0,
                        productPane.mod.gui.getComboBox("productTaxTypeTex", "Product").getSelectedItem().toString());

                Iterator it = query.iterate();
                if (it.hasNext()){
                    tax = (Companyproducttax)it.next();
                    productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(tax);
                }
            }

            double pre=0.0;
            if(!"".equals(productPane.mod.gui.getFormattedTextfield("productGrossText","Product").getText())){
                productPane.mod.gui.getFormattedTextfield("productGrossText","Product").commitEdit();
                pre = Double.parseDouble(productPane.mod.gui.getFormattedTextfield("productGrossText","Product").getValue().toString());
            }

            double clear = pre - (pre * tax.getTaxvalue() / 100 );
            BigDecimal bd = new BigDecimal(clear);
            BigDecimal bd_round = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").setValue(new Double(bd_round.doubleValue()));

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculatePreTaxPrice() {

        try {

            if(tax == null || !productPane.mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString().equals(tax.getName())) {
                final Query query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").
                        createQuery("from Companyproducttax where name=? ").setString(0,
                        productPane.mod.gui.getComboBox("productTaxTypeTex", "Product").getSelectedItem().toString());

                Iterator it = query.iterate();
                if(it.hasNext()){
                    tax = (Companyproducttax)it.next();
                    productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(tax);
                }
            }
            double clear =0.0;
            if(!"".equals(productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").getText())){
                productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").commitEdit();
                clear = Double.parseDouble(productPane.mod.gui.getFormattedTextfield("productNetamoutText","Product").getValue().toString());
            }

            double pre = (clear + ((clear * tax.getTaxvalue()) / 100));

            BigDecimal bd = new BigDecimal(pre);
            BigDecimal bd_round = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
            productPane.mod.gui.getFormattedTextfield("productGrossText","Product").setValue(new Double(bd_round.doubleValue()));
            
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
            Query query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
                        "from Crmproduct where productid=? ").setInteger(0, id);

            Iterator iter = query.iterate();

            while (iter.hasNext()) {
                Crmproduct pr = (Crmproduct) iter.next();
                productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").refresh(pr);
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
            Query query = productPane.mod.system.hibernate.getHibernateSession("EBIPRODUCT_SESSION").createQuery(
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