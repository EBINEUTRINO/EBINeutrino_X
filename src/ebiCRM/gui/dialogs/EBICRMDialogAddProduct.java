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
    private Crminvoice invoice = null;
    private boolean isProsol = false;
    private boolean isInvoice = false;
    private EBICRMModule ebiModule = null;
    private Crminvoiceposition invoicePosition = null;
    private NumberFormat currency = null;
    public int productID =0;


    public EBICRMDialogAddProduct(Crminvoice invoice, EBICRMModule module) {
            invoicePosition = new Crminvoiceposition();
            ebiModule = module;

            ebiModule.guiRenderer.loadGUI("CRMDialog/productInsertDialog.xml");

            this.invoice = invoice;

            isInvoice = true;
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
        saveInvoicePosition();
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
        if (isInvoice && invoicePosition.getProductnr() != null) {
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