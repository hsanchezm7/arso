package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.utils.EntityManagerHelper;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

public class RepositorioProductosAdHocJPA extends RepositorioProductosJPA
        implements RepositorioProductosAdHoc {

    @Override
    public List<Producto> getByPublicadosEntre(LocalDateTime inicio, LocalDateTime fin)
            throws RepositorioException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            String qs =
                    "SELECT p FROM Producto p WHERE p.fechaPublicacion >= :ini AND p.fechaPublicacion < :fin";
            TypedQuery<Producto> q = em.createQuery(qs, Producto.class);
            q.setParameter("ini", inicio);
            q.setParameter("fin", fin);
            q.setHint(QueryHints.REFRESH, HintValues.TRUE);
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error consultando productos por rango de fechas", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo)
            throws RepositorioException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            StringBuilder sb = new StringBuilder("SELECT p FROM Producto p WHERE 1=1");
            if (categoriaId != null) sb.append(" AND p.categoria.id = :cat");
            if (texto != null && !texto.isEmpty()) sb.append(" AND LOWER(p.descripcion) LIKE :txt");
            if (estadoMinimo != null) sb.append(" AND p.estado <= :estadoMin");
            if (precioMaximo != null) sb.append(" AND p.precio <= :precioMax");
            TypedQuery<Producto> q = em.createQuery(sb.toString(), Producto.class);
            if (categoriaId != null) q.setParameter("cat", categoriaId);
            if (texto != null && !texto.isEmpty())
                q.setParameter("txt", "%" + texto.toLowerCase() + "%");
            if (estadoMinimo != null) q.setParameter("estadoMin", estadoMinimo);
            if (precioMaximo != null) q.setParameter("precioMax", precioMaximo);
            q.setHint(QueryHints.REFRESH, HintValues.TRUE);
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error en búsqueda ad hoc de productos", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public String add(Producto entity) throws RepositorioException {
        if (entity.getFechaPublicacion() == null) entity.setFechaPublicacion(LocalDateTime.now());
        return super.add(entity);
    }

    // usa implementación heredada
}
