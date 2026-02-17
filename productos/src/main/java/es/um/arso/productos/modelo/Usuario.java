package es.um.arso.productos.modelo;

import es.um.arso.repositorio.Identificable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Usuario implements Identificable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String email;
    private String nombre;
    private String apellidos;

    public Usuario() {}

    public Usuario(String email, String nombre, String apellidos) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
