package es.um.arso.compraventa.servicio.puertos.out;

public interface IServicioUsuariosExterno {

    UsuarioInfo getUsuario(String idUsuario) throws Exception;
}
