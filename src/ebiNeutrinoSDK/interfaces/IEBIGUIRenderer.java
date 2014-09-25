package ebiNeutrinoSDK.interfaces;

import java.util.Hashtable;

import javax.swing.*;

import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;

import ebiNeutrino.core.GUIRenderer.EBIButton;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;


public interface IEBIGUIRenderer {

    public void loadGUI(String path);

    public void useGUI(String rootName);

    public String loadGUIPlus(String path);

    public void loadProject(String path);

    public void showGUI();

    public void initScripts();

    public void showToolBar(String name, boolean toVisualPanel);

    public EBIButton getButton(String name,String packages);

    public JTextField getTextfield(String name,String packages);

    public JFormattedTextField getFormattedTextfield(String name,String packages);

    public JLabel getLabel(String name,String packages);

    public JTextArea getTextarea(String name,String packages);

    public JXTable getTable(String name,String packages);

    public JXTreeTable getTreeTable(String name,String packages);

    public JEditorPane getEditor(String name,String packages);

    public JXDatePicker getTimepicker(String name,String packages);

    public JList getList(String name,String packages);

    public JPanel getPanel(String name,String packages);

    public JToolBar getToolBar(String name);

    public JComponent getToolBarComponent(String name, String packages);

    public JButton getToolBarButton(String name, String packages);

    public JComboBox getComboBox(String name,String packages);

    public JCheckBox getCheckBox(String name,String packages);

    public JRadioButton getRadioButton(String name,String packages);

    public EBIVisualPanelTemplate getVisualPanel(String name);

    public JProgressBar getProgressBar(String name,String packages);

    public EBIDialog getEBIDialog(String name);

    public boolean existPackage(String name);

    public JSpinner getSpinner(String name,String packages);

    public int getProjectModuleCount();

    public int getProjectModuleEnabled();

    public boolean isToolBarEmpty();

}
