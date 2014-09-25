package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Crmcampaignposition generated by hbm2java
 */
public class Crmcampaignposition  implements java.io.Serializable {


     private Integer positionid;
     private Crmcampaign crmcampaign;
     private Integer productid;
     private String productnr;
     private String deduction;
     private String productname;
     private Long quantity;
     private double netamount;
     private double pretax;
     private String taxtype;
     private String type;
     private String category;
     private String description;
     private String createdfrom;
     private Date createddate;
     private Date changeddate;
     private String changedfrom;

    public Crmcampaignposition() {
    }

	
    public Crmcampaignposition(double netamount, double pretax) {
        this.netamount = netamount;
        this.pretax = pretax;
    }
    public Crmcampaignposition(Crmcampaign crmcampaign, Integer productid, String productnr, String deduction, String productname, Long quantity, double netamount, double pretax, String taxtype, String type, String category, String description, String createdfrom, Date createddate, Date changeddate, String changedfrom) {
       this.crmcampaign = crmcampaign;
       this.productid = productid;
       this.productnr = productnr;
       this.deduction = deduction;
       this.productname = productname;
       this.quantity = quantity;
       this.netamount = netamount;
       this.pretax = pretax;
       this.taxtype = taxtype;
       this.type = type;
       this.category = category;
       this.description = description;
       this.createdfrom = createdfrom;
       this.createddate = createddate;
       this.changeddate = changeddate;
       this.changedfrom = changedfrom;
    }
   
    public Integer getPositionid() {
        return this.positionid;
    }
    
    public void setPositionid(Integer positionid) {
        this.positionid = positionid;
    }
    public Crmcampaign getCrmcampaign() {
        return this.crmcampaign;
    }
    
    public void setCrmcampaign(Crmcampaign crmcampaign) {
        this.crmcampaign = crmcampaign;
    }
    public Integer getProductid() {
        return this.productid;
    }
    
    public void setProductid(Integer productid) {
        this.productid = productid;
    }
    public String getProductnr() {
        return this.productnr;
    }
    
    public void setProductnr(String productnr) {
        this.productnr = productnr;
    }
    public String getDeduction() {
        return this.deduction;
    }
    
    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }
    public String getProductname() {
        return this.productname;
    }
    
    public void setProductname(String productname) {
        this.productname = productname;
    }
    public Long getQuantity() {
        return this.quantity;
    }
    
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
    public double getNetamount() {
        return this.netamount;
    }
    
    public void setNetamount(double netamount) {
        this.netamount = netamount;
    }
    public double getPretax() {
        return this.pretax;
    }
    
    public void setPretax(double pretax) {
        this.pretax = pretax;
    }
    public String getTaxtype() {
        return this.taxtype;
    }
    
    public void setTaxtype(String taxtype) {
        this.taxtype = taxtype;
    }
    public String getType() {
        return this.type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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




}


