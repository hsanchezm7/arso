package es.um.arso.usuarios.adaptadores.out;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import es.um.arso.usuarios.modelo.eventos.Evento;
import es.um.arso.usuarios.puertos.out.PublicadorEventos;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicadorRabbitMq implements PublicadorEventos {

    private static final Logger log = LoggerFactory.getLogger(PublicadorRabbitMq.class);

    public static final String RABBITMQ_URI =
            System.getenv("RABBITMQ_URI") != null ? System.getenv("RABBITMQ_URI") : "amqp://arso:arso@localhost:5672";

    public static final String EXCHANGE_NAME = "arso.bus";
    public static final String QUEUE_NAME = "arso.usuarios.queue";
    public static final String BINDING_KEY = "bus.usuarios.#";
    public static final String ROUTING_KEY_PREFIX = "bus.usuarios.";

    private final ConnectionFactory factory;
    private final Gson gson;

    public PublicadorRabbitMq() {
        this.factory = new ConnectionFactory();
        this.gson = new Gson();

        try {
            factory.setUri(RABBITMQ_URI);

            try (Connection connection = factory.newConnection();
                    Channel channel = connection.createChannel()) {

                boolean durable = true;
                channel.exchangeDeclare(EXCHANGE_NAME, "topic", durable);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al inicializar RabbitMq", e);
        }
    }

    @Override
    public void emitirEvento(Evento evento) throws IOException {
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel(); ) {
            String mensaje = this.gson.toJson(evento);

            String routingKey = ROUTING_KEY_PREFIX + evento.getTipoEvento();

            channel.basicPublish(
                    EXCHANGE_NAME,
                    routingKey,
                    new AMQP.BasicProperties.Builder()
                            .contentType("application/json")
                            .build(),
                    mensaje.getBytes());

            log.info(
                    "Evento enviado tipo={} routingKey={} exchange={} id={}",
                    evento.getTipoEvento(),
                    routingKey,
                    EXCHANGE_NAME,
                    evento.getId());
        } catch (Exception e) {
            throw new IOException("Error al enviar el evento", e);
        }
    }
}
