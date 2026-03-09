package es.um.arso.compraventa.servicio.puertos;

public interface IServicioUsuariosExterno {

    UsuarioInfo getUsuario(String idUsuario) throws Exception;
}
