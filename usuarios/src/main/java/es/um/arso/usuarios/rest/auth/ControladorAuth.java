package es.um.arso.usuarios.rest.auth;

import es.um.arso.repositorio.RepositorioException;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.servicio.IServicioUsuarios;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/auth")
public class ControladorAuth {

    private final IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@FormParam("username") String username, @FormParam("password") String password)
            throws RepositorioException {

        Map<String, Object> claims = verificarCredenciales(username, password);
        if (claims.isEmpty()) {
            // TODO: Crear custom exception INVALID_CREDENTIALS
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciales invalidas")
                    .build();
        }

        String token = JwtUtils.generateToken(claims);
        return Response.ok(token).build();
    }

    private Map<String, Object> verificarCredenciales(String username, String password)
            throws RepositorioException {

        Usuario usuario = servicio.autenticar(username, password);
        if (usuario == null) {
            // en bookle, se retorna null. es mejor devolver un mapa
            // vacío. además, emptyMap() es inmutable.
            return Collections.emptyMap();
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", usuario.getId()); // TODO: ¿usar id o email?
        claims.put("roles", "USUARIO");

        return claims;
    }
}
