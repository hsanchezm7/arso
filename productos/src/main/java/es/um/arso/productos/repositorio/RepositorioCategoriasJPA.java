package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.RepositorioJPA;

public class RepositorioCategoriasJPA extends RepositorioJPA<Categoria> {

    @Override
    public Class<Categoria> getClase() {
        return Categoria.class;
    }
}
