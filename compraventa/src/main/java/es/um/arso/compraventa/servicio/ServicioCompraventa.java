package es.um.arso.compraventa.servicio;

import es.um.arso.compraventa.modelo.Compraventa;
import es.um.arso.compraventa.modelo.eventos.EventoCompraventaCreada;
import es.um.arso.compraventa.puertos.out.PublicadorEventos;
import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.repositorio.RepositorioCompraventas;
import es.um.arso.compraventa.servicio.puertos.out.IServicioProductosExterno;
import es.um.arso.compraventa.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.compraventa.servicio.puertos.out.ProductoInfo;
import es.um.arso.compraventa.servicio.puertos.out.UsuarioInfo;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServicioCompraventa implements IServicioCompraventa {

    private static final Logger log = LoggerFactory.getLogger(ServicioCompraventa.class);

    @Autowired
    private RepositorioCompraventas repositorioCompraventas;

    @Autowired
    private IServicioProductosExterno servicioProductosExterno;

    @Autowired
    private IServicioUsuariosExterno servicioUsuariosExterno;

    @Autowired
    private PublicadorEventos publicadorEventos;

    @Override
    public String realizarCompraventa(String idProducto, String idComprador) throws Exception {

        if (idProducto == null || idProducto.isEmpty())
            throw new IllegalArgumentException("El ID del producto es obligatorio");

        if (idComprador == null || idComprador.isEmpty())
            throw new IllegalArgumentException("El ID del comprador es obligatorio");

        ProductoInfo producto = servicioProductosExterno.getProducto(idProducto);

        // comprobar disponibilidad del producto
        if (!producto.isDisponible()) throw new IllegalArgumentException("El producto no está disponible");

        UsuarioInfo comprador = servicioUsuariosExterno.getUsuario(idComprador);

        String idVendedor = producto.getIdVendedor();
        if (idVendedor.equals(idComprador)) {
            throw new IllegalArgumentException("No se puede comprar un producto propio");
        }

        UsuarioInfo vendedor = servicioUsuariosExterno.getUsuario(idVendedor);

        String recogidaString =
                producto.getRecogida() != null ? producto.getRecogida().toString() : null;

        Compraventa compraventa = new Compraventa(
                idProducto,
                producto.getTitulo(),
                producto.getPrecio(),
                recogidaString,
                idVendedor,
                vendedor.getNombre(),
                idComprador,
                comprador.getNombre());

        compraventa = repositorioCompraventas.save(compraventa);

        log.info("Compraventa realizada: id={}", compraventa.getId());

        // TODO: como idProducto es la entidad afectada, se podría eliminar
        // el campo idProducto de la clase Java.
        EventoCompraventaCreada evento = new EventoCompraventaCreada(idProducto, idProducto, idVendedor, idComprador);

        publicadorEventos.emitirEvento(evento);

        return compraventa.getId();
    }

    @Override
    public List<Compraventa> getComprasUsuario(String idComprador) {
        return repositorioCompraventas.findByIdComprador(idComprador);
    }

    @Override
    public List<Compraventa> getVentasUsuario(String idVendedor) {
        return repositorioCompraventas.findByIdVendedor(idVendedor);
    }

    @Override
    public List<Compraventa> getCompraventasEntreUsuarios(String idComprador, String idVendedor) {
        return repositorioCompraventas.findByIdCompradorAndIdVendedor(idComprador, idVendedor);
    }

    @Override
    public Page<CompraventaResumen> getComprasUsuarioPaginado(String idComprador, Pageable pageable) {
        Page<Compraventa> compraventas = repositorioCompraventas.findByIdComprador(idComprador, pageable);
        return compraventas.map(this::toCompraventaResumen);
    }

    @Override
    public Page<CompraventaResumen> getVentasUsuarioPaginado(String idVendedor, Pageable pageable) {
        Page<Compraventa> compraventas = repositorioCompraventas.findByIdVendedor(idVendedor, pageable);
        return compraventas.map(this::toCompraventaResumen);
    }

    @Override
    public Page<CompraventaResumen> getCompraventasEntreUsuariosPaginado(
            String idComprador, String idVendedor, Pageable pageable) {
        Page<Compraventa> compraventas =
                repositorioCompraventas.findByIdCompradorAndIdVendedor(idComprador, idVendedor, pageable);
        return compraventas.map(this::toCompraventaResumen);
    }

    @Override
    public Compraventa getCompraventa(String id) throws EntidadNoEncontrada {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("El ID de la compraventa no debe ser nulo.");

        return repositorioCompraventas
                .findById(id)
                .orElseThrow(() -> new EntidadNoEncontrada("Compraventa no encontrada: " + id));
    }

    private CompraventaResumen toCompraventaResumen(Compraventa compraventa) {
        CompraventaResumen resumen = new CompraventaResumen();
        resumen.setId(compraventa.getId());
        resumen.setIdProducto(compraventa.getIdProducto());
        resumen.setTitulo(compraventa.getTitulo());
        resumen.setPrecio(compraventa.getPrecio());
        resumen.setIdVendedor(compraventa.getIdVendedor());
        resumen.setNombreVendedor(compraventa.getNombreVendedor());
        resumen.setIdComprador(compraventa.getIdComprador());
        resumen.setNombreComprador(compraventa.getNombreComprador());
        resumen.setFecha(compraventa.getFecha());
        return resumen;
    }
}
