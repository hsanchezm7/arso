package es.um.arso.pasarela.servicio.puertos.out;

public class UsuarioAuthInfo {

    private String id;
    private String nombreCompleto;
    private String roles;

    public UsuarioAuthInfo() {}

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
