package ebiNeutrinoSDK.gui.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;

/**
 * 
 * Extends the default JDialog with the possibility
 * to store the location and size
 */

public class EBIDialog extends JDialog {

	private boolean storeLocation  = false;
	private boolean storeSize      = false;
    private boolean isModal        = false;
    private Dimension originalDimension = new Dimension();
    protected EBIPropertiesRW properties = null;
    private boolean closableEscape = true;
    private EBIMain ebiMain = null;
    private boolean haveSerial = false;

	public EBIDialog(EBIMain owner) {
		super(owner);
        ebiMain = owner;
		initialize();
        properties = EBIPropertiesRW.getPropertiesInstance(new File("./config/dialogstore.properties"),true,true);
    }

    /**
     * store the size
     * @param store
     */
	public void storeSize(boolean store){
		this.storeSize = store;
	}

    /**
     * store the loacation
     * @param store
     */

    public void storeLocation(boolean store){
	    this.storeLocation = store;
    }

    private void dialogIn(){

	   try{
           originalDimension.setSize(this.getWidth(),this.getHeight());
           if(isResizable()){
                Dimension dim = new Dimension();
                try{

                    dim.width  = (int)Double.parseDouble(properties.getValue("EBI_DIALOG_SIZE_"+this.getName().toUpperCase()+"_WIDTH"));
                    dim.height = (int)Double.parseDouble(properties.getValue("EBI_DIALOG_SIZE_"+this.getName().toUpperCase()+"_HEIGHT"));
                    this.setSize(dim);

                }catch(java.lang.NumberFormatException ex){
                    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                    Dimension frameSize = getSize();
                    this.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
                }
           }
           try{
                Point pt = new Point();
                pt.x = (int)Double.parseDouble(properties.getValue("EBI_DIALOG_LOCATION_"+this.getName().toUpperCase()+"_X"));
                pt.y = (int)Double.parseDouble(properties.getValue("EBI_DIALOG_LOCATION_"+this.getName().toUpperCase()+"_Y"));

                if(pt.x > 0 && pt.y > 0){
                    this.setLocation(pt);
                }else{
                    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                    Dimension frameSize = getSize();
                    this.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
                }

           }catch(java.lang.NumberFormatException ex){
                Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension frameSize = getSize();
                this.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
           }

	   }catch(NullPointerException ex){
		   Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	       Dimension frameSize = getSize();
	       this.setLocation((d.width - frameSize.width) / 2, ((d.height-150) - frameSize.height) / 2);
	   }
   }

   protected void dialogOut(){
	   try{
           if(this.storeSize == true){
                Dimension dim = this.getSize();
                properties.setValue("EBI_DIALOG_SIZE_"+this.getName().toUpperCase()+"_WIDTH", ""+dim.getWidth());
                properties.setValue("EBI_DIALOG_SIZE_"+this.getName().toUpperCase()+"_HEIGHT", ""+dim.getHeight());
                properties.saveProperties();
           }
           if(this.storeLocation == true){
                Point pt = this.getLocation();
                properties.setValue("EBI_DIALOG_LOCATION_"+this.getName().toUpperCase()+"_X", ""+pt.getX());
                properties.setValue("EBI_DIALOG_LOCATION_"+this.getName().toUpperCase()+"_Y", ""+pt.getY());
                properties.saveProperties();
           }
	   }catch(NullPointerException ex){ex.printStackTrace();}
   }

   @Override
   public void setVisible(final boolean isVisible){

       if(isVisible == false){
          dialogOut();
          dispose();
          if(ebiMain != null && isHaveSerial()){
             ebiMain.guiRenderer.removeGUIObject(getName());
          }
       }else{
          dialogIn();
          getRootPane().setBorder(new LineBorder(new Color(190, 190, 190)));
          InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
          setFocusableWindowState(true);
          setFocusable(true);

          Action closeAction = new AbstractAction(){
              public void actionPerformed(ActionEvent e) {
                 
                 setVisible(false);
              }
          };
          
          inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "CLOSE1");
          getRootPane().getActionMap().put("CLOSE1", closeAction);

       }

       super.setVisible(isVisible);
   }

    /**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.addKeyListener(new java.awt.event.KeyAdapter() {
		    public void keyPressed(KeyEvent e) {
	                if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
	                	dialogOut();
	                	setVisible(false);
                        dispose();
	                }
			    }
		});
	}

    public Dimension getOriginalDimension(){
        return originalDimension;
    }
    

    @Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        Action actionListener = new AbstractAction() {
          public void actionPerformed(ActionEvent actionEvent) {
           if(isClosableEscape()){
                setVisible(false);
           }
          }
        };
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");
        rootPane.getActionMap().put("ESCAPE", actionListener);

    return rootPane;
  }

  public boolean isClosableEscape() {
        return closableEscape;
  }

  public boolean isHaveSerial(){
        return haveSerial;
  }

  public void setHaveSerial(boolean haveSerial){
        this.haveSerial = haveSerial;
  }
}