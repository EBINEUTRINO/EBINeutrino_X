package ebiNeutrinoSDK.gui.component;


import java.awt.Image;
import java.awt.image.ImageObserver;

public class EBIDrawImgProp {

    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;
    private int intervall = 0;
    private int counterW = 0;
    private int counterH = 0;
    private Image img = null;
    private ImageObserver obs = null;
    private boolean isDrawRepeatImageW = false;
    private boolean isDrawRepeatImageH = false;

    public EBIDrawImgProp(){}

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getIntervall() {
        return intervall;
    }

    public void setIntervall(int intervall) {
        this.intervall = intervall;
    }

    public int getCounterW() {
        return counterW;
    }

    public void setCounterW(int counterW) {
        this.counterW = counterW;
    }

    public int getCounterH() {
        return counterH;
    }

    public void setCounterH(int counterH) {
        this.counterH = counterH;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public ImageObserver getObs() {
        return obs;
    }

    public void setObs(ImageObserver obs) {
        this.obs = obs;
    }

    public boolean isDrawRepeatImageW() {
        return isDrawRepeatImageW;
    }

    public void setDrawRepeatImageW(boolean drawRepeatImageW) {
        isDrawRepeatImageW = drawRepeatImageW;
    }

    public boolean isDrawRepeatImageH() {
        return isDrawRepeatImageH;
    }

    public void setDrawRepeatImageH(boolean drawRepeatImageH) {
        isDrawRepeatImageH = drawRepeatImageH;
    }
}
