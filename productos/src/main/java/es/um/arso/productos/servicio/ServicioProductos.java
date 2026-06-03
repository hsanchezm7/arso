package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.modelo.Usuario;
import es.um.arso.productos.repositorio.RepositorioCategorias;
import es.um.arso.productos.repositorio.RepositorioProductos;
import es.um.arso.productos.repositorio.RepositorioUsuarios;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
            String vendedorId)
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

        Producto producto = new Producto(titulo, descripcion, precio, estado, categoria, envioDisponible, vendedor);
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
            String productoId, Double nuevoPrecio, String nuevaDescripcion, boolean estaDisponible, String vendedorId)
            throws EntidadNoEncontrada {
        Producto producto = repositorioProductos
                .findById(productoId)
                .orElseThrow(() -> new EntidadNoEncontrada("Producto no encontrado: " + productoId));
        validarPropietario(producto, vendedorId);
        if (nuevoPrecio != null) {
            if (nuevoPrecio < 0) throw new IllegalArgumentException("precio no válido");
            producto.setPrecio(nuevoPrecio);
        }
        if (nuevaDescripcion != null && !nuevaDescripcion.isEmpty()) {
            producto.setDescripcion(nuevaDescripcion);
        }

        producto.setDisponible(estaDisponible);

        repositorioProductos.save(producto);
        log.info("Producto modificado: id={}", productoId);
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
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscar(
            String categoriaId,
            String texto,
            EstadoProducto estadoMinimo,
            Double precioMinimo,
            Double precioMaximo,
            String idVendedor) {
        java.util.Set<String> categoriasPermitidas = new java.util.HashSet<>();
        if (categoriaId != null) {
            repositorioCategorias.findById(categoriaId).ifPresent(cat -> {
                categoriasPermitidas.add(categoriaId);
                for (Categoria d : cat.getDescendientes()) categoriasPermitidas.add(d.getId());
            });
        }

        java.util.stream.Stream<Producto> stream =
                StreamSupport.stream(repositorioProductos.findAll().spliterator(), false);

        if (!categoriasPermitidas.isEmpty()) {
            stream = stream.filter(p -> p.getCategoria() != null
                    && categoriasPermitidas.contains(p.getCategoria().getId()));
        }
        if (texto != null && !texto.isEmpty()) {
            String t = texto.toLowerCase();
            stream = stream.filter(p -> p.getDescripcion() != null
                    && p.getDescripcion().toLowerCase().contains(t));
        }
        if (estadoMinimo != null) {
            stream = stream.filter(p -> p.getEstado() != null && p.getEstado().esIgualOMejorQue(estadoMinimo));
        }
        if (precioMinimo != null) {
            stream = stream.filter(p -> p.getPrecio() != null && p.getPrecio() >= precioMinimo);
        }
        if (precioMaximo != null) {
            stream = stream.filter(p -> p.getPrecio() != null && p.getPrecio() <= precioMaximo);
        }
        if (idVendedor != null && !idVendedor.isEmpty()) {
            stream = stream.filter(p ->
                    p.getVendedor() != null && idVendedor.equals(p.getVendedor().getId()));
        }

        return stream.collect(Collectors.toList());
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
        List<Producto> productos = buscar(categoriaId, texto, estadoMinimo, precioMinimo, precioMaximo, idVendedor);
        int start = (int) paginacion.getOffset();
        int end = Math.min((start + paginacion.getPageSize()), productos.size());
        List<Producto> pageContent = start > productos.size() ? Collections.emptyList() : productos.subList(start, end);

        List<ProductoResumen> resumenes = pageContent.stream()
                .map(p -> {
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

        return new PageImpl<>(resumenes, paginacion, productos.size());
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
