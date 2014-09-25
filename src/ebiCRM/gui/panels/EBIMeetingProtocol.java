package ebiCRM.gui.panels;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlMeetingProtocol;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyTableModelCRMContact;
import ebiCRM.table.models.MyTableModelCRMProtocol;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIMeetingProtocol  {

    public EBIDataControlMeetingProtocol meetingDataControl = null;
    public MyTableModelCRMProtocol tableModel = null;
    public static String[] art = null;
    public boolean isEdit = false;
    public MyTableModelCRMContact tabModelContact = null;
    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabmeetingDoc = null;
    private int selectedProtocolRow = -1;
    private int selectedContactRow = -1;
    private int selectedDocRow = -1;

    /**
     * This is the default constructor
     */
    public EBIMeetingProtocol(EBICRMModule main) {
        ebiModule = main;
        tabModelContact = new MyTableModelCRMContact();
        tableModel = new MyTableModelCRMProtocol();
        tabmeetingDoc = new MyTableModelDoc();
        meetingDataControl = new EBIDataControlMeetingProtocol(this);
    }

    public void initializeAction() {
        ebiModule.guiRenderer.getLabel("filterTable","MeetingCall").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","MeetingCall").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","MeetingCall").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","MeetingCall").getText()));
            }
        });

       /***********************************************************************************/
       // BEGIN OF TABLE MEETING DOCUMENT
       /***********************************************************************************/

        ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").setModel(tabmeetingDoc);
        ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
               
                    if(ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").getSelectedRow() != -1 && 
                    		ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").getSelectedRow() < tabmeetingDoc.data.length){
                        selectedDocRow = ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").convertRowIndexToModel(ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").getSelectedRow());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showMeetingDoc","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteMeetingDoc","MeetingCall").setEnabled(false);
                    } else if (!tabmeetingDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showMeetingDoc","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteMeetingDoc","MeetingCall").setEnabled(true);
                    }
                }
         });

         ebiModule.guiRenderer.getButton("newMeetingDoc","MeetingCall").setIcon(EBIConstant.ICON_NEW);
         ebiModule.guiRenderer.getButton("newMeetingDoc","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
         });
         
         
         ebiModule.guiRenderer.getButton("showMeetingDoc","MeetingCall").setIcon(EBIConstant.ICON_EXPORT);
         ebiModule.guiRenderer.getButton("showMeetingDoc","MeetingCall").setEnabled(false);
         ebiModule.guiRenderer.getButton("showMeetingDoc","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabmeetingDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabmeetingDoc.data[selectedDocRow][3].toString()));

                }
          });

         ebiModule.guiRenderer.getButton("deleteMeetingDoc","MeetingCall").setEnabled(false);
         ebiModule.guiRenderer.getButton("deleteMeetingDoc","MeetingCall").setIcon(EBIConstant.ICON_DELETE);
         ebiModule.guiRenderer.getButton("deleteMeetingDoc","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabmeetingDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDocs(Integer.parseInt(tabmeetingDoc.data[selectedDocRow][3].toString()));
                    }

                }
            });
       /***********************************************************************************/
       // BEGIN OF TABLE MEETING CONTACT
       /***********************************************************************************/
         ebiModule.guiRenderer.getTable("meetingContact","MeetingCall").setModel(tabModelContact);
         ebiModule.guiRenderer.getTable("meetingContact","MeetingCall").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("meetingContact","MeetingCall").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                  
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedContactRow = ebiModule.guiRenderer.getTable("meetingContact","MeetingCall").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("deleteMeetingContact","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("editMeetingContact","MeetingCall").setEnabled(false);
                    } else if (!tabModelContact.data[selectedContactRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {

                        ebiModule.guiRenderer.getButton("deleteMeetingContact","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("editMeetingContact","MeetingCall").setEnabled(true);
                    }
                }
            });

          ebiModule.guiRenderer.getButton("newMeetingContact","MeetingCall").setIcon(EBIConstant.ICON_NEW);
          ebiModule.guiRenderer.getButton("newMeetingContact","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addContact();
                }
          });

          ebiModule.guiRenderer.getButton("editMeetingContact","MeetingCall").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editMeetingContact","MeetingCall").setEnabled(false);
          ebiModule.guiRenderer.getButton("editMeetingContact","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelContact.data[selectedContactRow][0].toString())) {
                        return;
                    }

                    editContact(Integer.parseInt(tabModelContact.data[selectedContactRow][6].toString()));

                }
          });

          ebiModule.guiRenderer.getButton("deleteMeetingContact","MeetingCall").setIcon(EBIConstant.ICON_DELETE);
          ebiModule.guiRenderer.getButton("deleteMeetingContact","MeetingCall").setEnabled(false);
          ebiModule.guiRenderer.getButton("deleteMeetingContact","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedContactRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModelContact.data[selectedContactRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteContact(Integer.parseInt(tabModelContact.data[selectedContactRow][6].toString()));
                    }

                }
          });

        /***********************************************************************************/
        // BEGIN OF TABLE AVAILABLE MEETING 
        /***********************************************************************************/

         ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").setModel(tableModel);
         ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
         ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                   
                    if(ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectedRow() != -1 && 
                    			ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectedRow() < tableModel.data.length){
                     
                        	selectedProtocolRow = ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectedRow());
                   
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveMeeting","MeetingCall").setEnabled(false);
                        ebiModule.guiRenderer.getButton("mailMeeting","MeetingCall").setEnabled(false);
                    } else if (!tableModel.data[selectedProtocolRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveMeeting","MeetingCall").setEnabled(true);
                        ebiModule.guiRenderer.getButton("mailMeeting","MeetingCall").setEnabled(true);
                    }
                }
            });


            new JTableActionMaps(ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedProtocolRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedProtocolRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedProtocolRow = selRow;


                    if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedProtocolRow][0].toString())) {
                        return;
                    }
                    editMeeting(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
                    ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").grabFocus();
                }
            });

            ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                   if(ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").rowAtPoint(e.getPoint()) != -1){
                        selectedProtocolRow = ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").rowAtPoint(e.getPoint()));

                        if (e.getClickCount() == 2) {

                            if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                    equals(tableModel.data[selectedProtocolRow][0].toString())) {
                                return;
                            }

                            editMeeting(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
                            ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").grabFocus();
                        }
                   }
                }
            });

           ebiModule.guiRenderer.getButton("newMeeting","MeetingCall").setIcon(EBIConstant.ICON_NEW);
           ebiModule.guiRenderer.getButton("newMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
            });
           
           
           ebiModule.guiRenderer.getButton("copyMeeting","MeetingCall").setIcon(EBIConstant.ICON_COPY);
           ebiModule.guiRenderer.getButton("copyMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("copyMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                  public void actionPerformed(java.awt.event.ActionEvent e) {
                	  if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                              					equals(tableModel.data[selectedProtocolRow][0].toString())) {
                          return;
                      }

                      meetingDataControl.dataCopy(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
                  }
            });


        ebiModule.guiRenderer.getButton("moveMeeting","MeetingCall").setIcon(EBIConstant.ICON_MOVE_RECORD);
        ebiModule.guiRenderer.getButton("moveMeeting","MeetingCall").setEnabled(false);
        ebiModule.guiRenderer.getButton("moveMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                        equals(tableModel.data[selectedProtocolRow][0].toString())) {
                    return;
                }

                meetingDataControl.dataMove(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
            }
        });
           
           ebiModule.guiRenderer.getButton("editMeeting","MeetingCall").setIcon(EBIConstant.ICON_EDIT);
           ebiModule.guiRenderer.getButton("editMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("editMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            					equals(tableModel.data[selectedProtocolRow][0].toString())) {
                        return;
                    }
 
                    editMeeting(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
                    ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").grabFocus();
                }
           });

           ebiModule.guiRenderer.getButton("deleteMeeting","MeetingCall").setIcon(EBIConstant.ICON_DELETE);
           ebiModule.guiRenderer.getButton("deleteMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("deleteMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedProtocolRow][0].toString())) {
                        return;
                    }
                    ebiDelete();

                }
           });

           ebiModule.guiRenderer.getButton("reportMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("reportMeeting","MeetingCall").setIcon(EBIConstant.ICON_REPORT);
           ebiModule.guiRenderer.getButton("reportMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedProtocolRow][0].toString())) {
                        return;
                    }

                    boolean pass  ;
                    if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                            ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                        pass = true;
                    } else {
                        pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
                    }
                    if (pass) {
                        meetingDataControl.dataShowReport(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
                    }
                }
           });

           ebiModule.guiRenderer.getButton("historyMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("historyMeeting","MeetingCall").setIcon(EBIConstant.ICON_HISTORY);
           ebiModule.guiRenderer.getButton("historyMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Meeting"), ebiModule).setVisible();
                }
            });

           ebiModule.guiRenderer.getButton("mailMeeting","MeetingCall").setEnabled(false);
           ebiModule.guiRenderer.getButton("mailMeeting","MeetingCall").setIcon(EBIConstant.ICON_SEND_MAIL);
           ebiModule.guiRenderer.getButton("mailMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedProtocolRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selectedProtocolRow][0].toString())) {
                        return;
                    }
                    boolean pass  ;
                    if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                            ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                        pass = true;
                    } else {
                        pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
                    }
                    if (pass) {
                        meetingDataControl.dataShowAndMailReport(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()), false);
                    }

                }
           });

           ebiModule.guiRenderer.getButton("saveMeeting","MeetingCall").setToolTipText(EBIPGFactory.getLANG("EBI_LANG_C_TOOL_TIP_CRM_ADD_MEMO"));
           ebiModule.guiRenderer.getButton("saveMeeting","MeetingCall").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                   ebiSave();
                }
            });

    }

    public void initialize() {

        ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        ebiModule.guiRenderer.getVisualPanel("MeetingCall").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("MeetingCall").setChangedFrom("");

        ebiModule.guiRenderer.getTable("meetingDoc","MeetingCall").setModel(tabmeetingDoc);
        ebiModule.guiRenderer.getTable("meetingContact","MeetingCall").setModel(tabModelContact);
        ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").setModel(tableModel);

        ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").setText("");
        ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").grabFocus();

        ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setEditable(true);
        ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").setModel(new DefaultComboBoxModel(art));

        ebiModule.guiRenderer.getTextarea("meetingDescription","MeetingCall").setText("");

        ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").setDate(null);
        ebiModule.guiRenderer.getTimepicker("dateMeetingText","MeetingCall").setFormats(EBIPGFactory.DateFormat);
        meetingDataControl.dataShow();
    }

    public void showMeetingProtocol() {
        meetingDataControl.dataShow();
    }

    public void saveMeeting() {
        
      final Runnable run = new Runnable(){	
	       public void run(){	  
		    	if (!validateInput()) {
		            return;
		        }
		    	int row = 0;
		    	if(isEdit){
		    		row = ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").getSelectedRow();
		    	}
		        meetingDataControl.dataStore(isEdit);
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("companyMeetings","MeetingCall").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Meeting");
      save.start();
    }

    private void editMeeting(int id) {
        meetingDataControl.dataEdit(id);
        isEdit = true;
    }

    private void removeMeeting(int id) {
        boolean pass  ;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            meetingDataControl.dataDelete(id);
            newMeeting();
        }
    }

    private void newMeeting() {
        meetingDataControl.dataNew();
        if (isEdit == true) {
            isEdit = false;
        }
    }

    private boolean validateInput() {
 
        if (ebiModule.guiRenderer.getComboBox("meetingTypeText","MeetingCall").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_REPORT_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").getText().trim().equals("")) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SELECT_VALID_REF")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (isEdit == false) {
            for (int i = 0; i < tableModel.data.length; i++) {
                if (tableModel.data[i][1].toString().equals(ebiModule.guiRenderer.getTextfield("subjectMeetingText","MeetingCall").getText())) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_MEETING_DUPLICATE_RECORD_EXIST")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }


    private void addContact() {
        meetingDataControl.dataAddContact();

    }

    private void editContact(int id) {
        meetingDataControl.dataEditContact(id);
    }

    private void deleteContact(int id) {
        meetingDataControl.dataDeleteContact(id);
    }


    private void newDocs() {
        meetingDataControl.dataStoreDoc();
    }

    private void saveAndShowDocs(int id) {
        meetingDataControl.dataEditDoc(id);
    }

    private void deleteDocs(int id) {
        meetingDataControl.dataDeleteDoc(id);
    }

    public void ebiNew(){
        newMeeting();
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
            saveMeeting();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            removeMeeting(Integer.parseInt(tableModel.data[selectedProtocolRow][4].toString()));
        }
    }

}