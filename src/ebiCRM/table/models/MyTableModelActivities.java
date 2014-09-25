package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelActivities extends AbstractTableModel {

    public String[] columnNames = {
        EBIPGFactory.getLANG("EBI_LANG_NAME"),
        EBIPGFactory.getLANG("EBI_LANG_TYPE"),
        EBIPGFactory.getLANG("EBI_LANG_DUE_DATE"),
        EBIPGFactory.getLANG("EBI_LANG_DURATION"),
        EBIPGFactory.getLANG("EBI_LANG_COLOR"),
        EBIPGFactory.getLANG("EBI_LANG_C_STATUS"),
        EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION")
    };
    
    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "","","",""}};

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
        Object obj = null;

        try{
            obj =data[row][col];
        }catch(ArrayIndexOutOfBoundsException ex){ }

        return obj;
    }



}
