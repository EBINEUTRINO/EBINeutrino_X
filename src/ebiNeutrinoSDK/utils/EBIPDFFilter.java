package ebiNeutrinoSDK.utils;

import java.io.File;

	/** Filter to work with JFileChooser to select pdf file types. **/
public class EBIPDFFilter extends javax.swing.filechooser.FileFilter {
		
	  public boolean accept (File f) {
	    return f.getName ().toLowerCase ().endsWith (".pdf") || f.isDirectory();
	  }
	 
	  public String getDescription () {
	    return "PDF (*.pdf)";
	  }
	} // class JavaFilter