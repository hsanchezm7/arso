package es.um.arso.usuarios.servicio.test;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import es.um.arso.servicio.FactoriaServicios;
import es.um.arso.usuarios.modelo.Usuario;
import es.um.arso.usuarios.servicio.IServicioUsuarios;
import es.um.arso.utils.EntityManagerHelper;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramaUsuarios {
    private static final Logger log = LoggerFactory.getLogger(ProgramaUsuarios.class);

    public static void main(String[] args) {
        IServicioUsuarios servicio = null;
        try {
            // Obtener implementación del servicio de usuarios
            servicio = FactoriaServicios.getServicio(IServicioUsuarios.class);

            // Test 1: alta + modificación parcial (modificar sólo el nombre)
            String id1 =
                    servicio.alta(
                            "Ana",
                            "López",
                            "ana@um.es",
                            "secreta",
                            LocalDate.of(1995, 5, 21),
                            "600124226");

            Usuario mod1 = new Usuario();
            mod1.setNombre("Ana María");
            servicio.modificar(id1, mod1);

            // Test 2: alta + modificación de varios campos (apellidos, clave, fecha y
            // teléfono)
            String id2 =
                    servicio.alta(
                            "Luis",
                            "Martín",
                            "luis@example.com",
                            "clave123",
                            LocalDate.of(1990, 3, 10),
                            null);

            Usuario mod2 = new Usuario();
            mod2.setApellidos("García");
            mod2.setClave("nuevaClave456");
            mod2.setFechaNacimiento(LocalDate.of(1990, 4, 1));
            mod2.setTelefono("600998877");
            servicio.modificar(id2, mod2);

            log.info("Pruebas de usuarios completadas.");
        } catch (Exception e) {
            log.error("Error en ProgramaUsuarios", e);
        } finally {
            try {
                EntityManagerHelper.closeEntityManagerFactory();
            } catch (Exception ex) {
                log.warn("Error cerrando EntityManagerFactory: {}", ex.getMessage());
            }
            try {
                AbandonedConnectionCleanupThread.checkedShutdown();
            } catch (Exception ex) {
                log.warn("Error cerrando AbandonedConnectionCleanupThread: {}", ex.getMessage());
            }
        }
    }
}
