package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServicioProductos {

    String crear(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            String categoriaId,
            boolean envioDisponible,
            String vendedorId)
            throws EntidadNoEncontrada;

    void asignarLugarRecogida(
            String productoId, String descripcion, Double longitud, Double latitud)
            throws EntidadNoEncontrada;

    void modificar(
            String productoId, Double nuevoPrecio, String nuevaDescripcion, boolean estaDisponible)
            throws EntidadNoEncontrada;

    void anadirVisualizacion(String productoId) throws EntidadNoEncontrada;

    Producto getProducto(String id) throws EntidadNoEncontrada;

    List<ProductoResumen> getHistorialMes(int mes, int anio);

    List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo);

    Page<ProductoResumen> getHistorialMesPaginado(int mes, int anio, Pageable paginacion);

    Page<ProductoResumen> buscarPaginado(
            String categoriaId,
            String texto,
            EstadoProducto estadoMinimo,
            Double precioMaximo,
            Pageable paginacion);
}
