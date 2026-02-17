package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.repositorio.RepositorioString;
import java.time.LocalDateTime;
import java.util.List;

public interface RepositorioProductosAdHoc extends RepositorioString<Producto> {

    List<Producto> getByPublicadosEntre(LocalDateTime inicio, LocalDateTime fin)
            throws RepositorioException;

    List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo)
            throws RepositorioException;
}
