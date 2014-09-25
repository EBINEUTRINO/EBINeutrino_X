package ebiNeutrino.core.GUIRenderer;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.HashMap;
import java.util.Iterator;


public class EBIFocusTraversalPolicy extends FocusTraversalPolicy{

    private HashMap<Integer,Component> focuscomp = null;


    public EBIFocusTraversalPolicy(){
      focuscomp = new HashMap<Integer,Component>();
    }


    public void addComponent(int index, Component comp){
       this.focuscomp.put(index,comp);
    }

    public Component getComponentAfter(Container focusCycleRoot,Component aComponent){

        Component cmp = aComponent;

        if(aComponent.toString().indexOf("ComboBox") != -1){
           if(aComponent.getParent() instanceof JComboBox){
             cmp = aComponent.getParent();
           }
        }else if(aComponent.toString().indexOf("DatePicker") != -1 ){
            cmp = aComponent.getParent();
        }

        Iterator iter = this.focuscomp.keySet().iterator();

        while(iter.hasNext()){
            int idx = (Integer) iter.next();
            
            if(this.focuscomp.get(idx) == cmp){
                cmp = this.focuscomp.get(idx +1);
                break;
            }
        }

        return cmp;
    }

    public Component getComponentBefore(Container focusCycleRoot,Component aComponent){

        Component cmp = aComponent;

        if(aComponent.toString().indexOf("ComboBox") != -1){
            if(aComponent.getParent() instanceof JComboBox){
                cmp = aComponent.getParent();
            }
        }else if(aComponent.toString().indexOf("DatePicker") != -1 ){
            cmp = aComponent.getParent();
        }

        Iterator iter = this.focuscomp.keySet().iterator();

        while(iter.hasNext()){
            int idx = (Integer) iter.next();

            if(this.focuscomp.get(idx) == cmp){

                int id = idx -1;

                if(id < 0){
                    id = 0;
                }
                
                cmp = this.focuscomp.get(id);
                break;
            }
        }

        return cmp;
    }

    public Component getDefaultComponent(Container focusCycleRoot) {
      if(focuscomp.get(1) != null){
        focuscomp.get(1).requestFocus();
      }
        return focuscomp.get(1);
    }

    public Component getLastComponent(Container focusCycleRoot) {
        return focuscomp.get(focuscomp.size()+1);
    }

    public Component getFirstComponent(Container focusCycleRoot) {
       if(focuscomp.get(1) != null){
        focuscomp.get(1).requestFocus();
       }
        return focuscomp.get(1);
    }

    public boolean isEmpty(){
        return focuscomp.isEmpty();
    }
}