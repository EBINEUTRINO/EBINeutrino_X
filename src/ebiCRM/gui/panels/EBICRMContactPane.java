package ebiCRM.gui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlContact;
import ebiCRM.gui.dialogs.EBIAddressSelectionDialog;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMAddress;
import ebiCRM.table.models.MyTableModelCRMContact;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBICRMContactPane {

    public static final String[] gendersList = {EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), EBIPGFactory.getLANG("EBI_LANG_C_MALE"), EBIPGFactory.getLANG("EBI_LANG_C_FEMALE")};
    public EBICRMModule ebiModule = null;
    public MyTableModelCRMContact tableModel = null;
    public boolean isEdit = false;
    public MyTableModelCRMAddress addressModel = null;
    public EBIDataControlContact contactDataControl = null;
    private int selectedContactRow = -1;
    private int selectedAddressRow = -1;
    public IEBIGUIRenderer guiRenderer = null;

    /**
     * This is the default constructor
     */
    public EBICRMContactPane(EBICRMModule main) {
        ebiModule = main;
        guiRenderer = main.guiRenderer;
        tableModel = new MyTableModelCRMContact();
        addressModel = new MyTableModelCRMAddress();
        contactDataControl = new EBIDataControlContact(this);

    }

    public void initializeAction() {

         ebiModule.guiRenderer.getLabel("filterTable","Contact").setHorizontalAlignment(SwingUtilities.RIGHT);
         ebiModule.guiRenderer.getTextfield("filterTableText","Contact").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyContacts","Contact").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Contact").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyContacts","Contact").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Contact").getText()));
            }
        });

        guiRenderer.getButton("newContact","Contact").setIcon(EBIConstant.ICON_NEW);
        guiRenderer.getButton("editContact","Contact").setEnabled(false);

        guiRenderer.getButton("deleteContact","Contact").setIcon(EBIConstant.ICON_DELETE);
        guiRenderer.getButton("deleteContact","Contact").setEnabled(false);

        guiRenderer.getButton("historyContact","Contact").setIcon(EBIConstant.ICON_HISTORY);
        guiRenderer.getButton("addContactAddress","Contact").setIcon(EBIConstant.ICON_NEW);

        guiRenderer.getButton("copyContact","Contact").setIcon(EBIConstant.ICON_COPY);
        guiRenderer.getButton("copyContact","Contact").setEnabled(false);

        guiRenderer.getButton("moveContact","Contact").setIcon(EBIConstant.ICON_MOVE_RECORD);
        guiRenderer.getButton("moveContact","Contact").setEnabled(false);
        
        guiRenderer.getButton("deleteContactAddress","Contact").setIcon(EBIConstant.ICON_DELETE);
        guiRenderer.getButton("deleteContactAddress","Contact").setEnabled(false);
        
        guiRenderer.getButton("copyContact","Contact").addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	 if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                         equals(tableModel.data[selectedContactRow][0].toString())) {
                     return;
                 }

                 contactDataControl.dataCopy(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
            }
        });

        guiRenderer.getButton("moveContact","Contact").addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tableModel.data[selectedContactRow][0].toString())) {
                    return;
                }

                contactDataControl.dataMove(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
            }
        });


        guiRenderer.getButton("editContact","Contact").setIcon(EBIConstant.ICON_EDIT);
        guiRenderer.getButton("editContact","Contact").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedContactRow][0].toString())) {
                        return;
                    }

                    editContact(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
                    guiRenderer.getComboBox("genderTex","Contact").grabFocus();
                }
            });

        guiRenderer.getButton("deleteContact","Contact").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedContactRow][0].toString())) {
                        return;
                    }

                    ebiDelete();
                    
                }
            });

        guiRenderer.getButton("saveContact","Contact").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    ebiSave();
                }
         });

        guiRenderer.getButton("newContact","Contact").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                   ebiNew();
                }
        });

        guiRenderer.getTable("companyContacts","Contact").setModel(tableModel);
        guiRenderer.getTable("companyContacts","Contact").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        guiRenderer.getTable("companyContacts","Contact").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                     try{
                        selectedContactRow = guiRenderer.getTable("companyContacts","Contact").convertRowIndexToModel(lsm.getMinSelectionIndex());
                     }catch(IndexOutOfBoundsException ex){ selectedContactRow = 0;}
                    }

                    if (lsm.isSelectionEmpty()) {
                        guiRenderer.getButton("editContact","Contact").setEnabled(false);
                        guiRenderer.getButton("deleteContact","Contact").setEnabled(false);
                        guiRenderer.getButton("historyContact","Contact").setEnabled(false);
                        guiRenderer.getButton("copyContact","Contact").setEnabled(false);
                        guiRenderer.getButton("moveContact","Contact").setEnabled(false);
                    } else if (!tableModel.data[selectedContactRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        guiRenderer.getButton("editContact","Contact").setEnabled(true);
                        guiRenderer.getButton("deleteContact","Contact").setEnabled(true);
                        guiRenderer.getButton("historyContact","Contact").setEnabled(true);
                        guiRenderer.getButton("copyContact","Contact").setEnabled(true);
                        guiRenderer.getButton("moveContact","Contact").setEnabled(true);
                    }
                }
            });

        new JTableActionMaps(guiRenderer.getTable("companyContacts","Contact")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedContactRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedContactRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedContactRow = selRow;
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedContactRow][0].toString())) {
                        return;
                    }
                   
                    editContact(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
                    guiRenderer.getComboBox("genderTex","Contact").grabFocus();
                }
            });

        guiRenderer.getTable("companyContacts","Contact").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(guiRenderer.getTable("companyContacts","Contact").rowAtPoint(e.getPoint()) != -1){
                        selectedContactRow = guiRenderer.getTable("companyContacts","Contact").convertRowIndexToModel(guiRenderer.getTable("companyContacts","Contact").rowAtPoint(e.getPoint()));
                    }

                    if (e.getClickCount() == 2) {

                        if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tableModel.data[selectedContactRow][0].toString())) {
                            return;
                        }

                        editContact(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
                        guiRenderer.getComboBox("genderTex","Contact").grabFocus();

                    }
                }
            });

        guiRenderer.getButton("historyContact","Contact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Contact"), ebiModule).setVisible();
                }

         });

        guiRenderer.getButton("addContactAddress","Contact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIAddressSelectionDialog daddress = new EBIAddressSelectionDialog(ebiModule, ebiModule.getAddressPane().addressDataControl.getAddressList(), contactDataControl.getCoaddressList());
                    daddress.setVisible();
                    contactDataControl.showCompanyContactAddress();
                }
        });

        guiRenderer.getButton("deleteContactAddress","Contact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedAddressRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(addressModel.data[selectedAddressRow][0].toString())) {
                        return;
                    }

                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        removeAdress(Integer.parseInt(addressModel.data[selectedAddressRow][6].toString()));
                    }
                    
                }
         });

        guiRenderer.getTable("contactTableAddress","Contact").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedAddressRow = guiRenderer.getTable("contactTableAddress","Contact").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        guiRenderer.getButton("deleteContactAddress","Contact").setEnabled(false);
                    } else if (!addressModel.data[selectedAddressRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        guiRenderer.getButton("deleteContactAddress","Contact").setEnabled(true);
                    }
                }
         });
    }
    
    public void initialize() {

        guiRenderer.getVisualPanel("Contact").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        guiRenderer.getVisualPanel("Contact").setCreatedFrom(EBIPGFactory.ebiUser);
        guiRenderer.getVisualPanel("Contact").setChangedDate("");
        guiRenderer.getVisualPanel("Contact").setChangedFrom("");

        guiRenderer.getComboBox("genderTex","Contact").setModel(new DefaultComboBoxModel(gendersList));
        guiRenderer.getComboBox("genderTex","Contact").setEditable(true);
        guiRenderer.getComboBox("genderTex","Contact").grabFocus();
        guiRenderer.getTextfield("positionText","Contact").setText("");
        guiRenderer.getTextfield("nameText","Contact").setText("");
        guiRenderer.getTextfield("titleText","Contact").setText("");

        guiRenderer.getTextfield("surnameText","Contact").setText("");
        guiRenderer.getTextfield("middleNameText","Contact").setText("");
        guiRenderer.getTimepicker("birddateText","Contact").setFormats(EBIPGFactory.DateFormat);
        guiRenderer.getTimepicker("birddateText","Contact").setDate(new Date());
        guiRenderer.getTimepicker("birddateText","Contact").getEditor().setText("");
        guiRenderer.getTextfield("telefonText","Contact").setText("");
        guiRenderer.getTextfield("faxText","Contact").setText("");
        guiRenderer.getTextfield("mobileText","Contact").setText("");
        guiRenderer.getTextfield("emailText","Contact").setText("");
        guiRenderer.getTextarea("contactDescription","Contact").setText("");
        guiRenderer.getCheckBox("mainContactText","Contact").setSelected(false);

        guiRenderer.getComboBox("genderTex","Contact").grabFocus();
        guiRenderer.getTable("contactTableAddress","Contact").setModel(addressModel);
        contactDataControl.dataShow();
    }

    public void saveContact() {
      final Runnable run = new Runnable(){
	    	public void run(){
		        if (!validateInput()) {
		            return;
		        }
		        
		        int row = 0;
		        
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("companyContacts","Contact").getSelectedRow();
		        }
		        
		        contactDataControl.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyContacts","Contact").changeSelection(row,0,false,false);
	    	}
      };
      
      Thread save = new Thread(run,"Save contact");
      save.start();
      
    }

    public void editContact(int id) {
        contactDataControl.dataEdit(id);
        isEdit = true;
    }

    public void removeContact(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            contactDataControl.dataDelete(id);
            newContact();
        }
    }

    public void newContact() {
        contactDataControl.dataNew();
        if (isEdit == true) {
            isEdit = false;
        }
    }

    private boolean validateInput() {
        if (guiRenderer.getComboBox("genderTex","Contact").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_GENDER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(guiRenderer.getTextfield("surnameText","Contact").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void removeAdress(int id) {
        try {
            contactDataControl.dataAddressDelete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ebiNew(){
        newContact();
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
            saveContact();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            removeContact(Integer.parseInt(tableModel.data[selectedContactRow][6].toString()));
        }
    }

}

