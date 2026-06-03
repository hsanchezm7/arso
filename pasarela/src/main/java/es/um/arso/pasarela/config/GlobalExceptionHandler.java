package es.um.arso.pasarela.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RespuestaError handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Error de autenticacion: {}", ex.getMessage());
        return new RespuestaError("Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RespuestaError handleIllegalArgumentException(IllegalArgumentException ex) {
        return new RespuestaError("Bad Request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RespuestaError handleException(Exception ex) {
        log.error("Error interno del servidor", ex);
        return new RespuestaError("Internal Server Error", ex.getMessage());
    }
}
