/*******************************************************************************
 * EBIBizcal is a component library for calendar widgets written in java using swing.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Description:
 * This is the EBIBizcal is a GPL clone of Bizcal
 *******************************************************************************/

package bizcal.Main;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.hibernate.Query;

import bizcal.common.CalendarViewConfig;
import bizcal.common.DayViewConfig;
import bizcal.common.Event;
import bizcal.swing.DayStepper;
import bizcal.swing.DayView;
import bizcal.swing.MonthStepper;
import bizcal.swing.MonthView;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.StreamCopier;
import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyactivities;
import ebiNeutrinoSDK.model.hibernate.Crmcalendar;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class BizcalMain implements ChangeListener {

    public ThisModel calmodel = null;
    public DayView dayView = null;
    public MonthView monthView = null;
    public JFrame frame = null;
    public Event selectedEvent = null;
    public PopupMenuCallbacks popupCallback = null;
    public Date selectedDate = null;
    public Event copiedEvent = null;
    public DayViewConfig config = null;
    public DayStepper dayStepper = null;
    private JPanel panel = null;
    public BIZCalendarToolbar toolBar = null;
    public EBIPGFactory ebiPGFactory = null;
    private boolean isNewEvent = false;
    public String user = "";
    public ebiCRM.EBICRMModule ebiModule = null;
    public EBIPropertiesRW properties = null;
    private boolean isChanged = false;
    public boolean isMonthView = false;
    public boolean isDayView = false;
    public MonthStepper monthStepper = null;



    public BizcalMain(String locale, String user, EBICRMModule module) {
        properties = EBIPropertiesRW.getPropertiesInstance();
        this.ebiPGFactory = module.ebiPGFactory;
        ebiModule = module;
        this.user = user;

        try {
            this.ebiPGFactory.hibernate.openHibernateSession("CALENDAR_SESSION");

            if ("German".equals(locale)) {
                LocaleBroker.setLocale(Locale.GERMAN);
            } else if ("Italian".equals(locale)) {
                LocaleBroker.setLocale(Locale.ITALIAN);
            } else {
                LocaleBroker.setLocale(Locale.getDefault());
            }

            calmodel = new ThisModel(this);

            panel = new JPanel();
            panel.setLayout(new BorderLayout());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initMonthView() throws Exception{
         panel.removeAll();

         initializeToolBar();

         isMonthView = true;
         isDayView = false;

         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.DAY_OF_MONTH,1);
         Date start = cal.getTime();
         Date end = DateUtil.getDiffDay(new Date(), 31);

         calmodel.getInterval().setStart(start);
         calmodel.getInterval().setEnd(end);

         if(monthView == null){
            CalendarViewConfig calConfig = new CalendarViewConfig();

            monthView = new MonthView(calConfig);
            monthView.addListener(new BizcalListener(this));
            monthView.setModel(calmodel);

            popupCallback = new PopupMenuCallbacks(this);
            monthView.setPopupMenuCallback(popupCallback);

            monthStepper = new MonthStepper(this);
            monthStepper.addChangeListener(this);
        }
        
        refresh();
        monthView.refresh();

        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(monthView.getComponent(),BorderLayout.CENTER);
        panel.add(monthStepper.getComponent(),BorderLayout.SOUTH);
        panel.updateUI();
        isChanged = true;
    }


    public JComponent initDayView() throws Exception {
        
        if(isChanged){
           panel.removeAll(); 
        }

        isMonthView = false;
        isDayView = true;

        Date  start = null;
        if(dayStepper != null){
            start = dayStepper.getDate();
        }else{
            start = new Date();
        }

        calmodel.getInterval().setStart(start);
        calmodel.getInterval().setEnd( DateUtil.getDiffDay(start, Integer.parseInt("".equals(properties.getValue("EBI_CALENDAR_DAYS")) ? "8" : properties.getValue("EBI_CALENDAR_DAYS"))));

        if(dayView == null){
            config = new DayViewConfig();
            config.setDayStartHour(0);
            config.setDayEndHour(24);

            dayStepper = new DayStepper(this);
            dayStepper.addChangeListener(this);

            initializeToolBar();

            dayView = new DayView(config);
            dayView.setModel(calmodel);
            dayView.addListener(new BizcalListener(this));
            popupCallback = new PopupMenuCallbacks(this);
            dayView.setPopupMenuCallback(popupCallback);
        }
        dayView.refresh();

        panel.add(toolBar, BorderLayout.NORTH);
        panel.add(dayView.getComponent(), BorderLayout.CENTER);
        panel.add(dayStepper.getComponent(), BorderLayout.SOUTH);
        panel.updateUI();
        return panel;
    }


    public void resize(Event event,Date newDate){

        try {
            event.setEnd(newDate);

            if (isNewEvent) {
                saveEvents(event);
            } else {
                updateEvents(event);
            }
            refresh();
        } catch (Exception e) {
            e.printStackTrace();  
        }
    }

    private void initializeToolBar(){
        if(toolBar == null){
          toolBar = new BIZCalendarToolbar(this);
        }
    }

    public void move(Event event, Object orgCalId, Date orgDate, Object newCalId, Date newDate) throws Exception {

        Calendar orgCdate = new GregorianCalendar();
        orgCdate.setTime(orgDate);
        orgCdate.set(Calendar.SECOND, 0);
        orgCdate.set(Calendar.MILLISECOND, 0);

        Calendar newCdate = new GregorianCalendar();
        newCdate.setTime(newDate);
        newCdate.set(Calendar.SECOND, 0);
        newCdate.set(Calendar.MILLISECOND, 0);

        Calendar eventCdate = new GregorianCalendar();
        eventCdate.setTime(event.getEnd());
        eventCdate.set(Calendar.SECOND, 0);
        eventCdate.set(Calendar.MILLISECOND, 0);

        Date date = null;

        if (newCdate.get(Calendar.DAY_OF_MONTH) == orgCdate.get(Calendar.DAY_OF_MONTH)) {

            date = new Date(eventCdate.getTimeInMillis() +
                    (newCdate.getTimeInMillis() - orgCdate.getTimeInMillis()));


        } else {
            eventCdate.set(Calendar.DAY_OF_MONTH, newCdate.get(Calendar.DAY_OF_MONTH));
            orgCdate.set(Calendar.DAY_OF_MONTH, newCdate.get(Calendar.DAY_OF_MONTH));

            date = new Date(eventCdate.getTimeInMillis() +
                    (newCdate.getTimeInMillis() - orgCdate.getTimeInMillis()));

        }

        event.setStart(DateUtil.round2Minute(newDate));
        event.setEnd(DateUtil.round2Minute(date));

        if (isNewEvent) {
            saveEvents(event);
        } else {
            updateEvents(event);
        }
        refresh();

    }

    public void newEvent(List<Event> events, Date startDate, Date endDate) throws Exception {

        Event newEvent = new Event();
        newEvent.setStart(startDate);
        newEvent.setEnd(endDate);
        
        TimePicker time = new TimePicker(this,events, newEvent, false);
        if(newEvent.isActivity()){
        	newEvent.setIcon(new ImageIcon("images/run.png"));
        }else{
        	newEvent.setIcon(new ImageIcon("images/calendar_small_min.png"));
        }
        time.setVisible(true);
        
        if (time.success) {
            saveEvents(newEvent);
            dayView.refresh();
            refresh();
        }

    }

    public void editEvent() {
        try {
            TimePicker time = new TimePicker(this,calmodel.getEvents(1), selectedEvent, true);
            time.setVisible(true);
            if (time.success) {
                updateEvents(selectedEvent);
                refresh();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteEvent() {
        try {

            if (EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.WARNING_MESSAGE_YESNO) == false) {
                return;
            }

            Query query = null;
            if(selectedEvent.isActivity()){
                query = ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Companyactivities as act where act.activityid=? ").setString(0, String.valueOf(selectedEvent.getId()));
            }else{
                query = ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Crmcalendar where calendarid=? ").setString(0, String.valueOf(selectedEvent.getId().toString()));
            }
            Iterator it = query.iterate();
            Object calev = null;
            if (it.hasNext()) {
                if(selectedEvent.isActivity()){
                   calev = it.next();
                   ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(calev);
                }else{
                   calev =  it.next();
                   ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(calev); 
                }
                ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").delete(calev);
                ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();
                if(ebiPGFactory.company != null){
                    ebiModule.getActivitiesPane().activityDataControl.dataShow(true);
                    ebiModule.getActivitiesPane().activityDataControl.dataNew();
                }

            }

            calmodel.getEvents(1).remove(selectedEvent);

            resetComponent(false);
            refresh();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveEvents(Event evs) {
        try {
            ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
            StringBuffer color = new StringBuffer();
            color.append(evs.getColor().getRed());
            color.append(",");
            color.append(evs.getColor().getGreen());
            color.append(",");
            color.append(evs.getColor().getBlue());

           if(!evs.isActivity()){
                Crmcalendar crmcal = new Crmcalendar();
                crmcal.setCuser(user);
                crmcal.setName(evs.getSummary());
                crmcal.setDescription(evs.getDescription());
                crmcal.setColor(color.toString());
                crmcal.setStartdate(evs.getStart());
                crmcal.setEnddate(evs.getEnd());
                
                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").saveOrUpdate(crmcal);
                evs.setId(crmcal.getCalendarid());
                ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();

           }else{
               Companyactivities activity = new Companyactivities();
               activity.setCreateddate(new Date());
               activity.setCreatedfrom(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(evs.getCreatedFrom()) ? user : evs.getCreatedFrom());
               activity.setCompany(ebiModule.ebiPGFactory.company);
               activity.setActivitytype("");
               activity.setActivityname(evs.getSummary());
               activity.setActivitydescription(evs.getDescription());
               activity.setAcolor(color.toString());
               activity.setDuedate(evs.getStart());

               int minute  = Math.abs((int)(evs.getStart().getTime() - evs.getEnd().getTime()) / 60000);
               activity.setDuration(minute);
               
               ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").saveOrUpdate(activity);
               ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(activity);
               evs.setCompanyId(activity.getCompany() == null ? -1 : activity.getCompany().getCompanyid());
               evs.setId(activity.getActivityid());
               ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();
               ebiModule.getActivitiesPane().activityDataControl.dataShow(true);
           }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void updateEvents(Event evs) {
        try {
            ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").begin();
            StringBuffer color = new StringBuffer();
            color.append(evs.getColor().getRed());
            color.append(",");
            color.append(evs.getColor().getGreen());
            color.append(",");
            color.append(evs.getColor().getBlue());

          if(!evs.isActivity() && selectedEvent != null){

            Query query = ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Crmcalendar where calendarid=? ").setString(0, String.valueOf(selectedEvent.getId()));

            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmcalendar calev = (Crmcalendar) it.next();
                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(calev);
                calev.setCuser(user);
                calev.setName(evs.getSummary());
                calev.setDescription(evs.getDescription());
                calev.setColor(color.toString());
                calev.setStartdate(evs.getStart());
                calev.setEnddate(evs.getEnd());
                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").update(calev);
            }
            ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();  
          }else{
             Query query = ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").createQuery("from Companyactivities as act where act.activityid=? ").setString(0, String.valueOf(selectedEvent.getId()));

             Iterator it = query.iterate();

             if (it.hasNext()) {
                Companyactivities activity = (Companyactivities) it.next();
                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").refresh(activity); 
                activity.setCreatedfrom(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(evs.getCreatedFrom()) ? user : evs.getCreatedFrom());
                activity.setActivityname(evs.getSummary());
                activity.setActivitydescription(evs.getDescription());
                activity.setAcolor(color.toString());
                activity.setDuedate(evs.getStart());

                long minute  = (evs.getStart().getTime() - evs.getEnd().getTime()) / 60000;
                activity.setDuration(Math.abs((int)minute));

                ebiPGFactory.hibernate.getHibernateSession("CALENDAR_SESSION").update(activity);
             }
            ebiPGFactory.hibernate.getHibernateTransaction("CALENDAR_SESSION").commit();
            ebiModule.getActivitiesPane().activityDataControl.dataShow(true);
          }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void copyEvent(boolean newEvent) {
        try {
            isNewEvent = newEvent;
            if (newEvent) {
                copiedEvent = new Event();
                copiedEvent.setSummary(selectedEvent.getSummary());
                copiedEvent.setColor(selectedEvent.getColor());
                copiedEvent.setDescription(selectedEvent.getDescription());
                copiedEvent.setStart(selectedEvent.getStart());
                copiedEvent.setEnd(selectedEvent.getEnd());

            } else {
                copiedEvent = selectedEvent;
            }
            popupCallback.pasteItem.setEnabled(true);
            toolBar.jButtonPaste.setEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void cutEvent() {
        try {
            copyEvent(false);
            calmodel.getEvents(1).remove(selectedEvent);
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void pasteEvent() {
        try {
            move(copiedEvent, 1, copiedEvent.getStart(), 1, selectedDate);
            calmodel.getEvents(1).add(copiedEvent);
            popupCallback.pasteItem.setEnabled(false);
            toolBar.jButtonPaste.setEnabled(false);
            copiedEvent = null;
            refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh() throws Exception {

        if(!isMonthView){
            if(dayView != null){
                dayView.refresh();
            }
        }else{
           if(monthView != null){
               monthView.refresh();
           }
        }
        if(calmodel != null){
         calmodel.refresh();
        }
    }

    public void reload(){
        calmodel.reload();
    }

    public void stateChanged(ChangeEvent arg0) {
        try {
            Date dt  = null;
            try{

            if(isMonthView){

              Calendar cal = Calendar.getInstance();
              cal.setTime(monthStepper.getDate());
              cal.set(Calendar.DAY_OF_MONTH,1);
              dt =  new Date(cal.getTimeInMillis());
              calmodel.getInterval().setEndDate(DateUtil.getDiffDay(dt, 31));
            }else if(isDayView){
               dt = dayStepper.getDate();
               calmodel.getInterval().setEndDate(DateUtil.getDiffDay(dt, Integer.parseInt(this.toolBar.days.getText()))); 
            }

            calmodel.getInterval().setStartDate(dt);

            }catch(NumberFormatException ex){
                if(!isMonthView){
                    this.toolBar.days.setText("9");
                    calmodel.getInterval().setEndDate(DateUtil.getDiffDay(dayStepper.getDate(), 9));
                }
            }
            refresh();
        } catch (Exception ex) {
                ex.printStackTrace();
        }
    }

    public void resetComponent(boolean enabled) {
        if (enabled == false) {
            selectedEvent = null;
        }
        popupCallback.copyItem.setEnabled(enabled);
        popupCallback.cutItem.setEnabled(enabled);
        popupCallback.deleteItem.setEnabled(enabled);
        popupCallback.editItem.setEnabled(enabled);

        toolBar.jButtonCopy.setEnabled(enabled);
        toolBar.jButtonCut.setEnabled(enabled);
        toolBar.jButtonDelete.setEnabled(enabled);
    }

    public Icon getIcon(String filename) throws Exception {
        byte[] bytes = StreamCopier.copyToByteArray(getClass().getResourceAsStream(filename));
        return new ImageIcon(bytes);
    }


}


