package es.um.arso.productos.rest.dto;

import es.um.arso.productos.modelo.LugarRecogida;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "DTO con la información necesaria para modificar el lugar de recogida de un producto.")
public class LugarRecogidaDto {

    @NotBlank(message = "La descripción del lugar de recogida no debe estar vacía")
    @Schema(description = "Descripción del lugar de recogida.", example = "Amazon Locker Espinardo")
    private String descripcion;

    @NotNull(message = "La longitud es obligatoria")
    @Schema(description = "Coordenada de longitud.", example = "-1.1723137")
    private Double longitud;

    @NotNull(message = "La latitud es obligatoria")
    @Schema(description = "Coordenada de latitud.", example = "38.0219426")
    private Double latitud;

    public LugarRecogidaDto() {}

    public LugarRecogidaDto(String descripcion, Double longitud, Double latitud) {
        this.descripcion = descripcion;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public static LugarRecogidaDto fromEntity(LugarRecogida lugar) {
        if (lugar == null) {
            return null;
        }
        return new LugarRecogidaDto(lugar.getDescripcion(), lugar.getLongitud(), lugar.getLatitud());
    }
}
