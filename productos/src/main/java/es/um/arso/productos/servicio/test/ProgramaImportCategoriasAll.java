package es.um.arso.productos.servicio.test;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import es.um.arso.productos.servicio.ServicioCategorias;
import es.um.arso.repositorio.RepositorioException;
import es.um.arso.utils.EntityManagerHelper;
import java.io.File;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Importa todas las categorías desde cada fichero XML del directorio indicado. Uso por defecto:
 * xml/
 */
public class ProgramaImportCategoriasAll {

    private static final Logger log = LoggerFactory.getLogger(ProgramaImportCategoriasAll.class);

    public static void main(String[] args) {
        String directorio = args.length > 0 ? args[0] : "xml";

        // Siempre resolver el directorio desde recursos del classpath
        URL res = Thread.currentThread().getContextClassLoader().getResource(directorio);
        if (res == null) {
            log.error("No se encontró el recurso de directorio '{}' en el classpath.", directorio);
            return;
        }

        File dirFile;
        try {
            dirFile = new File(res.toURI());
            log.info("Usando directorio de recursos en classpath: {}", dirFile.getAbsolutePath());
        } catch (Exception ex) {
            dirFile = new File(res.getPath());
            log.info(
                    "Usando directorio de recursos en classpath (sin URI): {}",
                    dirFile.getAbsolutePath());
        }

        ServicioCategorias sc = new ServicioCategorias();

        try {
            sc.cargarTodas(dirFile.getPath());
            log.info("Importación masiva completada.");
        } catch (RepositorioException e) {
            log.error("Error en importación masiva", e);
        }

        try {
            EntityManagerHelper.closeEntityManagerFactory();
        } catch (Exception t) {
            log.warn("Error cerrando EntityManagerFactory: {}", t.getMessage());
        }

        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            log.warn("Error cerrando AbandonedConnectionCleanupThread: {}", e.getMessage());
        }
    }
}
