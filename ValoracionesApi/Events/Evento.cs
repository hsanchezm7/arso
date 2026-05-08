namespace ValoracionesApi.Events;

public abstract class Evento
{
    public required string Id { get; init; }
    public DateTime Timestamp { get; init; } = DateTime.UtcNow;
    public abstract string Tipo { get; }
}
