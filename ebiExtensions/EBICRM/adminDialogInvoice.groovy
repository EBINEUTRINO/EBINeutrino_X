import ebiCRM.EBICRMModule
import ebiCRM.gui.dialogs.EBIDialogInternalNumberAdministration
import ebiNeutrinoSDK.EBIPGFactory
import ebiNeutrinoSDK.gui.dialogs.EBIDialogValueSetter

def updateCombos(final def core){
    final Runnable runnable = new Runnable() {
        public void run() {
             ((EBICRMModule)core.mod_management.getActiveModule()).dynMethod.initComboBoxes(true);
        }
    };

    Thread thread=new Thread(runnable,"refreshComboThread");
    thread.start();
}

system.gui.getButton("admnInvoiceCategory","Invoice").actionPerformed ={
    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(system,"Crminvoicecategory", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
    setValueSetter.setVisible();
    updateCombos(core);
}

system.gui.getButton("admnInvoiceStatus","Invoice").actionPerformed ={
    EBIDialogValueSetter setValueSetter = new EBIDialogValueSetter(system,"Crminvoicestatus", EBIPGFactory.getLANG("EBI_LANG_STATUS"));
    setValueSetter.setVisible();
    updateCombos(core);
}

system.gui.getButton("admnInvoiceAutoInc","Invoice").actionPerformed ={
    EBIDialogInternalNumberAdministration incAdmin = new EBIDialogInternalNumberAdministration((EBICRMModule)core.mod_management.getActiveModule(),true);
    incAdmin.setVisible();
    updateCombos(core);
}