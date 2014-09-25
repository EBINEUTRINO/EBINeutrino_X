package ebiNeutrinoSDK.utils;

import java.io.File;

/** Filter to work with JFileChooser to select jpg file types. **/
public class EBISQLFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".sql") || f.isDirectory();
  }
 
  public String getDescription () {
    return "SQL (*.sql)";
  }
} // class JavaFilter