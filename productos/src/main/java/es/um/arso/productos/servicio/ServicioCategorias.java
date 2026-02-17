package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.servicio.xml.CategoriaXML;
import es.um.arso.repositorio.EntidadNoEncontrada;
import es.um.arso.repositorio.FactoriaRepositorios;
import es.um.arso.repositorio.Repositorio;
import es.um.arso.repositorio.RepositorioException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServicioCategorias implements IServicioCategorias {

    private static final Logger log = LoggerFactory.getLogger(ServicioCategorias.class);

    private Repositorio<Categoria, String> repoCategorias =
            FactoriaRepositorios.getRepositorio(Categoria.class);

    @Override
    public void cargarJerarquia(String rutaXml) throws RepositorioException {
        try {
            JAXBContext ctx = JAXBContext.newInstance(CategoriaXML.class);
            Unmarshaller u = ctx.createUnmarshaller();
            CategoriaXML raizXml = (CategoriaXML) u.unmarshal(new File(rutaXml));

            // Evitar duplicar raíz: comprobación por nombre
            boolean existe =
                    repoCategorias.getAll().stream()
                            .anyMatch(c -> c.getNombre().equalsIgnoreCase(raizXml.getNombre()));
            if (existe) return; // no cargar si existe

            Categoria raiz = convertir(raizXml);
            repoCategorias.add(raiz);
        } catch (Exception e) {
            throw new RepositorioException("Error cargando jerarquia categorias", e);
        }
    }

    /**
     * Carga todas las jerarquías de categorías desde los ficheros .xml de un directorio. Ignora
     * (log) errores individuales y continúa con el resto.
     *
     * @param directorio Ruta del directorio (relativa o absoluta)
     */
    public void cargarTodas(String directorio) throws RepositorioException {
        File dir = new File(directorio);
        if (!dir.exists() || !dir.isDirectory())
            throw new RepositorioException("Directorio no válido: " + directorio);
        File[] archivos =
                dir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".xml"));
        if (archivos == null) return;
        for (File f : archivos) {
            try {
                cargarJerarquia(f.getPath());
                log.info("Importada jerarquía desde {}", f.getName());
            } catch (RepositorioException e) {
                log.warn("Fallo importando {}: {}", f.getName(), e.getMessage());
            }
        }
    }

    private Categoria convertir(CategoriaXML cx) {
        Categoria c = new Categoria(cx.getNombre());
        c.setId(cx.getId());
        c.setDescripcion(cx.getDescripcion());
        c.setRuta(cx.getRuta());
        for (CategoriaXML hija : cx.getSubcategorias()) {
            c.addSubcategoria(convertir(hija));
        }
        return c;
    }

    @Override
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion)
            throws RepositorioException, EntidadNoEncontrada {
        Categoria c = repoCategorias.getById(categoriaId);
        c.setDescripcion(nuevaDescripcion);
        repoCategorias.update(c);
    }

    @Override
    public List<Categoria> getRaices() throws RepositorioException {
        List<Categoria> todas = repoCategorias.getAll();
        List<Categoria> raices = new LinkedList<>();
        for (Categoria c : todas) if (c.esRaiz()) raices.add(c);
        return raices;
    }

    @Override
    public List<Categoria> getDescendientes(String categoriaId)
            throws RepositorioException, EntidadNoEncontrada {
        Categoria c = repoCategorias.getById(categoriaId);
        return c.getDescendientes();
    }
}
