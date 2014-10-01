package ebiCRM.gui.dialogs;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class EBIMoveRecord {

    public EBICRMModule ebiModule = null;
    public long companyID = -1;

    public EBIMoveRecord(EBICRMModule ebiModule){
         this.ebiModule=ebiModule;
    }


    public void setVisible(){
        ebiModule.guiRenderer.loadGUI("CRMDialog/moveRecordDialog.xml");

        ebiModule.guiRenderer.getTextfield("companyName","moveRecordDialog").setEditable(false);
        ebiModule.guiRenderer.getTextfield("companyName","moveRecordDialog").setBackground(EBIPGFactory.systemColor);
        ebiModule.guiRenderer.getButton("closeDialog","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                companyID = -1;
                ebiModule.guiRenderer.getEBIDialog("moveRecordDialog").setVisible(false);
            }
        });

        ebiModule.guiRenderer.getButton("apply","moveRecordDialog").setEnabled(false);
        ebiModule.guiRenderer.getButton("apply","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ebiModule.guiRenderer.getEBIDialog("moveRecordDialog").setVisible(false);
            }
        });

        ebiModule.guiRenderer.getButton("searchCompany","moveRecordDialog").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchCompany","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final HashMap <String,Object> map = new HashMap<String, Object>();

                if(map.get("Name") != null && map.get("ID") != null ){
                    ebiModule.guiRenderer.getTextfield("companyName","moveRecordDialog").setText(map.get("Name").toString());
                    companyID = Integer.valueOf(map.get("ID").toString());
                    ebiModule.guiRenderer.getButton("apply","moveRecordDialog").setEnabled(true);
                }
            }
        });
        ebiModule.guiRenderer.showGUI();

    }


    public long getSelectedID(){
        return companyID;
    }


}
