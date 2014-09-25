package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelOpportunity extends AbstractTableModel {

    public String[] columnNames = {
        EBIPGFactory.getLANG("EBI_LANG_NAME"),
        EBIPGFactory.getLANG("EBI_LANG_SALE_STAGE"),
        EBIPGFactory.getLANG("EBI_LANG_PROBABILITY"),
        EBIPGFactory.getLANG("EBI_LANG_BUSINESS_TYP"),
        EBIPGFactory.getLANG("EBI_LANG_VALUE"),
        EBIPGFactory.getLANG("EBI_LANG_C_IS_CLOSED"),
        EBIPGFactory.getLANG("EBI_LANG_C_CLOSED_DATE")
    };
    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};

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

}