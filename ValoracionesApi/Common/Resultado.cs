namespace ValoracionesApi.Common;

public enum ResultadoTipo
{
    Ok,
    Error,
    NotFound,
    Conflict
}

public record Resultado(bool Success, string? Message, ResultadoTipo Tipo = ResultadoTipo.Ok)
{
    public static Resultado Ok() => new(true, null, ResultadoTipo.Ok);
    public static Resultado Error(string msg) => new(false, msg, ResultadoTipo.Error);
    public static Resultado NotFound(string msg) => new(false, msg, ResultadoTipo.NotFound);
    public static Resultado Conflict(string msg) => new(false, msg, ResultadoTipo.Conflict);
}

public record Resultado<T>(bool Success, string? Message, ResultadoTipo Tipo = ResultadoTipo.Ok, T? Entidad = default)
{
    public static Resultado<T> Ok(T entidad) => new(true, null, ResultadoTipo.Ok, entidad);
    public static Resultado<T> Error(string msg) => new(false, msg, ResultadoTipo.Error, default);
    public static Resultado<T> NotFound(string msg) => new(false, msg, ResultadoTipo.NotFound, default);
    public static Resultado<T> Conflict(string msg) => new(false, msg, ResultadoTipo.Conflict, default);
}
