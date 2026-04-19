package es.um.arso.usuarios.modelo.eventos;

public class EventoCompraventaCreada extends Evento {

    public static final String TIPO_EVENTO = "compraventa-creada";

    // TODO: Eliminar idProducto ya que no se usa en este microservicio
    private String idProducto;
    private String idVendedor;
    private String idComprador;

    public EventoCompraventaCreada() {}

    public EventoCompraventaCreada(String id, String idProducto, String idVendedor, String idComprador) {
        super(id, TIPO_EVENTO);
        this.idProducto = idProducto;
        this.idVendedor = idVendedor;
        this.idComprador = idComprador;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(String idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(String idComprador) {
        this.idComprador = idComprador;
    }
}
