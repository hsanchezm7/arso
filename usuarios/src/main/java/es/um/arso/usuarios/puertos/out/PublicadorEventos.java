package es.um.arso.usuarios.puertos.out;

import es.um.arso.usuarios.modelo.eventos.Evento;
import java.io.IOException;

public interface PublicadorEventos {

    public void emitirEvento(Evento evento) throws IOException;
}
