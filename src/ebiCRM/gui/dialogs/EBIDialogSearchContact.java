package ebiCRM.gui.dialogs;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelContactSearch;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;

public class EBIDialogSearchContact {

    private EBICRMModule ebiModule = null;
    public List<JComponent> jSetterComponent = null;
    public List<String> jSetterFieldName = null;
    public int jIndexer = 0;
    private MyTableModelContactSearch tabModel = null;
    private int selRowContact = -1;
    private boolean loadCompleteCompany = false;


    public EBIDialogSearchContact(EBICRMModule main, boolean load) {
        ebiModule = main;
        loadCompleteCompany = load;
        jSetterFieldName = new ArrayList<String>();
        jSetterComponent = new ArrayList<JComponent>();
        ebiModule.guiRenderer.loadGUI("CRMDialog/crmContactSearch.xml");
        tabModel = new MyTableModelContactSearch();
        ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").setModel(tabModel);
        ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }
    
    public void setVisible(){

       ebiModule.guiRenderer.getEBIDialog("searchCRMContact").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_CONTACT"));
       ebiModule.guiRenderer.getVisualPanel("searchCRMContact").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_CONTACT"));

       KeyAdapter adapt = new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
				   if(e.getKeyCode() == KeyEvent.VK_ENTER){
					   createContactBySearchView();
				   }
				}
	  };
	  ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMContact").requestFocus();
      ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMContact").addKeyListener(adapt);

      ListSelectionModel rowSM = ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").getSelectionModel();
      rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    ebiModule.guiRenderer.getButton("applyButton","searchCRMContact").setEnabled(false);
                } else {
                    if(lsm.getMinSelectionIndex() >= 0){
                    	if(lsm.getMinSelectionIndex() > 0){
                    		selRowContact = ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    	}else{
                    		selRowContact = 0;
                    	}
                        if (tabModel.data[selRowContact][0] != null && !tabModel.data[selRowContact][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.guiRenderer.getButton("applyButton","searchCRMContact").setEnabled(true);
                        }
                    }
                }
            }
        });

        new JTableActionMaps(ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                     selRowContact = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selRowContact = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selRowContact = selRow;
                    if (selRowContact < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selRowContact][0].toString())) {
                        return;
                    }

                  loadData();
                }
         });

        ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").rowAtPoint(e.getPoint()) > -1){
                        selRowContact = ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").convertRowIndexToModel(ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").rowAtPoint(e.getPoint()));
                    }

                    if (e.getClickCount() == 2) {
                       loadData();
                    }
                }
        });

        ebiModule.guiRenderer.getButton("searchButton","searchCRMContact").setText(EBIPGFactory.getLANG("EBI_LANG_SEARCH"));
        ebiModule.guiRenderer.getButton("searchButton","searchCRMContact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    createContactBySearchView();
                    ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").changeSelection(0, 0, false, false);
                    ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").requestFocus();
                }
        });

        ebiModule.guiRenderer.getButton("cancelButton","searchCRMContact").setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
        ebiModule.guiRenderer.getButton("cancelButton","searchCRMContact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog("searchCRMContact").setVisible(false);
                }
       });

        ebiModule.guiRenderer.getButton("applyButton","searchCRMContact").setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));
        ebiModule.guiRenderer.getButton("applyButton","searchCRMContact").setEnabled(false);
        ebiModule.guiRenderer.getButton("applyButton","searchCRMContact").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                	loadData();
                }
        });
        
        ebiModule.guiRenderer.showGUI();

    }

    public void createContactBySearchView() {


        try {
            ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMContact").getText()));

            String query = " SELECT * FROM COMPANYCONTACTS LEFT JOIN COMPANY ON " +
                    " COMPANYCONTACTS.COMPANYID=COMPANY.COMPANYID " +
                    " LEFT JOIN COMPANYCONTACTADDRESS ON COMPANYCONTACTADDRESS.CONTACTID = COMPANYCONTACTS.CONTACTID ";


            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement(query);
            ResultSet rs = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);

            int i = 0;
            rs.last();
            if (rs.getRow() > 0) {
                Object[][] da = new Object[rs.getRow()][10];
                rs.beforeFirst();
                while (rs.next()) {

                    da[i][0] = rs.getString("COMPANY.NAME") == null ? "" : rs.getString("COMPANY.NAME");
                    da[i][1] = rs.getString("POSITION") == null ? "" : rs.getString("POSITION");
                    da[i][2] = rs.getString("GENDER") == null ? "" : rs.getString("GENDER");
                    da[i][3] = rs.getString("SURNAME") == null ? "" : rs.getString("SURNAME");
                    da[i][4] = rs.getString("NAME") == null ? "" : rs.getString("NAME");
                    da[i][5] = rs.getString("ZIP") == null ? "" : rs.getString("ZIP");
                    da[i][6] = rs.getString("LOCATION") == null ? "" : rs.getString("LOCATION");
                    da[i][7] = rs.getString("EMAIL") == null ? "" : rs.getString("EMAIL");
                    da[i][8] = rs.getString("COMPANYID") == null ? "" : rs.getString("COMPANYID");
                    da[i][9] = rs.getString("CONTACTID");
                    i++;
                }
                if (da.length != 0) {
                    tabModel.data = da;
                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "","", "", "", "", "", "", ""}};
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                }
            }
           
            tabModel.fireTableDataChanged();
            ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").requestFocus();
            ebiModule.guiRenderer.getTable("searchContactTable","searchCRMContact").changeSelection(0, 0, false, false);
            rs.close();
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }
    }

    public void setValueToComponent(JComponent comp, String field) {
        jSetterFieldName.add(jIndexer, field);
        jSetterComponent.add(jIndexer, comp);
        jIndexer++;
    }
    
    private void loadData(){

        if(selRowContact < 0 || tabModel.data[0][0] == null ||
                tabModel.data[0][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))){
            return;
        }

    	final Runnable load = new Runnable(){
    		
    		public  void run(){
                ebiModule.guiRenderer.getEBIDialog("searchCRMContact").setVisible(false);
    			EBIWinWaiting wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_COMPANY_DATA"));
    			try{ 
    				wait.setVisible(true);
    			    
    				Thread.sleep(500);
    			    
	                if (loadCompleteCompany) {
	                    ebiModule.createUI(Integer.parseInt(tabModel.data[selRowContact][8].toString()),false);
                        if(ebiModule.guiRenderer.existPackage("Contact")){
                            ebiModule.getContactPane().editContact(Integer.parseInt(tabModel.data[selRowContact][9].toString()));
                        }
	                } else {
	                   setContactData(tabModel.data[selRowContact][9].toString(), jSetterFieldName, jSetterComponent);
	                }
    			}catch(Exception ex){
    				ex.printStackTrace();
    			}finally{
    				wait.setVisible(false);
    			}
    			
    		}
    		
    	};
    	
    	Thread startLoad = new Thread(load,"loadCompany");
        startLoad.start();

    }

    /**
     * 
     * @param String rownr
     * @param List<String> nof
     * @param List<JComponent> jcp
     * @return boolean
     */
    public  boolean setContactData(String rownr, List<String> nof, List<JComponent> jcp) {
        boolean ret = true;
        try {

            String query = " SELECT * FROM COMPANYCONTACTS as contact " +
                           " LEFT JOIN COMPANY as company on company.COMPANYID=contact.COMPANYID " +
                             "LEFT JOIN COMPANYCONTACTADDRESS as contactAddess ON contact.CONTACTID= contactAddess.CONTACTID WHERE contact.CONTACTID=? ";
            
            PreparedStatement ps = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement(query);
            ps.setString(1, rownr);
            ResultSet rs = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);

            rs.last();
            if(rs.getRow() > 0){
                rs.beforeFirst();
                rs.next();
                Object[] fieldName = nof.toArray();
                JComponent[] component = jcp.toArray(new JComponent[jcp.size()]);
                for (int i = 0; i < nof.size(); i++) {
                    if (component[i] instanceof JTextField) {
                        JTextField field = (JTextField) component[i];
                        field.setText(rs.getString(fieldName[i].toString().toUpperCase()));
                    }else if (component[i] instanceof JComboBox) {
                        JComboBox field = (JComboBox) component[i];
                        field.setSelectedItem(rs.getString(fieldName[i].toString().toUpperCase()));
                    }else if (component[i] instanceof JTextArea) {
                        JTextArea field = (JTextArea) component[i];
                        field.setText(rs.getString(fieldName[i].toString().toUpperCase()));
                    }else if (component[i] instanceof JXDatePicker) {
                        JXDatePicker field = (JXDatePicker) component[i];
                        field.setDate(rs.getDate(fieldName[i].toString().toUpperCase()));
                    }
                }
                rs.close();
            }
        } catch (SQLException ex) {
        	ex.printStackTrace();
            ret = false;
        }
        return ret;
    }

}

