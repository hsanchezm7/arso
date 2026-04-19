package es.um.arso.compraventa.servicio.puertos.out;

import com.google.gson.annotations.SerializedName;

public class ProductoInfo {
    private String id;
    private String titulo;
    private double precio;

    @SerializedName("vendedorId")
    private String idVendedor;

    private RecogidaInfo recogida;

    public ProductoInfo() {}

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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public RecogidaInfo getRecogida() {
        return recogida;
    }

    public void setRecogida(RecogidaInfo recogida) {
        this.recogida = recogida;
    }

    public static class RecogidaInfo {
        private String descripcion;
        private Double longitud;
        private Double latitud;

        public RecogidaInfo() {}

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

        @Override
        public String toString() {
            if (descripcion == null) return null;
            return descripcion + (longitud != null && latitud != null ? " (" + latitud + ", " + longitud + ")" : "");
        }
    }
}
