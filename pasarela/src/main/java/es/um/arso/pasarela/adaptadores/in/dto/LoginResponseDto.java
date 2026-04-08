package es.um.arso.pasarela.adaptadores.in.dto;

public class LoginResponseDto {

    private String identificadorUsuario;
    private String nombreCompleto;
    private String roles;

    public LoginResponseDto() {

    }

    public LoginResponseDto(String identificadorUsuario, String nombreCompleto, String roles) {
        this.identificadorUsuario = identificadorUsuario;
        this.nombreCompleto = nombreCompleto;
        this.roles = roles;
    }

    public String getIdentificadorUsuario() {
        return identificadorUsuario;
    }

    public void setIdentificadorUsuario(String identificadorUsuario) {
        this.identificadorUsuario = identificadorUsuario;
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
