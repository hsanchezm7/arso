package es.um.arso.productos.servicio.test;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.servicio.IServicioCategorias;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.IServicioUsuarios;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.utils.EntityManagerHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramaProductos {
    private static final Logger log = LoggerFactory.getLogger(ProgramaProductos.class);

    public static void main(String[] args) throws Exception {
        IServicioProductos productoService = null;
        IServicioUsuarios usuarioService = null;
        IServicioCategorias categoriaService = null;
        try {
            productoService = FactoriaServicios.getServicio(IServicioProductos.class);
            usuarioService = FactoriaServicios.getServicio(IServicioUsuarios.class);
            categoriaService = FactoriaServicios.getServicio(IServicioCategorias.class);

            // Preparación: asegurar que existe al menos una categoría
            List<?> raices = categoriaService.getRaices();
            String categoriaId;
            if (raices == null || raices.isEmpty()) {
                log.info(
                        "No hay categorías en base de datos. Creando categorías mínimas para las pruebas...");
                // Crear categorías mínimas usando repositorio directamente
                Repositorio<Categoria, String> repoCat =
                        FactoriaRepositorios.getRepositorio(Categoria.class);
                Categoria raiz = new Categoria("Oficina");
                raiz.setId("CAT_OFICINA");
                repoCat.add(raiz);
                // una subcategoria
                Categoria sub = new Categoria("Sillas");
                sub.setId("CAT_SILLAS");
                raiz.addSubcategoria(sub);
                repoCat.update(raiz);
                categoriaId = raiz.getId();
                log.info("Categorías creadas: {} (sub {})", raiz.getId(), sub.getId());
            } else {
                categoriaId = ((Categoria) raices.get(0)).getId();
            }

            // Crear un vendedor (usuario) para asociarlo al producto
            String vendedorId =
                    usuarioService.alta(
                            "Vendedor",
                            "Prueba",
                            "vendedor@prueba.local",
                            "vpass",
                            LocalDate.of(1985, 1, 1),
                            "600111222");
            // vendedor creado; ServicioUsuarios logs minimal info

            // TEST A: Alta de producto
            String prodId =
                    productoService.crear(
                            "Silla de oficina",
                            "Silla ergonómica",
                            49.99,
                            EstadoProducto.BUEN_ESTADO,
                            categoriaId,
                            true,
                            vendedorId);

            // TEST B: Asignar lugar de recogida
            productoService.asignarLugarRecogida(prodId, "Almacén central", -3.70379, 40.41678);

            // TEST C: Modificar datos (precio y descripción)
            productoService.modificar(prodId, 39.99, "Silla ergonómica - oferta");

            // TEST D: Añadir visualización
            productoService.anadirVisualizacion(prodId);
            productoService.anadirVisualizacion(prodId);

            // TEST E: Historial del mes (resumen ordenado por visualizaciones)
            LocalDateTime now = LocalDateTime.now();
            productoService.getHistorialMes(now.getMonthValue(), now.getYear());

            // TEST F: Búsqueda (por categoría, texto, estado mínimo y precio máximo)
            productoService.buscar(categoriaId, "ergonómica", EstadoProducto.ACEPTABLE, 100.0);

            log.info("Pruebas de productos completadas.");

        } catch (Exception e) {
            log.error("Error en ProgramaProductos", e);
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
