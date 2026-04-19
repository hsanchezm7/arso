package es.um.arso.productos.rest.dto;

import es.um.arso.productos.modelo.LugarRecogida;
import javax.validation.constraints.NotNull;

public class LugarRecogidaDto {

    @NotNull(message = "La descripción del lugar de recogida es obligatoria")
    private String descripcion;

    @NotNull(message = "La longitud es obligatoria")
    private Double longitud;

    @NotNull(message = "La latitud es obligatoria")
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
