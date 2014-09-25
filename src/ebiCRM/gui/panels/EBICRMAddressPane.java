package ebiCRM.gui.panels;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlAddress;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMAddress;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMAddressPane {

    public MyTableModelCRMAddress tabModel = null;
    public boolean isEdit = false;
    public EBICRMModule ebiModule = null;
    public static String[] AddressType = null;
    public EBIDataControlAddress addressDataControl = null;
    private int selectedRow = -1;
    public IEBIGUIRenderer guiRenderer = null;

    /**
     * This is the EBICRMAdressPane default constructor
     */
    public EBICRMAddressPane(EBICRMModule modul) {
        ebiModule = modul;
        guiRenderer = modul.guiRenderer;
        tabModel = new MyTableModelCRMAddress();
        addressDataControl = new EBIDataControlAddress(this);
    }

     public void initializeAction() {

         ebiModule.guiRenderer.getLabel("filterTable","Address").setHorizontalAlignment(SwingUtilities.RIGHT);
         ebiModule.guiRenderer.getTextfield("filterTableText","Address").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyAddess","Address").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Address").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyAddess","Address").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Address").getText()));
            }
        });

         guiRenderer.getButton("saveAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    
                   ebiSave();
                }
          });

         guiRenderer.getTable("companyAddess","Address").setModel(tabModel);
         guiRenderer.getTable("companyAddess","Address").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         guiRenderer.getTable("companyAddess","Address").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                      if(lsm.getMinSelectionIndex() < tabModel.data.length){
                        selectedRow = guiRenderer.getTable("companyAddess","Address").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }
                    }
                    
                    if(tabModel.data.length > selectedRow ){
	                    if (lsm.isSelectionEmpty()) {
	                        guiRenderer.getButton("editAddress","Address").setEnabled(false);
	                        guiRenderer.getButton("deleteAddress","Address").setEnabled(false);
	                        guiRenderer.getButton("historyAddress","Address").setEnabled(false);
	                        guiRenderer.getButton("copyAddress","Address").setEnabled(false);
	                        guiRenderer.getButton("moveAddress","Address").setEnabled(false);
	                    } else if (!tabModel.data[selectedRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
	                        guiRenderer.getButton("editAddress","Address").setEnabled(true);
	                        guiRenderer.getButton("deleteAddress","Address").setEnabled(true);
	                        guiRenderer.getButton("historyAddress","Address").setEnabled(true);
	                        guiRenderer.getButton("copyAddress","Address").setEnabled(true);
	                        guiRenderer.getButton("moveAddress","Address").setEnabled(true);
	                    }
                    }
                }
            });

            new JTableActionMaps(guiRenderer.getTable("companyAddess","Address")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedRow = selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                    editAdress(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                    guiRenderer.getComboBox("addressTypeText","Address").grabFocus();

                }
            });

            guiRenderer.getTable("companyAddess","Address").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                  if(guiRenderer.getTable("companyAddess","Address").rowAtPoint(e.getPoint()) != -1){
                    selectedRow = guiRenderer.getTable("companyAddess","Address").convertRowIndexToModel(guiRenderer.getTable("companyAddess","Address").rowAtPoint(e.getPoint()));
                  }
                    if (e.getClickCount() == 2) {

                        if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedRow][0].toString())) {
                            return;
                        }
                        editAdress(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                        guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
                    }
                }
            });

         guiRenderer.getButton("newAddress","Address").setIcon(EBIConstant.ICON_NEW);
         guiRenderer.getButton("newAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
         });
         
         
         guiRenderer.getButton("copyAddress","Address").setIcon(EBIConstant.ICON_COPY);
         guiRenderer.getButton("copyAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                	if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                	addressDataControl.dataCopy(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                }
         });


         guiRenderer.getButton("moveAddress","Address").setIcon(EBIConstant.ICON_MOVE_RECORD);
         guiRenderer.getButton("moveAddress","Address").addActionListener(new java.awt.event.ActionListener() {

             public void actionPerformed(java.awt.event.ActionEvent e) {
                 if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                         equals(tabModel.data[selectedRow][0].toString())) {
                     return;
                 }
                 addressDataControl.dataMove(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
             }
         });
         
         

         guiRenderer.getButton("editAddress","Address").setEnabled(false);
         guiRenderer.getButton("editAddress","Address").setIcon(EBIConstant.ICON_EDIT);
         guiRenderer.getButton("editAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                    editAdress(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                    guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
                }

         });

         guiRenderer.getButton("deleteAddress","Address").setEnabled(false);
         guiRenderer.getButton("deleteAddress","Address").setIcon(EBIConstant.ICON_DELETE);
         guiRenderer.getButton("deleteAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                    ebiDelete();
                }
            });

         guiRenderer.getButton("historyAddress","Address").setEnabled(false);
         guiRenderer.getButton("historyAddress","Address").setIcon(EBIConstant.ICON_HISTORY);
         guiRenderer.getButton("historyAddress","Address").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Address"), ebiModule).setVisible();
                }
            });
     }


    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {

         guiRenderer.getVisualPanel("Address").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
         guiRenderer.getVisualPanel("Address").setCreatedFrom(EBIPGFactory.ebiUser);
         guiRenderer.getVisualPanel("Address").setChangedDate("");
         guiRenderer.getVisualPanel("Address").setChangedFrom("");

         guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
         guiRenderer.getComboBox("addressTypeText","Address").setModel(new javax.swing.DefaultComboBoxModel(AddressType));
         guiRenderer.getComboBox("addressTypeText","Address").setEditable(true);
         guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
         guiRenderer.getTextfield("streetText","Address").setText("");
         guiRenderer.getTextfield("zipText","Address").setText("");
         guiRenderer.getTextfield("LocationText","Address").setText("");
         guiRenderer.getTextfield("postcodeText","Address").setText("");
         guiRenderer.getTextfield("countryText","Address").setText("");
         addressDataControl.dataShow();
    }


    private void editAdress(int id) {
        this.addressDataControl.dataEdit(id);
        isEdit = true;
        guiRenderer.getComboBox("addressTypeText","Address").grabFocus();
    }

    private void deleteAdress(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            this.addressDataControl.dataDelete(id);
            newAddress();
        }
    }

    public void showAddress() {
        this.addressDataControl.dataShow();
    }

    public void saveAddress() {
      final Runnable run = new Runnable(){
    	  
	       public void run(){
		        if (!validateInput()) {
		            return;
		        }
		        int row = 0;
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("companyAddess","Address").getSelectedRow();
		        }
		        addressDataControl.dataStore(isEdit);
		        showAddress();
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyAddess","Address").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Store Adress");
      save.start();
        
    }

    void newAddress() {
        this.addressDataControl.dataNew();
        isEdit = false;
    }

    private boolean validateInput() {
            if (guiRenderer.getComboBox("addressTypeText","Address").getSelectedIndex() == 0) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_ADRESS_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }

            if (guiRenderer.getTextfield("streetText","Address").getText().equals("")) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_STREET")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
            if ("".equals(guiRenderer.getTextfield("LocationText","Address").getText())) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_LOCATION")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }
            if ("".equals(guiRenderer.getTextfield("zipText","Address").getText())) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_ZIP")).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            }

        // Check for double address record
        for (int i = 0; i < this.tabModel.data.length; i++) {
            if (i != this.selectedRow) {
                if (guiRenderer.getComboBox("addressTypeText","Address").getSelectedItem().equals(this.tabModel.data[i][0].toString()) &&
                        guiRenderer.getTextfield("streetText","Address").getText().equals(this.tabModel.data[i][1]) &&
                        guiRenderer.getTextfield("zipText","Address").getText().equals(this.tabModel.data[i][2]) &&
                        guiRenderer.getTextfield("LocationText","Address").getText().equals(this.tabModel.data[i][3]) &&
                        guiRenderer.getTextfield("postcodeText","Address").equals(this.tabModel.data[i][4]) &&
                        guiRenderer.getTextfield("countryText","Address").getText().equals(this.tabModel.data[i][5])) {

                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }


    public void ebiNew(){
        newAddress();
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
            saveAddress();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteAdress(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
        }
    }

}