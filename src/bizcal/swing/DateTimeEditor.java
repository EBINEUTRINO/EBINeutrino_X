package bizcal.swing;

import java.text.DateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;

import bizcal.swing.util.TableLayoutPanel;
import bizcal.swing.util.TableLayoutPanel.Row;
import bizcal.util.DateUtil;
import bizcal.util.LocaleBroker;
import bizcal.util.TimeOfDay;

public class DateTimeEditor {
    private TableLayoutPanel panel;
    private JFormattedTextField dateField;
    private JFormattedTextField timeField;

    public DateTimeEditor()
            throws Exception {
        panel = new TableLayoutPanel();
        panel.createColumn();
        panel.createColumn(10);
        panel.createColumn();
        Row row = panel.createRow();

        DateFormat dateFormat =
                DateFormat.getDateInstance(DateFormat.SHORT, LocaleBroker.getLocale());
        DateFormat timeFormat =
                DateFormat.getTimeInstance(DateFormat.SHORT, LocaleBroker.getLocale());
        dateField = new JFormattedTextField(dateFormat);
        timeField = new JFormattedTextField(timeFormat);
        row.createCell(dateField);
        row.createCell();
        row.createCell(timeField);
    }

    public void setValue(Date date) {
        dateField.setValue(date);
        timeField.setValue(date);
    }

    public Date getValue()
            throws Exception {
        Date date = (Date) dateField.getValue();
        Date time = (Date) timeField.getValue();
        TimeOfDay timeOfDay = DateUtil.getTimeOfDay(time);
        date = timeOfDay.getDate(date);
        return date;
    }

    public JComponent getComponent() {
        return panel;
    }

}
