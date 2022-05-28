package entity;
// Generated 28-may-2022 19:18:53 by Hibernate Tools 4.3.1



/**
 * PublicarId generated by hbm2java
 */
public class PublicarId  implements java.io.Serializable {


     private int idUsuario;
     private int idOferta;

    public PublicarId() {
    }

    public PublicarId(int idUsuario, int idOferta) {
       this.idUsuario = idUsuario;
       this.idOferta = idOferta;
    }
   
    public int getIdUsuario() {
        return this.idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    public int getIdOferta() {
        return this.idOferta;
    }
    
    public void setIdOferta(int idOferta) {
        this.idOferta = idOferta;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof PublicarId) ) return false;
		 PublicarId castOther = ( PublicarId ) other; 
         
		 return (this.getIdUsuario()==castOther.getIdUsuario())
 && (this.getIdOferta()==castOther.getIdOferta());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getIdUsuario();
         result = 37 * result + this.getIdOferta();
         return result;
   }   


}


