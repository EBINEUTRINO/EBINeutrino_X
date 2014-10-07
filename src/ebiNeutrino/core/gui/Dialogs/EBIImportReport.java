package ebiNeutrino.core.gui.Dialogs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBIImportReport extends EBIDialog {

    private EBIMain ebiMain = null;
    private JProgressBar progress = new JProgressBar();
    private JLabel title = new JLabel();
    private EBIVisualPanelTemplate pane = new EBIVisualPanelTemplate(false);


    public EBIImportReport(EBIMain main){
      super(main);

      ebiMain = main;
      setName("InstallReports");


      pane.setEnableChangeComponent(false);
      pane.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_IMPORT_TILE"));
      pane.setModuleIcon(EBIConstant.ICON_COPY);
      pane.setClosable(true);

      this.setContentPane(pane);

      setSize(310, 120);
      storeLocation(true);
      storeSize(true);  
        
      title.setText(EBIPGFactory.getLANG("EBI_LANG_IMPORT_TILE"));
      title.setLocation(10,10);
      title.setSize(200,20);

      pane.add(title,null);
      progress.setSize(280,30);
      progress.setLocation(10,50);
      progress.setBorderPainted(true);
      progress.setIndeterminate(true);
      progress.setStringPainted(true);

      progress.setString(EBIPGFactory.getLANG("EBI_LANG_INSTALL_REPORTS"));
      pane.add(progress,null);
    }


    public void startReportImport(){

        setVisible(true);

        final Runnable run = new Runnable(){
            public void run(){
                try {
                    Thread.sleep(500);

                    ebiMain.system.getIEBIDatabase().setAutoCommit(true);
                    ebiMain.system.getIEBIDatabase().exec("DELETE FROM SET_REPORTFORMODULE ");
                    ebiMain.system.getIEBIDatabase().exec("DELETE FROM SET_REPORTPARAMETER ");

                    BufferedReader myBufferedReader = new BufferedReader(new FileReader("./reports/Reports.sql"));
                    String str="";
                    String line;
                    while(myBufferedReader.ready()){
                        line = myBufferedReader.readLine();
                        if("/".equals(line)){
                          ebiMain.system.getIEBIDatabase().exec(str);
                          str = "";
                        }else{
                          str+= line;
                        }
                    }

                    myBufferedReader = new BufferedReader(new FileReader("./reports/reportParameter.sql"));
                    str = "";
                    while(myBufferedReader.ready()){
                        str += myBufferedReader.readLine().replaceAll(System.getProperty("line.separator"),"");

                    }
                    myBufferedReader.close();
                    ebiMain.system.getIEBIDatabase().exec(str);
                    ebiMain.systemSetting.listName.report.showReports();
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_IMPORT_WAS_SUCCESSFULLY")).Show(EBIMessage.INFO_MESSAGE);

                }catch(FileNotFoundException ex){
                  EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_FILE_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                }catch(IOException ex){
                  EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_FILE_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }catch(SQLException ex){
                	ex.printStackTrace();
                }finally{
                   setVisible(false);
                   ebiMain.system.getIEBIDatabase().setAutoCommit(false);
                }
            }
        };

        Thread start = new Thread(run,"Install Reports");
        start.start();

    }

}
