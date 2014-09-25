package ebiNeutrinoSDK.gui.component;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;

import ebiNeutrinoSDK.EBIPGFactory;


public class EBIGRCManagement extends JPanel implements MouseListener, MouseMotionListener{

    private boolean drawSelectionRectagle = false;
    private boolean createNewEvent = false;
    public int width = 20;
    public int x = 0;
    public int y = 40;
    private HashMap<Long,TaskEvent> availableTask = new HashMap<Long,TaskEvent>();
    public long selectedRelationTask = -1;
    public boolean taskSelected = false;
    private EBIGRCManagementListener newTaskEvent=null;
    private JScrollPane pane = new JScrollPane();
    private Graphics2D g2  = null;
    private Date startDate = null;
    private long diff=-1;
    private boolean isConfigured=false;
    private BufferedImage buffImgHeader = null;

    public EBIGRCManagement() {

      pane.setViewportView(this);
      pane.setBorder(BorderFactory.createEmptyBorder());
      setOpaque(true);
      setLayout(null);
      setFocusable(true);

    }


    public void resetComponent(){
        availableTask.clear();
        removeAll();
        selectedRelationTask = -1;
        taskSelected = false;
        startDate = null;
        isConfigured = false;
        if(getMouseListeners().length >= 1){
            removeMouseListener(getMouseListeners()[0]);
        }
        repaint();
    }

    public void setStartEnd(Date date1, Date date2){
        startDate = date1;
        diff =  ((((date2.getTime() - date1.getTime()) / 1000) / 60) / 60)+24;

        if((diff % 24) > 0.0 ){
          diff = ((diff / 24) * 20) + 20;
        }else{
          diff = (diff / 24) * 20;  
        }
        buffImgHeader = null;
        pane.setPreferredSize(new Dimension(getWidth(),getHeight()));
        setPreferredSize(new Dimension((int)diff + 300 ,getHeight()+2500));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        isConfigured = true;
        repaint();
        pane.updateUI();


    }


    private void drawProjectStartEnd(long pWidth){

        GradientPaint redtowhite = new GradientPaint(0, 0, new Color(60,255,30,80), pWidth-60, 0, new Color(255,255,100,80));
        g2.setPaint(redtowhite);
        g2.fill(new RoundRectangle2D.Double(0, 0, pWidth-60 , getHeight(), 10, 10));

        GradientPaint yellowtoRed = new GradientPaint(pWidth -60,0, new Color(255,255,100,80), pWidth+60 , 0, new Color(255,0,0,90));
        g2.setPaint(yellowtoRed);
        g2.fill(new RoundRectangle2D.Double(pWidth-60 , 0,   pWidth - (pWidth - 60) , getHeight(), 10, 10));

    }

    public void paint(Graphics g){

        super.paint(g);

        g2 = (Graphics2D) g;
        g2.clearRect(0,0,getWidth(),getHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(255,255,255));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        if(isConfigured){
            
            if(diff > -1){
                drawProjectStartEnd(diff);
            }

            g2.setColor(new Color(235,235,235));

            for(int i = 0; i<= getWidth() / 20; i++){

              g2.drawLine(i*20,0,i*20,getHeight());

            }

            for(int i = 0; i<= getHeight() / 20; i++){

              g2.drawLine(0,i*20,getWidth(),i*20);

            }

            if(drawSelectionRectagle && !createNewEvent){

                g2.setColor(new Color(10,10,10));
                int w=20;
                if(!taskSelected){
                  g2.draw3DRect(x,y,w,20,true);
                }
                
            }

            if(taskSelected){
                g2.setColor(new Color(10,10,10));
               g2.drawRoundRect(x-1,y+1,width+1,18,10,10);
               //taskSelected = false;

            }

            if(createNewEvent){

              g2.setColor(new Color(10,10,10));
              g2.draw3DRect(x,y,width,20,true);

            }


            drawHeader();
            drawParentRelation();
            
        }else{

            g2.setColor(new Color(0,0,0));
            g2.drawString(EBIPGFactory.getLANG("EBI_LANG_PLEASE_SELECT_PROJECT_START_END_DATE"), 10, (getHeight() / 2)-40);
            
        }
        super.paintComponents(g2);
        g2.dispose();


    }

    private void drawParentRelation(){

       Iterator iter = availableTask.keySet().iterator();

        while(iter.hasNext()){
           long id = (Long)iter.next();
        
           TaskEvent rel = availableTask.get(id);
           if(rel != null && rel.isVisible()
                        && rel.getParents().size() >= 1 && rel.getParents().get(0) != null ){

               if(!rel.getParents().get(0).isVisible()){
                   rel.getParents().remove(0);
                   repaint();
                   return;
               }

               int y1 = rel.getY();
               int x1 = rel.getX();

               int x2=rel.getParents().get(0).getX();
               int y2=rel.getParents().get(0).getY();

                //Rigth Corner

                g2.setColor(new Color(rel.getBackgroundColor().darker().getRed(),
                                        rel.getBackgroundColor().darker().getGreen(),
                                            rel.getBackgroundColor().darker().getBlue(),70));
               
                if(x1 <= x2){
                    for(int i =x1; i<= x2; i++){
                       g2.drawLine(i,y1,i,y1);
                    }
                }else{
                  for(int i =x2; i<= x1; i++){
                      g2.drawLine(i,y1,i,y1);
                  }
                }

                g2.drawRect(x1,y1,rel.getWidth(),20);

                if(y1 <= y2){

                    for(int i =y1; i<= y2; i++){
                        g2.drawLine(x2,i,x2,i);
                    }

                }else{

                   for(int i =y2; i<= y1; i++){
                        g2.drawLine(x2,i,x2,i);
                   }
                }

                g2.drawRect(x2,y2,rel.getParents().get(0).getWidth(),20);
          }
       }

    }

    private void drawHeader(){
        if(buffImgHeader == null){
            buffImgHeader = new BufferedImage(getWidth(), 41,BufferedImage.TYPE_INT_ARGB);
            Graphics2D gbiHeader = buffImgHeader.createGraphics();

            gbiHeader.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            gbiHeader.setStroke(new BasicStroke(1.0f));

            Color startColor = new Color(255,255,255);
            Color endColor = new Color(240,240,240);

            // A non-cyclic gradient
            GradientPaint gradient = new GradientPaint(0, 0, endColor, 0, 13, startColor);

            gbiHeader.setPaint(gradient);
            gbiHeader.fill(new RoundRectangle2D.Double(0, 0, getWidth(), 40, 5, 5));
            gbiHeader.setColor(new Color(100,100,100));
            //gbiHeader.drawRoundRect(0, 0, getWidth()-1, 21, 5,5);

    	
       //g2.setColor(new Color(240,240,240));
       //g2.fill3DRect(0,0,getWidth(),40,true);

       //g2.drawLine(0,18,getWidth(),18);

               Calendar calendar1 = new GregorianCalendar();
               Calendar calendar2 = new GregorianCalendar();
               calendar2.setTime(new Date());
               if(startDate == null){
                 calendar1.setTime(new Date());
               }else{
                 calendar1.setTime(startDate);
               }

               String[] months = new DateFormatSymbols().getShortMonths();
               gbiHeader.setColor(new Color(100,100,100));

               int startDay = calendar1.get(Calendar.DAY_OF_MONTH);
               gbiHeader.setFont(new Font("Verdana",Font.PLAIN,10));

               for(int i=0; i<= getPreferredSize().width / 20; i++ ){

                   /** if(calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                            calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) ){
                       g2.setColor(new Color(25,2,255,70));
                       g2.fill3DRect(i*20+5,20,10,21,true);

                   }  **/

                   if(calendar1.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                       if(calendar1.get(Calendar.DAY_OF_MONTH) < 10){
                           gbiHeader.setColor(new Color(255,0,0));
                           gbiHeader.drawString(""+calendar1.get(Calendar.DAY_OF_MONTH),i*20+5,35);
                           gbiHeader.setColor(new Color(255,0,0,20));
                           gbiHeader.fillRect(i*20,40,20,getHeight());
                           gbiHeader.setColor(new Color(100,100,100));
                       }else{
                           gbiHeader.setColor(new Color(255,0,0));
                           gbiHeader.drawString(""+calendar1.get(Calendar.DAY_OF_MONTH),i*20+3,35);
                           gbiHeader.setColor(new Color(255,0,0,20));
                           gbiHeader.fillRect(i*20,40,20,getHeight());
                           gbiHeader.setColor(new Color(100,100,100));
                       }
                   }else{
                       gbiHeader.setColor(new Color(100,100,100));
                       if(calendar1.get(Calendar.DAY_OF_MONTH) < 10){
                           gbiHeader.drawString(""+calendar1.get(Calendar.DAY_OF_MONTH),i*20+5,35);
                       }else{
                           gbiHeader.drawString(""+calendar1.get(Calendar.DAY_OF_MONTH),i*20+3,35);
                       }
                   }

                  /**if(calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH) &&
                            calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) ){
                       g2.setColor(new Color(25,2,255,70));
                       g2.fill3DRect(i*20,40,20,getHeight(),true);
                   } **/

                   calendar1.set(Calendar.DAY_OF_MONTH,(calendar1.get(Calendar.DAY_OF_MONTH)+1));

                   if(calendar1.get(Calendar.DAY_OF_MONTH) == calendar1.getActualMaximum(Calendar.DAY_OF_MONTH) ){
                        int mon = calendar1.get(Calendar.MONTH);
                        if(mon == -1){
                           mon = 0;
                        }

                       gbiHeader.drawString(months[mon], (i * 20) - (((calendar1.getActualMaximum(Calendar.DAY_OF_MONTH) - startDay) * 20) / 2)+20, 15 );
                        startDay = 0;

                    }else if(i == getPreferredSize().width / 20 ){
                        int mon = calendar1.get(Calendar.MONTH);
                        if(mon == -1){
                           mon = 0;
                        }

                       gbiHeader.drawString(months[mon], (i * 20) - (((startDay) * 20) / 2)+20, 15 );
                        startDay = 0;
                   }
               }

        }
        // paint
        g2.drawImage(buffImgHeader, null, 0, 0);
    }


    public void showTasks(){

       Iterator kiter = this.availableTask.keySet().iterator();

       while( kiter.hasNext() ){

          Long i = (Long) kiter.next();

          if(this.availableTask.get(i) != null){
             TaskEvent evnt = this.availableTask.get(i);
             if(evnt.getId() == -1){
               evnt.setId((new Date().getTime()+availableTask.size()) * (-1));
             }
             this.add(this.availableTask.get(i),null);
          }
       }
       repaint();

    }

    public void mouseDragged(java.awt.event.MouseEvent mouseEvent){

       if(drawSelectionRectagle){
         if((mouseEvent.getX() - (mouseEvent.getX() % 20) - x + 20) >= 20){
           width = mouseEvent.getX() - (mouseEvent.getX() % 20) - x + 20;
         }
         createNewEvent = true;
       }
      repaint();
    }

    public void mouseMoved(java.awt.event.MouseEvent mouseEvent){}

    public void mouseClicked(java.awt.event.MouseEvent mouseEvent){}

    public void mousePressed(java.awt.event.MouseEvent mouseEvent){
       if(!drawSelectionRectagle){
           drawSelectionRectagle = true;
           taskSelected=false;
           x = mouseEvent.getX();
           y = mouseEvent.getY();

           if(x <= 20 && y <= 40 ){
               x = 0;
               y = 40;
           }else if(x <= 20 && y > 20){
               x = 0;
               y = y - (y % 20);
           }else if(y <=40 && x > 20){
               y = 40;
               x = x - (x % 20);
           }else{
               y = y - (y % 20);
               x = x - (x % 20);
           }
           repaint();
       }
    }

    public void mouseReleased(java.awt.event.MouseEvent mouseEvent){

       if(createNewEvent){
         TaskEvent taskEvent = new TaskEvent(this);
         taskEvent.setId(new Date().getTime()+availableTask.size());
         taskEvent.setDuration(width);
         taskEvent.setLocation(x,y);
 
         if(newTaskEvent != null){
           if(newTaskEvent.addNewTaskAction(taskEvent)){
              availableTask.put(taskEvent.getId(),taskEvent);
              this.add(taskEvent,null);
              taskSelected = true;
           }
         }
         repaint();
         createNewEvent = false;
       }

       drawSelectionRectagle = false;

    }

    public void mouseEntered(java.awt.event.MouseEvent mouseEvent){}

    public void mouseExited(java.awt.event.MouseEvent mouseEvent){}

    public void addEventListener(EBIGRCManagementListener actionEvent){
        newTaskEvent = actionEvent;
    }

    public EBIGRCManagementListener getEventListener(){
        return newTaskEvent;
    }

    public void setAvailableTask(HashMap<Long,TaskEvent> aTask){
       this.availableTask = aTask;
    }

    public HashMap<Long,TaskEvent> getAvailableTasks(){
        return this.availableTask;
    }

    public JComponent getScrollComponent(){
        return pane;
    }



}

