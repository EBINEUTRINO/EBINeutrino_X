package ebiNeutrinoSDK.utils;

import java.io.File;

/** Filter to work with JFileChooser to select java file types. **/
public class EBIGIFFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".gif") || f.isDirectory();
  }
 
  public String getDescription () {
    return "GIF (*.gif)";
  }
} // class JavaFilter