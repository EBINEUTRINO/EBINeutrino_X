package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiCRM.EBICRMModule;
import ebiCRM.functionality.EBIExportCSV;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

/**
 * CSV Export Dialog
 *
 */
public class EBIExportDialog  {

    private EBICRMModule ebiModule = null;
    private boolean ret = true;

    public EBIExportDialog(EBICRMModule ebiModule){
       this.ebiModule = ebiModule;
    }
     /**
     * Show and Initialize the CSV export dialog
     */
    public void setVisible(){
        ebiModule.guiRenderer.loadGUI("CRMDialog/exportDataDialog.xml");
        ebiModule.guiRenderer.getEBIDialog("dataExportDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_EXPORT_DIALOG"));
        ebiModule.guiRenderer.getLabel("selectDBTable","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_EXPORT_DATABASE_TABLE"));
        ebiModule.guiRenderer.getLabel("exportPath","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_EXPORT_EXPORT_PATH"));
        ebiModule.guiRenderer.getLabel("delimiter","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_EXPORT_DELIMITER"));
        ebiModule.guiRenderer.getLabel("sortLabel","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SORT"));
        ebiModule.guiRenderer.getProgressBar("exportProgress","dataExportDialog").setString(EBIPGFactory.getLANG("EBI_LANG_EXPORT_VALUE"));
        ebiModule.guiRenderer.getProgressBar("exportProgress","dataExportDialog").setStringPainted(true);
        ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ebiModule.guiRenderer.getComboBox("sortFieldsNameText","dataExportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
        ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").
                                addListSelectionListener(new ListSelectionListener() {
                                          public void valueChanged(ListSelectionEvent evt) {
                                               fillFieldList(); 
                                          }
                                 });

        ebiModule.guiRenderer.getList("fieldListText","dataExportDialog").
                                addListSelectionListener(new ListSelectionListener() {
                                          public void valueChanged(ListSelectionEvent evt) {
                                               fillFieldCombobox(); 
                                          }
                                 });
        fillList();
        ebiModule.guiRenderer.getComboBox("sortCSVText","dataExportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_SORT_ASCENDING_SORT"));
        ebiModule.guiRenderer.getComboBox("sortCSVText","dataExportDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_SORT_DISCENDING_SORT"));

        ebiModule.guiRenderer.getButton("browsButton","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_EXPORT"));
        ebiModule.guiRenderer.getButton("browsButton","dataExportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                 File dir = ebiModule.ebiPGFactory.getOpenDialog(JFileChooser.DIRECTORIES_ONLY);
                 if (dir != null) {
                     ebiModule.guiRenderer.getTextfield("exportPathText","dataExportDialog").setText(dir.getPath());
                 }
             }
        });

        ebiModule.guiRenderer.getButton("exportButton","dataExportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                 if(!validateInputs()){
                     return;
                 }
                 exportThread();
             }
        });

        ebiModule.guiRenderer.getButton("closeExportDialog","dataExportDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLOSE"));
        ebiModule.guiRenderer.getButton("closeExportDialog","dataExportDialog").addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                    ebiModule.guiRenderer.getEBIDialog("dataExportDialog").setVisible(false);
             }
        });

        ebiModule.guiRenderer.showGUI();

    }

   /**
     * Start thread for export a CSV file
     */
    private void exportThread(){

        final Runnable waitRunner = new Runnable(){
			
			 public void run() {

                     ebiModule.guiRenderer.getProgressBar("exportProgress","dataExportDialog").setIndeterminate(true);
                     EBIExportCSV  export = new EBIExportCSV(ebiModule);

                     try {

                         String tableName = ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").getSelectedValue().toString();

                           ret = export.exportCVS(ebiModule.guiRenderer.getTextfield("exportPathText","dataExportDialog").getText(),
                                                        tableName,
                                                        ebiModule.guiRenderer.getList("fieldListText","dataExportDialog").getSelectedValues(),
                                                        ebiModule.guiRenderer.getComboBox("sortFieldsNameText","dataExportDialog").getSelectedItem().toString(),
                                                        ebiModule.guiRenderer.getComboBox("sortCSVText","dataExportDialog").getSelectedIndex() == 0 ? true : false,
                                                        ebiModule.guiRenderer.getTextfield("delimiterText","dataExportDialog").getText());

                     } catch (IOException ex) {
                         ret = false;
                         EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                     }finally{

                        ebiModule.guiRenderer.getProgressBar("exportProgress","dataExportDialog").setIndeterminate(false);
                        if(ret){
                           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_EXPORT_WAS_SUCCESSFULLY")).Show(EBIMessage.INFO_MESSAGE);
                        }else{
                            return;
                        }
                     }
                }
          };

      Thread loaderThread = new Thread(waitRunner, "ExportCSV");
      loaderThread.start();
    }

    private boolean validateInputs() {

        if(ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").isSelectionEmpty()){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_VALUE_FROM_TABLE_LIST")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        
        if(ebiModule.guiRenderer.getList("fieldListText","dataExportDialog").isSelectionEmpty()){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_VALUE_FROM_FIELD_LIST")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        
        if("".equals(ebiModule.guiRenderer.getTextfield("delimiterText","dataExportDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_DELIMITED_SHOULD_NOT_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if("".equals( ebiModule.guiRenderer.getTextfield("exportPathText","dataExportDialog").getText())){
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_EXPORT_DIRECTORY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if("".equals( ebiModule.guiRenderer.getComboBox("sortFieldsNameText","dataExportDialog").getSelectedItem()) ||
            EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals( ebiModule.guiRenderer.getComboBox("sortFieldsNameText","dataExportDialog").getSelectedItem())){

            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_ORDER_FIELD")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    
    
  
    
    

    private void fillList() {
        DefaultListModel listModel = new DefaultListModel();

        try {
            // Gets the database metadata
            DatabaseMetaData dbmd = ebiModule.ebiPGFactory.getIEBIDatabase().getActiveConnection().getMetaData();

            String[] types = {"TABLE"};
            ResultSet resultSet = dbmd.getTables(null, null, "%", types);
                
            while (resultSet.next()) {
                if(!"EBIUSER".equals(resultSet.getString(3).toUpperCase()) && !"MAIL_ACCOUNT".equals(resultSet.getString(3).toUpperCase())
                    && !"MAIL_DELETED".equals(resultSet.getString(3).toUpperCase()) && !"MAIL_INBOX".equals(resultSet.getString(3).toUpperCase())
                    && !"MAIL_OUTBOX".equals(resultSet.getString(3).toUpperCase()) && !"SET_REPORTFORMODULE".equals(resultSet.getString(3).toUpperCase()) && !"SET_REPORTPARAMETER".equals(resultSet.getString(3).toUpperCase())
                    && !"EBIPESSIMISTIC".equals(resultSet.getString(3).toUpperCase()) && !"EBIDATASTORE".equals(resultSet.getString(3).toUpperCase())){
                    listModel.addElement(resultSet.getString(3).toUpperCase());
                }
            }
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }

        ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").setModel(listModel);
     }

    private void fillFieldCombobox(){
       ebiModule.guiRenderer.getComboBox("sortFieldsNameText","dataExportDialog").setModel(new DefaultComboBoxModel(ebiModule.guiRenderer.getList("fieldListText","dataExportDialog").getSelectedValues()));
    }

    private void fillFieldList(){

       DefaultListModel listModel = new DefaultListModel();

          try {
            // Gets the database metadata
            DatabaseMetaData dbmd = ebiModule.ebiPGFactory.getIEBIDatabase().getActiveConnection().getMetaData();

            ResultSet resultSet = dbmd.getColumns(null, null,
                            ebiModule.guiRenderer.getList("databaseTableList","dataExportDialog").getSelectedValue().toString(), null);

            while (resultSet.next()) {
                 listModel.addElement(resultSet.getString("COLUMN_NAME").toUpperCase());
            }

        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }

        ebiModule.guiRenderer.getList("fieldListText","dataExportDialog").setModel(listModel);
    }

     public String[] getColumnNames(ResultSet rs) throws SQLException {
         
        if (rs == null) {
          return null;
        }

        ResultSetMetaData rsMetaData = rs.getMetaData();
        int numberOfColumns = rsMetaData.getColumnCount();

        String[] columnName = new String[numberOfColumns+1];
        for (int i = 1; i < numberOfColumns ; i++) {
           columnName[i-1] = rsMetaData.getColumnName(i);
        }
       return columnName;
  }
}