package es.um.arso.usuarios.rest.auth;

import io.jsonwebtoken.Claims;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.Cookie;
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

    private static final String JWT_COOKIE_NAME = "jwt";

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
        String token = extractTokenFromCookies();
        if (token == null || token.trim().isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No se adjunta el token en la cookie")
                    .build());
            return;
        }

        try {
            Claims claims = JwtUtils.parseClaimsUnverified(token);
            servletRequest.setAttribute("claims", claims);

            if (resourceInfo != null
                    && resourceInfo.getResourceMethod() != null
                    && resourceInfo.getResourceMethod().isAnnotationPresent(RolesAllowed.class)) {
                String[] allowedRoles = resourceInfo
                        .getResourceMethod()
                        .getAnnotation(RolesAllowed.class)
                        .value();
                String rolesClaim = claims.get("roles", String.class);
                if (rolesClaim == null || rolesClaim.trim().isEmpty()) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("no tiene rol de acceso")
                            .build());
                    return;
                }

                Set<String> roles = new HashSet<>(Arrays.asList(rolesClaim.split(",")));

                if (roles.stream()
                        .noneMatch(userRole -> Arrays.asList(allowedRoles).contains(userRole))) {
                    requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
                            .entity("no tiene rol de acceso")
                            .build());
                }
            }
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(e.getMessage())
                    .build());
        }
    }

    private String extractTokenFromCookies() {
        if (servletRequest == null) {
            return null;
        }

        Cookie[] cookies = servletRequest.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
