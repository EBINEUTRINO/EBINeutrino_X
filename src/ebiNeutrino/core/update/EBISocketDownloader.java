package ebiNeutrino.core.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class EBISocketDownloader {

    private InputStream is 					   = null;
    public  Document xmlOnline = null;
    public  Document xmlLocal = null;
    public  String 	localVer				   = "";
    public  String 	onlineVer				   = "";
    public  List<Object>arrList				   = null;
    private String[] strArr					   = null;
    private boolean fileLocalExist			   = true;
    private boolean fileOnlineExist		       = true;
    public String SysPath					   = "";
	
	public EBISocketDownloader(){
		arrList = new ArrayList<Object>(); 
	}
	
	public void setConnection(){

		try{
                // Read file on server
				URL u=new URL(SysPath+"/update.xml");
		        is = u.openStream();    
		        
				try{

		     		SAXBuilder builder = new SAXBuilder();
		     		xmlOnline = builder.build(is);
		     	
		     	}catch(IOException e){
		     		 fileOnlineExist = false;
		     	}catch(JDOMException ex){
		     		 fileOnlineExist = false;
		     	}

                 // Read local file
		     	try{   
		     	 
			     	SAXBuilder builder1 = new SAXBuilder();
		     		xmlLocal = builder1.build(new File("update.xml"));
                      
				}catch(IOException e){
					 fileLocalExist = false;
				}catch(JDOMException ex){
		    		 fileLocalExist = false;
		    	}

			}catch (MalformedURLException ex) {}catch (IOException ioe) {}finally {
		         try {
                     if(is != null){
		                is.close();
                     }
		         } catch (IOException ioe) {
		           ioe.printStackTrace();
		         }
		    }
	}
	
	public boolean readConfig(){

      boolean ret = false;
      try{

          if(fileLocalExist  && fileOnlineExist){

              if(xmlLocal != null && xmlOnline != null){
                  if(!xmlLocal.getRootElement().getAttribute("Version").getValue().equals(xmlOnline.getRootElement().getAttribute("Version").getValue())){
                     onlineVer = xmlOnline.getRootElement().getAttribute("Version").getValue();
                     localVer  = xmlLocal.getRootElement().getAttribute("Version").getValue();
                     processElement(xmlOnline.getRootElement());
                     ret = true;
                  }
              }else{
                  ret = false;
              }

          }else{
              ret = false;
          }
      }catch(Exception ex){
          ex.printStackTrace();
         ret= false;
      }

	  return ret;
	}

	private void processElement(Element el) {

	     processAttributes1(el);

		 Iterator children = el.getChildren().iterator();

		 while(children.hasNext()){
		 	processElement((Element)children.next());
		 }

	}
	
	private void processAttributes1(Element el) {

        Iterator atts = el.getAttributes().iterator();

        if(!el.getName().equals("EBINeutrinoUpdate")){
            strArr = new String[4];
        }else{
            return;
        }

         while(atts.hasNext()){

             Attribute att = (Attribute) atts.next();
             if(att.getName().equals("Name") ){
                 strArr[0] = att.getValue();
             }

             if(att.getName().equals("FileName") ){
                 strArr[1] = att.getValue();
             }

             if(att.getName().equals("Version") ){
                 strArr[2] = att.getValue();
             }

             if(att.getName().equals("Destination") ){
                 strArr[3] = att.getValue();
             }

         }

          if(!el.getName().equals("EBINeutrinoUpdate")){
              this.arrList.add(strArr);
          }


	}
	
	public InputStream retriveData(String fileName){

        InputStream isDown = null;

		try{

			URL urlDown=new URL(SysPath +"/"+ fileName);
	        isDown = urlDown.openStream();
		
		}catch (MalformedURLException ex) {

	        ex.printStackTrace();

	    }catch (IOException ex) {

	         ex.printStackTrace();

        }

	    return isDown;
	}
	
	public void writeFileTo(InputStream stream, File pathDest){
	  
		try {

	         FileOutputStream streamOut = new FileOutputStream(pathDest.getAbsolutePath());

	         int c;

	         while ((c = stream.read()) != -1) {
	            streamOut.write(c);
	         }

	         stream.close();
	         streamOut.close();

	      }catch (FileNotFoundException ex) {

	          ex.printStackTrace();

	      } catch (IOException ex) {
            
	    	   ex.printStackTrace();

	      }
	}
	
}
