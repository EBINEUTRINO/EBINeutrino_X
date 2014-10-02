package ebiNeutrino.core.settings;

import javax.swing.ImageIcon;

public class EBIListItem{

    private ImageIcon icon = null;
    private String text;
    private String path = "";
    private String modName = "";
    private boolean isApp=false;

    public EBIListItem(ImageIcon iconpath, String text, String pt, String mName, boolean iapp){
        icon = iconpath;
        this.text = text;
        this.path = pt;
        this.modName = mName;
        this.isApp = iapp;
    }

    public EBIListItem(ImageIcon iconpath, String text){
        icon = iconpath;
        this.text = text;
    }


    public ImageIcon getIcon()
    {
    return icon;
    }
    public String getText()
    {
        return text;
    }
    public String getPath() { return path; }
    public String getModname() { return modName; }
    public boolean isApp() { return isApp; }
}