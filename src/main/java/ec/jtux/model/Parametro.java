package ec.jtux.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "parametros", uniqueConstraints = @UniqueConstraint(columnNames = "codigo"))
public class Parametro {

    @Id
    @Pattern(regexp = "[A-Za-z]*", message = "debe contener solo letras")
    private String codigo;
    @Pattern(regexp = "[A-Za-z ]*", message = "debe contener solo letras y espacios")
    private String nombre;
    @Pattern(regexp = "[A-Za-z]*", message = "debe contener solo letras")
    private String tipo;
    private String valor;
    private boolean eliminado;
    private boolean estado;
    
    public String getCodigo(){
        return this.codigo;
    }

    public void setCodigo(String codigo){
        this.codigo = codigo;
    }

    public String getNombre(){
        return this.nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getTipo(){
        return this.tipo;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

    public String getValor(){
        return this.valor;
    }

    public void setValor(String valor){
        this.valor = valor;
    }

    public boolean isEliminado(){
        return this.eliminado;
    }

    public void setEliminado(boolean eliminado){
        this.eliminado = eliminado;
    }

    public boolean isEstado(){
        return this.estado;
    }

    public void setEstado(boolean estado){
        this.estado = estado;
    }

}
