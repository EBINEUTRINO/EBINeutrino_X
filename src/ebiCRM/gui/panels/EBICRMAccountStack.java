package ebiCRM.gui.panels;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.sort.RowFilters;
import org.jdesktop.swingx.table.TableColumnExt;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlAccountStack;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMAccount;
import ebiCRM.table.models.MyTableModelCreditDebit;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMAccountStack {

    public EBICRMModule ebiModule = null;
    public EBIDataControlAccountStack dataControlAccount = null;
    public MyTableModelCRMAccount tabModAccount = null;
    public boolean isEdit = false;
    public int selectedInvoiceRow = -1;
    public int selectedCDRow = -1;
    public int selectedCDDialogRow = -1;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelCreditDebit creditDebitMod = null;
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
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBIACCOUNT_SESSION");
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
        tabModAccount = new MyTableModelCRMAccount();
        creditDebitMod = new MyTableModelCreditDebit();
        tabModAccount = new MyTableModelCRMAccount();
        tabModDoc = new MyTableModelDoc();

        dataControlAccount.dataShowCreditDebit(0);

    }

    public void initializeAction() {

        //AVAILABLE TABLE AND BUTTONS
        ebiModule.guiRenderer.getLabel("filterTable","Account").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Account").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("accountTable","Account").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "Account").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("accountTable","Account").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Account").getText()));
            }
        });


       ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").addKeyListener(new KeyListener() {
           public void keyTyped(KeyEvent e) {
               dataControlAccount.dataCalculateTax(showDebitID);
               dataControlAccount.dataCalculateTax(showCreditID);
           }

           public void keyPressed(KeyEvent e) {
               dataControlAccount.dataCalculateTax(showDebitID);
               dataControlAccount.dataCalculateTax(showCreditID);
           }

           public void keyReleased(KeyEvent e) {
               dataControlAccount.dataCalculateTax(showDebitID);
               dataControlAccount.dataCalculateTax(showCreditID);
           }
       });

       ebiModule.guiRenderer.getTextfield("debitText","Account").addKeyListener(new KeyListener() {
            int idx=-1;
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
               idx = dataControlAccount.getIDFromNumber(ebiModule.guiRenderer.getTextfield("debitText","Account").getText(),false);
                if(idx != -1){
                     showDebitCreditToList(idx);
                }else{
                  ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setValue(null);
                }
            }

            public void keyReleased(KeyEvent e) {
                idx = dataControlAccount.getIDFromNumber(ebiModule.guiRenderer.getTextfield("debitText","Account").getText(),false);
                if(idx != -1){
                     showDebitCreditToList(idx);
                }else{
                  ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setValue(null);
                }
            }
       });

       ebiModule.guiRenderer.getTextfield("creditText","Account").addKeyListener(new KeyListener() {
            int idx=-1;
            public void keyTyped(KeyEvent e) {}

            public void keyPressed(KeyEvent e) {
               idx = dataControlAccount.getIDFromNumber(ebiModule.guiRenderer.getTextfield("creditText","Account").getText(),true);
                if(idx != -1){
                     showDebitCreditToList(idx);
                }else{
                  ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setValue(null);
                }
            }

            public void keyReleased(KeyEvent e) {
                idx = dataControlAccount.getIDFromNumber(ebiModule.guiRenderer.getTextfield("creditText","Account").getText(),true);
                if(idx != -1){
                     showDebitCreditToList(idx);
                }else{
                  ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setText("");
                  ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setValue(null);
                }
            }
       });



       ebiModule.guiRenderer.getButton("selectDebit","Account").setIcon(EBIConstant.ICON_SEARCH);
       ebiModule.guiRenderer.getButton("selectDebit","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                showCreditDebitListDialog(2);
            }
       });

       ebiModule.guiRenderer.getButton("selectCredit","Account").setIcon(EBIConstant.ICON_SEARCH);
       ebiModule.guiRenderer.getButton("selectCredit","Account").addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
               showCreditDebitListDialog(3);
           }
       });

       ebiModule.guiRenderer.getButton("saveAccount","Account").addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent event) {
                ebiSave();
           }
       });

       ebiModule.guiRenderer.getTable("accountTable","Account").setModel(tabModAccount);
       ebiModule.guiRenderer.getTable("accountTable","Account").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.guiRenderer.getTable("accountTable","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                         selectedInvoiceRow = ebiModule.guiRenderer.getTable("accountTable","Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editAccount","Account").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteAccount","Account").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyAccount","Account").setEnabled(false);
                    } else if (!tabModAccount.data[selectedInvoiceRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editAccount","Account").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteAccount","Account").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyAccount","Account").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("accountTable","Account")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedInvoiceRow = selRow;

                    if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                        return;
                    }
                    editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("numberText","Account").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("accountTable","Account").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(ebiModule.guiRenderer.getTable("accountTable","Account").rowAtPoint(e.getPoint()) != -1){
                        selectedInvoiceRow = ebiModule.guiRenderer.getTable("accountTable","Account").convertRowIndexToModel(ebiModule.guiRenderer.getTable("accountTable","Account").rowAtPoint(e.getPoint()));
                    }

                    if (e.getClickCount() == 2) {

                        if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                            return;
                        }

                        editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
                        ebiModule.guiRenderer.getTextfield("numberText","Account").grabFocus();
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newAccount","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newAccount","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                   newAccount();
            }
        });

        ebiModule.guiRenderer.getButton("editAccount","Account").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editAccount","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("editAccount","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                  editAccount(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()));
            }
        });

        ebiModule.guiRenderer.getButton("deleteAccount","Account").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteAccount","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteAccount","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedInvoiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModAccount.data[selectedInvoiceRow][0].toString())) {
                        return;
                }
                ebiDelete();
            }
        });

        ebiModule.guiRenderer.getButton("historyAccount","Account").setIcon(EBIConstant.ICON_HISTORY);
        ebiModule.guiRenderer.getButton("historyAccount","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("historyAccount","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(Integer.parseInt(tabModAccount.data[selectedInvoiceRow][7].toString()), "Account"), ebiModule).setVisible();
            }
        });

        ebiModule.guiRenderer.getButton("reportAccount","Account").setIcon(EBIConstant.ICON_REPORT);
        ebiModule.guiRenderer.getButton("reportAccount","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                showAccountReport();
            }
        });

        ebiModule.guiRenderer.getButton("importInvoices","Account").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("importInvoices","Account").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                dataControlAccount.imporInvoicetoAccout(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
            }
        });


        //ACCOUNT DOCUMENTS
         ebiModule.guiRenderer.getTable("tableAccountDoc","Account").setModel(tabModDoc);
         ebiModule.guiRenderer.getTable("tableAccountDoc","Account").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableAccountDoc","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableAccountDoc","Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showAccountDoc","Account").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteAccountDoc","Account").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showAccountDoc","Account").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteAccountDoc","Account").setEnabled(true);
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newAccountDoc","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
        });

        ebiModule.guiRenderer.getButton("showAccountDoc","Account").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("showAccountDoc","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("showAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
        });

       ebiModule.guiRenderer.getButton("deleteAccountDoc","Account").setIcon(EBIConstant.ICON_DELETE);
       ebiModule.guiRenderer.getButton("deleteAccountDoc","Account").setEnabled(false);
       ebiModule.guiRenderer.getButton("deleteAccountDoc","Account").addActionListener(new java.awt.event.ActionListener() {

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

       ebiModule.guiRenderer.getComboBox("selectCreditDebitText","Account").setModel(new DefaultComboBoxModel(creditDebitType));
       ebiModule.guiRenderer.getComboBox("selectCreditDebitText","Account").addActionListener(new ActionListener() {

             public void actionPerformed(ActionEvent e) {
                 dataControlAccount.dataShowCreditDebitExt(((JComboBox) e.getSource()).getSelectedIndex());
             }
       });

       ebiModule.guiRenderer.getTable("debCreditTable","Account").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.guiRenderer.getTable("debCreditTable","Account").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedCDRow = ebiModule.guiRenderer.getTable("debCreditTable","Account").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editCreditDebit","Account").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteCreditDebit","Account").setEnabled(false);
                    } else if (!creditDebitMod.data[selectedCDRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editCreditDebit","Account").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteCreditDebit","Account").setEnabled(true);
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newCreditDebit","Account").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newCreditDebit(-1);
                }
        });

        ebiModule.guiRenderer.getButton("editCreditDebit","Account").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editCreditDebit","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("editCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedCDRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(creditDebitMod.data[selectedCDRow][0].toString())) {
                        return;
                    }

                    editCreditDebit(Integer.parseInt(creditDebitMod.data[selectedCDRow][2].toString()));
                }
        });

        ebiModule.guiRenderer.getButton("deleteCreditDebit","Account").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteCreditDebit","Account").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteCreditDebit","Account").addActionListener(new java.awt.event.ActionListener() {

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

        ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                if(((JComboBox)e.getSource()).getSelectedIndex() != -1){
                  selectedYear = ((JComboBox)e.getSource()).getSelectedItem().toString();
                }
             }
         });

         ebiModule.guiRenderer.getButton("updateYear","Account").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){

                if(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount() >= 1){
                    boolean isAvailable = false;
                    if(!"".equals(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem())){
                        for(int i = 0; i<=ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount(); i++ ){
                           if(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().equals(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemAt(i))){
                               isAvailable = true;
                               break;
                           }
                        }
                        if(!isAvailable){
                           ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").addItem(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                        }
                    }else{
                        if(!"".equals(selectedYear)){
                            ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").removeItem(selectedYear);
                        }
                    }
                }else{
                  if(!"".equals(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem())){
                    ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").addItem(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                  }
                }

                // create comma separated value
                String vSave = "";
                if(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount() > 0){

                    for(int i = 0; i<ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount(); i++){

                        if( i < ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount()-1){
                            vSave +=  ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemAt(i).toString()+",";
                        } else {
                            vSave +=  ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemAt(i).toString();
                        }
                    }
                }

                 //  Sort
                if(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getItemCount() > 0){

                    String [] avalItems = vSave.split(",");
                    Arrays.sort(avalItems);
                    String selected = ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getSelectedItem().toString();
                    ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").removeAllItems();
                    vSave = "";
                    for(int i = 0; i< avalItems.length; i++){
                        ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").addItem(avalItems[i]);
                        if( i < avalItems.length-1){
                            vSave +=  avalItems[i] +",";
                        } else {
                            vSave +=  avalItems[i];
                        }
                    }
                    ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").setSelectedItem(selected);
                    dataControlAccount.properties.setValue("ACCOUNTYEAR_TEXT",vSave);
                }


                dataControlAccount.properties.setValue("SELECTED_ACCOUNTYEAR_TEXT",ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());
                dataControlAccount.properties.saveProperties();

                dataControlAccount.dataShow(ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").getEditor().getItem().toString());

             }
         });

        for(int i = 0; i<ebiModule.guiRenderer.getTable("debCreditTable","Account").getColumnCount(); i++){
            TableColumnExt col = ebiModule.guiRenderer.getTable("debCreditTable","Account").getColumnExt(i);
            if(i != 1){
                col.setWidth(40);
                col.setPreferredWidth(40);
            }else{
                col.setWidth(500);
                col.setPreferredWidth(500);
            }
        }

    }
  

    public void initialize() {

        ebiModule.guiRenderer.getTable("debCreditTable","Account").setModel(creditDebitMod);
        ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").setEditable(true);
        ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").removeAllItems();

        ebiModule.guiRenderer.getVisualPanel("Account").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("Account").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Account").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Account").setChangedFrom("");

        ebiModule.guiRenderer.getTable("accountTable","Account").setModel(tabModAccount);
        ebiModule.guiRenderer.getTable("tableAccountDoc","Account").setModel(tabModDoc);

        ebiModule.guiRenderer.getTextfield("numberText","Account").setText("");
        ebiModule.guiRenderer.getTextfield("nameText","Account").setText("");
        ebiModule.guiRenderer.getTextfield("numberText","Account").setHorizontalAlignment(JTextField.RIGHT);
        ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").setText("");
        ebiModule.guiRenderer.getTextfield("descriptionDebit","Account").setEditable(false);

        ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setText("");
        ebiModule.guiRenderer.getTextfield("descriptionCredit","Account").setEditable(false);

        ebiModule.guiRenderer.getTextfield("debitText","Account").setText("");
        ebiModule.guiRenderer.getTextfield("creditText","Account").setText("");

        ebiModule.guiRenderer.getTextarea("descriptionText","Account").setText("");

        taxFormat=NumberFormat.getNumberInstance();
        taxFormat.setMinimumFractionDigits(2);
        taxFormat.setMaximumFractionDigits(2);

        ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").setHorizontalAlignment(SwingConstants.RIGHT);

        ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setHorizontalAlignment(SwingConstants.RIGHT);
        ebiModule.guiRenderer.getFormattedTextfield("debitCal","Account").setEditable(false);

        ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setValue(null);
        ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setHorizontalAlignment(SwingConstants.RIGHT);
        ebiModule.guiRenderer.getFormattedTextfield("creditCal","Account").setEditable(false);

        try {

             if(!"null".equals(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT")) && !"".equals(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"))){
                 ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").setSelectedItem(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"));
                 dataControlAccount.dataShow(dataControlAccount.properties.getValue("SELECTED_ACCOUNTYEAR_TEXT"));
             }else{
                dataControlAccount.dataShow("");
             }

             if(!"null".equals(dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT")) && !"".equals(dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT"))){

                 String [] years = dataControlAccount.properties.getValue("ACCOUNTYEAR_TEXT").split(",");
                 if(years != null){
                     for(int i = 0; i< years.length; i++){
                       if(years[i] != null){
                            ebiModule.guiRenderer.getComboBox("invoiceYearText","Account").insertItemAt(years[i],i);
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
      boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlAccount.dataDelete(i);
        }
    }

    public void editAccount(int i) {
        dataControlAccount.dataEdit(i);
    }

    public void newAccount() {
        dataControlAccount.dataNew(true);
    }

    private void deleteDocs(int i) {
      boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            this.dataControlAccount.dataDeleteDoc(i);
        }
    }

    private void saveAndShowDocs(int i) {
        this.dataControlAccount.dataViewDoc(i);
    }

    private void newDocs() {
        this.dataControlAccount.dataNewDoc();
    }


    private void deleteCreditDebit(int i) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlAccount.dataDeleteCreditDebit(i);
        }
    }

    private void editCreditDebit(int i) {
       newCreditDebit(i);
    }

    private void newCreditDebit(final int id) {
        ebiModule.guiRenderer.loadGUI("CRMDialog/creditDebitDialog.xml");
        ebiModule.guiRenderer.getFormattedTextfield("valueText","creditDebitDialog").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(taxFormat)));
        ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                    if(((JComboBox)e.getSource()).getSelectedIndex() == 3){
                        ebiModule.guiRenderer.getLabel("vale","creditDebitDialog").setVisible(true);
                        ebiModule.guiRenderer.getLabel("taxType","creditDebitDialog").setVisible(false);
                        ebiModule.guiRenderer.getLabel("vale", "creditDebitDialog").setLocation(10, 69);
                        ebiModule.guiRenderer.getFormattedTextfield("valueText", "creditDebitDialog").setVisible(true);
                        ebiModule.guiRenderer.getFormattedTextfield("valueText", "creditDebitDialog").setLocation(110, 69);
                        ebiModule.guiRenderer.getComboBox("taxTypeText", "creditDebitDialog").setVisible(false);
                        ebiModule.guiRenderer.getLabel("taxValue", "creditDebitDialog").setVisible(false);
                    }else{
                        ebiModule.guiRenderer.getLabel("vale","creditDebitDialog").setVisible(false);
                        ebiModule.guiRenderer.getLabel("taxType","creditDebitDialog").setVisible(true);
                        ebiModule.guiRenderer.getFormattedTextfield("valueText","creditDebitDialog").setVisible(false);
                        ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").setVisible(true);
                        ebiModule.guiRenderer.getLabel("taxValue","creditDebitDialog").setVisible(true);
                    }
            }
        });

        ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").setModel(new DefaultComboBoxModel(creditDebitType));
        ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").setModel(new DefaultComboBoxModel(EBICRMProduct.taxType));
        ebiModule.guiRenderer.getComboBox("taxTypeText","creditDebitDialog").addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(((JComboBox)e.getSource()).getSelectedItem() != null){
                    ebiModule.guiRenderer.getLabel("taxValue","creditDebitDialog").setText(dataControlAccount.getTaxValue(((JComboBox)e.getSource()).getSelectedItem().toString())+"%");
                }
            }
        });

        ebiModule.guiRenderer.getButton("saveValue","creditDebitDialog").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!validateInputDialog()){
                    return;
                }

                dataControlAccount.dataStoreCreditDebit(id < 0 ? false : true,id);
            }
        });

        ebiModule.guiRenderer.getButton("closeDialog","creditDebitDialog").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               ebiModule.guiRenderer.getEBIDialog("creditDebitDialog").setVisible(false);
            }
        });

        if(id > -1){
            dataControlAccount.dataEditCreditDebit(id);
        }

        ebiModule.guiRenderer.showGUI();
    }


    private void showCreditDebitListDialog(int type){

       if(type == 3){
           ebiModule.guiRenderer.loadGUI("CRMDialog/crmSelectionDialog.xml");
       }else{
           ebiModule.guiRenderer.loadGUI("CRMDialog/crmAccountingSelectionDialog.xml");
           ebiModule.guiRenderer.getComboBox("accoutingType","abstractSelectionDialog").setModel(new DefaultComboBoxModel(selCreditDebitType));
           ebiModule.guiRenderer.getComboBox("accoutingType","abstractSelectionDialog").addActionListener(new ActionListener() {

               public void actionPerformed(ActionEvent e) {
                   dataControlAccount.dataShowCreditDebitExt(((JComboBox) e.getSource()).getSelectedIndex());
               }
           });
       }

       dataControlAccount.dataShowCreditDebit(type);
       ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_DEBIT_CREDIT_LIST"));

            ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").addKeyListener(new KeyListener(){
                public void keyTyped(KeyEvent e){}

                public void keyPressed(KeyEvent e){
                    ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "abstractSelectionDialog").getText()));
                }
                public void keyReleased(KeyEvent e){
                    ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").getText()));
                }
            });


            ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setModel(creditDebitMod);
            ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedCDDialogRow = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog")).setTableAction(new AbstractTableKeyAction() {

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


            ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()) != -1){
                        selectedCDDialogRow = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()));
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

            ebiModule.guiRenderer.getButton("closeButton","abstractSelectionDialog").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                     ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
                }
            });

            ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                        if (selectedCDDialogRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(creditDebitMod.data[selectedCDDialogRow][0].toString())) {
                            return;
                        }
                       showDebitCreditToList(Integer.parseInt(creditDebitMod.data[selectedCDDialogRow][2].toString()));
                }
            });


        ebiModule.guiRenderer.showGUI();

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
        if( ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog") != null ){
            ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
        }
    }

    private void saveAccount() {
      final Runnable run = new Runnable(){
    	  public void run(){
    		  int row = 0;
    		  
    		  if(isEdit){
    			  row = ebiModule.guiRenderer.getTable("accountTable","Account").getSelectedRow();
    		  }
    		  dataControlAccount.dataStore(isEdit);
    		  
    		  try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
    		  ebiModule.guiRenderer.getTable("accountTable","Account").changeSelection(row,0,false,false);
    	  }
      };
    	
      Thread saveAccount = new Thread(run,"Save Account");
      saveAccount.start();
    }

    private boolean validateInputDialog(){

        if("".equals(ebiModule.guiRenderer.getTextfield("numberText","creditDebitDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.guiRenderer.getTextfield("nameText","creditDebitDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("creditDebitTypeText","creditDebitDialog").getSelectedItem().toString())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private boolean validateInput(){

        if("".equals(ebiModule.guiRenderer.getTimepicker("dateText","Account").getEditor().getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_ILLEGAL_DATE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.guiRenderer.getTextfield("numberText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.guiRenderer.getTextfield("nameText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } else if("".equals(ebiModule.guiRenderer.getFormattedTextfield("amountText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_AMOUNT")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.guiRenderer.getTextfield("debitText","Account").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_DEBIT")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.guiRenderer.getTextfield("creditText","Account").getText())){
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
