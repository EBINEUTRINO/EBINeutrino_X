/
    create table if not exists  accountstack (
        ACSTACKID integer not null auto_increment,
        ACCOUNTNR varchar(255),
        ACCOUNT_TYPE integer,
        ACCOUNT_TAX_TYPE varchar(255),
        ACCOUNT_DEBIT varchar(255),
        ACCOUNT_CREDIT varchar(255),
        ACCOUNT_D_NAME varchar(255),
        ACCOUNT_C_NAME varchar(255),
        ACCOUNT_C_VALUE double precision,
        ACCOUNT_D_VALUE double precision,
        ACCOUNTNAME varchar(255),
        ACCOUNTVALUE double precision,
        DESCRIPTION text,
        ACCOUNTDATE datetime,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (ACSTACKID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  accountstackcd (
        ACCOUNTSTACKCDID integer not null auto_increment,
        ACCOUNTSTACKID integer,
        CREDITDEBITNUMBER varchar(150),
        CREDITDEBITNAME varchar(255),
        CREDITDEBITVALUE double precision,
        CREDITDEBITTAXTNAME varchar(150),
        CREDITDEBITTYPE integer,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        primary key (ACCOUNTSTACKCDID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  accountstackdocs (
        ACCOUNTDOCID integer not null auto_increment,
        ACCOUNTID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (ACCOUNTDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  company (
        COMPANYID integer not null auto_increment,
        COMPANYNUMBER integer,
        CUSTOMERNR varchar(255),
        BEGINCHAR varchar(10),
        NAME varchar(255),
        NAME2 varchar(255),
        PHONE varchar(50),
        FAX varchar(50),
        EMAIL varchar(50),
        EMPLOYEE varchar(100),
        QUALIFICATION varchar(100),
        CATEGORY varchar(150),
        COOPERATION varchar(150),
        ISLOCK bit,
        WEB varchar(150),
        TAXNUMBER varchar(50),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        ISACTUAL bit,
        primary key (COMPANYID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyactivities (
        ACTIVITYID integer not null auto_increment,
        COMPANYID integer,
        ACTIVITYNAME varchar(150),
        ACTIVITYTYPE varchar(50),
        ACTIVITYSTATUS varchar(50),
        DUEDATE datetime,
        DURATION integer,
        ACOLOR varchar(18),
        ACTIVITYCLOSEDATE datetime,
        ACTIVITYISCLOSED bit,
        ACTIVITYDESCRIPTION text,
        ISLOCK bit,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        TIMERSTART integer,
        TIMERDISABLED integer,
        TIMER_EXTRA text,
        primary key (ACTIVITYID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyactivitiesdocs (
        ACTIVITYDOCID integer not null auto_increment,
        ACTIVITYID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (ACTIVITYDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyactivitystatus (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyactivitytype (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyaddress (
        ADDRESSID integer not null auto_increment,
        COMPANYID integer,
        ADDRESSTYPE varchar(150),
        STREET varchar(50),
        ZIP varchar(10),
        LOCATION varchar(50),
        PBOX varchar(150),
        COUNTRY varchar(150),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (ADDRESSID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companybank (
        BANKID integer not null auto_increment,
        COMPANYID integer,
        BANKNAME varchar(255),
        BANKBSB varchar(255),
        BANKACCOUNT varchar(255),
        BANKBIC varchar(255),
        BANKIBAN varchar(255),
        BANKCOUNTRY varchar(150),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (BANKID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companycategory (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyclassification (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companycontactaddress (
        ADDRESSID integer not null auto_increment,
        CONTACTID integer,
        ADDRESSTYPE varchar(150),
        STREET varchar(80),
        ZIP varchar(20),
        LOCATION varchar(150),
        COUNTRY varchar(150),
        PBOX varchar(150),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (ADDRESSID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companycontacts (
        CONTACTID integer not null auto_increment,
        COMPANYID integer,
        MAINCONTACT bit,
        GENDER varchar(20),
        TITLE varchar(255),
        SURNAME varchar(150),
        NAME varchar(150),
        MITTELNAME varchar(150),
        POSITION varchar(150),
        BIRDDATE datetime,
        PHONE varchar(30),
        FAX varchar(30),
        MOBILE varchar(30),
        EMAIL varchar(50),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (CONTACTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companycooperation (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyhirarchie (
        HIERARCHIEID integer not null auto_increment,
        COMPANYID integer,
        NAME varchar(255),
        ROOT integer,
        PARENT integer,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (HIERARCHIEID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companymeetingcontacts (
        MEETINGCONTACTID integer not null auto_increment,
        MEETINGID integer,
        POS integer,
        GENDER varchar(20),
        SURNAME varchar(150),
        NAME varchar(150),
        MITTELNAME varchar(150),
        POSITION varchar(150),
        BIRDDATE datetime,
        PHONE varchar(30),
        FAX varchar(30),
        MOBILE varchar(30),
        EMAIL varchar(50),
        STREET varchar(255),
        LOCATION varchar(255),
        ZIP varchar(255),
        COUNTRY varchar(255),
        PBOX varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE date not null,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (MEETINGCONTACTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companymeetingdoc (
        MEETINGDOCID integer not null auto_increment,
        MEETINGID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (MEETINGDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companymeetingprotocol (
        MEETINGPROTOCOLID integer not null auto_increment,
        COMPANYID integer,
        MEETINGSUBJECT varchar(255),
        MEETINGTYPE varchar(150),
        PROTOCOL text,
        METINGDATE datetime,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (MEETINGPROTOCOLID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companymeetingtype (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companynumber (
        ID integer not null auto_increment,
        CATEGORYID integer,
        CATEGORY varchar(255),
        BEGINCHAR varchar(10),
        NUMBERFROM integer,
        NUMBERTO integer,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyoffer (
        OFFERID integer not null auto_increment,
        COMPANYID integer,
        OPPORTUNITYID integer,
        OFFERNR varchar(255),
        NAME varchar(255),
        STATUS varchar(100),
        OFFERVERSION integer,
        OFFERDATE datetime,
        VALIDTO datetime,
        ISRECIEVED bit,
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (OFFERID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyofferdocs (
        OFFERDOCID integer not null auto_increment,
        OFFERID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (OFFERDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyofferpositions (
        POSITIONID integer not null auto_increment,
        OFFERID integer,
        PRODUCTID integer,
        PRODUCTNR varchar(255),
        DEDUCTION varchar(5),
        PRODUCTNAME varchar(255),
        QUANTITY bigint,
        NETAMOUNT double precision,
        PRETAX double precision,
        TAXTYPE varchar(150),
        TYPE varchar(255),
        CATEGORY varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (POSITIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyofferreceiver (
        RECEIVERID integer not null auto_increment,
        OFFERID integer,
        CNUM integer,
        RECEIVERVIA varchar(150),
        GENDER varchar(20),
        SURNAME varchar(150),
        MITTELNAME varchar(150),
        POSITION varchar(150),
        EMAIL varchar(50),
        PHONE varchar(30),
        FAX varchar(30),
        STREET varchar(150),
        ZIP varchar(15),
        LOCATION varchar(80),
        PBOX varchar(100),
        COUNTRY varchar(100),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        NAME varchar(150),
        primary key (RECEIVERID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyofferstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunity (
        OPPORTUNITYID integer not null auto_increment,
        COMPANYID integer,
        NAME varchar(255),
        SALESTAGE varchar(100),
        PROBABILITY varchar(100),
        OPPORTUNITYVALUE double precision,
        ISCLOSE bit,
        CLOSEDATE datetime,
        BUSINESSTYPE varchar(150),
        EVALUATIONSTATUS varchar(150),
        EVALUETIONDATE datetime,
        BUDGETSTATUS varchar(150),
        BUDGETDATE datetime,
        SALESTAGEDATE datetime,
        OPPORTUNITYSTATUS varchar(150),
        OPPORTUNITYSTATUSDATE datetime,
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (OPPORTUNITYID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunitybgstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunitybustyp (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunitycontact (
        OPPORTUNITYCONTACTID integer not null auto_increment,
        OPPORTUNITYID integer,
        POS integer,
        GENDER varchar(20),
        SURNAME varchar(150),
        NAME varchar(150),
        MITTELNAME varchar(150),
        STREET varchar(255),
        ZIP varchar(255),
        LOCATION varchar(255),
        PBOX varchar(255),
        COUNTRY varchar(255),
        POSITION varchar(150),
        BIRDDATE datetime,
        PHONE varchar(30),
        FAX varchar(30),
        MOBILE varchar(30),
        EMAIL varchar(50),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (OPPORTUNITYCONTACTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunityevstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunitysstage (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopportunitystatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyopporunitydocs (
        DOCID integer not null auto_increment,
        OPPORTUNITYID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (DOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyorder (
        ORDERID integer not null auto_increment,
        COMPANYID integer,
        ORDERNR varchar(150),
        OFFERID integer,
        NAME varchar(255),
        INVOICECREATED bit,
        STATUS varchar(150),
        ORDERVERSION integer,
        OFFERDATE datetime,
        VALIDTO datetime,
        ISRECIEVED bit,
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (ORDERID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyorderdocs (
        ORDERDOCID integer not null auto_increment,
        ORDERID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        primary key (ORDERDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyorderpositions (
        POSITIONID integer not null auto_increment,
        ORDERID integer,
        PRODUCTID integer,
        DEDUCTION varchar(5),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        QUANTITY bigint,
        NETAMOUNT double precision,
        PRETAX double precision,
        TAXTYPE varchar(150),
        TYPE varchar(255),
        CATEGORY varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (POSITIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyorderreceiver (
        RECEIVERID integer not null auto_increment,
        ORDERID integer,
        CNUM integer,
        RECEIVERVIA varchar(50),
        GENDER varchar(20),
        SURNAME varchar(150),
        NAME varchar(150),
        MITTELNAME varchar(150),
        POSITION varchar(150),
        EMAIL varchar(50),
        PHONE varchar(30),
        FAX varchar(30),
        STREET varchar(50),
        ZIP varchar(15),
        LOCATION varchar(100),
        PBOX varchar(100),
        COUNTRY varchar(100),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (RECEIVERID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyorderstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyproductcategory (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyproducttax (
        ID integer not null auto_increment,
        NAME varchar(255),
        TAXVALUE double precision,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyproducttaxvalue (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyproducttype (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservice (
        SERVICEID integer not null auto_increment,
        COMPANYID integer,
        SERVICENR varchar(150),
        NAME varchar(255),
        CATEGORY varchar(255),
        TYPE varchar(255),
        STATUS varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (SERVICEID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicecategory (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicedocs (
        SERVICEDOCID integer not null auto_increment,
        SERVICEID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        primary key (SERVICEDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicepositions (
        POSITIONID integer not null auto_increment,
        SERVICEID integer,
        PRODUCTID integer,
        DEDUCTION varchar(5),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        QUANTITY bigint,
        NETAMOUNT double precision,
        PRETAX double precision,
        TAXTYPE varchar(150),
        TYPE varchar(255),
        CATEGORY varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (POSITIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicepsol (
        PROSOLID integer not null auto_increment,
        SERVICEID integer,
        SOLUTIONNR varchar(150),
        NAME varchar(255),
        CLASSIFICATION varchar(255),
        CATEGORY varchar(255),
        TYPE varchar(255),
        STATUS varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (PROSOLID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicestatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  companyservicetype (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmaddresstype (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcalendar (
        CALENDARID integer not null auto_increment,
        CUSER varchar(150),
        NAME varchar(255),
        COLOR varchar(15),
        DESCRIPTION text,
        STARTDATE datetime,
        ENDDATE datetime,
        CICON longblob,
        primary key (CALENDARID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaign (
        CAMPAIGNID integer not null auto_increment,
        CAMPAIGNNR varchar(255),
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        STATUS varchar(255),
        VALIDFROM datetime,
        VALIDTO datetime,
        primary key (CAMPAIGNID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaigndocs (
        DOCID integer not null auto_increment,
        CAMPAIGNID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        primary key (DOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaignposition (
        POSITIONID integer not null auto_increment,
        CAMPAIGNID integer,
        PRODUCTID integer,
        PRODUCTNR varchar(255),
        DEDUCTION varchar(5),
        PRODUCTNAME varchar(255),
        QUANTITY bigint,
        NETAMOUNT double precision,
        PRETAX double precision,
        TAXTYPE varchar(150),
        TYPE varchar(255),
        CATEGORY varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (POSITIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaignprop (
        PROPERTIESID integer not null auto_increment,
        CAMPAIGNID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        VALUE varchar(255),
        primary key (PROPERTIESID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaignprops (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaignreceiver (
        RECEIVERID integer not null auto_increment,
        CAMPAIGNID integer,
        CNUM integer,
        RECEIVERVIA varchar(50),
        COMPANYNAME varchar(255),
        COMPANYNUMBER varchar(255),
        GENDER varchar(20),
        SURNAME varchar(150),
        NAME varchar(150),
        MITTELNAME varchar(150),
        POSITION varchar(150),
        EMAIL varchar(50),
        PHONE varchar(30),
        FAX varchar(30),
        STREET varchar(50),
        ZIP varchar(15),
        LOCATION varchar(100),
        PBOX varchar(100),
        COUNTRY varchar(100),
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (RECEIVERID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmcampaignstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crminvoice (
        INVOICEID integer not null auto_increment,
        ASSOSIATION varchar(150),
        INVOICENR integer,
        BEGINCHAR varchar(10),
        NAME varchar(255),
        STATUS varchar(255),
        CATEGORY varchar(255),
        DATE datetime,
        TAXTYPE varchar(150),
        GENDER varchar(100),
        POSITION varchar(100),
        COMPANYNAME varchar(255),
        CONTACTNAME varchar(255),
        CONTACTSURNAME varchar(255),
        CONTACTSTREET varchar(255),
        CONTACTZIP varchar(100),
        CONTACTLOCATION varchar(255),
        CONTACTPOSTCODE varchar(255),
        CONTACTCOUNTRY varchar(255),
        CONTACTTELEPHONE varchar(255),
        CONTACTFAX varchar(255),
        CONTACTEMAIL varchar(255),
        CONTACTWEB varchar(255),
        CONTACTDESCRIPTION text,
        CREATEDDATE datetime,
        CREATEDFROM varchar(255),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(255),
        primary key (INVOICEID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crminvoicecategory (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crminvoicenumber (
        ID integer not null auto_increment,
        CATEGORYID integer,
        CATEGORY varchar(255),
        BEGINCHAR varchar(10),
        NUMBERFROM integer,
        NUMBERTO integer,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crminvoiceposition (
        POSITIONID integer not null auto_increment,
        INVOICEID integer,
        PRODUCTID integer,
        DEDUCTION varchar(5),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        QUANTITY bigint,
        NETAMOUNT double precision,
        PRETAX double precision,
        TAXTYPE varchar(150),
        TYPE varchar(255),
        CATEGORY varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        primary key (POSITIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crminvoicestatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsolcategory (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsolclass (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsoldocs (
        SOLUTIONDOCID integer not null auto_increment,
        SOLUTIONID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        primary key (SOLUTIONDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsolposition (
        PRODUCTID integer not null auto_increment,
        SOLUTIONID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        CATEGORY varchar(255),
        TYPE varchar(255),
        TAXTYPE varchar(255),
        NETAMOUNT double precision,
        PRETAX double precision,
        DIPENDENCYID integer,
        DIMENSIONID integer,
        PICTURE longblob,
        PICTURENAME varchar(255),
        DESCRIPTION text,
        primary key (PRODUCTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsolstatus (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsoltype (
        ID integer not null auto_increment,
        NAME varchar(150),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproblemsolutions (
        PROSOLID integer not null auto_increment,
        SERVICENR varchar(150),
        NAME varchar(255),
        CLASSIFICATION varchar(255),
        CATEGORY varchar(255),
        TYPE varchar(255),
        STATUS varchar(255),
        DESCRIPTION text,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (PROSOLID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproduct (
        PRODUCTID integer not null auto_increment,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        CATEGORY varchar(255),
        TYPE varchar(255),
        TAXTYPE varchar(255),
        NETAMOUNT double precision,
        PRETAX double precision,
        SALEPRICE double precision,
        PICTURE longblob,
        PICTURENAME varchar(255),
        DESCRIPTION text,
        primary key (PRODUCTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproductdependency (
        DEPENDENCYID integer not null auto_increment,
        PRODUCTID integer,
        PRODUCTIDID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        PRODUCTNR varchar(255),
        PRODUCTNAME varchar(255),
        primary key (DEPENDENCYID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproductdimension (
        DIMENSIONID integer not null auto_increment,
        PRODUCTID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        VALUE varchar(255),
        primary key (DIMENSIONID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproductdimensions (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproductdocs (
        PRODUCTDOCID integer not null auto_increment,
        PRODUCTID integer,
        NAME varchar(255),
        FILES longblob,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        primary key (PRODUCTDOCID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmproject (
        PROJECTID integer not null auto_increment,
        PROJECTNR varchar(255),
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        MANAGER varchar(255),
        BUDGET double precision,
        ACTUALCOST double precision,
        REMAINCOST double precision,
        STATUS varchar(255),
        VALIDFROM datetime,
        VALIDTO datetime,
        primary key (PROJECTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojectcost (
        COSTID integer not null auto_increment,
        TASKID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        VALUE double precision,
        primary key (COSTID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojectcosts (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojectprop (
        PROPERTIESID integer not null auto_increment,
        TASKID integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        CHANGEDDATE datetime,
        CHANGEDFROM varchar(150),
        NAME varchar(255),
        VALUE varchar(255),
        primary key (PROPERTIESID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojectprops (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojectstatus (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojecttask (
        TASKIID integer not null auto_increment,
        PROJECTID integer,
        TASKID varchar(255),
        PARENTSTASKID text,
        X integer,
        Y integer,
        NAME varchar(255),
        STATUS varchar(255),
        TYPE varchar(255),
        DURATION integer,
        COLOR varchar(12),
        DONE integer,
        DESCRIPTION text,
        primary key (TASKIID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojecttaskstatus (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  crmprojecttasktype (
        ID integer not null auto_increment,
        NAME varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  ebicrmhistory (
        HISTORYID integer not null auto_increment,
        COMPANYID integer,
        CATEGORY varchar(255),
        CHANGEDVALUE text,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        primary key (HISTORYID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  ebidatastore (
        ID integer not null auto_increment,
        NAME varchar(255),
        TEXT text,
        EXTRA varchar(255),
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  ebipessimistic (
        OPTIMISTICID integer not null auto_increment,
        RECORDID integer,
        MODULENAME varchar(255),
        USER varchar(255),
        LOCKDATE datetime,
        STATUS integer,
        primary key (OPTIMISTICID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  ebiuser (
        ID integer not null auto_increment,
        CREATEDFROM varchar(150),
        CREATEDDATE datetime,
        CHANGEDFROM varchar(150),
        CHANGEDDATE datetime,
        EBIUSER varchar(50),
        PASSWD varchar(255),
        IS_ADMIN bit,
        MODULEID text,
        CANSAVE bit,
        CANPRINT bit,
        CANDELETE bit,
        primary key (ID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;

/
    create table if not exists  set_reportformodule (
        IDREPORTFORMODULE integer not null auto_increment,
        REPORTNAME varchar(255),
        REPORTCATEGORY varchar(255),
        REPORTFILENAME text,
        REPORTDATE datetime,
        SHOWASPDF bit,
        SHOWASWINDOW bit,
        PRINTAUTO bit,
        ISACTIVE bit,
        primary key (IDREPORTFORMODULE)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    create table if not exists  set_reportparameter (
        PARAMID integer not null auto_increment,
        REPORTID integer,
        POSITION integer,
        CREATEDDATE datetime,
        CREATEDFROM varchar(150),
        PARAMNAME varchar(150),
        PARAMTYPE varchar(150),
        primary key (PARAMID)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
/
    alter table accountstackdocs 
        add index FK9275F936EDEA7D2 (ACCOUNTID), 
        add constraint FK9275F936EDEA7D2 
        foreign key (ACCOUNTID) 
        references accountstack (ACSTACKID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyactivities 
        add index FK9B92C02A4B8DEA26 (COMPANYID), 
        add constraint FK9B92C02A4B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyactivitiesdocs 
        add index FK16B1BD65260096A5 (ACTIVITYID), 
        add constraint FK16B1BD65260096A5 
        foreign key (ACTIVITYID) 
        references companyactivities (ACTIVITYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyaddress 
        add index FK4887C8D74B8DEA26 (COMPANYID), 
        add constraint FK4887C8D74B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companybank 
        add index FKE1B8BC394B8DEA26 (COMPANYID), 
        add constraint FKE1B8BC394B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companycontactaddress 
        add index FK12E6B1918586275C (CONTACTID), 
        add constraint FK12E6B1918586275C 
        foreign key (CONTACTID) 
        references companycontacts (CONTACTID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companycontacts 
        add index FKEF42CFD04B8DEA26 (COMPANYID), 
        add constraint FKEF42CFD04B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyhirarchie 
        add index FK234282E64B8DEA26 (COMPANYID), 
        add constraint FK234282E64B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companymeetingcontacts 
        add index FK5CE1D971E2F680DB (MEETINGID), 
        add constraint FK5CE1D971E2F680DB 
        foreign key (MEETINGID) 
        references companymeetingprotocol (MEETINGPROTOCOLID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companymeetingdoc 
        add index FK5151515AE2F680DB (MEETINGID), 
        add constraint FK5151515AE2F680DB 
        foreign key (MEETINGID) 
        references companymeetingprotocol (MEETINGPROTOCOLID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companymeetingprotocol 
        add index FK43BF07B64B8DEA26 (COMPANYID), 
        add constraint FK43BF07B64B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companynumber 
        add index FK6C0E060610245D65 (CATEGORYID), 
        add constraint FK6C0E060610245D65 
        foreign key (CATEGORYID) 
        references companycategory (ID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyoffer 
        add index FK561823FF4B8DEA26 (COMPANYID), 
        add constraint FK561823FF4B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyofferdocs 
        add index FK85872EBA33647FC5 (OFFERID), 
        add constraint FK85872EBA33647FC5 
        foreign key (OFFERID) 
        references companyoffer (OFFERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyofferpositions 
        add index FKCA47774B33647FC5 (OFFERID), 
        add constraint FKCA47774B33647FC5 
        foreign key (OFFERID) 
        references companyoffer (OFFERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyofferreceiver 
        add index FK993B9EEE33647FC5 (OFFERID), 
        add constraint FK993B9EEE33647FC5 
        foreign key (OFFERID) 
        references companyoffer (OFFERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyopportunity 
        add index FKC073EA364B8DEA26 (COMPANYID), 
        add constraint FKC073EA364B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyopportunitycontact 
        add index FK5D67712A9A49D973 (OPPORTUNITYID), 
        add constraint FK5D67712A9A49D973 
        foreign key (OPPORTUNITYID) 
        references companyopportunity (OPPORTUNITYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyopporunitydocs 
        add index FKB8C5545B9A49D973 (OPPORTUNITYID), 
        add constraint FKB8C5545B9A49D973 
        foreign key (OPPORTUNITYID) 
        references companyopportunity (OPPORTUNITYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyorder 
        add index FK561D90F14B8DEA26 (COMPANYID), 
        add constraint FK561D90F14B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyorderdocs 
        add index FKFA48D2AC47C7E529 (ORDERID), 
        add constraint FKFA48D2AC47C7E529 
        foreign key (ORDERID) 
        references companyorder (ORDERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyorderpositions 
        add index FKE9087A9947C7E529 (ORDERID), 
        add constraint FKE9087A9947C7E529 
        foreign key (ORDERID) 
        references companyorder (ORDERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyorderreceiver 
        add index FKCBC5F9E047C7E529 (ORDERID), 
        add constraint FKCBC5F9E047C7E529 
        foreign key (ORDERID) 
        references companyorder (ORDERID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyservice 
        add index FK333F3984B8DEA26 (COMPANYID), 
        add constraint FK333F3984B8DEA26 
        foreign key (COMPANYID) 
        references company (COMPANYID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyservicedocs 
        add index FK9A8F89D33F54B1B7 (SERVICEID), 
        add constraint FK9A8F89D33F54B1B7 
        foreign key (SERVICEID) 
        references companyservice (SERVICEID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyservicepositions 
        add index FKDE923D23F54B1B7 (SERVICEID), 
        add constraint FKDE923D23F54B1B7 
        foreign key (SERVICEID) 
        references companyservice (SERVICEID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table companyservicepsol 
        add index FK9A950EB83F54B1B7 (SERVICEID), 
        add constraint FK9A950EB83F54B1B7 
        foreign key (SERVICEID) 
        references companyservice (SERVICEID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmcampaigndocs 
        add index FKECB8C02984114DEA (CAMPAIGNID), 
        add constraint FKECB8C02984114DEA 
        foreign key (CAMPAIGNID) 
        references crmcampaign (CAMPAIGNID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmcampaignposition 
        add index FK5E1F901784114DEA (CAMPAIGNID), 
        add constraint FK5E1F901784114DEA 
        foreign key (CAMPAIGNID) 
        references crmcampaign (CAMPAIGNID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmcampaignprop 
        add index FKECBE415184114DEA (CAMPAIGNID), 
        add constraint FKECBE415184114DEA 
        foreign key (CAMPAIGNID) 
        references crmcampaign (CAMPAIGNID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmcampaignreceiver 
        add index FK158E0DD84114DEA (CAMPAIGNID), 
        add constraint FK158E0DD84114DEA 
        foreign key (CAMPAIGNID) 
        references crmcampaign (CAMPAIGNID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crminvoicenumber 
        add index FKB7363F987D593035 (CATEGORYID), 
        add constraint FKB7363F987D593035 
        foreign key (CATEGORYID) 
        references crminvoicecategory (ID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crminvoiceposition 
        add index FK5FA825B8FB72A7A6 (INVOICEID), 
        add constraint FK5FA825B8FB72A7A6 
        foreign key (INVOICEID) 
        references crminvoice (INVOICEID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmproblemsoldocs 
        add index FK93D959AA3838265E (SOLUTIONID), 
        add constraint FK93D959AA3838265E 
        foreign key (SOLUTIONID) 
        references crmproblemsolutions (PROSOLID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmproblemsolposition 
        add index FKE86581183838265E (SOLUTIONID), 
        add constraint FKE86581183838265E 
        foreign key (SOLUTIONID) 
        references crmproblemsolutions (PROSOLID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmproductdependency 
        add index FK3B43375C9D2652AA (PRODUCTID), 
        add constraint FK3B43375C9D2652AA 
        foreign key (PRODUCTID) 
        references crmproduct (PRODUCTID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmproductdimension 
        add index FKC0B1C3359D2652AA (PRODUCTID), 
        add constraint FKC0B1C3359D2652AA 
        foreign key (PRODUCTID) 
        references crmproduct (PRODUCTID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmprojectcost 
        add index FK9DC30DC8B0FD4C6F (TASKID), 
        add constraint FK9DC30DC8B0FD4C6F 
        foreign key (TASKID) 
        references crmprojecttask (TASKIID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmprojectprop 
        add index FK9DC9015EB0FD4C6F (TASKID), 
        add constraint FK9DC9015EB0FD4C6F 
        foreign key (TASKID) 
        references crmprojecttask (TASKIID) ON DELETE CASCADE ON UPDATE CASCADE;
/
    alter table crmprojecttask 
        add index FK9DCA9380A6846C7E (PROJECTID), 
        add constraint FK9DCA9380A6846C7E 
        foreign key (PROJECTID) 
        references crmproject (PROJECTID) ON DELETE CASCADE ON UPDATE CASCADE;
/

    alter table set_reportparameter 
        add index FK765F07183724B25D (REPORTID), 
        add constraint FK765F07183724B25D 
        foreign key (REPORTID) 
        references set_reportformodule (IDREPORTFORMODULE) ON DELETE CASCADE ON UPDATE CASCADE;
/
