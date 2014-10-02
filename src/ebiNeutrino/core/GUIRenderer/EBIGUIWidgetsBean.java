package ebiNeutrino.core.GUIRenderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import ebiNeutrinoSDK.EBIPGFactory;
import ebiNeutrinoSDK.gui.dialogs.EBIExceptionDialog;
import ebiNeutrinoSDK.gui.dialogs.EBIMessage;


public class EBIGUIWidgetsBean {

     private String type = "";
     private Dimension dimension = new Dimension();
     private Point point = new Point();
     private String name = "";
     private String title="";
     private int x =0;
     private int y =0;
     private ImageIcon icon= null;
     private File image = new File(".");
     private String path = "";
     private String className = "";
     private int tabIndex = -1;
     private int min = -1;      
     private int max = -1;
     private java.util.List<EBIGUIWidgetsBean> subWidgets = new ArrayList<EBIGUIWidgetsBean>();
     private Color color = null;
     private String colorName="";
     private int KeyEvent = 0;
     private KeyStroke keyStroke = null;
     private boolean heightAutoResize = false;
     private boolean widthAutoResize = false;
     private boolean fitOnResize = false;
     private boolean isCeckable = false;
     private boolean visualProperties = false;
     private boolean modal = false;
     private boolean resizable = false;
     private boolean isEnabled = true;
     private boolean pessimistic = true;
     private boolean isVisible=true;
     private boolean isEditable=true;
     private boolean isShowTimer=false;
     private Object component=null;
     private String i18NToolTip = "";
     private boolean assignable = false;

    public EBIGUIWidgetsBean(){}

    public void setSubWidgets(java.util.List<EBIGUIWidgetsBean> subWidgets) {
       this.subWidgets = subWidgets;
    }

    public java.util.List<EBIGUIWidgetsBean> getSubWidgets() {
       return subWidgets;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
        this.icon = new ImageIcon(image.getAbsolutePath());
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        getPoint().x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        getPoint().y = y;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
       this.x = point.x;
       this.y = point.y;
       this.point = point;
    }


    public String getType() {
	   return type;
	}


	public void setType(String type) {
	   this.type = type;
	}


	public Dimension getDimension() {
		return dimension;
	}


	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	public ImageIcon getIcon() {
		return icon;
	}

    public String getIconPath(){
       return image.getAbsolutePath(); 
    }

    public void setIcon(String icon) {
		this.icon = new ImageIcon("images/"+icon);
        this.image = new File(icon);
    }

    public Color getColor() {
        return color;
    }

    public String getColorName(){
        return this.colorName;
    }

    public void setColor(String col) {
        try{
             color = Color.decode(col);
             colorName=col;
        }catch(NumberFormatException ex){
            EBIExceptionDialog.getInstance(EBIPGFactory.printStackTrace(ex)).Show(EBIMessage.ERROR_MESSAGE);
        }
    }

	public boolean isHeightAutoResize() {
        return heightAutoResize;
    }

    public void setHeightAutoResize(boolean hightAutoResize) {
        this.heightAutoResize = hightAutoResize;
    }

    public boolean isWidthAutoResize() {
      return widthAutoResize;
    }

    public void setWidthAutoResize(boolean widthAutoRisize) {
        this.widthAutoResize = widthAutoRisize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getKeyEvent() {
        return KeyEvent;
    }

    public void setKeyEvent(int keyEvent) {
        KeyEvent = keyEvent;
    }

    public KeyStroke getKeyStroke() {
        return keyStroke;
    }

    public void setKeyStroke(int keyStroke) {
        this.keyStroke = KeyStroke.getKeyStroke(keyStroke,Event.CTRL_MASK, true);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isCeckable() {
        return isCeckable;
    }

    public void setCeckable(boolean ceckable) {
        isCeckable = ceckable;
    }

    public boolean isVisualProperties() {
        return visualProperties;
    }

    public void setVisualProperties(boolean visualProperties) {
        this.visualProperties = visualProperties;
    }

    public int getTabIndex(){
        return this.tabIndex;
    }

    public void setTabIndex(int index){
        this.tabIndex = index;
    }

    public boolean isModal() {
        return modal;
    }

    public void setModal(boolean modal) {
        this.modal = modal;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public Object getComponent() {
        return component;
    }

    public void setComponent(Object component) {
        this.component = component;
    }

    public void setMin(int min){
        this.min = min;
    }

    public int getMin(){
        return this.min;
    }

    public void setMax(int max){
        this.max = max;
    }

    public int getMax(){
        return this.max;
    }

    public void setI18NToolTip(String toolTip){
        this.i18NToolTip = toolTip;
    }

    public String getI18NToolTip(){
        return this.i18NToolTip;
    }

    public boolean isFitOnResize() {
        return fitOnResize;
    }

    public void setFitOnResize(boolean fitOnResize) {
        this.fitOnResize = fitOnResize;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isAssignable() {
        return assignable;
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isPessimistic() {
        return pessimistic;
    }

    public void setPessimistic(boolean pessimistic) {
        this.pessimistic = pessimistic;
    }

	public boolean isShowTimer() {
		return isShowTimer;
	}

	public void setShowTimer(boolean isShowTimer) {
		this.isShowTimer = isShowTimer;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}
    
	
    
    
}
