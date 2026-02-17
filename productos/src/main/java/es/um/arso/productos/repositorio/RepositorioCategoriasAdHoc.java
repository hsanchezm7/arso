package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.repositorio.RepositorioString;
import java.util.List;

public interface RepositorioCategoriasAdHoc extends RepositorioString<Categoria> {

    List<Categoria> getRaices() throws RepositorioException;

    List<Categoria> getDescendientes(String categoriaId) throws RepositorioException;
}
