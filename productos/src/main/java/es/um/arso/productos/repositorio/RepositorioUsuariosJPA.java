package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.RepositorioJPA;

public class RepositorioUsuariosJPA extends RepositorioJPA<Usuario> {

    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }
}
