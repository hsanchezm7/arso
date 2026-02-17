package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.modelo.Usuario;
import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.repositorio.RepositorioException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioProductos implements IServicioProductos {

    private static final Logger log = LoggerFactory.getLogger(ServicioProductos.class);

    private Repositorio<Producto, String> repositorioProductos =
            FactoriaRepositorios.getRepositorio(Producto.class);
    private Repositorio<Categoria, String> repositorioCategorias =
            FactoriaRepositorios.getRepositorio(Categoria.class);
    private Repositorio<Usuario, String> repositorioUsuarios =
            FactoriaRepositorios.getRepositorio(Usuario.class);

    @Override
    public String crear(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            String categoriaId,
            boolean envioDisponible,
            String vendedorId)
            throws RepositorioException {

        if (titulo == null || titulo.isEmpty())
            throw new IllegalArgumentException("titulo obligatorio");
        if (precio == null || precio < 0) throw new IllegalArgumentException("precio no válido");
        if (estado == null) throw new IllegalArgumentException("estado obligatorio");

        Categoria categoria = null;
        Usuario vendedor = null;
        try {
            categoria = repositorioCategorias.getById(categoriaId);
            vendedor = repositorioUsuarios.getById(vendedorId);
        } catch (EntidadNoEncontrada e) {
            throw new IllegalArgumentException("Categoria o vendedor inexistente");
        }

        Producto producto =
                new Producto(
                        titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor);
        String id = repositorioProductos.add(producto);
        log.info("Producto creado: id={}", id);
        return id;
    }

    @Override
    public void asignarLugarRecogida(
            String productoId, String descripcion, Double longitud, Double latitud)
            throws RepositorioException, EntidadNoEncontrada {
        Producto producto = repositorioProductos.getById(productoId);
        producto.asignarLugarRecogida(descripcion, longitud, latitud);
        repositorioProductos.update(producto);

        log.info("Lugar recogida asignado producto={}", productoId);
    }

    @Override
    public void modificar(String productoId, Double nuevoPrecio, String nuevaDescripcion)
            throws RepositorioException, EntidadNoEncontrada {
        Producto producto = repositorioProductos.getById(productoId);
        if (nuevoPrecio != null) {
            if (nuevoPrecio < 0) throw new IllegalArgumentException("precio no válido");
            producto.setPrecio(nuevoPrecio);
        }
        if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
            producto.setDescripcion(nuevaDescripcion);
        }
        repositorioProductos.update(producto);
        try {
            log.info("Producto modificado: id={}", productoId);
        } catch (Exception ex) {
            // ignore logging failures
        }
    }

    @Override
    public void anadirVisualizacion(String productoId)
            throws RepositorioException, EntidadNoEncontrada {
        Producto producto = repositorioProductos.getById(productoId);
        producto.incrementarVisualizaciones();
        repositorioProductos.update(producto);

        log.info("Visualizacion añadida producto={}", productoId);
    }

    @Override
    public Producto getProducto(String id) throws RepositorioException, EntidadNoEncontrada {
        return repositorioProductos.getById(id);
    }

    @Override
    public List<ProductoResumen> getHistorialMes(int mes, int anio) throws RepositorioException {
        LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime fin = inicio.plusMonths(1);
        return repositorioProductos.getAll().stream()
                .filter(
                        p ->
                                p.getFechaPublicacion() != null
                                        && !p.getFechaPublicacion().isBefore(inicio)
                                        && p.getFechaPublicacion().isBefore(fin))
                .sorted((a, b) -> Integer.compare(b.getVisualizaciones(), a.getVisualizaciones()))
                .map(
                        p -> {
                            ProductoResumen r = new ProductoResumen();
                            r.setId(p.getId());
                            r.setTitulo(p.getTitulo());
                            r.setPrecio(p.getPrecio());
                            r.setFechaPublicacion(p.getFechaPublicacion());
                            r.setNombreCategoria(
                                    p.getCategoria() != null ? p.getCategoria().getNombre() : null);
                            r.setVisualizaciones(p.getVisualizaciones());
                            return r;
                        })
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> buscar(
            String categoriaId, String texto, EstadoProducto estadoMinimo, Double precioMaximo)
            throws RepositorioException {
        java.util.Set<String> categoriasPermitidas = new java.util.HashSet<>();
        if (categoriaId != null) {
            try {
                Categoria cat = repositorioCategorias.getById(categoriaId);
                categoriasPermitidas.add(categoriaId);
                for (Categoria d : cat.getDescendientes()) categoriasPermitidas.add(d.getId());
            } catch (EntidadNoEncontrada e) {
                // si no existe, no filtramos por categoría
            }
        }

        java.util.stream.Stream<Producto> stream = repositorioProductos.getAll().stream();

        if (!categoriasPermitidas.isEmpty()) {
            stream =
                    stream.filter(
                            p ->
                                    p.getCategoria() != null
                                            && categoriasPermitidas.contains(
                                                    p.getCategoria().getId()));
        }
        if (texto != null && !texto.isEmpty()) {
            String t = texto.toLowerCase();
            stream =
                    stream.filter(
                            p ->
                                    p.getDescripcion() != null
                                            && p.getDescripcion().toLowerCase().contains(t));
        }
        if (estadoMinimo != null) {
            stream =
                    stream.filter(
                            p ->
                                    p.getEstado() != null
                                            && p.getEstado().esIgualOMejorQue(estadoMinimo));
        }
        if (precioMaximo != null) {
            stream = stream.filter(p -> p.getPrecio() != null && p.getPrecio() <= precioMaximo);
        }

        return stream.collect(Collectors.toList());
    }
}
