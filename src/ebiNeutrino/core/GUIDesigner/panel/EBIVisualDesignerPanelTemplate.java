package ebiNeutrino.core.GUIDesigner.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicArrowButton;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIDesigner.DesignerPanel;
import ebiNeutrino.core.GUIDesigner.EBIGUIWidgetsBean;
import ebiNeutrino.core.GUIDesigner.EBIXMLGUIReader;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerDialogTools;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerNamesDialog;
import ebiNeutrino.core.GUIDesigner.gui.EBIDesignerProperties;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanel;

public class EBIVisualDesignerPanelTemplate extends EBIVisualPanel {

     protected int componentX=0;
     protected int componentY=0;
     protected int componentW=0;
     protected int componentH=0;
     protected boolean drawFocusReactangle = false;
     protected int offsetX = 0;
     protected int offsetY = 0;
     protected int mx = 0;
     protected int my = 0;
     protected HashMap<String, EBIGUIWidgetsBean> mainComponent = null;
     public HashMap<String,JComponent> componentsTable =  null;
     public String mainComponentName = "";
     public String selectedComponentName = "";
     public EBICustomPanel panel = null;
     protected boolean resizeNW = false;
     protected boolean resizeNW1 = false;
     protected boolean resizeNE = false;
     protected boolean resizeNE1 = false;
     public boolean isComponentMoved = false;
     public boolean resize = false;
     public boolean resizeFinal = true;
     public boolean isDraggable = false;
     protected EBIMain ebiMain = null;
     public boolean insertNewComponent = false;
     protected EBIDesignerDialogTools toolBox = null;
     protected EBIDesignerProperties propBox = null;
     protected EBIGUIWidgetsBean mainComponentBean = null;
     private EBIGUIWidgetsBean selectedBean = null;
     protected EBIXMLGUIReader xmlGui = null;
     public JEditorPane textArea = new JEditorPane();
     public File file = new File("");
     private MouseAdapter adapt = null;
     public final JPopupMenu popup = new JPopupMenu();
     public boolean showPopup = false;
     public boolean isChagingProperties = false;
     public List<File> codeControls = null;
     protected JTabbedPane tabPane = null;

     public EBIVisualDesignerPanelTemplate(final EBIMain ebiMain){
         super();
         this.ebiMain = ebiMain;
         //Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
         panel = new EBICustomPanel(this);
         panel.setLayout(null);
         super.add(panel,null);
         componentsTable =  new HashMap<String,JComponent>();
         super.setBackground(Color.GRAY);

         adapt = new MouseAdapter() {

			    public void mouseExited(MouseEvent e) {}

				public void mousePressed(MouseEvent e){
                    setDrawFocusRectangle(false);
                    repaint();
                    offsetX  =  e.getX();
                    offsetY  =  e.getY();

                    if(e.getButton() == MouseEvent.BUTTON3){
                       showPopup = true;
                    }else{
                       showPopup = false;
                    }

                    if(resizeFinal == true && resize == false 
                            && !selectedComponentName.equals(((JComponent)e.getSource()).getName())){
                        selectComponent(((JComponent)e.getSource()));
                        isDraggable = false;
                    }else{
                        isDraggable = true;
                    }
                }

		  // Mouse Release Action
		   public void mouseReleased(java.awt.event.MouseEvent e){
              
                  if(isChagingProperties){
                    showCode();
                    setTabSave(false);
                    isChagingProperties = false;
                  }

                  if(componentsTable.get(selectedComponentName) != null && !((JComponent)e.getSource()).getName().equals(mainComponentName)){
                    updateComponentSelection(selectedComponentName);
                  }else{
                    if(resizeFinal){
                       panel.selectMainComponent(((JComponent)e.getSource())); 
                    }else{
                       updateComponentSelection(selectedComponentName);
                    }
                  }
                  if(insertNewComponent){
                      Point pt = new Point(e.getX(), e.getY());

                      EBIDesignerNamesDialog ndialog = new EBIDesignerNamesDialog(ebiMain, mainComponentName, componentsTable);
                      ndialog.setVisible(true);

                      if(!"".equals(ndialog.name)){

                          EBIGUIWidgetsBean bean = new EBIGUIWidgetsBean();
                          bean.setName(ndialog.name);

                          if("button".equals(toolBox.componentName) ||
                                            "panel".equals(toolBox.componentName) ||
                                                "checkbox".equals(toolBox.componentName) ||
                                                            "radiobutton".equals(toolBox.componentName) ||
                                                                        "label".equals(toolBox.componentName)){
                              bean.setTitle(ndialog.name);
                          }

                          bean.setType(toolBox.componentName);
                          bean.setPoint(pt);
                          bean.setDimension(new Dimension(100,20));
                          addComponentToContainer(e.getSource(),bean,true);
                          toolBox.resetButtons();
                          setTabSave(false);
                      }
                      
                  }else{
                     if(e.isPopupTrigger() || showPopup){
                         popup.show((JComponent)e.getSource(),e.getX(),e.getY());
                     }
                    
                  }
                  setDrawFocusRectangle(true);
                  repaint();
                }
			};

            panel.addMouseListener(adapt);
     }

    public void add(JComponent component, Object obj){
      panel.add(component, obj);
    }

    public void add(JComponent component){
      panel.add(component, null);
    }

    public void setBackgroundColor(Color color){
        try{
            panel.setBackground(color);
            updateUI();
        }catch(NumberFormatException ex){}
    }

     public int getComponentX() {
        return componentX;
    }

    public void setComponentX(int componentX) {
       this.componentX = componentX;
    }

    public int getComponentY() {
        return componentY;
    }

    public void setComponentY(int componentY) {
        this.componentY = componentY;
    }

    public int getComponentW() {
        return componentW;
    }

    public void setComponentW(int componentW) {
        this.componentW = componentW;
    }

    public int getComponentH() {
        return componentH;
    }

    public void setComponentH(int componentH) {
        this.componentH = componentH;
    }

    public boolean isDrawFocusReactangle() {
        return drawFocusReactangle;
    }

    public void setDrawFocusRectangle(boolean drawFocusReactangle) {
        this.drawFocusReactangle = drawFocusReactangle;
    }

    protected void addComponentsToContainer(Object obj, java.util.List<EBIGUIWidgetsBean> lst){
           Iterator iter  ;
           if(lst == null){
              iter = mainComponentBean.getSubWidgets().iterator();
           }else{
             iter = lst.iterator();
           }
           codeControls = new ArrayList<File>();
           while(iter.hasNext()){
             addComponentToContainer(obj, (EBIGUIWidgetsBean)iter.next(),false);
           }
    }

    private void setJComponentProperties(JComponent component,EBIGUIWidgetsBean bean,Object obj){
        component.setLocation(bean.getPoint());
        if(bean.isHeightAutoResize() || bean.isWidthAutoResize()){
           component.setSize(getDimensionAutoSize(bean,obj));
        }else{
           component.setSize(bean.getDimension());
        }

        component.setName(bean.getName());

        if(bean.getColor() != null){
          component.setBackground(bean.getColor());
        }

        component.setFocusable(false);
        if(obj instanceof EBIVisualDesignerPanelTemplate){
           ((EBIVisualDesignerPanelTemplate)obj).add(component,null);
        }   
        else{((EBICustomPanel)obj).add(component,null);}
    }

    protected void addComponentToContainer(Object obj, EBIGUIWidgetsBean bean, boolean newComponent){

            JComponent component = null;
            
            if(newComponent){
               addNewComponentToBeanList(mainComponentBean.getSubWidgets(), obj, bean);
            }

            if("textfield".equals(bean.getType())){
              component = new JTextField(bean.getTitle());
              component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
              setJComponentProperties(component, bean,obj);
            }else if("formattedtextfield".equals(bean.getType())){
              component = new JTextField(bean.getTitle());
              component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
              setJComponentProperties(component, bean,obj);
            }else if("button".equals(bean.getType())){
              component = new JButton(bean.getTitle());
              ((JButton)component).getUI().uninstallUI(component);
              ((JButton)component).setText(bean.getTitle());
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              setJComponentProperties(component, bean,obj);

            }else if("timepicker".equals(bean.getType())){
              component = new JPanel();
              component.setBackground(new Color(255,255,255));
              setJComponentProperties(component, bean,obj);
            }else if("combobox".equals(bean.getType())){
              component = new JPanel();
              component.setLayout(new BorderLayout());
              component.add(new BasicArrowButton(BasicArrowButton.SOUTH),BorderLayout.EAST);
              component.setBackground(new Color(255,255,255));
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              setJComponentProperties(component, bean,obj);
            }else if("checkbox".equals(bean.getType())){
              component = new JCheckBox();
              ((JCheckBox)component).getUI().uninstallUI(component);
              ((JCheckBox)component).setText(bean.getTitle());
              setJComponentProperties(component, bean,obj);

            }else if("radiobutton".equals(bean.getType())){
              component = new JRadioButton();
              ((JRadioButton)component).getUI().uninstallUI(component);
              ((JRadioButton)component).setText(bean.getTitle());
              setJComponentProperties(component, bean,obj);
            }else if("list".equals(bean.getType())){
              component = new JList();
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              DefaultListModel data = new DefaultListModel();
              data.add(0,"ABC");
              data.add(1,"123");
              ((JList)component).setModel(data);
              setJComponentProperties(component, bean,obj);
            }else if("table".equals(bean.getType())){
              component = new JXTable();
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              setJComponentProperties(component, bean,obj);
            }else if("treetable".equals(bean.getType())){
              component = new JXTreeTable();
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              setJComponentProperties(component, bean,obj);
            }else if("label".equals(bean.getType())){
              component = new JLabel();
              component.setOpaque(true);
              ((JLabel)component).setText(bean.getTitle());
              component.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
              setJComponentProperties(component, bean,obj);
            }else if("textarea".equals(bean.getType())){
              component = new JTextArea();
              component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
              component.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
              setJComponentProperties(component, bean,obj);
            }else if("progressbar".equals(bean.getType())){
              component = new JProgressBar();
              component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
              ((JProgressBar)component).setBorderPainted(true);
              ((JProgressBar)component).setValue(10);   
              setJComponentProperties(component, bean,obj);
            }else if("panel".equals(bean.getType())){
              component = new EBICustomPanel(this);
              component.setBorder(javax.swing.BorderFactory.createTitledBorder(null, bean.getTitle() , javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
              component.setLayout(null);
              component.setBackground(new Color(255,255,255));
              setJComponentProperties(component, bean, obj);
              if(bean.getSubWidgets().size() > 0){
                addComponentsToContainer(component,bean.getSubWidgets());
              }

            }else if("codecontrol".equals(bean.getType())){
                codeControls.add(new File("ebiExtensions/"+bean.getPath()));
            }else{
               component = new JPanel();
               component.setLayout(new BorderLayout());
               component.add(new JLabel(bean.getType()),BorderLayout.CENTER);
               component.setBackground(new Color(255,255,255));
               setJComponentProperties(component, bean,obj);
            }

         if(component != null){
            componentsTable.put(bean.getName(),component);


            component.addMouseMotionListener(new MouseAdapter(){

                public void mouseMoved(MouseEvent e) {

                   if(resizeFinal == false){
                      if(((JComponent)e.getSource()).getName().equals(selectedComponentName)){
                          ((JComponent)e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                          resize = false;
                          resizeFinal = true;
                       }
                   }
                }

                public void mouseDragged(MouseEvent e){
                   if(isDraggable){
                       Component bnt = componentsTable.get(selectedComponentName);
    
                       if(!resize && !showPopup){

                        int x  =   e.getX();
                        int y  =   e.getY()-DesignerPanel.HEADER_HEIGHT;

                        Component comp = ((JComponent)e.getSource()).getParent();
                        while(!comp.getName().equals(mainComponentName)){
                            x -=  comp.getX();
                            y -=  comp.getY();
                            comp = comp.getParent();
                        }

                        Point pt = SwingUtilities.convertPoint(bnt, x, y, EBIVisualDesignerPanelTemplate.this);

                        int posX =  pt.x - offsetX;
                        int posY =  pt.y - offsetY;

                        ((JComponent)e.getSource()).setLocation(posX, posY);

                        if(!selectedBean.getName().equals(mainComponentName)){
                         selectedBean.setX(posX);
                         selectedBean.setY(posY);
                        }

                        isChagingProperties =true;
                        if(isComponentMoved == false){ isComponentMoved = true;}
                           
                      }
                       
                     }
                   }
            });

            component.addMouseListener(adapt);
          }

    }

    public void selectComponent(JComponent cmp){
      if(cmp != null){
          selectedComponentName = cmp.getName();
          getComponentFromBeanList(mainComponentBean, cmp.getName());
          cmp.getParent().setComponentZOrder(cmp,0);

      }
    }

    public void selectComponent(String name){

          selectedComponentName = name;
          getComponentFromBeanList(mainComponentBean, name);
          
    }

    public Dimension getDimensionAutoSize(EBIGUIWidgetsBean bean,Object obj) {
        int width ;
        int height ;

        if(bean.isWidthAutoResize()){
           int pwidth ;
           int beanWidth ;
           
           if(obj != null && obj instanceof EBICustomPanel){ pwidth = ((EBICustomPanel)obj).getWidth();}
           else { pwidth = panel.getWidth(); }

           if(bean.getDimension().width <=100){
             beanWidth = bean.getDimension().width;
           }else{
             beanWidth = 100;
           }
           width = ((beanWidth  * pwidth)  / 100) - bean.getX()- 10;

        }else{
          width = bean.getDimension().width;
        }

        if(bean.isHeightAutoResize()){
            int pheight ;
            int beanHeight ;

            if(obj != null && obj instanceof EBICustomPanel){ pheight = ((EBICustomPanel)obj).getHeight();}
            else { pheight = panel.getHeight(); }

            if(bean.getDimension().height <= 100){
                beanHeight = bean.getDimension().height;
            }else{
                beanHeight = 100;
            }
            height = ((beanHeight  * pheight) / 100) - bean.getY()-10;

       }else{
         height = bean.getDimension().height;
       }
    
        return new Dimension(width,height);
    }

    public void removeComponentFromBeanList(java.util.List<EBIGUIWidgetsBean> lst,
                                                        EBIGUIWidgetsBean bean, String name){

          EBIGUIWidgetsBean removeBean = null;

          Iterator iter = lst.iterator();
          while(iter.hasNext()){
              EBIGUIWidgetsBean b = (EBIGUIWidgetsBean) iter.next();

              if(b.getName().equals(name)){
                 removeBean = b;
                 break;

              }else if(b.getSubWidgets().size() > 0){
                removeComponentFromBeanList(b.getSubWidgets(), b, name);
              }
           }

          if(removeBean != null){
             componentsTable.remove(removeBean.getName());
             bean.getSubWidgets().remove(removeBean);
          }

    }

   public void getComponentFromBeanList(EBIGUIWidgetsBean list , String name){

            Iterator iter = list.getSubWidgets().iterator();
            while(iter.hasNext()){
              EBIGUIWidgetsBean b = (EBIGUIWidgetsBean) iter.next();
              if(b.getName().equals(name)){
                  setSelectedBean(b);
                  break;
              }
              if(b.getSubWidgets().size() > 0){
                getComponentFromBeanList(b,name);
              }
            }
    }

    private void addNewComponentToBeanList(java.util.List<EBIGUIWidgetsBean> lst, Object obj, EBIGUIWidgetsBean bean){
          Iterator iter  ;
          if(obj != null) {
                  if(mainComponentBean.getName().equals(((JComponent)obj).getName())){
                      mainComponentBean.getSubWidgets().add(bean);
                  }else{
                      iter = lst.iterator();
                      while(iter.hasNext()){
                          EBIGUIWidgetsBean b = (EBIGUIWidgetsBean) iter.next();
                          if("panel".equals(b.getType())){
                              if(b.getName().equals(((JComponent)obj).getName())){
                                   b.getSubWidgets().add(bean);
                                   break;
                              }else if(b.getSubWidgets().size() > 0){
                                addNewComponentToBeanList(b.getSubWidgets(), obj, bean);
                              }
                          }
                      }
                  }
          }else{
            mainComponentBean.getSubWidgets().add(bean);
          }
        showCode();
    }

    public void updateComponentSelection(String componentName){
       JComponent cmp = componentsTable.get(componentName);
       if(cmp != null){
            int x  = cmp.getX();
            int y  = cmp.getY()+ DesignerPanel.HEADER_HEIGHT;
            Component comp = cmp.getParent();
            if(comp != null){
                while(!comp.getName().equals(mainComponentName)){
                    x +=  comp.getX();
                    y +=  comp.getY();
                    comp = comp.getParent();
                }
                setComponentX(x-1);
                setComponentY(y);
                setComponentW(cmp.getWidth()+1);
                setComponentH(cmp.getHeight()+1);

            }
          repaint();
       }

    }

    public void showCode() {
              StringBuffer buf = new StringBuffer();
              buf.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
              buf.append("<");
              buf.append(mainComponentBean.getType());
              buf.append(" ");
              //Name
              buf.append("name=\"");
              buf.append(mainComponentBean.getName());
              buf.append("\" ");
               //Title
              buf.append("title=\"");
              buf.append(mainComponentBean.getTitle());
              buf.append("\" ");

              //Icon
              if(!".".equals(mainComponentBean.getImage().getName()) && !"".equals(mainComponentBean.getImage().getName())){
                buf.append("icon=\"");
                buf.append(mainComponentBean.getImage().getPath());
                buf.append("\" ");
              }

              //Location
              buf.append("Location=\"");
              buf.append(mainComponentBean.getX());
              buf.append(",");
              buf.append(mainComponentBean.getY());
              buf.append("\" ");
              //Size
              buf.append("Size=\"");
              buf.append(mainComponentBean.getDimension().width);
              buf.append(",");
              buf.append(mainComponentBean.getDimension().height);
              buf.append("\" ");
              //visualproperties

              buf.append("visualproperties=\"");
              buf.append(mainComponentBean.isVisualProperties());
              buf.append("\" ");

              buf.append("assignable=\"");
              buf.append(mainComponentBean.isAssignable());
              buf.append("\" ");

              if(mainComponentBean.isCeckable()){
                buf.append("checkable=\"");
                buf.append(mainComponentBean.isCeckable());
                buf.append("\" ");
              }

              //modal
              if(mainComponentBean.isModal()){
                buf.append("modal=\"");
                buf.append(mainComponentBean.isModal());
                buf.append("\" ");
              }
              
              //timer
              if(mainComponentBean.isShowTimer()){
                  buf.append("timer=\"");
                  buf.append(mainComponentBean.isShowTimer());
                  buf.append("\" ");
                }

              //resizable
              if(mainComponentBean.isResizable()){
                buf.append("resizable=\"");
                buf.append(mainComponentBean.isResizable());
                buf.append("\" ");
              }

              //backgroundcolor
              if(mainComponentBean.getColor() != null){
                buf.append("background=\"");
                buf.append(mainComponentBean.getColorName());
                buf.append("\" ");
              }

              buf.append(">\n ");
              showGUIToEditor(mainComponentBean,buf);
              buf.append("</");
              buf.append(mainComponentBean.getType());
              buf.append(">");
              textArea.setText(buf.toString());
        }


     private void showGUIToEditor(EBIGUIWidgetsBean lst,StringBuffer b) {

            Iterator iter = lst.getSubWidgets().iterator();
            StringBuffer buf = b;

            while(iter.hasNext()){
              EBIGUIWidgetsBean bn = (EBIGUIWidgetsBean)iter.next();
              //type
              if(!bn.getType().equals("panel")){
                buf.append("\t\t");
              }
              buf.append("<");
              buf.append(bn.getType());
              buf.append(" ");
              //Name
              buf.append("name=\"");
              buf.append(bn.getName());
              buf.append("\" ");

              if(!bn.isEnabled()){
                buf.append("enabled=\"");
                buf.append(bn.isEnabled());
                buf.append("\" ");
              }
              
              if(!bn.isEditable()){
                  buf.append("editable=\"");
                  buf.append(bn.isEditable());
                  buf.append("\" ");
                }

              if(!bn.isVisible()){
                buf.append("visible=\"");
                buf.append(bn.isVisible());
                buf.append("\" ");
              }

              //Max
              if(bn.getMax() != -1){
                buf.append("max=\"");
                buf.append(bn.getMax());
                buf.append("\" ");
              }

               //Min
              if(bn.getMin() != -1){
                buf.append("min=\"");
                buf.append(bn.getMin());
                buf.append("\" ");
              }

              //TabIndex
              if(bn.getTabIndex() != -1){
                buf.append("tabindex=\"");
                buf.append(bn.getTabIndex());
                buf.append("\" ");
              }

               //Title
              if(!"".equals(bn.getTitle())){  
                buf.append("title=\"");
                buf.append(bn.getTitle());
                buf.append("\" ");
              }
                
               //i18ntooltip
              if(!"".equals(bn.getI18NToolTip())){
                buf.append("i18ntooltip=\"");
                buf.append(bn.getI18NToolTip());
                buf.append("\" ");
              }

              //fitOnResize
              if(bn.isFitOnResize()){
                buf.append("fitonresize=\"");
                buf.append(bn.isFitOnResize());
                buf.append("\" ");
              }

              //Icon
              if(!".".equals(bn.getImage().getName())){
                buf.append("icon=\"");
                buf.append(bn.getImage().getPath());
                buf.append("\" ");
              }
                
             //backgroundcolor
              if(bn.getColor() != null){
                buf.append("background=\"");
                buf.append(bn.getColorName());
                buf.append("\" ");
              }

              //keyevent
              if(bn.getKeyEvent() >  0){
                buf.append("keyevent=\"");
                buf.append(bn.getKeyEvent());
                buf.append("\" ");
              }
              //keystroke
              if(bn.getKeyStroke() != null){
                buf.append("keyevent=\"");
                buf.append(bn.getKeyStroke().getKeyCode());
                buf.append("\" ");
              }
              
              //Location
              if(!bn.getType().toLowerCase().equals("codecontrol")){

                  buf.append("Location=\"");
                  buf.append(bn.getX());
                  buf.append(",");
                  buf.append(bn.getY());
                  buf.append("\" ");
                  //Size
                  buf.append("Size=\"");
                  if(bn.isWidthAutoResize()){
                   buf.append(bn.getDimension().width);
                   buf.append("%");
                  }else{
                   buf.append(bn.getDimension().width);
                  }
                  buf.append(",");

                  if(bn.isHeightAutoResize()){
                    buf.append(bn.getDimension().height);
                    buf.append("%");
                  }else{
                      buf.append(bn.getDimension().height);
                  }
                  buf.append("\"");
              }else{
                  buf.append("path=\"");
                  buf.append(bn.getPath());
                  buf.append("\" ");
                  buf.append("classname=\"");
                  buf.append(bn.getClassName());
                  buf.append("\" ");

              }

              if(bn.getSubWidgets().size() > 0){
               buf.append(">\n");
               showGUIToEditor(bn,buf);
               buf.append("</");
               buf.append(bn.getType());
               buf.append(">");
              }else{
               buf.append("/>");
              }
              buf.append("\n");
            }
        }


      public void loadXMLGUIToPanel(String xmlFile) {

            xmlGui = new EBIXMLGUIReader();
            if(xmlGui.loadXMLGUI(xmlFile)){
                mainComponentName = xmlGui.getCompObjects().getName();
                mainComponent.put(mainComponentName,xmlGui.getCompObjects());
                mainComponentBean =  mainComponent.get(mainComponentName);
                panel.removeAll();
                setPreferredSize(new Dimension(mainComponentBean.getDimension().width+20, mainComponentBean.getDimension().height+30));
                panel.setBounds(0,DesignerPanel.HEADER_HEIGHT, mainComponentBean.getDimension().width, mainComponentBean.getDimension().height);
                setModuleIcon(mainComponentBean.getIcon());
                setModuleTitle(mainComponentBean.getTitle());
                setChangePropertiesVisible(mainComponentBean.isVisualProperties());
                addComponentsToContainer(panel, mainComponentBean.getSubWidgets());
                panel.repaint();
            }
      }

      public void saveXMLToFile(){
         Writer output;

           try {
            if(textArea.getText().equals("")){
               showCode();
            }
            output = new BufferedWriter(new FileWriter(file));
            output.write(textArea.getText());
            output.close(); 
          } catch (IOException e) {
              e.printStackTrace();
          }
     }

    public void setTabSave(boolean saved){
       if(saved){
        tabPane.setTitleAt(0, "Designer");
           tabPane.setTitleAt(1,"Code");
       }else{
           tabPane.setTitleAt(0,"Designer*");
           tabPane.setTitleAt(1,"Code*");
       }
       
       EBIPGFactory.canRelease = saved;
    }

     public void deleteSelectedComponent(){
           Component comp =  componentsTable.get(selectedComponentName);
           if(comp != null){
               int n = JOptionPane.showConfirmDialog(
                                            null,
                                            EBIPGFactory.getLANG("EBI_LANG_WOULD_YOU_DELETE_THIS_COMPONENT"),
                                            EBIPGFactory.getLANG("EBI_LANG_WARNING"),
                                            JOptionPane.YES_NO_OPTION);
                  if(n == JOptionPane.YES_OPTION){
                       JComponent co = (JComponent)comp.getParent();
                       co.remove(comp);

                       removeComponentFromBeanList(mainComponentBean.getSubWidgets(), mainComponentBean,comp.getName());
                       panel.selectMainComponent(panel);
                       setDrawFocusRectangle(true);
                       panel.repaint();

                  }else{
                     return;
                  }
                  setTabSave(false);
           }
      }

    public JComponent updateComponentName(String name,String newName){
       JComponent comp = null;
        if(componentsTable.get(name) != null){
          JComponent tmpCmp =   componentsTable.get(name);
          componentsTable.remove(name);
          componentsTable.put(newName,tmpCmp);
          comp = componentsTable.get(newName);
        }
        return comp;
    }

    public EBIGUIWidgetsBean getSelectedBean(){
        return this.selectedBean;
    }

    public void setSelectedBean(EBIGUIWidgetsBean bean){
        this.selectedBean = bean;
    }

}