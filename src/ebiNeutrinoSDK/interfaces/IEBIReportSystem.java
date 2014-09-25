package ebiNeutrinoSDK.interfaces;

import java.util.Map;

/**
 * This interface allow you to use the standard EBI Neutrino R1 report system
 * it extends the jasper report framework
 *  
 * 
 */

public interface IEBIReportSystem {
	
	/**
	 * Show you a dialog for selecting all available reports
	 */
	public void useReportSystem(Map<String,Object> map);
	
	/**
	 * show the selected report category with report parameters
	 * @param map
	 * @param category
	 */
	public void useReportSystem(Map<String,Object> map,String category, String fileName);

    /**
     * show the specified report with parameter
     * @param map
     * @param category
     * @param fileName
     * @param createOnlyReportDontShowWindow
     * @return
     */
    public String useReportSystem(Map<String,Object> map,String category,String fileName,boolean createOnlyReportDontShowWindow, boolean MailRecord, String recs);
	
}
