package bizcal.Main;

import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import bizcal.common.Event;
import bizcal.swing.CalendarListener;
import bizcal.util.DateInterval;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;

public class BizcalListener extends CalendarListener.BaseImpl {
    private BizcalMain main;
    public List<Event> events = null;


    public BizcalListener(BizcalMain main) {
        this.main = main;
        try {
            events = main.calmodel.getEvents(1);
        } catch (Exception ex) {

        }
    }

    public void showEvent(Object id, Event event) throws Exception {
    }

    public void dateChanged(Date date) throws Exception {
// 		main.selectedDate = date;
    }

    public void dateSelected(Date date) throws Exception {
        main.selectedDate = date;
    }

    public void moved(Event event, Object orgCalId, Date orgDate, Object newCalId, Date newDate) throws Exception {
        main.move(event, orgCalId, orgDate, newCalId, newDate);
    }

    public void selectionReset() throws Exception {
        main.resetComponent(false);
    }

    public void eventDoubleClick(Object id, final Event event, MouseEvent mouseEvent){
        if(event.isActivity() && event.getCompanyId() != -1){
            final Runnable waitRunner = new Runnable(){
			 public void run() {

                  EBIWinWaiting wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_COMPANY_DATA"));

                  try{

                      wait.setVisible(true);

                      if(event.getCompanyId() != main.ebiModule.companyID){
                        main.ebiModule.createUI(event.getCompanyId(),false);
                      }

                      main.ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Activity");
                      main.ebiModule.getActivitiesPane().editActivity(Integer.parseInt(event.getId().toString()));

                  }catch(Exception ex){ex.printStackTrace();}finally{
                    wait.setVisible(false);
                  }
                }
            };
            Thread loaderThread = new Thread(waitRunner, "LoaderThread");
            loaderThread.start();

        }
      
    }

    public void resized(Event event, Object orgCalId, Date orgEndDate, Date newEndDate) throws Exception {
        main.resize(event,newEndDate);
    }

    public void eventSelected(Object calId, Event event) throws Exception {
        main.selectedEvent = event;
        main.resetComponent(true);
    }

    public void newEvent(Object id, DateInterval interval) throws Exception {
        main.newEvent(events, interval.getStartDate(), interval.getEndDate());
    }

    public void newEvent(Object id, Date date) throws Exception {
    }


    public void closeCalendar(Object calId) throws Exception {
    }

}