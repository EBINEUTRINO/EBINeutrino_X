package ebiNeutrino.core.settings;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.*;

import ebiNeutrino.core.EBIMain;
import ebiNeutrino.core.EBIVersion;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.utils.EBIConstant;

public class EBISystemSettingStart extends JPanel {

    private JTextArea infoArea = null;
    private EBIMain ebiMain = null;
    /**
     * This is the default constructor
     */
    public EBISystemSettingStart(EBIMain ebiMain) {
        super();
        this.ebiMain = ebiMain;
        initialize();
        setBackground(EBIPGFactory.systemColor);
        setInfo();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {

        JLabel jLabel1=new JLabel();
        jLabel1.setBounds(new java.awt.Rectangle(20, 10, 30, 30));
        jLabel1.setIcon(EBIConstant.ICON_SETTING);
        jLabel1.setOpaque(false);
        jLabel1.setText("");
        JLabel jLabel=new JLabel();
        jLabel.setBounds(new java.awt.Rectangle(70, 15, 220, 20));
        jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
        jLabel.setOpaque(false);
        jLabel.setText(EBIPGFactory.getLANG("EBI_LANG_SYSTEM_SETTING"));
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setBackground(EBIPGFactory.systemColor);
        infoArea.setBounds(new java.awt.Rectangle(20, 60, 600, 500));
        this.setLayout(null);
        this.setSize(842, 549);
        this.add(jLabel, null);
        this.add(jLabel1, null);
        this.add(infoArea,null);
    }

    private void setInfo(){

        StringBuffer buf = new StringBuffer();
        int mb = 1024*1024;
        ResultSet set=null;
        Runtime runtime = Runtime.getRuntime();
        buf.append("##### Environment Information ##### \n\n");
        buf.append("EBI Neutrino Version: "+ EBIVersion.getInstance().getVersion()+"\n");
        buf.append("Java Version: "+System.getProperty("java.version")+"\n");
        buf.append("Java Vendor: "+System.getProperty("java.vm.vendor")+"\n");
        buf.append("JVM Name: "+System.getProperty("java.vm.name")+"\n");
        buf.append("Country: "+System.getProperty("user.country")+"\n");
        buf.append("Operating System: "+System.getProperty("os.name")+"\n");
        buf.append("\n\n");
        buf.append("##### Heap statistics [MB] #####\n\n");
        buf.append("Used Memory :"+ (runtime.totalMemory() - runtime.freeMemory()) / mb+"\n");
        buf.append("Free Memory :"+ runtime.freeMemory() / mb+"\n");
        buf.append("Total Memory :"+ runtime.totalMemory() / mb+"\n");
        buf.append("Max Memory :"+ + runtime.maxMemory() / mb+"\n\n\n");

        buf.append("##### MySQL Database INFO #####\n\n");
        try {
            set = ebiMain._ebifunction.getIEBIDatabase().execute("SHOW VARIABLES LIKE 'version%'");

            while(set.next()){
               buf.append(set.getString("Variable_name")+": "+set.getString("Value")+"\n");
            }
            if(set != null){
                set.close();
            }
        } catch (SQLException e) {
            if(set!= null){
                try {
                    set.close();
                } catch (SQLException e1) {}
            }
            e.printStackTrace();
        }

        infoArea.setText(buf.toString());
    }
}  

