package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Ebipessimistic generated by hbm2java
 */
public class Ebipessimistic  implements java.io.Serializable {


     private Integer optimisticid;
     private Integer recordid;
     private String modulename;
     private String user;
     private Date lockdate;
     private Integer status;

    public Ebipessimistic() {
    }

    public Ebipessimistic(Integer recordid, String modulename, String user, Date lockdate, Integer status) {
       this.recordid = recordid;
       this.modulename = modulename;
       this.user = user;
       this.lockdate = lockdate;
       this.status = status;
    }
   
    public Integer getOptimisticid() {
        return this.optimisticid;
    }
    
    public void setOptimisticid(Integer optimisticid) {
        this.optimisticid = optimisticid;
    }
    public Integer getRecordid() {
        return this.recordid;
    }
    
    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }
    public String getModulename() {
        return this.modulename;
    }
    
    public void setModulename(String modulename) {
        this.modulename = modulename;
    }
    public String getUser() {
        return this.user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    public Date getLockdate() {
        return this.lockdate;
    }
    
    public void setLockdate(Date lockdate) {
        this.lockdate = lockdate;
    }
    public Integer getStatus() {
        return this.status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }




}


