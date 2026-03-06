package es.um.arso.productos.rest.dto;

import javax.validation.constraints.Positive;

public class ModificarProductoDto {

    @Positive(message = "El precio debe ser positivo")
    private Double precio;

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
