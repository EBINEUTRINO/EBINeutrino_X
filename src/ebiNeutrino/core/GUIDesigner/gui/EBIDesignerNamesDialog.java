package ebiNeutrino.core.GUIDesigner.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

public class EBIDesignerNamesDialog extends JDialog {

       public String name = "";
       private JTextField fieldName = new JTextField();
       private JLabel descLabel = new JLabel();
       private JLabel nameLabel = new JLabel();
       private JButton save = new JButton("Ok");
       private JButton cancel = new JButton("Cancel");
       private JButton browse = new JButton("...");
       private HashMap<String,JComponent> tab = null;
       private String compType = "";
       private EBIMain main = null;
    
       public EBIDesignerNamesDialog(EBIMain main,String cType, HashMap<String,JComponent> tab){
          super(main);
          this.tab = tab;
          this.compType = cType;
          this.main = main;
          setTitle("EBI Neutrino R1 Designer Component Name");
          setResizable(false);
          setAlwaysOnTop(true);
          setModal(true);
          setName("EBIDesignerNamesDialog");
          Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	      Dimension frameSize = getSize();
	      setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);

          if("codecontrol".equals(compType)){
           setSize(350,150);
          }else{
           setSize(300,150);
          }

          fieldName.addKeyListener(new KeyListener(){

               public void keyTyped(KeyEvent e) {}

               public void keyPressed(KeyEvent e){}

               public void keyReleased(KeyEvent e) {
                    if(e.getKeyCode() == KeyEvent.VK_ENTER){
                        if(validateInput()){
                            name = fieldName.getText();
                            setVisible(false);
                        }
                   }
               }

           });

         initUI();
       }

    private void initUI() {

        descLabel.setBounds(10,10,250,20);
        descLabel.setText("Insert a component name");
        descLabel.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        descLabel.setBackground(new Color(255,255,255));
        descLabel.setIcon(new ImageIcon("system/passwordfield_obj.gif"));

        if("codecontrol".equals(this.compType)){
           nameLabel.setText("File Name:");
        }else{
            nameLabel.setText("Name:");
        }
        nameLabel.setBounds(10,50,80,20);
        fieldName.setBounds(85,50,200,20);
        browse.setBounds(290,50,40,20);
        browse.addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent e){
                  File file = main._ebifunction.getOpenDialog(JFileChooser.FILES_ONLY);
                  if (file != null) {
                    fieldName.setText(file.getName());
                  }
              }
        });

        save.setBounds(120,90,80,25);
        save.addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent e){
                  if(validateInput()){
                    name = fieldName.getText();
                    setVisible(false);
                  }
              }
        });

        getContentPane().add(save,null);
        getContentPane().add(cancel,null);

        cancel.setBounds(205,90,80,25);
        cancel.addActionListener(new ActionListener(){
              public void actionPerformed(ActionEvent e){
                  name = "";
                  setVisible(false);
              }
        });

        getContentPane().setLayout(null);
        getContentPane().add(descLabel,null);
        getContentPane().add(nameLabel,null);
        getContentPane().add(fieldName,null);
        if("codecontrol".equals(compType)){
            getContentPane().add(browse,null);
        }
    }

    private boolean validateInput(){

        if("".equals(fieldName.getText())){

            if("codecontrol".equals(this.compType)){
                EBIExceptionDialog.getInstance("Please insert a file name").Show(EBIMessage.ERROR_MESSAGE);
            }else{
                EBIExceptionDialog.getInstance("Please insert a name").Show(EBIMessage.ERROR_MESSAGE);
            }
            return false;
        }

        if("codecontrol".equals(this.compType)){

            if(!fieldName.getText().endsWith(".groovy") &&
                    !fieldName.getText().endsWith(".class") &&
                                !fieldName.getText().endsWith(".bsh")) {
                 EBIExceptionDialog.getInstance("Please insert a valid file name like Ex. [ myFile.groovy ] ").Show(EBIMessage.ERROR_MESSAGE);
                 return false;
            }

        }


        if(tab.containsKey(fieldName.getText())){
            EBIExceptionDialog.getInstance("Component name already exist!").Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

}
