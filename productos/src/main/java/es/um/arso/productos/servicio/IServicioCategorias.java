package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.util.List;

public interface IServicioCategorias {

    void cargarJerarquia(String rutaXml);

    void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws EntidadNoEncontrada;

    List<Categoria> getRaices();

    List<Categoria> getDescendientes(String categoriaId) throws EntidadNoEncontrada;
}
