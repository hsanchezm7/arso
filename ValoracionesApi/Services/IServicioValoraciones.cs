using ValoracionesApi.Common;
using ValoracionesApi.Dtos;
using ValoracionesApi.Models;

namespace ValoracionesApi.Services;

public interface IServicioValoraciones
{
    Task<Resultado<Valoracion>> CreateAsync(ValoracionCreateDto valoracion);
    Task<Resultado> UpdateAsync(Valoracion valoracion);
    Task<Resultado> RemoveAsync(int id);
    Task<Valoracion?> GetAsync(int id);
    Task<List<Valoracion>> GetByVendedorAsync(string idVendedor);
    Task<List<Valoracion>> GetByCompradorAsync(string idComprador);
}
