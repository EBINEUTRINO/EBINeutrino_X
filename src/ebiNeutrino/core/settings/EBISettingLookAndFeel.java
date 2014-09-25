package ebiNeutrino.core.settings;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXPanel;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

public class EBISettingLookAndFeel extends JXPanel {

    private JPanel jPanelThemes = null;
    private JScrollPane jScrollPaneThemes = null;
    private JList jListThemes = null;
    private JButton jButtonApply = null;
    private JCheckBox jCheckBoxPreview = null;
    private EBIMain ebiMain = null;
    private JPanel jPanelExample = null;
    private JButton jButtonExample = null;
    private JCheckBox jCheckBoxExample = null;
    private JTextField jTextFieldExample = null;
    private JComboBox jComboBoxExample = null;
    private JRadioButton jRadioButtonExample1 = null;
    private JRadioButton jRadioButtonExample2 = null;
    private JScrollPane jScrollPaneExample = null;
    private JTextArea jTextAreaExample = null;
    private JScrollPane jScrollPaneExample2 = null;
    private JTable jTableExample = null;

    /**
     * This is the default constructor
     */
    public EBISettingLookAndFeel(EBIMain main) {
        super();

        ebiMain = main;
        setBackground(EBIPGFactory.systemColor);
        setLayout(null);
        initialize();
        EBISystemSetting.selectedModule = 3;
    }

    private DefaultListModel getThemeNames() {

        LookAndFeelInfo[] themes = UIManager.getInstalledLookAndFeels();
     
        DefaultListModel listModel = new DefaultListModel();

        for (int i = 0; i < themes.length; i++) {
          if(!"".equals(themes[i].getName().trim()) && !"Nimbus".equals(themes[i].getName().trim()) ){
        	  listModel.addElement(themes[i].getName());
          }
        }

        return listModel;
    }

    private void showExampleThemeFromName(String theme) {

        LookAndFeelInfo[] themes = UIManager.getInstalledLookAndFeels();

        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getName().equals(theme)) {
                try {
                    // Get the current look and feel
                    LookAndFeel previous = UIManager.getLookAndFeel();
                    // Set the look and feel to the selected one
                    UIManager.setLookAndFeel(themes[i].getClassName());
                    // Update the example components with the new look and feel
                    SwingUtilities.updateComponentTreeUI(getJPanelExample());
                    // Trade back to the old look and feel
                    // to prevent that new components are shown in
                    // the current selected look and feel
                    UIManager.setLookAndFeel(previous);
                } catch (UnsupportedLookAndFeelException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                } catch (IllegalAccessException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                } catch (InstantiationException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                } catch (ClassNotFoundException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_FOUND")).Show(EBIMessage.ERROR_MESSAGE);
                } catch (NullPointerException ex){

                }
            }
        }
    }

    private void setThemeFromName(String theme) {

        LookAndFeelInfo[] themes = UIManager.getInstalledLookAndFeels();
        
        for (int i = 0; i < themes.length; i++) {
            if (themes[i].getName().equals(theme)) {
                try {

                    UIManager.setLookAndFeel(themes[i].getClassName());
                    SwingUtilities.updateComponentTreeUI(ebiMain);
                    SwingUtilities.updateComponentTreeUI(this);
                    EBIPropertiesRW properties = EBIPropertiesRW.getPropertiesInstance();
                    properties.setValue("EBI_Neutrino_LookandFeel", themes[i].getClassName());
                    properties.saveProperties();
                } catch (UnsupportedLookAndFeelException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_SET")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                } catch (IllegalAccessException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_SET")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                } catch (InstantiationException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_SET")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                } catch (ClassNotFoundException ex) {
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_SET")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                } catch (NullPointerException ex){
                    EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_ERROR_THEME_NOT_SET")).Show(EBIMessage.ERROR_MESSAGE);
                    return;
                }
                ebiMain.ebiBar.repaint();
                ebiMain.ebiBar.updateUI();
                EBIExceptionDialog.getInstance(EBIPGFactory.getLANG("EBI_LANG_INFO_SETTING_SAVED")).Show(EBIMessage.INFO_MESSAGE);
                break;
            }
            
        }
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setBounds(new java.awt.Rectangle(0, 0, 700, 500));
        JLabel jLabel1=new JLabel();
        jLabel1.setBounds(new java.awt.Rectangle(70, 15, 120, 20));
        jLabel1.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        jLabel1.setText("Look and Feel");
        JLabel jLabel=new JLabel();
        jLabel.setBounds(new java.awt.Rectangle(20, 10, 30, 30));
        jLabel.setIcon(EBIConstant.ICON_DESIGNER);
        jLabel.setText("");
        this.add(jLabel, null);
        this.add(jLabel1, null);
        this.add(getJPanelThemes(), null);
        this.add(getJPanelExample(), null);

        jListThemes.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                if (jListThemes.getSelectedIndex() == -1) {
                    jButtonApply.setEnabled(false);
                } else if (!jListThemes.getValueIsAdjusting()) {
                    jButtonApply.setEnabled(true);

                    if (jCheckBoxPreview.isSelected()) {
                      if(jListThemes.getSelectedValue() != null){  
                        String theme = jListThemes.getSelectedValue().toString();
                        showExampleThemeFromName(theme);
                      }
                    }
                }
            }
        });
    }

    public void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        // Draw bg top
        Color startColor = new Color(250,250,250);
        Color endColor = new Color(230,230,230);

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(255, 255, endColor, 38, 255, startColor);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), 45);

        Color sColor = new JPanel().getBackground();
        Color eColor = sColor;

        // A non-cyclic gradient
        GradientPaint gradient1 = new GradientPaint(0,0, sColor, getWidth(), 38, eColor);
        g2.setPaint(gradient1);
        g2.setColor(EBIPGFactory.systemColor);
        g.fillRect(0, 46, getWidth(), getHeight());

        g2.setColor(EBIPGFactory.systemColor);

        g.setColor(new Color(220,220,220));
        g.drawLine(0, 45, getWidth(), 45);
        setOpaque(false);
    }

    /**
     * This method initializes jPanelThemes	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelThemes() {
        if (jPanelThemes == null) {
            jPanelThemes = new JPanel();
            jPanelThemes.setLayout(null);
            jPanelThemes.setBounds(new java.awt.Rectangle(5, 70, 450, 200));
            jPanelThemes.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_AVAILABLE_THEME"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            jPanelThemes.add(getJScrollPaneThemes(), null);
            jPanelThemes.add(getJButtonApply(), null);
            jPanelThemes.add(getJCheckBoxPreview(), null);
            jPanelThemes.setOpaque(false);
        }
        return jPanelThemes;
    }

    /**
     * This method initializes jScrollPaneThemes	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPaneThemes() {
        if (jScrollPaneThemes == null) {
            jScrollPaneThemes = new JScrollPane();
            jScrollPaneThemes.setBounds(new java.awt.Rectangle(5, 20, 200, 175));
            jScrollPaneThemes.setViewportView(getJListThemes());
        }
        return jScrollPaneThemes;
    }

    /**
     * This method initializes jListThemes	
     * 	
     * @return javax.swing.JList	
     */
    private JList getJListThemes() {
        if (jListThemes == null) {
            jListThemes = new JList(getThemeNames());
            jListThemes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        }
        return jListThemes;
    }

    /**
     * This method initializes jButtonUebernehmen	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonApply() {
        if (jButtonApply == null) {
            jButtonApply = new JButton();
            jButtonApply.setBounds(new java.awt.Rectangle(210, 50, 120, 22));
            jButtonApply.setText(EBIPGFactory.getLANG("EBI_LANG_APPLY"));
            jButtonApply.setEnabled(false);
            jButtonApply.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    saveTheme();
                }
            });
        }
        return jButtonApply;
    }

    public void saveTheme() {
        if (jListThemes.getSelectedIndex() > -1) {
            String theme = jListThemes.getSelectedValue().toString();
            setThemeFromName(theme);
        }
    }

    /**
     * This method initializes jCheckBoxPreview	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxPreview() {
        if (jCheckBoxPreview == null) {
            jCheckBoxPreview = new JCheckBox();
            jCheckBoxPreview.setBounds(new java.awt.Rectangle(210, 20, 120, 22));
            jCheckBoxPreview.setSelected(true);
            jCheckBoxPreview.setOpaque(false);
            jCheckBoxPreview.setText(EBIPGFactory.getLANG("EBI_LANG_VIEW"));
            jCheckBoxPreview.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (jListThemes.getSelectedIndex() > -1) {
                        if (jCheckBoxPreview.isSelected()) {
                            String theme = jListThemes.getSelectedValue().toString();
                            showExampleThemeFromName(theme);
                        }
                    }
                }
            });
        }
        return jCheckBoxPreview;
    }

    /**
     * This method initializes jPanelExample	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanelExample() {
        if (jPanelExample == null) {
            JLabel jLabelExample=new JLabel();
            jLabelExample.setText(EBIPGFactory.getLANG("EBI_LANG_THEME_ARE_SHOWED_AS_FOLOWING"));
            jLabelExample.setBounds(new java.awt.Rectangle(60, 20, 350, 22));
            jPanelExample = new JPanel();
            jPanelExample.setOpaque(false);
            jPanelExample.setLayout(null);
            jPanelExample.setBounds(new java.awt.Rectangle(5, 280, 450, 183));
            jPanelExample.setBorder(javax.swing.BorderFactory.createTitledBorder(null, EBIPGFactory.getLANG("EBI_LANG_VIEW"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            jPanelExample.add(jLabelExample, null);
            jPanelExample.add(getJButtonExample(), null);
            jPanelExample.add(getJCheckBoxExample(), null);
            jPanelExample.add(getJTextFieldExample(), null);
            jPanelExample.add(getJComboBoxExample(), null);
            jPanelExample.add(getJRadioButtonExample1(), null);
            jPanelExample.add(getJRadioButtonExample2(), null);
            jPanelExample.add(getJScrollPaneExample(), null);
            jPanelExample.add(getJScrollPaneExample2(), null);
        }
        return jPanelExample;
    }

    /**
     * This method initializes jButtonExample	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButtonExample() {
        if (jButtonExample == null) {
            jButtonExample = new JButton();
            jButtonExample.setBounds(new java.awt.Rectangle(5, 50, 120, 22));
            jButtonExample.setText("Button");
        }
        return jButtonExample;
    }

    /**
     * This method initializes jCheckBoxExample	
     * 	
     * @return javax.swing.JCheckBox	
     */
    private JCheckBox getJCheckBoxExample() {
        if (jCheckBoxExample == null) {
            jCheckBoxExample = new JCheckBox();
            jCheckBoxExample.setOpaque(false);
            jCheckBoxExample.setBounds(new java.awt.Rectangle(5, 80, 120, 22));
            jCheckBoxExample.setText("Checkbox");
        }
        return jCheckBoxExample;
    }

    /**
     * This method initializes jTextFieldExample	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextFieldExample() {
        if (jTextFieldExample == null) {
            jTextFieldExample = new JTextField();
            jTextFieldExample.setBounds(new java.awt.Rectangle(5, 110, 120, 22));
            jTextFieldExample.setText("Textfield");
        }
        return jTextFieldExample;
    }

    /**
     * This method initializes jComboBoxExample	
     * 	
     * @return javax.swing.JComboBox	
     */
    private JComboBox getJComboBoxExample() {
        if (jComboBoxExample == null) {
            jComboBoxExample = new JComboBox();
            jComboBoxExample.setBounds(new java.awt.Rectangle(5, 140, 120, 22));
            jComboBoxExample.addItem("Combobox 1");
            jComboBoxExample.addItem("Combobox 2");
            jComboBoxExample.addItem("Combobox 3");
        }
        return jComboBoxExample;
    }

    /**
     * This method initializes jRadioButtonExample1	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonExample1() {
        if (jRadioButtonExample1 == null) {
            jRadioButtonExample1 = new JRadioButton();
            jRadioButtonExample1.setOpaque(false);
            jRadioButtonExample1.setBounds(new java.awt.Rectangle(135, 50, 110, 22));
            jRadioButtonExample1.setText("Radiobutton 1");
        }
        return jRadioButtonExample1;
    }

    /**
     * This method initializes jRadioButtonExample2	
     * 	
     * @return javax.swing.JRadioButton	
     */
    private JRadioButton getJRadioButtonExample2() {
        if (jRadioButtonExample2 == null) {
            jRadioButtonExample2 = new JRadioButton();
            jRadioButtonExample2.setOpaque(false);
            jRadioButtonExample2.setBounds(new java.awt.Rectangle(135, 80, 110, 22));
            jRadioButtonExample2.setText("Radiobutton 2");
        }
        return jRadioButtonExample2;
    }

    /**
     * This method initializes jScrollPaneExample	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPaneExample() {
        if (jScrollPaneExample == null) {
            jScrollPaneExample = new JScrollPane();
            jScrollPaneExample.setBounds(new java.awt.Rectangle(135, 110, 110, 52));
            jScrollPaneExample.setViewportView(getJTextAreaExample());
            jScrollPaneExample.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
        return jScrollPaneExample;
    }

    /**
     * This method initializes jTextAreaExample	
     * 	
     * @return javax.swing.JTextArea	
     */
    private JTextArea getJTextAreaExample() {
        if (jTextAreaExample == null) {
            jTextAreaExample = new JTextArea();
            jTextAreaExample.setText("Textarea");
        }
        return jTextAreaExample;
    }

    /**
     * This method initializes jScrollPaneExample2	
     * 	
     * @return javax.swing.JScrollPane	
     */
    private JScrollPane getJScrollPaneExample2() {
        if (jScrollPaneExample2 == null) {
            jScrollPaneExample2 = new JScrollPane();
            jScrollPaneExample2.setBounds(new java.awt.Rectangle(255, 50, 185, 112));
            jScrollPaneExample2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            jScrollPaneExample2.setViewportView(getJTableExample());
        }
        return jScrollPaneExample2;
    }

    /**
     * This method initializes jTableExample	
     * 	
     * @return javax.swing.JTable	
     */
    private JTable getJTableExample() {
        if (jTableExample == null) {
            Object[][] data = {{"Foo", "Bar", "Baz"}, {"Qux", "Foo", "Bar"}, {"Foo", "Bar", "Baz"}, {"Qux", "Foo", "Bar"}, {"Foo", "Bar", "Baz"}, {"Qux", "Foo", "Bar"}};
            Object[] cols = {"XE", "DY", "CI"};
            jTableExample = new JTable(data, cols);

        }
        return jTableExample;
    }
}

