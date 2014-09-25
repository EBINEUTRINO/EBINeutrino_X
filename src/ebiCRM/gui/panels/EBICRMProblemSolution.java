package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Component;
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
import ebiCRM.data.control.EBIDataControlProblemSolution;
import ebiCRM.gui.dialogs.EBICRMDialogAddProduct;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.table.models.MyTableModelProblemSolution;
import ebiCRM.table.models.MyTableModelProsolPosition;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMProblemSolution {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabModDoc = null;
    public MyTableModelProsolPosition tabModProduct = null;
    public MyTableModelProblemSolution tabModProsol = null;
    public boolean isEdit = false;
    public static String[] prosolStatus = null;
    public static String[] prosolType = null;
    public static String[] prosolCategory = null;
    public static String[] prosolClassification = null;
    public EBIDataControlProblemSolution dataControlProsol = null;
    private int selectedprosolRow = -1;
    private int selectedDocRow = -1;
    private int selectedProductRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMProblemSolution(EBICRMModule ebiMod) {
        isEdit = false;
        ebiModule = ebiMod;

        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("PROSOL_SESSION");
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("PROSOL_SESSION").begin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabModDoc = new MyTableModelDoc();
        tabModProduct = new MyTableModelProsolPosition();
        tabModProsol = new MyTableModelProblemSolution();
        dataControlProsol = new EBIDataControlProblemSolution(this);
    }

    public void initializeAction(){


        ebiModule.guiRenderer.getLabel("filterTable","Prosol").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Prosol").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("prosolTable","Prosol").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Prosol").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("prosolTable","Prosol").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Prosol").getText()));
            }
        });

        ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").setModel(new DefaultComboBoxModel(prosolStatus));
        ebiModule.guiRenderer.getComboBox("prosolTypeText","Prosol").setModel(new DefaultComboBoxModel(prosolType));
        ebiModule.guiRenderer.getComboBox("prosolCategoryText","Prosol").setModel(new DefaultComboBoxModel(prosolCategory));
        ebiModule.guiRenderer.getComboBox("prosolClassificationText","Prosol").setModel(new DefaultComboBoxModel(prosolClassification));

        ebiModule.guiRenderer.getButton("saveprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiSave();

                }
            });

        /**************************************************************************************/
        //  prosol TABLE DOCUMENT
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("prosolTableDocument","Prosol").setModel(tabModDoc);
         ebiModule.guiRenderer.getTable("prosolTableDocument","Prosol").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("prosolTableDocument","Prosol").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("prosolTableDocument","Prosol").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showprosolDoc","Prosol").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").setEnabled(false);
                    } else if (!tabModDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showprosolDoc","Prosol").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").setEnabled(true);
                    }
                }
            });

        ebiModule.guiRenderer.getButton("newprosolDoc","Prosol").setIcon(EBIConstant.ICON_NEW);
        ebiModule.guiRenderer.getButton("newprosolDoc","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
        });

        ebiModule.guiRenderer.getButton("showprosolDoc","Prosol").setIcon(EBIConstant.ICON_EXPORT);
        ebiModule.guiRenderer.getButton("showprosolDoc","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabModDoc.data[selectedDocRow][3].toString()));
                }
        });

        ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").addActionListener(new java.awt.event.ActionListener() {

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
        //  prosol TABLE PRODUCT
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("tableprosolProduct","Prosol").setModel(tabModProduct);
         TableColumn col7 = ebiModule.guiRenderer.getTable("tableprosolProduct","Prosol").getColumnModel().getColumn(4);
            col7.setCellRenderer(new DefaultTableCellRenderer(){
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                    JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                    myself.setHorizontalAlignment(SwingConstants.RIGHT);
                    myself.setForeground(new Color(255,60,60));
                    return myself;
                }
         });

         ebiModule.guiRenderer.getTable("tableprosolProduct","Prosol").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("tableprosolProduct","Prosol").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProductRow = ebiModule.guiRenderer.getTable("tableprosolProduct","Prosol").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").setEnabled(false);
                    } else if (!tabModProduct.data[selectedProductRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").setEnabled(true);
                    }
                }
            });

           ebiModule.guiRenderer.getButton("newpProduct","Prosol").setIcon(EBIConstant.ICON_NEW);
           ebiModule.guiRenderer.getButton("newpProduct","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBICRMDialogAddProduct product = new EBICRMDialogAddProduct(dataControlProsol.getprosolPosList(), dataControlProsol.getcompProsol(), ebiModule);
                    product.setVisible();
                   
                }
            });

           ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").setIcon(EBIConstant.ICON_DELETE);
           ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProductRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProduct.data[selectedProductRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteProduct(Integer.parseInt(tabModProduct.data[selectedProductRow][6].toString()));
                    }

                }
            });


        /**************************************************************************************/
        //  AVAILABLE prosol TABLE
        /**************************************************************************************/

         ebiModule.guiRenderer.getTable("prosolTable","Prosol").setModel(tabModProsol);
         ebiModule.guiRenderer.getTable("prosolTable","Prosol").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            //jTableAvalprosol.setDefaultRenderer(Object.class, new MyOwnCellRederer(false));
         ebiModule.guiRenderer.getTable("prosolTable","Prosol").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();


                      try{
                            selectedprosolRow = ebiModule.guiRenderer.getTable("prosolTable","Prosol").convertRowIndexToModel(lsm.getMinSelectionIndex());

                            if (lsm.isSelectionEmpty()) {
                                ebiModule.guiRenderer.getButton("editprosol","Prosol").setEnabled(false);
                                ebiModule.guiRenderer.getButton("reportprosol","Prosol").setEnabled(false);
                                ebiModule.guiRenderer.getButton("deleteprosol","Prosol").setEnabled(false);
                                ebiModule.guiRenderer.getButton("historyprosol","Prosol").setEnabled(false);
                                ebiModule.guiRenderer.getButton("copyprosol","Prosol").setEnabled(false);
                            } else if (!tabModProsol.data[selectedprosolRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                                ebiModule.guiRenderer.getButton("editprosol","Prosol").setEnabled(true);
                                ebiModule.guiRenderer.getButton("reportprosol","Prosol").setEnabled(true);
                                ebiModule.guiRenderer.getButton("deleteprosol","Prosol").setEnabled(true);
                                ebiModule.guiRenderer.getButton("historyprosol","Prosol").setEnabled(true);
                                ebiModule.guiRenderer.getButton("copyprosol","Prosol").setEnabled(true);
                            }
                      }catch(IndexOutOfBoundsException ex){ selectedprosolRow = 0;}
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("prosolTable","Prosol")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedprosolRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedprosolRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedprosolRow = selRow;

                    if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                        return;
                    }
  
                    editprosol(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("prosolTable","Prosol").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("prosolTable","Prosol").rowAtPoint(e.getPoint()) > -1){
                         selectedprosolRow = ebiModule.guiRenderer.getTable("prosolTable","Prosol").convertRowIndexToModel(ebiModule.guiRenderer.getTable("prosolTable","Prosol").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2) {

                        if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                            return;
                        }

                        editprosol(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
                        ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").grabFocus();
                    }
                }
            });

          ebiModule.guiRenderer.getButton("newprosol","Prosol").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("newprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
          });
          
          
          ebiModule.guiRenderer.getButton("copyprosol","Prosol").setIcon(EBIConstant.ICON_COPY);
          ebiModule.guiRenderer.getButton("copyprosol","Prosol").setEnabled(false);
          ebiModule.guiRenderer.getButton("copyprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                    							equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                        return;
                    }

                    dataControlProsol.dataCopy(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
                }
          });
          
          
          ebiModule.guiRenderer.getButton("editprosol","Prosol").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editprosol","Prosol").setEnabled(false);
          ebiModule.guiRenderer.getButton("editprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                        return;
                    }

                    editprosol(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").grabFocus();
                }
          });

          ebiModule.guiRenderer.getButton("deleteprosol","Prosol").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteprosol","Prosol").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                        return;
                    }
                    ebiDelete();

                }
          });

          ebiModule.guiRenderer.getButton("reportprosol","Prosol").setEnabled(false);
          ebiModule.guiRenderer.getButton("reportprosol","Prosol").setIcon(EBIConstant.ICON_REPORT);
          ebiModule.guiRenderer.getButton("reportprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedprosolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModProsol.data[selectedprosolRow][0].toString())) {
                        return;
                    }

                    showprosolReport(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
                }
          });

          ebiModule.guiRenderer.getButton("historyprosol","Prosol").setEnabled(false);
          ebiModule.guiRenderer.getButton("historyprosol","Prosol").setIcon(EBIConstant.ICON_HISTORY);
          ebiModule.guiRenderer.getButton("historyprosol","Prosol").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()), "Prosol"),ebiModule).setVisible();
                }
            });

    }

    /**
     * This method initializes this
     *
     * @return void
     */
    public void initialize() {
        ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Prosol").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));

        ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedFrom("");
        ebiModule.guiRenderer.getVisualPanel("Prosol").setChangedDate("");

        ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").setText("");
        ebiModule.guiRenderer.getTextfield("prosolNameText","Prosol").setText("");
        ebiModule.guiRenderer.getTextarea("prosolDescriptionText","Prosol").setText("");

        ebiModule.guiRenderer.getButton("showprosolDoc","Prosol").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteprosolDoc","Prosol").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteprosolProduct","Prosol").setEnabled(false);
        dataControlProsol.dataShow();
    }

    private void newDocs() {
        dataControlProsol.dataNewDoc();
    }

    private void saveAndShowDocs(int id) {
       dataControlProsol.dataViewDoc(id);
    }

    public void showProduct() {
        dataControlProsol.dataShowProduct();
    }

    private void newprosol() {
        isEdit = false;
        dataControlProsol.dataNew(true);
    }

    public void saveprosol() {
       final Runnable run = new Runnable(){ 
	    	public void run(){
		    	if (!validateInput()) {
		            return;
		        }
		    	int row =0;
		    	if(isEdit){
		    		row = ebiModule.guiRenderer.getTable("prosolTable","Prosol").getSelectedRow();
		    	}
		        dataControlProsol.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("prosolTable","Prosol").changeSelection(row,0,false,false);
	    	}
       };
       
       Thread save = new Thread(run,"Save Problem/Solution");
       save.start();
       
    }

    public void editprosol(int id) {
        dataControlProsol.dataEdit(id);

        isEdit = true;
    }

    private void deleteprosol(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlProsol.dataDelete(id);
            newprosol();
        }
    }

    private boolean validateInput() {

        if ("".equals(ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (ebiModule.guiRenderer.getComboBox("prosolStatusText","Prosol").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_STATUS")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }


        if (isEdit == false) {
            for (int i = 0; i < this.tabModProsol.data.length; i++) {
                if (this.tabModProsol.data[i][0].equals(ebiModule.guiRenderer.getTextfield("prosolNrText","Prosol").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_prosol_EXIST_WITH_SAME_NAME")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    private void deleteDocs(int id) {
        dataControlProsol.dataDeleteDoc(id);

    }

    private void deleteProduct(int id) {
       dataControlProsol.dataDeleteProduct(id);
    }

    private void showprosolReport(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            dataControlProsol.dataShowReport(id);
        }
    }

    public void ebiNew(){
        newprosol();
    }

    public void ebiSave(){
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanSave() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                saveprosol();
        } else {
            if(ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule()){
                saveprosol();
            }
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteprosol(Integer.parseInt(tabModProsol.data[selectedprosolRow][7].toString()));
        }
    }
}