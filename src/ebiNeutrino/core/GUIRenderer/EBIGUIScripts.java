package ebiNeutrino.core.GUIRenderer;


public class EBIGUIScripts {

    private String type = "";
    private String path = "";
    private String name = "";
    private String className = "";
    private boolean executed = false;

    public EBIGUIScripts(){}


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}
