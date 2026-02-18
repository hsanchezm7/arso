package es.um.arso.usuarios.rest;

import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.rest.dto.UsuarioCreateDto;
import es.um.arso.usuarios.rest.dto.UsuarioDto;
import es.um.arso.usuarios.rest.dto.UsuarioUpdateDto;
import es.um.arso.usuarios.rest.Listado.ResumenExtendido;
import es.um.arso.usuarios.servicio.IServicioUsuarios;
import es.um.arso.usuarios.servicio.UsuarioResumen;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/usuarios")
public class ControladorUsuarios {

    private IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Context 
    private UriInfo uriInfo;

    // POST /usuarios
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
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

    // GET /usuarios/{id}
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUsuario(@PathParam("id") String id)
            throws RepositorioException, EntidadNoEncontrada {

        Usuario usuario = servicio.recuperar(id);
        UsuarioDto dto = toUsuarioDTO(usuario);
        return Response.status(Response.Status.OK).entity(dto).build();
    }

    // PUT /usuarios/{id}
    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response modificar(@PathParam("id") String id, UsuarioUpdateDto dto)
            throws RepositorioException, EntidadNoEncontrada {

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
