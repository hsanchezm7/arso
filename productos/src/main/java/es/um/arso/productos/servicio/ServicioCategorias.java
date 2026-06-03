package es.um.arso.productos.servicio;

import es.um.arso.productos.modelo.Categoria;
import es.um.arso.productos.repositorio.RepositorioCategorias;
import es.um.arso.productos.rest.dto.CategoriaDto;
import es.um.arso.productos.servicio.xml.CategoriaXML;
import es.um.arso.repositorio.EntidadNoEncontrada;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServicioCategorias implements IServicioCategorias {

    private static final Logger log = LoggerFactory.getLogger(ServicioCategorias.class);

    @Autowired
    private RepositorioCategorias repositorioCategorias;

    @Override
    public void cargarJerarquia(String rutaXml) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(CategoriaXML.class);
            Unmarshaller u = ctx.createUnmarshaller();
            CategoriaXML raizXml = (CategoriaXML) u.unmarshal(new File(rutaXml));

            // Evitar duplicar raíz: comprobación por nombre
            boolean existe = StreamSupport.stream(
                            repositorioCategorias.findAll().spliterator(), false)
                    .anyMatch(c -> c.getNombre().equalsIgnoreCase(raizXml.getNombre()));
            if (existe) return;

            Categoria raiz = convertir(raizXml);
            repositorioCategorias.save(raiz);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando jerarquia categorias", e);
        }
    }

    /** Carga todas las jerarquías de categorías desde los ficheros .xml de un directorio. */
    public void cargarTodas(String directorio) {
        File dir = new File(directorio);
        if (!dir.exists() || !dir.isDirectory()) throw new RuntimeException("Directorio no válido: " + directorio);
        File[] archivos =
                dir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".xml"));
        if (archivos == null) return;
        for (File f : archivos) {
            try {
                cargarJerarquia(f.getPath());
                log.info("Importada jerarquía desde {}", f.getName());
            } catch (Exception e) {
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
    public void modificarDescripcion(String categoriaId, String nuevaDescripcion) throws EntidadNoEncontrada {
        Categoria c = repositorioCategorias
                .findById(categoriaId)
                .orElseThrow(() -> new EntidadNoEncontrada("Categoría no encontrada: " + categoriaId));
        c.setDescripcion(nuevaDescripcion);
        repositorioCategorias.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> getRaices() {
        return repositorioCategorias.getRaices().stream()
                .map(c -> {
                    CategoriaDto dto = new CategoriaDto();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    dto.setDescripcion(c.getDescripcion());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> getDescendientes(String categoriaId) throws EntidadNoEncontrada {
        Categoria c = repositorioCategorias
                .findById(categoriaId)
                .orElseThrow(() -> new EntidadNoEncontrada("Categoría no encontrada: " + categoriaId));
        return c.getDescendientes().stream()
                .map(d -> {
                    CategoriaDto dto = new CategoriaDto();
                    dto.setId(d.getId());
                    dto.setNombre(d.getNombre());
                    dto.setDescripcion(d.getDescripcion());
                    return dto;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> getCategorias() {

        List<CategoriaDto> categorias = new ArrayList<>();

        repositorioCategorias.findAll().forEach(categoria -> {
            CategoriaDto dto = new CategoriaDto();
            dto.setId(categoria.getId());
            dto.setNombre(categoria.getNombre());
            dto.setDescripcion(categoria.getDescripcion());

            categorias.add(dto);
        });

        return categorias;
    }
}
