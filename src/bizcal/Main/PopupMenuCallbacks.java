package bizcal.Main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bizcal.common.Event;
import bizcal.swing.PopupMenuCallback;
import ebiNeutrinoSDK.EBIPGFactory;

public class PopupMenuCallbacks extends PopupMenuCallback.BaseImpl {

    public JPopupMenu popup = null;
    public JMenuItem newItem = null;
    public JMenuItem editItem = null;
    public JMenuItem copyItem = null;
    public JMenuItem cutItem = null;
    public JMenuItem pasteItem = null;
    public JMenuItem deleteItem = null;
    private BizcalMain main = null;


    public PopupMenuCallbacks(BizcalMain main) {
        this.main = main;
        init();
    }

    private void init() {
        popup = new JPopupMenu();

        newItem = new JMenuItem();
        newItem.setText(EBIPGFactory.getLANG("EBI_LANG_NEW"));
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    main.newEvent(main.calmodel.getEvents(1), main.selectedDate == null ? new Date() : main.selectedDate, new Date());
                } catch (Exception e) {
                }
            }
        });

        editItem = new JMenuItem();
        editItem.setText(EBIPGFactory.getLANG("EBI_LANG_EDIT"));
        editItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {

                    main.editEvent();
                } catch (Exception e) {
                }
            }
        });
        copyItem = new JMenuItem();
        copyItem.setText(EBIPGFactory.getLANG("EBI_LANG_COPY"));
        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    main.copyEvent(true);
                } catch (Exception e) {
                }
            }
        });
        cutItem = new JMenuItem();
        cutItem.setText(EBIPGFactory.getLANG("EBI_LANG_CUT"));
        cutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    main.cutEvent();
                } catch (Exception e) {
                }
            }
        });
        pasteItem = new JMenuItem();
        pasteItem.setText(EBIPGFactory.getLANG("EBI_LANG_PASTE"));
        pasteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    main.pasteEvent();
                } catch (Exception e) {
                }
            }
        });
        pasteItem.setEnabled(false);
        deleteItem = new JMenuItem();
        deleteItem.setText(EBIPGFactory.getLANG("EBI_LANG_DELETE"));
        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    main.deleteEvent();
                } catch (Exception e) {
                }
            }
        });

        popup.add(newItem);
        popup.add(editItem);
        popup.addSeparator();
        popup.add(copyItem);
        popup.add(cutItem);
        popup.add(pasteItem);
        popup.addSeparator();
        popup.add(deleteItem);

    }

    public JPopupMenu getEventPopupMenu(Object calId, Event event)
            throws Exception {
        return popup;
    }

    public JPopupMenu getCalendarPopupMenu(Object calId) throws Exception {
        return popup;
    }

    public JPopupMenu getProjectPopupMenu(Object calId) throws Exception {
        return popup;
    }

    public JPopupMenu getEmptyPopupMenu(Object calId, Date date) throws Exception {
        return popup;
    }

}
