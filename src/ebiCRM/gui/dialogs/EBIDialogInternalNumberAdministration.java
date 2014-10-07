package ebiCRM.gui.dialogs;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelInternalNumber;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBIDialogInternalNumberAdministration{

    private MyTableModelInternalNumber tabModel = null;
    private boolean isEdit = false;
    private int id = -1;
    private EBICRMModule ebiModule = null;
    private int selRow = -1;
    private boolean isInvoice = false;

    public EBIDialogInternalNumberAdministration(EBICRMModule module, boolean isInvoice) {

        this.ebiModule = module;
        this.isInvoice = isInvoice;
        ebiModule.gui.loadGUI("CRMDialog/autoIncNrDialog.xml");
        tabModel = new MyTableModelInternalNumber();
        if(isInvoice){
            fillComboInvoiceCategory();
            showInvoiceNumber();
        }else{
            fillComboCategory();
            showNumber();
        }

    }


     private void fillComboInvoiceCategory() {

        ResultSet set = null;

        try {
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("SELECT NAME FROM CRMINVOICECATEGORY ");
            set = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);
            ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {
                    ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").addItem(set.getString("NAME"));
                }
            }

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillComboCategory() {

        ResultSet set = null;

        try {
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("SELECT NAME FROM COMPANYCATEGORY ");
            set = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);
            ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {
                    ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").addItem(set.getString("NAME"));
                }
            }

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            try {
                if (set != null) {
                    set.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void setVisible() {
        ebiModule.gui.getEBIDialog("autoIncNrDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_FORM_INTERNAL_NUMBER_SETTING"));
        ebiModule.gui.getVisualPanel("autoIncNrDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_FORM_INTERNAL_NUMBER_SETTING"));
        ebiModule.gui.getLabel("numberTo","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_NUMBER_TO"));
        ebiModule.gui.getLabel("numberFrom","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_NUMBER_FROM"));
        ebiModule.gui.getLabel("category","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));

        ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").addKeyListener(new KeyAdapter() {

                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (isEdit == false) {
                            saveNumber();
                        } else {
                            updateNumber();
                        }
                    }
                }
            });

        ebiModule.gui.getButton("saveValue","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
        ebiModule.gui.getButton("saveValue","autoIncNrDialog").addActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isEdit == false) {
                        saveNumber();
                    } else {
                        updateNumber();
                    }
                }
            });

        ebiModule.gui.getTable("valueTable","autoIncNrDialog").setModel(tabModel);
        ebiModule.gui.getTable("valueTable","autoIncNrDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if(lsm.getMinSelectionIndex() != -1){
                        selRow = ebiModule.gui.getTable("valueTable","autoIncNrDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.gui.getButton("editBnt","autoIncNrDialog").setEnabled(false);
                        ebiModule.gui.getButton("deleteBnt","autoIncNrDialog").setEnabled(false);
                    } else if (!tabModel.getRow(ebiModule.gui.getTable("valueTable","autoIncNrDialog").getSelectedRow())[0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.gui.getButton("editBnt","autoIncNrDialog").setEnabled(true);
                        ebiModule.gui.getButton("deleteBnt","autoIncNrDialog").setEnabled(true);
                    }
                }
            });
            ebiModule.gui.getTable("valueTable","autoIncNrDialog").addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if(ebiModule.gui.getTable("valueTable","autoIncNrDialog").rowAtPoint(e.getPoint()) != -1){
                        selRow = ebiModule.gui.getTable("valueTable","autoIncNrDialog").convertRowIndexToModel(ebiModule.gui.getTable("valueTable","autoIncNrDialog").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2 && selRow != -1) {

                        if (!tabModel.data[selRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            editNumber(selRow);
                        }
                    }
                }
            });

           ebiModule.gui.getButton("newBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_NEW);
           ebiModule.gui.getButton("newBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newNumber();
                }
           });

          ebiModule.gui.getButton("editBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.gui.getButton("editBnt","autoIncNrDialog").setEnabled(false);
          ebiModule.gui.getButton("editBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editNumber(selRow);
                }
            });

        ebiModule.gui.getButton("deleteBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.gui.getButton("deleteBnt","autoIncNrDialog").setEnabled(false);
        ebiModule.gui.getButton("deleteBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deleteNumber(selRow);
                }
         });

        ebiModule.gui.showGUI();
    }

    private void newNumber() {
        ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").setSelectedIndex(0);
        ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").setText("");
        ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").setText("");
        ebiModule.gui.getTextfield("beginCharText","autoIncNrDialog").setText("");
        this.id = 0;
        isEdit = false;
        if(isInvoice){
            showInvoiceNumber(); 
        }else{
            showNumber();
        }
    }

    private void saveNumber() {
        if (!validateInput()) {
            return;
        }
        try {
            String table;
            if(isInvoice){
               table = "CRMINVOICENUMBER";
            }else{
               table ="COMPANYNUMBER";
            }
            ebiModule.system.getIEBIDatabase().setAutoCommit(true);
            int cid = retriveIDFromCategory(ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            String sql = "INSERT INTO "+table+" (CATEGORY,NUMBERFROM,NUMBERTO,BEGINCHAR,CATEGORYID) values(?,?,?,?,?) ";
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement(sql);
            ps.setString(1, ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").getText()));
            ps.setInt(3, Integer.parseInt(ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").getText()));
            ps.setString(4, ebiModule.gui.getTextfield("beginCharText","autoIncNrDialog").getText());
            ps.setInt(5, cid );
            ebiModule.system.getIEBIDatabase().executePreparedStmt(ps);
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            ebiModule.system.getIEBIDatabase().setAutoCommit(false);
        }
        newNumber();
    }

    private int retriveIDFromCategory(String category) {
        int toRet = -1;
        ResultSet set = null;
        try {
             String table;
             if(isInvoice){
               table = "CRMINVOICECATEGORY";
            }else{
               table ="COMPANYCATEGORY";
            }
            String sql = "SELECT * FROM "+table+" WHERE NAME=?";
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement(sql);
            ps.setString(1, category);
            set = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);
            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                set.next();
                toRet = set.getInt("ID");
            }

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        }finally{
            try {
                if (set != null) {
                    set.close();
                }
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        }

        return toRet;
    }

    private void updateNumber() {
        if (!validateInput()) {
            return;
        }
        try {
            ebiModule.system.getIEBIDatabase().setAutoCommit(true);
            String table;
            if(isInvoice){
               table = "CRMINVOICENUMBER";
            }else{
               table ="COMPANYNUMBER";
            }
            String sql = "UPDATE "+table+" SET CATEGORY=?,NUMBERFROM=?,NUMBERTO=?, CATEGORYID=?, BEGINCHAR=? where ID=?";
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement(sql);
            int cid = retriveIDFromCategory(ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setString(1, ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").getText()));
            ps.setInt(3, Integer.parseInt(ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").getText()));
            ps.setInt(4, cid);
            ps.setString(5, ebiModule.gui.getTextfield("beginCharText","autoIncNrDialog").getText());
            ps.setInt(6, id);
            ebiModule.system.getIEBIDatabase().executePreparedStmt(ps);

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            ebiModule.system.getIEBIDatabase().setAutoCommit(false);
        }
        newNumber();
    }

    private void editNumber(int row) {
        if (row < 0) {
            return;
        }
        if (tabModel.data[row][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            return;
        }

        id = Integer.parseInt(tabModel.data[row][0].toString());
        ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").setSelectedItem(tabModel.data[row][1].toString());
        ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").setText(tabModel.data[row][2].toString());
        ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").setText(tabModel.data[row][3].toString());
        ebiModule.gui.getTextfield("beginCharText","autoIncNrDialog").setText(tabModel.data[row][4].toString());

        isEdit = true;

    }

    private void deleteNumber(int row) {
        if (row < 0) {
            return;
        }
        if (tabModel.data[row][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            return;
        }

        try{
            ebiModule.system.getIEBIDatabase().setAutoCommit(true);
            String table;
            if(isInvoice){
               table = "CRMINVOICENUMBER";
            }else{
               table ="COMPANYNUMBER";
            }
            PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("DELETE FROM "+table+" where ID=? ");
            ps.setString(1,tabModel.data[row][0].toString());

            if (!ebiModule.system.getIEBIDatabase().executePreparedStmt(ps)) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_DELETE_RECORD")).Show(EBIMessage.ERROR_MESSAGE);
            }

            newNumber();
       }catch(SQLException ex){ex.printStackTrace();}
        finally{
            ebiModule.system.getIEBIDatabase().setAutoCommit(false);
        }
    }

    private void showNumber() {
        PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYNUMBER ");
        ResultSet set = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);
        try {
            set.last();
            if (set.getRow() > 0) {

                tabModel.data = new Object[set.getRow()][5];
                set.beforeFirst();
                int i = 0;
                while (set.next()) {
                    tabModel.data[i][0] = set.getString("ID");
                    tabModel.data[i][1] = set.getString("CATEGORY");
                    tabModel.data[i][2] = set.getString("NUMBERFROM");
                    tabModel.data[i][3] = set.getString("NUMBERTO");
                    tabModel.data[i][4] = set.getString("BEGINCHAR") == null ? "" : set.getString("BEGINCHAR");
                    i++;
                }
            } else {
                tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "",""}};
            }
            tabModel.fireTableDataChanged();
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showInvoiceNumber() {
        PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("SELECT * FROM CRMINVOICENUMBER ");
        ResultSet set = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);
        try {
            set.last();
            if (set.getRow() > 0) {

                tabModel.data = new Object[set.getRow()][5];
                set.beforeFirst();
                int i = 0;
                while (set.next()) {
                    tabModel.data[i][0] = set.getString("ID");
                    tabModel.data[i][1] = set.getString("CATEGORY");
                    tabModel.data[i][2] = set.getString("NUMBERFROM");
                    tabModel.data[i][3] = set.getString("NUMBERTO");
                    tabModel.data[i][4] = set.getString("BEGINCHAR") == null ? "" : set.getString("BEGINCHAR");
                    i++;
                }
            } else {
                tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "",""}};
            }
            tabModel.fireTableDataChanged();
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean validateInput() {
        if (ebiModule.gui.getComboBox("categoryCombo","autoIncNrDialog").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NUMBER_FROM_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(ebiModule.gui.getTextfield("numberFromText","autoIncNrDialog").getText());
        } catch (NumberFormatException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NUMBER_FROM_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERORR_NUMBER_TO_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(ebiModule.gui.getTextfield("numberToText","autoIncNrDialog").getText());
        } catch (NumberFormatException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERORR_NUMBER_TO_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}