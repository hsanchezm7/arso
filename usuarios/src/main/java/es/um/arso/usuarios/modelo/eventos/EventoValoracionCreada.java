package es.um.arso.usuarios.modelo.eventos;

public class EventoValoracionCreada extends Evento {

    public static final String TIPO_EVENTO = "valoracion-creada";

    private String idCompraventa;
    private String idUsuarioEvaluador;
    private String idUsuarioValorado;
    private String rolUsuarioValorado;
    private int puntuacion;

    public EventoValoracionCreada() {}

    public EventoValoracionCreada(
            String id,
            String idCompraventa,
            String idUsuarioEvaluador,
            String idUsuarioValorado,
            String rolUsuarioValorado,
            int puntuacion) {
        super(id, TIPO_EVENTO);
        this.idCompraventa = idCompraventa;
        this.idUsuarioEvaluador = idUsuarioEvaluador;
        this.idUsuarioValorado = idUsuarioValorado;
        this.rolUsuarioValorado = rolUsuarioValorado;
        this.puntuacion = puntuacion;
    }

    public String getIdCompraventa() {
        return idCompraventa;
    }

    public void setIdCompraventa(String idCompraventa) {
        this.idCompraventa = idCompraventa;
    }

    public String getIdUsuarioEvaluador() {
        return idUsuarioEvaluador;
    }

    public void setIdUsuarioEvaluador(String idUsuarioEvaluador) {
        this.idUsuarioEvaluador = idUsuarioEvaluador;
    }

    public String getIdUsuarioValorado() {
        return idUsuarioValorado;
    }

    public void setIdUsuarioValorado(String idUsuarioValorado) {
        this.idUsuarioValorado = idUsuarioValorado;
    }

    public String getRolUsuarioValorado() {
        return rolUsuarioValorado;
    }

    public void setRolUsuarioValorado(String rolUsuarioValorado) {
        this.rolUsuarioValorado = rolUsuarioValorado;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
