package ebiNeutrino.core.settings;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBIListSettingName extends JPanel {

	public JList jListnames = null;
	public DefaultListModel myListmodel = null;
    public JPanel cpanel = null;
    public EBIMain ebiMain = null;
    public EBISystemSettingPanel einstp = null;
    public EBIReportSetting report =null;
    
	/**
	 * This is the default constructor
	 */
	public EBIListSettingName(EBIMain main,JPanel start) {
		super();
		ebiMain = main;
		cpanel = start;
      
		myListmodel = new DefaultListModel();

        EBIListItem item0 = new EBIListItem(EBIConstant.ICON_NEW_BLANK, EBIPGFactory.getLANG("EBI_LANG_START")),
        			item1 = new EBIListItem(EBIConstant.ICON_SETTING, EBIPGFactory.getLANG("EBI_LANG_SYSTEM_SETTING")),
                    item2 = new EBIListItem(EBIConstant.ICON_REPORT, EBIPGFactory.getLANG("EBI_LANG_REPORT_SETTING"));


        myListmodel.addElement(item0);
        myListmodel.addElement(item1);
        myListmodel.addElement(item2);
        
		initialize();
		jListnames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jListnames.setSelectedIndex(0);
		setStart();
    		 
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(300, 200);
		this.add(getJListnames(), java.awt.BorderLayout.CENTER);
	}

	/**
	 * This method initializes jListnames	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListnames() {
		if (jListnames == null) {
			jListnames = new JList(myListmodel);
			jListnames.setCellRenderer(new EBIListCellRenderer());
			jListnames.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mousePressed(java.awt.event.MouseEvent e) {
					int index = jListnames.locationToIndex(e.getPoint());
					 
					switch(index){
					  case 0:
							 setStart();
						  break;
					  case 1:
						   cpanel.removeAll();
						   cpanel.updateUI();
						   einstp = new EBISystemSettingPanel(ebiMain);
						   cpanel.add(einstp,  java.awt.BorderLayout.CENTER);
						   einstp.updateUI();
						  
						  break;
					  case 2:
						    cpanel.removeAll();
	                    	cpanel.updateUI();
	                    	report = new EBIReportSetting(ebiMain); 
	                    	cpanel.add(report, java.awt.BorderLayout.CENTER);
	                    	report.updateUI();
						  break;
	                      
						  default:
							setStart();
						   break;
					}
				}
			});
		}
		return jListnames;
	}
	
    public void setStart(){
    	cpanel.removeAll();
    	cpanel.updateUI();
    	EBISystemSettingStart start = new EBISystemSettingStart(ebiMain);
    	cpanel.add(start, java.awt.BorderLayout.CENTER);
    	start.updateUI();		 
    }
}