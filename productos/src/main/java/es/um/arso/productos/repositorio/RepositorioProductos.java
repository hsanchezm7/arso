package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Producto;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface RepositorioProductos extends CrudRepository<Producto, String> {

    @Query("SELECT p FROM Producto p WHERE p.id IN :ids")
    List<Producto> getByIds(@Param("ids") List<String> ids);

    @Query("SELECT p FROM Producto p WHERE p.fechaPublicacion >= :ini AND p.fechaPublicacion < :fin")
    List<Producto> getByPublicadosEntre(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin);
}
