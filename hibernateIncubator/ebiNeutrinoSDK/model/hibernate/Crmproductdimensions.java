package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1



/**
 * Crmproductdimensions generated by hbm2java
 */
public class Crmproductdimensions  implements java.io.Serializable {


     private Integer id;
     private String name;

    public Crmproductdimensions() {
    }

    public Crmproductdimensions(String name) {
       this.name = name;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }




}


