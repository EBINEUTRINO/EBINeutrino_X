package ebiNeutrino.core;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import ebiNeutrino.core.gui.Dialogs.EBIReportSelection;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.gui.dialogs.EBIWinWaiting;
import ebiNeutrinoSDK.interfaces.IEBIReportSystem;

/**
 * 
 * Reportsystem class
 * Initialize JasperReport, Managing Reports files
 */

public class EBIReportSystem implements IEBIReportSystem{

	public EBIPGFactory ebiPGFunction = null;
	public Object[] report = null;
	public Map<String,Object> map = null;
	public String fileName = "";
	public boolean showWindow = false;
    public boolean eMailRecord = false;
    public String strRecs = null;
	private EBIWinWaiting wait = new EBIWinWaiting(EBIPGFactory.getLANG("EBI_LANG_LOAD_REPORT_DATA"));
	
	
	public EBIReportSystem(EBIPGFactory ebiPGFunc){
		ebiPGFunction = ebiPGFunc;
	}

    /**
     * Check for all available reports
     * @param map
     * @return
     */
	
	public Object[] checkForReport(Map<String,Object> map){
		EBIReportSelection reportSelection;
		
	    report = new Object[3];
		if(getReportCount() > 0){

			reportSelection	= new EBIReportSelection(this);
			reportSelection.setModal(true);
			reportSelection.setResizable(false);
			reportSelection.setVisible(true);


		}else{
			report[0] = "";
			report[1] = false;
            EBIExceptionDialog.getInstance().info(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_NOT_REPORT_FOUND"));
		}
		return report;
	}

    /**
     * Check for available reports with a specified category
     * @param map
     * @param category
     * @return
     */
		
	public Object[] checkForReport(Map<String,Object> map, String category){

		EBIReportSelection reportSelection;

	    report = new Object[3];
		if(getReportCount(category) > 0){

			reportSelection	= new EBIReportSelection(this,category);
		    if(getReportCount(category) > 1){
               reportSelection.setModal(true);
			   reportSelection.setResizable(false);
			   reportSelection.setVisible(true);
            }else{
            	reportSelection.createReport();
            }
		    
		}else{
            EBIExceptionDialog.getInstance().info(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_NOT_REPORT_FOUND"));
			report[0] = "";
			report[1] = false;
		}
		return report;
	}

    /**
     * get all available reports
     * @return
     */

	public int getReportCount(){
         int count = 0;
            ResultSet set = null;
            try{

                PreparedStatement ps1 = ebiPGFunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM SET_REPORTFORMODULE WHERE ISACTIVE <> 0 ORDER BY REPORTNAME ");
                set = ebiPGFunction.getIEBIDatabase().executePreparedQuery(ps1);
                set.last();
                count = set.getRow();

            }catch(SQLException ex){
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }finally{
                if(set != null){
                    try {
                        set.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return  count;
       }

    /**
     * get available reports from a category
     * @param category
     * @return
     */
    public int getReportCount(String category){
			 int count = 0;
				ResultSet set = null;
				try{
                    PreparedStatement ps1 = ebiPGFunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM SET_REPORTFORMODULE WHERE ISACTIVE <> 0 and REPORTCATEGORY=? ORDER BY REPORTNAME ");
                    ps1.setString(1,category);
                    set = ebiPGFunction.getIEBIDatabase().executePreparedQuery(ps1);
                    set.last();
                    count = set.getRow();
				}catch(SQLException ex){
					EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
				}finally{
                    if(set != null){
                        try {
                            set.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
				return  count;
    }
    
    //overloading method 1
    
    public synchronized void useReportSystem(final Map<String,Object> map){
    	this.map = map;
        eMailRecord = false;
    	report= checkForReport(map);
    	
        if(report == null){
           return;
        }
    }

    /**
     * Create reports show report select dialog
     * @param map
     */
	
	public synchronized void useReportSystemExt(final Map<String,Object> map){

        final Runnable run = new Runnable(){
            
            public void run() {
                try{
                	wait.setVisible(true);
                    if(!"".equals(report[0])){

                        //if no report was selected release this method
                        if("-1".equals(report[0].toString())){
                            return;
                        }

                        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("reports/"+report[0].toString()).getPath());
                        addParametertoReport(map);
                        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, ebiPGFunction.getIEBIDatabase().getActiveConnection());

                        if((Boolean)report[1] == true){
                            String fileN = report[0].toString().replaceAll("[^\\p{L}\\p{N}]", "");
                            JasperExportManager.exportReportToPdfFile(jasperPrint, "tmp/"+fileN+".pdf");
                            ebiPGFunction.openPDFReportFile("tmp/"+fileN+".pdf");

                        }else{
                            JasperViewer.setDefaultLookAndFeelDecorated(false);
                            JasperViewer view = new JasperViewer(jasperPrint,false);
                            view.setState(JasperViewer.MAXIMIZED_BOTH);
                            view.setVisible(true);
                        }

                    }
                } catch(Exception ex) {
                	ex.printStackTrace();
                   EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                }finally {
                   if(wait != null){
                       wait.setVisible(false);
                   }
                }
           }
        };

        Thread loaderThread = new Thread(run, "ShowReportThread");
        loaderThread.start();
    }
	
	//overloading Method 2
	public synchronized void useReportSystem(final Map<String,Object> map,final String category,final String fileName){
        this.map = map;
		this.fileName = fileName;
        eMailRecord = false;
    	report= checkForReport(map,category);
    	
        if(report[0] == null){
           return;
        }
	}
    /**
     * Create and show reports
     * @param map
     * @param category
     * @param fileName
     */
	
	public synchronized void useReportSystemExt(final Map<String,Object> map,final String category,final String fileName){

       final Runnable run = new Runnable(){

           public void run() {
               try{
                    wait.setVisible(true);

                    if(!"".equals(report[0])){

                                //if no report was selected release this method
                                if("-1".equals(report[0].toString())){
                                    return;
                                }

                                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File("reports/"+report[0].toString()).getPath());
                                addParametertoReport(map);
                                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, ebiPGFunction.getIEBIDatabase().getActiveConnection());
                                
                                if((Boolean)report[1] == true){
                                    String fN = fileName.replaceAll("[^\\p{L}\\p{N}]", "");
                                    JasperExportManager.exportReportToPdfFile(jasperPrint, "tmp/"+fN+".pdf");
                                    ebiPGFunction.openPDFReportFile("tmp/"+fN+".pdf");

                                }else{
                                    JasperViewer.setDefaultLookAndFeelDecorated(false);
                                    JasperViewer view = new JasperViewer(jasperPrint,false);
                                    view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                    view.setState(JasperViewer.MAXIMIZED_BOTH);
                                    view.setVisible(true);
                                }

                    }
                } catch(Exception ex) {
                     EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                     ex.printStackTrace();
                }finally {
                   if(wait != null) {
                       wait.setVisible(false);
                   }
               }
          }
       };

        Thread loaderThread = new Thread(run, "ShowReportThread");
        loaderThread.start();
    }
	
	// overloading method 3
	
	public String useReportSystem(final Map<String,Object> map,final String category,final String fileName, final boolean showWindows, final boolean EMailRecord,final String rec){
		
        final Runnable run = new Runnable(){
            public void run(){
                EBIReportSystem.this.map = map;
                EBIReportSystem.this.fileName = fileName;
                showWindow = showWindows;
                eMailRecord = EMailRecord;
                strRecs = rec;
                Object[] report = checkForReport(map, category);
                if(report[0] == null){
                    return;
                }
            }
        };
        
        Thread startReporting = new Thread(run,"Start Reporting");
        startReporting.start();
        
        return fileName;
	}

    /**
     * Create and show reports
     * @param map
     * @param category
     * @param fileName
     * @param showWindows
     * @param EMailRecord
     * @return
     */

    public String useReportSystemExt(Map<String,Object> map,String category,String fileName,
                                                            boolean showWindows, boolean EMailRecord,String rec){

		String fileToRet;
        try{
             wait.setVisible(true);
             if(!"".equals(report[0])){

                    //if no report was selected release this method
                    if("-1".equals(report[0].toString())){
                        return "-1";
                    }

                    File reportFile = new File("reports/"+report[0].toString());

                    JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
                    addParametertoReport(map);
                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, ebiPGFunction.getIEBIDatabase().getActiveConnection());

                    if((Boolean)report[1] == true){
                    	fileName = fileName.replaceAll("[^\\p{L}\\p{N}]", "");

                        fileToRet = "tmp/"+fileName+".pdf";
                        JasperExportManager.exportReportToPdfFile(jasperPrint,fileToRet);

                        if(showWindows){
                            ebiPGFunction.openPDFReportFile(fileToRet);
                        }
                    }else{
                        fileName = fileName;
                        fileToRet =  "tmp/"+fileName+".pdf";
                        JasperExportManager.exportReportToPdfFile(jasperPrint, fileToRet);

                        if(showWindows){
                            ebiPGFunction.openPDFReportFile(fileToRet);
                        }
                    }

                  if(EMailRecord){
                      ebiPGFunction.sendEMail(rec,"","subject","Body", fileToRet);
                  }

            }else{
                fileToRet = "-1";
            }

        } catch(Exception ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            fileToRet = "-1";
        }finally{
            wait.setVisible(false);
        }

        return fileToRet;

    }


    /**
     * Add system param to the report
     * @param map
     */
    public void addParametertoReport(Map<String, Object> map) {
        
        ResultSet set = null;
        ResultSet set1 = null;

        map.put("EBI_LANG",ebiPGFunction.getPropertiesLanguage());
        map.put("EBI_ISB2C", EBIPGFactory.USE_ASB2C);

        try {

            PreparedStatement ps1 = ebiPGFunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANY com " +
                    "LEFT JOIN COMPANYBANK bnk ON com.COMPANYID=bnk.COMPANYID WHERE com.ISACTUAL=? ");
    
            ps1.setInt(1,1);

            set = ebiPGFunction.getIEBIDatabase().executePreparedQuery(ps1);

                set.last();
                if (set.getRow() > 0) {
                    set.beforeFirst();
                    set.next();
                    map.put("COMPANY_NAME", set.getString("NAME"));
                    map.put("COMPANY_NAME1", set.getString("NAME2"));
                    map.put("COMPANY_TELEPHONE", set.getString("PHONE"));
                    map.put("COMPANY_FAX", set.getString("FAX"));
                    map.put("COMPANY_EMAIL", set.getString("EMAIL"));
                    map.put("COMPANY_WEB", set.getString("WEB"));


                    map.put("COMPANY_BANK_NAME", set.getString("BANKNAME"));
                    map.put("COMPANY_BANK_ACCOUNT_NR", set.getString("BANKACCOUNT"));
                    map.put("COMPANY_BANK_BSB", set.getString("BANKBSB"));
                    map.put("COMPANY_BANK_BIC", set.getString("BANKBIC"));
                    map.put("COMPANY_BANK_IBAN", set.getString("BANKIBAN"));
                    map.put("COMPANY_BANK_COUNTRY", set.getString("BANKCOUNTRY"));
                    map.put("COMPANY_TAX_INFORMATION", set.getString("TAXNUMBER"));
                    int companyID = set.getInt("COMPANYID");
                    set.close();

                    PreparedStatement ps2 = ebiPGFunction.getIEBIDatabase().initPreparedStatement("SELECT * FROM COMPANYCONTACTS con " +
                            " LEFT JOIN COMPANYCONTACTADDRESS cadr ON con.CONTACTID=cadr.CONTACTID  WHERE con.COMPANYID=? AND con.MAINCONTACT=? ");
                    ps2.setInt(1,companyID);
                    ps2.setInt(2,1);
          
                    set1 = ebiPGFunction.getIEBIDatabase().executePreparedQuery(ps2);

                    set1.last();
                    if (set1.getRow() > 0) {
                        set1.beforeFirst();
                        set1.next();
                        map.put("COMPANY_CONTACT_NAME", set1.getString("NAME") == null ? "" : set1.getString("NAME"));
                        map.put("COMPANY_CONTACT_SURNAME", set1.getString("SURNAME") == null ? "" : set1.getString("SURNAME"));
                        map.put("COMPANY_CONTACT_POSITION", set1.getString("POSITION") == null ? "" : set1.getString("POSITION"));
                        map.put("COMPANY_CONTACT_EMAIL", set1.getString("EMAIL") == null ? "" : set1.getString("EMAIL"));
                        map.put("COMPANY_CONTACT_TELEPHONE", set1.getString("PHONE") == null ? "" : set1.getString("PHONE"));
                        map.put("COMPANY_CONTACT_FAX", set1.getString("FAX") == null ? "" : set1.getString("FAX"));
                        map.put("COMPANY_STR_NR", set1.getString("STREET") == null ? "" : set1.getString("STREET"));
                        map.put("COMPANY_ZIP", set1.getString("ZIP") == null ? "" : set1.getString("ZIP"));
                        map.put("COMPANY_LOCATION", set1.getString("LOCATION") == null ? "" : set1.getString("LOCATION"));
                    }
                }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                set.close();
                if (set1 != null) {
                    set1.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    

}
