package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.*;
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
import ebiCRM.gui.dialogs.EBIDialogSearchContact;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMInvoice {

    public EBICRMModule mod = null;
    public EBIAbstractTableModel tabModProduct = null;
    public boolean isEdit = false;
    public static String[] invoiceStatus = null;
    public static String[] invoiceCategory = null;
    public EBIDataControlInvoice dataControlInvoice = null;
    private int selectedInvoiceRow = -1;
    private int selectedProductRow = -1;
    public String beginChar = "";
    public int invoiceNr = -1;
    private EBIAbstractTableModel model = null;
    public String selectedYear = "";

    /**
     * This is the default constructor
     */
    public EBICRMInvoice(EBICRMModule ebiMod) {
        isEdit = false;
        mod = ebiMod;
        try {
            mod.system.hibernate.openHibernateSession("EBIINVOICE_SESSION");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataControlInvoice = new EBIDataControlInvoice(this);
        tabModProduct = (EBIAbstractTableModel) mod.gui.getTable("invoicePositionTable","Invoice").getModel();
        model = (EBIAbstractTableModel) mod.gui.getTable("tableTotalInvoice","Invoice").getModel();
        dataControlInvoice.dataShow();
    }

    public void initializeAction(){

        mod.gui.getLabel("filterTable","Invoice").setHorizontalAlignment(SwingUtilities.RIGHT);
        mod.gui.getTextfield("filterTableText","Invoice").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                mod.gui.getTable("tableTotalInvoice","Invoice").setRowFilter(RowFilters.regexFilter("(?i)"+ mod.gui.getTextfield("filterTableText","Invoice").getText()));
            }
            public void keyReleased(KeyEvent e){
                mod.gui.getTable("tableTotalInvoice","Invoice").setRowFilter(RowFilters.regexFilter("(?i)"+ mod.gui.getTextfield("filterTableText","Invoice").getText()));
            }
        });

        mod.gui.getComboBox("invoiceStatusText", "Invoice").setEditable(true);
        mod.gui.getComboBox("categoryText", "Invoice").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    if(!mod.gui.getComboBox("categoryText","Invoice").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
                              if(!isEdit || "-1".equals(mod.gui.getTextfield("invoiceNrText","Invoice").getText())){
                                Object[] obj = mod.dynMethod.getInternNumber(mod.gui.getComboBox("categoryText","Invoice").getSelectedItem().toString(),true);
                                beginChar = obj[1].toString();
                                invoiceNr = Integer.parseInt(obj[0].toString());
                                mod.gui.getTextfield("invoiceNrText","Invoice").setText(obj[1].toString()+obj[0].toString());
                              }
				    }

                }
        });

        mod.gui.getButton("saveInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
            saveInvoice();
            }
        });

        /***************************************/
        // Contact Information
        /**************************************/
        mod.gui.getButton("searchContact","Invoice").setIcon(EBIConstant.ICON_SEARCH);
        mod.gui.getButton("searchContact","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                 EBIDialogSearchContact addCon = new EBIDialogSearchContact(mod,false);
                
                    addCon.setValueToComponent(mod.gui.getComboBox("genderText","Invoice"), "Gender");
                    addCon.setValueToComponent(mod.gui.getTextfield("titleText","Invoice"), "Position");
                    addCon.setValueToComponent(mod.gui.getTextfield("surnameText","Invoice"), "Surname");
                    addCon.setValueToComponent(mod.gui.getTextfield("nameText","Invoice"), "contact.Name");
                    addCon.setValueToComponent(mod.gui.getTextfield("streetNrText","Invoice"), "Street");
                    addCon.setValueToComponent(mod.gui.getTextfield("zipText","Invoice"), "Zip");
                    addCon.setValueToComponent(mod.gui.getTextfield("locationText","Invoice"), "Location");
                    addCon.setValueToComponent(mod.gui.getTextfield("countryText","Invoice"), "Country");
                    addCon.setValueToComponent(mod.gui.getTextfield("postCodeText","Invoice"), "PBox");
                    addCon.setValueToComponent(mod.gui.getTextfield("emailText","Invoice"), "EMail");
                    addCon.setValueToComponent(mod.gui.getTextfield("faxText","Invoice"), "Fax");
                    addCon.setValueToComponent(mod.gui.getTextfield("telefonText","Invoice"), "phone");
                    addCon.setValueToComponent(mod.gui.getTextarea("recDescription","Invoice"), "contact.description");
                    addCon.setValueToComponent(mod.gui.getTextfield("companyNameText","Invoice"), "company.NAME");
                    addCon.setValueToComponent(mod.gui.getTextfield("internetText","Invoice"), "company.WEB");

                    addCon.setVisible();
            }
        });

        mod.gui.getComboBox("genderText","Invoice").setEditable(true);


        /****************************************/
        // Product Information
        /***************************************/
    
        mod.gui.getButton("newPosition","Invoice").setIcon(EBIConstant.ICON_NEW);
        mod.gui.getButton("newPosition","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                  EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(dataControlInvoice.getInvoice(), mod);
                  product.setVisible();
                  dataControlInvoice.calculateTotalAmount(); 
            }
        });

        mod.gui.getButton("deletePosition","Invoice").setIcon(EBIConstant.ICON_DELETE);
        mod.gui.getButton("deletePosition","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                               equals(tabModProduct.data[selectedProductRow][0].toString())) {
                           return;
                }
                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                     dataControlInvoice.dataDeleteProduct(Integer.parseInt(tabModProduct.data[selectedProductRow][8].toString()));
                     dataControlInvoice.calculateTotalAmount();
                }
            }
        });

        mod.gui.getTable("invoicePositionTable","Invoice").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mod.gui.getTable("invoicePositionTable","Invoice").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = mod.gui.getTable("invoicePositionTable","Invoice").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if(tabModProduct.data.length > 0){
                        if (lsm.isSelectionEmpty()) {
                            mod.gui.getButton("deletePosition","Invoice").setEnabled(false);
                        } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            mod.gui.getButton("deletePosition","Invoice").setEnabled(true);
                        }
                    }
                }
            });

        /********************************************/
        // Available Invoice
        /*******************************************/

        mod.gui.getButton("newInvoice","Invoice").setIcon(EBIConstant.ICON_NEW);
        mod.gui.getButton("newInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
              newInvoice();
            }
        });


        mod.gui.getButton("deleteInvoice","Invoice").setIcon(EBIConstant.ICON_DELETE);
        mod.gui.getButton("deleteInvoice","Invoice").setEnabled(false);
        mod.gui.getButton("deleteInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                }

                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                    deleteInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                }
            }
        });


        mod.gui.getButton("reportInvoice","Invoice").setIcon(EBIConstant.ICON_REPORT);
        mod.gui.getButton("reportInvoice","Invoice").setEnabled(false);
        mod.gui.getButton("reportInvoice","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                }
                showInvoiceReport(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
            }
        });
        
        mod.gui.getButton("sendEmail","Invoice").setIcon(EBIConstant.ICON_SEND_MAIL);
        mod.gui.getButton("sendEmail","Invoice").setEnabled(false);
        mod.gui.getButton("sendEmail","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedInvoiceRow][0].toString())) {
                            return;
                    }

              mailInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));


                }

        });

        mod.gui.getTable("tableTotalInvoice","Invoice").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mod.gui.getTable("tableTotalInvoice","Invoice").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                         selectedInvoiceRow = mod.gui.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        mod.gui.getButton("deleteInvoice","Invoice").setEnabled(false);
                        mod.gui.getButton("reportInvoice","Invoice").setEnabled(false);
                        mod.gui.getButton("sendEmail","Invoice").setEnabled(false);
                    }else if (!model.data[selectedInvoiceRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        mod.gui.getButton("deleteInvoice","Invoice").setEnabled(true);
                        mod.gui.getButton("reportInvoice","Invoice").setEnabled(true);
                        mod.gui.getButton("sendEmail","Invoice").setEnabled(true);
                    }

                }
            });

            new JTableActionMaps(mod.gui.getTable("tableTotalInvoice","Invoice")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {

                    selectedInvoiceRow = selRow;

                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                }

                public void setUpKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;

                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                }

                public void setEnterKeyAction(int selRow) {

                }
            });

            mod.gui.getTable("tableTotalInvoice","Invoice").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseReleased(java.awt.event.MouseEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                        if (mod.gui.getTable("tableTotalInvoice","Invoice").getSelectedRow() < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(model.data[selectedInvoiceRow][0].toString())) {
                            return;
                        }
                        selectedInvoiceRow = mod.gui.getTable("tableTotalInvoice","Invoice").convertRowIndexToModel(mod.gui.getTable("tableTotalInvoice","Invoice").getSelectedRow());
                        editInvoice(Integer.parseInt(model.data[selectedInvoiceRow][9].toString()));
                        }
                    });

                }
            });


        // Initialize Action for Account years
        mod.gui.getComboBox("invoiceYearText", "Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(((JComboBox)e.getSource()).getSelectedIndex() != -1){
                    selectedYear = ((JComboBox)e.getSource()).getSelectedItem().toString();
                }
            }
        });

        mod.gui.getButton("updateYear","Invoice").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                if(mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount() >= 1){
                    boolean isAvailable = false;
                    if(!"".equals(mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem())){
                        for(int i = 0; i<= mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount(); i++ ){
                            if(mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem().equals(mod.gui.getComboBox("invoiceYearText","Invoice").getItemAt(i))){
                                isAvailable = true;
                                break;
                            }
                        }
                        if(!isAvailable){
                            mod.gui.getComboBox("invoiceYearText","Invoice").addItem(mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem().toString());
                        }
                    }else{
                        if(!"".equals(selectedYear)){
                            mod.gui.getComboBox("invoiceYearText","Invoice").removeItem(selectedYear);
                        }
                    }
                }else{
                    if(!"".equals(mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem())){
                        mod.gui.getComboBox("invoiceYearText","Invoice").addItem(mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem().toString());
                    }
                }

                // create comma separated value
                String vSave = "";
                if(mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount() > 0){

                    for(int i = 0; i< mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount(); i++){

                        if( i < mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount()-1){
                            vSave +=  mod.gui.getComboBox("invoiceYearText","Invoice").getItemAt(i).toString()+",";
                        } else {
                            vSave +=  mod.gui.getComboBox("invoiceYearText","Invoice").getItemAt(i).toString();
                        }
                    }
                }

                //  Sort
                if(mod.gui.getComboBox("invoiceYearText","Invoice").getItemCount() > 0){

                    final String [] avalItems = vSave.split(",");
                    Arrays.sort(avalItems);
                    final String selected = mod.gui.getComboBox("invoiceYearText","Invoice").getSelectedItem().toString();
                    mod.gui.getComboBox("invoiceYearText","Invoice").removeAllItems();
                    vSave = "";
                    for(int i = 0; i< avalItems.length; i++){
                        mod.gui.getComboBox("invoiceYearText","Invoice").addItem(avalItems[i]);
                        if( i < avalItems.length-1){
                            vSave +=  avalItems[i] +",";
                        } else {
                            vSave +=  avalItems[i];
                        }
                    }
                    mod.gui.getComboBox("invoiceYearText","Invoice").setSelectedItem(selected);
                    dataControlInvoice.properties.setValue("SELECTED_SYSTEMYEAR_TEXT",vSave);
                }

                dataControlInvoice.properties.setValue("SELECTED_SYSTEMYEAR_TEXT", mod.gui.getComboBox("invoiceYearText","Invoice").getEditor().getItem().toString());
                dataControlInvoice.properties.saveProperties();
                mod.system.updateSystemYears();
                dataControlInvoice.dataShow();
            }
        });
    }


    /**
     * This method initializes this
     *
     * @return void
     */
    public void initialize() {
        beginChar = "";
        mod.gui.getVisualPanel("Invoice").setCreatedDate(mod.system.getDateToString(new Date()));
        mod.gui.getVisualPanel("Invoice").setCreatedFrom(EBIPGFactory.ebiUser);
        mod.gui.getVisualPanel("Invoice").setChangedDate("");
        mod.gui.getVisualPanel("Invoice").setChangedFrom("");

        NumberFormat taxFormat=NumberFormat.getNumberInstance();
        taxFormat.setMinimumFractionDigits(2);
        taxFormat.setMaximumFractionDigits(3);

        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setValue(null);
        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setEditable(false);
        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setForeground(new Color(255, 40, 40));
        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);
        mod.gui.getFormattedTextfield("totalNetAmountText","Invoice").setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        mod.gui.getFormattedTextfield("taxText","Invoice").setValue(null);
        mod.gui.getFormattedTextfield("taxText","Invoice").setEditable(false);
        mod.gui.getFormattedTextfield("taxText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("taxText","Invoice").setForeground(new Color(255,40,40));
        mod.gui.getFormattedTextfield("taxText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);
        mod.gui.getFormattedTextfield("taxText","Invoice").setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setValue(null);
        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setEditable(false);
        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setForeground(new Color(255,40,40));
        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);
        mod.gui.getFormattedTextfield("totalGrossAmountText","Invoice").setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        mod.gui.getFormattedTextfield("deductionText","Invoice").setValue(null);
        mod.gui.getFormattedTextfield("deductionText","Invoice").setEditable(false);
        mod.gui.getFormattedTextfield("deductionText","Invoice").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        mod.gui.getFormattedTextfield("deductionText","Invoice").setForeground(new Color(255,40,40));
        mod.gui.getFormattedTextfield("deductionText","Invoice").setHorizontalAlignment(SwingConstants.RIGHT);
        mod.gui.getFormattedTextfield("deductionText","Invoice").setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

        TableColumn col7 = mod.gui.getTable("invoicePositionTable","Invoice").getColumnModel().getColumn(5);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        mod.gui.getTextfield("invoiceNrText","Invoice").setEditable(false);

        mod.gui.getComboBox("categoryText", "Invoice").setModel(new DefaultComboBoxModel(invoiceCategory));
        mod.gui.getComboBox("invoiceStatusText", "Invoice").setModel(new DefaultComboBoxModel(invoiceStatus));
        mod.gui.getComboBox("invoiceStatusText","Invoice").setSelectedIndex(0);
        mod.gui.getComboBox("categoryText","Invoice").setSelectedIndex(0);

        mod.gui.getTextfield("invoiceNrText","Invoice").setText("");
        mod.gui.getTextfield("invoiceNameText","Invoice").setText("");

        mod.gui.getComboBox("genderText","Invoice").setModel(new DefaultComboBoxModel(EBICRMModule.gendersList));
        mod.gui.getComboBox("genderText","Invoice").setSelectedIndex(0);

        mod.gui.getTextfield("titleText","Invoice").setText("");
        mod.gui.getTextfield("companyNameText","Invoice").setText("");
        mod.gui.getTextfield("nameText","Invoice").setText("");
        mod.gui.getTextfield("surnameText","Invoice").setText("");
        mod.gui.getTextfield("streetNrText","Invoice").setText("");
        mod.gui.getTextfield("zipText","Invoice").setText("");
        mod.gui.getTextfield("locationText","Invoice").setText("");
        mod.gui.getTextfield("postCodeText","Invoice").setText("");
        mod.gui.getTextfield("countryText","Invoice").setText("");
        mod.gui.getTextfield("telefonText","Invoice").setText("");
        mod.gui.getTextfield("faxText","Invoice").setText("");
        mod.gui.getTextfield("emailText","Invoice").setText("");
        mod.gui.getTextfield("internetText","Invoice").setText("");

        mod.gui.getTextarea("recDescription","Invoice").setText("");
        mod.gui.getTimepicker("invoiceDateText","Invoice").setDate(new Date());
        mod.gui.getTimepicker("invoiceDateText","Invoice").getEditor().setText("");
        mod.gui.getTimepicker("invoiceDateText","Invoice").setFormats(EBIPGFactory.DateFormat);

        mod.gui.getButton("deletePosition","Invoice").setEnabled(false);
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

		        dataControlInvoice.dataStore(isEdit);
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
        dataControlInvoice.dataDelete(id);
        newInvoice();
    }

    private boolean validateInput() {

        if("".equals(mod.gui.getTextfield("invoiceNrText", "Invoice").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_INVOICE_NR")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if(mod.gui.getComboBox("invoiceStatusText","Invoice").getSelectedIndex() == 0){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if("".equals(mod.gui.getTextfield("invoiceNameText", "Invoice").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (isEdit == false){
            for (int i = 0; i < model.data.length; i++) {
                if (model.data[i][0].equals(mod.gui.getTextfield("invoiceNrText", "Invoice").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INVOICE_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private void showInvoiceReport(int id) {
       dataControlInvoice.dataShowReport(id);
    }

    public void mailInvoice(final int id){
       dataControlInvoice.dataShowAndMailReport(id, false);
    }

}