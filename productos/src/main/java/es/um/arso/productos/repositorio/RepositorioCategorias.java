package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositorioCategorias extends CrudRepository<Categoria, String> {

    @Query("SELECT c FROM Categoria c WHERE c.parent IS NULL")
    List<Categoria> getRaices();
}
