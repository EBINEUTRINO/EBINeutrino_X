package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Date;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;

/**
 * Created by IntelliJ IDEA.
 * User: ebicrm
 * Date: 13.07.2010
 * Time: 20:33:42
 * Pessimistic view dialog
 */
public class EBIPessimisticViewDialog {

    private EBICRMModule ebiModule = null;
    private String moduleName = "";
    private int lockId = -1;
    private int lockStatus = 0;
    private Timestamp lockTime = null;
    private String lockUser = "";
    private EBIDialog pessimisticDialog = null;

    public EBIPessimisticViewDialog(EBICRMModule module){
        ebiModule  = module;
        ebiModule.guiRenderer.loadGUI("CRMDialog/crmPessimisticView.xml");
        ebiModule.guiRenderer.getButton("forceUnlock","pessimisticViewDialog").addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent event){
                  ebiModule.forceUnlock(lockId, moduleName);
                  pessimisticDialog.setVisible(false);
              }
       });
       pessimisticDialog = ebiModule.guiRenderer.getEBIDialog("pessimisticViewDialog");
    }


    public EBIDialog getPessimisticDialog(){
       return  pessimisticDialog;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public Timestamp getLockTime() {
        return lockTime;
    }

    public void setLockTime(Timestamp lockTime) {
        this.lockTime = lockTime;
    }

    public String getLockUser() {
        return lockUser;
    }

    public void setLockUser(String lockUser) {
        this.lockUser = lockUser;
    }
}
