package ebiCRM.table.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import ebiCRM.utils.EBISearchTreeNodeProduct;
import ebiNeutrinoSDK.EBIPGFactory;


public class MyTableModelCRMProductSearch extends AbstractTreeTableModel {

    protected boolean asksAllowsChildren;

    public MyTableModelCRMProductSearch() {
        this(new EBISearchTreeNodeProduct(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", ""), false);
        initData();
    }

    public MyTableModelCRMProductSearch(TreeTableNode root) {
        this(root, false);
    }

    public MyTableModelCRMProductSearch(TreeTableNode root, boolean asksAllowsChildren) {
        super(root);
        this.asksAllowsChildren = asksAllowsChildren;
    }

    public void initData() {
        EBISearchTreeNodeProduct root = (EBISearchTreeNodeProduct) this.getRoot();
        EBISearchTreeNodeProduct topLevel = new EBISearchTreeNodeProduct(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "");
        root.add(topLevel);

    }

    public Object getChild(Object parent, int index) {
        TreeTableNode fileNode = ((TreeTableNode) parent);
        return fileNode.getChildAt(index);
    }

    public int getChildCount(Object parent) {
        TreeTableNode fileNode = ((TreeTableNode) parent);
        return fileNode.getChildCount();
    }

    public int getColumnCount() {
        /**@todo Implement this org.jdesktopx.swing.treetable.TreeTableModel abstract method*/
        return 7;
    }

    public String getColumnName(int column) {


        switch (column) {
            case 1:
                return EBIPGFactory.getLANG("EBI_LANG_ID");
            case 2:
                return EBIPGFactory.getLANG("EBI_LANG_NUMBER");
            case 3:
                return EBIPGFactory.getLANG("EBI_LANG_NAME");
            case 4:
                return EBIPGFactory.getLANG("EBI_LANG_CATEGORY");
            case 5:
                return EBIPGFactory.getLANG("EBI_LANG_TYPE");
            default:
                return "";

        }
    }

    public Object getValueAt(Object node, int column) {

        try {
            switch (column) {
                case 1:
                    return ((EBISearchTreeNodeProduct) node).getProductID();
                case 2:
                    return ((EBISearchTreeNodeProduct) node).getProductNr();
                case 3:
                    return ((EBISearchTreeNodeProduct) node).getProductName();
                case 4:
                    return ((EBISearchTreeNodeProduct) node).getCategory();
                case 5:
                    return ((EBISearchTreeNodeProduct) node).getType();
                default:
                    break;

            }
        } catch (Exception ex) {

        }
        return null;
    }

    public int getIndexOfChild(Object arg0, Object arg1) {
        // TODO Auto-generated method stub
        return 0;
    }

//	        public Class getColumnClass(int c) {
//		        return getColumnClass(c).getClass();
//		    }
//	        public boolean isCellEditable(java.lang.Object node, int col) {
//		        if(col == 1 ){
//		        	if(i == 0){
//			        	if(((EBISearchTreeNodeProduct) node).getIsChecked()){
//	                		//System.out.println("YES IS CHECKED");
//							((EBISearchTreeNodeProduct) node).setIsChecked(new Boolean(false));
//						
//						}else{
//							//System.out.println("no is not checked");
//							((EBISearchTreeNodeProduct) node).setIsChecked(new Boolean(true));
//							
//						}
//			        	i++;
//		        	}
//			        	return true;
//		        }else{
//		        	i = 0;
//		    	    return false;
//		        }
//		    }
//	       
} 
