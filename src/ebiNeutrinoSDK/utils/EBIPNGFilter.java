package ebiNeutrinoSDK.utils;

import java.io.File;

/** Filter to work with JFileChooser to select png file types. **/
public class EBIPNGFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".png") || f.isDirectory();
  }
 
  public String getDescription () {
    return "PNG (*.png)";
  }
} // class JavaFilter
