package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.RepositorioJPA;

public class RepositorioProductosJPA extends RepositorioJPA<Producto> {

    @Override
    public Class<Producto> getClase() {
        return Producto.class;
    }
}
