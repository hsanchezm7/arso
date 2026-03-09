package es.um.arso.compraventa.rest.dto;

import javax.validation.constraints.NotNull;

public class NuevaCompraventaDto {

    @NotNull(message = "El ID del producto es obligatorio")
    private String idProducto;

    @NotNull(message = "El ID del comprador es obligatorio")
    private String idComprador;

    public NuevaCompraventaDto() {}

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }
}
