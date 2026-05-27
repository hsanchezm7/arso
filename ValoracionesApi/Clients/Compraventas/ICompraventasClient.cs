namespace ValoracionesApi.Clients.Compraventas;

public interface ICompraventasClient
{
    Task<CompraventaInfo?> GetByIdAsync(string idCompraventa);
}
