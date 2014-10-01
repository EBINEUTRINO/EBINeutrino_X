package ebiNeutrino.core.GUIRenderer;

import ebiNeutrinoSDK.EBIPGFactory;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;

import javax.swing.*;


public class EBIButton extends JButton implements MouseListener, MouseMotionListener, KeyListener, FocusListener {

    private ImageIcon icon =null;
    private boolean isClicked =false;
    private Color color = null;
    private Color foreColor = null;
    private Point corner =new Point(5,5);
    private boolean enabled=true;
    private boolean haveFocus=false;
    private boolean drawBorder = true;
    private BufferedImage buffImgHeader = null;
    private Graphics2D gbiHeader = null;
    private String text = "";


    public EBIButton(){
        this.addMouseListener(this);
        this.addMouseListener(this);
        this.addKeyListener(this);
        this.addFocusListener(this);
        setDoubleBuffered(true);
        setOpaque(false);
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
        setBorderPainted(false);
    }


    public void paint(Graphics gs){
        super.paint(gs);
        Graphics2D g2  = (Graphics2D) gs;

        if(buffImgHeader == null || isClicked || enabled){
            buffImgHeader=null;
            paintButton(enabled);
        }
        RescaleOp rop = null;
        if(!enabled){
            float[] scales = { 0.5f, 0.5f, 0.5f, 0.6f };
            float[] offsets = new float[4];
            rop = new RescaleOp(scales, offsets, null);
        }

        g2.drawImage(buffImgHeader, rop, 0, 0);

        if(!"".equals(text)){

            FontMetrics fm = g2.getFontMetrics();
            int i = 0;
            if(fm.stringWidth(text) > getWidth() - 25 ){
                while(i != text.length()){
                    if(fm.stringWidth(text.substring(0,i)) > getWidth() -25){
                        text = text.substring(0,i)+"...";
                        break;
                    }
                    i++;
                }
            }
            if(enabled){
                g2.drawString(text,(getWidth() /2) -(fm.stringWidth(text.substring(0,text.length())) / 2),
                    ((getHeight() /2) + (fm.getHeight() /2))-2);
            }
        }

        g2.dispose();

    }

    private void paintButton(boolean enabled){
        Color startColor;
        Color endColor;
        if(color == null){
            if(enabled){
                if(!isClicked){
                    if(drawBorder){
                        startColor = new Color(125,125,125);
                        endColor = startColor.brighter();
                    }else{
                        startColor = EBIPGFactory.systemColor;
                        endColor = startColor.brighter();

                    }
                }else{
                    startColor = EBIPGFactory.systemColor;
                    endColor = startColor.brighter();
                }
            }else{
                startColor = EBIPGFactory.systemColor;
                endColor = startColor.brighter();

            }

        }else{
            if(!isClicked){
                startColor = color.darker();
                endColor =   color.brighter();
            }else{
                startColor = color.brighter();
                endColor =   color.darker();
            }
        }
        if(buffImgHeader == null){
            buffImgHeader = new BufferedImage(getWidth(), getHeight(),  BufferedImage.TYPE_4BYTE_ABGR);
            gbiHeader =(Graphics2D) buffImgHeader.getGraphics();

            gbiHeader.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            gbiHeader.setStroke(new BasicStroke(1.0f));
            // A non-cyclic gradient
        }


        GradientPaint gradient = null;
        if(drawBorder){
            gradient = new GradientPaint(0, 0, startColor, 0, 33, endColor);
        }else{
            gradient = new GradientPaint(0, 0, startColor, 0, 23, endColor);
        }


        gbiHeader.setPaint(gradient);
        gbiHeader.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), corner.x, corner.y));


        if(this.getIcon() != null){

                gbiHeader.drawImage(this.getIcon().getImage(), (getWidth() / 2) -(this.getIcon().getIconWidth() / 2),
                        (getHeight() / 2) - (this.getIcon().getIconHeight() /2), null);

        }


        if(drawBorder){
            if(haveFocus){
                gbiHeader.setColor(Color.ORANGE);
                gbiHeader.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, corner.x,corner.y);
            }else{
                gbiHeader.setColor(new Color(100,100,100));
                gbiHeader.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, corner.x,corner.y);
            }
        }else{
            gbiHeader.setColor(new Color(100,100,100));
            gbiHeader.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, corner.x,corner.y);
        }

    }


    public void setIcon(ImageIcon icon){
        this.icon = icon;
    }

    public ImageIcon getIcon(){
        return icon;
    }

    @Override
    public void setText(String text){
        super.setText(text);
        this.text = text;
    }


    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseMoved(MouseEvent e) {}


    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mousePressed(MouseEvent e) {
        isClicked=true;
        //repaint();
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        isClicked=false;
        //repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }


    public Color getColor() {
        return color;
    }


    public void setColor(Color color) {
        this.color = color;
    }


    public Color getForeColor() {
        return foreColor;
    }


    public void setForeColor(Color foreColor) {
        this.foreColor = foreColor;
    }


    public Point getCorner() {
        return corner;
    }


    public void setCorner(int x, int y) {
        this.corner = new Point(x,y);
    }

    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        this.enabled = enabled;
        this.updateUI();
    }


    @Override
    public void focusGained(FocusEvent e) {
        haveFocus=true;
        repaint();
    }


    @Override
    public void focusLost(FocusEvent e) {
        haveFocus=false;
        repaint();
    }


    public void setDrawBorder(boolean drawBord){
        this.drawBorder = drawBord;
    }



}