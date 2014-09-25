package bizcal.Main;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import ebiNeutrinoSDK.utils.EBIConstant;
import org.jdesktop.swingx.JXDatePicker;

import bizcal.common.Event;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.component.EBIVisualPanel;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIDialogExt;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;

public class TimePicker extends EBIDialogExt {

    private EBIVisualPanelTemplate jContentPane = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JLabel jLabel6 = null;
    private JTextField jTextEventName = null;
    private JCheckBox jTextEventType = null;
    private JComboBox jTextForUser = null;
    private JSpinner jSpinnerStartH = null;
    private JSpinner jSpinnerStartM = null;
    private JSpinner jSpinnerEndH = null;
    private JSpinner jSpinnerEndM = null;
    private JLabel jLabel3 = null;
    private JButton jButton = null;
    private JLabel jLabel5 = null;
    private JButton jButton1 = null;
    private JButton jButtonSave = null;
    private JScrollPane jScrollPane = null;
    private JEditorPane jEditorPane = null;
    private JXDatePicker JXDateStartTime = null;
    private JXDatePicker JXDateEndTime = null;
    private JPanel jPanel = null;
    private List<Event> events = null;
    private Event event = null;
    private GregorianCalendar startDate = null;
    private GregorianCalendar endDate = null;
    public boolean success = false;
    private boolean isEdit = false;
    private BizcalMain bizMain = null;

  
    public TimePicker(BizcalMain main,List<Event> events, Event event, boolean isEdit) {
        super(main.ebiModule.ebiPGFactory.getMainFrame());
        setName("CalendarTimePicker");
        storeLocation(true);
        storeSize(true);
        
        setResizable(false);
        setModal(true);
        this.events = events;
        this.event = event;
        bizMain = main; 
        this.isEdit = isEdit;

        startDate = new GregorianCalendar();
        startDate.setTime(event.getStart());
        startDate.set(Calendar.SECOND, 0);
        startDate.set(Calendar.MILLISECOND, 0);

        endDate = new GregorianCalendar();
        endDate.setTime(event.getEnd());
        endDate.set(Calendar.SECOND, 0);
        endDate.set(Calendar.MILLISECOND, 0);
        initialize();
        jContentPane.setModuleTitle(EBIPGFactory.getLANG("EBI_LANG_NEW_EVENT"));
        jContentPane.setModuleIcon(EBIConstant.ICON_APP);
        jContentPane.setChangePropertiesVisible(false);
        jContentPane.setClosable(true);
        jContentPane.setBackground(EBIPGFactory.systemColor);
        jContentPane.setBackgroundColor(EBIPGFactory.systemColor);
        if (isEdit) {
            editEvent();
            jTextEventType.setVisible(false);
            jTextForUser.setVisible(false);
            jLabel6.setVisible(false);
        }

    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(709, 369);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private EBIVisualPanelTemplate getJContentPane() {
        if (jContentPane == null) {
            jLabel6 = new JLabel();
            jLabel6.setBounds(new Rectangle(462, 24, 80, 20));
            jLabel6.setText(EBIPGFactory.getLANG("EBI_LANG_ASSIGN_TO"));
            jLabel5 = new JLabel();
            jLabel5.setBounds(new Rectangle(22, 119, 154, 19));
            jLabel5.setText(EBIPGFactory.getLANG("EBI_LANG_DESCRIPTION"));
            jLabel3 = new JLabel();
            jLabel3.setBounds(new Rectangle(20, 76, 100, 20));
            jLabel3.setText(EBIPGFactory.getLANG("EBI_LANG_COLOR"));
            jLabel2 = new JLabel();
            jLabel2.setBounds(new Rectangle(360, 50, 100, 20));
            jLabel2.setText(EBIPGFactory.getLANG("EBI_LANG_END_TIME"));
            jLabel1 = new JLabel();
            jLabel1.setBounds(new Rectangle(19, 50, 100, 20));
            jLabel1.setText(EBIPGFactory.getLANG("EBI_LANG_START_TIME"));
            jLabel = new JLabel();
            jLabel.setBounds(new Rectangle(19, 24, 100, 20));
            jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_NAME"));
            jContentPane = new EBIVisualPanelTemplate();
            jContentPane.setLayout(null);
            jContentPane.add(jLabel, null);
            jContentPane.add(jLabel1, null);
            jContentPane.add(jLabel2, null);
            jContentPane.add(jLabel6, null);
            jContentPane.add(getJTextEventName(), null);
            jContentPane.add(getJTextEventType(), null);
            jContentPane.add(getJTextForUser(), null);
            jContentPane.add(getJSpinnerStartH(), null);
            jContentPane.add(getJSpinnerStartM(), null);
            jContentPane.add(getJSpinnerEndH(), null);
            jContentPane.add(getJSpinnerEndM(), null);
            jContentPane.add(jLabel3, null);
            jContentPane.add(getJButton(), null);
            jContentPane.add(jLabel5, null);
            jContentPane.add(getJButton1(), null);
            jContentPane.add(getJButtonSave(), null);
            jContentPane.add(getJScrollPane(), null);
            jContentPane.add(getJXDateStartTime(), null);
            jContentPane.add(getJXDateEndTime(), null);
            jContentPane.add(getJPanel(), null);
        }
        return jContentPane;
    }

    private void editEvent() {
        jTextEventName.setText(event.getSummary());
        jEditorPane.setText(event.getDescription());
        jPanel.setBackground(event.getColor());
        jTextEventType.setSelected(event.isActivity());
        jTextForUser.setSelectedItem(event.getCreatedFrom() == null ||
                                        "".equals(event.getCreatedFrom())
                                            ? EBIPGFactory.ebiUser: event.getCreatedFrom() );
    }

    /**
     * This method initializes jTextEventName
     *
     * @return javax.swing.JTextField
     */
    private JTextField getJTextEventName() {
        if (jTextEventName == null) {
            jTextEventName = new JTextField();
            jTextEventName.setBounds(new Rectangle(123, 24, 241, 20));
        }
        return jTextEventName;
    }

    private JCheckBox getJTextEventType() {
        if (jTextEventType == null) {
            jTextEventType = new JCheckBox();
            jTextEventType.setText(EBIPGFactory.getLANG("EBI_LANG_C_PANEL_ACTIVITY"));
            jTextEventType.setBounds(new Rectangle(370, 24, 90, 20));
            jTextEventType.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                         if(jTextEventType.isSelected()){
                             jTextForUser.setEnabled(true);
                         }else{
                             jTextForUser.setEnabled(false);
                         }
                    }
            });
        }
        return jTextEventType;
    }

    private JComboBox getJTextForUser(){
        if(jTextForUser == null){
          jTextForUser = new JComboBox();
          jTextForUser.setBounds(new Rectangle(550, 24, 135, 20));
          jTextForUser.setModel(new DefaultComboBoxModel(EBIPGFactory.systemUsers));
          jTextForUser.setEnabled(false);
        }
        return jTextForUser;
    }


    /**
     * This method initializes jSpinnerStartH
     *
     * @return javax.swing.JSpinner
     */
    private JSpinner getJSpinnerStartH() {
        if (jSpinnerStartH == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMaximum(24);
            model.setMinimum(0);
            jSpinnerStartH = new JSpinner();
            jSpinnerStartH.setBounds(new Rectangle(269, 50, 39, 20));
            jSpinnerStartH.setModel(model);
            jSpinnerStartH.setValue(startDate.get(Calendar.HOUR_OF_DAY));
        }
        return jSpinnerStartH;
    }

    /**
     * This method initializes jSpinnerStartM
     *
     * @return javax.swing.JSpinner
     */
    private JSpinner getJSpinnerStartM() {
        if (jSpinnerStartM == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMaximum(59);
            model.setMinimum(0);
            jSpinnerStartM = new JSpinner();
            jSpinnerStartM.setBounds(new Rectangle(312, 50, 39, 20));
            jSpinnerStartM.setModel(model);
            jSpinnerStartM.setValue(startDate.get(Calendar.MINUTE));
        }
        return jSpinnerStartM;
    }

    /**
     * This method initializes jSpinnerEndH
     *
     * @return javax.swing.JSpinner
     */
    private JSpinner getJSpinnerEndH() {
        if (jSpinnerEndH == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMaximum(24);
            model.setMinimum(0);
            jSpinnerEndH = new JSpinner();
            jSpinnerEndH.setBounds(new Rectangle(605, 50, 39, 20));
            jSpinnerEndH.setModel(model);
            jSpinnerEndH.setValue(endDate.get(Calendar.HOUR_OF_DAY));
        }
        return jSpinnerEndH;
    }

    /**
     * This method initializes jSpinnerEndM
     *
     * @return javax.swing.JSpinner
     */
    private JSpinner getJSpinnerEndM() {
        if (jSpinnerEndM == null) {
            SpinnerNumberModel model = new SpinnerNumberModel();
            model.setMaximum(59);
            model.setMinimum(0);
            jSpinnerEndM = new JSpinner();
            jSpinnerEndM.setBounds(new Rectangle(647, 50, 39, 20));
            jSpinnerEndM.setModel(model);
            jSpinnerEndM.setValue(endDate.get(Calendar.MINUTE));
        }
        return jSpinnerEndM;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setBounds(new Rectangle(157, 76, 190, 28));
            jButton.setText(EBIPGFactory.getLANG("EBI_LANG_CHOOS_COLOR"));
            jButton.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Color newColor = JColorChooser.showDialog(
                            TimePicker.this,
                            "",
                            jPanel.getBackground());
                    jPanel.setBackground(newColor);
                }
            });
        }
        return jButton;
    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setBounds(new Rectangle(562, 305, 128, 30));
            jButton1.setText(EBIPGFactory.getLANG("EBI_LANG_CANCEL"));
            jButton1.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    success = false;
                    setVisible(false);
                }
            });
        }
        return jButton1;
    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonSave() {
        if (jButtonSave == null) {
            jButtonSave = new JButton();
            jButtonSave.setBounds(new Rectangle(422, 305, 136, 30));
            jButtonSave.setText(EBIPGFactory.getLANG("EBI_LANG_SAVE"));
            jButtonSave.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!validateInput()) {
                        return;
                    }
                    GregorianCalendar sDate = new GregorianCalendar();
                    sDate.setTime(JXDateStartTime.getDate());

                    sDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(jSpinnerStartH.getValue().toString()));
                    sDate.set(Calendar.MINUTE, Integer.parseInt(jSpinnerStartM.getValue().toString()));
                    sDate.set(Calendar.SECOND, 0);
                    sDate.set(Calendar.MILLISECOND, 0);

                    GregorianCalendar eDate = new GregorianCalendar();
                    eDate.setTime(JXDateEndTime.getDate());
                    eDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(jSpinnerEndH.getValue().toString()));
                    eDate.set(Calendar.MINUTE, Integer.parseInt(jSpinnerEndM.getValue().toString()));
                    eDate.set(Calendar.SECOND, 0);
                    eDate.set(Calendar.MILLISECOND, 0);

                    event.setSummary(jTextEventName.getText());
                    event.setStart(new Date(sDate.getTime().getTime()));
                    event.setEnd(new Date(eDate.getTime().getTime()));
                    event.setColor(jPanel.getBackground());
                    event.setDescription(jEditorPane.getText());
                    event.setActivity(jTextEventType.isSelected());

                    if(jTextForUser.isEnabled()){
                        event.setCreatedFrom(
                                jTextForUser.getSelectedItem() == null ? EBIPGFactory.ebiUser : jTextForUser.getSelectedItem().toString());
                    }

                    if(!isEdit){
                     events.add(event);
                    }
                    setVisible(false);
                    success = true;
                }
            });
        }
        return jButtonSave;
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setBounds(new Rectangle(16, 146, 675, 148));
            jScrollPane.setViewportView(getJEditorPane());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jEditorPane
     *
     * @return javax.swing.JEditorPane
     */
    private JEditorPane getJEditorPane() {
        if (jEditorPane == null) {
            jEditorPane = new JEditorPane();
        }
        return jEditorPane;
    }

    /**
     * This method initializes JXDateStartTime
     *
     * @return org.jdesktop.swingx.JXDateStartTime
     */
    private JXDatePicker getJXDateStartTime() {
        if (JXDateStartTime == null) {
            JXDateStartTime = new JXDatePicker();
            JXDateStartTime.setBounds(new Rectangle(123, 50, 142, 21));
            JXDateStartTime.setFormats(EBIPGFactory.DateFormat);
            JXDateStartTime.setDate(startDate.getTime());
        }
        return JXDateStartTime;
    }

    /**
     * This method initializes JXDateEndTime
     *
     * @return org.jdesktop.swingx.JXDatePicker
     */
    private JXDatePicker getJXDateEndTime() {
        if (JXDateEndTime == null) {
            JXDateEndTime = new JXDatePicker();
            JXDateEndTime.setBounds(new Rectangle(461, 50, 142, 20));
            JXDateEndTime.setFormats(EBIPGFactory.DateFormat);
            JXDateEndTime.setDate(endDate.getTime());
        }
        return JXDateEndTime;
    }

    /**
     * This method initializes jPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new GridBagLayout());
            jPanel.setBounds(new Rectangle(123, 75, 29, 26));
            jPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            jPanel.setBackground(new Color(5, 125, 255));
        }
        return jPanel;
    }

    private boolean validateInput() {

        if ("".equals(this.jTextEventName.getText())) {
            EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_INSERT_NAME")).Show(EBIMessage.ERROR_MESSAGE);
            return false;
        }

        if (JXDateEndTime.getDate().getTime() < JXDateStartTime.getDate().getTime() || JXDateStartTime.getDate().getTime() > JXDateEndTime.getDate().getTime() || "".equals(JXDateStartTime.getEditor().getText()) && "".equals(JXDateEndTime.getEditor().getText())) {
           EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_MESSAGE_ILLEGAL_DATE")).Show(EBIMessage.ERROR_MESSAGE);
           return false;
        }


        return true;
    }

}

