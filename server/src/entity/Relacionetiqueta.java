package entity;
// Generated 30-abr-2022 18:28:06 by Hibernate Tools 4.3.1



/**
 * Relacionetiqueta generated by hbm2java
 */
public class Relacionetiqueta  implements java.io.Serializable {


     private RelacionetiquetaId id;
     private Integer contador;

    public Relacionetiqueta() {
    }

	
    public Relacionetiqueta(RelacionetiquetaId id) {
        this.id = id;
    }
    public Relacionetiqueta(RelacionetiquetaId id, Integer contador) {
       this.id = id;
       this.contador = contador;
    }
   
    public RelacionetiquetaId getId() {
        return this.id;
    }
    
    public void setId(RelacionetiquetaId id) {
        this.id = id;
    }
    public Integer getContador() {
        return this.contador;
    }
    
    public void setContador(Integer contador) {
        this.contador = contador;
    }




}

