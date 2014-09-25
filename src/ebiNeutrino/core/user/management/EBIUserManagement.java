package ebiNeutrino.core.user.management;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ebiNeutrinoSDK.utils.EBIConstant;
import org.hibernate.Query;
import org.jdesktop.swingx.JXTable;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.table.models.MyTableModelUserManagement;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.interfaces.IEBIToolBar;
import ebiNeutrinoSDK.model.hibernate.Ebiuser;

public class EBIUserManagement extends EBIVisualPanelTemplate {

    private JTextField jTextUsername = null;
    private JPasswordField jTextPassword = null;
    private JRadioButton jRadioNormal = null;
    private JRadioButton jRadioButtonRestriction = null;
    private JScrollPane jScrollPaneUser = null;
    private JXTable jTableUser = null;
    public MyTableModelUserManagement myModelmanagement = null;
    public EBIPGFactory _ebifunction = null;
    public ButtonGroup grupp = null;
    public int selRow = 0;
    public int selected_id = 0;
    private JPanel jPanelModule = null;
    public boolean isSaveOrUpdate = false;
    public EBIMain ebiMain = null;
    private JCheckBox jCheckCanDelete = null;
    private JCheckBox jCheckPrint = null;
    private JCheckBox jCheckSave = null;
    private JPanel pgui = null;
    public int DELETE_BUTTON_ID = -1;
    public IEBIToolBar bar = null;
    public JList list = new JList();


    /**
     * This is the default constructor
     */
    public EBIUserManagement(EBIMain main) {
        super();

        list = new JList(createData(new String[]{EBIPGFactory.getLANG("EBI_LANG_C_SUMMARY"), EBIPGFactory.getLANG("EBI_LANG_C_LEADS"),
                                                 EBIPGFactory.getLANG("EBI_LANG_C_COMPANY"), EBIPGFactory.getLANG("EBI_LANG_C_CONTACT"),
                                                    EBIPGFactory.getLANG("EBI_LANG_C_ADRESS"), EBIPGFactory.getLANG("EBI_LANG_C_BANK_DATA"),
                                                        EBIPGFactory.getLANG("EBI_LANG_C_MEETING_PROTOCOL"), EBIPGFactory.getLANG("EBI_LANG_C_ACTIVITIES"),
                                                           EBIPGFactory.getLANG("EBI_LANG_C_OPPORTUNITY"), EBIPGFactory.getLANG("EBI_LANG_C_OFFER"),
                                                               EBIPGFactory.getLANG("EBI_LANG_C_ORDER"), EBIPGFactory.getLANG("EBI_LANG_C_SERVICE"),
                                                                   EBIPGFactory.getLANG("EBI_LANG_C_TAB_PRODUCT"),EBIPGFactory.getLANG("EBI_LANG_C_TAB_CALENDAR"),
                                                                      EBIPGFactory.getLANG("EBI_LANG_C_TAB_CAMPAIGN"),
                                                                        EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROSOL"), EBIPGFactory.getLANG("EBI_LANG_C_TAB_INVOICE"),
                                                                          EBIPGFactory.getLANG("EBI_LANG_C_TAB_ACCOUNT"), EBIPGFactory.getLANG("EBI_LANG_C_TAB_CASH_REGISTER"),
                                                                                             EBIPGFactory.getLANG("EBI_LANG_C_TAB_PROJECT") }));

        getPanel().setLayout(new BorderLayout());

        pgui  = new JPanel();
        pgui.setLayout(null);
        pgui.setBackground(EBIPGFactory.systemColor);
        getPanel().add(pgui, BorderLayout.CENTER);
        ebiMain = main;
        grupp = new ButtonGroup();
        myModelmanagement = new MyTableModelUserManagement();
        _ebifunction = ebiMain._ebifunction;

        try {
            _ebifunction.hibernate.openHibernateSession("USER_SESSION");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        list.setCellRenderer(new CheckListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent e) {
            int index = list.locationToIndex(e.getPoint());
            CheckableItem item = (CheckableItem) list.getModel()
                .getElementAt(index);
            item.setSelected(!item.isSelected());
            Rectangle rect = list.getCellBounds(index, index);
            list.repaint(rect);
          }
        });
        JScrollPane sp = new JScrollPane(list);
        sp.setBounds(554, 23, 250, 200);

        initialize();
        pgui.add(sp,null);
        setClosable(true);
        jTableUser.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        createUserManagementView(myModelmanagement);
        myModelmanagement.fireTableDataChanged();
    }


      private CheckableItem[] createData(String[] strs) {
        int n = strs.length;
        CheckableItem[] items = new CheckableItem[n];
        for (int i = 0; i < n; i++) {
          items[i] = new CheckableItem(strs[i]);
        }
        return items;
      }

      public String getSelectedModuleId(){
        StringBuffer buffer = new StringBuffer();
        ListModel model = list.getModel();
        int n = model.getSize();
        for (int i = 0; i < n; i++) {
          CheckableItem item = (CheckableItem) model.getElementAt(i);
          if (item.isSelected()) {
            buffer.append(i);
            if(i < n){
                buffer.append("_");
            }
          }
        }
         return buffer.toString();
      }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        JLabel jLabel3=new JLabel();
        jLabel3.setText(EBIPGFactory.getLANG("EBI_LANG_USER_TABLE"));
        jLabel3.setBounds(new Rectangle(6, 211, 164, 19));
        //JLabel jLabel2=new JLabel();
        //jLabel2.setText("");
        //jLabel2.setBounds(new Rectangle(504, 82, 161, 136));
        //jLabel2.setIcon(EBIConstant.USER_SETTING);
        JLabel jLabel1=new JLabel();
        jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_PASSWORD"));
        jLabel1.setBounds(new java.awt.Rectangle(288, 22, 61, 19));
        JLabel jLabel=new JLabel();
        jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_USER"));
        jLabel.setBounds(new java.awt.Rectangle(17, 23, 67, 18));
        pgui.add(jLabel, null);
        pgui.add(getJTextUsername(), null);
        pgui.add(jLabel1, null);
        pgui.add(getJTextPassword(), null);
        pgui.add(getJRadioNormal(), null);
        pgui.add(getJRadioButtonRestriction(), null);
        pgui.add(getJScrollPaneUser(), null);
        //pgui.add(jLabel2, null);
        pgui.add(jLabel3, null);
        pgui.addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentResized(java.awt.event.ComponentEvent e) {
                jScrollPaneUser.setSize(getWidth() - 5, getHeight() - jScrollPaneUser.getY() - 75);
                jTableUser.updateUI();
            }
        });
        grupp.add(getJRadioNormal());
        grupp.add(getJRadioButtonRestriction());
        pgui.add(getJPanelModule(), null);

        jTableUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                editUser(jTableUser.convertRowIndexToModel(jTableUser.getSelectedRow()));
            }
        });

        ListSelectionModel rowSM = jTableUser.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }

                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                
                if (lsm.isSelectionEmpty()) {
                    bar.setComponentToolBarEnabled(DELETE_BUTTON_ID, false);
                } else {

                	selRow = jTableUser.convertRowIndexToModel(lsm.getMinSelectionIndex());
                    editUser(selRow);

                }
            }
        });
    }

    /**
     * Edit selected user
     * @param row
     */
    public void editUser(int row) {
        resetFields();
        selected_id = Integer.parseInt(myModelmanagement.data[row][0].toString());

        jTextUsername.setText(myModelmanagement.data[row][1].toString());
        if("root".equals(jTextUsername.getText())){
            jTextUsername.setEditable(false);
            ebiMain.userSysBar.getToolbarButton(ebiMain.USER_DELETE_ID).setEnabled(false);
            jRadioButtonRestriction.setEnabled(false);
        }
        jTextPassword.setText(myModelmanagement.data[row][2].toString());
        
        if (Boolean.valueOf(myModelmanagement.data[row][7].toString()) == false) {
            jRadioButtonRestriction.setSelected(true);
            list.setEnabled(true);
            String[] mods = myModelmanagement.data[row][11].toString().split("_");
            if(mods != null){
                for(int i = 0 ; i<mods.length; i++){
                    if(!"".equals(mods[i])){
                        ((CheckableItem)list.getModel().getElementAt(Integer.parseInt(mods[i]))).setSelected(true);
                    }
                }
                list.updateUI();
            }
        } else if (Boolean.valueOf(myModelmanagement.data[row][7].toString()) == true) {
            jRadioNormal.setSelected(true);
            list.setEnabled(false);
        }

        if (Boolean.valueOf(myModelmanagement.data[row][10].toString()) == true) {
            jCheckCanDelete.setSelected(true);
        } else {
            jCheckCanDelete.setSelected(false);
        }

        if (Boolean.valueOf(myModelmanagement.data[row][8].toString()) == true) {
            jCheckSave.setSelected(true);
        } else {
            jCheckSave.setSelected(false);
        }

        if (Boolean.valueOf(myModelmanagement.data[row][9].toString()) == true) {
            jCheckPrint.setSelected(true);
        } else {
            jCheckPrint.setSelected(false);
        }

        isSaveOrUpdate = true;
    }

    /**
     * This method initializes jTextUsername	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextUsername() {
        if (jTextUsername == null) {
            jTextUsername = new JTextField();
            jTextUsername.setBounds(new java.awt.Rectangle(87, 23, 191, 20));
            jTextUsername.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyTyped(java.awt.event.KeyEvent e) {
                    if (jTextUsername.hasFocus()) {
                        EBIMain.canReleaseUser = false;
                    }
                }
            });
        }
        return jTextUsername;
    }

    /**
     * This method initializes jTextPassword	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextPassword() {
        if (jTextPassword == null) {
            jTextPassword = new JPasswordField();
            jTextPassword.setBounds(new java.awt.Rectangle(354, 23, 180, 20));
            jTextPassword.addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyTyped(java.awt.event.KeyEvent e) {
                    if (jTextUsername.hasFocus()) {
                        EBIMain.canReleaseUser = false;
                    }
                }
            });
        }
        return jTextPassword;
    }

    /**
     * This method initializes jRadioNormal	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioNormal() {
        if (jRadioNormal == null) {
            jRadioNormal = new JRadioButton();
            jRadioNormal.setText(EBIPGFactory.getLANG("EBI_LANG_SUPERUSER"));
            jRadioNormal.setOpaque(false);
            jRadioNormal.setBounds(new java.awt.Rectangle(87, 62, 97, 20));
            jRadioNormal.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    jCheckCanDelete.setEnabled(false);
                    jCheckSave.setEnabled(false);
                    jCheckPrint.setEnabled(false);
                    list.setEnabled(false);
                }
            });
            jRadioNormal.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (jTextUsername.hasFocus()) {
                        EBIMain.canReleaseUser = false;
                    }
                }
            });
        }
        return jRadioNormal;
    }

    /**
     * This method initializes jRadioButtonBeschrenkung	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonRestriction() {
        if (jRadioButtonRestriction == null) {
            jRadioButtonRestriction = new JRadioButton();
            jRadioButtonRestriction.setText(EBIPGFactory.getLANG("EBI_LANG_NON_SUPERUSER"));
            jRadioButtonRestriction.setOpaque(false);
            jRadioButtonRestriction.setBounds(new java.awt.Rectangle(191, 62, 142, 20));
            jRadioButtonRestriction.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    jCheckSave.setEnabled(true);
                    jCheckPrint.setEnabled(true);
                    jCheckCanDelete.setEnabled(true);
                    list.setEnabled(true);

                }
            });
            jRadioButtonRestriction.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (jTextUsername.hasFocus()) {
                        EBIMain.canReleaseUser = false;
                    }
                }
            });
        }
        return jRadioButtonRestriction;
    }

    /**
     * This method initializes jScrollPaneUser	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPaneUser() {
        if (jScrollPaneUser == null) {
            jScrollPaneUser = new JScrollPane();
            jScrollPaneUser.setBounds(new Rectangle(2, 235, 892, 172));
            jScrollPaneUser.setViewportView(getJTableUser());
        }
        return jScrollPaneUser;
    }

    /**
     * This method initializes jTableUser	
     * 	
     * @return javax.swing.JTable	
     */
    private JXTable getJTableUser() {
        if (jTableUser == null) {
            jTableUser = new JXTable(myModelmanagement);
            jTableUser.setRolloverEnabled(true);
        }
        return jTableUser;
    }

    /**
     * This method initializes jPanelModule	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelModule() {
        if (jPanelModule == null) {
            jPanelModule = new JPanel();
            jPanelModule.setLayout(null);
            jPanelModule.setBounds(new Rectangle(14, 92, 464, 105));
            jPanelModule.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_EXTRA_RIGHTS"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            jPanelModule.add(getJCheckCanDelete(), null);
            jPanelModule.add(getJCheckPrint(), null);
            jPanelModule.add(getJCheckSave(), null);
            jPanelModule.setOpaque(false);
        }
        return jPanelModule;
    }

    /**
     * 	This Method create new EBI Neutrino User
     * @return true if the user is created otherwise false
     */
    public boolean saveUser() {

        if ("".equals(jTextUsername.getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_USER_IS_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        if (jRadioNormal.isSelected() == false && jRadioButtonRestriction.isSelected() == false) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_USER_RIGHTS_ARE_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }
        boolean isAdm = false;
        if (jRadioNormal.isSelected() == true) {
            isAdm = true;
        } else if (jRadioButtonRestriction.isSelected() == true) {
            isAdm = false;
        }

        boolean canPrint = false;
        boolean canSave = false;
        boolean canDelete = false;


        if (jCheckCanDelete.isSelected() == true) {
            canDelete = true;
        }
        if (jCheckSave.isSelected() == true) {
            canSave = true;
        }
        if (jCheckPrint.isSelected() == true) {
            canPrint = true;
        }

        try {
            _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").begin();
            Ebiuser User = new Ebiuser();

            User.setEbiuser(jTextUsername.getText());
            User.setPasswd(_ebifunction.encryptPassword(getPassword(jTextPassword.getPassword())));
            User.setCreateddate(new java.sql.Date(java.lang.System.currentTimeMillis()));
            User.setCreatedfrom(EBIPGFactory.ebiUser);
            User.setIsAdmin(isAdm);
            User.setCansave(canSave);
            User.setCanprint(canPrint);
            User.setCandelete(canDelete);
            if(!isAdm){
                User.setModuleid(getSelectedModuleId());
            }
            _ebifunction.hibernate.getHibernateSession("USER_SESSION").saveOrUpdate(User);
            _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").commit();
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        createUserManagementView(myModelmanagement);
        myModelmanagement.fireTableDataChanged();


        EBIExceptionDialog.getInstance(String.format(EBIPGFactory.getLANG("EBI_LANG_ERROR_USER_SAVED"), jTextUsername.getText())).Show(EBIMessage.INFO_MESSAGE);
        resetFields();

        return true;
    }

    /**
     * this method update EBI Neutrino user 
     * @return true if the user is updated otherwise false
     */
    public boolean updateUser() {

        if ("".equals(jTextUsername.getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_USER_IS_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (jRadioNormal.isSelected() == false && jRadioButtonRestriction.isSelected() == false) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_USER_RIGHTS_ARE_EMPTY")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (EBIExceptionDialog.getInstance(String.format(EBIPGFactory.getLANG("EBI_LANG_QUESTION_UPDATE_USER"), this.jTextUsername.getText())).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {

            try {
                _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").begin();

                boolean isAdm = false;
                if (jRadioNormal.isSelected() == true) {
                    isAdm = true;
                } else if (jRadioButtonRestriction.isSelected() == true) {
                    isAdm = false;
                }

                boolean canDelete = false;
                boolean canSave = false;
                boolean canPrint = false;

                if (jCheckCanDelete.isSelected() == true) {
                    canDelete = true;
                }
                if (jCheckSave.isSelected() == true) {
                    canSave = true;
                }
                if (jCheckPrint.isSelected() == true) {
                    canPrint = true;
                }

                Query query = _ebifunction.hibernate.getHibernateSession("USER_SESSION").createQuery("from Ebiuser user where user.id=? ").setInteger(0, this.selected_id);

                java.util.Iterator it = query.iterate();

                if (it.hasNext()) {
                    Ebiuser User = (Ebiuser) it.next();
                    _ebifunction.hibernate.getHibernateSession("USER_SESSION").refresh(User);
                    String passW = User.getPasswd() == null ? "" : User.getPasswd();

                    if (passW.equals(getPassword(jTextPassword.getPassword()))) {
                        User.setEbiuser(jTextUsername.getText());
                        User.setChangeddate(new java.sql.Date(new java.util.Date().getTime()));
                        User.setChangedfrom(EBIPGFactory.ebiUser);
                        User.setIsAdmin(isAdm);
                        User.setCansave(canSave);
                        User.setCanprint(canPrint);
                        User.setCandelete(canDelete);
                        if(!isAdm){
                            User.setModuleid(getSelectedModuleId());
                        }
                    } else {
                        User.setEbiuser(jTextUsername.getText());
                        User.setPasswd(_ebifunction.encryptPassword(getPassword(jTextPassword.getPassword())));
                        User.setChangeddate(new java.sql.Date(new java.util.Date().getTime()));
                        User.setChangedfrom(EBIPGFactory.ebiUser);
                        User.setIsAdmin(isAdm);
                        User.setCansave(canSave);
                        User.setCanprint(canPrint);
                        User.setCandelete(canDelete);
                        if(!isAdm){
                            User.setModuleid(getSelectedModuleId());
                        }
                    }
                    _ebifunction.hibernate.getHibernateSession("USER_SESSION").saveOrUpdate(User);
                }
                _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").commit();
            } catch (org.hibernate.HibernateException ex) {
                EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        createUserManagementView(myModelmanagement);
        myModelmanagement.fireTableDataChanged();
        resetFields();

        return true;
    }

    /**
     * 	This Method delete existing EBI Neutrino User
     * @return true if the user is created otherwise false
     */
    public boolean userDelete() {
        try {
            if (EBIExceptionDialog.getInstance(String.format(EBIPGFactory.getLANG("EBI_LANG_QUESTION_DELETE_USER"), this.jTextUsername.getText())).Show(EBIMessage.WARNING_MESSAGE_YESNO) == true) {

                _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").begin();

                Query query = _ebifunction.hibernate.getHibernateSession("USER_SESSION").createQuery("from Ebiuser user where user.id=? ").setInteger(0, this.selected_id);

                Iterator it = query.iterate();

                if (it.hasNext()) {
                    Ebiuser User = (Ebiuser) it.next();
                    _ebifunction.hibernate.getHibernateSession("USER_SESSION").delete(User);
                    _ebifunction.hibernate.getHibernateTransaction("USER_SESSION").commit();
                }
                createUserManagementView(myModelmanagement);

                myModelmanagement.fireTableDataChanged();
                if (selRow != 0) {
                    jTableUser.changeSelection(0, 0, false, false);
                }
            }
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return true;
    }

    public void resetFields() {
        jTextUsername.setText("");
        jTextPassword.setText("");
        jRadioButtonRestriction.setEnabled(true);
        jRadioNormal.setSelected(false);
        jRadioButtonRestriction.setSelected(false);
        jCheckSave.setSelected(false);
        jCheckPrint.setSelected(false);
        jCheckCanDelete.setSelected(false);
        selected_id = 0;
        //selRow = 0;
        list.setEnabled(true);
        ListModel model = list.getModel();
        int n = model.getSize();
        for (int i = 0; i < n; i++) {
          CheckableItem item = (CheckableItem) model.getElementAt(i);
          item.setSelected(false);
        }
        list.updateUI();
        //myModelmanagement.fireTableDataChanged();
        isSaveOrUpdate = false;
        jTextUsername.setEditable(true);
        ebiMain.userSysBar.getToolbarButton(ebiMain.USER_DELETE_ID).setEnabled(true);
        
    }

    /**
     * This method initializes jCheckCanDelete	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckCanDelete() {
        if (jCheckCanDelete == null) {
            jCheckCanDelete = new JCheckBox();
            jCheckCanDelete.setBounds(new Rectangle(134, 23, 114, 20));
            jCheckCanDelete.setText(EBIPGFactory.getLANG("EBI_LANG_DELETE"));
            jCheckCanDelete.setOpaque(false);
            jCheckCanDelete.addItemListener(new java.awt.event.ItemListener() {

                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    if (jTextUsername.hasFocus()) {
                        EBIMain.canReleaseUser = false;
                    }
                }
            });
        }
        return jCheckCanDelete;
    }

    /**
     * This method initializes jCheckPrint	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckPrint() {
        if (jCheckPrint == null) {
            jCheckPrint = new JCheckBox();
            jCheckPrint.setBounds(new Rectangle(254, 23, 110, 20));
            jCheckPrint.setOpaque(false);
            jCheckPrint.setText(EBIPGFactory.getLANG("EBI_LANG_PRINT"));
        }
        return jCheckPrint;
    }

    /**
     * This method initializes jCheckSave	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckSave() {
        if (jCheckSave == null) {
            jCheckSave = new JCheckBox();
            jCheckSave.setBounds(new Rectangle(13, 23, 115, 20));
            jCheckSave.setOpaque(false);
            jCheckSave.setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
        }
        return jCheckSave;
    }

    private void createUserManagementView(MyTableModelUserManagement tab) {
        try {
            //_ebifunction.hibernate.getHibernateTransaction("USER_SESSION").begin();

            Query query = _ebifunction.hibernate.getHibernateSession("USER_SESSION").createQuery("from Ebiuser user ");
            Object[][] da = new Object[query.list().size()][12];
            Iterator it = query.iterate();

            int i = 0;
            while (it.hasNext()) {
                Ebiuser user = (Ebiuser) it.next();
                _ebifunction.hibernate.getHibernateSession("USER_SESSION").refresh(user);
                da[i][0] = user.getId();
                da[i][1] = user.getEbiuser() == null ? "" : user.getEbiuser();
                da[i][2] = user.getPasswd() == null ? "" : user.getPasswd();
                da[i][3] = user.getCreateddate() == null ? "" : _ebifunction.getDateToString(user.getCreateddate());
                da[i][4] =  user.getCreatedfrom() == null ? "" : user.getCreatedfrom();
                da[i][5] = user.getChangeddate() == null ? "" : _ebifunction.getDateToString(user.getChangeddate());
                da[i][6] = user.getChangedfrom() == null ?  "" : user.getChangedfrom();
                da[i][7] = user.getIsAdmin() == null ? false : user.getIsAdmin();
                da[i][8] = user.getCansave() == null ? false : user.getCansave();
                da[i][9] = user.getCandelete() == null ? false : user.getCandelete();
                da[i][10] = user.getCanprint() == null ? false : user.getCanprint();
                da[i][11] = user.getModuleid() == null ? "" : user.getModuleid();
                i++;
            }
            if (da.length != 0) {
                tab.data = da;
            } else {
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_NO_USER_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
            }
            //_ebifunction.hibernate.getHibernateTransaction("USER_SESSION").commit();
        } catch (org.hibernate.HibernateException ex) {
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getPassword(char[] passw) {
        String rebuildPassword ="";
        for (int i = 0; i < passw.length; i++) {
            rebuildPassword += passw[i];
        }
        return rebuildPassword;
    }
}

