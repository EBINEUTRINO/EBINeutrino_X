package ebiCRM.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlProduct;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMProduct  {

    public EBICRMModule mod = null;
    public EBIAbstractTableModel productDependencyModel = null;
    public EBIAbstractTableModel productModel = null;
    public EBIAbstractTableModel productModelDimension = null;
    public EBIAbstractTableModel tabModDoc = null;
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
        mod = module;

        try {
            mod.system.hibernate.openHibernateSession("EBIPRODUCT_SESSION");
            mod.system.hibernate.getHibernateTransaction("EBIPRODUCT_SESSION").begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataControlProduct = new EBIDataControlProduct(this);
    }


    public void initializeAction(){
        mod.gui.getLabel("filterTable","Product").setHorizontalAlignment(SwingUtilities.RIGHT);
        mod.gui.getTextfield("filterTableText","Product").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                mod.gui.getTable("companyProductTable","Product").setRowFilter(RowFilters.regexFilter("(?i)"+ mod.gui.getTextfield("filterTableText","Product").getText()));
            }
            public void keyReleased(KeyEvent e){
                mod.gui.getTable("companyProductTable","Product").setRowFilter(RowFilters.regexFilter("(?i)"+ mod.gui.getTextfield("filterTableText","Product").getText()));
            }
        });

        mod.gui.getComboBox("ProductCategoryText","Product").setModel(new DefaultComboBoxModel(category));
        mod.gui.getComboBox("ProductTypeText","Product").setModel(new DefaultComboBoxModel(type));
        /***************************************************************************/
        // TAX PANEL
        /***************************************************************************/

        mod.gui.getComboBox("productTaxTypeTex","Product").setModel(new DefaultComboBoxModel(taxType));
        mod.gui.getComboBox("productTaxTypeTex","Product").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!"".equals(mod.gui.getFormattedTextfield("productGrossText","Product").getText())) {
                        mod.gui.getFormattedTextfield("productGrossText", "Product").commitEdit();
                        dataControlProduct.calculateClearPrice();
                    }else if (!"".equals(mod.gui.getFormattedTextfield("productNetamoutText","Product").getText())) {
                        mod.gui.getFormattedTextfield("productNetamoutText","Product").commitEdit();
                        dataControlProduct.calculatePreTaxPrice();
                    }

                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });

        mod.gui.getFormattedTextfield("productGrossText", "Product").addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString())) {
                    dataControlProduct.calculateClearPrice();
                }
            }
        });
        mod.gui.getFormattedTextfield("productNetamoutText","Product").addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(mod.gui.getComboBox("productTaxTypeTex","Product").getSelectedItem().toString())) {
                     dataControlProduct.calculatePreTaxPrice();
                }
            }
        });

        NumberFormat taxFormat=NumberFormat.getNumberInstance();
        taxFormat.setMinimumFractionDigits(2);
        taxFormat.setMaximumFractionDigits(3);

        mod.gui.getFormattedTextfield("productGrossText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("productNetamoutText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("salePriceText","Product").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));

        /**************************************************************************************/
        //  PRODUCT TABLE PROPERTIES
        /**************************************************************************************/
        mod.gui.getTable("ProductPropertiesTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mod.gui.getTable("ProductPropertiesTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDimensionRow = mod.gui.getTable("ProductPropertiesTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        mod.gui.getButton("deleteProperties","Product").setEnabled(false);
                        mod.gui.getButton("editProperties","Product").setEnabled(false);
                    } else if (!productModelDimension.data[selectedDimensionRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        mod.gui.getButton("deleteProperties","Product").setEnabled(true);
                        mod.gui.getButton("editProperties","Product").setEnabled(true);
                    }
                }
        });

        mod.gui.getButton("newProperties","Product").setIcon(EBIConstant.ICON_SEARCH);
        mod.gui.getButton("newProperties","Product").addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDimension();
                }
        });

        mod.gui.getButton("deleteProperties","Product").setIcon(EBIConstant.ICON_DELETE);
        mod.gui.getButton("deleteProperties","Product").setEnabled(false);
        mod.gui.getButton("deleteProperties","Product").addActionListener(new java.awt.event.ActionListener() {

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

        mod.gui.getButton("editProperties","Product").setEnabled(false);
        mod.gui.getButton("editProperties","Product").setIcon(EBIConstant.ICON_EDIT);
        mod.gui.getButton("editProperties","Product").addActionListener(new java.awt.event.ActionListener() {
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
            mod.gui.getTable("ProductRelationTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            mod.gui.getTable("ProductRelationTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDependencyRow = mod.gui.getTable("ProductRelationTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        mod.gui.getButton("deleteRelation","Product").setEnabled(false);
                    } else if (!productDependencyModel.data[selectedDependencyRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        mod.gui.getButton("deleteRelation","Product").setEnabled(true);
                    }
                }
            });

           mod.gui.getButton("newRelation","Product").setIcon(EBIConstant.ICON_SEARCH);
           mod.gui.getButton("newRelation","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDependency();
                }
            });

            mod.gui.getButton("deleteRelation","Product").setIcon(EBIConstant.ICON_DELETE);
            mod.gui.getButton("deleteRelation","Product").setEnabled(false);
            mod.gui.getButton("deleteRelation","Product").addActionListener(new java.awt.event.ActionListener() {

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

                mod.gui.getTable("productDoc","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                mod.gui.getTable("productDoc","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                        if(lsm.getMinSelectionIndex() != -1){
                            selectedDocRow = mod.gui.getTable("productDoc","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                        }

                        if (lsm.isSelectionEmpty()) {
                            mod.gui.getButton("showProductDoc","Product").setEnabled(false);
                            mod.gui.getButton("deleteProductDoc","Product").setEnabled(false);
                        } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            mod.gui.getButton("showProductDoc","Product").setEnabled(true);
                            mod.gui.getButton("deleteProductDoc","Product").setEnabled(true);
                        }
                    }
                });

                mod.gui.getButton("newProductDoc","Product").setIcon(EBIConstant.ICON_NEW);
                mod.gui.getButton("newProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        dataControlProduct.dataNewDoc();
                    }
                });

                mod.gui.getButton("showProductDoc","Product").setIcon(EBIConstant.ICON_EXPORT);
                mod.gui.getButton("showProductDoc","Product").setEnabled(false);
                mod.gui.getButton("showProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModDoc.data[selectedDocRow][0].toString())) {
                            return;
                        }

                        dataControlProduct.dataViewDoc(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                    }
                });

                mod.gui.getButton("deleteProductDoc","Product").setIcon(EBIConstant.ICON_DELETE);
                mod.gui.getButton("deleteProductDoc","Product").setEnabled(false);
                mod.gui.getButton("deleteProductDoc","Product").addActionListener(new java.awt.event.ActionListener() {

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

            mod.gui.getTable("companyProductTable","Product").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            mod.gui.getTable("companyProductTable","Product").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                     try{
                        selectedProductRow = mod.gui.getTable("companyProductTable","Product").convertRowIndexToModel(lsm.getMinSelectionIndex());
                     }catch(IndexOutOfBoundsException ex){}
                    }
                    if (lsm.isSelectionEmpty()) {
                        mod.gui.getButton("deleteProduct","Product").setEnabled(false);
                        mod.gui.getButton("reportProduct","Product").setEnabled(false);
                        mod.gui.getButton("copyProduct","Product").setEnabled(false);
                    } else if (!productModel.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        mod.gui.getButton("deleteProduct","Product").setEnabled(true);
                        mod.gui.getButton("reportProduct","Product").setEnabled(true);
                        mod.gui.getButton("copyProduct","Product").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(mod.gui.getTable("companyProductTable","Product")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedProductRow = selRow;
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                }

                public void setUpKeyAction(int selRow){
                    selectedProductRow = selRow;
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                }

                public void setEnterKeyAction(int selRow) {}
            });


            mod.gui.getTable("companyProductTable","Product").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseReleased(java.awt.event.MouseEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            if (mod.gui.getTable("companyProductTable","Product").getSelectedRow() < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(productModel.data[selectedProductRow][0].toString())) {
                                return;
                            }
                            selectedProductRow = mod.gui.getTable("companyProductTable","Product").convertRowIndexToModel(mod.gui.getTable("companyProductTable","Product").getSelectedRow());
                            editProduct(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                        }
                    });

                }
            });

            mod.gui.getButton("newProduct","Product").setIcon(EBIConstant.ICON_NEW);
            mod.gui.getButton("newProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
            });
            
            
            mod.gui.getButton("copyProduct","Product").setIcon(EBIConstant.ICON_COPY);
            mod.gui.getButton("copyProduct","Product").setEnabled(false);
            mod.gui.getButton("copyProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }
                    
                    dataControlProduct.dataCopy(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));
                }
            });


             mod.gui.getButton("deleteProduct","Product").setIcon(EBIConstant.ICON_DELETE);
             mod.gui.getButton("deleteProduct","Product").setEnabled(false);
             mod.gui.getButton("deleteProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }
                    ebiDelete();

                }
            });

            mod.gui.getButton("reportProduct","Product").setEnabled(false);
            mod.gui.getButton("reportProduct","Product").setIcon(EBIConstant.ICON_REPORT);
            mod.gui.getButton("reportProduct","Product").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(productModel.data[selectedProductRow][0].toString())) {
                        return;
                    }

                    showProductReport(Integer.parseInt(productModel.data[selectedProductRow][5].toString()));

                }
            });

            mod.gui.getButton("saveProduct","Product").addActionListener(new java.awt.event.ActionListener() {

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
        productModelDimension =(EBIAbstractTableModel) mod.gui.getTable("ProductPropertiesTable","Product").getModel();
        productDependencyModel = (EBIAbstractTableModel) mod.gui.getTable("ProductRelationTable","Product").getModel();
        tabModDoc = (EBIAbstractTableModel) mod.gui.getTable("productDoc","Product").getModel();
        productModel = (EBIAbstractTableModel) mod.gui.getTable("companyProductTable","Product").getModel();

        showProduct();
        mod.gui.getVisualPanel("Product").setCreatedFrom(EBIPGFactory.ebiUser);
        mod.gui.getVisualPanel("Product").setCreatedDate(mod.system.getDateToString(new Date()));

        mod.gui.getVisualPanel("Product").setChangedFrom("");
        mod.gui.getVisualPanel("Product").setChangedDate("");

        mod.gui.getTextfield("ProductNrTex", "Product").setText("");
        mod.gui.getTextfield("ProductNameText","Product").setText("");
        mod.gui.getTextarea("productDescription", "Product").setText("");

        mod.gui.getFormattedTextfield("productGrossText", "Product").setText("");
        mod.gui.getFormattedTextfield("productNetamoutText","Product").setText("");
        mod.gui.getFormattedTextfield("salePriceText","Product").setText("");

        mod.gui.getFormattedTextfield("productGrossText","Product").setValue(null);
        mod.gui.getFormattedTextfield("productNetamoutText","Product").setValue(null);
        mod.gui.getFormattedTextfield("salePriceText","Product").setValue(null);

    }

    /***************************************************************************
     * * 
     **************************************************************************/

    private void newProduct() {
        dataControlProduct.dataNew();
        isEdit = false;
    }

    public void saveProduct(){
        if (validateInput()){
          final Runnable run = new Runnable(){
            public void run(){
                try{ dataControlProduct.dataStore(isEdit); }catch(Exception ex){ex.printStackTrace();}
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

        if ("".equals(mod.gui.getTextfield("ProductNrTex", "Product").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(mod.gui.getTextfield("ProductNameText","Product").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_PRODUCT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if(mod.gui.getComboBox("ProductCategoryText","Product").getSelectedItem() != null){
            if (EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(
                    mod.gui.getComboBox("ProductCategoryText","Product").getSelectedItem().toString())) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SELECT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
        }

        if(!isEdit){
            if(dataControlProduct.existProduct(mod.gui.getTextfield("ProductNrTex", "Product").getText())){
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