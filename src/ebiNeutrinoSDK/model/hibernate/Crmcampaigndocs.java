package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Crmcampaigndocs generated by hbm2java
 */
public class Crmcampaigndocs  implements java.io.Serializable {


     private Integer docid;
     private Crmcampaign crmcampaign;
     private String name;
     private byte[] files;
     private Date createddate;
     private String createdfrom;

    public Crmcampaigndocs() {
    }

    public Crmcampaigndocs(Crmcampaign crmcampaign, String name, byte[] files, Date createddate, String createdfrom) {
       this.crmcampaign = crmcampaign;
       this.name = name;
       this.files = files;
       this.createddate = createddate;
       this.createdfrom = createdfrom;
    }
   
    public Integer getDocid() {
        return this.docid;
    }
    
    public void setDocid(Integer docid) {
        this.docid = docid;
    }
    public Crmcampaign getCrmcampaign() {
        return this.crmcampaign;
    }
    
    public void setCrmcampaign(Crmcampaign crmcampaign) {
        this.crmcampaign = crmcampaign;
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




}


