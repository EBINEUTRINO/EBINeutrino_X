package ebiCRM.utils;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;

public class HeaderSelector extends MouseAdapter{  
   
	private JXTable table;  
    public HeaderEditor editor;  
    private EBICRMModule ebiModule = null;
    
    public HeaderSelector(JXTable t, EBICRMModule mod){  
        table = t;  
        editor = new HeaderEditor(this);  
        ebiModule = mod;
    }  
   
    public void mouseClicked(MouseEvent e)  {  
    	if(e.getButton() == MouseEvent.BUTTON3){
	        JXTableHeader th = (JXTableHeader)e.getSource();  
	        Point p = e.getPoint();  
	        int col = getColumn(th, p);  
	        TableColumn column = th.getColumnModel().getColumn(col);  
	        String oldValue = (String)column.getHeaderValue();  
	        Object value = editor.showEditor(th, col, oldValue); 
	        ((EBIAbstractTableModel)table.getModel()).columnNames[col] =value.toString();
	        ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").removeAllItems();
	        ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
	        
	        for(int i =0; i<((EBIAbstractTableModel)table.getModel()).columnNames.length; i++){
	        	if(i <= ((EBIAbstractTableModel)table.getModel()).columnNames.length){
	        		ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").addItem(((EBIAbstractTableModel)table.getModel()).columnNames[i]);
	        	}
	        }
	        
	        if(!"".equals(value)){
	        	column.setHeaderValue(value);  
	        }
	        th.resizeAndRepaint();  
    	}
    }  
   
    private int getColumn(JXTableHeader th, Point p){  
        TableColumnModel model = th.getColumnModel();  
        int ret = -1;
        for(int col = 0; col < model.getColumnCount(); col++) { 
            if(th.getHeaderRect(col).contains(p)){  
            	ret= col; 
            	break;
            }
        }
        return ret;  
    }  
    
}  