package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlOrder;
import ebiCRM.gui.dialogs.EBICRMAddContactAddressType;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBIOfferSelectionDialog;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelOrder;
import ebiCRM.table.models.MyTableModelReceiver;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMOrder {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelReceiver tabModReceiver = null;
    public MyTableModelCRMProduct tabModProduct = null;
    public MyTableModelOrder tabModOrder = null;
    public boolean isEdit = false;
    public static String[] orderStatus = null;
    public EBIDataControlOrder orderDataControl = null;
    private int selectedOrderRow = -1;
    private int selectedDocRow = -1;
    private int selectedReceiverRow = -1;
    private int selectedProductRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMOrder(EBICRMModule ebiMod) {
        isEdit = false;
        ebiModule = ebiMod;
        tabModDoc = new MyTableModelDoc();
        tabModReceiver = new MyTableModelReceiver();
        tabModProduct = new MyTableModelCRMProduct();
        tabModOrder = new MyTableModelOrder();
        orderDataControl = new EBIDataControlOrder(this);
    }

    public void initializeAction(){
        ebiModule.guiRenderer.getLabel("filterTable","Order").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Order").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyorderTable","Order").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Order").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyorderTable","Order").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Order").getText()));
            }
        });


        ebiModule.guiRenderer.getButton("searchOffer","Order").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchOffer","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIOfferSelectionDialog dialog = new EBIOfferSelectionDialog(orderDataControl.getOfferList(), ebiModule);
                    dialog.setVisible();
                    if (dialog.shouldSave) {
                        ebiModule.guiRenderer.getTextfield("orderOfferText","Order").setText(dialog.name);
                        orderDataControl.setOfferID(dialog.id);
                    }
                }
        });

        ebiModule.guiRenderer.getButton("showOffer","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    
                    if (!"".equals(ebiModule.guiRenderer.getTextfield("orderOfferText","Order").getText())) {
                       ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Offer");
                       ebiModule.getOfferPane().editOffer(orderDataControl.getOfferID());

                    } else {
                        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NO_OFFER_SELECTED")).Show(EBIMessage.ERROR_MESSAGE);
                        return;
                    }
                }
        });

        ebiModule.guiRenderer.getButton("saveOrder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                   ebiSave();
                }
            });

        /**************************************************************************************/
        //  ORDER TABLE DOCUMENT
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("tableorderDocument","Order").setModel(tabModDoc);
         ebiModule.guiRenderer.getTable("tableorderDocument","Order").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableorderDocument","Order").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableorderDocument","Order").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showorderDoc","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteorderDoc","Order").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showorderDoc","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteorderDoc","Order").setEnabled(true);
                    }
                }
         });

        ebiModule.guiRenderer.getButton("neworderDoc","Order").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("neworderDoc","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    orderDataControl.dataNewDoc();
                }
        });

        ebiModule.guiRenderer.getButton("showorderDoc","Order").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("showorderDoc","Order").setEnabled(false);
        ebiModule.guiRenderer.getButton("showorderDoc","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    orderDataControl.dataViewDoc(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
        });

        ebiModule.guiRenderer.getButton("deleteorderDoc","Order").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteorderDoc","Order").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteorderDoc","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        orderDataControl.dataDeleteDoc(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                    }

                }
         });

        /**************************************************************************************/
        //  ORDER TABLE PRODUCT
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("tableorderProduct","Order").setModel(tabModProduct);
         ebiModule.guiRenderer.getTable("tableorderProduct","Order").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableorderProduct","Order").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                   
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("tableorderProduct","Order").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if(tabModProduct.data.length > 0){
                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("deleteorderProduct","Order").setEnabled(false);
                        } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("deleteorderProduct","Order").setEnabled(true);
                        }
                    }
                }
            });

           ebiModule.guiRenderer.getButton("neworderProduct","Order").setIcon(EBIConstant.ICON_NEW);
           ebiModule.guiRenderer.getButton("neworderProduct","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(orderDataControl.getOrderPosList(), orderDataControl.getCompOrder(), ebiModule);
                    product.setVisible();

                }
            });
        
           ebiModule.guiRenderer.getButton("deleteorderProduct","Order").setIcon(EBIConstant.ICON_DELETE);
           ebiModule.guiRenderer.getButton("deleteorderProduct","Order").setEnabled(false);
           ebiModule.guiRenderer.getButton("deleteorderProduct","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProduct.data[selectedProductRow][0].toString())) {                                     
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        orderDataControl.dataDeleteProduct(Integer.parseInt(tabModProduct.data[selectedProductRow][8].toString()));
                    }

                }
            });
        
        /**************************************************************************************/
        //  ORDER TABLE RECEIVER
        /**************************************************************************************/
       
       ebiModule.guiRenderer.getTable("tableOrderReceiver","Order").setModel(tabModReceiver);
       ebiModule.guiRenderer.getTable("tableOrderReceiver","Order").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.guiRenderer.getTable("tableOrderReceiver","Order").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedReceiverRow = ebiModule.guiRenderer.getTable("tableOrderReceiver","Order").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteorderReceiver","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("editOrderReceiver","Order").setEnabled(false);
                    } else if (!tabModReceiver.data[selectedReceiverRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteorderReceiver","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("editOrderReceiver","Order").setEnabled(true);
                    }
                }
            });

          ebiModule.guiRenderer.getButton("neworderReceiver","Order").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("neworderReceiver","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMAddContactAddressType addCo = new EBICRMAddContactAddressType(ebiModule, orderDataControl);
                    addCo.setVisible();
                }
          });

          ebiModule.guiRenderer.getButton("deleteorderReceiver","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteorderReceiver","Order").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteorderReceiver","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        orderDataControl.dataDeleteReceiver(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][10].toString()));
                    }
                }
          });

          ebiModule.guiRenderer.getButton("editOrderReceiver","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("editOrderReceiver","Order").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editOrderReceiver","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }

                    orderDataControl.dataEditReceiver(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][10].toString()));
                }
          });

        /**************************************************************************************/
        //  AVAILABLE ORDER TABLE 
        /**************************************************************************************/
        
         ebiModule.guiRenderer.getTable("companyorderTable","Order").setModel(tabModOrder);
         ebiModule.guiRenderer.getTable("companyorderTable","Order").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         //jTableAvalOrder.setDefaultRenderer(Object.class, new MyOwnCellRederer(false));
         ebiModule.guiRenderer.getTable("companyorderTable","Order").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1 && lsm.getMinSelectionIndex() < tabModOrder.data.length ){
                         selectedOrderRow = ebiModule.guiRenderer.getTable("companyorderTable","Order").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }else{
                        selectedOrderRow = 0;
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editorder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportorder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteorder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyorder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailOrder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createService","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createInvoice","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyorder","Order").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveorder","Order").setEnabled(false);
                    } else if (!tabModOrder.data[selectedOrderRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editorder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportorder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteorder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyorder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailOrder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createService","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createInvoice","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyorder","Order").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveorder","Order").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyorderTable","Order")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedOrderRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedOrderRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedOrderRow = selRow;

                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                    editOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("orderNrText","Order").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("companyorderTable","Order").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                   if(ebiModule.guiRenderer.getTable("companyorderTable","Order").rowAtPoint(e.getPoint()) != -1){
                    selectedOrderRow = ebiModule.guiRenderer.getTable("companyorderTable","Order").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyorderTable","Order").rowAtPoint(e.getPoint()));
                   }
                    if (e.getClickCount() == 2) {

                        if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                            return;
                        }

                        editOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                        ebiModule.guiRenderer.getTextfield("orderNrText","Order").grabFocus();
                    }
                }
            });

          ebiModule.guiRenderer.getButton("neworder","Order").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("neworder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
          });
          
          ebiModule.guiRenderer.getButton("copyorder","Order").setIcon(EBIConstant.ICON_COPY);
          ebiModule.guiRenderer.getButton("copyorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("copyorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                    							equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                    orderDataControl.dataCopy(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("moveorder","Order").setIcon(EBIConstant.ICON_MOVE_RECORD);
          ebiModule.guiRenderer.getButton("moveorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("moveorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                    orderDataControl.dataMove(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("editorder","Order").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("editorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                    
                    editOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("orderNrText","Order").grabFocus();
                }
          });

          ebiModule.guiRenderer.getButton("deleteorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteorder","Order").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                   ebiDelete();

                }
          });
        
          ebiModule.guiRenderer.getButton("reportorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("reportorder","Order").setIcon(EBIConstant.ICON_REPORT);
          ebiModule.guiRenderer.getButton("reportorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }

                    showOrderReport(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("historyorder","Order").setEnabled(false);
          ebiModule.guiRenderer.getButton("historyorder","Order").setIcon(EBIConstant.ICON_HISTORY);
          ebiModule.guiRenderer.getButton("historyorder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Order"), ebiModule).setVisible();
                }
            });

        ebiModule.guiRenderer.getButton("mailOrder","Order").setEnabled(false);
        ebiModule.guiRenderer.getButton("mailOrder","Order").setIcon(EBIConstant.ICON_SEND_MAIL);
        ebiModule.guiRenderer.getButton("mailOrder","Order").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                            return;
                    }

                    mailOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
             }
        });

        ebiModule.guiRenderer.getButton("createService","Order").setIcon(EBIConstant.ICON_NEW_BLANK);
        ebiModule.guiRenderer.getButton("createService","Order").setEnabled(false);
        ebiModule.guiRenderer.getButton("createService","Order").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                   if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Service");
                orderDataControl.createServiceFromOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
            }
        });

        ebiModule.guiRenderer.getButton("createInvoice","Order").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("createInvoice","Order").setEnabled(false);
        ebiModule.guiRenderer.getButton("createInvoice","Order").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                   if (selectedOrderRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModOrder.data[selectedOrderRow][0].toString())) {
                        return;
                    }
                ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Invoice");
                orderDataControl.createInvoiceFromOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
                ebiModule.getInvoicePane().dataControlInvoice.calculateTotalAmount();
            }
        });
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {

        ebiModule.guiRenderer.getVisualPanel("Order").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        ebiModule.guiRenderer.getVisualPanel("Order").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Order").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Order").setChangedFrom("");

        ebiModule.guiRenderer.getTable("tableorderDocument","Order").setModel(tabModDoc);
        ebiModule.guiRenderer.getTable("tableorderProduct","Order").setModel(tabModProduct);
        ebiModule.guiRenderer.getTable("tableOrderReceiver","Order").setModel(tabModReceiver);
        ebiModule.guiRenderer.getTable("companyorderTable","Order").setModel(tabModOrder);

        TableColumn col7 = ebiModule.guiRenderer.getTable("tableorderProduct","Order").getColumnModel().getColumn(5);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setModel(new javax.swing.DefaultComboBoxModel(orderStatus));
        ebiModule.guiRenderer.getComboBox("orderStatusText","Order").setSelectedIndex(0);

        ebiModule.guiRenderer.getTextfield("orderNrText","Order").setText("");
        ebiModule.guiRenderer.getTextfield("orderNameText","Order").setText("");
        ebiModule.guiRenderer.getTextfield("orderOfferText","Order").setText("");
        ebiModule.guiRenderer.getTextarea("orderDescription","Order").setText("");


        ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").setFormats(EBIPGFactory.DateFormat);
        ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").setFormats(EBIPGFactory.DateFormat);
        ebiModule.guiRenderer.getTimepicker("orderCreatedText","Order").getEditor().setText(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getTimepicker("orderReceiveText","Order").getEditor().setText(ebiModule.ebiPGFactory.getDateToString(new Date()));

        orderDataControl.dataShow();
    }


    public void showProduct() {
        orderDataControl.dataShowProduct();
    }

    private void newOrder() {
        isEdit = false;
        orderDataControl.dataNew(true);
    }

    public void saveOrder() {
      final Runnable run = new Runnable(){  
	       public void run(){	
		    	if (!validateInput()) {
		            return;
		        }
		    	int row = 0;
		    	if(isEdit){
		    		row = ebiModule.guiRenderer.getTable("companyorderTable","Order").getSelectedRow();
		    	}
		        orderDataControl.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyorderTable","Order").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Order");
      save.start();
    }

    public void editOrder(int id) {
        orderDataControl.dataEdit(id);

        isEdit = true;
    }

    private void deleteOrder(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            orderDataControl.dataDelete(id);
        }
    }

    private boolean validateInput() {
        
        if ("".equals(ebiModule.guiRenderer.getTextfield("orderNrText","Order").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (ebiModule.guiRenderer.getComboBox("orderStatusText","Order").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (isEdit == false) {
            for (int i = 0; i < this.tabModOrder.data.length; i++) {
                if (this.tabModOrder.data[i][0].equals(ebiModule.guiRenderer.getTextfield("orderNrText","Order").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_ORDER_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    public void showOrder() {
        orderDataControl.dataShow();
    }


    private void showOrderReport(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            orderDataControl.dataShowReport(id);
        }
    }

    public void mailOrder(final int id){

            boolean pass;
            if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                    ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                pass = true;
            } else {
                pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
            }

            if (pass) {
                orderDataControl.dataShowAndMailReport(id, false);
            }
    }

    public void ebiNew(){
        newOrder();
    }

    public void ebiSave(){
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanSave() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            saveOrder();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteOrder(Integer.parseInt(tabModOrder.data[selectedOrderRow][7].toString()));
        }
    }

}