package ebiNeutrino.core.GUIDesigner.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JToggleButton;

import ebiNeutrino.core.GUIDesigner.DesignerPanel;
import ebiNeutrino.core.GUIDesigner.gui.menu.DesignerMenu;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;


public class EBIDesignerDialogTools extends JDialog {

    private DesignerPanel designer = null;
    private JToggleButton pointButton = new JToggleButton(new ImageIcon("system/pointer.gif"));
    private JToggleButton labelButton = new JToggleButton(new ImageIcon("system/label_obj.gif"));
    private JToggleButton buttonButton = new JToggleButton(new ImageIcon("system/button_obj.gif"));
    private JToggleButton textButton = new JToggleButton(new ImageIcon("system/textfield_obj.gif"));
    private JToggleButton listButton = new JToggleButton(new ImageIcon("system/listbox_obj.gif"));
    private JToggleButton tableButton = new JToggleButton(new ImageIcon("system/table_obj.gif"));
    private JToggleButton comboButton = new JToggleButton(new ImageIcon("system/ccombo_obj.gif"));
    private JToggleButton panelButton = new JToggleButton(new ImageIcon("system/panel_obj.gif"));
    private JToggleButton checkButton = new JToggleButton(new ImageIcon("system/checkbox_obj.gif"));
    private JToggleButton radioButton = new JToggleButton(new ImageIcon("system/radiobutton_obj.gif"));
    private JToggleButton textAreaButton = new JToggleButton(new ImageIcon("system/slider_obj.gif"));
    private JToggleButton datePickerButton = new JToggleButton(new ImageIcon("system/datepicker_obj.gif"));
    private JToggleButton formattedTextButton = new JToggleButton(new ImageIcon("system/textfieldformatted_obj.gif"));
    private JToggleButton progressBarButton = new JToggleButton(new ImageIcon("system/progressbar_obj.gif"));


    private ButtonGroup group = new ButtonGroup();
    public String componentName = "";



    public EBIDesignerDialogTools(DesignerPanel panel, EBIPropertiesRW prop){
       setTitle("EBI Toolbox");
       designer = panel;
       setName("EBIDesignerDialogTools");
       setBounds(panel.ebiMain.getWidth()-250,panel.getY()+60,250,90);
       setResizable(false);
       setAlwaysOnTop(true);
       initialize();
       setJMenuBar(new DesignerMenu(panel));

    }

    public void initialize(){
        getContentPane().setLayout(new GridLayout(2, 2));

        pointButton.setToolTipText("Reset selection");
        pointButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
             componentName = "pointer";
             designer.insertNewComponent = false;

        }});

        labelButton.setToolTipText("Label");
        labelButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "label";
            designer.insertNewComponent = true;
        }});

        buttonButton.setToolTipText("Button");
        buttonButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "button";
            designer.insertNewComponent = true;
        }});

        textButton.setToolTipText("Textfield");
        textButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "textfield";
            designer.insertNewComponent = true;
        }});

        listButton.setToolTipText("List");
        listButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "list";
            designer.insertNewComponent = true;
        }});

        tableButton.setToolTipText("Table");
        tableButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "table";
            designer.insertNewComponent = true;
        }});

        comboButton.setToolTipText("Combobox");
        comboButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "combobox";
            designer.insertNewComponent = true;
        }});

        panelButton.setToolTipText("Panel");
        panelButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "panel";
            designer.insertNewComponent = true;
        }});

        checkButton.setToolTipText("Checkbox");
        checkButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "checkbox";
            designer.insertNewComponent = true;
        }});

        radioButton.setToolTipText("Radiobutton");
        radioButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "radiobutton";
            designer.insertNewComponent = true;
        }});

        textAreaButton.setToolTipText("Textarea");
        textAreaButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "textarea";
            designer.insertNewComponent = true;
        }});

        datePickerButton.setToolTipText("Timepicker");
        datePickerButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "timepicker";
            designer.insertNewComponent = true;
        }});

        formattedTextButton.setToolTipText("Formatted Textfield");
        formattedTextButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "formattedtextfield";
            designer.insertNewComponent = true;
        }});

        progressBarButton.setToolTipText("ProgressBar");
        progressBarButton.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
            componentName = "progressbar";
            designer.insertNewComponent = true;
        }});

        getContentPane().add(pointButton);
        group.add(pointButton);
        getContentPane().add(labelButton);
        group.add(labelButton);
        getContentPane().add(buttonButton);
        group.add(buttonButton);
        getContentPane().add(textButton);
        group.add(textButton);
        getContentPane().add(listButton);
        group.add(listButton);
        getContentPane().add(tableButton);
        group.add(tableButton);
        getContentPane().add(comboButton);
        group.add(comboButton);
        getContentPane().add(panelButton);
        group.add(panelButton);
        getContentPane().add(checkButton);
        group.add(checkButton);
        getContentPane().add(radioButton);
        group.add(radioButton);
        getContentPane().add(textAreaButton);
        group.add(textAreaButton);
        getContentPane().add(datePickerButton);
        group.add(datePickerButton);
        getContentPane().add(formattedTextButton);
        group.add(formattedTextButton);
        getContentPane().add(progressBarButton);
        group.add(progressBarButton);

    }

    public void resetButtons(){
        designer.insertNewComponent = false;
        pointButton.setSelected(true);
    }

}
