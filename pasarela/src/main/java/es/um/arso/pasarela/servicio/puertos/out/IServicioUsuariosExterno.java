package es.um.arso.pasarela.servicio.puertos.out;

public interface IServicioUsuariosExterno {

    UsuarioInfo getUsuario(String idUsuario);

    UsuarioAuthInfo autenticarCredenciales(String username, String password);

    UsuarioBusquedaInfo buscarUsuario(String githubId, String email);

    UsuarioAuthInfo crearUsuarioOauth(String nombre, String email, String githubId);
}
