using ValoracionesApi.Services.Messaging;

namespace ValoracionesApi.Events.Producers;


public class EventoProducer
{
    private readonly IServicioGenericoRabbitMq<Evento> _servicioRabbitMq;

    public EventoProducer(IServicioGenericoRabbitMq<Evento> servicioRabbitMq)
    {
        _servicioRabbitMq = servicioRabbitMq;
    }

    public async Task ProduceAsync(Evento evento)
    {
        await _servicioRabbitMq.PublishAsync(evento, evento.TipoEvento);
    }
}
