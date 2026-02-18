package es.um.arso.usuarios.rest;

import es.um.arso.repositorio.EntidadNoEncontrada;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class EntidadNoEncontradaMapper implements ExceptionMapper<EntidadNoEncontrada> {

    @Override
    public Response toResponse(EntidadNoEncontrada e) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
    }
}
