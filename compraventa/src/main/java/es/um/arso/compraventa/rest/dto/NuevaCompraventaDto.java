package es.um.arso.compraventa.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

@Schema(description = "DTO con la información necesaria para registrar una nueva compraventa.")
public class NuevaCompraventaDto {

    @NotNull(message = "El ID del producto es obligatorio")
    @Schema(description = "Identificador único del producto vendido.")
    private String idProducto;

    public NuevaCompraventaDto() {}

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
}
