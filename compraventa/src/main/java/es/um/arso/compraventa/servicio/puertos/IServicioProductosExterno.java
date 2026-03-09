package es.um.arso.compraventa.servicio.puertos;

public interface IServicioProductosExterno {

    ProductoInfo getProducto(String idProducto) throws Exception;
}
