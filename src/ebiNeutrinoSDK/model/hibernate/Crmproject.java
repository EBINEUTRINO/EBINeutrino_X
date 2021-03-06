package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Crmproject generated by hbm2java
 */
public class Crmproject  implements java.io.Serializable {


     private Integer projectid;
     private String projectnr;
     private Date createddate;
     private String createdfrom;
     private Date changeddate;
     private String changedfrom;
     private String name;
     private String manager;
     private Double budget;
     private Double actualcost;
     private Double remaincost;
     private String status;
     private Date validfrom;
     private Date validto;
     private Set<Crmprojecttask> crmprojecttasks = new HashSet<Crmprojecttask>(0);

    public Crmproject() {
    }

    public Crmproject(String projectnr, Date createddate, String createdfrom, Date changeddate, String changedfrom, String name, String manager, Double budget, Double actualcost, Double remaincost, String status, Date validfrom, Date validto, Set<Crmprojecttask> crmprojecttasks) {
       this.projectnr = projectnr;
       this.createddate = createddate;
       this.createdfrom = createdfrom;
       this.changeddate = changeddate;
       this.changedfrom = changedfrom;
       this.name = name;
       this.manager = manager;
       this.budget = budget;
       this.actualcost = actualcost;
       this.remaincost = remaincost;
       this.status = status;
       this.validfrom = validfrom;
       this.validto = validto;
       this.crmprojecttasks = crmprojecttasks;
    }
   
    public Integer getProjectid() {
        return this.projectid;
    }
    
    public void setProjectid(Integer projectid) {
        this.projectid = projectid;
    }
    public String getProjectnr() {
        return this.projectnr;
    }
    
    public void setProjectnr(String projectnr) {
        this.projectnr = projectnr;
    }
    public Date getCreateddate() {
        return this.createddate;
    }
    
    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }
    public String getCreatedfrom() {
        return this.createdfrom;
    }
    
    public void setCreatedfrom(String createdfrom) {
        this.createdfrom = createdfrom;
    }
    public Date getChangeddate() {
        return this.changeddate;
    }
    
    public void setChangeddate(Date changeddate) {
        this.changeddate = changeddate;
    }
    public String getChangedfrom() {
        return this.changedfrom;
    }
    
    public void setChangedfrom(String changedfrom) {
        this.changedfrom = changedfrom;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getManager() {
        return this.manager;
    }
    
    public void setManager(String manager) {
        this.manager = manager;
    }
    public Double getBudget() {
        return this.budget;
    }
    
    public void setBudget(Double budget) {
        this.budget = budget;
    }
    public Double getActualcost() {
        return this.actualcost;
    }
    
    public void setActualcost(Double actualcost) {
        this.actualcost = actualcost;
    }
    public Double getRemaincost() {
        return this.remaincost;
    }
    
    public void setRemaincost(Double remaincost) {
        this.remaincost = remaincost;
    }
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    public Date getValidfrom() {
        return this.validfrom;
    }
    
    public void setValidfrom(Date validfrom) {
        this.validfrom = validfrom;
    }
    public Date getValidto() {
        return this.validto;
    }
    
    public void setValidto(Date validto) {
        this.validto = validto;
    }
    public Set<Crmprojecttask> getCrmprojecttasks() {
        return this.crmprojecttasks;
    }
    
    public void setCrmprojecttasks(Set<Crmprojecttask> crmprojecttasks) {
        this.crmprojecttasks = crmprojecttasks;
    }




}


