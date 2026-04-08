package es.um.arso.pasarela.servicio;

import es.um.arso.pasarela.servicio.puertos.out.IServicioUsuariosExterno;
import es.um.arso.pasarela.servicio.puertos.out.UsuarioAuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final IServicioUsuariosExterno servicioUsuariosExterno;

    public AuthService(IServicioUsuariosExterno servicioUsuariosExterno) {
        this.servicioUsuariosExterno = servicioUsuariosExterno;
    }

    public UsuarioAuthInfo autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username obligatorio");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password obligatorio");
        }

        log.info("Verificando credenciales en servicio usuarios username={}", username);
        UsuarioAuthInfo usuario = servicioUsuariosExterno.autenticarCredenciales(username, password);
        if (usuario == null) {
            log.info("Credenciales invalidas segun servicio usuarios username={}", username);
            return null;
        }

        log.info("Usuario autenticado id={} roles={}", usuario.getId(), usuario.getRoles());

        return usuario;
    }
}
