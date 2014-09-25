package ebiCRM.gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;

import bizcal.Main.BizcalMain;
import ebiCRM.EBICRMModule;
import ebiNeutrinoSDK.EBIPGFactory;


public class EBICRMCalendar extends JPanel {

    private BizcalMain calendar = null;
    private JPanel jPanel = null;
    private EBICRMModule ebiModule = null;

    public EBICRMCalendar(EBICRMModule module) {
        ebiModule = module;
        setName("Calendar");
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    public void initialize() {
        this.setSize(new Dimension(990, 492));
        this.setLayout(new BorderLayout());
        this.add(getJPanel(), BorderLayout.CENTER);
    }

    BizcalMain getEBICalanderComponent() {
        if (calendar == null) {
            calendar = new BizcalMain(EBIPGFactory.selectedLanguage, EBIPGFactory.ebiUser,ebiModule);
        }
        return calendar;
    }

    public void refresh(){
        if(calendar != null){
            try {
                calendar.reload();
            } catch (Exception e) {
                e.printStackTrace(); 
            }
        }
    }

    /**
     * This method initializes jPanel	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            jPanel = new JPanel();
            jPanel.setLayout(new BorderLayout());
            jPanel.setBounds(new Rectangle(4, 62, 979, 395));
            
            try {
                jPanel.add(getEBICalanderComponent().initDayView(), BorderLayout.CENTER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jPanel;
    }
} 

