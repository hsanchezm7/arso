package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import java.util.List;

public interface IServicioProductos {

    String crear(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            String categoriaId,
            boolean envioDisponible,
            String vendedorId)
            throws RepositorioException;

    void asignarLugarRecogida(
            String productoId, String descripcion, Double longitud, Double latitud)
            throws RepositorioException, EntidadNoEncontrada;

    void modificar(String productoId, Double nuevoPrecio, String nuevaDescripcion)
            throws RepositorioException, EntidadNoEncontrada;

    void anadirVisualizacion(String productoId) throws RepositorioException, EntidadNoEncontrada;

    Producto getProducto(String id) throws RepositorioException, EntidadNoEncontrada;

    List<ProductoResumen> getHistorialMes(int mes, int anio) throws RepositorioException;

    List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo)
            throws RepositorioException;
}
