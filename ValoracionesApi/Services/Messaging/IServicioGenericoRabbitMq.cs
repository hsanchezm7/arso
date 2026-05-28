namespace ValoracionesApi.Services.Messaging;

public interface IServicioGenericoRabbitMq<T>
{
    Task PublishAsync(T item, string routingKey);
    // Task ConsumeAsync(Func<T, Task> onMessage);
}
