package ebiNeutrino.core.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.settings.EBIListCellRenderer;
import ebiNeutrino.core.settings.EBIListItem;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIContainer;
import org.jdesktop.swingx.JXPanel;

/**
 *  EBI Neutrino component container
 *  This class is managing the jTabbedPane component
 */

public class EBIExtensionContainer implements IEBIContainer {

    public EBIMain ebiMain = null;
    private JXPanel mainPane = null;
    public static final int NON_MNEMO = -1;
    private JScrollPane jscrollPane = null;
    public final List<Object> registeredTabs = new ArrayList<Object>();
    public JList jListnames = null;
    public DefaultListModel myListmodel=null;
    private JSplitPane splitVerticalPane = null;


    public EBIExtensionContainer(EBIMain main) {
        ebiMain = main;
        myListmodel = new DefaultListModel();
    }


    public void initContainer() {

        mainPane = new JXPanel();
        mainPane.setName("MainPanel");
        mainPane.setFocusable(false);
        mainPane.setFocusCycleRoot(false);
        mainPane.setFocusTraversalKeysEnabled(false);
        mainPane.setBorder(BorderFactory.createEmptyBorder());
        mainPane.setLayout(new BorderLayout());
        mainPane.setDoubleBuffered(true);

        jListnames = new JList(myListmodel);
        jListnames.setCellRenderer(new EBIListCellRenderer());

        jListnames.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                int index = jListnames.locationToIndex(e.getPoint());
                loadSelectedListIndex(index);
            }
        });

        jListnames.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jListnames.setSelectedIndex(0);

        JScrollPane treeViewScroll = new JScrollPane(jListnames);
        treeViewScroll.setBorder(null);
        JPanel lpan = new JPanel();
        lpan.setLayout(new BorderLayout());
        lpan.add(treeViewScroll,BorderLayout.CENTER);

        splitVerticalPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lpan , mainPane);
        splitVerticalPane.setDividerLocation(160);

        ebiMain.getContentPane().add(splitVerticalPane, BorderLayout.CENTER);

    }

    public void initContainer(JPanel panel) {

        mainPane = new JXPanel();
        mainPane.setName("MainPanel");
        mainPane.setFocusable(false);
        mainPane.setFocusCycleRoot(false);
        mainPane.setFocusTraversalKeysEnabled(false);
        mainPane.setBorder( BorderFactory.createEmptyBorder() );
        mainPane.setLayout(new BorderLayout());
        mainPane.setDoubleBuffered(true);
        panel.add(mainPane, BorderLayout.CENTER);
    }

    /**
     * Add a component to container
     * @param  title 		: Container title
     * @param  component : Component instance
     * @param  icon		: Container icon
     * @param  mnemo_key	    : Mnemonic key
     * @return index
     */
    public int addContainer(String title, JComponent component, ImageIcon icon, int mnemo_key) {

        mainPane.removeAll();
        mainPane.add(component, BorderLayout.CENTER);

        this.registeredTabs.add(0, new String[]{component.getName(), title});
        mainPane.revalidate();
        splitVerticalPane.repaint();
        return 0;
    }

    /**
     * Add scrollable component to container
     * @param  title 	   : Container title
     * @param  component   : Component instance
     * @param  icon		   : Container Icon
     * @param  mnemo_key   : Mnemonic key
     * @return index
     */

    public int addScrollableContainer(String title, final JComponent component, ImageIcon icon, int mnemo_key){
        mainPane.removeAll();
        mainPane.revalidate();
        mainPane.repaint();
        splitVerticalPane.repaint();
        component.setPreferredSize(new Dimension(component.getWidth(), component.getHeight()));
        jscrollPane = new JScrollPane();
        jscrollPane.setViewportView(component);
        jscrollPane.setDoubleBuffered(true);
        jscrollPane.getVerticalScrollBar().setUnitIncrement(150);
        jscrollPane.setBorder(null);
        mainPane.add(jscrollPane,BorderLayout.CENTER);

        if(ebiMain.getSize().width < 1100 && ebiMain.getSize().height < 800 ){
            if(component.getComponentListeners().length > 1 && component.getComponentListeners()[1] != null){
                component.getComponentListeners()[1].componentResized(new ComponentEvent(component, ComponentEvent.COMPONENT_RESIZED));
            }
        }

        this.registeredTabs.add(0,new String[]{component.getName(),title});


        return 0;
    }

    public void loadSelectedListIndex(int index){

        EBIListItem itm =  (EBIListItem)myListmodel.get(index);

        if(itm.isApp()) {
            ebiMain.mod_management.loadScript(itm.getModname(), itm.getPath());
        }else{
            ebiMain.mod_management.loadCRM(itm.getModname(), itm.getPath());
        }

        jListnames.setSelectedIndex(index);

    }

    public void setSelectedModIndex(int index){
        jListnames.setSelectedIndex(index);
    }

    public int getSelectedListIndex(){
        return jListnames.getSelectedIndex();
    }

    public void addListItem(EBIListItem itm){
        myListmodel.addElement(itm);
    }

    public void resetListItem(){
        myListmodel.removeAllElements();
    }


    public void removeAllFromContainer(){
        try{
            this.mainPane.removeAll();
            this.registeredTabs.clear();
        }catch(Exception ex){EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);}
    }

    public List<Object> getRegisteredTab(){
        return this.registeredTabs;
    }

    public JXPanel getMainPaneInstance(){
        return mainPane;
    }


    public void hideSplit(boolean hide){
        if(!hide) {
            splitVerticalPane.setDividerLocation(0);
        }else{
            splitVerticalPane.setDividerLocation(150);
        }
    }

}