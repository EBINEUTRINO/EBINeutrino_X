package ebiCRM.table.models;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import ebiCRM.utils.EBISearchTreeNode;
import ebiNeutrinoSDK.EBIPGFactory;

public class MyTableModelCRMCompany extends DefaultTreeTableModel {
       
        protected boolean asksAllowsChildren;

        public MyTableModelCRMCompany() {
                this(new EBISearchTreeNode(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "","","", "","", "","","","",""), false);
                initData();
        }

        public MyTableModelCRMCompany(TreeTableNode root) {
                this(root, false);
        }

        public MyTableModelCRMCompany(TreeTableNode root, boolean asksAllowsChildren) {
                super(root);
                this.asksAllowsChildren = asksAllowsChildren;
                
        }
       
        public void initData() {
        	    EBISearchTreeNode root = (EBISearchTreeNode)this.getRoot();
                EBISearchTreeNode topLevel = new EBISearchTreeNode(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "","","","", "", "","","","","");
                root.add(topLevel);
                
        }

        public Object getChild(Object parent, int index) {
                return super.getChild(parent, index);
        }

        public int getChildCount(Object parent) {
                return super.getChildCount(parent);
        }

        public int getColumnCount() {
                /**@todo Implement this org.jdesktopx.swing.treetable.TreeTableModel abstract method*/
                return 10;
        }

        public String getColumnName(int column) {
        	
        	
                switch (column) {
                case 0:
                        return EBIPGFactory.getLANG("EBI_LANG_C_INTERNAL_NUMBER");
                case 1:
                        return EBIPGFactory.getLANG("EBI_LANG_NAME");
                case 2:
                        return EBIPGFactory.getLANG("EBI_LANG_C_STREET_NR");
                case 3:
                        return EBIPGFactory.getLANG("EBI_LANG_C_ZIP");
                case 4:
                        return EBIPGFactory.getLANG("EBI_LANG_C_LOCATION");
                case 5:
                        return EBIPGFactory.getLANG("EBI_LANG_C_COUNTRY");
                case 6:
                        return EBIPGFactory.getLANG("EBI_LANG_CATEGORY");
                case 7:
                        return EBIPGFactory.getLANG("EBI_LANG_C_COOPERATION");
                case 8: 
                	    return EBIPGFactory.getLANG("EBI_LANG_C_CRM_CLASSIFICATION_TYPE");
                case 9: 
                        return EBIPGFactory.getLANG("EBI_LANG_C_LOCK");
                 default:
                        return "";
                         
                }
        }

        public Object getValueAt(Object node, int column) {

                try {
                        switch (column) {
                        case 0:
                            return ((EBISearchTreeNode) node).getIntNr();
                        case 1:
                        	return ((EBISearchTreeNode) node).getName();
                        case 2:
                        	return ((EBISearchTreeNode) node).getStreet();
                        case 3:
                        	return ((EBISearchTreeNode) node).getZip();
                        case 4:
                        	return ((EBISearchTreeNode) node).getLocation();
                        case 5:
                        	return ((EBISearchTreeNode) node).getCountry();
                        case 6:
                        	return ((EBISearchTreeNode) node).getCategory();
                        case 7:
                        	return ((EBISearchTreeNode) node).getCooperation();
                        case 8:
                        	return ((EBISearchTreeNode) node).getQualification();
                        case 9:
                        	return ((EBISearchTreeNode) node).getIsLock();
                        	
                        }
                } catch (Exception ex) {
                      
                }
                return null;
        }

} 