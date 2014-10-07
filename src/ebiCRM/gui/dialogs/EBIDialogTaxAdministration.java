package ebiCRM.gui.dialogs;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.table.models.MyTableModelTaxAdministration;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyproducttax;
import ebiNeutrinoSDK.model.hibernate.Companyproducttaxvalue;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIDialogTaxAdministration  {

    private EBICRMModule ebiModule = null;
    private MyTableModelTaxAdministration tabModel = null;
    private Companyproducttax crmTax = null;
    private boolean isEdit = false;
    private int selRow = -1;
    

    
    public EBIDialogTaxAdministration(EBICRMModule module) {

        ebiModule = module;

        tabModel = new MyTableModelTaxAdministration();
        ebiModule.gui.loadGUI("CRMDialog/taxAdminDialog.xml");
        
        initialize();
        setProductTax();
        showTax();
    }

   
    private void initialize() {
        try {
            ebiModule.system.hibernate.openHibernateSession("EBITAX_SESSION");
        } catch (Exception e) {
            e.printStackTrace();
        }

        crmTax = new Companyproducttax();
    }


    public void setVisible(){
        
       ebiModule.gui.getEBIDialog("taxAdminDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_ADMINISTRATION"));
       ebiModule.gui.getVisualPanel("taxAdminDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_TAX_ADMINISTRATION"));
       ebiModule.gui.getLabel("tax","taxAdminDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE"));
       ebiModule.gui.getLabel("value","taxAdminDialog").setText(EBIPGFactory.getLANG("EBI_LANG_TAX_VALUE"));

       ebiModule.gui.getButton("saveValue","taxAdminDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
       ebiModule.gui.getButton("saveValue","taxAdminDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!validateInput()) {
                        return;
                    }
                    saveTax();
                }
       });
        
       ebiModule.gui.getButton("newBnt","taxAdminDialog").setIcon(EBIConstant.ICON_NEW);
       ebiModule.gui.getButton("newBnt","taxAdminDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newTax();
                }
       });
        
       ebiModule.gui.getButton("editBnt","taxAdminDialog").setEnabled(false);
       ebiModule.gui.getButton("editBnt","taxAdminDialog").setIcon(EBIConstant.ICON_EDIT);
       ebiModule.gui.getButton("editBnt","taxAdminDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editTax(ebiModule.gui.getTable("taxValueTable","taxAdminDialog").getSelectedRow());
                }
       });

       ebiModule.gui.getButton("deleteBnt","taxAdminDialog").setEnabled(false);
       ebiModule.gui.getButton("deleteBnt","taxAdminDialog").setIcon(EBIConstant.ICON_DELETE);
       ebiModule.gui.getButton("deleteBnt","taxAdminDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deleteTax(ebiModule.gui.getTable("taxValueTable","taxAdminDialog").getSelectedRow());
                }
        });

       ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").setModel(new DefaultComboBoxModel(EBICRMProduct.taxType));

       ebiModule.gui.getTable("taxValueTable","taxAdminDialog").setModel(tabModel);
       ebiModule.gui.getTable("taxValueTable","taxAdminDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if(lsm.getMinSelectionIndex() != -1){
                        selRow = ebiModule.gui.getTable("taxValueTable","taxAdminDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.gui.getButton("editBnt","taxAdminDialog").setEnabled(false);
                        ebiModule.gui.getButton("deleteBnt","taxAdminDialog").setEnabled(false);
                    } else if (!tabModel.getRow(ebiModule.gui.getTable("taxValueTable","taxAdminDialog").getSelectedRow())[0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.gui.getButton("editBnt","taxAdminDialog").setEnabled(true);
                        ebiModule.gui.getButton("deleteBnt","taxAdminDialog").setEnabled(true);
                    }
                }
        });

        ebiModule.gui.getTable("taxValueTable","taxAdminDialog").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    selRow = ebiModule.gui.getTable("taxValueTable","taxAdminDialog").convertRowIndexToModel(ebiModule.gui.getTable("taxValueTable","taxAdminDialog").rowAtPoint(e.getPoint()));

                    if (e.getClickCount() == 2) {

                        if (!tabModel.data[selRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            editTax(selRow);
                        }
                    }
                }
        });

       ebiModule.gui.getTextfield("taxValue","taxAdminDialog").addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (!validateInput()) {
                            return;
                        }
                        saveTax();
                    }
                }
       });
        
     ebiModule.gui.showGUI();
    }

    private void setProductTax() {
        try {
            ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").removeAllItems();
            ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").begin();

            Query query = ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").createQuery("FROM Companyproducttaxvalue");
            Iterator it = query.iterate();
            int i = 1;


            ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
            while (it.hasNext()) {
                Companyproducttaxvalue compofst = (Companyproducttaxvalue) it.next();
                ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").refresh(compofst);
                ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").addItem(compofst.getName());
                i++;
            }
        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private boolean validateInput() {
        if (EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").getSelectedItem().toString())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT_TAX_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if(!isEdit){
            for(int i=0; i<tabModel.data.length; i++){
                if(tabModel.data[i][0].toString().toLowerCase().equals(ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").getSelectedItem().toString().toLowerCase())){
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SAME_RECORD_EXSIST")).Show(EBIMessage.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if ("".equals(ebiModule.gui.getTextfield("taxValue","taxAdminDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_INSERT_TAX_VALUE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void newTax() {
        isEdit = false;
        this.crmTax = new Companyproducttax();
        ebiModule.gui.getTextfield("taxValue","taxAdminDialog").setText("");
        ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").setSelectedIndex(0);
        showTax();
    }

    private void saveTax() {

        try {

            if (!ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").isActive()) {
                ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").begin();
            }

            if (!isEdit) {
                crmTax = new Companyproducttax();
                crmTax.setCreateddate(new java.util.Date());
                crmTax.setCreatedfrom(EBIPGFactory.ebiUser);
            } else {
                crmTax.setChangeddate(new java.util.Date());
                crmTax.setChangedfrom(EBIPGFactory.ebiUser);
            }

            crmTax.setName(ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").getSelectedItem().toString());
            crmTax.setTaxvalue(Double.parseDouble(ebiModule.gui.getTextfield("taxValue","taxAdminDialog").getText()));
            ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").saveOrUpdate(this.crmTax);
            ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").commit();

        } catch (org.hibernate.HibernateException ex) {
            try {
                ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").rollback();
            } catch (HibernateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        newTax();

    }

    private void editTax(int row) {
        if (row < 0) {
            return;
        }
        if (tabModel.data[row][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            return;
        }
        try {
            ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").begin();

            Query query = ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").createQuery("from Companyproducttax where id=? ").setString(0, tabModel.data[row][2].toString());

            Iterator iter = query.iterate();
            if (iter.hasNext()) {
                this.crmTax = (Companyproducttax) iter.next();
                ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").refresh(crmTax);
                ebiModule.gui.getComboBox("taxCombo","taxAdminDialog").setSelectedItem(crmTax.getName());
                ebiModule.gui.getTextfield("taxValue","taxAdminDialog").setText(String.valueOf(crmTax.getTaxvalue()));
            }

        } catch (org.hibernate.HibernateException ex) {
            try {
                ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").rollback();
            } catch (HibernateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        isEdit = true;
    }

    private void deleteTax(int row) {
        if (row < 0) {
            return;
        }
        if (tabModel.data[row][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            return;
        }

        if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_QUESTION_WOULD_YOU_DELETE_THIS_TYPE")).Show(EBIMessage.INFO_MESSAGE_YESNO)) {


            try {
                ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").begin();

                Query query = ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").createQuery("from Companyproducttax where id=? ").setString(0, tabModel.data[row][2].toString());

                Iterator iter = query.iterate();
                if (iter.hasNext()) {
                    this.crmTax = (Companyproducttax) iter.next();
                    ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").delete(this.crmTax);
                    ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").commit();
                }

            } catch (org.hibernate.HibernateException ex) {
                try {
                    ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").rollback();
                } catch (HibernateException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            newTax();
        }

    }

    private void showTax() {
        try {
            ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").begin();
            Query query = ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").createQuery("from Companyproducttax ");

            if (query.list().size() > 0) {
                tabModel.data = new Object[query.list().size()][3];

                Iterator iter = query.iterate();
                int i = 0;
                while (iter.hasNext()) {
                    Companyproducttax comtax = (Companyproducttax) iter.next();
                    ebiModule.system.hibernate.getHibernateSession("EBITAX_SESSION").refresh(comtax);
                    tabModel.data[i][0] = comtax.getName();
                    tabModel.data[i][1] = comtax.getTaxvalue();
                    tabModel.data[i][2] = comtax.getId();
                    i++;
                }
                ebiModule.system.hibernate.getHibernateTransaction("EBITAX_SESSION").commit();
            } else {
                tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
            }
            tabModel.fireTableDataChanged();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

