package ebiNeutrinoSDK.gui.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;
import ebiNeutrinoSDK.EBIPGFactory;
import org.jdesktop.swingx.JXPanel;

public class EBIVisualPanelTemplate extends EBIVisualPanel implements ComponentListener {

    private JXPanel panel = new JXPanel();
    private JXPanel tPanel = new JXPanel();
    private EBIPGFactory ebiPGFactory = null;

    public EBIVisualPanelTemplate(boolean showStripe){
        super(showStripe);
        tPanel.setDoubleBuffered(true);
        panel.setDoubleBuffered(true);
        tPanel.setLayout(null);
        if(showStripe) {
            tPanel.setLocation(0, 21);
        }else{
            tPanel.setLocation(0, 0);
        }
        super.add(tPanel, null);

        addComponentListener(this);
        super.repaint();

    }

    public EBIVisualPanelTemplate(boolean showStripe, EBIPGFactory ebiPGFactory){
        super(showStripe);
        tPanel.setDoubleBuffered(true);
        panel.setDoubleBuffered(true);
        tPanel.setLayout(null);
        if(showStripe) {
            tPanel.setLocation(0, 21);
        }else{
            tPanel.setLocation(0, 0);
        }
        super.add(tPanel, null);
        addComponentListener(this);
        super.repaint();
    }

    public void componentResized(ComponentEvent e){
        if(isShowStripe()) {
            tPanel.setSize(((EBIVisualPanel)e.getSource()).getWidth(), ((EBIVisualPanel)e.getSource()).getHeight()-20);
        }else{
            tPanel.setSize(((EBIVisualPanel)e.getSource()).getWidth(), ((EBIVisualPanel)e.getSource()).getHeight());
        }

    }

    public void componentMoved(ComponentEvent e){

    }

    public void componentShown(ComponentEvent e){

    }

    public void componentHidden(ComponentEvent e){

    }

    public void setEnableChangeComponent(boolean enabled){
         super.setChangePropertiesVisible(enabled);
    }

    public void add(JComponent component, Object obj){
        tPanel.add(component, obj);
    }

    @Override
    public void setSize(Dimension dim){
        tPanel.setSize(dim);
        super.setSize(dim);
    }

    @Override
    public void setBackground(Color col){
        super.setBackground(col);
        setBackgroundColor(col);
    }

    public void setBackgroundColor(Color color){
        if(tPanel != null){
           tPanel.setBackground(color);
           tPanel.updateUI();
        }
    }

    public JPanel getPanel(){
        return this.tPanel;
    }
}
