package es.um.arso.usuarios.rest.dto;

public class UsuarioAuthDto {

    private String id;
    private String nombre;
    private String roles;

    public UsuarioAuthDto() {}

    public UsuarioAuthDto(String id, String nombre, String roles) {
        this.id = id;
        this.nombre = nombre;
        this.roles = roles;
    }

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
