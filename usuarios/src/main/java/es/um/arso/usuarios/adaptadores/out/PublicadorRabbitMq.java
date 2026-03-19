package es.um.arso.usuarios.adaptadores.out;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import es.um.arso.usuarios.modelo.eventos.Evento;
import es.um.arso.usuarios.puertos.out.PublicadorEventos;
import java.io.IOException;

public class PublicadorRabbitMq implements PublicadorEventos {

    public static final String RABBITMQ_URI = "amqp://guest:guest@localhost:5672";
    public static final String EXCHANGE_NAME = "arso.bus";
    public static final String QUEUE_NAME = "arso.bus.usuarios.queue";
    public static final String BINDING_KEY = "arso.bus.usuarios.#";
    public static final String ROUTING_KEY = "";

    public PublicadorRabbitMq() {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(RABBITMQ_URI);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            boolean durable = true;
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", durable);

            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void emitirEvento(Evento evento) throws IOException {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(EXCHANGE_NAME);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            Gson gson = new Gson();
            String mensaje = gson.toJson(evento);

            channel.basicPublish(
                    EXCHANGE_NAME,
                    "arso.bus.usuarios." + evento.getTipoEvento(),
                    new AMQP.BasicProperties.Builder().contentType("application/json").build(),
                    mensaje.getBytes());

            channel.close();
            connection.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
