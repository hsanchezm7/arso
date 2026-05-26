package es.um.arso.compraventa.servicio.puertos.out;

import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.servicio.exception.ServicioExternoException;

public interface IServicioUsuariosExterno {

    UsuarioInfo getUsuario(String idUsuario) throws EntidadNoEncontrada, ServicioExternoException;
}
