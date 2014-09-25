package ebiCRM.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelProperties;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.component.TaskEvent;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmprojectcost;
import ebiNeutrinoSDK.model.hibernate.Crmprojectprop;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttask;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBINewProjectTaskDialog implements ChangeListener {

    private EBICRMModule ebiCRM = null;
    public  boolean saveTask = true;
    private JColorChooser jch = null;
    private MyTableModelProperties tabModCost = null;
    private MyTableModelProperties tabModProperties = null;
    private int selectedCost = -1;
    private int selectedProperties = -1;
    public static String[] taskStatus = null;
    public static String[] taskType = null;
    private Crmprojecttask projectTask = null;


    public EBINewProjectTaskDialog(EBICRMModule ebiCRM){
        this.ebiCRM = ebiCRM;
    }


    public void stateChanged(ChangeEvent changeEvent){
       ebiCRM.guiRenderer.getLabel("taskColor","projectTaskDialog").setBackground(jch.getColor());
    }


    public boolean setVisible(final TaskEvent te, final Crmprojecttask task){
        tabModProperties = new MyTableModelProperties();
        tabModCost = new MyTableModelProperties();
        projectTask = task;

        ebiCRM.guiRenderer.loadGUI("CRMDialog/newProjectTaskDialog.xml");
        ebiCRM.guiRenderer.getComboBox("taskStatusText","projectTaskDialog").setModel(new DefaultComboBoxModel(taskStatus));
        ebiCRM.guiRenderer.getComboBox("taskTypeText","projectTaskDialog").setModel(new DefaultComboBoxModel(taskType));
        ebiCRM.guiRenderer.getComboBox("taskDoneText","projectTaskDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

        for(int i=10; i<=100; i+=10){
           ebiCRM.guiRenderer.getComboBox("taskDoneText","projectTaskDialog").addItem(i+"%");
        }

        ebiCRM.guiRenderer.getComboBox("taskDoneText","projectTaskDialog").setSelectedIndex(te.getReached() / 10);
        ebiCRM.guiRenderer.getTextfield("taskNameText","projectTaskDialog").setText(te.getName());
        ebiCRM.guiRenderer.getEditor("taskDescription","projectTaskDialog").setText(te.getDescription());

        ebiCRM.guiRenderer.getTextfield("durationText","projectTaskDialog").setText(String.valueOf(te.getDuration()));
        ebiCRM.guiRenderer.getComboBox("taskStatusText","projectTaskDialog").setSelectedItem(te.getStatus());
        ebiCRM.guiRenderer.getComboBox("taskTypeText","projectTaskDialog").setSelectedItem(te.getType());

        ebiCRM.guiRenderer.getLabel("taskColor","projectTaskDialog").setBackground(te.getBackgroundColor());
        ebiCRM.guiRenderer.getLabel("taskColor","projectTaskDialog").setOpaque(true);

        ebiCRM.guiRenderer.getButton("chooseTaskColor","projectTaskDialog").addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
                EBIDialogExt diaColor = new EBIDialogExt(null);
                diaColor.setSize(600,400);
                diaColor.setModal(true);
                diaColor.setClosable(true);
                diaColor.setName("ProjectColorChooser");
                EBIVisualPanelTemplate pan = new EBIVisualPanelTemplate();
                pan.setEnableChangeComponent(false);
                pan.setModuleIcon(EBIConstant.ICON_APP);
                pan.setModuleTitle("Color Chooser");
                pan.setClosable(true);
                pan.getPanel().setLayout(new BorderLayout());
                jch = new JColorChooser();
                jch.getSelectionModel().addChangeListener(EBINewProjectTaskDialog.this);
                pan.add(jch,BorderLayout.CENTER);
                diaColor.setContentPane(pan);
                diaColor.setVisible(true);

            }
        });

        ebiCRM.guiRenderer.getButton("cancelTask","projectTaskDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent event){
                saveTask = false; 
                ebiCRM.guiRenderer.getEBIDialog("projectTaskDialog").setVisible(false);
             }
        });

        ebiCRM.guiRenderer.getButton("taskSave","projectTaskDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent event){
                if(!validateInput()){
                    return;
                }
                te.setName(ebiCRM.guiRenderer.getTextfield("taskNameText","projectTaskDialog").getText()); 
                te.setDescription("<html>"+ebiCRM.guiRenderer.getEditor("taskDescription","projectTaskDialog").getText()+"<html>");
                te.setBackgroundColor(ebiCRM.guiRenderer.getLabel("taskColor","projectTaskDialog").getBackground());
                
                te.setReached(ebiCRM.guiRenderer.getComboBox("taskDoneText","projectTaskDialog").getSelectedIndex() * 10);

                if(ebiCRM.guiRenderer.getComboBox("taskStatusText","projectTaskDialog").getSelectedItem() != null){
                    te.setStatus(ebiCRM.guiRenderer.getComboBox("taskStatusText","projectTaskDialog").getSelectedItem().toString() );
                }

                if(ebiCRM.guiRenderer.getComboBox("taskTypeText","projectTaskDialog").getSelectedItem() != null){
                    te.setType(ebiCRM.guiRenderer.getComboBox("taskTypeText","projectTaskDialog").getSelectedItem().toString());
                }

                try{
                    te.setDuration(Integer.parseInt(ebiCRM.guiRenderer.getTextfield("durationText","projectTaskDialog").getText()) * 20);
                }catch(NumberFormatException ex){
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_VALID_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
                }

                saveTask = true; 
                ebiCRM.guiRenderer.getEBIDialog("projectTaskDialog").setVisible(false);
             }
        });


        ebiCRM.guiRenderer.getButton("newCost","projectTaskDialog").setIcon(EBIConstant.ICON_NEW);
        ebiCRM.guiRenderer.getButton("newCost","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){
                   EBIDialogProperties properties = new EBIDialogProperties(ebiCRM.getProjectPane(), projectTask, null, true);
                   properties.setVisible();

               }
        });

        ebiCRM.guiRenderer.getButton("editCost","projectTaskDialog").setIcon(EBIConstant.ICON_EDIT);
        ebiCRM.guiRenderer.getButton("editCost","projectTaskDialog").setEnabled(false);
        ebiCRM.guiRenderer.getButton("editCost","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){

                   Iterator iter = projectTask.getCrmprojectcosts().iterator();
                   while(iter.hasNext()){
                      Crmprojectcost cost = (Crmprojectcost)iter.next();
                      if(cost.getCostid() == Integer.parseInt(tabModCost.data[selectedCost][2].toString())){
                         EBIDialogProperties properties = new EBIDialogProperties(ebiCRM.getProjectPane(), projectTask, cost, true);
                         properties.setVisible();
                         showCost();
                         break;
                      }
                   }
               }
        });

        ebiCRM.guiRenderer.getButton("deleteCost","projectTaskDialog").setIcon(EBIConstant.ICON_DELETE);
        ebiCRM.guiRenderer.getButton("deleteCost","projectTaskDialog").setEnabled(false);
        ebiCRM.guiRenderer.getButton("deleteCost","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){
                  Iterator iter = projectTask.getCrmprojectcosts().iterator();
                    while(iter.hasNext()){
                      Crmprojectcost cost = (Crmprojectcost)iter.next();
                      if(cost.getCostid() == Integer.parseInt(tabModCost.data[selectedCost][2].toString())){
                         projectTask.getCrmprojectcosts().remove(cost);
                         showCost();
                         break;
                      }
                   }
               }
        });


        ebiCRM.guiRenderer.getButton("newProperties","projectTaskDialog").setIcon(EBIConstant.ICON_NEW);
        ebiCRM.guiRenderer.getButton("newProperties","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){
                     EBIDialogProperties properties = new EBIDialogProperties(ebiCRM.getProjectPane(), projectTask, null, false);
                     properties.setVisible();
                     showProperties();
               }
        });
        
        ebiCRM.guiRenderer.getButton("editProperties","projectTaskDialog").setIcon(EBIConstant.ICON_EDIT);
        ebiCRM.guiRenderer.getButton("editProperties","projectTaskDialog").setEnabled(false);
        ebiCRM.guiRenderer.getButton("editProperties","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){

                 Iterator iter = projectTask.getCrmprojectprops().iterator();
                    while(iter.hasNext()){
                      Crmprojectprop prop = (Crmprojectprop)iter.next();
                      if(prop.getPropertiesid() == Integer.parseInt(tabModProperties.data[selectedProperties][2].toString())){
                         EBIDialogProperties properties = new EBIDialogProperties(ebiCRM.getProjectPane(), projectTask, prop, false);
                         properties.setVisible();
                         showProperties();
                         break;
                      }
                   }

               }
        });

        ebiCRM.guiRenderer.getButton("deleteProperties","projectTaskDialog").setIcon(EBIConstant.ICON_DELETE);
        ebiCRM.guiRenderer.getButton("deleteProperties","projectTaskDialog").setEnabled(false);
        ebiCRM.guiRenderer.getButton("deleteProperties","projectTaskDialog").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent event){
                    Iterator iter = projectTask.getCrmprojectprops().iterator();
                    while(iter.hasNext()){
                      Crmprojectprop prop = (Crmprojectprop)iter.next();
                      if(prop.getPropertiesid() == Integer.parseInt(tabModProperties.data[selectedProperties][2].toString())){
                         projectTask.getCrmprojectprops().remove(prop);
                         showProperties(); 
                         break;
                      }
                   }
               }
        });

        //Table cost
        ebiCRM.guiRenderer.getTable("tableCost","projectTaskDialog").setModel(tabModCost);
        TableColumn col7 = ebiCRM.guiRenderer.getTable("tableCost","projectTaskDialog").getColumnModel().getColumn(1);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        ebiCRM.guiRenderer.getTable("tableCost","projectTaskDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedCost = ebiCRM.guiRenderer.getTable("tableCost","projectTaskDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiCRM.guiRenderer.getButton("editCost","projectTaskDialog").setEnabled(false);
                        ebiCRM.guiRenderer.getButton("deleteCost","projectTaskDialog").setEnabled(false);
                    } else if (!tabModCost.data[selectedCost][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiCRM.guiRenderer.getButton("editCost","projectTaskDialog").setEnabled(true);
                        ebiCRM.guiRenderer.getButton("deleteCost","projectTaskDialog").setEnabled(true);
                    }
                }
            });


        ebiCRM.guiRenderer.getTable("taskPropertiesTable","projectTaskDialog").setModel(tabModProperties);
        ebiCRM.guiRenderer.getTable("taskPropertiesTable","projectTaskDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedProperties = ebiCRM.guiRenderer.getTable("taskPropertiesTable","projectTaskDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiCRM.guiRenderer.getButton("editProperties","projectTaskDialog").setEnabled(false);
                        ebiCRM.guiRenderer.getButton("deleteProperties","projectTaskDialog").setEnabled(false);
                    } else if (!tabModProperties.data[selectedProperties][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiCRM.guiRenderer.getButton("editProperties","projectTaskDialog").setEnabled(true);
                        ebiCRM.guiRenderer.getButton("deleteProperties","projectTaskDialog").setEnabled(true);
                    }
                }
            });

        showProperties();
        showCost();
        ebiCRM.guiRenderer.showGUI();

        return saveTask;
    }

    public void showProperties(){
       tabModProperties.data = new Object[projectTask.getCrmprojectprops().size()][3];

       Iterator iter = projectTask.getCrmprojectprops().iterator();
       int i=0;
       if(projectTask.getCrmprojectprops().size() >0){
            while(iter.hasNext()){
                Crmprojectprop prop = (Crmprojectprop) iter.next();
                tabModProperties.data[i][0] = prop.getName();
                tabModProperties.data[i][1] = prop.getValue();
                tabModProperties.data[i][2] = prop.getPropertiesid();
                i++;
            }
        }else{
           tabModProperties.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"",""}};
        }
        tabModProperties.fireTableDataChanged();
    }

    public void showCost(){
       tabModCost.data = new Object[projectTask.getCrmprojectcosts().size()][3];

       Iterator iter = projectTask.getCrmprojectcosts().iterator();
       int i=0;
       if(projectTask.getCrmprojectcosts().size() >0){

           NumberFormat currency=NumberFormat.getCurrencyInstance();
           
            while(iter.hasNext()){
                Crmprojectcost cost = (Crmprojectcost) iter.next();
                tabModCost.data[i][0] = cost.getName() == null ? "" : cost.getName();
                tabModCost.data[i][1] = currency.format(cost.getValue()) == null ? "" : currency.format(cost.getValue());
                tabModCost.data[i][2] = cost.getCostid();
                i++;
            }
        }else{
           tabModCost.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"",""}};
        }
        tabModCost.fireTableDataChanged();
    }


    private boolean validateInput(){

        if("".equals(ebiCRM.guiRenderer.getTextfield("taskNameText","projectTaskDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_TASK_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


}
