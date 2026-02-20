package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.EntidadNoEncontrada;
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
            throws EntidadNoEncontrada;

    void asignarLugarRecogida(
            String productoId, String descripcion, Double longitud, Double latitud)
            throws EntidadNoEncontrada;

    void modificar(String productoId, Double nuevoPrecio, String nuevaDescripcion)
            throws EntidadNoEncontrada;

    void anadirVisualizacion(String productoId) throws EntidadNoEncontrada;

    Producto getProducto(String id) throws EntidadNoEncontrada;

    List<ProductoResumen> getHistorialMes(int mes, int anio);

    List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo);
}
