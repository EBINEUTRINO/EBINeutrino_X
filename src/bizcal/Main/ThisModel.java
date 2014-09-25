package bizcal.Main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import org.hibernate.Query;

import bizcal.common.BCalendar;
import bizcal.common.CalendarModel;
import bizcal.common.Event;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import ebiNeutrinoSDK.model.hibernate.Companyactivities;
import ebiNeutrinoSDK.model.hibernate.Crmcalendar;

public class ThisModel extends CalendarModel.BaseImpl {

    public List<Event> events = new ArrayList<Event>();
    public List<BCalendar> calList = new ArrayList<BCalendar>();
    private DateInterval interval;
    private BCalendar cal;
    private BizcalMain main = null;

    public ThisModel(BizcalMain main) throws Exception {
        this.main = main;
        //CREATE THE CALENDAR VIEW
        cal = new BCalendar();
       
        cal.setId(1);
        calList.add(cal);
        try{
            if(main.isMonthView){
                setProperties(main.user, 31 ,false);
            }else{
                setProperties(main.user, Integer.parseInt(main.properties.getValue("EBI_CALENDAR_DAYS")) ,true);
            }
        }catch(NumberFormatException ex){
            setProperties(main.user, 9 ,false);
        }

    }

    public void setProperties(String user,int dayView,boolean actualDate) throws Exception {

        Date start;
        if(!actualDate){
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.DAY_OF_MONTH,1);   
         start =  new Date(cal.getTimeInMillis());
        }else{
         start = main.dayStepper == null ? new Date() : main.dayStepper.getDate();
        }
        Date end = DateUtil.getDiffDay(start, dayView);

        interval = new DateInterval(start, end);
        cal.setSummary(user == null ? "NO-USER" : user);
        if(events.size() > 0){events.clear();}
        showEventFromDatabase(user);
        showEventFromCompanyActivity();
        main.user = user;
        main.refresh();
    }

    public void showEventFromCompanyActivity(){
         try {
            ImageIcon icon = new ImageIcon("images/app_icon.png");

            main.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
            Query query = main.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Companyactivities as act ");

            if (query.list().size() > 0) {

                Iterator it = query.iterate();

                while (it.hasNext()) {
                    Companyactivities activity = (Companyactivities) it.next();
                    main.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(activity);
                    int r;
                    int g;
                    int b;

                    String[] splCol = activity.getAcolor().split(",");
                    r = Integer.parseInt(splCol[0]);
                    g = Integer.parseInt(splCol[1]);
                    b = Integer.parseInt(splCol[2]);

                    Event ev = new Event();

                    ev.setId(activity.getActivityid());
                    ev.setCompanyId(activity.getCompany() == null ? -1 : activity.getCompany().getCompanyid());
                    ev.setSummary(activity.getActivityname());
                    ev.setColor(new Color(r, g, b));
                    ev.setDescription(activity.getActivitydescription());
                    ev.setStart(activity.getDuedate());
                    ev.setIcon(icon);
                    GregorianCalendar startDate = new GregorianCalendar();
                    startDate.setTime(activity.getDuedate());
                    
                    int min = 0;
                    if(activity.getDuration() < 15){
                        min = 16;
                    }else{
                        min = activity.getDuration();
                    }

                    startDate.set(Calendar.MINUTE,  startDate.get(Calendar.MINUTE)+min);
                    startDate.set(Calendar.SECOND, 0);
                    startDate.set(Calendar.MILLISECOND, 0);
                    ev.setActivity(true);
                    ev.setEnd(new Date(startDate.getTime().getTime()));
                    events.add(ev);

                }
                main.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();
            }
         } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showEventFromDatabase(String user) {
        try {
            ImageIcon icon = new ImageIcon("images/calendar_small_min.png");
            ImageIcon icon1 = new ImageIcon("images/google_icon.jpg");
            main.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
            Query query = main.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Crmcalendar where cuser=? ").setString(0, user);

            if (query.list().size() > 0) {

                Iterator it = query.iterate();

                while (it.hasNext()) {
                    Crmcalendar calev = (Crmcalendar) it.next();
                    main.ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(calev);
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    String[] splCol = calev.getColor().split(",");
                    r = Integer.parseInt(splCol[0]);
                    g = Integer.parseInt(splCol[1]);
                    b = Integer.parseInt(splCol[2]);

                    Event ev = new Event();

                    ev.setId(calev.getCalendarid());
                    ev.setSummary(calev.getName());
                    ev.setColor(new Color(r, g, b));
                    if(calev.getCicon() != null && calev.getCicon()[0] == 'G'){
                        ev.setIcon(icon1);
                    }else{
                        ev.setIcon(icon);
                    }
                    ev.setDescription(calev.getDescription());
                    ev.setStart(calev.getStartdate());
                    ev.setEnd(calev.getEnddate());
                    ev.setActivity(false);
                    events.add(ev);
                }
                main.ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload(){
        try {
            events.clear();
            setProperties(main.user, Integer.parseInt(main.properties.getValue("EBI_CALENDAR_DAYS")) ,true);

        }catch(NumberFormatException ex){
            try {
                setProperties(main.user, 9 ,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch(Exception ex){ ex.printStackTrace();}
    }

    public List<Event> getEvents(Object calId) throws Exception {
        return events;
    }

    public List<BCalendar> getSelectedCalendars() throws Exception {
        return calList;
    }

    public DateInterval getInterval() {
        return interval;
    }
}
