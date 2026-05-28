using System.Diagnostics.CodeAnalysis;

namespace ValoracionesApi.Events;

public class EventoValoracionCreada : Evento
{
    public const string TIPO_EVENTO = "valoracion-creada";

    public override string TipoEvento { get; init; } = TIPO_EVENTO;

    public required string IdCompraventa { get; init; }
    public required string IdUsuarioEvaluador { get; init; }
    public required string IdUsuarioValorado { get; init; }
    public required string RolUsuarioValorado { get; init; }
    public required int Puntuacion { get; init; }

    [SetsRequiredMembers]
    public EventoValoracionCreada(
        string id,
        string idCompraventa,
        string idUsuarioEvaluador,
        string idUsuarioValorado,
        string rolUsuarioValorado,
        int puntuacion
    ) : base(id, TIPO_EVENTO)
    {
        IdCompraventa = idCompraventa;
        IdUsuarioEvaluador = idUsuarioEvaluador;
        IdUsuarioValorado = idUsuarioValorado;
        RolUsuarioValorado = rolUsuarioValorado;
        Puntuacion = puntuacion;
    }
}
