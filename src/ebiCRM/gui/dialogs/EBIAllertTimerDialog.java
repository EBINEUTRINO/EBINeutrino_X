package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;

import ebiCRM.EBICRMModule;
import ebiCRM.utils.EBIAllertTimer;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;

public class EBIAllertTimerDialog {

	private EBICRMModule ebiModule = null;
	private int companyId=-1;
	private int activityId=0;
    public  String guiSerialID="";
	
	public EBIAllertTimerDialog(EBICRMModule module){
		this.ebiModule = module;
        guiSerialID = ebiModule.guiRenderer.loadGUIPlus("CRMDialog/timerDialog.xml");
		initializeAction();
	}
	
	public void setVisible(int actId, int compId, String taskName, Date dueDate, int duration, String message){
			
			SimpleDateFormat formatter = new SimpleDateFormat("d.M.y HH:mm ");
			companyId = compId;
			activityId = actId;
			ebiModule.guiRenderer.getTextarea("messageViewText", guiSerialID).
			setText(taskName+"\n"+formatter.format(dueDate)+EBIPGFactory.getLANG("EBI_LANG_DURATION")+" "+duration+" Min \n\n"+message);
			ebiModule.guiRenderer.getLabel("pictureAlert", guiSerialID).setIcon(new ImageIcon("images/Warning.png"));
			ebiModule.guiRenderer.showGUI(); 
	}
	
	
	public void initializeAction(){
        ebiModule.guiRenderer.getEBIDialog(guiSerialID).setHaveSerial(true);
		ebiModule.guiRenderer.getButton("closeDialog", guiSerialID).addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				setActionforDialog();
			}
		});
		
		ebiModule.guiRenderer.getButton("openTask", guiSerialID).addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				setActionforDialog();
				   final Runnable waitRunner = new Runnable(){
						 public void run() {

			                  EBIWinWaiting wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_COMPANY_DATA"));

			                  try{

			                       wait.setVisible(true);
                                   if(companyId > 0 ){
                                      if(companyId != ebiModule.companyID){
                                         ebiModule.createUI(companyId,false);
                                      }
                                   }
                                  ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Activity");
			                      ebiModule.getActivitiesPane().editActivity(activityId);

			                  }catch(Exception ex){ex.printStackTrace();}finally{
			                    wait.setVisible(false);
			                  }
			                }
			            };
			            Thread loaderThread = new Thread(waitRunner, "LoaderThread");
			            loaderThread.start();
				
			}
		});
	}
	
	
	private synchronized void setActionforDialog(){
		if(ebiModule.guiRenderer.getCheckBox("setAsDone", guiSerialID).isSelected()){
			
			try {
				ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
				ebiModule.ebiPGFactory.getIEBIDatabase().exec("UPDATE COMPANYACTIVITIES SET TIMERDISABLED=1 WHERE ACTIVITYID="+activityId);
				ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
                EBIAllertTimer.countTask--;
			} catch (SQLException e) {
				EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
				e.printStackTrace();
			}
			
			ebiModule.allertTimer.setUpAvailableTimer();
		}
		
		ebiModule.guiRenderer.getEBIDialog(guiSerialID).setVisible(false);
	}
	
	
	
	
	
}
