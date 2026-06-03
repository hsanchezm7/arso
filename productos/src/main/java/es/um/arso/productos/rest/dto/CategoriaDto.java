package es.um.arso.productos.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO de la entidad Categoria")
public class CategoriaDto {

    @Schema(description = "Identificador de la categoría")
    private String id;

    @Schema(description = "Nombre de la categoría")
    private String nombre;

    @Schema(description = "Descripción de la categoría")
    private String descripcion;

    public CategoriaDto() {}

    public CategoriaDto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
