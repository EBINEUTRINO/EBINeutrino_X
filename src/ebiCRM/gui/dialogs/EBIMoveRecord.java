package ebiCRM.gui.dialogs;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.utils.EBIConstant;

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
        ebiModule.gui.loadGUI("CRMDialog/moveRecordDialog.xml");

        ebiModule.gui.getTextfield("companyName","moveRecordDialog").setEditable(false);

        ebiModule.gui.getButton("closeDialog","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                companyID = -1;
                ebiModule.gui.getEBIDialog("moveRecordDialog").setVisible(false);
            }
        });

        ebiModule.gui.getButton("apply","moveRecordDialog").setEnabled(false);
        ebiModule.gui.getButton("apply","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ebiModule.gui.getEBIDialog("moveRecordDialog").setVisible(false);
            }
        });

        ebiModule.gui.getButton("searchCompany","moveRecordDialog").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.gui.getButton("searchCompany","moveRecordDialog").addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                final HashMap <String,Object> map = new HashMap<String, Object>();

                if(map.get("Name") != null && map.get("ID") != null ){
                    ebiModule.gui.getTextfield("companyName","moveRecordDialog").setText(map.get("Name").toString());
                    companyID = Integer.valueOf(map.get("ID").toString());
                    ebiModule.gui.getButton("apply","moveRecordDialog").setEnabled(true);
                }
            }
        });
        ebiModule.gui.showGUI();

    }


    public long getSelectedID(){
        return companyID;
    }


}
