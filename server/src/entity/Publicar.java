package entity;
// Generated 24-abr-2022 19:21:23 by Hibernate Tools 4.3.1



/**
 * Publicar generated by hbm2java
 */
public class Publicar  implements java.io.Serializable {


     private Integer id;
     private Integer idUsuario;
     private Integer idOferta;

    public Publicar() {
    }

    public Publicar(Integer idUsuario, Integer idOferta) {
       this.idUsuario = idUsuario;
       this.idOferta = idOferta;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getIdUsuario() {
        return this.idUsuario;
    }
    
    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    public Integer getIdOferta() {
        return this.idOferta;
    }
    
    public void setIdOferta(Integer idOferta) {
        this.idOferta = idOferta;
    }




}


