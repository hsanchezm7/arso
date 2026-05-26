package es.um.arso.compraventa.servicio;

import es.um.arso.compraventa.modelo.Compraventa;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServicioCompraventa {

    String realizarCompraventa(String idProducto, String idComprador) throws Exception;

    List<Compraventa> getComprasUsuario(String idComprador);

    List<Compraventa> getVentasUsuario(String idVendedor);

    List<Compraventa> getCompraventasEntreUsuarios(String idComprador, String idVendedor);

    Page<CompraventaResumen> getComprasUsuarioPaginado(String idComprador, Pageable pageable);

    Page<CompraventaResumen> getVentasUsuarioPaginado(String idVendedor, Pageable pageable);

    Page<CompraventaResumen> getCompraventasEntreUsuariosPaginado(String idComprador, String idVendedor, Pageable pageable);

    Compraventa getCompraventa(String id);
}
