package ebiNeutrinoSDK.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBISQLFilter;


public class EBIImportSQLFiles extends EBIDialogExt {

    private EBIPGFactory ebiPGFactory = null;
    private JProgressBar progress = new JProgressBar();
    private JLabel title = new JLabel();
    private JTextField txtFile = new JTextField();
    private JButton bntLoadFile = new JButton();
    private JButton bntStartImport = new JButton();
    public boolean isFinish = false;
    private EBIVisualPanelTemplate pane = new EBIVisualPanelTemplate();


    public EBIImportSQLFiles(EBIPGFactory ebiPGFact){
       super(ebiPGFact.getMainFrame());
       ebiPGFactory = ebiPGFact;

       pane.setEnableChangeComponent(false);
       pane.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_IMPORT_TILE"));
       pane.setModuleIcon(EBIConstant.ICON_COPY);
       pane.setClosable(true);
       this.setContentPane(pane);
       this.setModal(false);
       this.setLayout(null);
       this.setResizable(false);
       //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
       setSize(310,170);  
       storeLocation(true);
       storeSize(true);

       title.setText(EBIPGFactory.getLANG("EBI_LANG_IMPORT_TILE"));
       title.setLocation(10,10);
       title.setSize(200,20);
       pane.add(title,null);

       txtFile.setLocation(10,40);
       txtFile.setSize(200,20);
       pane.add(txtFile,null);

       bntLoadFile.setText("...");
       bntLoadFile.setLocation(220,40);
       bntLoadFile.setSize(60,25);
       bntLoadFile.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ev) {
                EBISQLFilter sqlFilter = new EBISQLFilter();
                ebiPGFactory.fileDialog.addChoosableFileFilter(sqlFilter);

                File fs = ebiPGFactory.getOpenDialog(JFileChooser.FILES_ONLY);

                if(fs != null){
                    txtFile.setText(fs.getAbsolutePath());
                }
           }
       });

       pane.add(bntLoadFile,null);

       progress.setSize(280,25);
       progress.setLocation(10,70);
       progress.setBorderPainted(true);
       progress.setStringPainted(true);
        pane.add(progress,null);

       bntStartImport.setText(EBIPGFactory.getLANG("EBI_LANG_IMPORT"));
       bntStartImport.setLocation(190,105);
       bntStartImport.setSize(100,25);
       bntStartImport.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent ev) {
             startSQLImport(new String[]{txtFile.getText()});
           }
       });

       pane.add(bntStartImport,null);
       pane.setBackgroundColor(EBIPGFactory.systemColor);
    }



    public void startSQLImport(final String[] flx){
      isFinish = false;
      if(isVisible()){
         progress.setIndeterminate(true);
         progress.setString(EBIPGFactory.getLANG("EBI_LANG_IMPORT_SQL"));
      }

      final Runnable run = new Runnable(){
            public void run(){
               try{
                   for(int x = 0; x<flx.length; x++) {
                    try {
                       Thread.sleep(500);
                       String s;
                       //StringBuffer sb = new StringBuffer();

                       FileReader fr = new FileReader(new File(flx[x]));
                       BufferedReader br = new BufferedReader(fr);
                       String tmp ="";

                       ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
                       while((s = br.readLine()) != null){

                         if(s.endsWith(";")){
                    
                            if(!ebiPGFactory.getIEBIDatabase().exec(tmp+s)){
                                return;
                            }

                            tmp = "";

                         }else{
                           tmp += s;
                         }
                         
                       }
                       br.close();

                      if(isVisible()){
                        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_IMPORT_WAS_SUCCESSFULLY")).Show(EBIMessage.INFO_MESSAGE);
                        setVisible(false);
                      }

                    }catch(FileNotFoundException ex){
                      EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_FILE_NOT_FOUND")+ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
                    }catch(IOException ex){
                      EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_FILE_NOT_FOUND")+ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally{
                        ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
                        if(isVisible()){
                            progress.setIndeterminate(false);
                        }
                       isFinish = true;
                    }
                }
               }catch(SQLException ex){
            	   ex.printStackTrace();
            	   EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_LOADING_FILE")+"\n\n"+ex.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
               }
            }
       };

       Thread start = new Thread(run,"Import SQL");
       start.start();

    }

}
