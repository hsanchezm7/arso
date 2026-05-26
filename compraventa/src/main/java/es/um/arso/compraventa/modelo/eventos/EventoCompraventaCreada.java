package es.um.arso.compraventa.modelo.eventos;

public class EventoCompraventaCreada extends Evento {

    public static final String TIPO_EVENTO = "compraventa-creada";

    private String idProducto;
    private String idComprador;

    public EventoCompraventaCreada() {}

    public EventoCompraventaCreada(String id, String idProducto, String idComprador) {
        super(id, TIPO_EVENTO);
        this.idProducto = idProducto;
        this.idComprador = idComprador;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }
}
