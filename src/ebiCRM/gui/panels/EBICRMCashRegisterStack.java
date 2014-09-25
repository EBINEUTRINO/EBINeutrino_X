package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlCashRegister;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMCashRegisterStack {

    public EBICRMModule ebiModule = null;
    public EBIDataControlCashRegister dataControlCashRegister = null;
    public boolean isEdit = false;
    public MyTableModelCRMProduct tabModProduct = null;
    public int selectedProductRow = -1;
    public int selectedCashRow = -1;
    public boolean isText1 = false;
    public int counter = 0;
    private EBIAbstractTableModel model = null;

    public EBICRMCashRegisterStack(final EBICRMModule modul){


            ebiModule = modul;
            try {
                ebiModule.ebiPGFactory.hibernate.openHibernateSession("CASH_SESSION");
                ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tabModProduct = new MyTableModelCRMProduct();
            tabModProduct.columnNames = new String[] {

                    EBIPGFactory.getLANG("EBI_LANG_QUANTITY"),
                    EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NUMBER"),
                    EBIPGFactory.getLANG("EBI_LANG_NAME"),
                    EBIPGFactory.getLANG("EBI_LANG_PRICE"),
                    EBIPGFactory.getLANG("EBI_LANG_DEDUCTION")

            };
            dataControlCashRegister = new EBIDataControlCashRegister(this);

    }

    public void initializeAction() {
        showCashRegister();

        model  = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getModel();

        ebiModule.guiRenderer.getComboBox("payStatusText","CashRegister").setModel(new DefaultComboBoxModel(EBICRMInvoice.invoiceStatus));

        ebiModule.guiRenderer.getLabel("filterTable","CashRegister").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","CashRegister").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "CashRegister").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","CashRegister").getText()));
            }
        });

        ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").setFont(new Font("Arial", Font.BOLD, 16));

        ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isText1= false;
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isText1= true;
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });

        ActionListener act = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!isText1){
                    ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").setText( ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getText()+((JButton)e.getSource()).getText());
                }else{
                    ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").setText(ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText()+((JButton)e.getSource()).getText());
                }
            }
        };

        ebiModule.guiRenderer.getButton("bntZero","CashRegister").addActionListener(act);

        ebiModule.guiRenderer.getButton("bntOne","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntTwo","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntThree","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntFour","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntFive","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntSix","CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntSeven" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntEight" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntNine" ,"CashRegister").addActionListener(act);

        ebiModule.guiRenderer.getButton("bntA" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntB" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntC" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntD" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntE" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntF" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntG" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntH" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntI" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntJ" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntK" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntL" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntM" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntN" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntO" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntP" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntQ" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntR" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntS" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntT" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntU" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntV" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntW" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntX" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntY" ,"CashRegister").addActionListener(act);
        ebiModule.guiRenderer.getButton("bntZ" ,"CashRegister").addActionListener(act);

        ebiModule.guiRenderer.getButton("backSpaceBnt","CashRegister").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int lenght;

                if(!isText1){
                    lenght = ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getText().length();
                    if(lenght >0){
                        ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").setText(ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getText().substring(0,lenght-1));
                    }
                }else{
                    lenght = ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText().length();
                    if(lenght > 0){
                        ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").setText(ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText().substring(0,lenght-1));
                    }
                }

            }
        });


        Action txtEnter = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                 loadProduct();
            }
        };


       ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOADPRODUCT");
       ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getActionMap().put("LOADPRODUCT", txtEnter);

       ebiModule.guiRenderer.getButton("bntLoad","CashRegister").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 loadProduct();
            }
        });

       ebiModule.guiRenderer.getButton("bntSave","CashRegister").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    ebiSave();
              
            }
        });

        /***************************************************************************/
        // CASH REGISTER TABLE
        /***************************************************************************/

        ebiModule.guiRenderer.getButton("newCash","CashRegister").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newCash","CashRegister").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                   ebiNew();
            }
        });


        ebiModule.guiRenderer.getButton("editCash","CashRegister").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editCash","CashRegister").setEnabled(false);
        ebiModule.guiRenderer.getButton("editCash","CashRegister").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                  editCash(Integer.parseInt(model.data[selectedCashRow][4].toString()));
            }
        });

        ebiModule.guiRenderer.getButton("deleteCash","CashRegister").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteCash","CashRegister").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteCash","CashRegister").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedCashRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedCashRow][0].toString())) {
                        return;
                }

               ebiDelete();
            }
        });

        ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").setEnabled(false);
        ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                if (selectedCashRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedCashRow][0].toString())) {
                        return;
                }

                ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Invoice");
                ebiModule.getInvoicePane().dataControlInvoice.dataEdit(Integer.parseInt(model.data[selectedCashRow][4].toString()));

            }
        });

        ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                       try{
                         selectedCashRow = ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").convertRowIndexToModel(lsm.getMinSelectionIndex());
                       }catch(IndexOutOfBoundsException ex){}
                       
                    }
                   try{
                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("editCash","CashRegister").setEnabled(false);
                            ebiModule.guiRenderer.getButton("deleteCash","CashRegister").setEnabled(false);
                            ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").setEnabled(false);
                        } else if (!model.data[selectedCashRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("editCash","CashRegister").setEnabled(true);
                            ebiModule.guiRenderer.getButton("deleteCash","CashRegister").setEnabled(true);
                            ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").setEnabled(true);

                        }
                   }catch(ArrayIndexOutOfBoundsException ex){
                        ebiModule.guiRenderer.getButton("editCash","CashRegister").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteCash","CashRegister").setEnabled(false);
                        ebiModule.guiRenderer.getButton("invoiceCash","CashRegister").setEnabled(false);
                   }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedCashRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedCashRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedCashRow = selRow;

                    if (selectedCashRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedCashRow][0].toString())) {
                        return;
                    }
                    editCash(Integer.parseInt(model.data[selectedCashRow][4].toString()));
                }
            });


            ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                   if(ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").rowAtPoint(e.getPoint()) != -1){
                        selectedCashRow = ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").convertRowIndexToModel(ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").rowAtPoint(e.getPoint()));
                   }

                   if (e.getClickCount() == 2) {

                        if (selectedCashRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedCashRow][0].toString())) {
                            return;
                        }

                        editCash(Integer.parseInt(model.data[selectedCashRow][4].toString()));
                   }
                }
         });

        /***************************************************************************/
        // PRODUCT TABLE
        /***************************************************************************/

        ebiModule.guiRenderer.getTable("tableCashProduct","CashRegister").setModel(tabModProduct);
        TableColumn col7 = ebiModule.guiRenderer.getTable("tableCashProduct","CashRegister").getColumnModel().getColumn(3);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiModule.guiRenderer.getTable("tableCashProduct","CashRegister").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableCashProduct","CashRegister").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if(lsm.getMinSelectionIndex() != -1){
                    selectedProductRow = ebiModule.guiRenderer.getTable("tableCashProduct","CashRegister").convertRowIndexToModel(lsm.getMinSelectionIndex());
                }
                if (lsm.isSelectionEmpty()) {
                    ebiModule.guiRenderer.getButton("deleteCashProduct","CashRegister").setEnabled(false);
                } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                    ebiModule.guiRenderer.getButton("deleteCashProduct","CashRegister").setEnabled(true);
                }
            }
        });


        ebiModule.guiRenderer.getButton("deleteCashProduct","CashRegister").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteCashProduct","CashRegister").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteCashProduct","CashRegister").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModProduct.data[selectedProductRow][0].toString())) {
                    return;
                }

                if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                    deleteProduct(Integer.parseInt(tabModProduct.data[selectedProductRow][8].toString()));
                }

            }
        });

        ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dataControlCashRegister.dataShow(ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());
            }
        });

        if(!"".equals(EBIPGFactory.properties.getValue("CASH_NAME"))){
            ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").setText(EBIPGFactory.properties.getValue("CASH_NAME"));
        }

    }

    private void loadProduct() {
           if(!"".equals(ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getText())){
                dataControlCashRegister.insertProduct(ebiModule.guiRenderer.getTextfield("txtNumber","CashRegister").getText().trim()+"%",false);
           }
    }

    public void initialize() {
        ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").setDate(
                ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate() == null ? new Date() :
                    ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());

        ebiModule.guiRenderer.getEditor("cashView","CashRegister").setEditable(false);
        ebiModule.guiRenderer.getEditor("cashView","CashRegister").setText("");
        ebiModule.guiRenderer.getVisualPanel("CashRegister").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("CashRegister").setCreatedFrom(EBIPGFactory.ebiUser);
    }

    private void newCash() {
         dataControlCashRegister.dataNew(true);
    }

    private void editCash(int i) {
           dataControlCashRegister.dataEdit(i);
    }

    private void deleteCash(int i) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
           dataControlCashRegister.dataDelete(i);
        }
    }

    private void showCashRegister() {
        dataControlCashRegister.dataShow(ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());
    }


    private void deleteProduct(int i) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlCashRegister.deleteProduct(i);
        }

    }

    private boolean validateInput() {

        if ("".equals(ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (this.dataControlCashRegister.invoice.getCrminvoicepositions().size() <= 0 ) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PRODUCT_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void ebiNew(){
        newCash();
    }

    public void ebiSave(){

        final Runnable run = new Runnable(){

            public void run(){
                if(!validateInput()){
                    return;
                }

                int row = 0;

                if(isEdit){
                    row = ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getSelectedRow();
                }

                dataControlCashRegister.dataStore(isEdit);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").changeSelection(row,0,false,false);
            }
        };

        Thread save = new Thread(run,"Save Cashregister");
        save.start();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteCash(Integer.parseInt(model.data[selectedCashRow][4].toString()));
        }
    }

}
