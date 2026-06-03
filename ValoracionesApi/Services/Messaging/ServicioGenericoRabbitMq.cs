using System.Text;
using System.Text.Json;

using RabbitMQ.Client;

namespace ValoracionesApi.Services.Messaging;


public class ServicioGenericoRabbitMq<T> : IServicioGenericoRabbitMq<T> where T : class
{
    public const string RABBITMQ_URI = "amqp://arso:arso@rabbitmq:5672";
    public const string EXCHANGE_NAME = "arso.bus";
    public const string ROUTING_KEY_PREFIX = "bus.valoraciones.";
    private readonly ConnectionFactory _factory;

    /* serialización en camelCase para que todos los microservicios usen la mísma política de
    nombrado y así facilitar deserialización y mapeo a atributos */
    private static readonly JsonSerializerOptions OpcionesJson = new JsonSerializerOptions
    {
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };

    public ServicioGenericoRabbitMq()
    {
        _factory = new ConnectionFactory
        {
            Uri = new Uri(RABBITMQ_URI)
        };
    }

    public async Task PublishAsync(T item, string routingKey)
    {
        using var connection = await _factory.CreateConnectionAsync();
        using var channel = await connection.CreateChannelAsync();

        await channel.ExchangeDeclareAsync(
            exchange: EXCHANGE_NAME,
            type: ExchangeType.Topic,
            durable: true);

        var body = Encoding.UTF8.GetBytes(
            JsonSerializer.Serialize(item, item.GetType(), OpcionesJson));

        await channel.BasicPublishAsync(
            exchange: EXCHANGE_NAME,
            routingKey: ROUTING_KEY_PREFIX + routingKey,
            mandatory: false,
            basicProperties: new BasicProperties { Persistent = true },
            body: body
            );

    }
}
