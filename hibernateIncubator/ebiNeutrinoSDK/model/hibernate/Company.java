package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Company generated by hbm2java
 */
public class Company  implements java.io.Serializable {


     private Integer companyid;
     private Integer companynumber;
     private String customernr;
     private String beginchar;
     private String name;
     private String name2;
     private String phone;
     private String fax;
     private String email;
     private String employee;
     private String qualification;
     private String category;
     private String cooperation;
     private Boolean islock;
     private String web;
     private String taxnumber;
     private String description;
     private String createdfrom;
     private Date createddate;
     private String changedfrom;
     private Date changeddate;
     private Boolean isactual;
     private Set<MailAssigned> mailAssigneds = new HashSet<MailAssigned>(0);
     private Set<Companycontacts> companycontactses = new HashSet<Companycontacts>(0);
     private Set<Companyservice> companyservices = new HashSet<Companyservice>(0);
     private Set<Companyoffer> companyoffers = new HashSet<Companyoffer>(0);
     private Set<Companymeetingprotocol> companymeetingprotocols = new HashSet<Companymeetingprotocol>(0);
     private Set<Companybank> companybanks = new HashSet<Companybank>(0);
     private Set<Companyaddress> companyaddresses = new HashSet<Companyaddress>(0);
     private Set<Companyactivities> companyactivitieses = new HashSet<Companyactivities>(0);
     private Set<Companyopportunity> companyopportunities = new HashSet<Companyopportunity>(0);
     private Set<Companyhirarchie> companyhirarchies = new HashSet<Companyhirarchie>(0);
     private Set<Companyorder> companyorders = new HashSet<Companyorder>(0);

    public Company() {
    }

    public Company(Integer companynumber, String customernr, String beginchar, String name, String name2, String phone, String fax, String email, String employee, String qualification, String category, String cooperation, Boolean islock, String web, String taxnumber, String description, String createdfrom, Date createddate, String changedfrom, Date changeddate, Boolean isactual, Set<MailAssigned> mailAssigneds, Set<Companycontacts> companycontactses, Set<Companyservice> companyservices, Set<Companyoffer> companyoffers, Set<Companymeetingprotocol> companymeetingprotocols, Set<Companybank> companybanks, Set<Companyaddress> companyaddresses, Set<Companyactivities> companyactivitieses, Set<Companyopportunity> companyopportunities, Set<Companyhirarchie> companyhirarchies, Set<Companyorder> companyorders) {
       this.companynumber = companynumber;
       this.customernr = customernr;
       this.beginchar = beginchar;
       this.name = name;
       this.name2 = name2;
       this.phone = phone;
       this.fax = fax;
       this.email = email;
       this.employee = employee;
       this.qualification = qualification;
       this.category = category;
       this.cooperation = cooperation;
       this.islock = islock;
       this.web = web;
       this.taxnumber = taxnumber;
       this.description = description;
       this.createdfrom = createdfrom;
       this.createddate = createddate;
       this.changedfrom = changedfrom;
       this.changeddate = changeddate;
       this.isactual = isactual;
       this.mailAssigneds = mailAssigneds;
       this.companycontactses = companycontactses;
       this.companyservices = companyservices;
       this.companyoffers = companyoffers;
       this.companymeetingprotocols = companymeetingprotocols;
       this.companybanks = companybanks;
       this.companyaddresses = companyaddresses;
       this.companyactivitieses = companyactivitieses;
       this.companyopportunities = companyopportunities;
       this.companyhirarchies = companyhirarchies;
       this.companyorders = companyorders;
    }
   
    public Integer getCompanyid() {
        return this.companyid;
    }
    
    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }
    public Integer getCompanynumber() {
        return this.companynumber;
    }
    
    public void setCompanynumber(Integer companynumber) {
        this.companynumber = companynumber;
    }
    public String getCustomernr() {
        return this.customernr;
    }
    
    public void setCustomernr(String customernr) {
        this.customernr = customernr;
    }
    public String getBeginchar() {
        return this.beginchar;
    }
    
    public void setBeginchar(String beginchar) {
        this.beginchar = beginchar;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName2() {
        return this.name2;
    }
    
    public void setName2(String name2) {
        this.name2 = name2;
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
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmployee() {
        return this.employee;
    }
    
    public void setEmployee(String employee) {
        this.employee = employee;
    }
    public String getQualification() {
        return this.qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    public String getCategory() {
        return this.category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    public String getCooperation() {
        return this.cooperation;
    }
    
    public void setCooperation(String cooperation) {
        this.cooperation = cooperation;
    }
    public Boolean getIslock() {
        return this.islock;
    }
    
    public void setIslock(Boolean islock) {
        this.islock = islock;
    }
    public String getWeb() {
        return this.web;
    }
    
    public void setWeb(String web) {
        this.web = web;
    }
    public String getTaxnumber() {
        return this.taxnumber;
    }
    
    public void setTaxnumber(String taxnumber) {
        this.taxnumber = taxnumber;
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
    public Boolean getIsactual() {
        return this.isactual;
    }
    
    public void setIsactual(Boolean isactual) {
        this.isactual = isactual;
    }
    public Set<MailAssigned> getMailAssigneds() {
        return this.mailAssigneds;
    }
    
    public void setMailAssigneds(Set<MailAssigned> mailAssigneds) {
        this.mailAssigneds = mailAssigneds;
    }
    public Set<Companycontacts> getCompanycontactses() {
        return this.companycontactses;
    }
    
    public void setCompanycontactses(Set<Companycontacts> companycontactses) {
        this.companycontactses = companycontactses;
    }
    public Set<Companyservice> getCompanyservices() {
        return this.companyservices;
    }
    
    public void setCompanyservices(Set<Companyservice> companyservices) {
        this.companyservices = companyservices;
    }
    public Set<Companyoffer> getCompanyoffers() {
        return this.companyoffers;
    }
    
    public void setCompanyoffers(Set<Companyoffer> companyoffers) {
        this.companyoffers = companyoffers;
    }
    public Set<Companymeetingprotocol> getCompanymeetingprotocols() {
        return this.companymeetingprotocols;
    }
    
    public void setCompanymeetingprotocols(Set<Companymeetingprotocol> companymeetingprotocols) {
        this.companymeetingprotocols = companymeetingprotocols;
    }
    public Set<Companybank> getCompanybanks() {
        return this.companybanks;
    }
    
    public void setCompanybanks(Set<Companybank> companybanks) {
        this.companybanks = companybanks;
    }
    public Set<Companyaddress> getCompanyaddresses() {
        return this.companyaddresses;
    }
    
    public void setCompanyaddresses(Set<Companyaddress> companyaddresses) {
        this.companyaddresses = companyaddresses;
    }
    public Set<Companyactivities> getCompanyactivitieses() {
        return this.companyactivitieses;
    }
    
    public void setCompanyactivitieses(Set<Companyactivities> companyactivitieses) {
        this.companyactivitieses = companyactivitieses;
    }
    public Set<Companyopportunity> getCompanyopportunities() {
        return this.companyopportunities;
    }
    
    public void setCompanyopportunities(Set<Companyopportunity> companyopportunities) {
        this.companyopportunities = companyopportunities;
    }
    public Set<Companyhirarchie> getCompanyhirarchies() {
        return this.companyhirarchies;
    }
    
    public void setCompanyhirarchies(Set<Companyhirarchie> companyhirarchies) {
        this.companyhirarchies = companyhirarchies;
    }
    public Set<Companyorder> getCompanyorders() {
        return this.companyorders;
    }
    
    public void setCompanyorders(Set<Companyorder> companyorders) {
        this.companyorders = companyorders;
    }




}


