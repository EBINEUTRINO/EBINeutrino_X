package ebiNeutrino.core.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.GUIRenderer.EBIMutableTreeNode;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.CloseableTabbedPaneListener;
import ebiNeutrinoSDK.interfaces.IEBIContainer;
import ebiNeutrinoSDK.utils.EBIConstant;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

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
    private JXTree treeView = null;
    private JSplitPane splitVerticalPane = null;
    private JTextField field = null;
    private Timer tipTimer = null;

    public EBIExtensionContainer(EBIMain main) {
        ebiMain = main;
        tipTimer = new Timer(1200,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    setSelectedSearchedExtension(field.getText());
                    ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    treeView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                }catch(Exception ex){
                    ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    treeView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }finally {
                    tipTimer.stop();
                }

            }
        });
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

        treeView = new JXTree(EBITreeFactory.getEBITreeFactory().getEBIROOTreeNode());
        treeView.setFont(new Font("Verdana", Font.PLAIN, 10));
        treeView.setRolloverEnabled(true);
        treeView.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_CELL, Color.gray, Color.RED));

        treeView.setCellRenderer(new DefaultTreeCellRenderer(){
            public Component getTreeCellRendererComponent(final JTree tree,Object value,
                                                          boolean sel,boolean expanded,boolean leaf,int row,boolean hasFocus){


                JLabel label = (JLabel)super.getTreeCellRendererComponent(tree,value,sel,expanded,leaf,row,hasFocus);

                if(((DefaultMutableTreeNode)value).isRoot()){ label.setIcon(EBIConstant.ICON_ROOT_APP); }else
                { label.setIcon(EBIConstant.ICON_APP);}

                return label;
            }
        });

        treeView.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                EBIMutableTreeNode node = (EBIMutableTreeNode) ebiMain.container.getTreeViewInstance().getLastSelectedPathComponent();
                if(node == null){
                    return;
                }
                ebiMain.mng.loadCRM(node.getModuleName(),node.getXmlPath());

            }
        });

        treeView.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = (int) e.getPoint().getX();
                int y = (int) e.getPoint().getY();

                TreePath path = treeView.getPathForLocation(x, y);
                if(ebiMain.getCursor().getType() != Cursor.WAIT_CURSOR){
                    if (path == null) {
                        treeView.setCursor(Cursor.getDefaultCursor());
                    } else {
                        treeView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                }
            }
        });


        JScrollPane treeViewScroll = new JScrollPane(treeView);
        treeViewScroll.setBorder(null);
        JPanel lpan = new JPanel();
        lpan.setLayout(new BorderLayout());
        lpan.setBackground(EBIPGFactory.systemColor);
        field = new JTextField();
        field.setBorder(BorderFactory.createMatteBorder(7,15,7,0,Color.white));
        field.setText("Search...");
        field.setSize(100,40);
        lpan.add(field, BorderLayout.NORTH);
        lpan.add(treeViewScroll,BorderLayout.CENTER);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if("Search...".equals(field.getText())){
                    field.setText("");
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if("".equals(field.getText())){
                    field.setText("Search...");
                }
            }
        });

        field.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
               tipTimer.stop();
               ebiMain.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
               treeView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ebiMain.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                treeView.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                tipTimer.start();

            }
        });



        splitVerticalPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lpan , mainPane);
        splitVerticalPane.setUI(new BasicSplitPaneUI() {
            public BasicSplitPaneDivider createDefaultDivider() {
                return new BasicSplitPaneDivider(this) {
                    public void setBorder(Border b) {
                    }
                };
            }
        });

        splitVerticalPane.setBorder(null);
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
     * @param  String title 		: Container title 
     * @param  JComponent component : Component instance
     * @param  ImageIcon icon		: Container icon
     * @param  int mnemo_key	    : Mnemonic key 
     * @return index
     */
    public int addContainer(String title, JComponent component, ImageIcon icon, int mnemo_key) {

        mainPane.removeAll();
        mainPane.add(component,BorderLayout.CENTER);

        this.registeredTabs.add(0,new String[]{component.getName(),title});
        mainPane.revalidate();
        splitVerticalPane.repaint();
        return 0;
    }

    /**
     * Add scrollable component to container
     * @param  String title 		: Container title
     * @param  JComponent component : Component instance
     * @param  ImageIcon icon		: Container Icon 
     * @param  int mnemo_key	    : Mnemonic key 
     * @return index
     */
    public int addScrollableContainer(String title, final JComponent component, ImageIcon icon, int mnemo_key) {
        mainPane.removeAll();
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
        mainPane.revalidate();
        splitVerticalPane.repaint();
        return 0;
    }

    /**
     * Add scrollable and closable component to container
     * @param  String title 					: Container title
     * @param  JComponent component 			: Component instance
     * @param  ImageIcon icon					: Container Icon 
     * @param  int mnemo_key	    			: Mnemonic key 
     * @param  CloseableTabbedPaneListener 		
     * @return index
     */
    public int addScrollableClosableContainer(String title, JComponent component, ImageIcon icon, int mnemo_key, CloseableTabbedPaneListener l) {
        mainPane.removeAll();
        component.setPreferredSize(new Dimension(component.getWidth(), component.getHeight()));
        jscrollPane = new JScrollPane();
        jscrollPane.getVerticalScrollBar().setUnitIncrement(150);
        jscrollPane.setViewportView(component);
        jscrollPane.setDoubleBuffered(true);
        jscrollPane.setPreferredSize(new Dimension(component.getWidth(), component.getHeight()));
        jscrollPane.setBorder(null);
        mainPane.add(jscrollPane,BorderLayout.CENTER);

        this.registeredTabs.add(0,new String[]{component.getName(),title});
        mainPane.revalidate();
        splitVerticalPane.repaint();
        return 0;
    }

    public void removeAllFromContainer() {
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

    public JTree getTreeViewInstance(){
        return  treeView;
    }

    public void hideSplit(boolean hide){
        if(!hide) {
            splitVerticalPane.setDividerLocation(0);
        }else{
            splitVerticalPane.setDividerLocation(150);
        }
    }

    public void setSelectedExtension(String extension){

        EBIMutableTreeNode node = EBITreeFactory.getEBITreeFactory().getTreeNodeIndex(extension);
        if(node != null){
            TreePath path = new TreePath( ((DefaultTreeModel)treeView.getModel()).
                            getPathToRoot(node));
            if(path != null){

                treeView.setExpandsSelectedPaths(true);
                treeView.setSelectionPath(path);

            }
        }
    }

    public void setSelectedSearchedExtension(String extension){
        try{
            EBIMutableTreeNode node = EBITreeFactory.getEBITreeFactory().getSearchTreeNodeIndex(extension);
            if(node != null){
                TreePath path = new TreePath( ((DefaultTreeModel)treeView.getModel()).
                        getPathToRoot(node));
                if(path != null){
                    treeView.setExpandsSelectedPaths(true);
                    treeView.setSelectionPath(path);
                }
            }
        }catch (Exception ex) {}
    }

}