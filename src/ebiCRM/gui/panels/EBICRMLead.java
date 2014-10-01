package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlLeads;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIExtendedPanel;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;


public class EBICRMLead {

     public EBICRMModule ebiModule = null;
     public EBIDataControlLeads controlLeads = null;
     public int selectedRow = -1;
     public EBIAbstractTableModel tabModel = null;
     private final EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

     public EBICRMLead(EBICRMModule module){
        ebiModule = module;
        controlLeads = new EBIDataControlLeads(this);
     }

     public void initialize(){
        ebiModule.guiRenderer.getLabel("compNameLabel","Leads").setFont(new Font("Arial", Font.BOLD, 18));
        ebiModule.guiRenderer.getLabel("cName","Leads").setFont(new Font("Arial", Font.BOLD, 10));
        tabModel = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("leadsTable","Leads").getModel();

        ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));
        ebiModule.guiRenderer.getVisualPanel("Leads").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.guiRenderer.getVisualPanel("Leads").setChangedDate("");
        ebiModule.guiRenderer.getVisualPanel("Leads").setChangedFrom("");
        ebiModule.guiRenderer.getComboBox("genderText","Leads").setModel(new DefaultComboBoxModel(EBICRMModule.gendersList));
        ebiModule.guiRenderer.getComboBox("genderText","Leads").setEditable(true);
        ebiModule.guiRenderer.getComboBox("classificationText","Leads").setModel(new DefaultComboBoxModel(EBICRMModule.classification));
        EBIExtendedPanel businessCard = new EBIExtendedPanel("","images/user.png");
        businessCard.setSize(ebiModule.guiRenderer.getPanel("businessCard","Leads").getSize());
        businessCard.setBorder(BorderFactory.createLineBorder(Color.gray));
        businessCard.repaint();
        businessCard.updateUI();
        ebiModule.guiRenderer.getPanel("businessCard","Leads").add(businessCard,null);
        ebiModule.guiRenderer.getPanel("businessCard","Leads").invalidate();
        ebiModule.guiRenderer.getPanel("businessCard","Leads").repaint();

        try {

            if(!"null".equals(properties.getValue("LEADSSEARCH_TEXT")) && !"".equals(properties.getValue("LEADSSEARCH_TEXT"))){
                ebiModule.guiRenderer.getTextfield("searchLeadsText","Leads").setText(properties.getValue("LEADSSEARCH_TEXT"));
                controlLeads.dataShow(properties.getValue("LEADSSEARCH_TEXT"));
            }else{
                controlLeads.dataShow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeAction(){
        
       ebiModule.guiRenderer.getButton("saveLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
             ebiSave();
           }
       });

       ebiModule.guiRenderer.getButton("newLeads","Leads").setIcon(EBIConstant.ICON_NEW);
       ebiModule.guiRenderer.getButton("newLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              ebiNew();
           }
       });                                  

       ebiModule.guiRenderer.getButton("editLeads","Leads").setIcon(EBIConstant.ICON_EDIT);
       ebiModule.guiRenderer.getButton("editLeads","Leads").setEnabled(false); 
       ebiModule.guiRenderer.getButton("editLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
              }

              controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
              ebiModule.guiRenderer.getTextfield("compNameText","Leads").requestFocus();
           }
       });
       
       
       ebiModule.guiRenderer.getButton("copyLeads","Leads").setIcon(EBIConstant.ICON_COPY);
       ebiModule.guiRenderer.getButton("copyLeads","Leads").setEnabled(false);
       ebiModule.guiRenderer.getButton("copyLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
              }
              
              controlLeads.dataCopy(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
           }
       });

       ebiModule.guiRenderer.getButton("deleteLeads","Leads").setIcon(EBIConstant.ICON_DELETE);
       ebiModule.guiRenderer.getButton("deleteLeads","Leads").setEnabled(false);
       ebiModule.guiRenderer.getButton("deleteLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
              }
               
              ebiDelete();
           }
       });


      ebiModule.guiRenderer.getTable("leadsTable","Leads").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow() != -1 &&
                    				ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow() < tabModel.data.length){
                      
                    	selectedRow =  ebiModule.guiRenderer.getTable("leadsTable","Leads").convertRowIndexToModel(ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow());
 
                        if(selectedRow < tabModel.data.length){
	                        if (lsm.isSelectionEmpty()) {
	                            ebiModule.guiRenderer.getButton("editLeads","Leads").setEnabled(false);
	                            ebiModule.guiRenderer.getButton("deleteLeads","Leads").setEnabled(false);
	                            ebiModule.guiRenderer.getButton("copyLeads","Leads").setEnabled(false);
	                        } else if (!tabModel.data[selectedRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
	                            ebiModule.guiRenderer.getButton("editLeads","Leads").setEnabled(true);
	                            ebiModule.guiRenderer.getButton("deleteLeads","Leads").setEnabled(true);
	                            ebiModule.guiRenderer.getButton("copyLeads","Leads").setEnabled(true);
	                        }
                        }
                    }
                }
      });

      new JTableActionMaps(ebiModule.guiRenderer.getTable("leadsTable","Leads")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedRow= selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }

                }

                public void setUpKeyAction(int selRow) {
                    selectedRow = selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }

                }

                public void setEnterKeyAction(int selRow) {
                    selectedRow = selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }

                    controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
                }
            });

            ebiModule.guiRenderer.getTable("leadsTable","Leads").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow() != -1){
                        selectedRow =  ebiModule.guiRenderer.getTable("leadsTable","Leads").convertRowIndexToModel( ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow());

                            if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                    equals(tabModel.data[selectedRow][0].toString())) {
                                return;
                            }
                            if(e.getClickCount() >= 2 ){
                            	controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
                            }
                    }
                }
            });

           ebiModule.guiRenderer.getTextfield("searchLeadsText","Leads").addFocusListener(new FocusListener(){
                   public void focusLost(FocusEvent e){
                       properties.setValue("LEADSSEARCH_TEXT",ebiModule.guiRenderer.getTextfield("searchLeadsText","Leads").getText());
                       properties.saveProperties();
                   }
               public void focusGained(FocusEvent e){}
           });
        
          ebiModule.guiRenderer.getTextfield("searchLeadsText","Leads").addKeyListener(new KeyListener(){
             public void keyReleased(KeyEvent e){
                 controlLeads.dataShow(((JTextField)e.getSource()).getText());
                 if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    selectedRow = 0;
                    ebiModule.guiRenderer.getTable("leadsTable","Leads").changeSelection(0, 0, false, false);
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                    equals(tabModel.data[selectedRow][0].toString())) {
                                return;
                            }

                    controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
                    ebiModule.guiRenderer.getTable("leadsTable","Leads").requestFocus();
                 }
             }
             public void keyTyped(KeyEvent e){}
             public void keyPressed(KeyEvent e){}
          });


    }

    public void saveLeads(){
        if(!validateInput()){
            return;
        }

        boolean pass;
        if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanSave() ||
                ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
            pass = true;
        } else {
            pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
        }
        if (pass) {
            int row = 0;
            if(controlLeads.isEdit){
                row = ebiModule.guiRenderer.getTable("leadsTable","Leads").getSelectedRow();
            }
            controlLeads.dataStore();
            ebiModule.guiRenderer.getTable("leadsTable","Leads").changeSelection(row,0,false,false);
        }
        
    }

    private boolean validateInput(){
        if("".equals(ebiModule.guiRenderer.getTextfield("compNameText","Leads").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.guiRenderer.getComboBox("genderText","Leads").getSelectedItem())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_GENDER")).Show(EBIMessage.ERROR_MESSAGE); 
            return false;
        }

        return true;
    }

    public void ebiNew(){
        controlLeads.dataNew();
        ebiModule.guiRenderer.getTextfield("compNameText","Leads").requestFocus();
    }

    public void ebiSave(){
        saveLeads();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            boolean pass;
            if(ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                    ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()){
                pass = true;
            }else{
                pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
            }

            if(pass){
                if(controlLeads.dataDelete(Integer.parseInt(tabModel.data[selectedRow][11].toString())) == true){
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_DELETED")).Show(EBIMessage.INFO_MESSAGE);
                }
            }
        }
    }

}
