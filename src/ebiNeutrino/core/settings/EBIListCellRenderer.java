package ebiNeutrino.core.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class EBIListCellRenderer extends JPanel implements ListCellRenderer{
    private JLabel label = null;

    public EBIListCellRenderer(){

        super(new FlowLayout(FlowLayout.LEFT));

           setOpaque(true);
    
        label = new JLabel();
        label.setOpaque(false);
        add(label);                
    }

    public Component getListCellRendererComponent(JList list, 
                                                  Object value,
                                                  int index,    
                                                  boolean iss, 
                                                  boolean chf){
      
        label.setIcon(((EBIListItem)value).getIcon());
        label.setText(((EBIListItem)value).getText());

        if(iss) setBackground(new Color(0,22,255,20)); 
        else setBackground(list.getBackground()); 

        return this;
    }
}
