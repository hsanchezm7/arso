package es.um.arso.productos.adaptadores.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.arso.productos.config.RabbitMqConfig;
import es.um.arso.productos.modelo.eventos.EventoCompraventaCreada;
import es.um.arso.productos.modelo.eventos.EventoUsuarioCreado;
import es.um.arso.productos.puertos.in.IManejadorEventos;
import es.um.arso.productos.puertos.in.ManejadorEventos;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumidorEventos {

    private final IManejadorEventos manejadorEventos;
    private final ObjectMapper objectMapper;

    public ConsumidorEventos(
            @Autowired ManejadorEventos manejadorEventos, ObjectMapper objectMapper) {
        this.manejadorEventos = manejadorEventos;
        this.objectMapper = objectMapper;
    }

    // TODO: valorar implementar RabbitHandlers para no tener que usar ifs ni switch
    // ni desearilizacion con objectmapper
    // La anotación RabbitListener pasaría a nivel de clase junto a Component.
    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void handleEvento(Message message) {
        String contenido = new String(message.getBody(), StandardCharsets.UTF_8);
        System.out.println("Mensaje recibido: " + contenido);

        try {
            JsonNode raiz = objectMapper.readTree(contenido);
            String tipoEvento = raiz.path("tipoEvento").asText("");

            if ("compraventa-creada".equals(tipoEvento)) {
                EventoCompraventaCreada eventoCompraventa = objectMapper.treeToValue(raiz,
                        EventoCompraventaCreada.class);

                manejadorEventos.compraventaCreada(eventoCompraventa.getIdProducto());
            } else if ("usuario-creado".equals(tipoEvento)) {
                EventoUsuarioCreado eventoUsuario = objectMapper.treeToValue(raiz, EventoUsuarioCreado.class);

                manejadorEventos.usuarioCreado(
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
