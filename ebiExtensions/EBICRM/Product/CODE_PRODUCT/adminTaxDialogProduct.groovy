import ebiCRM.EBICRMModule
import ebiCRM.gui.dialogs.EBIDialogTaxAdministration

def updateCombos(final def core){
    final Runnable runnable = new Runnable() {
        public void run() {
            ((EBICRMModule)core.mod_management.getActiveModule()).dynMethod.initComboBoxes(true);
        }
    };

    Thread thread=new Thread(runnable,"refreshComboThread");
    thread.start();
}


system.gui.getButton("taxAdmin","Product").actionPerformed={
    EBIDialogTaxAdministration tadmin = new EBIDialogTaxAdministration(((EBICRMModule)core.mod_management.getActiveModule()));
    tadmin.setVisible();
    updateCombos(core);
}
