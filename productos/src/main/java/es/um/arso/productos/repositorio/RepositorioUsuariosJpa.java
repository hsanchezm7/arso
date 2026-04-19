package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioUsuariosJpa extends RepositorioUsuarios, JpaRepository<Usuario, String> {}
