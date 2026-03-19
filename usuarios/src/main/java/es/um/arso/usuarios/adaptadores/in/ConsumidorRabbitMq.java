package es.um.arso.usuarios.adaptadores.in;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.eventos.EventoCompraventaCreada;
import es.um.arso.usuarios.puertos.in.IManejadorEventos;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConsumidorRabbitMq implements ServletContextListener {

    public static final String RABBITMQ_URI = "amqp://guest:guest@localhost:5672";
    public static final String EXCHANGE_NAME = "arso.bus";
    public static final String QUEUE_NAME = "arso.bus.usuarios.queue";
    public static final String BINDING_KEY = "arso.compraventa.#";

    // inyección del puerto de entrada
    private final IManejadorEventos manejadorEventos =
            FactoriaServicios.getServicio(IManejadorEventos.class);

    private Connection connection;
    private Channel channel;

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(RABBITMQ_URI);

            connection = factory.newConnection();
            channel = connection.createChannel();

            boolean durable = true;
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", durable);

            durable = true;
            boolean exclusive = false;
            boolean autodelete = false;
            Map<String, Object> properties = null; // sin propiedades
            channel.queueDeclare(QUEUE_NAME, durable, exclusive, autodelete, properties);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);

            boolean autoAck = false;
            channel.basicConsume(
                    QUEUE_NAME,
                    autoAck,
                    "usuarios-consumer",
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(
                                String consumerTag,
                                Envelope envelope,
                                AMQP.BasicProperties properties,
                                byte[] body)
                                throws IOException {

                            long deliveryTag = envelope.getDeliveryTag();

                            String contenido = new String(body);

                            JsonObject objeto = JsonParser.parseString(contenido).getAsJsonObject();

                            String tipo = objeto.get("tipoEvento").getAsString();

                            switch (tipo) {
                                case "compraventa-creada":
                                    Gson gson = new Gson();
                                    EventoCompraventaCreada evento =
                                            gson.fromJson(objeto, EventoCompraventaCreada.class);
                                    try {
                                        manejadorEventos.compraventaCreada(
                                                evento.getIdVendedor(), evento.getIdComprador());
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                    break;
                                default:
                                    System.out.println(
                                            "No debería entrar al default del switch tipo");
                                    return;
                            }

                            channel.basicAck(deliveryTag, false);
                        }
                    });

            System.out.println("consumidor esperando ...");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        try {
            if (this.channel != null) this.channel.close();

            if (this.connection != null) this.connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
