using ValoracionesApi.Common;
using ValoracionesApi.Models;

namespace ValoracionesApi.Services;

public interface IServicioValoraciones
{
    Task<int> CreateAsync(Valoracion valoracion);
    Task<Resultado> UpdateAsync(Valoracion valoracion);
    Task<Resultado> RemoveAsync(int id);
    Task<Valoracion?> GetAsync(int id);
}
