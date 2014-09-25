package ebiNeutrinoSDK.utils;

import ebiNeutrinoSDK.EBIPGFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.border.Border;


public class EBITitleBorder implements Border {

  protected int ovalWidth = 8;
  protected int ovalHeight = 8;
  protected final Color lightColor = new Color(10,10,174);
  protected final Color darkColor = EBIPGFactory.systemColor;
  private boolean isClosable = true;

  public EBITitleBorder(boolean isClosable) {
    ovalWidth = 15;
    ovalHeight = 15;
    this.isClosable = isClosable;
  }

  public Insets getBorderInsets(Component c) {
    return new Insets(1, 2, 2, 2);
  }

  public boolean isBorderOpaque() {
    return true;
  }

  public void paintBorder(Component c, Graphics g, int x, int y, int width,int height) {

        g.setColor(lightColor);
        g.draw3DRect(x,y,width,height,true);
        g.setColor(darkColor);
        g.fill3DRect(x-1,y-1,width+1,height+1,true);
        Graphics2D g2 = (Graphics2D)g;
		// Draw bg top
        //Color startColor = lightColor;
        //Color endColor = darkColor;

        // A non-cyclic gradient
        // GradientPaint gradient = new GradientPaint(0, 0, endColor, 0, 60, startColor);
        //g2.setPaint(gradient);
		//g2.fillRoundRect(0, 0, width, 60,8,8);

        Color sColor = darkColor;
        Color eColor = lightColor;

        // A non-cyclic gradient
        GradientPaint gradient1 = new GradientPaint(0, 0, sColor, width, 80, eColor);
        g2.setPaint(gradient1);
		g.fillRect(0, 61, width, height);

  }
}