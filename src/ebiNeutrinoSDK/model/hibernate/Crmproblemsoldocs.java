package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Crmproblemsoldocs generated by hbm2java
 */
public class Crmproblemsoldocs  implements java.io.Serializable {


     private Integer solutiondocid;
     private Crmproblemsolutions crmproblemsolutions;
     private String name;
     private byte[] files;
     private String createdfrom;
     private Date createddate;

    public Crmproblemsoldocs() {
    }

    public Crmproblemsoldocs(Crmproblemsolutions crmproblemsolutions, String name, byte[] files, String createdfrom, Date createddate) {
       this.crmproblemsolutions = crmproblemsolutions;
       this.name = name;
       this.files = files;
       this.createdfrom = createdfrom;
       this.createddate = createddate;
    }
   
    public Integer getSolutiondocid() {
        return this.solutiondocid;
    }
    
    public void setSolutiondocid(Integer solutiondocid) {
        this.solutiondocid = solutiondocid;
    }
    public Crmproblemsolutions getCrmproblemsolutions() {
        return this.crmproblemsolutions;
    }
    
    public void setCrmproblemsolutions(Crmproblemsolutions crmproblemsolutions) {
        this.crmproblemsolutions = crmproblemsolutions;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public byte[] getFiles() {
        return this.files;
    }
    
    public void setFiles(byte[] files) {
        this.files = files;
    }
    public String getCreatedfrom() {
        return this.createdfrom;
    }
    
    public void setCreatedfrom(String createdfrom) {
        this.createdfrom = createdfrom;
    }
    public Date getCreateddate() {
        return this.createddate;
    }
    
    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }




}


