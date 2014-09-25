package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.Date;

/**
 * MailAccount generated by hbm2java
 */
public class MailAccount  implements java.io.Serializable {


     private Integer id;
     private String accountname;
     private String folderName;
     private Boolean deleteMessage;
     private String smtpServer;
     private String smtpUser;
     private String smtpPassword;
     private String emailadress;
     private String popServer;
     private String popUser;
     private String popPassword;
     private String emailsTitle;
     private String createfrom;
     private Date createdate;

    public MailAccount() {
    }

    public MailAccount(String accountname, String folderName, Boolean deleteMessage, String smtpServer, String smtpUser, String smtpPassword, String emailadress, String popServer, String popUser, String popPassword, String emailsTitle, String createfrom, Date createdate) {
       this.accountname = accountname;
       this.folderName = folderName;
       this.deleteMessage = deleteMessage;
       this.smtpServer = smtpServer;
       this.smtpUser = smtpUser;
       this.smtpPassword = smtpPassword;
       this.emailadress = emailadress;
       this.popServer = popServer;
       this.popUser = popUser;
       this.popPassword = popPassword;
       this.emailsTitle = emailsTitle;
       this.createfrom = createfrom;
       this.createdate = createdate;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getAccountname() {
        return this.accountname;
    }
    
    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }
    public String getFolderName() {
        return this.folderName;
    }
    
    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
    public Boolean getDeleteMessage() {
        return this.deleteMessage;
    }
    
    public void setDeleteMessage(Boolean deleteMessage) {
        this.deleteMessage = deleteMessage;
    }
    public String getSmtpServer() {
        return this.smtpServer;
    }
    
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }
    public String getSmtpUser() {
        return this.smtpUser;
    }
    
    public void setSmtpUser(String smtpUser) {
        this.smtpUser = smtpUser;
    }
    public String getSmtpPassword() {
        return this.smtpPassword;
    }
    
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }
    public String getEmailadress() {
        return this.emailadress;
    }
    
    public void setEmailadress(String emailadress) {
        this.emailadress = emailadress;
    }
    public String getPopServer() {
        return this.popServer;
    }
    
    public void setPopServer(String popServer) {
        this.popServer = popServer;
    }
    public String getPopUser() {
        return this.popUser;
    }
    
    public void setPopUser(String popUser) {
        this.popUser = popUser;
    }
    public String getPopPassword() {
        return this.popPassword;
    }
    
    public void setPopPassword(String popPassword) {
        this.popPassword = popPassword;
    }
    public String getEmailsTitle() {
        return this.emailsTitle;
    }
    
    public void setEmailsTitle(String emailsTitle) {
        this.emailsTitle = emailsTitle;
    }
    public String getCreatefrom() {
        return this.createfrom;
    }
    
    public void setCreatefrom(String createfrom) {
        this.createfrom = createfrom;
    }
    public Date getCreatedate() {
        return this.createdate;
    }
    
    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }




}


