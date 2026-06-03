using Microsoft.EntityFrameworkCore;

using ValoracionesApi.Clients.Compraventas;
using ValoracionesApi.Endpoints;
using ValoracionesApi.Events;
using ValoracionesApi.Events.Producers;
using ValoracionesApi.Middleware;
using ValoracionesApi.Models;
using ValoracionesApi.Repositories;
using ValoracionesApi.Services;
using ValoracionesApi.Services.Messaging;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddOpenApiDocument(config =>
{
    config.DocumentName = "ValoracionesApi";
    config.Title =
    "ValoracionesApi v1";
    config.Version = "v1";
});

var connectionString = builder.Configuration.GetConnectionString("DefaultConnection");

builder.Services.AddDbContext<ValoracionesDbContext>(options =>
    options.UseMySql(
        connectionString,
        ServerVersion.AutoDetect(connectionString),
        mySqlOptions => mySqlOptions.EnableRetryOnFailure()
    )
);

builder.Services.AddScoped<IRepositorio<Valoracion, int>, RepositorioValoracionesEFCore>();
builder.Services.AddScoped<IServicioValoraciones, ServicioValoraciones>();
builder.Services.AddScoped<IServicioGenericoRabbitMq<Evento>, ServicioGenericoRabbitMq<Evento>>();
builder.Services.AddScoped<EventoProducer>();

var compraventasBaseUrl = builder.Configuration["Compraventas:BaseUrl"];
if (string.IsNullOrWhiteSpace(compraventasBaseUrl))
{
    throw new InvalidOperationException("Compraventas:BaseUrl no debe ser nulo.");
}

builder.Services.AddHttpClient<ICompraventasClient, CompraventasClient>(client =>
    client.BaseAddress = new Uri(compraventasBaseUrl)
);

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<ValoracionesDbContext>();
    db.Database.EnsureCreated();
}

app.UseOpenApi();
app.UseSwaggerUi(config =>
{
    config.DocumentTitle = "ValoracionesApi";
    config.Path = "/swagger";
    config.DocumentPath = "/swagger/{documentName}/swagger.json";
    config.DocExpansion = "list";
});

app.UseMiddleware<ExceptionMiddleware>();
app.MapValoracionesEndpoints();

app.Run();
