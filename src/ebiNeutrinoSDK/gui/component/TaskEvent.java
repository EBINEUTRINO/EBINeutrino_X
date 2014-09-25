package ebiNeutrinoSDK.gui.component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import ebiNeutrinoSDK.EBIPGFactory;

public class TaskEvent extends JComponent implements MouseListener, MouseMotionListener, KeyListener {
    private String name="";
    private long id = -1;
    private List<TaskEvent> parents = new ArrayList<TaskEvent>();
    private int duration=0;
    private int reached =0;
    private String description="";
    private Color backgroundColor = new Color(120,20,255);
    private boolean startDrag = false;
    private boolean moveEvent = false;
    private boolean resizeEvent = false;
    private int offsetX = 0;
    private int offsetY = 0;
    private String status="";
    private String type="";
    private JPopupMenu popUp = new JPopupMenu();
    public JMenuItem relationFrom = null;
    public JMenuItem relationTo = null;
    private boolean isControl = false;
    private boolean rightClick = false;

    private EBIGRCManagement container = null;

    public TaskEvent(EBIGRCManagement cont){
        this.container = cont;
        setOpaque(true);
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        final String relationText = EBIPGFactory.getLANG("EBI_LANG_RELATION_TO");
        relationFrom = new JMenuItem(relationText);
        relationFrom.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){

                container.selectedRelationTask = getId();
                relationFrom.setText(relationFrom.getText()+" * ");
            }
        });
        popUp.add(relationFrom);


        relationTo = new JMenuItem(EBIPGFactory.getLANG("EBI_LANG_RELATION_FROM"));
        relationTo.setEnabled(false);
        relationTo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
               container.getEventListener().createRelation(container.selectedRelationTask,getId());
               container.getAvailableTasks().get(container.selectedRelationTask).setParent(TaskEvent.this);
               container.getAvailableTasks().get(container.selectedRelationTask).relationFrom.setText(relationText);

               container.selectedRelationTask = -1;
               relationTo.setEnabled(false); 
               container.repaint();
            }
        });
        popUp.add(relationTo);

        JMenuItem edit = new JMenuItem(EBIPGFactory.getLANG("EBI_LANG_EDIT_TASK"));
        edit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){

               if(container.getEventListener().editTaskAction(TaskEvent.this, true)){
                    repaint();
               }

            }
        });
        popUp.add(edit);
        popUp.addSeparator();
        JMenuItem delete = new JMenuItem(EBIPGFactory.getLANG("EBI_LANG_DELETE_TASK"));
        delete.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent event){
             if(container.getEventListener().deleteTaskAction(TaskEvent.this)){   
               if(container.getAvailableTasks().get(getId()) != null){
                  container.getAvailableTasks().remove(getId());
                  setVisible(false);
                  container.repaint(); 
               }
             }
            }
        });
        popUp.add(delete);

    }


    public void paintComponent(Graphics gs){
        super.paintComponents(gs);
        Graphics2D g2  = (Graphics2D) gs;
        BufferedImage buffImgHeader = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbiHeader = buffImgHeader.createGraphics();

        gbiHeader.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        gbiHeader.setStroke(new BasicStroke(1.0f));
        Color startColor = this.getBackgroundColor().brighter();

        Color endColor = this.getBackgroundColor();

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(0, 0, endColor, 0, 30, startColor);

        gbiHeader.setPaint(gradient);
        gbiHeader.fill(new RoundRectangle2D.Double(0, 2, getWidth(), 17, 10, 10));
        
        // paint
        g2.drawImage(buffImgHeader, null, 0, 0);
        
        if(startColor.getRed() < 100 && startColor.getGreen() < 100 && startColor.getBlue() < 100){
            g2.setPaint(new Color(255,255,255));
        }else{
            g2.setPaint(new Color(0,0,0));
        }

        g2.setFont(new Font("Verdana",Font.PLAIN,10));
        String name = getName();
        FontMetrics fm = g2.getFontMetrics();
        int i = 0;
        if(fm.stringWidth(name) > getWidth() - 25 ){
            while(i != name.length()){
                if(fm.stringWidth(name.substring(0,i)) > getWidth() -25){
                    name = name.substring(0,i)+"...";
                    break;
                }
              i++;
            }
        }
        g2.drawString(name,5,13);

        g2.dispose();

    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public List<TaskEvent> getParents() {
        return parents;
    }

    public void setParent(TaskEvent event) {
        this.parents.add(event);
    }

    public void setBackgroundColor(Color color){
      this.backgroundColor = color;
    }

    public Color getBackgroundColor(){
        return this.backgroundColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //this.setText(name);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration / 20;
        setSize(duration, 20);
    }

    public int getReached() {
        return reached;
    }

    public void setReached(int reached) {
        this.reached = reached;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        setToolTipText(description);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void mouseClicked(java.awt.event.MouseEvent mouseEvent){
        if(mouseEvent.getClickCount() >= 2){
          container.getEventListener().editTaskAction(TaskEvent.this, true);
          repaint();
        }
    }

    public void mousePressed(java.awt.event.MouseEvent mouseEvent){

        if(mouseEvent.getButton() == MouseEvent.BUTTON3){
            if(container.selectedRelationTask != -1){
                relationTo.setEnabled(true);
            }
            popUp.show((JComponent)mouseEvent.getSource(),mouseEvent.getX(),mouseEvent.getY());
            rightClick = true;
        }
         moveEvent = true;
         offsetX  =  mouseEvent.getX();
         offsetY  =  mouseEvent.getY();


         if(mouseEvent.getX() <= getWidth()-10 ){
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
            getParent().setCursor(new Cursor(Cursor.MOVE_CURSOR));

         }
        

        this.container.x = getX();
        this.container.y = getY();
        this.container.width = getWidth();
        this.container.taskSelected = true;
        this.container.repaint();

    }

    public void mouseReleased(java.awt.event.MouseEvent mouseEvent){
        if(mouseEvent.getButton() == MouseEvent.BUTTON3){
            popUp.show((JComponent)mouseEvent.getSource(),mouseEvent.getX(),mouseEvent.getY());
            rightClick = false;
        }
            if(resizeEvent &&  startDrag){
                //setDuration((getWidth() / 20));
                resizeEvent = false;
                container.getEventListener().editTaskAction(TaskEvent.this, false);
            }else if(moveEvent &&  startDrag){
                container.getEventListener().editTaskAction(TaskEvent.this, false);
                moveEvent = false;
            }else if(isControl){

               if(this.container.selectedRelationTask  == -1){
                   this.container.selectedRelationTask = this.getId();
               }else{
                   container.getEventListener().createRelation(container.selectedRelationTask,getId());
                   this.container.getAvailableTasks().get(this.container.selectedRelationTask).setParent(this);
                   this.container.selectedRelationTask = -1;
                   repaint();
               }

            }

            startDrag = false;
            this.container.x = getX();
            this.container.y = getY();
            this.container.width = getWidth();
            this.container.repaint();
            //repaint();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    public void mouseEntered(java.awt.event.MouseEvent mouseEvent){}

    public void mouseExited(java.awt.event.MouseEvent mouseEvent){
        if(!startDrag){
         setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
         getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
    }

    public void mouseDragged(java.awt.event.MouseEvent mouseEvent){
        if(!rightClick){
           int x  =   mouseEvent.getX();
           int y  =   mouseEvent.getY();
           if(resizeEvent){
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
                if(x >= 20){
                  setDuration(x);
                }
               if(!startDrag){
                   startDrag = true;
               }
               this.container.repaint();
           }else if(moveEvent){
             Point pt = SwingUtilities.convertPoint(this, x, y, getParent());
             int posX =  pt.x - offsetX;
             int posY =  pt.y - offsetY;
             if(posX <= 20 && posY <= 40 ){
                  posX = 0;
                  posY = 40;
                }else if(posX <= 20 && posY > 20){
                   posX = 0;
                   posY = posY - (posY % 20);
                }else if(posY <=40 && posX > 20){
                   posY = 40;
                   posX = posX - (posX % 20);
                }else{
                  posY = posY - (posY % 20);
                  posX = posX - (posX % 20);
                }
             setLocation(posX, posY);
               if(!startDrag){
                   startDrag = true;
               }
               this.container.repaint();
           }
        }
    }

    public void mouseMoved(java.awt.event.MouseEvent mouseEvent){
        
        if(mouseEvent.getX() >= getWidth()-10){
           setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
           getParent().setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
           resizeEvent = true;
           moveEvent = false;
        }else{
          resizeEvent = false;
          moveEvent = true;
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          getParent().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          requestFocus();
        }
    }

    public void keyTyped(java.awt.event.KeyEvent keyEvent){
       
    }

    public void keyPressed(java.awt.event.KeyEvent keyEvent){
        if(keyEvent.getKeyCode() == KeyEvent.VK_CONTROL){
            isControl = true;
        }
    }

    public void keyReleased(java.awt.event.KeyEvent keyEvent){
        isControl = false;
    }

}
