package es.um.arso.productos.repositorio.especificaciones;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;

public final class EspecificacionesProducto {

    public static Specification<Producto> conPrecioEntre(Double precioMin, Double precioMax) {
        return (root, query, cb) -> {
            if (precioMin != null && precioMax != null)
                return cb.between(root.get("precio"), precioMin, precioMax);
            
            if (precioMin != null)
                return cb.greaterThanOrEqualTo(root.get("precio"), precioMin);

            return cb.lessThanOrEqualTo(root.get("precio"), precioMax);
        };
    }

    public static Specification<Producto> conEstadoMinimo(EstadoProducto estadoMinimo) {
        return (root, query, cb) -> {
            List<EstadoProducto> estadosValidos = Arrays.stream(EstadoProducto.values())
            .filter(e -> e.esIgualOMejorQue(estadoMinimo))
            .collect(Collectors.toList());

            return root.get("estado").in(estadosValidos);
        };
    }

    public static Specification<Producto> delVendedor(String vendedorId) {
        return (root, query, cb) -> {
            return cb.equal(root.get("vendedor").get("id"), vendedorId);
        };
    }

    public static Specification<Producto> conTexto(String texto) {
        return (root, query, cb) -> {
            String match = "%" + texto + "%";
            Predicate predMatchTitulo = cb.like(cb.lower(root.get("titulo")), (match).toLowerCase());
            Predicate predMatchDesc = cb.like(cb.lower(root.get("descripcion")), (match).toLowerCase());

            return cb.or(predMatchTitulo, predMatchDesc);
        };
    }

    public static Specification<Producto> conCategoria(String categoriaId) {
        return (root, query, cb) -> {
            return cb.equal(root.get("categoria").get("id"), categoriaId);
        };
    }

    public static Specification<Producto> conCategorias(List<String> categoriaIds) {
        return (root, query, cb) -> root.get("categoria").get("id").in(categoriaIds);
    }

    public static Specification<Producto> conDisponibilidad(Boolean disponible) {
        return (root, query, cb) -> cb.equal(root.get("disponible"), disponible);
    }

    public static Specification<Producto> fetchCategoria() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("categoria", JoinType.LEFT);
            }
            return null;
        };
    }

    public static Specification<Producto> crearEspecificacionBusqueda(
            List<String> categoriasPermitidas,
            String texto,
            EstadoProducto estadoMinimo,
            Double precioMinimo,
            Double precioMaximo,
            String idVendedor,
            Boolean disponible) {
        Specification<Producto> spec = Specification.where(fetchCategoria());

        if (categoriasPermitidas != null && !categoriasPermitidas.isEmpty()) {
            spec = spec.and(conCategorias(categoriasPermitidas));
        }
        if (texto != null && !texto.isEmpty()) {
            spec = spec.and(conTexto(texto));
        }
        if (estadoMinimo != null) {
            spec = spec.and(conEstadoMinimo(estadoMinimo));
        }
        if (precioMinimo != null || precioMaximo != null) {
            spec = spec.and(conPrecioEntre(precioMinimo, precioMaximo));
        }
        if (idVendedor != null && !idVendedor.isEmpty()) {
            spec = spec.and(delVendedor(idVendedor));
        }
        if (disponible != null) {
            spec = spec.and(conDisponibilidad(disponible));
        }

        return spec;
    }


}
