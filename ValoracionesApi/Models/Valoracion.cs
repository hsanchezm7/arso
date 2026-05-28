using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ValoracionesApi.Models;

public class Valoracion
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public int Id { get; set; }
    public string IdCompraventa { get; set; } = string.Empty;
    public string IdUsuarioEvaluador { get; set; } = string.Empty;
    public string IdUsuarioValorado { get; set; } = string.Empty;
    public string RolUsuarioValorado { get; set; } = string.Empty;

    [Range(1, 5, ErrorMessage = "El rango de la puntuación debe estar entre 1 y 5")]
    public int Puntuacion { get; set; } = 5;

    [MaxLength(500)]
    public string? Comentario { get; set; }
}
