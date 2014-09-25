package ebiCRM.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Iterator;

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
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.data.control.EBIDataControlProject;
import ebiCRM.gui.dialogs.EBICRMHistoryView;
import ebiCRM.gui.dialogs.EBINewProjectTaskDialog;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIGRCManagement;
import ebiNeutrinoSDK.gui.component.EBIGRCManagementListener;
import ebiNeutrinoSDK.gui.component.EBIJTextFieldNumeric;
import ebiNeutrinoSDK.gui.component.TaskEvent;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmprojectcost;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttask;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBICRMProjectPane {

    public final EBICRMModule ebiModule;
    public EBIGRCManagement grcManagement = null;
    public EBIDataControlProject  dataControlProject = null;
    public boolean isEdit = false;
    private int selectedProjectRow = -1;
    public static String[] projectStatus = null;
    public EBINewProjectTaskDialog projTask = null;
    private EBIAbstractTableModel model = null;

    public EBICRMProjectPane(EBICRMModule ebiCRM){

        this.ebiModule = ebiCRM;
        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("EBIPROJECT_SESSION");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataControlProject = new EBIDataControlProject(this);
    }


    public void initialize(){
       NumberFormat valueFormat=NumberFormat.getNumberInstance();
       valueFormat.setMinimumFractionDigits(2);
       valueFormat.setMaximumFractionDigits(2);

       ebiModule.guiRenderer.getLabel("filterTable","Project").setHorizontalAlignment(SwingUtilities.RIGHT); 
       ebiModule.guiRenderer.getTextfield("filterTableText","Project").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("projectTable","Project").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Project").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("projectTable","Project").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Project").getText()));
            }
        });

       ebiModule.guiRenderer.getVisualPanel("Project").setCreatedFrom(EBIPGFactory.ebiUser);
       ebiModule.guiRenderer.getVisualPanel("Project").setCreatedDate(ebiModule.ebiPGFactory.getDateToString(new Date()));

       ebiModule.guiRenderer.getVisualPanel("Project").setChangedFrom("");
       ebiModule.guiRenderer.getVisualPanel("Project").setChangedDate("");

       ebiModule.guiRenderer.getComboBox("prjStatusText","Project").setModel(new DefaultComboBoxModel(projectStatus)); 

       ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(valueFormat)));
       ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.FLOAT));
       ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setColumns(10);

       ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setEditable(false);
       ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(valueFormat)));
       ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.FLOAT));
       ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setColumns(10);

       /*ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setEditable(false);
       ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(valueFormat)));
       ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.FLOAT));
       ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setColumns(10);*/ 


       //Init project listener control
       projTask  = new EBINewProjectTaskDialog(ebiModule);
       grcManagement = new EBIGRCManagement();
       grcManagement.addEventListener(new EBIGRCManagementListener(){
           public boolean addNewTaskAction(TaskEvent event){

               boolean saveTask = false;
               final Crmprojecttask taskBean = new Crmprojecttask();

               try{
                    if(dataControlProject.checkIslocked(dataControlProject.id,true)){
                        return false;
                    }
               }catch(Exception ex){
                    ex.printStackTrace();
               }

               if(projTask.setVisible(event,taskBean)){
                  taskBean.setTaskiid((dataControlProject.getProjectBean().getCrmprojecttasks().size()+1)*(-1));
                  taskBean.setTaskid(""+event.getId());
                  taskBean.setX(event.getX());
                  taskBean.setY(event.getY());
                  taskBean.setName(event.getName());
                  taskBean.setDone(event.getReached());
                  taskBean.setColor("#"+Integer.toHexString((event.getBackgroundColor().getRGB() & 0x00ffffff )));
                  taskBean.setDuration(event.getDuration());
                  taskBean.setStatus(event.getStatus());
                  taskBean.setType(event.getType());
                  taskBean.setDescription(event.getDescription());
                  dataControlProject.getProjectBean().getCrmprojecttasks().add(taskBean);  
                  if(!taskBean.getCrmprojectcosts().isEmpty()){
                      calculateActualCost();
                  }

                  saveTask = true;
               }

               return saveTask;
           }

           public boolean editTaskAction(TaskEvent event, boolean isFullEdit){

               boolean saveTask = false;

               try{
                    if(dataControlProject.checkIslocked(dataControlProject.id,true)){
                        return false;
                    }
               }catch(Exception ex){
                    ex.printStackTrace();
               }

               Iterator iter  = dataControlProject.getProjectBean().getCrmprojecttasks().iterator();
               Crmprojecttask taskBean = null;
               while(iter.hasNext()){
                 taskBean = (Crmprojecttask)iter.next();
                 if(Long.parseLong(taskBean.getTaskid()) == event.getId()){
                     break;
                 }
               }

               if(isFullEdit){
                   if(projTask.setVisible(event,taskBean)){
                     if(taskBean != null){
                       taskBean.setName(event.getName());
                       taskBean.setX(event.getX());
                       taskBean.setY(event.getY());
                       taskBean.setDone(event.getReached());
                       taskBean.setColor("#"+Integer.toHexString((event.getBackgroundColor().getRGB() & 0x00ffffff )));
                       taskBean.setDuration(event.getDuration());
                       taskBean.setStatus(event.getStatus());
                       taskBean.setType(event.getType());
                       taskBean.setDescription(event.getDescription());
                     }
                     if(taskBean != null){
                      if(!taskBean.getCrmprojectcosts().isEmpty()){
                        calculateActualCost();
                      }
                     }
                     saveTask = true;
                   }
               }else{
                 if(taskBean != null && event != null){
                  taskBean.setDuration(event.getDuration());
                  taskBean.setX(event.getX());
                  taskBean.setY(event.getY());
                 }
                  saveTask = true;
               }
               
               return saveTask;
           }

           public boolean deleteTaskAction(TaskEvent event){

                 boolean isDelete = false;
                 try{
                        if(dataControlProject.checkIslocked(dataControlProject.id,true)){
                            return false;
                        }
                 }catch(Exception ex){
                        ex.printStackTrace();
                 }

                 if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_DELETE_TASK")).Show(EBIMessage.WARNING_MESSAGE_YESNO)){
                   Iterator iter  = dataControlProject.getProjectBean().getCrmprojecttasks().iterator();

                   Crmprojecttask taskBean;

                   while(iter.hasNext()){
                     taskBean = (Crmprojecttask)iter.next();
                     if(Long.parseLong(taskBean.getTaskid()) == event.getId()){
                         try {
                             ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").delete(taskBean);
                             ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").refresh(dataControlProject.getProjectBean());
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                         dataControlProject.getProjectBean().getCrmprojecttasks().remove(taskBean);
                         if(!taskBean.getCrmprojectcosts().isEmpty()){
                            calculateActualCost();
                         }
                         break;
                     }
                   }
                   isDelete = true;
                 }

              return isDelete;
           }

           public boolean createRelation(long fromId, long toId){
               boolean saveRelation = false;
               try{
                    if(dataControlProject.checkIslocked(dataControlProject.id,true)){
                        return false;
                    }
               }catch(Exception ex){
                    ex.printStackTrace();
               }

               Iterator iter  = dataControlProject.getProjectBean().getCrmprojecttasks().iterator();
               Crmprojecttask taskBean = null;
               while(iter.hasNext()){
                 taskBean = (Crmprojecttask)iter.next();
                 if(Long.parseLong(taskBean.getTaskid()) == toId){
                     break;
                 }
               }

               if(taskBean != null){

                  taskBean.setParentstaskid(";"+fromId);
                  saveRelation = true;
               }
               
               return saveRelation;
           }

       });

       ebiModule.guiRenderer.getPanel("taskGraph","Project").setLayout(new BorderLayout());
       ebiModule.guiRenderer.getPanel("taskGraph","Project").add(grcManagement.getScrollComponent(),BorderLayout.CENTER);
    }

    public void initializeAction(){
      ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent actionEvent){

                   if(ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate() != null){
                       if(ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate().getTime() >
                                                                ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate().getTime()){

                         grcManagement.setStartEnd(ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate(),
                                                            ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate());
                       }else{
                           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_MESSAGE_STARTDATE_SMALL_ENDDATE")).Show(EBIMessage.ERROR_MESSAGE);
                       }
                   }

               }
        });

      ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").addActionListener(new ActionListener(){
               public void actionPerformed(ActionEvent actionEvent){
                   if(ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate() != null){
                       if(ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate().getTime() >
                                                                ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate().getTime()){

                         grcManagement.setStartEnd(ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate(),
                                                            ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate());
                       }else{
                           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_MESSAGE_STARTDATE_SMALL_ENDDATE")).Show(EBIMessage.ERROR_MESSAGE);
                       }
                   }
               }
        });

      ebiModule.guiRenderer.getButton("saveProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
              ebiSave();
          }
      });

      ebiModule.guiRenderer.getButton("newProject","Project").setIcon(EBIConstant.ICON_NEW);
      ebiModule.guiRenderer.getButton("newProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
                 ebiNew();
          }
      });
      
      ebiModule.guiRenderer.getButton("copyProject","Project").setIcon(EBIConstant.ICON_COPY);
      ebiModule.guiRenderer.getButton("copyProject","Project").setEnabled(false);  
      ebiModule.guiRenderer.getButton("copyProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
             model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
             if (selectedProjectRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
            		 									equals(model.data[selectedProjectRow][0].toString())) {
                 return;
             }

             dataControlProject.dataCopy(Integer.parseInt(model.data[selectedProjectRow][9].toString()));

          }

      });
      

      ebiModule.guiRenderer.getButton("editProject","Project").setIcon(EBIConstant.ICON_EDIT);
      ebiModule.guiRenderer.getButton("editProject","Project").setEnabled(false);  
      ebiModule.guiRenderer.getButton("editProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
             model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
             if (selectedProjectRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedProjectRow][0].toString())) {
                 return;
             }

             dataControlProject.dataEdit(Integer.parseInt(model.data[selectedProjectRow][9].toString()));
             calculateActualCost();

          }

      });

      ebiModule.guiRenderer.getButton("deleteProject","Project").setIcon(EBIConstant.ICON_DELETE);
      ebiModule.guiRenderer.getButton("deleteProject","Project").setEnabled(false);
      ebiModule.guiRenderer.getButton("deleteProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
             model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
             if (selectedProjectRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedProjectRow][0].toString())) {
                 return;
             }
              
            ebiDelete();
          }

      });


      ebiModule.guiRenderer.getButton("reportProject","Project").setIcon(EBIConstant.ICON_REPORT);
      ebiModule.guiRenderer.getButton("reportProject","Project").setEnabled(false);
      ebiModule.guiRenderer.getButton("reportProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
                boolean pass  ;
                if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanPrint() ||
                        ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                    pass = true;
                } else {
                    pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
                }
                if (pass) {
                    model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
                    dataControlProject.dataShowReport(Integer.parseInt(model.data[selectedProjectRow][9].toString()),model.data[selectedProjectRow][1].toString());
                }

          }

      });


      ebiModule.guiRenderer.getButton("historyProject","Project").setIcon(EBIConstant.ICON_HISTORY);
      ebiModule.guiRenderer.getButton("historyProject","Project").setEnabled(false);
      ebiModule.guiRenderer.getButton("historyProject","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
             model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
             new EBICRMHistoryView(ebiModule.hcreator.retrieveDBHistory(Integer.parseInt(model.data[selectedProjectRow][9].toString()), "Project"), ebiModule).setVisible();
          }

      });

      ebiModule.guiRenderer.getButton("createProduct","Project").setIcon(EBIConstant.ICON_PRODUCT);
      ebiModule.guiRenderer.getButton("createProduct","Project").setEnabled(false);
      ebiModule.guiRenderer.getButton("createProduct","Project").addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent event){
             model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
             ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Product");
             dataControlProject.createProduct(Integer.parseInt(model.data[selectedProjectRow][9].toString()));
          }

      });

      /*************************************************************/
      // Available Table
      /*************************************************************/
       model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
       TableColumn col7 = ebiModule.guiRenderer.getTable("projectTable","Project").getColumnModel().getColumn(7);
        col7.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

        TableColumn col6 = ebiModule.guiRenderer.getTable("projectTable","Project").getColumnModel().getColumn(6);
        col6.setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
                JLabel myself = (JLabel)super.getTableCellRendererComponent(table,value, isSelected, hasFocus,row, column);
                myself.setHorizontalAlignment(SwingConstants.RIGHT);
                myself.setForeground(new Color(255,60,60));
                return myself;
            }
        });

       ebiModule.guiRenderer.getTable("projectTable","Project").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
       ebiModule.guiRenderer.getTable("projectTable","Project").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                     try{
                        selectedProjectRow = ebiModule.guiRenderer.getTable("projectTable","Project").convertRowIndexToModel(lsm.getMinSelectionIndex());
                     }catch(IndexOutOfBoundsException ex){}
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editProject","Project").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteProject","Project").setEnabled(false);
                        ebiModule.guiRenderer.getButton("historyProject","Project").setEnabled(false);
                        ebiModule.guiRenderer.getButton("reportProject","Project").setEnabled(false);
                        ebiModule.guiRenderer.getButton("createProduct","Project").setEnabled(false);
                        ebiModule.guiRenderer.getButton("copyProject","Project").setEnabled(false);
                    } else if (!model.data[selectedProjectRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editProject","Project").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteProject","Project").setEnabled(true);
                        ebiModule.guiRenderer.getButton("historyProject","Project").setEnabled(true);
                        ebiModule.guiRenderer.getButton("reportProject","Project").setEnabled(true);
                        ebiModule.guiRenderer.getButton("createProduct","Project").setEnabled(true);
                        ebiModule.guiRenderer.getButton("copyProject","Project").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("projectTable","Project")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedProjectRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedProjectRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedProjectRow = selRow;


                    if (selectedProjectRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(model.data[selectedProjectRow][0].toString())) {
                        return;
                    }

                    dataControlProject.dataEdit(Integer.parseInt(model.data[selectedProjectRow][9].toString()));
                    ebiModule.guiRenderer.getTextfield("prjNrText","Project").grabFocus();
                    calculateActualCost(); 
                }
            });

            ebiModule.guiRenderer.getTable("projectTable","Project").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(ebiModule.guiRenderer.getTable("projectTable","Project").rowAtPoint(e.getPoint()) != -1){
                        selectedProjectRow = ebiModule.guiRenderer.getTable("projectTable","Project").convertRowIndexToModel(ebiModule.guiRenderer.getTable("projectTable","Project").rowAtPoint(e.getPoint()));
                    }

                    if (e.getClickCount() == 2) {

                        if (selectedProjectRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(model.data[selectedProjectRow][0].toString())) {
                            return;
                        }

                        dataControlProject.dataEdit(Integer.parseInt(model.data[selectedProjectRow][9].toString()));
                        ebiModule.guiRenderer.getTextfield("prjNrText","Project").grabFocus();
                        calculateActualCost();

                    }
                }
            });
    }


    public void saveProject(){
	      final Runnable run = new Runnable(){	
		       public void run(){ 
			    	if(!validateInput()){
			                  return;
			        }
			    	int row = 0;
			    	if(isEdit){
			    		row = ebiModule.guiRenderer.getTable("projectTable","Project").getSelectedRow();
			    	}
			        dataControlProject.dataStore(isEdit);
			        try {
						Thread.sleep(100);
					} catch (InterruptedException e) {}
					ebiModule.guiRenderer.getTable("projectTable","Project").changeSelection(row,0,false,false);
		       }
	      };
	      
	      Thread save = new Thread(run,"Save Poject");
	      save.start();
    }

    public boolean validateInput(){

        if("".equals(ebiModule.guiRenderer.getTextfield("prjNrText","Project").getText())){
           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROJ_NR")).Show(EBIMessage.ERROR_MESSAGE);
           return false;
        }

        if("".equals(ebiModule.guiRenderer.getTextfield("prjNameText","Project").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_PROJ_NAME")).Show(EBIMessage.ERROR_MESSAGE); 
           return false;
        }

        return true;
    }

    private void calculateActualCost(){
      if(ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getValue() != null){
            Iterator iter = dataControlProject.getProjectBean().getCrmprojecttasks().iterator();
            Double value=0.0;
            while(iter.hasNext()){
                Crmprojecttask task = (Crmprojecttask)iter.next();

                Iterator itc = task.getCrmprojectcosts().iterator();
                while(itc.hasNext()) {
                    Crmprojectcost cost = (Crmprojectcost)itc.next();
                    value += cost.getValue();
                }

            }
            if(value > Double.parseDouble(ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getValue().toString())){
              ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setForeground(new Color(255,0,0));
            }
            ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setValue(value);
      }
    }

    public void ebiNew(){
        dataControlProject.dataNew(true);
    }

    public void ebiSave(){
        saveProject();
    }

    public void ebiDelete(){
        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {
            boolean pass;
            if (ebiModule.ebiPGFactory.getIEBISystemUserRights().isCanDelete() ||
                    ebiModule.ebiPGFactory.getIEBISystemUserRights().isAdministrator()) {
                pass = true;
            } else {
                pass = ebiModule.ebiPGFactory.getIEBISecurityInstance().secureModule();
            }
            if (pass) {
                dataControlProject.dataDelete(Integer.parseInt(model.data[selectedProjectRow][9].toString()));
            }
        }
    }

}
