package ebiNeutrino.core.GUIDesigner;

import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.KeyStroke;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

public class EBIXMLGUIReader {

	private EBIGUIWidgetsBean widgetsObject = null;
	private File xmlPath = null;
    public Document xmlDoc = null;
    private SAXBuilder builder = null;
    private boolean stepOne = true;

    public EBIXMLGUIReader(File xmlPath){
        builder = new SAXBuilder();
        widgetsObject = new EBIGUIWidgetsBean();
        this.xmlPath = xmlPath;
        stepOne = true;
    }

    public EBIXMLGUIReader(){
        builder = new SAXBuilder();
        widgetsObject = new EBIGUIWidgetsBean();
        stepOne = true;
    }


    public boolean loadXMLGUI(){
        boolean ret = true;
         stepOne = true;
		    try{
     		    xmlDoc = builder.build(this.xmlPath);
                ret = readXMLGUI(xmlDoc.getRootElement());

         }catch(IOException ex){ret = false; EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);}
     	 catch(JDOMException ex){ret = false; EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);}
         finally{return ret;}

	}

    public boolean loadXMLGUI(String file){
         boolean ret = true;
         stepOne = true;
		    try{
             byte[] bytes = file.getBytes("utf-8");
             xmlDoc = builder.build(new ByteArrayInputStream(bytes));
             ret = readXMLGUI(xmlDoc.getRootElement());
         }catch(IOException ex){ret = false; EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);}
     	 catch(JDOMException ex){ret = false; EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);}
         finally{return ret;}    

    }

    private boolean readXMLGUI(Element el){
		//Read from xml file

         if(!parseXMLGUI(el)){
             return false;
         }

         Iterator children = el.getChildren().iterator();

		 while(children.hasNext()){
		 	readXMLGUI((Element)children.next());
		 }

        return true;
    }


	private boolean parseXMLGUI(Element el){
             try{
              Iterator atts = el.getAttributes().iterator();
              EBIGUIWidgetsBean widg = new EBIGUIWidgetsBean();

              while(atts.hasNext()){

				 Attribute att = (Attribute) atts.next();
                 String attName = att.getName().trim().toLowerCase();

                 if("name".equals(attName) ){
                    widg.setName(att.getValue());
				 }
				 if("location".equals(attName)){
                   widg.setPoint(parsePoint(att.getValue()));
				 }
				 if("size".equals(attName)){
                   parseDimension(widg,att.getValue());
				 }
				 if("title".equals(attName)){
                    widg.setTitle(att.getValue());
				 }
				 if("icon".equals(attName)){
                    if(!"".equals(att.getValue())){  
                     widg.setIcon(att.getValue());
                    }
				 }
                 if("background".equals(attName)){
                   if(!"".equals(att.getValue())){
                     widg.setColor(att.getValue());
                   }
                 }
                 if("path".equals(attName)){
                   if(!"".equals(att.getValue())){  
                     widg.setPath(att.getValue());
                   }
                 }
                 if("keyevent".equals(attName)){
                   if(!"".equals(att.getValue()) && KeyStroke.getKeyStroke(att.getValue()) != null){
                    widg.setKeyEvent(KeyStroke.getKeyStroke(att.getValue()).getKeyCode());
                   }
                 }
                 if("keystroke".equals(attName)){
                   if(!"".equals(att.getValue()) && KeyStroke.getKeyStroke(att.getValue()) != null){
                     widg.setKeyStroke(KeyStroke.getKeyStroke(att.getValue()).getKeyCode());
                   }
                 }
                 if("classname".equals(attName)){
                   if(!"".equals(att.getValue())){
                     widg.setClassName(att.getValue());
                   }
                 }
                 if("checkable".equals(attName)){
                   if(!"".equals(att.getValue())){
                       try{
                        widg.setCeckable(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setCeckable(false);
                       }
                   }
                 }
                 if("visualproperties".equals(attName)){
                   if(!"".equals(att.getValue())){
                       try{
                          widg.setVisualProperties(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setVisualProperties(false);
                       }
                   }
                 }
                 if("pessimistic".equals(attName)){
                   if(!"".equals(att.getValue())){
                       try{
                          widg.setPessimistic(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setPessimistic(true);
                       }
                   }
                 }

                  if("assignable".equals(attName)){
                   if(!"".equals(att.getValue())){
                       try{
                          widg.setAssignable(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setAssignable(false);
                       }
                   }
                 }
                 if("modal".equals(attName)){

                   if(!"".equals(att.getValue())){
                       try{
                          widg.setModal(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setModal(false);
                       }
                   }

                 }
                 if("enabled".equals(attName)){

                   if(!"".equals(att.getValue())){
                       try{
                          widg.setEnabled(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setEnabled(false);
                       }
                   }

                 }
                 if("editable".equals(attName)){

                     if(!"".equals(att.getValue())){
                         try{
                            widg.setEditable(Boolean.parseBoolean(att.getValue()));
                         }catch(Exception ex){
                            widg.setEnabled(false);
                         }
                     }

                 }
                 if("visible".equals(attName)){

                   if(!"".equals(att.getValue())){
                       try{
                          widg.setVisible(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                          widg.setVisible(true);
                       }
                   }

                 }
                 if("resizable".equals(attName)){
                     
                     if(!"".equals(att.getValue())){
                       try{
                           widg.setResizable(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                           widg.setResizable(false);
                       }
                     }

                 }
                 if("fitonresize".equals(attName)){
                     
                     if(!"".equals(att.getValue())){
                       try{
                          widg.setFitOnResize(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                           widg.setFitOnResize(false);
                       }
                     }
                 }
                 if("timer".equals(attName)){
                     
                     if(!"".equals(att.getValue())){
                       try{
                          widg.setShowTimer(Boolean.parseBoolean(att.getValue()));
                       }catch(Exception ex){
                    	   widg.setShowTimer(false);
                       }
                     }
                 }
                 if("i18ntooltip".equals(attName)){
                   if(!"".equals(att.getValue())){
                     widg.setI18NToolTip(att.getValue());
                   }
                 }
                 if("min".equals(attName)){
                     try{
                        widg.setMin(Integer.parseInt(att.getValue()));
                     }catch(NumberFormatException ex){
                        widg.setMin(0);  
                     }
                 }
                 if("max".equals(attName)){
                     try{
                        widg.setMax(Integer.parseInt(att.getValue()));
                     }catch(NumberFormatException ex){
                        widg.setMax(0);
                     }
                 }
                 if("tabindex".equals(attName)){
                     try{
                        widg.setTabIndex(Integer.parseInt(att.getValue()));
                     }catch(NumberFormatException ex){
                        widg.setTabIndex(0); 
                     }
                 }
               }

               widg.setType(el.getName().trim().toLowerCase());

               if(!stepOne){
                 setParentComponent(el.getParentElement().getAttributeValue(getAttName(el)),widgetsObject,widg);
               }else{
                 stepOne = false;
                 widgetsObject = widg;
               }
             }catch(Exception ex) {
                 ex.printStackTrace();
                 return false;
             }

        return true;
	}

    private String getAttName(Element el) {
        String retStr = "";
        List list = el.getParentElement().getAttributes();
        Iterator iter = list.iterator();

        while(iter.hasNext()){
           Attribute att = (Attribute) iter.next();
           if("name".equals(att.getName().toLowerCase())){
               retStr = att.getName();
                break;
            }
        }

        return retStr;
    }

    private Point parsePoint(String value) {
        Point pt  ;
        try{
            String[] val = value.trim().split(",");

            pt = new Point();
            pt.setLocation(Integer.parseInt(val[0].trim()), Integer.parseInt(val[1].trim()));
        }catch(NumberFormatException ex){
          pt = new Point(0,0);  
        }
        return pt;
    }

    private void parseDimension(EBIGUIWidgetsBean widg,String value) {

        String[] val = value.trim().split(",");
        Dimension dim = new Dimension();
        
        try{
            
            if(val[0].indexOf("%") != -1){
              val[0] = val[0].substring(0,val[0].length()-1);
              widg.setWidthAutoResize(true);

            }
            if(val[1].indexOf("%") != -1){
              val[1] = val[1].substring(0,val[1].length()-1);
              widg.setHeightAutoResize(true);
            }

            dim.setSize(Integer.parseInt(val[0].trim()), Integer.parseInt(val[1].trim()));
            
        }catch(NumberFormatException ex){
            dim.setSize(20,100);
        }
        widg.setDimension(dim);

    }

    private void setParentComponent(String name,EBIGUIWidgetsBean runBean, EBIGUIWidgetsBean toSet) {

        if(name.equals(runBean.getName())){
          runBean.getSubWidgets().add(toSet);
          return;
        }else{
            Iterator iter  = runBean.getSubWidgets().iterator();
            while(iter.hasNext()){
               EBIGUIWidgetsBean bn = (EBIGUIWidgetsBean)iter.next();
               setParentComponent(name,bn,toSet);
            }

        }
    }

    public EBIGUIWidgetsBean getCompObjects() {
		return widgetsObject;
	}


	public void setCompObjects(EBIGUIWidgetsBean compObjects) {
		this.widgetsObject = compObjects;
	}


	public File getXmlPath() {
		return xmlPath;
	}


	public void setXmlPath(File xmlPath) {
		this.xmlPath = xmlPath;
	}

	public EBIGUIWidgetsBean getWidgetsObject() {
		return widgetsObject;
	}

	public void setWidgetsObject(EBIGUIWidgetsBean widgetsObject) {
		this.widgetsObject = widgetsObject;
	}

}
