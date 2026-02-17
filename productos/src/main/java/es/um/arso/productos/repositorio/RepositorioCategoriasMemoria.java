package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.RepositorioMemoria;

public class RepositorioCategoriasMemoria extends RepositorioMemoria<Categoria> {

    /* Repositorio con datos de prueba */
    public RepositorioCategoriasMemoria() {
        Categoria root = new Categoria("Electrónica");
        this.add(root);
    }
}
