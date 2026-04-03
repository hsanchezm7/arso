package es.um.arso.usuarios.rest.dto;

public class UsuarioGithubCreateDto {

    private String nombre;
    private String email;
    private String githubId;

    public UsuarioGithubCreateDto() {}

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

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }
}
