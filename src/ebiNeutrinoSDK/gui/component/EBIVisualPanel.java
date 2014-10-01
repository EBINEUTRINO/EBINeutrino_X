package ebiNeutrinoSDK.gui.component;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import ebiNeutrino.core.GUIRenderer.EBIButton;
import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIDialog;
import ebiNeutrinoSDK.utils.EBIConstant;



/**
 * The EBIVisualPanel
 */

public class EBIVisualPanel extends JDesktopPane {

    protected JLabel jTextFieldAdded = null;
	protected JLabel jTextFieldAddedFrom = null;
	protected JLabel jTextFieldChanged = null;
	protected JLabel jTextFieldChangedFrom = null;
	protected JComboBox jTextComboAddedFrom = null;
	protected JLabel iconPanel = null;
	protected JLabel moduleTitle = null;
	private JLabel createdDate = null;
	private JLabel createdFrom = null;
	private JLabel changedDate = null;
	private JLabel changedFrom = null;
    private JButton lockButton = null;
    private boolean visualProperties = true;
    private boolean assignable = true;
    private boolean pessimistic = true;
    private EBIDialog dialog = null;
    private boolean pessimisticLocked = false;
    private String oldText = "";
    private boolean closable = false;

    public EBIVisualPanel(){
        initialize();
        initFocusTravesal();
    }

	private void initialize() {

		changedFrom = new JLabel();
		changedFrom.setBounds(new Rectangle(827, 0, 63, 20));
		changedFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		changedFrom.setFont(new Font("Dialog", Font.BOLD, 10));
        changedFrom.setForeground(new Color(240,240,240));
		changedFrom.setText("From:");
        changedFrom.setVisible(false);
		changedDate = new JLabel();
		changedDate.setBounds(new Rectangle(649, 0, 76, 20));
		changedDate.setHorizontalAlignment(SwingConstants.RIGHT);
		changedDate.setFont(new Font("Dialog", Font.BOLD, 10));
        changedDate.setForeground(new Color(240,240,240));
		changedDate.setText("Changed:");
        changedDate.setVisible(false);
		createdFrom = new JLabel();
		createdFrom.setBounds(new Rectangle(484, 0, 63, 20));
		createdFrom.setHorizontalAlignment(SwingConstants.RIGHT);
		createdFrom.setFont(new Font("Dialog", Font.BOLD, 10));
        createdFrom.setForeground(new Color(240,240,240));
		createdFrom.setText("From:");
		createdDate = new JLabel();
		createdDate.setBounds(new Rectangle(306, 0, 74, 20));
		createdDate.setHorizontalAlignment(SwingConstants.RIGHT);
		createdDate.setFont(new Font("Dialog", Font.BOLD, 10));
        createdDate.setForeground(new Color(240,240,240));
		createdDate.setText("Created:");
		moduleTitle = new JLabel();
		moduleTitle.setBounds(new Rectangle(30, 1, 222, 20));
		moduleTitle.setFont(new Font("Dialog", Font.BOLD, 10));
        moduleTitle.setForeground(new Color(240,240,240));
		moduleTitle.setText("Module Title");
		iconPanel = new JLabel();
		iconPanel.setBounds(new Rectangle(8, 3, 16, 16));
        iconPanel.setOpaque(false);
		this.setLayout(null);
		this.add(getJTextFieldAdded(), null);
		this.add(getJTextFieldAddedFrom(), null);
		this.add(getJComboAddedFrom(), null);
		this.add(getJTextFieldChanged(), null);
		this.add(getJTextFieldChangedFrom(), null);
        this.add(getShowLockButton(), null);
		this.add(iconPanel, null);
		this.add(moduleTitle, null);
		this.add(createdDate, null);
		this.add(createdFrom, null);
		this.add(changedDate, null);
		this.add(changedFrom, null);
        this.setDoubleBuffered(true);
        RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
	}
	
	public void paintComponent(Graphics g){

        Graphics2D g2 = (Graphics2D)g;
        // Draw bg top
        Color startColor = new Color(50,50,50);
        Color endColor = startColor.brighter();

        // A non-cyclic gradient
        GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, 10, endColor);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), 20);

        g2.setColor(new Color(250,250,250));
        g2.drawLine(0, 20, getWidth(), 20);

        setOpaque(true);

        if(isPessimistic()){
            if(pessimisticLocked){
                g2.drawImage(new ImageIcon("images/red.png").getImage(), 280,5 ,null);
                lockButton.setVisible(true);
            }else{
                //g2.drawImage(new ImageIcon("images/green.png").getImage(), 280, 5 ,null);
                lockButton.setVisible(false);
            }
        }
        //g.setColor(new Color(250,250,250));
		//g.drawLine(0, 21, getWidth(), 21);
        
        
        if(closable){
            BufferedImage bi = new BufferedImage(30,30, BufferedImage.TYPE_INT_ARGB);
            Graphics gx = bi.getGraphics();

            gx.drawImage(EBIConstant.ICON_CLOSE.getImage(), 0, 0, null);
            g2.drawImage(bi,getWidth()-25,2,null);
        }
        
        
		//setOpaque(false);

	}

    /**
     * Initialize the EBIVisualPanel focus traversal
     */

    public void initFocusTravesal(){
		this.setFocusCycleRoot(true);
    	final KeyStroke KEYSTROKE_TAB = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
    	final KeyStroke KEYSTROKE_SHIFT_TAB =KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_MASK);
    	//forward focus set
    	Set<KeyStroke> forward = new HashSet<KeyStroke>();
    	forward.add(KEYSTROKE_TAB);
    	this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,forward);
    	//backward focus set
    	Set<KeyStroke> backward = new HashSet<KeyStroke>();
    	backward.add(KEYSTROKE_SHIFT_TAB);
    	this.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,backward);
	}

    private JButton getShowLockButton(){

        if(lockButton == null){
            lockButton = new JButton();
            lockButton.setOpaque(false);
            lockButton.setIcon(new ImageIcon("images/lock.png"));
            lockButton.setBorder(null);
            lockButton.setBounds(280, 2,16,16);
            //lockButton.setVisible(false);
            lockButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                      if(dialog != null){
                          dialog.setBounds(280,19,dialog.getWidth(), dialog.getHeight());
                          dialog.setVisible(true);
                      }
                }
            });
        }

        return lockButton;
    }


    public void setPessimisticViewDialog(EBIDialog dia){
        this.dialog = dia;
    }

    public void showLockIcon(boolean showLock){
        this.pessimisticLocked = showLock;
        this.repaint();
    }

	private JLabel getJTextFieldAdded() {
		if (jTextFieldAdded == null) {
			jTextFieldAdded = new JLabel();
			jTextFieldAdded.setBounds(new Rectangle(382, 0, 98, 20));
			jTextFieldAdded.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jTextFieldAdded.setForeground(new Color(80, 80, 80));
			jTextFieldAdded.setOpaque(false);
            jTextFieldAdded.setFocusable(false);
		}
		return jTextFieldAdded;
	}

	private JLabel getJTextFieldAddedFrom() {
		if (jTextFieldAddedFrom == null) {
			jTextFieldAddedFrom = new JLabel();
			jTextFieldAddedFrom.setBounds(new Rectangle(548, 0, 98, 20));
			jTextFieldAddedFrom.setForeground(new Color(240,240,240));
			jTextFieldAddedFrom.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jTextFieldAddedFrom.setOpaque(false);
			jTextFieldAddedFrom.setFocusable(false);

		}
		return jTextFieldAddedFrom;
	}

    private JComboBox getJComboAddedFrom() {
		if (jTextComboAddedFrom == null) {
			jTextComboAddedFrom = new JComboBox();
            jTextComboAddedFrom.setBounds(new Rectangle(548, 0, 98, 20));
			jTextComboAddedFrom.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jTextComboAddedFrom.setFocusable(false);
			jTextComboAddedFrom.setVisible(false);
            if(EBIPGFactory.systemUsers != null){
                jTextComboAddedFrom.setModel(new DefaultComboBoxModel(EBIPGFactory.systemUsers));
            }
			jTextComboAddedFrom.addActionListener(new ActionListener(){
                 public void actionPerformed(ActionEvent e) {
                   jTextFieldAddedFrom.setText(jTextComboAddedFrom.getSelectedItem() == null ?
                                              EBIPGFactory.ebiUser : jTextComboAddedFrom.getSelectedItem().toString());
                 }
            });
		}
		return jTextComboAddedFrom;
	}

    private JLabel getJTextFieldChanged() {
		if (jTextFieldChanged == null) {
			jTextFieldChanged = new JLabel();
			jTextFieldChanged.setBounds(new Rectangle(727, 0, 98, 20));
			jTextFieldChanged.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jTextFieldChanged.setOpaque(false);
			jTextFieldChanged.setForeground(new Color(240,240,240));
			jTextFieldChanged.setFocusable(false);
			jTextFieldChanged.setVisible(false);

		}
		return jTextFieldChanged;
	}

	private JLabel getJTextFieldChangedFrom() {
		if (jTextFieldChangedFrom == null) {
			jTextFieldChangedFrom = new JLabel();
			jTextFieldChangedFrom.setBounds(new Rectangle(892, 0, 98, 20));
			jTextFieldChangedFrom.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
			jTextFieldChangedFrom.setOpaque(false);
			jTextFieldChangedFrom.setForeground(new Color(240,240,240));
			jTextFieldChangedFrom.setFocusable(false);
            jTextFieldChangedFrom.setVisible(false);
		}
		return jTextFieldChangedFrom;
	}

    public void setModuleTitle(String psTitle){
		moduleTitle.setText(psTitle == null ? "" : psTitle);
		changedFrom.setText(EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM") == null ? "" : EBIPGFactory.getLANG("EBI_LANG_CHANGED_FROM")+":  ");
		changedDate.setText(EBIPGFactory.getLANG("EBI_LANG_CHANGED") == null ? "" : EBIPGFactory.getLANG("EBI_LANG_CHANGED")+":  ");
		createdFrom.setText(EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM") == null ? "" : EBIPGFactory.getLANG("EBI_LANG_ADDED_FROM")+":  ");
		createdDate.setText(EBIPGFactory.getLANG("EBI_LANG_ADDED") == null ? "" : EBIPGFactory.getLANG("EBI_LANG_ADDED")+":  ");
        if("".equals(oldText)){
            oldText = psTitle;
        }
	}

    public String getModuleTile(){
        return oldText;
    }

    public void setModuleIcon(ImageIcon pIcon){
		if(pIcon != null){
		    iconPanel.setIcon(pIcon);
            iconPanel.repaint();
            this.updateUI();
		}
	}

    public ImageIcon getModuleIcon(){
        return (ImageIcon)iconPanel.getIcon();
    }

	/**
	 * Hide the create/change date textfields
	 * @param visible
	 */

	public void setChangePropertiesVisible(boolean visible){
        visualProperties = visible;
		if(visible == true ){
			jTextFieldAdded.setVisible(true);
			jTextFieldChanged.setVisible(true);
            if(isAssignable()){
              jTextComboAddedFrom.setVisible(true);
              jTextFieldAddedFrom.setVisible(false);
            }else{
              jTextFieldAddedFrom.setVisible(true);
              jTextComboAddedFrom.setVisible(false);  
            }
            jTextFieldChangedFrom.setVisible(true);

            createdDate.setVisible(true);
			createdFrom.setVisible(true);
			changedDate.setVisible(true);
			changedFrom.setVisible(true);

		}else{
			jTextFieldAdded.setVisible(false);
            jTextFieldAddedFrom.setVisible(false);
            jTextComboAddedFrom.setVisible(false);
			jTextFieldChanged.setVisible(false);
			jTextFieldChangedFrom.setVisible(false);
			createdDate.setVisible(false);
			createdFrom.setVisible(false);
			changedDate.setVisible(false);
			changedFrom.setVisible(false);
		}
	}

   public boolean isVisualProperties(){
       return visualProperties;
   }

   public boolean isAssignable() {
        return assignable;
   }

    public boolean isPessimistic() {
        return pessimistic;
    }

    public void setPessimistic(boolean pessimistic) {
        this.pessimistic = pessimistic;
        lockButton.setVisible(pessimistic);
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
        if(isAssignable()){
            jTextFieldAddedFrom.setVisible(false);
            jTextComboAddedFrom.setVisible(true);
        }else{
            if(visualProperties){
                jTextFieldAddedFrom.setVisible(true);
            }
            jTextComboAddedFrom.setVisible(false);
       }
    }
    
    public void setCreatedDate(String text){
        jTextFieldAdded.setText(text);
   }

   public void setCreatedFrom(String text){
        if(isAssignable()){
           jTextComboAddedFrom.setSelectedItem(text);
        }else{
           jTextFieldAddedFrom.setText(text);
        }         
   }

   public void setChangedDate(String text){
       jTextFieldChanged.setText(text);

       if(!"".equals(text)){
            changedDate.setVisible(true);
            jTextFieldChanged.setVisible(true);
       }else{
            changedDate.setVisible(false);
           jTextFieldChanged.setVisible(false);
       }
   }

   public void setChangedFrom(String text){
       jTextFieldChangedFrom.setText(text);

       if(!"".equals(text)){
           changedFrom.setVisible(true);
           jTextFieldChangedFrom.setVisible(true);
       }else{
           changedFrom.setVisible(false);
           jTextFieldChangedFrom.setVisible(false);
       }
   }

   public String getCreatedFrom(){
	   
	   String user = "";
	   
	   if(isAssignable()){
		   if(jTextComboAddedFrom.getSelectedItem() != null){
			   user = jTextComboAddedFrom.getSelectedItem().toString();
		   }else{
			   jTextComboAddedFrom.setSelectedItem(EBIPGFactory.ebiUser);
			   user = EBIPGFactory.ebiUser;
		   }
	   }else{
		   user = jTextFieldAddedFrom.getText();
	   }
	   
       return user;
   }
   
	public void setID(int id){

		if(id == -1){
			moduleTitle.setText(oldText);
		}else{
			moduleTitle.setText(oldText+" ID:"+id);
		}
		
	}


    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }
}