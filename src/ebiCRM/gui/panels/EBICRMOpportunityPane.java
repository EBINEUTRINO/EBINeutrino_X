package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
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
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlOpportunity;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMContact;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelOpportunity;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIJTextFieldNumeric;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMOpportunityPane {

    public EBICRMModule ebiModule = null;
    public MyTableModelOpportunity tabModel = null;
    public boolean isEdit = false;
    public MyTableModelCRMContact tabModelContact = null;
    public static String[] oppBussinesType = null;
    public static String[] oppSalesStage = null;
    public static String[] oppEvalStatus = null;
    public static String[] oppBudgetStatus = null;
    public static String[] oppStatus = null;
    public MyTableModelDoc tabOpportunityDoc = null;
    public EBIDataControlOpportunity opportuniyDataControl = null;
    private int selectedOpportunityRow = -1;
    private int selectedContactRow = -1;
    private int selectedDocRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMOpportunityPane(EBICRMModule modul) {
        ebiModule = modul;
        tabModel = new MyTableModelOpportunity();
        tabModelContact = new MyTableModelCRMContact();
        tabOpportunityDoc = new MyTableModelDoc();
        opportuniyDataControl = new EBIDataControlOpportunity(this);
    }

    public void initializeAction() {


        ebiModule.guiRenderer.getLabel("filterTable","Opportunity").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Opportunity").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Opportunity").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Opportunity").getText()));
            }
        });


        NumberFormat valueFormat=NumberFormat.getNumberInstance();
        valueFormat.setMinimumFractionDigits(2);
        valueFormat.setMaximumFractionDigits(3);

        ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").setEditable(true);

        ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                  
                    if(ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity") != null){
                        opportuniyDataControl.setPurchState(ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").getSelectedItem().toString());
                    }
                }
        });
        
        ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if(ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity") != null){
                        opportuniyDataControl.setEvalStatus(ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").getSelectedItem().toString());
                    }
                }
        });

        ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                   
                    if(ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity") != null){
                        opportuniyDataControl.setSStatus(ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").getSelectedItem().toString());
                    }

                }
        });
        
        ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    
                    if(ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity") != null){
                        opportuniyDataControl.setBudgetStatus(ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").getSelectedItem().toString());
                    }
                }
        });
        
        ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setEditable(true);

        /***********************************************************************************/
        // OPPORTUNITY CONTACT TABLE
        /***********************************************************************************/

        ebiModule.guiRenderer.getTable("contactTableOpportunity","Opportunity").setModel(tabModelContact);
        ebiModule.guiRenderer.getTable("contactTableOpportunity","Opportunity").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("contactTableOpportunity","Opportunity").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                       if(lsm.getMinSelectionIndex() != -1){
                        selectedContactRow = ebiModule.guiRenderer.getTable("contactTableOpportunity","Opportunity").convertRowIndexToModel(lsm.getMinSelectionIndex());
                       } 
                        if (lsm.isSelectionEmpty()) {
                            ebiModule.guiRenderer.getButton("editOppContact","Opportunity").setEnabled(false);
                            ebiModule.guiRenderer.getButton("delteOppContact","Opportunity").setEnabled(false);
                        } else if (!tabModelContact.data[selectedContactRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("editOppContact","Opportunity").setEnabled(true);
                            ebiModule.guiRenderer.getButton("delteOppContact","Opportunity").setEnabled(true);
                        }

                }
            });

            ebiModule.guiRenderer.getButton("newOppContact","Opportunity").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newOppContact","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addOpportunityContact();
                }
            });

            ebiModule.guiRenderer.getButton("editOppContact","Opportunity").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editOppContact","Opportunity").setEnabled(false);
            ebiModule.guiRenderer.getButton("editOppContact","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelContact.data[selectedContactRow][0].toString())) {
                        return;
                    }

                    editOpportunityContact(Integer.parseInt(tabModelContact.data[selectedContactRow][6].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("delteOppContact","Opportunity").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("delteOppContact","Opportunity").setEnabled(false);
            ebiModule.guiRenderer.getButton("delteOppContact","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelContact.data[selectedContactRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        removeOpportunityContact(Integer.parseInt(tabModelContact.data[selectedContactRow][6].toString()));
                    }
                }
        });

        ebiModule.guiRenderer.getButton("createOffer","Opportunity").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("createOffer","Opportunity").setEnabled(false);
        ebiModule.guiRenderer.getButton("createOffer","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                    return;
                }
                ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Offer");
                opportuniyDataControl.createOfferFromOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
            }
        });

        /***********************************************************************************/
        // OPPORTUNITY TABLE
        /***********************************************************************************/


        ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").setModel(tabModel);
        ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                   
                    if(lsm.getMinSelectionIndex() > -1){
                      try{
                        selectedOpportunityRow = ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }catch(IndexOutOfBoundsException ex){}
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createOffer","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyOpportunity","Opportunity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveOpportunity","Opportunity").setEnabled(false);
                    } else if (!tabModel.data[selectedOpportunityRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createOffer","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyOpportunity","Opportunity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveOpportunity","Opportunity").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedOpportunityRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedOpportunityRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedOpportunityRow = selRow;

                    if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                        return;
                    }
                    
                    editOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
                    ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").rowAtPoint(e.getPoint()) > -1){
                        selectedOpportunityRow = ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2) {

                        if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                            return;
                        }

                        editOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
                        ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").grabFocus();
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newOpportunity","Opportunity").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                ebiNew();
            }
        });

        

        ebiModule.guiRenderer.getButton("copyOpportunity","Opportunity").setIcon(EBIConstant.ICON_COPY);
        ebiModule.guiRenderer.getButton("copyOpportunity","Opportunity").setEnabled(false);
        ebiModule.guiRenderer.getButton("copyOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                    return;
                }
                
                opportuniyDataControl.dataCopy(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
                
            }
        });

        ebiModule.guiRenderer.getButton("moveOpportunity","Opportunity").setIcon(EBIConstant.ICON_MOVE_RECORD);
        ebiModule.guiRenderer.getButton("moveOpportunity","Opportunity").setEnabled(false);
        ebiModule.guiRenderer.getButton("moveOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                    return;
                }

                opportuniyDataControl.dataMove(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));

            }
        });
        
        
        ebiModule.guiRenderer.getButton("editOpportunity","Opportunity").setIcon(EBIConstant.ICON_EDIT);
        ebiModule.guiRenderer.getButton("editOpportunity","Opportunity").setEnabled(false);
        ebiModule.guiRenderer.getButton("editOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                    return;
                }
                
                editOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
                ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").grabFocus();
            }
        });


        ebiModule.guiRenderer.getButton("deleteOpportunity","Opportunity").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteOpportunity","Opportunity").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                    return;
                }
                ebiDelete();
            }
        });

         ebiModule.guiRenderer.getButton("reportOpportunity","Opportunity").setEnabled(false);
         ebiModule.guiRenderer.getButton("reportOpportunity","Opportunity").setIcon(EBIConstant.ICON_REPORT);
         ebiModule.guiRenderer.getButton("reportOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                        return;
                    }

                    showOpportunityRepots(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("historyOpportunity","Opportunity").setEnabled(false);
            ebiModule.guiRenderer.getButton("historyOpportunity","Opportunity").setIcon(EBIConstant.ICON_HISTORY);
            ebiModule.guiRenderer.getButton("historyOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Opportunity"), ebiModule).setVisible();
                }
            });

            ebiModule.guiRenderer.getButton("mailOpportunity","Opportunity").setEnabled(false);
            ebiModule.guiRenderer.getButton("mailOpportunity","Opportunity").setIcon(EBIConstant.ICON_SEND_MAIL);
            ebiModule.guiRenderer.getButton("mailOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedOpportunityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedOpportunityRow][0].toString())) {
                            return;
                    }

                    mailOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));

                }
           });

           ebiModule.guiRenderer.getButton("saveOpportunity","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiSave();
                }
            });
        
          ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(valueFormat)));
          ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.FLOAT));
          ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setColumns(10);

        /***********************************************************************************/
        // OPPORTUNITY DOCUMENT TABLE
        /***********************************************************************************/
         
         ebiModule.guiRenderer.getTable("opportunityDoc","Opportunity").setModel(tabOpportunityDoc);
         ebiModule.guiRenderer.getTable("opportunityDoc","Opportunity").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("opportunityDoc","Opportunity").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            if (e.getValueIsAdjusting()) {
                                return;
                            }

                            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                            if(lsm.getMinSelectionIndex() != -1){
                                selectedDocRow = ebiModule.guiRenderer.getTable("opportunityDoc","Opportunity").convertRowIndexToModel(lsm.getMinSelectionIndex());
                            }

                            if (lsm.isSelectionEmpty()) {
                                ebiModule.guiRenderer.getButton("showOppDoc","Opportunity").setEnabled(false);
                                ebiModule.guiRenderer.getButton("deleteOppDoc","Opportunity").setEnabled(false);
                            } else if (!tabOpportunityDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                                ebiModule.guiRenderer.getButton("showOppDoc","Opportunity").setEnabled(true);
                                ebiModule.guiRenderer.getButton("deleteOppDoc","Opportunity").setEnabled(true);
                            }
                        }
             });
         

             ebiModule.guiRenderer.getButton("newOppDoc","Opportunity").setIcon(EBIConstant.ICON_NEW);
             ebiModule.guiRenderer.getButton("newOppDoc","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                 public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                 }
             });

             ebiModule.guiRenderer.getButton("showOppDoc","Opportunity").setIcon(EBIConstant.ICON_EXPORT);
             ebiModule.guiRenderer.getButton("showOppDoc","Opportunity").setEnabled(false);
             ebiModule.guiRenderer.getButton("showOppDoc","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                 public void actionPerformed(java.awt.event.ActionEvent e) {

                     if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                             equals(tabOpportunityDoc.data[selectedDocRow][0].toString())) {
                         return;
                     }

                     saveAndShowDocs(Integer.parseInt(tabOpportunityDoc.data[selectedDocRow][3].toString()));

                 }
             });

             ebiModule.guiRenderer.getButton("deleteOppDoc","Opportunity").setIcon(EBIConstant.ICON_DELETE);
             ebiModule.guiRenderer.getButton("deleteOppDoc","Opportunity").setEnabled(false);
             ebiModule.guiRenderer.getButton("deleteOppDoc","Opportunity").addActionListener(new java.awt.event.ActionListener() {

                 public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                             equals(tabOpportunityDoc.data[selectedDocRow][0].toString())) {
                         return;
                     }
                     
                     if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDoc(Integer.parseInt(tabOpportunityDoc.data[selectedDocRow][3].toString()));
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
        ebiModule.guiRenderer.getTable("contactTableOpportunity","Opportunity").setModel(tabModelContact);
        ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").setModel(tabModel);
        ebiModule.guiRenderer.getTable("opportunityDoc","Opportunity").setModel(tabOpportunityDoc);
        TableColumn col7 = ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").getColumnModel().getColumn(4);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });
        ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("Opportunity").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Opportunity").setChangedFrom("");

        ebiModule.guiRenderer.getFormattedTextfield("oppValueText","Opportunity").setText("");
        ebiModule.guiRenderer.getTextarea("opportunityDescription","Opportunity").setText("");

        ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(oppSalesStage));
        ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(oppEvalStatus));
        ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(oppStatus));
        ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(oppBudgetStatus));
        ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(oppBussinesType));
        ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").setModel(new javax.swing.DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%", "100%"}));

        ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().setItem("");
        ebiModule.guiRenderer.getComboBox("oppSaleStateText","Opportunity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("oppEvalStatusText","Opportunity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("statusOppText","Opportunity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("oppBdgStatusText","Opportunity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("oppProbabilityText","Opportunity").setSelectedIndex(0);
        opportuniyDataControl.dataShow();
    }

    private void newDocs() {
       opportuniyDataControl.dataNewDoc();
    }

    private void saveAndShowDocs(int id) {
        opportuniyDataControl.dataViewDoc(id);
    }

    private void deleteDoc(int id) {
        opportuniyDataControl.dataDeleteDoc(id);
    }


    private void newOpportunity() {
        opportuniyDataControl.dataNew();
        isEdit = false;

    }

    public void saveOpportunity() {
      final Runnable run = new Runnable(){	
	       public void run(){
		        if (!validateInput()) {
		            return;
		        }
		        
		        int row = 0;
		        
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").getSelectedRow();
		        }
		        opportuniyDataControl.dataStore(isEdit);
		        showOpportunity();
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyOpportunityTable","Opportunity").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Opportunity");
      save.start();
      
    }

    public void editOpportunity(int id) {
        opportuniyDataControl.dataEdit(id);
        isEdit = true;
    }

    private void deleteOpportunity(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            opportuniyDataControl.dataDelete(id);
            newOpportunity();
            showOpportunity();
        }
    }

    public void showOpportunity() {
        opportuniyDataControl.dataShow();
    }

    private boolean validateInput() {
        
        if ("".equals(ebiModule.guiRenderer.getComboBox("opportunityNameText","Opportunity").getEditor().getItem())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem().toString()) || ebiModule.guiRenderer.getComboBox("oppBustypeText","Opportunity").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_BUSINESS_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }


    public void mailOpportunity(final int id){

        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            opportuniyDataControl.dataShowAndMailReport(id, false);
        }

    }

    private void addOpportunityContact() {
        opportuniyDataControl.dataAddContact();
    }

    private void removeOpportunityContact(int id) {
        opportuniyDataControl.dataRemoveContact(id);
    }

    private void editOpportunityContact(int id) {
        opportuniyDataControl.dataEditContact(id);
    }

    private void showOpportunityRepots(int id) {
        this.opportuniyDataControl.dataShowReport(id);
    }

    public void ebiNew(){
        newOpportunity();
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
            saveOpportunity();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteOpportunity(Integer.parseInt(tabModel.data[selectedOpportunityRow][7].toString()));
        }
    }
}