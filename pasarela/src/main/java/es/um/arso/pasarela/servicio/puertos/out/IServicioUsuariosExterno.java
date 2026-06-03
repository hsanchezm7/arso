package es.um.arso.pasarela.servicio.puertos.out;

public interface IServicioUsuariosExterno {

    UsuarioAuthInfo getUsuario(String idUsuario);

    UsuarioAuthInfo autenticarCredenciales(String username, String password);

    UsuarioAuthInfo buscarUsuario(String githubId, String email);

    UsuarioAuthInfo crearUsuarioOauth(String nombre, String email, String githubId);
}
