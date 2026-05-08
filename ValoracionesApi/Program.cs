using Microsoft.EntityFrameworkCore;

using ValoracionesApi.Endpoints;
using ValoracionesApi.Models;
using ValoracionesApi.Repositories;
using ValoracionesApi.Services;

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

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<ValoracionesDbContext>();
    db.Database.EnsureCreated();
}

if (app.Environment.IsDevelopment())
{
    app.UseOpenApi();
    app.UseSwaggerUi(config =>
    {
        config.DocumentTitle = "ValoracionesApi";
        config.Path = "/swagger";
        config.DocumentPath = "/swagger/{documentName}/swagger.json";
        config.DocExpansion = "list";
    });
}
app.UseHttpsRedirection();
app.MapValoracionesEndpoints();

app.Run();
