package es.um.arso.productos.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que representa el rango de precios mínimo y máximo de los productos.")
public class RangoPreciosDto {
    
    @Schema(description = "Precio del producto más barato del sistema", example = "2.5")
    private Double min;
    
    @Schema(description = "Precio del producto más caro del sistema", example = "8200.0")
    private Double max;

    public RangoPreciosDto() {}

    public RangoPreciosDto(Double min, Double max) {
        this.min = min;
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
