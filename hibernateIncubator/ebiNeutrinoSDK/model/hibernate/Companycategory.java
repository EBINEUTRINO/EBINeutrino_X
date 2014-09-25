package ebiNeutrinoSDK.model.hibernate;
// Generated 23-gen-2012 12.30.18 by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.Set;

/**
 * Companycategory generated by hbm2java
 */
public class Companycategory  implements java.io.Serializable {


     private Integer id;
     private String name;
     private Set<Companynumber> companynumbers = new HashSet<Companynumber>(0);

    public Companycategory() {
    }

    public Companycategory(String name, Set<Companynumber> companynumbers) {
       this.name = name;
       this.companynumbers = companynumbers;
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
    public Set<Companynumber> getCompanynumbers() {
        return this.companynumbers;
    }
    
    public void setCompanynumbers(Set<Companynumber> companynumbers) {
        this.companynumbers = companynumbers;
    }




}


