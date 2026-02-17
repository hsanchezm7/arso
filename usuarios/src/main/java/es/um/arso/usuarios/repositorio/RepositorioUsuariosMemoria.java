package es.um.arso.usuarios.repositorio;

import es.um.arso.repositorio.RepositorioMemoria;
import es.um.arso.usuarios.modelo.Usuario;
import java.time.LocalDate;

public class RepositorioUsuariosMemoria extends RepositorioMemoria<Usuario> {

    /* Repositorio con datos de prueba */
    public RepositorioUsuariosMemoria() {
        Usuario u = new Usuario("juan@um.es", "Juan", "Pérez");
        u.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        this.add(u);
    }
}
