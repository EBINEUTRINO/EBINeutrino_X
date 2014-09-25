package ebiNeutrinoSDK.utils;

import java.io.File;

/** Filter to work with JFileChooser to select jasper file types. **/
public class EBIReportFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".jasper") || f.isDirectory();
  }
 
  public String getDescription () {
    return "Reportdatei (*.jasper)";
  }
} // class JavaFilter