package es.um.arso.productos.adaptadores.out;

import es.um.arso.productos.config.RabbitMqConfig;
import es.um.arso.productos.modelo.eventos.Evento;
import es.um.arso.productos.puertos.out.PublicadorEventos;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicadorEventosRabbitMq implements PublicadorEventos {

    private RabbitTemplate rabbitTemplate;

    public PublicadorEventosRabbitMq(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void emitirEvento(Evento evento) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_NAME,
                RabbitMqConfig.ROUTING_KEY_PREFIX + evento.getTipoEvento(),
                evento);
    }
}
