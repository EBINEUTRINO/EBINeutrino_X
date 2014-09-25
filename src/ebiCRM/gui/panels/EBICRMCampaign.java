package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlCampaign;
import ebiCRM.gui.dialogs.EBICRMAddContactAddressType;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.table.models.MyTableModelCampaign;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelProperties;
import ebiCRM.table.models.MyTableModelReceiver;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMCampaign {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelReceiver tabModReceiver = null;
    public MyTableModelCRMProduct tabModProduct = null;
    public MyTableModelCampaign tabModelCampaign = null;
    public boolean isEdit = false;
    public static String[] campaignStatus = null;
    public MyTableModelProperties tabModProperties = null;
    public EBIDataControlCampaign dataControlCampaign = null;
    private int selectedDocRow = -1;
    private int selectedProductRow = -1;
    private int selectedReceiverRow = -1;
    private int selectedCampaignRow = -1;
    private int selectedPropertiesRow = -1;
    
    /**
     * This is the default constructor
     */
    public EBICRMCampaign(EBICRMModule ebiMod) {
        ebiModule = ebiMod;
        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("CAMPAIGN_SESSION");
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CAMPAIGN_SESSION").begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabModProperties = new MyTableModelProperties();
        tabModDoc = new MyTableModelDoc();
        tabModReceiver = new MyTableModelReceiver(true);
        tabModProduct = new MyTableModelCRMProduct();
        tabModelCampaign = new MyTableModelCampaign();
        dataControlCampaign = new EBIDataControlCampaign(this);
        showCampaign();

    }


    public void initializeAction(){

        ebiModule.guiRenderer.getLabel("filterTable","Campaign").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Campaign").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Campaign").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Campaign").getText()));
            }
        });
                                      
        ebiModule.guiRenderer.getComboBox("CampaignStatusText","Campaign").setModel(new javax.swing.DefaultComboBoxModel(campaignStatus));

        ebiModule.guiRenderer.getTimepicker("campaignValidFromText","Campaign").setFormats(EBIPGFactory.DateFormat);
        ebiModule.guiRenderer.getTimepicker("campaingValidToText","Campaign").setFormats(EBIPGFactory.DateFormat);

        /***************************************************************************/
        // CAMPAIGN PROPERTIES TABLE
        /***************************************************************************/

        ebiModule.guiRenderer.getTable("CampaignPropertiesTable","Campaign").setModel(tabModProperties);
        ebiModule.guiRenderer.getTable("CampaignPropertiesTable","Campaign").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("CampaignPropertiesTable","Campaign").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                 public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                           return;
                        }
                        ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                      
                        if(lsm.getMinSelectionIndex() != -1){
                            selectedPropertiesRow = ebiModule.guiRenderer.getTable("CampaignPropertiesTable","Campaign").convertRowIndexToModel(lsm.getMinSelectionIndex());
                        }
                        if (lsm.isSelectionEmpty()) {
                             ebiModule.guiRenderer.getButton("deleteCampaingProperties","Campaign").setEnabled(false);
                             ebiModule.guiRenderer.getButton("editCampaingProperties","Campaign").setEnabled(false);
                        } else if (!tabModProperties.data[selectedPropertiesRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                                ebiModule.guiRenderer.getButton("deleteCampaingProperties","Campaign").setEnabled(true);
                                ebiModule.guiRenderer.getButton("editCampaingProperties","Campaign").setEnabled(true);
                        }
               }
         });


        ebiModule.guiRenderer.getButton("newCampaingProperties","Campaign").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newCampaingProperties","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                     newCampaignProperties();
                }
        });


        ebiModule.guiRenderer.getButton("editCampaingProperties","Campaign").setEnabled(false);
        ebiModule.guiRenderer.getButton("editCampaingProperties","Campaign").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editCampaingProperties","Campaign").addActionListener(new java.awt.event.ActionListener() {

                 public void actionPerformed(java.awt.event.ActionEvent e) {
                      if (selectedPropertiesRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                               equals(tabModProperties.data[selectedPropertiesRow][0].toString())) {
                               return;
                      }

                       editCampaignProperties(Integer.parseInt(tabModProperties.data[selectedPropertiesRow][2].toString()));

                 }
        });

        ebiModule.guiRenderer.getButton("deleteCampaingProperties","Campaign").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteCampaingProperties","Campaign").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteCampaingProperties","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (selectedPropertiesRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModProperties.data[selectedPropertiesRow][0].toString())) {
                                return;
                        }
                     deleteCampaignProperties(Integer.parseInt(tabModProperties.data[selectedPropertiesRow][2].toString()));

                }
        });

        /***************************************************************************/
        // CAMPAIGN DOCUMENT TABLE
        /***************************************************************************/

         ebiModule.guiRenderer.getTable("tableCampaignDocument","Campaign").setModel(tabModDoc);
         ebiModule.guiRenderer.getTable("tableCampaignDocument","Campaign").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableCampaignDocument","Campaign").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                 
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableCampaignDocument","Campaign").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showCampaignDoc","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteCampaignDoc","Campaign").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showCampaignDoc","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteCampaignDoc","Campaign").setEnabled(true);
                    }
                }
            });

         ebiModule.guiRenderer.getButton("newCampaignDoc","Campaign").setIcon(EBIConstant.ICON_NEW);
         ebiModule.guiRenderer.getButton("newCampaignDoc","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
          });

          ebiModule.guiRenderer.getButton("showCampaignDoc","Campaign").setIcon(EBIConstant.ICON_EXPORT);
          ebiModule.guiRenderer.getButton("showCampaignDoc","Campaign").setEnabled(false);
          ebiModule.guiRenderer.getButton("showCampaignDoc","Campaign").addActionListener(new java.awt.event.ActionListener() {

               public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 ||
                            EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("deleteCampaignDoc","Campaign").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteCampaignDoc","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteCampaignDoc","Campaign").addActionListener(new java.awt.event.ActionListener() {

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

          /***************************************************************************/
          // CAMPAIGN PRODUCT TABLE
          /***************************************************************************/

            ebiModule.guiRenderer.getTable("tableCampaignProduct","Campaign").setModel(tabModProduct);
            TableColumn col7 = ebiModule.guiRenderer.getTable("tableCampaignProduct","Campaign").getColumnModel().getColumn(5);
            col7.setCellRenderer(new DefaultTableCellRenderer(){
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                    JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                    myself.setHorizontalAlignment(SwingConstants.RIGHT);
                    myself.setForeground(new Color(255,60,60));
                    return myself;
                }
            });
            ebiModule.guiRenderer.getTable("tableCampaignProduct","Campaign").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("tableCampaignProduct","Campaign").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
               
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("tableCampaignProduct","Campaign").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteCampaignProduct","Campaign").setEnabled(false);
                    } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteCampaignProduct","Campaign").setEnabled(true);
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newCampaignProduct","Campaign").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newCampaignProduct","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(dataControlCampaign.getCampaignPosList(), dataControlCampaign.getCampaign(), ebiModule);
                    product.setVisible();
                    
                }
            });


            ebiModule.guiRenderer.getButton("deleteCampaignProduct","Campaign").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteCampaignProduct","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteCampaignProduct","Campaign").addActionListener(new java.awt.event.ActionListener() {

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

          /***************************************************************************/
          // CAMPAIGN RECEIVER TABLE
          /***************************************************************************/

            ebiModule.guiRenderer.getTable("tableCampaignReceiver","Campaign").setModel(tabModReceiver);
            ebiModule.guiRenderer.getTable("tableCampaignReceiver","Campaign").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("tableCampaignReceiver","Campaign").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedReceiverRow = ebiModule.guiRenderer.getTable("tableCampaignReceiver","Campaign").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteCampaignReceiver","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("editCampaignReceiver","Campaign").setEnabled(false);
                    } else if (!tabModReceiver.data[selectedReceiverRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteCampaignReceiver","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("editCampaignReceiver","Campaign").setEnabled(true);
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newCampaignReceiver","Campaign").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newCampaignReceiver","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMAddContactAddressType addCo = new EBICRMAddContactAddressType(ebiModule, dataControlCampaign);
                    addCo.setVisible();
                }
            });

            ebiModule.guiRenderer.getButton("deleteCampaignReceiver","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteCampaignReceiver","Campaign").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteCampaignReceiver","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteReceiver(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][12].toString()));
                    }
                }
            });

            ebiModule.guiRenderer.getButton("editCampaignReceiver","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("editCampaignReceiver","Campaign").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editCampaignReceiver","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedReceiverRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModReceiver.data[selectedReceiverRow][0].toString())) {
                        return;
                    }

                    editReceiver(Integer.parseInt(tabModReceiver.data[selectedReceiverRow][12].toString()));
                }
            });

        /***************************************************************************/
        // CAMPAIGN AVAILABLE TABLE
        /***************************************************************************/

            ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").setModel(tabModelCampaign);
            ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            
                    if(lsm.getMinSelectionIndex() != -1){
                      try{
                        selectedCampaignRow = ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }catch(IndexOutOfBoundsException ex){}
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editCampaign","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportCampaign","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteCampaign","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyCampaign","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailCampaign","Campaign").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyCampaign","Campaign").setEnabled(false);
                    } else if (!tabModelCampaign.data[selectedCampaignRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editCampaign","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportCampaign","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteCampaign","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyCampaign","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailCampaign","Campaign").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyCampaign","Campaign").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                   selectedCampaignRow = selRow;
                }

                public void setUpKeyAction(int selRow){
                    selectedCampaignRow = selRow;
                }

                public void setEnterKeyAction(int selRow){
                    selectedCampaignRow = selRow;
                     if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                            return;
                     }
                    editCampaign(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));

                    ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").grabFocus();
                }

            });

            ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").rowAtPoint(e.getPoint()) != -1){
                        selectedCampaignRow =  ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2) {
                        if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                            return;
                        }
                        editCampaign(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));
                        ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").grabFocus();
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newCampaign","Campaign").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
            });
            
            
            
            ebiModule.guiRenderer.getButton("copyCampaign","Campaign").setIcon(EBIConstant.ICON_COPY);
            ebiModule.guiRenderer.getButton("copyCampaign","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("copyCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                        return;
                    }
                    
                    dataControlCampaign.dataCopy(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("editCampaign","Campaign").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editCampaign","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("editCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                        return;
                    }
                    
                    editCampaign(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));
                    ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").grabFocus();
                }
            });


             ebiModule.guiRenderer.getButton("deleteCampaign","Campaign").setEnabled(false);
             ebiModule.guiRenderer.getButton("deleteCampaign","Campaign").setIcon(EBIConstant.ICON_DELETE);
             ebiModule.guiRenderer.getButton("deleteCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {

                        if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                            return;
                        }
                        ebiDelete();

                    }
                });

               ebiModule.guiRenderer.getButton("historyCampaign","Campaign").setEnabled(false);
               ebiModule.guiRenderer.getButton("historyCampaign","Campaign").setIcon(EBIConstant.ICON_HISTORY);
               ebiModule.guiRenderer.getButton("historyCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()), "CRMCampaign"),ebiModule).setVisible();
                    }
               });

            ebiModule.guiRenderer.getButton("mailCampaign","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("mailCampaign","Campaign").setIcon(EBIConstant.ICON_SEND_MAIL);
            ebiModule.guiRenderer.getButton("mailCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                            return;
                    }

                    mailCampaign(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("reportCampaign","Campaign").setEnabled(false);
            ebiModule.guiRenderer.getButton("reportCampaign","Campaign").setIcon(EBIConstant.ICON_REPORT);
            ebiModule.guiRenderer.getButton("reportCampaign","Campaign").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedCampaignRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelCampaign.data[selectedCampaignRow][0].toString())) {
                        return;
                    }

                    showCampaignReport(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));

                }
           });

            ebiModule.guiRenderer.getButton("saveCampagin","Campaign").addActionListener(new java.awt.event.ActionListener() {

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

        ebiModule.guiRenderer.getTextfield("CampaignNrTex","Campaign").setText("");
        ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").setText("");

        ebiModule.guiRenderer.getTimepicker("campaignValidFromText","Campaign").getEditor().setText("");
        ebiModule.guiRenderer.getTimepicker("campaingValidToText","Campaign").getEditor().setText("");

        ebiModule.guiRenderer.getVisualPanel("Campaign").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Campaign").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));

        ebiModule.guiRenderer.getVisualPanel("Campaign").setChangedFrom("");
        ebiModule.guiRenderer.getVisualPanel("Campaign").setChangedDate("");


    }

    private void newDocs() {
        dataControlCampaign.dataNewDoc();
    }

    private void saveAndShowDocs(int id) {
        dataControlCampaign.dataViewDoc(id);
    }

    public void showProduct() {
        dataControlCampaign.dataShowProduct();
    }

    private void newCampaign() {
        dataControlCampaign.dataNew();
        isEdit = false;
    }

    public void saveCampaign() {
    	
	     final Runnable run = new Runnable(){	
		      public void run(){	 
			        if (!validateInput()) {
			            return;
			        }
			        int row = 0;
			        if(isEdit){
			        	row = ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").getSelectedRow();
			        }
			        dataControlCampaign.dataStore(isEdit);
			        try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					ebiModule.guiRenderer.getTable("companyCampaignTable","Campaign").changeSelection(row,0,false,false);
		      }
	     };
	     
	     Thread save = new Thread(run,"Save campaign");
	     save.start();
     
    }

    public void editCampaign(int id) {
        dataControlCampaign.dataEdit(id);
        isEdit = true;
    }

    private void deleteCampaign(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlCampaign.dataDelete(id);
            newCampaign();
        }
    }

    private boolean validateInput() {

        if ("".equals(ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (ebiModule.guiRenderer.getComboBox("CampaignStatusText","Campaign").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if ("".equals(ebiModule.guiRenderer.getTimepicker("campaignValidFromText","Campaign").getEditor().getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_MESSAGE_VALID_FROM")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if ("".equals(ebiModule.guiRenderer.getTimepicker("campaingValidToText","Campaign").getEditor().getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_MESSAGE_VALID_TO")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (isEdit == false) {
            for (int i = 0; i < this.tabModelCampaign.data.length; i++) {
                if (this.tabModelCampaign.data[i][0].equals(ebiModule.guiRenderer.getTextfield("CampaignNameText","Campaign").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_CAMPAIGN_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        return true;
    }

    public void showCampaign() {
        dataControlCampaign.dataShow();
    }

    private void deleteDocs(int id) {
        dataControlCampaign.dataDeleteDoc(id);
    }

    private void deleteReceiver(int id) {
        dataControlCampaign.dataDeleteReceiver(id);
    }

    private void editReceiver(int id) {
        dataControlCampaign.dataEditReceiver(id);
    }

    private void deleteProduct(int id) {
        dataControlCampaign.dataDeleteProduct(id);
    }

    private boolean showCampaignReport(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlCampaign.dataShowReport(id);
        }
        return pass;
    }

    private void newCampaignProperties() {
        dataControlCampaign.dataAddProperties();
    }

    private void editCampaignProperties(int id) {
        dataControlCampaign.dataEditPoperties(id);
    }

    private void deleteCampaignProperties(int id) {
        dataControlCampaign.dataDeleteProperties(id);
    }

    private void mailCampaign(final int id){

        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlCampaign.dataShowAndMailReport(id,false);
        }

    }

    public void ebiNew(){
        newCampaign();
    }

    public void ebiSave(){
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanSave() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            saveCampaign();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteCampaign(Integer.parseInt(tabModelCampaign.data[selectedCampaignRow][4].toString()));
        }
    }

}