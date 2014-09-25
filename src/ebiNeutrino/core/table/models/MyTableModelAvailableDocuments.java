package ebiNeutrino.core.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelAvailableDocuments extends AbstractTableModel {

		public String[] columnNames = {
				"Id", 
	            EBIPGFactory.getLANG("EBI_LANG_NAME"),
	            EBIPGFactory.getLANG("EBI_LANG_TYPE")
	            };
		public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "",""}};

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
