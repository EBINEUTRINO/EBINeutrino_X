package ebiCRM.gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.panels.EBICRMProduct;
import ebiCRM.table.models.MyTableModelCRMProductSearch;
import ebiCRM.utils.EBISearchTreeNodeProduct;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyofferpositions;
import ebiNeutrinoSDK.model.hibernate.Companyorderpositions;
import ebiNeutrinoSDK.model.hibernate.Companyservicepositions;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignposition;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolposition;
import ebiNeutrinoSDK.model.hibernate.Crmproduct;
import ebiNeutrinoSDK.model.hibernate.Crmproductdependency;

public class EBICRMDialogSearchProduct {

    private MyTableModelCRMProductSearch tabModel = null;
    private EBICRMModule ebiModule = null;
    private Set<Crmproductdependency> dipendency = null;
    private EBISearchTreeNodeProduct selectedNode = null;
    public  boolean haveProductSelected = false;
    private boolean isDependency = false;
    private boolean isCRMOrder = false;
    private boolean isCRMOffer = false;
    private boolean isCampaign = false;
    private boolean isCRMService = false;
    private boolean isProsol = false;
    private boolean isInvoice = false;
    private Companyorderpositions orderPosition = null;
    private Companyofferpositions offerPosition = null;
    private Crmcampaignposition campaignPosition = null;
    private Companyservicepositions servicePosition = null;
    private Crmproblemsolposition prosolPosition = null;
    private Crminvoiceposition invoicePosition = null;
    private EBICRMDialogAddProduct addProduct = null;

    /**
     * This is the xxx default constructor
     */
    public EBICRMDialogSearchProduct(EBICRMModule module) {

        ebiModule = module;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();
    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Set<Crmproductdependency> dipList) {

        dipendency = dipList;
        ebiModule = module;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();
        isDependency = true;

    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Companyorderpositions orposition, EBICRMDialogAddProduct addPro) {

        orderPosition = orposition;
        isCRMOrder = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();

    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Companyofferpositions ofposition, EBICRMDialogAddProduct addPro) {

        offerPosition = ofposition;
        isCRMOffer = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();

    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Companyservicepositions servicePosition, EBICRMDialogAddProduct addPro) {

        this.servicePosition = servicePosition;
        isCRMService = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();

    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Crmproblemsolposition prosolPosition, EBICRMDialogAddProduct addPro) {

        this.prosolPosition = prosolPosition;
        isProsol = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();

    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Crmcampaignposition cmppos, EBICRMDialogAddProduct addPro) {
        campaignPosition = cmppos;
        isCampaign = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();
    }

    public EBICRMDialogSearchProduct(EBICRMModule module, Crminvoiceposition invPos, EBICRMDialogAddProduct addPro) {
        invoicePosition = invPos;
        isInvoice = true;
        ebiModule = module;
        this.addProduct = addPro;
        tabModel = new MyTableModelCRMProductSearch();
        initialzeDialog();
    }

    private void initialzeDialog() {

        ebiModule.gui.loadGUI("CRMDialog/productSearchDialog.xml");
        
        try {
            ebiModule.system.hibernate.openHibernateSession("SEARCH_PRODUCT_SESSION");
            ebiModule.gui.getTreeTable("treetableProduct","searchProduct").setTreeTableModel(tabModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisible(){
        ebiModule.gui.getEBIDialog("searchProduct").setTitle(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_SEARCH"));
        ebiModule.gui.getVisualPanel("searchProduct").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_SEARCH"));

        ebiModule.gui.getLabel("category","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
        ebiModule.gui.getLabel("productName","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_NAME"));
        ebiModule.gui.getLabel("productNr","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NUMBER"));

        ebiModule.gui.getTreeTable("treetableProduct","searchProduct").setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        ebiModule.gui.getTextfield("productNrText","searchProduct").requestFocus();
        ebiModule.gui.getTextfield("productNrText","searchProduct").registerKeyboardAction(new ActionListener(){
               public void actionPerformed(ActionEvent ev) {
                  searchProduct();
               }
            },KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);

        ebiModule.gui.getTextfield("productNameText","searchProduct").registerKeyboardAction(new ActionListener(){
               public void actionPerformed(ActionEvent ev) {
                  searchProduct();
               }
            },KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);

        TreeSelectionModel rowSM = ebiModule.gui.getTreeTable("treetableProduct","searchProduct").getTreeSelectionModel();
        rowSM.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                try {
                    TreeSelectionModel lsm = (TreeSelectionModel) e.getSource();

                    if (lsm.isSelectionEmpty()) {
                        ebiModule.gui.getButton("applyButton","searchProduct").setEnabled(false);
                    } else {

                        //selRow = lsm.getMinSelectionRow();

                        javax.swing.tree.TreePath Node = lsm.getLeadSelectionPath();
                        selectedNode = (EBISearchTreeNodeProduct) Node.getLastPathComponent();

                        if (!selectedNode.getProductID().equals(
                                EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                            ebiModule.gui.getButton("applyButton","searchProduct").setEnabled(true);
                        }
                    }
                } catch (java.lang.ArrayIndexOutOfBoundsException ex) {
                }
            }
        });

        ebiModule.gui.getButton("productSearchButton","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_SEARCH"));
        ebiModule.gui.getButton("productSearchButton","searchProduct").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    searchProduct();
                }
            });

        ebiModule.gui.getButton("applyButton","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));
        ebiModule.gui.getButton("applyButton","searchProduct").setEnabled(false);
        ebiModule.gui.getButton("applyButton","searchProduct").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    applySearch();
                }
            });

        ebiModule.gui.getButton("cancelButton","searchProduct").setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
        ebiModule.gui.getButton("cancelButton","searchProduct").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.gui.getEBIDialog("searchProduct").setVisible(false);
                }
            });


         ebiModule.gui.getTreeTable("treetableProduct","searchProduct").addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if(selectedNode != null){
	                    	if (!selectedNode.getProductID().equals(
	                                EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
	                            applySearch();
	
	                        }
                        }
                    }
                }
            });
        
            ebiModule.gui.getTreeTable("treetableProduct","searchProduct").addMouseListener(new java.awt.event.MouseAdapter() {

                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                       if(selectedNode != null && selectedNode.getProductID() != null){
                            if (!selectedNode.getProductID().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                                applySearch();
                            }
                       }
                    }
                }
            });

         ebiModule.gui.getComboBox("categoryText","searchProduct").setModel(new DefaultComboBoxModel(EBICRMProduct.category));
         ebiModule.gui.getComboBox("categoryText","searchProduct").registerKeyboardAction(new ActionListener(){
               public void actionPerformed(ActionEvent ev) {
                  searchProduct();
               }
            },KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);

         ebiModule.gui.showGUI();
    }


    public void searchProduct() {
        ResultSet set1 = null;
        ResultSet set = null;
        try {
            
            EBISearchTreeNodeProduct root = new EBISearchTreeNodeProduct(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "");

            StringBuffer strQuery = new StringBuffer();
            strQuery.append("SELECT * FROM CRMPRODUCT");

            boolean param1 = false;
            boolean param2 = false;
            boolean param3 = false;

            if(!"".equals(ebiModule.gui.getTextfield("productNrText","searchProduct").getText())){
                param1 = true;
                strQuery.append(" WHERE PRODUCTNR LIKE ? ");
            }

            if(!"".equals(ebiModule.gui.getTextfield("productNameText","searchProduct").getText())){
                param2 = true;
                if(param1 == false){
                     strQuery.append(" WHERE PRODUCTNAME LIKE ? ");
                }else{
                    strQuery.append(" AND PRODUCTNAME LIKE ? ");
                }
            }

            if(ebiModule.gui.getComboBox("categoryText","searchProduct").getSelectedItem() != null){
                if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                        equals(ebiModule.gui.
                                                        getComboBox("categoryText","searchProduct").getSelectedItem().toString()) &&
                                                                        !"".equals(ebiModule.gui.getComboBox("categoryText","searchProduct")
                                                                                                             .getSelectedItem().toString()) ){

                    param3 = true;
                    if(param1 == false && param2 == false){
                        strQuery.append(" WHERE CATEGORY LIKE ? ");
                    }else{
                        strQuery.append(" AND CATEGORY LIKE ? ");
                    }
                }
            }

            if(!param1 && !param2 && !param3 ){
                strQuery.append(" order by PRODUCTID desc limit 500");
            }

            PreparedStatement pst = ebiModule.system.getIEBIDatabase().initPreparedStatement(strQuery.toString());
            if(param1){
                pst.setString(1, "%"+ebiModule.gui.getTextfield("productNrText","searchProduct").getText() + "%");
            }
            if(param2){
                pst.setString(param1 ? 2 : 1, "%"+ebiModule.gui.getTextfield("productNameText","searchProduct").getText() + "%");
            }
            if(param3){
                pst.setString((param1 && param2 ) ? 3 : (param1 || param2) ? 2 : 1,
                       "%"+ ebiModule.gui.getComboBox("categoryText","searchProduct").getSelectedItem().toString()+"%");
            }

            set = ebiModule.system.getIEBIDatabase().executePreparedQuery(pst);

            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {

                    EBISearchTreeNodeProduct topLevel = new EBISearchTreeNodeProduct(
                            set.getString("PRODUCTID"),
                            set.getString("PRODUCTNR"),
                            set.getString("PRODUCTNAME"),
                            set.getString("CATEGORY"),
                            set.getString("TYPE"));


                    PreparedStatement ps = ebiModule.system.getIEBIDatabase().initPreparedStatement("SELECT d.PRODUCTID as PID, d.PRODUCTIDID AS PPID,  c.PRODUCTID as PRID, d.PRODUCTNR as PRNR,d.PRODUCTNAME as PRNAME,c.CATEGORY as CAT, c.TYPE as TYPE " +
                            "FROM CRMPRODUCTDEPENDENCY d LEFT JOIN CRMPRODUCT c  ON c.PRODUCTID=d.PRODUCTID WHERE d.PRODUCTID=? ");
                    ps.setString(1, set.getString("PRODUCTID"));
                    set1 = ebiModule.system.getIEBIDatabase().executePreparedQuery(ps);

                    set1.last();
                    if (set1.getRow() > 0) {
                        set1.beforeFirst();
                        while (set1.next()) {
                           // System.out.println(set1.getString("PID")+" "+set1.getString("PRNR")+" "+set1.getString("PRNAME"));
                            // CRMPRODUCTDEPENDENCY
                            EBISearchTreeNodeProduct topLevel1 = new EBISearchTreeNodeProduct(
                                    set1.getString("PPID"),
                                    set1.getString("PRNR"),
                                    set1.getString("PRNAME"),
                                    set1.getString("CAT"),
                                    set1.getString("TYPE"));
                            topLevel.add(topLevel1);

                        }
                    }
                    root.add(topLevel);
                }

                this.tabModel = new MyTableModelCRMProductSearch(root);
                ebiModule.gui.getTreeTable("treetableProduct","searchProduct").setTreeTableModel(tabModel);
                ebiModule.gui.getTreeTable("treetableProduct","searchProduct").updateUI();
                ebiModule.gui.getTreeTable("treetableProduct","searchProduct").changeSelection(0, 0, false, false);
                ebiModule.gui.getTreeTable("treetableProduct","searchProduct").requestFocus();

            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PRODUCT_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
        } catch (SQLException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } finally {
            if (set != null) {
                try {
                    set.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (set1 != null) {
                try {
                    set1.close();
                } catch (SQLException e) {
                    e.printStackTrace(); 
                }
            }
        }
    }


    public void applySearch(){
        if (isDependency) {
            fillDipendencyList();
            ebiModule.getProductPane().showDependency();
        } else if (isCRMOrder) {
            fillProductFromOrder();
            this.addProduct.fillHTMLForm();
        } else if (isCRMOffer) {
            fillProductFromOffer();
            this.addProduct.fillHTMLForm();
        } else if (isCampaign) {
            fillProductFromCampaign();
            this.addProduct.fillHTMLForm();
        } else if (isCRMService){
            fillProductFromService();
            this.addProduct.fillHTMLForm();
        }else if(isProsol){
            fillProductFromProsol();
            this.addProduct.fillHTMLForm();
        }else if(isInvoice){
            fillProductFromInvoice();
            this.addProduct.fillHTMLForm();
        }

        if(ebiModule.gui.getButton("applyButton","productInsertDialog") != null){
            ebiModule.gui.getButton("applyButton","productInsertDialog").setEnabled(true);
        }

        ebiModule.gui.getEBIDialog("searchProduct").setVisible(false);
    }

    private void fillDipendencyList() {

        Query query;
        try {
            query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0,selectedNode.getProductID());


            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                //mod.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                Crmproductdependency dep = new Crmproductdependency();
                dep.setCrmproduct(product);
                dep.setProductidid(Integer.parseInt(selectedNode.getProductID()));
                dep.setCreateddate(new Date());
                dep.setCreatedfrom(EBIPGFactory.ebiUser);
                dep.setProductnr(product.getProductnr());
                dep.setProductname(product.getProductname());
                if(product.getProductid() != null){
                    dep.setCrmproduct(product);
                    ebiModule.system.hibernate.getHibernateTransaction("SEARCH_PRODUCT_SESSION").begin();
                    ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").saveOrUpdate(dep);
                    ebiModule.system.hibernate.getHibernateTransaction("SEARCH_PRODUCT_SESSION").commit();
                }
                dipendency.add(dep);
                ebiModule.getProductPane().dataControlProduct.getProduct().getCrmproductdependencies().add(dep);
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromCampaign() {
        Query query;
        try {
            query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());


            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                campaignPosition.setProductid(product.getProductid());
                campaignPosition.setCreateddate(new java.util.Date());
                campaignPosition.setCreatedfrom(EBIPGFactory.ebiUser);
                if(product.getProductnr() != null){
                    campaignPosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    campaignPosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    campaignPosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    campaignPosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    campaignPosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    campaignPosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    campaignPosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    campaignPosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromInvoice() {
        Query query;
        try {
            query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());


            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                invoicePosition.setProductid(product.getProductid());
                invoicePosition.setCreateddate(new java.util.Date());
                invoicePosition.setCreatedfrom(EBIPGFactory.ebiUser);
                if(product.getProductnr() != null){
                    invoicePosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    invoicePosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    invoicePosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    invoicePosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    invoicePosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    invoicePosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    invoicePosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    invoicePosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromOffer() {
        Query query  ;
        try {
            query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());

            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                offerPosition.setProductid(product.getProductid());
                offerPosition.setCreateddate(new java.util.Date());
                offerPosition.setCreatedfrom(EBIPGFactory.ebiUser);
                if(product.getProductnr() != null){
                    offerPosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    offerPosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    offerPosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    offerPosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    offerPosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    offerPosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    offerPosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    offerPosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromService() {
        Query query  ;
        try {
           query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());

            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                servicePosition.setProductid(product.getProductid());
                servicePosition.setCreateddate(new java.util.Date());
                servicePosition.setCreatedfrom(EBIPGFactory.ebiUser);
                if(product.getProductnr() != null){
                    servicePosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    servicePosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    servicePosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    servicePosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    servicePosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    servicePosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    servicePosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    servicePosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromProsol() {
        Query query  ;
        try {
           query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());

            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                prosolPosition.setCreateddate(new java.util.Date());
                prosolPosition.setCreatedfrom(EBIPGFactory.ebiUser);

                if(product.getProductnr() != null){
                    prosolPosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    prosolPosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    prosolPosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    prosolPosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    prosolPosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    prosolPosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    prosolPosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    prosolPosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillProductFromOrder() {
        Query query  ;
        try {
            query = ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").createQuery(
                    "from Crmproduct where productid=? ").setString(0, selectedNode.getProductID());

            Iterator it = query.iterate();

            if (it.hasNext()) {
                Crmproduct product = (Crmproduct) it.next();
                ebiModule.system.hibernate.getHibernateSession("SEARCH_PRODUCT_SESSION").refresh(product);
                orderPosition.setProductid(product.getProductid());
                orderPosition.setCreateddate(new java.util.Date());
                orderPosition.setCreatedfrom(EBIPGFactory.ebiUser);
                if(product.getProductnr() != null){
                    orderPosition.setProductnr(product.getProductnr());
                }
                if(product.getProductname() != null){
                    orderPosition.setProductname(product.getProductname());
                }
                if(product.getCategory() != null){
                    orderPosition.setCategory(product.getCategory());
                }
                if(product.getDescription() != null){
                    orderPosition.setDescription(product.getDescription());
                }
                if(product.getSaleprice() != null){
                    orderPosition.setNetamount(product.getSaleprice());
                }
                if(product.getPretax() != null){
                    orderPosition.setPretax(product.getPretax());
                }
                if(product.getTaxtype() != null){
                    orderPosition.setTaxtype(product.getTaxtype());
                }
                if(product.getType() != null){
                    orderPosition.setType(product.getType());
                }
                addProduct.productID = product.getProductid();
                
            }

        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public MyTableModelCRMProductSearch getTableModel(){
        return tabModel;
    }

    public void setSelectedNode(EBISearchTreeNodeProduct node){
        this.selectedNode = node;
    }

    public EBISearchTreeNodeProduct getSelectedNode(){
        return this.selectedNode;
    }
}