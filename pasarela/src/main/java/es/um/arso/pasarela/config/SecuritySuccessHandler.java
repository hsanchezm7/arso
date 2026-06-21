package es.um.arso.pasarela.config;

import es.um.arso.pasarela.servicio.JwtService;
import es.um.arso.pasarela.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import es.um.arso.pasarela.utils.JwtUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SecuritySuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(SecuritySuccessHandler.class);

    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final IServicioUsuariosExterno servicioUsuarios;
    private final String frontendRedirectUrl;

    public SecuritySuccessHandler(
            JwtService jwtService,
            JwtUtils jwtUtils,
            IServicioUsuariosExterno servicioUsuarios,
            @Value("${frontend.oauth2.redirect-url}") String frontendRedirectUrl) {
        this.jwtService = jwtService;
        this.jwtUtils = jwtUtils;
        this.servicioUsuarios = servicioUsuarios;
        this.frontendRedirectUrl = frontendRedirectUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        log.info("OAuth2 login success handler invoked");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof DefaultOAuth2User)) {
            log.warn(
                    "OAuth2 principal is not DefaultOAuth2User: {}",
                    principal != null ? principal.getClass().getName() : "null");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        DefaultOAuth2User usuarioDefault = (DefaultOAuth2User) principal;
        log.info("OAuth2 principal resolved; fetching/creating local user");

        UsuarioAuthInfo usuario = fetchUserInfo(usuarioDefault);
        if (usuario == null) {
            log.warn("OAuth2 user info unavailable; cannot issue JWT");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String accessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getRoles());
        String refreshToken = jwtService.generateRefreshToken(usuario.getId());

        log.info("JWT issued for oauth user id={}", usuario.getId());

        jwtUtils.addRefreshTokenCookie(response, refreshToken);

        String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
                .queryParam("token", accessToken)
                .build().toUriString();

        log.info("Redirecting to frontend: {}", targetUrl);
        response.sendRedirect(targetUrl);
    }

    private UsuarioAuthInfo fetchUserInfo(DefaultOAuth2User usuario) {
        String githubId = attributeValue(usuario, "id");
        String login = attributeValue(usuario, "login");
        String name = attributeValue(usuario, "name");
        String email = attributeValue(usuario, "email");

        log.info(
                "OAuth2 attributes received: githubId={}, login={}, emailPresent={}", githubId, login, !isBlank(email));

        if (isBlank(githubId)) {
            log.warn("OAuth2 user missing id attribute");
            return null;
        }

        String nombreOAuth = firstNonBlank(name, login, email);

        log.info("Calling usuarios service to locate OAuth user");
        UsuarioAuthInfo existente = servicioUsuarios.buscarUsuario(githubId, email);

        if (existente != null) {
            log.info("OAuth user found in usuarios service id={}", existente.getId());

            UsuarioAuthInfo usuariolocal = new UsuarioAuthInfo();
            usuariolocal.setId(existente.getId());
            usuariolocal.setNombre(existente.getNombre() != null ? existente.getNombre() : "");
            usuariolocal.setRoles(existente.getRoles());

            return usuariolocal;
        }

        if (isBlank(email)) {
            log.warn("OAuth2 user missing email; cannot create user");
            return null;
        }

        String nombreCrear = firstNonBlank(nombreOAuth, email);
        log.info(
                "OAuth user not found; creating in usuarios service githubId={}, emailPresent={}",
                githubId,
                !isBlank(email));
        UsuarioAuthInfo creado = servicioUsuarios.crearUsuarioOauth(nombreCrear, email, githubId);
        if (creado == null || isBlank(creado.getId())) {
            log.warn("OAuth2 user creation failed");
            return null;
        }

        log.info("OAuth user created in usuarios service id={}", creado.getId());

        UsuarioAuthInfo usuariolocal = new UsuarioAuthInfo();
        usuariolocal.setId(creado.getId());
        usuariolocal.setNombre(creado.getNombre());
        usuariolocal.setRoles(creado.getRoles());

        return usuariolocal;
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
