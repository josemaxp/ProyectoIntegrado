package entity;
// Generated 28-may-2022 19:18:53 by Hibernate Tools 4.3.1



/**
 * Tener generated by hbm2java
 */
public class Tener  implements java.io.Serializable {


     private TenerId id;
     private Boolean especial;

    public Tener() {
    }

	
    public Tener(TenerId id) {
        this.id = id;
    }
    public Tener(TenerId id, Boolean especial) {
       this.id = id;
       this.especial = especial;
    }
   
    public TenerId getId() {
        return this.id;
    }
    
    public void setId(TenerId id) {
        this.id = id;
    }
    public Boolean getEspecial() {
        return this.especial;
    }
    
    public void setEspecial(Boolean especial) {
        this.especial = especial;
    }




}


