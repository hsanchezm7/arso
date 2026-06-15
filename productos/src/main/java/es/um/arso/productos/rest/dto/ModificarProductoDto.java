package es.um.arso.productos.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Positive;

@Schema(description = "DTO con la información necesaria para modificar un producto existente.")
public class ModificarProductoDto {

    @Positive(message = "El precio debe ser positivo")
    @Schema(description = "Nuevo precio de venta del producto.", example = "45.95")
    private Double precio;

    @Schema(
            description = "Nueva descripción del producto.",
            example = "Rebajo el precio por venta urgente. Consola PlayStation Portable modelo 1004.")
    private String descripcion;

    public ModificarProductoDto() {}

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
