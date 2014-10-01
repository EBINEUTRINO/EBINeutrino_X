package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlInvoice;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBIDialogSearchContact;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMInvoice {

    public EBICRMModule ebiModule = null;
    public MyTableModelCRMProduct tabModProduct = null;
    public boolean isEdit = false;
    public static String[] invoiceStatus = null;
    public static String[] invoiceCategory = null;
    public EBIDataControlInvoice dataControlInvoice = null;
    private int selectedInvoiceRow = -1;
    private int selectedProductRow = -1;
    public String beginChar = "";
    public int invoiceNr = -1;
    private EBIAbstractTableModel model = null;

    /**
     * This is the default constructor
     */
    public EBICRMInvoice(EBICRMModule ebiMod) {
        isEdit = false;
        ebiModule = ebiMod;
        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBIINVOICE_SESSION");
        } catch (Exception e) {
            e.printStackTrace();
        }

        tabModProduct = new MyTableModelCRMProduct();
        dataControlInvoice = new EBIDataControlInvoice(this);
    }

    public void initializeAction(){
        model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getModel();

        ebiModule.guiRenderer.getLabel("filterTable","Invoice").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Invoice").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Invoice").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Invoice").getText()));
            }
        });

        ebiModule.guiRenderer.getComboBox("invoiceStatusText", "Invoice").setEditable(true);
        

        ebiModule.guiRenderer.getComboBox("categoryText", "Invoice").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    if(!ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
                              if(!isEdit || "-1".equals(ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").getText())){
                                Object[] obj = ebiModule.dynMethod.getInternNumber(ebiModule.guiRenderer.getComboBox("categoryText","Invoice").getSelectedItem().toString(),true);
                                beginChar = obj[1].toString();
                                invoiceNr = Integer.parseInt(obj[0].toString());
                                ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").setText(obj[1].toString()+obj[0].toString());
                              }
				    }

                }
        });

        ebiModule.guiRenderer.getButton("saveInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                ebiSave();
            }
        });

        /***************************************/
        // Contact Information
        /**************************************/

        ebiModule.guiRenderer.getButton("searchContact","Invoice").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchContact","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                 EBIDialogSearchContact addCon = new EBIDialogSearchContact(ebiModule,false);
                
                    addCon.setValueToComponent(ebiModule.guiRenderer.getComboBox("genderText","Invoice"), "Gender");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("titleText","Invoice"), "Position");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("surnameText","Invoice"), "Surname");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("nameText","Invoice"), "contact.Name");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("streetNrText","Invoice"), "Street");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("zipText","Invoice"), "Zip");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("locationText","Invoice"), "Location");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("countryText","Invoice"), "Country");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("postCodeText","Invoice"), "PBox");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("emailText","Invoice"), "EMail");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("faxText","Invoice"), "Fax");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("telefonText","Invoice"), "phone");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextarea("recDescription","Invoice"), "contact.description");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("companyNameText","Invoice"), "company.NAME");
                    addCon.setValueToComponent(ebiModule.guiRenderer.getTextfield("internetText","Invoice"), "company.WEB");

                    addCon.setVisible();
            }
        });

        ebiModule.guiRenderer.getComboBox("genderText","Invoice").setEditable(true);


        /****************************************/
        // Product Information
        /***************************************/
    
        ebiModule.guiRenderer.getButton("newPosition","Invoice").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newPosition","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                  EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(dataControlInvoice.getInvoice(), ebiModule);
                  product.setVisible();
                  dataControlInvoice.calculateTotalAmount(); 
            }
        });

        ebiModule.guiRenderer.getButton("deletePosition","Invoice").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deletePosition","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                               equals(tabModProduct.data[selectedProductRow][0].toString())) {
                           return;
                }
                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                    boolean pass  ;
                    if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                            ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                        pass = true;
                    } else {
                        pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
                    }
                    if (pass) {
                        dataControlInvoice.dataDeleteProduct(Integer.parseInt(tabModProduct.data[selectedProductRow][8].toString()));
                        dataControlInvoice.calculateTotalAmount();
                    }

                }
            }
        });

        ebiModule.guiRenderer.getTable("invoicePositionTable","Invoice").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("invoicePositionTable","Invoice").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("invoicePositionTable","Invoice").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if(tabModProduct.data.length > 0){
                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("deletePosition","Invoice").setEnabled(false);
                        } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("deletePosition","Invoice").setEnabled(true);
                        }
                    }
                }
            });

        /********************************************/
        // Available Invoice
        /*******************************************/

        ebiModule.guiRenderer.getButton("newInvoice","Invoice").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                   ebiNew();
            }
        });


        ebiModule.guiRenderer.getButton("editInvoice","Invoice").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editInvoice","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getButton("editInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                  editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
            }
        });

        ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                }

                ebiDelete();
            }
        });

        ebiModule.guiRenderer.getButton("historyInvoice","Invoice").setIcon(EBIConstant.ICON_HISTORY);
        ebiModule.guiRenderer.getButton("historyInvoice","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getButton("historyInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()), "Invoice"), ebiModule).setVisible();  
            }
        });

        ebiModule.guiRenderer.getButton("reportInvoice","Invoice").setIcon(EBIConstant.ICON_REPORT);
        ebiModule.guiRenderer.getButton("reportInvoice","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getButton("reportInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                }
                showInvoiceReport(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
            }
        });
        
        ebiModule.guiRenderer.getButton("sendEmail","Invoice").setIcon(EBIConstant.ICON_SEND_MAIL);
        ebiModule.guiRenderer.getButton("sendEmail","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getButton("sendEmail","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedInvoiceRow][0].toString())) {
                            return;
                    }

              mailInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));


                }

        });

        ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                       try{
                         selectedInvoiceRow = ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(lsm.getMinSelectionIndex());
                       }catch(IndexOutOfBoundsException ex){}
                    }
                   try{
                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("editInvoice","Invoice").setEnabled(false);
                            ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setEnabled(false);
                            ebiModule.guiRenderer.getButton("historyInvoice","Invoice").setEnabled(false);
                            ebiModule.guiRenderer.getButton("reportInvoice","Invoice").setEnabled(false);
                            ebiModule.guiRenderer.getButton("sendEmail","Invoice").setEnabled(false);
                        } else if (!model.data[selectedInvoiceRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("editInvoice","Invoice").setEnabled(true);
                            ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setEnabled(true);
                            ebiModule.guiRenderer.getButton("historyInvoice","Invoice").setEnabled(true);
                            ebiModule.guiRenderer.getButton("reportInvoice","Invoice").setEnabled(true);
                            ebiModule.guiRenderer.getButton("sendEmail","Invoice").setEnabled(true);
                        }
                   }catch(ArrayIndexOutOfBoundsException ex){
                        ebiModule.guiRenderer.getButton("editInvoice","Invoice").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteInvoice","Invoice").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyInvoice","Invoice").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportInvoice","Invoice").setEnabled(false);
                        ebiModule.guiRenderer.getButton("sendEmail","Invoice").setEnabled(false);
                   }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;

                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                    ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    
                   if(ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").rowAtPoint(e.getPoint()) != -1){
                        selectedInvoiceRow = ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").rowAtPoint(e.getPoint()));
                   }

                   if (e.getClickCount() == 2) {

                        if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedInvoiceRow][0].toString())) {
                            return;
                        }

                        editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                        ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").grabFocus();
                   }
                }
            });
    }


    /**
     * This method initializes this
     *
     * @return void
     */
    public void initialize() {

        tabModProduct = new MyTableModelCRMProduct();
        beginChar = "";
        ebiModule.guiRenderer.getVisualPanel("Invoice").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("Invoice").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Invoice").setChangedFrom("");


        NumberFormat taxFormat=NumberFormat.getNumberInstance();
        taxFormat.setMinimumFractionDigits(2);
        taxFormat.setMaximumFractionDigits(3);

        ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setEditable(false);
        ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setForeground(new Color(255,40,40));
        ebiModule.guiRenderer.getFormattedTextfield("totalNetAmountText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);
        ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setEditable(false);
        ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));

        ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setForeground(new Color(255,40,40));
        ebiModule.guiRenderer.getFormattedTextfield("taxText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);

        ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setEditable(false);
        ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setForeground(new Color(255,40,40));
        ebiModule.guiRenderer.getFormattedTextfield("totalGrossAmountText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);

        ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setEditable(false);
        ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setForeground(new Color(255,40,40));
        ebiModule.guiRenderer.getFormattedTextfield("deductionText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);

        ebiModule.guiRenderer.getButton("selectOrder","Invoice").setEnabled(false);

        ebiModule.guiRenderer.getTable("invoicePositionTable","Invoice").setModel(tabModProduct);

        TableColumn col7 = ebiModule.guiRenderer.getTable("invoicePositionTable","Invoice").getColumnModel().getColumn(5);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").setEditable(false);
        ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").setBackground(EBIPGFactory.systemColor);

        ebiModule.guiRenderer.getComboBox("categoryText", "Invoice").setModel(new DefaultComboBoxModel(invoiceCategory));
        ebiModule.guiRenderer.getComboBox("invoiceStatusText", "Invoice").setModel(new DefaultComboBoxModel(invoiceStatus));
        ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("categoryText","Invoice").setSelectedIndex(0);

        ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("invoiceNameText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("orderText","Invoice").setEnabled(false);
        ebiModule.guiRenderer.getTextfield("orderText","Invoice").setText("");

        ebiModule.guiRenderer.getComboBox("genderText","Invoice").setModel(new DefaultComboBoxModel(EBICRMModule.gendersList));
        ebiModule.guiRenderer.getComboBox("genderText","Invoice").setSelectedIndex(0);

        ebiModule.guiRenderer.getTextfield("titleText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("companyNameText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("nameText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("surnameText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("streetNrText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("zipText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("locationText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("postCodeText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("countryText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("telefonText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("faxText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("emailText","Invoice").setText("");
        ebiModule.guiRenderer.getTextfield("internetText","Invoice").setText("");

        ebiModule.guiRenderer.getTextarea("recDescription","Invoice").setText("");
        ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").setDate(new Date());
        ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").getEditor().setText("");
        ebiModule.guiRenderer.getTimepicker("invoiceDateText","Invoice").setFormats(EBIPGFactory.DateFormat);

        ebiModule.guiRenderer.getButton("deletePosition","Invoice").setEnabled(false);
    }

    public void showProduct() {
        dataControlInvoice.dataShowProduct();
    }
   

    private void newInvoice() {
        isEdit = false;
        dataControlInvoice.dataNew(true);
    }

    public void saveInvoice() {
      final Runnable run = new Runnable(){ 	
	       public void run(){	  
		        if (!validateInput()) {
		            return;
		        }
		        
		        int row = 0;
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getSelectedRow();
		        }
		        
		        dataControlInvoice.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Invoice");
      save.start();
      
    }

    public void editInvoice(int id) {
        isEdit = true;
        dataControlInvoice.dataEdit(id);
    }

    private void deleteInvoice(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlInvoice.dataDelete(id);
            newInvoice();
        }
    }

    private boolean validateInput() {

        if ("".equals(ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_INVOICE_NR")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (ebiModule.guiRenderer.getComboBox("invoiceStatusText","Invoice").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (isEdit == false) {
            EBIAbstractTableModel tabModel = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("tableTotalInvoice","Invoice").getModel();
            for (int i = 0; i < tabModel.data.length; i++) {
                if (tabModel.data[i][0].equals(ebiModule.guiRenderer.getTextfield("invoiceNrText","Invoice").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INVOICE_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private void showInvoiceReport(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlInvoice.dataShowReport(id);
        }
    }

    public void mailInvoice(final int id){

        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlInvoice.dataShowAndMailReport(id, false);
        }
    }

    public void ebiNew(){
        newInvoice();
    }

    public void ebiSave(){
        saveInvoice();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
        }
    }

}