package es.um.arso.compraventa.rest;

import es.um.arso.compraventa.rest.dto.CompraventaDto;
import es.um.arso.compraventa.rest.dto.NuevaCompraventaDto;
import es.um.arso.compraventa.servicio.CompraventaResumen;
import es.um.arso.compraventa.servicio.IServicioCompraventa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.security.Principal;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/compraventas")
public class ControladorCompraventas {

    private static final Logger log = LoggerFactory.getLogger(ControladorCompraventas.class);

    private final IServicioCompraventa servicioCompraventa;

    private final PagedResourcesAssembler<CompraventaResumen> pagedResourcesAssembler;

    private final CompraventaResumenAssembler compraventaResumenAssembler;

    public ControladorCompraventas(
            IServicioCompraventa servicioCompraventa,
            PagedResourcesAssembler<CompraventaResumen> pagedResourcesAssembler,
            CompraventaResumenAssembler compraventaResumenAssembler) {
        this.servicioCompraventa = servicioCompraventa;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.compraventaResumenAssembler = compraventaResumenAssembler;
    }

    @PostMapping
    @Operation(
            summary = "Realizar compraventa",
            description = "Registra una nueva compraventa entre un comprador y un vendedor.")
    @ApiResponse(
            responseCode = "201",
            description = "Compraventa registrada exitosamente. URL en la cabecera Location.")
    @PreAuthorize("hasAuthority('USUARIO')")
    public ResponseEntity<Void> realizarCompraventa(@Valid @RequestBody NuevaCompraventaDto nueva, Principal principal)
            throws Exception {

        log.info("POST /compraventas idProducto={}, idComprador={}", nueva.getIdProducto(), principal.getName());

        String id = this.servicioCompraventa.realizarCompraventa(nueva.getIdProducto(), principal.getName());

        URI nuevaURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(nuevaURL).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener compraventa", description = "Obtiene una compraventa por su id.")
    public EntityModel<CompraventaDto> getCompraventaById(@PathVariable String id) throws Exception {

        log.info("GET /compraventas/{}", id);

        CompraventaDto compraventaDto = this.servicioCompraventa.getCompraventa(id);

        EntityModel<CompraventaDto> model = EntityModel.of(compraventaDto);
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ControladorCompraventas.class)
                        .getCompraventaById(id))
                .withSelfRel());
        return model;
    }

    @GetMapping("/compras")
    @Operation(
            summary = "Obtener compras de usuario",
            description = "Obtiene un listado paginado de las compras realizadas por un usuario.")
    @PreAuthorize("hasAuthority('USUARIO')")
    public PagedModel<EntityModel<CompraventaResumen>> getComprasUsuario(
            Principal principal, Pageable paginacion) throws Exception {

        log.info("GET /compraventas/compras idComprador={}", principal.getName());

        Page<CompraventaResumen> resultado =
                this.servicioCompraventa.getComprasUsuarioPaginado(principal.getName(), paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaResumenAssembler);
    }

    @GetMapping("/ventas")
    @Operation(
            summary = "Obtener ventas de usuario",
            description = "Obtiene un listado paginado de las ventas realizadas por un usuario.")
    @PreAuthorize("hasAuthority('USUARIO')")
    public PagedModel<EntityModel<CompraventaResumen>> getVentasUsuario(
            Principal principal, Pageable paginacion) throws Exception {

        log.info("GET /compraventas/ventas idVendedor={}", principal.getName());

        Page<CompraventaResumen> resultado = this.servicioCompraventa.getVentasUsuarioPaginado(principal.getName(), paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaResumenAssembler);
    }

    @GetMapping
    @Operation(
            summary = "Buscar compraventas",
            description =
                    "Obtiene un listado paginado de compraventas. Permite filtrar por comprador, vendedor o ambos.")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public PagedModel<EntityModel<CompraventaResumen>> getCompraventasEntreUsuarios(
            @RequestParam(required = false) String idComprador,
            @RequestParam(required = false) String idVendedor,
            Pageable paginacion)
            throws Exception {

        log.info("GET /compraventas idComprador={}, idVendedor={}", idComprador, idVendedor);

        Page<CompraventaResumen> resultado =
                this.servicioCompraventa.getCompraventasEntreUsuariosPaginado(idComprador, idVendedor, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaResumenAssembler);
    }
}
