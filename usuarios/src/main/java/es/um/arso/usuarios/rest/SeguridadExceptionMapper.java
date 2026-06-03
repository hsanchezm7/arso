package es.um.arso.usuarios.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SeguridadExceptionMapper implements ExceptionMapper<SeguridadException> {

    @Override
    public Response toResponse(SeguridadException e) {
        String errorType = e.getStatus() == Response.Status.UNAUTHORIZED ? "Unauthorized" : "Forbidden";
        return Response.status(e.getStatus())
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + errorType + "\", \"message\": \"" + e.getMessage() + "\"}")
                .build();
    }
}
