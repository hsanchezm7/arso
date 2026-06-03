package es.um.arso.pasarela.adaptadores.in;

import es.um.arso.pasarela.adaptadores.in.dto.LoginRequestDto;
import es.um.arso.pasarela.adaptadores.in.dto.LoginResponseDto;
import es.um.arso.pasarela.servicio.AuthService;
import es.um.arso.pasarela.servicio.JwtService;
import es.um.arso.pasarela.servicio.exception.UsuariosClientException;
import es.um.arso.pasarela.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import es.um.arso.pasarela.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtService jwtService;
    private final JwtUtils jwtUtils;
    private final IServicioUsuariosExterno servicioUsuariosExterno;

    public AuthController(
            AuthService authService,
            JwtService jwtService,
            JwtUtils jwtCookieUtils,
            IServicioUsuariosExterno servicioUsuariosExterno) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.jwtUtils = jwtCookieUtils;
        this.servicioUsuariosExterno = servicioUsuariosExterno;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        log.info("POST /auth/login recibido username={}", request.getUsername());

        UsuarioAuthInfo usuario = authService.autenticar(request.getUsername(), request.getPassword());
        if (usuario == null) {
            log.info("POST /auth/login credenciales invalidas username={}", request.getUsername());
            throw new BadCredentialsException("Credenciales inválidas");
        }

        log.info("POST /auth/login correcto id={} roles={}", usuario.getId(), usuario.getRoles());

        String accessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getRoles());
        String refreshToken = jwtService.generateRefreshToken(usuario.getId());

        jwtUtils.addRefreshTokenCookie(response, refreshToken);

        LoginResponseDto resultado = new LoginResponseDto();
        resultado.setAccessToken(accessToken);
        resultado.setUsuario(usuario);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        log.info("POST /auth/refresh refreshToken={}", refreshToken);

        if (refreshToken == null) {
            log.warn("POST /auth/refresh sin cookie de refresh token");
            throw new BadCredentialsException("Falta token de renovación");
        }

        Claims claims = jwtService.validateToken(refreshToken);

        String userId = claims.getSubject();

        UsuarioAuthInfo usuario;
        try {
            usuario = servicioUsuariosExterno.getUsuario(userId);
        } catch (UsuariosClientException e) {
            log.warn("POST /auth/refresh error al obtener usuario id={}: {}", userId, e.getMessage());
            throw new BadCredentialsException("Error validando el usuario");
        }

        if (usuario == null) {
            log.warn("POST /auth/refresh usuario no encontrado id={}", userId);
            throw new BadCredentialsException("Usuario no existe");
        }

        log.info("POST /auth/refresh correcto id={} roles={}", usuario.getId(), usuario.getRoles());

        String newAccessToken = jwtService.generateAccessToken(usuario.getId(), usuario.getRoles());

        LoginResponseDto resultado = new LoginResponseDto();
        resultado.setAccessToken(newAccessToken);
        resultado.setUsuario(usuario);

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        log.info("POST /auth/logout recibido");

        jwtUtils.clearRefreshTokenCookie(response);

        return ResponseEntity.noContent().build();
    }
}
