package ebiCRM.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlActivity;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.table.models.MyOwnCellRederer;
import ebiCRM.table.models.MyTableModelActivities;
import ebiCRM.table.models.MyTableModelDoc;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMCompanyActivity implements ChangeListener {

    public EBICRMModule ebiModule = null;
    public MyTableModelDoc tabActDoc = null;
    public MyTableModelActivities tabModel = null;
    public boolean isEdit = false;
    public static String[] actType = null;
    public static String[] actStatus = null;
    public static String[] ActivityStatus = null;
    public EBIDataControlActivity activityDataControl = null;
    private int selectedActivityRow = -1;
    private int selectedDocRow = -1;
    private JColorChooser jch = null;

    /**
     * This is the default constructor
     */
    public EBICRMCompanyActivity(EBICRMModule modul) {
        ebiModule = modul;
        tabActDoc = new MyTableModelDoc();
        tabModel = new MyTableModelActivities();
        activityDataControl = new EBIDataControlActivity(this);
    }

    public void stateChanged(ChangeEvent e) {
        ebiModule.guiRenderer.getLabel("colorPanel","Activity").setBackground(jch.getColor());
    }

    public void initializeAction() {

        ebiModule.guiRenderer.getComboBox("timerStartText", "Activity").setModel(new DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"5 min","10 min","15 min","20 min","25 min","30 min","35 min","40 min","50 min","60 min"}));

        ebiModule.guiRenderer.getLabel("filterTable","Activity").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Activity").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableActivity","Activity").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Activity").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("tableActivity","Activity").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Activity").getText()));
            }
        });

         ebiModule.guiRenderer.getButton("selectColor","Activity").addActionListener(new java.awt.event.ActionListener() {

               public void actionPerformed(java.awt.event.ActionEvent e) {

                   EBIDialogExt diaColor = new EBIDialogExt(null);
                   EBIVisualPanelTemplate panel = new EBIVisualPanelTemplate();
                   panel.setEnableChangeComponent(false);
                   panel.setClosable(true);
                   panel.setModuleTitle("EBI Colorchooser");
                   panel.setModuleIcon(EBIConstant.ICON_APP);
                   diaColor.setSize(600,400);
                   diaColor.setClosable(true);
                   diaColor.setModal(true);
                   diaColor.setName("ActivityColorChooser");
                   jch = new JColorChooser();
                   jch.setBackground(EBIPGFactory.systemColor);
                   for(int i =0; i< jch.getChooserPanels().length;i++ ){
                     jch.getChooserPanels()[i].getParent().setBackground(EBIPGFactory.systemColor);
                     jch.getChooserPanels()[i].setBackground(EBIPGFactory.systemColor);
                   }
                   jch.getPreviewPanel().setBackground(EBIPGFactory.systemColor);
                   jch.getSelectionModel().addChangeListener(EBICRMCompanyActivity.this);
                   panel.getPanel().setLayout(new BorderLayout());
                   panel.add(jch,BorderLayout.CENTER);

                   diaColor.setContentPane(panel);
                   diaColor.setVisible(true);

                }
         });

         /******************************************************************************/
         // ACTIVITY TABLE DOC
         /*****************************************************************************/

          ebiModule.guiRenderer.getTable("tableActivityDoc","Activity").setModel(tabActDoc);
          ebiModule.guiRenderer.getTable("tableActivityDoc","Activity").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          ebiModule.guiRenderer.getTable("tableActivityDoc","Activity").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    
                    if(lsm.getMinSelectionIndex() != -1){
                        selectedDocRow = ebiModule.guiRenderer.getTable("tableActivityDoc","Activity").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showActivityDoc","Activity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteActivityDoc","Activity").setEnabled(false);
                    } else if (!tabActDoc.data[selectedDocRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showActivityDoc","Activity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteActivityDoc","Activity").setEnabled(true);
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newActivityDoc","Activity").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newActivityDoc","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newDocs();
                }
            });

            ebiModule.guiRenderer.getButton("showActivityDoc","Activity").setIcon(EBIConstant.ICON_EXPORT);
            ebiModule.guiRenderer.getButton("showActivityDoc","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("showActivityDoc","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabActDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }

                    saveAndShowDocs(Integer.parseInt(tabActDoc.data[selectedDocRow][3].toString()));

                }
            });

            ebiModule.guiRenderer.getButton("deleteActivityDoc","Activity").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteActivityDoc","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteActivityDoc","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectedDocRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabActDoc.data[selectedDocRow][0].toString())) {
                        return;
                    }
                    
                    if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
                        deleteDoc(Integer.parseInt(tabActDoc.data[selectedDocRow][3].toString()));
                    }

                }
            });

         /******************************************************************************/
         // ACTIVITY TABLE
         /*****************************************************************************/
          ebiModule.guiRenderer.getTable("tableActivity","Activity").setDefaultRenderer(Object.class, new MyOwnCellRederer(false,true));
          ebiModule.guiRenderer.getTable("tableActivity","Activity").setModel(tabModel);
          ebiModule.guiRenderer.getTable("tableActivity","Activity").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if( ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow() != -1 &&
                            ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow() <= tabModel.data.length){
                        selectedActivityRow =  ebiModule.guiRenderer.getTable("tableActivity","Activity").convertRowIndexToModel(ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow());
                    }else{
                        selectedActivityRow =0;
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editActivity","Activity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteActivity","Activity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyActivity","Activity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyActivity","Activity").setEnabled(false);
                        ebiModule.guiRenderer.getButton("moveActivity","Activity").setEnabled(false);
                    } else if (!tabModel.data[selectedActivityRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editActivity","Activity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteActivity","Activity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyActivity","Activity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyActivity","Activity").setEnabled(true);
                        ebiModule.guiRenderer.getButton("moveActivity","Activity").setEnabled(true);
                    }

                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("tableActivity","Activity")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedActivityRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedActivityRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedActivityRow = selRow;
                    if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedActivityRow][0].toString())) {
                        return;
                    }
                    
                    editActivity(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("activityNameText","Activity").grabFocus();
                }
            });

            ebiModule.guiRenderer.getTable("tableActivity","Activity").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    
                    if(ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow() != -1){
                        selectedActivityRow =  ebiModule.guiRenderer.getTable("tableActivity","Activity").convertRowIndexToModel( ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow());
    
                        if (e.getClickCount() == 2) {

                            if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                    equals(tabModel.data[selectedActivityRow][0].toString())) {
                                return;
                            }

                            editActivity(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
                            ebiModule.guiRenderer.getTextfield("activityNameText","Activity").grabFocus();
                        }
                    }
                }
            });

            ebiModule.guiRenderer.getButton("newActivity","Activity").setIcon(EBIConstant.ICON_NEW);
            ebiModule.guiRenderer.getButton("newActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiNew();
                }
            });

            ebiModule.guiRenderer.getButton("copyActivity","Activity").setIcon(EBIConstant.ICON_COPY);
            ebiModule.guiRenderer.getButton("copyActivity","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("copyActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedActivityRow][0].toString())) {
                        return;
                    }
                    activityDataControl.dataCopy(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
                }
            });

            ebiModule.guiRenderer.getButton("moveActivity","Activity").setIcon(EBIConstant.ICON_MOVE_RECORD);
            ebiModule.guiRenderer.getButton("moveActivity","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("moveActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedActivityRow][0].toString())) {
                        return;
                    }
                    activityDataControl.dataMove(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
                }
            });
            
            ebiModule.guiRenderer.getButton("editActivity","Activity").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("editActivity","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("editActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedActivityRow][0].toString())) {
                        return;
                    }
                    
                    editActivity(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
                    ebiModule.guiRenderer.getTextfield("activityNameText","Activity").grabFocus();
                }
            });

            ebiModule.guiRenderer.getButton("deleteActivity","Activity").setIcon(EBIConstant.ICON_DELETE);
            ebiModule.guiRenderer.getButton("deleteActivity","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("deleteActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    if (selectedActivityRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedActivityRow][0].toString())) {
                        return;
                    }

                    ebiDelete();

                }
            });

            ebiModule.guiRenderer.getButton("historyActivity","Activity").setEnabled(false);
            ebiModule.guiRenderer.getButton("historyActivity","Activity").setIcon(EBIConstant.ICON_HISTORY);
            ebiModule.guiRenderer.getButton("historyActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(ebiModule.companyID, "Activities"), ebiModule).setVisible();
                }
            });

            ebiModule.guiRenderer.getButton("saveActivity","Activity").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                   ebiSave();
                }
            });
         ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").getEditor().setText("");
     }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {

        ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new java.util.Date()));
        ebiModule.guiRenderer.getVisualPanel("Activity").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Activity").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Activity").setChangedFrom("");

        tabActDoc.data =  new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", ""}};
        tabActDoc.fireTableDataChanged();
        tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "","",""}};
        tabModel.fireTableDataChanged();

        ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setModel(new javax.swing.DefaultComboBoxModel(actType));
        ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setEditable(true);
        ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setModel(new javax.swing.DefaultComboBoxModel(actStatus));

        ebiModule.guiRenderer.getComboBox("activityStatusText","Activity").setSelectedIndex(0);
        ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").setSelectedIndex(0);

        ebiModule.guiRenderer.getSpinner("dueH","Activity").setValue(0);
        ebiModule.guiRenderer.getSpinner("dueMin","Activity").setValue(0);
        ebiModule.guiRenderer.getTextfield("durationText","Activity").setText("");

        ebiModule.guiRenderer.getLabel("colorPanel","Activity").setBackground(new Color(5, 125, 255));
        ebiModule.guiRenderer.getLabel("colorPanel","Activity").setOpaque(true);
        ebiModule.guiRenderer.getTextfield("activityNameText","Activity").setText("");
        ebiModule.guiRenderer.getTextarea("activityDescription","Activity").setText("");
        ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").setDate(null);
        ebiModule.guiRenderer.getTimepicker("activityTODOText","Activity").setFormats(EBIPGFactory.DateFormat);
        activityDataControl.dataShow(false);
    }

    private void newDocs() {
        activityDataControl.dataNewDoc();
    }

    private void saveAndShowDocs(int id) {
        activityDataControl.dataViewDoc(id);
    }

    private void deleteDoc(int id) {
        activityDataControl.dataDeleteDoc(id);
    }

    private void newActivity() {
        activityDataControl.dataNew();
        isEdit = false;
    }

    public void saveActivity() {
      final Runnable run = new Runnable(){
    	  
	       public synchronized void run(){
		        if (!validateInput()) {
		            return;
		        }
		        
		        int row = 0;
		        
		        if(isEdit){
		        	row = ebiModule.guiRenderer.getTable("tableActivity","Activity").getSelectedRow();
		        }
		        
		        activityDataControl.dataStore(isEdit);
		        showActivity();
		        try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
				ebiModule.guiRenderer.getTable("tableActivity","Activity").changeSelection(row,0,false,false);
	       }
      };
      
      Thread save = new Thread(run,"Save Activity");
      save.start();
      
    }

    public void editActivity(int id) {
        activityDataControl.dataEdit(id);
        isEdit = true;

    }

    private void deleteActivity(int id) {
        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            activityDataControl.dataDelete(id);
            newActivity();
        }
    }

    public void showActivity() {
        activityDataControl.dataShow(false);
    }

    private boolean validateInput() {

        try{
           if(Integer.parseInt(ebiModule.guiRenderer.getTextfield("durationText","Activity").getText()) <= 0){
              EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_VALID_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
              return false;
           }
        }catch(NumberFormatException ex){
           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_VALID_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
           return false; 
        }

        if ("".equals(ebiModule.guiRenderer.getTextfield("activityNameText","Activity").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (ebiModule.guiRenderer.getComboBox("activityTypeText","Activity").getSelectedItem().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void ebiNew(){
        newActivity();
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
            saveActivity();
        }
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            deleteActivity(Integer.parseInt(tabModel.data[selectedActivityRow][7].toString()));
        }
    }


}