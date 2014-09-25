package ebiCRM.gui.dialogs;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.sort.RowFilters;

import ebiCRM.EBICRMModule;
import ebiCRM.table.models.MyTableModelSearchCompany;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;

public class EBIDialogSearchCompany {

	public int selRowSearch =0;
	private MyTableModelSearchCompany tableModel = null;
	private EBICRMModule ebiModule = null;
	private boolean isHierarchies = false;
    private boolean isSummary = false;
    public static boolean threadFinish = false;
    private HashMap<String,Object> toMap = null;

	/**
	 * This is the default constructor
	 */
	public EBIDialogSearchCompany(EBICRMModule main,boolean isH,boolean isSummary) {
        ebiModule = main;
		isHierarchies = isH;
        this.isSummary = isSummary;
		threadFinish = false;
		tableModel = new MyTableModelSearchCompany();
		ebiModule.guiRenderer.loadGUI("CRMDialog/crmCompanySearch.xml");
        setVisible();
    }

    public EBIDialogSearchCompany(EBICRMModule main,HashMap<String,Object> map) {
        ebiModule = main;
        threadFinish = false;
        tableModel = new MyTableModelSearchCompany();
        toMap = map;
        ebiModule.guiRenderer.loadGUI("CRMDialog/crmCompanySearch.xml");

        setVisible();
    }

    /**
     * Show and Initialize the Company search dialog
     */

    private void setVisible(){
       ebiModule.guiRenderer.getEBIDialog("searchCRMCompany").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_COMPANY"));
       ebiModule.guiRenderer.getVisualPanel("searchCRMCompany").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_COMPANY"));

       ebiModule.guiRenderer.getButton("compSearchsearchButton","searchCRMCompany").setText(EBIPGFactory.getLANG("EBI_LANG_SEARCH"));
	   ebiModule.guiRenderer.getButton("compSearchsearchButton","searchCRMCompany").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                searchCompany();
            }
		});

       ebiModule.guiRenderer.getButton("compSearchcancelButton","searchCRMCompany").setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
	   ebiModule.guiRenderer.getButton("compSearchcancelButton","searchCRMCompany").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) { 
                    ebiModule.guiRenderer.getEBIDialog("searchCRMCompany").setVisible(false);
				}
		});

       ebiModule.guiRenderer.getButton("compSearchapplyButton","searchCRMCompany").setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));
	   ebiModule.guiRenderer.getButton("compSearchapplyButton","searchCRMCompany").setEnabled(false);
	   ebiModule.guiRenderer.getButton("compSearchapplyButton","searchCRMCompany").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					applySearch();
                }
		});

      KeyAdapter adapt = new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
				   if(e.getKeyCode() == KeyEvent.VK_ENTER){
					   searchCompany();
				   }
				}
	  };

      ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMCompany").requestFocus();
      ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMCompany").addKeyListener(adapt);

      ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").setModel(tableModel);
      ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    ebiModule.guiRenderer.getButton("compSearchapplyButton","searchCRMCompany").setEnabled(false);
                } else {
                    if(lsm.getMinSelectionIndex() > 0){
                        selRowSearch = ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    if (!tableModel.data[selRowSearch][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("compSearchapplyButton","searchCRMCompany").setEnabled(true);
                    }
                }
            }
        });

         new JTableActionMaps(ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                     selRowSearch = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selRowSearch = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selRowSearch = selRow;
                    if (selRowSearch < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tableModel.data[selRowSearch][0].toString())) {
                        return;
                    }


                   applySearch();
                }
         });

         ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {

                   if(ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").rowAtPoint(e.getPoint()) != -1){
                        selRowSearch = ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").convertRowIndexToModel(ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").rowAtPoint(e.getPoint()));
                   }

                   if (e.getClickCount() == 2) {
                        applySearch();
                   }
                }
            });


        ebiModule.guiRenderer.showGUI();


    }

	private void searchCompany(){

        ebiModule.guiRenderer.getEBIDialog("searchCRMCompany").setCursor(new Cursor(Cursor.WAIT_CURSOR));

        ResultSet set = null;
        try{
         ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").setRowFilter(RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "searchCRMCompany").getText()));
         PreparedStatement ps2 = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT COMPANYID,COMPANYNUMBER,CUSTOMERNR,BEGINCHAR,NAME,CATEGORY,COOPERATION,QUALIFICATION,ISLOCK FROM COMPANY");

         set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps2);

            if(set != null){
                set.last();
                if(set.getRow() > 0){
                   tableModel.data = new Object[set.getRow()][8];
                   set.beforeFirst();
                   int i= 0;
                   while(set.next()){
                        String bChar = set.getString("BEGINCHAR") == null ? "" : set.getString("BEGINCHAR");
                        String coNr  =   set.getString("COMPANYNUMBER") == null ? "" : set.getString("COMPANYNUMBER");

                        tableModel.data[i][0] = bChar+coNr;
                        tableModel.data[i][1] = set.getString("CUSTOMERNR") == null ? "" : set.getString("CUSTOMERNR");
                        tableModel.data[i][2] = set.getString("NAME") == null ? "" : set.getString("NAME");
                        tableModel.data[i][3] = set.getString("CATEGORY") == null ? "" : set.getString("CATEGORY");
                        tableModel.data[i][4] = set.getString("COOPERATION") == null ? "" : set.getString("COOPERATION");
                        tableModel.data[i][5] = set.getString("QUALIFICATION") == null ? "" : set.getString("QUALIFICATION");
                        tableModel.data[i][6] = set.getInt("ISLOCK") == 0 ? EBIPGFactory.getLANG("EBI_LANG_NO") : EBIPGFactory.getLANG("EBI_LANG_YES");
                        tableModel.data[i][7] = set.getString("COMPANYID");
                        i++;
                   }

                    tableModel.fireTableDataChanged();
                    ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").changeSelection(0, 0, false, false);
                    ebiModule.guiRenderer.getTable("searchCompanyTable","searchCRMCompany").requestFocus();

                }else{
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_NO_COMPANY_FOUND")).Show(EBIMessage.INFO_MESSAGE);
                }
            }
            ebiModule.guiRenderer.getEBIDialog("searchCRMCompany").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{

            if(set != null){
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

	private void applySearch(){

        if(tableModel.data[0][0] == null ||
                    EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tableModel.data[0][0].toString())){
            return;
        }

        if(toMap == null){
             final Runnable waitRunner = new Runnable(){
                 public void run() {
                      EBIWinWaiting wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_COMPANY_DATA"));

                      try{
                        wait.setVisible(true);

                            if(isHierarchies == false && isSummary == false){
                                if(ebiModule.createUI(Integer.parseInt(tableModel.data[selRowSearch][7].toString()),false)){
                                    threadFinish = true;
                                }
                            }else if(isHierarchies == true){
                                if(ebiModule.getCompanyPane().setHierarchies(tableModel.data[selRowSearch][1].toString(),
                                                                Integer.parseInt(tableModel.data[selRowSearch][7].toString()))){
                                    threadFinish = true;
                                }
                            }else if(isSummary){
                              ebiModule.getSummaryPane().setCompanyText(tableModel.data[selRowSearch][1].toString());
                              threadFinish = true;
                            }

                      }catch(Exception ex){ex.printStackTrace();}finally{
                        wait.setVisible(false);
                      }
                    }
             };

            Thread loaderThread = new Thread(waitRunner, "LoaderThread");
            loaderThread.start();
         }else{
            toMap.put("ID",Integer.parseInt(tableModel.data[selRowSearch][7].toString()));
            toMap.put("Name",tableModel.data[selRowSearch][2].toString());
        }
        ebiModule.guiRenderer.getEBIDialog("searchCRMCompany").setVisible(false);
    }

}