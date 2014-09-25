package ebiNeutrino.core.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelComboBox extends AbstractTableModel {

	public String[] columnNames = {
            "--",
            "--"
            };
	public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};

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
