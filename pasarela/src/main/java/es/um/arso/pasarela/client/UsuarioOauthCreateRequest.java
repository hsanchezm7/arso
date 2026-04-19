package es.um.arso.pasarela.client;

public class UsuarioOauthCreateRequest {

    private String nombre;
    private String email;
    private String githubId;

    public UsuarioOauthCreateRequest() {}

    public UsuarioOauthCreateRequest(String nombre, String email, String githubId) {
        this.nombre = nombre;
        this.email = email;
        this.githubId = githubId;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
