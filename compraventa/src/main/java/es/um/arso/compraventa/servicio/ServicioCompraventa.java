package es.um.arso.compraventa.servicio;

import es.um.arso.compraventa.modelo.Compraventa;
import es.um.arso.compraventa.repositorio.RepositorioCompraventas;
import es.um.arso.compraventa.servicio.puertos.IServicioProductosExterno;
import es.um.arso.compraventa.servicio.puertos.IServicioUsuariosExterno;
import es.um.arso.compraventa.servicio.puertos.ProductoInfo;
import es.um.arso.compraventa.servicio.puertos.UsuarioInfo;
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

    @Autowired private RepositorioCompraventas repositorioCompraventas;

    @Autowired private IServicioProductosExterno servicioProductosExterno;

    @Autowired private IServicioUsuariosExterno servicioUsuariosExterno;

    @Override
    public String realizarCompraventa(String idProducto, String idComprador) throws Exception {

        if (idProducto == null || idProducto.isEmpty()) {
            throw new IllegalArgumentException("El ID del producto es obligatorio");
        }
        if (idComprador == null || idComprador.isEmpty()) {
            throw new IllegalArgumentException("El ID del comprador es obligatorio");
        }

        ProductoInfo producto = servicioProductosExterno.getProducto(idProducto);
        if (producto == null) {
            throw new RuntimeException("Producto no encontrado: " + idProducto);
        }

        UsuarioInfo comprador = servicioUsuariosExterno.getUsuario(idComprador);
        if (comprador == null) {
            throw new RuntimeException("Comprador no encontrado: " + idComprador);
        }

        UsuarioInfo vendedor = servicioUsuariosExterno.getUsuario(producto.getIdVendedor());
        if (vendedor == null) {
            throw new RuntimeException("Vendedor no encontrado: " + producto.getIdVendedor());
        }

        String recogidaString =
                producto.getRecogida() != null ? producto.getRecogida().toString() : null;

        Compraventa compraventa =
                new Compraventa(
                        idProducto,
                        producto.getTitulo(),
                        producto.getPrecio(),
                        recogidaString,
                        producto.getIdVendedor(),
                        vendedor.getNombre(),
                        idComprador,
                        comprador.getNombre());

        compraventa = repositorioCompraventas.save(compraventa);

        log.info("Compraventa realizada: id={}", compraventa.getId());
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
    public Page<Compraventa> getComprasUsuarioPaginado(String idComprador, Pageable pageable) {
        return repositorioCompraventas.findByIdComprador(idComprador, pageable);
    }

    @Override
    public Page<Compraventa> getVentasUsuarioPaginado(String idVendedor, Pageable pageable) {
        return repositorioCompraventas.findByIdVendedor(idVendedor, pageable);
    }

    @Override
    public Page<Compraventa> getCompraventasEntreUsuariosPaginado(
            String idComprador, String idVendedor, Pageable pageable) {
        return repositorioCompraventas.findByIdCompradorAndIdVendedor(
                idComprador, idVendedor, pageable);
    }

    @Override
    public Compraventa getCompraventa(String id) {
        return repositorioCompraventas
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Compraventa no encontrada: " + id));
    }
}
