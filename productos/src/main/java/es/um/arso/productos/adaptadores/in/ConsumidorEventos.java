package es.um.arso.productos.adaptadores.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.arso.productos.config.DataInitializer;
import es.um.arso.productos.config.RabbitMqConfig;
import es.um.arso.productos.modelo.eventos.EventoCompraventaCreada;
import es.um.arso.productos.modelo.eventos.EventoUsuarioCreado;
import es.um.arso.productos.modelo.eventos.EventoUsuarioModificado;
import es.um.arso.productos.puertos.in.IManejadorEventos;
import es.um.arso.productos.puertos.in.ManejadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorEventos {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorEventos.class);

    private final IManejadorEventos manejadorEventos;
    private final ObjectMapper objectMapper;
    private final DataInitializer dataInitializer;

    public ConsumidorEventos(
            @Autowired ManejadorEventos manejadorEventos, ObjectMapper objectMapper, DataInitializer dataInitializer) {
        this.manejadorEventos = manejadorEventos;
        this.objectMapper = objectMapper;
        this.dataInitializer = dataInitializer;
    }

    // TODO: valorar implementar RabbitHandlers para no tener que usar ifs ni switch
    // ni desearilizacion con objectmapper
    // La anotación RabbitListener pasaría a nivel de clase junto a Component.
    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void handleEvento(String contenido) {
        log.info("Mensaje recibido: {}", contenido);

        try {
            JsonNode raiz = objectMapper.readTree(contenido);
            String tipoEvento = raiz.path("tipoEvento").asText("");

            if ("compraventa-creada".equals(tipoEvento)) {
                EventoCompraventaCreada eventoCompraventa =
                        objectMapper.treeToValue(raiz, EventoCompraventaCreada.class);

                manejadorEventos.compraventaCreada(eventoCompraventa.getIdProducto());
            } else if ("usuario-creado".equals(tipoEvento)) {
                EventoUsuarioCreado eventoUsuario = objectMapper.treeToValue(raiz, EventoUsuarioCreado.class);

                manejadorEventos.usuarioCreado(
                        eventoUsuario.getIdUsuario(),
                        eventoUsuario.getEmail(),
                        eventoUsuario.getNombre(),
                        eventoUsuario.getApellidos());

                // si es el usuario admin, inicializar datos
                if ("admin@arso.es".equals(eventoUsuario.getEmail())) {
                    log.info("Usuario admin creado. Inicializando datos de prueba.");
                    dataInitializer.initializeData(eventoUsuario.getIdUsuario());
                }
            } else if ("usuario-modificado".equals(tipoEvento)) {
                EventoUsuarioModificado eventoUsuario = objectMapper.treeToValue(raiz, EventoUsuarioModificado.class);

                manejadorEventos.usuarioModificado(
                        eventoUsuario.getIdUsuario(),
                        eventoUsuario.getEmail(),
                        eventoUsuario.getNombre(),
                        eventoUsuario.getApellidos());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
