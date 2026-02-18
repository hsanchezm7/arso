package es.um.arso.usuarios.servicio;

public class UsuarioResumen {
    
    private String id;
    private String nombre;
    private String email;

    public UsuarioResumen() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
