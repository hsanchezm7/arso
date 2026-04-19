package es.um.arso.productos.puertos.in;

import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.IServicioUsuarios;
import es.um.arso.repositorio.EntidadNoEncontrada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ManejadorEventos implements IManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorEventos.class);

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

    @Override
    public void usuarioModificado(String idUsuario, String email, String nombre, String apellidos)
            throws EntidadNoEncontrada {
        try {
            servicioUsuarios.modificar(idUsuario, nombre, apellidos, email);
        } catch (EntidadNoEncontrada e) {
            log.warn("Usuario no encontrado al aplicar usuario-modificado, creando id={}", idUsuario);
            servicioUsuarios.altaConId(idUsuario, nombre, apellidos, email);
        }
    }
}
