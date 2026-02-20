package es.um.arso.productos.modelo;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Categoria {

    @Id private String id;
    private String nombre;
    private String descripcion;
    private String ruta;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Categoria> subcategorias = new LinkedList<>();

    @ManyToOne private Categoria parent;

    public Categoria() {}

    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public List<Categoria> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<Categoria> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public Categoria getParent() {
        return parent;
    }

    public void setParent(Categoria parent) {
        this.parent = parent;
    }

    public void addSubcategoria(Categoria c) {
        c.setParent(this);
        this.subcategorias.add(c);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Categoria)) return false;
        Categoria other = (Categoria) o;
        if (id == null || other.id == null) return false;
        return id.equals(other.id);
    }

    public boolean esRaiz() {
        return parent == null;
    }

    public List<Categoria> getDescendientes() {
        List<Categoria> resultado = new LinkedList<>();
        for (Categoria sub : subcategorias) {
            resultado.add(sub);
            resultado.addAll(sub.getDescendientes());
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Categoria{" + id + ":" + nombre + "}";
    }
}
