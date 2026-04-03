package es.um.arso.usuarios.rest.dto;

public class UsuarioAuthDto {

    private String id;
    private String nombreCompleto;
    private String roles;

    public UsuarioAuthDto() {}

    public UsuarioAuthDto(String id, String nombreCompleto, String roles) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
