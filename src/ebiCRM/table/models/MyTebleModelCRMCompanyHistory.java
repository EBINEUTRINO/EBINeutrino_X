package ebiCRM.table.models;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import ebiCRM.utils.EBISearchTreeNodeHistory;
import ebiNeutrinoSDK.EBIPGFactory;


public class MyTebleModelCRMCompanyHistory extends DefaultTreeTableModel {
       
        protected boolean asksAllowsChildren;

        public MyTebleModelCRMCompanyHistory() {
                this(new EBISearchTreeNodeHistory(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "","","", "", "","","","","",""), false);
                initData();
        }

        public MyTebleModelCRMCompanyHistory(TreeTableNode root) {
                this(root, false);
        }

        public MyTebleModelCRMCompanyHistory(TreeTableNode root, boolean asksAllowsChildren) {
                super(root);
                this.asksAllowsChildren = asksAllowsChildren;
        }
       
        public void initData() {
        	EBISearchTreeNodeHistory root = (EBISearchTreeNodeHistory)this.getRoot();
        	EBISearchTreeNodeHistory topLevel = new EBISearchTreeNodeHistory(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),"","", "", "","","","","","","");
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
                        return EBIPGFactory.getLANG("EBI_LANG_CHANGED");
                case 8: 
                	    return EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM");
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
                            return ((EBISearchTreeNodeHistory) node).getIntNr();
                        case 1:
                        	return ((EBISearchTreeNodeHistory) node).getName();
                        case 2:
                        	return ((EBISearchTreeNodeHistory) node).getStreet();
                        case 3:
                        	return ((EBISearchTreeNodeHistory) node).getZip();
                        case 4:
                        	return ((EBISearchTreeNodeHistory) node).getLocation();
                        case 5:
                        	return ((EBISearchTreeNodeHistory) node).getCountry();
                        case 6:
                        	return ((EBISearchTreeNodeHistory) node).getCategory();
                        case 7:
                        	return ((EBISearchTreeNodeHistory) node).getChanged();
                        case 8:
                        	return ((EBISearchTreeNodeHistory) node).getChangedFrom();
                        case 9:
                        	return ((EBISearchTreeNodeHistory) node).getIsLock();
                        	
                        }
                } catch (Exception ex) {
                      
                }
                return null;
        }
} 