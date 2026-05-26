package es.um.arso.productos.rest.dto;

import es.um.arso.productos.modelo.EstadoProducto;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Schema(description = "DTO con la información necesaria para dar de alta un nuevo producto en el sistema.")
public class NuevoProductoDto {

    @NotNull(message = "El título es obligatorio")
    @Schema(description = "Nombre del producto.", example = "PSP 1004 Japan (Fat)")
    private String titulo;

    @Schema(description = "Descripción breve del producto.", example = "Consola PlayStation Portable modelo 1004 importada de Japón. Incluye cargador original.")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    @Schema(description = "Precio de venta del producto.", example = "55.95")
    private Double precio;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado de conservación actual del producto.", example = "COMO_NUEVO")
    private EstadoProducto estado;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Identificador único de la categoría a la que pertenece el producto.")
    private String categoriaId;

    @Schema(description = "Indica si el vendedor está dispuesto a enviar el producto.", example = "true")
    private boolean envioDisponible;

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
}
