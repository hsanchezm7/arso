package es.um.arso.productos.rest;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.rest.dto.LugarRecogidaDto;
import es.um.arso.productos.rest.dto.ModificarProductoDto;
import es.um.arso.productos.rest.dto.NuevoProductoDto;
import es.um.arso.productos.rest.dto.ProductoDto;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.ProductoResumen;
import java.net.URI;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/productos")
public class ControladorProductos {

    private IServicioProductos servicioProductos;

    @Autowired
    private PagedResourcesAssembler<ProductoResumen> pagedResourcesAssembler;

    @Autowired
    private ProductoResumenAssembler productoResumenAssembler;

    public ControladorProductos(IServicioProductos servicioProductos) {
        this.servicioProductos = servicioProductos;
    }

    @PostMapping
    public ResponseEntity<Void> crearProducto(@Valid @RequestBody NuevoProductoDto nuevoProducto) throws Exception {

        String id = this.servicioProductos.crear(
                nuevoProducto.getTitulo(),
                nuevoProducto.getDescripcion(),
                nuevoProducto.getPrecio(),
                nuevoProducto.getEstado(),
                nuevoProducto.getCategoriaId(),
                nuevoProducto.isEnvioDisponible(),
                nuevoProducto.getVendedorId());

        URI nuevaURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(nuevaURL).build();
    }

    @GetMapping("/{id}")
    public EntityModel<ProductoDto> getProductoById(@PathVariable String id) throws Exception {

        Producto producto = this.servicioProductos.getProducto(id);
        ProductoDto productoDto = ProductoDto.fromEntity(producto);

        EntityModel<ProductoDto> model = EntityModel.of(productoDto);
        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ControladorProductos.class).getProductoById(id))
                .withSelfRel());
        return model;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> modificarProducto(
            @PathVariable String id, @Valid @RequestBody ModificarProductoDto modificacion) throws Exception {

        this.servicioProductos.modificar(
                id, modificacion.getPrecio(), modificacion.getDescripcion(), modificacion.isDisponibilidad());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/recogida")
    public ResponseEntity<Void> asignarLugarRecogida(
            @PathVariable String id, @Valid @RequestBody LugarRecogidaDto recogida) throws Exception {

        this.servicioProductos.asignarLugarRecogida(
                id, recogida.getDescripcion(), recogida.getLongitud(), recogida.getLatitud());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/visualizaciones")
    public ResponseEntity<Void> anadirVisualizacion(@PathVariable String id) throws Exception {
        this.servicioProductos.anadirVisualizacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial/{mes}/{anio}")
    public PagedModel<EntityModel<ProductoResumen>> getHistorialMes(
            @PathVariable int mes, @PathVariable int anio, Pageable paginacion) throws Exception {

        Page<ProductoResumen> resultado = this.servicioProductos.getHistorialMesPaginado(mes, anio, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, productoResumenAssembler);
    }

    @GetMapping
    public PagedModel<EntityModel<ProductoResumen>> buscarProductos(
            @RequestParam(required = false) String categoriaId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoProducto estadoMinimo,
            @RequestParam(required = false) Double precioMaximo,
            Pageable paginacion)
            throws Exception {

        Page<ProductoResumen> resultado =
                this.servicioProductos.buscarPaginado(categoriaId, texto, estadoMinimo, precioMaximo, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, productoResumenAssembler);
    }
}
