package ebiNeutrino.core.GUIRenderer;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 * User: alfaRomeoPC
 * Date: 06/08/12
 * Time: 16.47
 * To change this template use File | Settings | File Templates.
 */
public class EBIMutableTreeNode extends DefaultMutableTreeNode {
    
    private String xmlPath = "";
    private String moduleName = "";
    private String title ="";
    
    public EBIMutableTreeNode(String text, String xmlPath,String title){
        super(title);
        this.title = title;
        this.xmlPath = xmlPath;
        this.moduleName = text;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public String getModuleName() {
        return moduleName;
    }
    
    public String getTitle(){
        return title;
    }

}
