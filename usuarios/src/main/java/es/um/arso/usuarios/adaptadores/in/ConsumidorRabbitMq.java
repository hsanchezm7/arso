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
import es.um.arso.usuarios.modelo.eventos.EventoValoracionCreada;
import es.um.arso.usuarios.puertos.in.IManejadorEventos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class ConsumidorRabbitMq implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(ConsumidorRabbitMq.class);

    public static final String RABBITMQ_URI = "amqp://arso:arso@rabbitmq:5672";
    public static final String EXCHANGE_NAME = "arso.bus";
    public static final String QUEUE_NAME = "arso.usuarios.queue";
    public static final String BINDING_KEY = "arso.compraventa.#";
    public static final String BINDING_KEY_VALORACIONES = "arso.valoraciones.#";

    private static final String EVENTO_COMPRAVENTA_CREADA = EventoCompraventaCreada.TIPO_EVENTO;
    private static final String EVENTO_VALORACION_CREADA = EventoValoracionCreada.TIPO_EVENTO;

    private final Gson gson = new Gson();

    private final IManejadorEventos manejadorEventos = FactoriaServicios.getServicio(IManejadorEventos.class);

    private Connection connection;
    private Channel channel;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.info(
                    "Inicializando consumidor RabbitMQ queue={} exchange={} binding={}",
                    QUEUE_NAME,
                    EXCHANGE_NAME,
                    BINDING_KEY);
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
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY_VALORACIONES);

            boolean autoAck = false;
            channel.basicConsume(QUEUE_NAME, autoAck, "usuarios-consumer", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(
                        String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {

                    long deliveryTag = envelope.getDeliveryTag();
                    String contenido = new String(body, StandardCharsets.UTF_8);

                    log.info(
                            "Mensaje recibido routingKey={} exchange={} redelivered={} deliveryTag={} payload={}",
                            envelope.getRoutingKey(),
                            envelope.getExchange(),
                            envelope.isRedeliver(),
                            deliveryTag,
                            contenido);

                    try {
                        JsonObject objeto = JsonParser.parseString(contenido).getAsJsonObject();
                        String tipoEvento = objeto.get("tipoEvento").getAsString();

                        if (EVENTO_COMPRAVENTA_CREADA.equals(tipoEvento)) {
                            EventoCompraventaCreada evento = gson.fromJson(objeto, EventoCompraventaCreada.class);

                            manejadorEventos.compraventaCreada(evento.getIdVendedor(), evento.getIdComprador());

                            log.info(
                                    "Evento procesado tipo={} vendedor={} comprador={}",
                                    tipoEvento,
                                    evento.getIdVendedor(),
                                    evento.getIdComprador());
                        } else if (EVENTO_VALORACION_CREADA.equals(tipoEvento)) {
                            EventoValoracionCreada evento = gson.fromJson(objeto, EventoValoracionCreada.class);

                            manejadorEventos.valoracionCreada(
                                    evento.getIdUsuarioValorado(),
                                    evento.getRolUsuarioValorado(),
                                    evento.getPuntuacion());

                            log.info(
                                    "Evento procesado tipo={} valorador={} valorado={} rol={} puntuacion={}",
                                    tipoEvento,
                                    evento.getIdUsuarioEvaluador(),
                                    evento.getIdUsuarioValorado(),
                                    evento.getRolUsuarioValorado(),
                                    evento.getPuntuacion());
                        } else {
                            log.info("Evento ignorado tipo={} payload={}", tipoEvento, contenido);
                        }

                        channel.basicAck(deliveryTag, false);
                    } catch (Exception e) {
                        log.error("Error procesando mensaje deliveryTag={} payload={}", deliveryTag, contenido, e);
                        channel.basicNack(deliveryTag, false, false);
                    }
                }
            });

            log.info("Consumidor RabbitMQ esperando mensajes...");
        } catch (Exception e) {
            log.error("Error inicializando consumidor RabbitMQ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        try {
            log.info("Cerrando consumidor RabbitMQ");
            if (this.channel != null) this.channel.close();
            if (this.connection != null) this.connection.close();
        } catch (Exception e) {
            log.error("Error cerrando consumidor RabbitMQ", e);
            throw new RuntimeException(e);
        }
    }
}
