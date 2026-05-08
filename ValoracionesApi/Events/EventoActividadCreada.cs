namespace ValoracionesApi.Events;

public abstract class EventoActtividadCreada : Evento
{
    public required string Titulo { get; set; }
    public override string Tipo => "actividad-creada";
}
