package ebiNeutrinoSDK.gui.component;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import org.jdesktop.swingx.JXPanel;

import javax.swing.*;


public class EBIContentPanel  extends JXPanel {

       public ArrayList<EBIDrawImgProp> imgList = new ArrayList<EBIDrawImgProp>();

       public  EBIContentPanel(){
           setOpaque(false);
       }


        @Override
        public void paintComponent(Graphics g){

              if(this.imgList.size() > 0){
                   Graphics2D g2 = (Graphics2D)g;
                   for(int l = 0; l<this.imgList.size(); l++ ){
                     EBIDrawImgProp prp = (EBIDrawImgProp) this.imgList.get(l);
                       if(prp.isDrawRepeatImageW()){
                           for(int i =prp.getX(); i< prp.getW(); i+=prp.getIntervall()){
                                 g2.drawImage(prp.getImg(),i,prp.getY(),prp.getObs());
                           }
                       }

                       if(prp.isDrawRepeatImageH()){
                           for(int i =prp.getY(); i< prp.getH(); i+=prp.getIntervall()){
                               g2.drawImage(prp.getImg(),prp.getX(),i,prp.getObs());
                           }
                       }
                   }
                  super.paintComponent(g);
              }

        }

        public void drawRepeatedImageWidth(Image img, int x, int y, int w, int intervall, ImageObserver obs){

            EBIDrawImgProp prp = new EBIDrawImgProp();

            prp.setImg(img);
            prp.setX(x);
            prp.setY(y);
            prp.setW(w);
            prp.setIntervall(intervall);
            prp.setObs(obs);
            prp.setDrawRepeatImageW(true);
            this.imgList.add(prp);
        }

        public void drawRepeatedImageHeight(Image img, int x, int y, int h, int intervall, ImageObserver obs){
            EBIDrawImgProp prp = new EBIDrawImgProp();

            prp.setImg(img);
            prp.setX(x);
            prp.setY(y);
            prp.setH(h);
            prp.setIntervall(intervall);
            prp.setObs(obs);
            prp.setDrawRepeatImageH(true);
            this.imgList.add(prp);
        }

}
