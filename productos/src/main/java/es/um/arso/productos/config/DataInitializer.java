package es.um.arso.productos.config;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.modelo.Usuario;
import es.um.arso.productos.repositorio.RepositorioCategorias;
import es.um.arso.productos.repositorio.RepositorioProductos;
import es.um.arso.productos.repositorio.RepositorioUsuarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RepositorioCategorias repositorioCategorias;
    private final RepositorioProductos repositorioProductos;
    private final RepositorioUsuarios repositorioUsuarios;

    public DataInitializer(
            RepositorioCategorias repositorioCategorias,
            RepositorioProductos repositorioProductos,
            RepositorioUsuarios repositorioUsuarios) {
        this.repositorioCategorias = repositorioCategorias;
        this.repositorioProductos = repositorioProductos;
        this.repositorioUsuarios = repositorioUsuarios;
    }

    public void initializeData(String adminId) {
        long categoriasCount = repositorioCategorias.count();
        if (categoriasCount > 0) {
            log.info("Base de datos ya contiene datos. Omitiendo inicialización.");
            return;
        }

        Usuario admin = repositorioUsuarios.findById(adminId).orElse(null);
        if (admin == null) {
            log.warn("Usuario admin con id {} no encontrado. No se puede inicializar datos.", adminId);
            return;
        }

        log.info("Inicializando base de datos con datos de prueba para admin id={}, email={}", admin.getId(),
                admin.getEmail());

        // Crear categorías
        Categoria electronica = new Categoria("Electrónica");
        electronica.setDescripcion("Dispositivos electrónicos");
        electronica.setRuta("/electronica");
        electronica = repositorioCategorias.save(electronica);

        Categoria libros = new Categoria("Libros");
        libros.setDescripcion("Literatura y no ficción");
        libros.setRuta("/libros");
        libros = repositorioCategorias.save(libros);

        Categoria ropa = new Categoria("Ropa");
        ropa.setDescripcion("Prendas de vestir");
        ropa.setRuta("/ropa");
        ropa = repositorioCategorias.save(ropa);

        log.info("Categorías creadas: Electrónica, Libros, Ropa");

        // Crear productos para Electrónica
        crearProducto("Smartphone X", "Teléfono inteligente de última generación", 599.99,
                EstadoProducto.BUEN_ESTADO, electronica, true, admin);
        crearProducto("Laptop Pro", "Portátil de alto rendimiento", 1299.99,
                EstadoProducto.ACEPTABLE, electronica, false, admin);
        crearProducto("Auriculares Bluetooth", "Auriculares inalámbricos premium", 199.99,
                EstadoProducto.COMO_NUEVO, electronica, true, admin);

        // Crear productos para Libros
        crearProducto("Clean Code", "Manual de programación limpia", 49.99,
                EstadoProducto.NUEVO, libros, true, admin);
        crearProducto("El Quijote", "Obra maestra de la literatura española", 25.99,
                EstadoProducto.PARA_PIEZAS_O_REPARAR, libros, true, admin);
        crearProducto("1984", "Novela de ciencia ficción distópica", 15.99,
                EstadoProducto.COMO_NUEVO, libros, true, admin);

        // Crear productos para Ropa
        crearProducto("Camiseta Básica", "Camiseta de algodón 100%", 19.99,
                EstadoProducto.PARA_PIEZAS_O_REPARAR, ropa, true, admin);
        crearProducto("Pantalón Vaquero", "Pantalón vaquero azul oscuro", 59.99,
                EstadoProducto.COMO_NUEVO, ropa, false, admin);
        crearProducto("Sudadera Deportiva", "Sudadera gris de poliéster", 49.99,
                EstadoProducto.NUEVO, ropa, true, admin);

        log.info("9 productos creados (3 por categoría). Inicialización completada.");
    }

    private void crearProducto(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            Categoria categoria,
            boolean envioDisponible,
            Usuario vendedor) {
        Producto producto = new Producto(titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor);
        producto.setDisponible(true);
        repositorioProductos.save(producto);
        log.debug("Producto creado: titulo={}, categoria={}", titulo, categoria.getNombre());
    }
}
