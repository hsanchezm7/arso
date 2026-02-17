package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import java.util.List;

public interface IServicioCategorias {

    void cargarJerarquia(String rutaXml) throws RepositorioException;

    void modificarDescripcion(String categoriaId, String nuevaDescripcion)
            throws RepositorioException, EntidadNoEncontrada;

    List<Categoria> getRaices() throws RepositorioException;

    List<Categoria> getDescendientes(String categoriaId)
            throws RepositorioException, EntidadNoEncontrada;
}
