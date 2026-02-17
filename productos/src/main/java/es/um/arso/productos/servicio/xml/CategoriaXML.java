package es.um.arso.productos.servicio.xml;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "categoria")
public class CategoriaXML {

    // atributos del XML: id y ruta
    private String id;
    private String ruta;
    // elementos hijo: nombre, (opcional) descripcion, y subcategorias
    private String nombre;
    private String descripcion;
    private List<CategoriaXML> subcategorias = new LinkedList<>();

    @XmlAttribute
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    @XmlElement(name = "nombre")
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlElement(name = "descripcion")
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @XmlElement(name = "categoria")
    public List<CategoriaXML> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<CategoriaXML> subcategorias) {
        this.subcategorias = subcategorias;
    }
}
