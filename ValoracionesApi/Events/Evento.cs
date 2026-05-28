using System.Diagnostics.CodeAnalysis;

namespace ValoracionesApi.Events;

public abstract class Evento
{
    public required string Id { get; init; }
    public abstract string TipoEvento { get; init; }
    public DateTime Timestamp { get; init; }


    [SetsRequiredMembers]
    public Evento(string id, string tipoEvento)
    {
        Id = id;
        TipoEvento = tipoEvento;
        Timestamp = DateTime.Now;
    }
}
