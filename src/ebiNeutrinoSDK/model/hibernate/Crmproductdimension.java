package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Crmproductdimension generated by hbm2java
 */
public class Crmproductdimension  implements java.io.Serializable {


     private Integer dimensionid;
     private Crmproduct crmproduct;
     private Date createddate;
     private String createdfrom;
     private Date changeddate;
     private String changedfrom;
     private String name;
     private String value;

    public Crmproductdimension() {
    }

    public Crmproductdimension(Crmproduct crmproduct, Date createddate, String createdfrom, Date changeddate, String changedfrom, String name, String value) {
       this.crmproduct = crmproduct;
       this.createddate = createddate;
       this.createdfrom = createdfrom;
       this.changeddate = changeddate;
       this.changedfrom = changedfrom;
       this.name = name;
       this.value = value;
    }
   
    public Integer getDimensionid() {
        return this.dimensionid;
    }
    
    public void setDimensionid(Integer dimensionid) {
        this.dimensionid = dimensionid;
    }
    public Crmproduct getCrmproduct() {
        return this.crmproduct;
    }
    
    public void setCrmproduct(Crmproduct crmproduct) {
        this.crmproduct = crmproduct;
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
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }




}


