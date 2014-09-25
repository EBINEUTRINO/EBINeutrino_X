package bizcal.Main;

import javax.swing.JDialog;

public interface IEBIToBizcalendar {

    public void setDialogForNewEvent(JDialog dialog);

    public void refresh() throws Exception;
}
