package es.um.arso.compraventa.servicio;

import es.um.arso.compraventa.repositorio.EntidadNoEncontrada;
import es.um.arso.compraventa.rest.dto.CompraventaDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IServicioCompraventa {

    String realizarCompraventa(String idProducto, String idComprador) throws Exception;

    List<CompraventaDto> getComprasUsuario(String idComprador);

    List<CompraventaDto> getVentasUsuario(String idVendedor);

    List<CompraventaDto> getCompraventasEntreUsuarios(String idComprador, String idVendedor);

    Page<CompraventaResumen> getComprasUsuarioPaginado(String idComprador, Pageable pageable);

    Page<CompraventaResumen> getVentasUsuarioPaginado(String idVendedor, Pageable pageable);

    Page<CompraventaResumen> getCompraventasEntreUsuariosPaginado(
            String idComprador, String idVendedor, Pageable pageable);

    CompraventaDto getCompraventa(String id) throws EntidadNoEncontrada;
}
