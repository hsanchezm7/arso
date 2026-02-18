package es.um.arso.usuarios.rest;

import es.um.arso.repositorio.RepositorioException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RepositorioExceptionMapper implements ExceptionMapper<RepositorioException> {

    @Override
    public Response toResponse(RepositorioException e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
    }
}
