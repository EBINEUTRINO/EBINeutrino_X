package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * Companyofferreceiver generated by hbm2java
 */
public class Companyofferreceiver  implements java.io.Serializable {


     private Integer receiverid;
     private Companyoffer companyoffer;
     private Integer cnum;
     private String receivervia;
     private String gender;
     private String surname;
     private String mittelname;
     private String position;
     private String email;
     private String phone;
     private String fax;
     private String street;
     private String zip;
     private String location;
     private String pbox;
     private String country;
     private String createdfrom;
     private Date createddate;
     private String name;

    public Companyofferreceiver() {
    }

    public Companyofferreceiver(Companyoffer companyoffer, Integer cnum, String receivervia, String gender, String surname, String mittelname, String position, String email, String phone, String fax, String street, String zip, String location, String pbox, String country, String createdfrom, Date createddate, String name) {
       this.companyoffer = companyoffer;
       this.cnum = cnum;
       this.receivervia = receivervia;
       this.gender = gender;
       this.surname = surname;
       this.mittelname = mittelname;
       this.position = position;
       this.email = email;
       this.phone = phone;
       this.fax = fax;
       this.street = street;
       this.zip = zip;
       this.location = location;
       this.pbox = pbox;
       this.country = country;
       this.createdfrom = createdfrom;
       this.createddate = createddate;
       this.name = name;
    }
   
    public Integer getReceiverid() {
        return this.receiverid;
    }
    
    public void setReceiverid(Integer receiverid) {
        this.receiverid = receiverid;
    }
    public Companyoffer getCompanyoffer() {
        return this.companyoffer;
    }
    
    public void setCompanyoffer(Companyoffer companyoffer) {
        this.companyoffer = companyoffer;
    }
    public Integer getCnum() {
        return this.cnum;
    }
    
    public void setCnum(Integer cnum) {
        this.cnum = cnum;
    }
    public String getReceivervia() {
        return this.receivervia;
    }
    
    public void setReceivervia(String receivervia) {
        this.receivervia = receivervia;
    }
    public String getGender() {
        return this.gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getSurname() {
        return this.surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getMittelname() {
        return this.mittelname;
    }
    
    public void setMittelname(String mittelname) {
        this.mittelname = mittelname;
    }
    public String getPosition() {
        return this.position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getFax() {
        return this.fax;
    }
    
    public void setFax(String fax) {
        this.fax = fax;
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
    public String getPbox() {
        return this.pbox;
    }
    
    public void setPbox(String pbox) {
        this.pbox = pbox;
    }
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
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
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }




}


