package ebiNeutrinoSDK.utils;

import javax.swing.table.TableColumnModel;
import org.jdesktop.swingx.JXTable;
import java.io.File;


public class EBISaveRestoreTableProperties {

    private EBIPropertiesRW properties = null;

    public EBISaveRestoreTableProperties(){
        properties = EBIPropertiesRW.getPropertiesInstance(new File("./config/dialogstore.properties"),true,true);
    }

    public void saveTableProperties(JXTable table){
        TableColumnModel col = table.getColumnModel();
        String tableStyle = "";

        for(int i=0; i<col.getColumnCount(); i++){
            tableStyle +="["+col.getColumnIndex(col.getColumn(i).getHeaderValue())+":"+col.getColumn(i).getWidth()+"];";
        }

         properties.setValue("table_style_"+table.getName(),tableStyle);
         properties.saveProperties();
    }

    public void restoreTableProperties(JXTable table){

       if(!"".equals(properties.getValue("table_style_"+table.getName()))){ 
           String[] str = properties.getValue("table_style_"+table.getName()).split(";");
           try{
            for(int i=0; i<str.length; i++){
              String [] blc =  str[i].split(":");
              int index = 0;
              for(int j=0; j<blc.length; j++){
                  if(j == 0){
                     index  = Integer.parseInt(blc[j].substring(1,blc[j].length()));
                  }else{
                    try{
                        table.getColumnModel().getColumn(index).setPreferredWidth(Integer.parseInt(blc[j].substring(0,blc[j].length()-1)));
                    }catch(ArrayIndexOutOfBoundsException ex){ ex.printStackTrace();}
                  }
              }
            }
           }catch(NumberFormatException ex){ex.printStackTrace();}
            table.updateUI();
        }
    }

}
