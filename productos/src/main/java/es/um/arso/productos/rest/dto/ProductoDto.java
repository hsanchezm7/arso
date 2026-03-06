package es.um.arso.productos.rest.dto;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de la entidad Producto")
public class ProductoDto {

    @Schema(description = "Identificador del producto")
    private String id;

    @Schema(description = "Título del producto")
    private String titulo;

    @Schema(description = "Descripción del producto")
    private String descripcion;

    @Schema(description = "Precio del producto")
    private Double precio;

    @Schema(description = "Estado del producto")
    private EstadoProducto estado;

    @Schema(description = "Fecha de publicación")
    private String fechaPublicacion;

    @Schema(description = "Número de visualizaciones")
    private int visualizaciones;

    @Schema(description = "Indica si el envío está disponible")
    private boolean envioDisponible;

    @Schema(description = "Lugar de recogida")
    private LugarRecogidaDto recogida;

    @Schema(description = "ID de la categoría")
    private String categoriaId;

    @Schema(description = "Nombre de la categoría")
    private String nombreCategoria;

    @Schema(description = "ID del vendedor")
    private String vendedorId;

    @Schema(description = "Nombre del vendedor")
    private String nombreVendedor;

    public ProductoDto() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(String fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(int visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public LugarRecogidaDto getRecogida() {
        return recogida;
    }

    public void setRecogida(LugarRecogidaDto recogida) {
        this.recogida = recogida;
    }

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(String vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public static ProductoDto fromEntity(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setTitulo(producto.getTitulo());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setEstado(producto.getEstado());
        dto.setFechaPublicacion(
                producto.getFechaPublicacion() != null
                        ? producto.getFechaPublicacion().toString()
                        : null);
        dto.setVisualizaciones(producto.getVisualizaciones());
        dto.setEnvioDisponible(producto.isEnvioDisponible());

        if (producto.getRecogida() != null) {
            dto.setRecogida(LugarRecogidaDto.fromEntity(producto.getRecogida()));
        }

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setNombreCategoria(producto.getCategoria().getNombre());
        }

        if (producto.getVendedor() != null) {
            dto.setVendedorId(producto.getVendedor().getId());
            dto.setNombreVendedor(producto.getVendedor().getNombre());
        }

        return dto;
    }
}
