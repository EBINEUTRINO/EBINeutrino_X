package ebiNeutrino.core.gui.component;


import ebiNeutrino.core.GUIRenderer.EBIMutableTreeNode;

import java.util.HashMap;
import java.util.Iterator;

public class EBITreeFactory {

    private EBIMutableTreeNode topModel;
    private HashMap<String,EBIMutableTreeNode> treeNodeIndex = new HashMap<String,EBIMutableTreeNode>();
    private static EBITreeFactory treeNodefactory = null;

    public EBITreeFactory(){
       topModel = new EBIMutableTreeNode("EBI Neutrino Modules",null,"EBI Neutrino");
    }

    public void initList(){
        treeNodeIndex = new HashMap<String,EBIMutableTreeNode>();
        topModel.removeAllChildren();
    }


    public EBIMutableTreeNode getEBIROOTreeNode(){
        return this.topModel;
    }

    public void addChildTORoot(EBIMutableTreeNode node){
          treeNodeIndex.put(node.getModuleName(),node);
          this.topModel.add(node);
    }

    public void addChildTONODE(EBIMutableTreeNode source_node,EBIMutableTreeNode target_node){
        treeNodeIndex.put(source_node.getModuleName(),target_node);
        target_node.add(source_node);
    }

    public EBIMutableTreeNode getSearchTreeNodeIndex(String modName){
      EBIMutableTreeNode nodeToReturn = null;

      try{

        if(!"".equals(modName)){
            Iterator iter = treeNodeIndex.keySet().iterator();
            while(iter.hasNext()){
                String key = (String)iter.next();

                     String sname = treeNodeIndex.get(key).getTitle();
                     if(sname.length() >= modName.length()){
                         if(sname.substring(0,modName.length()).toLowerCase().equals(modName.toLowerCase())){
                             nodeToReturn = treeNodeIndex.get(key);
                             break;
                         }
                     }
            }
        }
      }catch (Exception ex){}
        return nodeToReturn;
    }

    public EBIMutableTreeNode getTreeNodeIndex(String modName){
        return treeNodeIndex.get(modName);
    }

    public static EBITreeFactory getEBITreeFactory(){
        if(treeNodefactory == null){
            treeNodefactory = new EBITreeFactory();
        }
        return treeNodefactory;
    }


}
