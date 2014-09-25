package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import ebiCRM.EBICRMModule;
import ebiCRM.functionality.EBIExportCSV;
import ebiCRM.utils.HeaderSelector;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import org.apache.commons.lang.StringEscapeUtils;



/**
 * CSV Import Dialog
 */

public class EBIImportDialog {

    private EBICRMModule ebiModule = null;
    private boolean ret = true;
    private HeaderSelector hSelector = null;
    private EBIAbstractTableModel model=null;
    private int countInsert=0;
    private int countUpdate=0;
    private String[] cols=null;
    private String[][] rows=null;
    
    public EBIImportDialog(EBICRMModule ebiModule){
        this.ebiModule = ebiModule;
    }

    /**
     * Show and Initialize the CSV import dialog
     */

    public void setVisible(){
        ebiModule.guiRenderer.loadGUI("CRMDialog/importDataDialog.xml");

        ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setString(EBIPGFactory.getLANG("EBI_LANG_IMPORT_VALUE"));
        ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setStringPainted(true);
        ebiModule.guiRenderer.getButton("browsButton","dataImportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){

                 File fs = ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);

                 if(fs != null){
                     ebiModule.guiRenderer.getTextfield("importPathText","dataImportDialog").setText(fs.getAbsolutePath());
                 }
             }
        });

        ebiModule.guiRenderer.getButton("importButton","dataImportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                 if(!validateInputs()){
                     return;
                 }

                 importThread();
             }
        });

        ebiModule.guiRenderer.getButton("closeImportDialog","dataImportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                 ebiModule.guiRenderer.getEBIDialog("dataImportDialog").setVisible(false);
             }
        });

        ebiModule.guiRenderer.showGUI();
    }


    private boolean validateInputs() {

        if("".equals(ebiModule.guiRenderer.getTextfield("delimiterText","dataImportDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_DELIMITED_SHOULD_NOT_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if("".equals( ebiModule.guiRenderer.getTextfield("importPathText","dataImportDialog") .getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_IMPORT_FILE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Start thread for import a CSV file
     */
    private void importThread(){

        final Runnable waitRunner = new Runnable(){

			 public void run() {
               
                    ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setIndeterminate(true);
                    final EBIExportCSV  importCSV = new EBIExportCSV(ebiModule);

                    try {
                       ret = importCSV.importCVS(ebiModule.guiRenderer.getTextfield("importPathText","dataImportDialog").getText(),ebiModule.guiRenderer.getTextfield("delimiterText","dataImportDialog").getText());
                    } catch (IOException ex) {
                        EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                    }finally{

                        ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setIndeterminate(false);
                        
                        if(ret){
                        	ebiModule.guiRenderer.getEBIDialog("dataImportDialog").setVisible(false);
                        }
                    }
                    
                    
                    if(ret){
                      SwingUtilities.invokeLater(new Runnable(){
                    	 public void run(){
                    		ebiModule.guiRenderer.loadGUI("CRMDialog/csvSetImport.xml");
                            model = (EBIAbstractTableModel)ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getModel();
                         	
                         	hSelector = new HeaderSelector(ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog"),ebiModule);
                         	ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getTableHeader().addMouseListener(hSelector);  
                         	
                         	model.columnNames = importCSV.columnNames;
                         	
                         	model.data = importCSV.data;
                         	model.fireTableStructureChanged();

                         	fillList();

                            ebiModule.guiRenderer.getCheckBox("removeHeader","csvSetImportDialog").addActionListener(new ActionListener() {
                                 @Override
                                 public void actionPerformed(ActionEvent e) {

                                     if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_LIKE_TO_REMOVE_HEADER")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
                                         ebiModule.guiRenderer.getCheckBox("removeHeader","csvSetImportDialog").setVisible(false);
                                         Object[][] newData = new Object[model.data.length-1][model.columnNames.length];
                                         for(int i=0; i<model.data.length; i++){
                                             if(i >= 1){
                                                 newData[i-1] = model.data[i];
                                             }
                                         }
                                         model.data = newData;
                                         model.fireTableDataChanged();
                                     }
                                 }
                             });
                         	
                         	ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
								   if(ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").getSelectedIndex() > 0){
									   fillFieldList(ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").getSelectedItem().toString());
									   ebiModule.guiRenderer.getCheckBox("insertUpdate", "csvSetImportDialog").setEnabled(true);
									   ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").removeAllItems();
									   ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
								   }
								}
							});
                         	
                         	ebiModule.guiRenderer.getCheckBox("insertUpdate", "csvSetImportDialog").addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									if(((JCheckBox)e.getSource()).isSelected()){
										ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").setEnabled(true);
									}else{
										ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").setEnabled(false);
									}
								}
							});
                         	

                         	ebiModule.guiRenderer.getCheckBox("insertUpdate", "csvSetImportDialog").setEnabled(false);
                         	ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").setEnabled(false);
                         	
                         	ebiModule.guiRenderer.getButton("saveValue", "csvSetImportDialog").addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
                                    List<TableColumn> column = ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getColumns();

                                    Iterator itx = column.iterator();
                                    cols = new String[column.size()];
                                    int i=0;

                                    while(itx.hasNext()){
                                        TableColumn col = (TableColumn)itx.next();
                                        cols[i] = col.getHeaderValue().toString();
                                        i++;
                                    }

                                    rows = new String[ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getRowCount()][ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getColumnCount()];

                                    for(int x=0; x< ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getRowCount(); x++){
                                        for(int j=0; j<ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getColumnCount(); j++){
                                            rows[x][j] = ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getValueAt(x,j).toString();
                                        }
                                    }

								    if(!validateInput()){
									    return;
									}

									ebiModule.guiRenderer.loadGUI("CRMDialog/insertCSVDataDialog.xml");
									ebiModule.guiRenderer.getProgressBar("importProgress","insertCSVDialog").setMaximum(
																ebiModule.guiRenderer.getTable("valueTable", "csvSetImportDialog").getRowCount()-1);
									ebiModule.guiRenderer.getProgressBar("importProgress","insertCSVDialog").setStringPainted(true);
									
									ebiModule.guiRenderer.showGUI(); 
									
									final Runnable run = new Runnable(){
										
										public void run(){
											// DO INSERT OR UPDATE HERE update ui
                                           try{ 
											boolean ret= true;
											ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
											
											for(int i=0; i<model.data.length; i++){
												
												 if(!(ret = setDBCSVTOObject(
														 		ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").getSelectedItem().toString(),
                                                                cols,
                                                                rows[i],ebiModule.guiRenderer.getCheckBox("insertUpdate", "csvSetImportDialog").isSelected(),ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").getSelectedIndex()-1
														 ))){
													 EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_UNSUPORTED_FORMAT_EXCEPTION")).Show(EBIMessage.ERROR_MESSAGE);
													 break;
												 }

												ebiModule.guiRenderer.getLabel("importPath", "insertCSVDialog").setText(model.data[i][0].toString()+" "+model.data[i][1].toString()+" "+model.data[i][2].toString()+".........");
												ebiModule.guiRenderer.getProgressBar("importProgress","insertCSVDialog").setValue(i+1);
											}
											ebiModule.guiRenderer.getEBIDialog("insertCSVDialog").setVisible(false);
											if(ret){
												EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_RECORD_SUCCESSFULLY_IMPORTED")).Show(EBIMessage.INFO_MESSAGE); 
											}
                                           }catch(Exception ex){
                                                   ex.printStackTrace();
                                           }finally {
                                               ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
                                           }
										}
										
									};
									
									final Thread thread = new Thread(run,"Insert CSV to DB");
									thread.start();

								}
							});
                         	
                         	ebiModule.guiRenderer.getButton("closeDialog", "csvSetImportDialog").addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									ebiModule.guiRenderer.getEBIDialog("csvSetImportDialog").setVisible(false);
								}
							});
                         	
                         	ebiModule.guiRenderer.showGUI(); 
                    	 }
                      });	

                    }
              }
          };

      Thread loaderThread = new Thread(waitRunner, "importCSVThread");
      loaderThread.start();
    }

    
    public boolean validateInput(){

    	if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
    				equals(ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").getSelectedItem().toString())){
    		
    		EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_VALUE_FROM_TABLE_LIST")).Show(EBIMessage.ERROR_MESSAGE);
    		return false;
    	}
    	if(ebiModule.guiRenderer.getCheckBox("insertUpdate", "csvSetImportDialog").isSelected() && EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
				equals(ebiModule.guiRenderer.getComboBox("keyText", "csvSetImportDialog").getSelectedItem().toString())){
		
			EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_ASSIGN_RIGHT_DATABASE_FIELD_TO_COLUMN")).Show(EBIMessage.ERROR_MESSAGE);
			return false;
    	}

    	for(int i=0; i<cols.length; i++){
    		if(("Col "+i).equals(cols[i])){
    			EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_ASSIGN_RIGHT_DATABASE_FIELD_TO_COLUMN")).Show(EBIMessage.ERROR_MESSAGE);
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    
    private void fillList() {

        try {
            // Gets the database metadata
            DatabaseMetaData dbmd = ebiModule.ebiPGFactory.getIEBIDatabase().getActiveConnection().getMetaData();
            String[] types = {"TABLE"};
            
            ResultSet resultSet = dbmd.getTables(null, null, "%", types);
            ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
            
            while (resultSet.next()) {
                if(!"EBIUSER".equals(resultSet.getString(3).toUpperCase()) && !"MAIL_ACCOUNT".equals(resultSet.getString(3).toUpperCase())
                        && !"MAIL_DELETED".equals(resultSet.getString(3).toUpperCase()) && !"MAIL_INBOX".equals(resultSet.getString(3).toUpperCase())
                        && !"MAIL_OUTBOX".equals(resultSet.getString(3).toUpperCase()) && !"SET_REPORTFORMODULE".equals(resultSet.getString(3).toUpperCase()) && !"SET_REPORTPARAMETER".equals(resultSet.getString(3).toUpperCase())
                        && !"EBIPESSIMISTIC".equals(resultSet.getString(3).toUpperCase()) && !"EBIDATASTORE".equals(resultSet.getString(3).toUpperCase())){
            	    ebiModule.guiRenderer.getComboBox("dataTableText", "csvSetImportDialog").addItem(resultSet.getString(3).toUpperCase());
                }
            }
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }

     }
    
    
    private void fillFieldList(String table){

        try {
          // Gets the database metadata
          DatabaseMetaData dbmd = ebiModule.ebiPGFactory.getIEBIDatabase().getActiveConnection().getMetaData();

          ResultSet resultSet = dbmd.getColumns(null, null,table, null);
          
          resultSet.last();
          if(resultSet.getRow() > 0){
        	  hSelector.editor.items = new String[resultSet.getRow()];
        	  resultSet.beforeFirst();
        	  int i=0;
	          while (resultSet.next()) {
	        	  hSelector.editor.items[i] = resultSet.getString("COLUMN_NAME").toUpperCase();
	        	  i++;
	          }
          }

      } catch (SQLException ex) {
          EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
      }
    
  }
    
    
    /**
     * Import CSV help method, parse, create, insert or update the sql query
     * @param tableName
     * @param header
     * @param values
     * @param delimiter
     * @return true success otherwise false
     */
    private boolean setDBCSVTOObject(String tableName,String[] header,Object[] values, boolean isUpdate, int pos){

         String insertUpdate;
         boolean ret=true;

             Object[] headerValue = parseSyntax(header, values,isUpdate,pos);
             if(headerValue != null){
            	 
                 if(!Boolean.valueOf(headerValue[2].toString())){
                    insertUpdate  = "INSERT INTO "+tableName.toUpperCase()+" ( "+headerValue[0].toString()+" ) " +
                                                                           " VALUES("+headerValue[1].toString()+")";
                    countInsert++;
                 }else{
                    insertUpdate  = "UPDATE "+tableName.toUpperCase()+" SET "+headerValue[0].toString();
                    countUpdate++;
                 }


                 try{
                     if(!ebiModule.ebiPGFactory.getIEBIDatabase().exec(insertUpdate)){
                        return false;
                     }
                 }catch(Exception ex){
                	 EBIExceptionDialog.getInstance(ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
                     ex.printStackTrace();
                     ret = false;
                 }
                 
             }else{
               ret = false;
             }
        return ret;
    }

    /**
     * Import CSV help method parse and assert SQL syntax
     * @param table
     * @param header
     * @param values
     * @param delimiter
     * @return true success otherwise false
     */
    private Object[] parseSyntax(String[] splH, Object[] splV, boolean insertUpdate, int position){

        String[] returnValue = new String[3];
        boolean ret = true;
        ResultSet resultSet = null;
        
        try{

           boolean isOracle = false;

           if("oracle".equals(EBIPGFactory.DATABASE_SYSTEM)){
               isOracle = true;
           }

           
           // Build a SQL Query
           if(insertUpdate && (!"".equals(splV[position]) || splV[position] != null)){  // Update SQL

                String where="";
                returnValue[0] = "";
                for(int i=0; i<splH.length; i++){
                  if(i == position){
                      where = " WHERE "+splH[i]+"='"+StringEscapeUtils.escapeSql(splV[i].toString())+"'";
                  }else{
                    	  try{
                    		  returnValue[0] +=  splH[i]+"="+Integer.parseInt(splV[i].toString());
                    	  }catch(NumberFormatException ex){
                    		  returnValue[0] +=  splH[i]+"='"+StringEscapeUtils.escapeSql(splV[i] == null ? "" : splV[i].toString())+"'"; 
                    	  }
	                    	  
                          if(i < splH.length-1){
                        	   
                              returnValue[0] += ",";
                          }
                  }

                }               
                returnValue[0] += where;

           } else{     // Insert SQL

              returnValue[0] = "";
              for(int i=0; i<splH.length; i++){
            	  if(i != position){
	                  returnValue[0] += splH[i];
	                  if(i < splH.length-1){
	                      returnValue[0] += ",";
	                  }
            	  }
              }

              returnValue[1] = "";
              for(int i=0; i<splV.length; i++){
            	 if(i != position){
	            	  try{
	            		  returnValue[1] += Integer.parseInt(splV[i].toString()); 
	            	  }catch(NumberFormatException ex){
	            		  returnValue[1] += "'"+StringEscapeUtils.escapeSql(splV[i] == null ? "" : splV[i].toString())+"'";
	            	  }
	                  if(i < splV.length-1){
	                      returnValue[1] += ",";
	                  }
            	 }
              }
           }
           
           returnValue[0] = returnValue[0];

           if(returnValue[1]!=null){
               returnValue[1] = returnValue[1];
           }
           returnValue[2] = String.valueOf((insertUpdate && (!"".equals(splV[position]) || splV[position] != null)));

        }catch (Exception ex) {
            ex.printStackTrace();
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_UNSUPORTED_FORMAT_EXCEPTION")).Show(EBIMessage.ERROR_MESSAGE);
            ret = false;
        }finally{
            try {
                if(resultSet!= null){resultSet.close();}
            } catch (SQLException ex) {
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }
            if(ret == false){
                returnValue = null;
            }
        }

       return returnValue;
    }

}
