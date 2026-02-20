package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioProductosJpa
        extends RepositorioProductos, JpaRepository<Producto, String> {}
