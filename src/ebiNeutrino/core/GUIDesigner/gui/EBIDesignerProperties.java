package ebiNeutrino.core.GUIDesigner.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import ebiNeutrino.core.GUIDesigner.DesignerPanel;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;


class StoreButton extends JPanel{}

public class EBIDesignerProperties extends JDialog {

    private final List<String> lables = new ArrayList<String>();
    private final List<JComponent> component = new ArrayList<JComponent>();
    private DesignerPanel designerPanel = null;
    private final JPanel panel = new JPanel();
    private final JTextField name = new JTextField();
    private final JTextField type = new JTextField();
    private final JTextField title = new JTextField();
    private final JTextField tabIndex = new JTextField();
    private final JTextField size = new JTextField();
    private final JTextField location = new JTextField();
    private final JTextField toolTip = new JTextField();
    private final JTextField icon = new JTextField();
    private final JCheckBox visualProperties = new JCheckBox();
    private final JCheckBox fitresize = new JCheckBox();
    private final JCheckBox resizeWidth = new JCheckBox();
    private final JCheckBox resizeHeight = new JCheckBox();
    private final JCheckBox assignable = new JCheckBox();
    private final JCheckBox visible = new JCheckBox();
    private final JTextField background = new JTextField();
    private final JButton save = new JButton("Apply");
    private final JButton cancel = new JButton("Cancel");
    private final StoreButton storeBnt = new StoreButton();

    public EBIDesignerProperties(DesignerPanel panel){
        super(panel.ebiMain);
        designerPanel = panel;
        initDialog();
    }

    private void setSelectedComponent(){

        type.setText(designerPanel.getSelectedBean().getType());
        name.setText(designerPanel.getSelectedBean().getName());
        title.setText(designerPanel.getSelectedBean().getTitle());
        tabIndex.setText(String.valueOf(designerPanel.getSelectedBean().getTabIndex()));

        Dimension dim = designerPanel.getSelectedBean().getDimension();
        size.setText(String.valueOf((int)dim.getWidth()+"x"+(int)dim.getHeight()));
        location.setText(String.valueOf(designerPanel.getSelectedBean().getX()+","+designerPanel.getSelectedBean().getY()));
        background.setText(designerPanel.getSelectedBean().getColorName());
        toolTip.setText(designerPanel.getSelectedBean().getI18NToolTip());
        icon.setText(designerPanel.getSelectedBean().getIconPath());
        visualProperties.setSelected(designerPanel.getSelectedBean().isVisualProperties());
        assignable.setSelected(designerPanel.getSelectedBean().isAssignable());
        visible.setSelected(designerPanel.getSelectedBean().isVisible());

        if(designerPanel.getSelectedBean() != null &&
                         !designerPanel.getSelectedBean().getName().equals(designerPanel.mainComponentName)){

            fitresize.setEnabled(true);
            resizeWidth.setEnabled(true);
            resizeHeight.setEnabled(true);
            assignable.setEnabled(false);
            visualProperties.setEnabled(false); 
            fitresize.setSelected(designerPanel.getSelectedBean().isFitOnResize());
            resizeWidth.setSelected(designerPanel.getSelectedBean().isWidthAutoResize());
            resizeHeight.setSelected(designerPanel.getSelectedBean().isHeightAutoResize());
            
        }else{
           visualProperties.setEnabled(true);
           assignable.setEnabled(true);
           fitresize.setEnabled(false);
           resizeWidth.setEnabled(false);
           resizeHeight.setEnabled(false);
        }
    }

 
    public void Show(){
        try{
            setSelectedComponent();
            setVisible(true);
        }catch(Exception e){e.printStackTrace();}
    }


    private void initDialog(){
       setResizable(false);
       setSize(290,430);
       setLocation(designerPanel.ebiMain.getWidth()-250,panel.getY()+180);
       setName("componentProperties");
       setTitle("Component Properties");
       setAlwaysOnTop(true);
       panel.setLayout(null);
       setContentPane(panel);
       lables.add(0,"Type");
       type.setEnabled(false); 
       component.add(0,type);
       lables.add(1,"Name");
       component.add(1,name);
       lables.add(2,"Title");
       component.add(2,title);
       lables.add(3,"Tab Index");
       component.add(3,tabIndex);
       lables.add(4,"Size");
       component.add(4,size);
       lables.add(5,"Location");
       component.add(5,location);
       lables.add(6,"Background");
       component.add(6,background);
       lables.add(7,"i18NToolTip");
       component.add(7,toolTip);
       lables.add(8,"Icon");
       component.add(8,icon);
       lables.add(9,"Visual Properties");
       component.add(9,visualProperties);

       lables.add(10,"Fit On Resize");
       component.add(10,fitresize);
       lables.add(11,"Resize Width");
       component.add(11,resizeWidth);
       lables.add(12,"Resize Height");
       component.add(12,resizeHeight);
        lables.add(13,"Assignable");
       component.add(13,assignable);
       lables.add(14,"Visible");
       component.add(14,visible);
           
       storeBnt.setLayout(null);
       save.setBounds(5,0,75,28);
       storeBnt.add(save);
       cancel.setBounds(84,0,75,28);
       storeBnt.add(cancel);
       component.add(15,storeBnt);
       Iterator iter = lables.iterator();
       int x=0;
       while(iter.hasNext()){
           String label = (String)iter.next();
           createLabel(x+=20, label);
       }

       Iterator iter1 = component.iterator();
       int y=0;
       while(iter1.hasNext()){
           JComponent comp = (JComponent)iter1.next();
           if(comp instanceof StoreButton){
            comp.setBounds(110,y+=50,170,40);   
           }else if(comp instanceof JCheckBox){
            comp.setBounds(110,y+=20,40,20);
           }else{
            comp.setBounds(110,y+=20,155,20);
           }
           panel.add(comp);
       }


       save.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
             saveProperties();
             setVisible(false);  
           }
       });

       cancel.addActionListener(new ActionListener(){
           public void actionPerformed(ActionEvent e){
             setVisible(false);  
           }
       });

    }

    private void createLabel(int y, String text){
        JLabel label = new JLabel(text);
        label.setBounds(5,y, 100, 20);
        panel.add(label);
    }

    private void saveProperties(){
        designerPanel.getSelectedBean().setName(name.getText());
        designerPanel.getSelectedBean().setTitle(title.getText());

        try{

            designerPanel.getSelectedBean().setTabIndex(Integer.parseInt(tabIndex.getText()));

            String[] sz = size.getText().split("x");

            int w = Integer.parseInt(sz[0]);
            int h = Integer.parseInt(sz[1]);

            if(designerPanel.getSelectedBean() != null &&
                         !designerPanel.getSelectedBean().getName().equals(designerPanel.mainComponentName)){
                if(w > 100 && resizeWidth.isSelected()){
                    w = 100;
                }
                if(h > 100 && resizeHeight.isSelected()){
                    h = 100;
                }
            }

            designerPanel.getSelectedBean().setDimension(new Dimension(w,h));

            String[] xy = location.getText().split(",");
            designerPanel.getSelectedBean().setPoint(new Point(Integer.parseInt(xy[0]),Integer.parseInt(xy[1])));

            if(background.getText().length() <=7 && !"".equals(background.getText())){
                if(background.getText().charAt(0) == '#'){
                    designerPanel.getSelectedBean().setColor(background.getText());
                }
            }

            designerPanel.getSelectedBean().setI18NToolTip(toolTip.getText());
            designerPanel.getSelectedBean().setIcon(icon.getText());
            designerPanel.getSelectedBean().setVisualProperties(visualProperties.isSelected());
            designerPanel.getSelectedBean().setWidthAutoResize(resizeWidth.isSelected());
            designerPanel.getSelectedBean().setHeightAutoResize(resizeHeight.isSelected());
            designerPanel.getSelectedBean().setFitOnResize(fitresize.isSelected());
            designerPanel.getSelectedBean().setAssignable(assignable.isSelected());
            designerPanel.getSelectedBean().setVisible(visible.isSelected());
            designerPanel.showCode();
            designerPanel.loadXMLGUIToPanel(designerPanel.textArea.getText());

            if(designerPanel.getSelectedBean().getName().equals(designerPanel.mainComponentName)){
               designerPanel.panel.selectMainComponent(designerPanel.panel); 
            }else{
               designerPanel.selectComponent(name.getText());
               designerPanel.updateComponentSelection(name.getText());
            }
            designerPanel.setDrawFocusRectangle(true);
            designerPanel.getTabbedPane().repaint();
        }catch(NumberFormatException ex){}
    }

    public DesignerPanel getDesignerPanel(){
        return designerPanel;
    }
}