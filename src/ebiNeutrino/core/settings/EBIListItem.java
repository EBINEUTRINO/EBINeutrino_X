package ebiNeutrino.core.settings;

import javax.swing.ImageIcon;

public class EBIListItem
{
    // Das anzuzeigende Icon
    private ImageIcon icon = null;

    // Der Text
    private String text;

    public EBIListItem(ImageIcon iconpath, String text)
    {
        // Erzeugung des ImageIcons durch Angabe des Bild-Quellpfads
        icon = iconpath;

        // Zuweisung des Textes
        this.text = text;
    }      

    // Liefert das Icon
    public ImageIcon getIcon()
    {
    return icon;
    }
    
    // Liefert den Text
    public String getText()
    {
        return text;
    }
}