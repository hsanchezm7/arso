package es.um.arso.usuarios.modelo.eventos;

public class EventoUsuarioCreado extends Evento {

    public static final String TIPO_EVENTO = "usuario-creado";

    private String idUsuario;
    private String email;
    private String nombre;
    private String apellidos;

    public EventoUsuarioCreado() {
    }

    public EventoUsuarioCreado(String idUsuario, String email, String nombre, String apellidos) {
        super(idUsuario, TIPO_EVENTO);
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
