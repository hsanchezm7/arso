package es.um.arso.compraventa.adaptadores.in;

import es.um.arso.compraventa.config.RabbitMqConfig;
import es.um.arso.compraventa.modelo.eventos.Evento;
import es.um.arso.compraventa.puertos.in.ManejadorEventos;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/* TODO: Traer nueva implementación de la clase del microservicio productos */
@Component
public class ConsumidorEventos {

    private final ManejadorEventos manejadorEventos;

    public ConsumidorEventos(@Autowired ManejadorEventos manejadorEventos) {
        this.manejadorEventos = manejadorEventos;
    }

    // TODO: valorar implementar RabbitHandlers para no tener que usar ifs ni switch.
    // La anotación RabbitListener pasaría a nivel de clase junto a Component.
    @RabbitListener(queues = RabbitMqConfig.QUEUE_NAME)
    public void handleEvento(Evento evento) {
        System.out.println("Mensaje recibido: " + evento.toString());

        if (evento.getTipoEvento().equals("compraventa-creada")) {
            manejadorEventos.compraventaCreada(evento.getId());
        }
    }
}
