package es.um.arso.compraventa.puertos.out;

import es.um.arso.compraventa.modelo.eventos.Evento;

public interface PublicadorEventos {

    public void emitirEvento(Evento evento);
}
