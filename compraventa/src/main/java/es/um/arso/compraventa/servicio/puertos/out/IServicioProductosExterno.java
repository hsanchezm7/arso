package es.um.arso.compraventa.servicio.puertos.out;

public interface IServicioProductosExterno {

    ProductoInfo getProducto(String idProducto) throws Exception;
}
