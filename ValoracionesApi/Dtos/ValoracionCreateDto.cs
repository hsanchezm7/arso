using System.ComponentModel.DataAnnotations;

using ValoracionesApi.Common;
namespace ValoracionesApi.Dtos;

public class ValoracionCreateDto
{
    [Required(ErrorMessage = "El ID de la compraventa es obligatorio")]
    public string IdCompraventa { get; set; } = string.Empty;

    [Required(ErrorMessage = "El rol del evaluador es obligatorio")]
    [RegularExpression(RolesEvaluador.Patron, ErrorMessage = "RolEvaluador debe ser " + RolesEvaluador.RolComprador + " o " + RolesEvaluador.RolVendedor)]
    public string RolEvaluador { get; set; } = string.Empty;

    [Range(1, 5, ErrorMessage = "El rango de la puntuación debe estar entre 1 y 5")]
    public int Puntuacion { get; set; } = 5;

    [MaxLength(500)]
    public string? Comentario { get; set; }
}
