package ebiCRM.gui.dialogs;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import ebiCRM.EBICRMModule;
import ebiCRM.utils.EBICRMHistoryDataUtil;
import ebiNeutrinoSDK.EBIPGFactory;


public class EBICRMHistoryView {

	private List<EBICRMHistoryDataUtil> list = null;
    private EBICRMModule ebiModule = null;


	public EBICRMHistoryView(List<EBICRMHistoryDataUtil> list, EBICRMModule module) {
		this.list = list;
        ebiModule = module;
        ebiModule.guiRenderer.loadGUI("CRMDialog/crmHistoryDialog.xml");
	}

    public void setVisible(){

        ebiModule.guiRenderer.getEBIDialog("historyViewDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_HISTORY_VIEW"));
        ebiModule.guiRenderer.getVisualPanel("historyViewDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_HISTORY_VIEW"));
        ebiModule.guiRenderer.getLabel("history","historyViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_HISTORY"));
        ebiModule.guiRenderer.getComboBox("historyText","historyViewDialog").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

        ebiModule.guiRenderer.getEditor("viewhistoryText","historyViewDialog").setEditable(false);
        ebiModule.guiRenderer.getEditor("viewhistoryText","historyViewDialog").setBackground(EBIPGFactory.systemColor);

        ebiModule.guiRenderer.getButton("viewHistory","historyViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_VIEW"));
	    ebiModule.guiRenderer.getButton("viewHistory","historyViewDialog").addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("historyText","historyViewDialog").getSelectedItem().toString())){
                    showValueAsHTML();
                }
            }
        });

        ebiModule.guiRenderer.getButton("closeButton","historyViewDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLOSE"));
		ebiModule.guiRenderer.getButton("closeButton","historyViewDialog").addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

					ebiModule.guiRenderer.getEBIDialog("historyViewDialog").setVisible(false);
				}
			});
       fillComboHistory(); 
      ebiModule.guiRenderer.showGUI();  

    }

	private void fillComboHistory(){
		Iterator iter = list.iterator();
		int i = 1;
		while(iter.hasNext()){
			EBICRMHistoryDataUtil util = (EBICRMHistoryDataUtil)iter.next();
			ebiModule.guiRenderer.getComboBox("historyText","historyViewDialog").addItem(String.valueOf(++i)+" "+util.getChangedFrom()+" | "+util.getChangedDate()+" | "+util.getCategory());
		}
	}

	private void showValueAsHTML(){
		
		StringBuffer buf = new StringBuffer();
		
		EBICRMHistoryDataUtil util = list.get(ebiModule.guiRenderer.getComboBox("historyText","historyViewDialog").getSelectedIndex()-1);
		
		Iterator iter = util.getField().iterator();
		
		boolean trClose = false;
		buf.append("<table style=\"font-family: Verdana, serif;color:#000;font-size: 10px; border: solid 1px #a0f0ff; width:100%\" border=\"0\">");

		while(iter.hasNext()){

		    String str = (String)iter.next();
			if("*EOR*".equals(str)){
		       buf.append("<tr><td><hr></td></tr>");
		       trClose = true;
			}else{
                if(str.endsWith("$")){
				   buf.append("<tr><td bgcolor=\"red\"><font color=\"#ffffff\">"+str.substring(0,str.length()-1)+"</font></td></tr>");
                }else{
                   buf.append("<tr><td>"+str+"</td></tr>");
                }
			}
			if(trClose){
				trClose = false;

			}
            
		}

		buf.append("</table>");
		ebiModule.guiRenderer.getEditor("viewhistoryText","historyViewDialog").setText(buf.toString());
	}


} 
