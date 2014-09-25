package ebiNeutrino.core.GUIRenderer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class EBIGUIMenus {

    private int id = 0;
    private String name ="";
    private String type = "";
    private String title ="";
    private ImageIcon icon=null;
    private int KeyEvent = 0;
    private boolean isVisualPanel = false; 
    private javax.swing.KeyStroke keyStroke = null;
    private List<EBIGUIMenus> subMenu = null;
    private JComponent component = null;
    private Boolean isCheckable = false;

    public EBIGUIMenus(){
        subMenu = new ArrayList<EBIGUIMenus>();
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public int getKeyEvent() {
        return KeyEvent;
    }

    public void setKeyEvent(int keyEvent) {
        KeyEvent = keyEvent;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public void setKeyStroke(KeyStroke keyStroke ) {
        this.keyStroke =  keyStroke;
    }

    public List<EBIGUIMenus> getMenuItem() {
        return subMenu;
    }

    public void setMenuItem(EBIGUIMenus menuItem) {
        this.subMenu.add(menuItem);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Boolean getCheckable() {
        return isCheckable;
    }

    public void setCheckable(Boolean checkable) {
        isCheckable = checkable;
    }
}
