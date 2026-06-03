package es.um.arso.pasarela.adaptadores.in.dto;

import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;

public class LoginResponseDto {

    private String accessToken;
    private UsuarioAuthInfo usuario;

    public LoginResponseDto() {}

    public LoginResponseDto(String accessToken, UsuarioAuthInfo usuario) {
        this.accessToken = accessToken;
        this.usuario = usuario;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public UsuarioAuthInfo getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioAuthInfo usuario) {
        this.usuario = usuario;
    }
}
