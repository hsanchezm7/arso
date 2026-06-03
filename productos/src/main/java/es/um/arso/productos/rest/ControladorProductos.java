package es.um.arso.productos.rest;

import es.um.arso.productos.modelo.EstadoProducto;
import es.um.arso.productos.modelo.Producto;
import es.um.arso.productos.rest.dto.CategoriaDto;
import es.um.arso.productos.rest.dto.LugarRecogidaDto;
import es.um.arso.productos.rest.dto.ModificarProductoDto;
import es.um.arso.productos.rest.dto.NuevoProductoDto;
import es.um.arso.productos.rest.dto.ProductoDto;
import es.um.arso.productos.servicio.IServicioCategorias;
import es.um.arso.productos.servicio.IServicioProductos;
import es.um.arso.productos.servicio.ProductoResumen;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/productos")
public class ControladorProductos {

    private static final Logger log = LoggerFactory.getLogger(ControladorProductos.class);

    private final IServicioProductos servicioProductos;

    private final IServicioCategorias servicioCategorias;

    private final PagedResourcesAssembler<ProductoResumen> pagedResourcesAssembler;

    private final ProductoResumenAssembler productoResumenAssembler;

    public ControladorProductos(
            IServicioProductos servicioProductos,
            PagedResourcesAssembler<ProductoResumen> pagedResourcesAssembler,
            ProductoResumenAssembler productoResumenAssembler,
            IServicioCategorias servicioCategorias) {
        this.servicioProductos = servicioProductos;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.productoResumenAssembler = productoResumenAssembler;
        this.servicioCategorias = servicioCategorias;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USUARIO')")
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente. URL en la cabecera Location.")
    public ResponseEntity<Void> crearProducto(@Valid @RequestBody NuevoProductoDto nuevoProducto, Principal principal)
            throws Exception {

        log.info(
                "POST /productos titulo={}, descripcion={}, precio={}, estado={}, categoriaId={}, envioDisponible={}, vendedorId={}",
                nuevoProducto.getTitulo(),
                nuevoProducto.getDescripcion(),
                nuevoProducto.getPrecio(),
                nuevoProducto.getEstado(),
                nuevoProducto.getCategoriaId(),
                nuevoProducto.isEnvioDisponible(),
                principal.getName());

        String id = this.servicioProductos.crear(
                nuevoProducto.getTitulo(),
                nuevoProducto.getDescripcion(),
                nuevoProducto.getPrecio(),
                nuevoProducto.getEstado(),
                nuevoProducto.getCategoriaId(),
                nuevoProducto.isEnvioDisponible(),
                principal.getName());

        URI nuevaURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(nuevaURL).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Obtiene un producto por su id.")
    public EntityModel<ProductoDto> getProductoById(@PathVariable String id) throws Exception {

        log.info("GET /productos/{}", id);

        Producto producto = this.servicioProductos.getProducto(id);
        ProductoDto productoDto = ProductoDto.fromEntity(producto);

        EntityModel<ProductoDto> model = EntityModel.of(productoDto);
        model.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(ControladorProductos.class).getProductoById(id))
                .withSelfRel());

        return model;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USUARIO')")
    @Operation(summary = "Modificar producto", description = "Modifica un producto existente.")
    @ApiResponse(responseCode = "204", description = "Producto modificado exitosamente.")
    public ResponseEntity<Void> modificarProducto(
            @PathVariable String id, @Valid @RequestBody ModificarProductoDto modificacion, Principal principal)
            throws Exception {

        log.info(
                "PUT /productos/{} precio={}, descripcion={}, disponibilidad={}",
                id,
                modificacion.getPrecio(),
                modificacion.getDescripcion(),
                modificacion.isDisponibilidad());

        this.servicioProductos.modificar(
                id,
                modificacion.getPrecio(),
                modificacion.getDescripcion(),
                modificacion.isDisponibilidad(),
                principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/recogida")
    @PreAuthorize("hasAuthority('USUARIO')")
    @Operation(summary = "Asignar recogida", description = "Establece o actualiza el lugar de recogida de un producto.")
    @ApiResponse(responseCode = "204", description = "Lugar de recogida actualizado exitosamente.")
    public ResponseEntity<Void> asignarLugarRecogida(
            @PathVariable String id, @Valid @RequestBody LugarRecogidaDto recogida, Principal principal)
            throws Exception {

        log.info(
                "PUT /productos/{}/recogida descripcion={}, longitud={}, latitud={}",
                id,
                recogida.getDescripcion(),
                recogida.getLongitud(),
                recogida.getLatitud());

        this.servicioProductos.asignarLugarRecogida(
                id, recogida.getDescripcion(), recogida.getLongitud(), recogida.getLatitud(), principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/visualizaciones")
    @ApiResponse(responseCode = "204", description = "Incremento de visualizaciones exitoso.")
    @Operation(
            summary = "Añadir visualización",
            description = "Incrementa el contador de visualizaciones de un producto específico.")
    public ResponseEntity<Void> anadirVisualizacion(@PathVariable String id) throws Exception {
        log.info("POST /productos/{}/visualizaciones", id);
        this.servicioProductos.anadirVisualizacion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/historial/{mes}/{anio}")
    @Operation(
            summary = "Historial del mes",
            description =
                    "Obtiene un listado paginado con el resumen de los productos correspondientes a un mes y año concretos/")
    public PagedModel<EntityModel<ProductoResumen>> getHistorialMes(
            @PathVariable int mes, @PathVariable int anio, Pageable paginacion) throws Exception {

        log.info("GET /productos/historial/{}/{}", mes, anio);

        Page<ProductoResumen> resultado = this.servicioProductos.getHistorialMesPaginado(mes, anio, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, productoResumenAssembler);
    }

    @GetMapping
    @Operation(
            summary = "Buscar productos",
            description = "Realiza una búsqueda paginada de productos a la venta. Admite filtros/")
    public PagedModel<EntityModel<ProductoResumen>> buscarProductos(
            @RequestParam(required = false) String categoriaId,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoProducto estadoMinimo,
            @RequestParam(required = false) Double precioMinimo,
            @RequestParam(required = false) Double precioMaximo,
            @RequestParam(required = false) String idVendedor,
            Pageable paginacion)
            throws Exception {

        log.info(
                "GET /productos categoriaId={}, texto={}, estadoMinimo={}, precioMinimo={}, precioMaximo={}, idVendedor={}",
                categoriaId,
                texto,
                estadoMinimo,
                precioMinimo,
                precioMaximo,
                idVendedor);

        Page<ProductoResumen> resultado = this.servicioProductos.buscarPaginado(
                categoriaId, texto, estadoMinimo, precioMinimo, precioMaximo, idVendedor, paginacion);

        return this.pagedResourcesAssembler.toModel(resultado, productoResumenAssembler);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Categorías", description = "Obtiene todas las categorías de productos")
    public List<CategoriaDto> getCategorias() {
        log.info("GET /productos/categorias");

        return servicioCategorias.getCategorias();
    }

    @GetMapping("/categorias/raices")
    @Operation(summary = "Categorías raíz", description = "Obtiene las categorías de nivel raíz")
    public List<CategoriaDto> getCategoriasRaiz() {
        log.info("GET /productos/categorias/raices");

        return servicioCategorias.getRaices();
    }

    @GetMapping("/categorias/{categoriaId}/descendientes")
    @Operation(
            summary = "Descendientes de una categoría",
            description = "Obtiene todos los descendientes de la categoría indicada por su id")
    public List<CategoriaDto> getDescendientes(@PathVariable String categoriaId) throws Exception {
        log.info("GET /productos/categorias/{}/descendientes", categoriaId);

        return servicioCategorias.getDescendientes(categoriaId);
    }

    @GetMapping("/estados")
    @Operation(summary = "Estados de un producto", description = "Obtiene los posibles estados de un producto")
    public Map<EstadoProducto, String> getEstadosProducto() {
        log.info("GET /productos/estados");

        List<EstadoProducto> estados = servicioProductos.getEstadosProducto();

        Map<EstadoProducto, String> estadoValor = estados.stream()
                .collect(Collectors.toMap(e -> e, EstadoProducto::getValor, (e1, e2) -> e1, LinkedHashMap::new));

        return estadoValor;
    }
}
