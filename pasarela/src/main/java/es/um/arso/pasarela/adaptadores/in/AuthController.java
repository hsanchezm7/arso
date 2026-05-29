package es.um.arso.pasarela.adaptadores.in;

import es.um.arso.pasarela.adaptadores.in.dto.LoginRequestDto;
import es.um.arso.pasarela.adaptadores.in.dto.LoginResponseDto;
import es.um.arso.pasarela.servicio.AuthService;
import es.um.arso.pasarela.servicio.JwtService;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import es.um.arso.pasarela.utils.JwtCookieUtils;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final JwtCookieUtils jwtCookieUtils;

    public AuthController(AuthService authService, JwtService jwtService, JwtCookieUtils jwtCookieUtils) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.jwtCookieUtils = jwtCookieUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        log.info(
                "POST /auth/login recibido usernamePresent={}",
                request != null
                        && request.getUsername() != null
                        && !request.getUsername().trim().isEmpty());

        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            log.warn("POST /auth/login solicitud invalida");
            return ResponseEntity.badRequest().build();
        }

        UsuarioAuthInfo usuario = authService.autenticar(request.getUsername(), request.getPassword());
        if (usuario == null) {
            log.info("POST /auth/login credenciales invalidas username={}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        log.info("POST /auth/login correcto id={} roles={}", usuario.getId(), usuario.getRoles());

        String token = jwtService.generateToken(usuario.getId(), usuario.getRoles());
        jwtCookieUtils.addJwtCookie(response, token);

        LoginResponseDto resultado = new LoginResponseDto();
        resultado.setIdentificadorUsuario(usuario.getId());
        resultado.setNombreCompleto(usuario.getNombreCompleto());
        resultado.setRoles(usuario.getRoles());

        return ResponseEntity.ok(resultado);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        log.info("POST /auth/logout recibido");
        jwtCookieUtils.clearJwtCookie(response);

        return ResponseEntity.noContent().build();
    }
}
