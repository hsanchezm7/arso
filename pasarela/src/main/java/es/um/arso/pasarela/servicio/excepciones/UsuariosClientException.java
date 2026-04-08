package es.um.arso.pasarela.servicio.excepciones;

public class UsuariosClientException extends RuntimeException {

    public UsuariosClientException(String message) {
        super(message);
    }

    public UsuariosClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
