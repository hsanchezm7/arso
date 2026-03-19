package es.um.arso.productos.puertos.in;

import es.um.arso.repositorio.EntidadNoEncontrada;

public interface IManejadorEventos {

    void compraventaCreada(String idProducto) throws EntidadNoEncontrada;
}
