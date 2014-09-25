package ebiCRM.gui.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hibernate.Query;
import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelProblemSolution;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.model.hibernate.Companyservicepsol;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolutions;


public class EBIProblemSolutionSelectionDialog {

    public MyTableModelProblemSolution tabModel = null;
    public Set<Crmproblemsolutions> crmSolutionList = new HashSet<Crmproblemsolutions>();
    public Set<Companyservicepsol> serviceSolutionList = null;
    public int selRow = -1;
    public EBICRMModule ebiModule = null;

    public EBIProblemSolutionSelectionDialog(EBICRMModule module, Set<Companyservicepsol> ssolList) {
        super();
        ebiModule = module;

        tabModel = new MyTableModelProblemSolution();
        serviceSolutionList = ssolList;

        ebiModule.guiRenderer.loadGUI("CRMDialog/crmSelectionDialog.xml");

        showCollectionList();
    }

    public void setVisible(){
          ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_PROBLEMSOLUTION_DATA"));
          ebiModule.guiRenderer.getVisualPanel("abstractSelectionDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_PROBLEMSOLUTION_DATA"));

          ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setRowFilter(RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").getText()));
            }
          }); 

          ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setModel(tabModel);
          ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
          ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                  public void valueChanged(ListSelectionEvent e) {
                      if (e.getValueIsAdjusting()) {
                          return;
                      }

                      ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                      if(lsm.getMinSelectionIndex() != -1){
                        selRow = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                      }
                      if (lsm.isSelectionEmpty()) {
                          ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").setEnabled(false);
                          selRow = -1;
                      } else if (!tabModel.getRow(0)[0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                          selRow = lsm.getMinSelectionIndex();
                          ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").setEnabled(true);
                      }
                  }
              });
              ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").addMouseListener(new java.awt.event.MouseAdapter() {

                  public void mouseClicked(java.awt.event.MouseEvent e) {
                      if(ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()) != -1){
                        selRow = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").rowAtPoint(e.getPoint()));
                      }else{
                          return;
                      }
                      if (e.getClickCount() == 2) {

                          if (selRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                  equals(tabModel.data[selRow][0].toString())) {
                              return;
                          }

                          ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
                          fillCollection();

                      }
                  }
              });

            ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));
            ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").setEnabled(false);
            ebiModule.guiRenderer.getButton("applyButton","abstractSelectionDialog").addActionListener(new java.awt.event.ActionListener() {

                  public void actionPerformed(java.awt.event.ActionEvent e) {
                      if (selRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                              equals(tabModel.data[selRow][0].toString())) {
                          return;
                      }
                      fillCollection();
                      ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);

                  }
              });

           ebiModule.guiRenderer.getButton("closeButton","abstractSelectionDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
           ebiModule.guiRenderer.getButton("closeButton","abstractSelectionDialog").addActionListener(new java.awt.event.ActionListener() {

                  public void actionPerformed(java.awt.event.ActionEvent e) {
                      ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
                  }
              });

           ebiModule.guiRenderer.showGUI();
      }

    private void copyCollection(int[] id) {

        if (crmSolutionList != null) {
            int i = 0;
            Iterator iter = this.crmSolutionList.iterator();
            while (iter.hasNext()) {

                Crmproblemsolutions solution = (Crmproblemsolutions) iter.next();

                if (solution.getProsolid() == id[i]) {
                    Companyservicepsol csol = new Companyservicepsol();
                    csol.setCompanyservice(ebiModule.getServicePane().serviceDataControl.getcompService());
                    csol.setSolutionnr(solution.getServicenr());
                    csol.setName(solution.getName());
                    csol.setClassification(solution.getClassification());
                    csol.setCategory(solution.getCategory());
                    csol.setType(solution.getType());
                    csol.setStatus(solution.getStatus());
                    csol.setDescription(solution.getDescription());
                    csol.setCreateddate(solution.getCreateddate());
                    csol.setCreatedfrom(solution.getCreatedfrom());
                    csol.setChangeddate(solution.getChangeddate());
                    csol.setChangedfrom(solution.getChangedfrom());
                    serviceSolutionList.add(csol);
                    ebiModule.getServicePane().serviceDataControl.getcompService().getCompanyservicepsols().add(csol);
                    i++;
                }
            }
        }
    }

    private void fillCollection() {

        int[] rows = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").getSelectedRows();
        int[] id = new int[rows.length + 1];
        for (int i = 0; i < rows.length; i++) {
            id[i] = Integer.parseInt(tabModel.data[ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(rows[i])][7].toString());
        }
        copyCollection(id);

    }

 

    private void showCollectionList() {

         try {
            
            Query query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").createQuery("FROM Crmproblemsolutions");
            if(query.list().size() > 0){
                tabModel.data = new Object[query.list().size()][8];

                Iterator it = query.iterate();
                int i = 0;

                while (it.hasNext()) {
                    Crmproblemsolutions comps = (Crmproblemsolutions) it.next();
                    ebiModule.ebiPGFactory.hibernate.getHibernateSession("EBICRM_SESSION").refresh(comps);
                    tabModel.data[i][0] = comps.getServicenr() == null ? "" : comps.getServicenr();
                    tabModel.data[i][1] = comps.getName() == null ? "" : comps.getName();
                    tabModel.data[i][2] = comps.getClassification() == null ? "" : comps.getClassification();
                    tabModel.data[i][3] = comps.getCategory() == null ? "" : comps.getCategory();
                    tabModel.data[i][4] = comps.getType() == null ? "" : comps.getType();
                    tabModel.data[i][5] = comps.getStatus() == null ? "" : comps.getStatus();
                    tabModel.data[i][6] = comps.getDescription() == null ? "" : comps.getDescription();
                    tabModel.data[i][7] = comps.getProsolid();
                    crmSolutionList.add(comps);
                    i++;
                }
            }else{
                tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "",""}};
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabModel.fireTableDataChanged();
    }
}

