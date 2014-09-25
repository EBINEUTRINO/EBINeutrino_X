package bizcal.Main;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;

public class BIZCalendarToolbar extends JPanel {

    private static final long serialVersionUID = 1L;
    private JToolBar jToolBar = null;
    public JButton jButtonNew = null;
    public JButton jButtonCopy = null;
    public JButton jButtonCut = null;
    public JButton jButtonPaste = null;
    public JButton jButtonDelete = null;
    public JToggleButton jButtonDayView = null;
    public JToggleButton jButtonMonthView = null;
    public JToggleButton jButtonListView = null;
    private BizcalMain bizMain = null;
    public JComboBox box = new JComboBox();
    public JTextField days = null;
    public JButton update = new JButton(EBIPGFactory.getLANG("EBI_LANG_UPDATE"));
    public JButton gSync = new JButton(EBIPGFactory.getLANG("EBI_LANG_GOOGLE_SYNC"));
    private JLabel userLabel = new JLabel(EBIPGFactory.getLANG("EBI_LANG_SHOW_CALENDAR_FOR_USER"));
    private JLabel dayLabel = new JLabel(EBIPGFactory.getLANG("EBI_LANG_SHOW_CALENDAR_DAY"));

    /**
     * This is the default constructor
     */
    public BIZCalendarToolbar(BizcalMain main) {
        super();
        this.bizMain = main;

        days = new JTextField(main.properties.getValue("EBI_CALENDAR_DAYS"));

        userLabel.setBounds(5,5,85,20);
        box.setBounds(90,5,180,20);
        dayLabel.setBounds(280,5,80,20);
        days.setBounds(365,5,80,20);
        update.setBounds(454,5,110,20);
        
        gSync.setBounds(580,5,150,20);
        gSync.setIcon(new ImageIcon("images/google_icon.jpg"));

        gSync.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent actionEvent) {
                EBIGCalSync gcalSync = new EBIGCalSync(bizMain);
                gcalSync.setVisible();
            }
        });

        box.setModel(new DefaultComboBoxModel(EBIPGFactory.systemUsers));
        box.setSelectedItem(main.user);
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setLayout(new BorderLayout());
        this.add(getJToolBar(), BorderLayout.CENTER);
        ActionListener actionList = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(bizMain.isMonthView){
                    return;
                }
                try {
                    String nr = "".equals(days.getText()) ? "9" : days.getText();
                    try{
                        bizMain.calmodel.setProperties(box.getSelectedItem().toString(),Integer.parseInt(nr),true);
                        bizMain.properties.setValue("EBI_CALENDAR_DAYS",nr);
                        bizMain.properties.saveProperties();
                    }catch(NumberFormatException ex){
                        days.setText(bizMain.properties.getValue("EBI_CALENDAR_DAYS"));
                        try{
                            bizMain.calmodel.setProperties(box.getSelectedItem().toString(),Integer.parseInt(bizMain.properties.getValue("EBI_CALENDAR_DAYS")),true);
                        }catch(NumberFormatException exx){
                           bizMain.calmodel.setProperties(box.getSelectedItem().toString(),9,true);
                        }
                    }
                } catch (Exception e1) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(e1)).Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
                }
            }
        };
        box.addActionListener(actionList);
        update.addActionListener(actionList);
    }

    /**
     * This method initializes jToolBar
     *
     * @return javax.swing.JToolBar
     */
    private JToolBar getJToolBar() {
        JPanel panel = new JPanel();

        panel.setLayout(null);
        panel.add(box,null);
        panel.add(userLabel,null);
        panel.add(dayLabel,null);
        panel.add(days,null);
        panel.add(update,null);
        panel.add(gSync,null);

        if (jToolBar == null) {
            jToolBar = new JToolBar();
            jToolBar.setBorderPainted(true);
            jToolBar.setRollover(true);
            jToolBar.add(getJButtonNew());
            jToolBar.add(getJButtonCopy());
            jToolBar.add(getJButtonCut());
            jToolBar.add(getJButtonPaste());
            jToolBar.addSeparator();
            jToolBar.add(getJButtonDelete());
			jToolBar.addSeparator();

            ButtonGroup group = new ButtonGroup();
            group.add(getJButtonDayView());
            group.add(getJButtonMonthView());
            //group.add(getJButtonListView());

			jToolBar.add(getJButtonDayView());
			jToolBar.add(getJButtonMonthView());
			//jToolBar.add(getJButtonListView());
            jToolBar.add(panel);

        }
        return jToolBar;
    }

    /**
     * This method initializes jButtonNew
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonNew() {
        if (jButtonNew == null) {
            jButtonNew = new JButton();
            jButtonNew.setIcon(EBIConstant.ICON_NEW);
            jButtonNew.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        Date toDate = null;
                        toDate = bizMain.selectedDate == null ? new Date() : bizMain.selectedDate;
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(toDate);
                        cal.set(Calendar.MINUTE,15);
                        bizMain.newEvent(bizMain.calmodel.getEvents(1), toDate, new Date(cal.getTime().getTime()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });

        }
        return jButtonNew;
    }

    /**
     * This method initializes jButtonCopy
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCopy() {
        if (jButtonCopy == null) {
            jButtonCopy = new JButton();
            jButtonCopy.setIcon(EBIConstant.ICON_COPY);
            jButtonCopy.setEnabled(false);
            jButtonCopy.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    bizMain.copyEvent(false);
                }
            });
        }
        return jButtonCopy;
    }

    /**
     * This method initializes jButtonCut
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonCut() {
        if (jButtonCut == null) {
            jButtonCut = new JButton();
            jButtonCut.setEnabled(false);
            jButtonCut.setIcon(EBIConstant.ICON_CUT);
            jButtonCut.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    bizMain.cutEvent();
                }
            });
        }
        return jButtonCut;
    }

    /**
     * This method initializes jButtonPaste
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonPaste() {
        if (jButtonPaste == null) {
            jButtonPaste = new JButton();
            jButtonPaste.setEnabled(false);
            jButtonPaste.setIcon(EBIConstant.ICON_PASTE);
            jButtonPaste.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    bizMain.pasteEvent();
                }
            });
        }
        return jButtonPaste;
    }

    /**
     * This method initializes jButtonDelete
     *
     * @return javax.swing.JButton
     */
    private JButton getJButtonDelete() {
        if (jButtonDelete == null) {
            jButtonDelete = new JButton();
            jButtonDelete.setEnabled(false);
            jButtonDelete.setIcon(EBIConstant.ICON_DELETE);
            jButtonDelete.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    bizMain.deleteEvent();
                }
            });
        }
        return jButtonDelete;
    }

    /**
     * This method initializes jButtonDayView
     *
     * @return javax.swing.JButton
     */
    private JToggleButton getJButtonDayView() {
        if (jButtonDayView == null) {
            jButtonDayView = new JToggleButton();
            jButtonDayView.setSelected(true);
            try {
                jButtonDayView.setIcon(bizMain.getIcon("/bizcal/res/day.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            jButtonDayView.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        bizMain.initDayView();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jButtonDayView;
    }

    /**
     * This method initializes jButtonMountVIew
     *
     * @return javax.swing.JButton
     */
    private JToggleButton getJButtonMonthView() {
        if (jButtonMonthView == null) {
            jButtonMonthView = new JToggleButton();
            try {
                jButtonMonthView.setIcon(bizMain.getIcon("/bizcal/res/month.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            jButtonMonthView.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        bizMain.initMonthView();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jButtonMonthView;
    }

    /**
     * This method initializes jButtonListView
     *
     * @return javax.swing.JButton
     */
    private JToggleButton getJButtonListView() {
        if (jButtonListView == null) {
            jButtonListView = new JToggleButton();
            try {
                jButtonListView.setIcon(bizMain.getIcon("/bizcal/res/list.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            jButtonListView.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {

                }
            });
        }
        return jButtonListView;
    }
}