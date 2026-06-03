package es.um.arso.usuarios.rest;

import javax.ws.rs.core.Response;

public class SeguridadException extends RuntimeException {

    private final Response.Status status;

    public SeguridadException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public Response.Status getStatus() {
        return status;
    }
}
