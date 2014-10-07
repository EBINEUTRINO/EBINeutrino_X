package ebiNeutrino.core.GUIRenderer.tableModelInterface;

import org.jdesktop.swingx.JXTable;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


public abstract class ExtTableModelListener implements TableModelListener {

    private JXTable tb = null;

    public ExtTableModelListener(JXTable tb){this.tb = tb;}

    public JXTable getParentTable(){ return this.tb;}

    public void tableChanged(TableModelEvent e){}

}
