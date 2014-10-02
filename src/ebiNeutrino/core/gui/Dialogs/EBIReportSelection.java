package ebiNeutrino.core.gui.Dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import ebiNeutrino.core.EBIReportSystem;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIDatabase;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIReportSelection extends EBIDialogExt {

	private EBIVisualPanelTemplate jContentPane = null;
	private JList jListReports = null;
	private JButton jButtonAbbrechen = null;
	private JButton jButtonOk = null;
	private IEBIDatabase data = null;
	public String nameReport = "-1";
	public boolean showAttribute = true;
    public String reportCategory = "";
    public int reportID = -1;
	private JScrollPane listScroll = null;
	private JComboBox jComboBoxCategory = null;
	private final DefaultListModel model = new DefaultListModel();
	private int[] id = null;
	public boolean cancelled = true;
    public EBIPGFactory ebiPGFactory = null;
    public EBIReportSystem repsys = null;


    /**
	 * This is the default constructor
	 */
	public EBIReportSelection(EBIReportSystem sys) {
		super(sys.ebiPGFunction.getMainFrame());
        ebiPGFactory = sys.ebiPGFunction;
        repsys = sys;
        setName("EBIReportSelection");
		setModal(true);
		setResizable(false);
		storeLocation(true);
		storeSize(true);
		data = ebiPGFactory.getIEBIDatabase();
		initialize();
		fillCategoryComboBox();
        jContentPane.setBackgroundColor(new Color(200,200,200));
        jComboBoxCategory.setSelectedIndex(1);
	}
	
	public EBIReportSelection(EBIReportSystem sys, String reportCategory) {
		super(sys.ebiPGFunction.getMainFrame());
        ebiPGFactory = sys.ebiPGFunction;
        repsys = sys;
        setName("EBIReportSelection");
		setModal(true);
		setResizable(false);
		storeLocation(true);
		storeSize(true);
		this.reportCategory = reportCategory;
		data = ebiPGFactory.getIEBIDatabase();
		initialize();
        this.jComboBoxCategory.addItem(ebiPGFactory.convertReportIndexToCategory(Integer.parseInt(reportCategory)));
		listAReports(reportCategory);
		jListReports.setSelectedIndex(0);
        jContentPane.setBackgroundColor(new Color(200,200,200));
		getReport(id[jListReports.getSelectedIndex()]);

	}
	
	private void listAReports(String cat){
	   ResultSet set = null;
		try{
            this.model.clear();

            PreparedStatement ps1 = data.initPreparedStatement("SELECT REPORTCATEGORY,REPORTNAME,IDREPORTFORMODULE,ISACTIVE FROM SET_REPORTFORMODULE" +
                                          " WHERE REPORTCATEGORY=? and ISACTIVE <> 0 ORDER BY REPORTNAME ");
            ps1.setString(1,cat);
            set = data.executePreparedQuery(ps1);
            set.last();
            id = new int[set.getRow()];
            set.beforeFirst();
            int i =0;
            while(set.next()){
              this.model.add(i, set.getString("REPORTNAME"));
              id[i] = set.getInt("IDREPORTFORMODULE");
              i++;
            }

            if(this.model.getSize() > 0){
                this.jListReports.setSelectedIndex(0);
            }

		}catch(SQLException ex){
			EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
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

	private void getReport(int id){
        ResultSet set = null;
	  try {

          PreparedStatement ps1 = data.initPreparedStatement("SELECT * FROM SET_REPORTFORMODULE WHERE IDREPORTFORMODULE=?");
          ps1.setInt(1,id);
          set = data.executePreparedQuery(ps1);
		  set.last();
		  if(set.getRow() > 0){
			   set.beforeFirst();	  
			   set.next();
			   this.nameReport = set.getString("REPORTFILENAME").replaceAll(" ", "_");
			   this.showAttribute = set.getInt("SHOWASPDF") == 0 ? false : true;
			   this.reportID   = set.getInt("IDREPORTFORMODULE");
			   this.reportCategory = set.getString("REPORTCATEGORY");
		  }
	  }catch(SQLException ex){
		  EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
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
	
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(443, 278);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel1= new JLabel();
			jLabel1.setBounds(new Rectangle(12, 20, 75, 24));
			jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
			JLabel jLabel= new JLabel();
			jLabel.setBounds(new Rectangle(8, 67, 301, 21));
			jLabel.setText(EBIPGFactory.getLANG("EBI_REPORT_SELECTION_TEXT"));
			jContentPane = new EBIVisualPanelTemplate();
            jContentPane.setModuleTitle(EBIPGFactory.getLANG("EBI_REPORT_SELECTION"));
            jContentPane.setEnableChangeComponent(false);
            jContentPane.setModuleIcon(EBIConstant.ICON_REPORT);
			jContentPane.setClosable(true);
			jContentPane.add(getlistScrollPane(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(getJButtonAbbrechen(), null);
			jContentPane.add(getJButtonOk(), null);
			jContentPane.add(jLabel1, null);
			jContentPane.add(getJComboBox(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jListReports	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListReports() {
		if (jListReports == null) {
			jListReports = new JList(model);
			jListReports.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				
				@Override
				public void mousePressed(MouseEvent e) {}
				
				@Override
				public void mouseExited(MouseEvent e) {}
				
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					
					if(e.getClickCount() > 1){
						 createReport();
					}
				}
			});
            jListReports.registerKeyboardAction(new ActionListener(){
               public void actionPerformed(ActionEvent ev) {
                   createReport();
               }
            },KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);

		}
		return jListReports;
	}
	
	
	private JScrollPane getlistScrollPane(){
		if(this.listScroll == null ){
			listScroll = new JScrollPane();
			listScroll.setBounds(new Rectangle(8, 92, 419, 108));
			listScroll.setViewportView(getJListReports());
		}
		return listScroll;
	}
	

	/**
	 * This method initializes jButtonAbbrechen	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAbbrechen() {
		if (jButtonAbbrechen == null) {
			jButtonAbbrechen = new JButton();
			jButtonAbbrechen.setBounds(new Rectangle(323, 212, 102, 25));
			jButtonAbbrechen.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
			jButtonAbbrechen.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setVisible(false);
					repsys.report[0] = null;
					
				}
			});
		}
		return jButtonAbbrechen;
	}

	/**
	 * This method initializes jButtonOk	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton();
			jButtonOk.setBounds(new Rectangle(229, 212, 88, 25));
			jButtonOk.setText(EBIPGFactory.getLANG("EBI_LANG_OK"));
			jButtonOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
                    cancelled=false;
					createReport();
				}
			});
		}
		return jButtonOk;
	}


    public void createReport(){
        if(!validateInput()){
            return;
        }
        	getReport(id[jListReports.getSelectedIndex()]);
            repsys.report[0] = nameReport;
            repsys.report[1] = showAttribute;

            EBIDialogReportParamenter repParam = new EBIDialogReportParamenter(this,reportID,reportCategory);

            if(repParam.checkForParam() == true){
                repParam.setVisible(true);
            }else{
                  final Runnable run = new Runnable(){
                      public void run(){
                        if("".equals(reportCategory)){
                            repsys.useReportSystemExt(repsys.map);
                        }else{
                            if(repsys.eMailRecord){
                                repsys.useReportSystemExt(repsys.map, reportCategory, repsys.fileName, repsys.showWindow,repsys.eMailRecord,repsys.strRecs);
                            }else{
                                repsys.useReportSystemExt(repsys.map, reportCategory, repsys.fileName);
                            }
                        }
                      }
                  };
                
                Thread thread = new Thread(run,"Load Reports");
                thread.start();
            }

        setVisible(false);
    }

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox() {
		if (jComboBoxCategory == null) {
			jComboBoxCategory = new JComboBox();
			jComboBoxCategory.setBounds(new Rectangle(88, 20, 294, 25));
			jComboBoxCategory.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

                    listAReports(ebiPGFactory.convertReportCategoryToIndex(jComboBoxCategory.getSelectedItem().toString()));

				}
			});
		}
		return jComboBoxCategory;
	}
	
	private void fillCategoryComboBox(){
        ResultSet set = null;
		  try {
              PreparedStatement ps1 = data.initPreparedStatement("SELECT DISTINCT(REPORTCATEGORY) FROM SET_REPORTFORMODULE");
              set = data.executePreparedQuery(ps1);

			   set.last();
			   if(set.getRow() > 0){
			        set.beforeFirst();
				    this.jComboBoxCategory.addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
				    while(set.next()){
				    	this.jComboBoxCategory.addItem(ebiPGFactory.convertReportIndexToCategory(Integer.parseInt(set.getString("REPORTCATEGORY"))));
				    }
			   }

			  }catch(SQLException ex){
				  EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
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
	
	private boolean validateInput(){

        if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(jComboBoxCategory.getSelectedItem().toString())){
           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SELECT_CATEGORY")).Show(EBIMessage.ERROR_MESSAGE);
           return false;
        }

		if(jListReports.getSelectedIndex() == -1){
			EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_SELECT_VALUE_FROM_LIST")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
		}

		return true;
	}

}
