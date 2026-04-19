package es.um.arso.usuarios.puertos.in;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;

public interface IManejadorEventos {

    // TODO: completar con args correctos
    void compraventaCreada(String idVendedor, String idComprador) throws RepositorioException, EntidadNoEncontrada;
}
