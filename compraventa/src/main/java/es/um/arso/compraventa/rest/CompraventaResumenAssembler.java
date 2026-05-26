package es.um.arso.compraventa.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import es.um.arso.compraventa.servicio.CompraventaResumen;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CompraventaResumenAssembler
        implements RepresentationModelAssembler<CompraventaResumen, EntityModel<CompraventaResumen>> {

    @Override
    public EntityModel<CompraventaResumen> toModel(CompraventaResumen compraventaResumen) {
        try {
            return EntityModel.of(
                    compraventaResumen,
                    linkTo(methodOn(ControladorCompraventas.class).getCompraventaById(compraventaResumen.getId()))
                            .withSelfRel());
        } catch (Exception e) {
            return EntityModel.of(compraventaResumen);
        }
    }
}
