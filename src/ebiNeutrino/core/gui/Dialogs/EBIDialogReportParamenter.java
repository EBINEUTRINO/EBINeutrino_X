package ebiNeutrino.core.gui.Dialogs;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.swing.*;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.utils.EBIConstant;
import org.jdesktop.swingx.JXDatePicker;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class EBIDialogReportParamenter extends EBIDialogExt {

	private static final long serialVersionUID = 1L;
	private EBIVisualPanelTemplate jContentPane = null;
	private JButton jButtonOk = null;
	private JButton jButtonCancel = null;
	private Map<String,Object> param = null;
	private int repoID = -1; 
	public boolean aborted= false;
	public boolean haveParam = false;
    private EBIPGFactory _ebifunction = null;
    private JTextField[] field  = null;
    private JTextArea[] textArea = null;
    private JXDatePicker[] picker = null;
    private JLabel[] label = null;
    private int x =2;
    private int y =80;
    private int w =130;
    private int h =20;
    private int x1 =140;
    private int w1 =300;
    private int intText =0;
    private int initString =0;
    private int intDate =0;
    private String reportType ="";
    private boolean isDateTime = false;
    private EBIReportSelection sysSel = null;
   
    
	public EBIDialogReportParamenter(EBIReportSelection selection, int RepID , String repType) {
        super(selection.ebiPGFactory.getMainFrame());
        sysSel =selection;
        this.setName("EBIDialogReportParamenter");
        this.storeLocation(true);
		this.setModal(true);
		this.setResizable(false);
        reportType = repType;
		_ebifunction = selection.ebiPGFactory;
		repoID = RepID;
        param = selection.repsys.map;
        isDateTime = false;
		initialize();
        jContentPane.setBackgroundColor(new Color(200,200,200));
	}
	
	public boolean checkForParam(){
        ResultSet set = null;
		try{
			resolveComponentType();

            PreparedStatement ps1 = _ebifunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM SET_REPORTPARAMETER WHERE  REPORTID=? ORDER BY POSITION ASC ");
            ps1.setInt(1,repoID);
            set = _ebifunction.getIEBIDatabase().executePreparedQuery(ps1);

			set.last();
			if(set.getRow() > 0){ // This report have no parameter
				 
			  set.beforeFirst();
			  int i =0;
			  while(set.next()){
				  InitXComponent(i,set.getString("PARAMNAME"),set.getString("PARAMTYPE"));
			      i++;
			  }

			}else{
				return false;
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
		return true;
	}
	
	private void InitXComponent(int i,String name,String param){
	
		// Init Label 
		label[i] = new JLabel();
		label[i].setBounds(x, y, w, h);
		label[i].setText(name);
        label[i].setHorizontalAlignment(SwingConstants.RIGHT);
        label[i].setFont(new Font("Dialog", Font.PLAIN, 10));
        jContentPane.add(label[i], null);

        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
        reportType = reportType.replace(" ","_");

		if(param.equals("Date") || param.equals("DateTime")){
			picker[intDate] = new JXDatePicker();
			picker[intDate].setBounds(x1, y, w1, h);
			picker[intDate].setName(name);
			picker[intDate].setFormats(EBIPGFactory.DateFormat);

            picker[intDate].getEditor().setText(properties.getValue(name+"_saved"));
            picker[intDate].setDate(_ebifunction.getStringToDate(properties.getValue(reportType+"_"+name+"_saved")));

            if(param.equals("DateTime")){
              isDateTime = true;
            }

            jContentPane.add(picker[intDate], null);
			intDate++;
		}else if(param.equals("Text")){
			textArea[intText] = new JTextArea();

			textArea[intText].setBounds(x1, y, w1+100, h+140);
			textArea[intText].setName(name);
            textArea[intText].setText(properties.getValue(reportType+"_"+name+"_saved"));
            
            JScrollPane pane = new JScrollPane();
            pane.setViewportView(textArea[intText]);
            pane.setBounds(x1, y, w1+100, h+140);
            y = y + textArea[intText].getHeight() - 20;
            jContentPane.add(pane, null);
			intText++;
		}else {
			field[initString] = new JTextField();
			field[initString].setBounds(x1, y, w1, h);
			field[initString].setName(name+"#"+param);
			field[initString].setText(properties.getValue(reportType+"_"+name+"_saved"));
            jContentPane.add(field[initString], null);
			initString++;
		}

        this.setSize(x+x1+w1+w+30,y+h+85);
		jButtonOk.setBounds(getWidth()-jButtonOk.getWidth()-40-jButtonCancel.getWidth(),y+h+10, jButtonOk.getWidth(), jButtonOk.getHeight());
		jButtonCancel.setBounds(jButtonOk.getX()+jButtonOk.getWidth()+5, y+h+10, jButtonCancel.getWidth(), jButtonCancel.getHeight());
		y+=25;
	}
	
	
	private void addParameter(){
		EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();

		if(field.length > 0){
            
			for(int i=0; i<field.length; i++){
                
			    String name = field[i].getName().substring(0 , field[i].getName().indexOf('#'));
			    String type = field[i].getName().substring(field[i].getName().indexOf('#')+1);
                
			    if(type.equals("Double")){
			      this.param.put(name,Double.parseDouble(field[i].getText()));
			    }else if(type.equals("Integer")){
			      this.param.put(name,Integer.parseInt(field[i].getText()));
			    }else{
			      this.param.put(name,field[i].getText());
			    }

                properties.setValue(reportType.replace(" ","_")+"_"+name+"_saved", field[i].getText());
                properties.saveProperties();
			}
		}

        if(textArea.length > 0){
			for(int i=0; i<textArea.length; i++){
			    this.param.put(textArea[i].getName(),textArea[i].getText());
                properties.setValue(reportType.replace(" ","_")+"_"+textArea[i].getName().replace(" ","_")+"_saved", textArea[i].getText());
                properties.saveProperties();
			}
		}

		if(picker.length > 0){
			for(int j=0; j<picker.length; j++){
				if(isDateTime){
                    Date dtVal = picker[j].getDate();

                    Calendar calendar2 = new GregorianCalendar();
                    calendar2.setTime(dtVal);
                    calendar2.set(Calendar.HOUR,24);
                    calendar2.set(Calendar.MINUTE,0);
                    dtVal = calendar2.getTime();

                    this.param.put(picker[j].getName(), new Timestamp(dtVal.getTime()));
                }else{
				    this.param.put(picker[j].getName(), picker[j].getDate());
                }
                properties.setValue(reportType.replace(" ","_")+"_"+picker[j].getName().replace(" ","_")+"_saved",  _ebifunction.getDateToString(picker[j].getDate()));
                properties.saveProperties();
			}
		}

		if(!this.param.isEmpty()){
			this.haveParam = true;
		}else{
			this.haveParam = false;
		}
		
	}
	
	private void resolveComponentType(){
		int countString  = 0;
        int countText   = 0;
		int countPicker = 0;
		int countLabel  = 0;
		ResultSet set = null;
        try{
             PreparedStatement ps1 = _ebifunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM SET_REPORTPARAMETER WHERE REPORTID=? ORDER BY POSITION ASC ");
             ps1.setInt(1,repoID);
             set = _ebifunction.getIEBIDatabase().executePreparedQuery(ps1);

            while(set.next()){
                if("Date".equals(set.getString("PARAMTYPE")) || "DateTime".equals(set.getString("PARAMTYPE"))){
                    countPicker++;
                }else if("Text".equals(set.getString("PARAMTYPE"))){
                    countText++;
                }else{
                    countString++;
                }
                countLabel++;
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

		field  = new JTextField[countString];
        textArea  = new JTextArea[countText];
		picker = new JXDatePicker[countPicker];
		label = new JLabel[countLabel];
        
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(443, 206);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private EBIVisualPanelTemplate getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel1=new JLabel();
			jLabel1.setBounds(new Rectangle(13, 11, 375, 31));
			jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_REPORT_INSERT_PARAMETER"));
			jContentPane = new EBIVisualPanelTemplate();
            jContentPane.setClosable(true);
            jContentPane.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_REPORT_PARAMETER"));
            jContentPane.setModuleIcon(EBIConstant.ICON_REPORT);
            jContentPane.setEnableChangeComponent(false);
			jContentPane.setLayout(null);
			jContentPane.add(getJButtonOk(), null);
			jContentPane.add(getJButtonCancel(), null);
			jContentPane.add(jLabel1, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jButtonOk	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonOk() {
		if (jButtonOk == null) {
			jButtonOk = new JButton();
			jButtonOk.setBounds(new Rectangle(167, 132, 126, 29));
			jButtonOk.setText(EBIPGFactory.getLANG("EBI_LANG_OK"));
			jButtonOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addParameter();

                    final Runnable run = new Runnable(){
                        public void run(){
                            if("".equals(sysSel.reportCategory)){
                                sysSel.repsys.useReportSystemExt(sysSel.repsys.map);
                            }else{
                                if(sysSel.repsys.eMailRecord){
                                    sysSel.repsys.useReportSystemExt(sysSel.repsys.map, sysSel.reportCategory, sysSel.repsys.fileName, sysSel.repsys.showWindow,sysSel.repsys.eMailRecord,sysSel.repsys.strRecs);
                                }else{
                                    sysSel.repsys.useReportSystemExt(sysSel.repsys.map, sysSel.reportCategory, sysSel.repsys.fileName);
                                }
                            }
                        }
                    };

                    Thread thread = new Thread(run,"Load Reports");
                    thread.start();
					setVisible(false);
				}
			});
		}
		return jButtonOk;
	}

	/**
	 * This method initializes jButtonCancel	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setBounds(new Rectangle(296, 132, 126, 29));
			jButtonCancel.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
			jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					aborted= true;
					setVisible(false);
				}
			});
		}
		return jButtonCancel;
	}

}