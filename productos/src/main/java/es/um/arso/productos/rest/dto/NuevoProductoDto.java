package es.um.arso.productos.rest.dto;

import es.um.arso.productos.modelo.EstadoProducto;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class NuevoProductoDto {

    @NotNull(message = "El título es obligatorio")
    private String titulo;

    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double precio;

    @NotNull(message = "El estado es obligatorio")
    private EstadoProducto estado;

    @NotNull(message = "La categoría es obligatoria")
    private String categoriaId;

    private boolean envioDisponible;

    @NotNull(message = "El vendedor es obligatorio")
    private String vendedorId;

    public NuevoProductoDto() {}

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(String vendedorId) {
        this.vendedorId = vendedorId;
    }
}
