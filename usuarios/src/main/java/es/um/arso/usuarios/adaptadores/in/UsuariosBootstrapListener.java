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

    private static final String ADMIN_EMAIL = "admin@arso.es";
    private static final String ADMIN_PASSWORD = "admin";

    private final IServicioUsuarios servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Usuario existente = servicio.recuperarPorEmail(ADMIN_EMAIL);
            if (existente == null) {
                log.info("Admin no encontrado; creando administrador inicial");
                String id = servicio.alta("Admin", "Admin", ADMIN_EMAIL, ADMIN_PASSWORD, null, null);
                log.info("Admin creado id={}", id);
                Usuario admin = new Usuario();
                admin.setAdministrador(true);
                admin.setNumeroCompras(0);
                admin.setNumeroVentas(0);
                servicio.modificar(id, admin);
                log.info("Admin marcado como administrador id={}", id);
            } else {
                if (!existente.isAdministrador()) {
                    Usuario admin = new Usuario();
                    admin.setAdministrador(true);
                    admin.setNumeroCompras(existente.getNumeroCompras());
                    admin.setNumeroVentas(existente.getNumeroVentas());
                    servicio.modificar(existente.getId(), admin);
                    log.info("Admin actualizado como administrador id={}", existente.getId());
                } else {
                    log.info("Admin ya existe id={}", existente.getId());
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize admin user", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
