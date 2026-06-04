package es.um.arso.compraventa.adaptadores.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.arso.compraventa.config.RabbitMqConfig;
import es.um.arso.compraventa.modelo.eventos.EventoUsuarioModificado;
import es.um.arso.compraventa.puertos.in.ManejadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorEventos {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorEventos.class);

    private final ManejadorEventos manejadorEventos;
    private final ObjectMapper objectMapper;

    public ConsumidorEventos(@Autowired ManejadorEventos manejadorEventos, ObjectMapper objectMapper) {
        this.manejadorEventos = manejadorEventos;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME, containerFactory = "rabbitListenerContainerFactory")
    public void handleEvento(String contenido) {
        log.info("Mensaje recibido: {}", contenido);

        try {
            JsonNode raiz = objectMapper.readTree(contenido);
            String tipoEvento = raiz.path("tipoEvento").asText("");

            if ("usuario-modificado".equals(tipoEvento)) {
                EventoUsuarioModificado evento = objectMapper.treeToValue(raiz, EventoUsuarioModificado.class);

                manejadorEventos.usuarioModificado(
                        evento.getIdUsuario(),
                        evento.getNombre(),
                        evento.getApellidos());
            } else {
                log.info("Evento ignorado: {}", tipoEvento);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
