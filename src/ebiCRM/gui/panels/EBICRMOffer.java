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
import ebiCRM.data.control.EBIDataControlOffer;
import ebiCRM.gui.dialogs.EBICRMAddContactAddressType;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBIOpportunitySelectionDialog;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelOffer;
import ebiCRM.table.models.MyTableModelReceiver;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMOffer {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelReceiver tabModReceiver = null;
    public MyTableModelCRMProduct tabModProduct = null;
    public MyTableModelOffer tabModoffer = null;
    public boolean isEdit = false;
    public static String[] offerStatus = null;
    public EBIDataControlOffer offerDataControl = null;
    private int selectedDocRow = -1;
    private int selectedReceiverRow = -1;
    private int selectedProductRow = -1;
    private int selectedOfferRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMOffer(EBICRMModule ebiMod) {
        isEdit = false;
        ebiModule = ebiMod;
        tabModDoc = new MyTableModelDoc();
        tabModReceiver = new MyTableModelReceiver();
        tabModProduct = new MyTableModelCRMProduct();
        tabModoffer = new MyTableModelOffer();
        offerDataControl = new EBIDataControlOffer(this);
    }

    public void initializeAction(){

        ebiModule.guiRenderer.getLabel("filterTable","Offer").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Offer").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){
                            }

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyOfferTable","Offer").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Offer").getText()));
}
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyOfferTable","Offer").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Offer").getText()));
}
        });

        ebiModule.guiRenderer.getButton("searchOpportunity","Offer").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchOpportunity","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIOpportunitySelectionDialog dialog = new EBIOpportunitySelectionDialog(ebiModule.getOpportunityPane().opportuniyDataControl.getOppportunityList(), ebiModule);
                    dialog.setVisible();
                    if (dialog.shouldSave) {
 
                        ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").setText(dialog.name);
                        offerDataControl.opportunityID = dialog.id;
                    }
                }
        });

        ebiModule.guiRenderer.getButton("showOpportunity","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (!"".equals(ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").getText())) {
                        ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Opportunity");
                        ebiModule.getOpportunityPane().editOpportunity(offerDataControl.opportunityID);

                    } else {
                        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NO_OPPORTUNITY_SELECTED")).Show(EBIMessage.ERROR_MESSAGE);
                        return;
                    }
                }
        });

        ebiModule.guiRenderer.getButton("saveOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                   ebiSave();
                }
         });


        /*************************************************************************************/
        //   TABLE OFFER DOCUMENT
        /*************************************************************************************/
       
        ebiModule.guiRenderer.getTable("tableOfferDocument","Offer").setModel(tabModDoc);
        ebiModule.guiRenderer.getTable("tableOfferDocument","Offer").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableOfferDocument","Offer").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
 
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableOfferDocument","Offer").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showOfferDoc","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteOfferDOc","Offer").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showOfferDoc","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteOfferDOc","Offer").setEnabled(true);
                    }
                }
            });

         ebiModule.guiRenderer.getButton("newOfferDoc","Offer").setIcon(EBIConstant.ICON_NEW);
         ebiModule.guiRenderer.getButton("newOfferDoc","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
         });

         ebiModule.guiRenderer.getButton("showOfferDoc","Offer").setIcon(EBIConstant.ICON_EXPORT);
         ebiModule.guiRenderer.getButton("showOfferDoc","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("showOfferDoc","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
            });

         ebiModule.guiRenderer.getButton("deleteOfferDOc","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("deleteOfferDOc","Offer").setIcon(EBIConstant.ICON_DELETE);
         ebiModule.guiRenderer.getButton("deleteOfferDOc","Offer").addActionListener(new java.awt.event.ActionListener() {

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

        /*************************************************************************************/
        //   TABLE OFFER PRODUCTS
        /*************************************************************************************/

        ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").setModel(tabModProduct);
        ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteOfferProduct","Offer").setEnabled(false);
                    } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteOfferProduct","Offer").setEnabled(true);
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newOfferProduct","Offer").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newOfferProduct","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(offerDataControl.getOfferPosList(), offerDataControl.getCompOffer(), ebiModule);
                    product.setVisible();
                   
                }
        });
        
        ebiModule.guiRenderer.getButton("deleteOfferProduct","Offer").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteOfferProduct","Offer").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteOfferProduct","Offer").addActionListener(new java.awt.event.ActionListener() {

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

        /*************************************************************************************/
        //   TABLE OFFER RECEIVER
        /*************************************************************************************/
    
        ebiModule.guiRenderer.getTable("tableOfferReceiver","Offer").setModel(tabModReceiver);
        ebiModule.guiRenderer.getTable("tableOfferReceiver","Offer").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("tableOfferReceiver","Offer").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedReceiverRow = ebiModule.guiRenderer.getTable("tableOfferReceiver","Offer").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteOfferReceiver","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("editOfferReceiver","Offer").setEnabled(false);
                    } else if (!tabModReceiver.data[selectedReceiverRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteOfferReceiver","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("editOfferReceiver","Offer").setEnabled(true);
                    }
                }
        });

        ebiModule.guiRenderer.getButton("newOfferReceiver","Offer").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newOfferReceiver","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMAddContactAddressType addCo = new EBICRMAddContactAddressType(ebiModule, offerDataControl);
                    addCo.setVisible();
                }
         });

        ebiModule.guiRenderer.getButton("deleteOfferReceiver","Offer").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteOfferReceiver","Offer").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteOfferReceiver","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }
                   if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteReciever(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][10].toString()));
                   }
                }
         });

        ebiModule.guiRenderer.getButton("editOfferReceiver","Offer").setEnabled(false);
        ebiModule.guiRenderer.getButton("editOfferReceiver","Offer").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editOfferReceiver","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }

                   editReceiver(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][10].toString()));
                }
         });

        /*************************************************************************************/
        //   TABLE AVAILABLE OFFER
        /*************************************************************************************/

            ebiModule.guiRenderer.getTable("companyOfferTable","Offer").setModel(tabModoffer);
            ebiModule.guiRenderer.getTable("companyOfferTable","Offer").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //jTableAvalOffer.setDefaultRenderer(Object.class, new MyOwnCellRederer(true));
            ebiModule.guiRenderer.getTable("companyOfferTable","Offer").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1){
                      try{
                        selectedOfferRow = ebiModule.guiRenderer.getTable("companyOfferTable","Offer").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }catch(IndexOutOfBoundsException ex){

                          selectedOfferRow =0;
                      }
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createOrder","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyOffer","Offer").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveOffer","Offer").setEnabled(false);
                    } else if (!tabModoffer.data[selectedOfferRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createOrder","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyOffer","Offer").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveOffer","Offer").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyOfferTable","Offer")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedOfferRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedOfferRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedOfferRow = selRow;
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                    }

                    editOffer(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("offerNrText","Offer").grabFocus();
                }
                
            });

            ebiModule.guiRenderer.getTable("companyOfferTable","Offer").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("companyOfferTable","Offer").rowAtPoint(e.getPoint())  != -1){
                        selectedOfferRow = ebiModule.guiRenderer.getTable("companyOfferTable","Offer").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyOfferTable","Offer").rowAtPoint(e.getPoint()));
                    }
        
                    if (e.getClickCount() == 2) {

                        if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                            return;
                        }

                        editOffer(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
                        ebiModule.guiRenderer.getTextfield("offerNrText","Offer").grabFocus();
                    }
                }
            });

         ebiModule.guiRenderer.getButton("newOffer","Offer").setIcon(EBIConstant.ICON_NEW);
         ebiModule.guiRenderer.getButton("newOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
         });
         
         
         ebiModule.guiRenderer.getButton("copyOffer","Offer").setIcon(EBIConstant.ICON_COPY);
         ebiModule.guiRenderer.getButton("copyOffer","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("copyOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                    }
                    offerDataControl.dataCopy(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
                }
         });

         ebiModule.guiRenderer.getButton("moveOffer","Offer").setIcon(EBIConstant.ICON_MOVE_RECORD);
         ebiModule.guiRenderer.getButton("moveOffer","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("moveOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                    return;
                }
                offerDataControl.dataMove(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
            }
         });

         ebiModule.guiRenderer.getButton("editOffer","Offer").setIcon(EBIConstant.ICON_EDIT);
         ebiModule.guiRenderer.getButton("editOffer","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("editOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                    }

                    editOffer(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("offerNrText","Offer").grabFocus();
                }
         });

         ebiModule.guiRenderer.getButton("reportOffer","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("reportOffer","Offer").setIcon(EBIConstant.ICON_REPORT);
         ebiModule.guiRenderer.getButton("reportOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                    }

                    showOfferReport(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
                }
            });

         ebiModule.guiRenderer.getButton("deleteOffer","Offer").setEnabled(false);
         ebiModule.guiRenderer.getButton("deleteOffer","Offer").setIcon(EBIConstant.ICON_DELETE);
         ebiModule.guiRenderer.getButton("deleteOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                    }
                    ebiDelete();
                }
            });

            ebiModule.guiRenderer.getButton("historyOffer","Offer").setEnabled(false);
            ebiModule.guiRenderer.getButton("historyOffer","Offer").setIcon(EBIConstant.ICON_HISTORY);
            ebiModule.guiRenderer.getButton("historyOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Offer"), ebiModule).setVisible();
                }
            });
          
            ebiModule.guiRenderer.getButton("mailOffer","Offer").setEnabled(false);
            ebiModule.guiRenderer.getButton("mailOffer","Offer").setIcon(EBIConstant.ICON_SEND_MAIL);
            ebiModule.guiRenderer.getButton("mailOffer","Offer").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                            return;
                    }

                  offerDataControl.dataShowAndMailReport(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()), false);

                }
           });

        ebiModule.guiRenderer.getButton("createOrder","Offer").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("createOrder","Offer").setEnabled(false);
        ebiModule.guiRenderer.getButton("createOrder","Offer").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                 if (selectedOfferRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModoffer.data[selectedOfferRow][0].toString())) {
                        return;
                 }
                ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Order");
                offerDataControl.createOrderFromOffer(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
            }
        });
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {

        ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("Offer").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Offer").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Offer").setChangedFrom("");

        ebiModule.guiRenderer.getTable("tableOfferDocument","Offer").setModel(tabModDoc);
        ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").setModel(tabModProduct);
        ebiModule.guiRenderer.getTable("tableOfferReceiver","Offer").setModel(tabModReceiver);
        ebiModule.guiRenderer.getTable("companyOfferTable","Offer").setModel(tabModoffer);

        TableColumn col7 = ebiModule.guiRenderer.getTable("tableOfferProduct","Offer").getColumnModel().getColumn(5);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setModel(new javax.swing.DefaultComboBoxModel(offerStatus));
        ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").setSelectedIndex(0);

        ebiModule.guiRenderer.getTextfield("offerNrText","Offer").setText("");
        ebiModule.guiRenderer.getTextfield("offerNameText","Offer").setText("");
        ebiModule.guiRenderer.getTextfield("offerOpportunityText","Offer").setText("");
        ebiModule.guiRenderer.getTextarea("offerDescriptionText","Offer").setText("");

        ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer").setFormats(EBIPGFactory.DateFormat);
        ebiModule.guiRenderer.getTimepicker("validToText","Offer").setFormats(EBIPGFactory.DateFormat);

        ebiModule.guiRenderer.getTimepicker("offerReceiverText","Offer")
                    .getEditor().setText(ebiModule.ebiPGFactory.getDateToString(new Date()));

        ebiModule.guiRenderer.getTimepicker("validToText","Offer")
                    .getEditor().setText(ebiModule.ebiPGFactory.getDateToString(new Date()));

        offerDataControl.dataShow();
    }

    private void newDocs() {
        offerDataControl.dataAddDoc();
    }

    private void saveAndShowDocs(int id) {
        offerDataControl.dataViewDoc(id);
    }

    public void showProduct() {
        offerDataControl.dataShowProduct();
    }

    /**
     * Reset Offer
     *
     */
    private void newOffer() {
        isEdit = false;
        offerDataControl.dataNew(true);
    }

    /**
     * Save offer
     *
     */
    public void saveOffer() {

      final Runnable run = new Runnable(){
	       public void run(){	  
		        if (!validateInput()) {
		            return;
		        }
		        int row = 0;
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("companyOfferTable","Offer").getSelectedRow();
		        }
		        offerDataControl.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyOfferTable","Offer").changeSelection(row,0,false,false);
	       }
      };		
      
      Thread save = new Thread(run,"Save Offer");
      save.start();
      
    }

    public void editOffer(int id) {
        isEdit = true;
        offerDataControl.dataEdit(id);

    }

    private boolean validateInput() {
        if ("".equals(ebiModule.guiRenderer.getTextfield("offerNameText","Offer").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (ebiModule.guiRenderer.getComboBox("offerStatusText","Offer").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (isEdit == false) {
            for (int i = 0; i < this.tabModoffer.data.length; i++) {
                if (this.tabModoffer.data[i][0].equals(ebiModule.guiRenderer.getTextfield("offerNameText","Offer").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_OFFER_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    public void showOffer() {
        offerDataControl.dataShow();
    }

    private void deleteDocs(int id) {
        offerDataControl.dataDeleteDoc(id);

    }

    private void deleteReciever(int id) {
        offerDataControl.dataDeleteReceiver(id);
    }

    private void editReceiver(int id) {
        offerDataControl.dataEditReceiver(id);
    }

    private void deleteProduct(int id) {
        offerDataControl.dataDeleteProduct(id);
    }

    private boolean showOfferReport(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            offerDataControl.dataShowReport(id);
        }
        return pass;
    }

    private void deleteOffer(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            offerDataControl.dataDelete(id);
            newOffer();
        }
    }

    public void ebiNew(){
        newOffer();
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
            saveOffer();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteOffer(Integer.parseInt(tabModoffer.data[selectedOfferRow][7].toString()));
        }
    }

}

