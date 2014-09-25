package ebiNeutrinoSDK.gui.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.*;

import ebiNeutrinoSDK.EBIPGFactory;
import org.jdesktop.swingx.JXPanel;

public class EBIVisualPanelTemplate extends EBIVisualPanel implements ComponentListener {

    private EBIContentPanel panel = new EBIContentPanel();
    private JPanel tPanel = new JXPanel();
    private EBIPGFactory ebiPGFactory = null;


    public EBIVisualPanelTemplate(){
        super();
        tPanel.setDoubleBuffered(true);
        panel.setDoubleBuffered(true);
        tPanel.setLayout(null);
        //panel.setLayout(null);
        tPanel.setLocation(0, 21);
       // tPanel.add(panel,BorderLayout.CENTER);
        super.add(tPanel, null);

        addComponentListener(this);

    }

    public EBIVisualPanelTemplate(EBIPGFactory ebiPGFactory){
        super();
        tPanel.setDoubleBuffered(true);
        panel.setDoubleBuffered(true);
        tPanel.setLayout(null);
        //panel.setLayout(null);
        tPanel.setLocation(0, 21);
        // tPanel.add(panel,BorderLayout.CENTER);
        super.add(tPanel, null);

        addComponentListener(this);
    }


    public void componentResized(ComponentEvent e){
        tPanel.setSize(((EBIVisualPanel)e.getSource()).getWidth(), ((EBIVisualPanel)e.getSource()).getHeight()-20);
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
