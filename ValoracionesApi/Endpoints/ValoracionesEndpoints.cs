using ValoracionesApi.Common;
using ValoracionesApi.Dtos;
using ValoracionesApi.Services;
using ValoracionesApi.Endpoints.Filters;

namespace ValoracionesApi.Endpoints;

public static class ValoracionesEndpoints
{
    public static void MapValoracionesEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/valoraciones");

        group.MapGet("/{id:int}", GetById).WithName("GetValoracion");
        group.MapGet("/vendedor/{vendedorId:int}", GetByVendedor);
        group.MapGet("/comprador/{compradorId:int}", GetByComprador);
        group.MapPost("/", Create).AddEndpointFilter<ValidationFilter<ValoracionCreateDto>>();
    }

    private static async Task<IResult> GetById(int id, IServicioValoraciones servicio)
    {
        var valoracion = await servicio.GetAsync(id);

        return valoracion == null ? Results.NotFound() : Results.Ok(valoracion);
    }


    private static async Task<IResult> Create(ValoracionCreateDto valoracion,
    IServicioValoraciones servicio)
    {
        var res = await servicio.CreateAsync(valoracion);
        if (!res.Success)
        {
            return res.Tipo switch
            {
                ResultadoTipo.Conflict => Results.Conflict(res.Message),
                ResultadoTipo.NotFound => Results.NotFound(res.Message),
                _ => Results.BadRequest(res.Message)
            };
        }

        var creada = res.Entidad;
        if (creada == null)
            return Results.Problem("Error al crear la valoración.");

        return Results.CreatedAtRoute("GetValoracion", new { id = creada.Id }, creada);
    }

    private static async Task<IResult> GetByVendedor(string vendedorId, IServicioValoraciones servicio)
    {
        var valoraciones = await servicio.GetByVendedorAsync(vendedorId);

        return Results.Ok(valoraciones);
    }

    private static async Task<IResult> GetByComprador(string compradorId, IServicioValoraciones servicio)
    {
        var valoraciones = await servicio.GetByCompradorAsync(compradorId);

        return Results.Ok(valoraciones);
    }
}
