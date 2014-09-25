package bizcal.Main;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;

public class BizCalendarTestComponent extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;

    /**
     * @param owner
     */
    public BizCalendarTestComponent() {
        initialize();
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
        }
        return jContentPane;
    }
}
