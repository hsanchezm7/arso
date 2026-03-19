package es.um.arso.productos.modelo;

import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Producto {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String titulo;
    private String descripcion;
    private Double precio;

    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;

    private LocalDateTime fechaPublicacion;
    private int visualizaciones = 0;
    private boolean envioDisponible;

    @Embedded private LugarRecogida recogida;

    @ManyToOne
    @JoinColumn(name = "categoria_fk")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "vendedor_fk")
    private Usuario vendedor;

    private boolean disponible;

    public Producto() {}

    public Producto(
            String titulo,
            String descripcion,
            Double precio,
            EstadoProducto estado,
            Categoria categoria,
            boolean envioDisponible,
            Usuario vendedor) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.estado = estado;
        this.categoria = categoria;
        this.envioDisponible = envioDisponible;
        this.vendedor = vendedor;
        this.fechaPublicacion = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public EstadoProducto getEstado() {
        return estado;
    }

    public void setEstado(EstadoProducto estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public int getVisualizaciones() {
        return visualizaciones;
    }

    public void setVisualizaciones(int visualizaciones) {
        this.visualizaciones = visualizaciones;
    }

    public boolean isEnvioDisponible() {
        return envioDisponible;
    }

    public void setEnvioDisponible(boolean envioDisponible) {
        this.envioDisponible = envioDisponible;
    }

    public LugarRecogida getRecogida() {
        return recogida;
    }

    public void setRecogida(LugarRecogida recogida) {
        this.recogida = recogida;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario vendedor) {
        this.vendedor = vendedor;
    }

    public void incrementarVisualizaciones() {
        this.visualizaciones++;
    }

    public void asignarLugarRecogida(String descripcion, Double longitud, Double latitud) {
        this.recogida = new LugarRecogida(descripcion, longitud, latitud);
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return "Producto [id="
                + id
                + ", titulo="
                + titulo
                + ", descripcion="
                + descripcion
                + ", precio="
                + precio
                + ", estado="
                + estado
                + ", fechaPublicacion="
                + fechaPublicacion
                + ", disponible="
                + disponible
                + "]";
    }
}
