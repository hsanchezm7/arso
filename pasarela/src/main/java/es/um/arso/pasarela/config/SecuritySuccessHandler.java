package es.um.arso.pasarela.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.um.arso.pasarela.adaptadores.in.dto.LoginResponseDto;
import es.um.arso.pasarela.servicio.JwtService;
import es.um.arso.pasarela.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioBusquedaInfo;
import es.um.arso.pasarela.utils.JwtCookieUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class SecuritySuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(SecuritySuccessHandler.class);

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final JwtCookieUtils jwtCookieUtils;
    private final IServicioUsuariosExterno servicioUsuarios;

    public SecuritySuccessHandler(
            JwtService jwtService,
            ObjectMapper objectMapper,
            JwtCookieUtils jwtCookieUtils,
            IServicioUsuariosExterno servicioUsuarios) {
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
        this.jwtCookieUtils = jwtCookieUtils;
        this.servicioUsuarios = servicioUsuarios;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        log.info("OAuth2 login success handler invoked");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof DefaultOAuth2User)) {
            log.warn("OAuth2 principal is not DefaultOAuth2User: {}",
                    principal != null ? principal.getClass().getName() : "null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        DefaultOAuth2User usuario = (DefaultOAuth2User) principal;
        log.info("OAuth2 principal resolved; fetching/creating local user");
        Map<String, Object> claims = fetchUserInfo(usuario);
        if (claims == null) {
            log.warn("OAuth2 user info unavailable; cannot issue JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String idUsuario = stringValue(claims.get("id"));
        if (isBlank(idUsuario)) {
            log.warn("OAuth2 user resolved without id; cannot issue JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String roles = stringValue(claims.get("roles"));
        if (isBlank(roles)) {
            roles = "USUARIO";
        }

        String nombreCompleto = stringValue(claims.get("nombreCompleto"));
        if (isBlank(nombreCompleto)) {
            nombreCompleto = idUsuario;
        }

        String token = jwtService.generateToken(idUsuario, roles);
        jwtCookieUtils.addJwtCookie(response, token);

        log.info("JWT issued for oauth user id={}", idUsuario);

        LoginResponseDto resultado = new LoginResponseDto();
        resultado.setIdentificadorUsuario(idUsuario);
        resultado.setNombreCompleto(nombreCompleto);
        resultado.setRoles(roles);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getWriter(), resultado);
    }

    private Map<String, Object> fetchUserInfo(DefaultOAuth2User usuario) {
        String githubId = attributeValue(usuario, "id");
        String login = attributeValue(usuario, "login");
        String name = attributeValue(usuario, "name");
        String email = attributeValue(usuario, "email");

        log.info("OAuth2 attributes received: githubId={}, login={}, emailPresent={}",
                githubId, login, !isBlank(email));

        if (isBlank(githubId)) {
            log.warn("OAuth2 user missing id attribute");
            return null;
        }

        String nombreOAuth = firstNonBlank(name, login, email);

        log.info("Calling usuarios service to locate OAuth user");
        UsuarioBusquedaInfo existente = servicioUsuarios.buscarUsuario(githubId, email);
        if (existente != null && !isBlank(existente.getId())) {
            log.info("OAuth user found in usuarios service id={}", existente.getId());
            String nombreLocal = buildNombreCompleto(
                    existente.getNombre(), existente.getApellidos(), existente.getEmail());
            String nombreCompleto = firstNonBlank(nombreOAuth, nombreLocal, githubId);

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", existente.getId());
            claims.put("roles", "USUARIO");
            claims.put("nombreCompleto", nombreCompleto);
            return claims;
        }

        if (isBlank(email)) {
            log.warn("OAuth2 user missing email; cannot create user");
            return null;
        }

        String nombreCrear = firstNonBlank(nombreOAuth, email);
        log.info("OAuth user not found; creating in usuarios service githubId={}, emailPresent={}",
                githubId, !isBlank(email));
        UsuarioAuthInfo creado = servicioUsuarios.crearUsuarioOauth(nombreCrear, email, githubId);
        if (creado == null || isBlank(creado.getId())) {
            log.warn("OAuth2 user creation failed");
            return null;
        }

        log.info("OAuth user created in usuarios service id={}", creado.getId());

        String roles = !isBlank(creado.getRoles()) ? creado.getRoles() : "USUARIO";
        String nombreCompleto = firstNonBlank(creado.getNombreCompleto(), nombreCrear, email);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", creado.getId());
        claims.put("roles", roles);
        claims.put("nombreCompleto", nombreCompleto);
        return claims;
    }

    private String attributeValue(DefaultOAuth2User usuario, String key) {
        Object value = usuario.getAttribute(key);
        return value != null ? value.toString() : null;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String buildNombreCompleto(String nombre, String apellidos, String email) {
        StringBuilder builder = new StringBuilder();
        if (!isBlank(nombre)) {
            builder.append(nombre.trim());
        }
        if (!isBlank(apellidos)) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(apellidos.trim());
        }
        if (builder.length() == 0 && !isBlank(email)) {
            builder.append(email.trim());
        }
        return builder.length() == 0 ? null : builder.toString();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String stringValue(Object value) {
        return value != null ? value.toString() : null;
    }
}
