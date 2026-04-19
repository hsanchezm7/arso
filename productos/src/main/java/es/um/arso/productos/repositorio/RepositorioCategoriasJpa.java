package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCategoriasJpa extends RepositorioCategorias, JpaRepository<Categoria, String> {}
