package es.um.arso.productos.puertos.out;

import es.um.arso.productos.modelo.eventos.Evento;

public interface PublicadorEventos {

    public void emitirEvento(Evento evento);
}
