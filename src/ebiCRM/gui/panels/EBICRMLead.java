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

import javax.swing.*;
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
        ebiModule.gui.getLabel("compNameLabel","Leads").setFont(new Font("Arial", Font.BOLD, 18));
        ebiModule.gui.getLabel("cName","Leads").setFont(new Font("Arial", Font.BOLD, 10));
        tabModel = (EBIAbstractTableModel)ebiModule.gui.getTable("leadsTable","Leads").getModel();

        ebiModule.gui.getVisualPanel("Leads").setCreatedDate(ebiModule.system.getDateToString(new Date()));
        ebiModule.gui.getVisualPanel("Leads").setCreatedFrom(EBIPGFactory.ebiUser);
        ebiModule.gui.getVisualPanel("Leads").setChangedDate("");
        ebiModule.gui.getVisualPanel("Leads").setChangedFrom("");
        ebiModule.gui.getComboBox("genderText","Leads").setModel(new DefaultComboBoxModel(EBICRMModule.gendersList));
        ebiModule.gui.getComboBox("genderText","Leads").setEditable(true);
        ebiModule.gui.getComboBox("classificationText","Leads").setModel(new DefaultComboBoxModel(EBICRMModule.classification));
        EBIExtendedPanel businessCard = new EBIExtendedPanel("","images/user.png");
        businessCard.setSize(ebiModule.gui.getPanel("businessCard","Leads").getSize());
        businessCard.setBorder(BorderFactory.createLineBorder(Color.gray));
        businessCard.repaint();
        businessCard.updateUI();
        ebiModule.gui.getPanel("businessCard","Leads").add(businessCard,null);
        ebiModule.gui.getPanel("businessCard","Leads").invalidate();
        ebiModule.gui.getPanel("businessCard","Leads").repaint();

        try {

            if(!"null".equals(properties.getValue("LEADSSEARCH_TEXT")) && !"".equals(properties.getValue("LEADSSEARCH_TEXT"))){
                ebiModule.gui.getTextfield("searchLeadsText","Leads").setText(properties.getValue("LEADSSEARCH_TEXT"));
                controlLeads.dataShow(properties.getValue("LEADSSEARCH_TEXT"));
            }else{
                controlLeads.dataShow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeAction(){
        
       ebiModule.gui.getButton("saveLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
             ebiSave();
           }
       });

       ebiModule.gui.getButton("newLeads","Leads").setIcon(EBIConstant.ICON_NEW);
       ebiModule.gui.getButton("newLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              ebiNew();
           }
       });

       ebiModule.gui.getButton("copyLeads","Leads").setIcon(EBIConstant.ICON_COPY);
       ebiModule.gui.getButton("copyLeads","Leads").setEnabled(false);
       ebiModule.gui.getButton("copyLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
              }
              
              controlLeads.dataCopy(Integer.parseInt(tabModel.data[selectedRow][11].toString()));
           }
       });

       ebiModule.gui.getButton("deleteLeads","Leads").setIcon(EBIConstant.ICON_DELETE);
       ebiModule.gui.getButton("deleteLeads","Leads").setEnabled(false);
       ebiModule.gui.getButton("deleteLeads","Leads").addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
              if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
              }
               
              ebiDelete();
           }
       });


      ebiModule.gui.getTable("leadsTable","Leads").setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ebiModule.gui.getTable("leadsTable","Leads").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    /*if (e.getValueIsAdjusting()) {
                        return;
                    }*/

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(ebiModule.gui.getTable("leadsTable","Leads").getSelectedRow() != -1 &&
                            ebiModule.gui.getTable("leadsTable","Leads").getSelectedRow() < tabModel.data.length){

                        selectedRow =  ebiModule.gui.getTable("leadsTable","Leads").convertRowIndexToModel(lsm.getMinSelectionIndex());

                        if(selectedRow < tabModel.data.length){
                            if (lsm.isSelectionEmpty()) {
                                ebiModule.gui.getButton("deleteLeads","Leads").setEnabled(false);
                                ebiModule.gui.getButton("copyLeads","Leads").setEnabled(false);
                            }else if(!tabModel.data[selectedRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
                                ebiModule.gui.getButton("deleteLeads","Leads").setEnabled(true);
                                ebiModule.gui.getButton("copyLeads","Leads").setEnabled(true);
                            }
                        }

                        if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedRow][0].toString())) {
                            return;
                        }


                    }
                }
      });

      new JTableActionMaps(ebiModule.gui.getTable("leadsTable","Leads")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedRow = selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }

                    controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));

                }

                public void setUpKeyAction(int selRow) {
                    selectedRow = selRow;
                    if (selectedRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedRow][0].toString())) {
                        return;
                    }

                    controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));

                }

                public void setEnterKeyAction(int selRow) {

                }
            });

            ebiModule.gui.getTable("leadsTable","Leads").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseReleased(java.awt.event.MouseEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {

                            if(ebiModule.gui.getTable("leadsTable", "Leads").getSelectedRow() < 0){return;}
                            if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tabModel.data[selectedRow][0].toString())){ return; }

                            selectedRow = ebiModule.gui.getTable("leadsTable", "Leads").convertRowIndexToModel(ebiModule.gui.getTable("leadsTable", "Leads").getSelectedRow());
                            controlLeads.dataEdit(Integer.parseInt(tabModel.data[selectedRow][11].toString()));


                        }
                    });

                }
            });

           ebiModule.gui.getTextfield("searchLeadsText","Leads").addFocusListener(new FocusListener(){
                   public void focusLost(FocusEvent e){
                       properties.setValue("LEADSSEARCH_TEXT",ebiModule.gui.getTextfield("searchLeadsText","Leads").getText());
                       properties.saveProperties();
                   }
               public void focusGained(FocusEvent e){}
           });
        
          ebiModule.gui.getTextfield("searchLeadsText","Leads").addKeyListener(new KeyListener(){
             public void keyReleased(KeyEvent e){
                 controlLeads.dataShow(((JTextField)e.getSource()).getText());
             }
             public void keyTyped(KeyEvent e){}
             public void keyPressed(KeyEvent e){}
          });


    }

    public void saveLeads(){
        if(!validateInput()){
            return;
        }

        int row = 0;
        if(controlLeads.isEdit){
            row = ebiModule.gui.getTable("leadsTable","Leads").getSelectedRow();
        }
        controlLeads.dataStore();
        ebiModule.gui.getTable("leadsTable","Leads").changeSelection(row,0,false,false);
    }

    private boolean validateInput(){
        if("".equals(ebiModule.gui.getTextfield("compNameText","Leads").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }else if("".equals(ebiModule.gui.getComboBox("genderText","Leads").getSelectedItem())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_SELECT_GENDER")).Show(EBIMessage.ERROR_MESSAGE); 
            return false;
        }

        return true;
    }

    public void ebiNew(){
        controlLeads.dataNew();
        ebiModule.gui.getTextfield("compNameText","Leads").requestFocus();
    }

    public void ebiSave(){
        saveLeads();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            if(controlLeads.dataDelete(Integer.parseInt(tabModel.data[selectedRow][11].toString())) == true){
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_DELETED")).Show(EBIMessage.INFO_MESSAGE);
            }
        }
    }

}
