package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelReceiver extends AbstractTableModel {

    public String[] columnNames = {
        
        EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE"),
        EBIPGFactory.getLANG("EBI_LANG_C_GENDER"),
        EBIPGFactory.getLANG("EBI_LANG_SURNAME"),
        EBIPGFactory.getLANG("EBI_LANG_C_CNAME"),
        EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION"),
        EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR"),
        EBIPGFactory.getLANG("EBI_LANG_C_ZIP"),
        EBIPGFactory.getLANG("EBI_LANG_C_LOCATION"),
        EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE"),
        EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY"),
    };
    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "", "", ""}};

    public MyTableModelReceiver() {}

    public MyTableModelReceiver(boolean isCampaign) {
        if (isCampaign) {
            columnNames = new String[]{
                
                EBIPGFactory.getLANG("EBI_LANG_C_SEND_TYPE"),
                EBIPGFactory.getLANG("EBI_LANG_COMPANY"),
                EBIPGFactory.getLANG("EBI_LANG_COMPANY_NUMBER"),
                EBIPGFactory.getLANG("EBI_LANG_C_GENDER"),
                EBIPGFactory.getLANG("EBI_LANG_NAME"),
                EBIPGFactory.getLANG("EBI_LANG_C_CNAME"),
                EBIPGFactory.getLANG("EBI_LANG_CONTACT_POSITION"),
                EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR"),
                EBIPGFactory.getLANG("EBI_LANG_C_ZIP"),
                EBIPGFactory.getLANG("EBI_LANG_C_LOCATION"),
                EBIPGFactory.getLANG("EBI_LANG_C_POST_CODE"),
                EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY"),
            };
            
            data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", "", "", "", "", "", ""}};
            
        }
    }

    public Object[] getRow(int row) {
        if (row < 0 || row > data.length - 1) {
            return null;
        }

        return data[row];
    }

    public void setRow(int row, Object[] rowData) {
        if (row < 0 || row > data.length - 1) {
            return;
        }

        data[row] = rowData;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return false;
    }
}
