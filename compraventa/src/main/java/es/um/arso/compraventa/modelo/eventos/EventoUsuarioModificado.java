package es.um.arso.compraventa.modelo.eventos;

public class EventoUsuarioModificado extends Evento {

    public static final String TIPO_EVENTO = "usuario-modificado";

    private String idUsuario;
    private String nombre;
    private String apellidos;

    public EventoUsuarioModificado() {}

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
}
