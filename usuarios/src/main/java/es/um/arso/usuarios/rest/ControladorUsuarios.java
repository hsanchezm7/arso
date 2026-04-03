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

@Path("/usuarios")
public class ControladorUsuarios {

    private IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Context private UriInfo uriInfo;

    @Context private HttpServletRequest servletRequest;

    // POST /usuarios
    @POST
    @PermitAll
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response crear(UsuarioCreateDto dto) throws RepositorioException {

        String id =
                servicio.alta(
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response crearOauth(UsuarioGithubCreateDto dto)
            throws RepositorioException, EntidadNoEncontrada {

        if (dto == null || dto.getEmail() == null || dto.getGithubId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO: copmletar TODO del servicio de alta, para ver si se el email ya está en uso y por
        // tanto solo habría que actualizar el usuario con su nuevo githubId. También para comprobar
        // si ya está registrado.
        String id = servicio.altaOauth(dto.getNombre(), dto.getEmail(), dto.getGithubId());

        Usuario usuario = servicio.recuperar(id);

        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        String nombreCompleto = (nombre + " " + apellidos).trim();
        if (nombreCompleto.isEmpty()) {
            nombreCompleto = usuario.getEmail();
        }

        String roles = usuario.isAdministrador() ? "ADMINISTRADOR" : "USUARIO";
        UsuarioAuthDto dtoRespuesta = new UsuarioAuthDto(usuario.getId(), nombreCompleto, roles);

        return Response.status(Response.Status.CREATED).entity(dtoRespuesta).build();
    }

    // GET /usuarios/{id}
    @GET
    @Path("/{id}")
    @RolesAllowed("USUARIO")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUsuario(@PathParam("id") String id)
            throws RepositorioException, EntidadNoEncontrada {

        Usuario usuario = servicio.recuperar(id);
        UsuarioDto dto = toUsuarioDTO(usuario);
        return Response.status(Response.Status.OK).entity(dto).build();
    }

    // GET /usuarios/{id}/nombre (tarea 6)
    @GET
    @Path("/{id}/nombre")
    @PermitAll
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getNombreUsuario(@PathParam("id") String id)
            throws RepositorioException, EntidadNoEncontrada {

        Usuario usuario = servicio.recuperar(id);
        UsuarioNombreDto dto = new UsuarioNombreDto(usuario.getId(), usuario.getNombre());
        return Response.status(Response.Status.OK).entity(dto).build();
    }

    // POST /usuarios/verificar
    @POST
    @Path("/verificar")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificarCredenciales(VerificarCredencialesDto dto)
            throws RepositorioException {

        if (dto == null || dto.getUsername() == null || dto.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Solicitud inválida")
                    .build();
        }

        Usuario usuario = servicio.autenticar(dto.getUsername(), dto.getPassword());
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales inválidas")
                    .build();
        }

        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "";
        String apellidos = usuario.getApellidos() != null ? usuario.getApellidos() : "";
        String nombreCompleto = (nombre + " " + apellidos).trim();
        if (nombreCompleto.isEmpty()) {
            nombreCompleto = usuario.getEmail();
        }

        String roles = usuario.isAdministrador() ? "ADMINISTRADOR" : "USUARIO";
        UsuarioAuthDto dtoRespuesta = new UsuarioAuthDto(usuario.getId(), nombreCompleto, roles);

        return Response.status(Response.Status.OK).entity(dtoRespuesta).build();
    }

    // GET /usuarios/buscar?email=pepe@um.es&githubId=12345
    @GET
    @Path("/buscar")
    @PermitAll
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response buscarUsuario(
            @QueryParam("email") String email, @QueryParam("githubId") String githubId)
            throws RepositorioException {

        Usuario usuario = null;

        if (githubId != null && !githubId.trim().isEmpty()) {
            usuario = servicio.recuperarPorGithubId(githubId);
        }

        if (usuario == null && email != null && !email.trim().isEmpty()) {
            usuario = servicio.recuperarPorEmail(email);
        }

        if (usuario != null) {
            UsuarioDto dto = toUsuarioDTO(usuario);
            return Response.status(Response.Status.OK).entity(dto).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // PUT /usuarios/{id}
    @PUT
    @Path("/{id}")
    @RolesAllowed("USUARIO")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response modificar(@PathParam("id") String id, UsuarioUpdateDto dto)
            throws RepositorioException, EntidadNoEncontrada {

        Claims claims = (Claims) servletRequest.getAttribute("claims");
        if (claims == null || claims.getSubject() == null || !id.equals(claims.getSubject())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("solo puede modificar sus propios datos")
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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getListadoUsuarios() throws RepositorioException {

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

        return dto;
    }
}
