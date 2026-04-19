package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.EntidadNoEncontrada;

public interface IServicioUsuarios {
    void altaConId(String id, String nombre, String apellidos, String email);

    void modificar(String id, String nombre, String apellidos, String email) throws EntidadNoEncontrada;

    Usuario get(String id) throws EntidadNoEncontrada;
}
