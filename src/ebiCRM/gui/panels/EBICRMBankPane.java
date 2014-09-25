package ebiCRM.gui.panels;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlBank;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMBankdata;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class  EBICRMBankPane{

    public MyTableModelCRMBankdata tabModel = null;
    public boolean isEdit = false;
    public EBICRMModule ebiModule = null;
    public EBIDataControlBank bankDataControl = null;
    private int selectedRow = -1;

    /**
     * This is the default constructor
     */
    public EBICRMBankPane(EBICRMModule module) {
        ebiModule = module;
        tabModel = new MyTableModelCRMBankdata();
        bankDataControl = new EBIDataControlBank(this);
    }

    public void initializeAction() {

      ebiModule.guiRenderer.getLabel("filterTable","Bank").setHorizontalAlignment(SwingUtilities.RIGHT);
      ebiModule.guiRenderer.getTextfield("filterTableText","Bank").addKeyListener(new KeyListener(){
        public void keyTyped(KeyEvent e){}

        public void keyPressed(KeyEvent e){
            ebiModule.guiRenderer.getTable("companyBankTable","Bank").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Bank").getText()));
        }
        public void keyReleased(KeyEvent e){
            ebiModule.guiRenderer.getTable("companyBankTable","Bank").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Bank").getText()));
        }
      });
        
      ebiModule.guiRenderer.getTable("companyBankTable","Bank").setModel(tabModel);
      ebiModule.guiRenderer.getTable("companyBankTable","Bank").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectedRow() != -1 &&
                    					ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectedRow() < tabModel.data.length){
                       
                        selectedRow = ebiModule.guiRenderer.getTable("companyBankTable","Bank").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectedRow());
                       
	                    if (lsm.isSelectionEmpty()) {
	                        ebiModule.guiRenderer.getButton("editBank","Bank").setEnabled(false);
	                        ebiModule.guiRenderer.getButton("deleteBank","Bank").setEnabled(false);
	                        ebiModule.guiRenderer.getButton("historyBank","Bank").setEnabled(false);
	                        ebiModule.guiRenderer.getButton("copyBank","Bank").setEnabled(false);
	                        ebiModule.guiRenderer.getButton("moveBank","Bank").setEnabled(false);
	                    } else if (!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tabModel.data[selectedRow][0].toString())) {
	                        ebiModule.guiRenderer.getButton("editBank","Bank").setEnabled(true);
	                        ebiModule.guiRenderer.getButton("deleteBank","Bank").setEnabled(true);
	                        ebiModule.guiRenderer.getButton("historyBank","Bank").setEnabled(true);
	                        ebiModule.guiRenderer.getButton("moveBank","Bank").setEnabled(true);
	
	                    }
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyBankTable","Bank")).setTableAction(new AbstractTableKeyAction() {

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
                    
                    editBank(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                    ebiModule.guiRenderer.getTextfield("bankNameText","Bank").grabFocus();
                }
            });


            ebiModule.guiRenderer.getTable("companyBankTable","Bank").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("companyBankTable","Bank").rowAtPoint(e.getPoint()) != -1){
                     selectedRow = ebiModule.guiRenderer.getTable("companyBankTable","Bank").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyBankTable","Bank").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2) {

                        if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedRow][0].toString())) {
                            return;
                        }
                        editBank(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                        ebiModule.guiRenderer.getTextfield("bankNameText","Bank").grabFocus();
                    }
                }
            });

      ebiModule.guiRenderer.getButton("saveBank","Bank").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiSave();

                }
            });

      ebiModule.guiRenderer.getButton("newBank","Bank").setIcon(EBIConstant.ICON_NEW);
      ebiModule.guiRenderer.getButton("newBank","Bank").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
      });
      
      
      ebiModule.guiRenderer.getButton("copyBank","Bank").setIcon(EBIConstant.ICON_COPY);
      ebiModule.guiRenderer.getButton("copyBank","Bank").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                	if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                 
                    bankDataControl.dataCopy(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                }
      });

      ebiModule.guiRenderer.getButton("moveBank","Bank").setIcon(EBIConstant.ICON_MOVE_RECORD);
      ebiModule.guiRenderer.getButton("moveBank","Bank").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tabModel.data[selectedRow][0].toString())) {
                    return;
                }

                bankDataControl.dataMove(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
            }
      });

      ebiModule.guiRenderer.getButton("editBank","Bank").setEnabled(false);
      ebiModule.guiRenderer.getButton("editBank","Bank").setIcon(EBIConstant.ICON_EDIT);
      ebiModule.guiRenderer.getButton("editBank","Bank").addActionListener(new java.awt.event.ActionListener() {

             public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                 
                    editBank(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
                    ebiModule.guiRenderer.getTextfield("bankNameText","Bank").grabFocus();
                }
      });
        

      ebiModule.guiRenderer.getButton("deleteBank","Bank").setEnabled(false);
      ebiModule.guiRenderer.getButton("deleteBank","Bank").setIcon(EBIConstant.ICON_DELETE);
      ebiModule.guiRenderer.getButton("deleteBank","Bank").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }
                   ebiDelete();
                }
      });
        
      ebiModule.guiRenderer.getButton("historyBank","Bank").setEnabled(false);
      ebiModule.guiRenderer.getButton("historyBank","Bank").setIcon(EBIConstant.ICON_HISTORY);
      ebiModule.guiRenderer.getButton("historyBank","Bank").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Bankdata"),ebiModule).setVisible();
                }

       });
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {
        
        ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        ebiModule.guiRenderer.getVisualPanel("Bank").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Bank").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Bank").setChangedFrom("");

        ebiModule.guiRenderer.getTextfield("bankNameText","Bank").setText("");
        ebiModule.guiRenderer.getTextfield("bankNameText","Bank").grabFocus();
        ebiModule.guiRenderer.getTextfield("abaNrText","Bank").setText("");
        ebiModule.guiRenderer.getTextfield("accountNrText","Bank").setText("");
        ebiModule.guiRenderer.getTextfield("bicText","Bank").setText("");
        ebiModule.guiRenderer.getTextfield("ibanText","Bank").setText("");
        ebiModule.guiRenderer.getTextfield("countryBankText","Bank").setText("");
        bankDataControl.dataShow();
    }


    private void newBank() {
        bankDataControl.dataNew();
        isEdit = false;
    }

    private void editBank(int id) {
        
        bankDataControl.dataEdit(id);
        isEdit = true;
        ebiModule.guiRenderer.getTextfield("bankNameText","Bank").grabFocus();
    }

    public void saveBank() {
    	
    	final Runnable run = new Runnable(){
	    	public void run(){	
		    	if (!validateInput()) {
		            return;
		        }
		    	int row = 0;
		    	if(isEdit){
		    		row = ebiModule.guiRenderer.getTable("companyBankTable","Bank").getSelectedRow();
		    	}
		    	
		    	bankDataControl.dataStore(isEdit);
		        showBank();
		        
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyBankTable","Bank").changeSelection(row,0,false,false);
	    	}
      };
      
      Thread save = new Thread(run,"Save Bank");
      save.start();
      
    }

    private void deleteBank(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            bankDataControl.dataDelete(id);
            newBank();
        }
    }

    public void showBank() {
        bankDataControl.dataShow();
    }

    private boolean validateInput() {
  
        if ("".equals(ebiModule.guiRenderer.getTextfield("bankNameText","Bank").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_BANK_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("abaNrText","Bank").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_BANK_CODE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("accountNrText","Bank").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_BANK_NR")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
   
        for (int i = 0; i < this.tabModel.data.length; i++) {
            if(!isEdit){
                if (ebiModule.guiRenderer.getTextfield("accountNrText","Bank").getText().equals(this.tabModel.data[i][2].toString()) &&
                        ebiModule.guiRenderer.getTextfield("abaNrText","Bank").getText().equals(this.tabModel.data[i][1])) {

                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }


    public void ebiNew(){
        newBank();
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
            saveBank();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteBank(Integer.parseInt(tabModel.data[selectedRow][6].toString()));
        }
    }


}

