package ebiNeutrino.core.GUIDesigner.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ebiNeutrino.core.GUIDesigner.DesignerPanel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class DesignerMenu extends JMenuBar {

    private DesignerPanel desPanel = null;

    public DesignerMenu(DesignerPanel desPanel){
         this.desPanel = desPanel;
         initMenuBar();
    }

    public void initMenuBar(){
        JMenu fileMenu = new JMenu("Designer");

        JMenuItem newScriptItem = new JMenuItem("New File");
        newScriptItem.setIcon(EBIConstant.ICON_NEW);
        newScriptItem.addActionListener(newFileListenerAction());

        JMenuItem openFileItem = new JMenuItem("Open File");
        openFileItem.setIcon(EBIConstant.ICON_EDIT_DIALOG);
        openFileItem.addActionListener(openFileListenerAction());

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setIcon(EBIConstant.ICON_SAVE);
        saveItem.addActionListener(saveListenerAction());

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.setIcon(EBIConstant.ICON_DELETE);
        deleteItem.addActionListener(deleteListenerAction());


        fileMenu.add(newScriptItem);
        fileMenu.add(openFileItem);
        fileMenu.add(saveItem);   
        fileMenu.addSeparator();
        fileMenu.add(deleteItem);
        add(fileMenu);
    }


    protected ActionListener newFileListenerAction(){
	     return	new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                     desPanel.newSourceFile();
				}
		    };
	}

     protected ActionListener saveListenerAction(){
		 return	new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    desPanel.saveFile();
				}
		    };
	}

    protected ActionListener openFileListenerAction(){
		 return	new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    desPanel.openScriptFile();
				}
		    };
	}

    protected ActionListener deleteListenerAction(){
		 return	new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                     desPanel.deleteSelectedComponent();
				}
		    };
	}

}
