package es.um.arso.usuarios.rest.auth;

import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtTokenFilter implements ContainerRequestFilter {

    @Context 
    private ResourceInfo resourceInfo;

    @Context 
    private HttpServletRequest servletRequest;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (resourceInfo != null
                && resourceInfo.getResourceMethod() != null
                && resourceInfo.getResourceMethod().isAnnotationPresent(PermitAll.class)) {
            return;
        }

        // TODO: definir rutas públicas? Redundante? Ya comprobamos PermitAll

        String authorization = requestContext.getHeaderString("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("No se adjunta el token correctamente")
                            .build());
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();

        try {
            Claims claims = JwtUtils.validateToken(token);
            servletRequest.setAttribute("claims", claims);

            if (resourceInfo != null
                    && resourceInfo.getResourceMethod() != null
                    && resourceInfo.getResourceMethod().isAnnotationPresent(RolesAllowed.class)) {
                String[] allowedRoles =
                        resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class).value();
                Set<String> roles =
                        new HashSet<>(
                                Arrays.asList(claims.get("roles", String.class).split(",")));

                if (roles.stream().noneMatch(userRole -> Arrays.asList(allowedRoles).contains(userRole))) {
                    requestContext.abortWith(
                            Response.status(Response.Status.FORBIDDEN)
                                    .entity("no tiene rol de acceso")
                                    .build());
                }
            }
        } catch (Exception e) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build());
        }
    }
}
