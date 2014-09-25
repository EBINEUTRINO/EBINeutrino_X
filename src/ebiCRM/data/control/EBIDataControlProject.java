package ebiCRM.data.control;

import java.awt.Color;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMProjectPane;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.TaskEvent;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crmproject;
import ebiNeutrinoSDK.model.hibernate.Crmprojectcost;
import ebiNeutrinoSDK.model.hibernate.Crmprojectprop;
import ebiNeutrinoSDK.model.hibernate.Crmprojecttask;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;


public class EBIDataControlProject {

    private EBICRMProjectPane projectPane = null;
    private Crmproject project = null;
    public int id = -1;
    public Timestamp lockTime = null;
    public String lockUser ="";
    public String lockModuleName = "";
    public int lockStatus=0;
    public int lockId =-1;


    public EBIDataControlProject(EBICRMProjectPane pane){
       this.projectPane = pane;
       this.project = new Crmproject();
       dataShow(); 
    }


    public boolean dataStore(boolean isEdit){
        try {
            projectPane.ebiModule.ebiContainer.showInActionStatus("Project", true);
            if(id != -1){
                if(checkIslocked(id,true)){
                 return false;
                }
            }

            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").begin();

            if(!isEdit){
               project.setCreateddate(new Date());
               projectPane.isEdit = true;
            }else{
               createHistory(project.getProjectid()); 
               project.setChangedfrom(EBIPGFactory.ebiUser);
               project.setChangeddate(new Date());
            }

            project.setCreatedfrom(projectPane.ebiModule.guiRenderer.getVisualPanel("Project").getCreatedFrom());

            project.setProjectnr(projectPane.ebiModule.guiRenderer.getTextfield("prjNrText","Project").getText());
            project.setName(projectPane.ebiModule.guiRenderer.getTextfield("prjNameText","Project").getText());
            project.setManager(projectPane.ebiModule.guiRenderer.getTextfield("prjManagerText","Project").getText());
            if(projectPane.ebiModule.guiRenderer.getComboBox("prjStatusText","Project").getSelectedItem() != null){
                project.setStatus(projectPane.ebiModule.guiRenderer.getComboBox("prjStatusText","Project").getSelectedItem().toString());
            }
            if(projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate() != null){
                project.setValidfrom(projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getDate());
            }
            if(projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate() != null){
                project.setValidto(projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getDate());
            }

            if(projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getValue() != null){
                if(!"".equals(projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getText())){
                    project.setBudget(Double.parseDouble(projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getValue().toString()));
                }
            }

            if(projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").getValue() != null){
                if(!"".equals(projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").getText())){
                    project.setActualcost(Double.parseDouble(projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").getValue().toString()));
                }
            }
            
            /*if(!"".equals(projectPane.ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").getText())){
                            project.setRemaincost(Double.parseDouble(projectPane.ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").getValue().toString()));
                    } */

           
            if(!project.getCrmprojecttasks().isEmpty()){
                Iterator iter = project.getCrmprojecttasks().iterator();
                while(iter.hasNext()){
                    Crmprojecttask task = (Crmprojecttask)iter.next();
                    if(task.getTaskiid() < 0){
                        task.setTaskiid(null);
                    }
                    task.setCrmproject(project);

                    projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(task);

                    if(!task.getCrmprojectcosts().isEmpty()){
                        Iterator citer = task.getCrmprojectcosts().iterator();
                        while(citer.hasNext()){
                            Crmprojectcost cost = (Crmprojectcost)citer.next();
                            if(cost.getCostid() < 0){
                               cost.setCostid(null);
                            }
                            cost.setCrmprojecttask(task);
                            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(cost);
                        }
                    }
                    
                    if(!task.getCrmprojectprops().isEmpty()){
                        Iterator piter = task.getCrmprojectprops().iterator();
                        while(piter.hasNext()){
                            Crmprojectprop prop = (Crmprojectprop)piter.next();
                            if(prop.getPropertiesid() < 0){
                                prop.setPropertiesid(null);
                            }
                            prop.setCrmprojecttask(task);
                            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(prop);
                        }
                    }

                }
            }

            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(project);
            projectPane.ebiModule.ebiPGFactory.getDataStore("Project","ebiSave",true);

            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").commit();

            if(!isEdit){
            	projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setID(project.getProjectid());
            }
            
            dataShow();
            projectPane.ebiModule.ebiContainer.showInActionStatus("Project", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public void dataCopy(int id){
    	Query query;
        try {

            query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "from Crmproject where projectid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();
            if(iter.hasNext()){

            	 Crmproject pro = (Crmproject)iter.next();
            	 projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").refresh(pro);
            	 projectPane.ebiModule.ebiContainer.showInActionStatus("Project", true);
            	 projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").begin();
            	   
            	   Crmproject pnew = new Crmproject();
            	   pnew.setCreateddate(new Date());
            	   pnew.setCreatedfrom(EBIPGFactory.ebiUser);

            	   pnew.setProjectnr(pro.getProjectnr());
            	   pnew.setName(pro.getName()+" - (Copy)");
            	   pnew.setManager(pro.getManager());
                 
            	   pnew.setStatus(pro.getStatus());
                   
            	   pnew.setValidfrom(pro.getValidfrom());
            	   pnew.setValidto(pro.getValidto());
            	   pnew.setBudget(pro.getBudget());
            	   pnew.setActualcost(pro.getActualcost());

                   if(!pro.getCrmprojecttasks().isEmpty()){
                       Iterator itt = pro.getCrmprojecttasks().iterator();
                       while(itt.hasNext()){
                           Crmprojecttask task = (Crmprojecttask)itt.next();
                            
                           Crmprojecttask ntask = new Crmprojecttask();
                           ntask.setCrmproject(pnew);
                           ntask.setDescription(task.getDescription());
                           ntask.setDone(task.getDone());
                           ntask.setDuration(task.getDuration());
                           ntask.setName(task.getName());
                           ntask.setColor(task.getColor());
                           ntask.setParentstaskid(task.getParentstaskid());
                           ntask.setStatus(task.getStatus());
                           ntask.setTaskid(task.getTaskid());
                           ntask.setType(task.getType());
                           ntask.setX(task.getX());
                           ntask.setY(task.getY());

                           projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(ntask);

                           if(!task.getCrmprojectcosts().isEmpty()){
                               Iterator citer = task.getCrmprojectcosts().iterator();
                               while(citer.hasNext()){
                                   Crmprojectcost cost = (Crmprojectcost)citer.next();
                                    
                                   Crmprojectcost nc = new Crmprojectcost();
                                   nc.setCrmprojecttask(ntask);
                                   nc.setCreateddate(new Date());
                                   nc.setCreatedfrom(EBIPGFactory.ebiUser);
                                   nc.setName(cost.getName());
                                   nc.setValue(cost.getValue());
                                   
                                   projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(nc);
                               }
                           }
                           
                           if(!task.getCrmprojectprops().isEmpty()){
                               Iterator piter = task.getCrmprojectprops().iterator();
                               while(piter.hasNext()){
                                   Crmprojectprop prop = (Crmprojectprop)piter.next();
                                   
                                   Crmprojectprop np = new Crmprojectprop();
                                   np.setCreateddate(new Date());
                                   np.setCreatedfrom(EBIPGFactory.ebiUser);
                                   np.setCrmprojecttask(ntask);
                                   np.setName(prop.getName());
                                   np.setValue(prop.getValue());
                                  
                                   projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(np);
                               }
                           }
                       }
                   }

                   projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").saveOrUpdate(pnew);
                   projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").commit();
                   projectPane.ebiModule.ebiContainer.showInActionStatus("Project", false);
                   dataShow();
                   
                   projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").
                   changeSelection(projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").
       					convertRowIndexToView(projectPane.ebiModule.dynMethod.
       							getIdIndexFormArrayInATable(((EBIAbstractTableModel)projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").getModel()).data, 9, pnew.getProjectid())),0,false,false);

            }
            
       
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
    }

    public void dataEdit(int id){
        dataNew(false);

        Query query;
        try {

            query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "from Crmproject where projectid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();
            if(iter.hasNext()){
                this.id = id;
                
                // Set properties for pessimistic dialog
                projectPane.ebiModule.pessimisticStruct.setLockId(id);
                projectPane.ebiModule.pessimisticStruct.setModuleName("CRMProduct");

                project = (Crmproject)iter.next();
                projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").refresh(project);
                projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setID(project.getProjectid());

                projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setCreatedDate(projectPane.ebiModule.ebiPGFactory.getDateToString(project.getCreateddate() == null ? new Date() : project.getCreateddate()));
                projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setCreatedFrom(project.getCreatedfrom() == null ? EBIPGFactory.ebiUser : project.getCreatedfrom());

                if(project.getChangeddate() != null){
                    projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setChangedDate(projectPane.ebiModule.ebiPGFactory.getDateToString(project.getChangeddate()));
                    projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setChangedFrom(project.getChangedfrom());
                }

                projectPane.ebiModule.guiRenderer.getTextfield("prjNrText","Project").setText(project.getProjectnr());
                projectPane.ebiModule.guiRenderer.getTextfield("prjNameText","Project").setText(project.getName());
                projectPane.ebiModule.guiRenderer.getTextfield("prjManagerText","Project").setText(project.getManager());

                if(project.getStatus() != null){
                    projectPane.ebiModule.guiRenderer.getComboBox("prjStatusText","Project").setSelectedItem(project.getStatus());
                }

                if(project.getValidfrom() != null){
                    projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getEditor().setText(projectPane.ebiModule.ebiPGFactory.getDateToString(project.getValidfrom()));
                }

                if(project.getValidto() != null){
                    projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getEditor().setText(projectPane.ebiModule.ebiPGFactory.getDateToString(project.getValidto()));
                }

                if(project.getValidfrom() != null){
                    projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").setDate(project.getValidfrom());
                }
                if(project.getValidto() != null){
                    projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").setDate(project.getValidto());
                }

                if(project.getValidfrom() != null && project.getValidto() != null){
                    projectPane.grcManagement.setStartEnd(project.getValidfrom(), project.getValidto());
                }
                if(project.getBudget() != null){
                    projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setValue(project.getBudget());
                }
                if(project.getActualcost() != null){
                    projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setValue(project.getActualcost());
                }
                /*if(project.getRemaincost() != null){

                                projectPane.ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setValue(project.getRemaincost());
                             }*/

               if(project.getCrmprojecttasks().size() > 0){
                Iterator itk = project.getCrmprojecttasks().iterator();
                HashMap<Long,TaskEvent> mp = new HashMap<Long,TaskEvent>();
                HashMap<Long,Long>relation = new HashMap<Long,Long>();
                while(itk.hasNext()){
                   Crmprojecttask ctask = (Crmprojecttask)itk.next();
                   TaskEvent task = new TaskEvent(projectPane.grcManagement);
                   if(ctask.getParentstaskid() != null){ 
                    relation.put(Long.parseLong(ctask.getTaskid()),Long.parseLong(ctask.getParentstaskid().split(";")[1]));
                   }
                   task.setId(Long.parseLong(ctask.getTaskid()));
                   task.setName(ctask.getName());
                   task.setLocation(ctask.getX(),ctask.getY());
                   task.setDuration(ctask.getDuration()*20);
                   task.setDescription(ctask.getDescription());
                   task.setReached(ctask.getDone());
                   task.setStatus(ctask.getStatus() == null ? "" : ctask.getStatus());
                   task.setType(ctask.getType() == null ? "" : ctask.getType());
                   if(ctask.getColor() != null){
                	   task.setBackgroundColor(Color.decode(ctask.getColor().toUpperCase()));
                   }
                   mp.put(Long.parseLong(ctask.getTaskid()),task);
                }
                Iterator rel = relation.keySet().iterator();
                while(rel.hasNext()){
                   Long idr =(Long)rel.next();
                   if(mp.get(relation.get(idr)) != null){
                       mp.get(relation.get(idr)).setParent(mp.get(idr));
                   }
                }
                projectPane.grcManagement.setAvailableTask(mp);
                projectPane.grcManagement.showTasks();
               }
               projectPane.isEdit = true;

               projectPane.ebiModule.ebiPGFactory.getDataStore("Project","ebiEdit",true);
                
               checkIslocked(id,false);
               
               projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").
				changeSelection(projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").
						convertRowIndexToView(projectPane.ebiModule.dynMethod.
								getIdIndexFormArrayInATable(((EBIAbstractTableModel)projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").getModel()).data, 9, id)),0,false,false);

            }else{
               EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dataDelete(int id){
        Query query;
        try {

            if(checkIslocked(id,true)){
                 return;
            }

            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").begin();
            query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "delete from Crmproject where projectid=? ").setInteger(0, id);
            query.executeUpdate();

            projectPane.ebiModule.ebiPGFactory.getDataStore("Project","ebiDelete",true);
            projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBIPROJECT_SESSION").commit();
            dataNew(true);
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataShow(){
        
        Query query;
        int srow = projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").getSelectedRow();
        
        try {

          query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "from Crmproject order by createddate desc ");

          Iterator iter = query.iterate();
          EBIAbstractTableModel tabMod = (EBIAbstractTableModel)projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").getModel();
          if(query.list().size() > 0){
              tabMod.data = new Object[query.list().size()][10];
              int i=0;
              
              NumberFormat currency=NumberFormat.getCurrencyInstance();

              while(iter.hasNext()){
                  Crmproject pro = (Crmproject)iter.next();
                  projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").refresh(pro);
                  tabMod.data[i][0] = pro.getProjectnr() == null ? "" : pro.getProjectnr();
                  tabMod.data[i][1] = pro.getName() == null ? "" : pro.getName();
                  tabMod.data[i][2] = pro.getManager() == null ? "" : pro.getManager();
                  tabMod.data[i][3] = pro.getValidfrom() == null ? "" : projectPane.ebiModule.ebiPGFactory.getDateToString(pro.getValidfrom());
                  tabMod.data[i][4] = pro.getValidto() == null ? "" : projectPane.ebiModule.ebiPGFactory.getDateToString(pro.getValidto());
                  tabMod.data[i][5] = pro.getStatus() == null ? "" : pro.getStatus();
                  tabMod.data[i][6] = pro.getBudget() == null ? 0.0 : currency.format(pro.getBudget());
                  tabMod.data[i][7] = pro.getActualcost() == null ? 0.0 : currency.format(pro.getActualcost());
                  tabMod.data[i][8] = "";
                  tabMod.data[i][9] = pro.getProjectid();
                  i++;
              }
          }else{
              tabMod.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","","","","",""}};
          }
          tabMod.fireTableDataChanged();
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        projectPane.ebiModule.guiRenderer.getTable("projectTable","Project").changeSelection(srow,0,false,false);
    }


    public void dataShowReport(int id,String name){
       try{
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ID", id);

        projectPane.ebiModule.ebiPGFactory.getIEBIReportSystemInstance().
                useReportSystem(map,
                projectPane.ebiModule.ebiPGFactory.convertReportCategoryToIndex(EBIPGFactory.getLANG("EBI_LANG_PROJECT")),
                name);
       }catch (Exception ex){}
    }


    public void dataNew(boolean reload){
       try{
        // Remove lock
        projectPane.ebiModule.unlockCompanyRecord(id,lockUser,"CRMProject");
        lockId = -1;
        lockModuleName = "";
        lockUser = "";
        lockStatus = 0;
        lockTime =  null;
        id=-1;

        projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setCreatedFrom(EBIPGFactory.ebiUser);
        projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setCreatedDate(projectPane.ebiModule.ebiPGFactory.getDateToString(new Date()));
        projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setChangedFrom("");
        projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setChangedDate("");

        projectPane.ebiModule.guiRenderer.getTextfield("prjNrText","Project").setText("");
        projectPane.ebiModule.guiRenderer.getTextfield("prjNameText","Project").setText("");
        projectPane.ebiModule.guiRenderer.getTextfield("prjManagerText","Project").setText("");

        projectPane.ebiModule.guiRenderer.getComboBox("prjStatusText","Project").setSelectedIndex(0);
        projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getEditor().setText("");
        projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getEditor().setText("");
        projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setText("");
        projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setText("");
        //projectPane.ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setText("");
        projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").setValue(null);
        projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").setValue(null);
        //projectPane.ebiModule.guiRenderer.getFormattedTextfield("remainingCost","Project").setValue(null);
        projectPane.grcManagement.resetComponent();
        projectPane.isEdit = false;
        projectPane.ebiModule.guiRenderer.getVisualPanel("Project").setID(-1);
        projectPane.ebiModule.ebiPGFactory.getDataStore("Project","ebiNew",true);

        if(reload){
            project = new Crmproject();
            dataShow();
        }
       }catch (Exception ex){}
    }

    public void createHistory(int id){
        Query query;
        try {

            query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "from Crmproject where projectid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();
            if(iter.hasNext()){
               Crmproject proj = (Crmproject)iter.next();
               java.util.List<String> list = new ArrayList<String>();

               list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED") + ": " + projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getCreateddate()));
               list.add(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") + ": " + proj.getCreatedfrom());

               if (proj.getChangeddate() != null) {
                   list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED") + ": " + projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getChangeddate()));
                   list.add(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") + ": " + proj.getChangedfrom());
               }

               list.add(EBIPGFactory.getLANG("EBI_LANG_PROJECT_NR")+": "+(proj.getProjectnr().equals(projectPane.ebiModule.guiRenderer.getTextfield("prjNrText","Project").getText()) ? proj.getProjectnr() : proj.getProjectnr()+"$" ));
               list.add(EBIPGFactory.getLANG("EBI_LANG_NAME")+": "+ (proj.getName().equals(projectPane.ebiModule.guiRenderer.getTextfield("prjNameText","Project").getText()) ? proj.getName() : proj.getName()+"$"));

               if(proj.getActualcost() != null){
                list.add(EBIPGFactory.getLANG("EBI_LANG_ACTUAL_COST")+": "+(proj.getActualcost() == Double.parseDouble(projectPane.ebiModule.guiRenderer.getFormattedTextfield("actualCostText","Project").getValue().toString()) ? proj.getActualcost() : proj.getActualcost()+"$"));
               }
               if(proj.getManager() != null){
                list.add(EBIPGFactory.getLANG("EBI_LANG_PROJECT_MANAGER")+": "+(proj.getManager().equals(projectPane.ebiModule.guiRenderer.getTextfield("prjManagerText","Project").getText()) ? proj.getManager() : proj.getManager()+"$"));
               }
               if(proj.getValidfrom() != null){
                list.add(EBIPGFactory.getLANG("EBI_LANG_START_DATE")+": "+(projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidfrom()).equals(projectPane.ebiModule.guiRenderer.getTimepicker("prjstartDateText","Project").getEditor().getText()) ?  projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidfrom()) : projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidfrom())+"$"));
               }
               if(proj.getValidto() != null){
                list.add(EBIPGFactory.getLANG("EBI_LANG_END_DATE")+": "+( projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidto()).equals(projectPane.ebiModule.guiRenderer.getTimepicker("prjendDateText","Project").getEditor().getText()) ? projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidto()) : projectPane.ebiModule.ebiPGFactory.getDateToString(proj.getValidto())+"$"));
               }

               list.add(EBIPGFactory.getLANG("EBI_LANG_STATUS")+": "+(proj.getStatus().equals(projectPane.ebiModule.guiRenderer.getComboBox("prjStatusText","Project").getSelectedItem()) ? proj.getStatus() : proj.getStatus()+"$" ));

               if(proj.getBudget() != null){
                list.add(EBIPGFactory.getLANG("EBI_LANG_PROJECT_BUDGET")+": "+(proj.getBudget() == Double.parseDouble(projectPane.ebiModule.guiRenderer.getFormattedTextfield("budgetText","Project").getValue().toString()) ? proj.getBudget() : proj.getBudget()+"$"));
               }
                
               list.add("*EOR*");

               list.add(EBIPGFactory.getLANG("EBI_LANG_TASK"));

               Iterator itr = proj.getCrmprojecttasks().iterator();
               while(itr.hasNext()) {
                  Crmprojecttask task = (Crmprojecttask)itr.next();
                  
                  list.add(EBIPGFactory.getLANG("EBI_LANG_NAME")+":"+task.getName());
                  list.add(EBIPGFactory.getLANG("EBI_LANG_STATUS")+":"+task.getStatus());
                  list.add(EBIPGFactory.getLANG("EBI_LANG_TYPE")+":"+task.getType());
                  list.add(EBIPGFactory.getLANG("EBI_LANG_DURATION")+":"+task.getDuration());
                  list.add(EBIPGFactory.getLANG("EBI_LANG_TASK_DONE")+":"+task.getDone());
                  list.add(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION")+":"+task.getDescription());
                  list.add("");
                  list.add(EBIPGFactory.getLANG("EBI_LANG_COST"));

                  Iterator itc = task.getCrmprojectcosts().iterator();
                  while(itc.hasNext()){
                    Crmprojectcost cost = (Crmprojectcost)itc.next();
                    list.add(EBIPGFactory.getLANG("EBI_LANG_NAME")+":"+cost.getName());
                    list.add(EBIPGFactory.getLANG("EBI_LANG_VALUE")+":"+cost.getValue());
                    list.add("*EOR*");  
                  }

                  list.add("");
                  list.add(EBIPGFactory.getLANG("EBI_LANG_PROPERTIES"));
                   
                  Iterator itp = task.getCrmprojectprops().iterator();
                  while(itp.hasNext()){
                    Crmprojectprop prop = (Crmprojectprop)itp.next();
                    list.add(EBIPGFactory.getLANG("EBI_LANG_NAME")+":"+prop.getName());
                    list.add(EBIPGFactory.getLANG("EBI_LANG_VALUE")+":"+prop.getValue());
                    list.add("*EOR*");
                  }
                 list.add("*EOR*");
               }


               projectPane.ebiModule.hcreator.setDataToCreate(new EBICRMHistoryDataUtil(id, "Project", list));

            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createProduct(int id){
        Query query;
        try {

            query = projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").createQuery(
                    "from Crmproject where projectid=? ").setInteger(0, id);

            Iterator iter = query.list().iterator();
            if(iter.hasNext()){
                
               Crmproject proj = (Crmproject)iter.next();
               projectPane.ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBIPROJECT_SESSION").refresh(proj);

               projectPane.ebiModule.guiRenderer.getTextfield("ProductNrTex","Product").setText(proj.getProjectnr());
               projectPane.ebiModule.guiRenderer.getTextfield("ProductNameText","Product").setText(proj.getName());
               projectPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product").setValue(proj.getActualcost());
               projectPane.ebiModule.guiRenderer.getFormattedTextfield("productNetamoutText","Product")
                        .setText(projectPane.ebiModule.guiRenderer.
                                getFormattedTextfield("productNetamoutText","Product").getValue().toString());
            }

        }catch(Exception ex){
              ex.printStackTrace();
        }
    }


    public Crmproject getProjectBean(){
        return project;
    }


            /**
     * Check if a loaded record is locked
     * @param compNr
     * @param showMessage
     * @throws Exception
     */

    public boolean checkIslocked(int compNr, boolean showMessage) throws Exception{
            boolean ret = false;

         try{
            PreparedStatement ps =  projectPane.ebiModule.ebiPGFactory.database.initPreparedStatement("SELECT * FROM EBIPESSIMISTIC WHERE RECORDID=? AND MODULENAME=?  ");
            ps.setInt(1,compNr);
            ps.setString(2,"CRMProject");
            ResultSet rs = projectPane.ebiModule.ebiPGFactory.database.executePreparedQuery(ps);

            rs.last();

            if (rs.getRow() <= 0) {
                lockId = compNr;
                lockModuleName = "CRMProject";
                lockUser = EBIPGFactory.ebiUser;
                lockStatus = 1;
                lockTime =  new Timestamp(new Date().getTime());
                projectPane.ebiModule.lockCompanyRecord(compNr,"CRMProject",lockTime);
                activateLockedInfo(false);
            }else{
                rs.beforeFirst();
                rs.next();
                lockId = rs.getInt("RECORDID");
                lockModuleName = rs.getString("MODULENAME");
                lockUser = rs.getString("USER");
                lockStatus = rs.getInt("STATUS");
                lockTime =  rs.getTimestamp("LOCKDATE");

                if(!lockUser.equals(EBIPGFactory.ebiUser)){
                    activateLockedInfo(true);
                }

                if(showMessage && !lockUser.equals(EBIPGFactory.ebiUser)){
                    ret = true;
                }
            }

             // Pessimistic Dialog view info
             projectPane.ebiModule.guiRenderer.getLabel("userx","pessimisticViewDialog").setText(lockUser);
             projectPane.ebiModule.guiRenderer.getLabel("statusx","pessimisticViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_LOCKED"));

             if(lockTime != null){
                projectPane.ebiModule.guiRenderer.getLabel("timex","pessimisticViewDialog").setText(lockTime.toString());
             }
         }catch (Exception ex){}
        return ret;
    }

    /**
     * Activate Pessimistic Lock for the GUI
     * @param enabled
     */

    public void activateLockedInfo(boolean enabled){
        try{
            //show red icon to a visual panel
            projectPane.ebiModule.guiRenderer.getVisualPanel("Project").showLockIcon(enabled);

            //hide the delete buttons from crm panel
            projectPane.ebiModule.guiRenderer.getButton("saveProject","Project").setEnabled(enabled ? false : true);

            projectPane.ebiModule.guiRenderer.getButton("deleteProject","Project").setEnabled(enabled ? false : true);
        }catch (Exception ex){}
    }


}
