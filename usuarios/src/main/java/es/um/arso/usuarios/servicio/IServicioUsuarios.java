package es.um.arso.usuarios.servicio;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.usuarios.modelo.Usuario;
import java.time.LocalDate;
import java.util.List;

public interface IServicioUsuarios {

    String alta(
            String nombre,
            String apellidos,
            String email,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws RepositorioException;

    void modificar(String id, Usuario usuario) throws RepositorioException, EntidadNoEncontrada;

    Usuario recuperar(String id) throws RepositorioException, EntidadNoEncontrada;

    List<UsuarioResumen> recuperarTodos() throws RepositorioException;

    Usuario autenticar(String email, String clave) throws RepositorioException;
}
