package es.um.arso.productos.modelo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Entity
public class Usuario {

    @Id
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

    @PrePersist
    @PreUpdate
    private void validarId() {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalStateException("id obligatorio");
        }
    }
}
