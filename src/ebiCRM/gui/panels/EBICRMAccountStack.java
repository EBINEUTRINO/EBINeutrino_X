package ebiCRM.gui.panels;


import java.awt.event.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import org.jdesktop.swingx.sort.RowFilters;
import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlAccountStack;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMAccountStack {

    public EBICRMModule ebiModule = null;
    public EBIDataControlAccountStack dataControlAccount = null;
    public EBIAbstractTableModel tabModAccount = null;
    public boolean isEdit = false;
    public int selectedInvoiceRow = -1;
    public int selectedCDRow = -1;
    public int selectedCDDialogRow = -1;
    public EBIAbstractTableModel tabModDoc = null;
    public EBIAbstractTableModel creditDebitMod = null;
    public int selectedDocRow = -1;
    private String[] selCreditDebitType = null;
    private String[] creditDebitType = null;
    public int showDebitID = -1;
    public int showCreditID = -1;
    public boolean isDebit = false;
    private NumberFormat taxFormat = null;
    public String selectedYear = "";
    public int accountDebitCreditType = 0;
    public String accountDebitTaxName = "";


    public EBICRMAccountStack(EBICRMModule modul){
        ebiModule=modul;
        isEdit = false;
        try {
            ebiModule.system.hibernate.openHibernateSession("EBIACCOUNT_SESSION");
        } catch (Exception e) {
            e.printStackTrace();
        }

        creditDebitType = new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                                                        EBIPGFactory.getLANG("EBI_LANG_DEBIT"),
                                                             EBIPGFactory.getLANG("EBI_LANG_CREDIT"),EBIPGFactory.getLANG("EBI_LANG_DEPOSIT")};

        selCreditDebitType  = new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                                             EBIPGFactory.getLANG("EBI_LANG_DEBIT"),
                                                    EBIPGFactory.getLANG("EBI_LANG_CREDIT")};

        dataControlAccount = new EBIDataControlAccountStack(this);
        tabModAccount = (EBIAbstractTableModel)ebiModule.gui.getTable("accountTable","Account").getModel();
        tabModDoc = (EBIAbstractTableModel)ebiModule.gui.getTable("tableAccountDoc","Account").getModel();
        creditDebitMod = (EBIAbstractTableModel)ebiModule.gui.getTable("debCreditTable","Account").getModel();
        tabModDoc = (EBIAbstractTableModel)ebiModule.gui.getTable("tableAccountDoc","Account").getModel();
        dataControlAccount.dataShowCreditDebit(0);
    }

    public void initializeAction() {

        //AVAILABLE TABLE AND BUTTONS
        ebiModule.gui.getLabel("filterTable","Account").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.gui.getTextfield("filterTableText","Account").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.gui.getTable("accountTable","Account").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.gui.getTextfield("filterTableText", "Account").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.gui.getTable("accountTable","Account").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.gui.getTextfield("filterTableText","Account").getText()));
            }
        });


        ebiModule.gui.getFormattedTextfield("amountText","Account").addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {}

            @Override
            public void focusLost(FocusEvent e) {
                dataControlAccount.dataCalculateTax(showDebitID);
                dataControlAccount.dataCalculateTax(showCreditID);
            }
        });

       ebiModule.gui.getFormattedTextfield("amountText","Account").addKeyListener(new KeyListener() {
           public void keyTyped(KeyEvent e) { }

           public void keyPressed(KeyEvent e) {}

           public void keyReleased(KeyEvent e) {
               try {
                   ebiModule.gui.getFormattedTextfield("amountText","Account").commitEdit();
               } catch (ParseException e1) {}
               dataControlAccount.dataCalculateTax(showDebitID);
               dataControlAccount.dataCalculateTax(showCreditID);
           }
       });

       ebiModule.gui.getTextfield("debitText","Account").addKeyListener(new KeyListener() {
            int idx=-1;
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) { }

            public void keyReleased(KeyEvent e) {
                idx = dataControlAccount.getIDFromNumber(ebiModule.gui.getTextfield("debitText","Account").getText(),false);
                if(idx != -1){
                     showDebitCreditToList(idx);
                }else{
                  ebiModule.gui.getTextfield("descriptionDebit","Account").setText("");
                  ebiModule.gui.getFormattedTextfield("debitCal","Account").setText("");
                  ebiModule.gui.getFormattedTextfield("debitCal","Account").setValue(0.0);
                }
            }
       });

       ebiModule.gui.getTextfield("creditText", "Account").addKeyListener(new KeyListener() {
           int idx = -1;

           public void keyTyped(KeyEvent e) {
           }

           public void keyPressed(KeyEvent e) {
           }

           public void keyReleased(KeyEvent e) {
               idx = dataControlAccount.getIDFromNumber(ebiModule.gui.getTextfield("creditText", "Account").getText(), true);
               if (idx != -1) {
                   showDebitCreditToList(idx);
               } else {
                   ebiModule.gui.getTextfield("descriptionCredit", "Account").setText("");
                   ebiModule.gui.getFormattedTextfield("creditCal", "Account").setText("");
                   ebiModule.gui.getFormattedTextfield("creditCal", "Account").setValue(0.0);
               }
           }
       });

       ebiModule.gui.getButton("selectDebit","Account").setIcon(EBIConstant.ICON_SEARCH);
       ebiModule.gui.getButton("selectDebit","Account").addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               showCreditDebitListDialog(2);
           }
       });

       ebiModule.gui.getButton("selectCredit","Account").setIcon(EBIConstant.ICON_SEARCH);
       ebiModule.gui.getButton("selectCredit","Account").addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               showCreditDebitListDialog(3);
           }
       });

       ebiModule.gui.getButton("saveAccount", "Account").addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               ebiSave();
           }
       });

       ebiModule.gui.getTable("accountTable", "Account").setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
       ebiModule.gui.getTable("accountTable","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

           public void valueChanged(ListSelectionEvent e) {
               if (e.getValueIsAdjusting()) {
                   return;
               }
               ListSelectionModel lsm = (ListSelectionModel) e.getSource();

               if (lsm.getMinSelectionIndex() < 0) {
                  return;
               }
               selectedInvoiceRow = ebiModule.gui.getTable("accountTable", "Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
               if (lsm.isSelectionEmpty()) {
                   ebiModule.gui.getButton("deleteAccount", "Account").setEnabled(false);
               } else if (tabModAccount.data[selectedInvoiceRow][0] != null && !EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tabModAccount.data[selectedInvoiceRow][0])) {
                   ebiModule.gui.getButton("deleteAccount", "Account").setEnabled(true);
               }
           }
       });

       new JTableActionMaps(ebiModule.gui.getTable("accountTable","Account")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
                }

                public void setUpKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
                }

                public void setEnterKeyAction(int selRow) { }
        });

        ebiModule.gui.getTable("accountTable", "Account").addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseReleased(java.awt.event.MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        if (ebiModule.gui.getTable("accountTable", "Account").getSelectedRow() < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                            return;
                        }
                        selectedInvoiceRow = ebiModule.gui.getTable("accountTable", "Account").convertRowIndexToModel(ebiModule.gui.getTable("accountTable", "Account").getSelectedRow());
                        editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
                    }
                });

            }
        });

        ebiModule.gui.getButton("newAccount","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.gui.getButton("newAccount","Account").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                newAccount();
            }
        });

        ebiModule.gui.getButton("deleteAccount","Account").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.gui.getButton("deleteAccount","Account").setEnabled(false);
        ebiModule.gui.getButton("deleteAccount","Account").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                    return;
                }
                ebiDelete();
            }
        });

        ebiModule.gui.getButton("reportAccount","Account").setIcon(EBIConstant.ICON_REPORT);
        ebiModule.gui.getButton("reportAccount","Account").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                showAccountReport();
            }
        });

        ebiModule.gui.getButton("importInvoices","Account").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.gui.getButton("importInvoices","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                dataControlAccount.imporInvoicetoAccout(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
            }
        });


         //ACCOUNT DOCUMENTS
         ebiModule.gui.getTable("tableAccountDoc", "Account").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.gui.getTable("tableAccountDoc","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

             public void valueChanged(ListSelectionEvent e) {
                 if (e.getValueIsAdjusting()) {
                     return;
                 }

                 ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                 if (lsm.getMinSelectionIndex() != -1) {
                     selectedDocRow = ebiModule.gui.getTable("tableAccountDoc", "Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
                 }

                 if (lsm.isSelectionEmpty()) {
                     ebiModule.gui.getButton("showAccountDoc", "Account").setEnabled(false);
                     ebiModule.gui.getButton("deleteAccountDoc", "Account").setEnabled(false);
                 } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                     ebiModule.gui.getButton("showAccountDoc", "Account").setEnabled(true);
                     ebiModule.gui.getButton("deleteAccountDoc", "Account").setEnabled(true);
                 }
             }
         });

        ebiModule.gui.getButton("newAccountDoc","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.gui.getButton("newAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                newDocs();
            }
        });

        ebiModule.gui.getButton("showAccountDoc","Account").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.gui.getButton("showAccountDoc","Account").setEnabled(false);
        ebiModule.gui.getButton("showAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModDoc.data[selectedDocRow][0].toString())) {
                    return;
                }

                saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
            }
        });

       ebiModule.gui.getButton("deleteAccountDoc","Account").setIcon(EBIConstant.ICON_DELETE);
       ebiModule.gui.getButton("deleteAccountDoc","Account").setEnabled(false);
       ebiModule.gui.getButton("deleteAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                    }
                }
       });

        //CREDIT DEBIT PANEL
       ebiModule.gui.getComboBox("selectCreditDebitText","Account").setModel(new DefaultComboBoxModel(creditDebitType));
       ebiModule.gui.getComboBox("selectCreditDebitText", "Account").addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e) {
               dataControlAccount.dataShowCreditDebitExt(((JComboBox) e.getSource()).getSelectedIndex());
           }
       });

       ebiModule.gui.getTable("debCreditTable", "Account").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.gui.getTable("debCreditTable","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

           public void valueChanged(ListSelectionEvent e) {
               if (e.getValueIsAdjusting()) {
                   return;
               }

               ListSelectionModel lsm = (ListSelectionModel) e.getSource();

               if (lsm.getMinSelectionIndex() != -1) {
                   selectedCDRow = ebiModule.gui.getTable("debCreditTable", "Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
               }

               if (lsm.isSelectionEmpty()) {
                   ebiModule.gui.getButton("editCreditDebit", "Account").setEnabled(false);
                   ebiModule.gui.getButton("deleteCreditDebit", "Account").setEnabled(false);
               } else if (!creditDebitMod.data[selectedCDRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                   ebiModule.gui.getButton("editCreditDebit", "Account").setEnabled(true);
                   ebiModule.gui.getButton("deleteCreditDebit", "Account").setEnabled(true);
               }
           }
       });

        ebiModule.gui.getButton("newCreditDebit","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.gui.getButton("newCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                newCreditDebit(-1);
            }
        });

        ebiModule.gui.getButton("editCreditDebit","Account").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.gui.getButton("editCreditDebit","Account").setEnabled(false);
        ebiModule.gui.getButton("editCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedCDRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(creditDebitMod.data[selectedCDRow][0].toString())) {
                    return;
                }

                editCreditDebit(Integer.parseInt(creditDebitMod.data[selectedCDRow][2].toString()));
            }
        });

        ebiModule.gui.getButton("deleteCreditDebit","Account").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.gui.getButton("deleteCreditDebit","Account").setEnabled(false);
        ebiModule.gui.getButton("deleteCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedCDRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(creditDebitMod.data[selectedCDRow][0].toString())) {
                    return;
                }
                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                    deleteCreditDebit(Integer.parseInt(creditDebitMod.data[selectedCDRow][2].toString()));
                }
                }
         });

        // Initialize Action for Account years
        ebiModule.gui.getComboBox("invoiceYearText", "Account").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                if(((JComboBox)e.getSource()).getSelectedIndex() != -1){
                  selectedYear = ((JComboBox)e.getSource()).getSelectedItem().toString();
                }
             }
         });

         ebiModule.gui.getButton("updateYear","Account").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){

                if(ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount() >= 1){
                    boolean isAvailable = false;
                    if(!"".equals(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem())){
                        for(int i = 0; i<=ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount(); i++ ){
                           if(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().equals(ebiModule.gui.getComboBox("invoiceYearText","Account").getItemAt(i))){
                               isAvailable = true;
                               break;
                           }
                        }
                        if(!isAvailable){
                           ebiModule.gui.getComboBox("invoiceYearText","Account").addItem(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                        }
                    }else{
                        if(!"".equals(selectedYear)){
                            ebiModule.gui.getComboBox("invoiceYearText","Account").removeItem(selectedYear);
                        }
                    }
                }else{
                  if(!"".equals(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem())){
                    ebiModule.gui.getComboBox("invoiceYearText","Account").addItem(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                  }
                }

                // create comma separated value
                String vSave = "";
                if(ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount() > 0){

                    for(int i = 0; i<ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount(); i++){

                        if( i < ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount()-1){
                            vSave +=  ebiModule.gui.getComboBox("invoiceYearText","Account").getItemAt(i).toString()+",";
                        } else {
                            vSave +=  ebiModule.gui.getComboBox("invoiceYearText","Account").getItemAt(i).toString();
                        }
                    }
                }

                 //  Sort
                if(ebiModule.gui.getComboBox("invoiceYearText","Account").getItemCount() > 0){

                    String [] avalItems = vSave.split(",");
                    Arrays.sort(avalItems);
                    String selected = ebiModule.gui.getComboBox("invoiceYearText","Account").getSelectedItem().toString();
                    ebiModule.gui.getComboBox("invoiceYearText","Account").removeAllItems();
                    vSave = "";
                    for(int i = 0; i< avalItems.length; i++){
                        ebiModule.gui.getComboBox("invoiceYearText","Account").addItem(avalItems[i]);
                        if( i < avalItems.length-1){
                            vSave +=  avalItems[i] +",";
                        } else {
                            vSave +=  avalItems[i];
                        }
                    }
                    ebiModule.gui.getComboBox("invoiceYearText","Account").setSelectedItem(selected);
                    dataControlAccount.properties.setValue("ACCOUNTYEAR_TEXT",vSave);
                }

                dataControlAccount.properties.setValue("SELECTED_ACCOUNTYEAR_TEXT",ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                dataControlAccount.properties.saveProperties();
                dataControlAccount.dataShow(ebiModule.gui.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
             }
         });

    }


    public void initialize() {

        ebiModule.gui.getComboBox("invoiceYearText","Account").setEditable(true);
        ebiModule.gui.getComboBox("invoiceYearText","Account").removeAllItems();

        ebiModule.gui.getVisualPanel("Account").setCreatedDate(ebiModule.system.getDateToString(new Date()));
        ebiModule.gui.getVisualPanel("Account").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.gui.getVisualPanel("Account").setChangedDate("");
        ebiModule.gui.getVisualPanel("Account").setChangedFrom("");

        ebiModule.gui.getTextfield("numberText","Account").setText("");
        ebiModule.gui.getTextfield("nameText","Account").setText("");
        ebiModule.gui.getTextfield("numberText","Account").setHorizontalAlignment(JTextField.RIGHT);
        ebiModule.gui.getTextfield("descriptionDebit","Account").setText("");
        ebiModule.gui.getTextfield("descriptionDebit","Account").setEditable(false);

        ebiModule.gui.getTextfield("descriptionCredit","Account").setText("");
        ebiModule.gui.getTextfield("descriptionCredit","Account").setEditable(false);

        ebiModule.gui.getTextfield("debitText","Account").setText("");
        ebiModule.gui.getTextfield("creditText","Account").setText("");

        ebiModule.gui.getTextarea("descriptionText","Account").setText("");

        taxFormat=NumberFormat.getNumberInstance();
        taxFormat.setMinimumFractionDigits(2);
        taxFormat.setMaximumFractionDigits(2);

        ebiModule.gui.getFormattedTextfield("amountText","Account").setValue(null);
        ebiModule.gui.getFormattedTextfield("amountText","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.gui.getFormattedTextfield("amountText","Account").setHorizontalAlignment(SwingConstants.RIGHT);

        ebiModule.gui.getFormattedTextfield("debitCal","Account").setValue(null);
        ebiModule.gui.getFormattedTextfield("debitCal","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.gui.getFormattedTextfield("debitCal","Account").setHorizontalAlignment(SwingConstants.RIGHT);
        ebiModule.gui.getFormattedTextfield("debitCal","Account").setEditable(false);

        ebiModule.gui.getFormattedTextfield("creditCal","Account").setValue(null);
        ebiModule.gui.getFormattedTextfield("creditCal","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.gui.getFormattedTextfield("creditCal","Account").setHorizontalAlignment(SwingConstants.RIGHT);
        ebiModule.gui.getFormattedTextfield("creditCal","Account").setEditable(false);

        ebiModule.gui.getTimepicker("dateText","Account").setDate(new Date());
        ebiModule.gui.getTimepicker("dateText","Account").getEditor().setText("");
        ebiModule.gui.getTimepicker("dateText","Account").setFormats(EBIPGFactory.DateFormat);


        try {

             if(!"null".equals(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT")) && !"".equals(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"))){
                 ebiModule.gui.getComboBox("invoiceYearText","Account").setSelectedItem(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"));
                 dataControlAccount.dataShow(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"));
             }else{
                dataControlAccount.dataShow("");
             }

             if(!"null".equals(dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT")) && !"".equals(dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT"))){

                 String [] years = dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT").split(",");
                 if(years != null){
                     for(int i = 0; i< years.length; i++){
                       if(years[i] != null){
                            ebiModule.gui.getComboBox("invoiceYearText","Account").insertItemAt(years[i],i);
                       }
                     }
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

    }

    private void showAccountReport() {
        dataControlAccount.dataShowReport();
    }

    private void deleteAccount(int i) {
        dataControlAccount.dataDelete(i);
    }

    public void editAccount(int i) {
        dataControlAccount.dataEdit(i);
    }

    public void newAccount() {
        dataControlAccount.dataNew(true);
    }

    private void deleteDocs(int i) {
        this.dataControlAccount.dataDeleteDoc(i);
    }

    private void saveAndShowDocs(int i) {
        this.dataControlAccount.dataViewDoc(i);
    }

    private void newDocs() {
        this.dataControlAccount.dataNewDoc();
    }


    private void deleteCreditDebit(int i){
        dataControlAccount.dataDeleteCreditDebit(i);
    }

    private void editCreditDebit(int i){
       newCreditDebit(i);
    }

    private void newCreditDebit(final int id) {
        ebiModule.gui.loadGUI("CRMDialog/creditDebitDialog.xml");
        ebiModule.gui.getFormattedTextfield("valueText","creditDebitDialog").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.gui.getComboBox("creditDebitTypeText","creditDebitDialog").addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(((JComboBox)e.getSource()).getSelectedIndex() == 3){
                    ebiModule.gui.getLabel("vale","creditDebitDialog").setVisible(true);
                    ebiModule.gui.getLabel("taxType","creditDebitDialog").setVisible(false);
                    ebiModule.gui.getLabel("vale", "creditDebitDialog").setLocation(10, 69);
                    ebiModule.gui.getFormattedTextfield("valueText", "creditDebitDialog").setVisible(true);
                    ebiModule.gui.getFormattedTextfield("valueText", "creditDebitDialog").setLocation(110, 69);
                    ebiModule.gui.getComboBox("taxTypeText", "creditDebitDialog").setVisible(false);
                    ebiModule.gui.getLabel("taxValue", "creditDebitDialog").setVisible(false);
                }else{
                    ebiModule.gui.getLabel("vale","creditDebitDialog").setVisible(false);
                    ebiModule.gui.getLabel("taxType","creditDebitDialog").setVisible(true);
                    ebiModule.gui.getFormattedTextfield("valueText","creditDebitDialog").setVisible(false);
                    ebiModule.gui.getComboBox("taxTypeText","creditDebitDialog").setVisible(true);
                    ebiModule.gui.getLabel("taxValue","creditDebitDialog").setVisible(true);
                }
            }
        });

        ebiModule.gui.getComboBox("creditDebitTypeText","creditDebitDialog").setModel(new DefaultComboBoxModel(creditDebitType));
        ebiModule.gui.getComboBox("taxTypeText","creditDebitDialog").setModel(new DefaultComboBoxModel(EBICRMProduct.taxType));
        ebiModule.gui.getComboBox("taxTypeText","creditDebitDialog").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if(((JComboBox)e.getSource()).getSelectedItem() != null){
                    ebiModule.gui.getLabel("taxValue","creditDebitDialog").setText(dataControlAccount.getTaxValue(((JComboBox)e.getSource()).getSelectedItem().toString())+"%");
                }
            }
        });

        ebiModule.gui.getButton("saveValue","creditDebitDialog").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!validateInputDialog()){
                    return;
                }

                dataControlAccount.dataStoreCreditDebit(id < 0 ? false : true,id);
            }
        });

        ebiModule.gui.getButton("closeDialog","creditDebitDialog").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ebiModule.gui.getEBIDialog("creditDebitDialog").setVisible(false);
            }
        });

        if(id > -1){
            dataControlAccount.dataEditCreditDebit(id);
        }

        ebiModule.gui.showGUI();
    }


    private void showCreditDebitListDialog(int type){

       if(type == 3){
           ebiModule.gui.loadGUI("CRMDialog/crmSelectionDialog.xml");
       }else{
           ebiModule.gui.loadGUI("CRMDialog/crmDebitCreditSelectionDialog.xml");
           ebiModule.gui.getComboBox("accoutingType","abstractSelectionDialog").setModel(new DefaultComboBoxModel(selCreditDebitType));
           ebiModule.gui.getComboBox("accoutingType","abstractSelectionDialog").addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   dataControlAccount.dataShowCreditDebitExt(((JComboBox) e.getSource()).getSelectedIndex());
               }
           });
       }

       dataControlAccount.dataShowCreditDebit(type);
       ebiModule.gui.getEBIDialog("abstractSelectionDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_DEBIT_CREDIT_LIST"));

            ebiModule.gui.getTextfield("filterTableText","abstractSelectionDialog").addKeyListener(new KeyListener(){
                public void keyTyped(KeyEvent e){}

                public void keyPressed(KeyEvent e){
                    ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.gui.getTextfield("filterTableText", "abstractSelectionDialog").getText()));
                }
                public void keyReleased(KeyEvent e){
                    ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.gui.getTextfield("filterTableText","abstractSelectionDialog").getText()));
                }
            });

            ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").setModel(creditDebitMod);
            ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedCDDialogRow = ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                }
            });

            new JTableActionMaps(ebiModule.gui.getTable("abstractTable","abstractSelectionDialog")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedCDDialogRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedCDDialogRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedCDDialogRow = selRow;

                    if (selectedCDDialogRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(creditDebitMod.data[selectedCDDialogRow][0].toString())) {
                        return;
                    }
                    showDebitCreditToList(Integer.parseInt(creditDebitMod.data[selectedCDDialogRow][2].toString()));

                }
            });

            ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                if(ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()) != -1){
                    selectedCDDialogRow = ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(ebiModule.gui.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()));
                }

                if (e.getClickCount() == 2) {

                    if (selectedCDDialogRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(creditDebitMod.data[selectedCDDialogRow][0].toString())) {
                        return;
                    }

                    showDebitCreditToList(Integer.parseInt(creditDebitMod.data[selectedCDDialogRow][2].toString()));
                }
                }
            });

            ebiModule.gui.getButton("closeButton","abstractSelectionDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                 ebiModule.gui.getEBIDialog("abstractSelectionDialog").setVisible(false);
                }
            });

            ebiModule.gui.getButton("applyButton","abstractSelectionDialog").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedCDDialogRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(creditDebitMod.data[selectedCDDialogRow][0].toString())) {
                        return;
                    }
                   showDebitCreditToList(Integer.parseInt(creditDebitMod.data[selectedCDDialogRow][2].toString()));
                   ebiModule.gui.getEBIDialog("abstractSelectionDialog").setVisible(false);
                }
            });


        ebiModule.gui.showGUI();

    }

    private void showDebitCreditToList(int i) {
        if(i > -1){
            dataControlAccount.dataCalculateTax(i);
            if(showCreditID != i && showCreditID > -1){
               dataControlAccount.dataCalculateTax(showCreditID);
            }
            if(showDebitID != i && showDebitID > -1){
                dataControlAccount.dataCalculateTax(showDebitID);
            }
        }
    }

    private void saveAccount(){
      final Runnable run = new Runnable(){
    	  public void run(){
    		  dataControlAccount.dataStore(isEdit);
    	  }
      };
    	
      Thread saveAccount = new Thread(run,"Save Account");
      saveAccount.start();
    }

    private boolean validateInputDialog(){

        if("".equals(ebiModule.gui.getTextfield("numberText","creditDebitDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.gui.getTextfield("nameText","creditDebitDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.gui.getComboBox("creditDebitTypeText","creditDebitDialog").getSelectedItem().toString())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private boolean validateInput(){

        if("".equals(ebiModule.gui.getTimepicker("dateText","Account").getEditor().getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_ILLEGAL_DATE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.gui.getTextfield("numberText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.gui.getTextfield("nameText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.gui.getFormattedTextfield("amountText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_AMOUNT")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.gui.getTextfield("debitText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_DEBIT")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.gui.getTextfield("creditText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_CREDIT")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    public void ebiNew(){
        dataControlAccount.dataNew(true);
    }

    public void ebiSave(){
        if(!validateInput()){
            return;
        }
        saveAccount();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
        }
    }


}
