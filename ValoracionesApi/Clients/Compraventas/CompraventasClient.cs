using System.Net;
using System.Text.Json;

namespace ValoracionesApi.Clients.Compraventas;

public class CompraventasClient : ICompraventasClient
{
    private static readonly JsonSerializerOptions JsonOptions = new()
    {
        PropertyNameCaseInsensitive = true
    };

    private readonly HttpClient _httpClient;

    public CompraventasClient(HttpClient httpClient)
    {
        _httpClient = httpClient;
    }

    public async Task<CompraventaInfo?> GetByIdAsync(string idCompraventa)
    {
        using var response = await _httpClient.GetAsync($"compraventas/{idCompraventa}");

        /* TODO: implementar global handler */
        if (response.StatusCode == HttpStatusCode.NotFound)
            return null;

        response.EnsureSuccessStatusCode();

        return await response.Content.ReadFromJsonAsync<CompraventaInfo>(JsonOptions);
    }
}
