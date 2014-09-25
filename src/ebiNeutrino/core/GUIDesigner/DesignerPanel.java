package ebiNeutrino.core.GUIDesigner;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerDialogTools;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerNamesDialog;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerProperties;
import ebiNeutrino.core.GUIDesigner.panel.EBIVisualDesignerPanelTemplate;
import ebiNeutrino.core.GUIDesigner.utils.EBIScriptFiles;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIModule;
import ebiNeutrinoSDK.interfaces.IEBIStoreInterface;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

/**
 * Created by IntelliJ IDEA.
 * User: francesco
 * Date: 29.11.2008
 * Time: 18:00:20
 */

public class DesignerPanel extends EBIVisualDesignerPanelTemplate implements IEBIStoreInterface, IEBIModule {

        private HashMap<String,EBIScriptFiles> sourceEditor = new HashMap<String, EBIScriptFiles>();
        public EBIMain ebiMain = null;
        public static boolean drawLeftCorner = true;
        public static boolean drawRightCorner= true;
        public static boolean drawTopCorner = true;
        public static boolean drawBottomCorner = true;
        public static int HEADER_HEIGHT = 20;


        public DesignerPanel(File fl, final EBIMain ebiMain){
           super(ebiMain);

           this.ebiMain = ebiMain;

           tabPane = new JTabbedPane();
           this.ebiMain._ebifunction.resetDataStore();

           propBox = new EBIDesignerProperties(this);

           file = fl;
           mainComponent = new HashMap<String,EBIGUIWidgetsBean>();

           loadXMLGUI();
           createAndShowGUI();

           setPreferredSize(new Dimension(mainComponentBean.getDimension().width+20, mainComponentBean.getDimension().height+80));
           setBounds(0,0, mainComponentBean.getDimension().width, mainComponentBean.getDimension().height);

           setModuleTitle(mainComponentBean.getTitle());
           setModuleIcon(mainComponentBean.getIcon());
           setChangePropertiesVisible(mainComponentBean.isVisualProperties());

           setVisiblePopup(true);

           setComponentX(panel.getX());
           setComponentY(panel.getY());
           setComponentW(mainComponentBean.getDimension().width);
           setComponentH(mainComponentBean.getDimension().height);
           setDrawFocusRectangle(true);
           repaint();
           
           final Action enterAction = new AbstractAction() {
        	   
               public void actionPerformed(ActionEvent e) {
	               setTabSave(true);
	               saveFile();
                   if(!mainComponentBean.getType().toLowerCase().equals("dialog")){
                     ebiMain.mng.reloadSelectedModule();
                   }
                }
           };
           
           
           textArea.addKeyListener(new KeyListener() {
			
                    @Override
                    public void keyTyped(KeyEvent e) {
                       if(!e.isControlDown()){
                        setTabSave(false);
                       }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {}

                    @Override
                    public void keyPressed(KeyEvent e) {}
		    });
            
           InputMap inputMap = textArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
           inputMap.put(KeyStroke.getKeyStroke("ctrl S"), "Save");
           textArea.getActionMap().put("Save", enterAction);
           
           InputMap inputMap1 = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
           inputMap1.put(KeyStroke.getKeyStroke("ctrl S"), "Save");
           panel.getActionMap().put("Save", enterAction);
           
           InputMap inputMap2 = tabPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
           inputMap2.put(KeyStroke.getKeyStroke("ctrl S"), "Save");
           tabPane.getActionMap().put("Save", enterAction);

           tabPane.addChangeListener(new ChangeListener() {
              
                public void stateChanged(ChangeEvent evt) {
                    JTabbedPane pane = (JTabbedPane)evt.getSource();
                    int sel = pane.getSelectedIndex();
                    if(sel == 0){

                       if(!"".equals(textArea.getText())){
                        loadXMLGUIToPanel(textArea.getText());   
                       }
                       setDrawFocusRectangle(true);
                       panel.repaint();
                       panel.requestFocusInWindow();
                    }else if(sel == 1){
                      showCode();
                    }
                }
            });

        openCodeControlsFiles();

        JMenuItem addCodeControlItem = new JMenuItem("Add Code Control");
        addCodeControlItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                     EBIDesignerNamesDialog ndialog = new EBIDesignerNamesDialog(ebiMain,"codecontrol", componentsTable);
                     ndialog.setVisible(true);

                      if(!"".equals(ndialog.name)){

                          EBIGUIWidgetsBean bean = new EBIGUIWidgetsBean();
                          bean.setName("groovy");
                          bean.setType("codecontrol");
                          bean.setPath("EBICRM/"+ndialog.name);
                          addComponentToContainer(null, bean,true);
                          openCodeControlsFiles();
                          saveCodeFiles(tabPane.getTabCount()-1);
                          setTabSave(false);
                      }
                }
        });


         JMenuItem propertiesItem = new JMenuItem("Properties");
         propertiesItem.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
                 propBox.Show();
             }
         });

         JMenuItem deleteItem = new JMenuItem("Delete");
         deleteItem.addActionListener(new ActionListener(){
             public void actionPerformed(ActionEvent e){
               deleteSelectedComponent();
             }
         });

         popup.add(addCodeControlItem);
         popup.addSeparator();
         popup.add(deleteItem);
         popup.addSeparator();
         popup.add(propertiesItem);

        }

     private void openCodeControlsFiles(){
         if(codeControls.size()>0){
            for(int i = 0; i< codeControls.size(); i++){
                openFileAndTab(codeControls.get(i),false);
            }
         }
     }

     public void setVisiblePopup(boolean setVisible){
          if(this.toolBox==null){
             EBIPropertiesRW prop=EBIPropertiesRW.getPropertiesInstance();
             this.toolBox = new EBIDesignerDialogTools(this,prop);
             //this.toolBox.setClosable(false);
          }

          this.toolBox.setVisible(setVisible);
          if(this.propBox != null && !setVisible){ this.propBox.setVisible(setVisible); }
      }

      public void saveFile(){
           if(tabPane.getSelectedIndex() < 2 ){
             saveXMLToFile();
           }else{
             saveCodeFiles(tabPane.getSelectedIndex());
           }
           EBIPGFactory.canRelease = true;
      }

      private void saveCodeFiles(int sindex){

          String tlt = tabPane.getTitleAt(sindex);
          EBIScriptFiles scrFile = sourceEditor.get(tlt.replace("*",""));

          boolean isNewFile = false;

          if(scrFile != null && scrFile.getFile() == null ){

             isNewFile = true;
             File file = ebiMain._ebifunction.getSaveDialog(JFileChooser.FILES_ONLY);

             if(file == null){
                 return;
             }
             scrFile.setFile(file);
          }

          if(isNewFile){
            sourceEditor.remove(tlt.replace("*",""));
            sourceEditor.put(scrFile.getFile().getName(),scrFile);
            tabPane.setTitleAt(sindex,scrFile.getFile().getName());
          }

          writeSourceToFile(scrFile.getFile(), ((JEditorPane)scrFile.getComponent()).getText());
          tabPane.setTitleAt(tabPane.getSelectedIndex(),scrFile.getFile().getName());
      }

       public void writeSourceToFile(File file,String text) {
           Writer output;

           try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(text);
            output.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
       }

      public StringBuffer readSourceFromFile(File file){

            FileInputStream fis;
            BufferedInputStream bis;
            DataInputStream dis;
            StringBuffer buffer = new StringBuffer();
            try {
              fis = new FileInputStream(file);

              // Here BufferedInputStream is added for fast reading.
              bis = new BufferedInputStream(fis);
              dis = new DataInputStream(bis);
              String line;
              while( (line = dis.readLine()) != null){
                 buffer.append(line);
                 buffer.append("\n");
              }
              // dispose all the resources after using them.
              fis.close();
              bis.close();
              dis.close();

            } catch (FileNotFoundException ex) {} catch (IOException ex) {
              EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            }
          return buffer;
      }


      public void newSourceFile(){

            EBIScriptFiles scrFiles = new EBIScriptFiles();
            scrFiles.setFile(null);
            scrFiles.setName("Unnamed_"+(sourceEditor.size()+1));
            scrFiles.setComponent(createNewJEditorPane(scrFiles.getName()));
            sourceEditor.put("Unnamed_"+(sourceEditor.size()+1), scrFiles);
            tabPane.setSelectedIndex(tabPane.getTabCount()-1);
            EBIPGFactory.canRelease = false;
       }

       public void openScriptFile(){

            File file = ebiMain._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);

             if(file == null){
                 return;
             }

             openFileAndTab(file,true);

       }

       public void openFileAndTab(File fileName, boolean selectTab){
            EBIScriptFiles scrFile = null;
            if(sourceEditor.get(fileName.getName()) == null){
                scrFile = new EBIScriptFiles();
                scrFile.setFile(fileName);
                scrFile.setName(fileName.getName());

                scrFile.setComponent(createNewJEditorPane(scrFile.getName()));
                sourceEditor.put(scrFile.getName(),scrFile);

            } else{
                 scrFile = sourceEditor.get(fileName.getName());
                 if(scrFile.isClosed() && selectTab){
                   scrFile.setComponent(createNewJEditorPane(scrFile.getName()));
                 }
            }

            if(selectTab){
                 tabPane.setSelectedIndex(tabPane.getTabCount()-1);
            }
            try{
                if(scrFile.getComponent() != null){
                    scrFile.getComponent().grabFocus();
                    ((JEditorPane)scrFile.getComponent()).setText(readSourceFromFile(fileName).toString());
                }
            }catch(Exception ex){}
       }

       private JEditorPane createNewJEditorPane(String Title){
           
           JScrollPane sourcePane = new JScrollPane();
           JEditorPane paneE = new JEditorPane();

           sourcePane.setViewportView(paneE);
           jsyntaxpane.DefaultSyntaxKit.initKit();

           paneE.setContentType("text/java");
           JEditorPane area = paneE;

           area.addKeyListener(new KeyListener(){
               private boolean isSet = false;
               public void keyTyped(KeyEvent e) {
                   if(!isSet){
                     if(!e.isControlDown()){
                         tabPane.setTitleAt(tabPane.getSelectedIndex(),tabPane.getTitleAt(tabPane.getSelectedIndex())+"*");
                         isSet = true;
                         EBIPGFactory.canRelease = false;
                     }
                   }
               }

               public void keyPressed(KeyEvent e){}

               public void keyReleased(KeyEvent e) {
                  if(e.isControlDown()){
                        if(e.getKeyCode() == KeyEvent.VK_S){
                             saveFile();
                             isSet = false;
                            EBIPGFactory.canRelease = true;
                        }
                    }
               }
           });
           tabPane.addTab(Title, sourcePane);
           return area;
       }

       private void createAndShowGUI() {


            tabPane.setTabPlacement(JTabbedPane.BOTTOM);
            tabPane.setBorder(null);
            //Set up the scroll pane.
            JScrollPane sourcePane = new JScrollPane();
            JEditorPane paneE = new JEditorPane();
            //paneE.setFont(new Font( "monospace", Font.PLAIN, 11));
            sourcePane.setViewportView(paneE);
            jsyntaxpane.DefaultSyntaxKit.initKit();
            paneE.setContentType("text/xml");
            textArea = paneE;
            //textArea.setFont(new Font( "monospace", Font.PLAIN, 11));

            JScrollPane designerScrollPane = new JScrollPane(this);
            designerScrollPane.setPreferredSize(mainComponentBean.getDimension());
            designerScrollPane.setBorder(null);
            setDoubleBuffered(true);

            tabPane.addTab("Designer",designerScrollPane);
            tabPane.addTab("Code",sourcePane);

        }

       
        private void loadXMLGUI() {
            xmlGui = new EBIXMLGUIReader();
            xmlGui.setXmlPath(file);
            xmlGui.loadXMLGUI();
            mainComponentName = xmlGui.getCompObjects().getName();
            mainComponent.put(mainComponentName,xmlGui.getCompObjects());
            mainComponentBean =  mainComponent.get(mainComponentName);

            panel.setBounds(0,HEADER_HEIGHT, mainComponentBean.getDimension().width, mainComponentBean.getDimension().height);
            panel.setName(mainComponentName);
            panel.setBackground(new Color(255,255,255));
            addComponentsToContainer(this, mainComponentBean.getSubWidgets());
            showCode();
        }


        public void paint(Graphics g){
           try{
            super.paint(g);
           }catch(NullPointerException ex){
               ex.printStackTrace();
           }
           drawRightCorner = true;
           drawLeftCorner  = true;
           drawBottomCorner= true;
           drawTopCorner   = true;

          Graphics2D g2 = (Graphics2D) g;
             
             if(isDrawFocusReactangle()){
                 g2.setColor(Color.BLUE);
                 g2.drawRect(getComponentX(),getComponentY(),getComponentW(),getComponentH());
                    Component xcomp = componentsTable.get(selectedComponentName);
                    if(xcomp != null){
                       if(xcomp.getParent() != null){
                              
                           if((xcomp.getX()+xcomp.getWidth()) > xcomp.getParent().getWidth()){
                              drawRightCorner = false;
                           } else if(xcomp.getX() < 0){
                              drawLeftCorner = false;
                           }

                           if((xcomp.getY()+xcomp.getHeight()) > xcomp.getParent().getHeight()){
                              drawBottomCorner = false;
                           }else if(xcomp.getY() < 0){
                              drawTopCorner = false;
                           }
                              
                        }
                    }
                    if(drawLeftCorner){
                        if(drawTopCorner){
                            g2.fillRect(getComponentX()-5,getComponentY()-5,5,5);
                        }
                        if(drawBottomCorner){
                            g2.fillRect(getComponentX()-5,getComponentY()+getComponentH(),5,5);
                        }
                    }

                    if(drawRightCorner){
                        if(drawTopCorner){
                            g2.fillRect(getComponentX()+getComponentW(),getComponentY()-5,5,5);
                        }
                        if(drawBottomCorner){
                            g2.fillRect(getComponentX()+getComponentW(),getComponentY()+getComponentH(),5,5);
                        }
                    }
                 }
        }

    public boolean closeTab(String tabToClose) {
        if(tabToClose.indexOf("*") != -1) {
    	 if(EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_SAVE_THIS_FILE")).Show(EBIMessage.INFO_MESSAGE_YESNO) == true){
             saveFile();
         }
        }
        sourceEditor.get(tabToClose).setClosed(true);
      return true;
	}

    public boolean ebiSave(){
       saveXMLToFile();
       if(tabPane.getTabCount() > 2){
           for(int i=2; i<tabPane.getTabCount(); i++){
            saveCodeFiles(i);
           }
       }
      return true;
    }
	/**
	 * ebiUpdate
	 * @return
	 */
	public boolean ebiUpdate(){
      saveXMLToFile();
      if(tabPane.getTabCount() > 2){
       for(int i=0; i<tabPane.getTabCount(); i++){
        saveCodeFiles(i);
       }
      }
      return true;
    }
	/**
	 * ebiDelete
	 * @return
	 */
	public boolean ebiDelete(){
        return true;
    }

    public Object getActiveModule(){
     return this;    
    }

    public JTabbedPane getTabbedPane(){
        return tabPane;
    }

    public void requestStartFocus(){
        panel.requestFocusInWindow();
    }
}