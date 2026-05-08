using ValoracionesApi.Models;
using ValoracionesApi.Services;

namespace ValoracionesApi.Endpoints;

public static class ValoracionesEndpoints
{
    public static void MapValoracionesEndpoints(this WebApplication app)
    {
        var group = app.MapGroup("/api/valoraciones");

        group.MapGet("/{id}", GetById).WithName("GetValoracion");
        group.MapPost("/", Create);
    }

    private static async Task<IResult> GetById(int id, IServicioValoraciones servicio)
    {
        var valoracion = await servicio.GetAsync(id);

        return valoracion == null ? Results.NotFound() : Results.Ok(valoracion);
    }


    private static async Task<IResult> Create(Valoracion valoracion,
    IServicioValoraciones servicio)
    {
        var id = await servicio.CreateAsync(valoracion);

        return Results.CreatedAtRoute("GetValoracion", new { id }, valoracion);
    }
}
