using ValoracionesApi.Common;
using ValoracionesApi.Models;
using ValoracionesApi.Repositories;

namespace ValoracionesApi.Services;

public class ServicioValoraciones : IServicioValoraciones
{
    private readonly IRepositorio<Valoracion, int> _repositorio;

    public ServicioValoraciones(IRepositorio<Valoracion, int> repositorio)
    {
        _repositorio = repositorio;
    }
    public async Task<int> CreateAsync(Valoracion valoracion)
    {
        return await _repositorio.AddAsync(valoracion);
    }

    public async Task<Valoracion?> GetAsync(int id)
    {
        return await _repositorio.GetByIdAsync(id);
    }

    public async Task<Resultado> RemoveAsync(int id)
    {
        var valoracion = await _repositorio.GetByIdAsync(id);
        if (valoracion == null)
        {
            return Resultado.NotFound("Valoracion no encontrada");
        }
        await _repositorio.DeleteAsync(valoracion);
        return Resultado.Ok();
    }

    public async Task<Resultado> UpdateAsync(Valoracion valoracion)
    {
        await _repositorio.UpdateAsync(valoracion);
        return Resultado.Ok();
    }
}
