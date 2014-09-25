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
        ebiModule.guiRenderer.loadGUI("CRMDialog/autoIncNrDialog.xml");
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
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT NAME FROM CRMINVOICECATEGORY ");
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
            ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {
                    ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").addItem(set.getString("NAME"));
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
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT NAME FROM COMPANYCATEGORY ");
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
            ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {
                    ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").addItem(set.getString("NAME"));
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
        ebiModule.guiRenderer.getEBIDialog("autoIncNrDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_FORM_INTERNAL_NUMBER_SETTING"));
        ebiModule.guiRenderer.getVisualPanel("autoIncNrDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_CRM_FORM_INTERNAL_NUMBER_SETTING"));
        ebiModule.guiRenderer.getLabel("numberTo","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_NUMBER_TO"));
        ebiModule.guiRenderer.getLabel("numberFrom","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_C_NUMBER_FROM"));
        ebiModule.guiRenderer.getLabel("category","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));

        ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").addKeyListener(new KeyAdapter() {

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

        ebiModule.guiRenderer.getButton("saveValue","autoIncNrDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
        ebiModule.guiRenderer.getButton("saveValue","autoIncNrDialog").addActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (isEdit == false) {
                        saveNumber();
                    } else {
                        updateNumber();
                    }
                }
            });

        ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").setModel(tabModel);
        ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if(lsm.getMinSelectionIndex() != -1){
                        selRow = ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("editBnt","autoIncNrDialog").setEnabled(false);
                        ebiModule.guiRenderer.getButton("deleteBnt","autoIncNrDialog").setEnabled(false);
                    } else if (!tabModel.getRow(ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").getSelectedRow())[0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("editBnt","autoIncNrDialog").setEnabled(true);
                        ebiModule.guiRenderer.getButton("deleteBnt","autoIncNrDialog").setEnabled(true);
                    }
                }
            });
            ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    if(ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").rowAtPoint(e.getPoint()) != -1){
                        selRow = ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").convertRowIndexToModel(ebiModule.guiRenderer.getTable("valueTable","autoIncNrDialog").rowAtPoint(e.getPoint()));
                    }
                    if (e.getClickCount() == 2 && selRow != -1) {

                        if (!tabModel.data[selRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            editNumber(selRow);
                        }
                    }
                }
            });

           ebiModule.guiRenderer.getButton("newBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_NEW);
           ebiModule.guiRenderer.getButton("newBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    newNumber();
                }
           });

          ebiModule.guiRenderer.getButton("editBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_EDIT);
          ebiModule.guiRenderer.getButton("editBnt","autoIncNrDialog").setEnabled(false);
          ebiModule.guiRenderer.getButton("editBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editNumber(selRow);
                }
            });

        ebiModule.guiRenderer.getButton("deleteBnt","autoIncNrDialog").setIcon(EBIConstant.ICON_DELETE);
        ebiModule.guiRenderer.getButton("deleteBnt","autoIncNrDialog").setEnabled(false);
        ebiModule.guiRenderer.getButton("deleteBnt","autoIncNrDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deleteNumber(selRow);
                }
         });

        ebiModule.guiRenderer.showGUI();
    }

    private void newNumber() {
        ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").setSelectedIndex(0);
        ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").setText("");
        ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").setText("");
        ebiModule.guiRenderer.getTextfield("beginCharText","autoIncNrDialog").setText("");
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
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
            int cid = retriveIDFromCategory(ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            String sql = "INSERT INTO "+table+" (CATEGORY,NUMBERFROM,NUMBERTO,BEGINCHAR,CATEGORYID) values(?,?,?,?,?) ";
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement(sql);
            ps.setString(1, ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").getText()));
            ps.setInt(3, Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").getText()));
            ps.setString(4, ebiModule.guiRenderer.getTextfield("beginCharText","autoIncNrDialog").getText());
            ps.setInt(5, cid );
            ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps);
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
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
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement(sql);
            ps.setString(1, category);
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
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
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
            String table;
            if(isInvoice){
               table = "CRMINVOICENUMBER";
            }else{
               table ="COMPANYNUMBER";
            }
            String sql = "UPDATE "+table+" SET CATEGORY=?,NUMBERFROM=?,NUMBERTO=?, CATEGORYID=?, BEGINCHAR=? where ID=?";
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement(sql);
            int cid = retriveIDFromCategory(ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setString(1, ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").getSelectedItem().toString());
            ps.setInt(2, Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").getText()));
            ps.setInt(3, Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").getText()));
            ps.setInt(4, cid);
            ps.setString(5, ebiModule.guiRenderer.getTextfield("beginCharText","autoIncNrDialog").getText());
            ps.setInt(6, id);
            ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps);

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
        } finally {
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
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
        ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").setSelectedItem(tabModel.data[row][1].toString());
        ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").setText(tabModel.data[row][2].toString());
        ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").setText(tabModel.data[row][3].toString());
        ebiModule.guiRenderer.getTextfield("beginCharText","autoIncNrDialog").setText(tabModel.data[row][4].toString());

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
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
            String table;
            if(isInvoice){
               table = "CRMINVOICENUMBER";
            }else{
               table ="COMPANYNUMBER";
            }
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("DELETE FROM "+table+" where ID=? ");
            ps.setString(1,tabModel.data[row][0].toString());

            if (!ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedStmt(ps)) {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_DELETE_RECORD")).Show(EBIMessage.ERROR_MESSAGE);
            }

            newNumber();
       }catch(SQLException ex){ex.printStackTrace();}
        finally{
            ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
        }
    }

    private void showNumber() {
        PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYNUMBER ");
        ResultSet set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
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
        PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM CRMINVOICENUMBER ");
        ResultSet set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
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
        if (ebiModule.guiRenderer.getComboBox("categoryCombo","autoIncNrDialog").getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_INSERT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NUMBER_FROM_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberFromText","autoIncNrDialog").getText());
        } catch (NumberFormatException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NUMBER_FROM_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERORR_NUMBER_TO_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(ebiModule.guiRenderer.getTextfield("numberToText","autoIncNrDialog").getText());
        } catch (NumberFormatException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERORR_NUMBER_TO_IS_NOT_VALID")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}