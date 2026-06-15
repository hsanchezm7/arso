package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.modelo.Usuario;
import es.um.arso.productos.repositorio.RepositorioCategorias;
import es.um.arso.productos.repositorio.RepositorioProductos;
import es.um.arso.productos.repositorio.RepositorioUsuarios;
import es.um.arso.productos.repositorio.especificaciones.EspecificacionesProducto;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioProductos implements IServicioProductos {

    private static final Logger log = LoggerFactory.getLogger(ServicioProductos.class);

    @Autowired
    private RepositorioProductos repositorioProductos;

    @Autowired
    private RepositorioCategorias repositorioCategorias;

    @Autowired
    private RepositorioUsuarios repositorioUsuarios;

    @Override
    public String crear(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            String categoriaId,
            boolean envioDisponible,
            String vendedorId,
            String urlImagen)
            throws EntidadNoEncontrada {

        if (titulo == null || titulo.isEmpty()) throw new IllegalArgumentException("titulo obligatorio");
        if (precio == null || precio < 0) throw new IllegalArgumentException("precio no válido");
        if (estado == null) throw new IllegalArgumentException("estado obligatorio");

        if (vendedorId == null || vendedorId.isEmpty()) {
            throw new IllegalArgumentException("vendedorId obligatorio");
        }

        Categoria categoria = repositorioCategorias
                .findById(categoriaId)
                .orElseThrow(() -> new EntidadNoEncontrada("Categoría no encontrada: " + categoriaId));
        Usuario vendedor = repositorioUsuarios
                .findById(vendedorId)
                .orElseThrow(() -> new EntidadNoEncontrada("Vendedor no encontrado: " + vendedorId));

        Producto producto = new Producto(titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor, urlImagen);
        producto = repositorioProductos.save(producto);
        log.info("Producto creado: id={}", producto.getId());
        return producto.getId();
    }

    @Override
    public void asignarLugarRecogida(
            String productoId, String descripcion, Double longitud, Double latitud, String vendedorId)
            throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        validarPropietario(producto, vendedorId);
        producto.asignarLugarRecogida(descripcion, longitud, latitud);
        repositorioProductos.save(producto);
        log.info("Lugar recogida asignado producto={}", productoId);
    }

    @Override
    public void modificar(
            String productoId, Double nuevoPrecio, String nuevaDescripcion, String vendedorId)
            throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        validarPropietario(producto, vendedorId);

        if (nuevoPrecio != null) {
            if (nuevoPrecio < 0)
                throw new IllegalArgumentException("precio no válido");
            producto.setPrecio(nuevoPrecio);
        }
        
        if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty())
            producto.setDescripcion(nuevaDescripcion);

        repositorioProductos.save(producto);
        log.info("Producto modificado: id={}", productoId);
    }
    
    @Override
    public void eliminar(String productoId, String vendedorId) throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        validarPropietario(producto, vendedorId);
        
        if (!producto.isDisponible())
            throw new IllegalArgumentException("No se puede eliminar un producto que no está disponible");
        
        repositorioProductos.delete(producto);
        log.info("Producto eliminado: id={}", productoId);
    }

    @Override
    public void anadirVisualizacion(String productoId) throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        producto.incrementarVisualizaciones();
        repositorioProductos.save(producto);
        log.info("Visualizacion añadida producto={}", productoId);
    }

    @Override
    public void marcarNoDisponible(String productoId) throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        producto.setDisponible(false);
        repositorioProductos.save(producto);
        log.info("Producto marcado no disponible id={}", productoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Producto getProducto(String id) throws EntidadNoEncontrada {
        return repositorioProductos
                .findById(id)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResumen> getHistorialMes(int mes, int anio) {
        LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime fin = inicio.plusMonths(1);
        return repositorioProductos.getByPublicadosEntre(inicio, fin).stream()
                .sorted((a, b) -> Integer.compare(b.getVisualizaciones(), a.getVisualizaciones()))
                .map(p -> {
                    ProductoResumen r = new ProductoResumen();
                    r.setId(p.getId());
                    r.setTitulo(p.getTitulo());
                    r.setPrecio(p.getPrecio());
                    r.setFechaPublicacion(p.getFechaPublicacion());
                    r.setNombreCategoria(
                            p.getCategoria() != null ? p.getCategoria().getNombre() : null);
                    r.setVisualizaciones(p.getVisualizaciones());
                    r.setUrlImagen(p.getUrlImagen());
                    r.setEnvioDisponible(p.isEnvioDisponible());
                    r.setDescripcion(p.getDescripcion());
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResumen> getHistorialMesPaginado(int mes, int anio, Pageable paginacion) {
        List<ProductoResumen> historial = getHistorialMes(mes, anio);
        int start = (int) paginacion.getOffset();
        int end = Math.min((start + paginacion.getPageSize()), historial.size());
        List<ProductoResumen> pageContent =
                start > historial.size() ? Collections.emptyList() : historial.subList(start, end);
        return new PageImpl<>(pageContent, paginacion, historial.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResumen> buscarPaginado(
            String categoriaId,
            String texto,
            EstadoProducto estadoMinimo,
            Double precioMinimo,
            Double precioMaximo,
            String idVendedor,
            Pageable paginacion) {
        List<String> categoriasBusqueda = new LinkedList<>();

        if (categoriaId != null) {
            repositorioCategorias.findById(categoriaId).ifPresent(cat -> {
                categoriasBusqueda.add(categoriaId);
                for (Categoria c : cat.getDescendientes())
                    categoriasBusqueda.add(c.getId());
            });
        }

        Specification<Producto> spec = EspecificacionesProducto.crearEspecificacionBusqueda(
            categoriasBusqueda, texto, estadoMinimo, precioMinimo, precioMaximo, idVendedor);

        Page<Producto> productos = repositorioProductos.findAll(spec, paginacion);

        return productos.map(p -> {
            ProductoResumen r = new ProductoResumen();
            r.setId(p.getId());
            r.setTitulo(p.getTitulo());
            r.setPrecio(p.getPrecio());
            r.setFechaPublicacion(p.getFechaPublicacion());
            r.setNombreCategoria(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
            r.setVisualizaciones(p.getVisualizaciones());
            r.setUrlImagen(p.getUrlImagen());
            r.setEnvioDisponible(p.isEnvioDisponible());
            r.setDescripcion(p.getDescripcion());
            return r;
        });
    }

    private void validarPropietario(Producto producto, String vendedorId) {
        if (vendedorId == null || vendedorId.isEmpty()) {
            throw new IllegalArgumentException("vendedorId obligatorio");
        }
        if (producto.getVendedor() == null
                || !vendedorId.equals(producto.getVendedor().getId())) {
            throw new SecurityException("Solo el propietario puede modificar este producto");
        }
    }

    @Override
    public List<EstadoProducto> getEstadosProducto() {
        return Arrays.asList(EstadoProducto.values());
    }
}
