package ebiCRM.gui.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hibernate.Query;
import org.jdesktop.swingx.sort.RowFilters;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import ebiCRM.EBICRMModule;
import ebiCRM.gui.dialogs.EBIDialogSearchCompany;
import ebiCRM.table.models.MyTableModelSummaryTab;
import ebiCRM.utils.AbstractTableKeyAction;
import ebiCRM.utils.JTableActionMaps;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.Companyactivities;
import ebiNeutrinoSDK.model.hibernate.Companyactivitytype;
import ebiNeutrinoSDK.model.hibernate.Companyoffer;
import ebiNeutrinoSDK.model.hibernate.Companyofferpositions;
import ebiNeutrinoSDK.model.hibernate.Companyofferstatus;
import ebiNeutrinoSDK.model.hibernate.Companyopportunity;
import ebiNeutrinoSDK.model.hibernate.Companyopportunitystatus;
import ebiNeutrinoSDK.model.hibernate.Companyorder;
import ebiNeutrinoSDK.model.hibernate.Companyorderpositions;
import ebiNeutrinoSDK.model.hibernate.Companyorderstatus;
import ebiNeutrinoSDK.model.hibernate.Companyservice;
import ebiNeutrinoSDK.model.hibernate.Companyservicepositions;
import ebiNeutrinoSDK.model.hibernate.Companyservicestatus;
import ebiNeutrinoSDK.model.hibernate.Crmcampaign;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignposition;
import ebiNeutrinoSDK.model.hibernate.Crmcampaignstatus;
import ebiNeutrinoSDK.model.hibernate.Crminvoice;
import ebiNeutrinoSDK.model.hibernate.Crminvoiceposition;
import ebiNeutrinoSDK.model.hibernate.Crminvoicestatus;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolposition;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolstatus;
import ebiNeutrinoSDK.model.hibernate.Crmproblemsolutions;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;


public class EBICRMSummaryPane {
    
    private EBICRMModule ebiModule = null;
    private MyTableModelSummaryTab tabModel = null;
    private int selectedSummaryRow = -1;
    private NumberFormat currency = null;

    public EBICRMSummaryPane(EBICRMModule mod) {
        ebiModule = mod;
        initializeAction();
        initialize();
        currency=NumberFormat.getCurrencyInstance();

        try {
            ebiModule.ebiPGFactory.hibernate.openHibernateSession("SUMMARY_SESSION");
        }catch (Exception e) {
            e.printStackTrace();
        }

        EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
        ebiModule.guiRenderer.getTextfield("companyText","Summary").setText(properties.getValue("EBIDASHBOARD_COMPANY"));
        ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").setSelectedItem(properties.getValue("EBIDASHBOARD_CATEGORY"));
        ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").setSelectedItem(properties.getValue("EBIDASHBOARD_TYPE"));
        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").setSelectedItem(properties.getValue("EBIDASHBOARD_STATUS"));
        ebiModule.guiRenderer.getTextfield("summaryNameText","Summary").setText(properties.getValue("EBIDASHBOARD_NAME"));

        if(!"".equals(properties.getValue("EBIDASHBOARD_CREATEDFROM")) && !"null".equals(properties.getValue("EBIDASHBOARD_CREATEDFROM"))){
            ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").setDate(ebiModule.ebiPGFactory.getStringToDate(properties.getValue("EBIDASHBOARD_CREATEDFROM")));
            ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getEditor().setText(properties.getValue("EBIDASHBOARD_CREATEDFROM"));
        }

        if(!"".equals(properties.getValue("EBIDASHBOARD_CREATEDTO")) && !"null".equals(properties.getValue("EBIDASHBOARD_CREATEDTO"))){
            ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").setDate(ebiModule.ebiPGFactory.getStringToDate(properties.getValue("EBIDASHBOARD_CREATEDTO")));
            ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").getEditor().setText(properties.getValue("EBIDASHBOARD_CREATEDTO"));
        }

        ebiModule.guiRenderer.getCheckBox("showForUser","Summary").setSelected("yes".equals(properties.getValue("EBIDASHBOARD_FORUSER")) ? true : false);

    }

    private void initializeAction(){

        tabModel = new MyTableModelSummaryTab();
        ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").setModel(new DefaultComboBoxModel(
                    new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"),
                EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_ALL"),
                EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"),
                EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES"),
                EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),
                EBIPGFactory.getLANG("EBI_LANG_C_ORDER"),
                EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),
                EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE"),
                EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN"),
                EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL"),

        }));

        ebiModule.guiRenderer.getLabel("filterTable","Summary").setHorizontalAlignment(SwingUtilities.RIGHT);
        ebiModule.guiRenderer.getTextfield("filterTableText","Summary").addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){}

            public void keyPressed(KeyEvent e){
                ebiModule.guiRenderer.getTable("companySummaryTable","Summary").setRowFilter(
                                        RowFilters.regexFilter("(?i)" + ebiModule.guiRenderer.getTextfield("filterTableText", "Summary").getText()));
            }
            public void keyReleased(KeyEvent e){
                ebiModule.guiRenderer.getTable("companySummaryTable","Summary").setRowFilter(
                                            RowFilters.regexFilter("(?i)"+ebiModule.guiRenderer.getTextfield("filterTableText","Summary").getText()));
            }
        });


        ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").setModel(new DefaultComboBoxModel(EBICRMCompanyPane.categories));
        //ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").updateUI();

        ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").addActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    setStateForModule();
                }
        });

        ebiModule.guiRenderer.getButton("searchSummary","Summary").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (!validateInput()) {
                        return;
                    }

                     EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
                     properties.setValue("EBIDASHBOARD_COMPANY", ebiModule.guiRenderer.getTextfield("companyText","Summary").getText());
                     properties.setValue("EBIDASHBOARD_CATEGORY", ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").getSelectedItem().toString());
                     properties.setValue("EBIDASHBOARD_TYPE", ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString());
                     properties.setValue("EBIDASHBOARD_STATUS", ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").getSelectedItem().toString());
                     properties.setValue("EBIDASHBOARD_NAME", ebiModule.guiRenderer.getTextfield("summaryNameText","Summary").getText());
                     properties.setValue("EBIDASHBOARD_CREATEDFROM", ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getEditor().getText());
                     properties.setValue("EBIDASHBOARD_CREATEDTO",ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").getEditor().getText());
                     properties.setValue("EBIDASHBOARD_FORUSER",ebiModule.guiRenderer.getCheckBox("showForUser","Summary").isSelected() ? "yes" : "no");
                     properties.saveProperties();
                     searchSummary();
                }
         });

        /*************************************************************************************/
        // TABLE SEARCH SUMMARY
        /*************************************************************************************/

          ebiModule.guiRenderer.getTable("companySummaryTable","Summary").setModel(tabModel);
          ebiModule.guiRenderer.getTable("companySummaryTable","Summary").setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
          ebiModule.guiRenderer.getTable("companySummaryTable","Summary").getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selectedSummaryRow = ebiModule.guiRenderer.getTable("companySummaryTable","Summary").convertRowIndexToModel(ebiModule.guiRenderer.getTable("companySummaryTable","Summary").getSelectedRow());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        ebiModule.guiRenderer.getButton("showSummary","Summary").setEnabled(false);
                    } else if (!tabModel.data[selectedSummaryRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        ebiModule.guiRenderer.getButton("showSummary","Summary").setEnabled(true);
                    }
                }
            });

            new JTableActionMaps(ebiModule.guiRenderer.getTable("companySummaryTable","Summary")).setTableAction(new AbstractTableKeyAction() {

                public void setDownKeyAction(int selRow) {
                    selectedSummaryRow = selRow;
                }

                public void setUpKeyAction(int selRow) {
                    selectedSummaryRow = selRow;
                }

                public void setEnterKeyAction(int selRow) {
                    selectedSummaryRow = selRow;


                    if (selectedSummaryRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedSummaryRow][0].toString())) {
                        return;
                    }

                    showSummaryObjectView();

                }
            });

            ebiModule.guiRenderer.getTable("companySummaryTable","Summary").addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    
                    if(ebiModule.guiRenderer.getTable("companySummaryTable","Summary").rowAtPoint(e.getPoint()) != -1){
                      
                    	selectedSummaryRow = ebiModule.guiRenderer.
                    							getTable("companySummaryTable","Summary").
                    									convertRowIndexToModel(ebiModule.guiRenderer.
                    																getTable("companySummaryTable","Summary").
                    																						rowAtPoint(e.getPoint()));
                      
                    }
                    
                    if (e.getClickCount() == 2) {

                        if (selectedSummaryRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                equals(tabModel.data[selectedSummaryRow][0].toString())) {
                            return;
                        }
                        showSummaryObjectView();

                    }
                }
            });

            ebiModule.guiRenderer.getButton("showSummary","Summary").setIcon(EBIConstant.ICON_EDIT);
            ebiModule.guiRenderer.getButton("showSummary","Summary").setEnabled(false);
            ebiModule.guiRenderer.getButton("showSummary","Summary").addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (selectedSummaryRow < 0 || EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                            equals(tabModel.data[selectedSummaryRow][0].toString())) {
                        return;
                    }

                    showSummaryObjectView();
                }
            });

         ebiModule.guiRenderer.getButton("seachCompName","Summary").setIcon(EBIConstant.ICON_SEARCH);
         ebiModule.guiRenderer.getButton("seachCompName","Summary").addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
                    new EBIDialogSearchCompany(ebiModule,false,true);
				}
		});
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    public void initialize() {

       ebiModule.guiRenderer.getPanel("productChart","Summary").repaint();

       ebiModule.guiRenderer.getTextfield("summaryNameText","Summary").setText("");

       if(ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").getItemCount() > 0){
         ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").removeAllItems();
       }

       ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));

       ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").setFormats(EBIPGFactory.DateFormat);
       ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getEditor().setText("");
       ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").setFormats(EBIPGFactory.DateFormat);
       ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").getEditor().setText("");

       ebiModule.guiRenderer.getPanel("productChart","Summary").addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) { searchSummary();}

            @Override
            public void componentMoved(ComponentEvent componentEvent) { }

            @Override
            public void componentShown(ComponentEvent componentEvent) {}

            @Override
            public void componentHidden(ComponentEvent componentEvent) {}
       });

    }

    public void setCompanyText(String text){
       ebiModule.guiRenderer.getTextfield("companyText","Summary").setText(text);
    }

    /**
     * Fill ComboBox status from the selected category
    */

    private void setStateForModule() {

        Query query;
        Iterator objectIterator;
        try {
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("SUMMARY_SESSION").begin();

            ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").removeAllItems();
            ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"));
            //Companyservice
            if (EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Companyopportunitystatus");
                objectIterator = query.iterate();

                while (objectIterator.hasNext()) {

                    Companyopportunitystatus companyOpportunitystage = (Companyopportunitystatus) objectIterator.next();
                    ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOpportunitystage);
                    ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(companyOpportunitystage.getName());
                }

            } else if (EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Companyactivitytype");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Companyactivitytype companyactivityType = (Companyactivitytype) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyactivityType);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(companyactivityType.getName());
                    }
                }

            }else if (EBIPGFactory.getLANG("EBI_LANG_C_SERVICE").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Companyservicestatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Companyservicestatus companyserviceStatus = (Companyservicestatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyserviceStatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(companyserviceStatus.getName());
                    }
                }

            } else if (EBIPGFactory.getLANG("EBI_LANG_C_OFFER").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Companyofferstatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Companyofferstatus companyofferStatus = (Companyofferstatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyofferStatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(companyofferStatus.getName());
                    }
                }

            } else if (EBIPGFactory.getLANG("EBI_LANG_C_ORDER").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Companyorderstatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Companyorderstatus companyOrderstatus = (Companyorderstatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOrderstatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(companyOrderstatus.getName());
                    }
                }
            } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Crmcampaignstatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Crmcampaignstatus campaignStatus = (Crmcampaignstatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(campaignStatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(campaignStatus.getName());
                    }
                }
            }else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Crmproblemsolstatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Crmproblemsolstatus prosolStatus = (Crmproblemsolstatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(prosolStatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(prosolStatus.getName());
                    }
                }
            }else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery("FROM Crminvoicestatus");
                objectIterator = query.iterate();

                if (query.list().size() > 0) {

                    while (objectIterator.hasNext()) {
                        Crminvoicestatus invoiceStatus = (Crminvoicestatus) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(invoiceStatus);
                        ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(invoiceStatus.getName());
                    }
                    ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").addItem(EBIPGFactory.getLANG("EBI_LANG_STORNO"));
                }
            }

        } catch (org.hibernate.HibernateException ex) {
            return;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Show the select item to the appropriate panel 
     * 
     */
    private void showSummaryObjectView() {
      try{
        ebiModule.guiRenderer.getVisualPanel("Summary").setCursor(new Cursor(Cursor.WAIT_CURSOR));
        if(!EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").equals(tabModel.data[selectedSummaryRow][0].toString())&&
            !EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").equals(tabModel.data[selectedSummaryRow][0].toString())&&
                !EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").equals(tabModel.data[selectedSummaryRow][0].toString()) &&
                !"".equals(tabModel.data[selectedSummaryRow][6].toString())){

            ebiModule.createUI(Integer.parseInt(tabModel.data[selectedSummaryRow][6].toString()),false);
        }

        if (EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY").equals(tabModel.data[selectedSummaryRow][0].toString())) {
            ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Opportunity");
            ebiModule.getOpportunityPane().editOpportunity(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        }else if (EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES").equals(tabModel.data[selectedSummaryRow][0].toString())) {
            ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Activity");
            ebiModule.getActivitiesPane().editActivity(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        }else if (EBIPGFactory.getLANG("EBI_LANG_C_SERVICE").equals(tabModel.data[selectedSummaryRow][0].toString())) {
            ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Service");
            ebiModule.getServicePane().editService(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        } else if (EBIPGFactory.getLANG("EBI_LANG_C_OFFER").equals(tabModel.data[selectedSummaryRow][0].toString())) {
            ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Offer");
            ebiModule.getOfferPane().editOffer(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        } else if (EBIPGFactory.getLANG("EBI_LANG_C_ORDER").equals(tabModel.data[selectedSummaryRow][0].toString())) {
            ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Order");
            ebiModule.getOrderPane().editOrder(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").equals(tabModel.data[selectedSummaryRow][0].toString())) {
           ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Campaign");
           ebiModule.getEBICRMCampaign().dataControlCampaign.dataEdit(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").equals(tabModel.data[selectedSummaryRow][0].toString())) {
           ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Prosol");
           ebiModule.getProsolPane().dataControlProsol.dataEdit(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        }else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").equals(tabModel.data[selectedSummaryRow][0].toString())) {
           ebiModule.ebiPGFactory.getIEBIContainerInstance().setSelectedExtension("Invoice");
           ebiModule.getInvoicePane().dataControlInvoice.dataEdit(Integer.parseInt(tabModel.data[selectedSummaryRow][7].toString()));

        }
        searchSummary();
      }catch(Exception e){e.printStackTrace();}finally{
        ebiModule.guiRenderer.getVisualPanel("Summary").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    }

    private boolean validateInput() {
        if (EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_C_ERROR_SELECT_TYPE")).Show(EBIMessage.INFO_MESSAGE);
            return false;
        }
        return true;
    }

    public void searchSummary() {

        Iterator objectIterator;
        Query query;
        JFreeChart chart;
        JFreeChart chartproduct;
        DefaultPieDataset data;

        SortedMap<String,Double> sumCash = new TreeMap<String,Double>();

        try {
            ebiModule.ebiPGFactory.hibernate.getHibernateTransaction("SUMMARY_SESSION").begin();
            ebiModule.guiRenderer.getVisualPanel("Summary").setCursor(new Cursor(Cursor.WAIT_CURSOR));

            if (EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Companyopportunity"));

                setParamToHQuery(query,false);
                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();
                
                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {


                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {

                        Companyopportunity companyOpportunity = (Companyopportunity) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOpportunity);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY");
                        tabModel.data[i][1] = companyOpportunity.getCompany() == null ? "" : companyOpportunity.getCompany().getName();
                        tabModel.data[i][2] = companyOpportunity.getName() == null ? "" : companyOpportunity.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getChangeddate());
                        tabModel.data[i][5] = companyOpportunity.getOpportunitystatus() == null ? "" : companyOpportunity.getOpportunitystatus();
                        tabModel.data[i][6] = companyOpportunity.getCompany() == null ? "" : companyOpportunity.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOpportunity.getOpportunityid();
                        data.setValue(companyOpportunity.getSalestage() == null ? "" : companyOpportunity.getSalestage(), query.list().size());
                        sumCash.put( companyOpportunity.getEvaluationstatus(),companyOpportunity.getOpportunityvalue());
                        dtpr.addValue(companyOpportunity.getOpportunityvalue(), companyOpportunity.getBudgetstatus()+" - :"+companyOpportunity.getProbability(), companyOpportunity.getEvaluationstatus());

                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_OPPORTUNITY")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);

                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();
                            ebiModule.guiRenderer.getPanel("productChart","Summary").repaint();
                        }
                    });


                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_SERVICE").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Companyservice"));

                setParamToHQuery(query,false);
                
                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();
                objectIterator = query.iterate();
                int i = 0;



                if (query.list().size() > 0) {
                    tabModel.data = new Object[query.list().size()][8];
                    
                    while (objectIterator.hasNext()) {

                        Companyservice companyService = (Companyservice) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyService);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_SERVICE");
                        tabModel.data[i][1] = companyService.getCompany() == null ? "" : companyService.getCompany().getName();
                        tabModel.data[i][2] = companyService.getName() == null ? "" : companyService.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyService.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyService.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyService.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyService.getChangeddate());
                        tabModel.data[i][5] = companyService.getStatus() == null ? "" : companyService.getStatus();
                        tabModel.data[i][6] = companyService.getCompany() == null ? "" : companyService.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyService.getServiceid();
                        data.setValue(companyService.getStatus() == null ? "" : companyService.getStatus(), query.list().size());

                        Iterator srit = companyService.getCompanyservicepositionses().iterator();

                        while(srit.hasNext()){

                            Companyservicepositions pos = (Companyservicepositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());

                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(),cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(),cash);
                            }
                            dtpr.addValue(cash, pos.getProductname()+" - ID: "+pos.getPositionid(), pos.getCategory());
                        }

                        i++;
                    }
                    
                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }
                    
                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);

                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());
                    
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }



            } else if (EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {

                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Companyactivities"));

                setParamToHQuery(query,false);
                data = new DefaultPieDataset();
                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();
                
                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {
                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {

                        Companyactivities companyActivity = (Companyactivities) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyActivity);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES");
                        tabModel.data[i][1] = companyActivity.getCompany() == null ? "" : companyActivity.getCompany().getName();
                        tabModel.data[i][2] = companyActivity.getActivityname() == null ? "" : companyActivity.getActivityname();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyActivity.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyActivity.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyActivity.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyActivity.getChangeddate());
                        tabModel.data[i][5] = companyActivity.getActivitystatus() == null ? "" : companyActivity.getActivitystatus();
                        tabModel.data[i][6] = companyActivity.getCompany() == null ? "" : companyActivity.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyActivity.getActivityid();
                        data.setValue(companyActivity.getActivitystatus() == null ? "" : companyActivity.getActivitystatus(), query.list().size());
                        i++;
                    }

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);

                    ChartPanel panel = new ChartPanel(chart);
                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }

            } else if (EBIPGFactory.getLANG("EBI_LANG_C_OFFER").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {


                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Companyoffer"));

                setParamToHQuery(query,false);
                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();


                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {


                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {

                        Companyoffer companyOffer = (Companyoffer) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOffer);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_OFFER");
                        tabModel.data[i][1] = companyOffer.getCompany() == null ? "" : companyOffer.getCompany().getName();
                        tabModel.data[i][2] = companyOffer.getName() == null ? "" : companyOffer.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOffer.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOffer.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOffer.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOffer.getChangeddate());
                        tabModel.data[i][5] = companyOffer.getStatus() == null ? "" : companyOffer.getStatus();
                        tabModel.data[i][6] = companyOffer.getCompany() == null ? "" : companyOffer.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOffer.getOfferid();
                        data.setValue(companyOffer.getStatus() == null ? "" : companyOffer.getStatus(), query.list().size());

                        Iterator srit = companyOffer.getCompanyofferpositionses().iterator();

                        while(srit.hasNext()){

                            Companyofferpositions pos = (Companyofferpositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());


                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(), cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(), cash);
                            }
                            dtpr.addValue(cash, pos.getCategory(), pos.getCategory());
                        }

                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_OFFER")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);

                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_ORDER").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {


                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Companyorder"));

                setParamToHQuery(query,false);

                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();
                
                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {

                    tabModel.data = new Object[query.list().size()][8];
                    
                    while (objectIterator.hasNext()) {
                        Companyorder companyOrder = (Companyorder) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOrder);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_ORDER");
                        tabModel.data[i][1] = companyOrder.getCompany() == null ? "" : companyOrder.getCompany().getName();
                        tabModel.data[i][2] = companyOrder.getName() == null ? "" : companyOrder.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOrder.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOrder.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOrder.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyOrder.getChangeddate());
                        tabModel.data[i][5] = companyOrder.getStatus() == null ? "" : companyOrder.getStatus();
                        tabModel.data[i][6] = companyOrder.getCompany() == null ? "" : companyOrder.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOrder.getOrderid();                        
                        data.setValue(companyOrder.getStatus() == null ? "" : companyOrder.getStatus(), query.list().size());

                         Iterator srit = companyOrder.getCompanyorderpositionses().iterator();

                         while(srit.hasNext()){

                            Companyorderpositions pos = (Companyorderpositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());

                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(),cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(), cash);
                            }
                            dtpr.addValue(cash, pos.getCategory(), pos.getCategory());
                        }

                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_ORDER")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);


                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {


                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Crmcampaign"));

                setParamToHQuery(query,true);

                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {

                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {
                        Crmcampaign crmCampaign = (Crmcampaign) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(crmCampaign);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN");
                        tabModel.data[i][1] = crmCampaign.getCampaignnr();
                        tabModel.data[i][2] = crmCampaign.getName() == null ? "" : crmCampaign.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(crmCampaign.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmCampaign.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(crmCampaign.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmCampaign.getChangeddate());
                        tabModel.data[i][5] = crmCampaign.getStatus() == null ? "" : crmCampaign.getStatus();
                        tabModel.data[i][6] = crmCampaign.getCampaignid();
                        tabModel.data[i][7] = crmCampaign.getCampaignid();
                        data.setValue(crmCampaign.getStatus() == null ? "" : crmCampaign.getStatus(), query.list().size());

                         Iterator srit = crmCampaign.getCrmcampaignpositions().iterator();

                         while(srit.hasNext()){

                            Crmcampaignposition pos = (Crmcampaignposition)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());
                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(),cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(),cash);
                            }
                            dtpr.addValue(cash, pos.getProductname()+" - ID: "+pos.getPositionid(), pos.getCategory());
                        }

                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);


                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {


                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Crminvoice"));

                setParamToHQuery(query,true);

                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {

                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {
                        Crminvoice crmInvoice = (Crminvoice) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(crmInvoice);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE");
                        tabModel.data[i][1] = crmInvoice.getBeginchar()+crmInvoice.getInvoicenr();
                        tabModel.data[i][2] = crmInvoice.getName() == null ? "" : crmInvoice.getName();
                        tabModel.data[i][3] = crmInvoice.getCreateddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmInvoice.getCreateddate());
                        tabModel.data[i][4] = crmInvoice.getChangeddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmInvoice.getChangeddate());
                        tabModel.data[i][5] = crmInvoice.getStatus() == null ? "" : crmInvoice.getStatus();
                        tabModel.data[i][6] = crmInvoice.getInvoiceid();
                        tabModel.data[i][7] = crmInvoice.getInvoiceid();
                        data.setValue(crmInvoice.getStatus() == null ? "" : crmInvoice.getStatus(), query.list().size());

                         Iterator srit = crmInvoice.getCrminvoicepositions().iterator();

                         while(srit.hasNext()){

                            Crminvoiceposition pos = (Crminvoiceposition)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());
                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(),cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(),cash);
                            }
                            dtpr.addValue(cash, "ID: "+pos.getCategory(), pos.getCategory());
                        }

                        i++;
                    }


                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT") + " Chart", EBIPGFactory.getLANG("EBI_LANG_CATEGORY"), EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true, false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);

                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    LineAndShapeRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);

                    plopro.setRenderer(1, renderer2);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);


                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {


                query = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").
                        createQuery(returnBuildQuery("Crmproblemsolutions"));

                setParamToHQuery(query,true);

                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                objectIterator = query.iterate();
                int i = 0;

                if (query.list().size() > 0) {

                    tabModel.data = new Object[query.list().size()][8];

                    while (objectIterator.hasNext()) {
                        Crmproblemsolutions crmProsol = (Crmproblemsolutions) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(crmProsol);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL");
                        tabModel.data[i][1] = crmProsol.getServicenr();
                        tabModel.data[i][2] = crmProsol.getName() == null ? "" : crmProsol.getName();
                        tabModel.data[i][3] = crmProsol.getCreateddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmProsol.getCreateddate());
                        tabModel.data[i][4] = crmProsol.getChangeddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmProsol.getChangeddate());
                        tabModel.data[i][5] = crmProsol.getStatus() == null ? "" : crmProsol.getStatus();
                        tabModel.data[i][6] = crmProsol.getProsolid();
                        tabModel.data[i][7] = crmProsol.getProsolid();
                        data.setValue(crmProsol.getStatus() == null ? "" : crmProsol.getStatus(), query.list().size());

                         Iterator srit = crmProsol.getCrmproblemsolpositions().iterator();

                         while(srit.hasNext()){

                            Crmproblemsolposition pos = (Crmproblemsolposition)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),"1","");

                            if(sumCash.size() > 0){

                                if(sumCash.get(pos.getCategory()) != null){
                                   Double prc = sumCash.get(pos.getCategory());
                                   prc = prc + cash;
                                   sumCash.remove(pos.getCategory());
                                   sumCash.put(pos.getCategory(),prc);
                                }else{
                                  sumCash.put(pos.getCategory(),cash);
                                }

                            }else{
                                sumCash.put(pos.getCategory(),cash);
                            }
                            dtpr.addValue(cash, pos.getProductname()+" - ID: "+pos.getProductid(), pos.getCategory());
                        }

                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                        String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_CATEGORY"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);


                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();

                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }


            } else if (EBIPGFactory.getLANG("EBI_LANG_C_SEARCH_ALL").equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())) {

                Query query1 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Companyorder"));
                setParamToHQuery(query1,false);
                Query query2 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Companyoffer"));
                setParamToHQuery(query2,false);
                Query query3 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Companyactivities"));
                setParamToHQuery(query3,false);
                Query query4 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Companyopportunity"));
                setParamToHQuery(query4,false);
                Query query5 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Companyservice"));
                setParamToHQuery(query5,false);
                Query query6 = ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").createQuery(returnBuildQuery("Crmproblemsolutions"));
                setParamToHQuery(query6,true);
                
                data = new DefaultPieDataset();
                DefaultCategoryDataset dtpr  = new DefaultCategoryDataset();
                DefaultCategoryDataset dataset2 = new DefaultCategoryDataset();

                ebiModule.guiRenderer.getPanel("avCharts","Summary").removeAll();
                ebiModule.guiRenderer.getPanel("productChart","Summary").removeAll();

                int size = 0;
                int i = 0;

                if(query1.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"), query1.list().size());
                    size +=query1.list().size();
                }
                if(query2.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"), query2.list().size());
                    size +=query2.list().size();
                }
                if(query3.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES"), query3.list().size());
                    size +=query3.list().size();
                }
                if(query4.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"), query4.list().size());
                    size +=query4.list().size();
                }
                if(query5.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"), query5.list().size());
                    size +=query5.list().size();
                }
                if(query6.list() != null){
                    data.setValue(EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL"), query6.list().size());
                    size +=query6.list().size();
                }


                if (size > 0) {

                    tabModel.data = new Object[size][8];

                    objectIterator = query1.iterate();
                    while (objectIterator.hasNext()) {

                        Companyorder companyOrder = (Companyorder) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOrder);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_ORDER");
                        tabModel.data[i][1] = companyOrder.getCompany() == null ? "" : companyOrder.getCompany().getName();
                        tabModel.data[i][2] = companyOrder.getName() == null ? "" : companyOrder.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOrder.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOrder.getChangeddate());
                        tabModel.data[i][5] = companyOrder.getStatus() == null ? "" : companyOrder.getStatus();
                        tabModel.data[i][6] = companyOrder.getCompany() == null ? "" : companyOrder.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOrder.getOrderid();

                        Iterator srit = companyOrder.getCompanyorderpositionses().iterator();

                         while(srit.hasNext()){

                            Companyorderpositions pos = (Companyorderpositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);
                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());
                            if(sumCash.size() > 0){

                                if(sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_ORDER")) != null){
                                   Double prc = sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"));
                                   prc = prc + cash;
                                   sumCash.remove(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"));
                                   sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"),prc);
                                }else{
                                  sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"),cash);
                                }

                            }else{
                                sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_ORDER"),cash);
                            }
                            dtpr.addValue(cash, EBIPGFactory.getLANG("EBI_LANG_C_ORDER"), ""+companyOrder.getCompanyorderpositionses().size());
                        }

                        i++;

                    }

                    objectIterator = query2.iterate();
                    while (objectIterator.hasNext()) {
                        Companyoffer companyOffer = (Companyoffer) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOffer);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_OFFER");
                        tabModel.data[i][1] = companyOffer.getCompany() == null ? "" : companyOffer.getCompany().getName();
                        tabModel.data[i][2] = companyOffer.getName() == null ? "" : companyOffer.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOffer.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOffer.getChangeddate());
                        tabModel.data[i][5] = companyOffer.getStatus() == null ? "" : companyOffer.getStatus();
                        tabModel.data[i][6] = companyOffer.getCompany() == null ? "" : companyOffer.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOffer.getOfferid();

                        Iterator srit = companyOffer.getCompanyofferpositionses().iterator();

                        while(srit.hasNext()){

                            Companyofferpositions pos = (Companyofferpositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());

                            if(sumCash.size() > 0){

                                if(sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_OFFER")) != null){
                                   Double prc = sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"));
                                   prc = prc + cash;
                                   sumCash.remove(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"));
                                   sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),prc);
                                }else{
                                  sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),cash);
                                }

                            }else{
                                sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),cash);
                            }
                            dtpr.addValue(cash, EBIPGFactory.getLANG("EBI_LANG_C_OFFER"), ""+companyOffer.getCompanyofferpositionses().size());
                        }

                        i++;
                    }

                    objectIterator = query3.iterate();
                    while (objectIterator.hasNext()) {
                        Companyactivities companyActivity = (Companyactivities) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyActivity);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES");
                        tabModel.data[i][1] = companyActivity.getCompany() == null ? "" : companyActivity.getCompany().getName() == null ? "" : companyActivity.getCompany().getName();
                        tabModel.data[i][2] = companyActivity.getActivityname() == null ? "" : companyActivity.getActivityname();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyActivity.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyActivity.getChangeddate());
                        tabModel.data[i][5] = companyActivity.getActivitystatus() == null ? "" : companyActivity.getActivitystatus();
                        tabModel.data[i][6] = companyActivity.getCompany() == null ? "" : companyActivity.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyActivity.getActivityid();
                        i++;
                    }

                    objectIterator = query4.iterate();
                    while (objectIterator.hasNext()) {
                        Companyopportunity companyOpportunity = (Companyopportunity) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyOpportunity);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY");
                        tabModel.data[i][1] = companyOpportunity.getCompany() == null ? "" : companyOpportunity.getCompany().getName();
                        tabModel.data[i][2] = companyOpportunity.getName() == null ? "" : companyOpportunity.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyOpportunity.getChangeddate());
                        tabModel.data[i][5] = companyOpportunity.getOpportunitystatus() == null ? "" : companyOpportunity.getOpportunitystatus();
                        tabModel.data[i][6] = companyOpportunity.getCompany() == null ? "" : companyOpportunity.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyOpportunity.getOpportunityid();
                        sumCash.put( EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"),companyOpportunity.getOpportunityvalue());
                        dtpr.addValue(companyOpportunity.getOpportunityvalue(), EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"), companyOpportunity.getName());

                        i++;

                    }
                    
                    objectIterator = query5.iterate();
                    while (objectIterator.hasNext()) {

                        Companyservice companyService = (Companyservice) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(companyService);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_SERVICE");
                        tabModel.data[i][1] = companyService.getCompany() == null ? "" : companyService.getCompany().getName();
                        tabModel.data[i][2] = companyService.getName() == null ? "" : companyService.getName();
                        tabModel.data[i][3] = ebiModule.ebiPGFactory.getDateToString(companyService.getCreateddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyService.getCreateddate());
                        tabModel.data[i][4] = ebiModule.ebiPGFactory.getDateToString(companyService.getChangeddate()) == null ? "" : ebiModule.ebiPGFactory.getDateToString(companyService.getChangeddate());
                        tabModel.data[i][5] = companyService.getStatus() == null ? "" : companyService.getStatus();
                        tabModel.data[i][6] = companyService.getCompany() == null ? "" : companyService.getCompany().getCompanyid();
                        tabModel.data[i][7] = companyService.getServiceid();
                        
                        Iterator srit = companyService.getCompanyservicepositionses().iterator();

                        while(srit.hasNext()){

                            Companyservicepositions pos = (Companyservicepositions)srit.next();
                            ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(pos);

                            double cash = ebiModule.dynMethod.calculatePreTaxPrice(pos.getNetamount(),pos.getQuantity().toString(),pos.getDeduction());

                            if(sumCash.size() > 0){

                                if(sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE")) != null){
                                   Double prc = sumCash.get(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"));
                                   prc = prc + cash;
                                   sumCash.remove(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"));
                                   sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),prc);
                                }else{
                                  sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"), cash);
                                }

                            }else{
                                sumCash.put(EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),cash);
                            }
                            dtpr.addValue(cash, EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),""+companyService.getCompanyservicepositionses().size());
                        }

                        i++;
                    }
                    objectIterator = query6.iterate();
                    while (objectIterator.hasNext()) {
                        Crmproblemsolutions crmProsol = (Crmproblemsolutions) objectIterator.next();
                        ebiModule.ebiPGFactory.hibernate.getHibernateSession("SUMMARY_SESSION").refresh(crmProsol);
                        tabModel.data[i][0] = EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL");
                        tabModel.data[i][1] = crmProsol.getServicenr();
                        tabModel.data[i][2] = crmProsol.getName() == null ? "" : crmProsol.getName();
                        tabModel.data[i][3] = crmProsol.getCreateddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmProsol.getCreateddate());
                        tabModel.data[i][4] = crmProsol.getChangeddate() == null ? "" : ebiModule.ebiPGFactory.getDateToString(crmProsol.getChangeddate());
                        tabModel.data[i][5] = crmProsol.getStatus() == null ? "" : crmProsol.getStatus();
                        tabModel.data[i][6] = crmProsol.getProsolid();
                        tabModel.data[i][7] = crmProsol.getProsolid();
                        data.setValue(crmProsol.getStatus() == null ? "" : crmProsol.getStatus(), query6.list().size());
                        i++;
                    }

                    Iterator enrc = sumCash.keySet().iterator();
                    Double sumx = 0.0;
                    while(enrc.hasNext()){
                      String key  = (String) enrc.next();
                      sumx  += sumCash.get(key);
                      dataset2.addValue(sumCash.get(key),"", key);
                    }

                    chartproduct = ChartFactory.createBarChart3D(EBIPGFactory.getLANG("EBI_LANG_PRODUCT")+" Chart",  EBIPGFactory.getLANG("EBI_LANG_TYPE"),  EBIPGFactory.getLANG("EBI_LANG_VALUE"), dtpr, PlotOrientation.VERTICAL, true, true,false);

                    CategoryPlot plopro = chartproduct.getCategoryPlot();
                    plopro.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
                    plopro.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);
                    ValueAxis axis2 = new NumberAxis3D(EBIPGFactory.getLANG("EBI_LANG_SUM")+" "+currency.format(sumx));
                    plopro.setRangeAxis(1, axis2);
                    plopro.setDataset(1, dataset2);
                    plopro.mapDatasetToRangeAxis(1, 1);
                    CategoryItemRenderer renderer2 = new LineAndShapeRenderer();
                    renderer2.setSeriesPaint(0, Color.blue);
                    plopro.setRenderer(1, renderer2);

                    plopro.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

                    chart = ChartFactory.createPieChart3D(EBIPGFactory.getLANG("EBI_LANG_C_TITLE_SUMMARY")+" Chart",data,true, true,false);
                    PiePlot3D plot = (PiePlot3D) chart.getPlot();
                    plot.setForegroundAlpha(0.6f);
                    plot.setCircular(true);
                    
                    ChartPanel panel = new ChartPanel(chart);
                    ChartPanel panelProduct = new ChartPanel(chartproduct);

                    panel.setBounds(0,0, ebiModule.guiRenderer.getPanel("avCharts","Summary").getWidth(), ebiModule.guiRenderer.getPanel("avCharts","Summary").getHeight());
                    panelProduct.setBounds(0,0, ebiModule.guiRenderer.getPanel("productChart","Summary").getWidth(), ebiModule.guiRenderer.getPanel("productChart","Summary").getHeight());

                    ebiModule.guiRenderer.getPanel("avCharts","Summary").add(panel);
                    ebiModule.guiRenderer.getPanel("productChart","Summary").add(panelProduct);
                    ebiModule.guiRenderer.getPanel("avCharts","Summary").updateUI();
                    ebiModule.guiRenderer.getPanel("productChart","Summary").updateUI();
                    
                } else {
                    tabModel.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "", "", ""}};
                }

            }
          ebiModule.guiRenderer.getVisualPanel("Summary").repaint();
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally{
            tabModel.fireTableDataChanged();
            ebiModule.guiRenderer.getVisualPanel("Summary").setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }


    private String returnBuildQuery(String object) {
        boolean haveCreated = false;
        
        String toRet = "";
        String[] SQL = new String[8];

        if (!"".equals(ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getEditor().getText())) {
            haveCreated = true;
        }


        SQL[0] = " FROM ";
        SQL[1] = object + " o ";

        if ("Companyactivities".equals(object)) {
            SQL[2] = "  WHERE o.activityname LIKE ? ";
        } else {
            SQL[2] = "  WHERE o.name LIKE ?  ";
        }

        if (haveCreated == true) {
            SQL[3] = " AND o.createddate  BETWEEN ? AND ? ";
        } else {
            SQL[3] = "";
        }

        if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").getSelectedItem().toString())){
        	
            if("Companyopportunity".equals(object)){
              SQL[4] = "AND o.opportunitystatus LIKE ? ";
            }else if("Companyactivities".equals(object)){
              SQL[4] = "AND o.activitystatus LIKE ? ";
            }else{
              SQL[4] = "AND o.status LIKE ? ";
            }
        }else{
           SQL[4] = "";
        }
        
        if(!"".equals(ebiModule.guiRenderer.getTextfield("companyText","Summary").getText())
                && !"Crmcampaign".equals(object) && !"Crmproblemsolutions".equals(object) && !"Crminvoice".equals(object)){
          SQL[5] = " AND o.company.customernr LIKE ?";
        }else{
          SQL[5] = "";
        }

        if(!"".equals(ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").getSelectedItem().toString()) &&
                !EBIPGFactory.getLANG("EBI_LANG_PLESE_SELECT").equals(ebiModule.guiRenderer.
                                    getComboBox("companyCategoryText","Summary").getSelectedItem().toString())
                                         && !"Crmcampaign".equals(object) && !"Crmproblemsolutions".equals(object) && !"Crminvoice".equals(object)){
          SQL[6] = " AND o.company.category LIKE ?";
        }else{
          SQL[6] = "";
        }

        if(ebiModule.guiRenderer.getCheckBox("showForUser","Summary").isSelected()){
           SQL[7] = " AND o.createdfrom LIKE ? ";
        }else{
           SQL[7] = "";  
        }
        
        for(int i=0; i<SQL.length; i++){ // put the query together
            toRet += SQL[i];
        }

        return toRet;
    }

    private Query setParamToHQuery(Query qr,boolean ignore) {

        boolean haveCreated = false;
        
        if (!"".equals( ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getEditor().getText())) {
            haveCreated = true;
        }

        int c = 0;

        qr.setString(c, this.ebiModule.guiRenderer.getTextfield("summaryNameText","Summary").getText() + "%");

        if (haveCreated == true) {
            qr.setDate(++c, ebiModule.guiRenderer.getTimepicker("summaryCreatedFromText","Summary").getDate());
            qr.setDate(++c, ebiModule.guiRenderer.getTimepicker("summaryCreatedToText","Summary").getDate());
        }

        if(!EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").getSelectedItem().toString())){
          qr.setString(++c, ebiModule.guiRenderer.getComboBox("summaryStatusText","Summary").getSelectedItem() + "%");
        }

        if(!"".equals(ebiModule.guiRenderer.getTextfield("companyText","Summary").getText()) &&
                          !ignore  && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").
                                equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())
                                && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").
                                equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())
                                && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").
                                equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())){

              qr.setString(++c, ebiModule.guiRenderer.getTextfield("companyText","Summary").getText() + "%");
            
        }

        if(!"".equals(ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").getSelectedItem().toString())  &&
                !ignore  && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").
                                   equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())
                && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").
                                equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())
                && !EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").
                                equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())){
            
                       if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").
                                            equals(ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").
                                                                                                    getSelectedItem().toString())){
                                qr.setString(++c, "%");
                           
                       }else if(!EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN").
                                            equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString()) &&
                                            !EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL").
                                            equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString()) &&
                                            !EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE").
                                            equals(ebiModule.guiRenderer.getComboBox("summarytypeText","Summary").getSelectedItem().toString())){

                                qr.setString(++c, ebiModule.guiRenderer.getComboBox("companyCategoryText","Summary").getSelectedItem().toString());

                       }
        }

        if(ebiModule.guiRenderer.getCheckBox("showForUser","Summary").isSelected()){
            qr.setString(++c,EBIPGFactory.ebiUser);
        }

        return qr;
    }
}