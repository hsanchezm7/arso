package es.um.arso.usuarios.servicio;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.usuarios.modelo.Usuario;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
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
    public void modificar(String id, Usuario usuario)
            throws RepositorioException, EntidadNoEncontrada {
        Usuario u = repoUsuarios.getById(id);
        if (usuario.getNombre() != null && !usuario.getNombre().isEmpty())
            u.setNombre(usuario.getNombre());
        if (usuario.getApellidos() != null && !usuario.getApellidos().isEmpty())
            u.setApellidos(usuario.getApellidos());
        if (usuario.getClave() != null && !usuario.getClave().isEmpty())
            u.setClave(usuario.getClave());
        if (usuario.getFechaNacimiento() != null)
            u.setFechaNacimiento(usuario.getFechaNacimiento());
        if (usuario.getTelefono() != null) u.setTelefono(usuario.getTelefono());

        repoUsuarios.update(u);

        log.info("Usuario modificado: id={}", id);
    }

    @Override
    public Usuario recuperar(String id) throws RepositorioException, EntidadNoEncontrada {
        return repoUsuarios.getById(id);
    }

    @Override
    public List<UsuarioResumen> recuperarTodos() throws RepositorioException {
        LinkedList<UsuarioResumen> resultado = new LinkedList<>();

        for (String id : repoUsuarios.getIds()) {
            try {
                Usuario usuario = recuperar(id);
                UsuarioResumen resumen = new UsuarioResumen();
                resumen.setId(usuario.getId());
                resumen.setNombre(usuario.getNombre());
                resumen.setEmail(usuario.getEmail());
                resultado.add(resumen);
            } catch (EntidadNoEncontrada e) {
                log.warn("Usuario no encontrado al recuperar todos: id={}", id);
            }
        }

        return resultado;
    }

    @Override
    public Usuario autenticar(String email, String clave) throws RepositorioException {
        if (email == null || email.isEmpty() || clave == null || clave.isEmpty()) {
            return null;
        }

        for (Usuario usuario : repoUsuarios.getAll()) {
            if (email.equals(usuario.getEmail()) && clave.equals(usuario.getClave())) {
                return usuario;
            }
        }

        return null;
    }
}
