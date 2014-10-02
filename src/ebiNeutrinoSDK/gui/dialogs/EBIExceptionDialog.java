package ebiNeutrinoSDK.gui.dialogs;


import java.awt.*;

import javax.swing.*;

import org.apache.log4j.Logger;

import ebiNeutrinoSDK.EBIPGFactory;



public class EBIExceptionDialog extends JDialog {

	private JPanel jContentPane = null;
	public static final JTextArea jTextException = new JTextArea();
	public ImageIcon image = null;
	public JScrollPane pane=null;
	private JScrollPane jScrollPane = null;
	public  String Msg="";
	private static final Logger logger = Logger.getLogger (EBIExceptionDialog.class.getName());
    private static EBIExceptionDialog messageDialog = null;


	public EBIExceptionDialog(){
        setAlwaysOnTop(true);
		image = new ImageIcon("images/exception.gif");
		jTextException.setBounds(new java.awt.Rectangle(126,0,421,246));
		jTextException.setEditable(false);
		jTextException.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
        jTextException.setText(Msg);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        this.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
		//storeLocation(true);
        //storeSize(true);
		initialize();
        if(logger != null){
          logger.error(""+Msg);
        }

	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(557, 276);
		this.setTitle("EBI system Exception");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			JLabel jLabel=new JLabel(image);
			jLabel.setBounds(new java.awt.Rectangle(1, 0, 125, 248));
			jLabel.setBackground(new Color(200,200,200));
			jLabel.setText("");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.setBackground(new Color(200,200,200));
			jContentPane.add(jLabel, null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane(jTextException);
			jScrollPane.setBounds(new java.awt.Rectangle(129,0,421,249));
		}
		return jScrollPane;
	}
	/**
	 * Show EBIMessageBox dialog
	 * @param  msg
	 * @return
	 */

	public boolean Show(EBIMessage msg){
		boolean toRet = false;
            if(msg == EBIMessage.NEUTRINO_DEBUG_MESSAGE){
                setModal(true);
                jTextException.setText(Msg);

                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension frameSize = getSize();
                setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
                setVisible(true);
            }
            else{
                if(msg == EBIMessage.INFO_MESSAGE){
                    JOptionPane.showMessageDialog(null,Msg, EBIPGFactory.getLANG("EBI_LANG_INFO") == null ? "Info" : EBIPGFactory.getLANG("EBI_LANG_INFO")  ,JOptionPane.INFORMATION_MESSAGE);
                    toRet=false;
                }
                else if(msg == EBIMessage.INFO_MESSAGE_YESNO){
                    if(JOptionPane.showConfirmDialog(null,Msg, EBIPGFactory.getLANG("EBI_LANG_INFO") == null ? "Info" : EBIPGFactory.getLANG("EBI_LANG_INFO")  , JOptionPane.YES_NO_OPTION,JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION){
                        toRet = true;
                    }
                    else{
                        toRet = false;
                    }
                }else if(msg == EBIMessage.ERROR_MESSAGE){
                    JOptionPane.showMessageDialog(this, Msg, EBIPGFactory.getLANG("EBI_LANG_ERROR") == null ? "Error" : EBIPGFactory.getLANG("EBI_LANG_ERROR") , JOptionPane.ERROR_MESSAGE);
                    toRet=false;
                }else if(msg == EBIMessage.WARNING_MESSAGE_YESNO){
                    if(JOptionPane.showConfirmDialog(null,Msg, EBIPGFactory.getLANG("EBI_LANG_WARRNING") == null ? "Warning" : EBIPGFactory.getLANG("EBI_LANG_WARRNING") , JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
                        toRet =true;
                    }
                    else{
                        toRet =false;
                    }
                }
            }
           dispose();
		return toRet;
	}

    public void info(String msg){
        Msg = msg;
        Show(EBIMessage.INFO_MESSAGE);
    }

    public void error(String msg){
        Msg = msg;
        Show(EBIMessage.ERROR_MESSAGE);
    }

    public boolean warning(String msg){
        Msg = msg;
        return Show(EBIMessage.WARNING_MESSAGE_YESNO);
    }

    public boolean infoYesNo(String msg){
        Msg = msg;
        return Show(EBIMessage.INFO_MESSAGE_YESNO);
    }

    public boolean debug(String msg){
        Msg = msg;
        return Show(EBIMessage.NEUTRINO_DEBUG_MESSAGE);
    }
    
    public static EBIExceptionDialog getInstance(String Message){

        if(messageDialog == null){    
           messageDialog = new EBIExceptionDialog();
        }
        messageDialog.Msg = Message;
        logger.error(Message);
      return messageDialog;
    }

    public static EBIExceptionDialog getInstance(){

        if(messageDialog == null){
           messageDialog = new EBIExceptionDialog();
        }
      return messageDialog;
    }
}
