package ebiNeutrino.core.GUIRenderer;

import javax.swing.JComponent;

import ebiNeutrino.core.GUIDesigner.EBIGUIWidgetsBean;

public class EBIGUINBean {

    private JComponent component  = null;
    private JComponent parentComponent = null;
    private JComponent scrollComponent = null;
    private boolean resizeHight = false;
    private boolean resizeWidth = false;
    private boolean fitByResize = false;
    private int percentWidth  = 0;
    private int percentHeight = 0;
    private String name ="";
    private String imageName="";
    private EBIGUIWidgetsBean bean = null;

    public EBIGUINBean(){}


    public JComponent getScrollComponent() {
        return scrollComponent;
    }

    public void setScrollComponent(JComponent scrollComponent) {
        this.scrollComponent = scrollComponent;
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    public boolean isResizeHight() {
        return resizeHight;
    }

    public void setResizeHight(boolean resizeHight) {
        this.resizeHight = resizeHight;
    }

    public boolean isResizeWidth() {
        return resizeWidth;
    }

    public void setResizeWidth(boolean resizeWidth) {
        this.resizeWidth = resizeWidth;
    }

    public int getPercentWidth() {
        return percentWidth;
    }

    public void setPercentWidth(int percentWidth) {
        this.percentWidth = percentWidth;
    }

    public int getPercentHeight() {
        return percentHeight;
    }

    public void setPercentHeight(int percentHeight) {
        this.percentHeight = percentHeight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JComponent getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(JComponent parentComponent) {
        this.parentComponent = parentComponent;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public boolean isFitByResize() {
        return fitByResize;
    }

    public void setFitByResize(boolean fitByResize) {
        this.fitByResize = fitByResize;
    }

    public EBIGUIWidgetsBean getBean() {
        return bean;
    }

    public void setBean(EBIGUIWidgetsBean bean) {
        this.bean = bean;
    }
}
