package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
import ebiCRM.data.control.EBIDataControlService;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBIProblemSolutionSelectionDialog;
import ebiCRM.table.models.MyTableModelCRMProduct;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelProblemSolution;
import ebiCRM.table.models.MyTableModelService;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMService {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelProblemSolution tabModProsol= null;
    public MyTableModelCRMProduct tabModProduct = null;
    public MyTableModelService tabModService = null;
    public boolean isEdit = false;
    public static String[] serviceStatus = null;
    public static String[] serviceType = null;
    public static String[] serviceCategory = null;
    public EBIDataControlService serviceDataControl = null;
    private int selectedServiceRow = -1;
    private int selectedDocRow = -1;
    private int selectedProsolRow = -1;
    private int selectedProductRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMService(EBICRMModule ebiMod) {
        isEdit = false;
        ebiModule = ebiMod;
        tabModDoc = new MyTableModelDoc();
        tabModProsol = new MyTableModelProblemSolution();
        tabModProduct = new MyTableModelCRMProduct();
        tabModService = new MyTableModelService();
        serviceDataControl = new EBIDataControlService(this);
    }

    public void initializeAction(){
        ebiModule.guiRenderer.getLabel("filterTable","Service").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Service").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyServiceTable","Service").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Service").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyServiceTable","Service").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Service").getText()));
            }
        });


        ebiModule.guiRenderer.getButton("saveService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiSave();

                }
            });

        /**************************************************************************************/
        //  Service TABLE DOCUMENT
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("tableServiceDocument","Service").setModel(tabModDoc);
         ebiModule.guiRenderer.getTable("tableServiceDocument","Service").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableServiceDocument","Service").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableServiceDocument","Service").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showServiceDoc","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteServiceDoc","Service").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showServiceDoc","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteServiceDoc","Service").setEnabled(true);
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newServiceDoc","Service").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newServiceDoc","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
        });

        ebiModule.guiRenderer.getButton("showServiceDoc","Service").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("showServiceDoc","Service").setEnabled(false);
        ebiModule.guiRenderer.getButton("showServiceDoc","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
        });

        ebiModule.guiRenderer.getButton("deleteServiceDoc","Service").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteServiceDoc","Service").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteServiceDoc","Service").addActionListener(new java.awt.event.ActionListener() {

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

        /**************************************************************************************/
        //  Service TABLE PRODUCT
        /**************************************************************************************/
         
         ebiModule.guiRenderer.getTable("tableServiceProduct","Service").setModel(tabModProduct);
         ebiModule.guiRenderer.getTable("tableServiceProduct","Service").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableServiceProduct","Service").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("tableServiceProduct","Service").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteServiceProduct","Service").setEnabled(false);
                    } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteServiceProduct","Service").setEnabled(true);
                    }
                }
            });

           ebiModule.guiRenderer.getButton("newServiceProduct","Service").setIcon(EBIConstant.ICON_NEW);
           ebiModule.guiRenderer.getButton("newServiceProduct","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(serviceDataControl.getservicePosList(), serviceDataControl.getcompService(), ebiModule);
                    product.setVisible();
                    
                }
            });

           ebiModule.guiRenderer.getButton("deleteServiceProduct","Service").setIcon(EBIConstant.ICON_DELETE);
           ebiModule.guiRenderer.getButton("deleteServiceProduct","Service").setEnabled(false);
           ebiModule.guiRenderer.getButton("deleteServiceProduct","Service").addActionListener(new java.awt.event.ActionListener() {

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

        /**************************************************************************************/
        //  Service TABLE RECEIVER
        /**************************************************************************************/
      
       ebiModule.guiRenderer.getTable("tableServiceProsol","Service").setModel(tabModProsol);
       ebiModule.guiRenderer.getTable("tableServiceProsol","Service").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.guiRenderer.getTable("tableServiceProsol","Service").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProsolRow = ebiModule.guiRenderer.getTable("tableServiceProsol","Service").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteServiceProsol","Service").setEnabled(false);
                    } else if (!tabModProsol.data[selectedProsolRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteServiceProsol","Service").setEnabled(true);
                    }
                }
            });

          ebiModule.guiRenderer.getButton("newServiceProsol","Service").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("newServiceProsol","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIProblemSolutionSelectionDialog addCo = new EBIProblemSolutionSelectionDialog(ebiModule, serviceDataControl.getserviceProSolList());
                    addCo.setVisible();
                    serviceDataControl.dataShowProblemSolution();
                }
          });

          ebiModule.guiRenderer.getButton("deleteServiceProsol","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteServiceProsol","Service").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteServiceProsol","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProsolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProsol.data[selectedProsolRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteProSol(Integer.parseInt(tabModProsol.data[selectedProsolRow][7].toString()));
                    }
                }
          });

        /**************************************************************************************/
        //  AVAILABLE Service TABLE
        /**************************************************************************************/
       
         ebiModule.guiRenderer.getTable("companyServiceTable","Service").setModel(tabModService);
         ebiModule.guiRenderer.getTable("companyServiceTable","Service").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //jTableAvalService.setDefaultRenderer(Object.class, new MyOwnCellRederer(false));
         ebiModule.guiRenderer.getTable("companyServiceTable","Service").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                      try{
                        selectedServiceRow = ebiModule.guiRenderer.getTable("companyServiceTable","Service").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }catch(IndexOutOfBoundsException ex){}
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createInvoice","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveService","Service").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailService","Service").setEnabled(false);
                    } else if (!tabModService.data[selectedServiceRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createInvoice","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveService","Service").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailService","Service").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyServiceTable","Service")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedServiceRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedServiceRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedServiceRow = selRow;

                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }

                    editService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                    ebiModule.guiRenderer.getTextfield("serviceNrText","Service").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("companyServiceTable","Service").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                   if(ebiModule.guiRenderer.getTable("companyServiceTable","Service").rowAtPoint(e.getPoint()) > -1){
                    selectedServiceRow = ebiModule.guiRenderer.getTable("companyServiceTable","Service").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyServiceTable","Service").rowAtPoint(e.getPoint()));
                   }
                    if (e.getClickCount() == 2) {

                        if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModService.data[selectedServiceRow][0].toString())) {
                            return;
                        }

                        editService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                        ebiModule.guiRenderer.getTextfield("serviceNrText","Service").grabFocus();
                    }
                }
            });

          ebiModule.guiRenderer.getButton("newService","Service").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("newService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
          });
          
          ebiModule.guiRenderer.getButton("copyService","Service").setIcon(EBIConstant.ICON_COPY);
          ebiModule.guiRenderer.getButton("copyService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("copyService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    
                	if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            					equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }
                    
                    serviceDataControl.dataCopy(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("moveService","Service").setIcon(EBIConstant.ICON_MOVE_RECORD);
          ebiModule.guiRenderer.getButton("moveService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("moveService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }

                    serviceDataControl.dataMove(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                }
          });
          
          
          ebiModule.guiRenderer.getButton("editService","Service").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("editService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }

                    editService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                    ebiModule.guiRenderer.getTextfield("serviceNrText","Service").grabFocus();
                }
          });

          ebiModule.guiRenderer.getButton("deleteService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteService","Service").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }
                   ebiDelete();

                }
          });

          ebiModule.guiRenderer.getButton("reportService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("reportService","Service").setIcon(EBIConstant.ICON_REPORT);
          ebiModule.guiRenderer.getButton("reportService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }

                    showServiceReport(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("mailService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("mailService","Service").setIcon(EBIConstant.ICON_SEND_MAIL);
          ebiModule.guiRenderer.getButton("mailService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModService.data[selectedServiceRow][0].toString())) {
                        return;
                    }

                    mailService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("historyService","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("historyService","Service").setIcon(EBIConstant.ICON_HISTORY);
          ebiModule.guiRenderer.getButton("historyService","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Service"),ebiModule).setVisible();
                }
            });

          ebiModule.guiRenderer.getButton("createInvoice","Service").setIcon(EBIConstant.ICON_EXPORT);
          ebiModule.guiRenderer.getButton("createInvoice","Service").setEnabled(false);
          ebiModule.guiRenderer.getButton("createInvoice","Service").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                       if (selectedServiceRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModService.data[selectedServiceRow][0].toString())) {
                            return;
                        }
                    ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Invoice");
                    serviceDataControl.createInvoiceFromService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
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
        ebiModule.guiRenderer.getVisualPanel("Service").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        ebiModule.guiRenderer.getVisualPanel("Service").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Service").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Service").setChangedFrom("");

        ebiModule.guiRenderer.getTable("tableServiceDocument","Service").setModel(tabModDoc);
        ebiModule.guiRenderer.getTable("tableServiceProduct","Service").setModel(tabModProduct);
        ebiModule.guiRenderer.getTable("tableServiceProsol","Service").setModel(tabModProsol);
        ebiModule.guiRenderer.getTable("companyServiceTable","Service").setModel(tabModService);
        
        TableColumn col7 = ebiModule.guiRenderer.getTable("tableServiceProduct","Service").getColumnModel().getColumn(5);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setModel(new javax.swing.DefaultComboBoxModel(serviceStatus));
        ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setModel(new javax.swing.DefaultComboBoxModel(serviceType));
        ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setModel(new javax.swing.DefaultComboBoxModel(serviceCategory));

        ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("serviceTypeText","Service").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("serviceCategoryText","Service").setSelectedIndex(0);

        ebiModule.guiRenderer.getTextfield("serviceNrText","Service").setText("");
        ebiModule.guiRenderer.getTextfield("serviceNameText","Service").setText("");
        ebiModule.guiRenderer.getTextarea("serviceDescriptionText","Service").setText("");
        serviceDataControl.dataShow();
    }

    private void newDocs() {
        serviceDataControl.dataNewDoc();
    }

    private void saveAndShowDocs(int id) {
       serviceDataControl.dataViewDoc(id);
    }

    public void showProduct() {
        serviceDataControl.dataShowProduct();
    }

    private void newService() {
        isEdit = false;
        serviceDataControl.dataNew(true);
    }

    public void saveService() {
    	
      final Runnable run = new Runnable(){	
	       public void run(){	  
		        if (!validateInput()) {
		            return;
		        }
		        int row = 0;
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("companyServiceTable","Service").getSelectedRow();
		        }
		        serviceDataControl.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyServiceTable","Service").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Service");
      save.start();
      
    }

    public void editService(int id) {
        serviceDataControl.dataEdit(id);

        isEdit = true;
    }

    private void deleteService(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            serviceDataControl.dataDelete(id);
            newService();
        }
    }

    private boolean validateInput() {
        if ("".equals(ebiModule.guiRenderer.getTextfield("serviceNrText","Service").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (ebiModule.guiRenderer.getComboBox("serviceStatusText","Service").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (isEdit == false) {
            for (int i = 0; i < this.tabModService.data.length; i++) {
                if (this.tabModService.data[i][0].equals(ebiModule.guiRenderer.getTextfield("serviceNrText","Service").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SERVICE_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    public void showService() {
        serviceDataControl.dataShow();
    }

    private void deleteDocs(int id) {
        serviceDataControl.dataDeleteDoc(id);

    }

    private void deleteProSol(int id) {
        serviceDataControl.dataDeleteProblemSolution(id);
    }

    private void deleteProduct(int id) {
       serviceDataControl.dataDeleteProduct(id);
    }
    
    private void showServiceReport(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            serviceDataControl.dataShowReport(id);
        }
    }


    public void mailService(final int id){

        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }

        if (pass) {
            serviceDataControl.dataShowAndMailReport(id, false);
        }
    }

    public void ebiNew(){
        newService();
    }

    public void ebiSave(){
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanSave() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            saveService();
        } else {
            if(ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule()){
                saveService();
            }
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteService(Integer.parseInt(tabModService.data[selectedServiceRow][6].toString()));
        }
    }


}