package ebiNeutrinoSDK.gui.component;

import ebiNeutrinoSDK.EBIPGFactory;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EBIExtendedPanel extends JPanel {

	private String imagePath = null;
    private String title = null;
    private int x = 0;
    private int y = 0;
    private int offsetW = 0;

	/**
	 * This is the default constructor
	 */
	public EBIExtendedPanel(String title,String img) {
		this.title = title;
        this.imagePath = img;
		initialize();
        this.x = 0;
        this.y = 0;
        this.offsetW = 0;
	}

    public EBIExtendedPanel() {
        this.y = 8;
        this.x = 1;
        this.offsetW = 0;
        initialize();
    }

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(null);
		this.setSize(570, 206);
        if(title != null){
            JLabel jLabel=new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(20,-8,272,51));
            jLabel.setText(title);
            jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
            jLabel.setIcon(new ImageIcon(imagePath));
            this.add(jLabel, null);
        }
	}


} 
