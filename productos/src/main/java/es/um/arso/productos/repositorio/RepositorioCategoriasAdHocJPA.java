package es.um.arso.productos.repositorio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.utils.EntityManagerHelper;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

public class RepositorioCategoriasAdHocJPA extends RepositorioCategoriasJPA
        implements RepositorioCategoriasAdHoc {

    @Override
    public List<Categoria> getRaices() throws RepositorioException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            String qs = "SELECT c FROM Categoria c WHERE c.parent IS NULL";
            TypedQuery<Categoria> q = em.createQuery(qs, Categoria.class);
            q.setHint(QueryHints.REFRESH, HintValues.TRUE);
            return q.getResultList();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error obteniendo categorías raíz", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId) throws RepositorioException {
        try {
            EntityManager em = EntityManagerHelper.getEntityManager();
            String qs = "SELECT c FROM Categoria c";
            TypedQuery<Categoria> q = em.createQuery(qs, Categoria.class);
            q.setHint(QueryHints.REFRESH, HintValues.TRUE);
            List<Categoria> todas = q.getResultList();
            Categoria raiz = null;

            for (Categoria c : todas)
                if (c.getId().equals(categoriaId)) {
                    raiz = c;
                    break;
                }

            if (raiz == null) return new LinkedList<>();

            return raiz.getDescendientes();
        } catch (RuntimeException e) {
            throw new RepositorioException("Error obteniendo descendientes", e);
        } finally {
            EntityManagerHelper.closeEntityManager();
        }
    }
}
