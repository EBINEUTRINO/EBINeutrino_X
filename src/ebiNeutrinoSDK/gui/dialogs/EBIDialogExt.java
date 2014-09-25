package ebiNeutrinoSDK.gui.dialogs;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import ebiNeutrino.core.EBIMain;
import ebiNeutrinoSDK.gui.component.EBIVisualPanelTemplate;
import ebiNeutrinoSDK.utils.EBIConstant;
import ebiNeutrinoSDK.utils.EBIPropertiesRW;
import ebiNeutrinoSDK.utils.EBITitleBorder;

/**
 *
 * Extends the EBIDilaog
 */

public class EBIDialogExt extends EBIDialog implements MouseMotionListener, MouseListener {
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean isResize = false;
    private boolean resizeRight = false;
    private boolean resizeLeft = false;
    private boolean resizeBottom = false;
    private boolean isCloseDialog = false;
    private boolean resizeSE = false;
    private boolean resizeSW = false;
    private boolean isClosable = true;
    private boolean buttonPressed = false;

	public EBIDialogExt(EBIMain owner) {
		super(owner);
	   // setUndecorated(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        setFocusableWindowState(true);
        setFocusable(true);
        pack();
        
	}

    public void mouseMoved(java.awt.event.MouseEvent e) {
          e.consume();
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          isResize=false;
          resizeLeft = false;
          resizeRight = false;
          resizeBottom = false;
          isResize=false;
          isCloseDialog = false;
          resizeSE = false;
          resizeSW = false;

         if(e.getYOnScreen() >= getY()+getHeight()-4 && e.getYOnScreen() <= getY() +getHeight() &&
                                e.getXOnScreen() >= getX() && e.getXOnScreen() <= getX()+4 && isResizable()){
             setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));     // Resize width and height NE
             isResize = true;
             resizeSE = true;
         }else if(e.getYOnScreen() >= getY()+getHeight()-4 && e.getYOnScreen() <= getY() +getHeight() &&
                                e.getXOnScreen() >= getX()+getWidth()-4 && e.getXOnScreen() <= getX()+getWidth() && isResizable()){
             setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));     // Resize width and height NW

             isResize = true;
             resizeSW = true;
         }else if( e.getXOnScreen() >= ((getWidth()-4)+getX()) && e.getXOnScreen() <= (getWidth()+getX()) && isResizable()){
             setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));      //Resize width right
             resizeRight = true;
             isResize = true;
         }else if(e.getXOnScreen() <= getX()+4 && e.getXOnScreen() >= getX() && isResizable()){
             setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));     // Resize width left
             resizeLeft = true;
             isResize=true;
         }else if(e.getYOnScreen() >= ((getHeight()-4)+getY()) && e.getYOnScreen() <= (getHeight()+getY()) && isResizable()){
             setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));   //Resize height
             resizeBottom = true;
             isResize=true;
         }else if(e.getXOnScreen()>= (getX()+getWidth())-25 && e.getXOnScreen() <= (getX()+getWidth())-10 &&
                  e.getYOnScreen()>= getY()+4 && e.getYOnScreen()<= getY()+18 && isClosable()){     //Close Dialog
             isCloseDialog = true;
         }
    }
    
    public void mouseExited(java.awt.event.MouseEvent e) {if(!buttonPressed){setCursor(new Cursor(Cursor.DEFAULT_CURSOR));}}

    public void mouseDragged(java.awt.event.MouseEvent e){

        if(isResize == false){
         setLocation((e.getXOnScreen() - offsetX), (e.getYOnScreen() - offsetY));
        }else{
            if(resizeRight){
               setSize(e.getXOnScreen()-getX(), getHeight());
            }else if(resizeBottom){
               setSize(getWidth(),e.getYOnScreen()-getY());
            }else if(resizeLeft){
               setBounds(e.getXOnScreen(),getY(),getWidth()+(getX()-e.getYOnScreen()),getHeight());
            }else if(resizeSE){
               setBounds(e.getXOnScreen(),getY(),getWidth()+(getX()-e.getXOnScreen()),e.getYOnScreen()-getY());
            }else if(resizeSW){
               setSize(e.getXOnScreen()-getX(),e.getYOnScreen()-getY());
            }
        }

    }

    public void mousePressed(java.awt.event.MouseEvent e){
        offsetX  = e.getX();
        offsetY = e.getY();
        buttonPressed = true;
    }

    public void mouseClicked(java.awt.event.MouseEvent e){}

    public void mouseReleased(java.awt.event.MouseEvent e){
        isResize=false;
        if(isCloseDialog){
            onCloseDialog();
        }
        buttonPressed = false;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        if(getContentPane() instanceof  EBIVisualPanelTemplate){
            ((EBIVisualPanelTemplate)getContentPane()).updateUI();
        }
    }

    public void mouseEntered(java.awt.event.MouseEvent e){}


   protected void onCloseDialog(){
      super.setVisible(false);
   }

   public void setClosable(boolean isClosable){
       this.isClosable = isClosable;
   }

   public boolean isClosable(){
       return this.isClosable;
   }


}
