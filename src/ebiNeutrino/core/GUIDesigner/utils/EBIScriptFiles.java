package ebiNeutrino.core.GUIDesigner.utils;

import java.io.File;

import javax.swing.JComponent;

public class EBIScriptFiles {

    private String Name = "";
    private File file = new File(".");
    private JComponent component = null;
    private boolean isClosed=false;

    public EBIScriptFiles(){}

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public JComponent getComponent() {
        return component;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
