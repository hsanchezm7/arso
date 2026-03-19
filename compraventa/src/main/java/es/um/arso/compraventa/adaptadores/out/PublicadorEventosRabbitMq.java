package es.um.arso.compraventa.adaptadores.out;

import es.um.arso.compraventa.config.RabbitMqConfig;
import es.um.arso.compraventa.modelo.eventos.Evento;
import es.um.arso.compraventa.puertos.out.PublicadorEventos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublicadorEventosRabbitMq implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(PublicadorEventosRabbitMq.class);

    private RabbitTemplate rabbitTemplate;

    public PublicadorEventosRabbitMq(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void emitirEvento(Evento evento) {
        String routingKey = RabbitMqConfig.ROUTING_KEY_PREFIX + evento.getTipoEvento();

        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, routingKey, evento);

        log.info(
                "Evento {} enviado a exchange {}: id={}",
                routingKey,
                RabbitMqConfig.EXCHANGE_NAME,
                evento.getId());
    }
}
