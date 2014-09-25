package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelSummaryTab extends AbstractTableModel {

    public String[] columnNames = {
        EBIPGFactory.getLANG("EBI_LANG_TYPE"),
        EBIPGFactory.getLANG("EBI_LANG_INFO"),
        EBIPGFactory.getLANG("EBI_LANG_NAME"),
        EBIPGFactory.getLANG("EBI_LANG_CREATED"),
        EBIPGFactory.getLANG("EBI_LANG_CHANGED"),
        EBIPGFactory.getLANG("EBI_LANG_C_STATUS")
    };

    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "",""}};


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
