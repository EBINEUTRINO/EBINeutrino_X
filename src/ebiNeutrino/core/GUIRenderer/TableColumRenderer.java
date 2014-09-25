package ebiNeutrino.core.GUIRenderer;


import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
public class TableColumRenderer extends  EBIButton implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {

        setDrawBorder(false);
        setText(value.toString());
        //setOpaque(false);

        return this;
    }
}