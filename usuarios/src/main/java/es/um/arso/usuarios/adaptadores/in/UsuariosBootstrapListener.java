package es.um.arso.usuarios.adaptadores.in;

import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.servicio.IServicioUsuarios;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class UsuariosBootstrapListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(UsuariosBootstrapListener.class);
    private static final String ADMIN_EMAIL = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    private final IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Usuario existente = servicio.recuperarPorEmail(ADMIN_EMAIL);
            if (existente == null) {
                log.info("Admin user not found; creating default admin");
                String id = servicio.alta("Admin", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD, null, null);
                log.info("Admin user created id={}", id);
            } else {
                log.info("Admin user exists id={}", existente.getId());
            }
        } catch (Exception e) {
            log.error("Failed to initialize admin user", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
