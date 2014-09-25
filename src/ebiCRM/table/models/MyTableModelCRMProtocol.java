package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelCRMProtocol extends AbstractTableModel {

    public String[] columnNames = {
        EBIPGFactory.getLANG("EBI_LANG_DATE"),
        EBIPGFactory.getLANG("EBI_LANG_C_EMAIL_SUBJECT"),
        EBIPGFactory.getLANG("EBI_LANG_C_MEETING_TYPE"),
        EBIPGFactory.getLANG("EBI_LANG_C_MEMO_MEMO")

    };

    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", ""}};

    public Object[] getRow(int row) {
        if (row < 0 || row > data.length - 1) {
            return null;
        }

        return data[row];
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
        return data[row][col] == null ? "" :  data[row][col] ;
    }

}

