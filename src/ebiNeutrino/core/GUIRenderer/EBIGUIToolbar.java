package ebiNeutrino.core.GUIRenderer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class EBIGUIToolbar {

    private int id = 0;
    private String name = "";
    private String title = "";
    private String type = "";
    private String toolTip = "";
    private ImageIcon icon = null;
    private boolean isVisualPanel = false;
    private List<EBIGUIToolbar> barItem = null;
    private JComponent component = null;
    private KeyStroke keyStroke = null;



    public EBIGUIToolbar(){
        barItem = new ArrayList<EBIGUIToolbar>();
    }


    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public void setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public List<EBIGUIToolbar> getBarItem() {
        return barItem;
    }

    public void setBarItem(EBIGUIToolbar barItem) {
        this.barItem.add(barItem);
    }

    public boolean isVisualPanel() {
        return isVisualPanel;
    }

    public void setVisualPanel(boolean visualPanel) {
        isVisualPanel = visualPanel;
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setToolTip(String toolTip){
        this.toolTip = toolTip;
    }

    public String getToolTip(){
        return this.toolTip;
    }
}
