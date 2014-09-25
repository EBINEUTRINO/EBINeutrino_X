package ebiNeutrinoSDK.utils;


import java.io.File;

/** Filter to work with JFileChooser to select java file types. **/
public class EBIJARFilter extends javax.swing.filechooser.FileFilter {
	
  public boolean accept (File f) {
    return f.getName ().toLowerCase ().endsWith (".jar") || f.isDirectory();
  }
 
  public String getDescription () {
    return "JAR (*.jar)";
  }
} // class JavaFilter
