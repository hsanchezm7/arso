package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.RepositorioMemoria;

public class RepositorioProductosMemoria extends RepositorioMemoria<Producto> {

    /* Repositorio con datos de prueba */
    public RepositorioProductosMemoria() {
        Categoria cat = new Categoria("Electrónica");
        cat.setDescripcion("Dispositivos y gadgets");
        cat.setRuta("/electrónica");

        Usuario usr = new Usuario("juan@um.es", "Juan", "Pérez");

        Producto p =
                new Producto(
                        "Teléfono",
                        "Smartphone gama media",
                        150.0,
                        EstadoProducto.BUEN_ESTADO,
                        cat,
                        true,
                        usr);
        this.add(p);
    }
}
