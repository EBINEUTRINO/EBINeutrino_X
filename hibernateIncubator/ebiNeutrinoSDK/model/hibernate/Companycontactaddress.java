package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Companycontactaddress generated by hbm2java
 */
public class Companycontactaddress  implements java.io.Serializable {


     private Integer addressid;
     private Companycontacts companycontacts;
     private String addresstype;
     private String street;
     private String zip;
     private String location;
     private String country;
     private String pbox;
     private String createdfrom;
     private Date createddate;
     private String changedfrom;
     private Date changeddate;

    public Companycontactaddress() {
    }

    public Companycontactaddress(Companycontacts companycontacts, String addresstype, String street, String zip, String location, String country, String pbox, String createdfrom, Date createddate, String changedfrom, Date changeddate) {
       this.companycontacts = companycontacts;
       this.addresstype = addresstype;
       this.street = street;
       this.zip = zip;
       this.location = location;
       this.country = country;
       this.pbox = pbox;
       this.createdfrom = createdfrom;
       this.createddate = createddate;
       this.changedfrom = changedfrom;
       this.changeddate = changeddate;
    }
   
    public Integer getAddressid() {
        return this.addressid;
    }
    
    public void setAddressid(Integer addressid) {
        this.addressid = addressid;
    }
    public Companycontacts getCompanycontacts() {
        return this.companycontacts;
    }
    
    public void setCompanycontacts(Companycontacts companycontacts) {
        this.companycontacts = companycontacts;
    }
    public String getAddresstype() {
        return this.addresstype;
    }
    
    public void setAddresstype(String addresstype) {
        this.addresstype = addresstype;
    }
    public String getStreet() {
        return this.street;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    public String getZip() {
        return this.zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    public String getPbox() {
        return this.pbox;
    }
    
    public void setPbox(String pbox) {
        this.pbox = pbox;
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
    public String getChangedfrom() {
        return this.changedfrom;
    }
    
    public void setChangedfrom(String changedfrom) {
        this.changedfrom = changedfrom;
    }
    public Date getChangeddate() {
        return this.changeddate;
    }
    
    public void setChangeddate(Date changeddate) {
        this.changeddate = changeddate;
    }




}

