package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.RepositorioMemoria;

public class RepositorioUsuariosMemoria extends RepositorioMemoria<Usuario> {

    /* Repositorio con datos de prueba */
    public RepositorioUsuariosMemoria() {
        Usuario u = new Usuario("juan@um.es", "Juan", "Pérez");
        this.add(u);
    }
}
