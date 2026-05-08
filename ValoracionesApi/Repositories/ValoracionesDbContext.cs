using Microsoft.EntityFrameworkCore;

using ValoracionesApi.Models;

namespace ValoracionesApi.Repositories;

public class ValoracionesDbContext : DbContext
{
    public ValoracionesDbContext(DbContextOptions<ValoracionesDbContext> options) : base(options) { }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Valoracion>(entity =>
        {
            entity.ToTable("valoraciones");
            entity.HasKey(v => v.Id);

            entity.ToTable(t => t.HasCheckConstraint("CK_Valoracion_Puntuacion", "Puntuacion >= 1 AND Puntuacion <= 5"));

            entity.Property(v => v.Comentario)
                .HasMaxLength(500);

        });
    }
}
