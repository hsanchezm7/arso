package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.time.LocalDate;

public interface IServicioUsuarios {

    String alta(
            String nombre,
            String apellidos,
            String email,
            String clave,
            LocalDate fechaNacimiento,
            String telefono);

    void modificar(
            String id,
            String nombre,
            String apellidos,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws EntidadNoEncontrada;

    Usuario get(String id) throws EntidadNoEncontrada;
}
