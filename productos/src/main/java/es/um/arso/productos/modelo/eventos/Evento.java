package es.um.arso.productos.modelo.eventos;

import java.time.LocalDateTime;

public abstract class Evento {

    private String id;

    // TODO: ¿Implementar como enum?
    private String tipoEvento;
    private String timestamp;

    public Evento() {}

    public Evento(String id, String tipoEvento) {
        this.id = id;
        this.tipoEvento = tipoEvento;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Evento [id=" + id + ", tipoEvento=" + tipoEvento + ", timestamp=" + timestamp + "]";
    }
}
