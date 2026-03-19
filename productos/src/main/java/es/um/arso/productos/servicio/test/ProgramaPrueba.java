package es.um.arso.productos.servicio.test;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.repositorio.RepositorioCategorias;
import es.um.arso.productos.servicio.IServicioCategorias;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.IServicioUsuarios;
import es.um.arso.productos.servicio.ProductoResumen;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProgramaPrueba implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ProgramaPrueba.class);

    @Autowired private IServicioProductos servicioProductos;

    @Autowired private IServicioCategorias servicioCategorias;

    @Autowired private IServicioUsuarios servicioUsuarios;

    @Autowired private RepositorioCategorias repositorioCategorias;

    @Override
    public void run(String... args) throws Exception {

        log.info("=== INICIO PRUEBAS ===");

        // --- Pruebas de Categorías ---

        log.info("--- Pruebas de Categorías ---");

        // Crear categorías manualmente
        Categoria raiz = new Categoria("Oficina");
        raiz.setId("CAT_OFICINA");
        Categoria sub = new Categoria("Sillas");
        sub.setId("CAT_SILLAS");
        raiz.addSubcategoria(sub);
        repositorioCategorias.save(raiz);
        log.info("Categorías creadas: {} (sub {})", raiz.getId(), sub.getId());

        // Obtener raíces
        List<Categoria> raices = servicioCategorias.getRaices();
        log.info("Raíces encontradas: {}", raices.size());
        for (Categoria c : raices) {
            log.info("  Raíz: {} ({})", c.getNombre(), c.getId());
        }

        // Modificar descripción
        String categoriaId = raiz.getId();
        servicioCategorias.modificarDescripcion(categoriaId, "Descripción de prueba actualizada");
        List<Categoria> raicesDespues = servicioCategorias.getRaices();
        for (Categoria c : raicesDespues) {
            if (categoriaId.equals(c.getId())) {
                log.info("Descripción actualizada de {}: {}", c.getId(), c.getDescripcion());
            }
        }

        // Descendientes
        List<Categoria> descendientes = servicioCategorias.getDescendientes(categoriaId);
        log.info("Descendientes de {}: {}", categoriaId, descendientes.size());
        for (Categoria c : descendientes) {
            log.info("  - {} ({}) desc: {}", c.getNombre(), c.getId(), c.getDescripcion());
        }

        log.info("--- Pruebas de categorías completadas ---");

        // --- Pruebas de Productos ---

        log.info("--- Pruebas de Productos ---");

        // Crear un vendedor
        String vendedorId =
                servicioUsuarios.alta(
                        "Vendedor",
                        "Prueba",
                        "vendedor@prueba.local",
                        "vpass",
                        LocalDate.of(1985, 1, 1),
                        "600111222");
        log.info("Vendedor creado: id={}", vendedorId);

        // TEST A: Alta de producto
        String prodId =
                servicioProductos.crear(
                        "Silla de oficina",
                        "Silla ergonómica",
                        49.99,
                        EstadoProducto.BUEN_ESTADO,
                        categoriaId,
                        true,
                        vendedorId);
        log.info("Producto creado: id={}", prodId);

        // TEST B: Asignar lugar de recogida
        servicioProductos.asignarLugarRecogida(prodId, "Almacén central", -3.70379, 40.41678);
        log.info("Lugar de recogida asignado");

        // TEST C: Modificar datos
        servicioProductos.modificar(prodId, 39.99, "Silla ergonómica - oferta", true);
        log.info("Producto modificado");

        // TEST D: Añadir visualizaciones
        servicioProductos.anadirVisualizacion(prodId);
        servicioProductos.anadirVisualizacion(prodId);
        log.info("Visualizaciones añadidas");

        // TEST E: Consultar producto
        Producto producto = servicioProductos.getProducto(prodId);
        log.info(
                "Producto consultado: titulo={}, precio={}, visualizaciones={}",
                producto.getTitulo(),
                producto.getPrecio(),
                producto.getVisualizaciones());

        // TEST F: Historial del mes
        LocalDateTime now = LocalDateTime.now();
        List<ProductoResumen> historial =
                servicioProductos.getHistorialMes(now.getMonthValue(), now.getYear());
        log.info("Historial del mes: {} productos", historial.size());
        for (ProductoResumen r : historial) {
            log.info(
                    "  - {} ({}€) visualizaciones={}",
                    r.getTitulo(),
                    r.getPrecio(),
                    r.getVisualizaciones());
        }

        // TEST G: Búsqueda
        List<Producto> resultados =
                servicioProductos.buscar(
                        categoriaId, "ergonómica", EstadoProducto.ACEPTABLE, 100.0);
        log.info("Búsqueda: {} resultados", resultados.size());
        for (Producto p : resultados) {
            log.info("  - {} ({}€)", p.getTitulo(), p.getPrecio());
        }

        log.info("--- Pruebas de productos completadas ---");

        log.info("=== FIN PRUEBAS ===");
    }
}
