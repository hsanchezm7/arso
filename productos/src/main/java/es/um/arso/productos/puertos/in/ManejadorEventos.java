package es.um.arso.productos.puertos.in;

import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.IServicioUsuarios;
import es.um.arso.repositorio.EntidadNoEncontrada;
import org.springframework.stereotype.Component;

@Component
public class ManejadorEventos implements IManejadorEventos {

    private final IServicioProductos servicio;
    private final IServicioUsuarios servicioUsuarios;

    public ManejadorEventos(IServicioProductos servicio, IServicioUsuarios servicioUsuarios) {
        this.servicio = servicio;
        this.servicioUsuarios = servicioUsuarios;
    }

    @Override
    public void compraventaCreada(String idProducto) throws EntidadNoEncontrada {
        // marcar producto como no disponible
        Producto producto = this.servicio.getProducto(idProducto);
        producto.setDisponible(false);
        this.servicio.modificar(idProducto, null, null, false);
    }

    @Override
    public void usuarioCreado(String idUsuario, String email, String nombre, String apellidos) {
        servicioUsuarios.altaConId(idUsuario, nombre, apellidos, email);
    }
}
