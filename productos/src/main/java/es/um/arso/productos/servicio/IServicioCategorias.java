package es.um.arso.productos.servicio;

import es.um.arso.productos.rest.dto.CategoriaDto;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.util.List;

public interface IServicioCategorias {

    void cargarJerarquia(String rutaXml);

    void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws EntidadNoEncontrada;

    List<CategoriaDto> getRaices();

    List<CategoriaDto> getDescendientes(String categoriaId) throws EntidadNoEncontrada;

    List<CategoriaDto> getCategorias();
}
