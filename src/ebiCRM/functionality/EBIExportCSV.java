package ebiCRM.functionality;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import org.apache.commons.lang.StringEscapeUtils;


public class EBIExportCSV {

    private EBICRMModule ebiModule = null;
    private boolean isQuoteOpen=false;
    public String[] columnNames = null;
    public Object[][] data = null;
    private int columnCount = -1; 
    private int cellCount = 0;
    public boolean haveHeader = false;
    
    
    public EBIExportCSV(EBICRMModule ebiModule){
        this.ebiModule = ebiModule;
    }

    /**
     * Export database records as CSV file using JDBC
     * @param file
     * @param tableName
     * @param fieldsName
     * @param sortName
     * @param sortType
     * @param delimiter
     * @return  true successfully exported otherwise false
     * @throws IOException
     */
    public synchronized boolean exportCVS(String file,String tableName,Object[] fieldsName, String sortName,  boolean sortType,String delimiter) throws IOException {

        boolean ret = true;
        StringBuffer header = new StringBuffer();
        StringBuffer values = new StringBuffer();
        ResultSet set  = null;

        BufferedWriter out = null;
        try{

            String ascDesc = sortType == true ? "ASC" : "DESC";

            if(fieldsName != null){
                for(int i = 0; i<fieldsName.length; i++){
                  header.append(fieldsName[i].toString());
                  if(i < fieldsName.length-1 ) { 
                    header.append(delimiter);
                  }
                }
            }

            PreparedStatement prs = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT "+header.toString().replaceAll(delimiter, ",").toUpperCase()+" FROM "+tableName+" ORDER BY "+sortName+" "+ascDesc);
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(prs);

            if(set != null){

                FileWriter fstream = new FileWriter(file += "/"+tableName+".csv");
                out = new BufferedWriter(fstream);

                set.last();
                if(set.getRow() > 0){
                   set.beforeFirst();
                   while(set.next()){
                     for(int i = 0; i<fieldsName.length; i++){
                            
                            if(set.getString(fieldsName[i].toString()) != null){
                                if(!",".equals(delimiter) && set.getString(fieldsName[i].toString()).indexOf("\"") == -1 &&
                                        set.getString(fieldsName[i].toString()).indexOf(",") == -1){
                                  values.append("\"");
                                }

                                values.append(StringEscapeUtils.escapeCsv(set.getString(fieldsName[i].toString())));

                                if(!",".equals(delimiter) && set.getString(fieldsName[i].toString()).indexOf("\"") == -1 &&
                                                                            set.getString(fieldsName[i].toString()).indexOf(",") == -1){
                                    values.append("\"");
                                }
                            }
	                    	  
	                         if(i < fieldsName.length-1 ) {
	                    		   values.append(delimiter);
	                    	 }
	                       
                     }        
                     values.append(System.getProperty("line.separator"));
                   }
                }
                
                out.write(values.toString());
            }

        }catch(IOException ex){
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            ret = false;
        }catch(SQLException ex){
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            ret = false;
        }finally {
           if(out != null){
               out.close();
               try {
                  if(set != null){ 
                   set.close();
                  }
               } catch (SQLException ex) {
                   EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                   ret = false;
               }
           }
        }

        return ret; 
    }

    /**
     * Parse and import CSV file
     * @param file
     * @param delimiter
     * @return  true if CSV file is successfully imported otherwise false
     * @throws IOException
     */
    public synchronized boolean importCVS(String file, String delimiter) throws IOException {
        
        BufferedReader in = null;
        try {
               String line="";
               String xLine="";
               int count = 0;
               char[] buffer = new char[(int)(new File(file).length())];

                String ecoding =   ("English".equals(EBIPGFactory.selectedLanguage) || "German".equals(EBIPGFactory.selectedLanguage) ||
                                                                          "Italian".equals(EBIPGFactory.selectedLanguage) ) ?  "8859_1" : "utf-8";

                BufferedReader r2 = new BufferedReader(new InputStreamReader(new FileInputStream(file),ecoding));

                int bytesRead = 0;
                while ((bytesRead = r2.read(buffer)) != -1) {

                   if(haveHeader){
                     haveHeader = false;
                    
                     String[] hdr = line.replaceAll("\"", "").split(delimiter);
                     columnCount = hdr.length;
                     columnNames = new String[columnCount];
                     for(int i =0; i<columnCount; i++){
                    	 columnNames[i] =hdr[i].toString();
                     }
                     line = "";
                   }else{
                       xLine +=String.valueOf(buffer); // new String(buffer, 0, bytesRead);
                   }

                   ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setString("Reading: "+count++);
               }

               //xLine =
               //System.out.println(xLine);
               parseLine(xLine,delimiter);

               if(!haveHeader){
	               columnNames = new String[columnCount];
	               for(int i = 0; i<columnCount; i++){
	            	   columnNames[i] = "Col "+i;
	               }
               }
              // xLine = null;
               
        } catch (Exception ex) {
            ex.printStackTrace();
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
          
        } finally {
            if(in != null){
                in.close();
            }
        }

        return true;
    }

    public synchronized void parseLine(String line,String delimiter){
    	
    	int startOffset = 0;
        line = line.replaceAll("\r","");
    	int lenght =line.length();
    	int countRow = 0;
        int charCount = line.length() - line.replaceAll("\n", "").length();

    	Object[] row = new Object[1];
        int ni =0;
        int oi =0;
        int ccx = 0;
    	for(int i=0; i<=lenght; i++){

    		if(i+1 <=lenght){ 
		    	if(line.substring(i,i+1).equals("\"")){
		    		 if(isQuoteOpen){
                         isQuoteOpen = false;
		    		 }else{
		    			 isQuoteOpen=true;
		    		 }
		    	}
                if(!isQuoteOpen){
			      if(line.substring(i,i+1).equals(delimiter) && !isQuoteOpen){
		    		 //Add
			    	 if(columnCount == -1){
				    	 if(cellCount == 0){
                             ni = i;
                             oi = startOffset;
                             if("\"".equals(line.substring(oi,oi+1)) && "\"".equals(line.substring(ni-1,ni))){
                                 oi+=1;
                                 ni = i -1;
                             }

				    		 row[cellCount] = StringEscapeUtils.unescapeCsv(line.substring(oi, ni));
				    	 }else{
				    		 Object[] tmp_r = new Object[cellCount+2];
				    		 for(int r=0; r<row.length; r++){
				    			 tmp_r[r] = row[r];
				    		 }
				    		 row = tmp_r;

                             ni = i;
                             oi = startOffset;
                             if("\"".equals(line.substring(oi,oi+1)) && "\"".equals(line.substring(ni-1,ni))){
                                 oi+=1;
                                 ni = i -1;
                             }

                             row[cellCount] = StringEscapeUtils.unescapeCsv(line.substring(oi, ni));
				    	 }
			    	 }else{

                         ni = i;
                         oi = startOffset;
                         if("\"".equals(line.substring(oi,oi+1)) && "\"".equals(line.substring(ni-1,ni))){
                             oi+=1;
                             ni = i -1;
                         }

                         row[cellCount] = StringEscapeUtils.unescapeCsv(line.substring(oi, ni));
			    	 }
		    		 startOffset = i+1;
		    		 ++cellCount;
		    		 
	    		 }else if(line.substring(i,i+1).equals("\n") && !isQuoteOpen ){ // OF ROW
	    			 //Add

                    ebiModule.guiRenderer.getProgressBar("importProgress","dataImportDialog").setString("Parsing: "+charCount+" - "+ccx++);
                    ni = i;
                    oi = startOffset;

                    if("\"".equals(line.substring(oi,oi+1)) && "\"".equals(line.substring(ni-1,ni))){
                        oi+=1;
                        ni = i -1;
                    }

                    row[cellCount] = StringEscapeUtils.unescapeCsv(line.substring(oi, ni));

		    		 startOffset = i+1;
		    		 ++cellCount;
		    		 if(columnCount == -1){
		    			 columnCount = cellCount;
		    		 }
	    		 }
            }
	    	}else{

                ni =lenght;
                oi = startOffset;
               if("\"".equals(line.substring(ni-1,ni))){
                    oi+=1;
                    ni = i -1;
                }

                row[cellCount] = StringEscapeUtils.unescapeCsv(line.substring(oi, ni));

	    		 startOffset = i+1;
	    		 ++cellCount;
	    	}

	    	if(cellCount == columnCount){
	    		 
	    		 if(countRow == 0){
	    		  data = new Object[countRow+1][columnCount];
	    		  data[countRow] = row;
	    		 }else{
	    			 
	    			 Object[][] tmp_rows = new Object[countRow+1][columnCount];
	    			 
	    			 for(int a = 0; a<data.length; a++){
	    				 tmp_rows[a] = data[a];
	    			 }
	    			 
	    			 tmp_rows[countRow] = row;	  
	    			 data = tmp_rows;
	    		 }
	    		  ++countRow;
	    		  row = new Object[columnCount];
	    		  cellCount=0;
	    	 }
	    	 
    	}
    	
    }

}