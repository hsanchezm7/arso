package es.um.arso.productos.servicio.test;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.servicio.IServicioCategorias;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.utils.EntityManagerHelper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramaCategorias {
    private static final Logger log = LoggerFactory.getLogger(ProgramaCategorias.class);

    public static void main(String[] args) throws Exception {
        IServicioCategorias servicio = null;
        try {
            servicio = FactoriaServicios.getServicio(IServicioCategorias.class);

            // TEST 1: cargar jerarquía desde recurso XML (si existe)
            String recurso = args.length > 0 ? args[0] : "xml/Arte_y_ocio.xml";
            java.net.URL res = Thread.currentThread().getContextClassLoader().getResource(recurso);
            if (res != null) {
                String path = new java.io.File(res.toURI()).getPath();
                log.info("Cargando jerarquía desde recurso: {}", path);
                servicio.cargarJerarquia(path);
            } else {
                log.info(
                        "Recurso {} no encontrado en classpath. Si la BD está vacía, crearé categorías mínimas manualmente.",
                        recurso);
                // crear una categoría mínima si no existe
                List<Categoria> raicesChk = servicio.getRaices();
                if (raicesChk == null || raicesChk.isEmpty()) {
                    Repositorio<Categoria, String> repoCat =
                            FactoriaRepositorios.getRepositorio(Categoria.class);
                    Categoria raiz = new Categoria("General");
                    raiz.setId("CAT_GENERAL");
                    repoCat.add(raiz);
                }
            }

            // Comprobar que no duplica: contar raíces antes y después de recargar la misma
            // jerarquía
            List<Categoria> raicesAntes = servicio.getRaices();
            int countAntes = raicesAntes.size();
            log.info("Raíces antes de recarga: {}", countAntes);
            // intentar recargar (si recurso disponible)
            if (res != null) {
                servicio.cargarJerarquia(new java.io.File(res.toURI()).getPath());
            }
            List<Categoria> raicesDespues = servicio.getRaices();
            int countDespues = raicesDespues.size();
            log.info(
                    "Raíces después de recarga: {} (debería ser igual a antes si se evitó duplicado)",
                    countDespues);

            // TEST 2: modificar descripción de la primera raíz
            Categoria primera = servicio.getRaices().get(0);
            String id = primera.getId();
            log.info(
                    "--- Test modificar descripción para id={} (nombre={}) ---",
                    id,
                    primera.getNombre());
            servicio.modificarDescripcion(id, "Descripción de prueba actualizada");
            // Buscar la categoría actualizada entre las raíces
            Categoria actualizada = null;
            for (Categoria c : servicio.getRaices()) {
                if (id.equals(c.getId())) {
                    actualizada = c;
                    break;
                }
            }
            if (actualizada != null) {
                log.info("Descripción actual de {}: {}", id, actualizada.getDescripcion());
            } else {
                log.warn("No se pudo recuperar la categoría {} tras la modificación.", id);
            }

            // TEST 3: recuperar descendientes (si los hay)
            List<Categoria> descendientes = servicio.getDescendientes(id);
            log.info("Descendientes de {}: {}", id, descendientes.size());
            for (Categoria c : descendientes) {
                log.info("  - {} ({}) desc: {}", c.getNombre(), c.getId(), c.getDescripcion());
            }

            log.info("Pruebas de categorías completadas.");

        } catch (Exception e) {
            log.error("Error en ProgramaCategorias", e);
        } finally {
            try {
                EntityManagerHelper.closeEntityManagerFactory();
            } catch (Exception ex) {
                log.warn("Error cerrando EntityManagerFactory: {}", ex.getMessage());
            }
            try {
                AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Exception ex) {
                log.warn("Error cerrando AbandonedConnectionCleanupThread: {}", ex.getMessage());
            }
        }
    }
}
