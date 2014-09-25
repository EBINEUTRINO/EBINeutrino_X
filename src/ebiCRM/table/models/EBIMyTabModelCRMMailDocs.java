package ebiCRM.table.models;

import javax.swing.table.AbstractTableModel;

import ebiNeutrinoSDK.EBIPGFactory;

public class EBIMyTabModelCRMMailDocs extends AbstractTableModel{

		
			public String[] columnNames = {
					EBIPGFactory.getLANG("EBI_LANG_ID"),
		            EBIPGFactory.getLANG("EBI_LANG_FILENAME"),
		            };
			public Object[][] data = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"",}};
		
			public Object[][] dax = {{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","","","",}};
			
			public void addRow(int row,Object[] cols){
				if(data.length == 0 && data[0][0].equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT")) && dax.length == 0 && dax[0][0].equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
					data = new Object[1][2];
					dax  = new Object[1][5]; 
				}
				
				if(row >= dax.length){
					
					Object[][] tmp = new Object[row+1][5];
					for(int i = 0; i < dax.length; i++){
						tmp[i] = dax[i];
					}
					dax = tmp;
					
					
					Object[][] tmp_1 = new Object[row+1][2];
					for(int x = 0; x < data.length; x++){
						tmp_1[x] = data[x];
					}
					data = tmp_1;
				}
			    
			    data[row][0] = cols[3];
			    data[row][1] = cols[4];
			    data[row][2] = cols[5];
			    data[row][3] = cols[6];
			    data[row][4] = cols[7];
			    data[row][5] = cols[8];
			    dax[row] = cols;
			   
			}
			
			public void removeRow(int row){
				Object[][] tmp = new Object[data.length-1][5];
				int zahler = 0;
				for(int i = 0; i < data.length; i++){
					if(i != row){
						tmp[zahler] = data[i];
						zahler++;
					}
				}
				
				if(tmp.length == 0) {
					tmp = new Object[1][5];
					tmp[0][0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
					tmp[0][1] = "";
					tmp[0][2] = "";
					tmp[0][3] = "";
					tmp[0][4] = "";
					tmp[0][5] = "";
				}
				data = tmp;
				dax = tmp;
			}
			
			public Object[] getRow(int row){
				if(row < 0 || row > data.length - 1) {
					return null;
				}
				
				return data[row];
			}
			
			public void setRow(int row, Object[] rowData){
				if(row < 0 || row > data.length - 1) {
					return;
				}
				
			    data[row][0] = rowData[3];
			    data[row][1] = rowData[4];
			    data[row][2] = rowData[5];
			    data[row][3] = rowData[6];
			    data[row][4] = rowData[7];
			    data[row][5] = rowData[8];
			    dax[row] = rowData;

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


