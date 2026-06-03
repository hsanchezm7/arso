package es.um.arso.productos.modelo;

public enum EstadoProducto {
    NUEVO("Nuevo"),
    COMO_NUEVO("Como nuevo"),
    BUEN_ESTADO("Buen estado"),
    ACEPTABLE("Aceptable"),
    PARA_PIEZAS_O_REPARAR("Para piezas o reparar");

    private String valor;

    EstadoProducto(final String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public boolean esIgualOMejorQue(EstadoProducto otro) {
        return this.ordinal() <= otro.ordinal();
    }
}
