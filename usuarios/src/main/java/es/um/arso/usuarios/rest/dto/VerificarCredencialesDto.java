package es.um.arso.usuarios.rest.dto;

public class VerificarCredencialesDto {

    private String username;
    private String password;

    public VerificarCredencialesDto() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
