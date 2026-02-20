package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositorioUsuarios extends CrudRepository<Usuario, String> {}
