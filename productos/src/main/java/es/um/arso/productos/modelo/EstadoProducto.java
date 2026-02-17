package es.um.arso.productos.modelo;

public enum EstadoProducto {
    NUEVO,
    COMO_NUEVO,
    BUEN_ESTADO,
    ACEPTABLE,
    PARA_PIEZAS_O_REPARAR;

    public boolean esIgualOMejorQue(EstadoProducto otro) {
        return this.ordinal() <= otro.ordinal();
    }
}
