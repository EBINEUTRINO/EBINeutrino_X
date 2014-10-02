package ebiCRM.gui.panels;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import ebiCRM.table.models.MyTableModelDoc;
import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlProduct;
import ebiCRM.table.models.MyTableModelProduct;
import ebiCRM.table.models.MyTableModelProductDependency;
import ebiCRM.table.models.MyTableModelProperties;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMProduct  {

    public EBICRMModule ebiModule = null;
    public MyTableModelProductDependency productDependencyModel = null;
    public MyTableModelProduct productModel = null;
    public MyTableModelProperties productModelDimension = null;
    public MyTableModelDoc tabModDoc = null;
    public boolean isEdit = false;
    public static String[] category = null;
    public static String[] type = null;
    public static String[] taxType = null;
    public EBIDataControlProduct dataControlProduct = null;
    private int selectedProductRow = -1;
    private int selectedDimensionRow = -1;
    private int selectedDependencyRow = -1;
    private int selectedDocRow = -1;


    public EBICRMProduct(EBICRMModule module) {
        ebiModule = module;

        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBIPRODUCT_SESSION");
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        productDependencyModel = new MyTableModelProductDependency();
        productModel = new MyTableModelProduct();
        productModelDimension = new MyTableModelProperties();
        tabModDoc = new MyTableModelDoc();
        dataControlProduct = new EBIDataControlProduct(this);
        showProduct();

    }


    public void initializeAction(){


        ebiModule.guiRenderer.getLabel("filterTable","Product").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Product").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyProductTable","Product").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Product").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyProductTable","Product").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Product").getText()));
            }
        });


        ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").setModel(new DefaultComboBoxModel(category));
        ebiModule.guiRenderer.getComboBox("ProductTypeText","Product").setModel(new DefaultComboBoxModel(type));

        /***************************************************************************/
        // TAX PANEL
        /***************************************************************************/

        ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").setModel(new DefaultComboBoxModel(taxType));
        ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString())) {
                        ebiModule.guiRenderer.getButton("calcClear","Product").setEnabled(true);
                        ebiModule.guiRenderer.getButton("calcGross","Product").setEnabled(true);
                    }
                }
        });

         ebiModule.guiRenderer.getButton("calcClear","Product").setEnabled(false);
         ebiModule.guiRenderer.getButton("calcClear","Product").setIcon(EBIConstant.ICON_UP);
         ebiModule.guiRenderer.getButton("calcClear","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!"".equals(ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").getText())) {
                        dataControlProduct.calculatePreTaxPrice();
                    }
                }
            });


         ebiModule.guiRenderer.getButton("calcGross","Product").setIcon(EBIConstant.ICON_DOWN);
         ebiModule.guiRenderer.getButton("calcGross","Product").setEnabled(false);
         ebiModule.guiRenderer.getButton("calcGross","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!"".equals(ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").getText())) {
                        dataControlProduct.calculateClearPrice();
                    }
                }
         });

         NumberFormat taxFormat=NumberFormat.getNumberInstance();
         taxFormat.setMinimumFractionDigits(2);
         taxFormat.setMaximumFractionDigits(3);

         ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));

         ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
         ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));


        /**************************************************************************************/
        //  PRODUCT TABLE PROPERTIES
        /**************************************************************************************/

        ebiModule.guiRenderer.getTable("ProductPropertiesTable","Product").setModel(productModelDimension);
        ebiModule.guiRenderer.getTable("ProductPropertiesTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("ProductPropertiesTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDimensionRow = ebiModule.guiRenderer.getTable("ProductPropertiesTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteProperties","Product").setEnabled(false);
                        ebiModule.guiRenderer.getButton("editProperties","Product").setEnabled(false);
                    } else if (!productModelDimension.data[selectedDimensionRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteProperties","Product").setEnabled(true);
                        ebiModule.guiRenderer.getButton("editProperties","Product").setEnabled(true);
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newProperties","Product").setIcon(EBIConstant.ICON_SEARCH);
            ebiModule.guiRenderer.getButton("newProperties","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDimension();
                }
            });


            ebiModule.guiRenderer.getButton("deleteProperties","Product").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteProperties","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteProperties","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDimensionRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModelDimension.data[selectedDimensionRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDimension(Integer.parseInt(productModelDimension.data[selectedDimensionRow][2].toString()));
                    }
                }
            });

            ebiModule.guiRenderer.getButton("editProperties","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("editProperties","Product").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editProperties","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDimensionRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModelDimension.data[selectedDimensionRow][0].toString())) {
                        return;
                    }

                    editDimension(Integer.parseInt(productModelDimension.data[selectedDimensionRow][2].toString()));

                }
            });

          /**************************************************************************************/
          //  PRODUCT TABLE RELATION
          /**************************************************************************************/

            ebiModule.guiRenderer.getTable("ProductRelationTable","Product").setModel(productDependencyModel);
            ebiModule.guiRenderer.getTable("ProductRelationTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("ProductRelationTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDependencyRow = ebiModule.guiRenderer.getTable("ProductRelationTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteRelation","Product").setEnabled(false);
                    } else if (!productDependencyModel.data[selectedDependencyRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteRelation","Product").setEnabled(true);
                    }
                }
            });

           ebiModule.guiRenderer.getButton("newRelation","Product").setIcon(EBIConstant.ICON_SEARCH);
           ebiModule.guiRenderer.getButton("newRelation","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDependency();
                }
            });

            ebiModule.guiRenderer.getButton("deleteRelation","Product").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteRelation","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteRelation","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedDependencyRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productDependencyModel.data[selectedDependencyRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDependency(Integer.parseInt(productDependencyModel.data[selectedDependencyRow][2].toString()));
                    }

                }
            });

            /**************************************************************************************/
            //  PRODUCT DOCUMENTS
            /**************************************************************************************/

                ebiModule.guiRenderer.getTable("productDoc","Product").setModel(tabModDoc);
                ebiModule.guiRenderer.getTable("productDoc","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                ebiModule.guiRenderer.getTable("productDoc","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                        if(lsm.getMinSelectionIndex() != -1){
                            selectedDocRow = ebiModule.guiRenderer.getTable("productDoc","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                        }

                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("showProductDoc","Product").setEnabled(false);
                            ebiModule.guiRenderer.getButton("deleteProductDoc","Product").setEnabled(false);
                        } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("showProductDoc","Product").setEnabled(true);
                            ebiModule.guiRenderer.getButton("deleteProductDoc","Product").setEnabled(true);
                        }
                    }
                });

                ebiModule.guiRenderer.getButton("newProductDoc","Product").setIcon(EBIConstant.ICON_NEW);
                ebiModule.guiRenderer.getButton("newProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        dataControlProduct.dataNewDoc();
                    }
                });

                ebiModule.guiRenderer.getButton("showProductDoc","Product").setIcon(EBIConstant.ICON_EXPORT);
                ebiModule.guiRenderer.getButton("showProductDoc","Product").setEnabled(false);
                ebiModule.guiRenderer.getButton("showProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModDoc.data[selectedDocRow][0].toString())) {
                            return;
                        }

                        dataControlProduct.dataViewDoc(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                    }
                });

                ebiModule.guiRenderer.getButton("deleteProductDoc","Product").setIcon(EBIConstant.ICON_DELETE);
                ebiModule.guiRenderer.getButton("deleteProductDoc","Product").setEnabled(false);
                ebiModule.guiRenderer.getButton("deleteProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModDoc.data[selectedDocRow][0].toString())) {
                            return;
                        }
                        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                            dataControlProduct.dataDeleteDoc(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                        }

                    }
                });


            /**************************************************************************************/
            //  PRODUCT AVAILABLE TABLE
            /**************************************************************************************/

            ebiModule.guiRenderer.getTable("companyProductTable","Product").setModel(productModel);
            ebiModule.guiRenderer.getTable("companyProductTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("companyProductTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                     try{
                        selectedProductRow = ebiModule.guiRenderer.getTable("companyProductTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                     }catch(IndexOutOfBoundsException ex){}
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editProduct","Product").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteProduct","Product").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyProduct","Product").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportProduct","Product").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyProduct","Product").setEnabled(false);
                    } else if (!productModel.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editProduct","Product").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteProduct","Product").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportProduct","Product").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyProduct","Product").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyProductTable","Product")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedProductRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedProductRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedProductRow = selRow;


                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                    ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").grabFocus();
                }
            });

            ebiModule.guiRenderer.getTable("companyProductTable","Product").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    
                    if(ebiModule.guiRenderer.getTable("companyProductTable","Product").rowAtPoint(e.getPoint()) != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("companyProductTable","Product").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyProductTable","Product").rowAtPoint(e.getPoint()));
                    }

                    if (e.getClickCount() == 2) {

                        if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(productModel.data[selectedProductRow][0].toString())) {
                            return;
                        }

                        editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                        ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").grabFocus();

                    }
                }
            });

            ebiModule.guiRenderer.getButton("newProduct","Product").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
            });
            
            
            ebiModule.guiRenderer.getButton("copyProduct","Product").setIcon(EBIConstant.ICON_COPY);
            ebiModule.guiRenderer.getButton("copyProduct","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("copyProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }
                    
                    dataControlProduct.dataCopy(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("editProduct","Product").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editProduct","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("editProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                    ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").grabFocus();
                }
            });


             ebiModule.guiRenderer.getButton("deleteProduct","Product").setIcon(EBIConstant.ICON_DELETE);
             ebiModule.guiRenderer.getButton("deleteProduct","Product").setEnabled(false);
             ebiModule.guiRenderer.getButton("deleteProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }
                    ebiDelete();

                }
            });

            ebiModule.guiRenderer.getButton("reportProduct","Product").setEnabled(false);
            ebiModule.guiRenderer.getButton("reportProduct","Product").setIcon(EBIConstant.ICON_REPORT);
            ebiModule.guiRenderer.getButton("reportProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    showProductReport(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));

                }
            });

            ebiModule.guiRenderer.getButton("saveProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
               ebiSave();
                }
            });

    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {
           ebiModule.guiRenderer.getVisualPanel("Product").setCreatedFrom(EBIPGFactory.ebiUser);
           ebiModule.guiRenderer.getVisualPanel("Product").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));

           ebiModule.guiRenderer.getVisualPanel("Product").setChangedFrom("");
           ebiModule.guiRenderer.getVisualPanel("Product").setChangedDate("");

           ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").setText("");
           ebiModule.guiRenderer.getTextfield("ProductNameText","Product").setText("");
           ebiModule.guiRenderer.getTextarea("productDescription","Product").setText("");

           ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setText("");
           ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setText("");
           ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setText("");

           ebiModule.guiRenderer.getFormattedTextfield("productGrossText","Product").setValue(null);
           ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setValue(null);
           ebiModule.guiRenderer.getFormattedTextfield("salePriceText","Product").setValue(null);

    }

    /***************************************************************************
     * * 
     **************************************************************************/

    private void newProduct() {
        dataControlProduct.dataNew();
        isEdit = false;
    }

    public void saveProduct() {
        if (validateInput()) {
          final Runnable  run = new Runnable(){
                public void run(){
                  try{
                        int row =0;
                        if(isEdit){
                            row = ebiModule.guiRenderer.getTable("companyProductTable","Product").getSelectedRow();
                        }
                        dataControlProduct.dataStore(isEdit);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {}
                        ebiModule.guiRenderer.getTable("companyProductTable","Product").changeSelection(row,0,false,false);
                  }catch(Exception ex){}
                }
          };

          Thread save = new Thread(run,"Save Product");
          save.start();
        }
    }

    private void editProduct(int id) {
        dataControlProduct.dataEdit(id);
        isEdit = true;
    }

    private void deleteProduct(int id) {
        dataControlProduct.dataDelete(id);
        newProduct();
    }

    private void showProduct() {
        dataControlProduct.dataShow();
    }

    private boolean validateInput() {

        if ("".equals(ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("ProductNameText","Product").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_PRODUCT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if(ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").getSelectedItem() != null){
            if (EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(
                    ebiModule.guiRenderer.getComboBox("ProductCategoryText","Product").getSelectedItem().toString())) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SELECT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
        }

        if(!isEdit){
            if(dataControlProduct.existProduct(ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").getText())){
               EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
               return false;
            }
        }
        
        return true;
    }

    /***************************************************************************
     * Product Dependency manipulation
     **************************************************************************/
    
    private void newDependency() {
        dataControlProduct.dataNewDependency();
    }

    private void deleteDependency(int id) {
        dataControlProduct.dataDeleteDependency(id);
    }

    public void showDependency() {
        dataControlProduct.dataShowDependency();
    }

    /***************************************************************************
     * Product Dimension manipulation
     **************************************************************************/
    
    private void newDimension() {
        dataControlProduct.dataNewDimension();
    }

    private void editDimension(int id) {
        dataControlProduct.dataEditDimension(id);
    }

    private void deleteDimension(int id) {
        dataControlProduct.dataDeleteDimension(id);
    }

    private void showProductReport(int id) {
        dataControlProduct.dataShowReport(id);
    }

    public void ebiNew(){
        newProduct();
    }

    public void ebiSave(){
        saveProduct();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
        }
    }
    
}