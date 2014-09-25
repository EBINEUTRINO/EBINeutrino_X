package ebiCRM.gui.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelCRMContact;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.model.hibernate.Companycontacts;
import ebiNeutrinoSDK.model.hibernate.Companymeetingcontacts;
import ebiNeutrinoSDK.model.hibernate.Companymeetingprotocol;
import ebiNeutrinoSDK.model.hibernate.Companyopportunity;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitycontact;

public class EBIContactSelectionDialog  {

    private EBICRMModule ebiModule = null;
    private MyTableModelCRMContact tabModel = null;
    private Set<Companycontacts> contactList = null;
    private Companymeetingprotocol meetingProtocol = null;
    private Companyopportunity opportunity = null;
    private boolean isOpportunity  = false;
    private int selRow = -1;

    
    public EBIContactSelectionDialog(EBICRMModule module, Set<Companycontacts> list, Companymeetingprotocol meetpro) {
        super();
        ebiModule = module;
        ebiModule.guiRenderer.loadGUI("CRMDialog/crmSelectionDialog.xml");

        tabModel = new MyTableModelCRMContact();
        contactList = list;
        meetingProtocol = meetpro;
        isOpportunity = false;
        showCollectionList();
    }

    public EBIContactSelectionDialog(EBICRMModule module, Set<Companycontacts> list, Companyopportunity opport) {
        super();
        ebiModule = module;

        ebiModule.guiRenderer.loadGUI("CRMDialog/crmSelectionDialog.xml");

        tabModel = new MyTableModelCRMContact();
        contactList = list;
        this.opportunity = opport;
        isOpportunity = true;
        showCollectionList();
    }

    public void setVisible(){
        ebiModule.guiRenderer.getEBIDialog("abstractSelectionDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_CONTACT_LIST"));
        ebiModule.guiRenderer.getVisualPanel("abstractSelectionDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_CONTACT_LIST"));

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

    private void fillCollection() {

        int[] rows = ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
          copyCollection(Integer.parseInt(tabModel.data[ebiModule.guiRenderer.getTable("abstractTable","abstractSelectionDialog").convertRowIndexToModel(rows[i])][6].toString()));
        }
    }

    private void copyCollection(int id) {


        if (!this.contactList.isEmpty()) {
          if(!isOpportunity){
            Iterator iter = this.contactList.iterator();

            while (iter.hasNext()) {

                Companycontacts act = (Companycontacts) iter.next();

                if (act.getContactid() == id) {
                    try{
                        ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("EBICRM_SESSION").begin();
                        Companymeetingcontacts contact = new Companymeetingcontacts();
                        contact.setCompanymeetingprotocol(meetingProtocol);
                        contact.setCreateddate(act.getCreateddate());
                        contact.setCreatedfrom(act.getCreatedfrom());
                        contact.setChangeddate(act.getChangeddate());
                        contact.setChangedfrom(act.getChangedfrom());
                        contact.setGender(act.getGender());
                        contact.setPosition(act.getPosition());
                        contact.setSurname(act.getSurname());
                        contact.setName(act.getName());
                        contact.setBirddate(act.getBirddate());
                        contact.setPhone(act.getPhone());
                        contact.setFax(act.getFax());
                        contact.setMobile(act.getMobile());
                        contact.setEmail(act.getEmail());
                        contact.setDescription(act.getDescription());
                        this.meetingProtocol.getCompanymeetingcontactses().add(contact);
                        ebiModule.getMeetingProtocol().meetingDataControl.dataShowContact();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }

            }
        } else {

            Iterator iter = this.contactList.iterator();
            while (iter.hasNext()) {

                Companycontacts act1 = (Companycontacts) iter.next();

                if (act1.getContactid() == id) {
                    Companyopportunitycontact contact1 = new Companyopportunitycontact();
                    contact1.setCompanyopportunity(opportunity);
                    contact1.setCreateddate(act1.getCreateddate());
                    contact1.setCreatedfrom(act1.getCreatedfrom());
                    contact1.setChangeddate(act1.getChangeddate());
                    contact1.setChangedfrom(act1.getChangedfrom());
                    contact1.setGender(act1.getGender());
                    contact1.setPosition(act1.getPosition());
                    contact1.setSurname(act1.getSurname());
                    contact1.setName(act1.getName());
                    contact1.setBirddate(act1.getBirddate());
                    contact1.setPhone(act1.getPhone());
                    contact1.setFax(act1.getFax());
                    contact1.setMobile(act1.getMobile());
                    contact1.setEmail(act1.getEmail());
                    contact1.setDescription(act1.getDescription());
                    this.opportunity.getCompanyopportunitycontacts().add(contact1);
                    ebiModule.getOpportunityPane().opportuniyDataControl.showOpportunityContacts();
                }
            }
          }
        }
    }

    private void showCollectionList() {

        tabModel.data = new Object[this.contactList.size()][7];
           if(this.contactList.size() > 0){
                Iterator itr = this.contactList.iterator();
                int i = 0;
                while (itr.hasNext()) {
                    Companycontacts obj = (Companycontacts) itr.next();

                    tabModel.data[i][0] = obj.getPosition() == null ? "" : obj.getPosition();
                    tabModel.data[i][1] = obj.getGender() == null ? "" : obj.getGender();
                    tabModel.data[i][2] = obj.getSurname() == null ? "" : obj.getSurname();
                    tabModel.data[i][3] = obj.getName() == null ? "" : obj.getName();
                    tabModel.data[i][4] = obj.getPhone() == null ? "" : obj.getPhone();
                    tabModel.data[i][5] = obj.getMobile() == null ? "" : obj.getMobile();
                    tabModel.data[i][6] = obj.getContactid();
                    i++;
                }
           }else{
              tabModel.data = new Object[][] {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "",""}}; 
           }
        tabModel.fireTableDataChanged();
    }
}