package ebiNeutrino.core.run;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class EBIRunSystem {

    private int id=0;
    private int WINDOWS_64=0, WINDOWS=1,LINUX_64=2,LINUX=3,MACOS_64=4,MACOS=5;

    public EBIRunSystem(){}

    public static void main(String[] arg){

      EBIRunSystem sys = new EBIRunSystem();
      String[] fileNames = new String[]{"windows","windows64","linux","linux64","macos","macos64"};

      for(int i =0; i<6; i++){
       
       try{
    	   System.out.println("Create :"+fileNames[i]);
    	  
    	   String line = fileNames[i];
           String path="";
           String fileName ="";
           
           if("windows".equals(line.toLowerCase()) || "windows64".equals(line.toLowerCase())){

                if("windows64".equals(line.toLowerCase())){
                    sys.id = sys.WINDOWS_64;
                    fileName="startNeutrinoWindows64.bat";
                }else{
                	fileName="startNeutrinoWindows.bat";
                	 sys.id = sys.WINDOWS;
                }
                
                path+="\n";
                path="java.exe -classpath  lib;ebiExtensions;hibernate;lib/tiny_mce;"+sys.getLibraries("lib/")+"bin/ebiNeutrinoR1.jar ebiNeutrino.core.EBIMain ";
                
           }else if("linux".equals(line.toLowerCase()) || "linux64".equals(line.toLowerCase())){

                if("linux64".equals(line.toLowerCase())){
                	 sys.id = sys.LINUX_64;
                    fileName="startNeutrinoLinux64.sh";
                }else{
                	 sys.id = sys.LINUX;
                    fileName="startNeutrinoLinux.sh";
                }
                
                path+="#! /bin/sh\n";
                path+="exec java -classpath  lib:hibernate:ebiExtensions:lib/tiny_mce:"+sys.getLibraries("lib/")+"bin/ebiNeutrinoR1.jar ebiNeutrino.core.EBIMain ";

           }else if("macos".equals(line.toLowerCase()) || "macos64".equals(line.toLowerCase())){

                if("macos64".equals(line.toLowerCase())){
                	 sys.id = sys.MACOS_64;
                    fileName="startNeutrinoMAC64.sh";
                }else{
                	 sys.id = sys.MACOS;
                    fileName="startNeutrinoMAC.sh";
                }
                
                path+="#! /bin/sh\n";
                path+="exec java -classpath  lib:hibernate:ebiExtensions:lib/tiny_mce:"+sys.getLibraries("lib/")+"bin/ebiNeutrinoR1.jar ebiNeutrino.core.EBIMain ";
                
           }
           
           FileWriter fstream = new FileWriter(new File("buildFiles/"+fileName));
           BufferedWriter out = new BufferedWriter(fstream);
           out.write(path);
           //Close the output stream
           out.close();
           System.out.println("Start file created...");

       }catch(IOException ex){
         ex.printStackTrace();
       }catch(Exception ex) {
          ex.printStackTrace();
       }
      }

    }

    private String getLibraries(String libPath){
        StringBuffer buf = new StringBuffer();
        File dir = new File(libPath);

        FilenameFilter filter = new FilenameFilter() {
             public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
             }
        };

        String[] children = dir.list(filter);
        if (children != null) {

            if(id == WINDOWS || id == WINDOWS_64){
                for (int i=0; i<children.length; i++) {
                    // Get filename of file or directory
                    if(id == WINDOWS_64){
                        if(!"swtLinux.jar".equals(children[i]) && !"swtMAC.jar".equals(children[i]) &&
                                    !"swtLinux64.jar".equals(children[i]) && !"swtMAC64.jar".equals(children[i]) && !"swtWindows.jar".equals(children[i])){
                             buf.append(libPath+children[i]);
                             buf.append(";");
                        }
                    }else{
                        if(!"swtLinux.jar".equals(children[i]) && !"swtMAC.jar".equals(children[i]) &&
                                    !"swtLinux64.jar".equals(children[i]) && !"swtMAC64.jar".equals(children[i]) && !"swtWindows64.jar".equals(children[i])){
                             buf.append(libPath+children[i]);
                             buf.append(";");
                        }
                    }
                  }
            }else if(id == LINUX || id == LINUX_64){
                  for (int i=0; i<children.length; i++) {
                    // Get filename of file or directory
                      if(id == LINUX_64){
                            if(!"swtWindows.jar".equals(children[i]) && !"swtMAC.jar".equals(children[i]) &&
                                    !"swtWindows64.jar".equals(children[i]) && !"swtMAC64.jar".equals(children[i]) && !"swtLinux.jar".equals(children[i])){
                                 buf.append(libPath+children[i]);
                                 buf.append(":");
                            }
                      }else{
                          if(!"swtWindows.jar".equals(children[i]) && !"swtMAC.jar".equals(children[i]) &&
                                    !"swtWindows64.jar".equals(children[i]) && !"swtMAC64.jar".equals(children[i]) && !"swtLinux64.jar".equals(children[i])){
                                 buf.append(libPath+children[i]);
                                 buf.append(":");
                           }
                      }
                  }
            }else if(id == MACOS || id == MACOS_64){
                  for (int i=0; i<children.length; i++) {
	                    // Get filename of file or directory
	
	                    if(id == MACOS_64){
	                        if(!"swtLinux.jar".equals(children[i]) && !"swtWindows.jar".equals(children[i]) &&
	                                !"swtLinux64.jar".equals(children[i]) && !"swtWindows64.jar".equals(children[i]) && !"swtMAC.jar".equals(children[i])){
	                             buf.append(libPath+children[i]);
	                             buf.append(":");
	                        }
	                    }else{
	                        if(!"swtLinux.jar".equals(children[i]) && !"swtWindows.jar".equals(children[i]) &&
	                                !"swtLinux64.jar".equals(children[i]) && !"swtWindows64.jar".equals(children[i]) && !"swtMAC64.jar".equals(children[i])){
	                             buf.append(libPath+children[i]);
	                             buf.append(":");
	                        }
	                    }
                  }
            }
        }

       return buf.toString();
    }

}
