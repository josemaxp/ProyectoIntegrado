package entity;
// Generated 28-may-2022 19:18:53 by Hibernate Tools 4.3.1



/**
 * Etiqueta generated by hbm2java
 */
public class Etiqueta  implements java.io.Serializable {


     private Integer id;
     private String nombre;
     private Integer contador;

    public Etiqueta() {
    }

    public Etiqueta(String nombre, Integer contador) {
       this.nombre = nombre;
       this.contador = contador;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getContador() {
        return this.contador;
    }
    
    public void setContador(Integer contador) {
        this.contador = contador;
    }




}


