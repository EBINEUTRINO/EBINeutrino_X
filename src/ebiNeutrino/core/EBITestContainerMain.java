package ebiNeutrino.core;

import javax.swing.JFrame;

import ebiNeutrinoSDK.gui.component.EBIVisualPanel;

/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Description:
 * This is the main
 *  class for EBI Neutrino
 *
 * JVM start point:
 * public static void main(String[] args)
 */



public class EBITestContainerMain {

   
    public static void main(String[] args) throws Exception {
        EBIVisualPanel panel = new EBIVisualPanel(false);
        panel.setModuleTitle("Titolo");
        panel.showLockIcon(false);
        JFrame frame = new JFrame();
        frame.setSize(800,600);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);

    }


    public EBITestContainerMain() {

 
    }


}

