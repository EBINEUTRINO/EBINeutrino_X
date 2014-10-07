package ebiNeutrino.core.settings;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;

public class EBISystemSetting extends EBIVisualPanelTemplate {

    private JSplitPane jSplitPane = null;
    public EBIListSettingName listName = null;
    public JPanel cpanel = null;
    public EBIMain ebiMain = null;
    public static int selectedModule = -1;


    public EBISystemSetting(EBIMain main) {
        super(false);
        ebiMain = main;
        cpanel = new JPanel();
        cpanel.setLayout(new BorderLayout());
        cpanel.setSize(1000, 800);
        listName = new EBIListSettingName(ebiMain, cpanel);
        setClosable(true);
        initialize();
    }

    private void initialize() {
        super.getPanel().setLayout(new BorderLayout());
        super.getPanel().add(getJSplitPane(), java.awt.BorderLayout.CENTER);
    }

    private JSplitPane getJSplitPane() {
        if (jSplitPane == null) {
            jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listName, cpanel);
            jSplitPane.setOneTouchExpandable(true);
            jSplitPane.setBorder( BorderFactory.createEmptyBorder() );
            jSplitPane.setDividerLocation(230);
        }
        return jSplitPane;
    }
}
