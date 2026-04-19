package es.um.arso.compraventa.rest;

import es.um.arso.compraventa.modelo.Compraventa;
import es.um.arso.compraventa.rest.dto.CompraventaDto;
import es.um.arso.compraventa.rest.dto.NuevaCompraventaDto;
import es.um.arso.compraventa.servicio.IServicioCompraventa;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
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

    private final IServicioCompraventa servicioCompraventa;

    private final PagedResourcesAssembler<Compraventa> pagedResourcesAssembler;

    private final CompraventaAssembler compraventaAssembler;

    public ControladorCompraventas(
            IServicioCompraventa servicioCompraventa,
            PagedResourcesAssembler<Compraventa> pagedResourcesAssembler,
            CompraventaAssembler compraventaAssembler) {
        this.servicioCompraventa = servicioCompraventa;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.compraventaAssembler = compraventaAssembler;
    }

    @PostMapping
    public ResponseEntity<Void> realizarCompraventa(@Valid @RequestBody NuevaCompraventaDto nueva) throws Exception {

        String id = this.servicioCompraventa.realizarCompraventa(nueva.getIdProducto(), nueva.getIdComprador());

        URI nuevaURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(nuevaURL).build();
    }

    @GetMapping("/{id}")
    public EntityModel<CompraventaDto> getCompraventaById(@PathVariable String id) throws Exception {

        Compraventa compraventa = this.servicioCompraventa.getCompraventa(id);
        CompraventaDto compraventaDto = CompraventaDto.fromEntity(compraventa);

        EntityModel<CompraventaDto> model = EntityModel.of(compraventaDto);
        model.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ControladorCompraventas.class)
                        .getCompraventaById(id))
                .withSelfRel());
        return model;
    }

    @GetMapping("/compras/{idComprador}")
    public PagedModel<EntityModel<Compraventa>> getComprasUsuario(@PathVariable String idComprador, Pageable paginacion)
            throws Exception {

        Page<Compraventa> resultado = this.servicioCompraventa.getComprasUsuarioPaginado(idComprador, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaAssembler);
    }

    @GetMapping("/ventas/{idVendedor}")
    public PagedModel<EntityModel<Compraventa>> getVentasUsuario(@PathVariable String idVendedor, Pageable paginacion)
            throws Exception {

        Page<Compraventa> resultado = this.servicioCompraventa.getVentasUsuarioPaginado(idVendedor, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaAssembler);
    }

    @GetMapping
    public PagedModel<EntityModel<Compraventa>> getCompraventasEntreUsuarios(
            @RequestParam String idComprador, @RequestParam String idVendedor, Pageable paginacion) throws Exception {

        Page<Compraventa> resultado =
                this.servicioCompraventa.getCompraventasEntreUsuariosPaginado(idComprador, idVendedor, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, compraventaAssembler);
    }
}
