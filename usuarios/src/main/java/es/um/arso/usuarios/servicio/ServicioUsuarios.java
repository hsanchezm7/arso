package es.um.arso.usuarios.servicio;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.usuarios.modelo.Usuario;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioUsuarios implements IServicioUsuarios {

    private static final Logger log = LoggerFactory.getLogger(ServicioUsuarios.class);

    private Repositorio<Usuario, String> repoUsuarios =
            FactoriaRepositorios.getRepositorio(Usuario.class);

    @Override
    public String alta(
            String nombre,
            String apellidos,
            String email,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws RepositorioException {
        if (email == null || email.isEmpty())
            throw new IllegalArgumentException("email obligatorio");
        if (clave == null || clave.isEmpty())
            throw new IllegalArgumentException("clave obligatoria");
        Usuario u = new Usuario(email, nombre, apellidos);
        u.setClave(clave);
        u.setFechaNacimiento(fechaNacimiento);
        u.setTelefono(telefono);
        String id = repoUsuarios.add(u);

        log.info("Usuario creado: id={} email={}", id, email);

        return id;
    }

    @Override
    public void modificar(
            String id,
            String nombre,
            String apellidos,
            String clave,
            LocalDate fechaNacimiento,
            String telefono)
            throws RepositorioException, EntidadNoEncontrada {
        Usuario u = repoUsuarios.getById(id);
        if (nombre != null && !nombre.isEmpty()) u.setNombre(nombre);
        if (apellidos != null && !apellidos.isEmpty()) u.setApellidos(apellidos);
        if (clave != null && !clave.isEmpty()) u.setClave(clave);
        if (fechaNacimiento != null) u.setFechaNacimiento(fechaNacimiento);
        if (telefono != null) u.setTelefono(telefono);

        repoUsuarios.update(u);

        log.info("Usuario modificado: id={}", id);
    }

    @Override
    public Usuario get(String id) throws RepositorioException, EntidadNoEncontrada {
        return repoUsuarios.getById(id);
    }
}
