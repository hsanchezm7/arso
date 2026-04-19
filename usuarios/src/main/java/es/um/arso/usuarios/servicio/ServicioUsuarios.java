package es.um.arso.usuarios.servicio;

import es.um.arso.especificacion.Especificacion;
import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.usuarios.adaptadores.out.PublicadorRabbitMq;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.modelo.eventos.EventoUsuarioCreado;
import es.um.arso.usuarios.puertos.out.PublicadorEventos;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioUsuarios implements IServicioUsuarios {

    private static final Logger log = LoggerFactory.getLogger(ServicioUsuarios.class);

    private Repositorio<Usuario, String> repoUsuarios = FactoriaRepositorios.getRepositorio(Usuario.class);

    private PublicadorEventos publicadorEventos;

    @Override
    public String alta(
            String nombre, String apellidos, String email, String clave, LocalDate fechaNacimiento, String telefono)
            throws RepositorioException {
        if (email == null || email.isEmpty()) throw new IllegalArgumentException("email obligatorio");
        if (clave == null || clave.isEmpty()) throw new IllegalArgumentException("clave obligatoria");
        Usuario u = new Usuario(email, nombre, apellidos);
        u.setClave(clave);
        u.setFechaNacimiento(fechaNacimiento);
        u.setTelefono(telefono);
        String id = repoUsuarios.add(u);

        log.info("Usuario creado: id={} email={}", id, email);

        emitirEventoUsuarioCreado(id, email, nombre, apellidos);

        return id;
    }

    @Override
    public String altaOauth(String nombre, String email, String githubId) throws RepositorioException {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email obligatorio");
        }
        if (githubId == null || githubId.isEmpty()) {
            throw new IllegalArgumentException("githubId obligatorio");
        }

        // TODO: verificar usuario existente si existen github o email
        Usuario existente = null;
        if (existente != null) {
            return existente.getId();
        }

        Usuario u = new Usuario(email, nombre);
        u.setGithubId(githubId);
        String id = repoUsuarios.add(u);

        log.info("Usuario OAuth creado: id={} email={} githubId={}", id, email, githubId);

        emitirEventoUsuarioCreado(id, email, nombre, null);

        return id;
    }

    @Override
    public void modificar(String id, Usuario usuario) throws RepositorioException, EntidadNoEncontrada {
        Usuario u = repoUsuarios.getById(id);

        if (usuario.getNombre() != null && !usuario.getNombre().isEmpty()) u.setNombre(usuario.getNombre());
        if (usuario.getApellidos() != null && !usuario.getApellidos().isEmpty()) u.setApellidos(usuario.getApellidos());
        if (usuario.getClave() != null && !usuario.getClave().isEmpty()) u.setClave(usuario.getClave());
        if (usuario.getFechaNacimiento() != null) u.setFechaNacimiento(usuario.getFechaNacimiento());
        if (usuario.getTelefono() != null) u.setTelefono(usuario.getTelefono());

        u.setAdministrador(usuario.isAdministrador());
        u.setNumeroCompras(usuario.getNumeroCompras());
        u.setNumeroVentas(usuario.getNumeroVentas());

        repoUsuarios.update(u);

        log.info("Usuario modificado: id={}", id);
    }

    @Override
    public Usuario recuperar(String id) throws RepositorioException, EntidadNoEncontrada {
        return repoUsuarios.getById(id);
    }

    @Override
    public Usuario recuperarPorEmail(String email) throws RepositorioException {
        Especificacion<Usuario> spec = new Especificacion<>(usuario -> email.equals(usuario.getEmail()));

        List<Usuario> resultados = repoUsuarios.getByEspecificacion(spec);

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public Usuario recuperarPorGithubId(String githubId) throws RepositorioException {
        Especificacion<Usuario> spec = new Especificacion<>(usuario -> githubId.equals(usuario.getGithubId()));

        List<Usuario> resultados = repoUsuarios.getByEspecificacion(spec);

        return resultados.isEmpty() ? null : resultados.get(0);
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

    private void emitirEventoUsuarioCreado(String idUsuario, String email, String nombre, String apellidos) {
        EventoUsuarioCreado evento = new EventoUsuarioCreado(idUsuario, email, nombre, apellidos);
        try {
            if (publicadorEventos == null) {
                publicadorEventos = new PublicadorRabbitMq();
            }
            publicadorEventos.emitirEvento(evento);
            log.info("Evento usuario-creado enviado: id={}", idUsuario);
        } catch (IOException | RuntimeException e) {
            log.error("Error enviando evento usuario-creado id={}", idUsuario, e);
        }
    }
}
