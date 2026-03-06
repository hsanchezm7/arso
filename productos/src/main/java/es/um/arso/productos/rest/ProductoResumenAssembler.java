package es.um.arso.productos.rest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import es.um.arso.productos.servicio.ProductoResumen;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProductoResumenAssembler
        implements RepresentationModelAssembler<ProductoResumen, EntityModel<ProductoResumen>> {

    @Override
    public EntityModel<ProductoResumen> toModel(ProductoResumen productoResumen) {
        try {
            return EntityModel.of(
                    productoResumen,
                    linkTo(
                                    methodOn(ControladorProductos.class)
                                            .getProductoById(productoResumen.getId()))
                            .withSelfRel());
        } catch (Exception e) {
            return EntityModel.of(productoResumen);
        }
    }
}
