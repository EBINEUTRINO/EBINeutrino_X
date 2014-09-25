package ebiCRM.data.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import ebiNeutrinoSDK.model.hibernate.Crmproductdimension;
import org.hibernate.HibernateException;
import org.hibernate.Query;

import ebiCRM.gui.panels.EBICRMCashRegisterStack;
import ebiCRM.utils.EBICRMIProduct;
import ebiNeutrino.core.GUIRenderer.EBIButton;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIJTextFieldNumeric;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.model.hibernate.Crmproduct;
import ebiNeutrinoSDK.utils.EBIAbstractTableModel;
import ebiNeutrinoSDK.utils.EBIConstant;


public class EBIDataControlCashRegister {

    public EBICRMCashRegisterStack cashRegister = null;
    public Crminvoice invoice = null;
    private NumberFormat cashFormat = null;
    private boolean isEnter = false;
    private String selectedProductNr = "";
    private boolean isQuantityFocus = true;

    public EBIDataControlCashRegister(EBICRMCashRegisterStack creg){
       cashRegister = creg;
       invoice = new Crminvoice();

       cashFormat=NumberFormat.getCurrencyInstance();
       cashFormat.setMinimumFractionDigits(2);
       cashFormat.setMaximumFractionDigits(3);
    }


    public boolean dataStore(boolean isEdit){
         try{
            cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();
            if (isEdit == false) {
                invoice.setCreateddate(new Date());
                invoice.setCreatedfrom(cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").getCreatedFrom());

                Object[] obj = cashRegister.ebiModule.dynMethod.getInternNumber("Cash",true);
                invoice.setInvoicenr(Integer.parseInt(obj[0].toString()));
                invoice.setBeginchar(obj[1].toString());
                invoice.setDate(new Date());

                invoice.setStatus("Cash");
                invoice.setCategory("Cash");
            } else {
                invoice.setChangeddate(new Date());
                invoice.setChangedfrom(EBIPGFactory.ebiUser);
            }

           if(cashRegister.ebiModule.guiRenderer.getComboBox("payStatusText","CashRegister").getSelectedItem() != null){
               invoice.setStatus(cashRegister.ebiModule.guiRenderer.getComboBox("payStatusText","CashRegister").getSelectedItem().toString());
           }

           invoice.setName(cashRegister.ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText());

             // Position save
             if (!invoice.getCrminvoicepositions().isEmpty()) {
                Iterator iter = invoice.getCrminvoicepositions().iterator();
                while(iter.hasNext()){
                   Crminvoiceposition pos = (Crminvoiceposition)iter.next();
                   pos.setCrminvoice(invoice);
                   if(pos.getPositionid() < 0){ pos.setPositionid(null);}
                   cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").saveOrUpdate(pos);
                }
             }

             cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").saveOrUpdate(invoice);
             cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").commit();

             EBIPGFactory.properties.setValue("CASH_NAME",cashRegister.ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").getText());
             EBIPGFactory.properties.saveProperties();
             dataNew(true);
         }catch (Exception ex){}
        return true;
    }

    public void dataNew(boolean reload){
      try{
        if(!"".equals(EBIPGFactory.properties.getValue("CASH_NAME"))){

             cashRegister.ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").setText(EBIPGFactory.properties.getValue("CASH_NAME"));

        }

        if(reload){
            invoice = new Crminvoice();
            cashRegister.initialize();
        }
        cashRegister.ebiModule.guiRenderer.getComboBox("payStatusText","CashRegister").setSelectedIndex(0);
        this.cashRegister.isEdit = false;
        cashRegister.counter = 0;
        cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setID(-1);
        dataShow(cashRegister.ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());
        this.dataShowProduct();
      }catch(Exception ex){}
    }

    public void dataEdit(int id){
        dataNew(true);

        try {

            cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();

            Query query = cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

            if (iter.hasNext()) {

                invoice = (Crminvoice) iter.next();
                cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").refresh(invoice);
                cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setID(invoice.getInvoiceid());

                cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setCreatedDate(cashRegister.ebiModule.ebiPGFactory.getDateToString(invoice.getCreateddate() == null ? new Date() : invoice.getCreateddate() ));
                cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setCreatedFrom(invoice.getCreatedfrom() == null ? EBIPGFactory.ebiUser : invoice.getCreatedfrom());

                if (invoice.getChangeddate() != null) {
                    cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setChangedDate(cashRegister.ebiModule.ebiPGFactory.getDateToString(invoice.getChangeddate()));
                    cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setChangedFrom(invoice.getChangedfrom());
                } else {
                    cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setChangedDate("");
                    cashRegister.ebiModule.guiRenderer.getVisualPanel("CashRegister").setChangedFrom("");
                }

                if(invoice.getStatus() != null){
                    cashRegister.ebiModule.guiRenderer.getComboBox("payStatusText","CashRegister").setSelectedItem(invoice.getStatus());
                }

                 cashRegister.ebiModule.guiRenderer.getTextfield("comboNameTxt","CashRegister").setText(invoice.getName());

                 this.dataShowProduct();
                 calculateTotalAmount(invoice);
                 
                 cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").
 				 changeSelection(cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").
 						convertRowIndexToView(cashRegister.ebiModule.dynMethod.
 								getIdIndexFormArrayInATable(((EBIAbstractTableModel) cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getModel()).data, 4, id)),0,false,false);
                 
            }else{
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_RECORD_NOT_FOUND")).Show(EBIMessage.INFO_MESSAGE);
            }
          cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").commit();
        } catch (HibernateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cashRegister.isEdit=true;
    }
    
    public void loadProduct(int id){

            try {

                cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();

                Query query = cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").createQuery(
                        "from Crminvoice where invoiceId=? ").setInteger(0, id);

                Iterator iter =  query.iterate();
                
                if (iter.hasNext()) {
                	Crminvoice invoice = (Crminvoice) iter.next();
                	cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").refresh(invoice);
                	calculateTotalAmount(invoice);
                    
                }
                cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").commit();
            }catch(Exception ex){
            	ex.printStackTrace();
            }
    }

    public void dataDelete(int id){

        Query query;

        try {

            
            query = cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").createQuery(
                    "from Crminvoice where invoiceId=? ").setInteger(0, id);

            Iterator iter =  query.iterate();

                if (iter.hasNext()) {

                    Crminvoice inv = (Crminvoice) iter.next();

                    if (inv.getInvoiceid() == id) {
                        try {
                        	cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();
                            cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").delete(inv);
                            cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").commit();
                        } catch (HibernateException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                }
            dataNew(true);
            dataShow(cashRegister.ebiModule.guiRenderer.getTimepicker("cashDate","CashRegister").getDate());
        }catch(Exception ex){
           ex.printStackTrace();
        }

    }

    public void deleteProduct(int id){
      try{
           Iterator iter = invoice.getCrminvoicepositions().iterator();
           while (iter.hasNext()) {

                Crminvoiceposition invpro = (Crminvoiceposition) iter.next();

                if (invpro.getPositionid() == id) {
                  if(id >= 0){
                    try {
                        cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").begin();
                        cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").delete(invpro);
                        cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("CASH_SESSION").commit();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                  }

                  invoice.getCrminvoicepositions().remove(invpro);

                  this.dataShowProduct();
                  break;
                }
           }
      }catch(Exception ex){}
    }

    public void insertProduct(String id, boolean recursion){

      Query query;

        try {
            String stx ="";

            if(!recursion){
                stx ="left join pro.crmproductdimensions as dims where pro.productnr like ? or pro.productname like ? or dims.value like ? ";
            }else{
                stx ="where pro.productid=? ";
            }

            query = cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").createQuery(
                    "from Crmproduct as pro  "+stx);

            if(recursion){
                query.setString(0, id);
            }else{
                query.setString(0, id);
                query.setString(1, id);
                query.setString(2, id);
            }

               Iterator iter =  query.iterate();
               if(query.list().size() >1){   // Recursion if product greater then 1 show selection dialog

                  String[] values = new String[query.list().size()];
                  int i =0;
                  while(iter.hasNext()){
                      Object[] object = (Object[]) iter.next();
                      Crmproduct crmproduct = (Crmproduct)object[0];
                      Crmproductdimension dim = (Crmproductdimension)object[1];

                      values[i] = crmproduct.getProductid() +"-";
                      values[i] += crmproduct.getProductnr() == null ? "" : crmproduct.getProductnr()+" | ";
                      values[i] +=crmproduct.getProductname() == null ? "" : crmproduct.getProductname();
                      values[i] +=dim == null ? "" : " | "+dim.getValue();
                      i++;
                  }
                   
                  showAvailableProducts(values);
                  return;
               }

               if (iter.hasNext()) {

                    Crmproduct pro = null;

                    if(recursion){
                      pro = (Crmproduct)iter.next();
                    }else{
                        Object object[] = (Object[])iter.next();
                        pro = (Crmproduct)object[0];
                    }
                    cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").refresh(pro);
                    showQuantityDialog(pro);

               }
        }catch(Exception ex){
           ex.printStackTrace();
        }
    }

    private void showAvailableProducts(String[] query) {
        try{
            selectedProductNr = "";

            final EBIDialogExt win = new EBIDialogExt(cashRegister.ebiModule.ebiPGFactory.getMainFrame());
            win.setName("ShowProductDialog");
            win.setClosable(false);
            win.storeLocation(true);
            win.storeSize(true);
            //win.setModal(true);
            win.setFocusable(true);
            win.setResizable(true);
            win.setSize(500, 250);

            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = win.getSize();
            win.setLocation(((d.width - frameSize.width) / 2)+40, (((d.height-150) - frameSize.height) / 2)-50);

            final DefaultListModel  listModel = new DefaultListModel();
            final JList list = new JList(listModel);
            list.setFocusable(true);
            
            list.setFont(new Font("SansSerif", Font.BOLD, 20));
            JScrollPane sp = new JScrollPane();
            sp.setViewportView(list);
            
            Action closeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    isEnter = false;
                    selectedProductNr = "";
                    win.setVisible(false);
                }
             };


             Action enterAction = new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        isEnter = true;
                        win.setVisible(false);
                        selectedProductNr = list.getSelectedValue().toString().split("-")[0];
                        insertProduct(selectedProductNr,true);
                    }
             };

            InputMap inputMap = ((JPanel)win.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

            inputMap.put(KeyStroke.getKeyStroke("ENTER"), "CLOSE");
            ((JPanel)win.getContentPane()).getActionMap().put("CLOSE", enterAction);

            inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "CLOSE1");
            ((JPanel)win.getContentPane()).getActionMap().put("CLOSE1", closeAction);

            win.getContentPane().setLayout(new BorderLayout());


           /* Action moveUp = new AbstractAction() {
                 public void actionPerformed(ActionEvent e) {
                      if(list.getSelectedIndex()-1 <= listModel.size()){
                          list.setSelectedIndex(list.getSelectedIndex()-1);
                      }
                 }
            };

             Action moveDown = new AbstractAction() {
                 public void actionPerformed(ActionEvent e) {
                     if(list.getSelectedIndex()+1 <= listModel.size()){
                          list.setSelectedIndex(list.getSelectedIndex()+1);
                     }
                 }
             };


            inputMap.put(KeyStroke.getKeyStroke("UP"), "MOVEUP");
            ((JPanel)win.getContentPane()).getActionMap().put("MOVEUP", moveUp);

            inputMap.put(KeyStroke.getKeyStroke("DOWN"), "MOVEDOWN");
            ((JPanel)win.getContentPane()).getActionMap().put("MOVEDOWN", moveDown);*/

            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                   win.setVisible(false);
                   selectedProductNr = list.getSelectedValue().toString().split("-")[0];
                   insertProduct(selectedProductNr,true);
                }
            });

            for(int i =0; i<query.length; i++){
                  listModel.addElement(query[i]);
            }

           win.getContentPane().add(sp, BorderLayout.CENTER);
           list.setSelectedIndex(0);
	           win.addComponentListener(new ComponentListener() {
				
				@Override
				public void componentShown(ComponentEvent e) {
					list.requestFocusInWindow();
			        list.grabFocus();
					
				}
				
				@Override
				public void componentResized(ComponentEvent e) {}
				
				@Override
				public void componentMoved(ComponentEvent e) {}
				
				@Override
				public void componentHidden(ComponentEvent e) {}
			});
	           
           win.setVisible(true);
        }catch (Exception ex){}
    }

    private void showQuantityDialog(final Crmproduct pro) {
       try{
         final EBIDialogExt win = new EBIDialogExt(cashRegister.ebiModule.ebiPGFactory.getMainFrame());
         win.setContentPane(new JPanel());
         win.getContentPane().setBackground(EBIPGFactory.systemColor);
         final JTextField deduction = new JTextField();
         final JTextField txtF = new JTextField();
         final JCheckBox box = new JCheckBox();
         
         win.setName("SetQuantityDialog");
         win.setClosable(false);
         //win.setModal(true);
         win.storeLocation(true);
         win.storeSize(true);
         win.getContentPane().setLayout(null);
         win.setFocusable(true);
         win.setSize(290, 170);

         Action closeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    isEnter = false;
                    win.setVisible(false);
                }
         };


         final Action enterAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    isEnter = true;
                    try {

                        Crminvoiceposition ipro = new Crminvoiceposition();
                        ipro.setCrminvoice(invoice);
                        ipro.setCreateddate(new Date());
                        ipro.setCreatedfrom(EBIPGFactory.ebiUser);
                        ipro.setProductname(pro.getProductname());
                        ipro.setProductnr(pro.getProductnr());
                        ipro.setCategory(pro.getCategory());
                        ipro.setDescription(pro.getDescription());
                        ipro.setNetamount(pro.getSaleprice());
                        ipro.setPretax(pro.getPretax());
                        ipro.setTaxtype(pro.getTaxtype());
                        ipro.setType(pro.getType());
                        ipro.setDeduction("0");

                        int[] res = new int[]{Integer.parseInt(txtF.getText()), box.isSelected() ? Integer.parseInt(deduction.getText()) : 0};
                        if(!isEnter){
                            return;
                        }
                        
                        if(res[0] == 0){
                            ipro.setQuantity(1L);
                        }else{
                            ipro.setQuantity((long)res[0]);
                        }

                        if(res[1] > 0){
                        	ipro.setDeduction(""+res[1]);
                        }
                        
                        invoice.getCrminvoicepositions().add(ipro);
                        dataShowProduct();

                    } catch (HibernateException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    win.setVisible(false);
                }
         };

        InputMap inputMap = win.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "ENTER");
        win.getRootPane().getActionMap().put("ENTER", enterAction);

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "CLOSE1");
        win.getRootPane().getActionMap().put("CLOSE1", closeAction);

        JLabel qname = new JLabel(EBIPGFactory.getLANG("EBI_LANG_QUANTITY"));
        qname.setBounds(10, 23, 120, 20);
        win.add(qname, null);
        
        
        txtF.setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.NUMERIC_MINUS));
        txtF.setText("1");
        txtF.setSize(120,23);
        txtF.setLocation(10,45);
        txtF.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				isQuantityFocus = true;
				
			}
		});
        win.add(txtF,null);

        deduction.setDocument(new EBIJTextFieldNumeric(EBIJTextFieldNumeric.NUMERIC));
        deduction.setVisible(false);
        deduction.setBounds(10, 100, 120, 23);
        deduction.addFocusListener(new FocusListener() {	
			@Override
			public void focusLost(FocusEvent e) {}
			
			@Override
			public void focusGained(FocusEvent e) {
				isQuantityFocus=false;
				
			}
		});
        win.add(deduction,null);
        
        box.setText(EBIPGFactory.getLANG("EBI_LANG_DEDUCTION"));
        box.setBounds(10, 75, 120, 20);
        box.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				deduction.setVisible(((JCheckBox)e.getSource()).isSelected());
			}
		});
        win.add(box,null);
       
        ActionListener actB = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(((JButton)(e.getSource())).getName().equals("moveUp")){
	                  if("".equals(txtF.getText()) && isQuantityFocus ){
	                	  txtF.setText(""+1);
	                  }else if(isQuantityFocus){
	                	txtF.setText(""+(Integer.parseInt(txtF.getText())+1));
	                  }
	                  
	                  if("".equals(deduction.getText()) && !isQuantityFocus ){
	                	  deduction.setText(""+1); 
	                  }else if(!isQuantityFocus){
	                	  deduction.setText(""+(Integer.parseInt(deduction.getText())+1));  
	                  }
	                  
	                  
                }else{
                	if("".equals(txtF.getText()) && isQuantityFocus){
	                	  txtF.setText(""+1); 
	                }else if(isQuantityFocus){
	                	  txtF.setText(""+(Integer.parseInt(txtF.getText())-1));
	                }
                	
                	if("".equals(deduction.getText()) && !isQuantityFocus ){
	                	  deduction.setText(""+1); 
	                }else if(!isQuantityFocus){
	                	  deduction.setText(""+(Integer.parseInt(deduction.getText())-1));  
	                }
                }
            }
        };


        ActionListener actE = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 isEnter = true;
                 enterAction.actionPerformed(e);
                 win.setVisible(false);
            }
        };

        ActionListener actD = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                  isEnter = false;
                  win.setVisible(false);
            }
        };


        EBIButton bntUp = new EBIButton();
        bntUp.setIcon(EBIConstant.ICON_UP);
        bntUp.setName("moveUp");
        bntUp.setFocusable(false);
        bntUp.setSize(45,40);
        bntUp.setLocation(150,50);
        bntUp.addActionListener(actB);
        win.add(bntUp, null);

        EBIButton bntDown = new EBIButton();
        bntDown.setIcon(EBIConstant.ICON_DOWN);
        bntDown.setName("moveDown");
        bntDown.setFocusable(false);
        bntDown.setSize(45,40);
        bntDown.setLocation(200,50);
        bntDown.addActionListener(actB);
        win.add(bntDown, null);

        EBIButton bntEnter = new EBIButton();
        bntEnter.setIcon(EBIConstant.ICON_EDIT_DIALOG);
        bntEnter.setName("enter");
        bntEnter.setFocusable(false);
        bntEnter.setSize(45,40);
        bntEnter.setLocation(255,50);
        bntEnter.addActionListener(actE);
        win.add(bntEnter, null);

        EBIButton bntDelete = new EBIButton();
        bntDelete.setIcon(EBIConstant.ICON_CLOSE);
        bntDelete.setName("cancel");
        bntDelete.setFocusable(false);
        bntDelete.setSize(45,40);
        bntDelete.setLocation(315,50);
        bntDelete.addActionListener(actD);
        win.add(bntDelete, null);


         Action moveUp = new AbstractAction() {
             public void actionPerformed(ActionEvent e) {
            	 if("".equals(txtF.getText()) && isQuantityFocus ){
               	  	 txtF.setText(""+1);
                 }else if(isQuantityFocus){
                	 txtF.setText(""+(Integer.parseInt(txtF.getText())+1));
                 }
                 
                 if("".equals(deduction.getText()) && !isQuantityFocus ){
               	  	deduction.setText(""+1); 
                 }else if(!isQuantityFocus){
               	  	deduction.setText(""+(Integer.parseInt(deduction.getText())+1));  
                 }
             }
         };

         Action moveDown = new AbstractAction() {
             public void actionPerformed(ActionEvent e) {
            	 if("".equals(txtF.getText()) && isQuantityFocus){
               	  txtF.setText(""+1); 
            	 }else if(isQuantityFocus){
               	  txtF.setText(""+(Integer.parseInt(txtF.getText())-1));
            	 }
           	
            	 if("".equals(deduction.getText()) && !isQuantityFocus ){
               	  	deduction.setText(""+1); 
            	 }else if(!isQuantityFocus){
               	  	deduction.setText(""+(Integer.parseInt(deduction.getText())-1));  
            	 }
             }
         };

        inputMap.put(KeyStroke.getKeyStroke("UP"), "MOVEUP");
        win.getRootPane().getActionMap().put("MOVEUP", moveUp);

        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "MOVEDOWN");
        win.getRootPane().getActionMap().put("MOVEDOWN", moveDown);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = win.getSize();
        win.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
   
        win.setVisible(true);
        txtF.grabFocus();
       }catch (Exception ex){}
    }

    public void calculateTotalAmount(Crminvoice invc){
       try{
        double amount=0.0;
        double deduction=0.0;
        double tax=0.0;
        
        if(invc.getCrminvoicepositions().size() > 0){
        	
	        Iterator iter = invc.getCrminvoicepositions().iterator();
	        Hashtable<String,Object> productTable = new Hashtable<String,Object>();
	        
	        Hashtable<String,Double> taxTable = new Hashtable<String,Double>();
	        while(iter.hasNext()){
	        	
	            Crminvoiceposition inv = (Crminvoiceposition)iter.next();
	            String de = "";
	            
	            amount += inv.getNetamount() * inv.getQuantity();
	
	            if(!"".equals(inv.getDeduction())){
	                deduction += (((inv.getNetamount() * inv.getQuantity()) * Integer.parseInt(inv.getDeduction())) / 100);
	                de = inv.getDeduction().equals("0") ? "" : inv.getDeduction()+"%   - ";
	            }
	
	            double vtax = getTaxVal(inv.getTaxtype());
	            
	            if(vtax != 0.0){
	            	
	                double toNet = (inv.getNetamount() * inv.getQuantity());
	                double todeduct =  inv.getDeduction().equals("") ? 0.0 : ((toNet * Integer.parseInt(inv.getDeduction())) / 100);
	                
	                
	                if(taxTable.contains(inv.getTaxtype())){
		            	taxTable.put(inv.getTaxtype(), taxTable.get(inv.getTaxtype())+(((toNet - todeduct)* vtax) / 100));
		            }else{
		            	taxTable.put(inv.getTaxtype(), (((toNet - todeduct)* vtax) / 100));
		            }
	                
	                tax  +=   (((toNet - todeduct)* vtax) / 100);
	            }
	          
	            EBICRMIProduct prOTable = new EBICRMIProduct();
	            prOTable.setCalculatedSPriceNet(cashFormat.format(cashRegister.ebiModule.dynMethod.
	            						calculatePreTaxPrice(inv.getNetamount(),String.valueOf(inv.getQuantity()),String.valueOf(inv.getDeduction()))));
	            
	            prOTable.setQuantity(inv.getQuantity());
	            prOTable.setCalculatedSDeduction(de);
	            
	            
	            productTable.put(inv.getProductname(),prOTable);
	            
	        }
	
	        amount = amount - deduction;
	        deduction = deduction * (-1);
	       
	        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("productList",productTable);
	        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("taxTable",taxTable);
	        
	        if(iter.hasNext() == false){
		        StringBuffer cashHTML = new StringBuffer();
		        cashHTML.append("<table style='color:#ffffff; font-family: Courier;'><tr>");
		        cashHTML.append("<td><b>" + EBIPGFactory.getLANG("EBI_LANG_TOTAL_NETAMOUNT") + ":</b></td><td> " + cashFormat.format(amount) + "</td></tr><tr>");
		        if(deduction > 0.0){
		         cashHTML.append("<td><b>"+EBIPGFactory.getLANG("EBI_LANG_DEDUCTION")+":</b></td><td> "+cashFormat.format(deduction)+"</td></tr><tr>");
		        }
		        
		        Iterator itax = taxTable.keySet().iterator();
		        while(itax.hasNext()){
		        	String key = (String) itax.next();
		        	cashHTML.append("<td><b>"+key+":</b></td><td> "+cashFormat.format(taxTable.get(key))+"</td></tr><tr>");
		        }
		        cashHTML.append("<td><b></b></td><td><br></td></tr><tr>");
		        cashHTML.append("<td><b>"+EBIPGFactory.getLANG("EBI_LANG_TOTAL_GROSSAMOUNT")+":</b></td><td> "+(cashFormat.format(amount+tax))+"</td></tr></table>");
		        
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("valueEditCashR",cashHTML.toString());
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("totalAmountCashRValue",(amount+tax));
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("totalAmountCashNetValue",amount);
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("totaldDeduction",EBIPGFactory.getLANG("EBI_LANG_DEDUCTION")+": "+cashFormat.format(deduction));
		        
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("CashName",cashRegister.ebiModule.guiRenderer.getTextfield("comboNameTxt", "CashRegister").getText());
		        cashRegister.ebiModule.ebiPGFactory.globalVariable.put("totalTax",tax);
		        cashRegister.ebiModule.guiRenderer.getEditor("cashView","CashRegister").setText(cashHTML.toString());
	        }
	        
        }
       }catch (Exception ex){}
    }

    private double getTaxVal(String cat){
        double val = 0.0;
        
        ResultSet set = null;
        try{
        	cashRegister.ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(true);
        	PreparedStatement ps =cashRegister.ebiModule.ebiPGFactory.getIEBIDatabase().initPreparedStatement("SELECT NAME,TAXVALUE FROM COMPANYPRODUCTTAX WHERE NAME=?");
        	ps.setString(1, cat);
        	
            set = cashRegister.ebiModule.ebiPGFactory.getIEBIDatabase().executePreparedQuery(ps);
        	set.last();
        	if(set.getRow() > 0){
        		set.beforeFirst();
        		while(set.next()){
        			val = set.getDouble("TAXVALUE");
        			
        		}
        	}
  
        }catch(SQLException ex){
        	ex.printStackTrace();
        }finally{
        	try {
				set.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	cashRegister.ebiModule.ebiPGFactory.getIEBIDatabase().setAutoCommit(false);
        }
        
      return val;
    }

    public void dataShow(Date date){

        int srow = cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getSelectedRow();
        EBIAbstractTableModel model = (EBIAbstractTableModel) cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").getModel();

        String sName = "";
        if(srow > -1 && srow < model.data.length){
            sName = model.data[cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").convertRowIndexToModel(srow)][0].toString();
        }

        Query query;
        try {

            Calendar calendar1 = new GregorianCalendar();
            calendar1.setTime(date == null ? new Date() : date);
            calendar1.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH));
            calendar1.set(Calendar.HOUR,0);
            calendar1.set(Calendar.MINUTE,0);

            Calendar calendar2 = new GregorianCalendar();
            calendar2.setTime(date == null ? new Date() : date);
            calendar2.set(Calendar.DAY_OF_MONTH, calendar1.get(Calendar.DAY_OF_MONTH)+1);
            calendar2.set(Calendar.HOUR,0);
            calendar2.set(Calendar.MINUTE,0);

            query = cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").createQuery("from Crminvoice cm where cm.category=? and cm.date between ? and ? order by cm.createddate desc");

            query.setString(0,"Cash");
            query.setTimestamp(1, new Date(calendar1.getTime().getTime()));
            query.setTimestamp(2, new Date(calendar2.getTime().getTime()));

            if(query.list().size() > 0){

                    Iterator iter =  query.iterate();

                    model.data = new Object[query.list().size()][5];

                    int i = 0;
                    while (iter.hasNext()) {

                        Crminvoice inv = (Crminvoice) iter.next();
                        cashRegister.ebiModule.ebiPGFactory.hibernate.getHibernateSession("CASH_SESSION").refresh(inv);
                        try{
                            if((""+inv.getInvoicenr()).equals(sName)){       // Retrieve the row index to select after show
                                srow = cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").convertRowIndexToModel(i);
                            }
                        }catch(IndexOutOfBoundsException ex){ srow = 0;}

                        model.data[i][0] = inv.getBeginchar() +inv.getInvoicenr();
                        model.data[i][1] = inv.getName() == null ? "" : inv.getName();
                        model.data[i][2] = inv.getCategory() == null ? "" : inv.getCategory();
                        model.data[i][3] = inv.getDate() == null ? "" : cashRegister.ebiModule.ebiPGFactory.getDateToString(inv.getDate());
                        model.data[i][4] = inv.getInvoiceid();
                        i++;
                    }
              }else{
                model.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", ""}};
              }
            model.fireTableDataChanged();
        }catch(Exception ex){
            ex.printStackTrace();
        }

        cashRegister.ebiModule.guiRenderer.getTable("tableCashRegister","CashRegister").changeSelection(srow,0,false,false);

    }

    public void dataShowProduct() {
      try{
      if(this.invoice.getCrminvoicepositions().size() > 0){

            int i = 0;

            cashRegister.tabModProduct.data = new Object[this.invoice.getCrminvoicepositions().size()][9];

            Iterator itr =  invoice.getCrminvoicepositions().iterator();

            while (itr.hasNext()) {

                Crminvoiceposition obj = (Crminvoiceposition) itr.next();

                cashRegister.tabModProduct.data[i][0] = String.valueOf(obj.getQuantity());
                cashRegister.tabModProduct.data[i][1] = obj.getProductnr();
                cashRegister.tabModProduct.data[i][2] = obj.getProductname() == null ? "" : obj.getProductname();
                cashRegister.tabModProduct.data[i][3] = cashFormat.format(obj.getNetamount() == null ? "" : cashRegister.ebiModule.dynMethod.calculatePreTaxPrice(obj.getNetamount(),String.valueOf(obj.getQuantity()),String.valueOf(obj.getDeduction())));
                cashRegister.tabModProduct.data[i][4] = obj.getDeduction().equals("")  ? "" : obj.getDeduction()+"%";
                if(obj.getPositionid() == null || obj.getPositionid() < 0){ obj.setPositionid(((i + 1)*(-1)));}
                cashRegister.tabModProduct.data[i][5] = obj.getPositionid();
                i++;
            }
        }else{
            cashRegister.tabModProduct.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", ""}};
        }
        cashRegister.tabModProduct.fireTableDataChanged();
        
        calculateTotalAmount(this.invoice);
        
      }catch (Exception ex){}
    }


}



