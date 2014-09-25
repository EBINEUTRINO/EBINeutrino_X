package ebiCRM.gui.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

import ebiCRM.EBICRMModule;
import ebiCRM.utils.EBISearchTreeNodeProduct;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIJTextFieldNumeric;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyoffer;
import ebiNeutrinoSDK.model.hibernate.Companyofferpositions;
import ebiNeutrinoSDK.model.hibernate.Companyorder;
import ebiNeutrinoSDK.model.hibernate.Companyorderpositions;
import ebiNeutrinoSDK.model.hibernate.Companyservice;
import ebiNeutrinoSDK.model.hibernate.Companyservicepositions;
import ebiNeutrinoSDK.model.hibernate.Crmcampaign;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignposition;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolposition;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolutions;
import ebiNeutrinoSDK.utils.EBIConstant;
import org.jdesktop.swingx.treetable.TreeTableNode;

public class EBICRMDialogAddProduct {

    private Set<Companyofferpositions> poList = null;
    private Set<Companyorderpositions> porList = null;
    private Set<Crmcampaignposition> campProList = null;
    private Set<Companyservicepositions> serviceProList = null;
    private Set<Crmproblemsolposition> prosolProList = null;
    private Companyoffer offer = null;
    private Companyorder order = null;
    private Crmcampaign campaign = null;
    private Crminvoice invoice = null;
    private Companyservice service = null;
    private Crmproblemsolutions prosol = null;
    private boolean isOrder = false;
    private boolean isOffer = false;
    private boolean isCampaign = false;
    private boolean isService = false;
    private boolean isProsol = false;
    private boolean isInvoice = false;
    private EBICRMModule ebiModule = null;
    private Companyorderpositions orderPosition = null;
    private Companyofferpositions offerPosition = null;
    private Crmcampaignposition campaignPosition = null;
    private Crminvoiceposition invoicePosition = null;
    private Companyservicepositions servicePosition = null;
    private Crmproblemsolposition prosolPosition = null;
    private NumberFormat currency = null;
    public int productID =0;

   
    public EBICRMDialogAddProduct(Set<Companyofferpositions> polist, Companyoffer offer, EBICRMModule module) {
        ebiModule = module;
        offerPosition = new Companyofferpositions();

        ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

        this.poList = polist;
        this.offer = offer;

        isOffer = true;

    }

    public EBICRMDialogAddProduct(Set<Companyorderpositions> polist, Companyorder order, EBICRMModule module) {
        orderPosition = new Companyorderpositions();
        ebiModule = module;

        ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

        this.porList = polist;
        this.order = order;

        isOrder = true;
    }

    public EBICRMDialogAddProduct(Set<Crmcampaignposition> cpolist, Crmcampaign campaign, EBICRMModule module) {
        campaignPosition = new Crmcampaignposition();
        ebiModule = module;

        ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

        this.campProList = cpolist;
        this.campaign = campaign;

        isCampaign = true;
    }

    public EBICRMDialogAddProduct(Crminvoice invoice, EBICRMModule module) {
            invoicePosition = new Crminvoiceposition();
            ebiModule = module;

            ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

            this.invoice = invoice;

            isInvoice = true;
    }


    public EBICRMDialogAddProduct(Set<Companyservicepositions> cpolist, Companyservice service, EBICRMModule module) {
        servicePosition = new Companyservicepositions();
        ebiModule = module;

        ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

        this.serviceProList = cpolist;
        this.service = service;

        isService = true;
    }

    public EBICRMDialogAddProduct(Set<Crmproblemsolposition> cpolist, Crmproblemsolutions prosol, EBICRMModule module) {
        prosolPosition = new Crmproblemsolposition();
        ebiModule = module;

        ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

        this.prosolProList = cpolist;
        this.prosol = prosol;

        isProsol = true;
    }


    public void setVisible(){

        currency= NumberFormat.getCurrencyInstance();

        ebiModule.guiRenderer.getEBIDialog("productInsertDialog").setTitle(EBIPGFactory.getLANG("EBI_LANG_C_INSERT_PRODUCT"));
        ebiModule.guiRenderer.getVisualPanel("productInsertDialog").setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_C_INSERT_PRODUCT"));

        ebiModule.guiRenderer.getLabel("deduction","productInsertDialog").setText(EBIPGFactory.getLANG("EBI_LANG_DEDUCTION"));
        ebiModule.guiRenderer.getLabel("quantity","productInsertDialog").setText(EBIPGFactory.getLANG("EBI_LANG_QUANTITY"));
        ebiModule.guiRenderer.getLabel("productNr","productInsertDialog").setText(EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NUMBER"));

        ebiModule.guiRenderer.getEditor("productText","productInsertDialog").setEditable(false);
        ebiModule.guiRenderer.getEditor("productText","productInsertDialog").setBackground(EBIPGFactory.systemColor);

        if(isProsol){
           ebiModule.guiRenderer.getLabel("deduction","productInsertDialog").setVisible(false);
           ebiModule.guiRenderer.getLabel("quantity","productInsertDialog").setVisible(false);
           ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").setVisible(false);
           ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").setVisible(false);
           ebiModule.guiRenderer.getLabel("%","productInsertDialog").setVisible(false);
        }

        ebiModule.guiRenderer.getButton("closeButton","productInsertDialog").setText(EBIPGFactory.getLANG("EBI_LANG_CLOSE"));
        ebiModule.guiRenderer.getButton("closeButton","productInsertDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    ebiModule.guiRenderer.getEBIDialog("productInsertDialog").setVisible(false);
                }
        });

        ebiModule.guiRenderer.getButton("applyButton","productInsertDialog").setEnabled(false);
        ebiModule.guiRenderer.getButton("applyButton","productInsertDialog").setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
        ebiModule.guiRenderer.getButton("applyButton","productInsertDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    savePosistion();
                }
        });

        ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").addKeyListener(new KeyAdapter() {
            private String text="";
            public void keyReleased(java.awt.event.KeyEvent e) {

                if(e.getKeyCode() == KeyEvent.VK_SHIFT ||
                        e.getKeyCode() == KeyEvent.SHIFT_MASK || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_CONTROL ||
                        e.getKeyCode() == KeyEvent.SHIFT_DOWN_MASK  || e.getKeyCode() == KeyEvent.SHIFT_MASK ||  e.getKeyCode() == KeyEvent.VK_CAPS_LOCK ){
                    return;
                }

                text = ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").getText();

                if (isOffer) {
                    offerPosition.setProductname(text);
                }

                if (isOrder) {
                    orderPosition.setProductname(text);
                }

                if(isService){
                    servicePosition.setProductname(text);
                }

                if (isCampaign) {
                    campaignPosition.setProductname(text);
                }

                if(isProsol){
                    prosolPosition.setProductname(text);
                }

                if(isInvoice){
                    invoicePosition.setProductname(text);
                }
                int pos = ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").getCaretPosition();
                fillHTMLForm();
                ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").setCaretPosition(pos);
            }

        });

        ebiModule.guiRenderer.getTextarea("descriptionText","productInsertDialog").addKeyListener(new KeyAdapter() {
            private String text="";
            public void keyReleased(java.awt.event.KeyEvent e) {

                if(e.getKeyCode() == KeyEvent.VK_SHIFT ||
                        e.getKeyCode() == KeyEvent.SHIFT_MASK || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
                        || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_CONTROL ||
                         e.getKeyCode() == KeyEvent.SHIFT_DOWN_MASK  || e.getKeyCode() == KeyEvent.SHIFT_MASK ||  e.getKeyCode() == KeyEvent.VK_CAPS_LOCK ){
                    return;
                }

                text = ebiModule.guiRenderer.getTextarea("descriptionText","productInsertDialog").getText();
                if (isOffer) {
                    offerPosition.setDescription(text);
                }

                if (isOrder) {
                    orderPosition.setDescription(text);
                }

                if(isService){
                    servicePosition.setDescription(text);
                }

                if (isCampaign) {
                    campaignPosition.setDescription(text);
                }

                if(isProsol){
                    prosolPosition.setDescription(text);
                }

                if(isInvoice){
                    invoicePosition.setDescription(text);
                }

                int pos = ebiModule.guiRenderer.getTextarea("descriptionText","productInsertDialog").getCaretPosition();
                fillHTMLForm();
                ebiModule.guiRenderer.getTextarea("descriptionText","productInsertDialog").setCaretPosition(pos);
            }

        });


        ebiModule.guiRenderer.getButton("searchProduct","productInsertDialog").setIcon(EBIConstant.ICON_SEARCH);
        ebiModule.guiRenderer.getButton("searchProduct","productInsertDialog").addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                    EBICRMDialogSearchProduct prod = null;
                    if (isOffer) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, offerPosition,EBICRMDialogAddProduct.this);
                    }
                    if (isOrder) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, orderPosition,EBICRMDialogAddProduct.this);
                    }
                    if(isService){
                       prod = new EBICRMDialogSearchProduct(ebiModule, servicePosition,EBICRMDialogAddProduct.this);
                    }

                    if (isCampaign) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, campaignPosition,EBICRMDialogAddProduct.this);
                    }

                    if(isProsol){
                       prod = new EBICRMDialogSearchProduct(ebiModule, prosolPosition,EBICRMDialogAddProduct.this);
                    }

                    if(isInvoice){
                      prod = new EBICRMDialogSearchProduct(ebiModule, invoicePosition,EBICRMDialogAddProduct.this);  
                    }
                    if(prod != null){
                        prod.setVisible();
                    }
                    ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").requestFocus();
                }
            });


         ebiModule.guiRenderer.getTextfield("productNrText","productInsertDialog").requestFocus();
         ebiModule.guiRenderer.getTextfield("productNrText","productInsertDialog").registerKeyboardAction(new ActionListener(){
                public void actionPerformed(ActionEvent ev) {
                    EBICRMDialogSearchProduct prod = null;
                    if (isOffer) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, offerPosition,EBICRMDialogAddProduct.this);
                    }

                    if (isOrder) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, orderPosition,EBICRMDialogAddProduct.this);
                    }

                    if(isService){
                        prod = new EBICRMDialogSearchProduct(ebiModule, servicePosition,EBICRMDialogAddProduct.this);
                    }

                    if (isCampaign) {
                        prod = new EBICRMDialogSearchProduct(ebiModule, campaignPosition,EBICRMDialogAddProduct.this);
                    }

                    if(isProsol){
                        prod = new EBICRMDialogSearchProduct(ebiModule, prosolPosition,EBICRMDialogAddProduct.this);
                    }

                    if(isInvoice){
                        prod = new EBICRMDialogSearchProduct(ebiModule, invoicePosition,EBICRMDialogAddProduct.this);
                    }

                    ebiModule.guiRenderer.getTextfield("productNrText","searchProduct").setText(
                                        ebiModule.guiRenderer.getTextfield("productNrText","productInsertDialog").getText());

                    prod.searchProduct();
                    if(prod.getTableModel().getChildCount(prod.getTableModel().getRoot()) > 1){
                        prod.setVisible();
                    }else{
                        prod.setSelectedNode((EBISearchTreeNodeProduct)((EBISearchTreeNodeProduct) prod.getTableModel().getRoot()).getChildAt(0));
                        prod.applySearch();
                    }

                }
         }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);

         ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.NUMERIC_MINUS));
         ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").registerKeyboardAction(new ActionListener(){
               public void actionPerformed(ActionEvent ev) {
                  savePosistion();
               }
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),JComponent.WHEN_FOCUSED);



         ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.NUMERIC));
         ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").registerKeyboardAction(new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
                 savePosistion();
             }
         }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

         ebiModule.guiRenderer.getButton("newProduct","productInsertDialog").setIcon(EBIConstant.ICON_NEW);
         ebiModule.guiRenderer.getButton("newProduct","productInsertDialog").addActionListener(new java.awt.event.ActionListener() {

             public void actionPerformed(java.awt.event.ActionEvent e) {
                 resetFields();
             }
         });


         ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").addKeyListener(new KeyListener() {
             public void keyTyped(KeyEvent e) {
             }

             public void keyPressed(KeyEvent e) {
                 fillHTMLForm();
             }

             public void keyReleased(KeyEvent e) {
                 fillHTMLForm();
             }

         });

         ebiModule.guiRenderer.getTextfield("quantityText", "productInsertDialog").addKeyListener(new KeyListener() {
             public void keyTyped(KeyEvent e) {
             }

             public void keyPressed(KeyEvent e) {
                 fillHTMLForm();
             }

             public void keyReleased(KeyEvent e) {
                 fillHTMLForm();
             }

         });

        
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html>");
        buffer.append("<head><title></title></head>");
        buffer.append("<body bgcolor=#ffffff>");
        buffer.append("<table border=0 width=100%><tr><td bgcolor=#ebebeb colspan=2><b>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT") + "</b></td></tr>");
        buffer.append("</table></body>");
        ebiModule.guiRenderer.getEditor("productText","productInsertDialog").setText(buffer.toString());
        ebiModule.guiRenderer.showGUI();

    }

    /**
        *  Save product for specified module
        **/

    public void savePosistion(){
         if (isOffer) {
            saveOfferPosition();
         }
         if(isOrder){
            saveOrderPosition();
         }
         if (isCampaign) {
            saveCampaignPosition();
         }
         if(isService){
            saveServicePosition();
         }
         if(isProsol){
            saveProsolPosition();
         }
         if(isInvoice){
            saveInvoicePosition();
         }
    }



    private void saveOfferPosition() {
        if (!validateInput()) {
            return;
        }
        offerPosition.setCompanyoffer(this.offer);
        offerPosition.setQuantity(Long.parseLong(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText()));
        offerPosition.setDeduction(ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText());

        try{
            if(!"".equals(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText())){
              offerPosition.setNetamount(offerPosition.getNetamount());
            }
        }catch(NumberFormatException ex){}

        this.poList.add(offerPosition);
        this.offer.getCompanyofferpositionses().add(offerPosition);
        ebiModule.getOfferPane().showProduct();
        resetFields();
    }

    private void saveCampaignPosition() {
        if (!validateInput()) {
            return;
        }
        campaignPosition.setCrmcampaign(this.campaign);
        campaignPosition.setQuantity(Long.parseLong(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText()));
        campaignPosition.setDeduction(ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText());

        try{
            if(!"".equals(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText())){
              campaignPosition.setNetamount(campaignPosition.getNetamount());
            }
        }catch(NumberFormatException ex){}

        this.campProList.add(campaignPosition);
        this.campaign.getCrmcampaignpositions().add(campaignPosition);
        ebiModule.getEBICRMCampaign().showProduct();
        resetFields();
    }

    private void saveInvoicePosition() {
        if (!validateInput()) {
            return;
        }
        invoicePosition.setCrminvoice(this.invoice);
        invoicePosition.setQuantity(Long.parseLong(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText()));
        invoicePosition.setDeduction(ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText());

        try{
            if(!"".equals(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText())){
              invoicePosition.setNetamount(invoicePosition.getNetamount());
            }
        }catch(NumberFormatException ex){}

        this.invoice.getCrminvoicepositions().add(invoicePosition);
        ebiModule.getInvoicePane().showProduct();
        resetFields();
    }

    private void saveServicePosition() {
        if (!validateInput()) {
            return;
        }
        servicePosition.setCompanyservice(this.service);
        servicePosition.setQuantity(Long.parseLong(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText()));
        servicePosition.setDeduction(ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText());

         try{
            if(!"".equals(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText())){
              servicePosition.setNetamount(servicePosition.getNetamount());
            }
        }catch(NumberFormatException ex){}

        this.serviceProList.add(servicePosition);
        this.service.getCompanyservicepositionses().add(servicePosition);
        ebiModule.getServicePane().showProduct();
        resetFields();
    }

    private void saveProsolPosition() {

        prosolPosition.setCrmproblemsolutions(this.prosol);
        prosolPosition.setNetamount(prosolPosition.getNetamount());
        this.prosolProList.add(prosolPosition);
        this.prosol.getCrmproblemsolpositions().add(prosolPosition);
        ebiModule.getProsolPane().showProduct();
        resetFields();
    }

    private void saveOrderPosition() {
        if (!validateInput()) {
            return;
        }
        orderPosition.setCompanyorder(this.order);
        orderPosition.setQuantity(Long.parseLong(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText()));
        orderPosition.setDeduction(ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText());
         try{
            if(!"".equals(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText())){
              orderPosition.setNetamount(orderPosition.getNetamount());
            }
        }catch(NumberFormatException ex){}

        this.porList.add(orderPosition);
        this.order.getCompanyorderpositionses().add(orderPosition);
        ebiModule.getOrderPane().showProduct();
        resetFields();
    }

    private boolean validateInput() {

        try {
            Integer.parseInt(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText().replace(',', '.'));
        } catch (NumberFormatException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_INSERT_VALID_NUMBER")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }


    private void resetFields() {
        ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").setText("");
        ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").setText("");

        if (!isOrder) {
            offerPosition = new Companyofferpositions();
        }

        if (isOrder) {
            orderPosition = new Companyorderpositions();
        }

        if (isCampaign) {
            campaignPosition = new Crmcampaignposition();
        }

        if(isService){
            servicePosition = new Companyservicepositions(); 
        }

        if(isProsol){
            prosolPosition = new Crmproblemsolposition();
        }

        if(isInvoice){
            invoicePosition = new Crminvoiceposition();
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append("<body>");
        buffer.append("<table style=\"font-family: Verdana, serif;color:#000;font-size: 10px; border: solid 1px #a0f0ff;\" border=0 width=100%><tr><td bgcolor=#CCCCCC colspan=2><b>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT") + "</b></td></tr>");
        buffer.append("</table></body>");
        ebiModule.guiRenderer.getTextfield("productNrText","productInsertDialog").requestFocus();
        ebiModule.guiRenderer.getEditor("productText","productInsertDialog").setText(buffer.toString());
        ebiModule.guiRenderer.getButton("applyButton","productInsertDialog").setEnabled(false);
        ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").setText("");
        ebiModule.guiRenderer.getTextarea("descriptionText","productInsertDialog").setText("");
    }



    public void fillHTMLForm() {
        StringBuffer buffer = new StringBuffer();
        String name ="";
        String description ="";
        String productNr = "";

        setQuantityScale();

        buffer.append("<table style=\"font-family: Verdana, serif;color:#000;font-size: 10px; border: solid 1px #a0f0ff;\" border=0 width=100%><tr><td bgcolor=#a0f0ff style=\"border: solid 1px #a0f0ff;\" colspan=2><b>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT") + "</b></td></tr>");
        if (isOrder && orderPosition.getProductnr() != null) {
        	
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + orderPosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + orderPosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + orderPosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(orderPosition.getType()) ? "" : orderPosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(orderPosition.getTaxtype()) ? "" : orderPosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(orderPosition.getNetamount(),ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText(),ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText())) + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + orderPosition.getDescription() + "</td></tr>");

            productNr = orderPosition.getProductnr();
            name = orderPosition.getProductname();
            description = orderPosition.getDescription();


        } else if (isOffer && offerPosition.getProductnr() != null) {
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + offerPosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + offerPosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + offerPosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(offerPosition.getType()) ? "" : offerPosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(offerPosition.getTaxtype()) ? "" : offerPosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(offerPosition.getNetamount(),ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText(),ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText())) + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + offerPosition.getDescription() + "</td></tr>");

            productNr = offerPosition.getProductnr();
            name = offerPosition.getProductname();
            description = offerPosition.getDescription();
            
        } else if (isCampaign && campaignPosition.getProductnr() != null) {
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + campaignPosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + campaignPosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + campaignPosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(campaignPosition.getType()) ? "" : campaignPosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(campaignPosition.getTaxtype()) ? "" : campaignPosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(campaignPosition.getNetamount(),ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText(),ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText())) + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + campaignPosition.getDescription() + "</td></tr>");

            productNr = campaignPosition.getProductnr();
            name = campaignPosition.getProductname();
            description = campaignPosition.getDescription();

        } else if (isService && servicePosition.getProductnr() != null) {
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + servicePosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + servicePosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + servicePosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(servicePosition.getType()) ? "" : servicePosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(servicePosition.getTaxtype()) ? "" : servicePosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(servicePosition.getNetamount(),ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText(),ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText())) + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + servicePosition.getDescription() + "</td></tr>");

            productNr = servicePosition.getProductnr();
            name = servicePosition.getProductname();
            description = servicePosition.getDescription();

        } else if (isProsol && prosolPosition.getProductnr() != null) {
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + prosolPosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + prosolPosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + prosolPosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(prosolPosition.getType()) ? "" : prosolPosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(prosolPosition.getTaxtype()) ? "" : prosolPosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(prosolPosition.getNetamount(),"1",ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText())) + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + prosolPosition.getDescription() + "</td></tr>");

            productNr = prosolPosition.getProductnr();
            name = prosolPosition.getProductname();
            description = prosolPosition.getDescription();

        } else if (isInvoice && invoicePosition.getProductnr() != null) {
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_PRODUCT_NR") + "</td><td bgcolor='#ebebeb' >" + invoicePosition.getProductnr() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_NAME") + "</td><td bgcolor='#eeeeee'>" + invoicePosition.getProductname() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_CATEGORY") + "</td><td bgcolor='#ebebeb'>" + invoicePosition.getCategory() + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_TYPE") + "</td><td bgcolor='#eeeeee'>" + ("null".equals(invoicePosition.getType()) ? "" : invoicePosition.getType()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#ebebeb>" + EBIPGFactory.getLANG("EBI_LANG_TAX_TYPE") + "</td><td bgcolor='#ebebeb'>" + ("null".equals(invoicePosition.getTaxtype()) ? "" : invoicePosition.getTaxtype()) + "</td></tr>");
            buffer.append("<tr><td width=5% bgcolor=#eeeeee>" + EBIPGFactory.getLANG("EBI_LANG_SALE_PRICE") + "</td><td bgcolor='#eeeeee'>" + currency.format(ebiModule.dynMethod.calculatePreTaxPrice(invoicePosition.getNetamount(),ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText(),ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").getText()))  + "</td></tr>");
            buffer.append("<tr><td  bgcolor=#a0f0ff colspan='2'><b>" + EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION") + "</b></td></tr>");
            buffer.append("<tr><td  bgcolor=#eeeeee colspan='2'>" + invoicePosition.getDescription() + "</td></tr>");

            productNr = invoicePosition.getProductnr();
            name = invoicePosition.getProductname();
            description = invoicePosition.getDescription();
        }

        buffer.append("</table>");
        ebiModule.guiRenderer.getEditor("productText","productInsertDialog").setText(buffer.toString());
        ebiModule.guiRenderer.getTextfield("productNrText", "productInsertDialog").setText(productNr);
        ebiModule.guiRenderer.getTextfield("nameText","productInsertDialog").setText(name);
        ebiModule.guiRenderer.getTextarea("descriptionText", "productInsertDialog").setText(description);

    }
    
    
    public void setQuantityScale(){
    	
        ResultSet set = null;
        try {
           
            PreparedStatement pst = ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT * FROM CRMPRODUCTDIMENSION WHERE PRODUCTID=? AND NAME LIKE ? ORDER BY VALUE DESC ");
            
            pst.setInt(1,this.productID);
            pst.setString(2, EBIPGFactory.getLANG("EBI_LANG_DEDUCTION"));
      
            set = ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(pst);
            
            int count = Integer.parseInt(ebiModule.guiRenderer.getTextfield("quantityText","productInsertDialog").getText());
            
            set.last();
            if (set.getRow() > 0) {
                set.beforeFirst();
                while (set.next()) {
                	
                   if(set.getString("VALUE") != null || !"".equals(set.getString("VALUE"))){	
                	
                	   String [] splt = set.getString("VALUE").split("-"); 
                	   if(splt.length <= 1){
                		   splt = set.getString("VALUE").split(" "); 
                		   if(splt.length <= 0){
                			   return;
                		   }
                	   }
                	   
                	   if(count >= Integer.parseInt(splt[0]) && count <= Integer.parseInt(splt[1])){
                		   ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").setText(splt[2].substring(0, splt[2].length()-1)); 
                	   }else if(count < Integer.parseInt(splt[0]) && count <= Integer.parseInt(splt[1])){
                		   ebiModule.guiRenderer.getTextfield("deductionText","productInsertDialog").setText(""); 
                	   }
                	
                   }
                }
            }
            
        }catch(Exception ex){
        	return;
        }
    	
    	
    }
    
} 