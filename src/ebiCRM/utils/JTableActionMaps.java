/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ebiCRM.utils;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

import org.jdesktop.swingx.JXTable;

/**
 *
 * @author francesco
 */
public class JTableActionMaps {

    private JXTable table = null;

    public JTableActionMaps(JXTable tb) {
        this.table = tb;

    }

    public void setTableAction(final AbstractTableKeyAction act) {

        InputMap im = table.getInputMap(JXTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        KeyStroke down = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        KeyStroke up = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

        final Action oldDown = table.getActionMap().get(im.get(down));
        Action downAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                oldDown.actionPerformed(e);
                JXTable table = (JXTable) e.getSource();
                int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());

                act.setDownKeyAction(selectedRow);

            }
        };
        table.getActionMap().put(im.get(down), downAction);

        final Action oldUp = table.getActionMap().get(im.get(up));
        Action upAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                oldUp.actionPerformed(e);
                JXTable table = (JXTable) e.getSource();
                int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());

                act.setUpKeyAction(selectedRow);

            }
        };

        table.getActionMap().put(im.get(up), upAction);


        //final Action oldEnter = table.getActionMap().get(im.get(enter));
        Action enterAction = new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                //oldEnter.actionPerformed(e);
                JXTable table = (JXTable) e.getSource();
                int selectedRow = table.convertRowIndexToModel(table.getSelectedRow());

                act.setEnterKeyAction(selectedRow);

            }
        };

        table.getActionMap().put(im.get(enter), enterAction);

    }
}
