package es.um.arso.compraventa.puertos.in;

import es.um.arso.compraventa.servicio.IServicioCompraventa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ManejadorEventos {

    private static final Logger log = LoggerFactory.getLogger(ManejadorEventos.class);

    private final IServicioCompraventa servicioCompraventa;

    public ManejadorEventos(IServicioCompraventa servicioCompraventa) {
        this.servicioCompraventa = servicioCompraventa;
    }

    public void compraventaCreada(String id) {}

    public void usuarioModificado(String idUsuario, String nombre, String apellidos) {
        String nombreCompleto = (nombre != null ? nombre : "")
                + (apellidos != null && !apellidos.isEmpty() ? " " + apellidos : "");

        int actualizadas = servicioCompraventa.updateNombreUsuario(idUsuario, nombreCompleto.trim());
        log.info("usuario-modificado: id={} newNombre='{}', compraventasAfectadas={}", idUsuario, nombreCompleto,
                actualizadas);
    }
}
