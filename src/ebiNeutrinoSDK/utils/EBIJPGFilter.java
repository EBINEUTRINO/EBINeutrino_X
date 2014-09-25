package ebiNeutrinoSDK.utils;

import java.io.File;

/** Filter to work with JFileChooser to select jpg file types. **/
public class EBIJPGFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".jpg") || f.isDirectory();
  }
 
  public String getDescription () {
    return "JPG (*.jpg)";
  }
} // class JavaFilter