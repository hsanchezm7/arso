package es.um.arso.compraventa.rest.dto;

import es.um.arso.compraventa.modelo.Compraventa;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de la entidad Compraventa")
public class CompraventaDto {

    @Schema(description = "Identificador de la compraventa")
    private String id;

    @Schema(description = "ID del producto")
    private String idProducto;

    @Schema(description = "Título del producto")
    private String titulo;

    @Schema(description = "Precio de la compraventa")
    private double precio;

    @Schema(description = "Información del lugar de recogida")
    private String recogida;

    @Schema(description = "ID del vendedor")
    private String idVendedor;

    @Schema(description = "Nombre del vendedor")
    private String nombreVendedor;

    @Schema(description = "ID del comprador")
    private String idComprador;

    @Schema(description = "Nombre del comprador")
    private String nombreComprador;

    @Schema(description = "Fecha y hora de la compraventa")
    private String fecha;

    public CompraventaDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getRecogida() {
        return recogida;
    }

    public void setRecogida(String recogida) {
        this.recogida = recogida;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public static CompraventaDto fromEntity(Compraventa compraventa) {
        CompraventaDto dto = new CompraventaDto();
        dto.setId(compraventa.getId());
        dto.setIdProducto(compraventa.getIdProducto());
        dto.setTitulo(compraventa.getTitulo());
        dto.setPrecio(compraventa.getPrecio());
        dto.setRecogida(compraventa.getRecogida());
        dto.setIdVendedor(compraventa.getIdVendedor());
        dto.setNombreVendedor(compraventa.getNombreVendedor());
        dto.setIdComprador(compraventa.getIdComprador());
        dto.setNombreComprador(compraventa.getNombreComprador());
        dto.setFecha(compraventa.getFecha() != null ? compraventa.getFecha().toString() : null);
        return dto;
    }
}
