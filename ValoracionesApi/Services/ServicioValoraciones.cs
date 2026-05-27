using ValoracionesApi.Common;
using ValoracionesApi.Clients.Compraventas;
using ValoracionesApi.Models;
using ValoracionesApi.Repositories;
using ValoracionesApi.Dtos;

namespace ValoracionesApi.Services;

public class ServicioValoraciones : IServicioValoraciones
{
    private readonly IRepositorio<Valoracion, int> _repositorio;
    private readonly ICompraventasClient _compraventasClient;

    /* TODO: verificar parámetros en los métodos */
    public ServicioValoraciones(
        IRepositorio<Valoracion, int> repositorio,
        ICompraventasClient compraventasClient)
    {
        _repositorio = repositorio;
        _compraventasClient = compraventasClient;
    }

    public async Task<Resultado<Valoracion>> CreateAsync(ValoracionCreateDto valoracionCreate)
    {
        var compraventa = await _compraventasClient.GetByIdAsync(valoracionCreate.IdCompraventa);
        if (compraventa == null)
            return Resultado<Valoracion>.NotFound("Compraventa no existe.");

        var (idEvaluador, idValorado, rolValorado) = ResolveEvaluacion(valoracionCreate.RolEvaluador, compraventa);

        // comprobar duplicidad
        var existe = await _repositorio.ExistsByCompraventaAndEvaluadorAsync(
            valoracionCreate.IdCompraventa,
            idEvaluador);

        if (existe)
            return Resultado<Valoracion>.Conflict("Ya existe una valoracion para esta compraventa y usuario evaluador.");


        var valoracion = new Valoracion
        {
            IdCompraventa = valoracionCreate.IdCompraventa,
            IdUsuarioEvaluador = idEvaluador,
            IdUsuarioValorado = idValorado,
            RolUsuarioValorado = rolValorado,
            Puntuacion = valoracionCreate.Puntuacion,
            Comentario = valoracionCreate.Comentario
        };
        
        await _repositorio.AddAsync(valoracion);

        return Resultado<Valoracion>.Ok(valoracion);
    }

    public async Task<Valoracion?> GetAsync(int id)
    {
        return await _repositorio.GetByIdAsync(id);
    }

    public async Task<List<Valoracion>> GetByVendedorAsync(string idVendedor)
    {
        return await _repositorio.GetByUsuarioValoradoYRolAsync(idVendedor, RolesEvaluador.RolVendedor);
    }

    public async Task<List<Valoracion>> GetByCompradorAsync(string idComprador)
    {
        return await _repositorio.GetByUsuarioValoradoYRolAsync(idComprador, RolesEvaluador.RolComprador);
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

    private static (string idEvaluador, string idValorado, string rolValorado)  // tupla
    ResolveEvaluacion(
        string rolEvaluador,
        CompraventaInfo compraventa)
    {
        if (rolEvaluador.Equals(RolesEvaluador.RolComprador, StringComparison.OrdinalIgnoreCase))
        {
            return (
                compraventa.IdComprador,
                compraventa.IdVendedor,
                RolesEvaluador.RolVendedor
            );
        }

        return (
            compraventa.IdVendedor,
            compraventa.IdComprador,
            RolesEvaluador.RolComprador
        );
    }
}
