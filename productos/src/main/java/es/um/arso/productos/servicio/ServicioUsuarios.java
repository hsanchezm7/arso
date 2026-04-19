package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Usuario;
import es.um.arso.productos.repositorio.RepositorioUsuarios;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioUsuarios implements IServicioUsuarios {

    private static final Logger log = LoggerFactory.getLogger(ServicioUsuarios.class);

    @Autowired
    private RepositorioUsuarios repositorioUsuarios;

    @Override
    public void altaConId(String id, String nombre, String apellidos, String email) {
        Usuario usuario = new Usuario(email, nombre, apellidos);
        usuario.setId(id);
        repositorioUsuarios.save(usuario);
        log.info("Usuario creado: id={}", id);
    }

    @Override
    public void modificar(
            String id, String nombre, String apellidos, String clave, LocalDate fechaNacimiento, String telefono)
            throws EntidadNoEncontrada {

        Usuario usuario =
                repositorioUsuarios.findById(id).orElseThrow(() -> new EntidadNoEncontrada(id + " no existe"));
        if (nombre != null) usuario.setNombre(nombre);
        if (apellidos != null) usuario.setApellidos(apellidos);
        repositorioUsuarios.save(usuario);
        log.info("Usuario modificado: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario get(String id) throws EntidadNoEncontrada {
        return repositorioUsuarios.findById(id).orElseThrow(() -> new EntidadNoEncontrada(id + " no existe"));
    }
}
