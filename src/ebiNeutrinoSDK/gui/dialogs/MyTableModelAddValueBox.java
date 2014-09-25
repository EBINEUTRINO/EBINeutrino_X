package ebiNeutrinoSDK.gui.dialogs;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelAddValueBox extends AbstractTableModel {

		public String[] columnNames = {EBIPGFactory.getLANG("EBI_LANG_VALUE")};
		public Object[][] data = {{""}};	

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
	     * Don't need to implement this method unless your table's
	     * editable.
	     */
	    public boolean isCellEditable(int row, int col) {
	        //Note that the data/cell address is constant,
	        //no matter where the cell appears onscreen.
	        if (col <= 1) { 
	            return true;
	        } else {
	            return false;
	        }
	    }

	    /*
	     * Don't need to implement this method unless your table's
	     * data can change.
	     */
	    public void setValueAt(Object value, int row, int col) {

	        if (data[0][col] instanceof Integer                        
	                && !(value instanceof Integer)) {                  
	            try {
	                data[row][col] = new Integer(value.toString());
	                fireTableCellUpdated(row, col);
	            } catch (NumberFormatException e) {
	          
	            }
	        } else {
	            data[row][col] = value;
	            fireTableCellUpdated(row, col);
	        }

	    }

	}