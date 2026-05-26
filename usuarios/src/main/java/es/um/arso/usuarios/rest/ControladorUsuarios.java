package es.um.arso.usuarios.rest;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.rest.Listado.ResumenExtendido;
import es.um.arso.usuarios.rest.dto.UsuarioAuthDto;
import es.um.arso.usuarios.rest.dto.UsuarioCreateDto;
import es.um.arso.usuarios.rest.dto.UsuarioDto;
import es.um.arso.usuarios.rest.dto.UsuarioGithubCreateDto;
import es.um.arso.usuarios.rest.dto.UsuarioNombreDto;
import es.um.arso.usuarios.rest.dto.UsuarioUpdateDto;
import es.um.arso.usuarios.rest.dto.VerificarCredencialesDto;
import es.um.arso.usuarios.servicio.IServicioUsuarios;
import es.um.arso.usuarios.servicio.UsuarioResumen;
import io.jsonwebtoken.Claims;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ControladorUsuarios {

    private static final Logger log = LoggerFactory.getLogger(ControladorUsuarios.class);

    private IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Context
    private UriInfo uriInfo;

    @Context
    private HttpServletRequest servletRequest;

    // POST /usuarios
    @POST
    @PermitAll
    public Response crear(UsuarioCreateDto dto) throws RepositorioException {

        log.info(
            "POST /usuarios recibido email={}, telefono={}",
            dto.getEmail(),
            dto.getTelefono());

        String id = servicio.alta(
                dto.getNombre(),
                dto.getApellidos(),
                dto.getEmail(),
                dto.getClave(),
                dto.getFechaNacimiento(),
                dto.getTelefono());

        URI nuevaURL = uriInfo.getAbsolutePathBuilder().path(id).build();
        return Response.created(nuevaURL).build();
    }

    // POST /usuarios/oauth
    @POST
    @Path("/oauth")
    @PermitAll
    public Response crearOauth(UsuarioGithubCreateDto dto) throws RepositorioException, EntidadNoEncontrada {

        log.info(
            "POST /usuarios/oauth recibido githubId={}, email={}",
            dto.getGithubId(),
            dto.getEmail());

        if (dto == null || dto.getEmail() == null || dto.getGithubId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: copmletar TODO del servicio de alta, para ver si se el email ya está en
        // uso y por
        // tanto solo habría que actualizar el usuario con su nuevo githubId. También
        // para comprobar
        // si ya está registrado.
        String id = servicio.altaOauth(dto.getNombre(), dto.getEmail(), dto.getGithubId());

        log.info("Usuario OAuth creado en servicio: id={}", id);

        Usuario usuario = servicio.recuperar(id);

        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        String nombreCompleto = (nombre + " " + apellidos).trim();
        if (nombreCompleto.isEmpty()) {
            nombreCompleto = usuario.getEmail();
        }

        String roles = usuario.isAdministrador() ? "USUARIO,ADMINISTRADOR" : "USUARIO";
        UsuarioAuthDto dtoRespuesta = new UsuarioAuthDto(usuario.getId(), nombreCompleto, roles);

        log.info("Respuesta OAuth preparada: id={} roles={}", usuario.getId(), roles);

        return Response.status(Response.Status.CREATED).entity(dtoRespuesta).build();
    }

    // GET /usuarios/{id}
    @GET
    @Path("/{id}")
    @RolesAllowed("USUARIO")
    public Response getUsuario(@PathParam("id") String id) throws RepositorioException, EntidadNoEncontrada {

        log.info("GET /usuarios/{} recibido", id);

        Usuario usuario = servicio.recuperar(id);
        UsuarioDto dto = toUsuarioDTO(usuario);
        return Response.status(Response.Status.OK).entity(dto).build();
    }

    // GET /usuarios/{id}/nombre (tarea 6)
    @GET
    @Path("/{id}/nombre")
    @PermitAll
    public Response getNombreUsuario(@PathParam("id") String id) throws RepositorioException, EntidadNoEncontrada {

        log.info("GET /usuarios/{}/nombre recibido", id);

        Usuario usuario = servicio.recuperar(id);
        UsuarioNombreDto dto = new UsuarioNombreDto(usuario.getId(), usuario.getNombre());
        return Response.status(Response.Status.OK).entity(dto).build();
    }

    // POST /usuarios/verificar
    @POST
    @Path("/verificar")
    @PermitAll
    public Response verificarCredenciales(VerificarCredencialesDto dto) throws RepositorioException {
        log.info(
            "POST /usuarios/verificar recibido username={}, password={}",
            dto.getUsername(),
            dto.getPassword());

        if (dto == null || dto.getUsername() == null || dto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Solicitud inválida")
                    .build();
        }

        Usuario usuario = servicio.autenticar(dto.getUsername(), dto.getPassword());
        if (usuario == null) {
            log.info("Credenciales invalidas para username={}", dto.getUsername());
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        log.info("Credenciales validas para id={}", usuario.getId());

        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        String nombreCompleto = (nombre + " " + apellidos).trim();
        if (nombreCompleto.isEmpty()) {
            nombreCompleto = usuario.getEmail();
        }

        String roles = usuario.isAdministrador() ? "USUARIO,ADMINISTRADOR" : "USUARIO";
        UsuarioAuthDto dtoRespuesta = new UsuarioAuthDto(usuario.getId(), nombreCompleto, roles);

        return Response.status(Response.Status.OK).entity(dtoRespuesta).build();
    }

    // GET /usuarios/buscar?email=pepe@um.es&githubId=12345
    @GET
    @Path("/buscar")
    @PermitAll
    public Response buscarUsuario(@QueryParam("email") String email, @QueryParam("githubId") String githubId)
            throws RepositorioException {

        log.info(
            "GET /usuarios/buscar recibido githubId={}, email={}",
            githubId,
            email);

        Usuario usuario = null;

        if (githubId != null && !githubId.trim().isEmpty()) {
            log.info("Buscando usuario por githubId");
            usuario = servicio.recuperarPorGithubId(githubId);
        }

        if (usuario == null && email != null && !email.trim().isEmpty()) {
            log.info("Buscando usuario por email");
            usuario = servicio.recuperarPorEmail(email);
        }

        if (usuario != null) {
            log.info("Usuario encontrado id={}", usuario.getId());
            UsuarioDto dto = toUsuarioDTO(usuario);
            return Response.status(Response.Status.OK).entity(dto).build();
        } else {
            log.info("Usuario no encontrado");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // PUT /usuarios/{id}
    @PUT
    @Path("/{id}")
    @RolesAllowed("USUARIO")
    public Response modificar(@PathParam("id") String id, UsuarioUpdateDto dto)
            throws RepositorioException, EntidadNoEncontrada {

        log.info("PUT /usuarios/{} recibido body={}", id, dto);

        Claims claims = (Claims) servletRequest.getAttribute("claims");
        String subject = claims != null ? claims.getSubject() : null;
        log.info(
            "PUT /usuarios/{} claims={}, subject={}",
            id,
            claims,
            subject);

        if (claims == null || subject == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Solo puede modificar sus propios datos")
                    .build();
        }

        log.info("PUT /usuarios/{} subject={}, subjectMatchId={}", id, subject, subject.equals(id));

        if (!subject.equals(id)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Solo puede modificar sus propios datos")
                    .build();
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setClave(dto.getClave());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        usuario.setTelefono(dto.getTelefono());

        servicio.modificar(id, usuario);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    // GET /usuarios
    @GET
    @RolesAllowed("USUARIO")
    public Response getListadoUsuarios() throws RepositorioException {

        log.info("GET /usuarios recibido");

        List<UsuarioResumen> resultado = servicio.recuperarTodos();

        LinkedList<ResumenExtendido> extendido = new LinkedList<>();

        for (UsuarioResumen u : resultado) {
            ResumenExtendido resumenExt = new ResumenExtendido();
            resumenExt.setResumen(u);

            // contruir URL al recurso individual
            String id = u.getId();
            URI url = this.uriInfo.getAbsolutePathBuilder().path(id).build();
            resumenExt.setUrl(url.toString());

            extendido.add(resumenExt);
        }

        Listado listado = new Listado();
        listado.setUsuario(extendido);

        return Response.status(Response.Status.OK).entity(listado).build();
    }

    private UsuarioDto toUsuarioDTO(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellidos(u.getApellidos());
        dto.setEmail(u.getEmail());
        dto.setFechaNacimiento(u.getFechaNacimiento());
        dto.setTelefono(u.getTelefono());
        dto.setRoles(u.isAdministrador() ? "USUARIO,ADMINISTRADOR" : "USUARIO");

        return dto;
    }
}
