package bizcal.web;

import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ecs.AlignType;
import org.apache.ecs.Element;
import org.apache.ecs.html.B;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Font;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TH;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.apache.ecs.wml.Img;

import bizcal.common.BCalendar;
import bizcal.common.DayViewConfig;
import bizcal.common.Event;
import bizcal.util.DateInterval;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.NullSafe;
import bizcal.util.TimeOfDay;
import bizcal.util.TimeZoneBroker;
import bizcal.util.Tuple;

/**
 * @author Fredrik Bertilsson
 */
public class DayView
        extends CalendarView {
    public Element getContent()
            throws Exception {
        getCalendarCallback().refresh();
        DateFormat dateFormat = new SimpleDateFormat("MMM-dd");
        dateFormat.setTimeZone(TimeZoneBroker.getTimeZone());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeFormat.setTimeZone(TimeZoneBroker.getTimeZone());
        DateFormat hourFormat = new SimpleDateFormat("HH");
        hourFormat.setTimeZone(TimeZoneBroker.getTimeZone());
        DateFormat minuteFormat = new SimpleDateFormat("mm");
        minuteFormat.setTimeZone(TimeZoneBroker.getTimeZone());
        long resolution = getResolution();

        Table rootTable = new Table();
        rootTable.setWidth("100%");

        Table filterPanel = new Table();
        filterPanel.setCellPadding(0);
        filterPanel.setCellSpacing(0);
        rootTable.addElement(new TR(new TD(filterPanel)));
        TR tr = new TR();
        filterPanel.addElement(tr);

        Table table = new Table();
        table.setCellSpacing(1);
        table.setCellPadding(2);
        table.setWidth("100%");
        table.setClass("border");
        TR rootTR = new TR();
        rootTR.addElement(new TD(table));
        rootTable.addElement(rootTR);

        // Create headers and slots
        TH th;
        List calendars = getCalendarCallback().getCalendars();
        TR trCal = createHeaderRow();
        table.addElement(trCal);
        TH timeTH = createHeaderCell("Time");
        timeTH.setColSpan(2);
        trCal.addElement(timeTH);
        TR trDatum = createHeaderRow();
        th = new TH();
        th.setColSpan(2);
        trDatum.addElement(th);
        if (getDayCount() > 1)
            table.addElement(trDatum);
        int noOfCols = calendars.size() + 1;
        Map eventMatrix = new HashMap();
        Iterator i = calendars.iterator();
        while (i.hasNext()) {
            BCalendar cal = (BCalendar) i.next();
            BufferedImage image = cal.getImage();
            if (image == null)
                th = createHeaderCell(cal.getSummary());
            else {
                Img img = new Img();
                img.setSrc("image?table=resource&column=image&key=" + cal.getId());
                th = new TH(img);
                th.addElement(new BR());
                th.addElement(cal.getSummary());
                trCal.setClass("bgcolor2");
                ((Font) timeTH.elements().nextElement()).setColor("#000000");
            }
            th.setWidth("" + (100 / (noOfCols - 1)) + "%");
            th.setColSpan(getDayCount());
            trCal.addElement(th);
            List events = getModel().getEvents(cal.getId());
            Date date = getInterval().getStartDate();
            for (int j = 0; j < getDayCount(); j++) {
                th = createHeaderCell(dateFormat.format(date));
                trDatum.addElement(th);
                List slots = createSlots(events, getInterval(date));
                eventMatrix.put(new Tuple(cal.getId(), DateUtil.round2Day(date)), slots);
                date = DateUtil.getDiffDay(date, +1);
            }
        }


        TD td;
        long hourspan = 60 * 60 * 1000 / resolution;
        int slotno = 0;
        int ispan = 0;
        TimeOfDay startTime = getModel().getViewStart();
        TimeOfDay endTime = getModel().getViewEnd();
        for (long pos = startTime.getValue();
             pos < endTime.getValue();
             pos += resolution) {
            tr = new TR();
            table.addElement(tr);
            TimeOfDay posTime = new TimeOfDay(pos);
            Date posDate = posTime.getDate(getInterval().getStartDate());
            if (ispan == 0) {
                td = new TD(new B(hourFormat.format(posDate)));
                td.setBgColor("#FFFFFF");
                tr.addElement(td);
                td.setRowSpan((int) hourspan);
                td.setVAlign(AlignType.CENTER);
            }
            td = new TD(minuteFormat.format(posDate));
            td.setBgColor("#FFFFFF");
            tr.addElement(td);
            Iterator j = calendars.iterator();
            while (j.hasNext()) {
                BCalendar cal = (BCalendar) j.next();
                Date date = getInterval().getStartDate();
                for (int k = 0; k < getDayCount(); k++) {
                    Date time = posTime.getDate(date);
                    DateInterval interval = getInterval(date);
                    Tuple key = new Tuple(cal.getId(), DateUtil.round2Day(date));
                    List slots = (List) eventMatrix.get(key);
                    Event prevSlot = null;
                    if (slotno > 0)
                        prevSlot = (Event) slots.get(slotno - 1);
                    Event slot = (Event) slots.get(slotno);
                    if (slot != null) {
                        if (!NullSafe.equals(slot, prevSlot) || slot.isBackground())
                            tr.addElement(createCell(slot, interval, cal, time));
                    } else
                        tr.addElement(createFreeCell(cal, time));
                    date = DateUtil.getDiffDay(date, +1);
                }
            }
            slotno++;
            ispan++;
            if (ispan >= hourspan)
                ispan = 0;
        }

        return rootTable;
    }

    private int getDayCount() {
        return getDesc().getDayCount();
    }

    private DateInterval getInterval(Date date)
            throws Exception {
        return new DateInterval(getModel().getViewStart().getDate(date),
                getModel().getViewEnd().getDate(date));
    }

    public void setStartDate(Date date)
            throws Exception {
        if (getDayCount() == 7) {
            java.util.Calendar cal =
                    java.util.Calendar.getInstance(LocaleBroker.getLocale());
            cal.setTime(date);
            cal.set(java.util.Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            date = cal.getTime();
        }
        Date endDate = new Date(date.getTime() + getDayCount() * 24 * 3600 * 1000);
        setInterval(new DateInterval(date, endDate));
    }

    private DayViewConfig getDesc() {
        return (DayViewConfig) getConfig();
    }

}
