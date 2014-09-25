package ebiCRM.gui.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelOpportunity;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.model.hibernate.Companyopportunity;

public class EBIOpportunitySelectionDialog {

    public MyTableModelOpportunity tabModel = null;
    private List<Companyopportunity> opList = null;
    public int selRow = -1;
    public boolean shouldSave = false;
    public String name = "";
    public int id = 0;
    private EBICRMModule ebiModule = null;

    
    public EBIOpportunitySelectionDialog(List<Companyopportunity> opList, EBICRMModule module) {
        ebiModule = module;
        tabModel = new MyTableModelOpportunity();
        this.opList = opList;

        ebiModule.guiRenderer.loadGUI("CRMDialog/crmSelectionDialog.xml");

        showCollectionList();
    }

    public void setVisible(){
        ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY_LIST"));
        ebiModule.guiRenderer.getVisualPanel("abstractSelectionDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY_LIST"));
        
        
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

                        if (e.getClickCount() == 2) {

                            if (selRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                    equals(tabModel.data[selRow][0].toString())) {
                                return;
                            }

                            ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
                            copyCollection(selRow);
                            shouldSave = true;

                        }
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
                    copyCollection(selRow);
                    shouldSave = true;
                    ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);

                }
            });

         ebiModule.guiRenderer.getButton("closeButton","abstractSelectionDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
         ebiModule.guiRenderer.getButton("closeButton","abstractSelectionDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setVisible(false);
                }
         });

         ebiModule.guiRenderer.getTextfield("filterTableText","abstractSelectionDialog").requestFocus();
         ebiModule.guiRenderer.showGUI();
    }

    private void copyCollection(int row) {

        Iterator itr = this.opList.iterator();
        while (itr.hasNext()) {

            Companyopportunity obj = (Companyopportunity) itr.next();

            if (Integer.parseInt(tabModel.data[row][7].toString()) == obj.getOpportunityid()) {
                this.name = obj.getName();
                this.id = obj.getOpportunityid();
                break;
            }
        }
    }

    private void showCollectionList() {
      if(this.opList.size() > 0){
        tabModel.data = new Object[this.opList.size()][8];

        Iterator itr = this.opList.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Companyopportunity obj = (Companyopportunity) itr.next();
   
            tabModel.data[i][0] = obj.getName() == null ? "" : obj.getName();
            tabModel.data[i][1] = obj.getSalestage() == null ? "" : obj.getSalestage();
            tabModel.data[i][2] = obj.getProbability() == null ? "" : obj.getProbability();
            tabModel.data[i][3] = obj.getBusinesstype() == null ? "" : obj.getBusinesstype();
            tabModel.data[i][4] = obj.getOpportunityvalue() == null ? 0.0 : obj.getOpportunityvalue();

            String isClose = "";
            if (obj.getIsclose() != null) {
                if (obj.getIsclose() == false) {
                    isClose = EBIPGFactory.getLANG("EBI_LANG_NO");
                } else {
                    isClose = EBIPGFactory.getLANG("EBI_LANG_YES");
                }
            }
            tabModel.data[i][5] = isClose;
            tabModel.data[i][6] = obj.getClosedate() == null ? "" : obj.getClosedate();
            tabModel.data[i][7] = obj.getOpportunityid();
            i++;
        }
      }else{
         tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
      }
      tabModel.fireTableDataChanged();

    }
}