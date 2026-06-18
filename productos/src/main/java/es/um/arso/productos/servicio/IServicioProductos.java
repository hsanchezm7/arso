package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.util.List;
import java.util.Map;
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
            String vendedorId,
            String urlImagen)
            throws EntidadNoEncontrada;

    void asignarLugarRecogida(String productoId, String descripcion, Double longitud, Double latitud, String vendedorId)
            throws EntidadNoEncontrada;

    void modificar(
            String productoId, Double nuevoPrecio, String nuevaDescripcion, String vendedorId)
            throws EntidadNoEncontrada;

    void eliminar(String productoId, String vendedorId) throws EntidadNoEncontrada;

    void anadirVisualizacion(String productoId) throws EntidadNoEncontrada;

    void marcarNoDisponible(String productoId) throws EntidadNoEncontrada;

    Producto getProducto(String id) throws EntidadNoEncontrada;

    List<ProductoResumen> getHistorialMes(int mes, int anio);

    Page<ProductoResumen> getHistorialMesPaginado(int mes, int anio, Pageable paginacion);

    Page<ProductoResumen> buscarPaginado(
            String categoriaId,
            String texto,
            EstadoProducto estadoMinimo,
            Double precioMinimo,
            Double precioMaximo,
            String idVendedor,
            Pageable paginacion);

    List<EstadoProducto> getEstadosProducto();

    Map<String, Double> getRangoPrecios();
}
