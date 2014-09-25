package bizcal.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import bizcal.common.CalendarViewConfig;
import bizcal.common.Event;
import bizcal.swing.util.FrameArea;
import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.BizcalException;
import bizcal.util.DateUtil;
import bizcal.util.TextUtil;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

public class MonthView extends CalendarView {
    private ColumnHeaderPanel columnHeader;
    private List<List> cells = new ArrayList<List>();
    private List hLines = new ArrayList();
    private List vLines = new ArrayList();
    private JScrollPane scrollPane;
    private JPanel calPanel;

    public MonthView(CalendarViewConfig desc)
            throws Exception {
        super(desc);
        calPanel = new JPanel();
        calPanel.setLayout(new Layout());

        scrollPane =
                new JScrollPane(calPanel,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setCursor(Cursor.getDefaultCursor());
        scrollPane.getVerticalScrollBar().setUnitIncrement(15);
        columnHeader = new ColumnHeaderPanel(desc, 7);

        columnHeader.setShowExtraDateHeaders(true);
        columnHeader.setMonthView(true);
        scrollPane.setColumnHeaderView(columnHeader.getComponent());
    }

    @SuppressWarnings("unchecked")
    public void refresh0()
            throws Exception {
        calPanel.removeAll();
        cells.clear();
        hLines.clear();
        vLines.clear();

        Calendar cal = DateUtil.newCalendar();

        cal.setTime(getInterval().getStartDate() == null ? new Date() : getInterval().getStartDate());
        cal.add(Calendar.DAY_OF_YEAR, 15);
        int month = cal.get(Calendar.MONTH);

        int lastDayOfWeek = cal.getFirstDayOfWeek();
        lastDayOfWeek--;
        if (lastDayOfWeek < 1)
            lastDayOfWeek += 7;

        Iterator j = getModel().getSelectedCalendars().iterator();
        while (j.hasNext()) {
            bizcal.common.BCalendar calInfo = (bizcal.common.BCalendar) j.next();
            cal.setTime(getInterval().getStartDate());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            Map eventMap = createEventsPerDay(calInfo.getId());
            int rowno = 0;
            while (true) {
                List<JComponent> row;
                if (cells.size() <= rowno) {
                    row = new ArrayList<JComponent>();
                    cells.add(row);
                } else
                    row = (List) cells.get(rowno);
                JComponent cell = createDayCell(cal, eventMap, month, calInfo.getId());
                calPanel.add(cell);
                row.add(cell);
                if (cal.get(Calendar.DAY_OF_WEEK) == lastDayOfWeek) {
                    if (cal.get(Calendar.MONTH) != month)
                        break;
                    rowno++;
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        int colCount = getModel().getSelectedCalendars().size() * 7;
        for (int i = 0; i < colCount - 1; i++) {
            JLabel line = new JLabel();
            line.setBackground(Color.LIGHT_GRAY);
            line.setOpaque(true);
            if ((i + 1) % 7 == 0)
                line.setBackground(getDescriptor().getLineColor3());
            calPanel.add(line);
            vLines.add(line);
        }

        int rowCount = cells.size() - 1;
        for (int i = 0; i < rowCount; i++) {
            JLabel line = new JLabel();
            line.setBackground(Color.LIGHT_GRAY);
            line.setOpaque(true);
            calPanel.add(line);
            hLines.add(line);
        }

        columnHeader.setModel(getModel());
        columnHeader.setPopupMenuCallback(popupMenuCallback);
        columnHeader.refresh();
    }


    /**
     * @param cal
     * @param eventMap
     * @param month
     * @param calId
     * @return
     * @throws Exception
     */
    private JComponent createDayCell(Calendar cal, Map eventMap, int month, Object calId)
            throws Exception {
        Font eventFont = this.font;
        TableLayoutPanel panel = new TableLayoutPanel();

        if (cal.get(Calendar.MONTH) == month) {
            panel.setBackground(Color.WHITE);
        } else
            panel.setBackground(new Color(230, 230, 230));
        panel.createColumn(TableLayoutPanel.FILL);
        //panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        //panel.setBorder(BasicBorders.getRadioButtonBorder());
        int dayno = cal.get(Calendar.DAY_OF_MONTH);
        String text = "" + dayno;
        Row row = panel.createRow();
        JLabel label = new JLabel(text);
        //label.setOpaque(true);
        //label.setBackground(getDescriptor().getPrimaryColor());
        //label.setForeground(Color.black);
        label.setFont(font.deriveFont(Font.BOLD));
        row.createCell(label);
        panel.createRow(TableLayoutPanel.FILL);

        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        List events = (List) eventMap.get(DateUtil.round2Day(cal.getTime()));

        if (events != null) {
            Iterator i = events.iterator();
            while (i.hasNext()) {
                Event event = (Event) i.next();
                row = panel.createRow();
                String time = format.format(event.getStart());
                String summary = "";
                if (event.getSummary() != null)
                    summary = event.getSummary();
                JLabel eventLabel = new JLabel(time + " " + summary);
                eventLabel.setFont(eventFont);
                time += "-" + format.format(event.getEnd());
                eventLabel.setToolTipText(time + " " + summary);
                eventLabel.setOpaque(true);
                eventLabel.setBackground(event.getColor());
                /* ------------------------------------------------------- */
                // set foreground color
                eventLabel.setForeground(FrameArea.computeForeground(event.getColor()));
                /* ------------------------------------------------------- */
                eventLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (event.getIcon() != null)
                    eventLabel.setIcon(event.getIcon());
                eventLabel.addMouseListener(new EventMouseListener(event, calId));
                row.createCell(eventLabel, TableLayoutPanel.TOP, TableLayoutPanel.FULL);
            }
        }
        panel.addMouseListener(new DayMouseListener(calId, cal.getTime()));
        JScrollPane scrollPanel =
                new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setPreferredSize(new Dimension(100, 100));
        //return scrollPanel;
        return panel;
    }

   
    private class EventMouseListener
            extends MouseAdapter {
        private Event event;
        private Object calId;

        /**
         * @param event
         * @param calId
         */
        public EventMouseListener(Event event, Object calId) {
            this.calId = calId;
            this.event = event;
        }

        public void mouseClicked(MouseEvent mevent) {
            try {

                if (mevent.getClickCount() == 2){
                    listener.eventDoubleClick(calId, event, mevent);
                }
            } catch (Exception e) {
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.INFO_MESSAGE);
            }
        }
    }

    private class DayMouseListener
            extends MouseAdapter {
        private Object calId;
        private Date date;

        public DayMouseListener(Object calId, Date date) {
            this.calId = calId;
            this.date = date;
        }

        public void mouseEntered(MouseEvent e) {
            JPanel label = (JPanel) e.getSource();
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.setBackground(label.getBackground().darker());
            label.setForeground(Color.LIGHT_GRAY);
        }

        public void mouseExited(MouseEvent e) {
            JPanel label = (JPanel) e.getSource();
            label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            label.setBackground(label.getBackground().brighter());
            label.setForeground(Color.BLACK);
        }

        public void mouseClicked(MouseEvent e) {
            try {
                /* ------------------------------------------------------- */
                if (listener == null)
                    return;
                /* ------------------------------------------------------- */
                if (e.getClickCount() < 2) {
                    listener.dateSelected(date);
                    return;
                }
                /* ------------------------------------------------------- */
                if (!getModel().isInsertable(calId, date))
                    return;
                listener.newEvent(calId, date);
                /* ------------------------------------------------------- */
            } catch (Exception exc) {
                EBIExceptionDialog.getInstance(exc.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
            }
        }
    }

    protected Date getDate(int xPos, int yPos)
            throws Exception {
        return null;
    }

    public long getTimeInterval()
            throws Exception {
        return 24 * 3600 * 1000 * 30;
    }

    protected String getHeaderText() throws Exception {
        Calendar cal = DateUtil.newCalendar();
        cal.setTime(getInterval().getStartDate());
        DateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return TextUtil.formatCase(format.format(cal.getTime()));
    }

//	protected JComponent createCalendarPanel()
//	throws Exception
//	{
//		calPanel = new JPanel();
//		calPanel.setLayout(new Layout());
//		calPanel.setBackground(Color.WHITE);
//		return calPanel;
//	}

    protected boolean supportsDrag() {
        return false;
    }


    private class Layout implements LayoutManager {
        public void addLayoutComponent(String name, Component comp) {
        }

        public void removeLayoutComponent(Component comp) {
        }

        public Dimension preferredLayoutSize(Container parent) {
            try {
                int width = 7 * getModel().getSelectedCalendars().size() * DayView.PREFERRED_DAY_WIDTH;
                return new Dimension(width, getPreferredHeight());
            } catch (Exception e) {
                throw BizcalException.create(e);
            }
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(50, 100);
        }

        /* (non-Javadoc)
           * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
           */
        public void layoutContainer(Container parent) {
            try {
                double width = parent.getWidth();
                width = width / getModel().getSelectedCalendars().size();
                width = width / 7;
                double height = parent.getHeight();
                height = height / cells.size();
                for (int row = 0; row < cells.size(); row++) {
                    List rowList = (List) cells.get(row);
                    for (int col = 0; col < rowList.size(); col++) {
                        JComponent cell = (JComponent) rowList.get(col);
                        cell.setBounds((int) (col * width + 1),
                                (int) (row * height + 1),
                                (int) width - 1,
                                (int) height - 1);
                    }
                }

                int colCount = getModel().getSelectedCalendars().size() * 7;
                for (int i = 0; i < colCount - 1; i++) {
                    try {
                        JLabel line = (JLabel) vLines.get(i);
                        line.setBounds((int) ((i + 1) * width),
                                0,
                                1,
                                parent.getHeight());
                    } catch (Exception e) {
//		        		e.printStackTrace();
                    }
                }
                int rowCount = cells.size() - 1;
                for (int i = 0; i < rowCount; i++) {
                    try {
                        JLabel line = (JLabel) hLines.get(i);
                        line.setBounds(0,
                                (int) ((i + 1) * height),
                                parent.getWidth(),
                                1);
                    } catch (Exception e) {

                    }
                }

            } catch (Exception e) {
                throw BizcalException.create(e);
            }
        }
    }

    private int getPreferredHeight() {
        return cells.size() * 40;
    }

    public JComponent getComponent() {
        return scrollPane;
    }

    public void addListener(CalendarListener listener) {
        super.addListener(listener);
        columnHeader.addCalendarListener(listener);
	}


}
