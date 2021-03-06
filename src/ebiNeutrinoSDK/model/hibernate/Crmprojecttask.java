package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.Set;

/**
 * Crmprojecttask generated by hbm2java
 */
public class Crmprojecttask  implements java.io.Serializable {


     private Integer taskiid;
     private Crmproject crmproject;
     private String taskid;
     private String parentstaskid;
     private Integer x;
     private Integer y;
     private String name;
     private String status;
     private String type;
     private Integer duration;
     private String color;
     private Integer done;
     private String description;
     private Set<Crmprojectprop> crmprojectprops = new HashSet<Crmprojectprop>(0);
     private Set<Crmprojectcost> crmprojectcosts = new HashSet<Crmprojectcost>(0);

    public Crmprojecttask() {
    }

	
    public Crmprojecttask(String taskid) {
        this.taskid = taskid;
    }
    public Crmprojecttask(Crmproject crmproject, String taskid, String parentstaskid, Integer x, Integer y, String name, String status, String type, Integer duration, String color, Integer done, String description, Set<Crmprojectprop> crmprojectprops, Set<Crmprojectcost> crmprojectcosts) {
       this.crmproject = crmproject;
       this.taskid = taskid;
       this.parentstaskid = parentstaskid;
       this.x = x;
       this.y = y;
       this.name = name;
       this.status = status;
       this.type = type;
       this.duration = duration;
       this.color = color;
       this.done = done;
       this.description = description;
       this.crmprojectprops = crmprojectprops;
       this.crmprojectcosts = crmprojectcosts;
    }
   
    public Integer getTaskiid() {
        return this.taskiid;
    }
    
    public void setTaskiid(Integer taskiid) {
        this.taskiid = taskiid;
    }
    public Crmproject getCrmproject() {
        return this.crmproject;
    }
    
    public void setCrmproject(Crmproject crmproject) {
        this.crmproject = crmproject;
    }
    public String getTaskid() {
        return this.taskid;
    }
    
    public void setTaskid(String taskid) {
        this.taskid = taskid;
    }
    public String getParentstaskid() {
        return this.parentstaskid;
    }
    
    public void setParentstaskid(String parentstaskid) {
        this.parentstaskid = parentstaskid;
    }
    public Integer getX() {
        return this.x;
    }
    
    public void setX(Integer x) {
        this.x = x;
    }
    public Integer getY() {
        return this.y;
    }
    
    public void setY(Integer y) {
        this.y = y;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public Integer getDuration() {
        return this.duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    public String getColor() {
        return this.color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    public Integer getDone() {
        return this.done;
    }
    
    public void setDone(Integer done) {
        this.done = done;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public Set<Crmprojectprop> getCrmprojectprops() {
        return this.crmprojectprops;
    }
    
    public void setCrmprojectprops(Set<Crmprojectprop> crmprojectprops) {
        this.crmprojectprops = crmprojectprops;
    }
    public Set<Crmprojectcost> getCrmprojectcosts() {
        return this.crmprojectcosts;
    }
    
    public void setCrmprojectcosts(Set<Crmprojectcost> crmprojectcosts) {
        this.crmprojectcosts = crmprojectcosts;
    }




}


