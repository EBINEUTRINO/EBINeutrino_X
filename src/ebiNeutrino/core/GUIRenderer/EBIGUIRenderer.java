package ebiNeutrino.core.GUIRenderer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import ebiNeutrino.core.GUIRenderer.tableModelInterface.ExtTableModelListener;
import ebiNeutrino.core.settings.EBIListItem;
import ebiNeutrinoSDK.gui.component.EBIExtendedPanel;
import ebiNeutrinoSDK.utils.EBISaveRestoreTableProperties;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIGUIRenderer;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;
import groovy.lang.Binding;
import groovy.lang.Script;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;



public class EBIGUIRenderer implements IEBIGUIRenderer {

    private EBIMain ebiMain = null;
    public  final Hashtable<String,Object> toToolbar = new Hashtable<String,Object>();
    public  final Hashtable<String, EBIGUIWidgetsBean>  componentsTable = new Hashtable<String,EBIGUIWidgetsBean>();
    public  final Hashtable<String,List<Object>> resizeContainer = new Hashtable<String,List<Object>>();
    private final Hashtable<String,List<Object>> scriptContainer = new Hashtable<String,List<Object>>();
    private final Hashtable<String,Integer> componentGet = new Hashtable<String,Integer>();
    private String mainComponentName = "";
    private boolean  isInit = false;
    public String fileToTabPath = "";
    public boolean canShow = true;
    private EBIFocusTraversalPolicy focusTraversal = null;
    private int projectCount = 0;
    private int projectCountEnabled = 0;
    private EBISaveRestoreTableProperties tabProp = null;
    private JXTable actualTable = null;
    private String actualCmpStr = "";
    private String[] roots=null;
    private GroovyScriptEngine gse = null;


    public EBIGUIRenderer(EBIMain main) {
        ebiMain = main;
        focusTraversal = new EBIFocusTraversalPolicy();

        tabProp = new EBISaveRestoreTableProperties();
        roots = new String[] { "ebiExtensions/" };
        try {
            gse = new GroovyScriptEngine(roots);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final void init(){

        componentsTable.clear();
        toToolbar.clear();
        resizeContainer.clear();
        scriptContainer.clear();
        componentGet.clear();

        focusTraversal = new EBIFocusTraversalPolicy();
        fileToTabPath = "";
        isInit = true;
        projectCount =0;
        projectCountEnabled =0;
    }

    public final synchronized void loadProject(final String path){
   
        final EBIXMLGUIReader xmlGui1 = new EBIXMLGUIReader();
        xmlGui1.setXmlPath(new File("ebiExtensions/" + path));
        init();
        ebiMain.container.resetListItem();

        if(xmlGui1.loadXMLGUI()){

              final Iterator ix = xmlGui1.getCompObjects().getSubWidgets().iterator();

              while(ix.hasNext()) {
                final EBIGUIWidgetsBean bx = (EBIGUIWidgetsBean)ix.next();
                ebiMain.container.addListItem(new EBIListItem(bx.getIcon(), EBIPGFactory.getLANG(bx.getTitle()), bx.getPath(), bx.getName(), false));
            }

        }else{
           canShow = false;
        }

        final File dir = new File("ebiExtensions/EBICRM/");

        final FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".app.xml");
            }
        };

        final List<Object> toScript = new ArrayList<Object>();

        final String[] children = dir.list(filter);
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                // Get filename of file or directory

                final EBIXMLGUIReader xmlApps = new EBIXMLGUIReader();
                xmlApps.setXmlPath(new File("ebiExtensions/EBICRM/" +  children[i]));


                if(xmlApps.loadXMLGUI()){

                    final EBIGUIWidgetsBean bean = xmlApps.getWidgetsObject();

                    if("appmodule".equals(bean.getType().toLowerCase())){
                        Iterator iter  = bean.getSubWidgets().iterator();
                        while(iter.hasNext()){

                            EBIGUIWidgetsBean bx = (EBIGUIWidgetsBean)iter.next();

                            if("codecontrol".equals(bx.getType().toLowerCase())){
                                if ("groovy".equals(bx.getName())) {

                                    EBIGUIScripts script = new EBIGUIScripts();
                                    script.setType("groovy");
                                    script.setPath(bx.getPath());
                                    script.setName(bx.getName());
                                    script.setClassName(bx.getClassName());
                                    toScript.add(script);

                                }else if ("java".equals(bx.getName())) {

                                    EBIGUIScripts script = new EBIGUIScripts();
                                    script.setType("java");
                                    script.setPath(bx.getPath());
                                    script.setName(bx.getName());
                                    script.setClassName(bx.getClassName());
                                    toScript.add(script);
                                }
                                scriptContainer.put("app",toScript);
                            }else if("application".equals(bx.getType().toLowerCase())){
                                ebiMain.container.myListmodel.addElement(new EBIListItem(bx.getIcon(), EBIPGFactory.getLANG(bx.getTitle()),bx.getPath(),bx.getName(),true));
                            }
                        }
                    }
                }
            }
        }




    }

    private  final void addXMLGUI(EBIGUIWidgetsBean bx,String path){
        if(!"".equals(bx.getPath())){
          if(bx.getType().toLowerCase().equals("visualpanel")){
             fileToTabPath = path;
          }

          loadGUI(bx.getPath());
          showGUI();

          if(bx.isEnabled()){
            projectCountEnabled++;
          }
          projectCount++;
          fileToTabPath= "";
        }

    }

    public final void loadGUI(String path){
        loadGUIExt("EBICRM/"+path);
        canShow = true;
    }

    public final void useGUI(String name){
        this.mainComponentName = name;
    }

    public String loadGUIPlus(String path){

        loadGUIExtPlus("EBICRM/" + path);
        canShow = true;

        return mainComponentName;
    }

    public final void loadGUIExtPlus(String path) {

        mainComponentName = "";

        final EBIXMLGUIReader xmlGui= new EBIXMLGUIReader();
        xmlGui.setXmlPath(new File("ebiExtensions/" + path));

        if(xmlGui.loadXMLGUI()){
            mainComponentName =""+(new Date().getTime());

            if(!isInit){
                removeGUIObject(mainComponentName);
            }

            focusTraversal = new EBIFocusTraversalPolicy();
            componentsTable.put(mainComponentName , xmlGui.getCompObjects());

            final List<Object> toResize = new ArrayList<Object>();
            final List<Object> toScript = new ArrayList<Object>();

            resizeContainer.put(mainComponentName,toResize);
            scriptContainer.put(mainComponentName,toScript);

            if(!"ebibar".equals(xmlGui.getCompObjects().getType().toLowerCase())){

                renderGUI(null, null);

                if(xmlGui.getCompObjects().getType().toLowerCase().equals("visualpanel")){
                    fileToTabPath = path;
                }
            }
        }else{
            canShow = false;
        }

    }
    
    public final void removeGUIObject(String strID){
        if(componentsTable.containsKey(strID)){
            componentsTable.remove(strID);
        }
        if(resizeContainer.containsKey(strID)){
            resizeContainer.remove(strID);
        }
        if(scriptContainer.containsKey(strID)){
            scriptContainer.remove(strID);
        }
    }
    

    public final void loadGUIExt(String path) {
        mainComponentName = "";

        final EBIXMLGUIReader xmlGui= new EBIXMLGUIReader();
        xmlGui.setXmlPath(new File("ebiExtensions/" + path));

        if(xmlGui.loadXMLGUI()){
            mainComponentName = xmlGui.getCompObjects().getName();

            if(!isInit){
                removeGUIObject(mainComponentName);
            }

            focusTraversal = new EBIFocusTraversalPolicy();
            componentsTable.put(mainComponentName , xmlGui.getCompObjects());

            final List<Object> toResize = new ArrayList<Object>();
            final List<Object> toScript = new ArrayList<Object>();

            resizeContainer.put(mainComponentName,toResize);
            scriptContainer.put(mainComponentName,toScript);

            if(!"ebibar".equals(xmlGui.getCompObjects().getType().toLowerCase())){
                
            	renderGUI(null, null);
                
                if(xmlGui.getCompObjects().getType().toLowerCase().equals("visualpanel")){
                    fileToTabPath = path;
                }
            }

        }else{
           canShow = false; 
        }
    }

    /**
     * Create and Show components readed from the xml file
     * @param obj
     * @param tmpList
     * @return
     */

    public final void renderGUI(Object obj, List<EBIGUIWidgetsBean> tmpList){

        final EBIGUIWidgetsBean list = componentsTable.get(mainComponentName);

        EBIVisualPanelTemplate panel = null;
        EBIDialog dialog = null;
        boolean isVisualPanel = false;

        if(obj == null){

             if("visualpanel".equals(list.getType())){
                panel = new EBIVisualPanelTemplate(true,ebiMain.system);
             }else{
                panel = new EBIVisualPanelTemplate(false);
                panel.showStripe(false);
             }

             panel.setName(mainComponentName);
             panel.setDoubleBuffered(true);

             String title;
             if(list.getTitle().indexOf("EBI_LANG") != -1){
                 title = EBIPGFactory.getLANG(list.getTitle());
             }else{
                title = list.getTitle();
             }

             panel.setModuleTitle(title);

               // if(list.getIcon() == null){
             panel.setModuleIcon(EBIConstant.ICON_APP);
               // }else{
               //   panel.setModuleIcon(list.getIcon());
               // }

            if(!list.isPessimistic()){
              panel.setPessimistic(false);
            }else if(!"visualpanel".equals(list.getType())){
              panel.setPessimistic(false);
            }

            if(list.getColor() != null){
                panel.setBackgroundColor(list.getColor());
            }

            panel.setSize(list.getDimension());
            if(!list.isVisualProperties()){
               panel.setChangePropertiesVisible(false);
            }

            panel.setAssignable(list.isAssignable());
            if ("visualpanel".equals(list.getType())){

                isVisualPanel = true;
                panel.setClosable(false);

            } else if("dialog".equals(list.getType())){

                dialog = new EBIDialog(ebiMain);
                dialog.setName(mainComponentName);
                
                dialog.storeLocation(true);
                dialog.setModal(list.isModal());
                dialog.setResizable(list.isResizable());
                dialog.storeSize(list.isResizable());
                panel.setClosable(false);

                dialog.setTitle(title);
                panel.setClosable(true);
                if(list.getIcon() != null){
                    dialog.setIconImage(list.getIcon().getImage());
                }

                dialog.setLocation(list.getPoint());
                dialog.setSize(list.getDimension().width, list.getDimension().height+30);
                isVisualPanel = false;
            }
        }

        // check if recursion
        Iterator iter;
        if (obj == null) {
            iter = list.getSubWidgets().iterator();
        } else {
            iter = tmpList.iterator();
        }

        //parse component from Widgetbeans
        while (iter.hasNext()) {
            final EBIGUIWidgetsBean bean = (EBIGUIWidgetsBean) iter.next();

            if ("button".equals(bean.getType())) {
                final JButton button = new JButton();
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                   button.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                   button.setText(bean.getTitle());
                }

                button.setIcon(bean.getIcon());
                button.setName(bean.getName());
                button.setEnabled(bean.isEnabled());

                if(!bean.isVisible()){
                    button.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    button.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),button);
                }

                if(bean.getColor() != null){button.setBackground(bean.getColor());}
                button.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setImageName(bean.getImage().toString());
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(button);

                if (obj == null) {
                    panel.add(button, null);
                } else {
                    ((JPanel) obj).add(button, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());
                
            } else if ("list".equals(bean.getType())) {
                final JList jlist = new JList();
                jlist.setEnabled(bean.isEnabled());
                jlist.setName(bean.getName());
                jlist.setFont(new Font("Verdana", Font.PLAIN, 10));
                jlist.setDoubleBuffered(true);
                jlist.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                final JScrollPane scr = new JScrollPane();
                scr.setViewportView(jlist);
                scr.setName(bean.getName());

                if(!bean.isVisible()){
                    jlist.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    jlist.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),jlist);
                }

                if(bean.getColor() != null){jlist.setBackground(bean.getColor());}
                scr.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                scr.setBackground(bean.getColor());

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setScrollComponent(jlist);
                res.setComponent(scr);

                if (obj == null) {
                    panel.add(scr, null);
                } else {
                    ((JPanel) obj).add(scr, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("textfield".equals(bean.getType())) {

                final JTextField textField = new JTextField();
                textField.setDoubleBuffered(true);
                textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                   textField.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                   textField.setText(bean.getTitle());
                }
                textField.setName(bean.getName());
                textField.setEnabled(bean.isEnabled());

                if(!bean.isVisible()){
                    textField.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    textField.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),textField);
                }

                textField.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                if(bean.getColor() != null){textField.setBackground(bean.getColor());}

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(textField);

                if (obj == null) {
                    panel.add(textField, null);
                } else {
                    ((JPanel) obj).add(textField, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            }  else if ("formattedtextfield".equals(bean.getType())) {
                final JFormattedTextField textField = new JFormattedTextField();
                textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                textField.setBackground(new Color(250,250,250));
                textField.setDoubleBuffered(true);
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                   textField.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    textField.setText(bean.getTitle());
                }
                textField.setName(bean.getName());
                textField.setEnabled(bean.isEnabled());

                if(!bean.isVisible()){
                    textField.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    textField.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),textField);
                }

                textField.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                if(bean.getColor() != null){textField.setBackground(bean.getColor());}

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(textField);

                if (obj == null) {
                    panel.add(textField, null);
                } else {
                    ((JPanel) obj).add(textField, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("combobox".equals(bean.getType())) {

                final JComboBox combo = new JComboBox();
                combo.setFont(new Font("Verdana", Font.PLAIN, 10));
                combo.setName(bean.getName());
                combo.setEnabled(bean.isEnabled());
                combo.setEditable(bean.isEditable());
                combo.setDoubleBuffered(true);

                if(!bean.isVisible()){
                    combo.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    combo.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){

                    focusTraversal.addComponent(bean.getTabIndex(), combo);
                }

                combo.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                if(bean.getColor() != null){combo.setBackground(bean.getColor());}

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(combo);

                if (obj == null) {
                    panel.add(combo, null);
                } else {
                    ((JPanel) obj).add(combo, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("checkbox".equals(bean.getType())) {

                final JCheckBox box = new JCheckBox();
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                    box.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    box.setText(bean.getTitle());
                }
                box.setName(bean.getName());
                box.setEnabled(bean.isEnabled());
                box.setDoubleBuffered(true);

                if(!bean.isVisible()){
                    box.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    box.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),box);
                }

                box.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                if(bean.getColor() != null){box.setBackground(bean.getColor());}

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(box);

                if (obj == null) {
                    panel.add(box, null);
                } else {
                    ((JPanel) obj).add(box, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("radiobutton".equals(bean.getType())) {

                final JRadioButton radio = new JRadioButton();
                radio.setDoubleBuffered(true);

                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                    radio.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    radio.setText(bean.getTitle());
                }
                
                radio.setName(bean.getName());
                radio.setEnabled(bean.isEnabled());

                if(!"".equals(bean.getI18NToolTip())){
                    radio.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(!bean.isVisible()){
                    radio.setVisible(bean.isVisible());
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),radio);
                }

                radio.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));
                if(bean.getColor() != null){radio.setBackground(bean.getColor());}

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(radio);

                if (obj == null) {
                    panel.add(radio, null);
                } else {
                    ((JPanel) obj).add(radio, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("textarea".equals(bean.getType())) {

                final JTextArea textArea = new JTextArea();
                textArea.setFont(new Font("Verdana", Font.PLAIN, 10));
                textArea.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                    textArea.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    textArea.setText(bean.getTitle());
                }
                textArea.setDoubleBuffered(true);
                textArea.setName(bean.getName());
                textArea.setEnabled(bean.isEnabled());
                textArea.setMargin(new Insets(10,10,0,0));

                if(!"".equals(bean.getI18NToolTip())){
                    textArea.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),textArea);
                }

                final JScrollPane scr = new JScrollPane();
                scr.setViewportView(textArea);
                scr.setName(bean.getName());
                scr.setDoubleBuffered(true);
                scr.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(bean.getColor() != null){
                    scr.setBackground(bean.getColor());
                    textArea.setBackground(bean.getColor());
                }

                if(!bean.isVisible()){
                    scr.setVisible(bean.isVisible());
                }

                scr.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setScrollComponent(textArea);
                res.setComponent(scr);

                if (obj == null) {
                    panel.add(scr, null);
                } else {
                    ((JPanel) obj).add(scr, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            }else if("editor".equals(bean.getType())){
                final JEditorPane editPane = new JEditorPane();
                editPane.setContentType("text/html");
                editPane.setDoubleBuffered(true);
                editPane.setMargin(new Insets(10,10,0,0));

                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                    editPane.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    editPane.setText(bean.getTitle());
                }

                editPane.setName(bean.getName());
                editPane.setEnabled(bean.isEnabled());

                if(!"".equals(bean.getI18NToolTip())){
                    editPane.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),editPane);
                }

                final JScrollPane scr = new JScrollPane();
                scr.setViewportView(editPane);
                scr.setName(bean.getName());
                scr.setDoubleBuffered(true);
                scr.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(bean.getColor() != null){
                    scr.setBackground(bean.getColor());
                    editPane.setBackground(bean.getColor());
                }

                if(!bean.isVisible()){
                    scr.setVisible(bean.isVisible());
                }

                scr.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setScrollComponent(editPane);
                res.setComponent(scr);

                if (obj == null) {
                    panel.add(scr, null);
                } else {
                    ((JPanel) obj).add(scr, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("label".equals(bean.getType())) {
                final JLabel label = new JLabel();
                label.setFont(new Font("Verdana", Font.PLAIN, 10));
                if(bean.getTitle().indexOf("EBI_LANG") != -1){
                    label.setText(EBIPGFactory.getLANG(bean.getTitle()));
                }else{
                    label.setText(bean.getTitle());
                }
                label.setName(bean.getName());
                label.setDoubleBuffered(true);

                if(!bean.isVisible()){
                    label.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    label.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }
                
                if(bean.getColor() != null){label.setOpaque(true); label.setBackground(bean.getColor());}
                                        
                label.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(label);

                if (obj == null) {
                    panel.add(label, null);
                } else {
                    ((JPanel) obj).add(label, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("table".equals(bean.getType())) {
                final JXTable jtable = new JXTable(){
                    public Component prepareRenderer(TableCellRenderer renderer, int rowIndex, int vColIndex) {
                        Component c = null;
                           try{
                            c = super.prepareRenderer(renderer, rowIndex, vColIndex);
                            if (c instanceof JComponent) {
                                JComponent jc = (JComponent)c;
                                if(getValueAt(rowIndex, vColIndex) != null){
                                    jc.setToolTipText("<html><head></head><body><b>"+getValueAt(rowIndex, vColIndex).toString().replaceAll("\n","<br>")+"</b></body></html>");
                                }
                            }
                           }catch(IndexOutOfBoundsException ex){
                                c =  super.prepareRenderer(renderer, 0, 0);
                           }
                        return c;
                    }
                };
                jtable.setName(mainComponentName+"_"+bean.getName());
                jtable.setDoubleBuffered(true);
                final JTableHeader header = jtable.getTableHeader();
                header.setFont(new Font("Verdana", Font.PLAIN, 10));
                header.setDoubleBuffered(true);

                jtable.setFont(new Font("Verdana", Font.PLAIN, 10));
                jtable.setColumnControlVisible(true);
                jtable.setEnabled(bean.isEnabled());
                jtable.setAutoResizeMode(JXTable.AUTO_RESIZE_OFF);

                if(bean.getMin() != 1){
                    jtable.addHighlighter(HighlighterFactory.createAlternateStriping());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    jtable.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                final JScrollPane scr = new JScrollPane(jtable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                scr.setName(bean.getName());
                scr.setDoubleBuffered(true);
                scr.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(!bean.isVisible()){
                    scr.setVisible(bean.isVisible());
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),jtable);
                }

                scr.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIAbstractTableModel abstractModel = new EBIAbstractTableModel();
                jtable.setModel(abstractModel);

                if(bean.getSubWidgets().size() > 0){
                    final Iterator modIT  = bean.getSubWidgets().iterator();
                    
                    abstractModel.data = new Object[1][bean.getSubWidgets().size()+1];
                    abstractModel.columnNames = new String[bean.getSubWidgets().size()];
                    int i = 0;

                    while(modIT.hasNext()){
                        final EBIGUIWidgetsBean colBean = (EBIGUIWidgetsBean) modIT.next();

                        if(colBean.getTitle().indexOf("EBI_LANG") != -1){
                            abstractModel.columnNames[i] = EBIPGFactory.getLANG(colBean.getTitle() == null ? "" : colBean.getTitle());
                        }else{
                            abstractModel.columnNames[i] = colBean.getTitle() == null ? "" : colBean.getTitle();
                        }
                        abstractModel.data[0][i] = "";
                        i++;
                    }
                    abstractModel.data[0][0] = EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT");
                    abstractModel.fireTableStructureChanged();

                }

                header.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        tabProp.saveTableProperties(jtable);
                    }
                });

                jtable.getModel().addTableModelListener(new ExtTableModelListener(jtable) {
                    int selRow = 0;

                    @Override
                    public void tableChanged(TableModelEvent e) {
                        super.tableChanged(e);
                        selRow = super.getParentTable().getSelectedRow();
                        tabProp.restoreTableProperties(super.getParentTable());

                        if(selRow != -1) {

                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {

                                    if(selRow <= getParentTable().getRowCount()-1) {
                                        getParentTable().changeSelection(selRow, 0, false, false);
                                    }else{
                                        getParentTable().changeSelection(getParentTable().getRowCount()-1, 0, false, false);
                                    }
                                }
                            });

                        }

                    }
                });

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setScrollComponent(jtable);
                res.setComponent(scr);

                if (obj == null) {
                    panel.add(scr, null);
                } else {
                    ((JPanel) obj).add(scr, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("treetable".equals(bean.getType())) {
                final JXTreeTable jtable = new JXTreeTable();
                jtable.setName(bean.getName());
                jtable.setEnabled(bean.isEnabled());
                jtable.setDoubleBuffered(true);
                if(!"".equals(bean.getI18NToolTip())){
                    jtable.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),jtable);
                }

                final JScrollPane scr = new JScrollPane();
                scr.setName(bean.getName());
                scr.setViewportView(jtable);
                scr.setDoubleBuffered(true);
                scr.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(bean.getColor() != null){
                    scr.setBackground(bean.getColor());
                    jtable.setBackground(bean.getColor());
                }

                if(!bean.isVisible()){
                    scr.setVisible(bean.isVisible());
                }

                scr.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setScrollComponent(jtable);
                res.setComponent(scr);

                if (obj == null) {
                    panel.add(scr, null);
                } else {
                    ((JPanel) obj).add(scr, null);
                    res.setParentComponent((JComponent) obj);
                }

                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

            } else if ("timepicker".equals(bean.getType())) {
                final JXDatePicker timePicker = new JXDatePicker();
                timePicker.setName(bean.getName());
                timePicker.setEnabled(bean.isEnabled());
                timePicker.setFormats(EBIPGFactory.DateFormat);
                timePicker.setDoubleBuffered(true);
                timePicker.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(!"".equals(bean.getI18NToolTip())){
                    timePicker.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(!bean.isVisible()){
                    timePicker.setVisible(bean.isVisible());
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),timePicker);
                }

                if(bean.getColor() != null){timePicker.setBackground(bean.getColor());}
                timePicker.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(timePicker);
                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

                if (obj == null) {
                    panel.add(timePicker, null);
                } else {
                    ((JPanel) obj).add(timePicker, null);
                }

            } else if ("spinner".equals(bean.getType())) {
                final JSpinner spinner = new JSpinner();
                spinner.setEnabled(bean.isEnabled());
                spinner.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                spinner.setDoubleBuffered(true);
                final SpinnerNumberModel model = new SpinnerNumberModel();
                spinner.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                model.setMaximum(bean.getMax());
                model.setMinimum(bean.getMin());

                if(!bean.isVisible()){
                    spinner.setVisible(bean.isVisible());
                }

                if(!"".equals(bean.getI18NToolTip())){
                    spinner.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(bean.getTabIndex() != -1){
                    focusTraversal.addComponent(bean.getTabIndex(),spinner);
                }

                spinner.setName(bean.getName());
                spinner.setModel(model);
                if(bean.getColor() != null){spinner.setBackground(bean.getColor());}
                spinner.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(spinner);
                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

                if (obj == null) {
                    panel.add(spinner, null);
                } else {
                    ((JPanel) obj).add(spinner, null);
                }

            } else if ("progressbar".equals(bean.getType())) {
                final JProgressBar progressBar = new JProgressBar();
                progressBar.setName(bean.getName());
                progressBar.setEnabled(bean.isEnabled());
                progressBar.setDoubleBuffered(true);
                progressBar.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                if(!"".equals(bean.getI18NToolTip())){
                    progressBar.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                if(!bean.isVisible()){
                    progressBar.setVisible(bean.isVisible());
                }

                if(bean.getColor() != null){progressBar.setBackground(bean.getColor());}
                progressBar.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(progressBar);
                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

                if (obj == null) {
                    panel.add(progressBar, null);
                } else {
                    ((JPanel) obj).add(progressBar, null);
                }

            } else if ("panel".equals(bean.getType())) {

                final EBIExtendedPanel p = new EBIExtendedPanel();
                p.setDoubleBuffered(true);
                p.setName(bean.getName());
                p.setOpaque(false);
                p.setLayout(null);
                String title = "";
                if (!"".equals(bean.getTitle())) {
                    if(bean.getTitle().indexOf("EBI_LANG") != -1){
                        title=EBIPGFactory.getLANG(bean.getTitle());
                    }else{
                        title= bean.getTitle();
                    }

                    final TitledBorder tltB = BorderFactory.createTitledBorder( BorderFactory.createMatteBorder( 1, 1, 1,1, new Color(200, 200, 200)), title);
                    tltB.setTitleColor(new Color(100,100,100));
                    tltB.setTitleFont(new Font("Verdana", Font.BOLD, 10));
                    p.setBorder(tltB);
                }

                if(!"".equals(bean.getI18NToolTip())){
                    p.setToolTipText(EBIPGFactory.getLANG(bean.getI18NToolTip()));
                }

                p.setVisible(true);

                if(bean.getColor() != null){p.setBackground(bean.getColor());}
                p.setBounds(new Rectangle(bean.getPoint().x, bean.getPoint().y, bean.getDimension().width, bean.getDimension().height));

                final EBIGUINBean res = new EBIGUINBean();
                res.setResizeHight(bean.isHeightAutoResize());
                res.setResizeWidth(bean.isWidthAutoResize());
                res.setPercentHeight(bean.getDimension().height);
                res.setPercentWidth(bean.getDimension().width);
                res.setFitByResize(bean.isFitOnResize());
                res.setName(bean.getName());
                res.setComponent(p);
                resizeContainer.get(mainComponentName).add(res);
                componentGet.put(mainComponentName+"."+res.getName(),resizeContainer.get(mainComponentName).size());

                if (obj == null) {
                    panel.add(p,null);
                } else {
                    ((JPanel) obj).add(p);
                    res.setParentComponent((JComponent) obj);
                }

                if (bean.getSubWidgets().size() > 0) {
                    renderGUI(p, bean.getSubWidgets());
                }

            } else if ("codecontrol".equals(bean.getType())) {

                 if ("groovy".equals(bean.getName())) {

                    final EBIGUIScripts script = new EBIGUIScripts();
                    script.setType("groovy");
                    script.setPath(bean.getPath());
                    script.setName(bean.getName());
                    script.setClassName(bean.getClassName());
                    scriptContainer.get(mainComponentName).add(script);

                }else if ("java".equals(bean.getName())) {

                    final EBIGUIScripts script = new EBIGUIScripts();
                    script.setType("java");
                    script.setPath(bean.getPath());
                    script.setName(bean.getName());
                    script.setClassName(bean.getClassName());
                    scriptContainer.get(mainComponentName).add(script);
                }
            }
        }
        
        if (obj == null) {
            if (isVisualPanel){
                list.setComponent(panel);
            }else{
                dialog.setContentPane(panel);
                list.setComponent(dialog);
            }
            if(!focusTraversal.isEmpty()){
                panel.setFocusTraversalKeysEnabled(true);
                panel.setFocusTraversalPolicy(focusTraversal);
            }
            setResizeListener();
        }
    }


    public final void showGUI() {
        try{
            ebiMain.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            if(canShow){
                if(componentsTable.get(mainComponentName) != null){
                    final EBIGUIWidgetsBean list = componentsTable.get(mainComponentName);

                    if (list.getComponent() instanceof EBIVisualPanelTemplate) {

                        if(list.isCeckable()){
                            if(list.getTitle().indexOf("EBI_LANG") != -1){
                                ebiMain.container.addScrollableContainer(EBIPGFactory.getLANG(list.getTitle()), ((EBIVisualPanelTemplate) list.getComponent()), list.getIcon(), list.getKeyEvent());
                            }else{
                                ebiMain.container.addScrollableContainer(list.getTitle(), ((EBIVisualPanelTemplate) list.getComponent()), list.getIcon(), list.getKeyEvent());
                            }
                        }else{
                            if(list.getTitle().indexOf("EBI_LANG") != -1){
                                ebiMain.container.addScrollableContainer(EBIPGFactory.getLANG(list.getTitle()), ((EBIVisualPanelTemplate) list.getComponent()), list.getIcon(), list.getKeyEvent());
                            }else{
                                ebiMain.container.addScrollableContainer(list.getTitle(), ((EBIVisualPanelTemplate) list.getComponent()), list.getIcon(), list.getKeyEvent());
                            }
                        }

                    }else if (list.getComponent() instanceof EBIDialog) {
                        ((EBIDialog) list.getComponent()).setVisible(true);
                    }
                    initScript();

                    isInit = false;
                    fileToTabPath = "";
                }
            }
            ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }catch(Exception ex){
            ex.printStackTrace();
            ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }


    public final void initScripts(){
      final Iterator itr =  scriptContainer.keySet().iterator();
      while(itr.hasNext()){
         excScript((String)itr.next());
      }
    }

    private final void initScript() {
        excScript(mainComponentName);
    }

    public final void excScript(final String componentName){
      if(scriptContainer.get(componentName) != null){
        final Iterator iter = scriptContainer.get(componentName).iterator();
        while (iter.hasNext()) {

            final EBIGUIScripts script = (EBIGUIScripts) iter.next();

            if("groovy".equals(script.getType()) && !script.isExecuted()){

                    Thread thr = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try{

                                        final Binding binding = new Binding();
                                        binding.setVariable("system", ebiMain.system);
                                        binding.setVariable("core", ebiMain);

                                        if(resizeContainer.get(componentName) != null){
                                            for(int i=0; i< resizeContainer.get(componentName).size(); i++){
                                                final EBIGUINBean bx = (EBIGUINBean) resizeContainer.get(componentName).get(i);
                                                binding.setVariable(componentName+"_"+bx.getName(),bx.getComponent() );
                                            }
                                        }else{
                                            Iterator itx = resizeContainer.keySet().iterator();
                                            while(itx.hasNext()){
                                                String compName = (String)itx.next();
                                                for(int i=0; i< resizeContainer.get(compName).size(); i++){
                                                    final EBIGUINBean bx = (EBIGUINBean) resizeContainer.get(compName).get(i);
                                                    binding.setVariable(compName+"_"+bx.getName(),bx.getComponent() );
                                                }
                                            }
                                        }
                                        gse.run(script.getPath(),binding);

                                        final Script scr = gse.createScript(script.getPath(),binding);

                                        if(!"app".equals(componentName)){
                                            if(scr.getMetaClass().getMetaMethod("ebiEdit",null) != null ||
                                                    scr.getMetaClass().getMetaMethod("ebiDelete",null) != null ||
                                                    scr.getMetaClass().getMetaMethod("ebiNew",null) != null  ||
                                                    scr.getMetaClass().getMetaMethod("ebiSave",null) != null){

                                                ebiMain.system.setDataStore(componentName, scr);
                                            }
                                        }else{

                                            if(scr.getMetaClass().getMetaMethod("ebiEdit",null) != null ||
                                                    scr.getMetaClass().getMetaMethod("ebiDelete",null) != null ||
                                                    scr.getMetaClass().getMetaMethod("ebiNew",null) != null  ||
                                                    scr.getMetaClass().getMetaMethod("ebiSave",null) != null){
                                                final String cmpName = (String)scr.getMetaClass().invokeMethod(scr,"useComponent",null);
                                                if(!"".equals(cmpName)){
                                                    ebiMain.system.setDataStore(cmpName, scr);
                                                }
                                            }
                                        }

                                        script.setExecuted(true);
                                    }
                                    catch(ResourceException ex){ EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE); }
                                    catch(ScriptException ex){ EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE); }
                                    catch(Exception ex){ EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE); }
                                    catch(NoClassDefFoundError e){ EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);}
                                }
                            });
                        }
                    });

                thr.start();



            } else if ("java".equals(script.getType()) && !script.isExecuted()) {

                // Create a File object on the root of the directory containing the class file
                final File file = new File("ebiExtensions/" + script.getPath());

                try {
                    // Convert File to a URL
                    URL url = file.toURL();
                    URL[] urls = new URL[]{url};

                    // Create a new class loader with the directory
                    ClassLoader cl = new URLClassLoader(urls);

                    Class cls = cl.loadClass(script.getClassName());
                    cls.newInstance();
                    script.setExecuted(true);
                } catch (MalformedURLException e) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                } catch (ClassNotFoundException e) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                } catch (InstantiationException e) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                } catch (IllegalAccessException e) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                } catch (Exception e) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                }catch(NoClassDefFoundError e){
                    EBIExceptionDialog.getInstance(e.getMessage()).Show(EBIMessage.ERROR_MESSAGE);
                }
            }
        }
      }
    }


    /**
     * Add resizelistener to the Dialog/VisualPanel
     */

    private final void setResizeListener() {

        final Object obj = componentsTable.get(mainComponentName);
        final  ComponentAdapter adt;
			
	     adt = new ComponentAdapter() {
	
	            private int oldWidth = 0;
	            private int oldHeight = 0;
	            private int calX = 0;
	            private int calY = 0;
	
	            public final void componentShown(java.awt.event.ComponentEvent e){
	                componentResized(e);
	            }
	
	            public final void componentResized(java.awt.event.ComponentEvent e) {
	
	                 String compName = "";
	                 if (e.getSource() instanceof EBIVisualPanelTemplate) {
	                       compName = ((EBIVisualPanelTemplate) e.getSource()).getName();
	                 }else if (e.getSource() instanceof EBIDialog){
	                       compName = ((EBIDialog) e.getSource()).getName();
	                 }
	
	              EBIGUINBean res;
	
	              try{
	
	                if(e.getSource() instanceof EBIDialog ){
	                  if(oldWidth > 0){
	                    calX = ((EBIDialog) e.getSource()).getWidth() - oldWidth;
	                    oldWidth = ((EBIDialog) e.getSource()).getWidth();
	                  }else{
	                     calX = ((EBIDialog) e.getSource()).getWidth() - ((EBIDialog) e.getSource()).getOriginalDimension().width - 10;
	                     oldWidth = ((EBIDialog) e.getSource()).getWidth();
	                  }
	
	                  if(oldHeight > 0){
	                    calY = ((EBIDialog) e.getSource()).getHeight() - oldHeight;
	                    oldHeight = ((EBIDialog) e.getSource()).getHeight();
	                  }else{
	                     calY = ((EBIDialog) e.getSource()).getHeight() - ((EBIDialog) e.getSource()).getOriginalDimension().height ;
	                     oldHeight = ((EBIDialog) e.getSource()).getHeight();
	                  }
	                }
	
	                for(int i = 0; i<resizeContainer.get(compName).size(); i++ ) {
	                    res = (EBIGUINBean) resizeContainer.get(compName).get(i);
	                    if (res.getComponent() instanceof JComponent) {

                           if(res.getScrollComponent() instanceof JXTable){
                              final JXTable tb = (JXTable)res.getScrollComponent();
                              SwingUtilities.invokeLater(new Runnable() {
                                   @Override
                                   public void run() {
                                     tabProp.restoreTableProperties(tb);
                                   }
                               });

                           }

	                        if(res.isFitByResize()){
	
	                            if (e.getSource() instanceof EBIDialog) {
	
	                                if (res.getParentComponent() == null) {

	                                   res.getComponent().setLocation(res.getComponent().getX()+calX,res.getComponent().getY()+calY);
	                                }
	                            }
	
	                        }
	
	                        if (res.isResizeHight()) {
	
	                            if (e.getSource() instanceof EBIVisualPanelTemplate) {
	
	                                if (res.getParentComponent() != null) {
	                                    res.getComponent().setSize(new Dimension(res.getComponent().getWidth(), res.getParentComponent().getHeight() * res.getPercentHeight() / 100 - res.getComponent().getY() - 10));
	                                } else {
	                                    res.getComponent().setSize(new Dimension(res.getComponent().getWidth(), ((EBIVisualPanelTemplate)e.getSource()).getHeight() * res.getPercentHeight() / 100 - res.getComponent().getY()-42));
	                                }
	
	                            } else if (e.getSource() instanceof EBIDialog) {
	
	                                if (res.getParentComponent() != null) {
	                                    res.getComponent().setSize(new Dimension(res.getComponent().getWidth(), res.getParentComponent().getHeight() * res.getPercentHeight() / 100 - res.getComponent().getY() - 13));
	                                } else {
	                                    res.getComponent().setSize(new Dimension(res.getComponent().getWidth(), (((((EBIDialog) e.getSource()).getHeight() * res.getPercentHeight()) / 100) - res.getComponent().getY()) - 67));
	                                }
	                            }
	                        }
	
	                        if (res.isResizeWidth()) {
	
	                            if (e.getSource() instanceof EBIVisualPanelTemplate) {
	
	                                if (res.getParentComponent() != null) {
	                                    res.getComponent().setSize(new Dimension(res.getParentComponent().getWidth() * res.getPercentWidth() / 100 - res.getComponent().getX() - 10, res.getComponent().getHeight()));
	                                } else {
	                                    res.getComponent().setSize(new Dimension(((EBIVisualPanelTemplate)e.getSource()).getWidth() * res.getPercentWidth() / 100 - res.getComponent().getX() - 10, res.getComponent().getHeight()));
	                                }
	                            } else if (e.getSource() instanceof EBIDialog) {
	
	                                if (res.getParentComponent() != null) {
	                                    res.getComponent().setSize(new Dimension(res.getParentComponent().getWidth() * res.getPercentWidth() / 100 - res.getComponent().getX() - 20, res.getComponent().getHeight()));
	                                } else {
	                                    res.getComponent().setSize(new Dimension(((EBIDialog) e.getSource()).getWidth() * res.getPercentWidth() / 100 - res.getComponent().getX() - 20, res.getComponent().getHeight()));
	                                }
	                            }
	                        }

	                    }
	                }
	                }catch(NullPointerException ex){ex.printStackTrace();}
	            }
	        };
        if (((EBIGUIWidgetsBean)obj).getComponent() instanceof EBIVisualPanelTemplate) {
            ((EBIVisualPanelTemplate) ((EBIGUIWidgetsBean)obj).getComponent()).addComponentListener(adt);
        } else if (((EBIGUIWidgetsBean)obj).getComponent() instanceof EBIDialog) {
            ((EBIDialog) ((EBIGUIWidgetsBean)obj).getComponent()).addComponentListener(adt);
        }
        
    }

    public final EBIVisualPanelTemplate getVisualPanel(String Name){
        final EBIVisualPanelTemplate retPanel;

            if(componentsTable.get(Name) != null && componentsTable.get(Name).getComponent() instanceof EBIDialog){
                retPanel = (EBIVisualPanelTemplate) ((EBIDialog) componentsTable.get(Name).getComponent()).getContentPane();
            }else{
                retPanel =  componentsTable.get(Name) == null ? null : ((EBIVisualPanelTemplate) componentsTable.get(Name).getComponent());
            }

       return retPanel;
    }

    public final EBIDialog getEBIDialog(String Name){
        return ((EBIDialog) componentsTable.get(Name).getComponent());
    }


    public final JRadioButton getRadioButton(String name,String packages) {
        JRadioButton actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JRadioButton)res.getComponent();
            }
        }
        return actualComponent;
    }

    public final JCheckBox getCheckBox(String name,String packages) {
        JCheckBox actualComponent=null;
        if(resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null ) {
                actualComponent = (JCheckBox) res.getComponent();
            }
        }
        return actualComponent;
    }

    public final JComboBox getComboBox(final String name,final String packages) {
       JComboBox actualComponent=null;
       if( resizeContainer.get(packages) != null){
           EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
           if (res != null ) {
                actualComponent = (JComboBox)res.getComponent();
           }
       }
        return actualComponent;
    }

    public final JButton getButton(String name,String packages) {
    	JButton actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JButton) res.getComponent();
            }
        }
      return actualComponent;
    }

    public final JFormattedTextField getFormattedTextfield(String name,String packages) {
        JFormattedTextField actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JFormattedTextField) res.getComponent();
            }
        }
       return actualComponent;
    }

    public final JTextField getTextfield(String name,String packages) {
        JTextField actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JTextField) res.getComponent();
            }
        }
       return actualComponent;
    }

    public final JLabel getLabel(String name,String packages) {
        JLabel actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JLabel) res.getComponent();
            }
        }
       return actualComponent;
    }

    public final JTextArea getTextarea(String name,String packages) {
        JTextArea actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JTextArea)res.getScrollComponent();
            }
        }
        return actualComponent;
    }

    public final JEditorPane getEditor(String name,String packages) {
        JEditorPane actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JEditorPane)res.getScrollComponent();
            }
        }
        return actualComponent;
    }

    public final JXTable getTable(String name,String packages) {

        if(!actualCmpStr.equals(packages+"."+name)) {
            if (resizeContainer.get(packages) != null) {
                EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages + "." + name) - 1);
                if (res != null) {
                    actualTable = (JXTable) res.getScrollComponent();
                }
            }
        }
       return actualTable;
    }

    public final JXTreeTable getTreeTable(String name,String packages) {
        JXTreeTable actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JXTreeTable)res.getScrollComponent();
            }
        }
       return actualComponent;
    }

    public final JXDatePicker getTimepicker(String name,String packages) {
        JXDatePicker actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JXDatePicker)res.getComponent();
            }
        }
       return actualComponent;
    }

    public final JList getList(String name,String packages) {
        JList actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JList) res.getScrollComponent();
            }
        }
       return actualComponent;
    }

    public final JPanel getPanel(String name,String packages) {
        JPanel actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JPanel)res.getComponent();
            }
        }
      return actualComponent;
    }

    public final JProgressBar getProgressBar(String name,String packages) {
        JProgressBar actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
                actualComponent = (JProgressBar)res.getComponent();
            }
        }
       return actualComponent;
    }

    public final JSpinner getSpinner(String name,String packages) {
        JSpinner actualComponent=null;
        if( resizeContainer.get(packages) != null){
            EBIGUINBean res = (EBIGUINBean) resizeContainer.get(packages).get(componentGet.get(packages+"."+name)-1);
            if (res != null) {
               actualComponent = (JSpinner)res.getComponent();
            }
        }
       return actualComponent;
    }

    public final boolean existPackage(String name){
         return resizeContainer.containsKey(name);
    }

    public final boolean isToolBarEmpty(){
        return toToolbar.isEmpty() ? true : false;
    }

    public final String getFileToPath(){
        return this.fileToTabPath;
    }

    public  int getProjectModuleCount(){
         return projectCount;
    }

    public  int getProjectModuleEnabled(){
        return projectCountEnabled;
    }

    public final Hashtable<String,List<Object>> getScriptContainer(){ return scriptContainer; }
}