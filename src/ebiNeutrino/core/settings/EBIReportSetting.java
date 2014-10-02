package ebiNeutrino.core.settings;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.jdesktop.swingx.JXTable;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.gui.Dialogs.EBIImportReport;
import ebiNeutrino.core.table.models.MyTableModelReportSetting;
import ebiNeutrino.core.table.models.MyTableModelSysSetReportParam;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.model.hibernate.SetReportformodule;
import ebiNeutrinoSDK.model.hibernate.SetReportparameter;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIReportFilter;

public class EBIReportSetting extends JPanel {

    private JPanel jPanelAllgemein = null;
    private EBIMain ebiMain = null;
    private JPanel jPanelAvailableReports = null;
    private JScrollPane jScrollPaneAvailableReport = null;
    private JXTable jTableAvailableReport = null;
    private JComboBox jComboReportCat = null;
    private JTextField jTextReportPath = null;
    private JButton jButtonOpenPath = null;
    private JRadioButton jRadioShowAsPDF = null;
    private JRadioButton jRadioShowAsNormal = null;
    private ButtonGroup group = null; 
    private MyTableModelReportSetting tabModel = null;
    private JCheckBox jCheckIsAktive = null;
    private JButton jButtonInstallReport = null;
    private int selRow = -1;
    private int selParamRow = -1;
    private JTextField jTextReportName = null;
    private int reportID = 0;
    private JPanel jPanelReportParam = null;
    private JTextField jTextParamName = null;
    private JComboBox jComboParamType = null;
    private JScrollPane jScrollPaneParam = null;
    private JXTable jTableParam = null;
    private JButton jButtonAdd = null;
    private JButton jButtonParamDelete = null;
    private MyTableModelSysSetReportParam tabMod = null;
    private SetReportformodule report = null;  
    private File actualReportPath = null;
    private int paramPosition = 0;

    /**
     * This is the default constructor
     */
    public EBIReportSetting(EBIMain main) {
        super();
        ebiMain = main;

        try {
            ebiMain.system.hibernate.openHibernateSession("REPORT_SESSION");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        report = new SetReportformodule();
        tabMod = new MyTableModelSysSetReportParam();
        tabModel = new MyTableModelReportSetting();

        initialize();
        showReports();
        ListSelectionModel rowSM = this.jTableAvailableReport.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                if (!lsm.isSelectionEmpty()) {
                   if(lsm.getMinSelectionIndex() != -1){
                    selRow = jTableAvailableReport.convertRowIndexToModel(lsm.getMinSelectionIndex());
                    editReport(selRow);
                   }
                }
            }
        });
        EBISystemSetting.selectedModule = 4;
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        // Draw bg top
        Color startColor = new Color(230,230,230);
        Color endColor = startColor.darker();

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(255, 255, endColor, 38, 255, startColor);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), 45);

        Color sColor = new JPanel().getBackground();
        Color eColor = sColor;

        // A non-cyclic gradient
        GradientPaint gradient1 = new GradientPaint(0,0, sColor, getWidth(), 38, eColor);
        g2.setPaint(gradient1);
        g2.setColor(startColor);
        g.fillRect(0, 46, getWidth(), getHeight());

        g2.setColor(startColor);

        g.setColor(new Color(220,220,220));
        g.drawLine(0, 45, getWidth(), 45);
        setOpaque(false);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        JLabel jLabel4= new JLabel();
        jLabel4.setBounds(new java.awt.Rectangle(70, 15, 120, 20));
        jLabel4.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        jLabel4.setText(EBIPGFactory.getLANG("EBI_LANG_REPORT_SETTING"));
        JLabel jLabel3= new JLabel();
        jLabel3.setBounds(new java.awt.Rectangle(20, 10, 30, 30));
        jLabel3.setText("");
        jLabel3.setIcon(EBIConstant.ICON_REPORT);
        this.setLayout(null);
        this.setSize(845, 685);
        this.add(getJPanelAllgemein(), null);
        this.add(getJPanelAvailableReports(), null);
        this.add(jLabel3, null);
        this.add(jLabel4, null);
        this.add(getJPanelReportParam(), null);
        this.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(java.awt.event.ComponentEvent e) {
                jPanelAvailableReports.setSize(getWidth() - 20, getHeight() - jPanelAvailableReports.getY() - 35);
                jScrollPaneAvailableReport.setSize(jPanelAvailableReports.getWidth() - 20, jPanelAvailableReports.getHeight() - jScrollPaneAvailableReport.getY() - 10);
            }
        });

    }

    /**
     * This method initializes jPanelAllgemein	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelAllgemein() {
        if (jPanelAllgemein == null) {
            JLabel jLabel2= new JLabel();

            jLabel2.setBounds(new java.awt.Rectangle(15, 30, 68, 20));
            jLabel2.setText(EBIPGFactory.getLANG("EBI_LANG_NAME"));
            JLabel jLabel1= new JLabel();
            jLabel1.setBounds(new Rectangle(248, 30, 88, 20));
            jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_FILE_NAME"));
            JLabel jLabel= new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(16, 64, 67, 20));
            jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_CATEGORY"));
            jPanelAllgemein = new JPanel();
            jPanelAllgemein.setLayout(null);
            jPanelAllgemein.setBounds(new Rectangle(14, 70, 600, 149));
            jPanelAllgemein.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_GENERAL_REPORT_SETTING"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            jPanelAllgemein.add(getJComboReportCat(), null);
            jPanelAllgemein.add(jLabel, null);
            jPanelAllgemein.add(jLabel1, null);
            jPanelAllgemein.add(getJTextReportPath(), null);
            jPanelAllgemein.add(getJButtonOpenPath(), null);
            jPanelAllgemein.add(getJRadioShowAsPDF(), null);
            jPanelAllgemein.add(getJRadioShowAsNormal(), null);
            jPanelAllgemein.add(getJCheckIsAktive(), null);
            jPanelAllgemein.add(getInstallReports(), null);
            jPanelAllgemein.add(jLabel2, null);
            jPanelAllgemein.add(getJTextReportName(), null);
            setRadioGroup();
        }
        return jPanelAllgemein;
    }

    private void setRadioGroup() {
        if (this.group == null) {
            this.group = new ButtonGroup();
            this.group.add(this.jRadioShowAsPDF);
            this.group.add(this.jRadioShowAsNormal);
        }
    }

    private JPanel getJPanelAvailableReports() {
        if (jPanelAvailableReports == null) {
            jPanelAvailableReports = new JPanel();
            jPanelAvailableReports.setLayout(null);
            jPanelAvailableReports.setBounds(new Rectangle(16, 330, 823, 273));
            jPanelAvailableReports.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_AVAILABLE_REPORT"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            jPanelAvailableReports.add(getJScrollPaneAvailableReport(), null);
        }
        return jPanelAvailableReports;
    }

    private JScrollPane getJScrollPaneAvailableReport() {
        if (jScrollPaneAvailableReport == null) {
            jScrollPaneAvailableReport = new JScrollPane();
            jScrollPaneAvailableReport.setBounds(new java.awt.Rectangle(13, 23, 800, 244));
            jScrollPaneAvailableReport.setViewportView(getJTableAvailableReport());
        }
        return jScrollPaneAvailableReport;
    }

    private JXTable getJTableAvailableReport() {
        if (jTableAvailableReport == null) {
            jTableAvailableReport = new JXTable(tabModel);
        }
        return jTableAvailableReport;
    }

    private JComboBox getJComboReportCat() {
        if (jComboReportCat == null) {
            jComboReportCat = new JComboBox();
            jComboReportCat.setEditable(true);
            jComboReportCat.setBounds(new java.awt.Rectangle(86, 64, 154, 20));
            jComboReportCat.setModel(new javax.swing.DefaultComboBoxModel(
                    new String[]{EBIPGFactory.getLANG("EBI_LANG_PRINT_VIEWS"),
                EBIPGFactory.getLANG("EBI_LANG_PRINT_INVOICES"),
                EBIPGFactory.getLANG("EBI_LANG_PRINT_DELIVERIES"),
                EBIPGFactory.getLANG("EBI_LANG_PRINT_STATISTIC"),
                EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL"),
                EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),
                EBIPGFactory.getLANG("EBI_LANG_C_ORDER"),
                EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"),
                EBIPGFactory.getLANG("EBI_LANG_C_CAMPAIGN"),
                EBIPGFactory.getLANG("EBI_LANG_C_PRODUCT"),
                EBIPGFactory.getLANG("EBI_LANG_C_PROSOL"),
                EBIPGFactory.getLANG("EBI_LANG_PROJECT"),
                EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),
                EBIPGFactory.getLANG("EBI_LANG_PRINT_ACCOUNT"),
                EBIPGFactory.getLANG("EBI_LANG_PRINT_OTHERS")
            }));
        }
        return jComboReportCat;
    }

    private JTextField getJTextReportPath() {
        if (jTextReportPath == null) {
            jTextReportPath = new JTextField();
            jTextReportPath.setBounds(new Rectangle(339, 30, 154, 20));
        }
        return jTextReportPath;
    }

    private JButton getJButtonOpenPath() {
        if (jButtonOpenPath == null) {
            jButtonOpenPath = new JButton();
            jButtonOpenPath.setBounds(new Rectangle(510, 30, 40, 22));
            jButtonOpenPath.setText("...");
            jButtonOpenPath.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    EBIReportFilter filter = new EBIReportFilter();
                    ebiMain.system.fileDialog.setFileFilter(filter);

                    File file = ebiMain.system.getOpenDialog(JFileChooser.FILES_ONLY);
                    if (file != null) {
                        actualReportPath = file;
                        jTextReportPath.setText(actualReportPath.getName());
                    }
                }
            });
        }
        return jButtonOpenPath;
    }

    private JRadioButton getJRadioShowAsPDF() {
        if (jRadioShowAsPDF == null) {
            jRadioShowAsPDF = new JRadioButton();
            jRadioShowAsPDF.setBounds(new Rectangle(18, 102, 139, 20));
            jRadioShowAsPDF.setOpaque(false);
            jRadioShowAsPDF.setText(EBIPGFactory.getLANG("EBI_LANG_SHOW_AS_PDF"));
            jRadioShowAsPDF.setSelected(true);
        }
        return jRadioShowAsPDF;
    }

    private JRadioButton getJRadioShowAsNormal() {
        if (jRadioShowAsNormal == null) {
            jRadioShowAsNormal = new JRadioButton();
            jRadioShowAsNormal.setOpaque(false);
            jRadioShowAsNormal.setBounds(new Rectangle(163, 102, 180, 20));
            jRadioShowAsNormal.setText(EBIPGFactory.getLANG("EBI_LANG_SHOW_NORMAL"));
        }
        return jRadioShowAsNormal;
    }

    public void checkIsSaveOrUpdate() {
        saveReport();
    }

    private JCheckBox getJCheckIsAktive() {
        if (jCheckIsAktive == null) {
            jCheckIsAktive = new JCheckBox();
            jCheckIsAktive.setBounds(new Rectangle(345, 102, 82, 20));
            jCheckIsAktive.setBackground(new java.awt.Color(228, 171, 95));
            jCheckIsAktive.setText(EBIPGFactory.getLANG("EBI_LANG_ACTIVE"));
        }
        return jCheckIsAktive;
    }

    private JButton getInstallReports() {
        if (jButtonInstallReport == null) {
            jButtonInstallReport = new JButton();
            jButtonInstallReport.setBounds(new Rectangle(440, 102, 150, 25));
            jButtonInstallReport.setText(EBIPGFactory.getLANG("EBI_LANG_LANG_INSTALL_REPORT"));
            jButtonInstallReport.setVisible(false);
            jButtonInstallReport.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ev){
                  new EBIImportReport(ebiMain).startReportImport();
                }
            });
        }
        return jButtonInstallReport;
    }

    private JTextField getJTextReportName() {
        if (jTextReportName == null) {
            jTextReportName = new JTextField();
            jTextReportName.setBounds(new java.awt.Rectangle(86, 30, 154, 20));
        }
        return jTextReportName;
    }

    public boolean validateInput() {
        if ("".equals(this.jTextReportName.getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FIELD_NAME_IS_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if ("".equals(this.jTextReportPath.getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_FIELD_REPORT_FILE_IS_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public void newReport() {
        this.report = new SetReportformodule();
        this.showParam();
        this.paramPosition = 0;
        this.jTextReportName.setText("");
        this.jTextReportPath.setText("");
        this.jComboReportCat.getEditor().setItem("");
        this.jRadioShowAsNormal.setSelected(false);
        this.jRadioShowAsPDF.setSelected(false);
        this.jCheckIsAktive.setSelected(false);
        showReports();
        
    }

    public void saveReport() {
        if (!validateInput()) {
            return;
        }

        try {
            ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").begin();

            report.setIsactive(this.jCheckIsAktive.isSelected());
            report.setReportname(this.jTextReportName.getText());
            report.setReportcategory(String.valueOf(this.jComboReportCat.getSelectedIndex()));

            if(actualReportPath != null){
                 report.setReportfilename(actualReportPath.getName());
            }
            
            report.setReportdate(new Date());
            report.setShowaspdf(this.jRadioShowAsPDF.isSelected());
            report.setShowaswindow(this.jRadioShowAsNormal.isSelected());

            Iterator itp = report.getSetReportparameters().iterator();
            while(itp.hasNext()){
                SetReportparameter param = (SetReportparameter)itp.next();
                param.setSetReportformodule(report);
                if(param.getParamid() < 0){
                    param.setParamid(null);
                }
                ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").saveOrUpdate(param);
            }
            
            ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").saveOrUpdate(report);
            ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").commit();

        } catch (HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }
        EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_INFO_SETTING_SAVED")).Show(EBIMessage.INFO_MESSAGE);
        newReport();

    }
    
    
    public void deleteReport() {

        if (this.reportID == -1) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT_RECORD")).Show(EBIMessage.INFO_MESSAGE);
            return;
        }

        if (!EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_DELETE_RECORD")).Show(EBIMessage.INFO_MESSAGE_YESNO)) {
            return;
        }

        try {
            ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").begin();
            Query query = ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").createQuery("from SetReportformodule where idReportForModule=? ").setInteger(0, this.reportID);

            Iterator it = query.iterate();

            if (it.hasNext()) {
                report = (SetReportformodule) it.next();
            }

            ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").delete(report);
            ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").commit();

            showReports();
            newReport();

        } catch (HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editReport(int row) {
        try {

            try{
                this.reportID = Integer.parseInt(tabModel.data[row][6].toString());
            }catch(NumberFormatException ex){
                return;
            }

            if(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT").equals(tabModel.data[row][1].toString())){
                return;
            }

            ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").begin();
            Query query = ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").createQuery("from SetReportformodule where idReportForModule=? ").setInteger(0, this.reportID);

            Iterator it = query.iterate();

            if (it.hasNext()) {
                report = (SetReportformodule) it.next();
                ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").refresh(report);
                this.reportID = report.getIdreportformodule();
                this.jCheckIsAktive.setSelected(report.getIsactive());
                this.jTextReportName.setText(report.getReportname());
                this.jComboReportCat.setSelectedIndex(Integer.parseInt(report.getReportcategory()));
                this.jTextReportPath.setText(report.getReportfilename());
                this.jRadioShowAsPDF.setSelected(report.getShowaspdf());
                this.jRadioShowAsNormal.setSelected(report.getShowaswindow());
                
                this.showParam();
                ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").commit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showReports() {

        int i = 0;
        ResultSet set = null;
        try {
            PreparedStatement ps1 = ebiMain.system.getIEBIDatabase().initPreparedStatement("SELECT * FROM SET_REPORTFORMODULE");
            set = ebiMain.system.getIEBIDatabase().executePreparedQuery(ps1);
            set.last();
            if (set.getRow() > 0) {

                tabModel.data = new Object[set.getRow()][7];

                set.beforeFirst();
                while (set.next()) {


                    if (set.getInt("ISACTIVE") == 1) {
                        tabModel.data[i][0] = Boolean.valueOf(true);
                    } else {
                        tabModel.data[i][0] = Boolean.valueOf(false);
                    }
                    tabModel.data[i][1] = set.getString("REPORTNAME") == null ? "" : set.getString("REPORTNAME");
                    try{
                        tabModel.data[i][2] = set.getString("REPORTCATEGORY") == null ? "" : this.jComboReportCat.getItemAt(Integer.parseInt(set.getString("REPORTCATEGORY")));
                    }catch(NumberFormatException ex){
                       tabModel.data[i][2] = set.getString("REPORTCATEGORY") == null ? "" : this.jComboReportCat.getItemAt(0); 
                    }
                    tabModel.data[i][3] = ebiMain.system.getDateToString(set.getDate("REPORTDATE")) == null ? "" : ebiMain.system.getDateToString(set.getDate("REPORTDATE"));

                    if (set.getInt("SHOWASPDF") == 1) {
                        tabModel.data[i][4] = EBIPGFactory.getLANG("EBI_LANG_YES");
                    } else {
                        tabModel.data[i][4] = EBIPGFactory.getLANG("EBI_LANG_NO");
                    }

                    if (set.getInt("SHOWASWINDOW") == 1) {
                        tabModel.data[i][5] = EBIPGFactory.getLANG("EBI_LANG_YES");
                    } else {
                        tabModel.data[i][5] = EBIPGFactory.getLANG("EBI_LANG_NO");
                    }
                    tabModel.data[i][6] = set.getInt("IDREPORTFORMODULE");
                    ++i;
                }

            } else {
                tabModel.data = new Object[][]{{Boolean.valueOf(false), EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "", "", "", "",""}};
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            tabModel.fireTableDataChanged();
            try {
                set.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel getJPanelReportParam() {
        if (jPanelReportParam == null) {
            JLabel jLabel6= new JLabel();
            jLabel6.setBounds(new Rectangle(11, 42, 117, 20));
            jLabel6.setText(EBIPGFactory.getLANG("EBI_LANG_PARAM_TYPE"));
            JLabel jLabel5= new JLabel();
            jLabel5.setBounds(new Rectangle(11, 16, 116, 20));
            jLabel5.setText(EBIPGFactory.getLANG("EBI_LANG_PARAM_NAME"));
            jPanelReportParam = new JPanel();
            jPanelReportParam.setLayout(null);
            jPanelReportParam.setBounds(new Rectangle(14, 222, 823, 130));
            jPanelReportParam.add(jLabel5, null);
            jPanelReportParam.add(jLabel6, null);
            jPanelReportParam.add(getJTextParamName(), null);
            jPanelReportParam.add(getJComboParamType(), null);
            jPanelReportParam.add(getJScrollPaneParam(), null);
            jPanelReportParam.add(getJButtonAdd(), null);
            jPanelReportParam.add(getJButtonParamDelete(), null);
        }
        return jPanelReportParam;
    }

    private JTextField getJTextParamName() {
        if (jTextParamName == null) {
            jTextParamName = new JTextField();
            jTextParamName.setBounds(new Rectangle(130, 16, 175, 20));
        }
        return jTextParamName;
    }

    private JComboBox getJComboParamType() {
        if (jComboParamType == null) {
            jComboParamType = new JComboBox();
            jComboParamType.setBounds(new Rectangle(130, 42, 175, 20));
            jComboParamType.setModel(new javax.swing.DefaultComboBoxModel(new String[]{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), "Integer", "String", "Text", "Double", "Date","DateTime"}));
        }
        return jComboParamType;
    }

    private JScrollPane getJScrollPaneParam() {
        if (jScrollPaneParam == null) {
            jScrollPaneParam = new JScrollPane();
            jScrollPaneParam.setBounds(new Rectangle(365, 4, 312, 82));
            jScrollPaneParam.setViewportView(getJTableParam());
        }
        return jScrollPaneParam;
    }

    private JXTable getJTableParam() {
        if (jTableParam == null) {
            jTableParam = new JXTable(tabMod);
            jTableParam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            jTableParam.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();

                    if(lsm.getMinSelectionIndex() != -1){
                        selParamRow = jTableParam.convertRowIndexToModel(lsm.getMinSelectionIndex());
                    }
                    
                    if (lsm.isSelectionEmpty()) {
                        jButtonParamDelete.setEnabled(false);
                    } else if (!tabMod.data[selParamRow][0].toString().equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
                        jButtonParamDelete.setEnabled(true);
                    }
                }
            });
        }
        return jTableParam;
    }

    public boolean validateInputParam() {
        if ("".equals(this.jTextParamName.getText()) || this.jComboParamType.getSelectedIndex() == 0) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_PLEASE_SELECT_REPORT_PARAM_TYPE")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void showParam() {
        if (this.report.getSetReportparameters().size() > 0) {
            tabMod.data = new Object[this.report.getSetReportparameters().size()][3];

            Iterator itr = this.report.getSetReportparameters().iterator();
            int i = 0;

            while (itr.hasNext()) {

                SetReportparameter obj = (SetReportparameter) itr.next();
                tabMod.data[i][0] = obj.getParamname();
                tabMod.data[i][1] = obj.getParamtype();
                if(obj.getParamid() == null){obj.setParamid((i+1) * (-1));}
                tabMod.data[i][2] = obj.getParamid();
                i++;
            }
        } else {
            tabMod.data = new Object[][]{{EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"), ""}};
        }
        tabMod.fireTableDataChanged();
    }

    public void addParam() {
        if (!validateInputParam()) {
            return;
        }
        try{

            SetReportparameter repParam= new SetReportparameter();
            repParam.setCreateddate(new Date());
            repParam.setPosition(++paramPosition);
            repParam.setCreatedfrom(EBIPGFactory.ebiUser);
            repParam.setParamname(this.jTextParamName.getText());
            repParam.setParamtype(this.jComboParamType.getSelectedItem().toString());
            report.getSetReportparameters().add(repParam);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        this.showParam();
        this.newParam();
    }

    public void newParam() {
        this.jTextParamName.setText("");
        this.jComboParamType.setSelectedIndex(0);
    }

    public void deleteParam(int row) {
        if (row < 0) {
            return;
        }
        Object[] data = tabMod.getRow(row);
        if (data[0].equals(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT"))) {
            return;
        }

        Iterator iter = this.report.getSetReportparameters().iterator();
        while (iter.hasNext()) {

            SetReportparameter param = (SetReportparameter) iter.next();

            if (Integer.parseInt(data[2].toString()) == param.getParamid()) {

                  if(param.getParamid() > 0){
                    try {
                        ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").begin();
                        ebiMain.system.hibernate.getHibernateSession("REPORT_SESSION").delete(param);
                        ebiMain.system.hibernate.getHibernateTransaction("REPORT_SESSION").commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                  }
                this.report.getSetReportparameters().remove(param);
                this.showParam();
                break;
            }

        }
    }

    private JButton getJButtonAdd() {
        if (jButtonAdd == null) {
            jButtonAdd = new JButton();
            jButtonAdd.setBounds(new Rectangle(324, 13, 31, 24));
            jButtonAdd.setIcon(EBIConstant.ICON_SAVE);
            jButtonAdd.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addParam();
                }
            });
        }
        return jButtonAdd;
    }

    private JButton getJButtonParamDelete() {
        if (jButtonParamDelete == null) {
            jButtonParamDelete = new JButton();
            jButtonParamDelete.setBounds(new Rectangle(324, 42, 31, 24));
            jButtonParamDelete.setIcon(EBIConstant.ICON_DELETE);
            jButtonParamDelete.setEnabled(false);
            jButtonParamDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deleteParam(selParamRow);
                }
            });
        }
        return jButtonParamDelete;
    }
} 