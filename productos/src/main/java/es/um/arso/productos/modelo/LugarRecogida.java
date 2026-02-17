package es.um.arso.productos.modelo;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class LugarRecogida {
    @Column(name = "recogida_descripcion")
    private String descripcion;

    @Column(name = "recogida_longitud")
    private Double longitud;

    @Column(name = "recogida_latitud")
    private Double latitud;

    public LugarRecogida() {}

    public LugarRecogida(String descripcion, Double longitud, Double latitud) {
        this.descripcion = descripcion;
        this.longitud = longitud;
        this.latitud = latitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
}
