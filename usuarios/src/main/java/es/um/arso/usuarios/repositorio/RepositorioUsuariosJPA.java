package es.um.arso.usuarios.repositorio;

import es.um.arso.repositorio.RepositorioJPA;
import es.um.arso.usuarios.modelo.Usuario;

public class RepositorioUsuariosJPA extends RepositorioJPA<Usuario> {

    @Override
    public Class<Usuario> getClase() {
        return Usuario.class;
    }
}
