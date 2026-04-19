package es.um.arso.compraventa.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import es.um.arso.compraventa.modelo.Compraventa;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CompraventaAssembler implements RepresentationModelAssembler<Compraventa, EntityModel<Compraventa>> {

    @Override
    public EntityModel<Compraventa> toModel(Compraventa compraventa) {
        try {
            return EntityModel.of(
                    compraventa,
                    linkTo(methodOn(ControladorCompraventas.class).getCompraventaById(compraventa.getId()))
                            .withSelfRel());
        } catch (Exception e) {
            return EntityModel.of(compraventa);
        }
    }
}
