package ebiNeutrino.core.GUIDesigner.panel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ebiNeutrino.core.GUIDesigner.DesignerPanel;


public class EBICustomPanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

    private EBIVisualDesignerPanelTemplate val = null;
    private Component bnt = null;
    private boolean isPanelDragged = false;
    private boolean isPressed = false;

    public EBICustomPanel(EBIVisualDesignerPanelTemplate temp){
        val = temp;
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);


    }

    public void mouseMoved(java.awt.event.MouseEvent e) {

          if(val.isDrawFocusReactangle()){
                int mousex = e.getX();
                int mousey = e.getY() + DesignerPanel.HEADER_HEIGHT;

                JComponent comp = ((JComponent)e.getSource());
                if(!comp.getName().equals(val.mainComponentName)){
                    String compName = "";
                    int cx =0;
                    int cy =0;
                    while(!val.mainComponentName.equals(compName)){
                        cx += comp.getX();
                        cy += comp.getY();

                        mousex =  e.getX() + cx;
                        mousey =  (e.getY() + cy) + DesignerPanel.HEADER_HEIGHT;

                        if(comp.getParent() != null  && comp.getParent() instanceof JComponent){
                           comp  =  (JComponent)comp.getParent();
                        }
                       compName = comp.getName();
                    }
                }

                 if(mousex >= val.getComponentX()-10 && mousey >= val.getComponentY()-10 && mousex <= val.getComponentX()-2 && mousey <= val.getComponentY()-2 ){
                     if(DesignerPanel.drawLeftCorner && DesignerPanel.drawTopCorner){
                      setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                      //System.out.println("1");
                      val.resize = true;
                      val.resizeFinal = false;
                     }
                 }else if(mousex >= val.getComponentX()+val.getComponentW()-5 && mousey >= val.getComponentY()-5 && mousex <= val.getComponentX()+val.getComponentW()+5 && mousey <= val.getComponentY()+5 ){
                     if(DesignerPanel.drawRightCorner && DesignerPanel.drawTopCorner){
                       setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                       //System.out.println("2");
                       val.resize = true;
                       val.resizeFinal = false;
                     }

                 }else if(mousex >= val.getComponentX()+val.getComponentW()-5 && mousey >= val.getComponentY()+val.getComponentH()-5 && mousex <= val.getComponentX()+val.getComponentW()+5 && mousey <= val.getComponentY()+val.getComponentH()+5 ){
                    if(DesignerPanel.drawRightCorner && DesignerPanel.drawBottomCorner){
                     setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
                     //System.out.println("3");
                     val.resize = true;
                     val.resizeFinal = false;
                    }
                 }else if(mousex >= val.getComponentX()-5 && mousey >= val.getComponentY()+val.getComponentH()-5 && mousex <= val.getComponentX()+5 && mousey <= val.getComponentY()+val.getComponentH()+5 ){
                    if(DesignerPanel.drawLeftCorner && DesignerPanel.drawBottomCorner){
                     setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
                     //System.out.println("4");
                     val.resize = true;
                     val.resizeFinal = false;
                    }
                 }else if(!isPressed){
                   if(val.insertNewComponent){
                     setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));   
                   }else if(!isPanelDragged){
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                   }
                   val.resize = false;
                   val.resizeFinal = true;
                 }
               }
        }
      // Add Mouse Drag event for each Items
    public void mouseDragged(java.awt.event.MouseEvent e){

           if( val.resize && !val.showPopup ){
                
            int mousex;
            int mousey;

            if(!val.mainComponentName.equals(((JComponent)e.getSource()).getName())){
               if(!bnt.getParent().getName().equals(val.mainComponentName)){
                   mousex = e.getX();
                   mousey = e.getY();
                   //System.out.println("Drag1");
               }else{
                   mousex = e.getXOnScreen() - val.ebiMain.getX() - DesignerPanel.HEADER_HEIGHT -10;
                   mousey = e.getYOnScreen() - val.ebiMain.getY() - ((EBICustomPanel)e.getSource()).getY()  - DesignerPanel.HEADER_HEIGHT -130;
                   //System.out.println("Drag2");
               }

            }else{
                //System.out.println("Drag3");
                if(bnt != null && bnt.getParent().getName() != null && !val.mainComponentName.equals(bnt.getParent().getName())){
                    return;
                }else{
                   mousex = e.getX() + ((JComponent)e.getSource()).getX();
                   mousey = Math.abs(e.getY());
                }
            }

               if(val.resizeNW){
                  int oldX = bnt.getX();
                  int oldY = bnt.getY();
                  bnt.setBounds(mousex,mousey,bnt.getWidth()+(oldX-mousex),Math.abs(bnt.getHeight()+(oldY-mousey)));

               }else if(val.resizeNE){
                  int oldY = bnt.getY();
                  int oldX = bnt.getX();
                  // int oldW = bnt.getWidth();
                  bnt.setBounds(bnt.getX(),mousey,mousex - oldX, Math.abs(bnt.getHeight()+(oldY-mousey)));

               }else if(val.resizeNW1){
                  int oldY = bnt.getY();
                  int oldX = bnt.getX();
                  //int oldW = bnt.getWidth();
                  bnt.setBounds(bnt.getX(),bnt.getY(),mousex - oldX, Math.abs(mousey - oldY));

               }else if(val.resizeNE1){
                  int oldY = bnt.getY();
                  int oldX = bnt.getX();
                  //int oldW = bnt.getWidth();
                  bnt.setBounds(mousex,bnt.getY(),bnt.getWidth()+(oldX-mousex), Math.abs(mousey - oldY));

               }
               if(bnt instanceof JPanel){
                    ((JPanel)bnt).updateUI();
               }
                    int x1  =0;
                    int y1  =0;
                    JComponent component  = (JComponent) bnt;
                    while(!component.getName().equals(val.mainComponentName)){
                         x1 += component.getX();
                         y1 += component.getY();
                         if(component.getParent() != null){
                            component = (JComponent) component.getParent();
                         }
                    }
                    val.setComponentX(x1);
                    val.setComponentY(y1+DesignerPanel.HEADER_HEIGHT);
                    val.setComponentW(bnt.getWidth()-1);
                    val.setComponentH(bnt.getHeight()-1);
                    if(isPanelDragged == false){
                        isPanelDragged = true;
                    }

               setBeanProperties((JComponent)bnt);
            }
         
      }

    public void mouseClicked(java.awt.event.MouseEvent e) {}

    public void mouseEntered(java.awt.event.MouseEvent e) {}

    public void mouseExited(java.awt.event.MouseEvent e) {}

    public void mousePressed(java.awt.event.MouseEvent e){
         val.setDrawFocusRectangle(false);
         repaint();
         grabFocus();
            bnt = val.componentsTable.get(val.selectedComponentName);

            int mousex = e.getX();
            int mousey = e.getY() + DesignerPanel.HEADER_HEIGHT;

            JComponent comp = ((JComponent)e.getSource());
            if(!comp.getName().equals(val.mainComponentName)){
                String compName = "";
                int cx =0;
                int cy =0;
                while(!val.mainComponentName.equals(compName)){
                    cx += comp.getX();
                    cy += comp.getY();

                    mousex =  e.getX() + cx;
                    mousey =  e.getY() + cy+DesignerPanel.HEADER_HEIGHT;

                    if(comp.getParent() != null  && comp.getParent() instanceof JComponent){
                       comp  =  (JComponent)comp.getParent();
                    }
                   compName = comp.getName();
                }

        }
         
        if(mousex >= val.getComponentX()-10 && mousey >= val.getComponentY()-10 && mousex <= val.getComponentX()-2 && mousey <= val.getComponentY()-2 ){
           setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
           val.resizeNW = true;
         }else if(mousex >= val.getComponentX()+val.getComponentW()-5 && mousey >= val.getComponentY()-5 && mousex <= val.getComponentX()+val.getComponentW()+5 && mousey <= val.getComponentY()+5 ){
           setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
           val.resizeNE = true;
         }else if(mousex >= val.getComponentX()+val.getComponentW()-5 && mousey >= val.getComponentY()+val.getComponentH()-5 && mousex <= val.getComponentX()+val.getComponentW()+5 && mousey <= val.getComponentY()+val.getComponentH()+5 ){
           setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));
           val.resizeNW1 = true;
         }else if(mousex >= val.getComponentX()-5 && mousey >= val.getComponentY()+val.getComponentH()-5 && mousex <= val.getComponentX()+5 && mousey <= val.getComponentY()+ val.getComponentH()+5 ){
           setCursor(new Cursor(Cursor.NE_RESIZE_CURSOR));
           val.resizeNE1 = true;
         }else{
           setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         }
         isPressed = true;
     }

	 // Mouse Release Action
	 public void mouseReleased(java.awt.event.MouseEvent e){
         val.setDrawFocusRectangle(true);
         repaint();
         if(val.resize  && isPanelDragged){
            val.setTabSave(false);
         }

         val.resizeNW = false;
         val.resizeNE = false;
         val.resizeNW1 = false;
         val.resizeNE1 = false;
         isPanelDragged = false;
         val.resize = false;
         if(((JComponent)e.getSource()).getName().equals(val.mainComponentName) &&
                                                            val.resizeFinal == true ){
             selectMainComponent((JComponent)e.getSource());
         }
         isPressed = false;
     }

    public void selectMainComponent(JComponent comp){
         updatePanelSelection(comp);
    }


    public void keyPressed(KeyEvent e){

     if(val.isDrawFocusReactangle()){
        val.setDrawFocusRectangle(false);
        val.repaint(((JComponent)e.getSource()).getX(),((JComponent)e.getSource()).getY(),
                     ((JComponent)e.getSource()).getWidth(),((JComponent)e.getSource()).getHeight()+DesignerPanel.HEADER_HEIGHT);
     }

     if(val.componentsTable.get(val.selectedComponentName)!= null){

         JComponent comp =  val.componentsTable.get(val.selectedComponentName);
         int key = e.getKeyCode();
         
          if(e.isAltDown()){
             if (key == KeyEvent.VK_UP ){
                comp.setLocation(comp.getX(),comp.getY()-1);
                val.setTabSave(false);
             }else if (key == KeyEvent.VK_RIGHT ){
                comp.setLocation(comp.getX()+1,comp.getY());
                 val.setTabSave(false);
             }else if (key == KeyEvent.VK_DOWN){
               comp.setLocation(comp.getX(),comp.getY()+1);
                 val.setTabSave(false);
             }else if (key == KeyEvent.VK_LEFT){
               comp.setLocation(comp.getX()-1,comp.getY());
                 val.setTabSave(false);
            }
          }
          if(e.isShiftDown()){

              if(key == KeyEvent.VK_UP){

                  comp.setBounds(comp.getX(),comp.getY(),comp.getWidth(),comp.getHeight()-1);
                  if(comp instanceof JPanel){
                    comp.updateUI();
                  }
                 val.setTabSave(false);
               }else if(key == KeyEvent.VK_RIGHT){

                  comp.setBounds(comp.getX(),comp.getY(),comp.getWidth()+1,comp.getHeight());
                  if(comp instanceof JPanel){
                    comp.updateUI();
                  }
                val.setTabSave(false);
               }else if(key == KeyEvent.VK_DOWN){

                  comp.setBounds(comp.getX(),comp.getY(),comp.getWidth(),comp.getHeight()+1);
                  if(comp instanceof JPanel){
                    comp.updateUI();
                  }
                val.setTabSave(false);
               }else if(key == KeyEvent.VK_LEFT){

                  comp.setBounds(comp.getX(),comp.getY(),comp.getWidth()-1,comp.getHeight());
                  if(comp instanceof JPanel){
                    comp.updateUI();
                  }
                val.setTabSave(false);
               }
          }

          int x1=0;
          int y1=0;
          JComponent component = comp;
          if(component != null ){

                while(!component.getName().equals(val.mainComponentName)){
                   x1 += component.getX();
                   y1 += component.getY();
                   if(component.getParent() != null){
                      component = (JComponent) component.getParent();
                   }   
                }

                val.setComponentX(x1);
                val.setComponentY(y1+DesignerPanel.HEADER_HEIGHT);
                val.setComponentW(comp.getWidth()-1);
                val.setComponentH(comp.getHeight()-1);

           }else{
                val.setComponentX(((JComponent)e.getSource()).getX());
                val.setComponentY(((JComponent)e.getSource()).getX()+DesignerPanel.HEADER_HEIGHT);
                val.setComponentW(((JComponent)e.getSource()).getWidth()-1);
                val.setComponentH(((JComponent)e.getSource()).getHeight()-1);

          }

          setBeanProperties(comp);
          if(key == KeyEvent.VK_DELETE){
              val.deleteSelectedComponent();
          }
       }
         /*if(e.isControlDown()){
              if(e.getKeyCode() == KeyEvent.VK_S){
                 val.showCode();
                 val.saveXMLToFile();
                 val.setTabSave(true);
              }
          }*/
    }

    public void keyReleased(KeyEvent e){
      boolean allUp = false;
      if(!e.isShiftDown()){ 
         allUp = true;
      }else if(!e.isShiftDown()){
          allUp = true;
      }
      if(allUp){
       val.setDrawFocusRectangle(true);
       val.repaint(((JComponent)e.getSource()).getX(),((JComponent)e.getSource()).getY(),
                        ((JComponent)e.getSource()).getWidth(),((JComponent)e.getSource()).getHeight()+DesignerPanel.HEADER_HEIGHT);
      }

   }
    public void keyTyped(KeyEvent e){}

    
    public void paintComponent(Graphics g) {
       super.paintComponent(g);

       Graphics2D g2 = (Graphics2D) g;
         for(int a= 10; a<=getHeight(); a+=10){
          for(int i=10; i<=getWidth(); i+=10){
             g2.setColor(Color.BLACK);
             g2.drawLine(i,a,i,a);
          }
         }
    }

    public void updatePanelSelection(JComponent comp){
          val.setComponentX(comp.getX());
          val.setComponentY(comp.getY());
          val.setComponentW(comp.getWidth()-1);
          val.setComponentH(comp.getHeight()-1);
          val.setBackground(Color.GRAY);
          val.setSelectedBean(val.mainComponentBean);
    }

    private void setBeanProperties(JComponent selComponent){

         if(!val.getSelectedBean().getName().equals(selComponent.getName())){
             val.getComponentFromBeanList(val.mainComponentBean,selComponent.getName());
         }

           val.getSelectedBean().setPoint(new Point(selComponent.getX(),selComponent.getY()));

           Dimension dim = selComponent.getSize();

           if(val.getSelectedBean().isWidthAutoResize()){
              if(dim.getWidth() >100){
                dim.width = 100;
              }
           }

           if(val.getSelectedBean().isHeightAutoResize()){
               if(dim.getHeight() > 100){
                   dim.height = 100;
               }
           }

           val.getSelectedBean().setDimension(dim);
           //val.setTabIsSaved(false);
           val.isChagingProperties = true;
    }
}