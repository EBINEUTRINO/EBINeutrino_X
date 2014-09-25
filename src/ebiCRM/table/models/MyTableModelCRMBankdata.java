package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelCRMBankdata extends AbstractTableModel {

    public String[] columnNames = {
        EBIPGFactory.getLANG("EBI_LANG_NAME"),
        EBIPGFactory.getLANG("EBI_LANG_C_BANK_CODE"),
        EBIPGFactory.getLANG("EBI_LANG_C_KONTO_NR"),
        EBIPGFactory.getLANG("EBI_LANG_C_BIC"),
        EBIPGFactory.getLANG("EBI_LANG_C_IBAN"),
        EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY")
    };

    public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", ""}};

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
