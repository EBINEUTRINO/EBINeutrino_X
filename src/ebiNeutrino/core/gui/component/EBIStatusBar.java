package ebiNeutrino.core.gui.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import ebiNeutrino.core.EBIMain;


public class EBIStatusBar extends JPanel {

	public  static JLabel jLabel1 = null;
	private JProgressBar jProgressBarLoader = null;
	private EBIMain ebiMain = null;
	/**
	 * This is the default constructor
	 */
	public EBIStatusBar(EBIMain main) {
		super();
		ebiMain = main;
        setFocusable(false);
        setFocusCycleRoot(false);
		initialize();
	}
	
	
	public void statusBarInit(){
		ebiMain.getContentPane().add(this,java.awt.BorderLayout.SOUTH);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		JLabel jLabel2=new JLabel();
		jLabel2.setBounds(new Rectangle(385, -2, 232, 22));
		jLabel2.setText("Database: ");
		jLabel2.setIcon(new ImageIcon("images/db.png"));
		jLabel1 = new JLabel();
		jLabel1.setBounds(new Rectangle(133, -2, 236, 22));
		jLabel1.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		jLabel1.setText("");
		jLabel1.setIcon(new ImageIcon("images/agt_runit.png"));
		JLabel jLabel=new JLabel();
		jLabel.setText("EBI Neutrino");
		jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
		jLabel.setBounds(new java.awt.Rectangle(4,-2,122,22));
		this.setLayout(null);
		this.setSize(829, 25);
		this.add(jLabel, null);
		this.add(jLabel1, null);
		this.add(jLabel2, null);

	}
    
	public void paint(Graphics g){
		super.paint(g);
		g.setColor(new Color(200,200,200));
		g.drawLine(120, 0, 120, 14);
		
		g.setColor(new Color(250,250,250));
		g.drawLine(121, 0, 121, 14);
		
		g.setColor(new Color(200,200,200));
		g.drawLine(360, 0, 360, 14);
		
		g.setColor(new Color(250,250,250));
		g.drawLine(361, 0, 361, 14);
	}

}
