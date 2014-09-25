package ebiCRM.utils;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Timer;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.dialogs.EBIAllertTimerDialog;
import ebiNeutrino.core.GUIRenderer.EBIButton;

public class EBICRMTaskItem extends EBIButton {

	private int id=0;
	private int companyId=0;
	private String taskName = "";
	private String taskMessage = "";
	private Date dueDate = null;
	private int duration =0;
	private Timer timer = null;
	private EBICRMModule ebiModule = null;
	
	public EBICRMTaskItem(EBICRMModule module){
		super();
		setOpaque(true);
		ebiModule = module;
		setCorner(2,2);
		
		addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				EBIAllertTimerDialog dialog = new EBIAllertTimerDialog(ebiModule);
				dialog.setVisible(getId(),getCompanyId(), getTaskName(), getDueDate(), getDuration(), getTaskMessage());
				
			}
		});
		
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getCompanyId() {
		return companyId;
	}


	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}


	public String getTaskName() {
		return taskName;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
		setText(taskName);
	}


	public String getTaskMessage() {
		return taskMessage;
	}


	public void setTaskMessage(String taskMessage) {
		this.taskMessage = taskMessage;
	}


	public Date getDueDate() {
		return dueDate;
	}


	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}


	public int getDuration() {
		return duration;
	}


	public void setDuration(int duration) {
		this.duration = duration;
	}


	public Timer getTimer() {
		return timer;
	}


	public void setTimer(Timer timer) {
		this.timer = timer;
	}

    public void setBackgroundColor(String col){
        if(!"".equals(col) && col != null){
            String [] c = col.split(",");
            if(c.length > 2){
                Color co = new Color(Integer.parseInt(c[0]),Integer.parseInt(c[1]),Integer.parseInt(c[2]));
                setColor(co);

                if(Integer.parseInt(c[0]) < 90 && Integer.parseInt(c[1]) < 90 && Integer.parseInt(c[2]) < 90){
                    setForeColor(new Color(255,255,255));
                }else{
                    setForeColor(new Color(0,0,0));
                }
            }else{
                setBackground(Color.red);
                setForeColor(Color.white);
            }
        }else{
            setBackground(Color.red);
            setForeColor(Color.white);
        }
    }
    

	
}
