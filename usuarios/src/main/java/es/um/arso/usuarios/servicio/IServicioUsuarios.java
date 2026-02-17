package es.um.arso.usuarios.servicio;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.usuarios.modelo.Usuario;
import java.time.LocalDate;

public interface IServicioUsuarios {

    String alta(
            String nombre,
            String apellidos,
            String email,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws RepositorioException;

    void modificar(
            String id,
            String nombre,
            String apellidos,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws RepositorioException, EntidadNoEncontrada;

    Usuario get(String id) throws RepositorioException, EntidadNoEncontrada;
}
